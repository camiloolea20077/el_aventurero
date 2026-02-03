import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { lastValueFrom } from 'rxjs';

// PrimeNG
import { DialogModule } from 'primeng/dialog';
import { ButtonModule } from 'primeng/button';
import { CalendarModule } from 'primeng/calendar';
import { CardModule } from 'primeng/card';
import { TableModule } from 'primeng/table';
import { ChartModule } from 'primeng/chart';
import { DividerModule } from 'primeng/divider';
import { TagModule } from 'primeng/tag';
import { FlujoCajaService } from '../../../../core/services/flujo-caja.service';
import { VentaService } from '../../../../core/services/venta.service';
import { AlertService } from '../../../../../shared/pipes/alert.service';
import { CierreSemanalModel } from '../../../../core/models/flujo-caja/cierre-semanal.model';

@Component({
  selector: 'app-cierre-semanal',
  standalone: true,
  templateUrl: './cierre-semanal.component.html',
  styleUrls: ['./cierre-semanal.component.scss'],
  imports: [
    CommonModule,
    FormsModule,
    DialogModule,
    ButtonModule,
    CalendarModule,
    CardModule,
    TableModule,
    ChartModule,
    DividerModule,
    TagModule,
  ],
})
export class CierreSemanalComponent implements OnInit {
  @Input() displayModal: boolean = false;
  @Output() modalClosed = new EventEmitter<void>();

  fechaInicio: Date = new Date();
  fechaFin: Date = new Date();
  loading: boolean = false;
  cierre: CierreSemanalModel | null = null;

  // Gráficas
  metodosPagoChart: any;
  metodosPagoChartOptions: any;

  productosChart: any;
  productosChartOptions: any;

  constructor(
    private readonly flujoCajaService: FlujoCajaService,
    private readonly ventaService: VentaService,
    private readonly _alertService: AlertService,
  ) {}

  ngOnInit() {
    this.setFechasSemanaActual();
  }

  ngOnChanges() {
    if (this.displayModal) {
      this.cargarCierre();
    }
  }

  setFechasSemanaActual(): void {
    const hoy = new Date();
    const primerDia = hoy.getDate() - hoy.getDay(); // Domingo
    const ultimoDia = primerDia + 6; // Sábado

    this.fechaInicio = new Date(hoy.setDate(primerDia));
    this.fechaInicio.setHours(0, 0, 0, 0);

    this.fechaFin = new Date(hoy.setDate(ultimoDia));
    this.fechaFin.setHours(23, 59, 59, 999);
  }

  async cargarCierre(): Promise<void> {
    this.loading = true;
    try {
      const fechaInicioStr = this.formatDate(this.fechaInicio);
      const fechaFinStr = this.formatDate(this.fechaFin);

      // Consumir servicio real en lugar de datos quemados
      const response = await lastValueFrom(
        this.flujoCajaService.getCierreSemanal(fechaInicioStr, fechaFinStr),
      );

      this.cierre = response.data;

      if (this.cierre) {
        this.inicializarGraficas();
      }
    } catch (error) {
      this._alertService.showError(
        'Error',
        'No se pudo cargar el cierre semanal',
      );
    } finally {
      this.loading = false;
    }
  }

  getNumeroSemana(fecha: Date): number {
    const primerEnero = new Date(fecha.getFullYear(), 0, 1);
    const diasTranscurridos = Math.floor(
      (fecha.getTime() - primerEnero.getTime()) / 86400000,
    );
    return Math.ceil((diasTranscurridos + primerEnero.getDay() + 1) / 7);
  }

  formatDate(date: Date): string {
    const year = date.getFullYear();
    const month = ('0' + (date.getMonth() + 1)).slice(-2);
    const day = ('0' + date.getDate()).slice(-2);
    return `${year}-${month}-${day}`;
  }

  formatDateDisplay(dateString: string): string {
    const date = new Date(dateString);
    return date.toLocaleDateString('es-CO', {
      year: 'numeric',
      month: 'long',
      day: 'numeric',
    });
  }

  inicializarGraficas(): void {
    if (!this.cierre) return;

    this.inicializarMetodosPagoChart();
    this.inicializarProductosChart();
  }

  inicializarMetodosPagoChart(): void {
    if (!this.cierre) return;

    const documentStyle = getComputedStyle(document.documentElement);

    this.metodosPagoChart = {
      labels: this.cierre.metodos_pago.map((m) => m.metodo),
      datasets: [
        {
          data: this.cierre.metodos_pago.map((m) => m.total),
          backgroundColor: ['#28a745', '#007bff', '#ffc107', '#6f42c1'],
          hoverBackgroundColor: ['#218838', '#0056b3', '#e0a800', '#5a32a3'],
        },
      ],
    };

    this.metodosPagoChartOptions = {
      plugins: {
        legend: {
          labels: {
            usePointStyle: true,
            color: documentStyle.getPropertyValue('--text-color') || '#495057',
          },
        },
        tooltip: {
          callbacks: {
            label: (context: any) => {
              const label = context.label || '';
              const value = context.parsed || 0;
              return `${label}: $${value.toLocaleString()}`;
            },
          },
        },
      },
    };
  }

  inicializarProductosChart(): void {
    if (!this.cierre) return;

    const documentStyle = getComputedStyle(document.documentElement);

    this.productosChart = {
      labels: this.cierre.productos_top.map((p) => p.producto_nombre),
      datasets: [
        {
          label: 'Total Vendido',
          backgroundColor: '#667eea',
          borderColor: '#667eea',
          data: this.cierre.productos_top.map((p) => p.total_vendido),
        },
      ],
    };

    this.productosChartOptions = {
      indexAxis: 'y',
      maintainAspectRatio: false,
      aspectRatio: 0.8,
      plugins: {
        legend: {
          labels: {
            color: documentStyle.getPropertyValue('--text-color') || '#495057',
          },
        },
        tooltip: {
          callbacks: {
            label: (context: any) => {
              return `Total: $${context.parsed.x.toLocaleString()}`;
            },
          },
        },
      },
      scales: {
        x: {
          ticks: {
            color:
              documentStyle.getPropertyValue('--text-color-secondary') ||
              '#6c757d',
            callback: (value: any) => {
              return '$' + value.toLocaleString();
            },
          },
          grid: {
            color:
              documentStyle.getPropertyValue('--surface-border') || '#e9ecef',
          },
        },
        y: {
          ticks: {
            color:
              documentStyle.getPropertyValue('--text-color-secondary') ||
              '#6c757d',
          },
          grid: {
            color:
              documentStyle.getPropertyValue('--surface-border') || '#e9ecef',
          },
        },
      },
    };
  }

  imprimirCierre(): void {
    window.print();
  }

  exportarPDF(): void {
    // Implementar exportación a PDF
    this._alertService.showInfo(
      'Función en desarrollo',
      'La exportación a PDF estará disponible próximamente',
    );
  }

  closeModal() {
    this.displayModal = false;
    this.cierre = null;
    this.modalClosed.emit();
  }

  onModalHide() {
    this.closeModal();
  }
}

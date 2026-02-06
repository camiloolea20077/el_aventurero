import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { MessageService, ConfirmationService } from 'primeng/api';
import { lastValueFrom } from 'rxjs';

// PrimeNG
import { ButtonModule } from 'primeng/button';
import { TableModule } from 'primeng/table';
import { TagModule } from 'primeng/tag';
import { CalendarModule } from 'primeng/calendar';
import { ToastModule } from 'primeng/toast';
import { ConfirmDialogModule } from 'primeng/confirmdialog';
import { TooltipModule } from 'primeng/tooltip';
import { CardModule } from 'primeng/card';
import { ConteoInventarioModel } from '../../../../core/models/conteo-inventario/conteo-inventario.model';
import { ConteoInventarioService } from '../../../../core/services/conteo-inventario.service';
import { AlertService } from '../../../../../shared/pipes/alert.service';
import { RealizarConteoComponent } from '../../components/realizar-conteo/realizar-conteo.component';
import { DetalleConteoComponent } from '../../components/detalle-conteo/detalle-conteo.component';

@Component({
  selector: 'app-index-conteo-inventario',
  standalone: true,
  templateUrl: './index-conteo-inventario.component.html',
  styleUrls: ['./index-conteo-inventario.component.scss'],
  imports: [
    CommonModule,
    FormsModule,
    ButtonModule,
    TableModule,
    TagModule,
    CalendarModule,
    ToastModule,
    ConfirmDialogModule,
    TooltipModule,
    CardModule,
    RealizarConteoComponent,
    DetalleConteoComponent,
  ],
  providers: [MessageService, ConfirmationService, AlertService],
})
export class IndexConteoInventarioComponent implements OnInit {
  public conteoEnProceso: ConteoInventarioModel | null = null;
  // Datos
  public conteos: ConteoInventarioModel[] = [];
  public loading: boolean = false;
  public ultimoConteo: ConteoInventarioModel | null = null;
  public totalRecords: number = 0;
  public rowSize: number = 10;
  public selectedConteo: ConteoInventarioModel | null = null;

  // Filtros
  public fechaInicio: Date = new Date(
    new Date().getFullYear(),
    new Date().getMonth(),
    1,
  );
  public fechaFin: Date = new Date();

  // Modales
  public showRealizarConteoModal: boolean = false;
  public showDetalleModal: boolean = false;
  public conteoIdSeleccionado: number | null = null;

  constructor(
    private readonly conteoInventarioService: ConteoInventarioService,
    private readonly messageService: MessageService,
    private readonly confirmationService: ConfirmationService,
    private readonly _alertService: AlertService,
  ) {}

  ngOnInit() {
    this.cargarDatos();
    this.cargarUltimoConteo();
  }

  async cargarDatos(): Promise<void> {
    this.loading = true;
    try {
      const fechaInicioStr = this.formatDate(this.fechaInicio);
      const fechaFinStr = this.formatDate(this.fechaFin);

      const response = await lastValueFrom(
        this.conteoInventarioService.getConteosPorFecha(
          fechaInicioStr,
          fechaFinStr,
        ),
      );
      this.conteos = response.data;
    } catch (error) {
      console.error('Error cargando conteos:', error);
      this._alertService.showError(
        'Error',
        'No se pudieron cargar los conteos',
      );
    } finally {
      this.loading = false;
    }
  }

  async cargarUltimoConteo(): Promise<void> {
    try {
      const response = await lastValueFrom(
        this.conteoInventarioService.getUltimoConteo(),
      );
      this.ultimoConteo = response.data;

      // ✅ NUEVO: Detectar si hay conteo en proceso
      if (this.ultimoConteo && this.ultimoConteo.estado === 'EN_PROCESO') {
        this.conteoEnProceso = this.ultimoConteo;
      } else {
        this.conteoEnProceso = null;
      }
    } catch (error) {
      console.error('Error cargando último conteo:', error);
    }
  }

  formatDate(date: Date): string {
    const year = date.getFullYear();
    const month = String(date.getMonth() + 1).padStart(2, '0');
    const day = String(date.getDate()).padStart(2, '0');
    return `${year}-${month}-${day}`;
  }

  aplicarFiltroHoy(): void {
    this.fechaInicio = new Date();
    this.fechaFin = new Date();
    this.cargarDatos();
  }

  aplicarFiltroEsteMes(): void {
    const now = new Date();
    this.fechaInicio = new Date(now.getFullYear(), now.getMonth(), 1);
    this.fechaFin = new Date(now.getFullYear(), now.getMonth() + 1, 0);
    this.cargarDatos();
  }

  openRealizarConteoModal(): void {
    this.showRealizarConteoModal = true;
  }

  onRealizarConteoModalClosed(): void {
    this.showRealizarConteoModal = false;
    this.cargarDatos();
    this.cargarUltimoConteo();
  }

  openDetalleModal(conteoId: number): void {
    this.conteoIdSeleccionado = conteoId;
    this.showDetalleModal = true;
  }

  onDetalleModalClosed(): void {
    this.showDetalleModal = false;
    this.conteoIdSeleccionado = null;
    this.cargarDatos();
  }

  getEstadoSeverity(
    estado: string,
  ): 'success' | 'secondary' | 'info' | 'warn' | 'danger' | 'contrast' {
    const severityMap: {
      [key: string]:
        | 'success'
        | 'secondary'
        | 'info'
        | 'warn'
        | 'danger'
        | 'contrast';
    } = {
      CUADRADO: 'success',
      PENDIENTE: 'warn', // Changed from 'warning' to 'warn'
      AJUSTADO: 'info',
    };
    return severityMap[estado] || 'secondary';
  }
  getEstadoLabel(estado: string): string {
    const labelMap: { [key: string]: string } = {
      EN_PROCESO: 'En Proceso',
      COMPLETADO: 'Completado',
    };
    return labelMap[estado] || estado;
  }

  getTipoLabel(tipo: string): string {
    const labelMap: { [key: string]: string } = {
      PERIODICO: 'Periódico',
      CICLICO: 'Cíclico',
      ANUAL: 'Anual',
    };
    return labelMap[tipo] || tipo;
  }

  getDiasDesdeUltimoConteo(): number | null {
    if (!this.ultimoConteo) return null;

    const fechaConteo = new Date(this.ultimoConteo.fecha);
    const hoy = new Date();
    const diferencia = hoy.getTime() - fechaConteo.getTime();
    return Math.floor(diferencia / (1000 * 60 * 60 * 24));
  }

  getAlertaUltimoConteo(): { severity: string; message: string } | null {
    const dias = this.getDiasDesdeUltimoConteo();

    if (dias === null) {
      return {
        severity: 'warn',
        message:
          'No hay conteos registrados. Se recomienda realizar un conteo.',
      };
    }

    if (dias === 0) {
      return {
        severity: 'success',
        message: 'Conteo realizado hoy. Todo al día.',
      };
    }

    if (dias <= 7) {
      return {
        severity: 'info',
        message: `Último conteo hace ${dias} día(s). Todo en orden.`,
      };
    }

    if (dias <= 14) {
      return {
        severity: 'warn',
        message: `Último conteo hace ${dias} día(s). Se recomienda realizar un nuevo conteo pronto.`,
      };
    }

    return {
      severity: 'danger',
      message: `¡Atención! Último conteo hace ${dias} día(s). Es urgente realizar un nuevo conteo.`,
    };
  }
  // ✅ NUEVO: Método para continuar conteo
  continuarConteo(): void {
    if (this.conteoEnProceso) {
      this.conteoIdSeleccionado = this.conteoEnProceso.id;
      this.showRealizarConteoModal = true;
    }
  }

  // ✅ NUEVO: Método para iniciar nuevo conteo
  iniciarNuevoConteo(): void {
    this.conteoIdSeleccionado = null;
    this.showRealizarConteoModal = true;
  }
}

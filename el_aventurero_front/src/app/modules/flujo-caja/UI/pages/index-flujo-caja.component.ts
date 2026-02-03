import { CommonModule } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { ConfirmationService, MessageService } from 'primeng/api';
import { lastValueFrom } from 'rxjs';

// PrimeNG
import { ButtonModule } from 'primeng/button';
import { CalendarModule } from 'primeng/calendar';
import { CardModule } from 'primeng/card';
import { TableModule } from 'primeng/table';
import { TagModule } from 'primeng/tag';
import { ToastModule } from 'primeng/toast';
import { ConfirmDialogModule } from 'primeng/confirmdialog';
import { TabViewModule } from 'primeng/tabview';
import { MovimientoCajaModel } from '../../../../core/models/flujo-caja/movimiento-caja.model';
import { ResumenFlujoModel } from '../../../../core/models/flujo-caja/resumen-flujo.model';
import { FlujoCajaService } from '../../../../core/services/flujo-caja.service';
import { CierreSemanalComponent } from '../../components/cierre-semanal/cierre-semanal.component';
import { AlertService } from '../../../../../shared/pipes/alert.service';
import { RegistroMovimientoComponent } from '../../components/registro-movimiento/registro-movimiento.component';

@Component({
  selector: 'app-index-flujo-caja',
  standalone: true,
  templateUrl: './index-flujo-caja.component.html',
  styleUrls: ['./index-flujo-caja.component.scss'],
  providers: [MessageService, ConfirmationService, AlertService],
  imports: [
    CommonModule,
    FormsModule,
    ButtonModule,
    CalendarModule,
    CardModule,
    TableModule,
    TagModule,
    ToastModule,
    ConfirmDialogModule,
    TabViewModule,
    RegistroMovimientoComponent,
    CierreSemanalComponent,
  ],
})
export class IndexFlujoCajaComponent implements OnInit {
  // Modal de registro
  showRegistroModal = false;

  // Modal de cierre semanal
  showCierreModal = false;

  // Fechas
  fechaInicio: Date = new Date();
  fechaFin: Date = new Date();

  // Datos
  movimientos: MovimientoCajaModel[] = [];
  resumen: ResumenFlujoModel | null = null;
  loading: boolean = false;

  // Tab activo
  activeIndex: number = 0;

  constructor(
    private flujoCajaService: FlujoCajaService,
    private messageService: MessageService,
    private confirmationService: ConfirmationService,
  ) {}

  ngOnInit(): void {
    this.setFechasHoy();
    this.cargarDatos();
  }

  setFechasHoy(): void {
    const hoy = new Date();
    this.fechaInicio = new Date(hoy.setHours(0, 0, 0, 0));
    this.fechaFin = new Date(hoy.setHours(23, 59, 59, 999));
  }

  setFechasSemana(): void {
    const hoy = new Date();
    const primerDia = hoy.getDate() - hoy.getDay(); // Domingo
    const ultimoDia = primerDia + 6; // Sábado

    this.fechaInicio = new Date(hoy.setDate(primerDia));
    this.fechaInicio.setHours(0, 0, 0, 0);

    this.fechaFin = new Date(hoy.setDate(ultimoDia));
    this.fechaFin.setHours(23, 59, 59, 999);

    this.cargarDatos();
  }

  async cargarDatos(): Promise<void> {
    this.loading = true;
    try {
      await Promise.all([this.cargarMovimientos(), this.cargarResumen()]);
    } catch (error) {
      console.error('Error cargando datos:', error);
    } finally {
      this.loading = false;
    }
  }

  async cargarMovimientos(): Promise<void> {
    try {
      const fechaInicioStr = this.formatDate(this.fechaInicio);
      const fechaFinStr = this.formatDate(this.fechaFin);

      const response = await lastValueFrom(
        this.flujoCajaService.getMovimientosPorFecha(
          fechaInicioStr,
          fechaFinStr,
        ),
      );
      this.movimientos = response.data || [];
    } catch (error) {
      this.movimientos = [];
    }
  }

  async cargarResumen(): Promise<void> {
    try {
      const fechaInicioStr = this.formatDate(this.fechaInicio);
      const fechaFinStr = this.formatDate(this.fechaFin);

      const response = await lastValueFrom(
        this.flujoCajaService.getResumenFlujo(fechaInicioStr, fechaFinStr),
      );
      this.resumen = response.data || null;
    } catch (error) {
      this.resumen = null;
    }
  }

  formatDate(date: Date): string {
    const year = date.getFullYear();
    const month = ('0' + (date.getMonth() + 1)).slice(-2);
    const day = ('0' + date.getDate()).slice(-2);
    return `${year}-${month}-${day}`;
  }

  formatDateTime(dateString: string): string {
    const date = new Date(dateString);
    return date.toLocaleDateString('es-CO', {
      year: 'numeric',
      month: 'short',
      day: 'numeric',
      hour: '2-digit',
      minute: '2-digit',
    });
  }

  getTipoSeverity(tipo: string): 'success' | 'danger' {
    return tipo === 'INGRESO' ? 'success' : 'danger';
  }

  getTipoIcon(tipo: string): string {
    return tipo === 'INGRESO' ? 'pi pi-arrow-up' : 'pi pi-arrow-down';
  }

  getCategoriaSeverity(
    categoria: string,
  ): 'success' | 'warn' | 'danger' | 'info' | 'secondary' {
    const severityMap: {
      [key: string]: 'success' | 'warn' | 'danger' | 'info' | 'secondary';
    } = {
      VENTA: 'success',
      COMPRA: 'warn',
      GASTO: 'danger',
      SALARIO: 'info',
      SERVICIO: 'info',
      OTRO: 'secondary',
    };
    return severityMap[categoria] || 'secondary';
  }

  getMovimientosIngresos(): MovimientoCajaModel[] {
    return this.movimientos.filter((m) => m.tipo === 'INGRESO');
  }

  getMovimientosEgresos(): MovimientoCajaModel[] {
    return this.movimientos.filter((m) => m.tipo === 'EGRESO');
  }

  // Modales
  openRegistroModal(): void {
    this.showRegistroModal = true;
  }

  onRegistroModalClosed(): void {
    this.showRegistroModal = false;
  }

  onMovimientoRegistrado(): void {
    this.cargarDatos();
    this.showRegistroModal = false;
  }

  openCierreModal(): void {
    this.showCierreModal = true;
  }

  onCierreModalClosed(): void {
    this.showCierreModal = false;
  }

  // Eliminar movimiento
  async deleteMovimiento(id: number): Promise<void> {
    this.confirmationService.confirm({
      message: '¿Estás seguro de eliminar este movimiento?',
      header: 'Eliminar Movimiento',
      icon: 'pi pi-exclamation-triangle',
      accept: async () => {
        try {
          await lastValueFrom(this.flujoCajaService.deleteMovimiento(id));
          this.messageService.add({
            severity: 'success',
            summary: 'Éxito',
            detail: 'Movimiento eliminado correctamente',
          });
          this.cargarDatos();
        } catch (error) {
          this.messageService.add({
            severity: 'error',
            summary: 'Error',
            detail: 'No se pudo eliminar el movimiento',
          });
        }
      },
    });
  }
}

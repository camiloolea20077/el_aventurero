import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MessageService } from 'primeng/api';
import { lastValueFrom } from 'rxjs';

// PrimeNG
import { DialogModule } from 'primeng/dialog';
import { ButtonModule } from 'primeng/button';
import { DividerModule } from 'primeng/divider';
import { ToastModule } from 'primeng/toast';
import { CardModule } from 'primeng/card';
import { TagModule } from 'primeng/tag';
import { TableModule } from 'primeng/table';
import { AccordionModule } from 'primeng/accordion';
import { ConteoInventarioModel } from '../../../../core/models/conteo-inventario/conteo-inventario.model';
import { DetalleConteoModel } from '../../../../core/models/conteo-inventario/detalle-conteo.model';
import { AjusteInventarioModel } from '../../../../core/models/conteo-inventario/ajuste-inventario.model';
import { ConteoInventarioService } from '../../../../core/services/conteo-inventario.service';
import { AlertService } from '../../../../../shared/pipes/alert.service';

// Servicios y Modelos

@Component({
  selector: 'app-detalle-conteo',
  standalone: true,
  templateUrl: './detalle-conteo.component.html',
  styleUrls: ['./detalle-conteo.component.scss'],
  imports: [
    CommonModule,
    DialogModule,
    ButtonModule,
    DividerModule,
    ToastModule,
    CardModule,
    TagModule,
    TableModule,
    AccordionModule,
  ],
})
export class DetalleConteoComponent implements OnInit {
  @Input() displayModal: boolean = false;
  @Input() conteoId: number | null = null;
  @Output() modalClosed = new EventEmitter<void>();

  public conteo: ConteoInventarioModel | null = null;
  public detalles: DetalleConteoModel[] = [];
  public ajustes: AjusteInventarioModel[] = [];
  public loading: boolean = false;

  // Estadísticas
  public totalProductos: number = 0;
  public productosCuadrados: number = 0;
  public productosConDiferencia: number = 0;
  public productosAjustados: number = 0;

  constructor(
    private readonly conteoInventarioService: ConteoInventarioService,
    private readonly messageService: MessageService,
    private readonly _alertService: AlertService,
  ) {}

  ngOnInit() {}

  ngOnChanges() {
    if (this.displayModal && this.conteoId) {
      this.cargarConteo();
    }
  }

  async cargarConteo(): Promise<void> {
    if (!this.conteoId) return;

    this.loading = true;
    try {
      // Cargar conteo
      const conteoResponse = await lastValueFrom(
        this.conteoInventarioService.getConteoById(this.conteoId),
      );
      this.conteo = conteoResponse.data;

      // Cargar detalles
      const detallesResponse = await lastValueFrom(
        this.conteoInventarioService.getDetallesByConteoId(this.conteoId),
      );
      this.detalles = detallesResponse.data;

      // Cargar ajustes
      const ajustesResponse = await lastValueFrom(
        this.conteoInventarioService.getAjustesByConteoId(this.conteoId),
      );
      this.ajustes = ajustesResponse.data;

      // Calcular estadísticas
      this.calcularEstadisticas();
    } catch (error) {
      console.error('Error cargando conteo:', error);
      this._alertService.showError('Error', 'No se pudo cargar el conteo');
      this.closeModal();
    } finally {
      this.loading = false;
    }
  }

  calcularEstadisticas(): void {
    this.totalProductos = this.detalles.length;
    this.productosCuadrados = this.detalles.filter(
      (d) => d.diferencia === 0,
    ).length;
    this.productosConDiferencia = this.detalles.filter(
      (d) => d.diferencia !== 0,
    ).length;
    this.productosAjustados = this.detalles.filter((d) => d.ajustado).length;
  }

  formatDateTime(dateString: string | undefined): string {
    // ✅ Agregar | undefined
    if (!dateString) return '';
    const date = new Date(dateString);
    return date.toLocaleDateString('es-CO', {
      year: 'numeric',
      month: 'long',
      day: 'numeric',
      hour: '2-digit',
      minute: '2-digit',
    });
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

  getDiferenciaSeverity(
    diferencia: number,
  ): 'success' | 'info' | 'warn' | 'danger' | 'secondary' {
    if (diferencia === 0) return 'success';
    if (Math.abs(diferencia) <= 5) return 'warn'; // ✅ Cambiar de 'warning' a 'warn'
    return 'danger';
  }

  getDiferenciaIcon(diferencia: number): string {
    if (diferencia === 0) return 'pi-check';
    if (diferencia > 0) return 'pi-arrow-up';
    return 'pi-arrow-down';
  }

  getMotivoLabel(motivo: string): string {
    const motivoMap: { [key: string]: string } = {
      VENTA_NO_REGISTRADA: 'Venta no registrada',
      ROBO: 'Robo/Pérdida',
      MERMA: 'Merma/Producto dañado',
      ERROR_CONTEO: 'Error en conteo',
      ERROR_COMPRA: 'Error al registrar compra',
      OTRO: 'Otro motivo',
    };
    return motivoMap[motivo] || motivo;
  }

  getTipoAjusteLabel(tipo: string): string {
    return tipo === 'SUMA' ? 'Suma' : 'Resta';
  }

  getTipoAjusteSeverity(
    tipo: string,
  ): 'success' | 'info' | 'warn' | 'danger' | 'secondary' {
    return tipo === 'SUMA' ? 'success' : 'danger';
  }

  closeModal() {
    this.conteo = null;
    this.detalles = [];
    this.ajustes = [];
    this.displayModal = false;
    this.modalClosed.emit();
  }

  onModalHide() {
    this.closeModal();
  }
}

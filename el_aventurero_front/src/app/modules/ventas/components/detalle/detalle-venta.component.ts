import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { CommonModule } from '@angular/common';
import { lastValueFrom } from 'rxjs';

// PrimeNG
import { ToastModule } from 'primeng/toast';
import { DialogModule } from 'primeng/dialog';
import { ButtonModule } from 'primeng/button';
import { DividerModule } from 'primeng/divider';
import { TableModule } from 'primeng/table';
import { TagModule } from 'primeng/tag';
import { VentaService } from '../../../../core/services/venta.service';
import { VentaModel } from '../../../../core/models/venta/venta.model';
import { AlertService } from '../../../../../shared/pipes/alert.service';
@Component({
  selector: 'app-detalle-venta',
  standalone: true,
  templateUrl: './detalle-venta.component.html',
  styleUrls: ['./detalle-venta.component.scss'],
  imports: [
    CommonModule,
    ToastModule,
    DialogModule,
    ButtonModule,
    DividerModule,
    TableModule,
    TagModule,
  ],
})
export class DetalleVentaComponent implements OnInit {
  @Input() displayModal: boolean = false;
  @Input() ventaId: number | null = null;
  @Output() modalClosed = new EventEmitter<void>();

  public venta: VentaModel | null = null;
  public loading: boolean = false;

  constructor(
    private readonly ventaService: VentaService,
    private readonly _alertService: AlertService,
  ) {}

  ngOnInit() {}

  ngOnChanges() {
    if (this.displayModal && this.ventaId) {
      this.cargarVenta();
    }
  }

  async cargarVenta(): Promise<void> {
    if (!this.ventaId) return;

    this.loading = true;
    try {
      const response = await lastValueFrom(
        this.ventaService.getVentaById(this.ventaId),
      );

      if (response && response.data) {
        this.venta = response.data;
      }
    } catch (error) {
      this._alertService.showError(
        'Error',
        'No se pudo cargar la información de la venta',
      );
      this.closeModal();
    } finally {
      this.loading = false;
    }
  }

  formatDate(dateString: string): string {
    const date = new Date(dateString);
    return date.toLocaleDateString('es-CO', {
      year: 'numeric',
      month: 'long',
      day: 'numeric',
      hour: '2-digit',
      minute: '2-digit',
    });
  }

  getMetodoPagoSeverity(
    metodo: string,
  ): 'success' | 'info' | 'warn' | 'danger' | 'secondary' | 'contrast' {
    const severityMap: {
      [key: string]:
        | 'success'
        | 'info'
        | 'warn'
        | 'danger'
        | 'secondary'
        | 'contrast';
    } = {
      EFECTIVO: 'success',
      TARJETA: 'info',
      TRANSFERENCIA: 'warn',
      DIGITAL: 'secondary',
    };
    return severityMap[metodo] || 'secondary';
  }

  getMetodoPagoIcon(metodo: string): string {
    const iconMap: { [key: string]: string } = {
      EFECTIVO: 'pi pi-money-bill',
      TARJETA: 'pi pi-credit-card',
      TRANSFERENCIA: 'pi pi-send',
      DIGITAL: 'pi pi-mobile',
    };
    return iconMap[metodo] || 'pi pi-wallet';
  }

  getTotalCantidad(): number {
    return (this.venta?.detalles || []).reduce((sum, d) => sum + d.cantidad, 0);
  }

  closeModal() {
    this.displayModal = false;
    this.venta = null;
    this.modalClosed.emit();
  }

  onModalHide() {
    this.closeModal();
  }
}

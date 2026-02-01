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
import { CompraService } from '../../../../core/services/compra.service';
import { CompraModel } from '../../../../core/models/compra/compra.model';
import { AlertService } from '../../../../../shared/pipes/alert.service';

@Component({
  selector: 'app-detalle-compra',
  standalone: true,
  templateUrl: './detalle-compra.component.html',
  styleUrls: ['./detalle-compra.component.scss'],
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
export class DetalleCompraComponent implements OnInit {
  @Input() displayModal: boolean = false;
  @Input() compraId: number | null = null;
  @Output() modalClosed = new EventEmitter<void>();

  public compra: CompraModel | null = null;
  public loading: boolean = false;

  constructor(
    private readonly compraService: CompraService,
    private readonly _alertService: AlertService,
  ) {}

  ngOnInit() {}

  ngOnChanges() {
    if (this.displayModal && this.compraId) {
      this.cargarCompra();
    }
  }

  async cargarCompra(): Promise<void> {
    if (!this.compraId) return;

    this.loading = true;
    try {
      const response = await lastValueFrom(
        this.compraService.getCompraById(this.compraId),
      );

      if (response && response.data) {
        this.compra = response.data;
      }
    } catch (error) {
      this._alertService.showError(
        'Error',
        'No se pudo cargar la información de la compra',
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

  getTotalUnidades(): number {
    return (this.compra?.detalles || []).reduce(
      (sum, d) => sum + d.total_unidades,
      0,
    );
  }

  closeModal() {
    this.displayModal = false;
    this.compra = null;
    this.modalClosed.emit();
  }

  onModalHide() {
    this.closeModal();
  }
}

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
import { TextareaModule } from 'primeng/textarea';
import { FormsModule } from '@angular/forms';
import { ArqueoCajaModel } from '../../../../core/models/arqueo-caja/arqueo-caja.model';
import { ArqueoCajaService } from '../../../../core/services/arqueo-caja.service';
import { AlertService } from '../../../../../shared/pipes/alert.service';

@Component({
  selector: 'app-detalle-arqueo',
  standalone: true,
  templateUrl: './detalle-arqueo.component.html',
  styleUrls: ['./detalle-arqueo.component.scss'],
  imports: [
    CommonModule,
    FormsModule,
    DialogModule,
    ButtonModule,
    DividerModule,
    ToastModule,
    CardModule,
    TagModule,
    TextareaModule,
  ],
})
export class DetalleArqueoComponent implements OnInit {
  @Input() displayModal: boolean = false;
  @Input() arqueoId: number | null = null;
  @Output() modalClosed = new EventEmitter<void>();
  @Output() arqueoActualizado = new EventEmitter<void>();

  public arqueo: ArqueoCajaModel | null = null;
  public loading: boolean = false;
  public showAjustarForm: boolean = false;
  public observacionesAjuste: string = '';
  public isSubmitting: boolean = false;

  // Detalle de billetes
  billetes = [
    { denominacion: 100000, cantidad: 0, total: 0 },
    { denominacion: 50000, cantidad: 0, total: 0 },
    { denominacion: 20000, cantidad: 0, total: 0 },
    { denominacion: 10000, cantidad: 0, total: 0 },
    { denominacion: 5000, cantidad: 0, total: 0 },
    { denominacion: 2000, cantidad: 0, total: 0 },
    { denominacion: 1000, cantidad: 0, total: 0 },
  ];

  // Detalle de monedas
  monedas = [
    { denominacion: 1000, cantidad: 0, total: 0 },
    { denominacion: 500, cantidad: 0, total: 0 },
    { denominacion: 200, cantidad: 0, total: 0 },
    { denominacion: 100, cantidad: 0, total: 0 },
    { denominacion: 50, cantidad: 0, total: 0 },
  ];

  totalBilletes: number = 0;
  totalMonedas: number = 0;

  constructor(
    private readonly arqueoCajaService: ArqueoCajaService,
    private readonly messageService: MessageService,
    private readonly _alertService: AlertService,
  ) {}

  ngOnInit() {}

  ngOnChanges() {
    if (this.displayModal && this.arqueoId) {
      this.cargarArqueo();
    }
  }

  async cargarArqueo(): Promise<void> {
    if (!this.arqueoId) return;

    this.loading = true;
    try {
      const response = await lastValueFrom(
        this.arqueoCajaService.getArqueoById(this.arqueoId),
      );
      this.arqueo = response.data;
      this.cargarDetalleConteo();
    } catch (error) {
      console.error('Error cargando arqueo:', error);
      this._alertService.showError('Error', 'No se pudo cargar el arqueo');
      this.closeModal();
    } finally {
      this.loading = false;
    }
  }

  cargarDetalleConteo(): void {
    if (!this.arqueo) return;

    // Cargar billetes
    this.billetes[0].cantidad = this.arqueo.billetes_100000 || 0;
    this.billetes[1].cantidad = this.arqueo.billetes_50000 || 0;
    this.billetes[2].cantidad = this.arqueo.billetes_20000 || 0;
    this.billetes[3].cantidad = this.arqueo.billetes_10000 || 0;
    this.billetes[4].cantidad = this.arqueo.billetes_5000 || 0;
    this.billetes[5].cantidad = this.arqueo.billetes_2000 || 0;
    this.billetes[6].cantidad = this.arqueo.billetes_1000 || 0;

    // Cargar monedas
    this.monedas[0].cantidad = this.arqueo.monedas_1000 || 0;
    this.monedas[1].cantidad = this.arqueo.monedas_500 || 0;
    this.monedas[2].cantidad = this.arqueo.monedas_200 || 0;
    this.monedas[3].cantidad = this.arqueo.monedas_100 || 0;
    this.monedas[4].cantidad = this.arqueo.monedas_50 || 0;

    // Calcular totales
    this.billetes.forEach((b) => {
      b.total = b.denominacion * b.cantidad;
    });
    this.monedas.forEach((m) => {
      m.total = m.denominacion * m.cantidad;
    });

    this.totalBilletes = this.billetes.reduce((sum, b) => sum + b.total, 0);
    this.totalMonedas = this.monedas.reduce((sum, m) => sum + m.total, 0);
  }

  formatDateTime(dateString: string | undefined): string {
    if (!dateString) return 'N/A';
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

  getEstadoIcon(estado: string): string {
    const iconMap: { [key: string]: string } = {
      CUADRADO: 'pi pi-check-circle',
      PENDIENTE: 'pi pi-exclamation-triangle',
      AJUSTADO: 'pi pi-wrench',
    };
    return iconMap[estado] || 'pi pi-circle';
  }

  getDiferenciaSeverity(
    diferencia: number,
  ): 'success' | 'secondary' | 'info' | 'warn' | 'danger' | 'contrast' {
    if (diferencia === 0) return 'success';
    if (Math.abs(diferencia) <= 10000) return 'warn'; // Changed from 'warning' to 'warn'
    return 'danger';
  }

  getDiferenciaIcon(diferencia: number): string {
    if (diferencia === 0) return 'pi pi-check-circle';
    if (diferencia > 0) return 'pi pi-arrow-up';
    return 'pi pi-arrow-down';
  }

  mostrarFormAjuste(): void {
    this.showAjustarForm = true;
    this.observacionesAjuste = this.arqueo?.observaciones || '';
  }

  cancelarAjuste(): void {
    this.showAjustarForm = false;
    this.observacionesAjuste = '';
  }

  async marcarComoAjustado(): Promise<void> {
    if (!this.arqueo || !this.arqueoId) return;

    if (!this.observacionesAjuste || this.observacionesAjuste.trim() === '') {
      this._alertService.showError(
        'Error',
        'Debe agregar observaciones para justificar el ajuste',
      );
      return;
    }

    this.isSubmitting = true;

    try {
      await lastValueFrom(
        this.arqueoCajaService.updateEstado(this.arqueoId, {
          estado: 'AJUSTADO',
          observaciones: this.observacionesAjuste,
        }),
      );

      this.messageService.add({
        severity: 'success',
        summary: 'Éxito',
        detail: 'Arqueo marcado como ajustado correctamente',
        life: 5000,
      });

      this.arqueoActualizado.emit();
      this.closeModal();
    } catch (error: any) {
      this._alertService.showError(
        'Error',
        error.error?.message || 'No se pudo actualizar el arqueo',
      );
    } finally {
      this.isSubmitting = false;
    }
  }

  closeModal() {
    this.displayModal = false;
    this.showAjustarForm = false;
    this.observacionesAjuste = '';
    this.arqueo = null;
    this.modalClosed.emit();
  }

  onModalHide() {
    this.closeModal();
  }
}

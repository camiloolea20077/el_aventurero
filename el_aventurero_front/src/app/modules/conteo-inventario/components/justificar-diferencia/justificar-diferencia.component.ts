import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { CommonModule } from '@angular/common';
import {
  FormBuilder,
  FormGroup,
  ReactiveFormsModule,
  Validators,
} from '@angular/forms';
import { MessageService } from 'primeng/api';
import { lastValueFrom } from 'rxjs';

// PrimeNG
import { DialogModule } from 'primeng/dialog';
import { ButtonModule } from 'primeng/button';
import { RadioButtonModule } from 'primeng/radiobutton';
import { ToastModule } from 'primeng/toast';
import { DividerModule } from 'primeng/divider';
import { DetalleConteoModel } from '../../../../core/models/conteo-inventario/detalle-conteo.model';
import { TextareaModule } from 'primeng/textarea';
import { ConteoInventarioService } from '../../../../core/services/conteo-inventario.service';
import { AlertService } from '../../../../../shared/pipes/alert.service';

// Servicios y Modelos

interface MotivoAjuste {
  label: string;
  value: string;
  descripcion: string;
}

@Component({
  selector: 'app-justificar-diferencia',
  standalone: true,
  templateUrl: './justificar-diferencia.component.html',
  styleUrls: ['./justificar-diferencia.component.scss'],
  imports: [
    CommonModule,
    ReactiveFormsModule,
    DialogModule,
    ButtonModule,
    RadioButtonModule,
    TextareaModule,
    ToastModule,
    DividerModule,
  ],
})
export class JustificarDiferenciaComponent implements OnInit {
  public Math = Math;
  @Input() displayModal: boolean = false;
  @Input() detalle: DetalleConteoModel | null = null;
  @Output() modalClosed = new EventEmitter<void>();
  @Output() diferenciaJustificada = new EventEmitter<void>();

  public frmJustificar!: FormGroup;
  public isSubmitting: boolean = false;

  public motivosAjuste: MotivoAjuste[] = [
    {
      label: 'Venta no registrada',
      value: 'VENTA_NO_REGISTRADA',
      descripcion: 'Se vendieron productos que no se registraron en el sistema',
    },
    {
      label: 'Robo/Pérdida',
      value: 'ROBO',
      descripcion: 'Productos robados o extraviados',
    },
    {
      label: 'Merma/Producto dañado',
      value: 'MERMA',
      descripcion: 'Productos vencidos, dañados o no aptos para la venta',
    },
    {
      label: 'Error en conteo',
      value: 'ERROR_CONTEO',
      descripcion: 'Error al contar el inventario físico',
    },
    {
      label: 'Error al registrar compra',
      value: 'ERROR_COMPRA',
      descripcion: 'Se registró mal la cantidad en una compra anterior',
    },
    {
      label: 'Otro motivo',
      value: 'OTRO',
      descripcion: 'Otro motivo no listado',
    },
  ];

  constructor(
    private readonly fb: FormBuilder,
    private readonly conteoInventarioService: ConteoInventarioService,
    private readonly messageService: MessageService,
    private readonly _alertService: AlertService,
  ) {
    this.buildForm();
  }

  ngOnInit() {}

  ngOnChanges() {
    if (this.displayModal && this.detalle) {
      this.resetForm();
    }
  }

  buildForm(): void {
    this.frmJustificar = this.fb.group({
      motivo: ['', Validators.required],
      descripcion: ['', [Validators.required, Validators.minLength(10)]],
    });
  }

  resetForm(): void {
    this.frmJustificar.reset();
  }

  getDescripcionMotivo(): string {
    const motivoSeleccionado = this.frmJustificar.get('motivo')?.value;
    const motivo = this.motivosAjuste.find(
      (m) => m.value === motivoSeleccionado,
    );
    return motivo ? motivo.descripcion : '';
  }

  async ajustarInventario(): Promise<void> {
    if (this.frmJustificar.invalid || !this.detalle) {
      this._alertService.showError(
        'Error',
        'Por favor completa todos los campos requeridos',
      );
      return;
    }

    this.isSubmitting = true;

    try {
      const diferencia = this.detalle.diferencia;
      const tipo = diferencia > 0 ? 'SUMA' : 'RESTA';
      const cantidad = Math.abs(diferencia);

      const fechaStr = this.formatDate(new Date());

      await lastValueFrom(
        this.conteoInventarioService.ajustarInventario({
          producto_id: this.detalle.producto_id,
          tipo: tipo,
          cantidad: cantidad,
          motivo: this.frmJustificar.get('motivo')?.value,
          descripcion: this.frmJustificar.get('descripcion')?.value,
          conteo_id: this.detalle.conteo_id,
          fecha: fechaStr,
        }),
      );

      this.messageService.add({
        severity: 'success',
        summary: 'Éxito',
        detail: 'Diferencia justificada e inventario ajustado correctamente',
        life: 5000,
      });

      this.diferenciaJustificada.emit();
      this.closeModal();
    } catch (error: any) {
      this._alertService.showError(
        'Error',
        error.error?.message || 'No se pudo ajustar el inventario',
      );
    } finally {
      this.isSubmitting = false;
    }
  }

  getDiferenciaSeverity(): string {
    if (!this.detalle) return 'secondary';
    if (this.detalle.diferencia === 0) return 'success';
    if (this.detalle.diferencia > 0) return 'warning';
    return 'danger';
  }

  getDiferenciaLabel(): string {
    if (!this.detalle) return '';
    if (this.detalle.diferencia === 0) return 'Sin diferencia';
    if (this.detalle.diferencia > 0) return 'SOBRANTE';
    return 'FALTANTE';
  }

  getDiferenciaIcon(): string {
    if (!this.detalle) return 'pi-circle';
    if (this.detalle.diferencia === 0) return 'pi-check-circle';
    if (this.detalle.diferencia > 0) return 'pi-arrow-up';
    return 'pi-arrow-down';
  }

  formatDate(date: Date): string {
    const year = date.getFullYear();
    const month = String(date.getMonth() + 1).padStart(2, '0');
    const day = String(date.getDate()).padStart(2, '0');
    return `${year}-${month}-${day}`;
  }

  closeModal() {
    this.resetForm();
    this.displayModal = false;
    this.modalClosed.emit();
  }

  onModalHide() {
    this.closeModal();
  }
}

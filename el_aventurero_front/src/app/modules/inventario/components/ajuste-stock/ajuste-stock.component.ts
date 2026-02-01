import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import {
  FormBuilder,
  FormGroup,
  FormsModule,
  ReactiveFormsModule,
  Validators,
} from '@angular/forms';
import { CommonModule } from '@angular/common';
import { MessageService } from 'primeng/api';
import { lastValueFrom } from 'rxjs';

// PrimeNG
import { InputNumberModule } from 'primeng/inputnumber';
import { ToastModule } from 'primeng/toast';
import { DialogModule } from 'primeng/dialog';
import { ButtonModule } from 'primeng/button';
import { DividerModule } from 'primeng/divider';
import { RadioButtonModule } from 'primeng/radiobutton';
import { InventarioTableModel } from '../../../../core/models/inventario/inventario-table.model';
import { InventarioService } from '../../../../core/services/inventario.service';
import { AlertService } from '../../../../../shared/pipes/alert.service';
import { AjusteStockDto } from '../../../../core/models/inventario/ajuste-stock.dto';

@Component({
  selector: 'app-ajuste-stock',
  standalone: true,
  templateUrl: './ajuste-stock.component.html',
  styleUrls: ['./ajuste-stock.component.scss'],
  providers: [MessageService, AlertService],
  imports: [
    CommonModule,
    ReactiveFormsModule,
    FormsModule,
    InputNumberModule,
    ToastModule,
    DialogModule,
    ButtonModule,
    DividerModule,
    RadioButtonModule,
  ],
})
export class AjusteStockComponent implements OnInit {
  @Input() displayModal: boolean = false;
  @Input() inventario: InventarioTableModel | null = null;
  @Output() modalClosed = new EventEmitter<void>();
  @Output() stockAjustado = new EventEmitter<void>();

  public frmAjuste!: FormGroup;
  public isSubmitting: boolean = false;
  public tipoAjuste: 'SUMA' | 'RESTA' = 'SUMA';
  public nuevoStock: number = 0;

  constructor(
    private readonly inventarioService: InventarioService,
    private readonly messageService: MessageService,
    private readonly _alertService: AlertService,
    private readonly formBuilder: FormBuilder,
  ) {}

  ngOnInit() {
    this.loadForm();
  }

  ngOnChanges() {
    if (this.displayModal && this.inventario) {
      this.resetForm();
      this.calcularNuevoStock();
    }
  }

  loadForm() {
    this.frmAjuste = this.formBuilder.group({
      cantidad: [0, [Validators.required, Validators.min(1)]],
    });

    // Suscribirse a cambios en cantidad para recalcular
    this.frmAjuste.get('cantidad')?.valueChanges.subscribe(() => {
      this.calcularNuevoStock();
    });
  }

  resetForm() {
    this.frmAjuste.reset({
      cantidad: 0,
    });
    this.tipoAjuste = 'SUMA';
  }

  onTipoAjusteChange() {
    this.calcularNuevoStock();
  }

  calcularNuevoStock() {
    if (!this.inventario) {
      this.nuevoStock = 0;
      return;
    }

    const cantidad = this.frmAjuste.get('cantidad')?.value || 0;
    const stockActual = this.inventario.stock;

    if (this.tipoAjuste === 'SUMA') {
      this.nuevoStock = stockActual + cantidad;
    } else {
      this.nuevoStock = stockActual - cantidad;
    }
  }

  async buildAjusteStock(): Promise<void> {
    const msgSystem = 'Alerta del sistema';

    if (this.isFormInvalid()) {
      this.markFormAsTouched();
      this._alertService.showError(msgSystem, 'Ingrese una cantidad válida');
      return;
    }

    if (!this.inventario) {
      this._alertService.showError(msgSystem, 'No se encontró el inventario');
      return;
    }

    // Validar que no quede stock negativo
    if (this.nuevoStock < 0) {
      this._alertService.showError(
        msgSystem,
        'El stock no puede quedar en negativo',
      );
      return;
    }

    this.isSubmitting = true;

    const ajusteDto: AjusteStockDto = {
      producto_id: this.inventario.producto_id,
      cantidad: this.frmAjuste.get('cantidad')?.value,
      tipo: this.tipoAjuste,
    };

    try {
      await lastValueFrom(this.inventarioService.ajustarStock(ajusteDto));

      this.messageService.add({
        severity: 'success',
        summary: 'Operación exitosa',
        detail: `Stock ${
          this.tipoAjuste === 'SUMA' ? 'aumentado' : 'reducido'
        } correctamente`,
        life: 5000,
      });

      this.stockAjustado.emit();
      this.closeModal();
    } catch (error: any) {
      this._alertService.showError(
        msgSystem,
        error.error?.message || 'Error al ajustar el stock',
      );
    } finally {
      this.isSubmitting = false;
    }
  }

  isFormInvalid(): boolean {
    return this.frmAjuste.invalid;
  }

  markFormAsTouched(): void {
    this.frmAjuste.markAllAsTouched();
  }

  closeModal() {
    this.displayModal = false;
    this.resetForm();
    this.modalClosed.emit();
  }

  onModalHide() {
    this.closeModal();
  }

  getStockSeverity(stock: number): string {
    if (stock < 0) return 'danger';
    if (stock === 0) return 'danger';
    if (stock <= 10) return 'warning';
    return 'success';
  }
}

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
import { DialogModule } from 'primeng/dialog';
import { ButtonModule } from 'primeng/button';
import { InputTextModule } from 'primeng/inputtext';
import { InputNumberModule } from 'primeng/inputnumber';
import { DropdownModule } from 'primeng/dropdown';
import { CalendarModule } from 'primeng/calendar';
import { RadioButtonModule } from 'primeng/radiobutton';
import { DividerModule } from 'primeng/divider';
import { ToastModule } from 'primeng/toast';
import { FlujoCajaService } from '../../../../core/services/flujo-caja.service';
import { AlertService } from '../../../../../shared/pipes/alert.service';
import { CreateMovimientoDto } from '../../../../core/models/flujo-caja/create-movimiento.dto';
import { TextareaModule } from 'primeng/textarea';

@Component({
  selector: 'app-registro-movimiento',
  standalone: true,
  templateUrl: './registro-movimiento.component.html',
  styleUrls: ['./registro-movimiento.component.scss'],
  imports: [
    CommonModule,
    ReactiveFormsModule,
    FormsModule,
    DialogModule,
    ButtonModule,
    InputTextModule,
    TextareaModule,
    InputNumberModule,
    DropdownModule,
    CalendarModule,
    RadioButtonModule,
    DividerModule,
    ToastModule,
  ],
})
export class RegistroMovimientoComponent implements OnInit {
  @Input() displayModal: boolean = false;
  @Output() modalClosed = new EventEmitter<void>();
  @Output() movimientoRegistrado = new EventEmitter<void>();

  public frmMovimiento!: FormGroup;
  public isSubmitting: boolean = false;

  tipoMovimiento: 'INGRESO' | 'EGRESO' = 'INGRESO';

  categoriasIngreso = [
    { label: 'Venta', value: 'VENTA' },
    { label: 'Otro Ingreso', value: 'OTRO' },
  ];

  categoriasEgreso = [
    { label: 'Compra de Productos', value: 'COMPRA' },
    { label: 'Salarios', value: 'SALARIO' },
    { label: 'Servicios', value: 'SERVICIO' },
    { label: 'Gastos Generales', value: 'GASTO' },
    { label: 'Otro Egreso', value: 'OTRO' },
  ];

  metodosPago = [
    { label: 'Efectivo', value: 'EFECTIVO' },
    { label: 'Tarjeta', value: 'TARJETA' },
    { label: 'Transferencia', value: 'TRANSFERENCIA' },
    { label: 'Nequi/Daviplata', value: 'DIGITAL' },
  ];

  constructor(
    private readonly flujoCajaService: FlujoCajaService,
    private readonly messageService: MessageService,
    private readonly _alertService: AlertService,
    private readonly formBuilder: FormBuilder,
  ) {}

  ngOnInit() {
    this.loadForm();
  }

  ngOnChanges() {
    if (this.displayModal) {
      this.resetForm();
    }
  }

  loadForm() {
    this.frmMovimiento = this.formBuilder.group({
      concepto: ['', [Validators.required, Validators.minLength(3)]],
      categoria: ['', Validators.required],
      monto: [0, [Validators.required, Validators.min(1)]],
      metodo_pago: ['EFECTIVO'],
      descripcion: [''],
      fecha: [new Date(), Validators.required],
    });
  }

  onTipoChange() {
    this.frmMovimiento.patchValue({ categoria: '' });
  }

  getCategorias() {
    return this.tipoMovimiento === 'INGRESO'
      ? this.categoriasIngreso
      : this.categoriasEgreso;
  }

  resetForm() {
    this.frmMovimiento.reset({
      metodo_pago: 'EFECTIVO',
      fecha: new Date(),
    });
    this.tipoMovimiento = 'INGRESO';
  }

  async buildSaveMovimiento(): Promise<void> {
    if (this.frmMovimiento.invalid) {
      this.frmMovimiento.markAllAsTouched();
      this._alertService.showError(
        'Formulario inválido',
        'Complete todos los campos requeridos',
      );
      return;
    }

    this.isSubmitting = true;

    const formValue = this.frmMovimiento.value;
    const fecha = new Date(formValue.fecha);

    const movimiento: CreateMovimientoDto = {
      tipo: this.tipoMovimiento,
      concepto: formValue.concepto,
      categoria: formValue.categoria,
      monto: formValue.monto,
      metodo_pago: formValue.metodo_pago,
      descripcion: formValue.descripcion,
      fecha: fecha.toISOString().split('T')[0],
    };

    try {
      await lastValueFrom(
        this.flujoCajaService.registrarMovimiento(movimiento),
      );

      this.messageService.add({
        severity: 'success',
        summary: 'Éxito',
        detail: 'Movimiento registrado correctamente',
        life: 5000,
      });

      this.movimientoRegistrado.emit();
      this.closeModal();
    } catch (error: any) {
      this._alertService.showError(
        'Error',
        error.error?.message || 'No se pudo registrar el movimiento',
      );
    } finally {
      this.isSubmitting = false;
    }
  }

  closeModal() {
    this.displayModal = false;
    this.resetForm();
    this.modalClosed.emit();
  }

  onModalHide() {
    this.closeModal();
  }
}

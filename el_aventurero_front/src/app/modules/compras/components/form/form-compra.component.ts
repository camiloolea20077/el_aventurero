import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import {
  FormArray,
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
import { DropdownModule } from 'primeng/dropdown';
import { InputTextModule } from 'primeng/inputtext';
import { InputNumberModule } from 'primeng/inputnumber';
import { ToastModule } from 'primeng/toast';
import { DialogModule } from 'primeng/dialog';
import { ButtonModule } from 'primeng/button';
import { DividerModule } from 'primeng/divider';
import { TableModule } from 'primeng/table';
import { CompraModel } from '../../../../core/models/compra/compra.model';
import { ProductoListDto } from '../../../../core/models/producto/producto-list.dto';
import { CompraService } from '../../../../core/services/compra.service';
import { ProductoService } from '../../../../core/services/producto.service';
import { AlertService } from '../../../../../shared/pipes/alert.service';
import { CreateCompraDto } from '../../../../core/models/compra/create-compra.dto';
import { CreateDetalleCompraDto } from '../../../../core/models/compra/create-detalle-compra.dto';
import { ResponseModel } from '../../../../../shared/models/responde.models';
@Component({
  selector: 'app-form-compra',
  standalone: true,
  templateUrl: './form-compra.component.html',
  styleUrls: ['./form-compra.component.scss'],
  imports: [
    CommonModule,
    ReactiveFormsModule,
    FormsModule,
    DropdownModule,
    InputTextModule,
    InputNumberModule,
    ToastModule,
    DialogModule,
    ButtonModule,
    DividerModule,
    TableModule,
  ],
})
export class FormCompraComponent implements OnInit {
  @Input() displayModal: boolean = false;
  @Output() modalClosed = new EventEmitter<void>();
  @Output() compraSaved = new EventEmitter<CompraModel>();

  public frmCompra!: FormGroup;
  public isSubmitting: boolean = false;
  public productos: ProductoListDto[] = [];
  public totalCompra: number = 0;

  metodosPago = [
    { label: 'Efectivo', value: 'EFECTIVO' },
    { label: 'Tarjeta', value: 'TARJETA' },
    { label: 'Transferencia', value: 'TRANSFERENCIA' },
    { label: 'Nequi/Daviplata', value: 'DIGITAL' },
  ];

  constructor(
    private readonly compraService: CompraService,
    private readonly productoService: ProductoService,
    private readonly messageService: MessageService,
    private readonly _alertService: AlertService,
    private readonly formBuilder: FormBuilder,
  ) {}

  ngOnInit() {
    this.loadForm();
    this.cargarProductos();
  }

  ngOnChanges() {
    if (this.displayModal) {
      this.resetForm();
    }
  }

  async cargarProductos(): Promise<void> {
    try {
      const response = await lastValueFrom(
        this.productoService.getAllProductos(),
      );
      this.productos = response.data || [];
    } catch (error) {
      console.error('Error cargando productos:', error);
    }
  }

  loadForm() {
    this.frmCompra = this.formBuilder.group({
      metodo_pago: ['EFECTIVO'],
      detalles: this.formBuilder.array([]),
    });

    // Agregar primer detalle por defecto
    this.agregarDetalle();
  }

  get detalles(): FormArray {
    return this.frmCompra.get('detalles') as FormArray;
  }

  crearDetalleFormGroup(): FormGroup {
    const detalle = this.formBuilder.group({
      producto_id: [null, Validators.required],
      cajas: [1, [Validators.required, Validators.min(1)]],
      unidades_por_caja: [1, [Validators.required, Validators.min(1)]],
      total_unidades: [{ value: 0, disabled: true }],
      costo_total: [0, [Validators.required, Validators.min(0)]],
      costo_unitario: [{ value: 0, disabled: true }],
      precio_sugerido: [0],
      precio_venta: [0, [Validators.required, Validators.min(0)]],
    });

    // Suscribirse a cambios para calcular automáticamente
    detalle.get('cajas')?.valueChanges.subscribe(() => {
      this.calcularTotalUnidades(detalle);
    });

    detalle.get('unidades_por_caja')?.valueChanges.subscribe(() => {
      this.calcularTotalUnidades(detalle);
    });

    detalle.get('costo_total')?.valueChanges.subscribe(() => {
      this.calcularCostoUnitario(detalle);
      this.calcularTotalCompra();
    });

    return detalle;
  }

  agregarDetalle(): void {
    this.detalles.push(this.crearDetalleFormGroup());
  }

  eliminarDetalle(index: number): void {
    if (this.detalles.length > 1) {
      this.detalles.removeAt(index);
      this.calcularTotalCompra();
    } else {
      this._alertService.showError(
        'Advertencia',
        'Debe haber al menos un producto',
      );
    }
  }

  calcularTotalUnidades(detalleGroup: FormGroup): void {
    const cajas = detalleGroup.get('cajas')?.value || 0;
    const unidadesPorCaja = detalleGroup.get('unidades_por_caja')?.value || 0;
    const totalUnidades = cajas * unidadesPorCaja;
    detalleGroup.get('total_unidades')?.setValue(totalUnidades);
    this.calcularCostoUnitario(detalleGroup);
  }

  calcularCostoUnitario(detalleGroup: FormGroup): void {
    const costoTotal = detalleGroup.get('costo_total')?.value || 0;
    const totalUnidades = detalleGroup.get('total_unidades')?.value || 0;
    const costoUnitario = totalUnidades > 0 ? costoTotal / totalUnidades : 0;
    detalleGroup.get('costo_unitario')?.setValue(costoUnitario);
  }

  calcularTotalCompra(): void {
    this.totalCompra = this.detalles.controls.reduce((sum, control) => {
      return sum + (control.get('costo_total')?.value || 0);
    }, 0);
  }

  getProductoNombre(productoId: number): string {
    const producto = this.productos.find((p) => p.id === productoId);
    return producto ? producto.nombre : 'N/A';
  }

  resetForm() {
    // Limpiar todos los detalles
    while (this.detalles.length > 0) {
      this.detalles.removeAt(0);
    }

    this.frmCompra.reset({
      metodo_pago: 'EFECTIVO',
    });

    // Agregar primer detalle
    this.agregarDetalle();
    this.totalCompra = 0;
  }

  async buildDataCompra(): Promise<CreateCompraDto> {
    const formValue = this.frmCompra.value;

    const detalles: CreateDetalleCompraDto[] = formValue.detalles.map(
      (detalle: any) => ({
        producto_id: detalle.producto_id,
        cajas: detalle.cajas,
        unidades_por_caja: detalle.unidades_por_caja,
        costo_total: detalle.costo_total,
        precio_sugerido: detalle.precio_sugerido || 0,
        precio_venta: detalle.precio_venta,
      }),
    );

    return {
      metodo_pago: formValue.metodo_pago,
      detalles: detalles,
    };
  }

  async buildSaveCompra(): Promise<void> {
    const msgSystem = 'Alerta del sistema';
    const msgText = 'Complete el formulario correctamente';

    if (this.isFormInvalid()) {
      this.markFormAsTouched();
      this._alertService.showError(msgSystem, msgText);
      return;
    }

    if (this.detalles.length === 0) {
      this._alertService.showError(
        msgSystem,
        'Debe agregar al menos un producto',
      );
      return;
    }

    this.isSubmitting = true;
    const data: CreateCompraDto = await this.buildDataCompra();

    try {
      const response = await lastValueFrom(
        this.compraService.createCompra(data),
      );

      if (response) {
        this.handleResponse(response);
      }
    } catch (error: any) {
      const msg = error.error?.message || 'Error al registrar la compra';
      this._alertService.showError(msgSystem, msg);
    } finally {
      this.isSubmitting = false;
    }
  }

  isFormInvalid(): boolean {
    return this.frmCompra.invalid;
  }

  markFormAsTouched(): void {
    this.frmCompra.markAllAsTouched();
  }

  private handleResponse(response: ResponseModel<CompraModel>): void {
    if (response?.status === 200 || response?.status === 201) {
      this.messageService.add({
        severity: 'success',
        summary: 'Operación exitosa',
        detail: 'Compra registrada correctamente',
        life: 5000,
      });

      if (response.data) {
        this.compraSaved.emit(response.data);
      }

      this.closeModal();
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

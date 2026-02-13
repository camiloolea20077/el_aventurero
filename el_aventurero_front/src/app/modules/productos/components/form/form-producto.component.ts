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
import { InputSwitchModule } from 'primeng/inputswitch';
import { DropdownModule } from 'primeng/dropdown';
import { InputTextModule } from 'primeng/inputtext';
import { ToastModule } from 'primeng/toast';
import { DialogModule } from 'primeng/dialog';
import { ButtonModule } from 'primeng/button';
import { DividerModule } from 'primeng/divider';
import { ProductoModel } from '../../../../core/models/producto/producto.model';
import { ProductoService } from '../../../../core/services/producto.service';
import { AlertService } from '../../../../../shared/pipes/alert.service';
import { CreateProductoDto } from '../../../../core/models/producto/create-producto.dto';
import { UpdateProductoDto } from '../../../../core/models/producto/update-producto.dto';
import { ResponseModel } from '../../../../../shared/models/responde.models';

@Component({
  selector: 'app-form-producto',
  standalone: true,
  templateUrl: './form-producto.component.html',
  styleUrls: ['./form-producto.component.scss'],
  providers: [MessageService, AlertService],
  imports: [
    CommonModule,
    ReactiveFormsModule,
    FormsModule,
    InputSwitchModule,
    DropdownModule,
    InputTextModule,
    ToastModule,
    DialogModule,
    ButtonModule,
    DividerModule,
  ],
})
export class FormProductoComponent implements OnInit {
  @Input() displayModal: boolean = false;
  @Input() productoId: number | null = null;
  @Input() slug: string = 'create';
  @Output() modalClosed = new EventEmitter<void>();
  @Output() productoSaved = new EventEmitter<ProductoModel>();

  public frmProducto!: FormGroup;
  public isEditMode: boolean = false;
  public isSubmitting: boolean = false;

  tipoVentaOptions = [
    { name: 'Unidad', value: 'UNIDAD' },
    { name: 'Botella', value: 'BOTELLA' },
  ];

  // ✅ NUEVAS OPCIONES
  tipoOptions = [
    { name: 'Producto (Vendible)', value: 'PRODUCTO' },
    { name: 'Insumo (Consumible)', value: 'INSUMO' },
  ];

  categoriasProducto = [
    { name: 'Bebidas Alcohólicas', value: 'BEBIDAS_ALCOHOLICAS' },
    { name: 'Bebidas Sin Alcohol', value: 'BEBIDAS_SIN_ALCOHOL' },
    { name: 'Licores', value: 'LICORES' },
    { name: 'Cervezas', value: 'CERVEZAS' },
    { name: 'Cócteles', value: 'COCTELES' },
    { name: 'Otros', value: 'OTROS' },
  ];

  categoriasInsumo = [
    { name: 'Descartables', value: 'DESCARTABLES' },
    { name: 'Frutas', value: 'FRUTAS' },
    { name: 'Hielo', value: 'HIELO' },
    { name: 'Guarniciones', value: 'GUARNICIONES' },
    { name: 'Limpieza', value: 'LIMPIEZA' },
    { name: 'Otros', value: 'OTROS' },
  ];

  unidadMedidaOptions = [
    { name: 'Unidad', value: 'UNIDAD' },
    { name: 'Gramos', value: 'GRAMOS' },
    { name: 'Kilos', value: 'KILOS' },
    { name: 'Litros', value: 'LITROS' },
    { name: 'Mililitros', value: 'MILILITROS' },
  ];

  constructor(
    private readonly productoService: ProductoService,
    private readonly messageService: MessageService,
    private readonly _alertService: AlertService,
    private readonly formBuilder: FormBuilder,
  ) {}

  ngOnInit() {
    this.loadForm();
  }

  ngOnChanges() {
    if (this.displayModal) {
      this.isEditMode = !!this.productoId;

      if (this.isEditMode && this.productoId) {
        this.productoService.getProductoById(this.productoId).subscribe(
          (response) => {
            if (response && response.data) {
              this.loadProductoData(response.data);
            }
          },
          (error) => {
            this._alertService.showError(
              'Error',
              'No se pudo cargar la información del producto',
            );
          },
        );
      } else {
        this.resetForm();
      }
    }
  }

  loadForm() {
    this.frmProducto = this.formBuilder.group({
      id: [null],
      nombre: [null, [Validators.required, Validators.minLength(3)]],
      tipo_venta: ['UNIDAD'],
      tipo: ['PRODUCTO', Validators.required],
      categoria: [null],
      unidad_medida: [null],
      activo: [true],
    });
    this.frmProducto.get('tipo')?.valueChanges.subscribe((tipo) => {
      this.onTipoChange(tipo);
    });
  }

  loadProductoData(producto: ProductoModel) {
    this.frmProducto.patchValue({
      id: producto.id,
      nombre: producto.nombre,
      tipo_venta: producto.tipo_venta,
      tipo: producto.tipo,
      categoria: producto.categoria,
      unidad_medida: producto.unidad_medida,
      activo: producto.activo === 1,
    });
  }

  resetForm() {
    this.frmProducto.reset({
      activo: true,
      tipo_venta: 'UNIDAD',
      tipo: 'PRODUCTO',
    });
  }

  get categoriasDisponibles() {
    const tipo = this.frmProducto.get('tipo')?.value;
    return tipo === 'PRODUCTO'
      ? this.categoriasProducto
      : this.categoriasInsumo;
  }

  get mostrarUnidadMedida() {
    return this.frmProducto.get('tipo')?.value === 'INSUMO';
  }

  onTipoChange(tipo: string) {
    const tipoVentaControl = this.frmProducto.get('tipo_venta');
    const unidadMedidaControl = this.frmProducto.get('unidad_medida');

    this.frmProducto.patchValue({
      categoria: null,
    });

    if (tipo === 'PRODUCTO') {
      // Si es PRODUCTO: tipo_venta es requerido, unidad_medida se limpia
      tipoVentaControl?.setValidators([Validators.required]);
      tipoVentaControl?.patchValue('UNIDAD');
      unidadMedidaControl?.patchValue(null);
    } else {
      // Si es INSUMO: tipo_venta se limpia y no es requerido
      tipoVentaControl?.clearValidators();
      tipoVentaControl?.patchValue(null);
    }

    tipoVentaControl?.updateValueAndValidity();
  }

  async buildDataProducto(): Promise<CreateProductoDto | UpdateProductoDto> {
    const formValue = this.frmProducto.value;

    return {
      id: formValue.id,
      nombre: formValue.nombre,
      tipo_venta: formValue.tipo_venta,
      // ✅ NUEVOS CAMPOS
      tipo: formValue.tipo,
      categoria: formValue.categoria,
      unidad_medida: formValue.unidad_medida,
      // FIN NUEVOS CAMPOS
      activo: formValue.activo ? 1 : 2,
    };
  }

  async buildSaveProducto(): Promise<void> {
    const msgSystem = 'Alerta del sistema';
    const msgText = 'Complete el formulario correctamente';

    if (this.isFormInvalid()) {
      this.markFormAsTouched();
      this._alertService.showError(msgSystem, msgText);
      return;
    }

    this.isSubmitting = true;
    const data: CreateProductoDto | UpdateProductoDto =
      await this.buildDataProducto();

    try {
      const response = await this.saveProducto(data);
      if (response) {
        this.handleResponse(response);
      }
    } catch (error: any) {
      const msg = 'Error al guardar el producto';
      this.showErrorMessage(msg);
    } finally {
      this.isSubmitting = false;
    }
  }

  isFormInvalid(): boolean {
    return this.frmProducto.invalid;
  }

  markFormAsTouched(): void {
    this.frmProducto.markAllAsTouched();
  }

  private handleResponse(
    response: ResponseModel<ProductoModel | boolean>,
  ): void {
    if (response?.status === 200 || response?.status === 201) {
      const message = this.isEditMode
        ? 'Producto actualizado correctamente'
        : 'Producto creado correctamente';

      this.messageService.add({
        severity: 'success',
        summary: 'Operación exitosa',
        detail: message,
        life: 5000,
      });

      if (response.data && typeof response.data === 'object') {
        this.productoSaved.emit(response.data as ProductoModel);
      }

      this.closeModal();
    }
  }
  get mostrarTipoVenta() {
    return this.frmProducto.get('tipo')?.value === 'PRODUCTO';
  }
  private async saveProducto(
    data: CreateProductoDto | UpdateProductoDto,
  ): Promise<ResponseModel<boolean | ProductoModel> | void> {
    if (this.isEditMode) {
      return await lastValueFrom(
        this.productoService.updateProducto(data as UpdateProductoDto),
      ).catch((error) => {
        this.showErrorMessage(error.message);
      });
    } else {
      return lastValueFrom(
        this.productoService.createProducto(data as CreateProductoDto),
      ).catch((error) => {
        this.showErrorMessage(error.message);
      });
    }
  }

  private showErrorMessage(message: string): void {
    const msgSystem = 'Alerta del sistema';
    this._alertService.showError(msgSystem, message ?? 'Error desconocido');
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

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
import { InputNumberModule } from 'primeng/inputnumber';
import { ToastModule } from 'primeng/toast';
import { DialogModule } from 'primeng/dialog';
import { ButtonModule } from 'primeng/button';
import { DividerModule } from 'primeng/divider';
import { InventarioModel } from '../../../../core/models/inventario/inventario.model';
import { ProductoListDto } from '../../../../core/models/producto/producto-list.dto';
import { InventarioService } from '../../../../core/services/inventario.service';
import { ProductoService } from '../../../../core/services/producto.service';
import { AlertService } from '../../../../../shared/pipes/alert.service';
import { UpdateInventarioDto } from '../../../../core/models/inventario/update-inventario.dto';
import { CreateInventarioDto } from '../../../../core/models/inventario/create-inventario.dto';
import { ResponseModel } from '../../../../../shared/models/responde.models';

@Component({
  selector: 'app-form-inventario',
  standalone: true,
  templateUrl: './form-inventario.component.html',
  styleUrls: ['./form-inventario.component.scss'],
  providers: [MessageService, AlertService],
  imports: [
    CommonModule,
    ReactiveFormsModule,
    FormsModule,
    InputSwitchModule,
    DropdownModule,
    InputTextModule,
    InputNumberModule,
    ToastModule,
    DialogModule,
    ButtonModule,
    DividerModule,
  ],
})
export class FormInventarioComponent implements OnInit {
  @Input() displayModal: boolean = false;
  @Input() inventarioId: number | null = null;
  @Input() slug: string = 'create';
  @Output() modalClosed = new EventEmitter<void>();
  @Output() inventarioSaved = new EventEmitter<InventarioModel>();

  public frmInventario!: FormGroup;
  public isEditMode: boolean = false;
  public isSubmitting: boolean = false;
  public productos: ProductoListDto[] = [];

  constructor(
    private readonly inventarioService: InventarioService,
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
      this.isEditMode = !!this.inventarioId;

      if (this.isEditMode && this.inventarioId) {
        this.inventarioService.getInventarioById(this.inventarioId).subscribe(
          (response) => {
            if (response && response.data) {
              this.loadInventarioData(response.data);
            }
          },
          (error) => {
            this._alertService.showError(
              'Error',
              'No se pudo cargar la información del inventario',
            );
          },
        );
      } else {
        this.resetForm();
      }
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
    this.frmInventario = this.formBuilder.group({
      id: [null],
      producto_id: [null, Validators.required],
      stock: [0, [Validators.required, Validators.min(0)]],
      costo_unitario: [0, [Validators.required, Validators.min(0)]],
      precio_venta: [0, [Validators.required, Validators.min(0)]],
      activo: [true],
    });
  }

  loadInventarioData(inventario: InventarioModel) {
    this.frmInventario.patchValue({
      id: inventario.id,
      producto_id: inventario.producto_id,
      stock: inventario.stock,
      costo_unitario: inventario.costo_unitario,
      precio_venta: inventario.precio_venta,
      activo: inventario.activo === 1,
    });
  }

  resetForm() {
    this.frmInventario.reset({
      activo: true,
      stock: 0,
      costo_unitario: 0,
      precio_venta: 0,
    });
  }

  async buildDataInventario(): Promise<
    CreateInventarioDto | UpdateInventarioDto
  > {
    const formValue = this.frmInventario.value;

    return {
      id: formValue.id,
      producto_id: formValue.producto_id,
      stock: formValue.stock,
      costo_unitario: formValue.costo_unitario,
      precio_venta: formValue.precio_venta,
      activo: formValue.activo ? 1 : 2,
    };
  }

  async buildSaveInventario(): Promise<void> {
    const msgSystem = 'Alerta del sistema';
    const msgText = 'Complete el formulario correctamente';

    if (this.isFormInvalid()) {
      this.markFormAsTouched();
      this._alertService.showError(msgSystem, msgText);
      return;
    }

    this.isSubmitting = true;
    const data: CreateInventarioDto | UpdateInventarioDto =
      await this.buildDataInventario();

    try {
      const response = await this.saveInventario(data);
      if (response) {
        this.handleResponse(response);
      }
    } catch (error: any) {
      const msg = 'Error al guardar el inventario';
      this.showErrorMessage(msg);
    } finally {
      this.isSubmitting = false;
    }
  }

  isFormInvalid(): boolean {
    return this.frmInventario.invalid;
  }

  markFormAsTouched(): void {
    this.frmInventario.markAllAsTouched();
  }

  private handleResponse(
    response: ResponseModel<InventarioModel | boolean>,
  ): void {
    if (response?.status === 200 || response?.status === 201) {
      const message = this.isEditMode
        ? 'Inventario actualizado correctamente'
        : 'Inventario creado correctamente';

      this.messageService.add({
        severity: 'success',
        summary: 'Operación exitosa',
        detail: message,
        life: 5000,
      });

      if (response.data && typeof response.data === 'object') {
        this.inventarioSaved.emit(response.data as InventarioModel);
      }

      this.closeModal();
    }
  }

  private async saveInventario(
    data: CreateInventarioDto | UpdateInventarioDto,
  ): Promise<ResponseModel<boolean | InventarioModel> | void> {
    if (this.isEditMode) {
      return await lastValueFrom(
        this.inventarioService.updateInventario(data as UpdateInventarioDto),
      ).catch((error) => {
        this.showErrorMessage(error.message);
      });
    } else {
      return lastValueFrom(
        this.inventarioService.createInventario(data as CreateInventarioDto),
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

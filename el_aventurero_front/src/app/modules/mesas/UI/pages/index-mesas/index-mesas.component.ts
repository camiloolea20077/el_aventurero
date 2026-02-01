import { CommonModule } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { ConfirmationService, MessageService } from 'primeng/api';
import { ButtonModule } from 'primeng/button';
import { DialogModule } from 'primeng/dialog';
import { DropdownModule } from 'primeng/dropdown';
import { InputNumberModule } from 'primeng/inputnumber';
import { ToastModule } from 'primeng/toast';
import { ConfirmDialogModule } from 'primeng/confirmdialog';
import { lastValueFrom } from 'rxjs';
import { MesaModel } from '../../../../../core/models/mesa/mesa.model';
import { ConsumoMesaModel } from '../../../../../core/models/consumo-mesa/consumo-mesa.model';
import { ProductoListDto } from '../../../../../core/models/producto/producto-list.dto';
import { MesaService } from '../../../../../core/services/mesa.service';
import { ConsumoMesaService } from '../../../../../core/services/consumo-mesa.service';
import { ProductoService } from '../../../../../core/services/producto.service';
import { VentaService } from '../../../../../core/services/venta.service';
import { CreateConsumoMesaDto } from '../../../../../core/models/consumo-mesa/create-consumo-mesa.dto';
import { CreateVentaDto } from '../../../../../core/models/venta/create-venta.dto';

@Component({
  selector: 'app-index-mesa',
  standalone: true,
  templateUrl: './index-mesas.component.html',
  styleUrls: ['./index-mesas.component.scss'],
  providers: [MessageService, ConfirmationService],
  imports: [
    CommonModule,
    FormsModule,
    ButtonModule,
    DialogModule,
    DropdownModule,
    InputNumberModule,
    ToastModule,
    ConfirmDialogModule,
  ],
})
export class IndexMesasComponent implements OnInit {
  // Mesas
  mesas: MesaModel[] = [];
  loading: boolean = true;

  // Dialog de consumo
  displayConsumoDialog: boolean = false;
  mesaSeleccionada: MesaModel | null = null;
  consumos: ConsumoMesaModel[] = [];
  totalMesa: number = 0;

  // Productos para el dropdown
  productos: ProductoListDto[] = [];
  productoSeleccionado: ProductoListDto | null = null;
  cantidad: number = 1;
  loadingConsumo: boolean = false;

  // Métodos de pago
  metodosPago = [
    { label: 'Efectivo', value: 'EFECTIVO' },
    { label: 'Tarjeta', value: 'TARJETA' },
    { label: 'Transferencia', value: 'TRANSFERENCIA' },
    { label: 'Nequi/Daviplata', value: 'DIGITAL' },
  ];
  metodoPagoSeleccionado: string = 'EFECTIVO';

  constructor(
    private mesaService: MesaService,
    private consumoMesaService: ConsumoMesaService,
    private productoService: ProductoService,
    private ventaService: VentaService,
    private messageService: MessageService,
    private confirmationService: ConfirmationService,
  ) {}

  ngOnInit(): void {
    this.cargarMesas();
    this.cargarProductos();
  }

  async cargarMesas(): Promise<void> {
    this.loading = true;
    try {
      const response = await lastValueFrom(this.mesaService.getAllMesas());
      this.mesas = response.data || [];
    } catch (error) {
      this.messageService.add({
        severity: 'error',
        summary: 'Error',
        detail: 'No se pudieron cargar las mesas',
      });
    } finally {
      this.loading = false;
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

  async abrirMesa(mesa: MesaModel): Promise<void> {
    this.mesaSeleccionada = mesa;
    this.displayConsumoDialog = true;
    this.productoSeleccionado = null;
    this.cantidad = 1;
    await this.cargarConsumosMesa(mesa.id);
  }

  async cargarConsumosMesa(mesaId: number): Promise<void> {
    this.loadingConsumo = true;
    try {
      const response = await lastValueFrom(
        this.consumoMesaService.getConsumosByMesaId(mesaId),
      );
      this.consumos = response.data || [];
      this.calcularTotal();
    } catch (error) {
      this.consumos = [];
      this.totalMesa = 0;
    } finally {
      this.loadingConsumo = false;
    }
  }

  calcularTotal(): void {
    this.totalMesa = this.consumos.reduce(
      (sum, consumo) => sum + (consumo.subtotal || 0),
      0,
    );
  }

  async agregarProducto(): Promise<void> {
    if (!this.productoSeleccionado || !this.mesaSeleccionada) {
      this.messageService.add({
        severity: 'warn',
        summary: 'Advertencia',
        detail: 'Seleccione un producto',
      });
      return;
    }

    if (this.cantidad <= 0) {
      this.messageService.add({
        severity: 'warn',
        summary: 'Advertencia',
        detail: 'La cantidad debe ser mayor a 0',
      });
      return;
    }

    const createDto: CreateConsumoMesaDto = {
      mesa_id: this.mesaSeleccionada.id,
      producto_id: this.productoSeleccionado.id,
      cantidad: this.cantidad,
    };

    try {
      await lastValueFrom(this.consumoMesaService.createConsumoMesa(createDto));

      this.messageService.add({
        severity: 'success',
        summary: 'Éxito',
        detail: 'Producto agregado correctamente',
      });

      // Recargar consumos y mesas
      await this.cargarConsumosMesa(this.mesaSeleccionada.id);
      await this.cargarMesas();

      // Limpiar formulario
      this.productoSeleccionado = null;
      this.cantidad = 1;
    } catch (error: any) {
      this.messageService.add({
        severity: 'error',
        summary: 'Error',
        detail: error.error?.message || 'No se pudo agregar el producto',
      });
    }
  }

  async eliminarConsumo(consumo: ConsumoMesaModel): Promise<void> {
    this.confirmationService.confirm({
      message: '¿Está seguro de eliminar este producto?',
      header: 'Confirmar Eliminación',
      icon: 'pi pi-exclamation-triangle',
      accept: async () => {
        try {
          await lastValueFrom(
            this.consumoMesaService.deleteConsumoMesa(consumo.id),
          );

          this.messageService.add({
            severity: 'success',
            summary: 'Éxito',
            detail: 'Producto eliminado correctamente',
          });

          if (this.mesaSeleccionada) {
            await this.cargarConsumosMesa(this.mesaSeleccionada.id);
            await this.cargarMesas();
          }
        } catch (error) {
          this.messageService.add({
            severity: 'error',
            summary: 'Error',
            detail: 'No se pudo eliminar el producto',
          });
        }
      },
    });
  }

  async cerrarCuenta(): Promise<void> {
    if (!this.mesaSeleccionada) return;

    if (this.consumos.length === 0) {
      this.messageService.add({
        severity: 'warn',
        summary: 'Advertencia',
        detail: 'No hay consumos para cerrar',
      });
      return;
    }

    this.confirmationService.confirm({
      message: `¿Cerrar cuenta de Mesa ${this.mesaSeleccionada.numero}?<br>Total: $${this.totalMesa.toLocaleString()}`,
      header: 'Cerrar Cuenta',
      icon: 'pi pi-check-circle',
      accept: async () => {
        const createVenta: CreateVentaDto = {
          mesa_id: this.mesaSeleccionada!.id,
          metodo_pago: this.metodoPagoSeleccionado,
        };

        try {
          await lastValueFrom(this.ventaService.createVenta(createVenta));

          this.messageService.add({
            severity: 'success',
            summary: 'Éxito',
            detail: 'Cuenta cerrada correctamente',
            life: 5000,
          });

          this.displayConsumoDialog = false;
          await this.cargarMesas();
        } catch (error) {
          this.messageService.add({
            severity: 'error',
            summary: 'Error',
            detail: 'No se pudo cerrar la cuenta',
          });
        }
      },
    });
  }

  cerrarDialog(): void {
    this.displayConsumoDialog = false;
    this.mesaSeleccionada = null;
    this.consumos = [];
    this.totalMesa = 0;
    this.productoSeleccionado = null;
    this.cantidad = 1;
  }

  getMesaClass(mesa: MesaModel): string {
    return mesa.estado === 'LIBRE' ? 'mesa-libre' : 'mesa-ocupada';
  }

  getMesaIcon(mesa: MesaModel): string {
    return mesa.estado === 'LIBRE' ? 'pi pi-check-circle' : 'pi pi-users';
  }
}

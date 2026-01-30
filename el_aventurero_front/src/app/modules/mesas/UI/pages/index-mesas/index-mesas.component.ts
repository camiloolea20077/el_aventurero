import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import {
  FormBuilder,
  FormGroup,
  Validators,
  ReactiveFormsModule,
} from '@angular/forms';
import { TableModule } from 'primeng/table';
import { CardModule } from 'primeng/card';
import { ButtonModule } from 'primeng/button';
import { DialogModule } from 'primeng/dialog';
import { InputTextModule } from 'primeng/inputtext';
import { DropdownModule } from 'primeng/dropdown';
import { InputNumberModule } from 'primeng/inputnumber';
import { ToastModule } from 'primeng/toast';
import { MessageService } from 'primeng/api';
import {
  ConsumoMesa,
  Mesa,
  Producto,
} from '../../../../../../shared/interfaces/interfaces';

@Component({
  selector: 'app-mesas',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    TableModule,
    CardModule,
    ButtonModule,
    DialogModule,
    InputTextModule,
    DropdownModule,
    InputNumberModule,
    ToastModule,
  ],
  providers: [MessageService],
  templateUrl: './index-mesas.component.html',
  styleUrls: ['./index-mesas.component.css'],
})
export class IndexMesasComponent implements OnInit {
  mesas: Mesa[] = [];
  productos: Producto[] = [];
  consumos: ConsumoMesa[] = [];

  displayConsumoDialog = false;
  displayAdminDialog = false;
  displayMesaFormDialog = false;
  editModeMesa = false;

  mesaSeleccionada: Mesa | null = null;
  consumoForm!: FormGroup;
  mesaForm!: FormGroup;

  constructor(
    private fb: FormBuilder,
    private messageService: MessageService,
  ) {}

  ngOnInit() {
    this.initForms();
    this.loadMesas();
    this.loadProductos();
  }

  initForms() {
    this.consumoForm = this.fb.group({
      producto_id: ['', Validators.required],
      cantidad: [1, [Validators.required, Validators.min(1)]],
    });

    this.mesaForm = this.fb.group({
      numero: ['', [Validators.required, Validators.min(1)]],
    });
  }

  loadMesas() {
    // Datos de ejemplo - aquí conectarías con tu servicio
    this.mesas = [
      {
        id: 1,
        numero: 1,
        estado: 'LIBRE',
        total_acumulado: 0,
        activo: 1,
        created_at: new Date(),
      },
      {
        id: 2,
        numero: 2,
        estado: 'OCUPADA',
        total_acumulado: 45000,
        activo: 1,
        created_at: new Date(),
      },
      {
        id: 3,
        numero: 3,
        estado: 'LIBRE',
        total_acumulado: 0,
        activo: 1,
        created_at: new Date(),
      },
      {
        id: 4,
        numero: 4,
        estado: 'OCUPADA',
        total_acumulado: 78000,
        activo: 1,
        created_at: new Date(),
      },
      {
        id: 5,
        numero: 5,
        estado: 'LIBRE',
        total_acumulado: 0,
        activo: 1,
        created_at: new Date(),
      },
      {
        id: 6,
        numero: 6,
        estado: 'LIBRE',
        total_acumulado: 0,
        activo: 1,
        created_at: new Date(),
      },
      {
        id: 7,
        numero: 7,
        estado: 'OCUPADA',
        total_acumulado: 32000,
        activo: 1,
        created_at: new Date(),
      },
      {
        id: 8,
        numero: 8,
        estado: 'LIBRE',
        total_acumulado: 0,
        activo: 1,
        created_at: new Date(),
      },
      {
        id: 9,
        numero: 9,
        estado: 'LIBRE',
        total_acumulado: 0,
        activo: 1,
        created_at: new Date(),
      },
      {
        id: 10,
        numero: 10,
        estado: 'LIBRE',
        total_acumulado: 0,
        activo: 1,
        created_at: new Date(),
      },
    ];
  }

  loadProductos() {
    // Datos de ejemplo
    this.productos = [
      { id: 1, nombre: 'Cerveza Corona', tipo_venta: 'UNIDAD', activo: 1 },
      {
        id: 2,
        nombre: 'Aguardiente Antioqueño',
        tipo_venta: 'BOTELLA',
        activo: 1,
      },
      { id: 3, nombre: 'Ron Medellín', tipo_venta: 'BOTELLA', activo: 1 },
      { id: 4, nombre: 'Coca Cola', tipo_venta: 'UNIDAD', activo: 1 },
      { id: 5, nombre: "Whisky Buchanan's", tipo_venta: 'BOTELLA', activo: 1 },
    ];
  }

  abrirMesa(mesa: Mesa) {
    this.mesaSeleccionada = mesa;
    this.consumos = this.loadConsumosMesa(mesa.id!);
    this.consumoForm.reset({ cantidad: 1 });
    this.displayConsumoDialog = true;
  }

  loadConsumosMesa(mesaId: number): ConsumoMesa[] {
    // Aquí cargarías los consumos desde tu servicio
    // Datos de ejemplo para mesas ocupadas
    if (mesaId === 2) {
      return [
        {
          id: 1,
          mesa_id: 2,
          producto_id: 1,
          producto: {
            id: 1,
            nombre: 'Cerveza Corona',
            tipo_venta: 'UNIDAD',
            activo: 1,
          },
          cantidad: 3,
          precio_unitario: 5000,
          subtotal: 15000,
          activo: 1,
        },
        {
          id: 2,
          mesa_id: 2,
          producto_id: 2,
          producto: {
            id: 2,
            nombre: 'Aguardiente Antioqueño',
            tipo_venta: 'BOTELLA',
            activo: 1,
          },
          cantidad: 1,
          precio_unitario: 30000,
          subtotal: 30000,
          activo: 1,
        },
      ];
    }
    return [];
  }

  agregarProducto() {
    if (this.consumoForm.valid && this.mesaSeleccionada) {
      const formValue = this.consumoForm.value;
      const producto = this.productos.find(
        (p) => p.id === formValue.producto_id,
      );

      if (producto) {
        // Precio de ejemplo - aquí lo obtendrías del inventario
        const precioUnitario = 5000;
        const subtotal = formValue.cantidad * precioUnitario;

        const nuevoConsumo: ConsumoMesa = {
          mesa_id: this.mesaSeleccionada.id!,
          producto_id: formValue.producto_id,
          producto: producto,
          cantidad: formValue.cantidad,
          precio_unitario: precioUnitario,
          subtotal: subtotal,
          activo: 1,
        };

        this.consumos.push(nuevoConsumo);

        // Actualizar estado de la mesa
        if (this.mesaSeleccionada.estado === 'LIBRE') {
          this.mesaSeleccionada.estado = 'OCUPADA';
        }

        this.consumoForm.reset({ cantidad: 1 });

        this.messageService.add({
          severity: 'success',
          summary: 'Producto agregado',
          detail: `${producto.nombre} agregado a la cuenta`,
        });
      }
    }
  }

  eliminarConsumo(index: number) {
    this.consumos.splice(index, 1);

    this.messageService.add({
      severity: 'info',
      summary: 'Producto eliminado',
      detail: 'Producto removido de la cuenta',
    });
  }

  calcularTotal(): number {
    return this.consumos.reduce((sum, consumo) => sum + consumo.subtotal, 0);
  }

  cerrarCuenta() {
    if (
      this.mesaSeleccionada &&
      confirm('¿Desea cerrar la cuenta de esta mesa?')
    ) {
      const total = this.calcularTotal();

      // Aquí guardarías la venta en la BD

      this.messageService.add({
        severity: 'success',
        summary: 'Cuenta cerrada',
        detail: `Total: ${total.toLocaleString('es-CO', { style: 'currency', currency: 'COP' })}`,
      });

      // Limpiar mesa
      this.mesaSeleccionada.estado = 'LIBRE';
      this.mesaSeleccionada.total_acumulado = 0;
      this.consumos = [];
      this.displayConsumoDialog = false;
    }
  }

  showAdminDialog() {
    this.displayAdminDialog = true;
  }

  showMesaForm() {
    this.editModeMesa = false;
    this.mesaForm.reset();
    this.displayMesaFormDialog = true;
  }

  editMesa(mesa: Mesa) {
    this.editModeMesa = true;
    this.mesaForm.patchValue({ numero: mesa.numero });
    this.displayMesaFormDialog = true;
  }

  saveMesa() {
    if (this.mesaForm.valid) {
      this.messageService.add({
        severity: 'success',
        summary: 'Éxito',
        detail: this.editModeMesa ? 'Mesa actualizada' : 'Mesa creada',
      });
      this.displayMesaFormDialog = false;
      this.loadMesas();
    }
  }

  deleteMesa(id: number | undefined) {
    if (confirm('¿Eliminar esta mesa?')) {
      this.messageService.add({
        severity: 'success',
        summary: 'Mesa eliminada',
      });
      this.loadMesas();
    }
  }
}

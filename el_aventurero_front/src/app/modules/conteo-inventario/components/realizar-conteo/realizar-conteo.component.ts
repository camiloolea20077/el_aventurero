import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { MessageService } from 'primeng/api';
import { lastValueFrom } from 'rxjs';

// PrimeNG
import { DialogModule } from 'primeng/dialog';
import { ButtonModule } from 'primeng/button';
import { InputTextModule } from 'primeng/inputtext';
import { DropdownModule } from 'primeng/dropdown';
import { CalendarModule } from 'primeng/calendar';
import { ToastModule } from 'primeng/toast';
import { TableModule } from 'primeng/table';
import { InputNumberModule } from 'primeng/inputnumber';
import { TagModule } from 'primeng/tag';
import { DividerModule } from 'primeng/divider';
import { AutoCompleteModule } from 'primeng/autocomplete';
import { FormsModule } from '@angular/forms';
import { ConfirmDialogModule } from 'primeng/confirmdialog';
import { TooltipModule } from 'primeng/tooltip';
import { CardModule } from 'primeng/card';
import { ProductoModel } from '../../../../core/models/producto/producto.model';
import { ProductoListDto } from '../../../../core/models/producto/producto-list.dto';
import { ConteoInventarioModel } from '../../../../core/models/conteo-inventario/conteo-inventario.model';
import { ConteoInventarioService } from '../../../../core/services/conteo-inventario.service';
import { DetalleConteoModel } from '../../../../core/models/conteo-inventario/detalle-conteo.model';
import { ProductoService } from '../../../../core/services/producto.service';
import { InventarioService } from '../../../../core/services/inventario.service';
import { InventarioModel } from '../../../../core/models/inventario/inventario.model';
import { AlertService } from '../../../../../shared/pipes/alert.service';

// Servicios y Modelos


// Componente Justificar Diferencia
// import { JustificarDiferenciaComponent } from '../justificar-diferencia/justificar-diferencia.component';

interface ProductoConteo {
    producto: ProductoListDto;
    stock_sistema: number;
    stock_fisico: number;
    diferencia: number;
    guardado: boolean;
    detalle_id?: number;
}

@Component({
    selector: 'app-realizar-conteo',
    standalone: true,
    templateUrl: './realizar-conteo.component.html',
    styleUrls: ['./realizar-conteo.component.scss'],
  imports: [
        CommonModule,
        FormsModule,
        ReactiveFormsModule,
        DialogModule,
        ButtonModule,
        InputTextModule,
        DropdownModule,
        CalendarModule,
        ToastModule,
        ConfirmDialogModule,
        TooltipModule,
        CardModule,
        TableModule,
        InputNumberModule,
        TagModule,
        DividerModule,
        AutoCompleteModule,
        // RealizarConteoComponent,
        // DetalleConteoComponent,
    ],
})
export class RealizarConteoComponent implements OnInit {
    @Input() displayModal: boolean = false;
    @Output() modalClosed = new EventEmitter<void>();

    // Formulario
    public frmConteo!: FormGroup;
    public tiposConteo = [
        { label: 'Periódico (Semanal/Mensual)', value: 'PERIODICO' },
        { label: 'Cíclico (Rotar productos)', value: 'CICLICO' },
        { label: 'Anual (Inventario completo)', value: 'ANUAL' },
    ];

    // Conteo actual
    public conteoActual: ConteoInventarioModel | null = null;
    public productosConteo: ProductoConteo[] = [];

    // Productos disponibles
    public productos: ProductoListDto[] = [];
    public productosFiltrados: ProductoListDto[] = [];
    public productoSeleccionado: ProductoListDto | null = null;
    
    // Inventario para obtener stock
    public inventarios: InventarioModel[] = [];

    // Estados
    public isSubmitting: boolean = false;
    public paso: 'INICIAR' | 'CONTEO' = 'INICIAR';

    // Modal Justificar
    public showJustificarModal: boolean = false;
    public detalleParaJustificar: DetalleConteoModel | null = null;

    constructor(
        private readonly fb: FormBuilder,
        private readonly conteoInventarioService: ConteoInventarioService,
        private readonly productoService: ProductoService,
        private readonly inventarioService: InventarioService,
        private readonly messageService: MessageService,
        private readonly _alertService: AlertService
    ) {
        this.buildForm();
    }

    ngOnInit() {
        if (this.displayModal) {
            this.cargarProductos();
        }
    }

    ngOnChanges() {
        if (this.displayModal) {
            this.cargarProductos();
            this.resetear();
        }
    }

    buildForm(): void {
        this.frmConteo = this.fb.group({
            fecha: [new Date(), Validators.required],
            tipo: ['PERIODICO', Validators.required],
        });
    }

    async cargarProductos(): Promise<void> {
        try {
            // Cargar productos e inventario en paralelo
            const [productosResponse, inventarioResponse] = await Promise.all([
                lastValueFrom(this.productoService.getAllProductos()),
                lastValueFrom(this.inventarioService.getAllInventario())
            ]);
            
            this.productos = productosResponse.data;
            this.inventarios = inventarioResponse.data;
        } catch (error) {
            console.error('Error cargando productos:', error);
            this._alertService.showError('Error', 'No se pudieron cargar los productos');
        }
    }

    buscarProductos(event: any): void {
        const query = event.query.toLowerCase();
        this.productosFiltrados = this.productos.filter(p =>
            p.nombre.toLowerCase().includes(query)
        );
    }

    async iniciarConteo(): Promise<void> {
        if (this.frmConteo.invalid) {
            this._alertService.showError('Error', 'Por favor completa todos los campos');
            return;
        }

        this.isSubmitting = true;

        try {
            const fecha = this.frmConteo.get('fecha')?.value;
            const fechaStr = this.formatDate(fecha);

            const response = await lastValueFrom(
                this.conteoInventarioService.iniciarConteo({
                    fecha: fechaStr,
                    tipo: this.frmConteo.get('tipo')?.value,
                })
            );

            this.conteoActual = response.data;
            this.paso = 'CONTEO';

            this.messageService.add({
                severity: 'success',
                summary: 'Éxito',
                detail: 'Conteo iniciado correctamente',
                life: 3000,
            });
        } catch (error: any) {
            this._alertService.showError(
                'Error',
                error.error?.message || 'No se pudo iniciar el conteo'
            );
        } finally {
            this.isSubmitting = false;
        }
    }

    agregarProducto(): void {
        if (!this.productoSeleccionado || !this.conteoActual) return;

        // Verificar si ya está agregado
        const yaAgregado = this.productosConteo.some(
            pc => pc.producto.id === this.productoSeleccionado!.id
        );

        if (yaAgregado) {
            this._alertService.showError('Error', 'Este producto ya fue agregado al conteo');
            return;
        }

        // Obtener stock del sistema (desde el inventario del producto)
        const inventarioProducto = this.inventarios.find(inv => inv.producto_id === this.productoSeleccionado!.id);
        const stockSistema = inventarioProducto?.stock || 0;

        this.productosConteo.push({
            producto: this.productoSeleccionado,
            stock_sistema: stockSistema,
            stock_fisico: 0,
            diferencia: 0,
            guardado: false,
        });

        this.productoSeleccionado = null;
    }

    onStockFisicoChange(productoConteo: ProductoConteo): void {
        productoConteo.diferencia = productoConteo.stock_fisico - productoConteo.stock_sistema;
    }

    async guardarDetalle(productoConteo: ProductoConteo): Promise<void> {
        if (!this.conteoActual) return;

        if (productoConteo.stock_fisico < 0) {
            this._alertService.showError('Error', 'El stock físico no puede ser negativo');
            return;
        }

        try {
            const response = await lastValueFrom(
                this.conteoInventarioService.registrarDetalle({
                    conteo_id: this.conteoActual.id,
                    producto_id: productoConteo.producto.id,
                    stock_fisico: productoConteo.stock_fisico,
                })
            );

            productoConteo.guardado = true;
            productoConteo.detalle_id = response.data.id;

            this.messageService.add({
                severity: 'success',
                summary: 'Guardado',
                detail: `${productoConteo.producto.nombre} registrado`,
                life: 2000,
            });
        } catch (error: any) {
            this._alertService.showError(
                'Error',
                error.error?.message || 'No se pudo guardar el detalle'
            );
        }
    }

    eliminarProducto(index: number): void {
        const productoConteo = this.productosConteo[index];

        if (productoConteo.guardado) {
            this._alertService.showError(
                'Error',
                'No se puede eliminar un producto ya guardado'
            );
            return;
        }

        this.productosConteo.splice(index, 1);
    }

    async completarConteo(): Promise<void> {
        if (!this.conteoActual) return;

        // Verificar que todos los productos estén guardados
        const hayPendientes = this.productosConteo.some(pc => !pc.guardado);
        if (hayPendientes) {
            this._alertService.showError(
                'Error',
                'Debes guardar todos los productos antes de completar el conteo'
            );
            return;
        }

        // Verificar si hay diferencias sin justificar
        const diferencias = this.productosConteo.filter(pc => pc.diferencia !== 0);
        if (diferencias.length > 0) {
            this._alertService.showError(
                'Diferencias Detectadas',
                `Hay ${diferencias.length} producto(s) con diferencias. Debes justificar todas las diferencias antes de completar el conteo.`
            );
            return;
        }

        this.isSubmitting = true;

        try {
            await lastValueFrom(
                this.conteoInventarioService.completarConteo(this.conteoActual.id)
            );

            this.messageService.add({
                severity: 'success',
                summary: 'Éxito',
                detail: 'Conteo completado correctamente',
                life: 5000,
            });

            this.closeModal();
        } catch (error: any) {
            this._alertService.showError(
                'Error',
                error.error?.message || 'No se pudo completar el conteo'
            );
        } finally {
            this.isSubmitting = false;
        }
    }

    openJustificarModal(productoConteo: ProductoConteo): void {
        if (!productoConteo.detalle_id) {
            this._alertService.showError('Error', 'Primero debes guardar el producto');
            return;
        }

        this.detalleParaJustificar = {
            id: productoConteo.detalle_id,
            conteo_id: this.conteoActual!.id,
            producto_id: productoConteo.producto.id,
            producto_nombre: productoConteo.producto.nombre,
            stock_sistema: productoConteo.stock_sistema,
            stock_fisico: productoConteo.stock_fisico,
            diferencia: productoConteo.diferencia,
            ajustado: false,
        };

        this.showJustificarModal = true;
    }

    onJustificarModalClosed(): void {
        this.showJustificarModal = false;
        this.detalleParaJustificar = null;
    }

    onDiferenciaJustificada(): void {
        this.showJustificarModal = false;
        this.detalleParaJustificar = null;

        // Recargar el producto ajustado
        if (this.conteoActual) {
            this.recargarDetalles();
        }
    }

    async recargarDetalles(): Promise<void> {
        if (!this.conteoActual) return;

        try {
            const response = await lastValueFrom(
                this.conteoInventarioService.getDetallesByConteoId(this.conteoActual.id)
            );

            // Actualizar productos conteo con datos frescos
            response.data.forEach(detalle => {
                const pc = this.productosConteo.find(
                    p => p.producto.id === detalle.producto_id
                );
                if (pc) {
                    pc.stock_fisico = detalle.stock_fisico;
                    pc.diferencia = detalle.diferencia;
                    pc.guardado = true;
                    pc.detalle_id = detalle.id;
                }
            });
        } catch (error) {
            console.error('Error recargando detalles:', error);
        }
    }

    getDiferenciaSeverity(diferencia: number): 'success' | 'warn' | 'danger' | 'secondary' | 'info' | 'contrast' {
        if (diferencia === 0) return 'success';
        return 'warn';
    }

    getDiferenciaIcon(diferencia: number): string {
        if (diferencia === 0) return 'pi-check';
        if (diferencia > 0) return 'pi-arrow-up';
        return 'pi-arrow-down';
    }

    // Helper para obtener stock del sistema
    getStockSistema(productoId: number): number {
        const inventario = this.inventarios.find(inv => inv.producto_id === productoId);
        return inventario?.stock || 0;
    }

    // Propiedades computadas para el template
    get productosGuardados(): number {
        return this.productosConteo.filter(pc => pc.guardado).length;
    }

    get productosPendientes(): number {
        return this.productosConteo.filter(pc => !pc.guardado).length;
    }

    get productosConDiferencias(): number {
        return this.productosConteo.filter(pc => pc.diferencia !== 0).length;
    }

    formatDate(date: Date): string {
        const year = date.getFullYear();
        const month = String(date.getMonth() + 1).padStart(2, '0');
        const day = String(date.getDate()).padStart(2, '0');
        return `${year}-${month}-${day}`;
    }

    resetear(): void {
        this.paso = 'INICIAR';
        this.conteoActual = null;
        this.productosConteo = [];
        this.productoSeleccionado = null;
        this.frmConteo.reset({
            fecha: new Date(),
            tipo: 'PERIODICO',
        });
    }

    closeModal() {
        this.resetear();
        this.displayModal = false;
        this.modalClosed.emit();
    }

    onModalHide() {
        this.closeModal();
    }
}
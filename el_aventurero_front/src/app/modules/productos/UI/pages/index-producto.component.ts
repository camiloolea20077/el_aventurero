import { CommonModule } from '@angular/common';
import { Component } from '@angular/core';
import { FormBuilder, FormsModule, ReactiveFormsModule } from '@angular/forms';
import { Router, RouterModule } from '@angular/router';
import { ConfirmationService, MessageService } from 'primeng/api';
import { ButtonModule } from 'primeng/button';
import { InputTextModule } from 'primeng/inputtext';
import { TableLazyLoadEvent, TableModule } from 'primeng/table';
import { lastValueFrom } from 'rxjs';
import { ConfirmDialogModule } from 'primeng/confirmdialog';
import { ToastModule } from 'primeng/toast';
import { ProgressSpinnerModule } from 'primeng/progressspinner';
import { TagModule } from 'primeng/tag';
import { FormProductoComponent } from '../../components/form/form-producto.component';
import { ProductoTableModel } from '../../../../core/models/producto/producto-table.model';
import { ColsModel } from '../../../../../shared/models/cols.model';
import { IFilterTable } from '../../../../../shared/models/filter-table';
import { HelpersService } from '../../../../../shared/pipes/helper.service';
import { ProductoService } from '../../../../core/services/producto.service';
import { ProductoModel } from '../../../../core/models/producto/producto.model';

@Component({
  selector: 'app-index-producto',
  standalone: true,
  templateUrl: './index-producto.component.html',
  styleUrls: ['./index-producto.component.scss'],
  providers: [MessageService, ConfirmationService],
  imports: [
    FormsModule,
    RouterModule,
    InputTextModule,
    ConfirmDialogModule,
    ProgressSpinnerModule,
    TagModule,
    ButtonModule,
    ReactiveFormsModule,
    TableModule,
    CommonModule,
    ToastModule,
    FormProductoComponent,
  ],
})
export class IndexProductoComponent {
  // Variables para el modal
  showProductoModal = false;
  selectedProductoId: number | null = null;
  modalSlug: string = 'create';

  public rowSize = 10;
  public totalRecords = 0;
  public loadingTable = true;
  productos: ProductoTableModel[] = [];
  filtersTable!: IFilterTable<any>;

  cols: ColsModel[] = [
    {
      field: 'nombre',
      header: 'Nombre',
      type: 'string',
      nameClass: 'text-left',
    },
    {
      field: 'tipo_venta',
      header: 'Tipo de Venta',
      type: 'string',
      nameClass: 'text-center',
    },
    {
      field: 'activo',
      header: 'Estado',
      type: 'icon',
      nameClass: 'text-center',
    },
  ];

  globalFilter: string = '';
  selectedProducto: any = null;

  constructor(
    private fb: FormBuilder,
    readonly _helperService: HelpersService,
    private readonly _confirmationService: ConfirmationService,
    private productoService: ProductoService,
    private router: Router,
    private messageService: MessageService,
  ) {}

  ngOnInit(): void {
    this.loadTable;
    this.loadColumnActions();
  }

  async loadTable(lazyTable: TableLazyLoadEvent): Promise<void> {
    this.loadingTable = true;
    this.filtersTable = this.prepareTableParams(lazyTable);

    try {
      const response = await lastValueFrom(
        this.productoService.pageProducto(this.filtersTable),
      );
      this.productos = response.data?.content ?? [];
      this.totalRecords = response.data?.totalElements ?? 0;
      this.loadingTable = false;
    } catch (error) {
      this.productos = [];
      this.totalRecords = 0;
      this.loadingTable = false;
    }
  }

  private prepareTableParams(lazyTable: TableLazyLoadEvent): IFilterTable<any> {
    this.rowSize = lazyTable.rows ?? this.rowSize;
    const currentPage = lazyTable.first
      ? Math.floor(lazyTable.first / this.rowSize)
      : 0;
    return {
      page: currentPage,
      rows: this.rowSize,
      search: lazyTable.globalFilter,
      order: lazyTable.sortOrder === -1 ? 'desc' : 'asc',
      order_by: lazyTable.sortField ?? 'id',
    };
  }

  async loadColumnActions(): Promise<void> {
    const columnAction = await this._helperService.showActionsTable();
    if (columnAction) {
      this.cols.push(columnAction);
    }
  }

  async deleteProducto(id: number): Promise<void> {
    this._confirmationService.confirm({
      message: '¿Estás seguro de eliminar este producto?',
      header: 'Eliminar Registro',
      icon: 'pi pi-exclamation-triangle',
      accept: async () => {
        try {
          const response = await lastValueFrom(
            this.productoService.deleteProducto(id),
          );
          if (response.status === 200) {
            this.messageService.add({
              severity: 'success',
              summary: 'Éxito',
              detail: 'Producto eliminado correctamente',
            });
            this.loadTable({ first: 0, rows: this.rowSize });
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

  getFilterFields(): string[] {
    return this.cols
      .filter((col) => col.field !== 'actions')
      .map((col) => col.field);
  }

  onGlobalFilter(event: any): void {
    this.globalFilter = event.target.value;
  }

  clearFilter(): void {
    this.globalFilter = '';
    this.loadTable({ first: 0, rows: this.rowSize });
  }

  getColumnIcon(field: string): string {
    const iconMap: { [key: string]: string } = {
      nombre: 'pi pi-tag',
      tipo_venta: 'pi pi-shopping-cart',
      activo: 'pi pi-power-off',
    };
    return iconMap[field] || 'pi pi-circle';
  }

  getEstadoLabel(activo: number): string {
    return activo === 1 ? 'Activo' : 'Inactivo';
  }

  getEstadoSeverity(activo: number): 'success' | 'danger' {
    return activo === 1 ? 'success' : 'danger';
  }

  getTipoVentaSeverity(tipoVenta: string): 'info' | 'warn' {
    return tipoVenta === 'UNIDAD' ? 'info' : 'warn';
  }

  getTipoVentaIcon(tipoVenta: string): string {
    return tipoVenta === 'UNIDAD' ? 'pi pi-box' : 'pi pi-inbox';
  }

  filterGlobal(event: Event) {
    this.loadTable({
      first: 0,
      rows: this.rowSize,
      globalFilter: (event.target as HTMLInputElement)?.value ?? '',
    });
  }

  // Métodos para el modal
  openCreateModal(): void {
    this.modalSlug = 'create';
    this.selectedProductoId = null;
    this.showProductoModal = true;
  }

  openEditModal(producto: ProductoTableModel): void {
    this.modalSlug = 'edit';
    this.selectedProductoId = producto.id;
    this.showProductoModal = true;
  }

  onModalClosed(): void {
    this.showProductoModal = false;
    this.selectedProductoId = null;
  }

  onProductoSaved(producto: ProductoModel): void {
    this.loadTable({ first: 0, rows: this.rowSize });
    this.showProductoModal = false;
  }
}

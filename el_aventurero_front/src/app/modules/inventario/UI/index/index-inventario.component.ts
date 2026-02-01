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
import { DialogModule } from 'primeng/dialog';
import { InventarioTableModel } from '../../../../core/models/inventario/inventario-table.model';
import { IFilterTable } from '../../../../../shared/models/filter-table';
import { ColsModel } from '../../../../../shared/models/cols.model';
import { HelpersService } from '../../../../../shared/pipes/helper.service';
import { InventarioService } from '../../../../core/services/inventario.service';
import { InventarioModel } from '../../../../core/models/inventario/inventario.model';
import { FormInventarioComponent } from '../../components/form/form-inventario.component';
import { AjusteStockComponent } from '../../components/ajuste-stock/ajuste-stock.component';

@Component({
  selector: 'app-index-inventario',
  standalone: true,
  templateUrl: './index-inventario.component.html',
  styleUrls: ['./index-inventario.component.scss'],
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
    FormInventarioComponent,
    DialogModule,
    AjusteStockComponent,
  ],
})
export class IndexInventarioComponent {
  // Variables para el modal
  showInventarioModal = false;
  selectedInventarioId: number | null = null;
  modalSlug: string = 'create';

  // Variables para ajuste de stock
  showAjusteStockModal = false;
  selectedInventario: InventarioTableModel | null = null;

  public rowSize = 10;
  public totalRecords = 0;
  public loadingTable = true;
  inventarios: InventarioTableModel[] = [];
  filtersTable!: IFilterTable<any>;

  cols: ColsModel[] = [
    {
      field: 'producto_nombre',
      header: 'Producto',
      type: 'string',
      nameClass: 'text-left',
    },
    {
      field: 'tipo_venta',
      header: 'Tipo',
      type: 'string',
      nameClass: 'text-center',
    },
    {
      field: 'stock',
      header: 'Stock',
      type: 'number',
      nameClass: 'text-center',
    },
    {
      field: 'costo_unitario',
      header: 'Costo Unitario',
      type: 'currency',
      nameClass: 'text-right',
    },
    {
      field: 'precio_venta',
      header: 'Precio Venta',
      type: 'currency',
      nameClass: 'text-right',
    },
    {
      field: 'valor_total',
      header: 'Valor Total',
      type: 'currency',
      nameClass: 'text-right',
    },
    {
      field: 'activo',
      header: 'Estado',
      type: 'icon',
      nameClass: 'text-center',
    },
  ];

  globalFilter: string = '';
  selectedItem: any = null;

  constructor(
    private fb: FormBuilder,
    readonly _helperService: HelpersService,
    private readonly _confirmationService: ConfirmationService,
    private inventarioService: InventarioService,
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
        this.inventarioService.pageInventario(this.filtersTable),
      );
      this.inventarios = response.data?.content ?? [];
      this.totalRecords = response.data?.totalElements ?? 0;
      this.loadingTable = false;
    } catch (error) {
      this.inventarios = [];
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

  async deleteInventario(id: number): Promise<void> {
    this._confirmationService.confirm({
      message: '¿Estás seguro de eliminar este registro de inventario?',
      header: 'Eliminar Registro',
      icon: 'pi pi-exclamation-triangle',
      accept: async () => {
        try {
          const response = await lastValueFrom(
            this.inventarioService.deleteInventario(id),
          );
          if (response.status === 200) {
            this.messageService.add({
              severity: 'success',
              summary: 'Éxito',
              detail: 'Inventario eliminado correctamente',
            });
            this.loadTable({ first: 0, rows: this.rowSize });
          }
        } catch (error) {
          this.messageService.add({
            severity: 'error',
            summary: 'Error',
            detail: 'No se pudo eliminar el inventario',
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
      producto_nombre: 'pi pi-tag',
      tipo_venta: 'pi pi-shopping-cart',
      stock: 'pi pi-box',
      costo_unitario: 'pi pi-dollar',
      precio_venta: 'pi pi-money-bill',
      valor_total: 'pi pi-chart-line',
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

  getStockSeverity(stock: number): 'success' | 'warn' | 'danger' {
    if (stock === 0) return 'danger';
    if (stock <= 10) return 'warn';
    return 'success';
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
    this.selectedInventarioId = null;
    this.showInventarioModal = true;
  }

  openEditModal(inventario: InventarioTableModel): void {
    this.modalSlug = 'edit';
    this.selectedInventarioId = inventario.id;
    this.showInventarioModal = true;
  }

  onModalClosed(): void {
    this.showInventarioModal = false;
    this.selectedInventarioId = null;
  }

  onInventarioSaved(inventario: InventarioModel): void {
    this.loadTable({ first: 0, rows: this.rowSize });
    this.showInventarioModal = false;
  }

  // Métodos para ajuste de stock
  openAjusteStockModal(inventario: InventarioTableModel): void {
    this.selectedInventario = inventario;
    this.showAjusteStockModal = true;
  }

  onAjusteStockClosed(): void {
    this.showAjusteStockModal = false;
    this.selectedInventario = null;
  }

  onStockAjustado(): void {
    this.loadTable({ first: 0, rows: this.rowSize });
    this.showAjusteStockModal = false;
    this.selectedInventario = null;
  }
}

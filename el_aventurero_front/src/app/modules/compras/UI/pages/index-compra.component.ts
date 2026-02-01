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
import { CompraTableModel } from '../../../../core/models/compra/compra-table.model';
import { IFilterTable } from '../../../../../shared/models/filter-table';
import { ColsModel } from '../../../../../shared/models/cols.model';
import { HelpersService } from '../../../../../shared/pipes/helper.service';
import { CompraService } from '../../../../core/services/compra.service';
import { CompraModel } from '../../../../core/models/compra/compra.model';
import { IconFieldModule } from 'primeng/iconfield';
import { InputIconModule } from 'primeng/inputicon';
import { FormCompraComponent } from '../../components/form/form-compra.component';
import { AlertService } from '../../../../../shared/pipes/alert.service';
import { DetalleCompraComponent } from '../../components/detalle/detalle-compra.component';

@Component({
  selector: 'app-index-compra',
  standalone: true,
  templateUrl: './index-compra.component.html',
  styleUrls: ['./index-compra.component.scss'],
  providers: [MessageService, ConfirmationService, AlertService],
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
    IconFieldModule,
    InputIconModule,
    FormCompraComponent,
    DetalleCompraComponent,
  ],
})
export class IndexCompraComponent {
  // Variables para el modal de crear
  showCompraModal = false;

  // Variables para ver detalles
  showDetalleModal = false;
  selectedCompraId: number | null = null;

  public rowSize = 10;
  public totalRecords = 0;
  public loadingTable = true;
  compras: CompraTableModel[] = [];
  filtersTable!: IFilterTable<any>;

  cols: ColsModel[] = [
    {
      field: 'created_at',
      header: 'Fecha',
      type: 'date',
      nameClass: 'text-left',
    },
    {
      field: 'total_compra',
      header: 'Total',
      type: 'currency',
      nameClass: 'text-right',
    },
    {
      field: 'metodo_pago',
      header: 'Método de Pago',
      type: 'string',
      nameClass: 'text-center',
    },
    {
      field: 'cantidad_productos',
      header: 'Productos',
      type: 'number',
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
  selectedItem: any = null;

  constructor(
    private fb: FormBuilder,
    readonly _helperService: HelpersService,
    private readonly _confirmationService: ConfirmationService,
    private compraService: CompraService,
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
        this.compraService.pageCompra(this.filtersTable),
      );
      this.compras = response.data?.content ?? [];
      this.totalRecords = response.data?.totalElements ?? 0;
      this.loadingTable = false;
    } catch (error) {
      this.compras = [];
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

  async deleteCompra(id: number): Promise<void> {
    this._confirmationService.confirm({
      message: '¿Estás seguro de eliminar esta compra?',
      header: 'Eliminar Registro',
      icon: 'pi pi-exclamation-triangle',
      accept: async () => {
        try {
          const response = await lastValueFrom(
            this.compraService.deleteCompra(id),
          );
          if (response.status === 200) {
            this.messageService.add({
              severity: 'success',
              summary: 'Éxito',
              detail: 'Compra eliminada correctamente',
            });
            this.loadTable({ first: 0, rows: this.rowSize });
          }
        } catch (error) {
          this.messageService.add({
            severity: 'error',
            summary: 'Error',
            detail: 'No se pudo eliminar la compra',
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

  clearFilter(): void {
    this.globalFilter = '';
    this.loadTable({ first: 0, rows: this.rowSize });
  }

  getColumnIcon(field: string): string {
    const iconMap: { [key: string]: string } = {
      created_at: 'pi pi-calendar',
      total_compra: 'pi pi-dollar',
      metodo_pago: 'pi pi-credit-card',
      cantidad_productos: 'pi pi-box',
      activo: 'pi pi-power-off',
    };
    return iconMap[field] || 'pi pi-circle';
  }

  getEstadoLabel(activo: number): string {
    return activo === 1 ? 'Activo' : 'Eliminado';
  }

  getEstadoSeverity(activo: number): 'success' | 'danger' {
    return activo === 1 ? 'success' : 'danger';
  }

  getMetodoPagoSeverity(
    metodo: string,
  ): 'success' | 'info' | 'warn' | 'secondary' {
    const severityMap: {
      [key: string]: 'success' | 'info' | 'warn' | 'secondary';
    } = {
      EFECTIVO: 'success',
      TARJETA: 'info',
      TRANSFERENCIA: 'warn',
      DIGITAL: 'secondary',
    };
    return severityMap[metodo] || 'secondary';
  }

  getMetodoPagoIcon(metodo: string): string {
    const iconMap: { [key: string]: string } = {
      EFECTIVO: 'pi pi-money-bill',
      TARJETA: 'pi pi-credit-card',
      TRANSFERENCIA: 'pi pi-send',
      DIGITAL: 'pi pi-mobile',
    };
    return iconMap[metodo] || 'pi pi-wallet';
  }

  filterGlobal(event: Event) {
    this.loadTable({
      first: 0,
      rows: this.rowSize,
      globalFilter: (event.target as HTMLInputElement)?.value ?? '',
    });
  }

  formatDate(dateString: string): string {
    const date = new Date(dateString);
    return date.toLocaleDateString('es-CO', {
      year: 'numeric',
      month: 'short',
      day: 'numeric',
      hour: '2-digit',
      minute: '2-digit',
    });
  }

  // Métodos para modales
  openCreateModal(): void {
    this.showCompraModal = true;
  }

  onModalClosed(): void {
    this.showCompraModal = false;
  }

  onCompraSaved(compra: CompraModel): void {
    this.loadTable({ first: 0, rows: this.rowSize });
    this.showCompraModal = false;
  }

  // Ver detalles
  verDetalle(compra: CompraTableModel): void {
    this.selectedCompraId = compra.id;
    this.showDetalleModal = true;
  }

  onDetalleClosed(): void {
    this.showDetalleModal = false;
    this.selectedCompraId = null;
  }
}

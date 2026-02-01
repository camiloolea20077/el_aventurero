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
import { VentaTableModel } from '../../../../core/models/venta/venta-table.model';
import { IFilterTable } from '../../../../../shared/models/filter-table';
import { ColsModel } from '../../../../../shared/models/cols.model';
import { HelpersService } from '../../../../../shared/pipes/helper.service';
import { VentaService } from '../../../../core/services/venta.service';
import { DetalleVentaComponent } from '../../components/detalle/detalle-venta.component';
import { AlertService } from '../../../../../shared/pipes/alert.service';

@Component({
  selector: 'app-index-venta',
  standalone: true,
  templateUrl: './index-venta.component.html',
  styleUrls: ['./index-venta.component.scss'],
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
    DetalleVentaComponent,
  ],
})
export class IndexVentaComponent {
  // Variables para ver detalles
  showDetalleModal = false;
  selectedVentaId: number | null = null;

  public rowSize = 10;
  public totalRecords = 0;
  public loadingTable = true;
  ventas: VentaTableModel[] = [];
  filtersTable!: IFilterTable<any>;

  cols: ColsModel[] = [
    {
      field: 'created_at',
      header: 'Fecha',
      type: 'date',
      nameClass: 'text-left',
    },
    {
      field: 'mesa_numero',
      header: 'Mesa',
      type: 'number',
      nameClass: 'text-center',
    },
    {
      field: 'total',
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
  ];

  globalFilter: string = '';
  selectedItem: any = null;

  constructor(
    private fb: FormBuilder,
    readonly _helperService: HelpersService,
    private readonly _confirmationService: ConfirmationService,
    private ventaService: VentaService,
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
        this.ventaService.pageVenta(this.filtersTable),
      );
      this.ventas = response.data?.content ?? [];
      this.totalRecords = response.data?.totalElements ?? 0;
      this.loadingTable = false;
    } catch (error) {
      this.ventas = [];
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

  async deleteVenta(id: number): Promise<void> {
    this._confirmationService.confirm({
      message: '¿Estás seguro de eliminar esta venta?',
      header: 'Eliminar Registro',
      icon: 'pi pi-exclamation-triangle',
      accept: async () => {
        try {
          const response = await lastValueFrom(
            this.ventaService.deleteVenta(id),
          );
          if (response.status === 200) {
            this.messageService.add({
              severity: 'success',
              summary: 'Éxito',
              detail: 'Venta eliminada correctamente',
            });
            this.loadTable({ first: 0, rows: this.rowSize });
          }
        } catch (error) {
          this.messageService.add({
            severity: 'error',
            summary: 'Error',
            detail: 'No se pudo eliminar la venta',
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
      mesa_numero: 'pi pi-table',
      total: 'pi pi-dollar',
      metodo_pago: 'pi pi-credit-card',
      cantidad_productos: 'pi pi-box',
      activo: 'pi pi-power-off',
    };
    return iconMap[field] || 'pi pi-circle';
  }

  getEstadoLabel(activo: number): string {
    return activo === 1 ? 'Activo' : 'Eliminado';
  }

  getEstadoSeverity(
    activo: number,
  ): 'success' | 'info' | 'warn' | 'danger' | 'secondary' | 'contrast' {
    return activo === 1 ? 'success' : 'danger';
  }

  getMetodoPagoSeverity(
    metodo: string,
  ): 'success' | 'info' | 'warn' | 'danger' | 'secondary' | 'contrast' {
    const severityMap: {
      [key: string]:
        | 'success'
        | 'info'
        | 'warn'
        | 'danger'
        | 'secondary'
        | 'contrast';
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

  // Ver detalles
  verDetalle(venta: VentaTableModel): void {
    this.selectedVentaId = venta.id;
    this.showDetalleModal = true;
  }

  onDetalleClosed(): void {
    this.showDetalleModal = false;
    this.selectedVentaId = null;
  }
}

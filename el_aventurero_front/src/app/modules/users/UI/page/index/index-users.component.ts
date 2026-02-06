import { CommonModule } from '@angular/common';
import { Component } from '@angular/core';
import { Router, RouterModule } from '@angular/router';
import { ButtonModule } from 'primeng/button';
import { ConfirmDialogModule } from 'primeng/confirmdialog';
import { InputTextModule } from 'primeng/inputtext';
import { TableLazyLoadEvent, TableModule } from 'primeng/table';
import { ToastModule } from 'primeng/toast';
import { UsersTableModel } from '../../../domain/models/users-table.model';
import { IUsersFilterTable } from '../../../domain/models/users-filter-table.model';
import { ConfirmationService, MessageService } from 'primeng/api';
import { lastValueFrom } from 'rxjs';
import { IFilterTable } from '../../../../../../shared/models/filter-table';
import { ColsModel } from '../../../../../../shared/models/cols.model';
import { HelpersService } from '../../../../../../shared/pipes/helper.service';
import { UsersService } from '../../../../../core/services/users.service';
import { TagModule } from 'primeng/tag';
import { DividerModule } from 'primeng/divider';
import { IconFieldModule } from 'primeng/iconfield';
import { InputIconModule } from 'primeng/inputicon';

@Component({
  selector: 'app-index-users',
  standalone: true,
  templateUrl: './index-users.component.html',
  styleUrls: ['./index-users.component.scss'],
  imports: [
    DividerModule,
    RouterModule,
    InputTextModule,
    ConfirmDialogModule,
    ButtonModule,
    TableModule,
    CommonModule,
    ToastModule,
    TagModule,
    DividerModule,
    IconFieldModule,
    InputIconModule,
  ],
  providers: [ConfirmationService, MessageService],
})
export class IndexUsersComponent {
  globalFilter: string = '';
  public rowSize = 10;
  public totalRecords = 0;
  public loadingTable = true;
  users: UsersTableModel[] = [];
  filtersTable!: IFilterTable<IUsersFilterTable>;
  cols: ColsModel[] = [
    {
      field: 'name',
      header: 'Nombre',
      type: 'string',
      nameClass: 'text-center',
      minWidth: 'min-width: 280px;',
    },
    {
      field: 'email',
      header: 'Correo',
      type: 'string',
      nameClass: 'text-center',
      minWidth: 'min-width: 180px;',
    },
    {
      field: 'role',
      header: 'Rol',
      type: 'string',
      nameClass: 'text-center',
    },
    {
      field: 'activo',
      header: 'Activo',
      type: 'icon',
      minWidth: 'min-width: 60px;',
      nameClass: 'text-center',
    },
  ];
  constructor(
    readonly _helperService: HelpersService,
    private readonly _confirmationService: ConfirmationService,
    private readonly usersService: UsersService,
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
        this.usersService.pageClients(this.filtersTable),
      );
      this.users = response.data?.content ?? [];
      this.totalRecords = response.data?.totalElements ?? 0;
      this.loadingTable = false;
    } catch (error) {
      this.users = [];
      this.totalRecords = 0;
      this.loadingTable = false;
    }
  }
  private prepareTableParams(
    lazyTable: TableLazyLoadEvent,
  ): IFilterTable<IUsersFilterTable> {
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
  filterGlobal(event: Event) {
    this.loadTable({
      first: 0,
      rows: this.rowSize,
      globalFilter: (event.target as HTMLInputElement)?.value ?? '',
    });
  }
}

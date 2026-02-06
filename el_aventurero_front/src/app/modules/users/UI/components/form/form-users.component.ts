import { CommonModule } from '@angular/common';
import { Component } from '@angular/core';
import {
  FormBuilder,
  FormGroup,
  FormsModule,
  ReactiveFormsModule,
  Validators,
} from '@angular/forms';
import { ActivatedRoute, Router, RouterModule } from '@angular/router';
import { MessageService } from 'primeng/api';
import { CalendarModule } from 'primeng/calendar';
import { ConfirmDialogModule } from 'primeng/confirmdialog';
import { DropdownModule } from 'primeng/dropdown';
import { InputSwitchModule } from 'primeng/inputswitch';
import { InputTextModule } from 'primeng/inputtext';
import { TextareaModule } from 'primeng/textarea';
import { ToastModule } from 'primeng/toast';
import { CreateUsersDto } from '../../../domain/dto/create-users.dto';
import { UpdateUsersDto } from '../../../domain/dto/update-users.dto';
import { UsersModels } from '../../../domain/models/users.model';
import { lastValueFrom } from 'rxjs';
import { MultiSelectModule } from 'primeng/multiselect';
import { UsersService } from '../../../../../core/services/users.service';
import { AlertService } from '../../../../../../shared/pipes/alert.service';
import { ResponseModel } from '../../../../../../shared/models/responde.models';
import { customEmailValidator } from '../../../../../../shared/pipes/custom-email.validator';
import { ToggleSwitchModule } from 'primeng/toggleswitch';
import { ButtonModule } from 'primeng/button';
import { DividerModule } from 'primeng/divider';
import { RolesService } from '../../../../../core/services/roles.service';
import { RolesListDto } from '../../../../../core/models/roles/list-roles.dto';

@Component({
  selector: 'app-form-users',
  standalone: true,
  templateUrl: './form-users.component.html',
  styleUrls: ['./form-users.component.scss'],
  imports: [
    DividerModule,
    ButtonModule,
    CommonModule,
    ReactiveFormsModule,
    FormsModule,
    InputSwitchModule,
    DropdownModule,
    RouterModule,
    CalendarModule,
    TextareaModule,
    InputTextModule,
    ToastModule,
    ToggleSwitchModule,
    ConfirmDialogModule,
    MultiSelectModule,
  ],
  providers: [MessageService, AlertService],
})
export class FormUsersComponent {
  roles_list: RolesListDto[] = [];
  public frm!: FormGroup;
  id!: number;
  slug: string | null = 'create';
  permissionOptions = [
    { label: 'Acceso a Administración', value: 'ADMIN_ACCESS' },
    { label: 'Acceso a Inventario', value: 'INVENTORY_VIEW' },
    { label: 'Acceso a Caja', value: 'CAJA_ACCESS' },
    { label: 'Acceso a ventas', value: 'SALES_ACCESS' },
    { label: 'Acceso a productos', value: 'PRODUCTS_ACCESS' },
    // ...otros permisos
  ];

  constructor(
    private readonly usersService: UsersService,
    private readonly _router: Router,
    private readonly rolesService: RolesService,
    private readonly messageService: MessageService,
    private readonly _alertService: AlertService,
    private readonly formBuilder: FormBuilder,
    public readonly _activatedRoute: ActivatedRoute,
  ) {
    this.slug = this._activatedRoute.snapshot.data['slug'];
    this.id = Number(this._activatedRoute.snapshot.params['id']);
  }
  async ngOnInit() {
    this.loadForm();
    this.getRoles();
  }
  loadForm() {
    this.frm = this.formBuilder.group({
      active: [true],
      name: [null, Validators.required],
      email: [null, [Validators.required, customEmailValidator]],
      password: [null, Validators.required],
      role_id: [null, Validators.required],
      username: [null, Validators.required],
      permissions: [[], Validators.required],
    });
  }

  async buildDataUsers(): Promise<CreateUsersDto | UpdateUsersDto> {
    const formValue = this.frm.value;
    return {
      username: this.frm.controls['username'].value,
      active: formValue.active ? 1 : 2,
      name: this.frm.controls['name'].value,
      email: this.frm.controls['email'].value,
      password: this.frm.controls['password'].value,
      rol_id: this.frm.controls['role_id'].value,
      permisos: this.frm.controls['permissions'].value,
    };
  }
  async buildSaveUsers(): Promise<void> {
    const msgSystem = 'Alerta del sistema';
    const msgText = 'Complete el formulario correctamente';
    if (this.isFormInvalid()) {
      this.markFormAsTouched();
      this._alertService.showError(msgSystem, msgText);
      return;
    }
    const data: CreateUsersDto | UpdateUsersDto = await this.buildDataUsers();
    try {
      const response = await this.saveUsers(data);
      if (response) this.handleResponse(response);
    } catch (error) {
      const msg = 'Error al guardar el empleado';
      this.showErrorMessage(msg);
    }
  }
  private saveUsers(
    data: CreateUsersDto | UpdateUsersDto,
  ): Promise<ResponseModel<boolean | UsersModels> | void> {
    if (this.slug === 'create') {
      return lastValueFrom(
        this.usersService.createUsers(data as CreateUsersDto),
      ).catch((error) => {
        this.showErrorMessage(error.message);
      });
    }
    if (this.slug === 'edit') {
      return lastValueFrom(
        this.usersService.updateUsers(this.id, data as UpdateUsersDto),
      ).catch((error) => {
        this.showErrorMessage(error.message);
      });
    }
    throw new Error('Slug inválido');
  }
  private handleResponse(response: ResponseModel<UsersModels | boolean>): void {
    if (response?.status === 200 || response?.status === 201) {
      const message =
        this.slug === 'edit'
          ? 'Usuario actualizado correctamente'
          : 'Usuario creado correctamente';
      this.messageService.add({
        severity: 'success',
        summary: 'Operación exitosa',
        detail: message,
        life: 5000,
      });
      if (response?.status === 201) {
        this._router.navigate(['/users']);
      }
    }
  }
  async getRoles(): Promise<void> {
    try {
      const response = await lastValueFrom(this.rolesService.getAtllRoles());
      this.roles_list = response.data || [];
    } catch (error) {
      console.error('Error cargando productos:', error);
    }
  }
  markFormAsTouched(): void {
    this.frm.markAllAsTouched();
  }
  isFormInvalid(): boolean {
    return this.frm.invalid;
  }
  private showErrorMessage(message: string): void {
    const msgSystem = 'Alerta del sistema';
    this._alertService.showError(msgSystem, message ?? 'Ok');
  }
}

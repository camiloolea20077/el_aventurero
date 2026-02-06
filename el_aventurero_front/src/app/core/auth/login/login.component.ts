import { CommonModule } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { CheckboxModule } from 'primeng/checkbox';
import { ButtonModule } from 'primeng/button';
import {
  FormBuilder,
  FormGroup,
  FormsModule,
  ReactiveFormsModule,
  Validators,
} from '@angular/forms';
import { Router } from '@angular/router';
import { MessageService } from 'primeng/api';
import { InputTextModule } from 'primeng/inputtext';
import { PasswordModule } from 'primeng/password';
import { ToastModule } from 'primeng/toast';
import { DropdownModule } from 'primeng/dropdown';
import { AuthService } from '../../services/auth.service';
import { resolveLandingRoute } from '../../../../shared/constants/permissions';

@Component({
  selector: 'app-login',
  standalone: true,
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.scss'],
  imports: [
    FormsModule,
    ButtonModule,
    CheckboxModule,
    InputTextModule,
    PasswordModule,
    ReactiveFormsModule,
    CommonModule,
    ToastModule,
    DropdownModule,
  ],
  providers: [MessageService],
})
export class LoginComponent implements OnInit {
  loginForm!: FormGroup;
  constructor(
    private fb: FormBuilder,
    private router: Router,
    private messageService: MessageService,
    private authService: AuthService,
  ) {}

  ngOnInit(): void {
    this.loadForm();
  }

  loadForm() {
    this.loginForm = this.fb.group({
      email: ['', [Validators.required, Validators.email]],
      password: ['', Validators.required],
      farms: [''],
    });
  }
  onSubmit(): void {
    if (this.loginForm.invalid) {
      this.messageService.add({
        severity: 'error',
        summary: 'Error',
        detail: 'Datos inválidos',
      });
      return;
    }
    const { email, password, farms } = this.loginForm.value;

    // Construir el objeto de login, solo enviamos farmId si no es un SUPER_ADMIN
    const loginData = farms
      ? { email, password, farmId: farms }
      : { email, password };

    this.authService.login(loginData).subscribe({
      next: (res) => {
        localStorage.setItem('user', JSON.stringify(res.data.user));
        localStorage.setItem('token', res.data.token);

        this.messageService.add({
          severity: 'success',
          summary: 'Bienvenido',
          detail: `Has iniciado sesión correctamente.`,
          life: 4000,
        });
        const landingRoute = resolveLandingRoute(res.data.user.permisos);
        this.router.navigate([landingRoute]);
      },
      error: (err) => {
        if (err.status === 401) {
          this.messageService.add({
            severity: 'error',
            summary: 'Error',
            detail:
              'El usuario no tiene acceso, elija una finca para iniciar sesión.',
          });
        } else {
          this.messageService.add({
            severity: 'error',
            summary: 'Error',
            detail: 'Credenciales incorrectas',
          });
        }
      },
    });
  }
}

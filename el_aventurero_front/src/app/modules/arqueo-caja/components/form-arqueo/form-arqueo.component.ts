import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import {
  FormBuilder,
  FormGroup,
  FormsModule,
  ReactiveFormsModule,
  Validators,
} from '@angular/forms';
import { CommonModule } from '@angular/common';
import { MessageService } from 'primeng/api';
import { lastValueFrom } from 'rxjs';

// PrimeNG
import { DialogModule } from 'primeng/dialog';
import { ButtonModule } from 'primeng/button';
import { InputNumberModule } from 'primeng/inputnumber';
import { DividerModule } from 'primeng/divider';
import { ToastModule } from 'primeng/toast';
import { CardModule } from 'primeng/card';
import { ArqueoCajaService } from '../../../../core/services/arqueo-caja.service';
import { AlertService } from '../../../../../shared/pipes/alert.service';
import { DetalleConteoModel } from '../../../../core/models/arqueo-caja/arqueo-caja.model';
import { CreateArqueoDto } from '../../../../core/models/arqueo-caja/create-arqueo.dto';
import { TextareaModule } from 'primeng/textarea';

// Servicios
@Component({
  selector: 'app-form-arqueo',
  standalone: true,
  templateUrl: './form-arqueo.component.html',
  styleUrls: ['./form-arqueo.component.scss'],
  imports: [
    CommonModule,
    ReactiveFormsModule,
    FormsModule,
    DialogModule,
    ButtonModule,
    InputNumberModule,
    TextareaModule,
    DividerModule,
    ToastModule,
    CardModule,
  ],
})
export class FormArqueoComponent implements OnInit {
  @Input() displayModal: boolean = false;
  @Output() modalClosed = new EventEmitter<void>();
  @Output() arqueoGuardado = new EventEmitter<void>();
  Math = Math;
  public frmArqueo!: FormGroup;
  public isSubmitting: boolean = false;
  public loadingDatos: boolean = false;

  // Datos del sistema
  saldoInicial: number = 0;
  totalIngresosSistema: number = 0;
  totalEgresosSistema: number = 0;
  saldoEsperado: number = 0;

  // Conteo de billetes y monedas
  billetes = [
    { denominacion: 100000, cantidad: 0, total: 0 },
    { denominacion: 50000, cantidad: 0, total: 0 },
    { denominacion: 20000, cantidad: 0, total: 0 },
    { denominacion: 10000, cantidad: 0, total: 0 },
    { denominacion: 5000, cantidad: 0, total: 0 },
    { denominacion: 2000, cantidad: 0, total: 0 },
    { denominacion: 1000, cantidad: 0, total: 0 },
  ];

  monedas = [
    { denominacion: 1000, cantidad: 0, total: 0 },
    { denominacion: 500, cantidad: 0, total: 0 },
    { denominacion: 200, cantidad: 0, total: 0 },
    { denominacion: 100, cantidad: 0, total: 0 },
    { denominacion: 50, cantidad: 0, total: 0 },
  ];

  // Totales
  totalBilletes: number = 0;
  totalMonedas: number = 0;
  efectivoReal: number = 0;
  diferencia: number = 0;

  constructor(
    private readonly arqueoCajaService: ArqueoCajaService,
    private readonly messageService: MessageService,
    private readonly _alertService: AlertService,
    private readonly formBuilder: FormBuilder,
  ) {}

  ngOnInit() {
    this.loadForm();
  }

  ngOnChanges() {
    if (this.displayModal) {
      this.resetForm();
      this.cargarDatosDelDia();
    }
  }

  loadForm() {
    this.frmArqueo = this.formBuilder.group({
      observaciones: [''],
    });
  }

  async cargarDatosDelDia(): Promise<void> {
    this.loadingDatos = true;
    try {
      const hoy = this.formatDate(new Date());
      const response = await lastValueFrom(
        this.arqueoCajaService.getDatosParaArqueo(hoy),
      );

      const datos = response.data;
      this.saldoInicial = datos.saldo_inicial || 0;
      this.totalIngresosSistema = datos.total_ingresos || 0;
      this.totalEgresosSistema = datos.total_egresos || 0;
      this.saldoEsperado = datos.saldo_esperado || 0;
    } catch (error) {
      console.error('Error cargando datos:', error);
      this._alertService.showError(
        'Error',
        'No se pudieron cargar los datos del día',
      );
    } finally {
      this.loadingDatos = false;
    }
  }

  formatDate(date: Date): string {
    const year = date.getFullYear();
    const month = ('0' + (date.getMonth() + 1)).slice(-2);
    const day = ('0' + date.getDate()).slice(-2);
    return `${year}-${month}-${day}`;
  }

  onCantidadChange(tipo: 'billete' | 'moneda', index: number): void {
    if (tipo === 'billete') {
      const billete = this.billetes[index];
      billete.total = billete.denominacion * billete.cantidad;
    } else {
      const moneda = this.monedas[index];
      moneda.total = moneda.denominacion * moneda.cantidad;
    }

    this.calcularTotales();
  }

  calcularTotales(): void {
    this.totalBilletes = this.billetes.reduce((sum, b) => sum + b.total, 0);
    this.totalMonedas = this.monedas.reduce((sum, m) => sum + m.total, 0);
    this.efectivoReal = this.totalBilletes + this.totalMonedas;
    this.diferencia = this.efectivoReal - this.saldoEsperado;
  }

  getDiferenciaSeverity():
    | 'success'
    | 'info'
    | 'warn'
    | 'danger'
    | 'help'
    | 'primary'
    | 'secondary'
    | 'contrast'
    | null
    | undefined {
    if (this.diferencia === 0) return 'success';
    if (Math.abs(this.diferencia) <= 10000) return 'warn';
    return 'danger';
  }
  getDiferenciaIcon(): string {
    if (this.diferencia === 0) return 'pi pi-check-circle';
    if (this.diferencia > 0) return 'pi pi-arrow-up';
    return 'pi pi-arrow-down';
  }

  resetForm(): void {
    this.frmArqueo.reset();

    // Reset billetes y monedas
    this.billetes.forEach((b) => {
      b.cantidad = 0;
      b.total = 0;
    });
    this.monedas.forEach((m) => {
      m.cantidad = 0;
      m.total = 0;
    });

    // Reset totales
    this.totalBilletes = 0;
    this.totalMonedas = 0;
    this.efectivoReal = 0;
    this.diferencia = 0;

    // Reset datos del sistema
    this.saldoInicial = 0;
    this.totalIngresosSistema = 0;
    this.totalEgresosSistema = 0;
    this.saldoEsperado = 0;
  }

  async buildSaveArqueo(): Promise<void> {
    if (this.efectivoReal === 0) {
      this._alertService.showError(
        'Error',
        'Debe contar el efectivo antes de guardar',
      );
      return;
    }

    this.isSubmitting = true;

    const arqueo: CreateArqueoDto = {
      fecha: this.formatDate(new Date()),
      saldo_inicial: this.saldoInicial,
      efectivo_real: this.efectivoReal,
      observaciones: this.frmArqueo.value.observaciones,

      // Detalle del conteo (aplanado)
      billetes_100000: this.billetes[0].cantidad,
      billetes_50000: this.billetes[1].cantidad,
      billetes_20000: this.billetes[2].cantidad,
      billetes_10000: this.billetes[3].cantidad,
      billetes_5000: this.billetes[4].cantidad,
      billetes_2000: this.billetes[5].cantidad,
      billetes_1000: this.billetes[6].cantidad,
      monedas_1000: this.monedas[0].cantidad,
      monedas_500: this.monedas[1].cantidad,
      monedas_200: this.monedas[2].cantidad,
      monedas_100: this.monedas[3].cantidad,
      monedas_50: this.monedas[4].cantidad,
    };

    try {
      await lastValueFrom(this.arqueoCajaService.create(arqueo));

      this.messageService.add({
        severity: 'success',
        summary: 'Éxito',
        detail: 'Arqueo registrado correctamente',
        life: 5000,
      });

      this.arqueoGuardado.emit();
      this.closeModal();
    } catch (error: any) {
      this._alertService.showError(
        'Error',
        error.error?.message || 'No se pudo registrar el arqueo',
      );
    } finally {
      this.isSubmitting = false;
    }
  }

  closeModal() {
    this.displayModal = false;
    this.resetForm();
    this.modalClosed.emit();
  }

  onModalHide() {
    this.closeModal();
  }
}

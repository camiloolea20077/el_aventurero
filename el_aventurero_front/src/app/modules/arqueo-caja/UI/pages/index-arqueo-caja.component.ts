import { CommonModule } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { ConfirmationService, MessageService } from 'primeng/api';
import { lastValueFrom } from 'rxjs';

// PrimeNG
import { ButtonModule } from 'primeng/button';
import { CalendarModule } from 'primeng/calendar';
import { CardModule } from 'primeng/card';
import { TableModule } from 'primeng/table';
import { TagModule } from 'primeng/tag';
import { ToastModule } from 'primeng/toast';
import { ConfirmDialogModule } from 'primeng/confirmdialog';
import { TooltipModule } from 'primeng/tooltip';
import { ArqueoCajaModel } from '../../../../core/models/arqueo-caja/arqueo-caja.model';
import { ArqueoCajaService } from '../../../../core/services/arqueo-caja.service';
import { FormArqueoComponent } from '../../components/form-arqueo/form-arqueo.component';
import { AlertService } from '../../../../../shared/pipes/alert.service';

@Component({
    selector: 'app-index-arqueo-caja',
    standalone: true,
    templateUrl: './index-arqueo-caja.component.html',
    styleUrls: ['./index-arqueo-caja.component.scss'],
    providers: [MessageService, ConfirmationService,AlertService],
    imports: [
        CommonModule,
        FormsModule,
        ButtonModule,
        CalendarModule,
        CardModule,
        TableModule,
        TagModule,
        ToastModule,
        ConfirmDialogModule,
        TooltipModule,
        FormArqueoComponent,
    ],
})
export class IndexArqueoCajaComponent implements OnInit {
    // Modal de formulario
    showFormModal = false;

    // Fechas
    fechaInicio: Date = new Date();
    fechaFin: Date = new Date();

    // Datos
    arqueos: ArqueoCajaModel[] = [];
    arqueoDelDia: ArqueoCajaModel | null = null;
    loading: boolean = false;

    constructor(
        private arqueoCajaService: ArqueoCajaService,
        private messageService: MessageService,
        private confirmationService: ConfirmationService
    ) {}

    ngOnInit(): void {
        this.setFechasMesActual();
        this.cargarDatos();
        this.verificarArqueoDelDia();
    }

    setFechasMesActual(): void {
        const hoy = new Date();
        this.fechaInicio = new Date(hoy.getFullYear(), hoy.getMonth(), 1);
        this.fechaFin = new Date(hoy.getFullYear(), hoy.getMonth() + 1, 0);
    }

    setFechasHoy(): void {
        const hoy = new Date();
        this.fechaInicio = new Date(hoy.setHours(0, 0, 0, 0));
        this.fechaFin = new Date(hoy.setHours(23, 59, 59, 999));
        this.cargarDatos();
    }

    async cargarDatos(): Promise<void> {
        this.loading = true;
        try {
            const fechaInicioStr = this.formatDate(this.fechaInicio);
            const fechaFinStr = this.formatDate(this.fechaFin);

            const response = await lastValueFrom(
                this.arqueoCajaService.getArqueosPorFecha(fechaInicioStr, fechaFinStr)
            );
            this.arqueos = response.data || [];
        } catch (error) {
            console.error('Error cargando arqueos:', error);
            this.arqueos = [];
        } finally {
            this.loading = false;
        }
    }

    async verificarArqueoDelDia(): Promise<void> {
        try {
            const hoy = this.formatDate(new Date());
            const response = await lastValueFrom(
                this.arqueoCajaService.getArqueoDelDia(hoy)
            );
            this.arqueoDelDia = response.data;
        } catch (error) {
            this.arqueoDelDia = null;
        }
    }

    formatDate(date: Date): string {
        const year = date.getFullYear();
        const month = ('0' + (date.getMonth() + 1)).slice(-2);
        const day = ('0' + date.getDate()).slice(-2);
        return `${year}-${month}-${day}`;
    }

    formatDateTime(dateString: string): string {
        const date = new Date(dateString);
        return date.toLocaleDateString('es-CO', {
            year: 'numeric',
            month: 'short',
            day: 'numeric',
            hour: '2-digit',
            minute: '2-digit',
        });
    }

getEstadoSeverity(estado: string): "success" | "info" | "warn" | "danger" | "secondary" | "contrast" | undefined {
    const severityMap: { [key: string]: "success" | "info" | "warn" | "danger" | "secondary" } = {
        CUADRADO: 'success',
        PENDIENTE: 'warn',
        AJUSTADO: 'info',
    };
    return severityMap[estado] || 'secondary';
}
    getEstadoIcon(estado: string): string {
        const iconMap: { [key: string]: string } = {
            CUADRADO: 'pi pi-check-circle',
            PENDIENTE: 'pi pi-exclamation-triangle',
            AJUSTADO: 'pi pi-wrench',
        };
        return iconMap[estado] || 'pi pi-circle';
    }

getDiferenciaSeverity(diferencia: number): "success" | "info" | "warn" | "danger" | "secondary" | "contrast" | undefined {
    if (diferencia === 0) return 'success';
    if (Math.abs(diferencia) <= 10000) return 'warn';
    return 'danger';
}

    openFormModal(): void {
        this.showFormModal = true;
    }

    onFormModalClosed(): void {
        this.showFormModal = false;
    }

    onArqueoGuardado(): void {
        this.cargarDatos();
        this.verificarArqueoDelDia();
        this.showFormModal = false;
    }

    async deleteArqueo(id: number): Promise<void> {
        this.confirmationService.confirm({
            message: '¿Estás seguro de eliminar este arqueo?',
            header: 'Eliminar Arqueo',
            icon: 'pi pi-exclamation-triangle',
            accept: async () => {
                try {
                    await lastValueFrom(this.arqueoCajaService.delete(id));
                    this.messageService.add({
                        severity: 'success',
                        summary: 'Éxito',
                        detail: 'Arqueo eliminado correctamente',
                    });
                    this.cargarDatos();
                    this.verificarArqueoDelDia();
                } catch (error) {
                    this.messageService.add({
                        severity: 'error',
                        summary: 'Error',
                        detail: 'No se pudo eliminar el arqueo',
                    });
                }
            },
        });
    }
}
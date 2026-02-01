import { CommonModule } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { lastValueFrom } from 'rxjs';

// PrimeNG
import { CardModule } from 'primeng/card';
import { ChartModule } from 'primeng/chart';
import { TableModule } from 'primeng/table';
import { TagModule } from 'primeng/tag';
import { ButtonModule } from 'primeng/button';
import { SkeletonModule } from 'primeng/skeleton';
import { MesaModel } from '../../../../core/models/mesa/mesa.model';
import { InventarioModel } from '../../../../core/models/inventario/inventario.model';
import { VentaService } from '../../../../core/services/venta.service';
import { InventarioService } from '../../../../core/services/inventario.service';
import { MesaService } from '../../../../core/services/mesa.service';
import { ProductoService } from '../../../../core/services/producto.service';

interface EstadisticaCard {
  titulo: string;
  valor: string | number;
  icono: string;
  color: string;
  descripcion?: string;
  trend?: {
    valor: number;
    direccion: 'up' | 'down';
  };
}

interface ProductoTop {
  nombre: string;
  cantidad: number;
  total: number;
}

@Component({
  selector: 'app-index-dashboard',
  standalone: true,
  templateUrl: './index-dashboard.component.html',
  styleUrls: ['./index-dashboard.component.scss'],
  imports: [
    CommonModule,
    CardModule,
    ChartModule,
    TableModule,
    TagModule,
    ButtonModule,
    SkeletonModule,
  ],
})
export class IndexDashboardComponent implements OnInit {
  loading: boolean = true;

  // Estadísticas principales
  estadisticas: EstadisticaCard[] = [];

  // Mesas
  totalMesas: number = 0;
  mesasLibres: number = 0;
  mesasOcupadas: number = 0;
  mesas: MesaModel[] = [];

  // Inventario
  inventarios: InventarioModel[] = [];
  productosConStockBajo: InventarioModel[] = [];
  valorTotalInventario: number = 0;

  // Gráficas
  ventasChart: any;
  ventasChartOptions: any;

  metodosPagoChart: any;
  metodosPagoChartOptions: any;

  stockChart: any;
  stockChartOptions: any;

  constructor(
    private ventaService: VentaService,
    private inventarioService: InventarioService,
    private mesaService: MesaService,
    private productoService: ProductoService,
  ) {}

  async ngOnInit() {
    await this.cargarDatos();
    this.inicializarGraficas();
  }

  async cargarDatos(): Promise<void> {
    this.loading = true;
    try {
      await Promise.all([
        this.cargarMesas(),
        this.cargarInventario(),
        this.cargarVentas(),
      ]);

      this.construirEstadisticas();
    } catch (error) {
      console.error('Error cargando datos del dashboard:', error);
    } finally {
      this.loading = false;
    }
  }

  async cargarMesas(): Promise<void> {
    try {
      const response = await lastValueFrom(this.mesaService.getAllMesas());
      this.mesas = response.data || [];
      this.totalMesas = this.mesas.length;
      this.mesasLibres = this.mesas.filter((m) => m.estado === 'LIBRE').length;
      this.mesasOcupadas = this.mesas.filter(
        (m) => m.estado === 'OCUPADA',
      ).length;
    } catch (error) {
      console.error('Error cargando mesas:', error);
    }
  }

  async cargarInventario(): Promise<void> {
    try {
      const response = await lastValueFrom(
        this.inventarioService.getAllInventario(),
      );
      this.inventarios = response.data || [];

      // Calcular valor total del inventario
      this.valorTotalInventario = this.inventarios.reduce(
        (sum, inv) => sum + inv.stock * inv.precio_venta,
        0,
      );

      // Productos con stock bajo (menos de 10 unidades)
      this.productosConStockBajo = this.inventarios
        .filter((inv) => inv.stock <= 10)
        .sort((a, b) => a.stock - b.stock)
        .slice(0, 5);
    } catch (error) {
      console.error('Error cargando inventario:', error);
    }
  }

  async cargarVentas(): Promise<void> {
    try {
      // Aquí podrías cargar estadísticas de ventas
      // Por ahora usaremos datos de ejemplo
    } catch (error) {
      console.error('Error cargando ventas:', error);
    }
  }

  construirEstadisticas(): void {
    this.estadisticas = [
      {
        titulo: 'Mesas Totales',
        valor: this.totalMesas,
        icono: 'pi pi-table',
        color: '#667eea',
        descripcion: `${this.mesasLibres} libres, ${this.mesasOcupadas} ocupadas`,
      },
      {
        titulo: 'Mesas Ocupadas',
        valor: this.mesasOcupadas,
        icono: 'pi pi-users',
        color: '#dc3545',
        descripcion: `${Math.round(
          (this.mesasOcupadas / this.totalMesas) * 100,
        )}% de ocupación`,
      },
      {
        titulo: 'Productos en Stock',
        valor: this.inventarios.length,
        icono: 'pi pi-box',
        color: '#28a745',
        descripcion: `${this.productosConStockBajo.length} con stock bajo`,
      },
      {
        titulo: 'Valor Inventario',
        valor: `$${this.formatearNumero(this.valorTotalInventario)}`,
        icono: 'pi pi-dollar',
        color: '#ffc107',
        descripcion: 'Valor total en stock',
      },
    ];
  }

  inicializarGraficas(): void {
    this.inicializarVentasChart();
    this.inicializarMetodosPagoChart();
    this.inicializarStockChart();
  }

  inicializarVentasChart(): void {
    const documentStyle = getComputedStyle(document.documentElement);

    this.ventasChart = {
      labels: ['Lun', 'Mar', 'Mié', 'Jue', 'Vie', 'Sáb', 'Dom'],
      datasets: [
        {
          label: 'Ventas de la Semana',
          data: [250000, 320000, 180000, 450000, 520000, 680000, 590000],
          fill: true,
          borderColor:
            documentStyle.getPropertyValue('--primary-color') || '#667eea',
          backgroundColor: 'rgba(102, 126, 234, 0.2)',
          tension: 0.4,
        },
      ],
    };

    this.ventasChartOptions = {
      maintainAspectRatio: false,
      aspectRatio: 0.6,
      plugins: {
        legend: {
          labels: {
            color: documentStyle.getPropertyValue('--text-color') || '#495057',
          },
        },
      },
      scales: {
        x: {
          ticks: {
            color:
              documentStyle.getPropertyValue('--text-color-secondary') ||
              '#6c757d',
          },
          grid: {
            color:
              documentStyle.getPropertyValue('--surface-border') || '#e9ecef',
          },
        },
        y: {
          ticks: {
            color:
              documentStyle.getPropertyValue('--text-color-secondary') ||
              '#6c757d',
            callback: (value: any) => {
              return '$' + value.toLocaleString();
            },
          },
          grid: {
            color:
              documentStyle.getPropertyValue('--surface-border') || '#e9ecef',
          },
        },
      },
    };
  }

  inicializarMetodosPagoChart(): void {
    const documentStyle = getComputedStyle(document.documentElement);

    this.metodosPagoChart = {
      labels: ['Efectivo', 'Tarjeta', 'Transferencia', 'Digital'],
      datasets: [
        {
          data: [45, 30, 15, 10],
          backgroundColor: ['#28a745', '#007bff', '#ffc107', '#6f42c1'],
          hoverBackgroundColor: ['#218838', '#0056b3', '#e0a800', '#5a32a3'],
        },
      ],
    };

    this.metodosPagoChartOptions = {
      plugins: {
        legend: {
          labels: {
            usePointStyle: true,
            color: documentStyle.getPropertyValue('--text-color') || '#495057',
          },
        },
      },
    };
  }

  inicializarStockChart(): void {
    const documentStyle = getComputedStyle(document.documentElement);
    const productos = this.inventarios.slice(0, 5);

    this.stockChart = {
      labels: productos.map(
        (p) => p.producto_nombre?.substring(0, 15) || 'N/A',
      ),
      datasets: [
        {
          label: 'Stock Actual',
          backgroundColor: '#667eea',
          borderColor: '#667eea',
          data: productos.map((p) => p.stock),
        },
      ],
    };

    this.stockChartOptions = {
      indexAxis: 'y',
      maintainAspectRatio: false,
      aspectRatio: 0.8,
      plugins: {
        legend: {
          labels: {
            color: documentStyle.getPropertyValue('--text-color') || '#495057',
          },
        },
      },
      scales: {
        x: {
          ticks: {
            color:
              documentStyle.getPropertyValue('--text-color-secondary') ||
              '#6c757d',
          },
          grid: {
            color:
              documentStyle.getPropertyValue('--surface-border') || '#e9ecef',
          },
        },
        y: {
          ticks: {
            color:
              documentStyle.getPropertyValue('--text-color-secondary') ||
              '#6c757d',
          },
          grid: {
            color:
              documentStyle.getPropertyValue('--surface-border') || '#e9ecef',
          },
        },
      },
    };
  }

  formatearNumero(valor: number): string {
    return valor.toLocaleString('es-CO');
  }

  getStockSeverity(
    stock: number,
  ): 'success' | 'info' | 'warn' | 'danger' | 'secondary' | 'contrast' {
    if (stock === 0) return 'danger';
    if (stock <= 10) return 'warn';
    return 'success';
  }

  getMesaClass(mesa: MesaModel): string {
    return mesa.estado === 'LIBRE' ? 'mesa-libre' : 'mesa-ocupada';
  }
}

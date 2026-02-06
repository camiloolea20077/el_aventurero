# El Aventurero Front - Bar Management System

## Project Overview
El Aventurero Front is a comprehensive Angular-based bar management system designed for full-service bar operations. This is a production-ready application with complete CRUD functionality, real-time table management, inventory control, and financial tracking.

## Architecture
- **Framework**: Angular 18+ with standalone components
- **UI Framework**: PrimeNG with Lara theme
- **Language**: TypeScript 5.5+
- **Database**: IndexedDB for client-side storage
- **API**: RESTful backend at localhost:9001
- **Authentication**: JWT-based with role-based access control
- **Styling**: CSS with PrimeFlex for responsive design

## Key Features
- Table management with real-time status tracking
- Product catalog and inventory control
- Order processing and consumption tracking
- Multi-payment method support
- Cash flow management and reconciliation
- Role-based permissions system
- Responsive mobile-first design
- Offline capabilities with IndexedDB

## Project Structure

### Core Layer (`src/app/core/`)
**Services**: Authentication, tables, products, inventory, sales, purchases, cash flow, and more
**Models**: Complete TypeScript interfaces for all entities
**Security**: Auth guards and HTTP interceptors

### Feature Modules (`src/app/modules/`)
- Dashboard with real-time statistics
- Table management (mesas)
- Product catalog
- Inventory control
- Sales management
- Purchase tracking
- Cash flow operations
- Register reconciliation

### Shared Components (`src/shared/`)
- Layout components (navbar, sidebar, main layout)
- Utility services and helpers
- Common UI components

## Development Setup

### Prerequisites
- Node.js 22.14.0+
- npm 10.9.2+
- Angular CLI 18.2.21

### Installation
```bash
npm install
```

### Development Server
```bash
ng serve
```
Navigate to `http://localhost:4200/`

### Build
```bash
ng build
```

### Testing
```bash
ng test
```

## API Configuration
Base URL: `http://localhost:9001/`

### Endpoints
- `/api/productos` - Product management
- `/api/mesas` - Table operations
- `/api/inventario` - Inventory control
- `/api/consumo-mesa` - Table consumption
- `/api/compras` - Purchase management
- `/api/ventas` - Sales operations
- `/api/flujo-caja` - Cash flow
- `/api/arqueo-caja` - Register reconciliation

## Authentication
- JWT tokens stored in IndexedDB
- Role-based access control
- Route guards for protected areas
- Automatic token refresh

## Data Models
Key entities include:
- Users with role-based permissions
- Tables with status tracking
- Products with pricing and inventory
- Sales transactions with multiple payment methods
- Purchase orders with supplier tracking
- Cash flow movements
- Inventory adjustments

## UI/UX Features
- Responsive design for all devices
- Real-time notifications
- Interactive dashboard with charts
- Visual table status indicators
- Confirmation dialogs for destructive actions
- Loading states and skeleton screens

## Business Logic
The system handles complete bar operations:
- Table assignment and release
- Order taking and consumption tracking
- Bill splitting and payment processing
- Inventory level monitoring
- Automated low-stock alerts
- Daily/weekly closing procedures
- Revenue and expense tracking

## Security Considerations
- JWT-based authentication
- Role-based authorization
- Input validation and sanitization
- XSS prevention measures
- CSRF protection headers

## Performance Optimizations
- Lazy-loaded modules
- OnPush change detection strategy
- Efficient data caching with IndexedDB
- Optimized bundle splitting
- Image lazy loading

## Localization
- Colombian Spanish (es-CO) configuration
- Localized date and number formatting
- Currency formatting for Colombian Peso

## Deployment Notes
- SSR (Server-Side Rendering) enabled
- Environment-specific configurations
- Production build optimizations
- Docker containerization ready

## Future Enhancements
- Real-time WebSocket updates
- Advanced reporting capabilities
- Mobile app development
- Integration with payment gateways
- Multi-location support

## Dependencies
Key production dependencies:
- Angular 18.2.14
- PrimeNG 18.0.2
- Chart.js 4.5.1
- LocalForage 1.10.0
- Express 4.18.2

## Contributing Guidelines
- Follow Angular best practices
- Maintain consistent code formatting
- Add unit tests for new features
- Update documentation for API changes
- Use semantic versioning for releases
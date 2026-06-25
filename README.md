# HR Onboarding Application

An enterprise-grade Java Spring Boot application designed to streamline the employee onboarding process. The system allows HR teams to create reusable onboarding templates, managers to assign employees and monitor progress, and new hires to track their onboarding journey.

## Technology Stack

- **Framework**: Spring Boot 3.3.0
- **Language**: Java 21
- **Build Tool**: Maven
- **Database**: H2 (development), PostgreSQL compatible
- **Template Engine**: Thymeleaf
- **Authentication**: Spring Security with role-based access control
- **ORM**: Spring Data JPA with Hibernate

## Architecture

### Core Components

1. **Models** (`model/entity/`)
   - `User` - System users with role-based access
   - `OnboardingTemplate` - Reusable onboarding templates created by HR team
   - `TemplateTask` - Individual tasks within a template
   - `Employee` - Employee records linked to users
   - `OnboardingProgress` - Tracks onboarding lifecycle for each employee
   - `ProgressTask` - Individual task progress for an employee

2. **Repositories** (`repository/`)
   - Data access layer using Spring Data JPA
   - Custom query methods for efficient data retrieval

3. **Services** (`service/`)
   - `UserService` - User management and authentication
   - `OnboardingTemplateService` - Template CRUD and task management
   - `EmployeeService` - Employee record management
   - `OnboardingProgressService` - Onboarding workflow management
   - `CustomUserDetailsService` - Spring Security integration

4. **Controllers** (`controller/`)
   - `AuthController` - Authentication (login, register, logout)
   - `HomeController` - Home page and dashboard routing
   - `HrController` - HR team features (template management)
   - `ManagerController` - Manager features (team monitoring)
   - `EmployeeController` - Employee features (progress tracking)

### Security Model

The application uses role-based access control with four roles:

| Role | Permissions | Endpoint |
|------|-------------|----------|
| `ADMIN` | Full system access | `/admin/**` |
| `HR_TEAM` | Create/manage onboarding templates | `/hr/**` |
| `MANAGER` | Monitor employee progress | `/manager/**` |
| `EMPLOYEE` | View assigned onboarding tasks | `/employee/**` |

## Features

### HR Team Features
- ✅ Create new onboarding templates
- ✅ Define tasks and milestones for templates
- ✅ View all created templates
- ✅ Add tasks to templates

### Manager Features
- ✅ View team members assigned to them
- ✅ Monitor employee onboarding progress
- ✅ View detailed task status for each employee
- ✅ Track expected vs. actual completion dates

### Employee Features
- ✅ View assigned onboarding workflows
- ✅ Track progress on assigned tasks
- ✅ View task status (Not Started, In Progress, Completed, Blocked)
- ✅ See expected onboarding timeline

## Getting Started

### Prerequisites
- Java 21+
- Maven 3.6+

### Build
```bash
mvn clean install
```

### Run
```bash
mvn spring-boot:run
```

The application will start on `http://localhost:8080`

### Database
- Development: H2 in-memory database
- H2 Console: `http://localhost:8080/h2-console`
- Credentials: username `sa`, password (empty)

### Demo Accounts

The application seeds three starter users on startup:

| Role | Email | Password |
|------|-------|----------|
| HR Team | `hr@example.com` | `password` |
| Manager | `manager@example.com` | `password` |
| Employee | `employee@example.com` | `password` |

## Project Structure

```
hr-onboarding-java-app/
├── src/main/java/com/example/hronboarding/
│   ├── config/                 # Security and configuration
│   ├── controller/             # REST/MVC controllers
│   ├── model/
│   │   ├── entity/             # JPA entities
│   │   └── enums/              # Enumerations
│   ├── repository/             # Spring Data JPA repositories
│   ├── service/                # Business logic
│   └── HrOnboardingApplication.java
├── src/main/resources/
│   ├── static/                 # CSS, JS assets
│   ├── templates/              # Thymeleaf HTML templates
│   └── application.properties
└── pom.xml
```

## Key Entities and Relationships

```
User (1) ──────→ (Many) Employee
         └──────→ (Many) OnboardingTemplate (created_by)
         └──────→ (Many) Employee (as manager)

OnboardingTemplate (1) ──────→ (Many) TemplateTask
                      └──────→ (Many) OnboardingProgress

Employee (1) ──────→ (Many) OnboardingProgress

OnboardingProgress (1) ──────→ (Many) ProgressTask
                      └──────→ TemplateTask (reference)
```

## API Endpoints

### Authentication
- `GET /` - Home page
- `GET /auth/login` - Login form
- `POST /auth/login` - Process login
- `GET /auth/register` - Registration form
- `POST /auth/register` - Create new user
- `GET /auth/logout` - Logout

### Dashboard
- `GET /dashboard` - User dashboard

### HR Team
- `GET /hr/templates` - List all templates
- `GET /hr/templates/new` - Create template form
- `POST /hr/templates` - Save template
- `GET /hr/templates/{id}` - View template details
- `POST /hr/templates/{id}/tasks` - Add task to template

### Manager
- `GET /manager/team` - List team members
- `GET /manager/employee/{employeeId}/progress` - View employee progress
- `GET /manager/progress/{progressId}` - View progress details

### Employee
- `GET /employee/dashboard` - Employee dashboard
- `GET /employee/progress/{progressId}` - View progress details

## Development Notes

### Adding New Features

1. **Database Model**: Add JPA entity in `model/entity/`
2. **Data Access**: Create repository interface extending `JpaRepository`
3. **Business Logic**: Add service class with CRUD operations
4. **Controller**: Create controller with mapped endpoints
5. **Templates**: Add Thymeleaf HTML templates in `resources/templates/`

### Entity Annotations Used
- `@Entity` - Marks class as JPA entity
- `@Table` - Specifies database table
- `@Id` - Primary key
- `@GeneratedValue` - Auto-generate ID
- `@ManyToOne` - Many-to-one relationship
- `@OneToOne` - One-to-one relationship
- `@ManyToMany` - Many-to-many relationship
- `@Column` - Column configuration
- `@Enumerated` - Enum field mapping

## Future Enhancements

- [ ] Document upload for onboarding tasks
- [ ] Email notifications for progress updates
- [ ] Advanced reporting and analytics
- [ ] API for third-party integrations
- [ ] Mobile-friendly improvements
- [ ] Approval workflow system
- [ ] Employee survey/feedback system
- [ ] Audit logging
- [ ] Task templates library
- [ ] Role-based dashboard customization

## Configuration

### application.properties
```properties
# Server
server.port=8080

# Database (H2)
spring.datasource.url=jdbc:h2:mem:testdb
spring.jpa.hibernate.ddl-auto=create-drop

# Thymeleaf
spring.thymeleaf.cache=false

# Logging
logging.level.com.example.hronboarding=DEBUG
```

## Testing

Run all tests:
```bash
mvn test
```

Run specific test class:
```bash
mvn test -Dtest=ClassName
```

## Deployment

### Docker

1. Build the application:
```bash
mvn clean package
```

2. Create Dockerfile (if needed) and build image
3. Run container with appropriate database connection

### Production Considerations
- Switch to PostgreSQL for production
- Configure proper logging
- Set up monitoring and alerts
- Enable HTTPS/SSL
- Configure proper authentication (OAuth2, SAML)
- Implement backup strategy
- Set up CI/CD pipeline

## Contributing

1. Create feature branch: `git checkout -b feature/your-feature`
2. Commit changes: `git commit -m "Add your feature"`
3. Push to branch: `git push origin feature/your-feature`
4. Open a Pull Request

## License

This project is part of an HR management system.

## Support

For issues or questions, please contact the development team.

---

**Built with** ☕ **Java** and **Spring Boot**

# Stock Inventory Management System — Backend Architecture

## Tech Stack

- **Java** 21+
- **Spring Boot** 3.x
- **Spring Data JPA** (Hibernate)
- **MySQL**
- **Maven**
- **Lombok**
- **Jakarta Validation**
- **REST APIs**

---

## Base Package

```
com.rutika.inventory
```

---

## Project Structure

```
inventory-backend/
├── pom.xml
└── src/
    ├── main/
    │   ├── java/com/rutika/inventory/
    │   │   ├── InventoryBackendApplication.java
    │   │   ├── config/
    │   │   │   ├── CorsConfig.java
    │   │   │   └── JacksonConfig.java
    │   │   ├── constants/
    │   │   │   ├── ApiConstants.java
    │   │   │   └── MessageConstants.java
    │   │   ├── controller/
    │   │   │   ├── ProductController.java
    │   │   │   └── StockController.java
    │   │   ├── dto/
    │   │   │   ├── request/
    │   │   │   │   ├── ProductRequest.java
    │   │   │   │   ├── StockInRequest.java
    │   │   │   │   └── StockOutRequest.java
    │   │   │   └── response/
    │   │   │       ├── ProductResponse.java
    │   │   │       ├── StockInResponse.java
    │   │   │       └── StockOutResponse.java
    │   │   ├── entity/
    │   │   │   ├── Product.java
    │   │   │   ├── StockIn.java
    │   │   │   ├── StockOut.java
    │   │   │   └── Category.java
    │   │   ├── enums/
    │   │   │   ├── ProductStatus.java
    │   │   │   └── StockStatus.java
    │   │   ├── exception/
    │   │   │   ├── BadRequestException.java
    │   │   │   ├── GlobalExceptionHandler.java
    │   │   │   ├── ResourceNotFoundException.java
    │   │   │   └── ValidationException.java
    │   │   ├── mapper/
    │   │   │   ├── ProductMapper.java
    │   │   │   └── StockMapper.java
    │   │   ├── repository/
    │   │   │   ├── CategoryRepository.java
    │   │   │   ├── ProductRepository.java
    │   │   │   ├── StockInRepository.java
    │   │   │   └── StockOutRepository.java
    │   │   ├── response/
    │   │   │   ├── ApiResponse.java
    │   │   │   └── PageResponse.java
    │   │   ├── service/
    │   │   │   ├── interfaces/
    │   │   │   │   ├── ProductService.java
    │   │   │   │   └── StockService.java
    │   │   │   └── impl/
    │   │   │       ├── ProductServiceImpl.java
    │   │   │       └── StockServiceImpl.java
    │   │   ├── util/
    │   │   │   ├── DateTimeUtil.java
    │   │   │   └── UuidUtil.java
    │   │   └── validator/
    │   │       ├── ProductValidator.java
    │   │       └── StockValidator.java
    │   └── resources/
    │       ├── application.yml
    │       ├── static/
    │       └── templates/
    └── test/java/
```

---

## Package Responsibilities

### `config/` — Application Configuration

| Aspect | Detail |
|---|---|
| **Why** | Centralizes Spring beans, CORS, and serialization configuration |
| **Classes** | `CorsConfig` — cross-origin settings for React frontend on `localhost:3000`; `JacksonConfig` — registers `JavaTimeModule` for ISO-8601 date serialization |
| **Never put here** | Business logic, repositories, controllers |
| **Best practice** | Keep beans stateless; use `@Configuration` + `@Bean` over XML |

---

### `constants/` — Centralized Constants

| Aspect | Detail |
|---|---|
| **Why** | Eliminates magic strings and hardcoded literals; single source of truth |
| **Classes** | `ApiConstants` — endpoint paths (`/api/v1/products`, etc.); `MessageConstants` — response messages (`" created successfully"`, `" not found with id: "`) |
| **Never put here** | Mutable state, business logic, environment-specific configuration |
| **Best practice** | Use `final class` with `private constructor()` to prevent instantiation |

---

### `controller/` — HTTP Request Handling

| Aspect | Detail |
|---|---|
| **Why** | Exposes REST endpoints; must be thin |
| **Classes** | `ProductController` — product CRUD endpoints; `StockController` — stock-in/stock-out endpoints |
| **Never put here** | Business logic, database calls, DTO-to-entity mapping, validation logic |
| **Best practice** | One controller per aggregate root; use `@RestController` + constructor injection; delegate everything to services |

---

### `dto/request/` — Inbound Payloads

| Aspect | Detail |
|---|---|
| **Why** | Decouples API contract from entity model; Jakarta Validation annotations live here |
| **Classes** | `ProductRequest` — name, sku, price, etc.; `StockInRequest` — productId, quantity, referenceNumber; `StockOutRequest` — productId, quantity, reason |
| **Never put here** | Entity annotations (`@Entity`, `@Table`), business logic, database mappings |
| **Best practice** | Use Lombok `@Getter` `@Setter`; validate with `@NotBlank`, `@Positive`, etc.; never reuse for multiple endpoints with different constraints |

---

### `dto/response/` — Outbound Payloads

| Aspect | Detail |
|---|---|
| **Why** | Controls exactly what data is exposed to the client; prevents entity leaking |
| **Classes** | `ProductResponse` — id, name, sku, price, stockQuantity, status, timestamps; `StockInResponse` — product details + quantity + reference; `StockOutResponse` — product details + quantity + reason |
| **Never put here** | Entity references, JPA annotations, sensitive fields |
| **Best practice** | Flatten nested entities; use only primitive types and Strings; append `Response` suffix for clarity |

---

### `entity/` — JPA Database Mappings

| Aspect | Detail |
|---|---|
| **Why** | ORM mapping between Java objects and MySQL tables |
| **Classes** | `Product` — core product with name, sku, price, stockQuantity, status, timestamps, category FK; `StockIn` — inbound stock transaction; `StockOut` — outbound stock transaction; `Category` — product category |
| **Never put here** | Business logic, DTO conversions, API logic |
| **Best practice** | UUID as `CHAR(36)`; UTC timestamps with `Instant`; `@PrePersist`/`@PreUpdate` for lifecycle hooks; explicit `@Column(name = "snake_case")`; soft delete via `status` field |

---

### `enums/` — Domain Enumerations

| Aspect | Detail |
|---|---|
| **Why** | Type-safe constants for statuses and fixed domain values |
| **Classes** | `ProductStatus` — `ACTIVE`, `INACTIVE`, `DISCONTINUED`; `StockStatus` — `ACTIVE`, `CANCELLED`, `COMPLETED` |
| **Never put here** | Complex logic, database interactions |
| **Best practice** | Store as string in DB; enums provide a controlled vocabulary for status transitions |

---

### `exception/` — Exception Handling

| Aspect | Detail |
|---|---|
| **Why** | Separates error handling from business logic; ensures consistent error JSON |
| **Classes** | `ResourceNotFoundException` — 404 with resource name + field info; `ValidationException` — 400 with field-level error map; `BadRequestException` — 400 with message; `GlobalExceptionHandler` — `@RestControllerAdvice` catching all exceptions |
| **Never put here** | Business logic, service calls, API responses |
| **Best practice** | One handler class for the entire app; log at handler level; never swallow exceptions; use `ResponseEntity` for full HTTP control |

---

### `mapper/` — Object Conversion

| Aspect | Detail |
|---|---|
| **Why** | Converts DTO ↔ Entity; keeps mapping logic out of controllers and services |
| **Classes** | `ProductMapper` — `toEntity()`, `toResponse()`, `updateEntityFromRequest()`; `StockMapper` — `toInEntity()`, `toInResponse()`, `toOutEntity()`, `toOutResponse()` |
| **Never put here** | Repository calls, business logic, validation |
| **Best practice** | Use `@Component`; one method per conversion direction; consider MapStruct for large projects, but manual mappers give full control |

---

### `repository/` — Database Access Layer

| Aspect | Detail |
|---|---|
| **Why** | Data access only; Spring Data JPA provides CRUD and query derivation |
| **Classes** | `ProductRepository` — `findBySku()`, `existsBySku()`, `findByStatus()`; `StockInRepository` — `findByProductIdOrderByCreatedAtDesc()`; `StockOutRepository` — `findByProductIdOrderByCreatedAtDesc()`; `CategoryRepository` — `findByName()`, `existsByName()` |
| **Never put here** | Business logic, service-layer concerns, manual transaction management |
| **Best practice** | Only `extends JpaRepository<T, ID>`; use derived query methods; put `@Transactional` at service layer, not repository |

---

### `response/` — API Response Wrappers

| Aspect | Detail |
|---|---|
| **Why** | Standardized JSON envelope for every API response |
| **Classes** | `ApiResponse<T>` — `success`, `message`, `data`, `timestamp`; `PageResponse<T>` — `content`, `page`, `size`, `totalElements`, `totalPages`, `first`, `last` |
| **Never put here** | Entity references, request handling, business logic |
| **Best practice** | Static factory methods (`success()`, `error()`); generic `<T>` for data field; `@Builder` for clean construction |

---

### `service/interfaces/` — Service Contracts

| Aspect | Detail |
|---|---|
| **Why** | Programming to interfaces enables mocking in tests and swapping implementations |
| **Classes** | `ProductService` — `createProduct()`, `getProductById()`, `getAllProducts()`, `updateProduct()`, `deleteProduct()`; `StockService` — `addStock()`, `removeStock()` |
| **Never put here** | Implementation details, database calls, `@Transactional`, `@Service` |
| **Best practice** | Define all public business methods here; method signatures use DTOs, never entities |

---

### `service/impl/` — Business Logic

| Aspect | Detail |
|---|---|
| **Why** | Single place for all business rules, orchestration, and transaction management |
| **Classes** | `ProductServiceImpl` — CRUD operations with validation integration; `StockServiceImpl` — stock in/out with quantity updates |
| **Never put here** | HTTP concerns, entity-to-DTO mapping (use mapper), database query logic |
| **Best practice** | `@Service` + `@RequiredArgsConstructor` + `@Transactional`; keep methods under 20 lines; inject only repositories and mappers |

---

### `util/` — Stateless Helpers

| Aspect | Detail |
|---|---|
| **Why** | Reusable utility functions with no side effects |
| **Classes** | `DateTimeUtil` — `toUtcString()`, `toInstant()`, `nowUtc()`; `UuidUtil` — `generateId()`, `isValidUuid()` |
| **Never put here** | Spring beans with dependencies, database access, mutable state |
| **Best practice** | `final class` with `private constructor()`; all methods `static`; group by concern |

---

### `validator/` — Business Validation

| Aspect | Detail |
|---|---|
| **Why** | Complex validation rules beyond simple annotations; keeps controllers clean |
| **Classes** | `ProductValidator` — SKU uniqueness on create/update; `StockValidator` — sufficient stock check before stock-out |
| **Never put here** | Database writes, service orchestration, HTTP concerns |
| **Best practice** | `@Component` with injected repositories; throw `ValidationException` with field-level error maps; name methods `validateCreate()`, `validateUpdate()`, etc. |

---

## Coding Standards

### General

- Lowercase package names
- Java naming conventions (`camelCase`, `PascalCase`, `SCREAMING_SNAKE_CASE`)
- Controllers are thin — no business logic
- Business logic lives only in `service/impl/`
- Repositories only interact with the database
- **Never expose entities directly to the frontend** — always use DTOs
- Constructor injection over field injection
- Methods are small and readable
- Follow SOLID principles

### Database Standards

- UUID stored as `CHAR(36)`
- UUID generated in `@PrePersist` lifecycle callback
- All timestamps in UTC using `Instant`
- Soft delete via `status` field (`ACTIVE` / `INACTIVE`)
- Lowercase `snake_case` column names
- Foreign key constraints with named `@ForeignKey`
- JPA entities map correctly via `@Column`, `@JoinColumn`, `@Table`

### Entity Standards

- Only database mappings, relationships, and lifecycle callbacks
- No business logic inside entities
- `@PrePersist` sets UUID, timestamps, and default status
- `@PreUpdate` updates the `updatedAt` timestamp

### DTO Standards

- Separate `dto/request/` and `dto/response/` packages
- Request DTOs carry Jakarta Validation annotations
- Response DTOs contain only what the frontend needs
- Never reuse request DTOs for different endpoints with different constraints

### API Response Format

Every API returns a consistent JSON envelope:

```json
{
  "success": true,
  "message": "Product created successfully",
  "data": {},
  "timestamp": "2026-06-26T10:30:00Z"
}
```

Error responses return the same structure with `success: false` and relevant error details in `data`.

---

## Data Flow

```
HTTP Request
    │
    ▼
Controller  ──→  DTO (Request)  ──→  Validator
    │                                       │
    │                                       ▼
    │                              (throws ValidationException on failure)
    │
    ▼
Service Interface  ──→  Service Impl  ──→  Mapper (Request → Entity)
    │                                               │
    │                                               ▼
    │                                        Repository (JPA)
    │                                               │
    │                                               ▼
    │                                        Database (MySQL)
    │                                               │
    │                                               ▼
    │                                        Mapper (Entity → Response)
    │                                               │
    ▼                                               ▼
Controller  ◄──────────────────────────────  ApiResponse<DTO>
    │
    ▼
HTTP Response (JSON)
```

---

## Dependencies (`pom.xml`)

| Dependency | Purpose |
|---|---|
| `spring-boot-starter-data-jpa` | Hibernate + JPA + Spring Data |
| `spring-boot-starter-validation` | Jakarta Bean Validation |
| `spring-boot-starter-web` | REST controllers / embedded Tomcat |
| `mysql-connector-j` | MySQL JDBC driver |
| `lombok` | Boilerplate reduction (`@Getter`, `@Setter`, `@Builder`, etc.) |
| `spring-boot-starter-test` | JUnit 5, Mockito, integration test support |

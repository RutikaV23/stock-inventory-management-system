# File Reference — Stock Inventory Management System

Every Java file in this project, its purpose, and when to use it.

---

## `InventoryBackendApplication.java`

**Path:** `src/main/java/com/rutika/inventory/InventoryBackendApplication.java`

| | |
|---|---|
| **What it does** | Spring Boot entry point. Bootstraps the entire application |
| **When to use** | Only to start the app via `java -jar` or `mvn spring-boot:run`. Never import or instantiate this class elsewhere |
| **Key annotation** | `@SpringBootApplication` — enables auto-configuration, component scanning, and `@EnableAutoConfiguration` |

---

## config/

### `CorsConfig.java`

**Path:** `src/main/java/com/rutika/inventory/config/CorsConfig.java`

| | |
|---|---|
| **What it does** | Allows the React frontend (localhost:3000) to call backend APIs from a different origin |
| **When to use** | Automatically loaded on startup. Add new allowed origins/methods here if needed |
| **Key method** | `addCorsMappings()` — exposes all `/api/**` endpoints to `localhost:3000` |

### `JacksonConfig.java`

**Path:** `src/main/java/com/rutika/inventory/config/JacksonConfig.java`

| | |
|---|---|
| **What it does** | Configures JSON serialization — registers `JavaTimeModule` for `Instant` support and disables timestamp output |
| **When to use** | Automatically loaded on startup. Modify if you need custom date formats or serialization rules |

---

## constants/

### `ApiConstants.java`

**Path:** `src/main/java/com/rutika/inventory/constants/ApiConstants.java`

| | |
|---|---|
| **What it does** | Single source of truth for all REST endpoint paths |
| **When to use** | In `@RequestMapping` on controllers. In tests or clients that reference API paths. Never hardcode a path string |
| **Key constants** | `BASE_PATH`, `PRODUCT_PATH`, `STOCK_IN_PATH`, `STOCK_OUT_PATH` |

### `MessageConstants.java`

**Path:** `src/main/java/com/rutika/inventory/constants/MessageConstants.java`

| | |
|---|---|
| **What it does** | Centralizes all human-readable response messages and resource names |
| **When to use** | In services when building response messages or exception messages. Never hardcode strings like `"Product not found"` |
| **Key constants** | `CREATED_SUCCESS`, `NOT_FOUND`, `PRODUCT`, `STOCK_IN` |

---

## controller/

### `ProductController.java`

**Path:** `src/main/java/com/rutika/inventory/controller/ProductController.java`

| | |
|---|---|
| **What it does** | REST controller for product CRUD — maps to `GET/POST/PUT/DELETE /api/v1/products` |
| **When to use** | When the frontend needs to create, read, update, or delete products. Add methods like `createProduct()`, `getProductById()` |
| **Coding rule** | Keep thin — only call service methods and return `ApiResponse`. No business logic here |

### `StockController.java`

**Path:** `src/main/java/com/rutika/inventory/controller/StockController.java`

| | |
|---|---|
| **What it does** | REST controller for stock movements — maps to `/api/v1/stock` endpoints |
| **When to use** | When handling stock-in or stock-out operations from the frontend |
| **Coding rule** | Same as `ProductController` — delegate everything to `StockService` |

---

## dto/request/

### `ProductRequest.java`

**Path:** `src/main/java/com/rutika/inventory/dto/request/ProductRequest.java`

| | |
|---|---|
| **What it does** | Inbound payload for creating/updating a product. Carries Jakarta Validation annotations |
| **When to use** | As `@RequestBody` parameter in `ProductController` methods |
| **Fields** | `name`, `description`, `sku`, `price`, `categoryId`, `reorderLevel` |
| **Validation** | `@NotBlank` on name and sku, `@Positive` on price |

### `StockInRequest.java`

**Path:** `src/main/java/com/rutika/inventory/dto/request/StockInRequest.java`

| | |
|---|---|
| **What it does** | Inbound payload for adding stock to a product |
| **When to use** | As `@RequestBody` in stock-in endpoint |
| **Fields** | `productId`, `quantity`, `referenceNumber`, `notes` |

### `StockOutRequest.java`

**Path:** `src/main/java/com/rutika/inventory/dto/request/StockOutRequest.java`

| | |
|---|---|
| **What it does** | Inbound payload for removing stock from a product |
| **When to use** | As `@RequestBody` in stock-out endpoint |
| **Fields** | `productId`, `quantity`, `reason`, `referenceNumber` |

---

## dto/response/

### `ProductResponse.java`

**Path:** `src/main/java/com/rutika/inventory/dto/response/ProductResponse.java`

| | |
|---|---|
| **What it does** | Outbound payload sent to the frontend for product data. Never exposes the `Product` entity directly |
| **When to use** | As return type in `ProductService` and `ProductController` methods |
| **Fields** | `id`, `name`, `description`, `sku`, `price`, `stockQuantity`, `categoryName`, `status`, `createdAt`, `updatedAt` |

### `StockInResponse.java`

**Path:** `src/main/java/com/rutika/inventory/dto/response/StockInResponse.java`

| | |
|---|---|
| **What it does** | Outbound payload for stock-in transaction data |
| **When to use** | Returned after a successful stock-in operation |
| **Fields** | Includes flattened product details: `productId`, `productName`, `productSku` |

### `StockOutResponse.java`

**Path:** `src/main/java/com/rutika/inventory/dto/response/StockOutResponse.java`

| | |
|---|---|
| **What it does** | Outbound payload for stock-out transaction data |
| **When to use** | Returned after a successful stock-out operation |
| **Fields** | Includes `reason` field to explain why stock was removed |

---

## entity/

### `Product.java`

**Path:** `src/main/java/com/rutika/inventory/entity/Product.java`

| | |
|---|---|
| **What it does** | JPA entity mapping to the `products` table. Core domain object holding product data and stock quantity |
| **Lifecycle** | `@PrePersist` generates UUID, sets timestamps, defaults status to `ACTIVE` and stock to `0`. `@PreUpdate` refreshes `updatedAt` |
| **Relationships** | `@ManyToOne` to `Category` (lazy loaded) |
| **When to use** | This is the central entity — referenced by `StockIn`, `StockOut`, and all product operations |

### `StockIn.java`

**Path:** `src/main/java/com/rutika/inventory/entity/StockIn.java`

| | |
|---|---|
| **What it does** | JPA entity mapping to the `stock_in` table. Records inbound stock transactions |
| **Fields** | `quantity`, `referenceNumber`, `notes` |
| **Relationship** | `@ManyToOne` to `Product` with foreign key `fk_stock_in_product` |
| **When to use** | Whenever stock is added to a product's inventory |

### `StockOut.java`

**Path:** `src/main/java/com/rutika/inventory/entity/StockOut.java`

| | |
|---|---|
| **What it does** | JPA entity mapping to the `stock_out` table. Records outbound stock transactions |
| **Fields** | `quantity`, `reason`, `referenceNumber` |
| **Relationship** | `@ManyToOne` to `Product` with foreign key `fk_stock_out_product` |
| **When to use** | Whenever stock is removed from a product's inventory |

### `Category.java`

**Path:** `src/main/java/com/rutika/inventory/entity/Category.java`

| | |
|---|---|
| **What it does** | JPA entity mapping to the `categories` table. Groups products by category |
| **Fields** | `name` (unique), `description` |
| **When to use** | Referenced by `Product.category`. Manage categories separately for product organization |

---

## enums/

### `ProductStatus.java`

**Path:** `src/main/java/com/rutika/inventory/enums/ProductStatus.java`

| | |
|---|---|
| **What it does** | Defines valid states for a product: `ACTIVE`, `INACTIVE`, `DISCONTINUED` |
| **When to use** | Referenced when setting/filtering product status. Stored as string in the `status` column |

### `StockStatus.java`

**Path:** `src/main/java/com/rutika/inventory/enums/StockStatus.java`

| | |
|---|---|
| **What it does** | Defines valid states for stock transactions: `ACTIVE`, `CANCELLED`, `COMPLETED` |
| **When to use** | Referenced when auditing or filtering stock movements |

---

## exception/

### `ResourceNotFoundException.java`

**Path:** `src/main/java/com/rutika/inventory/exception/ResourceNotFoundException.java`

| | |
|---|---|
| **What it does** | Thrown when a requested entity (Product, Category, etc.) is not found in the database |
| **When to use** | In services, after a `findById()` returns empty — triggers a 404 response |
| **Signature** | `new ResourceNotFoundException("Product", "id", productId)` |

### `ValidationException.java`

**Path:** `src/main/java/com/rutika/inventory/exception/ValidationException.java`

| | |
|---|---|
| **What it does** | Thrown when custom business validation fails (e.g., duplicate SKU, insufficient stock) |
| **When to use** | In validator classes — triggers a 400 response with field-level error details |
| **Variants** | Can accept a single string message or a `Map<String, String>` of field-level errors |

### `BadRequestException.java`

**Path:** `src/main/java/com/rutika/inventory/exception/BadRequestException.java`

| | |
|---|---|
| **What it does** | Thrown for generic bad request scenarios (e.g., malformed input that passes annotation validation but fails semantic checks) |
| **When to use** | In services when a request is semantically invalid — triggers a 400 response |

### `GlobalExceptionHandler.java`

**Path:** `src/main/java/com/rutika/inventory/exception/GlobalExceptionHandler.java`

| | |
|---|---|
| **What it does** | Catches all exceptions across the entire application and returns standardized JSON error responses |
| **When to use** | Never called directly — it intercepts exceptions automatically via `@RestControllerAdvice`. Add new handlers here when creating new exception types |
| **Handles** | `ResourceNotFoundException` → 404, `ValidationException` → 400, `BadRequestException` → 400, `MethodArgumentNotValidException` → 400, generic `Exception` → 500 |

---

## mapper/

### `ProductMapper.java`

**Path:** `src/main/java/com/rutika/inventory/mapper/ProductMapper.java`

| | |
|---|---|
| **What it does** | Converts between `ProductRequest` ↔ `Product` ↔ `ProductResponse`. Isolates all mapping logic from controllers and services |
| **Methods** | `toEntity(ProductRequest)` — creates a new `Product` from a request; `toResponse(Product)` — builds the response DTO; `updateEntityFromRequest(ProductRequest, Product)` — merges request data into existing entity |
| **When to use** | Inject into `ProductServiceImpl` and call before/after database operations |

### `StockMapper.java`

**Path:** `src/main/java/com/rutika/inventory/mapper/StockMapper.java`

| | |
|---|---|
| **What it does** | Converts between stock request/response DTOs and `StockIn`/`StockOut` entities |
| **Methods** | `toInEntity()`, `toInResponse()`, `toOutEntity()`, `toOutResponse()` |
| **When to use** | Inject into `StockServiceImpl` for all stock-in and stock-out conversions |

---

## repository/

### `ProductRepository.java`

**Path:** `src/main/java/com/rutika/inventory/repository/ProductRepository.java`

| | |
|---|---|
| **What it does** | Data access for `Product` entities. Extends `JpaRepository<Product, String>` |
| **Custom queries** | `findBySku(String)` — look up by unique SKU; `findByStatus(String)` — filter active/inactive; `existsBySku(String)` — duplicate check |
| **When to use** | Inject into any service or validator that needs to read/write products |

### `StockInRepository.java`

**Path:** `src/main/java/com/rutika/inventory/repository/StockInRepository.java`

| | |
|---|---|
| **What it does** | Data access for `StockIn` entities |
| **Custom queries** | `findByProductIdOrderByCreatedAtDesc(String)` — stock-in history for a product (newest first); `findByStatus(String)` — filter by status |
| **When to use** | Inject into `StockServiceImpl` for recording inbound stock |

### `StockOutRepository.java`

**Path:** `src/main/java/com/rutika/inventory/repository/StockOutRepository.java`

| | |
|---|---|
| **What it does** | Data access for `StockOut` entities |
| **Custom queries** | Same pattern as `StockInRepository` |
| **When to use** | Inject into `StockServiceImpl` for recording outbound stock |

### `CategoryRepository.java`

**Path:** `src/main/java/com/rutika/inventory/repository/CategoryRepository.java`

| | |
|---|---|
| **What it does** | Data access for `Category` entities |
| **Custom queries** | `findByName(String)` — look up by unique name; `existsByName(String)` — duplicate check |
| **When to use** | Inject into category-related services |

---

## response/

### `ApiResponse.java`

**Path:** `src/main/java/com/rutika/inventory/response/ApiResponse.java`

| | |
|---|---|
| **What it does** | Standard JSON wrapper for every API response. Ensures the frontend always gets a predictable structure |
| **Structure** | `{ success, message, data, timestamp }` |
| **When to use** | As the return type in every controller method. Use `ApiResponse.success(msg, data)` for success, or `ApiResponse.builder()` with `success(false)` for errors |
| **Notable** | The `data` field is generic `<T>` — can hold any DTO, `PageResponse`, or `null` |

### `PageResponse.java`

**Path:** `src/main/java/com/rutika/inventory/response/PageResponse.java`

| | |
|---|---|
| **What it does** | Wraps paginated results with metadata (page, size, total elements, total pages, first/last flags) |
| **When to use** | Embedded inside `ApiResponse.data` for any endpoint that returns a list with pagination |
| **Fields** | `content` (the list), `page`, `size`, `totalElements`, `totalPages`, `first`, `last` |

---

## service/interfaces/

### `ProductService.java`

**Path:** `src/main/java/com/rutika/inventory/service/interfaces/ProductService.java`

| | |
|---|---|
| **What it does** | Interface defining the product service contract |
| **Methods** | `createProduct()`, `getProductById()`, `getAllProducts()`, `updateProduct()`, `deleteProduct()` |
| **When to use** | Inject this interface (not the impl) into the controller. Enables mocking in tests |

### `StockService.java`

**Path:** `src/main/java/com/rutika/inventory/service/interfaces/StockService.java`

| | |
|---|---|
| **What it does** | Interface defining the stock movement service contract |
| **Methods** | `addStock()`, `removeStock()` |
| **When to use** | Same as `ProductService` — inject into `StockController` |

---

## service/impl/

### `ProductServiceImpl.java`

**Path:** `src/main/java/com/rutika/inventory/service/impl/ProductServiceImpl.java`

| | |
|---|---|
| **What it does** | Full implementation of product CRUD. Orchestrates validation, persistence, and mapping |
| **Logic** | `createProduct` — maps request to entity, saves, maps back to response; `getProductById` — fetches or throws 404; `getAllProducts` — paginated query; `updateProduct` — merges and saves; `deleteProduct` — soft delete (sets status to `INACTIVE`) |
| **When to use** | Automatically injected via the `ProductService` interface. Contains all product business logic |
| **Key detail** | Uses `@Transactional` on write operations |

### `StockServiceImpl.java`

**Path:** `src/main/java/com/rutika/inventory/service/impl/StockServiceImpl.java`

| | |
|---|---|
| **What it does** | Implements stock-in and stock-out operations, including updating product quantities |
| **Logic** | `addStock` — looks up product, creates `StockIn` record, increments `product.stockQuantity`; `removeStock` — looks up product, creates `StockOut` record, decrements `product.stockQuantity` |
| **When to use** | Automatically injected via `StockService` interface |
| **Key detail** | Both methods are `@Transactional` to ensure stock movement and quantity update happen atomically |

---

## util/

### `DateTimeUtil.java`

**Path:** `src/main/java/com/rutika/inventory/util/DateTimeUtil.java`

| | |
|---|---|
| **What it does** | Static helper for UTC date/time formatting and parsing |
| **Methods** | `toUtcString(Instant)` — formats to `"2026-06-26T10:30:00Z"`; `toInstant(String)` — parses back; `nowUtc()` — shorthand for `Instant.now()` |
| **When to use** | Anywhere you need consistent UTC string representation of timestamps |

### `UuidUtil.java`

**Path:** `src/main/java/com/rutika/inventory/util/UuidUtil.java`

| | |
|---|---|
| **What it does** | Static helper for UUID generation and validation |
| **Methods** | `generateId()` — creates a random UUID string; `isValidUuid(String)` — checks if a string is a valid UUID |
| **When to use** | For manual ID generation outside entities, or for validating incoming UUID strings |

---

## validator/

### `ProductValidator.java`

**Path:** `src/main/java/com/rutika/inventory/validator/ProductValidator.java`

| | |
|---|---|
| **What it does** | Business validation rules for product operations that go beyond simple annotation checks |
| **Methods** | `validateCreate(ProductRequest)` — checks SKU uniqueness; `validateUpdate(String id, ProductRequest)` — checks SKU uniqueness excluding the current product |
| **When to use** | Call from `ProductServiceImpl` before persisting. Keeps validation logic out of services |
| **Error format** | Throws `ValidationException` with a `Map<String, String>` of field → error message |

### `StockValidator.java`

**Path:** `src/main/java/com/rutika/inventory/validator/StockValidator.java`

| | |
|---|---|
| **What it does** | Validates stock-out operations — ensures sufficient quantity exists |
| **Methods** | `validateStockOut(StockOutRequest)` — checks if `product.stockQuantity >= request.quantity` |
| **When to use** | Call from `StockServiceImpl.removeStock()` before decrementing |
| **Error format** | Throws `ValidationException` with a descriptive message including available vs requested quantities |

---

## Resources

### `application.yml`

**Path:** `src/main/resources/application.yml`

| | |
|---|---|
| **What it does** | Central configuration for server port, MySQL connection, JPA/Hibernate settings, Jackson serialization, and API docs |
| **When to edit** | Change database credentials, enable/disable SQL logging, configure connection pool, adjust timezone |

---

## Quick Reference: Which File for Which Job

| I want to... | Open this file |
|---|---|
| Start the application | `InventoryBackendApplication.java` |
| Add a new REST endpoint | A controller in `controller/` |
| Define what data the frontend sends me | A request DTO in `dto/request/` |
| Define what data I send to the frontend | A response DTO in `dto/response/` |
| Map a database table | An entity in `entity/` |
| Query the database | A repository in `repository/` |
| Write business logic | A service impl in `service/impl/` |
| Convert DTO to Entity or back | A mapper in `mapper/` |
| Validate complex business rules | A validator in `validator/` |
| Handle an error or exception | The handler in `exception/GlobalExceptionHandler.java` |
| Define a new error type | A new class in `exception/` |
| Add a reusable helper | A utility in `util/` |
| Change an API path | `constants/ApiConstants.java` |
| Change a response message | `constants/MessageConstants.java` |
| Configure CORS or JSON | A config class in `config/` |
| Wrap a response consistently | `response/ApiResponse.java` |
| Wrap a paginated response | `response/PageResponse.java` |
| Change DB connection or JPA settings | `resources/application.yml` |

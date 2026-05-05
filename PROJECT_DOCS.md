# Project Documentation

## Architecture Overview
The application uses a clean architecture pattern:
- `servlet` layer: HTTP controllers that parse requests and return JSON responses.
- `service` layer: business logic and authorization handling.
- `repository` layer: JDBC persistence for MySQL.
- `util` layer: JSON serialization, JWT generation, password hashing, response helpers.
- `config` layer: shared application configuration stored in `ServletContext`.

## Shared Configuration
`AppConfigListener` loads `application.properties` at application startup. It creates:
- `HikariDataSource` for MySQL access.
- `JedisPool` for Redis caching.
- JWT secret and expiration values.

These objects are stored in `ServletContext` and reused by servlets and repositories.

## Authentication & Authorization
- JWT tokens are issued by `JwtUtil` when users log in.
- Tokens contain claims: `userId`, `username`, `role`.
- `AuthFilter` validates tokens on protected routes and adds user context to requests.
- Role checks are applied in servlets via helper methods.
- Logout revokes tokens by persisting them in the `auth_tokens` table.

## Caching
- Product list responses are cached in Redis by `ProductService`.
- Cache invalidates when products are created, updated, or deleted.
- Redis is also used by `PaymentRateLimiter` to prevent repeated payment calls.

## Rate Limiting for Payments
- `PaymentServlet` checks Redis for a payment lock key before processing.
- Duplicate or rapid repeated payment attempts for the same order are blocked.
- Payment status is tracked in the `payments` table.

## Data Flow for a User Request
1. HTTP request reaches the servlet mapped to `/api/...`.
2. `AuthFilter` validates JWT and loads user data.
3. MVC servlet parses request body and delegates to service.
4. Service enforces authorization and business rules.
5. Repository executes database queries.
6. Response is serialized to JSON and returned.

## Important Classes
- `AppConfigListener`: initializes DB and Redis.
- `AuthFilter`: protects endpoints and enforces JWT auth.
- `JwtUtil`: signs and verifies tokens.
- `JsonUtil`: serializes/deserializes payloads.
- `ProductService`, `UserService`, `CartService`, `OrderService`, `PaymentService`: business logic.
- `ProductRepository`, `UserRepository`, `CartRepository`, `OrderRepository`, `PaymentRepository`: database access.

## Endpoints
- `POST /api/auth/register`
- `POST /api/auth/login`
- `POST /api/auth/logout`
- `GET /api/products`
- `GET /api/products/{id}`
- `POST /api/products`
- `PUT /api/products/{id}`
- `DELETE /api/products/{id}`
- `POST /api/cart/add`
- `GET /api/cart`
- `POST /api/orders`
- `GET /api/orders`
- `POST /api/payments`
- `GET /api/payments`
- `GET /api/categories`

## Deployment
- Build the WAR and deploy to a servlet container.
- Ensure Redis and MySQL are running before app startup.
- Set correct values in `application.properties`.

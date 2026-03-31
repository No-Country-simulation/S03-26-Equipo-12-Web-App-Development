# Testimonial CMS — Backend

**Java 21 · Spring Boot 4.0.2 · PostgreSQL · Multi-service**

---

## Requisitos

- Java 21
- Maven 3.9+
- Docker + Docker Compose

---

## Setup local (rápido)

```bash
# 1. Copiá las variables de entorno
cp .env.example .env
# Editá .env con tus claves reales (Cloudinary, YouTube, JWT)

# 2. Levantá la infra (postgres, redis, rabbitmq)
docker-compose up -d postgres redis rabbitmq

# 3. Compilá el proyecto completo desde la raíz
mvn clean install -DskipTests

# 4. Corré un servicio individualmente (ej: auth)
cd services/auth-service
mvn spring-boot:run
```

---

## Setup completo con Docker

```bash
cp .env.example .env
# Editá .env

docker-compose up --build
```

---

## Puertos

| Servicio            | Puerto | Swagger UI                              |
|---------------------|--------|-----------------------------------------|
| API Gateway         | 8080   | —                                       |
| Auth Service        | 8081   | http://localhost:8081/swagger-ui.html   |
| Testimonial Service | 8082   | http://localhost:8082/swagger-ui.html   |
| Media Service       | 8083   | http://localhost:8083/swagger-ui.html   |
| Analytics Service   | 8085   | http://localhost:8085/swagger-ui.html   |
| Embed Service       | 8086   | http://localhost:8086/swagger-ui.html   |
| PostgreSQL          | 5432   | —                                       |
| Redis               | 6379   | —                                       |
| RabbitMQ UI         | 15672  | http://localhost:15672 (guest/guest)    |

---

## Usuario admin por defecto

```
Email:    admin@testimonialcms.com
Password: Admin1234!
```

> ⚠️ Cambiar en producción via migración SQL.

---

## Estructura

```
backend/
├── pom.xml                   ← Parent POM
├── docker-compose.yml
├── .env.example
├── shared/                   ← DTOs, Enums, Exceptions, JwtUtil
├── api-gateway/              ← Spring Cloud Gateway (puerto 8080)
└── services/
    ├── auth-service/         ← JWT, usuarios, roles    (8081)
    ├── testimonial-service/  ← CRUD + moderación       (8082)
    ├── media-service/        ← Cloudinary + YouTube    (8083)
    ├── analytics-service/    ← Eventos + métricas      (8085)
    └── embed-service/        ← API pública + widget JS (8086)
```

---

## Flujo de autenticación

```
Frontend → POST /api/auth/login
         ← { accessToken, refreshToken }

Frontend → GET /api/testimonials
           Header: Authorization: Bearer <accessToken>

Gateway  → Valida JWT → propaga X-User-Id, X-User-Role
Servicio → Lee headers, no valida JWT directamente
```

---

## Variables de entorno requeridas

| Variable              | Descripción                        |
|-----------------------|------------------------------------|
| `JWT_SECRET`          | Clave secreta JWT (min 256 bits)   |
| `DB_USER`             | Usuario PostgreSQL                 |
| `DB_PASSWORD`         | Contraseña PostgreSQL              |
| `CLOUDINARY_CLOUD_NAME` | Cloud name de Cloudinary         |
| `CLOUDINARY_API_KEY`  | API Key de Cloudinary              |
| `CLOUDINARY_API_SECRET` | API Secret de Cloudinary         |
| `YOUTUBE_API_KEY`     | API Key de YouTube Data v3         |

# Spend Count Backend

Backend para una aplicación web de **gestión financiera personal**. Permite administrar usuarios, roles, permisos, ingresos, egresos, deudas, metas financieras y solicitudes a un **coach financiero con IA** basado en OpenAI.

El proyecto está desarrollado con **Java 17**, **Spring Boot 3.3.4**, **Gradle**, **Spring Data JPA**, **MySQL**, **Swagger/OpenAPI**, **Log4j2**, **Jacoco** y el SDK oficial de **OpenAI para Java**.

---

## Tabla de contenido

- [Descripción general](#descripción-general)
- [Funcionalidades principales](#funcionalidades-principales)
- [Arquitectura del proyecto](#arquitectura-del-proyecto)
- [Tecnologías utilizadas](#tecnologías-utilizadas)
- [Requisitos previos](#requisitos-previos)
- [Configuración local](#configuración-local)
- [Configuración de OpenAI](#configuración-de-openai)
- [Base de datos](#base-de-datos)
- [Ejecución local](#ejecución-local)
- [Swagger / OpenAPI](#swagger--openapi)
- [Servicios REST disponibles](#servicios-rest-disponibles)
- [Ejemplos de consumo](#ejemplos-de-consumo)
- [Pruebas y cobertura](#pruebas-y-cobertura)
- [SonarQube / SonarLint](#sonarqube--sonarlint)
- [Docker](#docker)
- [Buenas prácticas de seguridad](#buenas-prácticas-de-seguridad)
- [Problemas comunes](#problemas-comunes)
- [Autor](#autor)

---

## Descripción general

**Spend Count Backend** es el backend de una solución de gestión financiera personal orientada a registrar, consultar y analizar información económica de los usuarios.

El sistema permite registrar movimientos financieros como **ingresos** y **egresos**, manejar **deudas**, definir **metas financieras** y solicitar recomendaciones personalizadas a un **coach financiero IA**, el cual analiza el comportamiento financiero del usuario y genera consejos orientados al cumplimiento de una meta.

La aplicación está pensada como parte de un proyecto académico de gestión financiera personal con inteligencia artificial, donde el usuario puede visualizar y analizar su comportamiento económico a partir de datos históricos.

---

## Funcionalidades principales

### Gestión de seguridad funcional

- Gestión de usuarios.
- Gestión de roles.
- Gestión de permisos.
- Asociación de roles y permisos.
- Login básico.
- Cifrado de contraseñas mediante `spring-security-crypto`.

### Gestión financiera

- Registro de ingresos.
- Registro de egresos.
- Registro de salario como **ingreso recurrente**.
- Registro de gastos recurrentes.
- Consulta de movimientos financieros por usuario.
- Consulta de movimientos por tipo: `INCOME` o `EXPENSE`.
- Consulta de movimientos recurrentes.

### Gestión de deudas

- Registro de deudas por usuario.
- Manejo de valor inicial de la deuda.
- Manejo de saldo pendiente.
- Consulta de deudas por usuario.
- Consulta de deudas por estado: `ACTIVE`, `PAID`, `CANCELLED`.
- Actualización y eliminación de deudas.

### Gestión de metas financieras

- Registro de metas financieras.
- Actualización de metas.
- Consulta por usuario.
- Consulta por estado: `ACTIVE`, `COMPLETED`, `CANCELLED`.
- Eliminación de metas financieras.

### Coach financiero IA

- Solicitud de recomendaciones financieras usando OpenAI.
- Construcción de contexto financiero con:
    - ingresos;
    - egresos;
    - deudas activas;
    - movimientos recurrentes;
    - salario como ingreso recurrente;
    - meta financiera asociada;
    - historial de solicitudes anteriores relacionadas con la misma meta.
- Persistencia de cada pregunta, contexto enviado y respuesta generada.
- Consulta del histórico de solicitudes al coach IA por usuario.

---

## Arquitectura del proyecto

El proyecto sigue una arquitectura por capas:

```text
controller          -> Exposición de servicios REST
service             -> Contratos de lógica de negocio
service/implementation -> Implementación de lógica de negocio
repository          -> Acceso a datos con Spring Data JPA
model/entity        -> Entidades JPA
model/dto           -> Objetos de transferencia de datos
model/enums         -> Enumeraciones de dominio
exception           -> Excepciones personalizadas
exception/handler   -> Manejo global de errores
utils               -> Utilidades, constantes y mapeadores
```

Estructura general:

```text
count-spent-backend
├── gradle/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/udistrital/spendcount/
│   │   │       ├── controller/
│   │   │       ├── exception/
│   │   │       ├── model/
│   │   │       │   ├── dto/
│   │   │       │   ├── entity/
│   │   │       │   └── enums/
│   │   │       ├── repository/
│   │   │       ├── service/
│   │   │       │   └── implementation/
│   │   │       ├── utils/
│   │   │       └── SpendCountApplication.java
│   │   └── resources/
│   │       └── application.yaml
│   └── test/
├── build.gradle
├── Dockerfile
├── gradlew
├── gradlew.bat
└── settings.gradle
```

---

## Tecnologías utilizadas

| Tecnología | Uso |
|---|---|
| Java 17 | Lenguaje principal |
| Spring Boot 3.3.4 | Framework backend |
| Gradle | Gestión de dependencias y build |
| Spring Web | Servicios REST |
| Spring Data JPA | Persistencia |
| Hibernate | ORM |
| MySQL | Base de datos local recomendada |
| MySQL Connector/J | Driver MySQL |
| Microsoft SQL Server JDBC | Driver adicional disponible |
| Lombok | Reducción de código repetitivo |
| Spring Validation | Validaciones |
| Spring Security Crypto | Cifrado de contraseñas |
| Log4j2 | Logging |
| Springdoc OpenAPI | Swagger |
| OpenAI Java SDK 4.11.0 | Integración con OpenAI |
| Jacoco | Cobertura de pruebas |
| JUnit Platform | Pruebas unitarias |
| Docker | Contenerización |

---

## Requisitos previos

Antes de ejecutar el proyecto se requiere:

- Java 17.
- Gradle o Gradle Wrapper.
- MySQL local.
- IntelliJ IDEA, Visual Studio Code o IDE equivalente.
- Cuenta de OpenAI Platform con billing activo para usar la API.
- Opcional: Docker.
- Opcional: SonarQube local o SonarLint en el IDE.

Validar Java:

```bash
java -version
```

Validar Gradle Wrapper en Windows:

```bash
gradlew.bat --version
```

Validar Gradle Wrapper en Linux/macOS:

```bash
./gradlew --version
```

---

## Configuración local

El archivo principal de configuración es:

```text
src/main/resources/application.yaml
```

Configuración recomendada para desarrollo local:

```yaml
spring:
  application:
    name: spendcount

  datasource:
    url: jdbc:mysql://${DB_HOST:localhost}:3306/${DB_NAME:spendcount}?useSSL=true&requireSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true
    username: ${DB_USER:root}
    password: ${DB_PASSWORD:}
    driver-class-name: com.mysql.cj.jdbc.Driver

  jpa:
    hibernate:
      ddl-auto: none
    show-sql: true
    properties:
      hibernate:
        dialect: org.hibernate.dialect.MySQLDialect
        format_sql: true

openai:
  api:
    key: ${OPENAI_API_KEY}
  model: ${OPENAI_MODEL:gpt-5.4-mini}

logging:
  level:
    org.hibernate.SQL: DEBUG
```

> Nota: no se recomienda dejar la API Key quemada en el archivo `application.yaml`.

Variables soportadas:

| Variable | Descripción | Valor por defecto |
|---|---|---|
| `DB_HOST` | Host de MySQL | `localhost` |
| `DB_NAME` | Nombre de la base de datos | `spendcount` |
| `DB_USER` | Usuario de base de datos | `root` |
| `DB_PASSWORD` | Contraseña de base de datos | vacío |
| `OPENAI_API_KEY` | API Key de OpenAI | requerido |
| `OPENAI_MODEL` | Modelo de OpenAI | `gpt-5.4-mini` |

---

## Configuración de OpenAI

El módulo de coach financiero usa OpenAI mediante el SDK oficial:

```gradle
implementation 'com.openai:openai-java:4.11.0'
```

La API Key debe obtenerse desde OpenAI Platform, en la sección **API Keys**.

### Configurar API Key en Windows PowerShell

```powershell
setx OPENAI_API_KEY "TU_API_KEY"
setx OPENAI_MODEL "gpt-5.4-mini"
```

Después de ejecutar `setx`, se debe cerrar y abrir nuevamente IntelliJ o la terminal para que tome las variables.

### Configurar API Key en Linux/macOS

```bash
export OPENAI_API_KEY="TU_API_KEY"
export OPENAI_MODEL="gpt-5.4-mini"
```

### Importante sobre ChatGPT Plus

La suscripción de **ChatGPT Plus** no cubre automáticamente el uso de la **API de OpenAI**. La API se factura por separado desde OpenAI Platform. Si aparece un error similar a:

```text
429: You exceeded your current quota, please check your plan and billing details.
```

se debe revisar billing, método de pago, presupuesto y límites del proyecto en OpenAI Platform.

---

## Base de datos

La base de datos local recomendada es MySQL.

Nombre sugerido:

```sql
CREATE DATABASE spendcount;
```

La aplicación usa `ddl-auto: none`, por lo tanto las tablas deben existir previamente en el esquema.

Tablas principales del modelo actual:

```text
users
roles
permissions
roles_permissions
financial_records
debts
financial_goals
ai_coach_requests
```

Relaciones principales:

- `users.role_code` -> `roles.role_code`
- `roles_permissions.role_code` -> `roles.role_code`
- `roles_permissions.permission_code` -> `permissions.permission_code`
- `financial_records.user_document_number` -> `users.document_number`
- `debts.user_document_number` -> `users.document_number`
- `financial_goals.user_document_number` -> `users.document_number`
- `ai_coach_requests.user_document_number` -> `users.document_number`
- `ai_coach_requests.financial_goal_id` -> `financial_goals.financial_goal_id`

---

## Ejecución local

### Opción 1: desde IntelliJ IDEA

1. Abrir el proyecto.
2. Verificar que el JDK configurado sea Java 17.
3. Configurar variables de entorno:
    - `DB_HOST`
    - `DB_NAME`
    - `DB_USER`
    - `DB_PASSWORD`
    - `OPENAI_API_KEY`
    - `OPENAI_MODEL`
4. Ejecutar la clase:

```text
com.udistrital.spendcount.SpendCountApplication
```

### Opción 2: desde terminal

En Windows:

```bash
gradlew.bat bootRun
```

En Linux/macOS:

```bash
./gradlew bootRun
```

La aplicación queda disponible en:

```text
http://localhost:8080
```

---

## Swagger / OpenAPI

Swagger UI local:

```text
http://localhost:8080/swagger-ui/index.html
```

Especificación OpenAPI JSON:

```text
http://localhost:8080/v3/api-docs
```

Si se configura un `context-path`, se debe anteponer al path de Swagger.

---

## Servicios REST disponibles

### Usuarios

| Método | Endpoint | Descripción |
|---|---|---|
| `POST` | `/users` | Crear usuario |
| `PUT` | `/users/{documentNumber}` | Actualizar usuario |
| `DELETE` | `/users/{documentNumber}` | Eliminar usuario |
| `GET` | `/users` | Consultar usuarios |
| `GET` | `/users/{documentNumber}` | Consultar usuario por documento |
| `POST` | `/users/login` | Login de usuario |

### Roles

| Método | Endpoint | Descripción |
|---|---|---|
| `POST` | `/roles` | Crear rol |
| `PUT` | `/roles/{roleCode}` | Actualizar rol |
| `DELETE` | `/roles/{roleCode}` | Eliminar rol |
| `GET` | `/roles` | Consultar roles |
| `GET` | `/roles/{roleCode}` | Consultar rol por código |
| `GET` | `/roles/{roleCode}/permissions` | Consultar permisos de un rol |

### Permisos

| Método | Endpoint | Descripción |
|---|---|---|
| `POST` | `/permissions` | Crear permiso |
| `PUT` | `/permissions/{permissionCode}` | Actualizar permiso |
| `DELETE` | `/permissions/{permissionCode}` | Eliminar permiso |
| `GET` | `/permissions` | Consultar permisos |
| `GET` | `/permissions/{permissionCode}` | Consultar permiso por código |

### Movimientos financieros

| Método | Endpoint | Descripción |
|---|---|---|
| `POST` | `/financial-records/incomes` | Registrar ingreso |
| `POST` | `/financial-records/expenses` | Registrar egreso |
| `PUT` | `/financial-records/{financialRecordId}` | Actualizar movimiento financiero |
| `DELETE` | `/financial-records/{financialRecordId}` | Eliminar movimiento financiero |
| `GET` | `/financial-records/{financialRecordId}` | Consultar movimiento por ID |
| `GET` | `/financial-records/users/{userDocumentNumber}` | Consultar movimientos por usuario |
| `GET` | `/financial-records/users/{userDocumentNumber}/type/{recordType}` | Consultar movimientos por tipo |
| `GET` | `/financial-records/users/{userDocumentNumber}/recurring` | Consultar movimientos recurrentes |

Tipos permitidos para `recordType`:

```text
INCOME
EXPENSE
```

Periodicidades permitidas:

```text
DAILY
WEEKLY
BIWEEKLY
MONTHLY
YEARLY
```

### Deudas

| Método | Endpoint | Descripción |
|---|---|---|
| `POST` | `/debts` | Registrar deuda |
| `PUT` | `/debts/{debtId}` | Actualizar deuda |
| `DELETE` | `/debts/{debtId}` | Eliminar deuda |
| `GET` | `/debts/{debtId}` | Consultar deuda por ID |
| `GET` | `/debts/users/{userDocumentNumber}` | Consultar deudas por usuario |
| `GET` | `/debts/users/{userDocumentNumber}/status/{status}` | Consultar deudas por estado |

Estados permitidos:

```text
ACTIVE
PAID
CANCELLED
```

### Metas financieras

| Método | Endpoint | Descripción |
|---|---|---|
| `POST` | `/financial-goals` | Crear meta financiera |
| `PUT` | `/financial-goals/{financialGoalId}` | Actualizar meta financiera |
| `DELETE` | `/financial-goals/{financialGoalId}` | Eliminar meta financiera |
| `GET` | `/financial-goals/{financialGoalId}` | Consultar meta por ID |
| `GET` | `/financial-goals/users/{userDocumentNumber}` | Consultar metas por usuario |
| `GET` | `/financial-goals/users/{userDocumentNumber}/status/{status}` | Consultar metas por estado |

Estados permitidos:

```text
ACTIVE
COMPLETED
CANCELLED
```

### Coach financiero IA

| Método | Endpoint | Descripción |
|---|---|---|
| `POST` | `/ai-coach/advice` | Solicitar consejo financiero IA |
| `GET` | `/ai-coach/{aiCoachRequestId}` | Consultar solicitud IA por ID |
| `GET` | `/ai-coach/users/{userDocumentNumber}/requests` | Consultar histórico de solicitudes IA por usuario |

---

## Ejemplos de consumo

### Crear ingreso recurrente de salario

```bash
curl --location 'http://localhost:8080/financial-records/incomes' \
--header 'Content-Type: application/json' \
--data '{
  "userDocumentNumber": "1019109757",
  "category": "Salario",
  "description": "Salario mensual empresa ABC",
  "amount": 3500000,
  "recordDate": "2026-05-23",
  "recurring": true,
  "periodicity": "MONTHLY"
}'
```

### Crear egreso recurrente

```bash
curl --location 'http://localhost:8080/financial-records/expenses' \
--header 'Content-Type: application/json' \
--data '{
  "userDocumentNumber": "1019109757",
  "category": "Arriendo",
  "description": "Pago mensual de arriendo",
  "amount": 1200000,
  "recordDate": "2026-05-01",
  "recurring": true,
  "periodicity": "MONTHLY"
}'
```

### Crear deuda

```bash
curl --location 'http://localhost:8080/debts' \
--header 'Content-Type: application/json' \
--data '{
  "userDocumentNumber": "1019109757",
  "creditor": "Tarjeta de crédito",
  "description": "Compra de computador",
  "initialAmount": 2500000,
  "startDate": "2026-05-01",
  "dueDate": "2026-11-01",
  "status": "ACTIVE"
}'
```

Si no se envía `pendingAmount`, el backend lo inicializa con el mismo valor de `initialAmount`.

### Crear meta financiera

```bash
curl --location 'http://localhost:8080/financial-goals' \
--header 'Content-Type: application/json' \
--data '{
  "userDocumentNumber": "1019109757",
  "name": "Comprar computador",
  "description": "Ahorrar para comprar un computador portátil para estudiar y trabajar",
  "targetAmount": 5000000,
  "currentAmount": 500000,
  "startDate": "2026-05-23",
  "targetDate": "2026-12-23",
  "status": "ACTIVE"
}'
```

### Solicitar consejo financiero IA

```bash
curl --location 'http://localhost:8080/ai-coach/advice' \
--header 'Content-Type: application/json' \
--data '{
  "userDocumentNumber": "1019109757",
  "financialGoalId": 1,
  "question": "¿Qué debo hacer para cumplir mi meta de ahorro?"
}'
```

### Consultar histórico de solicitudes IA

```bash
curl --location 'http://localhost:8080/ai-coach/users/1019109757/requests'
```

---

## Pruebas y cobertura

Ejecutar pruebas:

En Windows:

```bash
gradlew.bat test
```

En Linux/macOS:

```bash
./gradlew test
```

Generar reporte de cobertura Jacoco:

```bash
gradlew.bat jacocoTestReport
```

o:

```bash
./gradlew jacocoTestReport
```

Reporte HTML local:

```text
build/reports/jacoco/test/html/index.html
```

Reporte XML para herramientas externas:

```text
build/reports/jacoco/test/jacocoTestReport.xml
```

---

## SonarQube / SonarLint

### SonarLint en IntelliJ IDEA

Para análisis local desde IntelliJ:

1. Instalar el plugin **SonarQube for IDE** o **SonarLint**.
2. Abrir el proyecto.
3. Ejecutar análisis sobre archivos o proyecto.
4. Corregir issues como:
    - orden de modificadores (`public static final`);
    - uso correcto de logs con `{}`;
    - eliminación de duplicidad;
    - constantes para literales repetidos.

### SonarQube local

Se puede levantar SonarQube local con Docker:

```bash
docker run -d --name sonarqube-local -p 9000:9000 sonarqube:community
```

URL local de SonarQube:

```text
http://localhost:9000
```

Credenciales iniciales habituales:

```text
Usuario: admin
Contraseña: admin
```

Al ingresar por primera vez, SonarQube solicitará cambiar la contraseña.

### Link local del dashboard

Si el proyecto se registra con la key `spendcount`, el dashboard local será:

```text
http://localhost:9000/dashboard?id=spendcount
```

### Análisis con Sonar Scanner

Instalar Sonar Scanner o usar la integración del IDE. Un ejemplo de ejecución sería:

```bash
sonar-scanner \
  -Dsonar.projectKey=spendcount \
  -Dsonar.projectName="Spend Count Backend" \
  -Dsonar.sources=src/main/java \
  -Dsonar.tests=src/test/java \
  -Dsonar.java.binaries=build/classes/java/main \
  -Dsonar.coverage.jacoco.xmlReportPaths=build/reports/jacoco/test/jacocoTestReport.xml \
  -Dsonar.host.url=http://localhost:9000 \
  -Dsonar.token=TU_TOKEN_DE_SONAR
```

Antes de ejecutar el análisis, generar el build y la cobertura:

```bash
gradlew.bat clean build jacocoTestReport
```

o:

```bash
./gradlew clean build jacocoTestReport
```

---

## Docker

El proyecto incluye un `Dockerfile` con dos etapas:

1. Compilación con Gradle y JDK 17.
2. Ejecución con Eclipse Temurin JRE 17.

Construir imagen:

```bash
docker build -t spendcount-backend .
```

Ejecutar contenedor:

```bash
docker run -p 8080:8080 \
  -e DB_HOST=host.docker.internal \
  -e DB_NAME=spendcount \
  -e DB_USER=root \
  -e DB_PASSWORD=tu_password \
  -e OPENAI_API_KEY=tu_api_key \
  -e OPENAI_MODEL=gpt-5.4-mini \
  spendcount-backend
```

En Linux puede ser necesario reemplazar `host.docker.internal` por la IP real del host o ejecutar MySQL también en Docker.

---

## Buenas prácticas de seguridad

No subir al repositorio:

```text
API Keys
.env
application-local.yaml
application-local.properties
build/
.gradle/
.idea/
```

La API Key de OpenAI debe manejarse mediante variable de entorno:

```yaml
openai:
  api:
    key: ${OPENAI_API_KEY}
  model: ${OPENAI_MODEL:gpt-5.4-mini}
```

Si una API Key fue compartida accidentalmente, se debe:

1. Revocar inmediatamente en OpenAI Platform.
2. Crear una nueva.
3. Eliminar la key del código.
4. Revisar el historial de Git si fue commiteada.
5. Reescribir historial si el repositorio fue publicado.

---

## Problemas comunes

### Error 429 de OpenAI

Mensaje típico:

```text
429: You exceeded your current quota, please check your plan and billing details.
```

Causas posibles:

- No hay billing activo en OpenAI Platform.
- No hay método de pago.
- El crédito disponible es `0.00`.
- Se superó el límite mensual.
- La API Key pertenece a un proyecto sin presupuesto.
- ChatGPT Plus está activo, pero la API no tiene billing configurado.

Solución:

- Revisar billing en OpenAI Platform.
- Agregar método de pago.
- Configurar presupuesto mensual.
- Validar que la API Key pertenezca al proyecto correcto.

### Swagger no carga

Validar:

```text
http://localhost:8080/swagger-ui/index.html
```

Si no carga:

- revisar que la aplicación esté arriba;
- revisar puerto configurado;
- validar dependencia `springdoc-openapi-starter-webmvc-ui`;
- revisar logs de arranque.

### Error de conexión MySQL

Validar:

- MySQL iniciado.
- Base de datos `spendcount` creada.
- Usuario y contraseña correctos.
- Variables `DB_HOST`, `DB_NAME`, `DB_USER`, `DB_PASSWORD`.
- Puerto `3306` disponible.

### Error con métodos de Repository

Si se usa una relación `ManyToOne` hacia `User`, los métodos deben navegar la propiedad correctamente:

```java
findByUser_DocumentNumber(...)
findByUser_DocumentNumberAndStatus(...)
findByUser_DocumentNumberAndRecordType(...)
```

Esto evita errores como:

```text
No property 'userDocumentNumber' found for type ...
```

### Problemas con Gradle Wrapper en Linux

Dar permisos de ejecución:

```bash
chmod +x gradlew
```

---

## Flujo recomendado para probar el coach IA

1. Crear usuario.
2. Crear rol y permisos si aplica.
3. Crear ingreso de salario mensual.
4. Crear egreso recurrente.
5. Crear deuda activa.
6. Crear meta financiera.
7. Consumir:

```text
POST /ai-coach/advice
```

8. Consultar histórico:

```text
GET /ai-coach/users/{userDocumentNumber}/requests
```

El coach IA tendrá en cuenta el comportamiento financiero actual y las solicitudes anteriores relacionadas con la misma meta financiera.

---

## Notas de dominio

- El salario no se modela como una entidad independiente.
- El salario se registra como un `INCOME` con `recurring = true`.
- Las deudas se manejan en una tabla independiente porque tienen ciclo de vida propio.
- Las metas financieras se asocian a usuarios.
- Las solicitudes al coach IA se asocian a usuario y opcionalmente a una meta financiera.
- El historial del coach IA permite continuidad contextual por meta financiera.

---

## Autor

Proyecto desarrollado por:

```text
Diego Muñoz
Andrés Ramirez
Fabian Garcia
Universidad Distrital Francisco José de Caldas
Especialización / Ingeniería de Software I
```

---

## Estado actual

El proyecto cuenta con una base funcional para:

- gestión de usuarios, roles y permisos;
- gestión de ingresos y egresos;
- gestión de deudas;
- gestión de metas financieras;
- integración con OpenAI para coach financiero IA;
- persistencia de solicitudes y respuestas IA;
- documentación Swagger;
- cobertura con Jacoco;
- análisis local con SonarQube/SonarLint;
- ejecución local y Docker.

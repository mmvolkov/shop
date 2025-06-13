# Полноценный backend для интернет-магазина на Spring Boot с JWT авторизацией и всеми требуемыми функциями

## Структура проекта

```
shop/
├── src/main/java/com/shop/
│   ├── ShopApplication.java
│   ├── config/
│   │   └── SecurityConfig.java
│   ├── controller/
│   │   ├── AuthController.java
│   │   ├── CategoryController.java
│   │   ├── ItemController.java 
│   │   └── ShipmentController.java 
│   ├── dto/ 
│   │   ├── AuthRequest.java 
│   │   ├── AuthResponse.java 
│   │   ├── RegisterRequest.java 
│   │   ├── CategoryDto.java 
│   │   ├── ItemDto.java 
│   │   ├── ItemUpdateDto.java 
│   │   └── ShipmentDto.java 
│   ├── entity/ 
│   │   ├── Category.java 
│   │   ├── Item.java 
│   │   └── User.java 
│   ├── repository/ 
│   │   ├── CategoryRepository.java 
│   │   ├── ItemRepository.java 
│   │   └── UserRepository.java 
│   ├── security/ 
│   │   ├── JwtAuthenticationFilter.java 
│   │   └── JwtUtil.java 
│   └── service/ 
│       └── CustomUserDetailsService.java 
└── src/main/resources/ 
    └── application.properties
````



# Shop Backend API - Полное описание проекта

## Оглавление

1. Обзор проекта
2. Архитектура приложения
3. Технологический стек
4. Структура базы данных
5. API Endpoints
6. Система безопасности
7. Установка и запуск
8. Тестирование
9. Примеры использования

## Обзор проекта

### Назначение

Backend API для интернет-магазина, предоставляющий полный функционал для управления товарами, категориями и процессами отгрузки.

### Ключевые возможности

- **Управление пользователями**: регистрация, авторизация с JWT
- **Управление категориями**: CRUD операции для категорий товаров
- **Управление товарами**: создание, просмотр, обновление количества и цены
- **Отгрузка товаров**: защищенный API для процессов отгрузки
- **Двухуровневая авторизация**: JWT для обычных операций, API Key для критических

### Целевая аудитория

- Владельцы интернет-магазинов
- Разработчики frontend приложений
- Системы интеграции (ERP, CRM)
- Мобильные приложения

## Архитектура приложения

### Паттерн проектирования

Проект построен на основе **многослойной архитектуры** (Layered Architecture):

```
┌─────────────────────────────────────────┐
│         Presentation Layer              │
│         (REST Controllers)              │
├─────────────────────────────────────────┤
│         Business Logic Layer            │
│            (Services)                   │
├─────────────────────────────────────────┤
│         Data Access Layer               │
│          (Repositories)                 │
├─────────────────────────────────────────┤
│           Database Layer                │
│           (PostgreSQL)                  │
└─────────────────────────────────────────┘
```

### Структура пакетов

```
com.shop/
├── config/          # Конфигурация Spring Security
├── controller/      # REST контроллеры
├── dto/            # Data Transfer Objects
├── entity/         # JPA сущности
├── repository/     # Spring Data репозитории
├── security/       # JWT и безопасность
└── service/        # Бизнес-логика
```

## Технологический стек

### Backend

- **Java 21** - современная LTS версия Java
- **Spring Boot 3.4.6** - фреймворк для быстрой разработки
- **Spring Security** - аутентификация и авторизация
- **Spring Data JPA** - работа с базой данных
- **Hibernate** - ORM фреймворк

### База данных

- **PostgreSQL** - надежная реляционная СУБД
- **JPA/Hibernate** - объектно-реляционное отображение

### Безопасность

- **JWT (JSON Web Tokens)** - stateless аутентификация
- **BCrypt** - хеширование паролей
- **API Keys** - дополнительная защита критических операций

### Инструменты разработки

- **Maven** - управление зависимостями
- **Lombok** - сокращение boilerplate кода
- **IntelliJ IDEA** - рекомендуемая IDE

## Структура базы данных

### Схема БД

```sql
-- Таблица пользователей
CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(255) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    api_key VARCHAR(255) UNIQUE
);

-- Таблица ролей пользователей
CREATE TABLE user_roles (
    user_id BIGINT REFERENCES users(id),
    roles VARCHAR(255)
);

-- Таблица категорий
CREATE TABLE categories (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) UNIQUE NOT NULL,
    description TEXT
);

-- Таблица товаров
CREATE TABLE items (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    price DECIMAL(10,2) NOT NULL,
    quantity INTEGER NOT NULL DEFAULT 0,
    category_id BIGINT NOT NULL REFERENCES categories(id)
);
```

### ER-диаграмма

```
┌─────────────┐       ┌─────────────┐
│    User     │       │  Category   │
├─────────────┤       ├─────────────┤
│ id          │       │ id          │
│ username    │       │ name        │
│ password    │       │ description │
│ api_key     │       └──────┬──────┘
│ roles       │              │ 1
└─────────────┘              │
                             │ *
                      ┌──────┴──────┐
                      │    Item     │
                      ├─────────────┤
                      │ id          │
                      │ name        │
                      │ description │
                      │ price       │
                      │ quantity    │
                      │ category_id │
                      └─────────────┘
```

## API Endpoints

### 1. Аутентификация

#### Регистрация

```http
POST /api/auth/register
Content-Type: application/json

{
    "username": "user1",
    "password": "password123"
}

Response: 200 OK
"User registered successfully"
```

#### Вход

```http
POST /api/auth/login
Content-Type: application/json

{
    "username": "user1",
    "password": "password123"
}

Response: 200 OK
{
    "token": "eyJhbGciOiJIUzI1NiJ9...",
    "apiKey": "550e8400-e29b-41d4-a716-446655440000"
}
```

### 2. Категории

#### Создание категории

```http
POST /api/categories
Authorization: Bearer {JWT_TOKEN}
Content-Type: application/json

{
    "name": "Электроника",
    "description": "Электронные устройства и гаджеты"
}

Response: 200 OK
{
    "id": 1,
    "name": "Электроника",
    "description": "Электронные устройства и гаджеты"
}
```

#### Получение всех категорий

```http
GET /api/categories
Authorization: Bearer {JWT_TOKEN}

Response: 200 OK
[
    {
        "id": 1,
        "name": "Электроника",
        "description": "Электронные устройства и гаджеты"
    }
]
```

### 3. Товары

#### Создание товара

```http
POST /api/items
Authorization: Bearer {JWT_TOKEN}
Content-Type: application/json

{
    "name": "Ноутбук ASUS ROG",
    "description": "Мощный игровой ноутбук",
    "price": 75000.00,
    "quantity": 10,
    "categoryId": 1
}

Response: 200 OK
{
    "id": 1,
    "name": "Ноутбук ASUS ROG",
    "description": "Мощный игровой ноутбук",
    "price": 75000.00,
    "quantity": 10,
    "category": {
        "id": 1,
        "name": "Электроника"
    }
}
```

#### Обновление товара

```http
PUT /api/items/{id}
Authorization: Bearer {JWT_TOKEN}
Content-Type: application/json

{
    "quantity": 5,      // добавить 5 единиц
    "price": 80000.00   // новая цена
}
```

### 4. Отгрузка

#### Отгрузка товара

```http
POST /api/shipment
X-API-KEY: {API_KEY}
Content-Type: application/json

{
    "itemId": 1,
    "quantity": 2
}

Response: 200 OK
"Shipment processed successfully"
```

## Система безопасности

### JWT Authentication Flow

```
┌──────────┐     ┌──────────┐     ┌───────────┐
│  Client  │────>│   Login  │────>│ Generate  │
│          │<────│ Endpoint │<────│   JWT     │
└──────────┘     └──────────┘     └───────────┘
     │                                    │
     │ Authorization: Bearer {JWT}        │
     ▼                                    │
┌──────────┐     ┌──────────┐     ┌───────────┐
│ Request  │────>│   JWT    │────>│ Validate  │
│ with JWT │     │  Filter  │     │   Token   │
└──────────┘     └──────────┘     └───────────┘
```

### Конфигурация безопасности

```java
// Открытые endpoints
.requestMatchers("/api/auth/**").permitAll()

// Защищенные JWT
.requestMatchers("/api/categories/**").authenticated()
.requestMatchers("/api/items/**").authenticated()

// Защищенные API Key
.requestMatchers("/api/shipment/**").authenticated()
```

### Хеширование паролей

- Алгоритм: **BCrypt**
- Сложность: 10 раундов (по умолчанию)
- Пример хеша: `$2a$10$N9qo8uLOickgx2ZMRZoMye...`

## Установка и запуск

### Требования

- Java 17+
- PostgreSQL 12+
- Maven 3.6+

### Шаги установки

1. **Клонирование репозитория**

```bash
git clone https://github.com/your-repo/shop-backend.git
cd shop-backend
```

2. **Создание базы данных**

```sql
CREATE DATABASE shop_db;
```

3. **Настройка application.properties**

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/shop_db
spring.datasource.username=postgres
spring.datasource.password=your_password
```

4. **Сборка проекта**

```bash
mvn clean install
```

5. **Запуск приложения**

```bash
mvn spring-boot:run
```

Приложение запустится на `http://localhost:8080`

## Тестирование

### 1. Использование Postman

- Импортируйте коллекцию `Shop-API-Collection.postman_collection.json`
- Выполняйте запросы в порядке нумерации
- Токены и ID сохраняются автоматически

### 2. Использование HTML тестовой страницы

- Откройте `shop-api-test.html` в браузере
- Интерактивный интерфейс для всех операций
- Автоматическая проверка статуса сервера

### 3. Использование cURL

```bash
# Регистрация
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{"username":"test","password":"test123"}'

# Вход
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"test","password":"test123"}'
```

## Примеры использования

### Сценарий 1: Добавление нового товара

```javascript
// 1. Авторизация
const loginResponse = await fetch('/api/auth/login', {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ username: 'admin', password: 'admin123' })
});
const { token } = await loginResponse.json();

// 2. Создание категории
const categoryResponse = await fetch('/api/categories', {
    method: 'POST',
    headers: {
        'Content-Type': 'application/json',
        'Authorization': `Bearer ${token}`
    },
    body: JSON.stringify({
        name: 'Смартфоны',
        description: 'Мобильные устройства'
    })
});
const category = await categoryResponse.json();

// 3. Создание товара
const itemResponse = await fetch('/api/items', {
    method: 'POST',
    headers: {
        'Content-Type': 'application/json',
        'Authorization': `Bearer ${token}`
    },
    body: JSON.stringify({
        name: 'iPhone 15 Pro',
        description: 'Флагманский смартфон Apple',
        price: 120000,
        quantity: 5,
        categoryId: category.id
    })
});
```

### Сценарий 2: Процесс отгрузки

```javascript
// Отгрузка с использованием API Key
const shipmentResponse = await fetch('/api/shipment', {
    method: 'POST',
    headers: {
        'Content-Type': 'application/json',
        'X-API-KEY': apiKey
    },
    body: JSON.stringify({
        itemId: 1,
        quantity: 2
    })
});
```

## Обработка ошибок

### Типы ошибок

```java
// 400 Bad Request
{
    "error": "Username already exists"
}

// 401 Unauthorized
{
    "error": "Invalid token"
}

// 404 Not Found
{
    "error": "Category not found"
}

// 500 Internal Server Error
{
    "error": "Internal server error"
}
```

## Дальнейшее развитие

### Планируемые улучшения

1. **Функциональность**
    
    - Поиск и фильтрация товаров
    - История отгрузок
    - Управление складами
    - Система скидок
2. **Техническое развитие**
    
    - Добавление Redis для кеширования
    - Внедрение Swagger/OpenAPI документации
    - Добавление unit и integration тестов
    - Контейнеризация с Docker
3. **Безопасность**
    
    - Refresh tokens
    - Rate limiting
    - Логирование действий
    - 2FA авторизация

### Масштабирование

- Микросервисная архитектура
- Использование Kafka для событий
- Elasticsearch для поиска
- Kubernetes для оркестрации

## Заключение

Проект представляет собой полноценный backend для интернет-магазина с современной архитектурой, надежной системой безопасности и готовностью к масштабированию. Код организован по принципам чистой архитектуры, что облегчает поддержку и развитие системы.



--------------

## API Endpoints

### Авторизация
- `POST /api/auth/register` - Регистрация пользователя
- `POST /api/auth/login` - Вход (возвращает JWT токен и API ключ)

### Категории
- `POST /api/categories` - Создание категории
- `GET /api/categories` - Получение всех категорий
- `GET /api/categories/{id}` - Получение категории по ID

### Товары
- `POST /api/items` - Создание товара
- `GET /api/items` - Получение всех товаров
- `GET /api/items/category/{categoryId}` - Получение товаров по категории
- `PUT /api/items/{id}` - Обновление количества или цены товара

### Отгрузка (защищенный endpoint)
- `POST /api/shipment` - Отгрузка товара (требует X-API-KEY в заголовке)

## Инструкция по запуску

1. Создать базу данных PostgreSQL:
```sql
CREATE DATABASE shop_db;
````

2. Запустить приложение:

```bash
mvn spring-boot:run
```

## Примеры использования API

### Регистрация:

```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{"username":"user1","password":"password123"}'
```

### Вход:

```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"user1","password":"password123"}'
```

### Создание категории:

```bash
curl -X POST http://localhost:8080/api/categories \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer {JWT_TOKEN}" \
  -d '{"name":"Электроника","description":"Электронные устройства"}'
```

### Создание товара:

```bash
curl -X POST http://localhost:8080/api/items \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer {JWT_TOKEN}" \
  -d '{"name":"Ноутбук","description":"Мощный ноутбук","price":50000,"quantity":10,"categoryId":1}'
```

### Отгрузка товара:

```bash
curl -X POST http://localhost:8080/api/shipment \
  -H "Content-Type: application/json" \
  -H "X-API-KEY: {API_KEY}" \
  -d '{"itemId":1,"quantity":2}'
```

```
Скрипт PowerShell создан. Для его использования:

1. **Сохраните скрипт** как `create-project.ps1`
    
2. **Откройте PowerShell** от имени администратора
    
3. **Разрешите выполнение скриптов** (если еще не разрешено):
    
    ```powershell
    Set-ExecutionPolicy -ExecutionPolicy RemoteSigned -Scope CurrentUser
    ```
    
4. **Перейдите в папку**, где хотите создать проект:
    
    ```powershell
    cd C:\YourProjectsFolder
    ```
    
5. **Запустите скрипт**:
    
    ```powershell
    .\create-project.ps1
    ```
    

Скрипт автоматически:

- Создаст всю структуру папок проекта
- Создаст все Java-файлы, конфигурационные файлы и pom.xml
- Все файлы будут в кодировке UTF-8 без BOM
- Выведет цветную структуру проекта в консоль
- Покажет инструкции по запуску

После выполнения скрипта проект будет готов к открытию в IntelliJ IDEA и запуску.


```

```
# PowerShell script to create the Spring Boot project
# Messages in English, encoding UTF-8 without BOM

$ErrorActionPreference = 'Stop'

$projectName = 'shop'
$groupId = 'com'
$packageName = "$groupId.$projectName"
$downloadUrl = "https://start.spring.io/starter.zip" +
  "?type=maven-project&language=java&bootVersion=3.4.6" +
  "&baseDir=$projectName" +
  "&groupId=$groupId" +
  "&artifactId=$projectName" +
  "&name=$projectName" +
  "&description=Demo%20Project%20Spring%20Boot%20application%20with%20database" +
  "&packageName=$packageName" +
  "&packaging=jar" +
  "&javaVersion=21" +
  "&dependencies=web,data-jpa,postgresql,lombok,security"

Write-Host "Downloading Spring Boot project template..."
Invoke-WebRequest -Uri $downloadUrl -OutFile "$projectName.zip"

Write-Host "Unpacking project..."
Expand-Archive -LiteralPath "$projectName.zip" -DestinationPath '.'

# Remove-Item "$projectName.zip"

$projectDir = Join-Path (Get-Location) $projectName

# $srcPath = Join-Path $projectDir "src\main\java\ru\exampledb\demodatabase"
# $resourcePath = Join-Path $projectDir "src\main\resources"
# $entities = Join-Path $srcPath 'model'
# $repositories = Join-Path $srcPath 'repository'
# $controllers = Join-Path $srcPath 'controller'
# $security = Join-Path $srcPath 'security'

# New-Item -ItemType Directory -Force -Path $entities, $repositories, $controllers, $security | Out-Null
# New-Item -ItemType Directory -Force -Path $entities, $repositories, $controllers, $security


$utf8NoBom = New-Object System.Text.UTF8Encoding $false


# Создание структуры проекта для Spring Boot Shop Backend
# Кодировка: UTF-8 без BOM

# $projectRoot = Join-Path (Get-Location) $projectName
# $projectRoot = Join-Path (Get-Location) "shop-backend"
$projectRoot = Join-Path (Get-Location) $projectName

Write-Host "Creating project directory..."

# Создание основной структуры папок
$folders = @(
    "$projectRoot\src\main\java\com\shop",
    "$projectRoot\src\main\java\com\shop\config",
    "$projectRoot\src\main\java\com\shop\controller",
    "$projectRoot\src\main\java\com\shop\dto",
    "$projectRoot\src\main\java\com\shop\entity",
    "$projectRoot\src\main\java\com\shop\repository",
    "$projectRoot\src\main\java\com\shop\security",
    "$projectRoot\src\main\java\com\shop\service",
    "$projectRoot\src\main\resources",
    "$projectRoot\src\test\java\com\shop"
)

foreach ($folder in $folders) {
    New-Item -ItemType Directory -Path $folder -Force | Out-Null
	Write-Host "Created $folder"
}

Write-Host "Folders created" -ForegroundColor Green

# Функция для создания файлов в UTF-8 без BOM
function Create-UTF8NoBOM {
    param(
        [string]$Path,
        [string]$Content
    )
    $utf8NoBom = New-Object System.Text.UTF8Encoding $false
    [System.IO.File]::WriteAllText($Path, $Content, $utf8NoBom)
}

Write-Host "Project files structure creating" -ForegroundColor Green

# pom.xml
$pomContent = @'
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 
         http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>
    
    <parent>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-parent</artifactId>
        <version>3.2.0</version>
    </parent>
    
    <groupId>com.shop</groupId>
    <artifactId>shop-backend</artifactId>
    <version>1.0</version>
    
    <properties>
        <java.version>21</java.version>
    </properties>
    
    <dependencies>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>
        
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-data-jpa</artifactId>
        </dependency>
        
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-security</artifactId>
        </dependency>
        
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-validation</artifactId>
        </dependency>
        
        <dependency>
            <groupId>org.postgresql</groupId>
            <artifactId>postgresql</artifactId>
            <scope>runtime</scope>
        </dependency>
        
        <dependency>
            <groupId>io.jsonwebtoken</groupId>
            <artifactId>jjwt-api</artifactId>
            <version>0.11.5</version>
        </dependency>
        
        <dependency>
            <groupId>io.jsonwebtoken</groupId>
            <artifactId>jjwt-impl</artifactId>
            <version>0.11.5</version>
            <scope>runtime</scope>
        </dependency>
        
        <dependency>
            <groupId>io.jsonwebtoken</groupId>
            <artifactId>jjwt-jackson</artifactId>
            <version>0.11.5</version>
            <scope>runtime</scope>
        </dependency>
        
        <dependency>
            <groupId>org.projectlombok</groupId>
            <artifactId>lombok</artifactId>
            <optional>true</optional>
        </dependency>
    </dependencies>
    
    <build>
        <plugins>
            <plugin>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-maven-plugin</artifactId>
            </plugin>
        </plugins>
    </build>
</project>
'@
Create-UTF8NoBOM -Path "$projectRoot\pom.xml" -Content $pomContent

# application.properties
$appPropertiesContent = @'
spring.datasource.url=jdbc:postgresql://localhost:5432/shop_db
spring.datasource.username=postgres
spring.datasource.password=postgres
spring.datasource.driver-class-name=org.postgresql.Driver

spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.PostgreSQLDialect

jwt.secret=mySecretKey
jwt.expiration=86400000
'@
Create-UTF8NoBOM -Path "$projectRoot\src\main\resources\application.properties" -Content $appPropertiesContent

# ShopApplication.java
$shopAppContent = @'
package com.shop;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ShopApplication {
    public static void main(String[] args) {
        SpringApplication.run(ShopApplication.class, args);
    }
}
'@
Create-UTF8NoBOM -Path "$projectRoot\src\main\java\com\shop\ShopApplication.java" -Content $shopAppContent

# Entity - Category.java
$categoryContent = @'
package com.shop.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.util.List;

@Entity
@Table(name = "categories")
@Data
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, unique = true)
    private String name;
    
    private String description;
    
    @OneToMany(mappedBy = "category", cascade = CascadeType.ALL)
    private List<Item> items;
}
'@
Create-UTF8NoBOM -Path "$projectRoot\src\main\java\com\shop\entity\Category.java" -Content $categoryContent

# Entity - Item.java
$itemContent = @'
package com.shop.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;

@Entity
@Table(name = "items")
@Data
public class Item {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String name;
    
    private String description;
    
    @Column(nullable = false)
    private BigDecimal price;
    
    @Column(nullable = false)
    private Integer quantity = 0;
    
    @ManyToOne
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;
}
'@
Create-UTF8NoBOM -Path "$projectRoot\src\main\java\com\shop\entity\Item.java" -Content $itemContent

# Entity - User.java
$userContent = @'
package com.shop.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.util.Set;

@Entity
@Table(name = "users")
@Data
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, unique = true)
    private String username;
    
    @Column(nullable = false)
    private String password;
    
    @Column(unique = true)
    private String apiKey;
    
    @ElementCollection(fetch = FetchType.EAGER)
    @Enumerated(EnumType.STRING)
    private Set<Role> roles;
    
    public enum Role {
        USER, ADMIN
    }
}
'@
Create-UTF8NoBOM -Path "$projectRoot\src\main\java\com\shop\entity\User.java" -Content $userContent

# Repository - UserRepository.java
$userRepoContent = @'
package com.shop.repository;

import com.shop.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
    Optional<User> findByApiKey(String apiKey);
    boolean existsByUsername(String username);
}
'@
Create-UTF8NoBOM -Path "$projectRoot\src\main\java\com\shop\repository\UserRepository.java" -Content $userRepoContent

# Repository - ItemRepository.java
$itemRepoContent = @'
package com.shop.repository;

import com.shop.entity.Item;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Long> {
    List<Item> findByCategoryId(Long categoryId);
}
'@
Create-UTF8NoBOM -Path "$projectRoot\src\main\java\com\shop\repository\ItemRepository.java" -Content $itemRepoContent

# Repository - CategoryRepository.java
$categoryRepoContent = @'
package com.shop.repository;

import com.shop.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, Long> {
}
'@
Create-UTF8NoBOM -Path "$projectRoot\src\main\java\com\shop\repository\CategoryRepository.java" -Content $categoryRepoContent

# Security - JwtUtil.java
$jwtUtilContent = @'
package com.shop.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import java.security.Key;
import java.util.Date;

@Component
public class JwtUtil {
    @Value("${jwt.secret}")
    private String secret;
    
    @Value("${jwt.expiration}")
    private Long expiration;
    
    private Key getSigningKey() {
        return Keys.hmacShaKeyFor(secret.getBytes());
    }
    
    public String generateToken(String username) {
        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }
    
    public String extractUsername(String token) {
        return extractClaims(token).getSubject();
    }
    
    public boolean validateToken(String token, String username) {
        return extractUsername(token).equals(username) && !isTokenExpired(token);
    }
    
    private boolean isTokenExpired(String token) {
        return extractClaims(token).getExpiration().before(new Date());
    }
    
    private Claims extractClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}
'@
Create-UTF8NoBOM -Path "$projectRoot\src\main\java\com\shop\security\JwtUtil.java" -Content $jwtUtilContent

# Security - JwtAuthenticationFilter.java
$jwtFilterContent = @'
package com.shop.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    @Autowired
    private JwtUtil jwtUtil;
    
    @Autowired
    private UserDetailsService userDetailsService;
    
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, 
                                  FilterChain filterChain) throws ServletException, IOException {
        String authHeader = request.getHeader("Authorization");
        
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            try {
                String username = jwtUtil.extractUsername(token);
                
                if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                    UserDetails userDetails = userDetailsService.loadUserByUsername(username);
                    
                    if (jwtUtil.validateToken(token, userDetails.getUsername())) {
                        UsernamePasswordAuthenticationToken authToken = 
                            new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                        SecurityContextHolder.getContext().setAuthentication(authToken);
                    }
                }
            } catch (Exception e) {
                // Invalid token
            }
        }
        
        filterChain.doFilter(request, response);
    }
}
'@
Create-UTF8NoBOM -Path "$projectRoot\src\main\java\com\shop\security\JwtAuthenticationFilter.java" -Content $jwtFilterContent

# Config - SecurityConfig.java
$securityConfigContent = @'
package com.shop.config;

import com.shop.security.JwtAuthenticationFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    @Autowired
    private JwtAuthenticationFilter jwtAuthFilter;
    
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf(csrf -> csrf.disable())
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/auth/**").permitAll()
                .requestMatchers("/api/shipment/**").authenticated()
                .anyRequest().permitAll()
            )
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);
        
        return http.build();
    }
    
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}
'@
Create-UTF8NoBOM -Path "$projectRoot\src\main\java\com\shop\config\SecurityConfig.java" -Content $securityConfigContent

# Service - CustomUserDetailsService.java
$userDetailsServiceContent = @'
package com.shop.service;

import com.shop.entity.User;
import com.shop.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import java.util.stream.Collectors;

@Service
public class CustomUserDetailsService implements UserDetailsService {
    @Autowired
    private UserRepository userRepository;
    
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
            .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        
        return org.springframework.security.core.userdetails.User.builder()
            .username(user.getUsername())
            .password(user.getPassword())
            .authorities(user.getRoles().stream()
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role.name()))
                .collect(Collectors.toList()))
            .build();
    }
}
'@
Create-UTF8NoBOM -Path "$projectRoot\src\main\java\com\shop\service\CustomUserDetailsService.java" -Content $userDetailsServiceContent

# Controller - AuthController.java
$authControllerContent = @'
package com.shop.controller;

import com.shop.dto.AuthRequest;
import com.shop.dto.AuthResponse;
import com.shop.dto.RegisterRequest;
import com.shop.entity.User;
import com.shop.repository.UserRepository;
import com.shop.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import java.util.Set;
import java.util.UUID;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    @Autowired
    private AuthenticationManager authManager;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Autowired
    private JwtUtil jwtUtil;
    
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            return ResponseEntity.badRequest().body("Username already exists");
        }
        
        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setApiKey(UUID.randomUUID().toString());
        user.setRoles(Set.of(User.Role.USER));
        
        userRepository.save(user);
        
        return ResponseEntity.ok("User registered successfully");
    }
    
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody AuthRequest request) {
        authManager.authenticate(
            new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
        );
        
        User user = userRepository.findByUsername(request.getUsername()).orElseThrow();
        String token = jwtUtil.generateToken(user.getUsername());
        
        return ResponseEntity.ok(new AuthResponse(token, user.getApiKey()));
    }
}
'@
Create-UTF8NoBOM -Path "$projectRoot\src\main\java\com\shop\controller\AuthController.java" -Content $authControllerContent

# Controller - CategoryController.java
$categoryControllerContent = @'
package com.shop.controller;

import com.shop.dto.CategoryDto;
import com.shop.entity.Category;
import com.shop.repository.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/categories")
public class CategoryController {
    @Autowired
    private CategoryRepository categoryRepository;
    
    @PostMapping
    public ResponseEntity<?> createCategory(@RequestBody CategoryDto dto) {
        Category category = new Category();
        category.setName(dto.getName());
        category.setDescription(dto.getDescription());
        
        return ResponseEntity.ok(categoryRepository.save(category));
    }
    
    @GetMapping
    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<?> getCategory(@PathVariable Long id) {
        return categoryRepository.findById(id)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }
}
'@
Create-UTF8NoBOM -Path "$projectRoot\src\main\java\com\shop\controller\CategoryController.java" -Content $categoryControllerContent

# Controller - ItemController.java
$itemControllerContent = @'
package com.shop.controller;

import com.shop.dto.ItemDto;
import com.shop.dto.ItemUpdateDto;
import com.shop.entity.Category;
import com.shop.entity.Item;
import com.shop.repository.CategoryRepository;
import com.shop.repository.ItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/items")
public class ItemController {
    @Autowired
    private ItemRepository itemRepository;
    
    @Autowired
    private CategoryRepository categoryRepository;
    
    @PostMapping
    public ResponseEntity<?> createItem(@RequestBody ItemDto dto) {
        Category category = categoryRepository.findById(dto.getCategoryId())
            .orElseThrow(() -> new RuntimeException("Category not found"));
        
        Item item = new Item();
        item.setName(dto.getName());
        item.setDescription(dto.getDescription());
        item.setPrice(dto.getPrice());
        item.setQuantity(dto.getQuantity());
        item.setCategory(category);
        
        return ResponseEntity.ok(itemRepository.save(item));
    }
    
    @GetMapping
    public List<Item> getAllItems() {
        return itemRepository.findAll();
    }
    
    @GetMapping("/category/{categoryId}")
    public List<Item> getItemsByCategory(@PathVariable Long categoryId) {
        return itemRepository.findByCategoryId(categoryId);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<?> updateItem(@PathVariable Long id, @RequestBody ItemUpdateDto dto) {
        Item item = itemRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Item not found"));
        
        if (dto.getQuantity() != null) {
            item.setQuantity(item.getQuantity() + dto.getQuantity());
        }
        
        if (dto.getPrice() != null) {
            item.setPrice(dto.getPrice());
        }
        
        return ResponseEntity.ok(itemRepository.save(item));
    }
}
'@
Create-UTF8NoBOM -Path "$projectRoot\src\main\java\com\shop\controller\ItemController.java" -Content $itemControllerContent

# Controller - ShipmentController.java
$shipmentControllerContent = @'
package com.shop.controller;

import com.shop.dto.ShipmentDto;
import com.shop.entity.Item;
import com.shop.entity.User;
import com.shop.repository.ItemRepository;
import com.shop.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/shipment")
public class ShipmentController {
    @Autowired
    private ItemRepository itemRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @PostMapping
    public ResponseEntity<?> shipItem(@RequestHeader("X-API-KEY") String apiKey, 
                                    @RequestBody ShipmentDto dto) {
        // Проверка API ключа
        User user = userRepository.findByApiKey(apiKey)
            .orElseThrow(() -> new RuntimeException("Invalid API key"));
        
        Item item = itemRepository.findById(dto.getItemId())
            .orElseThrow(() -> new RuntimeException("Item not found"));
        
        if (item.getQuantity() < dto.getQuantity()) {
            return ResponseEntity.badRequest().body("Insufficient quantity");
        }
        
        item.setQuantity(item.getQuantity() - dto.getQuantity());
        itemRepository.save(item);
        
        return ResponseEntity.ok("Shipment processed successfully");
    }
}
'@
Create-UTF8NoBOM -Path "$projectRoot\src\main\java\com\shop\controller\ShipmentController.java" -Content $shipmentControllerContent

# Создание DTO файлов
$dtoFiles = @{
    "AuthRequest.java" = @'
package com.shop.dto;

import lombok.Data;

@Data
public class AuthRequest {
    private String username;
    private String password;
}
'@
    "AuthResponse.java" = @'
package com.shop.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AuthResponse {
    private String token;
    private String apiKey;
}
'@
    "RegisterRequest.java" = @'
package com.shop.dto;

import lombok.Data;

@Data
public class RegisterRequest {
    private String username;
    private String password;
}
'@
    "CategoryDto.java" = @'
package com.shop.dto;

import lombok.Data;

@Data
public class CategoryDto {
    private String name;
    private String description;
}
'@
    "ItemDto.java" = @'
package com.shop.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class ItemDto {
    private String name;
    private String description;
    private BigDecimal price;
    private Integer quantity;
    private Long categoryId;
}
'@
    "ItemUpdateDto.java" = @'
package com.shop.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class ItemUpdateDto {
    private Integer quantity;
    private BigDecimal price;
}
'@
    "ShipmentDto.java" = @'
package com.shop.dto;

import lombok.Data;

@Data
public class ShipmentDto {
    private Long itemId;
    private Integer quantity;
}
'@
}

foreach ($file in $dtoFiles.GetEnumerator()) {
    Create-UTF8NoBOM -Path "$projectRoot\src\main\java\com\shop\dto\$($file.Key)" -Content $file.Value
}

# Создание .gitignore
$gitignoreContent = @'
target/
!.mvn/wrapper/maven-wrapper.jar
!**/src/main/**/target/
!**/src/test/**/target/

### IntelliJ IDEA ###
.idea
*.iws
*.iml
*.ipr

### Eclipse ###
.apt_generated
.classpath
.factorypath
.project
.settings
.springBeans
.sts4-cache

### VS Code ###
.vscode/

### Mac OS ###
.DS_Store
'@
Create-UTF8NoBOM -Path "$projectRoot\.gitignore" -Content $gitignoreContent

# Создание README.md
$readmeContent = @'
# Shop Backend API

Backend для интернет-магазина на Spring Boot с JWT авторизацией.

## Технологии
- Spring Boot 3.2.0
- Spring Security
- Spring Data JPA
- PostgreSQL
- JWT
- Lombok

## Установка и запуск

1. Создайте базу данных PostgreSQL:
sql
CREATE DATABASE shop_db;

2. Настройте подключение к БД в `application.properties`

3. Запустите приложение:
bash
mvn spring-boot:run

## API Endpoints

### Авторизация
- `POST /api/auth/register` - Регистрация
- `POST /api/auth/login` - Вход

### Категории
- `POST /api/categories` - Создание категории
- `GET /api/categories` - Все категории
- `GET /api/categories/{id}` - Категория по ID

### Товары
- `POST /api/items` - Создание товара
- `GET /api/items` - Все товары
- `GET /api/items/category/{categoryId}` - Товары по категории
- `PUT /api/items/{id}` - Обновление товара

### Отгрузка
- `POST /api/shipment` - Отгрузка товара (требует X-API-KEY)
'@
Create-UTF8NoBOM -Path "$projectRoot\README.md" -Content $readmeContent

Write-Host ""
Write-Host ""
Write-Host "The project is ready to use!" -ForegroundColor Green
```

Тестовая страница включает:

## Функциональность:

1. **Проверка статуса сервера** - автоматически каждые 5 секунд
2. **Регистрация и вход** - с сохранением JWT токена и API ключа
3. **Создание категорий** - с автоматическим обновлением выпадающих списков
4. **Создание товаров** - с выбором категории
5. **Просмотр всех товаров и категорий**
6. **Просмотр товаров по категориям**
7. **Обновление количества и цены товаров**
8. **Отгрузка товаров** - с использованием API ключа

## Использование:

1. Сохраните файл как `shop-api-test.html`
2. Запустите Spring Boot приложение на `http://localhost:8080`
3. Откройте HTML файл в браузере
4. Следуйте порядку тестирования:
    - Сначала зарегистрируйтесь и войдите
    - Создайте категории
    - Создайте товары
    - Тестируйте остальные функции

Страница показывает результаты всех операций и автоматически отображает статус сервера (Online/Offline).

```
<!DOCTYPE html>
<html lang="ru">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Тестирование API Магазина</title>
    <style>
        * {
            margin: 0;
            padding: 0;
            box-sizing: border-box;
        }
        
        body {
            font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, Arial, sans-serif;
            background-color: #f5f5f5;
            color: #333;
            line-height: 1.6;
        }
        
        .container {
            max-width: 1200px;
            margin: 0 auto;
            padding: 20px;
        }
        
        h1 {
            text-align: center;
            color: #2c3e50;
            margin-bottom: 30px;
        }
        
        .auth-info {
            background: #e8f4f8;
            border: 1px solid #b8e0ea;
            border-radius: 8px;
            padding: 15px;
            margin-bottom: 30px;
        }
        
        .auth-info h3 {
            color: #2980b9;
            margin-bottom: 10px;
        }
        
        .token-display {
            background: #fff;
            padding: 10px;
            border-radius: 4px;
            word-break: break-all;
            font-family: monospace;
            font-size: 12px;
        }
        
        .section {
            background: white;
            border-radius: 10px;
            padding: 25px;
            margin-bottom: 20px;
            box-shadow: 0 2px 5px rgba(0,0,0,0.1);
        }
        
        .section h2 {
            color: #34495e;
            margin-bottom: 15px;
            padding-bottom: 10px;
            border-bottom: 2px solid #ecf0f1;
        }
        
        .form-group {
            margin-bottom: 15px;
        }
        
        label {
            display: block;
            margin-bottom: 5px;
            color: #555;
            font-weight: 500;
        }
        
        input[type="text"],
        input[type="password"],
        input[type="number"],
        select {
            width: 100%;
            padding: 10px;
            border: 1px solid #ddd;
            border-radius: 5px;
            font-size: 14px;
        }
        
        input[type="text"]:focus,
        input[type="password"]:focus,
        input[type="number"]:focus,
        select:focus {
            outline: none;
            border-color: #3498db;
        }
        
        button {
            background: #3498db;
            color: white;
            border: none;
            padding: 10px 20px;
            border-radius: 5px;
            cursor: pointer;
            font-size: 14px;
            transition: background 0.3s;
        }
        
        button:hover {
            background: #2980b9;
        }
        
        button:disabled {
            background: #95a5a6;
            cursor: not-allowed;
        }
        
        .result {
            margin-top: 15px;
            padding: 15px;
            background: #ecf0f1;
            border-radius: 5px;
            max-height: 300px;
            overflow-y: auto;
        }
        
        .result pre {
            white-space: pre-wrap;
            word-wrap: break-word;
        }
        
        .success {
            background: #d4edda;
            color: #155724;
            border: 1px solid #c3e6cb;
        }
        
        .error {
            background: #f8d7da;
            color: #721c24;
            border: 1px solid #f5c6cb;
        }
        
        .grid {
            display: grid;
            grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));
            gap: 15px;
        }
        
        .status {
            display: inline-block;
            padding: 3px 8px;
            border-radius: 3px;
            font-size: 12px;
            margin-left: 10px;
        }
        
        .status.online {
            background: #27ae60;
            color: white;
        }
        
        .status.offline {
            background: #e74c3c;
            color: white;
        }
    </style>
</head>
<body>
    <div class="container">
        <h1>Тестирование API Магазина <span id="serverStatus" class="status offline">Offline</span></h1>
        
        <div class="auth-info">
            <h3>Информация об авторизации</h3>
            <div>
                <strong>Пользователь:</strong> <span id="currentUser">Не авторизован</span><br>
                <strong>JWT Token:</strong> <div id="jwtToken" class="token-display">Отсутствует</div>
                <strong>API Key:</strong> <div id="apiKey" class="token-display">Отсутствует</div>
            </div>
        </div>

        <!-- 1. Авторизация -->
        <div class="section">
            <h2>1. Авторизация пользователя</h2>
            <div class="grid">
                <div>
                    <h3>Регистрация</h3>
                    <div class="form-group">
                        <label>Имя пользователя:</label>
                        <input type="text" id="regUsername" placeholder="user1">
                    </div>
                    <div class="form-group">
                        <label>Пароль:</label>
                        <input type="password" id="regPassword" placeholder="password123">
                    </div>
                    <button onclick="register()">Зарегистрироваться</button>
                </div>
                <div>
                    <h3>Вход</h3>
                    <div class="form-group">
                        <label>Имя пользователя:</label>
                        <input type="text" id="loginUsername" placeholder="user1">
                    </div>
                    <div class="form-group">
                        <label>Пароль:</label>
                        <input type="password" id="loginPassword" placeholder="password123">
                    </div>
                    <button onclick="login()">Войти</button>
                </div>
            </div>
            <div id="authResult" class="result" style="display:none;"></div>
        </div>

        <!-- 2. Категории -->
        <div class="section">
            <h2>3. Добавить новую категорию</h2>
            <div class="form-group">
                <label>Название категории:</label>
                <input type="text" id="categoryName" placeholder="Электроника">
            </div>
            <div class="form-group">
                <label>Описание:</label>
                <input type="text" id="categoryDesc" placeholder="Электронные устройства и гаджеты">
            </div>
            <button onclick="createCategory()">Создать категорию</button>
            <div id="categoryResult" class="result" style="display:none;"></div>
        </div>

        <!-- 3. Товары -->
        <div class="section">
            <h2>4. Добавить новый товар</h2>
            <div class="form-group">
                <label>Название товара:</label>
                <input type="text" id="itemName" placeholder="Ноутбук ASUS">
            </div>
            <div class="form-group">
                <label>Описание:</label>
                <input type="text" id="itemDesc" placeholder="Мощный игровой ноутбук">
            </div>
            <div class="form-group">
                <label>Цена:</label>
                <input type="number" id="itemPrice" placeholder="75000" step="0.01">
            </div>
            <div class="form-group">
                <label>Количество:</label>
                <input type="number" id="itemQuantity" placeholder="10">
            </div>
            <div class="form-group">
                <label>Категория:</label>
                <select id="itemCategory">
                    <option value="">Выберите категорию</option>
                </select>
            </div>
            <button onclick="createItem()">Создать товар</button>
            <div id="itemResult" class="result" style="display:none;"></div>
        </div>

        <!-- 4. Просмотр товаров -->
        <div class="section">
            <h2>5-7. Просмотр товаров</h2>
            <div class="grid">
                <button onclick="getAllItems()">Показать все товары</button>
                <button onclick="getAllCategories()">Показать все категории</button>
                <div>
                    <select id="viewCategory" style="width: auto; display: inline-block;">
                        <option value="">Выберите категорию</option>
                    </select>
                    <button onclick="getItemsByCategory()">Показать товары категории</button>
                </div>
            </div>
            <div id="viewResult" class="result" style="display:none;"></div>
        </div>

        <!-- 5. Обновление товара -->
        <div class="section">
            <h2>8. Изменение количества и цены товара</h2>
            <div class="form-group">
                <label>ID товара:</label>
                <input type="number" id="updateItemId" placeholder="1">
            </div>
            <div class="form-group">
                <label>Добавить количество (может быть отрицательным):</label>
                <input type="number" id="updateQuantity" placeholder="5">
            </div>
            <div class="form-group">
                <label>Новая цена (оставьте пустым, чтобы не менять):</label>
                <input type="number" id="updatePrice" placeholder="80000" step="0.01">
            </div>
            <button onclick="updateItem()">Обновить товар</button>
            <div id="updateResult" class="result" style="display:none;"></div>
        </div>

        <!-- 6. Отгрузка -->
        <div class="section">
            <h2>9. Отгрузка товаров</h2>
            <div class="form-group">
                <label>ID товара:</label>
                <input type="number" id="shipItemId" placeholder="1">
            </div>
            <div class="form-group">
                <label>Количество для отгрузки:</label>
                <input type="number" id="shipQuantity" placeholder="2">
            </div>
            <button onclick="shipItem()">Отгрузить товар</button>
            <div id="shipResult" class="result" style="display:none;"></div>
        </div>
    </div>

    <script>
        const API_BASE = 'http://localhost:8080/api';
        let authToken = '';
        let userApiKey = '';

        // Проверка статуса сервера
        async function checkServerStatus() {
            try {
                const response = await fetch(API_BASE + '/categories');
                document.getElementById('serverStatus').textContent = 'Online';
                document.getElementById('serverStatus').className = 'status online';
            } catch (error) {
                document.getElementById('serverStatus').textContent = 'Offline';
                document.getElementById('serverStatus').className = 'status offline';
            }
        }

        // Вспомогательные функции
        function showResult(elementId, message, isSuccess = true) {
            const element = document.getElementById(elementId);
            element.style.display = 'block';
            element.className = 'result ' + (isSuccess ? 'success' : 'error');
            element.innerHTML = '<pre>' + message + '</pre>';
        }

        function getHeaders(includeAuth = true) {
            const headers = {
                'Content-Type': 'application/json'
            };
            if (includeAuth && authToken) {
                headers['Authorization'] = 'Bearer ' + authToken;
            }
            return headers;
        }

        // 1. Регистрация
        async function register() {
            const username = document.getElementById('regUsername').value;
            const password = document.getElementById('regPassword').value;
            
            try {
                const response = await fetch(API_BASE + '/auth/register', {
                    method: 'POST',
                    headers: getHeaders(false),
                    body: JSON.stringify({ username, password })
                });
                
                const data = await response.text();
                showResult('authResult', data, response.ok);
            } catch (error) {
                showResult('authResult', 'Ошибка: ' + error.message, false);
            }
        }

        // 2. Вход
        async function login() {
            const username = document.getElementById('loginUsername').value;
            const password = document.getElementById('loginPassword').value;
            
            try {
                const response = await fetch(API_BASE + '/auth/login', {
                    method: 'POST',
                    headers: getHeaders(false),
                    body: JSON.stringify({ username, password })
                });
                
                if (response.ok) {
                    const data = await response.json();
                    authToken = data.token;
                    userApiKey = data.apiKey;
                    
                    document.getElementById('currentUser').textContent = username;
                    document.getElementById('jwtToken').textContent = authToken;
                    document.getElementById('apiKey').textContent = userApiKey;
                    
                    showResult('authResult', 'Вход выполнен успешно!\nToken: ' + authToken + '\nAPI Key: ' + userApiKey);
                    
                    // Загрузка категорий для выпадающих списков
                    loadCategories();
                } else {
                    showResult('authResult', 'Ошибка входа', false);
                }
            } catch (error) {
                showResult('authResult', 'Ошибка: ' + error.message, false);
            }
        }

        // 3. Создание категории
        async function createCategory() {
            const name = document.getElementById('categoryName').value;
            const description = document.getElementById('categoryDesc').value;
            
            try {
                const response = await fetch(API_BASE + '/categories', {
                    method: 'POST',
                    headers: getHeaders(),
                    body: JSON.stringify({ name, description })
                });
                
                const data = await response.json();
                showResult('categoryResult', JSON.stringify(data, null, 2), response.ok);
                
                if (response.ok) {
                    loadCategories();
                }
            } catch (error) {
                showResult('categoryResult', 'Ошибка: ' + error.message, false);
            }
        }

        // 4. Создание товара
        async function createItem() {
            const item = {
                name: document.getElementById('itemName').value,
                description: document.getElementById('itemDesc').value,
                price: parseFloat(document.getElementById('itemPrice').value),
                quantity: parseInt(document.getElementById('itemQuantity').value),
                categoryId: parseInt(document.getElementById('itemCategory').value)
            };
            
            try {
                const response = await fetch(API_BASE + '/items', {
                    method: 'POST',
                    headers: getHeaders(),
                    body: JSON.stringify(item)
                });
                
                const data = await response.json();
                showResult('itemResult', JSON.stringify(data, null, 2), response.ok);
            } catch (error) {
                showResult('itemResult', 'Ошибка: ' + error.message, false);
            }
        }

        // 5. Получение всех товаров
        async function getAllItems() {
            try {
                const response = await fetch(API_BASE + '/items', {
                    headers: getHeaders()
                });
                
                const data = await response.json();
                showResult('viewResult', JSON.stringify(data, null, 2), response.ok);
            } catch (error) {
                showResult('viewResult', 'Ошибка: ' + error.message, false);
            }
        }

        // 6. Получение всех категорий
        async function getAllCategories() {
            try {
                const response = await fetch(API_BASE + '/categories', {
                    headers: getHeaders()
                });
                
                const data = await response.json();
                showResult('viewResult', JSON.stringify(data, null, 2), response.ok);
            } catch (error) {
                showResult('viewResult', 'Ошибка: ' + error.message, false);
            }
        }

        // 7. Получение товаров по категории
        async function getItemsByCategory() {
            const categoryId = document.getElementById('viewCategory').value;
            if (!categoryId) {
                showResult('viewResult', 'Выберите категорию', false);
                return;
            }
            
            try {
                const response = await fetch(API_BASE + '/items/category/' + categoryId, {
                    headers: getHeaders()
                });
                
                const data = await response.json();
                showResult('viewResult', JSON.stringify(data, null, 2), response.ok);
            } catch (error) {
                showResult('viewResult', 'Ошибка: ' + error.message, false);
            }
        }

        // 8. Обновление товара
        async function updateItem() {
            const itemId = document.getElementById('updateItemId').value;
            const update = {};
            
            const quantity = document.getElementById('updateQuantity').value;
            const price = document.getElementById('updatePrice').value;
            
            if (quantity) update.quantity = parseInt(quantity);
            if (price) update.price = parseFloat(price);
            
            try {
                const response = await fetch(API_BASE + '/items/' + itemId, {
                    method: 'PUT',
                    headers: getHeaders(),
                    body: JSON.stringify(update)
                });
                
                const data = await response.json();
                showResult('updateResult', JSON.stringify(data, null, 2), response.ok);
            } catch (error) {
                showResult('updateResult', 'Ошибка: ' + error.message, false);
            }
        }

        // 9. Отгрузка товара
        async function shipItem() {
            const shipment = {
                itemId: parseInt(document.getElementById('shipItemId').value),
                quantity: parseInt(document.getElementById('shipQuantity').value)
            };
            
            try {
                const response = await fetch(API_BASE + '/shipment', {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/json',
                        'X-API-KEY': userApiKey
                    },
                    body: JSON.stringify(shipment)
                });
                
                const data = await response.text();
                showResult('shipResult', data, response.ok);
            } catch (error) {
                showResult('shipResult', 'Ошибка: ' + error.message, false);
            }
        }

        // Загрузка категорий для выпадающих списков
        async function loadCategories() {
            try {
                const response = await fetch(API_BASE + '/categories', {
                    headers: getHeaders()
                });
                
                if (response.ok) {
                    const categories = await response.json();
                    
                    const selects = ['itemCategory', 'viewCategory'];
                    selects.forEach(selectId => {
                        const select = document.getElementById(selectId);
                        select.innerHTML = '<option value="">Выберите категорию</option>';
                        categories.forEach(cat => {
                            select.innerHTML += `<option value="${cat.id}">${cat.name}</option>`;
                        });
                    });
                }
            } catch (error) {
                console.error('Ошибка загрузки категорий:', error);
            }
        }

        // Инициализация
        checkServerStatus();
        setInterval(checkServerStatus, 5000);
    </script>
</body>
</html>
```

# Коллекция Postman

## Инструкция по использованию:

### 1. Импорт коллекции в Postman:

- Сохраните файл как `Shop-API-Collection.postman_collection.json`
- Откройте Postman
- Нажмите "Import" → выберите файл
- Коллекция появится в левой панели

### 2. Структура коллекции:

- **Авторизация** - регистрация и вход
- **Категории** - создание и просмотр
- **Товары** - все CRUD операции
- **Отгрузка** - защищенный endpoint
- **Дополнительные примеры** - готовые запросы

### 3. Автоматические функции:

- JWT токен и API ключ **автоматически сохраняются** после входа
- ID категории и товара **сохраняются** для использования в других запросах
- **Тесты** проверяют статус ответов и структуру данных

### 4. Последовательность тестирования:

1. Запустите Spring Boot приложение
2. Выполните "Регистрация пользователя"
3. Выполните "Вход пользователя" (токен сохранится автоматически)
4. Создайте категорию
5. Создайте товар
6. Тестируйте остальные endpoints

### 5. Переменные коллекции:

- `{{baseUrl}}` - базовый URL (http://localhost:8080)
- `{{authToken}}` - JWT токен (заполняется автоматически)
- `{{apiKey}}` - API ключ для отгрузки (заполняется автоматически)
- `{{categoryId}}` - ID созданной категории
- `{{itemId}}` - ID созданного товара

Все запросы готовы к использованию и содержат примеры данных!

```
{
  "info": {
    "_postman_id": "shop-api-collection-v1",
    "name": "Shop API - Интернет магазин",
    "description": "Коллекция для тестирования API интернет-магазина с JWT авторизацией",
    "schema": "https://schema.getpostman.com/json/collection/v2.1.0/collection.json"
  },
  "item": [
    {
      "name": "1. Авторизация",
      "item": [
        {
          "name": "Регистрация пользователя",
          "event": [
            {
              "listen": "test",
              "script": {
                "exec": [
                  "pm.test(\"Статус ответа 200\", function () {",
                  "    pm.response.to.have.status(200);",
                  "});",
                  "",
                  "pm.test(\"Успешная регистрация\", function () {",
                  "    pm.expect(pm.response.text()).to.include(\"successfully\");",
                  "});"
                ],
                "type": "text/javascript"
              }
            }
          ],
          "request": {
            "method": "POST",
            "header": [
              {
                "key": "Content-Type",
                "value": "application/json"
              }
            ],
            "body": {
              "mode": "raw",
              "raw": "{\n    \"username\": \"testuser1\",\n    \"password\": \"password123\"\n}"
            },
            "url": {
              "raw": "{{baseUrl}}/api/auth/register",
              "host": ["{{baseUrl}}"],
              "path": ["api", "auth", "register"]
            }
          }
        },
        {
          "name": "Вход пользователя",
          "event": [
            {
              "listen": "test",
              "script": {
                "exec": [
                  "pm.test(\"Статус ответа 200\", function () {",
                  "    pm.response.to.have.status(200);",
                  "});",
                  "",
                  "var jsonData = pm.response.json();",
                  "",
                  "pm.test(\"Получен JWT токен\", function () {",
                  "    pm.expect(jsonData).to.have.property('token');",
                  "});",
                  "",
                  "pm.test(\"Получен API ключ\", function () {",
                  "    pm.expect(jsonData).to.have.property('apiKey');",
                  "});",
                  "",
                  "// Сохранение токена и API ключа в переменные коллекции",
                  "if (jsonData.token) {",
                  "    pm.collectionVariables.set(\"authToken\", jsonData.token);",
                  "}",
                  "",
                  "if (jsonData.apiKey) {",
                  "    pm.collectionVariables.set(\"apiKey\", jsonData.apiKey);",
                  "}"
                ],
                "type": "text/javascript"
              }
            }
          ],
          "request": {
            "method": "POST",
            "header": [
              {
                "key": "Content-Type",
                "value": "application/json"
              }
            ],
            "body": {
              "mode": "raw",
              "raw": "{\n    \"username\": \"testuser1\",\n    \"password\": \"password123\"\n}"
            },
            "url": {
              "raw": "{{baseUrl}}/api/auth/login",
              "host": ["{{baseUrl}}"],
              "path": ["api", "auth", "login"]
            }
          }
        }
      ]
    },
    {
      "name": "2. Категории",
      "item": [
        {
          "name": "Создать категорию",
          "event": [
            {
              "listen": "test",
              "script": {
                "exec": [
                  "pm.test(\"Статус ответа 200\", function () {",
                  "    pm.response.to.have.status(200);",
                  "});",
                  "",
                  "var jsonData = pm.response.json();",
                  "",
                  "pm.test(\"Категория создана\", function () {",
                  "    pm.expect(jsonData).to.have.property('id');",
                  "    pm.expect(jsonData).to.have.property('name');",
                  "});",
                  "",
                  "// Сохранение ID категории для дальнейшего использования",
                  "if (jsonData.id) {",
                  "    pm.collectionVariables.set(\"categoryId\", jsonData.id);",
                  "}"
                ],
                "type": "text/javascript"
              }
            }
          ],
          "request": {
            "method": "POST",
            "header": [
              {
                "key": "Content-Type",
                "value": "application/json"
              },
              {
                "key": "Authorization",
                "value": "Bearer {{authToken}}"
              }
            ],
            "body": {
              "mode": "raw",
              "raw": "{\n    \"name\": \"Электроника\",\n    \"description\": \"Электронные устройства и гаджеты\"\n}"
            },
            "url": {
              "raw": "{{baseUrl}}/api/categories",
              "host": ["{{baseUrl}}"],
              "path": ["api", "categories"]
            }
          }
        },
        {
          "name": "Получить все категории",
          "event": [
            {
              "listen": "test",
              "script": {
                "exec": [
                  "pm.test(\"Статус ответа 200\", function () {",
                  "    pm.response.to.have.status(200);",
                  "});",
                  "",
                  "pm.test(\"Ответ является массивом\", function () {",
                  "    var jsonData = pm.response.json();",
                  "    pm.expect(jsonData).to.be.an('array');",
                  "});"
                ],
                "type": "text/javascript"
              }
            }
          ],
          "request": {
            "method": "GET",
            "header": [
              {
                "key": "Authorization",
                "value": "Bearer {{authToken}}"
              }
            ],
            "url": {
              "raw": "{{baseUrl}}/api/categories",
              "host": ["{{baseUrl}}"],
              "path": ["api", "categories"]
            }
          }
        },
        {
          "name": "Получить категорию по ID",
          "event": [
            {
              "listen": "test",
              "script": {
                "exec": [
                  "pm.test(\"Статус ответа 200\", function () {",
                  "    pm.response.to.have.status(200);",
                  "});",
                  "",
                  "pm.test(\"Категория получена\", function () {",
                  "    var jsonData = pm.response.json();",
                  "    pm.expect(jsonData).to.have.property('id');",
                  "    pm.expect(jsonData).to.have.property('name');",
                  "});"
                ],
                "type": "text/javascript"
              }
            }
          ],
          "request": {
            "method": "GET",
            "header": [
              {
                "key": "Authorization",
                "value": "Bearer {{authToken}}"
              }
            ],
            "url": {
              "raw": "{{baseUrl}}/api/categories/{{categoryId}}",
              "host": ["{{baseUrl}}"],
              "path": ["api", "categories", "{{categoryId}}"]
            }
          }
        }
      ]
    },
    {
      "name": "3. Товары",
      "item": [
        {
          "name": "Создать товар",
          "event": [
            {
              "listen": "test",
              "script": {
                "exec": [
                  "pm.test(\"Статус ответа 200\", function () {",
                  "    pm.response.to.have.status(200);",
                  "});",
                  "",
                  "var jsonData = pm.response.json();",
                  "",
                  "pm.test(\"Товар создан\", function () {",
                  "    pm.expect(jsonData).to.have.property('id');",
                  "    pm.expect(jsonData).to.have.property('name');",
                  "    pm.expect(jsonData).to.have.property('price');",
                  "});",
                  "",
                  "// Сохранение ID товара для дальнейшего использования",
                  "if (jsonData.id) {",
                  "    pm.collectionVariables.set(\"itemId\", jsonData.id);",
                  "}"
                ],
                "type": "text/javascript"
              }
            }
          ],
          "request": {
            "method": "POST",
            "header": [
              {
                "key": "Content-Type",
                "value": "application/json"
              },
              {
                "key": "Authorization",
                "value": "Bearer {{authToken}}"
              }
            ],
            "body": {
              "mode": "raw",
              "raw": "{\n    \"name\": \"Ноутбук ASUS ROG\",\n    \"description\": \"Мощный игровой ноутбук\",\n    \"price\": 75000.00,\n    \"quantity\": 10,\n    \"categoryId\": {{categoryId}}\n}"
            },
            "url": {
              "raw": "{{baseUrl}}/api/items",
              "host": ["{{baseUrl}}"],
              "path": ["api", "items"]
            }
          }
        },
        {
          "name": "Получить все товары",
          "event": [
            {
              "listen": "test",
              "script": {
                "exec": [
                  "pm.test(\"Статус ответа 200\", function () {",
                  "    pm.response.to.have.status(200);",
                  "});",
                  "",
                  "pm.test(\"Ответ является массивом\", function () {",
                  "    var jsonData = pm.response.json();",
                  "    pm.expect(jsonData).to.be.an('array');",
                  "});"
                ],
                "type": "text/javascript"
              }
            }
          ],
          "request": {
            "method": "GET",
            "header": [
              {
                "key": "Authorization",
                "value": "Bearer {{authToken}}"
              }
            ],
            "url": {
              "raw": "{{baseUrl}}/api/items",
              "host": ["{{baseUrl}}"],
              "path": ["api", "items"]
            }
          }
        },
        {
          "name": "Получить товары по категории",
          "event": [
            {
              "listen": "test",
              "script": {
                "exec": [
                  "pm.test(\"Статус ответа 200\", function () {",
                  "    pm.response.to.have.status(200);",
                  "});",
                  "",
                  "pm.test(\"Ответ является массивом\", function () {",
                  "    var jsonData = pm.response.json();",
                  "    pm.expect(jsonData).to.be.an('array');",
                  "});"
                ],
                "type": "text/javascript"
              }
            }
          ],
          "request": {
            "method": "GET",
            "header": [
              {
                "key": "Authorization",
                "value": "Bearer {{authToken}}"
              }
            ],
            "url": {
              "raw": "{{baseUrl}}/api/items/category/{{categoryId}}",
              "host": ["{{baseUrl}}"],
              "path": ["api", "items", "category", "{{categoryId}}"]
            }
          }
        },
        {
          "name": "Обновить товар (количество и цену)",
          "event": [
            {
              "listen": "test",
              "script": {
                "exec": [
                  "pm.test(\"Статус ответа 200\", function () {",
                  "    pm.response.to.have.status(200);",
                  "});",
                  "",
                  "pm.test(\"Товар обновлен\", function () {",
                  "    var jsonData = pm.response.json();",
                  "    pm.expect(jsonData).to.have.property('id');",
                  "    pm.expect(jsonData.quantity).to.equal(15);",
                  "    pm.expect(jsonData.price).to.equal(80000);",
                  "});"
                ],
                "type": "text/javascript"
              }
            }
          ],
          "request": {
            "method": "PUT",
            "header": [
              {
                "key": "Content-Type",
                "value": "application/json"
              },
              {
                "key": "Authorization",
                "value": "Bearer {{authToken}}"
              }
            ],
            "body": {
              "mode": "raw",
              "raw": "{\n    \"quantity\": 5,\n    \"price\": 80000.00\n}"
            },
            "url": {
              "raw": "{{baseUrl}}/api/items/{{itemId}}",
              "host": ["{{baseUrl}}"],
              "path": ["api", "items", "{{itemId}}"]
            }
          }
        }
      ]
    },
    {
      "name": "4. Отгрузка",
      "item": [
        {
          "name": "Отгрузить товар",
          "event": [
            {
              "listen": "test",
              "script": {
                "exec": [
                  "pm.test(\"Статус ответа 200\", function () {",
                  "    pm.response.to.have.status(200);",
                  "});",
                  "",
                  "pm.test(\"Отгрузка выполнена\", function () {",
                  "    pm.expect(pm.response.text()).to.include(\"successfully\");",
                  "});"
                ],
                "type": "text/javascript"
              }
            }
          ],
          "request": {
            "method": "POST",
            "header": [
              {
                "key": "Content-Type",
                "value": "application/json"
              },
              {
                "key": "X-API-KEY",
                "value": "{{apiKey}}"
              }
            ],
            "body": {
              "mode": "raw",
              "raw": "{\n    \"itemId\": {{itemId}},\n    \"quantity\": 2\n}"
            },
            "url": {
              "raw": "{{baseUrl}}/api/shipment",
              "host": ["{{baseUrl}}"],
              "path": ["api", "shipment"]
            }
          }
        }
      ]
    },
    {
      "name": "5. Дополнительные примеры",
      "item": [
        {
          "name": "Создать категорию - Одежда",
          "request": {
            "method": "POST",
            "header": [
              {
                "key": "Content-Type",
                "value": "application/json"
              },
              {
                "key": "Authorization",
                "value": "Bearer {{authToken}}"
              }
            ],
            "body": {
              "mode": "raw",
              "raw": "{\n    \"name\": \"Одежда\",\n    \"description\": \"Мужская и женская одежда\"\n}"
            },
            "url": {
              "raw": "{{baseUrl}}/api/categories",
              "host": ["{{baseUrl}}"],
              "path": ["api", "categories"]
            }
          }
        },
        {
          "name": "Создать товар - Смартфон",
          "request": {
            "method": "POST",
            "header": [
              {
                "key": "Content-Type",
                "value": "application/json"
              },
              {
                "key": "Authorization",
                "value": "Bearer {{authToken}}"
              }
            ],
            "body": {
              "mode": "raw",
              "raw": "{\n    \"name\": \"iPhone 15 Pro\",\n    \"description\": \"Флагманский смартфон Apple\",\n    \"price\": 120000.00,\n    \"quantity\": 5,\n    \"categoryId\": {{categoryId}}\n}"
            },
            "url": {
              "raw": "{{baseUrl}}/api/items",
              "host": ["{{baseUrl}}"],
              "path": ["api", "items"]
            }
          }
        },
        {
          "name": "Уменьшить количество товара",
          "request": {
            "method": "PUT",
            "header": [
              {
                "key": "Content-Type",
                "value": "application/json"
              },
              {
                "key": "Authorization",
                "value": "Bearer {{authToken}}"
              }
            ],
            "body": {
              "mode": "raw",
              "raw": "{\n    \"quantity\": -3\n}"
            },
            "url": {
              "raw": "{{baseUrl}}/api/items/{{itemId}}",
              "host": ["{{baseUrl}}"],
              "path": ["api", "items", "{{itemId}}"]
            }
          }
        }
      ]
    }
  ],
  "event": [
    {
      "listen": "prerequest",
      "script": {
        "type": "text/javascript",
        "exec": [""]
      }
    },
    {
      "listen": "test",
      "script": {
        "type": "text/javascript",
        "exec": [""]
      }
    }
  ],
  "variable": [
    {
      "key": "baseUrl",
      "value": "http://localhost:8080",
      "type": "string"
    },
    {
      "key": "authToken",
      "value": "",
      "type": "string"
    },
    {
      "key": "apiKey",
      "value": "",
      "type": "string"
    },
    {
      "key": "categoryId",
      "value": "",
      "type": "number"
    },
    {
      "key": "itemId",
      "value": "",
      "type": "number"
    }
  ]
}
```

# Различия между JWT токеном и API ключом

### JWT (JSON Web Token)

**Что это:**

- Самодостаточный токен, содержащий зашифрованную информацию о пользователе
- Состоит из трех частей: Header.Payload.Signature
- Может содержать claims (утверждения) о пользователе

**Характеристики:**

```java
// Пример JWT токена
eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ1c2VyMSIsImlhdCI6MTcwMjQ1NjMyMCwiZXhwIjoxNzAyNTQyNzIwfQ.xKz5H9J8K_lX3vM2QzK8nL4P6hU3J8K9mN5Q2R4T6Y8

// Расшифровка Payload:
{
  "sub": "user1",           // subject (пользователь)
  "iat": 1702456320,       // issued at (время создания)
  "exp": 1702542720        // expiration (время истечения)
}
```

**Особенности:**

- **Временный** - имеет срок действия (в нашем случае 24 часа)
- **Stateless** - сервер не хранит токены
- **Безопасный** - подписан секретным ключом
- **Информативный** - содержит данные о пользователе

**Использование в нашем проекте:**

```java
// Генерация при входе
String token = jwtUtil.generateToken(user.getUsername());

// Передача в заголовке
Authorization: Bearer eyJhbGciOiJIUzI1NiJ9...

// Проверка в фильтре
if (jwtUtil.validateToken(token, username)) {
    // Авторизация прошла
}
```

### API Key

**Что это:**

- Простая строка-идентификатор (обычно UUID)
- Постоянный ключ, привязанный к пользователю
- Хранится в базе данных

**Характеристики:**

```java
// Пример API ключа
550e8400-e29b-41d4-a716-446655440000
```

**Особенности:**

- **Постоянный** - не имеет срока действия
- **Stateful** - хранится в БД
- **Простой** - just a string
- **Быстрый** - простая проверка в БД

**Использование в нашем проекте:**

```java
// Генерация при регистрации
user.setApiKey(UUID.randomUUID().toString());

// Передача в заголовке
X-API-KEY: 550e8400-e29b-41d4-a716-446655440000

// Проверка в контроллере
User user = userRepository.findByApiKey(apiKey)
    .orElseThrow(() -> new RuntimeException("Invalid API key"));
```

## Сравнительная таблица

|Параметр|JWT Token|API Key|
|---|---|---|
|**Срок действия**|Временный (24 часа)|Постоянный|
|**Хранение на сервере**|Не хранится|Хранится в БД|
|**Содержимое**|Данные пользователя|Только идентификатор|
|**Размер**|~200-500 символов|~36 символов|
|**Производительность**|Быстрая проверка подписи|Запрос к БД|
|**Отзыв**|Сложно (нужен blacklist)|Просто (удалить из БД)|
|**Использование**|Для сессий пользователя|Для сервис-сервис|

## Почему в проекте используются оба?

### JWT для основных операций:

```java
@GetMapping("/api/items")
@PreAuthorize("hasRole('USER')")  // Проверка JWT
public List<Item> getAllItems() {
    // Обычные операции пользователя
}
```

**Преимущества:**

- Безопасные сессии с ограниченным сроком
- Не нагружает БД при каждом запросе
- Содержит роли и права пользователя

### API Key для критических операций:

```java
@PostMapping("/api/shipment")
public ResponseEntity<?> shipItem(@RequestHeader("X-API-KEY") String apiKey) {
    // Критическая операция - отгрузка товара
}
```

**Преимущества:**

- Дополнительный уровень защиты
- Легко отследить кто делал отгрузки
- Можно отозвать доступ мгновенно
- Подходит для интеграций с внешними системами

## Практический пример

```javascript
// 1. Вход - получаем оба ключа
POST /api/auth/login
Response: {
    "token": "eyJhbGci...",     // для обычных операций
    "apiKey": "550e8400..."      // для критических операций
}

// 2. Обычная операция - используем JWT
GET /api/items
Headers: {
    "Authorization": "Bearer eyJhbGci..."
}

// 3. Критическая операция - используем API Key
POST /api/shipment
Headers: {
    "X-API-KEY": "550e8400..."
}
```

## Рекомендации по безопасности

### Для JWT:

- Короткий срок жизни (1-24 часа)
- Использовать HTTPS
- Хранить в httpOnly cookies или localStorage
- Реализовать refresh tokens

### Для API Key:

- Генерировать криптографически стойкие ключи
- Никогда не передавать в URL
- Ограничивать по IP/домену
- Вести логи использования
- Возможность отзыва

Такой подход обеспечивает баланс между удобством (JWT для повседневных операций) и безопасностью (API Key для критических действий).
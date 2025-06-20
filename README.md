# Shop Backend API

Backend для интернет-магазина на Spring Boot с JWT авторизацией.

## Технологии
- Spring Boot 3.4.6
- Spring Security
- Spring Data JPA
- PostgreSQL
- JWT
- Lombok

## Установка и запуск

1. Создайте базу данных PostgreSQL:
```sql
CREATE DATABASE shop_db;
```

2. Настройте подключение к БД в `application.properties`

3. Запустите приложение:
```bash
mvn spring-boot:run
```

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
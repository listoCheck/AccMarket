# AccMarket
**Курсовая работа по дисциплине "Информационные системы"**

![Last Commit](https://img.shields.io/github/last-commit/listoCheck/AccMarket?style=for-the-badge&logo=github&logoColor=white)
![Commits](https://img.shields.io/github/commit-activity/m/listoCheck/AccMarket?style=for-the-badge&logo=github&logoColor=white)
![Languages](https://img.shields.io/github/languages/top/listoCheck/AccMarket?style=for-the-badge&logo=github&logoColor=white)
![Total Commits](https://img.shields.io/github/commit-activity/y/listoCheck/AccMarket?style=for-the-badge&logo=github&logoColor=white)
![Open Issues](https://img.shields.io/github/issues/listoCheck/AccMarket?style=for-the-badge&logo=github&logoColor=white)
![Closed Issues](https://img.shields.io/github/issues-closed/listoCheck/AccMarket?style=for-the-badge&logo=github&logoColor=white)

---

## Стек технологий

### BackEnd

![Spring](https://img.shields.io/badge/Spring-%236DB33F.svg?style=for-the-badge&logo=spring&logoColor=white)
![Kotlin](https://img.shields.io/badge/Kotlin-%237F52FF.svg?style=for-the-badge&logo=kotlin&logoColor=white)
![Hibernate](https://img.shields.io/badge/Hibernate-%23596563.svg?style=for-the-badge&logo=hibernate&logoColor=white)
![JPA](https://img.shields.io/badge/JPA-%23007ACC.svg?style=for-the-badge&logoColor=white)
![Swagger](https://img.shields.io/badge/Swagger-85EA2D?style=for-the-badge&logo=swagger&logoColor=white)
![Custom Banwords Library](https://img.shields.io/badge/BanwordsLibrary-FF6F61?style=for-the-badge&logo=appveyor&logoColor=white)
![Spring Security](https://img.shields.io/badge/Security-6DB33F?style=for-the-badge&logo=spring&logoColor=white)
![Spring Boot](https://img.shields.io/badge/SpringBoot-6DB33F?style=for-the-badge&logo=springboot&logoColor=white)
![GraphQL DGS](https://img.shields.io/badge/GraphQL_DGS-FF4088?style=for-the-badge&logo=graphql&logoColor=white)
![Jackson Kotlin](https://img.shields.io/badge/JacksonKotlin-00557F?style=for-the-badge&logo=jackson&logoColor=white)
![AOP](https://img.shields.io/badge/AOP-00ACC1?style=for-the-badge&logoColor=white)
![Mail](https://img.shields.io/badge/Mail-4285F4?style=for-the-badge&logo=gmail&logoColor=white)
![Dotenv](https://img.shields.io/badge/Dotenv-6F42C1?style=for-the-badge&logo=envato&logoColor=white)

### FrontEnd
![React](https://img.shields.io/badge/React-%2361DAFB.svg?style=for-the-badge&logo=react&logoColor=black)
![TypeScript](https://img.shields.io/badge/TypeScript-%233178C6.svg?style=for-the-badge&logo=typescript&logoColor=white)

### 🗄 Базы данных
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-%234169E1.svg?style=for-the-badge&logo=postgresql&logoColor=white)

### 🐳 Контейнеризация
![Docker](https://img.shields.io/badge/Docker-%232496ED.svg?style=for-the-badge&logo=docker&logoColor=white)

### 🧪 Тестирование
![Spring Boot Test](https://img.shields.io/badge/SpringBootTest-6DB33F?style=for-the-badge&logo=springboot&logoColor=white)
![MockK](https://img.shields.io/badge/MockK-FF4081?style=for-the-badge&logo=mockk&logoColor=white)
![JUnit](https://img.shields.io/badge/JUnit-25A162?style=for-the-badge&logo=junit5&logoColor=white)
![Postman](https://img.shields.io/badge/Postman-FF6C37?style=for-the-badge&logo=postman&logoColor=white)

---

## 📚 Документация проекта

1. **SRS-документ** — [docs1](./docs/part1.md)
2. **ER и другие модели проекта** — [docs2](./docs/part2.md)

---

## Docker

| Script | Command | Description |
|--------|---------|-------------|
| ![Up](https://img.shields.io/badge/⬆_Up-2496ED?style=for-the-badge&logo=docker&logoColor=white) | `docker compose up -d` | Start containers |

---

## 🛠 OpenAPI / Swagger

Документация всех REST API эндпоинтов проекта **AccMarket** доступна через Swagger UI.

- **URL для локального запуска:**  
  [http://localhost:8090/api/v1/swagger-ui/index.html](http://localhost:8090/api/v1/swagger-ui/index.html)

- **Описание:**  
  Swagger UI позволяет просматривать все доступные API, тестировать запросы и видеть модели данных (DTO), возвращаемые сервисами.  
  Каждый эндпоинт содержит описание запроса, тело (Request Body), параметры, заголовки и примеры ответов.

- **Поддерживаемые группы эндпоинтов:**
    1. `auth` — регистрация, вход, выход, обновление токена.
    2. `core` — работа с объявлениями: создание, редактирование, удаление, получение.
    3. `admin` — управление ролями пользователей: назначение админов и модераторов, просмотр ролей, обновление ролей.

- **Примечания:**
    - Для эндпоинтов, требующих аутентификации, необходимо передавать JWT-токен в заголовке `Authorization: Bearer <token>`.
    - Swagger UI полностью интерактивен: можно выполнять запросы прямо из браузера и смотреть ответы сервера.  


---


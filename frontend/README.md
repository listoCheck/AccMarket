# AccMarket Frontend

Фронтенд приложение для биржи игровых аккаунтов AccMarket, построенное на Vue.js 3 + TypeScript + Vite.

## Технологии

- **Vue 3** - прогрессивный JavaScript фреймворк
- **TypeScript** - типизированный JavaScript
- **Vite** - быстрый сборщик и dev-сервер
- **Vue Router** - маршрутизация
- **Pinia** - управление состоянием
- **Axios** - HTTP клиент

## Структура проекта

```
frontend/
├── src/
│   ├── api/              # API клиент и запросы
│   │   └── client.ts     # Axios клиент с interceptors
│   ├── stores/           # Pinia stores
│   │   ├── auth.ts       # Аутентификация
│   │   └── advertisements.ts  # Объявления
│   ├── views/            # Страницы приложения
│   │   ├── HomeView.vue
│   │   ├── LoginView.vue
│   │   ├── RegisterView.vue
│   │   ├── AdvertisementsView.vue
│   │   ├── CreateAdvertisementView.vue
│   │   ├── EditAdvertisementView.vue
│   │   ├── BalanceView.vue
│   │   ├── NotificationsView.vue
│   │   └── ProfileView.vue
│   ├── router/           # Конфигурация маршрутов
│   │   └── index.ts
│   ├── App.vue           # Корневой компонент
│   ├── main.ts           # Точка входа
│   └── style.css         # Глобальные стили
├── index.html
├── package.json
├── tsconfig.json
├── vite.config.ts
└── README.md
```

## Установка и запуск

### Установка зависимостей

```bash
cd frontend
npm install
```

### Запуск dev-сервера

```bash
npm run dev
```

Приложение будет доступно по адресу: http://localhost:3000

### Сборка для production

```bash
npm run build
```

### Preview production сборки

```bash
npm run preview
```

## API Endpoints

Фронтенд взаимодействует с бэкендом через следующие endpoints:

### Аутентификация (`/api/v1/auth`)
- `POST /register` - Регистрация
- `POST /login` - Вход
- `POST /logout` - Выход
- `POST /update-token` - Обновление токена

### Объявления (`/api/v1/core`)
- `GET /` - Получить список объявлений
- `GET /user/:userId` - Получить объявления пользователя
- `POST /make-advertisement` - Создать объявление
- `PATCH /edit-advertisement` - Редактировать объявление
- `DELETE /` - Удалить объявление

### Баланс (`/api/v1/balance`)
- `GET /:userId` - Получить баланс
- `POST /deposit` - Пополнить баланс
- `POST /withdraw` - Вывести средства

### Уведомления (`/api/v1/notifications`)
- `GET /:userId` - Получить все уведомления
- `GET /:userId/unread` - Получить непрочитанные
- `POST /read/:notificationId` - Отметить как прочитанное
- `POST /read-all/:userId` - Отметить все как прочитанные

## Особенности

### Аутентификация
- JWT токены (access + refresh)
- Автоматическое обновление access токена
- Защищенные маршруты
- Сохранение сессии в localStorage

### Управление состоянием
- Pinia stores для auth и advertisements
- Реактивное обновление данных
- Централизованная обработка ошибок

### Маршрутизация
- Защита маршрутов (requiresAuth, requiresGuest)
- Lazy loading компонентов
- Автоматическая навигация при истечении сессии

### UI/UX
- Адаптивный дизайн
- Модальные окна
- Уведомления об успехе/ошибках
- Индикаторы загрузки
- Пагинация списков

## Конфигурация

### Proxy для API
В `vite.config.ts` настроен proxy для перенаправления запросов к бэкенду:

```typescript
server: {
  port: 3000,
  proxy: {
    '/api': {
      target: 'http://localhost:8090',
      changeOrigin: true
    }
  }
}
```

### TypeScript
Строгая типизация включена в `tsconfig.json`:
- strict mode
- noUnusedLocals
- noUnusedParameters
- noFallthroughCasesInSwitch

## Разработка

### Добавление нового view
1. Создайте компонент в `src/views/`
2. Добавьте маршрут в `src/router/index.ts`
3. При необходимости добавьте ссылку в навигацию в `App.vue`

### Добавление нового API endpoint
1. Добавьте метод в `src/api/client.ts`
2. Используйте в компонентах или stores

### Добавление нового store
1. Создайте файл в `src/stores/`
2. Используйте `defineStore` из Pinia
3. Импортируйте и используйте в компонентах

## Troubleshooting

### Ошибки TypeScript
Если видите ошибки о недостающих модулях, убедитесь что установлены все зависимости:
```bash
npm install
```

### Проблемы с CORS
Убедитесь что бэкенд запущен на порту 8090 и настроен для приема запросов с localhost:3000

### Проблемы с аутентификацией
Проверьте что токены сохраняются в localStorage и правильно передаются в заголовках запросов
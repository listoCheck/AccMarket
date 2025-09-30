-- Индексы для пользователей (частая авторизация и поиск)
CREATE INDEX idx_users_login ON USERS(login);
CREATE INDEX idx_users_email ON USERS(email);
CREATE INDEX idx_users_created_at ON USERS(created_at);

-- Индексы для токенов (быстрая проверка токенов)
CREATE INDEX idx_tokens_refresh_token ON TOKENS(refresh_token);
CREATE INDEX idx_tokens_user_id ON TOKENS(user_id);
CREATE INDEX idx_tokens_expire ON TOKENS(refresh_expire);

-- Индексы для баланса и операций (финансовые операции)
CREATE INDEX idx_balance_user_id ON BALANCE(user_id);
CREATE INDEX idx_operations_user_id ON OPERATIONS(user_id);
CREATE INDEX idx_operations_date ON OPERATIONS(date);
CREATE INDEX idx_operations_type_date ON OPERATIONS(type, date);

-- Индексы для объявлений (поиск и фильтрация)
CREATE INDEX idx_ads_status ON ADS(status);
CREATE INDEX idx_ads_user_id ON ADS(user_id);
CREATE INDEX idx_ads_type_price ON ADS(вид, цена) WHERE цена IS NOT NULL;
CREATE INDEX idx_ads_created_at ON ADS(created_at);
CREATE INDEX idx_ads_search ON ADS USING gin(to_tsvector('russian', заголовок || ' ' || текст));

-- Индексы для модерации (работа модераторов)
CREATE INDEX idx_moderation_status ON MODERATION(status);
CREATE INDEX idx_moderation_ad_id ON MODERATION(ad_id);
CREATE INDEX idx_moderation_created_at ON MODERATION(created_at);

-- Индексы для игр (поиск и фильтрация)
CREATE INDEX idx_games_title ON GAMES(title);
CREATE INDEX idx_games_platform ON GAMES(platform);
CREATE INDEX idx_games_genre ON GAMES(genre);
CREATE INDEX idx_games_title_platform ON GAMES(title, platform);

-- Индексы для связей объявлений и игр
CREATE INDEX idx_ad_games_ad_id ON AD_GAMES(ad_id);
CREATE INDEX idx_ad_games_game_id ON AD_GAMES(game_id);

-- Индексы для уведомлений (личный кабинет)
CREATE INDEX idx_notifications_user_id ON NOTIFICATIONS(user_id);
CREATE INDEX idx_notifications_status ON NOTIFICATIONS(status);
CREATE INDEX idx_notifications_date ON NOTIFICATIONS(date);

-- Индексы для обращений (поддержка)
CREATE INDEX idx_appeals_user_id ON APPEALS(user_id);
CREATE INDEX idx_appeals_status ON APPEALS(status);
CREATE INDEX idx_appeals_created_at ON APPEALS(created_at);
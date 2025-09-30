-- Функция для создания нового пользователя
CREATE OR REPLACE FUNCTION create_user(
    p_login VARCHAR(100),
    p_password VARCHAR(255),
    p_email VARCHAR(255)
) RETURNS INTEGER AS $$
DECLARE
    new_user_id INTEGER;
BEGIN
    INSERT INTO USERS (login, password, email) 
    VALUES (p_login, p_password, p_email)
    RETURNING user_id INTO new_user_id;
    
    INSERT INTO USER_ROLES (user_id, role_id) 
    VALUES (new_user_id, 1); -- Роль "пользователь"
    
    RETURN new_user_id;
END;
$$ LANGUAGE plpgsql;

-- Функция для пополнения баланса
CREATE OR REPLACE FUNCTION add_balance(
    p_user_id INTEGER,
    p_amount DECIMAL(15,2),
    p_description VARCHAR(500) DEFAULT NULL
) RETURNS VOID AS $$
BEGIN
    INSERT INTO OPERATIONS (user_id, type, amount, description)
    VALUES (p_user_id, 'пополнение', p_amount, p_description);
END;
$$ LANGUAGE plpgsql;

-- Функция для создания объявления
CREATE OR REPLACE FUNCTION create_ad(
    p_user_id INTEGER,
    p_title VARCHAR(255),
    p_text TEXT,
    p_type VARCHAR(50),
    p_price DECIMAL(15,2) DEFAULT NULL,
    p_game_ids INTEGER[] DEFAULT NULL
) RETURNS INTEGER AS $$
DECLARE
    new_ad_id INTEGER;
    game_id INTEGER;
BEGIN
    INSERT INTO ADS (user_id, заголовок, текст, вид, цена, status)
    VALUES (p_user_id, p_title, p_text, p_type, p_price, 'на модерации')
    RETURNING ad_id INTO new_ad_id;
    
    -- Добавление связей с играми
    IF p_game_ids IS NOT NULL THEN
        FOREACH game_id IN ARRAY p_game_ids
        LOOP
            INSERT INTO AD_GAMES (ad_id, game_id) VALUES (new_ad_id, game_id);
        END LOOP;
    END IF;
    
    RETURN new_ad_id;
END;
$$ LANGUAGE plpgsql;

-- Функция для модерации объявления
CREATE OR REPLACE FUNCTION moderate_ad(
    p_ad_id INTEGER,
    p_moderator_id INTEGER,
    p_status VARCHAR(20),
    p_comment TEXT DEFAULT NULL
) RETURNS VOID AS $$
BEGIN
    UPDATE MODERATION 
    SET status = p_status, 
        moderator_id = p_moderator_id,
        comment = p_comment,
        updated_at = CURRENT_TIMESTAMP
    WHERE ad_id = p_ad_id;
    
    UPDATE ADS 
    SET status = CASE 
        WHEN p_status = 'одобрено' THEN 'активно'
        WHEN p_status = 'отклонено' THEN 'отклонено'
        ELSE status
    END
    WHERE ad_id = p_ad_id;
    
    -- Создание уведомления для пользователя
    INSERT INTO NOTIFICATIONS (user_id, text, type, related_entity_type, related_entity_id)
    SELECT user_id, 
           'Ваше объявление "' || заголовок || '" ' || 
           CASE p_status 
               WHEN 'одобрено' THEN 'одобрено модератором' 
               WHEN 'отклонено' THEN 'отклонено модератором: ' || COALESCE(p_comment, '')
               ELSE 'требует доработки: ' || COALESCE(p_comment, '')
           END,
           'модерация',
           'ad',
           p_ad_id
    FROM ADS WHERE ad_id = p_ad_id;
END;
$$ LANGUAGE plpgsql;

-- Функция для поиска объявлений по критериям
CREATE OR REPLACE FUNCTION search_ads(
    p_search_text VARCHAR(255) DEFAULT NULL,
    p_game_id INTEGER DEFAULT NULL,
    p_ad_type VARCHAR(50) DEFAULT NULL,
    p_min_price DECIMAL(15,2) DEFAULT NULL,
    p_max_price DECIMAL(15,2) DEFAULT NULL,
    p_limit INTEGER DEFAULT 50,
    p_offset INTEGER DEFAULT 0
) RETURNS TABLE(
    ad_id INTEGER,
    заголовок VARCHAR(255),
    текст TEXT,
    вид VARCHAR(50),
    цена DECIMAL(15,2),
    user_login VARCHAR(100),
    game_titles TEXT
) AS $$
BEGIN
    RETURN QUERY
    SELECT 
        a.ad_id,
        a.заголовок,
        a.текст,
        a.вид,
        a.цена,
        u.login as user_login,
        STRING_AGG(g.title, ', ') as game_titles
    FROM ADS a
    JOIN USERS u ON a.user_id = u.user_id
    LEFT JOIN AD_GAMES ag ON a.ad_id = ag.ad_id
    LEFT JOIN GAMES g ON ag.game_id = g.game_id
    WHERE a.status = 'активно'
      AND (p_search_text IS NULL OR 
           a.заголовок ILIKE '%' || p_search_text || '%' OR 
           a.текст ILIKE '%' || p_search_text || '%')
      AND (p_game_id IS NULL OR ag.game_id = p_game_id)
      AND (p_ad_type IS NULL OR a.вид = p_ad_type)
      AND (p_min_price IS NULL OR a.цена >= p_min_price)
      AND (p_max_price IS NULL OR a.цена <= p_max_price)
    GROUP BY a.ad_id, u.login
    ORDER BY a.created_at DESC
    LIMIT p_limit OFFSET p_offset;
END;
$$ LANGUAGE plpgsql;

-- Процедура для обработки покупки
CREATE OR REPLACE FUNCTION process_purchase(
    p_buyer_id INTEGER,
    p_ad_id INTEGER
) RETURNS VOID AS $$
DECLARE
    ad_price DECIMAL(15,2);
    seller_id INTEGER;
BEGIN
    -- Получение информации об объявлении
    SELECT цена, user_id INTO ad_price, seller_id 
    FROM ADS 
    WHERE ad_id = p_ad_id AND status = 'активно' AND вид = 'продажа';
    
    IF NOT FOUND THEN
        RAISE EXCEPTION 'Объявление не найдено или недоступно для покупки';
    END IF;
    
    -- Списание средств у покупателя
    INSERT INTO OPERATIONS (user_id, type, amount, description)
    VALUES (p_buyer_id, 'списание', ad_price, 'Покупка объявления #' || p_ad_id);
    
    -- Зачисление средств продавцу
    INSERT INTO OPERATIONS (user_id, type, amount, description)
    VALUES (seller_id, 'пополнение', ad_price, 'Продажа объявления #' || p_ad_id);
    
    -- Архивирование объявления
    UPDATE ADS SET status = 'архив' WHERE ad_id = p_ad_id;
    
    -- Уведомления для участников сделки
    INSERT INTO NOTIFICATIONS (user_id, text, type) VALUES
    (p_buyer_id, 'Вы успешно приобрели товар по объявлению #' || p_ad_id, 'покупка'),
    (seller_id, 'Ваше объявление #' || p_ad_id || ' успешно продано', 'продажа');
END;
$$ LANGUAGE plpgsql;
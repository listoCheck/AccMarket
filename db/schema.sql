CREATE TABLE ROLES (
    role_id SERIAL PRIMARY KEY,
    role_name VARCHAR(50) NOT NULL UNIQUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE USERS (
    user_id SERIAL PRIMARY KEY,
    login VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    is_active BOOLEAN DEFAULT TRUE,
    CONSTRAINT valid_email CHECK (email ~* '^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\.[A-Za-z]{2,}$')
);

CREATE TABLE USER_ROLES (
    user_id INTEGER REFERENCES USERS(user_id) ON DELETE CASCADE,
    role_id INTEGER REFERENCES ROLES(role_id) ON DELETE CASCADE,
    assigned_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (user_id, role_id)
);

CREATE TABLE TOKENS (
    token_id SERIAL PRIMARY KEY,
    user_id INTEGER NOT NULL REFERENCES USERS(user_id) ON DELETE CASCADE,
    refresh_token TEXT NOT NULL UNIQUE,
    refresh_expire TIMESTAMP NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT token_expiry_check CHECK (refresh_expire > created_at)
);

CREATE TABLE BALANCE (
    balance_id SERIAL PRIMARY KEY,
    user_id INTEGER NOT NULL UNIQUE REFERENCES USERS(user_id) ON DELETE CASCADE,
    protect_token VARCHAR(255),
    protect_expire TIMESTAMP,
    money DECIMAL(15,2) DEFAULT 0.00 CHECK (money >= 0),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE GAMES (
    game_id SERIAL PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    platform VARCHAR(100) NOT NULL,
    genre VARCHAR(100) NOT NULL,
    description TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT unique_game_platform UNIQUE (title, platform)
);

CREATE TABLE ADS (
    ad_id SERIAL PRIMARY KEY,
    user_id INTEGER NOT NULL REFERENCES USERS(user_id) ON DELETE CASCADE,
    заголовок VARCHAR(255) NOT NULL,
    текст TEXT NOT NULL,
    вид VARCHAR(50) NOT NULL CHECK (вид IN ('продажа', 'покупка', 'обмен')),
    цена DECIMAL(15,2) CHECK (цена >= 0),
    status VARCHAR(20) DEFAULT 'черновик' CHECK (status IN ('черновик', 'на модерации', 'активно', 'отклонено', 'архив')),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    expires_at TIMESTAMP,
    CONSTRAINT price_required_for_sale CHECK (
        NOT (вид = 'продажа' AND цена IS NULL)
    )
);

CREATE TABLE AD_GAMES (
    ad_id INTEGER REFERENCES ADS(ad_id) ON DELETE CASCADE,
    game_id INTEGER REFERENCES GAMES(game_id) ON DELETE CASCADE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (ad_id, game_id)
);

CREATE TABLE OPERATIONS (
    operation_id SERIAL PRIMARY KEY,
    user_id INTEGER NOT NULL REFERENCES USERS(user_id) ON DELETE CASCADE,
    type VARCHAR(50) NOT NULL CHECK (type IN ('пополнение', 'списание', 'возврат')),
    amount DECIMAL(15,2) NOT NULL CHECK (amount > 0),
    description VARCHAR(500),
    date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    status VARCHAR(20) DEFAULT 'выполнено' CHECK (status IN ('выполнено', 'ожидание', 'отменено'))
);

CREATE TABLE MODERATION (
    moderation_id SERIAL PRIMARY KEY,
    ad_id INTEGER NOT NULL REFERENCES ADS(ad_id) ON DELETE CASCADE,
    moderator_id INTEGER REFERENCES USERS(user_id) ON DELETE SET NULL,
    status VARCHAR(20) NOT NULL CHECK (status IN ('на проверке', 'одобрено', 'отклонено', 'требует правок')),
    comment TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE NOTIFICATIONS (
    notification_id SERIAL PRIMARY KEY,
    user_id INTEGER NOT NULL REFERENCES USERS(user_id) ON DELETE CASCADE,
    text TEXT NOT NULL,
    status VARCHAR(20) DEFAULT 'непрочитано' CHECK (status IN ('непрочитано', 'прочитано')),
    type VARCHAR(50) NOT NULL,
    date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    related_entity_type VARCHAR(50),
    related_entity_id INTEGER
);

CREATE TABLE APPEALS (
    appeal_id SERIAL PRIMARY KEY,
    user_id INTEGER NOT NULL REFERENCES USERS(user_id) ON DELETE CASCADE,
    reason TEXT NOT NULL,
    status VARCHAR(20) DEFAULT 'открыто' CHECK (status IN ('открыто', 'в работе', 'решено', 'отклонено')),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    moderator_comment TEXT
);
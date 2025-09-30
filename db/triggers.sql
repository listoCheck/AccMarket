-- Триггер для автоматического обновления updated_at
CREATE OR REPLACE FUNCTION update_updated_at_column()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ language 'plpgsql';

CREATE TRIGGER update_users_updated_at BEFORE UPDATE ON USERS FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();
CREATE TRIGGER update_ads_updated_at BEFORE UPDATE ON ADS FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();
CREATE TRIGGER update_balance_updated_at BEFORE UPDATE ON BALANCE FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();
CREATE TRIGGER update_moderation_updated_at BEFORE UPDATE ON MODERATION FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();
CREATE TRIGGER update_appeals_updated_at BEFORE UPDATE ON APPEALS FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

-- Триггер для автоматического создания баланса при регистрации пользователя
CREATE OR REPLACE FUNCTION create_user_balance()
RETURNS TRIGGER AS $$
BEGIN
    INSERT INTO BALANCE (user_id) VALUES (NEW.user_id);
    RETURN NEW;
END;
$$ language 'plpgsql';

CREATE TRIGGER create_balance_after_user_insert 
AFTER INSERT ON USERS 
FOR EACH ROW EXECUTE FUNCTION create_user_balance();

-- Триггер для обновления баланса при операциях
CREATE OR REPLACE FUNCTION update_balance_on_operation()
RETURNS TRIGGER AS $$
BEGIN
    IF NEW.status = 'выполнено' THEN
        IF NEW.type = 'пополнение' OR NEW.type = 'возврат' THEN
            UPDATE BALANCE 
            SET money = money + NEW.amount 
            WHERE user_id = NEW.user_id;
        ELSIF NEW.type = 'списание' THEN
            UPDATE BALANCE 
            SET money = money - NEW.amount 
            WHERE user_id = NEW.user_id;
        END IF;
    END IF;
    RETURN NEW;
END;
$$ language 'plpgsql';

CREATE TRIGGER balance_update_on_operation 
AFTER INSERT ON OPERATIONS 
FOR EACH ROW EXECUTE FUNCTION update_balance_on_operation();

-- Триггер для проверки достаточности средств
CREATE OR REPLACE FUNCTION check_balance_before_operation()
RETURNS TRIGGER AS $$
DECLARE
    current_balance DECIMAL(15,2);
BEGIN
    IF NEW.type = 'списание' THEN
        SELECT money INTO current_balance FROM BALANCE WHERE user_id = NEW.user_id;
        IF current_balance < NEW.amount THEN
            RAISE EXCEPTION 'Недостаточно средств на балансе';
        END IF;
    END IF;
    RETURN NEW;
END;
$$ language 'plpgsql';

CREATE TRIGGER check_balance_before_spending 
BEFORE INSERT ON OPERATIONS 
FOR EACH ROW EXECUTE FUNCTION check_balance_before_operation();

-- Триггер для автоматического создания записи модерации при создании объявления
CREATE OR REPLACE FUNCTION create_moderation_record()
RETURNS TRIGGER AS $$
BEGIN
    IF NEW.status = 'на модерации' THEN
        INSERT INTO MODERATION (ad_id, status) VALUES (NEW.ad_id, 'на проверке');
    END IF;
    RETURN NEW;
END;
$$ language 'plpgsql';

CREATE TRIGGER moderation_on_ad_submit 
AFTER INSERT OR UPDATE ON ADS 
FOR EACH ROW EXECUTE FUNCTION create_moderation_record();
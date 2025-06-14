-- DDL：建立書本資料表
CREATE TABLE book (
    id SERIAL PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    author VARCHAR(100) NOT NULL,
    isbn VARCHAR(13) NOT NULL,
    publish_year INTEGER,
    price DECIMAL(10,2),
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uk_isbn UNIQUE (isbn) 
);

-- DDL：建立索引
-- CREATE INDEX：建立索引，用於優化查詢效能
CREATE INDEX idx_author ON book (author);
CREATE INDEX idx_publish_year ON book (publish_year);

-- 為資料表和欄位添加註解
COMMENT ON TABLE book IS '書本資料表，用於儲存書本的基本資訊';
COMMENT ON COLUMN book.id IS '書本 ID，使用 SERIAL 自動產生遞增值';
COMMENT ON COLUMN book.title IS '書名，必填，最大長度 255 字元';
COMMENT ON COLUMN book.author IS '作者名稱，必填，最大長度 100 字元';
COMMENT ON COLUMN book.isbn IS '國際標準書號，必填，固定 13 位數';
COMMENT ON COLUMN book.publish_year IS '出版年份，可為空';
COMMENT ON COLUMN book.price IS '價格，可為空，最多 10 位數，小數點後 2 位';
COMMENT ON COLUMN book.created_at IS '建立時間，自動填入當前時間';
COMMENT ON COLUMN book.updated_at IS '更新時間，自動填入當前時間';

-- 為索引添加註解
COMMENT ON INDEX idx_author IS '作者索引：加速依作者名稱查詢和排序';
COMMENT ON INDEX idx_publish_year IS '出版年份索引：加速依出版年份查詢和排序';

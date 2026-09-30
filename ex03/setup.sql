-- Chọn database rikkei_erp_db trong pgAdmin rồi chạy file này.
-- Sau đó chạy Main.java để thêm tài khoản và kiểm tra số tài khoản trùng.
-- Khi chạy lại Main.java, tài khoản đã được thêm trước đó sẽ bị từ chối.

CREATE TABLE IF NOT EXISTS bank_accounts (
    account_number VARCHAR(20) PRIMARY KEY,
    account_holder VARCHAR(100) NOT NULL,
    balance NUMERIC(15, 2) NOT NULL DEFAULT 0.00,
    account_type VARCHAR(20) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

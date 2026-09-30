package vn.edu.rikkei.session17.ex03;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Locale;

public class AccountRegistrationService {
    private static final String DB_URL = "jdbc:postgresql://localhost:5432/rikkei_erp_db";
    private static final String USER = "postgres";
    // Thay bằng mật khẩu PostgreSQL trên máy của bạn.
    private static final String PASSWORD = "THAY_BANG_MAT_KHAU_CUA_BAN";

    private static final String CHECK_ACCOUNT_SQL =
            "SELECT 1 FROM bank_accounts WHERE account_number = ?";
    private static final String INSERT_ACCOUNT_SQL =
            "INSERT INTO bank_accounts (account_number, account_holder, balance, account_type) VALUES (?, ?, ?, ?)";

    public boolean registerAccount(String accountNumber, String holderName,
                                   double initialBalance, String accountType) {
        if (accountNumber == null || holderName == null || accountType == null) {
            System.out.println("[TỪ CHỐI] Số tài khoản, tên chủ tài khoản và loại hình không được để trống.");
            return false;
        }

        accountNumber = accountNumber.trim();
        holderName = holderName.trim();
        accountType = accountType.trim();

        if (accountNumber.isEmpty() || accountNumber.length() > 20) {
            System.out.println("[TỪ CHỐI] Số tài khoản phải có từ 1 đến 20 ký tự.");
            return false;
        } else if (holderName.isEmpty() || holderName.length() > 100) {
            System.out.println("[TỪ CHỐI] Tên chủ tài khoản phải có từ 1 đến 100 ký tự.");
            return false;
        } else if (accountType.isEmpty() || accountType.length() > 20) {
            System.out.println("[TỪ CHỐI] Loại hình tài khoản phải có từ 1 đến 20 ký tự.");
            return false;
        } else if (!Double.isFinite(initialBalance) || initialBalance < 0) {
            System.out.println("[TỪ CHỐI] Số dư ban đầu phải là số hữu hạn và không âm.");
            return false;
        }

        DriverManager.setLoginTimeout(5);

        try (Connection connection = DriverManager.getConnection(DB_URL, USER, PASSWORD)) {
            // Dữ liệu người dùng được truyền riêng qua tham số, không ghép vào SQL.
            try (PreparedStatement checkStatement = connection.prepareStatement(CHECK_ACCOUNT_SQL)) {
                checkStatement.setString(1, accountNumber);

                try (ResultSet resultSet = checkStatement.executeQuery()) {
                    if (resultSet.next()) {
                        System.out.printf("[TỪ CHỐI] Số tài khoản %s đã tồn tại trong hệ thống!%n",
                                accountNumber);
                        return false;
                    }
                }
            }

            try (PreparedStatement insertStatement = connection.prepareStatement(INSERT_ACCOUNT_SQL)) {
                insertStatement.setString(1, accountNumber);
                insertStatement.setString(2, holderName);
                insertStatement.setDouble(3, initialBalance);
                insertStatement.setString(4, accountType);

                int affectedRows = insertStatement.executeUpdate();
                if (affectedRows != 1) {
                    System.out.println("[THẤT BẠI] Không thêm được bản ghi tài khoản.");
                    return false;
                }
            }
        } catch (SQLException e) {
            // Khóa chính vẫn ngăn trùng nếu hai yêu cầu cùng vượt qua bước SELECT.
            if ("23505".equals(e.getSQLState())) {
                System.out.printf("[TỪ CHỐI] Số tài khoản %s đã tồn tại trong hệ thống!%n",
                        accountNumber);
            } else {
                System.out.println("[THẤT BẠI] Không thể đăng ký tài khoản.");
                System.out.println("SQLState: " + e.getSQLState());
                System.out.println("Chi tiết: " + e.getMessage());

                if ("28P01".equals(e.getSQLState())) {
                    System.out.println("Kiểm tra USER và PASSWORD trong AccountRegistrationService.");
                } else if ("3D000".equals(e.getSQLState())) {
                    System.out.println("Tạo database rikkei_erp_db hoặc sửa DB_URL cho đúng.");
                } else if ("42P01".equals(e.getSQLState())) {
                    System.out.println("Chạy file setup.sql của bài ex03 trong database rikkei_erp_db trước.");
                } else if (e.getSQLState() != null && e.getSQLState().startsWith("08")) {
                    System.out.println("Kiểm tra JDBC Driver, dịch vụ PostgreSQL, host và port.");
                } else if ("22003".equals(e.getSQLState())) {
                    System.out.println("Số dư vượt giới hạn lưu trữ của cột balance NUMERIC(15, 2).");
                } else {
                    System.out.println("Kiểm tra cấu trúc bảng, quyền truy cập và thông báo lỗi ở trên.");
                }
            }
            return false;
        }

        System.out.println("[THÀNH CÔNG] Đăng ký mở tài khoản ngân hàng thành công!");
        System.out.println("  - Số tài khoản : " + accountNumber);
        System.out.println("  - Chủ tài khoản: " + holderName);
        System.out.printf(Locale.US, "  - Số dư đầu kỳ : %,.2f VND%n", initialBalance);
        System.out.println("  - Loại hình    : " + accountType);
        return true;
    }
}

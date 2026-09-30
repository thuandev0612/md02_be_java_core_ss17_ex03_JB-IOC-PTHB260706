package vn.edu.rikkei.session17.ex03;

public class Main {
    public static void main(String[] args) {
        AccountRegistrationService service = new AccountRegistrationService();

        System.out.println("==========================================================");
        System.out.println("         HỆ THỐNG MỞ TÀI KHOẢN RIKKEI BANK (EX03)");
        System.out.println("==========================================================");

        // Trường hợp 1: Đăng ký tài khoản mới hợp lệ.
        service.registerAccount("10100019283", "NGUYEN VAN QUYET", 5000000.00, "SAVING");

        System.out.println("----------------------------------------------------------");

        // Trường hợp 2: Thử đăng ký lại số tài khoản để kiểm tra trùng lặp.
        service.registerAccount("10100019283", "NGUYEN VAN QUYET", 1000000.00, "SAVING");

        System.out.println("==========================================================");
    }
}

//package edu.cupk.trafficviolationidentificationsystem.util;
//
//import org.springframework.boot.CommandLineRunner;
//import org.springframework.security.crypto.password.PasswordEncoder;
//import org.springframework.stereotype.Component;
//
///**
// * 这是一个一次性的工具类，用于在应用启动时生成并打印密码的BCrypt哈希值。
// * 使用后应将其删除或注释掉。
// */
//@Component
//public class PasswordGenerator implements CommandLineRunner {
//
//    private final PasswordEncoder passwordEncoder;
//
//    public PasswordGenerator(PasswordEncoder passwordEncoder) {
//        this.passwordEncoder = passwordEncoder;
//    }
//
//    @Override
//    public void run(String... args) throws Exception {
//        String rawPassword = "password123";
//        String encodedPassword = passwordEncoder.encode(rawPassword);
//
//        System.out.println("\n\n\n");
//        System.out.println("************************* PASSWORD GENERATOR *************************");
//        System.out.println("这是一个为你生成好的、保证正确的密码哈希值。");
//        System.out.println("请复制下面这行哈希，并用它更新数据库中的 admin 用户密码。");
//        System.out.println("密码 'password123' 的哈希值是: " + encodedPassword);
//        System.out.println("********************************************************************");
//        System.out.println("\n\n\n");
//    }
//}
package edu.cupk.trafficviolationidentificationsystem.config;

import jakarta.annotation.PostConstruct;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JasperReportsConfig {

    @PostConstruct
    public void init() {
        // 这段代码会在 Spring Boot 应用启动时自动执行
        // 它通过 Java 代码实现了和 jasperreports_extension.properties 文件完全相同的配置效果
        // 这种方式比配置文件更明确、更强制，可以绕过类加载器可能带来的问题

//        System.out.println("--- 正在通过Java代码强制配置 JasperReports 字体扩展 ---");

        // 设置字体扩展的注册工厂 (告诉 JasperReports 如何加载字体)
        System.setProperty(
                "net.sf.jasperreports.extension.registry.factory.simple.font.families",
                "net.sf.jasperreports.engine.fonts.SimpleFontExtensionsRegistryFactory"
        );

        // 指定字体定义的XML配置文件名 (告诉 JasperReports 字体信息在哪个文件里)
        // JasperReports 会自动在 classpath 的根目录寻找这个文件
        System.setProperty(
                "net.sf.jasperreports.extension.simple.font.families.xml",
                "fonts.xml"
        );

//        System.out.println("--- JasperReports 字体扩展配置完成 ---");
    }
}
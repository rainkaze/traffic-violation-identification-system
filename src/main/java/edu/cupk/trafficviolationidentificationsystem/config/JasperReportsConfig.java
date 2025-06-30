package edu.cupk.trafficviolationidentificationsystem.config;

import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;

/**
 * JasperReports 报表引擎的全局配置.
 * <p>
 * 该配置类通过 {@link PostConstruct} 注解确保在 Spring 容器初始化bean之后，
 * 通过编程方式设置 JasperReports 的字体扩展属性。
 * 这样做可以避免因类加载器问题或环境差异导致的字体文件加载失败，
 * 尤其是在生成包含中文字符的PDF报表时至关重要。
 * </p>
 */
@Configuration
public class JasperReportsConfig {

    private static final Logger log = LoggerFactory.getLogger(JasperReportsConfig.class);

    /**
     * 初始化 JasperReports 的字体配置.
     * <p>
     * 通过设置系统属性来强制 JasperReports 使用指定的字体扩展注册表和
     * 字体定义XML文件 (fonts.xml)。
     * </p>
     */
    @PostConstruct
    public void init() {
        log.info("--- 正在通过Java代码强制配置 JasperReports 字体扩展 ---");

        // 1. 设置字体扩展的注册工厂 (告诉 JasperReports 如何加载字体)
        //    这指定了使用基于XML的简单字体扩展注册机制。
        final String FONT_REGISTRY_FACTORY_KEY = "net.sf.jasperreports.extension.registry.factory.simple.font.families";
        final String FONT_REGISTRY_FACTORY_VALUE = "net.sf.jasperreports.engine.fonts.SimpleFontExtensionsRegistryFactory";
        System.setProperty(FONT_REGISTRY_FACTORY_KEY, FONT_REGISTRY_FACTORY_VALUE);
        log.debug("设置 JasperReports 属性: {} = {}", FONT_REGISTRY_FACTORY_KEY, FONT_REGISTRY_FACTORY_VALUE);

        // 2. 指定字体定义的XML配置文件名 (告诉 JasperReports 字体信息在哪个文件里)
        //    JasperReports 会自动在 classpath 的根目录寻找这个文件 (例如 src/main/resources/fonts.xml)。
        final String FONT_FAMILIES_XML_KEY = "net.sf.jasperreports.extension.simple.font.families.xml";
        final String FONT_FAMILIES_XML_VALUE = "fonts.xml";
        System.setProperty(FONT_FAMILIES_XML_KEY, FONT_FAMILIES_XML_VALUE);
        log.debug("设置 JasperReports 属性: {} = {}", FONT_FAMILIES_XML_KEY, FONT_FAMILIES_XML_VALUE);

        log.info("--- JasperReports 字体扩展配置完成 ---");
    }
}
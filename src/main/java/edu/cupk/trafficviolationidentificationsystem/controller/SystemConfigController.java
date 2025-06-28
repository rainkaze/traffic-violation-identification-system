package edu.cupk.trafficviolationidentificationsystem.controller;

import edu.cupk.trafficviolationidentificationsystem.entity.SystemConfig;
import edu.cupk.trafficviolationidentificationsystem.repository.SystemConfigMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/system")
public class SystemConfigController {

    @Autowired
    private SystemConfigMapper configMapper;

//    // GET 请求返回固定配置
//    @GetMapping("/config")
//    public SystemConfig getConfig() {
//        SystemConfig config = new SystemConfig();
//        config.setSystemName("城市交通智能执法平台");
//        config.setSessionTimeout(30);
//        config.setDataRetentionDays(365);
//        return config;
//    }
//
//    // PUT 请求打印日志并返回成功
//    @PutMapping("/config")
//    public ResponseEntity<String> updateConfig(@RequestBody SystemConfig config) {
//        System.out.println("收到更新参数: " + config);
//        return ResponseEntity.ok("保存成功");
//    }

    @GetMapping("/config")
    public SystemConfig getConfig() {
        return configMapper.getConfig();
    }

    @PutMapping("/config")
    public ResponseEntity<String> updateConfig(@RequestBody SystemConfig config) {
        config.setId(1L); // 默认只更新 id=1 的配置
        int rows = configMapper.updateConfig(config);
        return rows > 0 ? ResponseEntity.ok("保存成功") : ResponseEntity.status(500).body("更新失败");
    }


}

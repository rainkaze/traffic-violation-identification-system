package edu.cupk.trafficviolationidentificationsystem.controller;

import edu.cupk.trafficviolationidentificationsystem.config.DbConfig;
import edu.cupk.trafficviolationidentificationsystem.util.AliOssUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.*;
import java.net.URL;
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@RestController
@RequestMapping("/api/db")
public class DatabaseController {

    @Autowired
    private AliOssUtil aliOssUtil;

    @Autowired
    private DbConfig dbConfig;

    // 备份数据库并上传 OSS
    @PostMapping("/backup")
    public ResponseEntity<String> backupDatabase() {
        System.out.println("开始备份数据库");

        try {
            String localPath = "D:/backup/backup.sql";
            File backupFile = new File(localPath);
            if (!backupFile.getParentFile().exists()) {
                backupFile.getParentFile().mkdirs();
            }

            Map<String, String> dbInfo = parseDbUrl(dbConfig.getUrl());

            ProcessBuilder pb = new ProcessBuilder(
                    "mysqldump",
                    "-h", dbInfo.get("host"),
                    "-P", dbInfo.get("port"),
                    "-u", dbConfig.getUsername(),
                    "-p" + dbConfig.getPassword(),
                    dbInfo.get("dbName")
            );
            pb.redirectOutput(backupFile);
            Process process = pb.start();
            int exitCode = process.waitFor();

            if (exitCode != 0) {
                return ResponseEntity.status(500).body("备份失败");
            }

            byte[] bytes = Files.readAllBytes(backupFile.toPath());

            LocalDateTime now = LocalDateTime.now();
            String fileName = "backups/backup_" + now.format(DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss")) + ".sql";

            String ossUrl = aliOssUtil.upload(bytes, fileName);

            return ResponseEntity.ok("备份成功，OSS地址：" + ossUrl);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("异常：" + e.getMessage());
        }
    }

    // 恢复数据库
    @PostMapping("/restore")
    public ResponseEntity<String> restoreDatabase(@RequestBody Map<String, String> body) {
        String ossFileUrl = body.get("fileUrl");
        if (ossFileUrl == null || ossFileUrl.isEmpty()) {
            return ResponseEntity.badRequest().body("请提供备份文件 URL");
        }

        try {
            String localPath = "D:/backup/restore_temp.sql";
            File restoreFile = new File(localPath);
            if (!restoreFile.getParentFile().exists()) {
                restoreFile.getParentFile().mkdirs();
            }

            try (InputStream in = new URL(ossFileUrl).openStream();
                 OutputStream out = new FileOutputStream(restoreFile)) {
                byte[] buffer = new byte[4096];
                int len;
                while ((len = in.read(buffer)) != -1) {
                    out.write(buffer, 0, len);
                }
            }

            Map<String, String> dbInfo = parseDbUrl(dbConfig.getUrl());

            ProcessBuilder pb = new ProcessBuilder(
                    "mysql",
                    "-h", dbInfo.get("host"),
                    "-P", dbInfo.get("port"),
                    "-u", dbConfig.getUsername(),
                    "-p" + dbConfig.getPassword(),
                    dbInfo.get("dbName")
            );
            pb.redirectInput(restoreFile);
            Process process = pb.start();

            try (BufferedReader errorReader = new BufferedReader(new InputStreamReader(process.getErrorStream()))) {
                String line;
                while ((line = errorReader.readLine()) != null) {
                    System.err.println(line);
                }
            }

            int exitCode = process.waitFor();
            return exitCode == 0 ?
                    ResponseEntity.ok("数据库恢复成功") :
                    ResponseEntity.status(500).body("恢复失败");

        } catch (Exception e) {
            return ResponseEntity.status(500).body("异常：" + e.getMessage());
        }
    }

    // 列出 OSS 上所有备份文件
    @GetMapping("/listBackups")
    public ResponseEntity<List<String>> listBackups() {
        List<String> backups = aliOssUtil.listBackupFiles("backups/");
        return ResponseEntity.ok(backups);
    }

    // 删除 OSS 中指定备份
    @PostMapping("/deleteBackup")
    public ResponseEntity<String> deleteBackup(@RequestBody Map<String, String> params) {
        String fileUrl = params.get("fileUrl");
        if (fileUrl == null || fileUrl.isEmpty()) {
            return ResponseEntity.badRequest().body("文件路径不能为空");
        }

        String prefix = "https://" + aliOssUtil.getBucketName() + "." + aliOssUtil.getEndpoint() + "/";
        if (!fileUrl.startsWith(prefix)) {
            return ResponseEntity.badRequest().body("文件路径不合法");
        }

        String objectName = fileUrl.substring(prefix.length());

        try {
            aliOssUtil.deleteObject(objectName);
            return ResponseEntity.ok("删除成功");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("删除失败：" + e.getMessage());
        }
    }

    // 解析 spring.datasource.url 中的信息
    private Map<String, String> parseDbUrl(String url) {
        Map<String, String> map = new HashMap<>();
        try {
            String main = url.replace("jdbc:mysql://", "").split("\\?")[0];
            String[] parts = main.split("/");
            String hostPort = parts[0];
            String dbName = parts[1];

            String[] hostPortParts = hostPort.split(":");
            map.put("host", hostPortParts[0]);
            map.put("port", hostPortParts.length > 1 ? hostPortParts[1] : "3306");
            map.put("dbName", dbName);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return map;
    }
}

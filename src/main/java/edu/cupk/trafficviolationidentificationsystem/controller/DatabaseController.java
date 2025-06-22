package edu.cupk.trafficviolationidentificationsystem.controller;

import edu.cupk.trafficviolationidentificationsystem.util.AliOssUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.*;
import java.net.URL;
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

//@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/db")
public class DatabaseController {

    @Autowired
    private AliOssUtil aliOssUtil;

    // 备份数据库 并上传到 OSS
    @PostMapping("/backup")
    public ResponseEntity<String> backupDatabase() {
        System.out.println("开始备份数据库");
        try {
            String localPath = "D:/backup/backup.sql";
            File backupFile = new File(localPath);
            if (!backupFile.getParentFile().exists()) {
                backupFile.getParentFile().mkdirs();
            }

            ProcessBuilder pb = new ProcessBuilder(
                    "mysqldump",
                    "-h", "47.94.105.113",           // 指定远程地址
                    "-P", "3306",
                    "-u", "traffic_user_02",
                    "-pL2tTWWWbjR4zh6F4",
                    "traffic_violation_system_02"
            );
            pb.redirectOutput(backupFile);
            Process process = pb.start();
            int exitCode = process.waitFor();

            if (exitCode != 0) {
                return ResponseEntity.status(500).body("备份失败");
            }

            byte[] bytes = Files.readAllBytes(backupFile.toPath());
//            String ossObjectName = "backups/backup_" + System.currentTimeMillis() + ".sql";
            // 时间格式化文件名
            LocalDateTime now = LocalDateTime.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss");
            String formattedTime = now.format(formatter);
            String ossObjectName = "backups/backup_" + formattedTime + ".sql";

            String ossUrl = aliOssUtil.upload(bytes, ossObjectName);

            return ResponseEntity.ok("备份并上传成功，OSS地址：" + ossUrl);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("异常：" + e.getMessage());
        }
    }

    @GetMapping("/listBackups")
    public ResponseEntity<List<String>> listBackups() {
        System.out.println("开始列出备份文件");
        List<String> backups = aliOssUtil.listBackupFiles("backups/");
        return ResponseEntity.ok(backups);
    }


    @PostMapping("/deleteBackup")
    public ResponseEntity<String> deleteBackup(@RequestBody Map<String, String> params) {

        System.out.println("开始删除备份文件");

        String fileUrl = params.get("fileUrl");
        if (fileUrl == null || fileUrl.isEmpty()) {
            return ResponseEntity.badRequest().body("文件路径不能为空");
        }

        // 从完整 URL 中提取 OSS 内的 objectName，比如去掉 https://bucket.endpoint/ 部分
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



    // 恢复数据库，前端传来 OSS 上的备份文件 URL
    @PostMapping("/restore")
    public ResponseEntity<String> restoreDatabase(@RequestBody Map<String, String> body) {
        String ossFileUrl = body.get("fileUrl");
        if (ossFileUrl == null || ossFileUrl.isEmpty()) {
            return ResponseEntity.badRequest().body("请提供备份文件 URL");
        }

        try {
            // 先下载备份文件到本地
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

            // 执行恢复命令
            ProcessBuilder pb = new ProcessBuilder(
                    "mysql",
                    "-h", "47.94.105.113",           // 指定远程地址
                    "-P", "3306",
                    "-u", "traffic_user_02",
                    "-pL2tTWWWbjR4zh6F4",
                    "traffic_violation_system_02"
            );
            pb.redirectInput(restoreFile);
            Process process = pb.start();

            // 读取错误流方便调试
            try (BufferedReader errorReader = new BufferedReader(new InputStreamReader(process.getErrorStream()))) {
                String line;
                while ((line = errorReader.readLine()) != null) {
                    System.err.println(line);
                }
            }

            int exitCode = process.waitFor();
            if (exitCode == 0) {
                return ResponseEntity.ok("数据库恢复成功");
            } else {
                return ResponseEntity.status(500).body("恢复失败");
            }

        } catch (Exception e) {
            return ResponseEntity.status(500).body("异常：" + e.getMessage());
        }
    }

}

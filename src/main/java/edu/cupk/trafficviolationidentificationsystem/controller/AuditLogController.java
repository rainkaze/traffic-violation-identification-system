package edu.cupk.trafficviolationidentificationsystem.controller;


import edu.cupk.trafficviolationidentificationsystem.dto.AudilVo;
import edu.cupk.trafficviolationidentificationsystem.dto.AuditLog;
import edu.cupk.trafficviolationidentificationsystem.dto.PageResultDto;
import edu.cupk.trafficviolationidentificationsystem.model.User;
import edu.cupk.trafficviolationidentificationsystem.repository.AuditLogMapper;
import edu.cupk.trafficviolationidentificationsystem.repository.UserMapper;
import edu.cupk.trafficviolationidentificationsystem.service.UserService;
import edu.cupk.trafficviolationidentificationsystem.websocket.WebSocketServer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/logs")
public class AuditLogController {

    @Autowired
    AuditLogMapper auditLogMapper;
    @Autowired
    UserMapper userMapper;

    @Autowired
    private WebSocketServer webSocketServer;

    @GetMapping("/page")
    public PageResultDto<AudilVo> listLogs(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String date) {


        List<Integer> a=new ArrayList<>();
        a.add(1);
        a.add(11);
        a.add(8);
        webSocketServer.sendToClientsByInt(a,"6666");
        webSocketServer.sendToAllClient("你好啊");
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String username="";
        if (principal instanceof UserDetails userDetails) {
            System.out.println( userDetails.getUsername());
            username= userDetails.getUsername();
        } else {
            System.out.println(principal.toString());
        }

        Optional<User> byUsername = userMapper.findByUsername(username);
        Integer userId=byUsername.get().getUserId();
        System.out.println(userId);
//        System.out.println(byUsername.toString());



//        int offset = (page - 1) * size;
        // 替换 mockData 为数据库分页查询结果
        List<AudilVo> mockData = auditLogMapper.selectPage1();

//        List<AudilVo> mockData = Arrays.asList(
//                createAuditLog(1L, "admin", "login", "user", "1001", "用户登录系统", "192.168.1.1", LocalDateTime.now()),
//                createAuditLog(2L, "张三", "edit_role", "role", "2", "编辑角色权限：交通执法员", "192.168.1.10", LocalDateTime.now().minusHours(2)),
//                createAuditLog(3L, "李四", "add_user", "user", "1002", "新增用户：王五", "202.55.10.18", LocalDateTime.now().minusDays(1)),
//                createAuditLog(4L, "王五", "delete_rule", "rule", "3", "删除法规条款：超速", "172.16.0.5", LocalDateTime.now().minusDays(2)),
//                createAuditLog(5L, "赵六", "update_setting", "system", null, "修改系统参数：会话超时时间由30分钟改为60分钟", "172.16.0.10", LocalDateTime.now().minusHours(5))
//                ,createAuditLog(6L, "孙七", "add_district", "district", "4", "新增地区：上海", "192.168.1.20", LocalDateTime.now().minusDays(3))
//                ,createAuditLog(7L, "周八", "assign_user", "user", "1003", "分配用户：张三到上海地区", "192.168.1.30", LocalDateTime.now().minusDays(4))
//        );


        // Step 1: 过滤 keyword
        List<AudilVo> filteredList = mockData;

        if (keyword != null && !keyword.trim().isEmpty()) {
            String lowerKeyword = keyword.toLowerCase();
            filteredList = filteredList.stream()
                    .filter(log ->
                            (log.getUsername() != null && log.getUsername().toLowerCase().contains(lowerKeyword)) ||
                                    (log.getActionType() != null && log.getActionType().toLowerCase().contains(lowerKeyword)) ||
                                    (log.getDetails() != null && log.getDetails().toLowerCase().contains(lowerKeyword))
                    )
                    .collect(Collectors.toList());
        }

        // Step 2: 过滤 date（格式为 yyyy-MM-dd）
        if (date != null && !date.trim().isEmpty()) {
            try {
                LocalDate filterDate = LocalDate.parse(date);
                filteredList = filteredList.stream()
                        .filter(log -> log.getCreatedAt().toLocalDate().isEqual(filterDate))
                        .collect(Collectors.toList());
            } catch (Exception e) {
                // 日期格式错误可忽略或抛异常
                System.err.println("日期格式错误：" + date);
            }
        }

        // Step 3: 分页
        int total = filteredList.size();
        int start = Math.min((page - 1) * size, total);
        int end = Math.min(page * size, total);

        List<AudilVo> pagedList = new ArrayList<>();
        for (int i = start; i < end; i++) {
            pagedList.add(filteredList.get(i));
        }

        // Step 4: 构造返回值
        PageResultDto<AudilVo> result = new PageResultDto<>();
        result.setItems(pagedList);
        result.setTotalItems(total);

        return result;


    }

    private AudilVo createAuditLog(Long id, String username, String actionType, String targetType, String targetId, String details, String ip, LocalDateTime time) {
        AudilVo log = new AudilVo();
        log.setLogId(id);
        log.setUsername(username);
        log.setActionType(actionType);
        log.setTargetEntityType(targetType);
        log.setTargetEntityId(targetId);
        log.setDetails(details);
        log.setClientIpAddress(ip);
        log.setCreatedAt(time);
        return log;
    }
}

package edu.cupk.trafficviolationidentificationsystem.controller;

import edu.cupk.trafficviolationidentificationsystem.dto.UserDataDto;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/users")
public class testSettingsController {
    AtomicLong idGenerator = new AtomicLong(10);
    // 修改为存储 UserDataDto 对象的列表
    private static final List<UserDataDto> ALL_USERS = new ArrayList<>();

    static {
        ALL_USERS.add(new UserDataDto(1L, "张三", "zhangsan@example.com", "1234567890", "https://example.com/avatar.jpg", "管理员", "active"));
        ALL_USERS.add(new UserDataDto(2L, "李四", "lisi@example.com", "1234567890", "https://example.com/avatar.jpg", "普通用户", "active"));
        ALL_USERS.add(new UserDataDto(3L, "王五", "wangwu@example.com", "1234567890", "https://example.com/avatar.jpg", "普通用户", "active"));
        ALL_USERS.add(new UserDataDto(4L, "赵六", "zhaoliu@example.com", "1234567890", "https://example.com/avatar.jpg", "普通用户", "active"));
        ALL_USERS.add(new UserDataDto(5L, "孙七", "sunqi@example.com", "1234567890", "https://example.com/avatar.jpg", "普通用户", "active"));
        ALL_USERS.add(new UserDataDto(6L, "周八", "zhouba@example.com", "1234567890", "https://example.com/avatar.jpg", "普通用户", "active"));
        ALL_USERS.add(new UserDataDto(7L, "吴九", "wujue@example.com", "1234567890", "https://example.com/avatar.jpg", "普通用户", "active"));
    }


    //获取用户对象
    @RequestMapping("/getUser")
    public UserDataDto getUser() {
        return new UserDataDto(1L, "张三", "zhangsan@example.com", "1234567890", null, "管理员", "正常");
    }

//    //获取用户集合
//    @RequestMapping("/getUsers")
//    public UserDataDto[] getUsers( ) {
//        return new UserDataDto[]{
//            new UserDataDto(1L, "张三", "zhangsan@example.com", "1234567890", "https://example.com/avatar.jpg", "管理员", "正常"),
//            new UserDataDto(2L, "李四", "lisi@example.com", "1234567890", "https://example.com/avatar.jpg", "普通用户", "正常"),
//            new UserDataDto(3L, "王五", "wangwu@example.com", "1234567890", "https://example.com/avatar.jpg", "普通用户", "禁用")
//
//
//
//        };
//    }



    @RequestMapping("/getUsers")
    public Map<String, Object> getUsers(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(defaultValue = "") String keyword) {

        // 先过滤，忽略大小写匹配姓名或邮箱包含关键词的用户
        List<UserDataDto> filteredUsers = ALL_USERS.stream()
                .filter(user ->
                        user.getName().toLowerCase().contains(keyword.toLowerCase()) ||
                                user.getEmail().toLowerCase().contains(keyword.toLowerCase())
                )
                .collect(Collectors.toList());

        int total = filteredUsers.size();

        int fromIndex = (page - 1) * size;
        int toIndex = Math.min(fromIndex + size, total);

        List<UserDataDto> pageData = new ArrayList<>();

        if (fromIndex < total) {
            pageData = filteredUsers.subList(fromIndex, toIndex);
        }

        Map<String, Object> result = new HashMap<>();
        result.put("total", total);
        result.put("data", pageData);

        System.out.println("page: " + page + ", size: " + size + ", keyword: " + keyword);
        System.out.println("total filtered: " + total + ", pageData: " + pageData);

        return result;
    }










    // 添加用户
    @PostMapping
    public UserDataDto addUser(@RequestBody UserDataDto user) {
        Long id =(long) idGenerator.incrementAndGet();
        user.setId(id);
        System.out.println(user);
        return user;
    }


    // 编辑用户
    @PutMapping("/{id}")
    public UserDataDto updateUser(@PathVariable Long id, @RequestBody UserDataDto updatedUser) {
        updatedUser.setId(id); // 确保 ID 不变
        System.out.println(updatedUser);
        return updatedUser;
    }


}

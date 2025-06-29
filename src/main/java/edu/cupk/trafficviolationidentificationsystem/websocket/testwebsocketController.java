package edu.cupk.trafficviolationidentificationsystem.websocket;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/")
public class testwebsocketController {
    @Autowired
    private WebSocketServer webSocketServer;
    @GetMapping("websocket")
    public String test() {
//        发通知这么写就行
//        List<Integer> a=new ArrayList<>();
//        a.add(8);                 这里是需要发送给的用户ids
//        webSocketServer.sendToClientsByInt(a,"新增条款");
//        我已经在前端调用插入通知表了 后端可以不用写了
        webSocketServer.sendToAllClient("这是后端发给所有前端的消息");
        return "test";
    }

}

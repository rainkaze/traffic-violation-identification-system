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
//        List<Integer> a=new ArrayList<>();
//        a.add(8);
//        webSocketServer.sendToClientsByInt(a,"新增条款");
        webSocketServer.sendToAllClient("这是后端发给所有前端的消息");
        return "test";
    }

}

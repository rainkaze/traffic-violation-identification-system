package edu.cupk.trafficviolationidentificationsystem.task;


import edu.cupk.trafficviolationidentificationsystem.websocket.WebSocketServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * WebSocket 相关计划任务。
 * <p>
 * 此类用于定义通过 WebSocket 向客户端推送消息的定时任务。
 * 注意：当前所有任务均被注释掉，处于非激活状态。
 * </p>
 */
@Component
public class WebSocketTask {

    private static final Logger log = LoggerFactory.getLogger(WebSocketTask.class);

    @Autowired
    private WebSocketServer webSocketServer;

    public WebSocketTask() {
        log.info("WebSocketTask Bean 已创建，但所有计划任务当前均未激活。");
    }

    /**
     * 通过 WebSocket 每隔5秒向所有客户端发送一次心跳或服务器时间消息。
     * <p>
     * Cron 表达式 "0/5 * * * * ?" 表示每5秒执行一次。
     * 当前此功能被注释，处于禁用状态。如果需要启用，请取消注释 {@code @Scheduled} 注解。
     * </p>
     */
    // @Scheduled(cron = "0/5 * * * * ?")
    // public void sendMessageToClient() {
    //     String message = "这是来自服务端的消息：" + DateTimeFormatter.ofPattern("HH:mm:ss").format(LocalDateTime.now());
    //     log.debug("通过 WebSocket 推送消息: {}", message);
    //     webSocketServer.sendToAllClient(message);
    // }
}
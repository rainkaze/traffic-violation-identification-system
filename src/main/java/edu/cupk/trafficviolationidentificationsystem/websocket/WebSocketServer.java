package edu.cupk.trafficviolationidentificationsystem.websocket;

import jakarta.websocket.*;
import jakarta.websocket.server.PathParam;
import jakarta.websocket.server.ServerEndpoint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 【增强版】WebSocket服务
 * 1. 使用ConcurrentHashMap保证线程安全。
 * 2. 添加了详细的日志记录，方便追踪问题。
 * 3. 实现了标准的错误处理。
 */
@Component
@ServerEndpoint("/ws/{sid}")
public class WebSocketServer {

    private static final Logger log = LoggerFactory.getLogger(WebSocketServer.class);

    // 【核心修正1】使用线程安全的 ConcurrentHashMap 来存储会话
    private static final ConcurrentHashMap<String, Session> sessionMap = new ConcurrentHashMap<>();

    /**
     * 获取当前在线连接数。
     * @return 在线连接数
     */
    public int getOnlineCount() {
        return sessionMap.size();
    }

    /**
     * 连接建立成功调用的方法
     */
    @OnOpen
    public void onOpen(Session session, @PathParam("sid") String sid) {
        sessionMap.put(sid, session);
        log.info("[WebSocket] 用户 {} 建立连接, 当前总连接数: {}", sid, getOnlineCount());
    }

    /**
     * 收到客户端消息后调用的方法
     * @param message 客户端发送过来的消息
     */
    @OnMessage
    public void onMessage(String message, @PathParam("sid") String sid) {
        log.info("[WebSocket] 收到来自用户 {} 的消息: {}", sid, message);
    }

    /**
     * 连接关闭调用的方法
     */
    @OnClose
    public void onClose(Session session, @PathParam("sid") String sid) {
        sessionMap.remove(sid);
        log.info("[WebSocket] 用户 {} 连接断开, 当前总连接数: {}", sid, getOnlineCount());
    }

    /**
     * 发生错误时调用
     */
    @OnError
    public void onError(Session session, Throwable error) {
        log.error("[WebSocket] 连接发生错误: " + error.getMessage(), error);
    }

    /**
     * 群发消息
     * @param message 消息内容
     */
    public void sendToAllClient(String message) {
        log.info("[WebSocket] 准备向所有 {} 个客户端群发消息...", getOnlineCount());
        sessionMap.values().forEach(session -> sendMessage(session, message));
    }

    /**
     * 发送给指定的sid的客户端 (增强日志版)
     */
    public void sendToClientsByInt(List<Integer> sids, String message) {
        if (sids == null || sids.isEmpty()) {
            return;
        }
        log.info("[WebSocket] 准备向指定用户列表 {} 发送消息...", sids);
        for (Integer id : sids) {
            String sid = String.valueOf(id);
            Session session = sessionMap.get(sid);

            // 【核心修正2】添加了详细的诊断日志
            if (session != null && session.isOpen()) {
                log.info("  -> 找到用户 {} 的有效连接，正在发送...", sid);
                sendMessage(session, message);
            } else {
//                log.warn("  -> 未找到用户 {} 的有效连接，跳过发送。", sid);
            }
        }
    }

    /**
     * 封装的单个消息发送方法，包含异常处理
     * @param session 会话
     * @param message 消息
     */
    private void sendMessage(Session session, String message) {
        try {
            session.getBasicRemote().sendText(message);
        } catch (IOException e) {
            log.error("[WebSocket] 发送消息给 {} 时发生IO异常", session.getId(), e);
        }
    }
}
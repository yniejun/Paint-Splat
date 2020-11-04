package com.game.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;



@ServerEndpoint("/websocket/{userId}")
@Component
public class WebSocketServer {
    static Logger logger = LoggerFactory.getLogger(WebSocketServer.class);
    private static final AtomicInteger OnlineCount = new AtomicInteger(0);
    //concurrent Set，MyWebSocket object for each client, key matches the user
    private static final ConcurrentHashMap<String, WebSocketServer> webSocketSet = new ConcurrentHashMap<>();
    private Session webSocketSession;
    private String userId = "";

    /**
     * connect
     * */
    @OnOpen
    public void onOpen(@PathParam(value = "userId") String param, Session webSocketSession) {
        userId = param;
        //log.info("authKey:{}",authKey);
        this.webSocketSession = webSocketSession;
        webSocketSet.put(param, this);
        int cnt = OnlineCount.incrementAndGet();
        logger.info("join，connect count: {}", cnt);
        sendMessage(this.webSocketSession, "success");
    }

    /**
     * close
     */
    @OnClose
    public void onClose() {
        if (!userId.equals("")){
            webSocketSet.remove(userId);
            int cnt = OnlineCount.decrementAndGet();
            logger.info("connect close，connect count: {}", cnt);
        }
    }

    /**
     * receive the message from client
     *
     * @param message client message
     */
    @OnMessage
    public void onMessage(String message, Session session) {
        logger.info("receive message：{}",message);
        sendMessage(session, "received message："+message);
    }

    /**
     * error
     * @param session session
     * @param error error
     */
    @OnError
    public void onError(Session session, Throwable error) {
        logger.error("error：{}，Session ID： {}",error.getMessage(),session.getId());
        error.printStackTrace();
    }

    /**
     * sendMessage
     * @param session session
     * @param message message
     */
    public void sendMessage(Session session, String message) {
        try {
            session.getBasicRemote().sendText(String.format("%s (From Server，Session ID=%s)",message,session.getId()));
            //session.getBasicRemote().sendText(String.format("%s",message));
        } catch (IOException e) {
            logger.error("send error：{}", e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * broadCast
     * @param message broadCast message
     */
    public void broadCastInfo(String message) {
        for (String key : webSocketSet.keySet()) {
            Session session = webSocketSet.get(key).webSocketSession;
            if(session != null && session.isOpen() && !userId.equals(key)){
                sendMessage(session, message);
            }
        }
    }

    /**
     * send message to specified user
     * @param message message
     */
    public void sendToUser(String userId, String message) {
        WebSocketServer webSocketServer = webSocketSet.get(userId);
        if ( webSocketServer != null && webSocketServer.webSocketSession.isOpen()){
            sendMessage(webSocketServer.webSocketSession, message);
        }
        else{
            logger.warn("can not connect with specified session：{}",userId);
        }
    }
}


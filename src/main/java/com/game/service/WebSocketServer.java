package com.game.service;

import com.game.pojo.Game;
import com.game.pojo.Message;
import com.game.util.RedisUtil;
import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;



@ServerEndpoint("/websocket/{userId}")
@Component
public class WebSocketServer {
    static Logger logger = LoggerFactory.getLogger(WebSocketServer.class);
    private static final AtomicInteger OnlineCount = new AtomicInteger(0);
    //concurrent Set，MyWebSocket object for each client, key matches the user
    private static final ConcurrentHashMap<String, WebSocketServer> webSocketSet = new ConcurrentHashMap<>();
    private final RedisUtil redisUtil;
    private Session webSocketSession;
    private String userId = "";
    private String userName = "";
    private Integer MaxPlayer = 4;

    public WebSocketServer(RedisUtil redisUtil) {
        this.redisUtil = redisUtil;
    }

    /**
     * connect
     */
    @OnOpen
    public void onOpen(@PathParam(value = "userId") String param1, @PathParam(value = "userName") String param2, Session webSocketSession) {
        userId = param1;
        userName = param2;
        HashMap<String, Object> map = new HashMap();
        this.webSocketSession = webSocketSession;
        webSocketSet.put(param1, this);
        int cnt = OnlineCount.incrementAndGet();
        if (cnt < MaxPlayer) {
            map.put("eventType", "connect");
            map.put("gamerNum", cnt);
            //set gamer map
            redisUtil.hset("userName", userId, userName);
            sendMessage(this.webSocketSession, new Gson().toJson(map));
        } else if (cnt == MaxPlayer) {
            redisUtil.hset("userName", userName, Game.color[cnt]);
            map.put("userName",redisUtil.hmget("userName"));
            map.put("eventType", "gameStart");
            map.put("gamerNum", cnt);
            broadCastInfo(new Gson().toJson(map));
        } else {
            logger.info("join，connect count: {}", cnt);
            map.put("eventType", "close");
            sendMessage(this.webSocketSession, new Gson().toJson(map));
        }
    }

    /**
     * close
     */
    @OnClose
    public void onClose() {
        if (!userId.equals("")) {
            webSocketSet.remove(userId);
            int cnt = OnlineCount.decrementAndGet();
            logger.info("connect close，connect count: {}", cnt);
            if (cnt == 0) {
                redisUtil.del("userName");
            }
        }
    }

    /**
     * receive the message from client
     *
     * @param message client message
     */
    @OnMessage
    public void onMessage(String message, Session session) {
        Gson gson = new Gson();
        Message msg = gson.fromJson(message, Message.class);
        HashMap<String, Object> map = new HashMap();
        // hit message
        if (msg.getEventType().equals("Hit")) {
            boolean isHit = GameService.isHit(msg.getDetail().getLocationX(), msg.getDetail().getLocationY(), msg.getDetail().getSize());
            // if acceptable hit
            if (!isHit) {
                map.put("eventType", "HitNeg");
                sendMessage(session, new Gson().toJson(map));
            } else {
                map.put("eventType", "HitPos");
                map.put("userId", userId);
                map.put("detail", msg.getDetail());
                broadCastInfo(new Gson().toJson(map));
            }
        } else {
            map.put("eventType", "Error");
            sendMessage(session, new Gson().toJson(map));
        }

    }

    /**
     * error
     *
     * @param session session
     * @param error   error
     */
    @OnError
    public void onError(Session session, Throwable error) {
        logger.error("error：{}，Session ID： {}", error.getMessage(), session.getId());
        error.printStackTrace();
    }

    /**
     * sendMessage
     *
     * @param session session
     * @param message message
     */
    public void sendMessage(Session session, String message) {
        try {
            session.getBasicRemote().sendText(String.format("%s (From Server，Session ID=%s)", message, session.getId()));
            //session.getBasicRemote().sendText(String.format("%s",message));
        } catch (IOException e) {
            logger.error("send error：{}", e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * broadCast
     *
     * @param message broadCast message
     */
    public void broadCastInfo(String message) {
        for (String key : webSocketSet.keySet()) {
            Session session = webSocketSet.get(key).webSocketSession;
            if (session != null && session.isOpen()) {
                sendMessage(session, message);
            }
        }
    }

    /**
     * send message to specified user
     *
     * @param message message
     */
    public void sendToUser(String userId, String message) {
        WebSocketServer webSocketServer = webSocketSet.get(userId);
        if (webSocketServer != null && webSocketServer.webSocketSession.isOpen()) {
            sendMessage(webSocketServer.webSocketSession, message);
        } else {
            logger.warn("can not connect with specified session：{}", userId);
        }
    }
}


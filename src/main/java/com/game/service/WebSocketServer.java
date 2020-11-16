package com.game.service;

import com.game.pojo.Message;
import com.game.util.SpringUtils;
import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;
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
    private String userName = "";
    private Integer MaxPlayer = 4;
    RedisTemplate<String, Object> redisTemplate = null;

    /**
     * connect
     * */
    @OnOpen
    public void onOpen(@PathParam(value = "userId") String param, Session webSocketSession) {
        String[] strArr = param.split("-");
        userId = strArr[0];
        userName = strArr[1];
        HashMap<String, Object> map = new HashMap();
        this.webSocketSession = webSocketSession;
        webSocketSet.put(param, this);
        int cnt = OnlineCount.incrementAndGet();
        logger.info("join connect，connect count: {}", cnt);
        logger.info("{} join connect", userName);
        redisTemplate = (RedisTemplate<String, Object>) SpringUtils.getBean("redisTemplate");
        if (cnt < MaxPlayer) {
            map.put("eventType", "connect");
            map.put("gamerNum", cnt);
            //set gamer map
            redisTemplate.opsForHash().put("userName", userId, userName);
            redisTemplate.opsForHash().put("userScore", userName, 0);
            sendMessage(this.webSocketSession, new Gson().toJson(map));
        } else if (cnt == MaxPlayer) {
            redisTemplate.opsForHash().put("userName", userId, userName);
            map.put("userName",redisTemplate.opsForHash().entries("userName"));
            map.put("eventType", "gameStart");
            map.put("gamerNum", cnt);
            gameTimer();
            broadCastInfo(new Gson().toJson(map));
        } else {
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
            redisTemplate.delete(userName);
            int cnt = OnlineCount.decrementAndGet();
            logger.info("connect close，connect count: {}", cnt);
            logger.info("{} close connect", userName);
            if (cnt == 0) {
                gameClean();
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
            boolean isHit = GameService.isHit(msg.getDetail().getLocationX(), msg.getDetail().getLocationY(), msg.getDetail().getSize(), redisTemplate);
            // if acceptable hit
            if (!isHit) {
                map.put("eventType", "HitNeg");
                sendMessage(session, new Gson().toJson(map));
            } else {
                redisTemplate.opsForHash().increment("userScore", userName, 1);
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

    public void gameTimer() {
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            public void run() {
                HashMap<String, Object> map = new HashMap();
                map.put("eventType", "gameOver");
                map.put("score", redisTemplate.opsForHash().entries("userScore"));
                broadCastInfo(new Gson().toJson(map));
                gameClean();
            }
        }, 60000);
    }

    public void gameClean(){
        redisTemplate.delete("userName");
        redisTemplate.delete("userScore");
        redisTemplate.delete("hitRecord");
    }

}


package com.game.controller;

import com.game.service.WebSocketServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


@RestController
public class WebSocketController {
    static Logger logger = LoggerFactory.getLogger(WebSocketController.class);

    @Autowired
    WebSocketServer webSocketServer;

    /**
     * send message
     *
     * @param message
     * @return
     */
    @RequestMapping(value = "/ws/sendAll", method = RequestMethod.GET)
    public String sendAllMessage(@RequestParam(required = true) String message) {
        webSocketServer.broadCastInfo(message);
        return "success";
    }

    /**
     * send to specified user
     *
     * @param message message
     * @param userId      user
     * @return
     */
    @RequestMapping(value = "/ws/sendOne", method = RequestMethod.GET)
    public String sendOneMessage(@RequestParam(required = true) String message,
                                 @RequestParam(required = true) String userId) {
        webSocketServer.sendToUser(userId, message);
        return "success";
    }
}
package com.game.controller;

import com.game.pojo.Game;
import com.game.util.RedisUtil;
import com.google.gson.Gson;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.Map;

@Controller
public class LobbyController {
    private final RedisUtil redisUtil;

    public LobbyController(RedisUtil redisUtil) {
        this.redisUtil = redisUtil;
    }

    @ResponseBody
    @RequestMapping("/lobby")
    public String index() {
        HashMap<String, Object> map = new HashMap();
        Map userName = redisUtil.hmget("userName");
        if (userName!=null && userName.size() >= 4) {
            map.put("roundStatus", 1);
            return new Gson().toJson(map);
        }
        Game game = new Game(redisUtil);
        int roundId = game.getRoundId();
        int gamer = game.getGamer();
        int seed = game.getSeed(redisUtil);
        int color = Game.color[gamer - 1];
        map.put("roundStatus", 0);
        map.put("roundId", roundId);
        map.put("userId", roundId * 10 + gamer);
        map.put("seed", seed);
        map.put("userColor", color);
        //convert holder object to JSONObject directly and return as string as follows
        return new Gson().toJson(map);
    }
}

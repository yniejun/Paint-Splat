package com.game.controller;

import com.game.util.RedisUtil;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;


@Controller
public class IndexController {
    private RedisUtil redisUtil;

    public IndexController(RedisUtil redisUtil) {
        this.redisUtil = redisUtil;
    }

    @ResponseBody
    @RequestMapping("/index")
    public String index(){
        boolean a = redisUtil.hasKey("123");
        if (a){
            return "111";
        }
        return "222";
    }

}

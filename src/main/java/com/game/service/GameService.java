package com.game.service;

import org.springframework.data.redis.core.RedisTemplate;

import java.util.Map;

public class GameService {
    private final static String hitRecord = "hitRecord";

    public static boolean isHit(double x, double y, double R, RedisTemplate<String, Object> redisTemplate) {
        boolean hitExist = redisTemplate.hasKey(hitRecord);
        if (hitExist) {
            Map<Object, Object> hits = redisTemplate.opsForHash().entries(hitRecord);
            for (Map.Entry entry : hits.entrySet()) {
                double xExist = Double.parseDouble((String) entry.getKey());
                double yExist = Double.parseDouble((String) entry.getValue());
                double len = calculateDis(x, y, xExist, yExist);
                if (len < 2 * (R + 1)) {
                    System.out.println("location：" + x + "," + y + "   len:" + len + "   2R:" + 2 * (R + 1));
                    return false;
                }
            }
        }
        redisTemplate.opsForHash().put(hitRecord, String.valueOf(x), String.valueOf(y));
        return true;
    }

    private static double calculateDis(double x, double y, double xExist, double yExist) {
        double a = xExist - x;
        double b = yExist - y;
        return Math.sqrt(Math.pow(a, 2) + Math.pow(b, 2));
    }
}

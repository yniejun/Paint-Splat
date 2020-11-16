package com.game.pojo;

import com.game.util.RedisUtil;

import java.util.Random;

public class Game {
    public final static int[] color = {1, 2, 3, 4};
    private final String game = "game"; // redisKey game redisValue roundId
    private int roundId; // redisKey roundId redisValue gamer num
    private int seed;
    private int gamer;

    public Game(RedisUtil redisUtil) {
        boolean hasGame = redisUtil.hasKey(game);
        if (hasGame) {
            this.roundId = (Integer) redisUtil.get(game);
            this.gamer = redisUtil.get(this.roundId) == null ? 4 : (Integer) redisUtil.get(this.roundId);
            // got the round num
            // search the gamer num
            if (this.gamer < 4) {// don't have 4 gamer
                this.gamer++;
                redisUtil.set(this.roundId, gamer, 2 * 60 * 60);
            } else {
                //open new round
                this.roundId++;
                this.gamer = 1;
                redisUtil.set(game, roundId);
                redisUtil.set(this.roundId, gamer, 2 * 60 * 60);
                setSeed(redisUtil);
            }
        } else {
            //first game round :set round set 1 gamer
            this.roundId = 1;
            redisUtil.set(game, roundId);
            redisUtil.set(roundId, 1, 2 * 60 * 60);
            //set random seed
            setSeed(redisUtil);
        }

    }

    public int getGamer() {
        return gamer;
    }


    public void setSeed(RedisUtil redisUtil) {
        this.seed = new Random().nextInt(1000);
        redisUtil.set("seed", this.seed);
    }

    public int getSeed(RedisUtil redisUtil) {
        return  (Integer) redisUtil.get("seed");
    }

    public int getRoundId() {
        return roundId;
    }

    public String getGame() {
        return game;
    }
}

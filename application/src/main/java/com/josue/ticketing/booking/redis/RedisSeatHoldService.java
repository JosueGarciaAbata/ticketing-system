package com.josue.ticketing.booking.redis;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RedisSeatHoldService {

    private final StringRedisTemplate redisTemplate;
    private final DefaultRedisScript<Long> holdScript;

    public RedisSeatHoldService(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;

        this.holdScript = new DefaultRedisScript<>();
        this.holdScript.setScriptText("""
            for i=1,#KEYS do
                if redis.call("EXISTS", KEYS[i]) == 1 then
                    return 0
                end
            end
            for i=1,#KEYS do
                redis.call("SET", KEYS[i], ARGV[1], "EX", ARGV[2])
            end
            return 1
        """);
        this.holdScript.setResultType(Long.class);
    }

    public boolean holdSeats(Integer showId, List<Integer> seatsId, String bookingPublicId, long ttlSeconds) {
        List<String> keys = seatsId.stream()
                .map(seatId -> keyFor(showId, seatId))
                .toList();

        Long result = redisTemplate.execute(
                holdScript,
                keys,
                bookingPublicId,
                String.valueOf(ttlSeconds)
        );

        return Long.valueOf(1).equals(result);
    }

    public void releaseSeats(Integer showId, List<Integer> seatsId) {
        List<String> keys = seatsId.stream()
                .map(seat -> keyFor(showId, seat))
                .toList();

        redisTemplate.delete(keys);
    }

    public boolean isSeatHeld(Integer showId, Integer seatId) {
        return Boolean.TRUE.equals(redisTemplate.hasKey(keyFor(showId, seatId)));
    }

    private String keyFor(Integer showId, Integer seatId) {
        return "seat:" + showId + ":" + seatId;
    }
}

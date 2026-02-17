package com.josue.ticketing.booking.redis;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Servicio para retención temporal de asientos usando Redis.
 */
@Service
public class RedisSeatHoldService {

    private final StringRedisTemplate redisTemplate;
    private final DefaultRedisScript<Long> holdScript;

    /**
     * Constructor que inicializa el script Lua para bloqueo atómico de asientos.
     * 
     * @param redisTemplate template de Redis para operaciones con strings
     */
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

    /**
     * Intenta retener un conjunto de asientos de forma atómica.
     * 
     * @param showId          ID del show
     * @param seatsId         lista de IDs de asientos a retener
     * @param bookingPublicId ID público de la reserva
     * @param ttlSeconds      tiempo de vida en segundos del bloqueo
     * @return true si todos los asientos fueron retenidos, false si alguno ya
     *         estaba ocupado
     */
    public boolean holdSeats(Integer showId, List<Integer> seatsId, String bookingPublicId, long ttlSeconds) {
        List<String> keys = seatsId.stream()
                .map(seatId -> keyFor(showId, seatId))
                .toList();

        Long result = redisTemplate.execute(
                holdScript,
                keys,
                bookingPublicId,
                String.valueOf(ttlSeconds));

        return Long.valueOf(1).equals(result);
    }

    /**
     * Libera los asientos retenidos en Redis.
     * 
     * @param showId  ID del show
     * @param seatsId lista de IDs de asientos a liberar
     */
    public void releaseSeats(Integer showId, List<Integer> seatsId) {
        List<String> keys = seatsId.stream()
                .map(seat -> keyFor(showId, seat))
                .toList();

        redisTemplate.delete(keys);
    }

    /**
     * Verifica si un asiento específico está retenido.
     * 
     * @param showId ID del show
     * @param seatId ID del asiento
     * @return true si el asiento está retenido
     */
    public boolean isSeatHeld(Integer showId, Integer seatId) {
        return Boolean.TRUE.equals(redisTemplate.hasKey(keyFor(showId, seatId)));
    }

    /**
     * Verifica si ALGUNO de los asientos ya está retenido en Redis.
     * Útil para fail-fast antes de hacer queries costosas a la BD.
     * 
     * @return true si al menos un asiento está retenido, false si todos están
     *         libres
     */
    public boolean areAnySeatsHeld(Integer showId, List<Integer> seatsId) {
        if (seatsId == null || seatsId.isEmpty()) {
            return false;
        }
        List<String> keys = seatsId.stream()
                .map(seatId -> keyFor(showId, seatId))
                .toList();

        // Usar multiGet es más eficiente que múltiples EXISTS
        List<String> values = redisTemplate.opsForValue().multiGet(keys);
        if (values == null) {
            return false;
        }
        return values.stream().anyMatch(v -> v != null);
    }

    /**
     * Genera la clave Redis para un asiento específico.
     * 
     * @param showId ID del show
     * @param seatId ID del asiento
     * @return clave en formato "seat:{showId}:{seatId}"
     */
    private String keyFor(Integer showId, Integer seatId) {
        return "seat:" + showId + ":" + seatId;
    }
}

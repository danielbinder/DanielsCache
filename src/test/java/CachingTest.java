import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class CachingTest {
    private static final int LARGE_PRIME = 479_001_599;

    @Test
    public void testTimeSaving() {
        DanielsCache<Integer, Boolean> cache = new DanielsCache<>(100, this::isPrime);

        for(int i = 10; i < 200; i++) cache.get(i);

        long time = System.currentTimeMillis();
        boolean result = cache.get(LARGE_PRIME);
        long timeComputed = System.currentTimeMillis() - time;
        System.out.println("It took " + timeComputed + "ms to add result " + result);

        time = System.currentTimeMillis();
        result = cache.get(LARGE_PRIME);
        long timeCached = System.currentTimeMillis() - time;
        System.out.println("But only " + timeCached  + "ms to get already computed result " + result);

        assertTrue(timeComputed > timeCached);
    }

    @Test
    public void testMaxCacheSize() throws NoSuchFieldException, IllegalAccessException {
        DanielsCache<Integer, Boolean> cache = new DanielsCache<>(100, this::isPrime);

        for(int i = 100; i < 300; i++) cache.get(i);

        Field actualCache = cache.getClass().getDeclaredField("cache");
        actualCache.setAccessible(true);
        Map<?, ?> cacheMap = (Map<?, ?>) actualCache.get(cache);

        assertEquals(100, cacheMap.size());
    }

    @Test
    public void testFirstRepeatingElements() {
        DanielsCache<Integer, Boolean> cache = new DanielsCache<>(100, this::isPrime);

        cache.get(LARGE_PRIME);
        for(int i = 101; i < 200; i++) cache.get(i);

        long time = System.currentTimeMillis();
        boolean result = cache.get(LARGE_PRIME);
        long timeCached = System.currentTimeMillis() - time;
        System.out.println("It took " + timeCached  + "ms to get already computed result " + result);

        assertTrue(timeCached < 10);
    }

    @Test
    public void testLastRepeatingElements() {
        DanielsCache<Integer, Boolean> cache = new DanielsCache<>(100, this::isPrime);

        for(int i = 100; i < 199; i++) cache.get(i);
        cache.get(LARGE_PRIME);
        for(int i = 100; i < 199; i++) cache.get(i);

        long time = System.currentTimeMillis();
        boolean result = cache.get(LARGE_PRIME);
        long timeCached = System.currentTimeMillis() - time;
        System.out.println("It took " + timeCached  + "ms to get already computed result " + result);

        assertTrue(timeCached < 10);
    }

    boolean isPrime(int n) {
        for(int i = 2; i < n; i++) {
            if(n % i == 0) return false;
        }

        return true;
    }
}

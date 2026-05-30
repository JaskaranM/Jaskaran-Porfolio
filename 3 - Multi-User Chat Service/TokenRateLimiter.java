import java.time.Duration;
import java.time.Instant;

public class TokenRateLimiter {
    private final long capacity;
    private long tokens;
    private final Duration refillPeriod;

    private Instant lastRefill;

    public TokenRateLimiter(long capacity, Duration refillPeriod) {
        this.capacity = capacity;
        this.tokens = capacity;
        this.refillPeriod = refillPeriod;
        this.lastRefill = Instant.now();
    }

    public synchronized boolean isAllowed() {
        refillTokens();

        if (tokens > 0) {
            tokens--;
            return true;
        }

        return false;
    }

    private void refillTokens() {
        Instant now = Instant.now();
        long secondsElapsed = Duration.between(lastRefill, now).toSeconds();
        long refillRate = refillPeriod.toSeconds();

        if (refillRate > 0 && secondsElapsed >= refillRate) {
            long tokensToAdd = secondsElapsed / refillRate;

            lastRefill = lastRefill.plusSeconds(tokensToAdd * refillRate);

            // ensures tokens don't pass capacity
            tokens = Math.min(capacity, tokens + tokensToAdd);
        }
    }
}

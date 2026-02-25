local key = KEYS[1]

local rpm = tonumber(ARGV[1])          -- requests per minute
local now = tonumber(ARGV[2])          -- current time in ms

local capacity = rpm
local refill_rate = rpm / 60000        -- tokens per millisecond

local bucket = redis.call("HMGET", key, "tokens", "last_refill")

local tokens = tonumber(bucket[1])
local last_refill = tonumber(bucket[2])

if tokens == nil then
    tokens = capacity
    last_refill = now
end

-- Calculate time passed
local delta = math.max(0, now - last_refill)

-- Refill tokens
local refill = delta * refill_rate
tokens = math.min(capacity, tokens + refill)

local allowed = 0

if tokens >= 1 then
    tokens = tokens - 1
    allowed = 1
end

redis.call("HMSET", key,
        "tokens", tokens,
        "last_refill", now)

-- Expire after 2 minutes of inactivity
redis.call("PEXPIRE", key, 120000)

return allowed
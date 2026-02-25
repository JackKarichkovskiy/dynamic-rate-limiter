local key = KEYS[1]

local rpm = tonumber(ARGV[1])       -- requests per minute
local now = tonumber(ARGV[2])       -- current time in ms

-- Calculate current window (minute-based)
local window = math.floor(now / 60000)

-- Redis key per window
local window_key = key .. ":" .. window

-- Increment counter atomically
local current = redis.call("INCR", window_key)

-- If first request in window → set TTL to 60s
if current == 1 then
    redis.call("PEXPIRE", window_key, 60000)
end

local allowed = 0

if current <= rpm then
    allowed = 1
end

return allowed
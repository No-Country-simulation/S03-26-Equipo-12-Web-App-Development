CREATE TABLE IF NOT EXISTS analytics_events (
    id             UUID        PRIMARY KEY DEFAULT gen_random_uuid(),
    testimonial_id UUID        NOT NULL,
    event_type     VARCHAR(50) NOT NULL,
    source         VARCHAR(50),
    user_agent     TEXT,
    ip_hash        VARCHAR(64),
    occurred_at    TIMESTAMP   NOT NULL DEFAULT NOW()
);

CREATE INDEX IF NOT EXISTS idx_analytics_testimonial_id ON analytics_events(testimonial_id);
CREATE INDEX IF NOT EXISTS idx_analytics_event_type     ON analytics_events(event_type);
CREATE INDEX IF NOT EXISTS idx_analytics_occurred_at    ON analytics_events(occurred_at DESC);
CREATE INDEX IF NOT EXISTS idx_analytics_source         ON analytics_events(source);

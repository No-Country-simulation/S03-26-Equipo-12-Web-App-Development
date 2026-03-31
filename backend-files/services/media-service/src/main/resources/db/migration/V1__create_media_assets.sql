CREATE TABLE IF NOT EXISTS media_assets (
    id               UUID         PRIMARY KEY DEFAULT gen_random_uuid(),
    testimonial_id   UUID         NOT NULL,
    type             VARCHAR(20)  NOT NULL,
    url              TEXT         NOT NULL,
    cloudinary_id    VARCHAR(255),
    youtube_id       VARCHAR(100),
    thumbnail_url    TEXT,
    created_at       TIMESTAMP    NOT NULL DEFAULT NOW()
);

CREATE INDEX IF NOT EXISTS idx_media_testimonial_id ON media_assets(testimonial_id);
CREATE INDEX IF NOT EXISTS idx_media_type          ON media_assets(type);

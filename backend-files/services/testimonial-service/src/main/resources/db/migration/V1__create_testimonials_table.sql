CREATE TABLE IF NOT EXISTS testimonials (
    id               UUID         PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id          UUID         NOT NULL,
    category_id      UUID,
    title            VARCHAR(500) NOT NULL,
    content          TEXT,
    type             VARCHAR(20)  NOT NULL DEFAULT 'TEXT',
    status           VARCHAR(30)  NOT NULL DEFAULT 'DRAFT',
    author_name      VARCHAR(255),
    author_role      VARCHAR(255),
    author_company   VARCHAR(255),
    author_avatar_url TEXT,
    featured         BOOLEAN      NOT NULL DEFAULT FALSE,
    rating           INTEGER      NOT NULL DEFAULT 0,
    created_at       TIMESTAMP    NOT NULL DEFAULT NOW(),
    updated_at       TIMESTAMP    NOT NULL DEFAULT NOW(),
    published_at     TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_testimonials_status     ON testimonials(status);
CREATE INDEX IF NOT EXISTS idx_testimonials_user_id    ON testimonials(user_id);
CREATE INDEX IF NOT EXISTS idx_testimonials_category   ON testimonials(category_id);
CREATE INDEX IF NOT EXISTS idx_testimonials_featured   ON testimonials(featured);
CREATE INDEX IF NOT EXISTS idx_testimonials_created_at ON testimonials(created_at DESC);

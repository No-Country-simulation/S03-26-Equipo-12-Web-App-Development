CREATE TABLE IF NOT EXISTS embed_configs (
    id              UUID         PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id         UUID         NOT NULL,
    name            VARCHAR(255) NOT NULL,
    category_filter VARCHAR(255),
    tag_filter      VARCHAR(255),
    layout          VARCHAR(50)  NOT NULL DEFAULT 'grid',
    max_items       INTEGER      NOT NULL DEFAULT 6,
    show_rating     BOOLEAN      NOT NULL DEFAULT TRUE,
    show_avatar     BOOLEAN      NOT NULL DEFAULT TRUE,
    active          BOOLEAN      NOT NULL DEFAULT TRUE,
    created_at      TIMESTAMP    NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMP    NOT NULL DEFAULT NOW()
);

CREATE INDEX IF NOT EXISTS idx_embed_configs_user_id ON embed_configs(user_id);
CREATE INDEX IF NOT EXISTS idx_embed_configs_active  ON embed_configs(active);

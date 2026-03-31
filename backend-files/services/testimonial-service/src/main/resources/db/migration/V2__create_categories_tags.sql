CREATE TABLE IF NOT EXISTS categories (
    id          UUID         PRIMARY KEY DEFAULT gen_random_uuid(),
    name        VARCHAR(255) NOT NULL UNIQUE,
    slug        VARCHAR(255) NOT NULL UNIQUE,
    description TEXT,
    created_at  TIMESTAMP    NOT NULL DEFAULT NOW()
);

CREATE TABLE IF NOT EXISTS tags (
    id         UUID         PRIMARY KEY DEFAULT gen_random_uuid(),
    name       VARCHAR(100) NOT NULL UNIQUE,
    slug       VARCHAR(100) NOT NULL UNIQUE,
    created_at TIMESTAMP    NOT NULL DEFAULT NOW()
);

CREATE TABLE IF NOT EXISTS testimonial_tags (
    testimonial_id UUID NOT NULL REFERENCES testimonials(id) ON DELETE CASCADE,
    tag_id         UUID NOT NULL REFERENCES tags(id)         ON DELETE CASCADE,
    PRIMARY KEY (testimonial_id, tag_id)
);

ALTER TABLE testimonials
    ADD CONSTRAINT fk_testimonials_category
    FOREIGN KEY (category_id) REFERENCES categories(id) ON DELETE SET NULL;

-- Seed categories
INSERT INTO categories (name, slug, description) VALUES
    ('Producto',  'producto',  'Testimonios sobre productos'),
    ('Evento',    'evento',    'Testimonios sobre eventos'),
    ('Cliente',   'cliente',   'Testimonios de clientes'),
    ('Industria', 'industria', 'Testimonios por industria')
ON CONFLICT (slug) DO NOTHING;

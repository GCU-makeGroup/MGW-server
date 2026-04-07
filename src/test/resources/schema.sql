CREATE TABLE member (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    email BIGINT NOT NULL,
    name VARCHAR(100) NOT NULL,
    image_url TEXT,
    introduction TEXT,
    point INT,
    created_at TIMESTAMP(6),
    updated_at TIMESTAMP(6),
    deleted_at TIMESTAMP(6)
);

CREATE TABLE `group` (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    title TEXT NOT NULL,
    content TEXT NOT NULL,
    is_public BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP(6),
    updated_at TIMESTAMP(6)
);

CREATE TABLE group_member (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    member_id BIGINT NOT NULL,
    group_id BIGINT NOT NULL
);

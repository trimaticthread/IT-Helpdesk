CREATE TABLE IF NOT EXISTS roles (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    name        VARCHAR(50)  NOT NULL UNIQUE,
    description VARCHAR(255),
    is_active   BOOLEAN DEFAULT TRUE
);

CREATE TABLE IF NOT EXISTS groups_ (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    name        VARCHAR(100) NOT NULL UNIQUE,
    description VARCHAR(255),
    email       VARCHAR(100),
    is_active   BOOLEAN DEFAULT TRUE
);

CREATE TABLE IF NOT EXISTS categories (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    name        VARCHAR(100) NOT NULL UNIQUE,
    description VARCHAR(255),
    is_active   BOOLEAN DEFAULT TRUE
);

CREATE TABLE IF NOT EXISTS users (
    id            BIGINT AUTO_INCREMENT PRIMARY KEY,
    username      VARCHAR(50)  NOT NULL UNIQUE,
    email         VARCHAR(100) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    first_name    VARCHAR(50)  NOT NULL,
    last_name     VARCHAR(50)  NOT NULL,
    phone         VARCHAR(20),
    department    VARCHAR(100),
    is_active     BOOLEAN   DEFAULT TRUE,
    created_at    TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at    TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS tickets (
    id            BIGINT AUTO_INCREMENT PRIMARY KEY,
    ticket_number VARCHAR(20)  NOT NULL UNIQUE,
    title         VARCHAR(255) NOT NULL,
    description   TEXT         NOT NULL,
    status        VARCHAR(20)  DEFAULT 'NEW',
    priority      VARCHAR(20)  DEFAULT 'MEDIUM',
    category_id   BIGINT,
    requester_id  BIGINT NOT NULL,
    assignee_id   BIGINT,
    group_id      BIGINT,
    created_at    TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at    TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    resolved_at   TIMESTAMP NULL,
    closed_at     TIMESTAMP NULL,
    sla_due_date  TIMESTAMP NULL,
    FOREIGN KEY (category_id)  REFERENCES categories(id),
    FOREIGN KEY (requester_id) REFERENCES users(id),
    FOREIGN KEY (assignee_id)  REFERENCES users(id),
    FOREIGN KEY (group_id)     REFERENCES groups_(id)
);

CREATE TABLE IF NOT EXISTS comments (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    ticket_id   BIGINT NOT NULL,
    author_id   BIGINT NOT NULL,
    content     TEXT   NOT NULL,
    is_internal BOOLEAN   DEFAULT FALSE,
    created_at  TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (ticket_id) REFERENCES tickets(id),
    FOREIGN KEY (author_id) REFERENCES users(id)
);

CREATE TABLE IF NOT EXISTS attachments (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    ticket_id   BIGINT        NOT NULL,
    filename    VARCHAR(255)  NOT NULL,
    file_path   VARCHAR(500)  NOT NULL,
    file_size   BIGINT        NOT NULL,
    mime_type   VARCHAR(100)  NOT NULL,
    uploaded_by BIGINT        NOT NULL,
    created_at  TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (ticket_id)   REFERENCES tickets(id),
    FOREIGN KEY (uploaded_by) REFERENCES users(id)
);

CREATE TABLE IF NOT EXISTS user_roles (
    user_id BIGINT NOT NULL,
    role_id BIGINT NOT NULL,
    PRIMARY KEY (user_id, role_id),
    FOREIGN KEY (user_id) REFERENCES users(id),
    FOREIGN KEY (role_id) REFERENCES roles(id)
);

CREATE TABLE IF NOT EXISTS group_users (
    group_id BIGINT NOT NULL,
    user_id  BIGINT NOT NULL,
    PRIMARY KEY (group_id, user_id),
    FOREIGN KEY (group_id) REFERENCES groups_(id),
    FOREIGN KEY (user_id)  REFERENCES users(id)
);

-- Roller
INSERT INTO roles (name, description) VALUES ('ADMIN',      'Sistem yoneticisi');
INSERT INTO roles (name, description) VALUES ('SUPERVISOR', 'Ekip yoneticisi');
INSERT INTO roles (name, description) VALUES ('AGENT',      'Destek personeli');
INSERT INTO roles (name, description) VALUES ('CUSTOMER',   'Son kullanici');

-- Departmanlar
INSERT INTO groups_ (name, description) VALUES ('IT Department',       'Bilgi teknolojileri ekibi');
INSERT INTO groups_ (name, description) VALUES ('Software Department', 'Yazilim gelistirme ekibi');
INSERT INTO groups_ (name, description) VALUES ('Textile Department',  'Tekstil departmani');

-- Kategoriler
INSERT INTO categories (name, description) VALUES ('Network Issue',    'Ag ve baglanti sorunlari');
INSERT INTO categories (name, description) VALUES ('Software Bug',     'Yazilim hatasi');
INSERT INTO categories (name, description) VALUES ('Hardware Failure', 'Donanim arizasi');
INSERT INTO categories (name, description) VALUES ('Access Request',   'Erisim ve yetki talepleri');
INSERT INTO categories (name, description) VALUES ('General Request',  'Genel talepler');

-- Kullanicilar
INSERT INTO users (username, email, password_hash, first_name, last_name, department, is_active)
VALUES ('trimaticthread', 'toprakgulec34@gmail.com', '$2b$10$kbLceP/0P6.X2pZ0euUM2u3RMYK26631ZsgR9ueppP41VJ1WNS/pu', 'Toprak', 'Gulec', 'IT Department', TRUE);

INSERT INTO users (username, email, password_hash, first_name, last_name, department, is_active)
VALUES ('RequemArcade', 'requem@helpdesk.com', '$2b$10$JApIowXeIm06R/o2HXjzuOhK9K9uj8n3AlrLrEFs4fOpn3HEaJsVe', 'Murat', 'Dugan', 'IT Department', TRUE);

INSERT INTO users (username, email, password_hash, first_name, last_name, department, is_active)
VALUES ('sclexx3002', 'sclexx@helpdesk.com', '$2b$10$TJPu0Shb1dzeAlzIMlSLduXnnU69ooWkbDsQGyiu7wgaYWvDgTYeG', 'Kaan', 'Celik', 'Software Department', TRUE);

INSERT INTO users (username, email, password_hash, first_name, last_name, department, is_active)
VALUES ('basibozuk', 'basibozuk@helpdesk.com', '$2b$10$T8ofI7xeilU9chAs/GvCO..RwwKOATLKq.OXt5dNyoeRelJTP5ETu', 'Bozuk', 'Basi', 'Textile Department', TRUE);

-- Rol atamalari
INSERT INTO user_roles (user_id, role_id) VALUES (1, 1); -- trimaticthread → ADMIN
INSERT INTO user_roles (user_id, role_id) VALUES (2, 2); -- RequemArcade   → SUPERVISOR
INSERT INTO user_roles (user_id, role_id) VALUES (3, 3); -- sclexx3002     → AGENT
INSERT INTO user_roles (user_id, role_id) VALUES (4, 4); -- basibozuk      → CUSTOMER

-- Grup atamalari
INSERT INTO group_users (group_id, user_id) VALUES (1, 1); -- trimaticthread → IT Department
INSERT INTO group_users (group_id, user_id) VALUES (1, 2); -- RequemArcade   → IT Department
INSERT INTO group_users (group_id, user_id) VALUES (2, 3); -- sclexx3002     → Software Department
INSERT INTO group_users (group_id, user_id) VALUES (3, 4); -- basibozuk      → Textile Department

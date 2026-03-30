CREATE TABLE IF NOT EXISTS roles (
                       id BIGINT AUTO_INCREMENT PRIMARY KEY,
                       name VARCHAR(50) NOT NULL UNIQUE,
                       description VARCHAR(255),
                       is_active BOOLEAN DEFAULT TRUE
);

CREATE TABLE IF NOT EXISTS groups_ (
                         id BIGINT AUTO_INCREMENT PRIMARY KEY,
                         name VARCHAR(100) NOT NULL UNIQUE,
                         description VARCHAR(255),
                         email VARCHAR(100),
                         is_active BOOLEAN DEFAULT TRUE
);

CREATE TABLE IF NOT EXISTS categories (
                            id BIGINT AUTO_INCREMENT PRIMARY KEY,
                            name VARCHAR(100) NOT NULL UNIQUE,
                            description VARCHAR(255),
                            is_active BOOLEAN DEFAULT TRUE
);

CREATE TABLE IF NOT EXISTS users (
                       id BIGINT AUTO_INCREMENT PRIMARY KEY,
                       username VARCHAR(50) NOT NULL UNIQUE,
                       email VARCHAR(100) NOT NULL UNIQUE,
                       password_hash VARCHAR(255) NOT NULL,
                       first_name VARCHAR(50) NOT NULL,
                       last_name VARCHAR(50) NOT NULL,
                       phone VARCHAR(20),
                       department VARCHAR(100),
                       is_active BOOLEAN DEFAULT TRUE,
                       created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                       updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS tickets (
                         id BIGINT AUTO_INCREMENT PRIMARY KEY,
                         ticket_number VARCHAR(20) NOT NULL UNIQUE,
                         title VARCHAR(255) NOT NULL,
                         description TEXT NOT NULL,
                         status VARCHAR(20) DEFAULT 'NEW',
                         priority VARCHAR(20) DEFAULT 'MEDIUM',
                         category_id BIGINT,
                         requester_id BIGINT NOT NULL,
                         assignee_id BIGINT,
                         group_id BIGINT,
                         created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                         updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                         resolved_at TIMESTAMP NULL,
                         closed_at TIMESTAMP NULL,
                         sla_due_date TIMESTAMP NULL
);

CREATE TABLE IF NOT EXISTS comments (
                          id BIGINT AUTO_INCREMENT PRIMARY KEY,
                          ticket_id BIGINT NOT NULL,
                          author_id BIGINT NOT NULL,
                          content TEXT NOT NULL,
                          is_internal BOOLEAN DEFAULT FALSE,
                          created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS attachments (
                             id BIGINT AUTO_INCREMENT PRIMARY KEY,
                             ticket_id BIGINT NOT NULL,
                             filename VARCHAR(255) NOT NULL,
                             file_path VARCHAR(500) NOT NULL,
                             file_size BIGINT NOT NULL,
                             mime_type VARCHAR(100) NOT NULL,
                             uploaded_by BIGINT NOT NULL,
                             created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS user_roles (
                            user_id BIGINT NOT NULL,
                            role_id BIGINT NOT NULL,
                            PRIMARY KEY (user_id, role_id)
);

CREATE TABLE IF NOT EXISTS group_users (
                             group_id BIGINT NOT NULL,
                             user_id BIGINT NOT NULL,
                             PRIMARY KEY (group_id, user_id)
);
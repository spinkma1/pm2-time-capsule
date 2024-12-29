-- Enum Types
CREATE TYPE role AS ENUM ('ROLE_REGISTERED', 'ROLE_ADMIN', 'ROLE_PREMIUM', 'ROLE_BANNED', 'ROLE_DELETED');
CREATE TYPE state AS ENUM ('EDIT', 'WAIT', 'OPEN');
CREATE TYPE type AS ENUM ('PRIVATE', 'PUBLIC');
CREATE TYPE notification_type AS ENUM ('NEW_ACCESS', 'REMINDER', 'OPENED');

-- Tables
CREATE TABLE T_USER (
                        id BIGSERIAL PRIMARY KEY,
                        google_id VARCHAR(255) UNIQUE,
                        email VARCHAR(255) NOT NULL UNIQUE,
                        password VARCHAR(255),
                        role role DEFAULT 'ROLE_REGISTERED',
                        UNIQUE(email)
);

CREATE TABLE IF NOT EXISTS T_CAPSULE (
                                         id BIGSERIAL PRIMARY KEY,
                                         user_id BIGINT NOT NULL,
                                         capsule_size DOUBLE PRECISION,
                                         name VARCHAR(255) NOT NULL UNIQUE,
                                         description TEXT NOT NULL,
                                         state state,
                                         type type,
                                         team_work BOOLEAN DEFAULT false,
                                         unlock_time TIMESTAMP,
                                         qr_code_password VARCHAR(255),
                                         unlock_lat DOUBLE PRECISION,
                                         unlock_longit DOUBLE PRECISION,
                                         FOREIGN KEY (user_id) REFERENCES T_USER(id)
);

CREATE TABLE T_CONTENT (
                           id BIGSERIAL PRIMARY KEY,
                           data_type VARCHAR(255),
                           date_of_upload TIMESTAMP,
                           data BYTEA,
                           name VARCHAR(255),
                           url VARCHAR(255),
                           capsule_id BIGINT,
                           FOREIGN KEY (capsule_id) REFERENCES T_CAPSULE(id)
);

CREATE TABLE T_NOTIFICATION (
                                id BIGSERIAL PRIMARY KEY,
                                content TEXT,
                                date_of_creation TIMESTAMP,
                                notification_type notification_type,
                                capsule_id BIGINT,
                                FOREIGN KEY (capsule_id) REFERENCES T_CAPSULE(id)
);

-- Many-to-Many Relationships
CREATE TABLE capsule_user (
                              capsule_id BIGINT,
                              user_id BIGINT,
                              PRIMARY KEY (capsule_id, user_id),
                              FOREIGN KEY (capsule_id) REFERENCES T_CAPSULE(id),
                              FOREIGN KEY (user_id) REFERENCES T_USER(id)
);

CREATE TABLE notification_user (
                                   notification_id BIGINT,
                                   user_id BIGINT,
                                   PRIMARY KEY (notification_id, user_id),
                                   FOREIGN KEY (notification_id) REFERENCES T_NOTIFICATION(id),
                                   FOREIGN KEY (user_id) REFERENCES T_USER(id)
);

CREATE TABLE user_followers (
                                user_id BIGINT,
                                follower_id BIGINT,
                                PRIMARY KEY (user_id, follower_id),
                                FOREIGN KEY (user_id) REFERENCES T_USER(id),
                                FOREIGN KEY (follower_id) REFERENCES T_USER(id)
);
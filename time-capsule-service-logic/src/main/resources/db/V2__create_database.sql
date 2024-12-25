-- Enum Types
CREATE TYPE role AS ENUM ('REGISTERED', 'ADMIN', 'PREMIUM', 'BANNED', 'DELETED');
CREATE TYPE state AS ENUM ('EDIT', 'WAIT', 'OPEN');
CREATE TYPE type AS ENUM ('PRIVATE', 'PUBLIC');
CREATE TYPE notification_type AS ENUM ('NEW_ACCESS', 'REMINDER', 'OPENED');

-- Tables
CREATE TABLE T_USER (
                        id SERIAL PRIMARY KEY,
                        google_id VARCHAR(255) UNIQUE,
                        email VARCHAR(255) NOT NULL UNIQUE,
                        password VARCHAR(255),
                        role role DEFAULT 'REGISTERED',
                        UNIQUE(email)
);

CREATE TABLE T_CAPSULE (
                           id SERIAL PRIMARY KEY,
                           user_id INTEGER NOT NULL,
                           capsule_size DOUBLE PRECISION,
                           name VARCHAR(255) NOT NULL UNIQUE,
                           description TEXT NOT NULL,
                           state state,
                           type type,
                           FOREIGN KEY (user_id) REFERENCES T_USER(id)
);

CREATE TABLE T_CONTENT (
                           id SERIAL PRIMARY KEY,
                           data_type VARCHAR(255),  -- Assuming DataType is stored as a VARCHAR, modify if enum
                           date_of_upload TIMESTAMP,
                           data BYTEA,
                           name VARCHAR(255),
                           url VARCHAR(255),
                           capsule_id INTEGER,
                           FOREIGN KEY (capsule_id) REFERENCES T_CAPSULE(id)
);

CREATE TABLE T_NOTIFICATION (
                                id SERIAL PRIMARY KEY,
                                content TEXT,
                                date_of_creation TIMESTAMP,
                                notification_type notification_type,
                                capsule_id INTEGER,
                                FOREIGN KEY (capsule_id) REFERENCES T_CAPSULE(id)
);

-- Many-to-Many Relationships
CREATE TABLE capsule_user (
                              capsule_id INTEGER,
                              user_id INTEGER,
                              PRIMARY KEY (capsule_id, user_id),
                              FOREIGN KEY (capsule_id) REFERENCES T_CAPSULE(id),
                              FOREIGN KEY (user_id) REFERENCES T_USER(id)
);

CREATE TABLE notification_user (
                                   notification_id INTEGER,
                                   user_id INTEGER,
                                   PRIMARY KEY (notification_id, user_id),
                                   FOREIGN KEY (notification_id) REFERENCES T_NOTIFICATION(id),
                                   FOREIGN KEY (user_id) REFERENCES T_USER(id)
);

CREATE TABLE user_followers (
                                user_id INTEGER,
                                follower_id INTEGER,
                                PRIMARY KEY (user_id, follower_id),
                                FOREIGN KEY (user_id) REFERENCES T_USER(id),
                                FOREIGN KEY (follower_id) REFERENCES T_USER(id)
);

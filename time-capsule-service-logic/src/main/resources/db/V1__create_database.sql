-- User Table
CREATE TABLE T_USER (
                        id BIGINT PRIMARY KEY,
                        email VARCHAR(255) NOT NULL UNIQUE,
                        password VARCHAR(255) NOT NULL,
                        role VARCHAR(50),
                        created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                        updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- Capsule Table
CREATE TABLE T_CAPSULE (
                           id BIGINT PRIMARY KEY,
                           user_id BIGINT NOT NULL,
                           capsule_size DOUBLE,
                           name VARCHAR(255) NOT NULL UNIQUE,
                           description TEXT NOT NULL,
                           state VARCHAR(50),
                           type VARCHAR(50),
                           created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                           updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                           FOREIGN KEY (user_id) REFERENCES T_USER(id)
);

-- Notification Table
CREATE TABLE T_NOTIFICATION (
                                id BIGINT PRIMARY KEY,
                                content TEXT,
                                date_of_creation TIMESTAMP,
                                notification_type VARCHAR(50),
                                capsule_id BIGINT,
                                created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                                updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                                FOREIGN KEY (capsule_id) REFERENCES T_CAPSULE(id)
);

-- Content Table
CREATE TABLE T_CONTENT (
                           id BIGINT PRIMARY KEY,
                           data_type VARCHAR(50),
                           date_of_upload TIMESTAMP,
--                            data BLOB,
                           name VARCHAR(255),
                           url VARCHAR(255),
                           capsule_id BIGINT,
                           created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                           updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                           FOREIGN KEY (capsule_id) REFERENCES T_CAPSULE(id)
);

-- Many-to-Many Table for Capsule and User
CREATE TABLE capsule_user (
                              capsule_id BIGINT,
                              user_id BIGINT,
                              PRIMARY KEY (capsule_id, user_id),
                              FOREIGN KEY (capsule_id) REFERENCES T_CAPSULE(id),
                              FOREIGN KEY (user_id) REFERENCES T_USER(id)
);

-- Many-to-Many Table for Notification and User
CREATE TABLE notification_user (
                                   notification_id BIGINT,
                                   user_id BIGINT,
                                   PRIMARY KEY (notification_id, user_id),
                                   FOREIGN KEY (notification_id) REFERENCES T_NOTIFICATION(id),
                                   FOREIGN KEY (user_id) REFERENCES T_USER(id)
);

-- Self-referencing Many-to-Many Table for User Followers
CREATE TABLE user_followers (
                                user_id BIGINT,
                                follower_id BIGINT,
                                PRIMARY KEY (user_id, follower_id),
                                FOREIGN KEY (user_id) REFERENCES T_USER(id),
                                FOREIGN KEY (follower_id) REFERENCES T_USER(id)
);

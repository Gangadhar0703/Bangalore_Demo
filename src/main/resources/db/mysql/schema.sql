CREATE DATABASE IF NOT EXISTS accessibility_mapper
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;
USE accessibility_mapper;

CREATE TABLE IF NOT EXISTS users (
    id BIGINT NOT NULL AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(160) NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    role VARCHAR(20) NOT NULL DEFAULT 'USER',
    PRIMARY KEY (id),
    UNIQUE KEY uk_users_email (email)
);

CREATE TABLE IF NOT EXISTS facilities (
    id BIGINT NOT NULL AUTO_INCREMENT,
    name VARCHAR(140) NOT NULL,
    category VARCHAR(60) NOT NULL,
    address VARCHAR(240) NOT NULL,
    latitude DOUBLE NOT NULL,
    longitude DOUBLE NOT NULL,
    wheelchair_ramp BOOLEAN NOT NULL DEFAULT FALSE,
    working_elevator BOOLEAN NOT NULL DEFAULT FALSE,
    accessible_washroom BOOLEAN NOT NULL DEFAULT FALSE,
    tactile_paving BOOLEAN NOT NULL DEFAULT FALSE,
    wide_doorways BOOLEAN NOT NULL DEFAULT FALSE,
    is_verified BOOLEAN NOT NULL DEFAULT FALSE,
    is_approved BOOLEAN NOT NULL DEFAULT FALSE,
    cover_image_url VARCHAR(500),
    last_accessibility_check DATETIME,
    wheelchair_ramp_checked_at DATETIME,
    working_elevator_checked_at DATETIME,
    accessible_washroom_checked_at DATETIME,
    tactile_paving_checked_at DATETIME,
    wide_doorways_checked_at DATETIME,
    PRIMARY KEY (id),
    KEY idx_facilities_approved_category (is_approved, category),
    CONSTRAINT chk_facilities_category CHECK (category IN ('Parks', 'Theatres', 'Malls'))
);

CREATE TABLE IF NOT EXISTS reviews (
    id BIGINT NOT NULL AUTO_INCREMENT,
    facility_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    rating INT NOT NULL,
    comments VARCHAR(2000) NOT NULL,
    photo_url VARCHAR(500),
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    is_approved BOOLEAN NOT NULL DEFAULT FALSE,
    report_wheelchair_ramp BOOLEAN,
    report_working_elevator BOOLEAN,
    report_accessible_washroom BOOLEAN,
    report_tactile_paving BOOLEAN,
    report_wide_doorways BOOLEAN,
    PRIMARY KEY (id),
    KEY idx_reviews_facility_approved (facility_id, is_approved),
    CONSTRAINT fk_reviews_facility FOREIGN KEY (facility_id) REFERENCES facilities (id),
    CONSTRAINT fk_reviews_user FOREIGN KEY (user_id) REFERENCES users (id),
    CONSTRAINT chk_reviews_rating CHECK (rating BETWEEN 1 AND 5)
);

-- ============================================================
-- V1: Baseline schema
-- Represents the tables that Hibernate auto-created previously.
-- Flyway will apply this on a fresh database; existing deployments
-- use baseline-on-migrate=true so Flyway skips V1 and starts from V2.
-- ============================================================

CREATE TABLE IF NOT EXISTS users (
    id       BIGSERIAL    PRIMARY KEY,
    username VARCHAR(50)  NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    role     VARCHAR(20)  NOT NULL,
    enabled  BOOLEAN      NOT NULL DEFAULT TRUE
);

CREATE TABLE IF NOT EXISTS san_pham (
    id         BIGSERIAL     PRIMARY KEY,
    ten        VARCHAR(255),
    mo_ta      VARCHAR(500),
    short_text VARCHAR(500),
    hinh_anh   VARCHAR(255),
    danh_muc   VARCHAR(100),
    brand      VARCHAR(100),
    link       VARCHAR(500),
    gia        DOUBLE PRECISION,
    range_id   VARCHAR(50),
    applications TEXT,
    features     TEXT,
    specs        TEXT
);

CREATE TABLE IF NOT EXISTS yeu_cau (
    id               BIGSERIAL     PRIMARY KEY,
    ho_ten           VARCHAR(255),
    so_dien_thoai    VARCHAR(20),
    email            VARCHAR(255),
    san_pham_quan_tam TEXT,
    noi_dung         VARCHAR(1000),
    thoi_gian        TIMESTAMP
);

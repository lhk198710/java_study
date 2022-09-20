-- 사용자 생성
-- CREATE USER test_user@localhost
    -- IDENTIFIED BY 'admin';

-- DB 권한 부여
GRANT ALL PRIVILEGES
    ON TEST_DB.*
    TO test_user@localhost;

-- MEMBER 테이블 생성
CREATE TABLE IF NOT EXISTS TEST_DB.MEMBER (
                                              PID BIGINT NOT NULL AUTO_INCREMENT,
                                              USERNAME VARCHAR(200),
    NAME VARCHAR(200),
    PRIMARY KEY(PID)
    );

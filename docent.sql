-- --------------------------------------------------------
-- 호스트:                          pjtc-dev-db-1.cluster-ckvpjgxfe2c4.ap-northeast-2.rds.amazonaws.com
-- 서버 버전:                        5.7.12 - MySQL Community Server (GPL)
-- 서버 OS:                        Linux
-- HeidiSQL 버전:                  12.0.0.6468
-- --------------------------------------------------------

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET NAMES utf8 */;
/*!50503 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;


-- docent 데이터베이스 구조 내보내기
CREATE DATABASE IF NOT EXISTS `docent` /*!40100 DEFAULT CHARACTER SET utf8mb4 */;
USE `docent`;

-- 테이블 docent.docents 구조 내보내기
CREATE TABLE IF NOT EXISTS `docents` (
  `docent_id` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT '회원 아이디',
  `nickname` varchar(20) NOT NULL COMMENT '닉네임',
  `password` varchar(100) NOT NULL COMMENT '비밀번호',
  `email` varchar(50) NOT NULL COMMENT '이메일',
  `mobile` varchar(50) NOT NULL COMMENT '휴대전화',
  `address` varchar(200) DEFAULT NULL COMMENT '주소',
  `zipcode` varchar(6) DEFAULT NULL COMMENT '우편번호',
  `thumbnail` varchar(200) NOT NULL COMMENT '썸네일',
  `birthday` date DEFAULT NULL COMMENT '생년월일',
  `job` varchar(100) NOT NULL COMMENT '직업',
  `like` int(10) unsigned NOT NULL COMMENT '좋아요',
  `follower` int(10) unsigned NOT NULL COMMENT '팔로워',
  `status` smallint(5) unsigned NOT NULL COMMENT '상태',
  `created` datetime NOT NULL COMMENT '생성 일시',
  `updated` datetime DEFAULT NULL COMMENT '수정 일시',
  PRIMARY KEY (`docent_id`),
  UNIQUE KEY `udx_nickname` (`nickname`),
  UNIQUE KEY `udx_email` (`email`),
  UNIQUE KEY `udx_mobile` (`mobile`),
  KEY `idx_status_1` (`status`,`created`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COMMENT='회원';

-- 내보낼 데이터가 선택되어 있지 않습니다.

-- 테이블 docent.docent_followers 구조 내보내기
CREATE TABLE IF NOT EXISTS `docent_followers` (
  `docent_follow_id` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT '도슨트 팔로우 id',
  `docent_id` bigint(20) unsigned NOT NULL COMMENT '도슨트 id',
  `member_id` bigint(20) unsigned NOT NULL COMMENT '회원 id',
  `created` datetime NOT NULL COMMENT '팔로우 일시',
  PRIMARY KEY (`docent_follow_id`),
  UNIQUE KEY `udx_docent_member` (`docent_id`,`member_id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4;

-- 내보낼 데이터가 선택되어 있지 않습니다.

-- 테이블 docent.docent_likes 구조 내보내기
CREATE TABLE IF NOT EXISTS `docent_likes` (
  `docent_like_id` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT '도슨트 좋아요 id',
  `docent_id` bigint(20) unsigned NOT NULL COMMENT '도슨트 id',
  `member_id` bigint(20) unsigned NOT NULL COMMENT '회원 id',
  `created` datetime NOT NULL COMMENT '좋아요 일시',
  PRIMARY KEY (`docent_like_id`),
  UNIQUE KEY `udx_docent_member` (`docent_id`,`member_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 내보낼 데이터가 선택되어 있지 않습니다.

/*!40103 SET TIME_ZONE=IFNULL(@OLD_TIME_ZONE, 'system') */;
/*!40101 SET SQL_MODE=IFNULL(@OLD_SQL_MODE, '') */;
/*!40014 SET FOREIGN_KEY_CHECKS=IFNULL(@OLD_FOREIGN_KEY_CHECKS, 1) */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40111 SET SQL_NOTES=IFNULL(@OLD_SQL_NOTES, 1) */;

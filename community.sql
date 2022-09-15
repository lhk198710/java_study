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


-- community 데이터베이스 구조 내보내기
CREATE DATABASE IF NOT EXISTS `community` /*!40100 DEFAULT CHARACTER SET latin1 */;
USE `community`;

-- 테이블 community.boards 구조 내보내기
CREATE TABLE IF NOT EXISTS `boards` (
  `board_id` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT '게시판 id',
  `board_code` varchar(50) NOT NULL COMMENT '게시판 코드',
  `name` varchar(50) NOT NULL COMMENT '게시판 이름',
  `status` smallint(5) unsigned NOT NULL COMMENT '상태',
  `created` datetime NOT NULL COMMENT '생성일시',
  PRIMARY KEY (`board_id`),
  UNIQUE KEY `udx_ref` (`board_code`,`status`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8 COMMENT='게시판';

-- 내보낼 데이터가 선택되어 있지 않습니다.

-- 테이블 community.board_articles 구조 내보내기
CREATE TABLE IF NOT EXISTS `board_articles` (
  `board_article_id` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT '게시글 id',
  `ref_article_id` bigint(20) unsigned DEFAULT NULL COMMENT '참조 게시글 id',
  `board_id` bigint(20) unsigned NOT NULL COMMENT '게시판 id',
  `subject` varchar(100) DEFAULT NULL COMMENT '제목',
  `contents` varchar(4096) DEFAULT NULL COMMENT '내용',
  `html` tinyint(1) NOT NULL DEFAULT '0' COMMENT 'html 여부',
  `thumbnail` varchar(200) DEFAULT NULL COMMENT '썸네일',
  `view` int(10) unsigned NOT NULL DEFAULT '0' COMMENT '조회',
  `like` int(10) unsigned NOT NULL DEFAULT '0' COMMENT '좋아요',
  `creator` varchar(45) NOT NULL COMMENT '게시자',
  `created` datetime NOT NULL COMMENT '게시일시',
  `completed` datetime DEFAULT NULL COMMENT '완료일시',
  `status` smallint(5) unsigned NOT NULL COMMENT '게시 상태',
  PRIMARY KEY (`board_article_id`),
  KEY `idx_board_ref_article_completed` (`board_id`,`ref_article_id`,`completed`),
  KEY `idx_status_created` (`status`,`created`),
  KEY `⁯idx_creator` (`creator`)
) ENGINE=InnoDB AUTO_INCREMENT=22 DEFAULT CHARSET=utf8 COMMENT='게시글';

-- 내보낼 데이터가 선택되어 있지 않습니다.

-- 테이블 community.board_article_likes 구조 내보내기
CREATE TABLE IF NOT EXISTS `board_article_likes` (
  `board_article_like_id` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT '게시글 좋아요 id',
  `board_article_id` bigint(20) unsigned NOT NULL COMMENT '게시글 id',
  `member_id` bigint(20) unsigned NOT NULL COMMENT '회원 id',
  `created` datetime NOT NULL COMMENT '좋아요 일시',
  PRIMARY KEY (`board_article_like_id`),
  KEY `udx_article_member` (`board_article_id`,`member_id`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=latin1 COMMENT='게시글 좋아요';

-- 내보낼 데이터가 선택되어 있지 않습니다.

-- 테이블 community.board_article_media_files 구조 내보내기
CREATE TABLE IF NOT EXISTS `board_article_media_files` (
  `board_article_file_id` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT '파일 id',
  `board_article_id` bigint(20) unsigned NOT NULL COMMENT '게시글 id',
  `name` varchar(256) NOT NULL COMMENT '파일 이름',
  `type` smallint(6) NOT NULL COMMENT '100: 이미지, 200: 비디오',
  `preview_path` varchar(256) DEFAULT NULL COMMENT '미리보기 파일',
  `sd_path` varchar(256) DEFAULT NULL COMMENT 'sd 파일',
  `hd_path` varchar(256) DEFAULT NULL COMMENT 'hd 파일',
  `fhd_path` varchar(256) DEFAULT NULL COMMENT 'fhd 파일',
  `uhd_path` varchar(256) DEFAULT NULL COMMENT 'uhd 파일',
  `length` int(10) unsigned DEFAULT NULL COMMENT '길이',
  `failure_count` tinyint(3) unsigned NOT NULL COMMENT '업로드 실패 횟수',
  `status` smallint(5) unsigned NOT NULL COMMENT '게시 상태',
  `created` datetime NOT NULL COMMENT '파일 업로드 일시',
  PRIMARY KEY (`board_article_file_id`),
  KEY `idx_article` (`board_article_id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8 COMMENT='게시글 미디어 파일';

-- 내보낼 데이터가 선택되어 있지 않습니다.

-- 테이블 community.board_article_view 구조 내보내기
CREATE TABLE IF NOT EXISTS `board_article_view` (
  `board_article_view_id` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT '게시글 좋아요 id',
  `board_article_id` bigint(20) unsigned NOT NULL COMMENT '게시글 id',
  `member_id` bigint(20) unsigned NOT NULL COMMENT '회원 id',
  `created` datetime NOT NULL COMMENT '조회 일시',
  PRIMARY KEY (`board_article_view_id`),
  KEY `idx_article_member` (`board_article_id`,`member_id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COMMENT='게시글 좋아요';

-- 내보낼 데이터가 선택되어 있지 않습니다.

-- 테이블 community.spaces 구조 내보내기
CREATE TABLE IF NOT EXISTS `spaces` (
  `space_id` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT '도슨트의 공간 pk',
  `docent_id` bigint(20) NOT NULL COMMENT '운영하는 도슨트 id',
  `name` varchar(64) NOT NULL COMMENT '공간 이름',
  `location_category` varchar(64) NOT NULL COMMENT '공간 위치 카테고리',
  `address` varchar(128) NOT NULL,
  `operation_hour` varchar(128) DEFAULT NULL,
  `category` varchar(64) DEFAULT NULL COMMENT '공간이 속한 취향 카테고리',
  `short_description` varchar(128) NOT NULL COMMENT '짧은 소개',
  `description` varchar(1024) NOT NULL COMMENT '소개',
  `head_image_path` varchar(128) DEFAULT NULL COMMENT '대표 이미지',
  `head_video_path` varchar(128) DEFAULT NULL COMMENT '대표 동영상',
  `space_detail_id` bigint(20) unsigned DEFAULT NULL,
  `status` smallint(5) unsigned NOT NULL COMMENT '상태',
  `created` datetime NOT NULL COMMENT '생성일',
  PRIMARY KEY (`space_id`),
  KEY `idx_location_category` (`location_category`),
  KEY `idx_category` (`category`),
  KEY `idx_docent_id` (`docent_id`),
  KEY `idx_status_created` (`status`,`created`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='도슨트의 공간';

-- 내보낼 데이터가 선택되어 있지 않습니다.

-- 테이블 community.space_details 구조 내보내기
CREATE TABLE IF NOT EXISTS `space_details` (
  `space_detail_id` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT '공간 디테일 pk',
  `space_id` bigint(20) unsigned NOT NULL,
  `space_detail` varchar(10000) NOT NULL COMMENT '공간 상세',
  `status` smallint(5) unsigned NOT NULL COMMENT '상태',
  `created` datetime NOT NULL COMMENT '생성일',
  PRIMARY KEY (`space_detail_id`),
  KEY `idx_space_id` (`space_id`),
  KEY `idx_status_created` (`status`,`created`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='도슨트 공간 상세';

-- 내보낼 데이터가 선택되어 있지 않습니다.

-- 테이블 community.test 구조 내보내기
CREATE TABLE IF NOT EXISTS `test` (
  `t` bit(1) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- 내보낼 데이터가 선택되어 있지 않습니다.

/*!40103 SET TIME_ZONE=IFNULL(@OLD_TIME_ZONE, 'system') */;
/*!40101 SET SQL_MODE=IFNULL(@OLD_SQL_MODE, '') */;
/*!40014 SET FOREIGN_KEY_CHECKS=IFNULL(@OLD_FOREIGN_KEY_CHECKS, 1) */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40111 SET SQL_NOTES=IFNULL(@OLD_SQL_NOTES, 1) */;

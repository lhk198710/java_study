-- --------------------------------------------------------
-- 호스트:                          127.0.0.1
-- 서버 버전:                        10.4.24-MariaDB - mariadb.org binary distribution
-- 서버 OS:                        Win64
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


-- payment 데이터베이스 구조 내보내기
CREATE DATABASE IF NOT EXISTS `payment` /*!40100 DEFAULT CHARACTER SET utf8mb4 */;
USE `payment`;

-- 테이블 payment.bank_codes 구조 내보내기
CREATE TABLE IF NOT EXISTS `bank_codes` (
  `bank_code_id` bigint(20) NOT NULL AUTO_INCREMENT,
  `bank_name` varchar(50) NOT NULL DEFAULT '',
  `lf_code` varchar(50) NOT NULL DEFAULT '',
  `settle_bank` varchar(50) DEFAULT NULL,
  `kg_inicis` varchar(50) DEFAULT NULL,
  `nhn_kcp` varchar(50) DEFAULT NULL,
  `lg_uplus` varchar(50) DEFAULT NULL,
  `nice_payments` varchar(50) DEFAULT NULL,
  `jtnet` varchar(50) DEFAULT NULL,
  `danal` varchar(50) DEFAULT NULL,
  `kicc` varchar(50) DEFAULT NULL,
  `smartro` varchar(50) DEFAULT NULL,
  `daou_data` varchar(50) DEFAULT NULL,
  PRIMARY KEY (`bank_code_id`),
  KEY `lf_code` (`lf_code`)
) ENGINE=InnoDB AUTO_INCREMENT=23 DEFAULT CHARSET=utf8mb4 COMMENT='은행코드표';

-- 테이블 데이터 payment.bank_codes:~22 rows (대략적) 내보내기
INSERT INTO `bank_codes` (`bank_code_id`, `bank_name`, `lf_code`, `settle_bank`, `kg_inicis`, `nhn_kcp`, `lg_uplus`, `nice_payments`, `jtnet`, `danal`, `kicc`, `smartro`, `daou_data`) VALUES
	(1, 'KB국민은행', '01', '004', '04', 'BK04', '004', '004', '004', '004', '004', '004', '04'),
	(2, 'SC제일은행', '02', '023', '23', 'BK23', '023', '023', '023', '023', '023', '023', '11'),
	(3, '경남은행', '03', '039', '39', 'BK39', '039', '039', '039', '039', '039', '039', '20'),
	(4, '광주은행', '04', '034', '34', 'BK34', '034', '034', '034', '034', '034', '034', '16'),
	(5, '기업은행', '05', '003', '03', 'BK03', '003', '003', '003', '003', '003', '003', '03'),
	(6, '농협은행', '06', '011', '11', 'BK11', '011', '011', '011', '011', '011', '011', '08'),
	(7, '대구은행', '07', '031', '31', 'BK31', '031', '031', '031', '031', '031', '031', '14'),
	(8, '부산은행', '08', '032', '32', 'BK32', '032', '032', '032', '032', '032', '032', '15'),
	(9, '산업은행', '09', NULL, '02', 'BK02', '002', '002', '002', '002', '002', '002', '02'),
	(10, '새마을금고', '10', NULL, '45', 'BK45', '045', '045', '045', '045', '045', '045', '21'),
	(11, '수협', '11', NULL, '07', 'BK07', '007', '007', '007', '007', '007', '007', '06'),
	(12, '신한은행', '12', '088', '88', 'BK88', '088', '088', '088', '088', '026', '088', '10'),
	(13, '신협', '13', NULL, '48', 'BK48', '048', '048', '048', '048', '048', '048', '22'),
	(14, '외환은행', '14', '005', '05', 'BK81', '005', '005', '005', '005', '081', '005', '05'),
	(15, '우리은행', '15', '020', '20', 'BK20', '020', '020', '020', '020', '020', '020', '09'),
	(16, '우체국은행', '16', '071', '71', 'BK71', '071', '071', '071', '071', '071', '071', '31'),
	(17, '전북은행', '17', NULL, '37', 'BK37', '037', '037', '037', '037', '037', '037', '18'),
	(18, '축협', '18', NULL, '16', 'BK12', NULL, NULL, NULL, NULL, NULL, NULL, '34'),
	(19, '카카오뱅크', '19', NULL, '90', 'BK90', NULL, '090', '090', '090', '090', '090', '49'),
	(20, '케이뱅크', '20', NULL, '89', 'BK89', NULL, '089', '089', '089', '089', '089', '48'),
	(21, '하나은행(서울은행)', '21', NULL, '81', 'BK81', '081', '081', '081', '081', '081', '081', '12'),
	(22, '한국씨티은행(한미은행)', '22', '027', '53', 'BK27', '027', '027', '027', '027', '027', '027', '13');

-- 테이블 payment.lf_pay_data 구조 내보내기
CREATE TABLE IF NOT EXISTS `lf_pay_data` (
  `lf_pay_data_id` bigint(20) NOT NULL AUTO_INCREMENT,
  `order_id` varchar(50) NOT NULL,
  `imp_uid` varchar(50) DEFAULT NULL,
  `member_srl` varchar(50) DEFAULT NULL,
  `user_key` varchar(50) DEFAULT NULL,
  `pg_provider` varchar(50) DEFAULT NULL,
  `emb_pg_provider` varchar(50) DEFAULT NULL,
  `pg_tid` varchar(50) DEFAULT NULL,
  `pay_method` varchar(50) DEFAULT NULL,
  `total_amount` bigint(20) NOT NULL DEFAULT 0,
  `cancel_amount` bigint(20) NOT NULL DEFAULT 0,
  `remain_amount` bigint(20) NOT NULL DEFAULT 0,
  `refund_holder` varchar(50) DEFAULT NULL,
  `refund_bank_code` varchar(50) DEFAULT NULL,
  `refund_account` varchar(50) DEFAULT NULL,
  `card_name` varchar(50) DEFAULT NULL,
  `card_number` varchar(50) DEFAULT NULL,
  `card_code` varchar(50) DEFAULT NULL,
  `card_quota` varchar(50) DEFAULT NULL,
  `bank_code` varchar(50) DEFAULT NULL,
  `bank_name` varchar(50) DEFAULT NULL,
  `receipt_url` text DEFAULT NULL,
  `vbank_code` varchar(50) DEFAULT NULL,
  `vbank_name` varchar(50) DEFAULT NULL,
  `vbank_num` varchar(50) DEFAULT NULL,
  `vbank_holder` varchar(50) DEFAULT NULL,
  `vbank_date` varchar(50) DEFAULT NULL COMMENT '가상계좌 완료 일시',
  `vbank_issued_at` varchar(50) DEFAULT NULL,
  `paid_at` varchar(50) DEFAULT NULL,
  `cancelled_at` varchar(50) DEFAULT NULL,
  `status` varchar(50) DEFAULT NULL,
  `created_at` datetime NOT NULL DEFAULT current_timestamp(),
  `updated_at` datetime NOT NULL DEFAULT current_timestamp() ON UPDATE current_timestamp(),
  PRIMARY KEY (`lf_pay_data_id`),
  KEY `order_id` (`order_id`),
  KEY `imp_uid` (`imp_uid`),
  KEY `created_at` (`created_at`),
  KEY `updated_at` (`updated_at`),
  KEY `pg_tid` (`pg_tid`),
  KEY `member_srl` (`member_srl`)
) ENGINE=InnoDB AUTO_INCREMENT=15 DEFAULT CHARSET=utf8mb4 COMMENT='LF에서 관리하는 결제 관련 데이터';

-- 테이블 payment.payment_cancel_details 구조 내보내기
CREATE TABLE IF NOT EXISTS `payment_cancel_details` (
  `payment_cancel_detail_id` bigint(20) NOT NULL AUTO_INCREMENT,
  `lf_pay_data_id` bigint(20) NOT NULL,
  `pg_tid` varchar(50) DEFAULT NULL,
  `amount` bigint(20) DEFAULT NULL,
  `canceled_at` varchar(50) DEFAULT '',
  `reason` varchar(50) DEFAULT '',
  `receipt_url` text DEFAULT NULL,
  PRIMARY KEY (`payment_cancel_detail_id`),
  KEY `pg_tid` (`pg_tid`),
  KEY `lf_pay_data_id` (`lf_pay_data_id`)
) ENGINE=InnoDB AUTO_INCREMENT=25 DEFAULT CHARSET=utf8mb4;

-- 테이블 payment.pg_infos 구조 내보내기
CREATE TABLE IF NOT EXISTS `pg_infos` (
  `pg_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'PG 아이디',
  `pg_key` varchar(50) NOT NULL DEFAULT '0' COMMENT '아임포트 pg key',
  `lf_key` varchar(50) NOT NULL,
  `pg_name` varchar(50) NOT NULL DEFAULT '0' COMMENT '아임포트 pg name',
  `available` tinyint(4) NOT NULL DEFAULT 0 COMMENT 'PG사 연동 가능여부',
  `default_pg` tinyint(4) NOT NULL DEFAULT 0 COMMENT '기본 사용',
  PRIMARY KEY (`pg_id`),
  KEY `pg_key` (`pg_key`),
  KEY `lf_key` (`lf_key`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COMMENT='PG 정보';

-- 테이블 데이터 payment.pg_infos:~2 rows (대략적) 내보내기
INSERT INTO `pg_infos` (`pg_id`, `pg_key`, `lf_key`, `pg_name`, `available`, `default_pg`) VALUES
	(1, 'danal', 'danal', '다날', 1, 0),
	(2, 'html5_inicis', 'inicis', '이니시스', 1, 0),
	(3, 'kcp', 'kcp', 'KCP', 1, 0);

-- 테이블 payment.pg_pay_data 구조 내보내기
CREATE TABLE IF NOT EXISTS `pg_pay_data` (
  `pg_pay_data_id` bigint(20) NOT NULL AUTO_INCREMENT,
  `imp_uid` varchar(50) DEFAULT '' COMMENT 'pg_pay_id',
  `merchant_uid` varchar(50) NOT NULL COMMENT '주문번호',
  `pay_method` varchar(50) NOT NULL,
  `channel` varchar(50) DEFAULT NULL,
  `pg_provider` varchar(50) NOT NULL COMMENT '결제승인/시도된 PG사',
  `emb_pg_provider` varchar(50) DEFAULT NULL COMMENT '결제창에서 간편결제 호출시 결제 승인된 PG사',
  `pg_tid` varchar(50) NOT NULL COMMENT 'PG사 고유거래번호',
  `escrow` tinyint(4) DEFAULT 0,
  `apply_num` varchar(50) DEFAULT '0',
  `bank_code` varchar(50) DEFAULT '0',
  `bank_name` varchar(50) DEFAULT '0',
  `card_code` varchar(50) DEFAULT '0',
  `card_name` varchar(50) DEFAULT '0',
  `card_number` varchar(50) DEFAULT '0',
  `card_quota` varchar(50) DEFAULT '0',
  `card_type` varchar(50) DEFAULT '0',
  `vbank_code` varchar(50) DEFAULT '0' COMMENT '가상계좌 은행코드',
  `vbank_name` varchar(50) DEFAULT '0' COMMENT '가상계좌 은행명',
  `vbank_num` varchar(50) DEFAULT '0' COMMENT '가상계좌 입금계좌번호',
  `vbank_holder` varchar(50) DEFAULT '0' COMMENT '가상계좌 예금주',
  `vbank_date` varchar(50) DEFAULT '0' COMMENT '가상계좌 입금기한',
  `vbank_issued_at` varchar(50) DEFAULT '0' COMMENT '가상계좌 발급 일시',
  `name` varchar(50) DEFAULT '0',
  `amount` bigint(20) DEFAULT NULL,
  `cancel_amount` bigint(20) DEFAULT NULL,
  `currency` varchar(50) DEFAULT '0',
  `buyer_name` varchar(50) DEFAULT '0',
  `buyer_email` varchar(50) DEFAULT '0',
  `buyer_tel` varchar(50) DEFAULT '0',
  `buyer_addr` varchar(50) DEFAULT '0',
  `buyer_postcode` varchar(50) DEFAULT '0',
  `custom_data` varchar(50) DEFAULT '0',
  `status` varchar(50) DEFAULT '0',
  `started_at` varchar(50) DEFAULT '0',
  `paid_at` varchar(50) DEFAULT '0',
  `failed_at` varchar(50) DEFAULT '0',
  `cancelled_at` varchar(50) DEFAULT '0',
  `fail_reason` text DEFAULT NULL,
  `cancel_reason` text DEFAULT NULL,
  `receipt_url` text DEFAULT NULL,
  `created_at` datetime NOT NULL DEFAULT current_timestamp(),
  `updated_at` datetime NOT NULL DEFAULT current_timestamp() ON UPDATE current_timestamp(),
  PRIMARY KEY (`pg_pay_data_id`) USING BTREE,
  KEY `pg_pay_id` (`imp_uid`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=14 DEFAULT CHARSET=utf8mb4 COMMENT='서비스 결제 테이블';

-- 테이블 payment.pg_pay_methods 구조 내보내기
CREATE TABLE IF NOT EXISTS `pg_pay_methods` (
  `pg_pay_methods_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'PG사별 사용가능 결제수단 아이디',
  `pay_method` varchar(50) NOT NULL DEFAULT '0',
  `pay_method_name` varchar(50) NOT NULL COMMENT '결제수단명',
  `inicis` int(11) NOT NULL DEFAULT 0 COMMENT '이니시스 웹 표준 사용 비율',
  `kcp` int(11) NOT NULL DEFAULT 0 COMMENT 'kcp 사용 비율',
  `danal` int(11) NOT NULL DEFAULT 0 COMMENT '다날 사용 비율',
  `min_amount` int(11) NOT NULL DEFAULT 300,
  `max_amount` int(11) NOT NULL DEFAULT 10000,
  PRIMARY KEY (`pg_pay_methods_id`),
  KEY `pay_method` (`pay_method`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 COMMENT='PG사별 결제 가능 수단 & 결제 수단 별 비율 관리';

-- 테이블 데이터 payment.pg_pay_methods:~6 rows (대략적) 내보내기
INSERT INTO `pg_pay_methods` (`pg_pay_methods_id`, `pay_method`, `pay_method_name`, `inicis`, `kcp`, `danal`, `min_amount`, `max_amount`) VALUES
	(1, 'card', '신용카드', 70, 30, 0, 1000, 10000),
	(2, 'trans', '실시간계좌이체', 50, 50, 0, 300, 10000),
	(3, 'phone', '휴대폰결제', 30, 0, 70, 1000, 500000),
	(4, 'vbank', '가상계좌', 30, 70, 0, 300, 10000),
	(5, 'kakaopay', '카카오페이', 100, 0, 0, 300, 10000),
	(6, 'payco', '페이코', 0, 100, 0, 300, 10000);

/*!40103 SET TIME_ZONE=IFNULL(@OLD_TIME_ZONE, 'system') */;
/*!40101 SET SQL_MODE=IFNULL(@OLD_SQL_MODE, '') */;
/*!40014 SET FOREIGN_KEY_CHECKS=IFNULL(@OLD_FOREIGN_KEY_CHECKS, 1) */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40111 SET SQL_NOTES=IFNULL(@OLD_SQL_NOTES, 1) */;

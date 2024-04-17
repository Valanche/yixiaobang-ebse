DROP TABLE IF EXISTS `nucleic_acid_booking`;
CREATE TABLE IF NOT EXISTS `nucleic_acid_booking`
(
	`booking_id`             BIGINT                    NOT NULL AUTO_INCREMENT,
	`user_id`                BIGINT                    NOT NULL COMMENT '预约通知对应的user id',
	`manager_id`             BIGINT      DEFAULT 0     NOT NULL COMMENT '发布通知对应的manager id',
	`booking_title`          VARCHAR(40) DEFAULT '未指定' COMMENT '通知标题',
	`booking_deadline`       DATETIME                  NOT NULL COMMENT '预约截止时间',
	`booking_is_open_remind` BOOL        DEFAULT FALSE NOT NULL COMMENT '是否打开通知提醒',
	`booking_finish_status`  VARCHAR(10) DEFAULT '进行中' NOT NULL COMMENT '预约完成状态',
	CONSTRAINT PRIMARY KEY (`booking_id`)
) ENGINE = InnoDB
  CHARSET = utf8;

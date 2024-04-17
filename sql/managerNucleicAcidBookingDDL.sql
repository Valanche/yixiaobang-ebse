CREATE TABLE IF NOT EXISTS `manager_nucleic_acid_booking`
(
	`booking_id`            BIGINT      NOT NULL AUTO_INCREMENT,
	`manager_id`            BIGINT      NOT NULL DEFAULT 0 COMMENT '发布该预约信息的管理员id',
	`booking_finish_status` VARCHAR(10) NOT NULL DEFAULT '进行中' COMMENT '预约信息的完成状态',
	`booking_title`         VARCHAR(40) NOT NULL DEFAULT '未指定' COMMENT '预约通知的标题',
	`booking_deadline`      DATETIME    NOT NULL COMMENT '预约的截止时间',
	CONSTRAINT PRIMARY KEY (`booking_id`)
) ENGINE = InnoDB
  CHARSET = UTF8;

CREATE TABLE IF NOT EXISTS `manager_nucleic_acid_info`
(
	`info_id`            BIGINT      NOT NULL AUTO_INCREMENT,
	`manager_id`         BIGINT      NOT NULL DEFAULT 0 COMMENT '发布该上报信息的管理员id',
	`info_finish_status` VARCHAR(10) NOT NULL DEFAULT '进行中' COMMENT '上报信息的完成状态',
	`info_title`         VARCHAR(60) NOT NULL DEFAULT '未指定' COMMENT '上报通知的标题',
	`info_deadline`      DATETIME    NOT NULL COMMENT '上报的截止时间',
	CONSTRAINT PRIMARY KEY (`info_id`)
) ENGINE = InnoDB
  CHARSET = UTF8;

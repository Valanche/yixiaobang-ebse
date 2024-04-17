CREATE TABLE IF NOT EXISTS `manager_nucleic_acid_testing`
(
	`testing_id`            BIGINT       NOT NULL AUTO_INCREMENT,
	`manager_id`            BIGINT       NOT NULL DEFAULT 0 COMMENT '发布该检测信息的管理员id',
	`testing_finish_status` VARCHAR(10)  NOT NULL DEFAULT '进行中' COMMENT '检测信息的完成状态',
	`testing_title`         VARCHAR(60)  NOT NULL DEFAULT '未指定' COMMENT '检测通知的标题',
	`testing_require`       VARCHAR(100) NOT NULL DEFAULT '无要求' COMMENT '检测的要求',
	`testing_place`         VARCHAR(100) NOT NULL DEFAULT '未指定' COMMENT '检测的地点',
	`testing_start_time`    DATETIME     NOT NULL COMMENT '检测的开始时间',
	`testing_end_time`      DATETIME     NOT NULL COMMENT '检测的结束时间',
	CONSTRAINT PRIMARY KEY (`testing_id`)
) ENGINE = InnoDB
  CHARSET = UTF8;

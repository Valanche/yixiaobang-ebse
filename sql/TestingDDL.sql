CREATE TABLE IF NOT EXISTS `nucleic_acid_testing`
(
	`testing_id`             BIGINT                       NOT NULL AUTO_INCREMENT,
	`user_id`                BIGINT                       NOT NULL COMMENT '用户id',
	`manager_id`             BIGINT       DEFAULT 0       NOT NULL COMMENT '管理员id',
	`testing_require`        VARCHAR(100) DEFAULT '无要求'   NOT NULL COMMENT '核酸检测要求',
	`testing_title`          VARCHAR(60)  DEFAULT '请填写标题' NOT NULL COMMENT '上报的标题',
	`testing_start_time`     DATETIME                     NOT NULL COMMENT '检测开始时间',
	`testing_end_time`       DATETIME                     NOT NULL COMMENT '检测结束时间',
	`testing_true_time`      DATETIME     DEFAULT NULL COMMENT '实际检测时间',
	`testing_is_open_remind` BOOL         DEFAULT FALSE   NOT NULL COMMENT '是否打开核酸检测提醒',
	`testing_place`          VARCHAR(100) DEFAULT '未指定'   NOT NULL COMMENT '核酸检测地点',
	`testing_finish_status`  VARCHAR(10)  DEFAULT '进行中'   NOT NULL COMMENT '核酸检测完成状态',
	CONSTRAINT PRIMARY KEY (`testing_id`)
) ENGINE = InnoDB
  CHARSET = utf8;

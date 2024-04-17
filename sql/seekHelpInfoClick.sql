CREATE TABLE IF NOT EXISTS `seek_help_info_click`
(
	`click_id`         BIGINT NOT NULL AUTO_INCREMENT,
	`user_id`          BIGINT NOT NULL COMMENT '点击的用户id',
	`seek_help_id`     BIGINT NOT NULL COMMENT '帮助信息的id',
	`click_start_time` DATETIME DEFAULT NULL COMMENT '点击该帮助信息的时间',
	`click_end_time`   DATETIME DEFAULT NULL COMMENT '退出该帮助信息页面的时间',
	CONSTRAINT PRIMARY KEY (`click_id`)
) ENGINE = InnoDB
  CHARSET = utf8;

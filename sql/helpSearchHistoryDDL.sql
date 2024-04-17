CREATE TABLE IF NOT EXISTS `help_search_history`
(
	`history_id`          BIGINT      NOT NULL AUTO_INCREMENT,
	`user_id`             BIGINT      NOT NULL COMMENT '搜索的用户id',
	`history_search_time` DATETIME    NOT NULL COMMENT '搜索的时间',
	`history_keyword`     VARCHAR(30) NOT NULL COMMENT '搜索的关键词',
	CONSTRAINT PRIMARY KEY (`history_id`)
) ENGINE = InnoDB
  CHARSET = utf8;

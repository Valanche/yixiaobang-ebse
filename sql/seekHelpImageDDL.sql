CREATE TABLE IF NOT EXISTS `seek_help_image`
(
	`image_id`     BIGINT                     NOT NULL AUTO_INCREMENT,
	`seek_help_id` BIGINT                     NOT NULL COMMENT '帮助通知的id',
	`image_url`    VARCHAR(500) DEFAULT '未指定' NOT NULL COMMENT '帮助的图片',
	CONSTRAINT PRIMARY KEY (`image_id`)
) ENGINE = InnoDB
  CHARSET = utf8

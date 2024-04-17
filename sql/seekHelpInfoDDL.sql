CREATE TABLE IF NOT EXISTS `seek_help_info`
(
	`seek_help_id`            BIGINT                     NOT NULL AUTO_INCREMENT,
	`user_id`                 BIGINT                     NOT NULL COMMENT '发布该求助信息的用户id',
	`seek_help_type`          VARCHAR(10)                NOT NULL COMMENT '求助的类型',
	`seek_help_publish_date`  DATETIME                   NOT NULL COMMENT '该求助信息发布的时间',
	`seek_help_name`          VARCHAR(40)  DEFAULT '未指定' NOT NULL COMMENT '求助的物品名字',
	`seek_help_deadline`      DATETIME                   NOT NULL COMMENT '求助的截止日期',
	`seek_help_comment`       VARCHAR(200) DEFAULT '未指定' NOT NULL COMMENT '备注',
	`seek_help_tag`           VARCHAR(40)  DEFAULT '未指定' NOT NULL COMMENT '所属的tag',
	`seek_help_urgency`       INT          DEFAULT 0     NOT NULL COMMENT '紧急程度 1-5',
	`seek_help_finish_status` VARCHAR(10)  DEFAULT '未解决' NOT NULL COMMENT '求助信息的解决状态',
	CONSTRAINT PRIMARY KEY (`seek_help_id`)
) ENGINE = InnoDB
  CHARSET = utf8;

CREATE TABLE IF NOT EXISTS `help_info`
(
	`help_id`            BIGINT                     NOT NULL AUTO_INCREMENT,
	`user_id`            BIGINT                     NOT NULL COMMENT '发布该帮忙信息的用户id',
	`help_type`          VARCHAR(10)                NOT NULL COMMENT '帮忙的类型',
	`help_publish_date`  DATETIME                   NOT NULL COMMENT '该信息发布的时间',
	`help_name`          VARCHAR(40)  DEFAULT '未指定' NOT NULL COMMENT '帮忙的物品名字',
	`help_deadline`      DATETIME                   NOT NULL COMMENT '帮忙的截止日期',
	`help_comment`       VARCHAR(200) DEFAULT '未指定' NOT NULL COMMENT '备注',
	`help_tag`           VARCHAR(40)  DEFAULT '未指定' NOT NULL COMMENT '所属的tag',
	`help_urgency`       INT          DEFAULT 0     NOT NULL COMMENT '紧急程度 1-5',
	`help_finish_status` VARCHAR(10)  DEFAULT '进行中' NOT NULL COMMENT '帮助信息的解决状态',
	CONSTRAINT PRIMARY KEY (`help_id`)
) ENGINE = InnoDB
  CHARSET = utf8;

CREATE TABLE IF NOT EXISTS `nucleic_acid_info`
(
	`info_id`             BIGINT                        NOT NULL AUTO_INCREMENT,
	`user_id`             BIGINT                        NOT NULL COMMENT '被通知的用户id',
	`manager_id`          BIGINT      DEFAULT 0         NOT NULL COMMENT '发布该通知的管理员id',
	-- Date 无法设置默认值 TIMESTAMP 可以用 NOW() 来设置 --
	`info_deadline`       DATETIME                      NOT NULL COMMENT '该次上报截止的时间',
	`info_is_open_remind` BOOL        DEFAULT FALSE     NOT NULL COMMENT '是否打开提醒',
	`info_title`          VARCHAR(60) DEFAULT '请填写标题'   NOT NULL COMMENT '上报的标题',
	`info_status`         VARCHAR(10) DEFAULT '进行中'     NOT NULL COMMENT '是否已经完成该通知',
	`info_image_name`     VARCHAR(50) DEFAULT 'UNKNOWN' NOT NULL COMMENT '核酸截图的文件路径',
	`info_comment` VARCHAR(500) DEFAULT '无特殊理由' NOT NULL COMMENT '未做核酸的理由',
	CONSTRAINT PRIMARY KEY (`info_id`)
) ENGINE = InnoDB
  CHARSET = utf8;

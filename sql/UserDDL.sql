CREATE TABLE IF NOT EXISTS `user`
(
	`user_id`        BIGINT                                   NOT NULL AUTO_INCREMENT,
	`user_open_id`   VARCHAR(60)                              NOT NULL COMMENT '微信openid',
	`user_nick_name` VARCHAR(40)            DEFAULT 'UNKNOWN' NOT NULL COMMENT '用户昵称',
	`user_name`      VARCHAR(40)            DEFAULT 'UNKNOWN' NOT NULL COMMENT '用户真实姓名',
	`user_school`    VARCHAR(40)            DEFAULT '未选定'     NOT NULL COMMENT '用户学校',
	`user_institute` VARCHAR(40)            DEFAULT '未选定'     NOT NULL COMMENT '用户院系',
	`user_major`     VARCHAR(40)            DEFAULT '未选定'     NOT NULL COMMENT '用户专业',
	`user_grade`     VARCHAR(10)            DEFAULT '未选定'     NOT NULL COMMENT '用户年级',
	`user_gender`    ENUM ('男', '女', '不清楚') DEFAULT '不清楚'     NOT NULL COMMENT '用户性别',
	user_avatar_url  VARCHAR(200)           DEFAULT ''        NOT NULL,
	CONSTRAINT PRIMARY KEY (`user_id`)
) ENGINE = InnoDB
  CHARSET = utf8;

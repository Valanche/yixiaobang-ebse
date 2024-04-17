CREATE TABLE IF NOT EXISTS `manager`
(
	`manager_id`        BIGINT       NOT NULL AUTO_INCREMENT,
	`manager_name`      VARCHAR(30)  NOT NULL DEFAULT '未指定' COMMENT '管理员姓名',
	`manager_nick_name` VARCHAR(30)  NOT NULL DEFAULT '未指定' COMMENT '管理员昵称',
	`manager_role`      VARCHAR(20)  NOT NULL DEFAULT '普通管理员' COMMENT '管理员权限',
	`manager_password`  VARCHAR(100) NOT NULL DEFAULT '未指定' COMMENT '管理员账号密码',
	`manager_post`      VARCHAR(30)  NOT NULL DEFAULT '未指定' COMMENT '管理员职务',
	`manager_school`    VARCHAR(30)  NOT NULL DEFAULT '未指定' COMMENT '管理员学校',
	`manager_institute` VARCHAR(50)  NOT NULL DEFAULT '未指定' COMMENT '管理员所属院系',
	`manager_major`     VARCHAR(50)  NOT NULL DEFAULT '未指定' COMMENT '管理员所属专业',
	`manager_grade`     VARCHAR(50)  NOT NULL DEFAULT '未指定' COMMENT '管理员所属年级',
	`manager_avatar_url` VARCHAR(200) NOT NULL DEFAULT '未指定' COMMENT '管理员头像',
	`manager_open_id` VARCHAR(60) NOT NULL DEFAULT '未指定' COMMENT '管理员小程序open id',
	`manager_gender` VARCHAR(3) NOT NULL DEFAULT '未指定' COMMENT '管理员性别',
	CONSTRAINT PRIMARY KEY (`manager_id`)
) ENGINE = InnoDB
  CHARSET = UTF8;

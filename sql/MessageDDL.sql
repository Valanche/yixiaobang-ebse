CREATE TABLE IF NOT EXISTS `message`
(
	`message_id`           BIGINT                      NOT NULL AUTO_INCREMENT,
	`message_sender_id`    BIGINT                      NOT NULL COMMENT '发送消息的用户id',
	`message_receiver_id`  BIGINT                      NOT NULL COMMENT '接收消息的用户id',
	`message_content`      VARCHAR(2000) DEFAULT ''    NOT NULL COMMENT '消息内容',
	`message_send_time`    DATETIME                    NOT NULL COMMENT '发送该消息的时间',
	`message_is_show_time` BOOL          DEFAULT FALSE NOT NULL COMMENT '是否显示该消息的时间',
	`message_is_read`      BOOL          DEFAULT FALSE NOT NULL COMMENT '信息是否已经被阅读',
	CONSTRAINT PRIMARY KEY (`message_id`)
) ENGINE = InnoDB
  CHARSET = utf8;

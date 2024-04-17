CREATE TABLE IF NOT EXISTS `similarity_sentence`
(
	`similarity_id`           BIGINT AUTO_INCREMENT,
	`similarity_score`        DOUBLE      NOT NULL COMMENT '两个词之间的相似度分数',
	`similarity_sentence_one` VARCHAR(30) NOT NULL COMMENT '第一个词',
	`similarity_sentence_two` VARCHAR(30) NOT NULL COMMENT '第二个词',
	CONSTRAINT PRIMARY KEY (`similarity_id`)
) ENGINE = InnoDB
  CHARSET = utf8;

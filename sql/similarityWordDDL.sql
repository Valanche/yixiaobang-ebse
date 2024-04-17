CREATE TABLE IF NOT EXISTS `similarity_word`
(
	`similarity_id`       BIGINT AUTO_INCREMENT,
	`similarity_score`    DOUBLE      NOT NULL COMMENT '两个词之间的相似度分数',
	`similarity_word_one` VARCHAR(20) NOT NULL COMMENT '第一个词',
	`similarity_word_two` VARCHAR(20) NOT NULL COMMENT '第二个词',
	CONSTRAINT PRIMARY KEY (`similarity_id`)
) ENGINE = InnoDB
  CHARSET = utf8;

package com.weixin.njuteam.entity.vo.help;

import com.weixin.njuteam.entity.po.help.SimilaritySentencePO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.BeanUtils;

/**
 * @author Zyi
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SimilaritySentenceVO {

	/**
	 * 相似度id
	 */
	private Long id;
	/**
	 * 相似度得分
	 */
	private Double score;
	/**
	 * 第一个短句子
	 */
	private String sentenceOne;
	/**
	 * 第二个短句子
	 */
	private String sentenceTwo;

	public SimilaritySentenceVO(SimilaritySentencePO similarityPo) {
		BeanUtils.copyProperties(similarityPo, this);
	}
}

package com.weixin.njuteam.entity.po.help;

import com.weixin.njuteam.entity.vo.help.SimilaritySentenceVO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.ibatis.type.Alias;
import org.springframework.beans.BeanUtils;

/**
 * @author Zyi
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Alias("similaritySentencePO")
public class SimilaritySentencePO {

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

	public SimilaritySentencePO(SimilaritySentenceVO similarityVo) {
		BeanUtils.copyProperties(similarityVo, this);
	}
}

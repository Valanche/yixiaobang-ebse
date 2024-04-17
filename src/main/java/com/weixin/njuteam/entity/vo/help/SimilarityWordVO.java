package com.weixin.njuteam.entity.vo.help;

import com.weixin.njuteam.entity.po.help.SimilarityWordPO;
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
public class SimilarityWordVO {

	/**
	 * 相似度id
	 */
	private Long id;
	/**
	 * 相似度得分
	 */
	private Double score;
	/**
	 * 第一个词
	 */
	private String wordOne;
	/**
	 * 第二个词
	 */
	private String wordTwo;

	public SimilarityWordVO(SimilarityWordPO similarityWordPo) {
		BeanUtils.copyProperties(similarityWordPo, this);
	}
}

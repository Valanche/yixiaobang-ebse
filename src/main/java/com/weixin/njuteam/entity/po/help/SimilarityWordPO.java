package com.weixin.njuteam.entity.po.help;

import com.weixin.njuteam.entity.vo.help.SimilarityWordVO;
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
@Alias("similarityWordPO")
public class SimilarityWordPO {

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

	public SimilarityWordPO(SimilarityWordVO similarityWordVo) {
		BeanUtils.copyProperties(similarityWordVo, this);
	}
}

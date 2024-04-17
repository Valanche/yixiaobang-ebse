package com.weixin.njuteam.service;

import com.weixin.njuteam.entity.vo.help.HelpInfoVO;
import com.weixin.njuteam.entity.vo.help.SeekHelpInfoVO;
import com.weixin.njuteam.entity.vo.help.SimilaritySentenceVO;
import com.weixin.njuteam.entity.vo.help.SimilarityWordVO;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @author Zyi
 */
public interface RecommendService {

	/**
	 * get the similarity between two words
	 *
	 * @param wordOne word one
	 * @param wordTwo word two
	 * @return similarity word value object
	 */
	SimilarityWordVO queryWordSimilarity(String wordOne, String wordTwo);

	/**
	 * get the similarity between two short sentence
	 *
	 * @param sentenceOne sentence one
	 * @param sentenceTwo sentence two
	 * @return similarity sentence value object
	 */
	SimilaritySentenceVO querySentenceSimilarity(String sentenceOne, String sentenceTwo);

	/**
	 * insert the similarity between two words
	 *
	 * @param similarityWordVo similarity word value object
	 * @return similarity word value object after insert
	 */
	SimilarityWordVO insertWordSimilarity(SimilarityWordVO similarityWordVo);

	/**
	 * insert the similarity between two sentence
	 *
	 * @param similaritySentenceVO similarity sentence value object
	 * @return similarity word value object after insert
	 */
	SimilaritySentenceVO insertSentenceSimilarity(SimilaritySentenceVO similaritySentenceVO);

	/**
	 * get help info recommend list
	 *
	 * @param openId user open id
	 * @return list of recommend help info value object
	 */
	List<HelpInfoVO> getHelpInfoRecommendList(@NotNull String openId);

	/**
	 * get seek help info recommend list
	 *
	 * @param openId user open id
	 * @return list of recommend seek help info value object
	 */
	List<SeekHelpInfoVO> getSeekHelpInfoRecommendList(@NotNull String openId);

	/**
	 * delete sentence similarity
	 *
	 * @param similarityId similarity id
	 * @return true if delete successfully, false otherwise
	 */
	boolean deleteSentenceSimilarity(long similarityId);
}

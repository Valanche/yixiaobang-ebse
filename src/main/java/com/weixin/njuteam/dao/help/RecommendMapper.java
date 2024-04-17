package com.weixin.njuteam.dao.help;

import com.weixin.njuteam.entity.po.help.SimilaritySentencePO;
import com.weixin.njuteam.entity.po.help.SimilarityWordPO;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author Zyi
 */
@Mapper
@Repository
public interface RecommendMapper {

	/**
	 * get two word's similarity by word
	 *
	 * @param wordOne word one
	 * @param wordTwo word two
	 * @return similarity word persistent object
	 */
	SimilarityWordPO queryWordSimilarity(String wordOne, String wordTwo);

	/**
	 * get two sentence's similarity by sentence
	 *
	 * @param sentenceOne sentence one
	 * @param sentenceTwo sentence two
	 * @return similarity sentence persistent object
	 */
	List<SimilaritySentencePO> querySentenceSimilarity(String sentenceOne, String sentenceTwo);

	/**
	 * insert word similarity
	 *
	 * @param similarity word similarity persistent object
	 * @return the amount of row affect by insert
	 */
	int insertWordSimilarity(SimilarityWordPO similarity);

	/**
	 * insert sentence similarity
	 *
	 * @param similarity sentence similarity persistent object
	 * @return the amount of row affect by insert
	 */
	int insertSentenceSimilarity(SimilaritySentencePO similarity);

	/**
	 * delete sentence similarity
	 *
	 * @param similarityId similarity id
	 * @return the amount of row affect by delete
	 */
	int deleteSentenceSimilarity(long similarityId);
}

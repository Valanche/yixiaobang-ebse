package com.weixin.njuteam.service.impl.help;

import com.weixin.njuteam.config.NLPClient;
import com.weixin.njuteam.dao.help.HelpInfoMapper;
import com.weixin.njuteam.dao.help.RecommendMapper;
import com.weixin.njuteam.dao.help.SeekHelpInfoMapper;
import com.weixin.njuteam.entity.po.help.*;
import com.weixin.njuteam.entity.po.help.base.BaseInfoPO;
import com.weixin.njuteam.entity.vo.help.*;
import com.weixin.njuteam.entity.vo.help.base.BaseClickHistoryVO;
import com.weixin.njuteam.enums.HelpFinishStatus;
import com.weixin.njuteam.enums.SeekHelpFinishStatus;
import com.weixin.njuteam.service.ClickHistoryService;
import com.weixin.njuteam.service.RecommendService;
import com.weixin.njuteam.service.UserService;
import com.weixin.njuteam.util.Pair;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.validation.constraints.NotNull;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author Zyi
 */
@Service
@Slf4j
public class RecommendServiceImpl implements RecommendService {

	private static final int MAX_RECOMMEND_LIST_SIZE = 20;
	private static final double MIN_SIMILARITY_SCORE = 0.2;

	private final RecommendMapper recommendMapper;
	private final HelpInfoMapper helpInfoMapper;
	private final SeekHelpInfoMapper seekHelpInfoMapper;
	private final UserService userService;
	private final ClickHistoryService clickHistoryService;
	private final NLPClient client;

	private final Random random;

	public RecommendServiceImpl(RecommendMapper recommendMapper, HelpInfoMapper helpInfoMapper, SeekHelpInfoMapper seekHelpInfoMapper,
								ClickHistoryService clickHistoryService, UserService userService, NLPClient client) {
		this.recommendMapper = recommendMapper;
		this.helpInfoMapper = helpInfoMapper;
		this.seekHelpInfoMapper = seekHelpInfoMapper;
		this.userService = userService;
		this.clickHistoryService = clickHistoryService;
		this.client = client;
		this.random = new Random();
	}

	@Override
	public SimilarityWordVO queryWordSimilarity(String wordOne, String wordTwo) {
		// 首先查找数据库看是否有对应的数据
		SimilarityWordPO similarityWord = recommendMapper.queryWordSimilarity(wordOne, wordTwo);
		SimilarityWordVO newSimilarity = null;

		if (similarityWord == null || similarityWord.getScore() == null) {
			// 说明数据库没有
			double res = client.calWordSimilarity(wordOne, wordTwo);
			newSimilarity = SimilarityWordVO.builder()
				.score(res)
				.wordOne(wordOne)
				.wordTwo(wordTwo)
				.build();

			// 插入数据库
			newSimilarity = insertWordSimilarity(newSimilarity);
		} else {
			// 说明数据库有
			newSimilarity = new SimilarityWordVO(similarityWord);
		}

		return newSimilarity;
	}

	@Override
	public SimilaritySentenceVO querySentenceSimilarity(String sentenceOne, String sentenceTwo) {
		// 首先查找数据库看是否有对应的数据
		List<SimilaritySentencePO> similaritySentence = recommendMapper.querySentenceSimilarity(sentenceOne, sentenceTwo);
		SimilaritySentenceVO newSimilarity;

		if (similaritySentence.isEmpty() || similaritySentence.get(0).getScore() == null) {
			// 说明数据库没有
			double res = client.calSentenceSimilarity(sentenceOne, sentenceTwo);
			newSimilarity = SimilaritySentenceVO.builder()
				.score(res)
				.sentenceOne(sentenceOne)
				.sentenceTwo(sentenceTwo)
				.build();

			// 插入数据库
			newSimilarity = insertSentenceSimilarity(newSimilarity);
		} else {
			// 说明数据库有
			newSimilarity = new SimilaritySentenceVO(similaritySentence.get(0));
		}

		if (similaritySentence.size() > 1) {
			// 说明插入的时候因为并发导致了重复插入
			for (int i = 1; i < similaritySentence.size(); i++) {
				deleteSentenceSimilarity(similaritySentence.get(i).getId());
			}
		}

		return newSimilarity;
	}

	@Override
	public SimilarityWordVO insertWordSimilarity(SimilarityWordVO similarityWordVo) {
		SimilarityWordPO similarityWordPo = new SimilarityWordPO(similarityWordVo);
		int i = recommendMapper.insertWordSimilarity(similarityWordPo);

		if (i <= 0) {
			return null;
		}

		similarityWordVo.setId(similarityWordPo.getId());
		return similarityWordVo;
	}

	@Override
	public SimilaritySentenceVO insertSentenceSimilarity(SimilaritySentenceVO similaritySentenceVO) {
		SimilaritySentencePO similaritySentencePo = new SimilaritySentencePO(similaritySentenceVO);
		int i = recommendMapper.insertSentenceSimilarity(similaritySentencePo);

		if (i <= 0) {
			return null;
		}

		similaritySentenceVO.setId(similaritySentencePo.getId());
		return similaritySentenceVO;
	}

	@Override
	@SuppressWarnings("all")
	public List<HelpInfoVO> getHelpInfoRecommendList(@NotNull String openId) {
		// 思路：获取用户搜索的历史记录的关键词和用户发布过的信息和用户浏览过的信息
		// 然后根据物品之间的相似度排序并去重 (去掉该用户已经发布过的)
		// 最后生成推荐列表
		long userId = userService.queryUserId(openId);
		List<HelpSearchHistoryPO> historyList = helpInfoMapper.querySearchHistory(userId, -1);
		// 用户自己发布过的帮助信息列表
		List<HelpInfoPO> userHelpInfoList = helpInfoMapper.queryHelpInfoByUserId(userId);
		// 所有进行中的帮助信息列表 用来生成推荐结果
		List<HelpInfoPO> allHelpInfoList = helpInfoMapper.queryHelpInfoInProgress();
		// 获取用户浏览记录
		List<HelpClickHistoryVO> clickHistoryList = clickHistoryService.queryHelpClickByUserId(openId);

		// 如果该用户没有浏览过也没有历史搜索记录 则从所有列表里随机返回
		if (isEmpty(historyList) && isEmpty(userHelpInfoList) && isEmpty(clickHistoryList)) {
			List<HelpInfoPO> finalRecommendList = (List<HelpInfoPO>) removeClickBefore(allHelpInfoList, clickHistoryList, HelpInfoPO.class);
			return convertHelpInfoList(randomRemoveHelpList(finalRecommendList));
		}

		// 获得相同tag的list
		List<HelpInfoPO> tagEqualList = (List<HelpInfoPO>) getTagEqualList(allHelpInfoList, userHelpInfoList, userId, HelpInfoPO.class);

		// 取历史搜索记录与所有帮助信息中相似度最高的
		List<HelpInfoPO> searchRecommendList = calSimilarityByHelpHistory(allHelpInfoList, historyList, userId);
		// 取浏览记录中与所有帮助信息中心相似度最高的
		List<HelpInfoVO> clickRecommendList = calHelpSimilarityByClick(allHelpInfoList, clickHistoryList, userId);
		List<HelpInfoPO> recommendList = new ArrayList<>(searchRecommendList);
		// 去除重复后的添加
		recommendList = addHelpInfoExceptDuplicate(recommendList, clickRecommendList, userId);

		// 优先返回历史搜索记录和浏览记录的推荐
		if (recommendList.size() > MAX_RECOMMEND_LIST_SIZE) {
			log.info("recommend size is bigger than max recommend list size");
			recommendList = randomRemoveHelpList(recommendList);
			return convertHelpInfoList(recommendList);
		}

		// 如果不足20条，则从tag相同的里面补充
		// recommendList = addHelpInfoExceptDuplicate(recommendList, convertHelpInfoList(tagEqualList), userId);
		// 从中取20条随机返回
//		if (recommendList.size() > MAX_RECOMMEND_LIST_SIZE) {
//			recommendList = randomRemoveHelpList(recommendList);
//		}

		// 去重
		recommendList = recommendList.stream().collect(Collectors.collectingAndThen(
			Collectors.toCollection(() -> new TreeSet<>(Comparator.comparing(HelpInfoPO::getId))), ArrayList::new
		));

		// 排除已经看过的
		List<HelpInfoPO> finalRecommendList = (List<HelpInfoPO>) removeClickBefore(recommendList, clickHistoryList, HelpInfoPO.class);
		// 排除非进行中的
		finalRecommendList = (List<HelpInfoPO>) removeNotInProgress(finalRecommendList, HelpInfoPO.class);
		return convertHelpInfoList(finalRecommendList);
	}

	@Override
	@SuppressWarnings("all")
	public List<SeekHelpInfoVO> getSeekHelpInfoRecommendList(@NotNull String openId) {
		// 思路：获取用户搜索的历史记录的关键词和用户发布过的信息和用户浏览过的信息
		// 然后根据物品之间的相似度排序并去重 (去掉该用户已经发布过的)
		// 最后生成推荐列表
		long userId = userService.queryUserId(openId);
		List<SeekHelpSearchHistoryPO> historyList = seekHelpInfoMapper.querySearchHistory(userId, -1);
		// 用户自己发布过的帮助信息列表
		List<SeekHelpInfoPO> userHelpInfoList = seekHelpInfoMapper.querySeekInfoByUserId(userId);
		// 所有进行中的帮助信息列表 用来生成推荐结果
		List<SeekHelpInfoPO> allHelpInfoList = seekHelpInfoMapper.querySeekInfoInProgress();
		// 获取用户浏览记录
		List<SeekHelpClickHistoryVO> clickHistoryList = clickHistoryService.querySeekHelpClickByUserId(openId);

		// 如果该用户没有浏览过也没有历史搜索记录 则从所有列表里随机返回
		if (isEmpty(historyList) && isEmpty(userHelpInfoList) && isEmpty(clickHistoryList)) {
			List<SeekHelpInfoPO> finalRecommendList = (List<SeekHelpInfoPO>) removeClickBefore(allHelpInfoList, clickHistoryList, SeekHelpInfoPO.class);
			return convertSeekHelpInfoList(finalRecommendList);
		}

		// 获得相同tag的list
		List<SeekHelpInfoPO> tagEqualList = (List<SeekHelpInfoPO>) getTagEqualList(allHelpInfoList, userHelpInfoList, userId, SeekHelpInfoPO.class);

		// 取历史搜索记录与所有帮助信息中相似度最高的
		List<SeekHelpInfoPO> searchRecommendList = calSimilarityBySeekHelpHistory(allHelpInfoList, historyList, userId);
		// 取浏览记录中与所有帮助信息中心相似度最高的
		List<SeekHelpInfoVO> clickRecommendList = calSeekHelpSimilarityByClick(allHelpInfoList, clickHistoryList, userId);
		List<SeekHelpInfoPO> recommendList = new ArrayList<>(searchRecommendList);
		// 去除重复后的添加
		recommendList = addSeekHelpInfoExceptDuplicate(recommendList, clickRecommendList, userId);

		// 优先返回历史搜索记录和浏览记录的推荐
		if (recommendList.size() > MAX_RECOMMEND_LIST_SIZE) {
			recommendList = randomRemoveSeekHelpList(recommendList);
			return convertSeekHelpInfoList(recommendList);
		}

		// 如果不足20条，则从tag相同的里面补充
		// recommendList = addSeekHelpInfoExceptDuplicate(recommendList, convertSeekHelpInfoList(tagEqualList), userId);
		// 从中取20条随机返回
//		if (recommendList.size() > MAX_RECOMMEND_LIST_SIZE) {
//			recommendList = randomRemoveSeekHelpList(recommendList);
//		}

		// 去重
		recommendList = recommendList.stream().collect(Collectors.collectingAndThen(
			Collectors.toCollection(() -> new TreeSet<>(Comparator.comparing(SeekHelpInfoPO::getId))), ArrayList::new
		));

		// 排除已经看过的
		List<SeekHelpInfoPO> finalRecommendList = (List<SeekHelpInfoPO>) removeClickBefore(recommendList, clickHistoryList, SeekHelpInfoPO.class);
		// 排除非进行中的
		finalRecommendList = (List<SeekHelpInfoPO>) removeNotInProgress(finalRecommendList, SeekHelpInfoPO.class);
		return convertSeekHelpInfoList(finalRecommendList);
	}

	@Override
	public boolean deleteSentenceSimilarity(long similarityId) {
		int i = recommendMapper.deleteSentenceSimilarity(similarityId);

		return i > 0;
	}

	private List<HelpInfoVO> convertHelpInfoList(List<HelpInfoPO> list) {
		return list.stream().filter(Objects::nonNull).map(HelpInfoVO::new).collect(Collectors.toList());
	}

	private List<SeekHelpInfoVO> convertSeekHelpInfoList(List<SeekHelpInfoPO> list) {
		return list.stream().filter(Objects::nonNull).map(SeekHelpInfoVO::new).collect(Collectors.toList());
	}

	private List<HelpInfoPO> calSimilarityByHelpHistory(List<HelpInfoPO> allHelpInfoList, List<HelpSearchHistoryPO> historyList, Long userId) {
		// 要排除自己的！
		List<Pair<Double, HelpInfoPO>> similarityHelpInfoList = new ArrayList<>();
		for (HelpSearchHistoryPO history : historyList) {
			Pair<Double, HelpInfoPO> highestScoreHelpInfo = new Pair<>(-1.0, null);
			for (HelpInfoPO helpInfo : allHelpInfoList) {
				// 排除自己的信息
				if (helpInfo.getUserId().equals(userId)) {
					continue;
				}
				SimilaritySentenceVO similarity = querySentenceSimilarity(helpInfo.getName(), history.getKeyword());
				if (similarity.getScore() > highestScoreHelpInfo.getKey()) {
					highestScoreHelpInfo = new Pair<>(similarity.getScore(), helpInfo);
				}
			}

			if (highestScoreHelpInfo.getKey() > MIN_SIMILARITY_SCORE) {
				similarityHelpInfoList.add(highestScoreHelpInfo);
			}
		}

		// 然后排序返回
		return compareHelpSimilarity(similarityHelpInfoList);
	}

	private List<SeekHelpInfoPO> calSimilarityBySeekHelpHistory(List<SeekHelpInfoPO> allHelpInfoList, List<SeekHelpSearchHistoryPO> historyList, Long userId) {
		List<Pair<Double, SeekHelpInfoPO>> similaritySeekHelpInfoList = new ArrayList<>();
		for (SeekHelpSearchHistoryPO history : historyList) {
			Pair<Double, SeekHelpInfoPO> highestScoreHelpInfo = new Pair<>(-1.0, null);
			for (SeekHelpInfoPO seekHelpInfo : allHelpInfoList) {
				// 排除自己的信息
				if (seekHelpInfo.getUserId().equals(userId)) {
					continue;
				}
				SimilaritySentenceVO similarity = querySentenceSimilarity(seekHelpInfo.getName(), history.getKeyword());
				if (similarity.getScore() > highestScoreHelpInfo.getKey()) {
					highestScoreHelpInfo = new Pair<>(similarity.getScore(), seekHelpInfo);
				}
			}

			// 只有大于阙值的才可以加入
			if (highestScoreHelpInfo.getKey() > MIN_SIMILARITY_SCORE) {
				similaritySeekHelpInfoList.add(highestScoreHelpInfo);
			}
		}

		// 然后排序返回
		return compareSeekHelpSimilarity(similaritySeekHelpInfoList);
	}

	private List<HelpInfoVO> calHelpSimilarityByClick(List<HelpInfoPO> allHelpInfoList, List<HelpClickHistoryVO> clickList, Long userId) {
		// 首先先按访问的时间长短排序一下
		// 访问时间长的在前
		sortByClickTime(clickList);
		List<HelpInfoVO> clickHelpInfoList = clickList.stream().map(HelpClickHistoryVO::getHelpInfo).collect(Collectors.toList());
		List<Pair<Double, HelpInfoPO>> similarityHelpInfoList = new ArrayList<>();
		for (HelpInfoPO helpInfo : allHelpInfoList) {
			if (helpInfo.getUserId().equals(userId)) {
				continue;
			}
			Pair<Double, HelpInfoPO> highestScoreHelpInfo = new Pair<>(-1D, null);
			for (HelpInfoVO clickHelpInfo : clickHelpInfoList) {
				if (clickHelpInfo == null || helpInfo.getName().equals(clickHelpInfo.getName())) {
					continue;
				}
				SimilaritySentenceVO similarity = querySentenceSimilarity(helpInfo.getName(), clickHelpInfo.getName());
				if (similarity.getScore() > highestScoreHelpInfo.getKey()) {
					// 将相似度加入
					highestScoreHelpInfo = new Pair<>(similarity.getScore(), helpInfo);
				}
			}

			if (highestScoreHelpInfo.getKey() > MIN_SIMILARITY_SCORE) {
				similarityHelpInfoList.add(highestScoreHelpInfo);
			}
		}

		// 然后排序返回
		List<HelpInfoPO> compareList = compareHelpSimilarity(similarityHelpInfoList);
		return convertHelpInfoList(compareList);
	}

	private List<SeekHelpInfoVO> calSeekHelpSimilarityByClick(List<SeekHelpInfoPO> allSeekHelpInfoList, List<SeekHelpClickHistoryVO> clickList, Long userId) {
		// 首先先按访问的时间长短排序一下
		// 访问时间长的在前
		sortByClickTime(clickList);
		List<SeekHelpInfoVO> clickSeekHelpList = clickList.stream().map(SeekHelpClickHistoryVO::getSeekHelpInfo).collect(Collectors.toList());

		List<Pair<Double, SeekHelpInfoPO>> similarityHelpInfoList = new ArrayList<>();
		for (SeekHelpInfoPO seekHelpInfo : allSeekHelpInfoList) {
			if (seekHelpInfo.getUserId().equals(userId)) {
				continue;
			}
			Pair<Double, SeekHelpInfoPO> highestScoreSeekHelpInfo = new Pair<>(-1D, null);
			for (SeekHelpInfoVO clickHelpInfo : clickSeekHelpList) {
				if (clickHelpInfo == null || seekHelpInfo.getName().equals(clickHelpInfo.getName())) {
					continue;
				}
				SimilaritySentenceVO similarity = querySentenceSimilarity(seekHelpInfo.getName(), clickHelpInfo.getName());
				if (similarity.getScore() > highestScoreSeekHelpInfo.getKey()) {
					// 将相似度加入
					highestScoreSeekHelpInfo = new Pair<>(similarity.getScore(), seekHelpInfo);
				}
			}

			if (highestScoreSeekHelpInfo.getKey() > MIN_SIMILARITY_SCORE) {
				similarityHelpInfoList.add(highestScoreSeekHelpInfo);
			}
		}

		// 然后排序返回
		List<SeekHelpInfoPO> compareList = compareSeekHelpSimilarity(similarityHelpInfoList);
		return convertSeekHelpInfoList(compareList);
	}

	private void sortByClickTime(List<? extends BaseClickHistoryVO> clickHistoryList) {
		clickHistoryList.sort((o1, o2) -> {
			long gap1 = Optional.ofNullable(o1.getClickEndTime()).map(Date::getTime).orElse(o1.getClickStartTime().getTime()) - Optional.ofNullable(o1.getClickStartTime()).map(Date::getTime).orElse(0L);
			long gap2 = Optional.ofNullable(o2.getClickEndTime()).map(Date::getTime).orElse(o2.getClickStartTime().getTime()) - Optional.ofNullable(o2.getClickStartTime()).map(Date::getTime).orElse(0L);
			return (int) (gap2 - gap1);
		});
	}


	private List<HelpInfoPO> compareHelpSimilarity(List<Pair<Double, HelpInfoPO>> list) {
		// 自定义的比较器排序
		// 筛选相似度前20的 这里我们认为数据很多 前20的相似度已经相差无几
		list.sort(Comparator.comparing(Pair::getKey, Comparator.nullsLast(Comparator.naturalOrder())));
		return list.stream().map(Pair::getValue).limit(MAX_RECOMMEND_LIST_SIZE).collect(Collectors.toList());
	}

	private List<SeekHelpInfoPO> compareSeekHelpSimilarity(List<Pair<Double, SeekHelpInfoPO>> list) {
		// 自定义的比较器排序
		// 筛选相似度前20的 这里我们认为数据很多 前20的相似度已经相差无几
		list.sort(Comparator.comparing(Pair::getKey, Comparator.nullsLast(Comparator.naturalOrder())));
		return list.stream().map(Pair::getValue).limit(MAX_RECOMMEND_LIST_SIZE).collect(Collectors.toList());
	}

	private List<HelpInfoPO> randomRemoveHelpList(List<HelpInfoPO> list) {
		if (list.size() <= MAX_RECOMMEND_LIST_SIZE) {
			return list;
		}
		// 随机删除
		List<HelpInfoPO> newList = new ArrayList<>();
		boolean[] isSelected = new boolean[list.size()];

		while (newList.size() <= MAX_RECOMMEND_LIST_SIZE) {
			int index = random.nextInt(list.size());
			while (isSelected[index]) {
				index = random.nextInt(list.size());
			}
			newList.add(list.get(index));
			isSelected[index] = true;
		}

		return newList;
	}

	private List<SeekHelpInfoPO> randomRemoveSeekHelpList(List<SeekHelpInfoPO> list) {
		if (list.size() <= MAX_RECOMMEND_LIST_SIZE) {
			return list;
		}
		// 随机删除
		List<SeekHelpInfoPO> newList = new ArrayList<>();
		boolean[] isSelected = new boolean[list.size()];

		while (newList.size() <= MAX_RECOMMEND_LIST_SIZE) {
			int index = random.nextInt(list.size());
			while (isSelected[index]) {
				index = random.nextInt(list.size());
			}
			newList.add(list.get(index));
			isSelected[index] = true;
		}

		return newList;
	}

	private List<HelpInfoPO> addHelpInfoExceptDuplicate(List<HelpInfoPO> recommendList, List<HelpInfoVO> list, Long userId) {
		// 去重之后添加
		List<HelpInfoPO> newList = new ArrayList<>(recommendList);
		List<HelpInfoPO> helpInfoPoList = list.stream().map(HelpInfoPO::new).collect(Collectors.toList());
		for (HelpInfoPO helpInfo : helpInfoPoList) {
			if (!helpInfo.getUserId().equals(userId) && !contains(newList, helpInfo)) {
				newList.add(helpInfo);
			}
		}

		return newList;
	}

	private List<SeekHelpInfoPO> addSeekHelpInfoExceptDuplicate(List<SeekHelpInfoPO> recommendList, List<SeekHelpInfoVO> list, Long userId) {
		// 去重之后添加
		List<SeekHelpInfoPO> newList = new ArrayList<>(recommendList);
		List<SeekHelpInfoPO> seekHelpInfoPoList = list.stream().map(SeekHelpInfoPO::new).collect(Collectors.toList());
		for (SeekHelpInfoPO seekHelpInfo : seekHelpInfoPoList) {
			if (!seekHelpInfo.getUserId().equals(userId) && !contains(newList, seekHelpInfo)) {
				newList.add(seekHelpInfo);
			}
		}

		return newList;
	}

	private boolean contains(List<? extends BaseInfoPO> list, BaseInfoPO helpInfo) {
		for (BaseInfoPO listHelpInfo : list) {
			if (listHelpInfo.getId() != null && listHelpInfo.getId().equals(helpInfo.getId())) {
				return true;
			}
		}

		return false;
	}

	private boolean isEmpty(List<?> list) {
		return list == null || list.isEmpty();
	}

	@SuppressWarnings("unchecked")
	private boolean isClickBefore(BaseInfoPO info, List<? extends BaseClickHistoryVO> clickHistoryList, Class<?> clazz) {
		if (clazz.isAssignableFrom(HelpClickHistoryVO.class)) {
			List<HelpClickHistoryVO> list = (List<HelpClickHistoryVO>) clickHistoryList;
			for (HelpClickHistoryVO clickHistory : list) {
				if (clickHistory == null || clickHistory.getHelpInfo() == null) {
					continue;
				}
				if (clickHistory.getHelpInfo().getId().equals(info.getId())) {
					return true;
				}
			}
		} else if (clazz.isAssignableFrom(SeekHelpClickHistoryVO.class)) {
			List<SeekHelpClickHistoryVO> list = (List<SeekHelpClickHistoryVO>) clickHistoryList;
			for (SeekHelpClickHistoryVO clickHistory : list) {
				if (clickHistory == null || clickHistory.getSeekHelpInfo() == null) {
					continue;
				}
				if (clickHistory.getSeekHelpInfo().getId().equals(info.getId())) {
					return true;
				}
			}
		}

		return false;
	}

	@SuppressWarnings("unchecked")
	private List<? extends BaseInfoPO> removeClickBefore(List<? extends BaseInfoPO> recommendList, List<? extends BaseClickHistoryVO> clickHistoryList, Class<?> clazz) {
		if (clazz.isAssignableFrom(SeekHelpInfoPO.class)) {
			List<SeekHelpInfoPO> finalRecommendList = new ArrayList<>();
			List<SeekHelpInfoPO> newList = (List<SeekHelpInfoPO>) recommendList;
			for (SeekHelpInfoPO recommendHelpInfo : newList) {
				if (isClickBefore(recommendHelpInfo, clickHistoryList, SeekHelpClickHistoryVO.class)) {
					continue;
				}
				finalRecommendList.add(recommendHelpInfo);
			}

			return finalRecommendList;
		} else if (clazz.isAssignableFrom(HelpInfoPO.class)) {
			List<HelpInfoPO> finalRecommendList = new ArrayList<>();
			List<HelpInfoPO> newList = (List<HelpInfoPO>) recommendList;
			for (HelpInfoPO recommendHelpInfo : newList) {
				if (isClickBefore(recommendHelpInfo, clickHistoryList, HelpClickHistoryVO.class)) {
					continue;
				}
				finalRecommendList.add(recommendHelpInfo);
			}

			return finalRecommendList;
		}

		return new ArrayList<>();
	}

	@SuppressWarnings("unchecked")
	private List<? extends BaseInfoPO> getTagEqualList(List<? extends BaseInfoPO> allInfoList, List<? extends BaseInfoPO> userList, long userId, Class<?> clazz) {
		if (clazz.isAssignableFrom(HelpInfoPO.class)) {
			List<HelpInfoPO> tagEqualList = new ArrayList<>();
			List<HelpInfoPO> allHelpInfoList = (List<HelpInfoPO>) allInfoList;
			List<HelpInfoPO> userHelpInfoList = (List<HelpInfoPO>) userList;
			for (HelpInfoPO helpInfo : allHelpInfoList) {
				for (HelpInfoPO myHelpInfo : userHelpInfoList) {
					if (helpInfo.getTag().equals(myHelpInfo.getTag()) && helpInfo.getUserId() != userId) {
						tagEqualList.add(helpInfo);
						break;
					}
				}
			}

			return tagEqualList;
		} else if (clazz.isAssignableFrom(SeekHelpInfoPO.class)) {
			List<SeekHelpInfoPO> tagEqualList = new ArrayList<>();
			List<SeekHelpInfoPO> allSeekHelpInfoList = (List<SeekHelpInfoPO>) allInfoList;
			List<SeekHelpInfoPO> userSeekHelpInfoList = (List<SeekHelpInfoPO>) userList;
			for (SeekHelpInfoPO seekHelpInfo : allSeekHelpInfoList) {
				for (SeekHelpInfoPO mySeekHelpInfo : userSeekHelpInfoList) {
					if (seekHelpInfo.getTag().equals(mySeekHelpInfo.getTag()) && seekHelpInfo.getUserId() != userId) {
						tagEqualList.add(seekHelpInfo);
						break;
					}
				}
			}

			return tagEqualList;
		}

		return new ArrayList<>();
	}

	@SuppressWarnings("unchecked")
	private List<? extends BaseInfoPO> removeNotInProgress(List<? extends BaseInfoPO> recommendList, Class<?> clazz) {
		if (clazz.isAssignableFrom(HelpInfoPO.class)) {
			List<HelpInfoPO> helpInfoList = new ArrayList<>();
			List<HelpInfoPO> list = (List<HelpInfoPO>) recommendList;
			for (HelpInfoPO helpInfo : list) {
				if (HelpFinishStatus.IN_PROGRESS.getValue().equals(helpInfo.getFinishStatus())) {
					helpInfoList.add(helpInfo);
				}
			}

			return helpInfoList;
		} else if (clazz.isAssignableFrom(SeekHelpInfoPO.class)) {
			List<SeekHelpInfoPO> seekHelpInfoList = new ArrayList<>();
			List<SeekHelpInfoPO> list = (List<SeekHelpInfoPO>) recommendList;
			for (SeekHelpInfoPO seekHelpInfo : list) {
				if (SeekHelpFinishStatus.IN_PROGRESS.getValue().equals(seekHelpInfo.getFinishStatus())) {
					seekHelpInfoList.add(seekHelpInfo);
				}
			}

			return seekHelpInfoList;
		}

		return new ArrayList<>();
	}
}

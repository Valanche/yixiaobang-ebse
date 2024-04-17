package com.weixin.njuteam.service.impl.help;

import com.weixin.njuteam.dao.help.ClickHistoryMapper;
import com.weixin.njuteam.entity.po.help.HelpClickHistoryPO;
import com.weixin.njuteam.entity.po.help.SeekHelpClickHistoryPO;
import com.weixin.njuteam.entity.vo.help.HelpClickHistoryVO;
import com.weixin.njuteam.entity.vo.help.SeekHelpClickHistoryVO;
import com.weixin.njuteam.service.ClickHistoryService;
import com.weixin.njuteam.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author Zyi
 */
@Service
@CacheConfig(cacheNames = "helpClick")
public class ClickHistoryServiceImpl implements ClickHistoryService {

	private final UserService userService;
	private final ClickHistoryMapper clickHistoryMapper;

	@Autowired
	public ClickHistoryServiceImpl(UserService userService, ClickHistoryMapper clickHistoryMapper) {
		this.userService = userService;
		this.clickHistoryMapper = clickHistoryMapper;
	}

	@Override
	@Cacheable(cacheNames = "helpClick", key = "#id")
	public HelpClickHistoryVO queryHelpClickById(@NotNull Long id) {
		HelpClickHistoryPO helpClickHistoryPo = clickHistoryMapper.queryClickHelpHistoryById(id);

		return helpClickHistoryPo == null ? null : new HelpClickHistoryVO(helpClickHistoryPo);
	}

	@Override
	@Cacheable(cacheNames = "seekHelpClick", key = "#id")
	public SeekHelpClickHistoryVO querySeekHelpClickById(@NotNull Long id) {
		SeekHelpClickHistoryPO helpClickHistoryPo = clickHistoryMapper.queryClickSeekHelpHistoryById(id);

		return helpClickHistoryPo == null ? null : new SeekHelpClickHistoryVO(helpClickHistoryPo);
	}

	@Override
	public List<HelpClickHistoryVO> queryHelpClickByUserId(@NotNull String openId) {
		long userId = userService.queryUserId(openId);
		if (userId <= 0) {
			return new ArrayList<>();
		}

		// mybatis对list返回的话会先初始化，如果找不到数据也只是返回一个size为0的list
		List<HelpClickHistoryPO> historyPoList = clickHistoryMapper.queryUserClickHelpHistory(userId);
		sortHelpClickByTime(historyPoList);
		return convertHelpList(historyPoList);
	}

	@Override
	public List<SeekHelpClickHistoryVO> querySeekHelpClickByUserId(@NotNull String openId) {
		long userId = userService.queryUserId(openId);
		if (userId <= 0) {
			return new ArrayList<>();
		}

		List<SeekHelpClickHistoryPO> historyPoList = clickHistoryMapper.queryUserClickSeekHelpHistory(userId);
		sortSeekHelpClickByTime(historyPoList);
		return convertSeekHelpList(historyPoList);
	}

	@Override
	@CacheEvict(cacheNames = "helpClick", key = "#id")
	public boolean deleteHelpClickById(@NotNull Long id) {
		return clickHistoryMapper.deleteClickHelpHistoryById(id) > 0;
	}

	@Override
	@CacheEvict(cacheNames = "seekHelpClick", key = "#id")
	public boolean deleteSeekHelpClickById(@NotNull Long id) {
		return clickHistoryMapper.deleteClickSeekHelpHistoryById(id) > 0;
	}

	private List<HelpClickHistoryVO> convertHelpList(List<HelpClickHistoryPO> list) {
		return list.stream().filter(Objects::nonNull).map(HelpClickHistoryVO::new).collect(Collectors.toList());
	}

	private List<SeekHelpClickHistoryVO> convertSeekHelpList(List<SeekHelpClickHistoryPO> list) {
		return list.stream().filter(Objects::nonNull).map(SeekHelpClickHistoryVO::new).collect(Collectors.toList());
	}

	private void sortHelpClickByTime(List<HelpClickHistoryPO> list) {
		// 从时间晚到时间早排序
		// 即越新的索引越小
		// getTime()获得的越大证明时间越晚
		list.sort((o1, o2) -> (int) (o2.getClickStartTime().getTime() - o1.getClickStartTime().getTime()));
	}

	private void sortSeekHelpClickByTime(List<SeekHelpClickHistoryPO> list) {
		list.sort((o1, o2) -> (int) (o2.getClickStartTime().getTime() - o1.getClickStartTime().getTime()));
	}
}

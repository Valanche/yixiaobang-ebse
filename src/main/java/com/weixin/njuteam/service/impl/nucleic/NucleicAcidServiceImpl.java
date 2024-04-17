package com.weixin.njuteam.service.impl.nucleic;

import com.weixin.njuteam.dao.nucleic.NucleicAcidMapper;
import com.weixin.njuteam.entity.po.nucleic.NucleicAcidPO;
import com.weixin.njuteam.entity.vo.nucleic.NucleicAcidVO;
import com.weixin.njuteam.service.NucleicAcidService;
import com.weixin.njuteam.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author Zyi
 */
@Service
public class NucleicAcidServiceImpl implements NucleicAcidService {

	private final NucleicAcidMapper mapper;
	private final UserService userService;

	@Autowired
	public NucleicAcidServiceImpl(NucleicAcidMapper mapper, UserService userService) {
		this.mapper = mapper;
		this.userService = userService;
	}

	@Override
	public List<NucleicAcidVO> getUserNucleicAcid(String openId) {
		if (openId == null) {
			return null;
		}

		long userId = userService.queryUserId(openId);
		if (userId <= 0) {
			return null;
		}

		List<NucleicAcidPO> nucleicAcidPoList = mapper.queryUserNucleicAcid(userId);
		return nucleicAcidPoList == null ? null : convertList(nucleicAcidPoList);
	}

	private List<NucleicAcidVO> convertList(List<NucleicAcidPO> list) {
		return list.stream().filter(Objects::nonNull).map(NucleicAcidVO::new).collect(Collectors.toList());
	}
}

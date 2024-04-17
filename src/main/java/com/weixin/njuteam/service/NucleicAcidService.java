package com.weixin.njuteam.service;

import com.weixin.njuteam.entity.vo.nucleic.NucleicAcidVO;

import java.util.List;

/**
 * @author Zyi
 */
public interface NucleicAcidService {

	/**
	 * 根据openid来获得用户的所有核酸状态
	 *
	 * @param openId open id
	 * @return list of nucleic acid value object
	 */
	List<NucleicAcidVO> getUserNucleicAcid(String openId);
}

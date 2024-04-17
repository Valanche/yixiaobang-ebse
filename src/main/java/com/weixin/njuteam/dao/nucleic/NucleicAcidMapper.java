package com.weixin.njuteam.dao.nucleic;

import com.weixin.njuteam.entity.po.nucleic.NucleicAcidPO;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author Zyi
 */
@Mapper
@Repository
public interface NucleicAcidMapper {

	/**
	 * 获得某个用户的所有核酸列表
	 *
	 * @param userId user id
	 * @return list of nucleic acid persistent object
	 */
	List<NucleicAcidPO> queryUserNucleicAcid(long userId);
}

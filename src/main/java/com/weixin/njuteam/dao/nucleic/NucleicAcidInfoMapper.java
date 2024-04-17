package com.weixin.njuteam.dao.nucleic;

import com.weixin.njuteam.entity.po.InfoAssociatedTestingPO;
import com.weixin.njuteam.entity.po.nucleic.NucleicAcidInfoPO;
import com.weixin.njuteam.entity.po.nucleic.UpdateInfoPO;
import com.weixin.njuteam.enums.FinishStatus;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.sql.SQLException;
import java.util.List;

/**
 * @author Zyi
 */
@Mapper
@Repository
public interface NucleicAcidInfoMapper {

	/**
	 * 根据用户openid获取所有的核酸上报通知记录
	 *
	 * @param openId user open id
	 * @return 用户的所有核酸通知记录
	 */
	List<NucleicAcidInfoPO> queryInfoByOpenId(String openId);

	/**
	 * 根据上报通知id来获取对应检测的时间
	 *
	 * @param infoId info id
	 * @param userId user id
	 * @return info and testing po
	 */
	InfoAssociatedTestingPO queryTestingByInfoId(long infoId, long userId);

	/**
	 * query info by user id and title
	 *
	 * @param userId   user id
	 * @param oldTitle title
	 * @return info persistent object
	 */
	NucleicAcidInfoPO queryInfoByUserIdAndTitle(long userId, String oldTitle);

	/**
	 * 获取用户待上报的数量
	 *
	 * @param userId       user id
	 * @param finishStatus finish status
	 * @return the amount of reporting info which is in progress
	 */
	int queryInfoCount(long userId, FinishStatus finishStatus);

	/**
	 * 插入新的核酸上报通知
	 *
	 * @param infoPo 核酸上报通知
	 * @return 插入影响的行数
	 */
	int insertInfo(NucleicAcidInfoPO infoPo);

	/**
	 * 根据id来查询核酸上报通知记录
	 *
	 * @param id info id
	 * @return info
	 */
	NucleicAcidInfoPO queryInfoById(long id);

	/**
	 * 根据user id来查找该用户拥有的核酸上报通知
	 *
	 * @param userId user id
	 * @return info list
	 * @throws SQLException query info fail
	 */
	List<NucleicAcidInfoPO> queryInfoByUserId(long userId) throws SQLException;

	/**
	 * update info in db
	 *
	 * @param infoPo info persistent object
	 * @return the amount of row affected by update
	 */
	int updateInfo(NucleicAcidInfoPO infoPo);

	/**
	 * update info comment
	 *
	 * @param infoId  info id
	 * @param comment comment
	 * @return the amount of row affected by update
	 */
	int updateComment(long infoId, String comment);

	/**
	 * 更新核酸截图文件名
	 *
	 * @param id        info id
	 * @param imageName image name
	 * @return the amount of row affected by update
	 */
	int updateImageName(Long id, String imageName);

	/**
	 * 更新核酸通知的完成状态
	 *
	 * @param id     info id
	 * @param status user is finish nucleic acid
	 * @return 更新影响的行数
	 */
	int updateRecordFinish(long id, FinishStatus status);

	/**
	 * update info by user id and old title
	 *
	 * @param infoPo info persistent object
	 * @return the amount of row affected by update operation
	 */
	int updateTestingByUserIdAndTitle(UpdateInfoPO infoPo);

	/**
	 * open reporting info remind
	 *
	 * @param infoId       reporting info id
	 * @param isOpenRemind is open remind
	 * @return the amount of row affect by update
	 */
	int openRemind(long infoId, boolean isOpenRemind);

	/**
	 * 删除核酸上报通知
	 *
	 * @param id info id
	 * @return 删除影响的行数
	 */
	int deleteInfo(long id);

	/**
	 * delete info by user id and title
	 *
	 * @param userId user id
	 * @param title  title
	 * @return the amount of row affect by delete
	 */
	int deleteInfoByUserIdAndTitle(long userId, String title);
}

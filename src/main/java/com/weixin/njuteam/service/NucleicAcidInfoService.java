package com.weixin.njuteam.service;

import com.weixin.njuteam.entity.vo.nucleic.NucleicAcidInfoVO;
import com.weixin.njuteam.entity.vo.nucleic.RecognizeResultVO;
import com.weixin.njuteam.enums.FinishStatus;
import com.weixin.njuteam.exception.AddTaskException;
import com.weixin.njuteam.exception.UpdateNucleicAcidException;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotNull;
import java.sql.SQLException;
import java.util.List;

/**
 * @author Zyi
 */
public interface NucleicAcidInfoService {

	/**
	 * 通过info id获取核酸上报通知
	 *
	 * @param id info id
	 * @return nucleic acid info value object
	 */
	NucleicAcidInfoVO queryInfoById(@NotNull Long id);

	/**
	 * 获取用户对应的核酸上报通知
	 *
	 * @param openId user open id
	 * @return info list
	 * @throws SQLException query info fail
	 */
	List<NucleicAcidInfoVO> queryInfoByUserId(@NotNull String openId) throws SQLException;

	/**
	 * 获取用户待上报的数量
	 *
	 * @param openId       user open id
	 * @param finishStatus finish status
	 * @return the amount of booking info which is in progress
	 */
	int queryInfoCount(@NotNull String openId, FinishStatus finishStatus);

	/**
	 * 插入核酸上报通知记录
	 *
	 * @param infoVo nucleic acid info
	 * @return true if insert successfully, false otherwise
	 * @throws AddTaskException add task failed
	 */
	NucleicAcidInfoVO insertInfo(NucleicAcidInfoVO infoVo) throws AddTaskException;

	/**
	 * OCR识别核酸截图并消除对应的核酸提醒
	 *
	 * @param image  核酸截图
	 * @param openId user open id
	 * @param infoId info id
	 * @return result
	 */
	RecognizeResultVO recognize(MultipartFile image, @NotNull String openId, @NotNull Long infoId);

	/**
	 * 更新未做核酸的理由
	 *
	 * @param infoId  info id
	 * @param openId  user open id
	 * @param comment 未做核酸的理由
	 * @return nucleic acid info value object
	 */
	NucleicAcidInfoVO updateComment(@NotNull Long infoId, @NotNull String openId, String comment) throws UpdateNucleicAcidException;

	/**
	 * 修正结果
	 *
	 * @param userName username
	 * @param openId   user open id
	 * @param infoId   info id
	 * @return result
	 */
	NucleicAcidInfoVO fixResult(String userName, @NotNull String openId, @NotNull Long infoId);

	/**
	 * update nucleic info in db
	 *
	 * @param infoVo nucleic info value object
	 * @return true if update successfully, false otherwise
	 */
	NucleicAcidInfoVO updateInfo(NucleicAcidInfoVO infoVo);

	/**
	 * 更新完成状态
	 *
	 * @param id           info id
	 * @param finishStatus finish status
	 * @return true if update successfully, false otherwise
	 */
	boolean updateRecordFinish(@NotNull Long id, FinishStatus finishStatus);

	/**
	 * update info
	 *
	 * @param infoVo   info value object
	 * @param oldTitle old title
	 * @return true if update successfully, false otherwise
	 */
	NucleicAcidInfoVO updateInfoByUserIdAndTitle(NucleicAcidInfoVO infoVo, String oldTitle);

	/**
	 * open testing info remind
	 *
	 * @param openId       user open id
	 * @param infoId       info id
	 * @param isOpenRemind is open remind
	 * @return reporting info value object after open remind
	 */
	NucleicAcidInfoVO openRemind(@NotNull String openId, @NotNull Long infoId, Boolean isOpenRemind);

	/**
	 * 根据id删除核酸上报通知
	 *
	 * @param id info id
	 * @return true if delete successfully, false otherwise
	 */
	boolean deleteInfoById(@NotNull Long id);

	/**
	 * delete info by user id and title
	 *
	 * @param userId user id
	 * @param title  title
	 * @return true if delete successfully, false otherwise
	 */
	boolean deleteInfoByUserIdAndTitle(@NotNull Long userId, String title);
}

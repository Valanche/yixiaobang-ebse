package com.weixin.njuteam.entity.po.nucleic;

import com.alibaba.fastjson.annotation.JSONField;
import com.weixin.njuteam.enums.FinishStatus;
import lombok.*;
import org.apache.ibatis.type.Alias;
import org.springframework.beans.BeanUtils;

import java.util.Date;
import java.util.Optional;

/**
 * @author Zyi
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
@Alias("updateInfoPO")
public class UpdateInfoPO {

	/**
	 * 通知id
	 */
	private Long id;
	/**
	 * 被通知的用户id
	 */
	private Long userId;
	/**
	 * 发布该通知的管理员id
	 */
	private Long managerId;
	/**
	 * 通知的开始时间
	 */
	private Date deadLine;
	/**
	 * 通知的内容
	 */
	private String title;
	/**
	 * 通知是否完成
	 */
	private FinishStatus status;
	/**
	 * 完成的截图文件名
	 */
	private String imageName;
	/**
	 * 是否打开提醒
	 */
	private Boolean isOpenRemind;
	/**
	 * 旧标题
	 */
	private String oldTitle;

	public UpdateInfoPO(NucleicAcidInfoPO infoPo, String oldTitle) {
		BeanUtils.copyProperties(infoPo, this);
		this.oldTitle = oldTitle;
	}

	@JSONField(name = "finishStatus")
	public String getStatus() {
		return Optional.ofNullable(status).map(FinishStatus::getValue).orElse(null);
	}

	@JSONField(name = "finishStatus")
	public void setStatus(String value) {
		this.status = FinishStatus.getStatusByValue(value);
	}
}

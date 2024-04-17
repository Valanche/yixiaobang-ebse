package com.weixin.njuteam.entity.po.help;

import com.weixin.njuteam.entity.po.help.base.BaseInfoPO;
import com.weixin.njuteam.entity.vo.help.HelpInfoVO;
import com.weixin.njuteam.enums.HelpFinishStatus;
import com.weixin.njuteam.enums.HelpType;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.apache.ibatis.type.Alias;
import org.springframework.beans.BeanUtils;

import java.util.List;
import java.util.Optional;

/**
 * @author Zyi
 */
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Alias("helpInfoPO")
public class HelpInfoPO extends BaseInfoPO {

	/**
	 * 帮忙的类型
	 */
	private HelpType helpType;
	/**
	 * 图片url列表
	 */
	private List<HelpImage> urlList;
	/**
	 * 帮助信息的完成状态
	 */
	private HelpFinishStatus finishStatus;

	public HelpInfoPO(HelpInfoVO helpInfoVo) {
		BeanUtils.copyProperties(helpInfoVo, this);
	}

	public String getHelpType() {
		return Optional.ofNullable(helpType).map(HelpType::getValue).orElse(null);
	}

	public void setHelpType(String value) {
		this.helpType = HelpType.getTypeByValue(value);
	}

	public String getFinishStatus() {
		return Optional.ofNullable(finishStatus).map(HelpFinishStatus::getValue).orElse(null);
	}

	public void setFinishStatus(String value) {
		this.finishStatus = HelpFinishStatus.getStatusByValue(value);
	}
}

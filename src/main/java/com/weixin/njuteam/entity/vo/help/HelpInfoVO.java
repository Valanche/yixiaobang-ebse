package com.weixin.njuteam.entity.vo.help;

import com.alibaba.fastjson.annotation.JSONField;
import com.weixin.njuteam.entity.po.help.HelpImage;
import com.weixin.njuteam.entity.po.help.HelpInfoPO;
import com.weixin.njuteam.entity.vo.help.base.BaseInfoVO;
import com.weixin.njuteam.enums.HelpFinishStatus;
import com.weixin.njuteam.enums.HelpType;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.springframework.beans.BeanUtils;

import java.util.List;
import java.util.Optional;

/**
 * @author Zyi
 */
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class HelpInfoVO extends BaseInfoVO {

	/**
	 * 帮忙的类型
	 */
	@ApiModelProperty(value = "帮忙的类型", name = "helpType", example = "帮助")
	private HelpType helpType;
	/**
	 * 图片url列表
	 */
	@ApiModelProperty(value = "图片url列表", name = "urlList", example = "xxx")
	private List<HelpImage> urlList;
	/**
	 * 帮助信息的完成状态
	 */
	@ApiModelProperty(value = "帮助信息的状态", name = "finishStatus", example = "进行中")
	private HelpFinishStatus finishStatus;

	public HelpInfoVO(HelpInfoPO helpInfoPo) {
		BeanUtils.copyProperties(helpInfoPo, this);
	}

	@JSONField(name = "helpType")
	public String getHelpType() {
		return Optional.ofNullable(helpType).map(HelpType::getValue).orElse(null);
	}

	@JSONField(name = "helpType")
	public void setHelpType(String value) {
		this.helpType = HelpType.getTypeByValue(value);
	}

	@JSONField(name = "finishStatus")
	public String getFinishStatus() {
		return Optional.ofNullable(finishStatus).map(HelpFinishStatus::getValue).orElse(null);
	}

	@JSONField(name = "finishStatus")
	public void setFinishStatus(String value) {
		this.finishStatus = HelpFinishStatus.getStatusByValue(value);
	}
}

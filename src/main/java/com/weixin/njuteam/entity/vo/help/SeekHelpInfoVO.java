package com.weixin.njuteam.entity.vo.help;

import com.alibaba.fastjson.annotation.JSONField;
import com.weixin.njuteam.entity.po.help.SeekHelpImage;
import com.weixin.njuteam.entity.po.help.SeekHelpInfoPO;
import com.weixin.njuteam.entity.vo.help.base.BaseInfoVO;
import com.weixin.njuteam.enums.SeekHelpFinishStatus;
import com.weixin.njuteam.enums.SeekHelpType;
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
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class SeekHelpInfoVO extends BaseInfoVO {

	/**
	 * 求助的类型
	 */
	@ApiModelProperty(value = "发布求助的类型", name = "seekHelpType", example = "求帮跑腿")
	private SeekHelpType seekHelpType;
	/**
	 * 图片url列表
	 */
	@ApiModelProperty(value = "图片url列表", name = "urlList", example = "xxx")
	private List<SeekHelpImage> urlList;
	/**
	 * 求助信息的完成状态
	 */
	@ApiModelProperty(value = "求助信息完成状态", name = "finishStatus", example = "进行中")
	private SeekHelpFinishStatus finishStatus;

	public SeekHelpInfoVO(SeekHelpInfoPO seekHelpInfoPo) {
		BeanUtils.copyProperties(seekHelpInfoPo, this);
	}

	@JSONField(name = "seekHelpType")
	public String getSeekHelpType() {
		return Optional.ofNullable(seekHelpType).map(SeekHelpType::getValue).orElse(null);
	}

	@JSONField(name = "seekHelpType")
	public void setSeekHelpType(String value) {
		this.seekHelpType = SeekHelpType.getTypeByValue(value);
	}

	@JSONField(name = "finishStatus")
	public String getFinishStatus() {
		return Optional.ofNullable(finishStatus).map(SeekHelpFinishStatus::getValue).orElse(null);
	}

	@JSONField(name = "finishStatus")
	public void setFinishStatus(String value) {
		this.finishStatus = SeekHelpFinishStatus.getStatusByValue(value);
	}
}

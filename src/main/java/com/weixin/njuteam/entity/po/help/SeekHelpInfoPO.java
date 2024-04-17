package com.weixin.njuteam.entity.po.help;

import com.weixin.njuteam.entity.po.help.base.BaseInfoPO;
import com.weixin.njuteam.entity.vo.help.SeekHelpInfoVO;
import com.weixin.njuteam.enums.SeekHelpFinishStatus;
import com.weixin.njuteam.enums.SeekHelpType;
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
@Alias("seekHelpInfoPO")
public class SeekHelpInfoPO extends BaseInfoPO {

	/**
	 * 求助的类型
	 */
	private SeekHelpType seekHelpType;
	/**
	 * 图片url列表
	 */
	private List<SeekHelpImage> urlList;
	/**
	 * 求助信息的完成状态
	 */
	private SeekHelpFinishStatus finishStatus;

	public SeekHelpInfoPO(SeekHelpInfoVO seekHelpInfoVo) {
		BeanUtils.copyProperties(seekHelpInfoVo, this);
	}

	public String getSeekHelpType() {
		return Optional.ofNullable(seekHelpType).map(SeekHelpType::getValue).orElse(null);
	}

	public void setSeekHelpType(String value) {
		this.seekHelpType = SeekHelpType.getTypeByValue(value);
	}

	public String getFinishStatus() {
		return Optional.ofNullable(finishStatus).map(SeekHelpFinishStatus::getValue).orElse(null);
	}

	public void setFinishStatus(String value) {
		this.finishStatus = SeekHelpFinishStatus.getStatusByValue(value);
	}

}

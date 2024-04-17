package com.weixin.njuteam.entity.vo.help;

import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.weixin.njuteam.entity.po.help.HelpInfoClickPO;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

/**
 * @author Zyi
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HelpInfoClickVO {

	/**
	 * 点击id
	 */
	@ApiModelProperty(value = "点击id", name = "id", example = "1")
	private Long id;
	/**
	 * 用户id
	 */
	@ApiModelProperty(value = "用户id", name = "userId", example = "1")
	private Long userId;
	/**
	 * help info id
	 */
	@ApiModelProperty(value = "帮助信息id", name = "helpId", example = "1")
	private Long helpId;
	/**
	 * 用户开始看该条信息的时间
	 */
	@JsonFormat(pattern = "yyyy/MM/dd HH:mm:ss", timezone = "GMT+8")
	@JSONField(format = "yyyy/MM/dd HH:mm:ss")
	@DateTimeFormat(pattern = "yyyy/MM/dd HH:mm:ss")
	@ApiModelProperty(value = "用户开始看该条帮助信息的时间 可以不用传", name = "startTime", example = "2022/04/25 9:55:00")
	private Date startTime;
	/**
	 * 用户结束看该条信息的时间
	 */
	@JsonFormat(pattern = "yyyy/MM/dd HH:mm:ss", timezone = "GMT+8")
	@JSONField(format = "yyyy/MM/dd HH:mm:ss")
	@DateTimeFormat(pattern = "yyyy/MM/dd HH:mm:ss")
	@ApiModelProperty(value = "用户结束看该条帮助信息的时间 可以不用传", name = "startTime", example = "2022/04/25 9:55:00")
	private Date endTime;

	public HelpInfoClickVO(HelpInfoClickPO clickPo) {
		BeanUtils.copyProperties(clickPo, this);
	}
}

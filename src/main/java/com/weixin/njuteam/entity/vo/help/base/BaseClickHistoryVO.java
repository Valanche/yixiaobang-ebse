package com.weixin.njuteam.entity.vo.help.base;

import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

/**
 * @author Zyi
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public abstract class BaseClickHistoryVO {

	/**
	 * 浏览记录id
	 */
	@ApiModelProperty(value = "浏览记录id", name = "id", example = "1")
	protected Long id;
	/**
	 * 该浏览记录所属的user id
	 */
	@ApiModelProperty(value = "该浏览记录所属的user id", name = "userId", example = "1")
	protected Long userId;
	/**
	 * 该浏览的开始时间
	 */
	@JsonFormat(pattern = "yyyy/MM/dd HH:mm:ss", timezone = "GMT+8")
	@JSONField(format = "yyyy/MM/dd HH:mm:ss")
	@DateTimeFormat(pattern = "yyyy/MM/dd HH:mm:ss")
	@ApiModelProperty(value = "该浏览的开始时间", name = "clickStartTime", example = "2022/04/25 9:55:00")
	protected Date clickStartTime;
	/**
	 * 该浏览的结束时间
	 */
	@JsonFormat(pattern = "yyyy/MM/dd HH:mm:ss", timezone = "GMT+8")
	@JSONField(format = "yyyy/MM/dd HH:mm:ss")
	@DateTimeFormat(pattern = "yyyy/MM/dd HH:mm:ss")
	@ApiModelProperty(value = "该浏览的结束时间", name = "clickEndTime", example = "2022/04/25 9:55:00")
	protected Date clickEndTime;
}

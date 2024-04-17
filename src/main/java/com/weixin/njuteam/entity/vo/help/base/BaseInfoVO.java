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
public abstract class BaseInfoVO {

	/**
	 * 帮忙的id
	 */
	@ApiModelProperty(value = "帮助id", name = "id", example = "1")
	private Long id;
	/**
	 * 发布该帮忙信息的用户id
	 */
	@ApiModelProperty(value = "发布该帮忙信息的用户id 传token即可", name = "userId", example = "1")
	private Long userId;
	/**
	 * 该信息发布的时间
	 */
	@JsonFormat(pattern = "yyyy/MM/dd HH:mm:ss", timezone = "GMT+8")
	@JSONField(format = "yyyy/MM/dd HH:mm:ss")
	@DateTimeFormat(pattern = "yyyy/MM/dd HH:mm:ss")
	@ApiModelProperty(value = "信息发布的时间", name = "publishDate", example = "2022/04/25 9:55:00")
	private Date publishDate;
	/**
	 * 帮忙的物品名字
	 */
	@ApiModelProperty(value = "帮忙的物品名字", name = "name", example = "一只口罩")
	private String name;
	/**
	 * 帮忙的截止日期
	 */
	@JsonFormat(pattern = "yyyy/MM/dd HH:mm:ss", timezone = "GMT+8")
	@JSONField(format = "yyyy/MM/dd HH:mm:ss")
	@DateTimeFormat(pattern = "yyyy/MM/dd HH:mm:ss")
	@ApiModelProperty(value = "该帮忙信息的截止日期", name = "deadLine", example = "2022/04/25 9:55:00")
	private Date deadLine;
	/**
	 * 备注
	 */
	@ApiModelProperty(value = "该帮忙信息的备注", name = "comment", example = "备注")
	private String comment;
	/**
	 * 所属的tag
	 */
	@ApiModelProperty(value = "该帮忙信息的tag", name = "tag", example = "日用品")
	private String tag;
	/**
	 * 紧急程度 1-5
	 */
	@ApiModelProperty(value = "紧急程度，1-5", name = "urgency", example = "2")
	private Integer urgency;
}

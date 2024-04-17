package com.weixin.njuteam.entity.po.help.base;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.Date;

/**
 * 信息基类
 *
 * @author Zyi
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public abstract class BaseInfoPO {

	/**
	 * id
	 */
	protected Long id;
	/**
	 * 发布该信息的用户id
	 */
	protected Long userId;
	/**
	 * 该信息发布的时间
	 */
	protected Date publishDate;
	/**
	 * 物品名字
	 */
	protected String name;
	/**
	 * 截止日期
	 */
	protected Date deadLine;
	/**
	 * 备注
	 */
	protected String comment;
	/**
	 * 所属的tag
	 */
	protected String tag;
	/**
	 * 紧急程度 1-5
	 */
	protected Integer urgency;
}

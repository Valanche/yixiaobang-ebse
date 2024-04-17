package com.weixin.njuteam.entity.po.help.base;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.Date;

/**
 * @author Zyi
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public abstract class BaseClickHistoryPO {

	/**
	 * 浏览记录id
	 */
	protected Long id;
	/**
	 * 该浏览记录所属的user id
	 */
	protected Long userId;
	/**
	 * 该浏览的开始时间
	 */
	protected Date clickStartTime;
	/**
	 * 该浏览的结束时间
	 */
	protected Date clickEndTime;
}

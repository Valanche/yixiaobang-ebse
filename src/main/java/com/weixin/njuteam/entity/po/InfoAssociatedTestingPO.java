package com.weixin.njuteam.entity.po;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.ibatis.type.Alias;

import java.util.Date;

/**
 * 关联上报和检测的对象
 *
 * @author Zyi
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Alias("infoAssociatedTestingPO")
public class InfoAssociatedTestingPO {
	/**
	 * 上报通知id
	 */
	private long infoId;
	/**
	 * 检测通知id
	 */
	private long testingId;
	/**
	 * 上报截止日期
	 */
	private Date infoDeadLine;
	/**
	 * 检测开始日期
	 */
	private Date testingStartTime;
	/**
	 * 核酸截止日期
	 */
	private Date testingEndTime;
	/**
	 * 检测的标题(和上报标题相同)
	 */
	private String title;
}

package com.weixin.njuteam.entity.vo.nucleic;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * @author Zyi
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TaskVO {

	private String name;

	private Date startTime;

	private RemindVO remindVo;
}

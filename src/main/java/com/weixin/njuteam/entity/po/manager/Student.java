package com.weixin.njuteam.entity.po.manager;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.ibatis.type.Alias;

/**
 * @author Zyi
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Alias("student")
public class Student {

	/**
	 * 小程序端用户id
	 */
	@ApiModelProperty(value = "用户id", name = "userId", example = "1")
	private Long userId;
	/**
	 * 学生名字
	 */
	@ApiModelProperty(value = "姓名", name = "name", example = "xxx")
	private String name;
	/**
	 * 学生学校
	 */
	@ApiModelProperty(value = "学校", name = "school", example = "南京大学")
	private String school;
	/**
	 * 学生院系
	 */
	@ApiModelProperty(value = "院系", name = "institute", example = "软件学院")
	private String institute;
	/**
	 * 学生专业
	 */
	@ApiModelProperty(value = "专业", name = "major", example = "软件工程")
	private String major;
	/**
	 * 学生年级
	 */
	@ApiModelProperty(value = "年级", name = "grade", example = "本科2020级")
	private String grade;
}

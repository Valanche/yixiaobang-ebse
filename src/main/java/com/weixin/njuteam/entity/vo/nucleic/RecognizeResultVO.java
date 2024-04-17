package com.weixin.njuteam.entity.vo.nucleic;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Zyi
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RecognizeResultVO {

	/**
	 * 返回的类型
	 */
	@ApiModelProperty(value = "识别名字是否成功", name = "isRecSuccess", example = "false")
	private Boolean isRecSuccess;
	/**
	 * 用户姓名
	 */
	@ApiModelProperty(value = "用户姓名，识别不成功的话会返回", name = "userName", example = "xxx")
	private String userName;
	/**
	 * 是否有在检测时间（包括后一天）的核酸检测记录
	 */
	@ApiModelProperty(value = "是否有在检测时间(包括后一天)的核酸检测记录", name = "isMatch", example = "false")
	private Boolean isMatch;
	/**
	 * 该结果对应的核酸上报通知
	 */
	@ApiModelProperty(value = "该结果对应的核酸上报通知", name = "infoVo", example = "xxx")
	private NucleicAcidInfoVO infoVo;
}

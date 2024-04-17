package com.weixin.njuteam.entity.vo.nucleic;

import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

/**
 * @author Zyi
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RemindVO {

	/**
	 * access token
	 */
	@ApiModelProperty(value = "access_token", name = "accessToken", example = "xxx")
	private String accessToken;
	/**
	 * open id
	 */
	@ApiModelProperty(value = "用户openid 不需要传输", name = "touser", example = "xxx")
	private String touser;
	/**
	 * template id
	 */
	@ApiModelProperty(value = "template id", name = "templateId", example = "xxx")
	private String templateId;
	/**
	 * 要开始执行的时间
	 */
	@JsonFormat(pattern = "yyyy/MM/dd HH:mm:ss", timezone = "GMT+8")
	@JSONField(format = "yyyy/MM/dd HH:mm:ss")
	@DateTimeFormat(pattern = "yyyy/MM/dd HH:mm:ss")
	@ApiModelProperty(value = "要开始执行的时间", name = "executeTime", example = "2022/04/23 16:45:45")
	private Date executeTime;
	/**
	 * data
	 */
	@ApiModelProperty(value = "data", name = "data", example = "xxx")
	private DataVO data;
}

package com.weixin.njuteam.entity.vo.manager;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Zyi
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoginManager {

	/**
	 * nickName
	 */
	@ApiModelProperty(value = "昵称", name = "nickName", example = "xxx")
	private String nickName;
	/**
	 * password
	 */
	@ApiModelProperty(value = "密码", name = "password", example = "xxx")
	private String password;
}

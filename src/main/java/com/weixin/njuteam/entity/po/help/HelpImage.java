package com.weixin.njuteam.entity.po.help;

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
@Alias("helpImage")
public class HelpImage {

	/**
	 * id
	 */
	@ApiModelProperty(value = "图片id", name = "id", example = "1")
	private Long id;
	/**
	 * help info id
	 */
	@ApiModelProperty(value = "帮助信息的id", name = "helpId", example = "1")
	private Long helpId;
	/**
	 * image url
	 */
	@ApiModelProperty(value = "图片url", name = "imageUrl", example = "xxx")
	private String imageUrl;
}

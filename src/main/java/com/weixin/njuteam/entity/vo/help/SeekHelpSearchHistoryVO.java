package com.weixin.njuteam.entity.vo.help;

import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.weixin.njuteam.entity.po.help.SeekHelpSearchHistoryPO;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.ibatis.type.Alias;
import org.springframework.beans.BeanUtils;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

/**
 * @author Zyi
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Alias("helpSearchHistoryPO")
public class SeekHelpSearchHistoryVO {

	/**
	 * 帮助信息搜索记录id
	 */
	@ApiModelProperty(value = "求助信息历史记录id", name = "id", example = "1")
	private Long id;
	/**
	 * 该历史记录对应的user id
	 */
	@ApiModelProperty(value = "user id", name = "userId", example = "1")
	private Long userId;
	/**
	 * 本次搜索的时间
	 */
	@JsonFormat(pattern = "yyyy/MM/dd HH:mm:ss", timezone = "GMT+8")
	@JSONField(format = "yyyy/MM/dd HH:mm:ss")
	@DateTimeFormat(pattern = "yyyy/MM/dd HH:mm:ss")
	@ApiModelProperty(value = "求助搜索时间", name = "searchTime", example = "2022/04/26 19:56")
	private Date searchTime;
	/**
	 * 本次搜索的关键词
	 */
	@ApiModelProperty(value = "求助搜索关键词", name = "keyword", example = "xxx")
	private String keyword;

	public SeekHelpSearchHistoryVO(SeekHelpSearchHistoryPO historyPo) {
		BeanUtils.copyProperties(historyPo, this);
	}
}

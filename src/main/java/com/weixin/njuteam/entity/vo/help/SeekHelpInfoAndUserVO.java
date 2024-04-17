package com.weixin.njuteam.entity.vo.help;

import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.weixin.njuteam.entity.po.help.SeekHelpImage;
import com.weixin.njuteam.entity.po.help.SeekHelpInfoPO;
import com.weixin.njuteam.enums.SeekHelpFinishStatus;
import com.weixin.njuteam.enums.SeekHelpType;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;
import java.util.List;
import java.util.Optional;

/**
 * @author Zyi
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SeekHelpInfoAndUserVO {

	/**
	 * 求助的id
	 */
	@ApiModelProperty(value = "求助id", name = "id", example = "1")
	private Long id;
	/**
	 * 发布该求助信息的用户id
	 */
	@ApiModelProperty(value = "发布该求助信息的用户id", name = "userId", example = "1")
	private Long userId;
	/**
	 * 用户昵称
	 */
	@ApiModelProperty(value = "用户昵称", name = "nickName", example = "name")
	private String nickName;
	/**
	 * 求助的类型
	 */
	@ApiModelProperty(value = "发布求助的类型", name = "seekHelpType", example = "求帮跑腿")
	private SeekHelpType seekHelpType;
	/**
	 * 该信息发布的时间
	 */
	@JsonFormat(pattern = "yyyy/MM/dd HH:mm:ss", timezone = "GMT+8")
	@JSONField(format = "yyyy/MM/dd HH:mm:ss")
	@DateTimeFormat(pattern = "yyyy/MM/dd HH:mm:ss")
	@ApiModelProperty(value = "该求助信息的发布日期", name = "publishDate", example = "2022/04/25 9:55:00")
	private Date publishDate;
	/**
	 * 求助的物品名字
	 */
	@ApiModelProperty(value = "该求助物品的名字", name = "name", example = "xxx")
	private String name;
	/**
	 * 求助的截止日期
	 */
	@JsonFormat(pattern = "yyyy/MM/dd HH:mm:ss", timezone = "GMT+8")
	@JSONField(format = "yyyy/MM/dd HH:mm:ss")
	@DateTimeFormat(pattern = "yyyy/MM/dd HH:mm:ss")
	@ApiModelProperty(value = "该求助信息的截止日期", name = "deadLine", example = "2022/04/25 9:55:00")
	private Date deadLine;
	/**
	 * 备注
	 */
	@ApiModelProperty(value = "该求助信息的备注", name = "comment", example = "xxx")
	private String comment;
	/**
	 * 所属的tag
	 */
	@ApiModelProperty(value = "该求助信息的tag", name = "tag", example = "xxx")
	private String tag;
	/**
	 * 紧急程度 1-5
	 */
	@ApiModelProperty(value = "该求助信息的紧急程度", name = "urgency", example = "3")
	private Integer urgency;
	/**
	 * 图片url列表
	 */
	@ApiModelProperty(value = "图片url列表", name = "urlList", example = "xxx")
	private List<SeekHelpImage> urlList;
	/**
	 * 头像url
	 */
	@ApiModelProperty(value = "用户头像url", name = "avatarURL", example = "xxx")
	private String avatarUrl;
	/**
	 * 求助信息的完成状态
	 */
	@ApiModelProperty(value = "求助信息完成状态", name = "finishStatus", example = "进行中")
	private SeekHelpFinishStatus finishStatus;

	public SeekHelpInfoAndUserVO(SeekHelpInfoPO seekHelpInfoPo, String nickName, String avatarUrl) {
		BeanUtils.copyProperties(seekHelpInfoPo, this);
		this.nickName = nickName;
		this.avatarUrl = avatarUrl;
	}

	public SeekHelpInfoAndUserVO(SeekHelpInfoVO seekHelpInfoVo, String nickName, String avatarUrl) {
		BeanUtils.copyProperties(seekHelpInfoVo, this);
		this.nickName = nickName;
		this.avatarUrl = avatarUrl;
	}

	@JSONField(name = "SeekHelpType")
	public String getSeekHelpType() {
		return Optional.ofNullable(seekHelpType).map(SeekHelpType::getValue).orElse(null);
	}

	@JSONField(name = "SeekHelpType")
	public void setSeekHelpType(String value) {
		this.seekHelpType = SeekHelpType.getTypeByValue(value);
	}

	@JSONField(name = "finishStatus")
	public String getFinishStatus() {
		return Optional.ofNullable(finishStatus).map(SeekHelpFinishStatus::getValue).orElse(null);
	}

	@JSONField(name = "finishStatus")
	public void setFinishStatus(String value) {
		this.finishStatus = SeekHelpFinishStatus.getStatusByValue(value);
	}
}

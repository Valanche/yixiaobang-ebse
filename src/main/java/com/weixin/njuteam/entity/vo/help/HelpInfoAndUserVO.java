package com.weixin.njuteam.entity.vo.help;

import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.weixin.njuteam.entity.po.help.HelpImage;
import com.weixin.njuteam.entity.po.help.HelpInfoPO;
import com.weixin.njuteam.enums.HelpFinishStatus;
import com.weixin.njuteam.enums.HelpType;
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
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class HelpInfoAndUserVO {

	/**
	 * 帮忙的id
	 */
	@ApiModelProperty(value = "帮助id", name = "id", example = "1")
	private Long id;
	/**
	 * 发布该帮忙信息的用户id
	 */
	@ApiModelProperty(value = "发布该帮忙信息的用户id 传token即可", name = "userId", example = "1")
	private Long userId;
	/**
	 * 用户昵称
	 */
	@ApiModelProperty(value = "用户昵称", name = "nickName", example = "name")
	private String nickName;
	/**
	 * 帮忙的类型
	 */
	@ApiModelProperty(value = "帮忙的类型", name = "helpType", example = "帮助")
	private HelpType helpType;
	/**
	 * 该信息发布的时间
	 */
	@JsonFormat(pattern = "yyyy/MM/dd HH:mm:ss", timezone = "GMT+8")
	@JSONField(format = "yyyy/MM/dd HH:mm:ss")
	@DateTimeFormat(pattern = "yyyy/MM/dd HH:mm:ss")
	@ApiModelProperty(value = "信息发布的时间", name = "publishDate", example = "2022/04/25 9:55:00")
	private Date publishDate;
	/**
	 * 帮忙的物品名字
	 */
	@ApiModelProperty(value = "帮忙的物品名字", name = "name", example = "一只口罩")
	private String name;
	/**
	 * 帮忙的截止日期
	 */
	@JsonFormat(pattern = "yyyy/MM/dd HH:mm:ss", timezone = "GMT+8")
	@JSONField(format = "yyyy/MM/dd HH:mm:ss")
	@DateTimeFormat(pattern = "yyyy/MM/dd HH:mm:ss")
	@ApiModelProperty(value = "该帮忙信息的截止日期", name = "deadLine", example = "2022/04/25 9:55:00")
	private Date deadLine;
	/**
	 * 备注
	 */
	@ApiModelProperty(value = "该帮忙信息的备注", name = "comment", example = "备注")
	private String comment;
	/**
	 * 所属的tag
	 */
	@ApiModelProperty(value = "该帮忙信息的tag", name = "tag", example = "日用品")
	private String tag;
	/**
	 * 紧急程度 1-5
	 */
	@ApiModelProperty(value = "紧急程度，1-5", name = "urgency", example = "2")
	private Integer urgency;
	/**
	 * 图片url列表
	 */
	@ApiModelProperty(value = "图片url列表", name = "urlList", example = "xxx")
	private List<HelpImage> urlList;
	/**
	 * 头像url
	 */
	@ApiModelProperty(value = "用户头像url", name = "avatarURL", example = "xxx")
	private String avatarUrl;
	/**
	 * 帮助信息的完成状态
	 */
	@ApiModelProperty(value = "帮助信息的状态", name = "finishStatus", example = "进行中")
	private HelpFinishStatus finishStatus;

	public HelpInfoAndUserVO(HelpInfoPO helpInfoPo, String nickName, String avatarUrl) {
		BeanUtils.copyProperties(helpInfoPo, this);
		this.nickName = nickName;
		this.avatarUrl = avatarUrl;
	}

	public HelpInfoAndUserVO(HelpInfoVO helpInfoVo, String nickName, String avatarUrl) {
		BeanUtils.copyProperties(helpInfoVo, this);
		this.nickName = nickName;
		this.avatarUrl = avatarUrl;
	}

	@JSONField(name = "helpType")
	public String getHelpType() {
		return Optional.ofNullable(helpType).map(HelpType::getValue).orElse(null);
	}

	@JSONField(name = "helpType")
	public void setHelpType(String value) {
		this.helpType = HelpType.getTypeByValue(value);
	}

	public String getFinishStatus() {
		return Optional.ofNullable(finishStatus).map(HelpFinishStatus::getValue).orElse(null);
	}

	public void setFinishStatus(String value) {
		this.finishStatus = HelpFinishStatus.getStatusByValue(value);
	}
}

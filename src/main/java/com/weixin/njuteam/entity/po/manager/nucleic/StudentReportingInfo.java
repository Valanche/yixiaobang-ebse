package com.weixin.njuteam.entity.po.manager.nucleic;

import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.weixin.njuteam.enums.FinishStatus;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.ibatis.type.Alias;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;
import java.util.Optional;

/**
 * @author Zyi
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Alias("studentReportingInfo")
public class StudentReportingInfo {

	/**
	 * user id
	 */
	@ApiModelProperty(value = "用户id", name = "userId", example = "1")
	private Long userId;
	/**
	 * username
	 */
	@ApiModelProperty(value = "用户的真名", name = "name", example = "xxx")
	private String name;
	/**
	 * testing true time
	 */
	@JsonFormat(pattern = "yyyy/MM/dd HH:mm:ss", timezone = "GMT+8")
	@JSONField(format = "yyyy/MM/dd HH:mm:ss")
	@DateTimeFormat(pattern = "yyyy/MM/dd HH:mm:ss")
	@ApiModelProperty(value = "OCR识别出的检测真正时间", name = "testingTrueTime", example = "2022/05/14 11:42:00")
	private Date testingTrueTime;
	/**
	 * finish status
	 */
	@ApiModelProperty(value = "完成状态", name = "finishStatus", example = "未完成")
	private FinishStatus finishStatus;
	/**
	 * image name
	 */
	@ApiModelProperty(value = "上传截图的路径", name = "imageName", example = "xxx")
	private String imageName;
	/**
	 * 未做核酸的理由
	 */
	@ApiModelProperty(value = "未做核酸的理由", name = "comment", example = "xxx")
	private String comment;

	public String getFinishStatus() {
		return Optional.ofNullable(finishStatus).map(FinishStatus::getValue).orElse(null);
	}

	public void setFinishStatus(String value) {
		this.finishStatus = FinishStatus.getStatusByValue(value);
	}
}

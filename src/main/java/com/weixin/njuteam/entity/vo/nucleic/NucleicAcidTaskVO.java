package com.weixin.njuteam.entity.vo.nucleic;

import com.weixin.njuteam.enums.FinishStatus;
import com.weixin.njuteam.enums.NucleicAcidType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.Optional;

/**
 * @author Zyi
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NucleicAcidTaskVO {

	private Long id;

	private NucleicAcidType type;

	private Date deadLine;

	private FinishStatus finishStatus;

	public String getType() {
		return Optional.ofNullable(type).map(NucleicAcidType::getValue).orElse(null);
	}

	public void setType(String value) {
		this.type = NucleicAcidType.getTypeByValue(value);
	}

	public NucleicAcidType checkType() {
		return type;
	}

	public String getFinishStatus() {
		return Optional.ofNullable(finishStatus).map(FinishStatus::getValue).orElse(null);
	}

	public void setFinishStatus(String value) {
		this.finishStatus = FinishStatus.getStatusByValue(value);
	}
}

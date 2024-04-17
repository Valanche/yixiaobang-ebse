package com.weixin.njuteam.entity.po.nucleic;

import com.weixin.njuteam.entity.vo.nucleic.DataVO;
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
public class RemindPO {

	private String touser;

	private String template_id;

	private DataVO data;
}

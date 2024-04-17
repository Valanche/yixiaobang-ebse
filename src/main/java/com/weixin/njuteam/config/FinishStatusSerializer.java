package com.weixin.njuteam.config;

import com.alibaba.fastjson.serializer.JSONSerializer;
import com.alibaba.fastjson.serializer.ObjectSerializer;
import com.weixin.njuteam.enums.FinishStatus;

import java.io.IOException;
import java.lang.reflect.Type;

/**
 * 该类用于前端传值和后端响应时的enum type值和类型转化
 *
 * @author Zyi
 */
public class FinishStatusSerializer implements ObjectSerializer {

	@Override
	public void write(JSONSerializer jsonSerializer, Object o, Object o1, Type type, int i) throws IOException {
		// 序列化 enum转String
		jsonSerializer.write(((FinishStatus) o).getValue());
	}
}

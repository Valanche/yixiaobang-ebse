package com.weixin.njuteam.config;

import com.weixin.njuteam.enums.ValueEnum;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Mybatis type handler
 *
 * @author Zyi
 */
public class ValueEnumTypeHandler<E extends Enum<E> & ValueEnum> extends BaseTypeHandler<ValueEnum> {

	private final Class<E> type;

	public ValueEnumTypeHandler(Class<E> type) {
		if (type == null) {
			throw new IllegalArgumentException("Type argument cannot be null");
		}
		this.type = type;
	}

	private static <E extends Enum<?> & ValueEnum> E codeOf(Class<E> enumClass, String code) {
		// 这里是利用反射来获得常量池中的枚举常量
		E[] enumConstants = enumClass.getEnumConstants();
		for (E e : enumConstants) {
			if (e.getValue().equals(code)) {
				return e;
			}
		}

		return null;
	}

	@Override
	public void setNonNullParameter(PreparedStatement ps, int i, ValueEnum parameter, JdbcType jdbcType) throws SQLException {
		ps.setString(i, parameter.getValue());
	}

	@Override
	public E getNullableResult(ResultSet rs, String columnName) throws SQLException {
		String value = rs.getString(columnName);
		return rs.wasNull() ? null : codeOf(value);
	}

	@Override
	public E getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
		String value = rs.getString(columnIndex);
		return rs.wasNull() ? null : codeOf(value);
	}

	@Override
	public E getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
		String value = cs.getString(columnIndex);
		return cs.wasNull() ? null : codeOf(value);
	}

	private E codeOf(String code) {
		try {
			return codeOf(type, code);
		} catch (Exception ex) {
			throw new IllegalArgumentException("Cannot convert " + code + " to " + type.getSimpleName() + " by code value.", ex);
		}
	}
}

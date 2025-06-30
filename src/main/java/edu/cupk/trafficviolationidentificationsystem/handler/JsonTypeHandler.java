package edu.cupk.trafficviolationidentificationsystem.handler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedTypes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;

/**
 * MyBatis 自定义类型处理器 (TypeHandler).
 * <p>
 * 负责在 Java 的 {@code List<String>} 类型和数据库的 JSON 文本类型之间进行自动转换。
 * 当向数据库写入时，它将 List 序列化为 JSON 字符串；从数据库读取时，它将 JSON 字符串反序列化为 List。
 * </p>
 * {@link MappedTypes}: 指定这个处理器专门用于处理 Java 的 List.class 类型。
 */
@MappedTypes(List.class)
public class JsonTypeHandler extends BaseTypeHandler<List<String>> {

    private static final Logger log = LoggerFactory.getLogger(JsonTypeHandler.class);
    private static final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * 在设置 PreparedStatement 的参数时被调用。
     * 将 List<String> 参数转换为 JSON 字符串。
     */
    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, List<String> parameter, JdbcType jdbcType) throws SQLException {
        try {
            ps.setString(i, objectMapper.writeValueAsString(parameter));
        } catch (JsonProcessingException e) {
            log.error("将 List 转换为 JSON 字符串时出错。List: {}", parameter, e);
            throw new SQLException("将 List 转换为 JSON 时出错", e);
        }
    }

    /**
     * 从 ResultSet 中根据列名获取结果时被调用。
     * 将 JSON 字符串转换为 List<String>。
     */
    @Override
    public List<String> getNullableResult(ResultSet rs, String columnName) throws SQLException {
        return parseJson(rs.getString(columnName));
    }

    /**
     * 从 ResultSet 中根据列索引获取结果时被调用。
     * 将 JSON 字符串转换为 List<String>。
     */
    @Override
    public List<String> getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        return parseJson(rs.getString(columnIndex));
    }

    /**
     * 从 CallableStatement 中获取结果时被调用。
     * 将 JSON 字符串转换为 List<String>。
     */
    @Override
    public List<String> getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        return parseJson(cs.getString(columnIndex));
    }

    /**
     * 解析 JSON 字符串的辅助方法。
     *
     * @param json 从数据库获取的 JSON 字符串。
     * @return 解析后的 {@code List<String>}。如果输入为 null 或空，则返回空列表。
     * @throws SQLException 如果 JSON 解析失败。
     */
    private List<String> parseJson(String json) throws SQLException {
        if (json == null || json.trim().isEmpty()) {
            return Collections.emptyList();
        }
        try {
            // 使用 TypeReference 来正确地反序列化泛型列表
            return objectMapper.readValue(json, new TypeReference<>() {});
        } catch (JsonProcessingException e) {
            log.error("解析 JSON 字符串 '{}' 到 List 时出错", json, e);
            throw new SQLException("解析 JSON 字符串到 List 时出错", e);
        }
    }
}
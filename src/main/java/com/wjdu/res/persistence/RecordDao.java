package com.wjdu.res.persistence;

import com.wjdu.res.domain.Record;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.util.Assert;

import javax.annotation.Resource;
import java.sql.ResultSet;
import java.sql.SQLException;

public class RecordDao {
    @Resource
    private JdbcTemplate jdbcTemplate;

    public Record getJyeooPaperById(String id) {
        Assert.notNull(id, "id is required!");
        String sql = "select * from record where id = ?";
        Record record = jdbcTemplate.queryForObject(sql, new Object[] { id }, new RecordRowMap());
        return null == record ? null : record;
    }

    class RecordRowMap implements RowMapper<Record> {
        public Record mapRow(ResultSet rs, int rowNum) throws SQLException {
            Record record = new Record();
            record.setId(rs.getString("id"));
            return record;
        }
    }
}

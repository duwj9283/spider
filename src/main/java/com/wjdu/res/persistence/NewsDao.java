package com.wjdu.res.persistence;

import com.wjdu.res.domain.News;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import org.springframework.util.Assert;

import javax.annotation.Resource;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository("newsDao")
public class NewsDao {
    @Resource
    private JdbcTemplate jdbcTemplate;

    public void batchInsertNews(List<News> newsList) {
        final List<News> newsListTemp = newsList;
        String sql = "INSERT INTO news(`title`, `href`, `content`, `date`, `type_name`, `from`) values (?, ?, ?, ?, ?, ?) ON DUPLICATE KEY UPDATE `title` = ?,`content`=?";
        jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                ps.setString(1, newsListTemp.get(i).getTitle());
                ps.setString(2, newsListTemp.get(i).getHref());
                ps.setString(3, newsListTemp.get(i).getContent());
                ps.setString(4, newsListTemp.get(i).getDate());
                ps.setString(5, newsListTemp.get(i).getType_name());
                ps.setString(6, newsListTemp.get(i).getFrom());
                ps.setString(7, newsListTemp.get(i).getTitle());
                ps.setString(8, newsListTemp.get(i).getContent());
            }
            @Override
            public int getBatchSize() {
                return newsListTemp.size();
            }
        });
    }


    class NewsRowMap implements RowMapper<News> {
        public News mapRow(ResultSet rs, int rowNum) throws SQLException {
            News news = new News();
            news.setHref(rs.getString("href"));
            return news;
        }
    }
}

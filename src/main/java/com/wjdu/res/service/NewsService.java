package com.wjdu.res.service;

import com.wjdu.res.domain.News;
import com.wjdu.res.persistence.NewsDao;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service("newsService")
public class NewsService {

    @Resource
    private NewsDao newsDao;

    public void batchInsertNews(List<News> newsList){
        newsDao.batchInsertNews(newsList);
    }
}

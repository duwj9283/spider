package com.wjdu.res.service;

import com.wjdu.res.domain.Record;
import com.wjdu.res.persistence.RecordDao;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service("recordService")
public class RecordService {

    @Resource
    private RecordDao recordDao;

    public Record getRecordById(String id){
        return  recordDao.getRecordById(id);
    }
}

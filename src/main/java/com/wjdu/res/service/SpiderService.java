package com.wjdu.res.service;

import com.wjdu.res.domain.Record;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service("spiderService")
public class SpiderService{
	private Logger logger = LoggerFactory.getLogger(SpiderService.class);

	@Resource
	private RecordService recordService;

	public void getData(){
		Record record = recordService.getRecordById("1");
		System.out.println(record.getUrl());
	}
}

package com.wjdu.res.service.task;

import com.wjdu.res.service.SpiderService;

import javax.annotation.Resource;


public class TaskService {
	
	@Resource
	private SpiderService spiderService;
	
	public void doStart(){
		spiderService.getData();
	}
}

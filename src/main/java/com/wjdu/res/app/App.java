package com.wjdu.res.app;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.ClassPathXmlApplicationContext;


public class App {
	private static Logger logger = LoggerFactory.getLogger(App.class);

	/**
	 * 执行作业入口
	 */
	public static void main(String[] args) {
		try {
			if (args.length > 0) {
				if ("start".equals(args[0])) {
					new ClassPathXmlApplicationContext("classpath:config/applicationContext_*.xml");
					logger.warn("spider job start success.");
				} else {
					System.exit(0);
				}
			}
		} catch (Throwable e) {

			e.printStackTrace();
			logger.error(e.getMessage(),e);
		}
	}
}

package com.wjdu.res.uitl;

public class TimeUtil {

	/**
	 * 暂停xxx秒，针对mongo读写分离产生的数据同步不及时问题。
	 *
	 * @author leichu@iflytek.com
	 * @date 2016年11月22日
	 */
	public static void pauseToSecond(int time){
		try {
			Thread.sleep(time * 1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 暂停xxx分
	 *
	 * @author leichu@iflytek.com
	 * @date 2016年12月4日
	 */
	public static void pauseToMinute(int time){
		try {
			Thread.sleep(time * 60 * 1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}

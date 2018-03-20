package com.wjdu.res.service;

import com.wjdu.res.domain.News;
import com.wjdu.res.dto.ImageDTO;
import com.wjdu.res.service.thread.GetPictureThread;
import org.apache.commons.lang.StringUtils;
import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service("spiderService")
public class SpiderService{
	private static Logger logger = LoggerFactory.getLogger(SpiderService.class);

	@Resource
	private NewsService newsService;
	@Resource
	private ThreadPoolTaskExecutor taskExecutor;

	private final static String Domain = "http://www.hefei.gov.cn/";

	//时事新闻
	private final static String SsxwDomain = Domain+"xwzxdt/";

	//agent
	private final static String agent = "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/49.0.2623.87 Safari/537.36";

//	public static void main(String[] args) {
//
//	}

	public void getData(){
		try {
			getNewsPage("合肥市人民政府");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

//	private boolean isContainChinese(String str) {
//		Pattern p = Pattern.compile("[\u4e00-\u9fa5]");
//		Matcher m = p.matcher(str);
//		if (m.find()) {
//			return true;
//		}
//		return false;
//	}

//	private String getRealUrl(String url,String fatherUrl){
//		if(url.contains("../")){
//			if(fatherUrl.contains(".html")){
//				String[] strArr = fatherUrl.split("/");
//				fatherUrl = fatherUrl.substring(0,fatherUrl.length()-strArr[strArr.length-1].length()-strArr[strArr.length-2].length()-1);
//			}else{
//				String[] strArr = fatherUrl.split("/");
//				fatherUrl = fatherUrl.substring(0,fatherUrl.length()-strArr[strArr.length-1].length()-1);
//			}
//			return fatherUrl + url.replaceFirst("../","");
//		}else if(url.contains("./")){
//			if(fatherUrl.contains(".html")){
//				String[] strArr = fatherUrl.split("/");
//				fatherUrl = fatherUrl.substring(0,fatherUrl.length()-strArr[strArr.length-1].length());
//			}
//			return fatherUrl + url.replaceFirst("./","");
//		}else if(url.contains("http")){
//			return url;
//		}else{
//			return Domain + url.substring(1,url.length());
//		}
//	}

//	private String getTypeUrl(String url){
//		if(url.contains("../")){
//			return SsxwDomain + url.replaceFirst("../","");
//		}else if(url.contains("./")){
//			return SsxwDomain + "jryw/" + url.replaceFirst("./","");
//		}else{
//			return Domain + url.substring(1,url.length());
//		}
//	}

	private Document connect(String url) {
		Document doc = null;
		try {
			doc = Jsoup.connect(url).userAgent(agent).timeout(10000).get();
		} catch (NullPointerException e) {
			return null;
		} catch (HttpStatusException e) {
			logger.error("访问异常，状态码:" + e.getStatusCode(),",URl:"+url);
			return null;
		} catch (IOException e) {
			return null;
		}
		return doc;
	}

	public void getNewsPage(String from) throws IOException {
		String baseUrl = SsxwDomain+"nrtj/";
		Document doc = connect(baseUrl);
		//获取时事新闻主要分类
		List<Node> types = doc.getElementsByClass("news_list").select("ul").first().childNodes();
		for (Node child : types) {
			if (child instanceof Element) {
				Element echild = (Element) child;
				Elements elements = echild.children();
				if (null != elements && elements.size()>0) {
					Element firstChild = elements.first();
					if (firstChild.tagName().equals("a")){
						logger.warn(">>>["+firstChild.text()+"]开始更新<<<");
						String href = firstChild.attr("abs:href");
						logger.warn("第[1]页:" + href);
						if("http://www.hefei.gov.cn/xwzxdt/nrtj/".equals(href)){    //待删除
                            getNewsList(href,from);
                            doc = connect(href);
                            if(doc.getElementsByClass("page").size()>0){
                                String totalPage = doc.getElementsByClass("page").html().replaceAll("^<script>createPageHTML\\(([0-9]+),[\\s\\S]*$","$1");
                                if(StringUtils.isNotEmpty(totalPage)){
                                    Integer totalCount = Integer.parseInt(totalPage);
                                    logger.warn("总页数:"+totalCount);
                                    if(totalCount>1){
                                        for (int k=1;k<totalCount;k++){
                                            logger.warn("第["+(k+1)+"]页:"+href+"index_"+k+".html");
                                            getNewsList(href+"index_"+k+".html",from);
                                        }
                                    }
                                }
                            }
                        }

						logger.warn("***["+firstChild.text()+"]更新结束***");
					}
				}
			}
		}
		logger.warn("时事新闻所有新闻更新完毕");
	}

	public void getNewsList(String baseUrl, String from) throws IOException {
		Document doc;
		doc = connect(baseUrl);
		List<Node> newChilds = doc.select("ul.con_list").first().childNodes();
		List<News> newsList = new ArrayList<>();
		for (Node child : newChilds) {
			if (child instanceof Element) {
				News news = new News();
				Element echild = (Element) child;
				Elements elements = echild.children();
				if (null != elements && elements.size()>0) {
					Element firstChild = elements.first();
					if (firstChild.tagName().equals("a")){
						String href = firstChild.attr("abs:href");
						news.setHref(href);
						//新闻内容地址
						String newsUrl =news.getHref();
						Document newsDoc = connect(newsUrl);
						if(null != newsDoc && newsDoc.getElementsByClass("title").size() == 0 && newsDoc.getElementsByClass("copy").size() == 0){
							continue;
						}
                        logger.warn("新闻内容URl:"+newsUrl);
						//新闻标题
						String title = newsDoc.getElementsByClass("title").first().select("p").first().text();
						Element contentElement = newsDoc.getElementsByClass("news_con").first();
						//解析图片，并多线程下载
						Elements imgTags = contentElement.select("img[src]");
						String content = contentElement.html();
						if(null != imgTags && imgTags.size()>0){
							List<FutureTask<ImageDTO>> taskList = new ArrayList<FutureTask<ImageDTO>>();
							for (Element imgTag : imgTags) {
								String absSrc = imgTag.attr("abs:src");
								String src = imgTag.attr("src");
								GetPictureThread getPictureThread = new GetPictureThread(src,absSrc,"G:\\images");
								FutureTask<ImageDTO> task = new FutureTask<ImageDTO>(getPictureThread);
								taskExecutor.execute(task);
								taskList.add(task);
							}
							for (FutureTask<ImageDTO> task : taskList) {
								try {
									ImageDTO imageDTO = task.get(30, TimeUnit.SECONDS);
									if (null != imageDTO) {
										content = content.replace(imageDTO.getRemoteShortSrc(),imageDTO.getLocalScr());
									}
								} catch (Exception e) {
									logger.error("task error", e);
								}
							}
						}
						String date =  newsDoc.getElementsByClass("time").text().replace("&nbsp","").trim().substring(0,16);
						news.setTitle(title);
						news.setContent(content);
						news.setDate(date);
						int tagCount = newsDoc.getElementsByClass("cur_site").select("a").size();
						Element curSite;
						if(tagCount>2){
							curSite =  newsDoc.getElementsByClass("cur_site").select("a").get(2);
						}else{
							curSite =  newsDoc.getElementsByClass("cur_site").select("a").get(tagCount-1);
						}
						String typeName = curSite.attr("title");
						news.setType_name(typeName);
						news.setFrom(from);
						newsList.add(news);
					}
				}
			}
		}
        newsService.batchInsertNews(newsList);
	}
}

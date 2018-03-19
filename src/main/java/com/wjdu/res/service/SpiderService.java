package com.wjdu.res.service;

import com.wjdu.res.domain.News;
import org.apache.commons.lang.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service("spiderService")
public class SpiderService{
	private Logger logger = LoggerFactory.getLogger(SpiderService.class);

	@Resource
	private NewsService newsService;

	private final static String HefeiDomain = "http://www.hefei.gov.cn/xwzxdt/";

	public void getData(){
		try {
			getNewsPage("合肥市人民政府");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private boolean isContainChinese(String str) {
		Pattern p = Pattern.compile("[\u4e00-\u9fa5]");
		Matcher m = p.matcher(str);
		if (m.find()) {
			return true;
		}
		return false;
	}

	private String getRealUrl(String url){
		if(url.contains("../")){
			return HefeiDomain + url.replaceFirst("../","");
		}else if(url.contains("./")){
			return HefeiDomain+"jryw/"+url.replaceFirst("./","");
		}
		return url;
	}

	public void getNewsPage(String from) throws IOException {
		Document doc;
		String baseUrl = HefeiDomain+"jryw/";
		doc = Jsoup.connect(baseUrl).timeout(10000).userAgent("Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/49.0.2623.87 Safari/537.36").get();
		//获取时事新闻主要分类
		List<Node> types = doc.getElementsByClass("news_list").select("ul").first().childNodes();
		for (Node child : types) {
			if (child instanceof Element) {
				Element echild = (Element) child;
				Elements elements = echild.children();
				if (null != elements && elements.size()>0) {
					Element firstChild = elements.first();
					if (firstChild.tagName().equals("a")){
						String href = getRealUrl(firstChild.attr("href"));
						getNewsList(href,from);
						String totalPage = doc.getElementsByClass("page").html().replaceAll("^<script>createPageHTML\\(([0-9]+),[\\s\\S]*$","$1");
						if(StringUtils.isNotEmpty(totalPage)){
							Integer totalCount = Integer.parseInt(totalPage);
							for (int k=1;k<totalCount;k++){
								getNewsList(href+"index_"+k+".html",from);
							}
						}
					}
				}
			}
		}
		logger.warn("时事新闻所有新闻更新完毕");
	}

	public void getNewsList(String baseUrl,String from) throws IOException {
		Document doc;
		doc = Jsoup.connect(baseUrl).timeout(10000).userAgent("Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/49.0.2623.87 Safari/537.36").get();
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
                        String href = getRealUrl(firstChild.attr("href"));
						news.setHref(href);
						//新闻内容地址
						String newsUrl =news.getHref();
						if (isContainChinese(news.getHref())) {
							newsUrl = URLEncoder.encode(news.getHref(), "utf-8").toLowerCase().replace("%3a", ":").replace("%2f", "/");
						}
						Document newsDoc = null;
						newsDoc = Jsoup.connect(newsUrl).userAgent("Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/49.0.2623.87 Safari/537.36").timeout(10000).get();
						//新闻标题
						String title = newsDoc.getElementsByClass("title").first().select("p").first().text();
						String content = newsDoc.getElementsByClass("con").html();
						String date =  newsDoc.getElementsByClass("time").text().replace("&nbsp","").trim().substring(0,16);
						news.setTitle(title);
						news.setContent(content);
						news.setDate(date);
						Element curSite =  newsDoc.getElementsByClass("cur_site").select("a").last();
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

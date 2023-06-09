package cn.wanghaomiao.seimi.crawlers;

import cn.wanghaomiao.seimi.annotation.Crawler;
import cn.wanghaomiao.seimi.def.BaseSeimiCrawler;
import cn.wanghaomiao.seimi.struct.Request;
import cn.wanghaomiao.seimi.struct.Response;
import org.seimicrawler.xpath.JXDocument;

import java.util.List;

/**
 * @author 汪浩淼 [et.tw@163.com]
 * @since 2015/10/21.
 */
//@Crawler(name = "basic_a")
public class Basic extends BaseSeimiCrawler {
    @Override
    public String[] startUrls() {
        //两个是测试去重的
        return new String[]{"https://www.h513.com/"};
    }

    @Override
    public void start(Response response) {
        JXDocument doc = response.document();
        try {
//            List<Object> urls = doc.sel("//span[@class='s2']/");
            List<Object> urls = doc.sel("(//a[contains(@href,'/book/')]/@href)");
            logger.info("{}", urls.size());
            for (Object s:urls){
                System.out.println(s.toString());
                push(Request.build("https://www.h513.com"+s.toString(),Basic::getTitle));
                break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void getTitle(Response response){
        JXDocument doc = response.document();
        try {
            logger.info("url:{} {}", response.getUrl(), doc.sel("//h1[@class='postTitle']/a/text()|//a[@id='cb_post_title_url']/text()"));
            //do something
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

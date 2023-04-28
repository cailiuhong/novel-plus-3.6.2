package cn.wanghaomiao.crawlers;

import cn.wanghaomiao.seimi.annotation.Crawler;
import cn.wanghaomiao.seimi.def.BaseSeimiCrawler;
import cn.wanghaomiao.seimi.struct.Request;
import cn.wanghaomiao.seimi.struct.Response;
import org.seimicrawler.xpath.JXDocument;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author 汪浩淼 [et.tw@163.com]
 * @since 2015/10/21.
 */
@Crawler(name = "basic_h")
public class BasicH extends BaseSeimiCrawler {
    private List<Object> safeAllList = Collections.synchronizedList(new ArrayList<>(512));
    @Override
    public String[] startUrls() {
        //两个是测试去重的
        return new String[]{"https://www.h513.com/book/12372/"};
    }

    @Override
    public void start(Response response) {
        JXDocument doc = response.document();
        try {
//            List<Object> urls = doc.sel("//span[@class='s2']/");
            List<Object> urls = doc.sel("(//div[@class='listmain']//a[contains(@href,'/book/')]/@href)");
            logger.info("{}", urls.size());
            int i = 0;
            for (Object s:urls){
                System.out.println(s.toString());
                push(Request.build("https://www.h513.com"+s.toString(), BasicH::getTitle));
                i++;
//                if(i>= 1000){
//                    break;
//                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void getTitle(Response response){
        JXDocument doc = response.document();
        try {
            List<Object> sel = doc.sel("//div[@id='chaptercontent']");
//            logger.info("url:{} {}", response.getUrl(), doc.sel("//div[@id='chaptercontent']"));
            safeAllList.add(sel);
//            logger.info("aaaaa{}"+safeAllList.toString());
            logger.info("allList size()={}",safeAllList.size());
            //do something
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

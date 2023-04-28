package cn.wanghaomiao.seimi.controller;

import cn.wanghaomiao.seimi.config.SeimiConfig;
import cn.wanghaomiao.seimi.core.Seimi;
import cn.wanghaomiao.seimi.core.SeimiContext;
//import cn.wanghaomiao.seimi.crawlers.BasicH;
import cn.wanghaomiao.seimi.crawlers.BasicD;
import cn.wanghaomiao.seimi.crawlers.BasicH;
import cn.wanghaomiao.seimi.def.BaseSeimiCrawler;
import cn.wanghaomiao.seimi.spring.common.CrawlerCache;
import cn.wanghaomiao.seimi.struct.CrawlerModel;
import cn.wanghaomiao.seimi.struct.Request;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import static java.lang.Thread.sleep;

/**
 *
 * @author github.com/zhegexiaohuozi seimimaster@gmail.com
 */
@RestController
@RequestMapping(value = "/seimi")
public class IndexController {
    @RequestMapping(value = "/info/{cname}")
    public String crawler(@PathVariable String cname) {
        CrawlerModel model = CrawlerCache.getCrawlerModel(cname);
        if (model == null) {
            return "not find " + cname;
        }
        return model.queueInfo();
    }

    @RequestMapping(value = "send_req")
    public String sendRequest(Request request){
        CrawlerCache.consumeRequest(request);
        return "consume suc";
    }

    @RequestMapping(value = "basic_d")
    public String sendRequest1(Request request) throws InterruptedException {
        String url = "http://www.e52p.buzz/AAbook/AAAtb/xiaoyuan/index-number.html";
        url.replace("number","1");
        Request request1 = new Request();
        request1.setCrawlerName("basic_d");
        request1.setUrl(url);
        request1.setCallBack("getTitle");
        request1.setMaxReqCount(5);
        request1.setCurrentReqCount(1);

        CrawlerModel crawlerModel = null;
        List<String> urls = new ArrayList<>();

        Integer index = 2;
        while (index <= request.getMaxReqCount()) {
            crawlerModel = CrawlerCache.getCrawlerModel(request1.getCrawlerName());
            if (crawlerModel == null){
                return "error,错误的CrawlerModel！";
            }

            urls.add(url.replace("number",index.toString()));
            crawlerModel.startRequest(urls);

            System.out.println(BasicD.flag);
            while (!BasicD.flag){
                Thread.sleep(3000);
                System.out.println("wait:"+BasicD.flag);
            }
            BasicD.flag =false;
            urls.clear();
            index++;
        };

        return "consume suc ok";
    }

    @RequestMapping(value = "basic_h")
    public String basic_h(Request request) throws InterruptedException {
//        String url = "https://www.h513.com/book/number/";
//        url = url.replace("number","2");
//        Request request1 = new Request();
//        request1.setCrawlerName(request.getCrawlerName());
//        request1.setUrl(url);
//        request1.setCallBack("getTitle");
//        request1.setMaxReqCount(5);
//        request1.setCurrentReqCount(1);

        CrawlerModel crawlerModel = null;
        List<String> urls = new ArrayList<>();

        Integer index = request.getCurrentReqCount();
        while (index <= request.getMaxReqCount()) {
            crawlerModel = CrawlerCache.getCrawlerModel(request.getCrawlerName());
            if (crawlerModel == null){
                return "error,错误的CrawlerModel！";
            }



            urls.add(request.getUrl().replace("number",index.toString()));
            crawlerModel.startRequest(urls);
            Integer dex = 0;
//            System.out.println(BasicH.flag);
            while (!BasicH.flag){
                Thread.sleep(1000);
                System.out.println("wait:"+BasicH.flag);
                if(dex >= 100){
                    BasicH.flag =true;
                }
                dex++;
            }
            BasicH.flag =false;
            urls.clear();
            index++;
        };

        return "consume suc ok";
    }
}

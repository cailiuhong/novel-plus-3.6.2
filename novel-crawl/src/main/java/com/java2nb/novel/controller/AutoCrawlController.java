package com.java2nb.novel.controller;

import cn.wanghaomiao.seimi.spring.common.CrawlerCache;
import cn.wanghaomiao.seimi.struct.CrawlerModel;
import cn.wanghaomiao.seimi.struct.Request;
import com.java2nb.novel.crawls.BasicD;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping(value = "/seimi")
public class AutoCrawlController {
    @PostMapping(value = "basic_d")
    public String sendRequest1(Request request) throws InterruptedException {
//        String url = "http://www.e52p.buzz/AAbook/AAAtb/yinqi/index-number.html";
//        url.replace("number","72");

        CrawlerModel crawlerModel = null;
        List<String> urls = new ArrayList<>();

        Integer index = request.getCurrentReqCount();
        while (index <= request.getMaxReqCount()) {
            crawlerModel = CrawlerCache.getCrawlerModel(request.getCrawlerName());
            if (crawlerModel == null){
                return "error,错误的CrawlerModel！";
            }

            Integer dex = 0;

            urls.add(request.getUrl().replace("number",index.toString()));
            crawlerModel.startRequest(urls);

            System.out.println(BasicD.flag);
            while (!BasicD.flag){
                Thread.sleep(500);
                System.out.println("wait:"+BasicD.flag);
                dex++;

                if(dex >= 20){
                    BasicD.flag =true;
                }
            }
            BasicD.flag =false;
            urls.clear();
            index++;
        };

        return "consume suc ok";
    }
}

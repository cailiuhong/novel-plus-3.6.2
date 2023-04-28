package cn.wanghaomiao.seimi.crawlers;

import cn.wanghaomiao.seimi.annotation.Crawler;
import cn.wanghaomiao.seimi.def.BaseSeimiCrawler;
import cn.wanghaomiao.seimi.struct.Request;
import cn.wanghaomiao.seimi.struct.Response;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.nodes.Element;
import org.seimicrawler.xpath.JXDocument;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author 汪浩淼 [et.tw@163.com]
 * @since 2015/10/21.
 */
//@Crawler(name = "basic_d")
public class BasicD extends BaseSeimiCrawler {
    private List<Object> contentList = Collections.synchronizedList(new ArrayList<>(512));
    private List<Object> bookIndexList = Collections.synchronizedList(new ArrayList<>(512));
    private AtomicInteger atomicInteger = new AtomicInteger(0);
    public  volatile  AtomicBoolean atomicBoolean = new AtomicBoolean(false);

    private AtomicInteger indexnum = new AtomicInteger(1);
    public static Boolean flag = false;

//    public Boolean getAtomicBoolean(){
//        System.out.println(atomicBoolean.get());
//        System.out.println("flag:"+atomicBoolean.get());
////        if(){
////
////        }
//        return atomicBoolean.get();
//    }

    @Override
    public String[] startUrls() {
        //两个是测试去重的
        return new String[]{"http://www.e52p.buzz/AAbook/AAAtb/huangse/index.html"};
//        return new String[]{"http://www.d72y.com"};
    }

    @Override
    public void start(Response response) {
        JXDocument doc = response.document();
        try {
            //书的信息book
//            getBookInfo(doc);
            atomicInteger.set(0);
            List<Object> urls = doc.sel("(//div[@class='classList']//a[contains(@href,'/AAbook/AAAwz/')]/@href)");
            logger.info("{}", urls.size());
            atomicInteger.getAndSet(urls.size());
            atomicBoolean.getAndSet(false);
            flag = false;
            for (Object s:urls){
                System.out.println(s.toString());
                push(Request.build("http://www.e52p.buzz"+s.toString(), BasicD::getTitle));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void getTitle(Response response){
        JXDocument doc = response.document();
        try {
            //Id
//        Long indexId = idWorker.nextId();
            Long indexId = 1L;
            //content
            getBookContentInfo(doc,indexId);
            //index
            getBookIndexInfo(doc,indexId);

            logger.info("listSize:"+contentList.size());
            if(contentList.size() == atomicInteger.get()){
                logger.info("allList size()={}",contentList.size());
                atomicBoolean.getAndSet(true);
                flag = true;
                System.out.println(atomicBoolean.get());
                contentList.clear();
                Thread.sleep(1200);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void getBookContentInfo(JXDocument doc,Long indexId){
        //内容
        List<Object> content = doc.sel("//div[@class='content']");
        contentList.add(content);

//        BookContent bookContent = new BookContent();
//        bookContent.setContent(content.toString());
//        bookContent.setIndexId(indexId);
//        contentList.add(bookContent);
    }

    public void getBookIndexInfo(JXDocument doc,Long indexId){
        //indexName
        List<Object> indexName = doc.sel("//h1");
        Element indexNameE = (Element)indexName.get(0);
//        System.out.println("indexName"+indexNameE.text());

        //indexNum
        Integer indexNumTmp = indexnum.getAndAdd(1);
        System.out.println("indexNum"+indexNumTmp);

        //wordCount
        //内容
        List<Object> content = doc.sel("//div[@class='content']");
        Element contentStr = (Element)content.get(0);
//        System.out.println("indexName"+contentStr.text());
        System.out.println("------------");

//        BookIndex bookIndex = new BookIndex();
//        bookIndex.setIndexName(indexNameE.text());
//        bookIndex.setIndexNum(Integer.parseInt(indexNumTmp));
//        int wordCount = StringUtil.getStrValidWordCount(contentStr.text());
//        bookIndex.setWordCount(wordCount);
//        bookIndex.setBookId(book.getId());
//        bookIndex.setId(indexId);
//        bookIndex.setCreateTime(date);
//        bookIndex.setUpdateTime(date);
//        bookIndexList.add(bookIndex);
    }
    public void getBookInfo(JXDocument doc){
//        Book book = new Book();
        //设置小说名
        List<Object> bkNm = doc.sel("//title");
        Element bkNmStr = (Element)bkNm.get(0);
        String bkNmtext = bkNmStr.text().split("-")[0] +"-"+ bkNmStr.text().split("-")[1];
        System.out.println("bkNm:"+bkNmtext);
//        book.setBookName(bkNm.toString());

        //设置作者名
        String auNm ="佚名";
        System.out.println("auNm"+auNm);
//        book.setAuthorName(auNm.toString());
        //设置封面图片路径
        String url = "";
        System.out.println("url"+url);
//        book.setPicUrl(url.toString());
        //设置评分
//        book.setVisitCount(RandomBookInfoUtil.getVisitCountByScore(8.5f));
        //设置访问次数
//        book.setScore(RandomBookInfoUtil.getScoreByVisitCount(20000L));
        //设置书籍简介
        String desc =  "";
        System.out.println("desc"+desc);
//        book.setBookDesc(desc.toString());

        //设置更新状态
        Byte status = 1;
        System.out.println("status"+status.toString());
//        book.setBookStatus(status);

        //设置分类
//            book.setWorkDirection(new Byte("0"));
//            book.setCatId(1);
//            book.setCatName("都市");

        //bookid
        String bookId = bkNmStr.text().split("-")[1];
        System.out.println("bookId"+bookId);

//        book.setCrawlBookId(bookId);
//        book.setCrawlSourceId(6);  //笔趣阁
//        book.setCreateTime(new Date());
//        book.setCrawlLastTime(new Date());
//        book.setId(idWorker.nextId());
//
//        book.setWordCount(10000);
//        book.setUpdateTime(new Date());
    }
}

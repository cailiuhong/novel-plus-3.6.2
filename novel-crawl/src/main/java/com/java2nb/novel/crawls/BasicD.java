package com.java2nb.novel.crawls;

import cn.wanghaomiao.seimi.annotation.Crawler;
import cn.wanghaomiao.seimi.def.BaseSeimiCrawler;
import cn.wanghaomiao.seimi.struct.Request;
import cn.wanghaomiao.seimi.struct.Response;
import com.java2nb.novel.core.utils.RandomBookInfoUtil;
import com.java2nb.novel.core.utils.StringUtil;
import com.java2nb.novel.entity.Book;
import com.java2nb.novel.entity.BookContent;
import com.java2nb.novel.entity.BookIndex;
import com.java2nb.novel.service.BookService;
import io.github.xxyopen.util.IdWorker;
import org.jsoup.nodes.Element;
import org.seimicrawler.xpath.JXDocument;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author 汪浩淼 [et.tw@163.com]
 * @since 2015/10/21.
 */
@Crawler(name = "basic_d")
public class BasicD extends BaseSeimiCrawler {
    private static final IdWorker idWorker = IdWorker.INSTANCE;
    private List<BookContent> contentList = Collections.synchronizedList(new ArrayList<>(512));
    private List<BookIndex> bookIndexList = Collections.synchronizedList(new ArrayList<>(512));
    private AtomicInteger atomicInteger = new AtomicInteger(0);

    private AtomicInteger indexnum = new AtomicInteger(1);

    @Resource
    private BookService bookService;

    private Book book = new Book();
    public volatile static Boolean flag = false;

    @Override
    public String[] startUrls() {
        //两个是测试去重的
        return new String[]{"http://www.w78e.buzz/AAbook/AAAtb/lingleix/index.html"};
    }

    @Override
    public void start(Response response) {
        JXDocument doc = response.document();
        try {

            contentList.clear();
            bookIndexList.clear();
            atomicInteger.set(0);
            //书的信息book
            getBookInfo(doc);
            List<Object> urls = doc.sel("(//div[@class='classList']//a[contains(@href,'/AAbook/AAAwz/')]/@href)");
            logger.info("{}", urls.size());
            atomicInteger.getAndSet(urls.size());
            flag = false;
            for (Object s:urls){
                System.out.println(s.toString());
                push(Request.build("http://www.w78e.buzz"+s.toString(), BasicD::getTitle));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void getTitle(Response response){
        JXDocument doc = response.document();
        try {
            //Id
            Long indexId = idWorker.nextId();
            //content
            getBookContentInfo(doc,indexId);
            //index
            getBookIndexInfo(doc,indexId);

//            logger.info("listSize:"+contentList.size());
            if(contentList.size() == atomicInteger.get() && bookIndexList.size() == atomicInteger.get()){
                logger.info("allList size()={}",contentList.size());
                flag = true;
                BookIndex lastIndex = bookIndexList.get(bookIndexList.size() - 1);
                book.setLastIndexId(lastIndex.getId());
                book.setLastIndexName(lastIndex.getIndexName());
                book.setLastIndexUpdateTime(new Date());
                book.setCrawlLastTime(new Date());
                bookService.saveBookAndIndexAndContent(book,bookIndexList,contentList);

                contentList.clear();
                bookIndexList.clear();
//                book = new Book();
                Thread.sleep(2200);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void getBookContentInfo(JXDocument doc,Long indexId){
        //内容
        List<Object> content = doc.sel("//div[@class='content']");

        BookContent bookContent = new BookContent();
        bookContent.setContent(content.toString());
        bookContent.setIndexId(indexId);
        contentList.add(bookContent);
    }

    public void getBookIndexInfo(JXDocument doc,Long indexId){
        //indexName
        List<Object> indexName = doc.sel("//h1");
        Element indexNameE = (Element)indexName.get(0);
//        System.out.println("indexName"+indexNameE.text());
        int andAdd = indexnum.getAndAdd(1);
        //indexNum
        Integer indexNumTmp = andAdd;
//        String indexNumTmp = Long.toString(l);
        System.out.println("indexNum"+indexNumTmp);

        //wordCount
        //内容
        List<Object> content = doc.sel("//div[@class='content']");
        Element contentStr = (Element)content.get(0);
//        System.out.println("indexName"+contentStr.text());
        System.out.println("------------");

        BookIndex bookIndex = new BookIndex();
        bookIndex.setIndexName(indexNameE.text());
        bookIndex.setIndexNum(indexNumTmp);
        int wordCount = StringUtil.getStrValidWordCount(contentStr.text());
        bookIndex.setWordCount(wordCount);
        Long id = book.getId();
        if(id == null){
            id = book.getId();
        }
        bookIndex.setBookId(id);
        bookIndex.setId(indexId);
        bookIndex.setCreateTime(new Date());
        bookIndex.setUpdateTime(new Date());
        bookIndexList.add(bookIndex);
    }
    public void getBookInfo(JXDocument doc){
//        Book book = new Book();
        //设置小说名
        List<Object> bkNm = doc.sel("//title");
        Element bkNmStr = (Element)bkNm.get(0);
        String bkNmtext = bkNmStr.text().split("-")[0] +"-"+ bkNmStr.text().split("-")[1];
        System.out.println("bkNm:"+bkNmtext);
//        bkNmtext = bkNmtext.toString() + new Date().toString();
        book.setBookName(bkNmtext.toString());

        //设置作者名
        String auNm ="佚名";
        System.out.println("auNm"+auNm);
        book.setAuthorName(auNm.toString());
        //设置封面图片路径
        String url = "xxx";
        System.out.println("url"+url);
        book.setPicUrl(url.toString());
        //设置评分
        book.setVisitCount(RandomBookInfoUtil.getVisitCountByScore(8.5f));
        //设置访问次数
        book.setScore(RandomBookInfoUtil.getScoreByVisitCount(20000L));
        //设置书籍简介
        String desc =  "";
        System.out.println("desc"+desc);
        book.setBookDesc(desc.toString());

        //设置更新状态
        Byte status = 1;
        System.out.println("status"+status.toString());
        book.setBookStatus(status);

        //设置分类
        book.setWorkDirection(new Byte("0"));
        book.setCatId(1);
        book.setCatName("都市");

        //bookid
        String bookId = bkNmStr.text().split("-")[1];
        System.out.println("bookId"+bookId);

        book.setCrawlBookId(bookId);
        book.setCrawlSourceId(6);  //笔趣阁
        book.setCreateTime(new Date());
        book.setCrawlLastTime(new Date());
        book.setId(idWorker.nextId());
        System.out.println("book:"+book.getId());

        book.setWordCount(10000);
        book.setUpdateTime(new Date());
    }
}

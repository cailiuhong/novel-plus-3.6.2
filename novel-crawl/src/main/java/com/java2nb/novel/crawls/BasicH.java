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
import org.apache.commons.lang3.StringUtils;
import org.jsoup.nodes.Element;
import org.seimicrawler.xpath.JXDocument;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
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
@Crawler(name = "basic_h")
@Service
//@RequiredArgsConstructor
public class BasicH extends BaseSeimiCrawler {
    private static final IdWorker idWorker = IdWorker.INSTANCE;
    private List<BookContent> contentList = Collections.synchronizedList(new ArrayList<>(512));
    private List<BookIndex> bookIndexList = Collections.synchronizedList(new ArrayList<>(512));
    private AtomicInteger atomicInteger = new AtomicInteger(0);

    private Book book = new Book();
    public   static  AtomicBoolean atomicBoolean = new AtomicBoolean(false);

//    @Resource
//    private   BookMapper bookMapper;
//
//    @Resource
//    private BookContentMapper bookContentMapper;
//
//    @Resource
//    private  CrawlBookIndexMapper bookIndexMapper;

    @Resource
    private  BookService bookService;


    @Override
    public String[] startUrls() {
        //两个是测试去重的
        return new String[]{"https://www.h513.com/book/95936/"};
    }

    @Override
    public void start(Response response) {
        JXDocument doc = response.document();
        try {
            //书的信息book
            book = getBookInfo(doc);
            atomicInteger.set(0);
            List<Object> urls = doc.sel("(//div[@class='listmain']//a[contains(@href,'/book/')]/@href)");
            logger.info("{}", urls.size());
            atomicInteger.getAndSet(urls.size());
            atomicBoolean.getAndSet(false);
            for (Object s:urls){
                System.out.println(s.toString());
                push(Request.build("https://www.h513.com"+s.toString(), BasicH::getTitle));
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
            System.out.println("Id"+indexId);
            //content
            getBookContentInfo(doc,indexId);
            //index
            getBookIndexInfo(doc,indexId);

            logger.info("listSize:"+contentList.size());
            if(contentList.size() == atomicInteger.get() && bookIndexList.size() == atomicInteger.get()){
//                logger.info("allList size()={}",contentList.size());
                atomicBoolean.getAndSet(true);
                BookIndex lastIndex = bookIndexList.get(bookIndexList.size() - 1);
                book.setLastIndexId(lastIndex.getId());
                book.setLastIndexName(lastIndex.getIndexName());
                book.setLastIndexUpdateTime(new Date());
                book.setCrawlLastTime(new Date());
                bookService.saveBookAndIndexAndContent(book,bookIndexList,contentList);

                contentList.clear();
                bookIndexList.clear();
                book = new Book();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void getBookContentInfo(JXDocument doc,Long indexId){
        //内容
        List<Object> content = doc.sel("//div[@id='chaptercontent']");

        BookContent bookContent = new BookContent();
        bookContent.setContent(content.toString());
        bookContent.setIndexId(indexId);
        contentList.add(bookContent);
    }

    public void getBookIndexInfo(JXDocument doc,Long indexId){

        //indexName
        List<Object> indexName = doc.sel("//h1/text()");
        Element indexNameE = (Element)indexName.get(0);
        System.out.println("indexName"+indexNameE.text());

        //indexName
        List<Object> indexNum = doc.sel("//meta[@http-equiv='mobile-agent']/@content");
        String indexNumStr = (String)indexNum.get(0);
        String reg = "([0-9]+).html";
        Pattern pattern = Pattern.compile(reg);
        Matcher matcher = pattern.matcher(indexNumStr);

        String indexNumTmp = "";
        if(matcher.find()){
            indexNumTmp = matcher.group(0);
            indexNumTmp = indexNumTmp.split("\\.")[0];

        }
        System.out.println("indexNum"+indexNumTmp);

        //wordCount
        //内容
        List<Object> content = doc.sel("//div[@id='chaptercontent']");
        Element contentStr = (Element)content.get(0);
        System.out.println("indexName"+contentStr.text());

        //BookId
        List<Object> bookId = doc.sel("//meta[@http-equiv='mobile-agent']/@content");
        String bookIdStr = (String)bookId.get(0);
        reg = "/book/([0-9]+)";
        pattern = Pattern.compile(reg);
        matcher = pattern.matcher(indexNumStr);

        String bookIdStrTmp = "";
        if(matcher.find()){
            bookIdStrTmp = matcher.group(0);
            bookIdStrTmp = bookIdStrTmp.split("/")[2];
        }

        System.out.println("BookId"+bookIdStrTmp.toString());
        Date date = new Date();

        BookIndex bookIndex = new BookIndex();
        bookIndex.setIndexName(indexNameE.text());
        bookIndex.setIndexNum(Integer.parseInt(indexNumTmp));
        int wordCount = StringUtil.getStrValidWordCount(contentStr.text());
        bookIndex.setWordCount(wordCount);
        bookIndex.setBookId(book.getId());
        bookIndex.setId(indexId);
        bookIndex.setCreateTime(date);
        bookIndex.setUpdateTime(date);
        bookIndexList.add(bookIndex);
    }
    public Book getBookInfo(JXDocument doc){
        Book book = new Book();
        //设置小说名
        List<Object> bkNm = doc.sel("//meta[@property='og:novel:book_name']/@content");
        System.out.println("bkNm"+bkNm.toString());
        book.setBookName(bkNm.toString());
        //设置作者名
        List<Object> auNm = doc.sel("//meta[@property='og:novel:author']/@content");
        System.out.println("auNm"+auNm.toString());
        book.setAuthorName(auNm.toString());
        //设置封面图片路径
        List<Object> url =  doc.sel("//meta[@property='og:url']/@content");
        System.out.println("url"+url.toString());
        book.setPicUrl(url.toString());
        //设置评分
        book.setVisitCount(RandomBookInfoUtil.getVisitCountByScore(8.5f));
        //设置访问次数
        book.setScore(RandomBookInfoUtil.getScoreByVisitCount(20000L));
        //设置书籍简介
        List<Object> desc =  doc.sel("//div[@class='intro']");
        System.out.println("desc"+desc.toString());
        book.setBookDesc(desc.toString());

        //设置更新状态
        List<Object> status =  doc.sel("//meta[@property='og:novel:status']/@content");
        System.out.println("status"+status.toString());
        book.setBookStatus((new Byte(status.toString().equals("连载") ? "0" : "1")));

        //设置更新时间
        List<Object> update_time =  doc.sel("//meta[@property='og:novel:update_time']/@content");
        System.out.println("update_time"+update_time.toString());
//        book.setUpdateTime(new Dupdate_time.toString());

        //设置分类
        List<Object> category =  doc.sel("//meta[@property='og:novel:category']/@content");
        System.out.println("category"+category.toString());
        String workDirection = category.toString();
        if(StringUtils.isNotBlank(workDirection)){
            Integer wd = workDirection.contains("女生")? 1:0 ;
            book.setWorkDirection(wd.byteValue() );

            Integer catIdTmp = 1;
            if(workDirection.contains("玄幻")){
                catIdTmp = 1;
            } else if (workDirection.contains("武侠")){
                catIdTmp = 1;
            }
            else if (workDirection.contains("都市")){
                catIdTmp = 1;
            }
            else if (workDirection.contains("历史")){
                catIdTmp = 1;
            }
            else if (workDirection.contains("网游")){
                catIdTmp = 1;
            }
            else if (workDirection.contains("科幻")){
                catIdTmp = 1;
            }
            else if (workDirection.contains("女生")){
                catIdTmp = 1;
            }
            book.setCatId(catIdTmp);
            book.setCatName(workDirection);

        } else {
            book.setWorkDirection(new Byte("0"));
            book.setCatId(1);
            book.setCatName("玄幻奇幻");
        }

        //男频女频
//        List<Object> catId =  doc.sel("//meta[@property='og:novel:category']/@content");
//        System.out.println("catId"+catId.toString());
//        workDirection = category.toString();


        //bookid
        List<Object> bookIds =  doc.sel("//meta[@property='og:url']/@content");
        String bookId = (String)bookIds.get(0);
        bookId = bookId.split("book/")[1].split("/")[0];
        System.out.println("bookId"+bookId);
        book.setCrawlBookId(bookId);

//        List<Object> bookId = doc.sel("//meta[@http-equiv='mobile-agent']/@content");
//        String bookIdStr = (String)bookId.get(0);
//        reg = "/book/([0-9]+)";
//        pattern = Pattern.compile(reg);
//        matcher = pattern.matcher(indexNumStr);
//
//        String bookIdStrTmp = "";
//        if(matcher.find()){
//            bookIdStrTmp = matcher.group(0);
//            bookIdStrTmp = bookIdStrTmp.split("/")[2];
//        }


        book.setCrawlSourceId(6);  //笔趣阁
        book.setCreateTime(new Date());
        book.setCrawlLastTime(new Date());
        book.setId(idWorker.nextId());

//        book.setLastIndexId(lastIndex.getId());
//        book.setLastIndexName(lastIndex.getIndexName());
//        book.setLastIndexUpdateTime(currentDate);
        book.setWordCount(10000);
        book.setUpdateTime(new Date());
        return book;
    }
}

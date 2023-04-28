package com.java2nb.novel.controller;

import com.java2nb.novel.entity.CrawlSource;
import com.java2nb.novel.service.CrawlService;
import com.java2nb.novel.vo.AutoCrawlVo;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequestMapping("crawl")
@RequiredArgsConstructor
public class CrawlAutoController {
    private final CrawlService crawlService;

    @PostMapping("autoCrawlSource")
    public  Integer CrawAuto(AutoCrawlVo autoCrawlVo) throws IOException {
        crawlService.autoCrawlSource(autoCrawlVo);
        return 0;
    }
}

package cn.wanghaomiao.seimi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ComponentScans;
import org.springframework.context.annotation.ImportResource;

@ComponentScan("com.java2nb.novel")
@SpringBootApplication
public class SeimiCrawlerApplication {
	public static void main(String[] args) {
		SpringApplication.run(SeimiCrawlerApplication.class, args);
	}
}

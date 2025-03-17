package com.lcyanxi.controller;

import com.lcyanxi.util.CourseClient;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@Slf4j
@RestController
public class ZuicoolController {
    @Resource
    private CourseClient courseClient;

    @GetMapping("/zuicool")
    public String zuicoolList() {
        try {
            // 连接到指定的 URL
            Document doc =Jsoup.parse(courseClient.zuicoolList());

            // 选择包含赛事信息的元素
            Elements events = doc.select("div.event"); // 根据实际的 HTML 结构调整选择器

            // 遍历并输出每个赛事的信息
            for (Element event : events) {
                String title = event.select("h4.name a").text(); // 根据实际情况调整选择器
                String cave = event.select("div.logo img").attr("src");
                // 提取日期和地点
                String[] dateAndLocation = event.select("div.info").text().split("·");
                String eventDate = dateAndLocation[0].trim();
                String eventLocation = dateAndLocation[1].trim();

                String detailUrl = event.select("a.status_bth").attr("href");
                String registrationDeadline = event.select("div.meta span ").get(1).select("span span").text().replace("报名截止：", "").trim();
                String[] split = detailUrl.split("/");
                detailProcess(split[split.length - 1]);
                System.out.printf("赛事名称: %s, 日期:%s , 地点: %s, 报名时间: %s, id：%s%n", title, eventDate, eventLocation, registrationDeadline, detailUrl);
                break;
            }
            String href = doc.select("li.next a").attr("href");
            System.out.println(href);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return "success";
    }

    private void detailProcess(String id){
        Document doc =Jsoup.parse(courseClient.zuicoolDetail(id));
        String description = doc.select("meta[name=description]").attr("content");
        // 提取 event-contact 联系信息
        Element contactElement = doc.select(".event-contact").first();
        String contactInfo = contactElement != null ? contactElement.text() : "未找到联系信息";
        System.out.printf("详情内容: %s, 联系人:%s%n", description,contactInfo);
    }
}

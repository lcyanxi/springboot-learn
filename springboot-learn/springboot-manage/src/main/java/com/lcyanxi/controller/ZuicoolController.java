//package com.lcyanxi.controller;
//
//import com.lcyanxi.util.CourseClient;
//import lombok.extern.slf4j.Slf4j;
//import okhttp3.OkHttpClient;
//import okhttp3.Request;
//import okhttp3.Response;
//import org.apache.commons.lang3.StringUtils;
//import org.jsoup.Jsoup;
//import org.jsoup.nodes.Document;
//import org.jsoup.nodes.Element;
//import org.jsoup.select.Elements;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.RestController;
//
//import javax.annotation.Resource;
//import java.io.IOException;
//import java.util.ArrayList;
//import java.util.List;
//import java.util.regex.Matcher;
//import java.util.regex.Pattern;
//
//@Slf4j
//@RestController
//public class ZuicoolController {
//    @Resource
//    private CourseClient courseClient;
//
//    @GetMapping("/zuicool")
//    public String zuicoolList() {
//        try {
//            // 连接到指定的 URL
//            Document doc =Jsoup.parse(courseClient.zuicoolList());
//
//            // 选择包含赛事信息的元素
//            Elements events = doc.select("div.event"); // 根据实际的 HTML 结构调整选择器
//
//            // 遍历并输出每个赛事的信息
//            for (Element event : events) {
//                String title = event.select("h4.name a").text(); // 根据实际情况调整选择器
//                String cave = event.select("div.logo img").attr("src");
//                // 提取日期和地点
//                String[] dateAndLocation = event.select("div.info").text().split("·");
//                String eventDate = dateAndLocation[0].trim();
//                String eventLocation = dateAndLocation[1].trim();
//
//                String detailUrl = event.select("a.status_bth").attr("href");
//                String registrationDeadline = event.select("div.meta span ").get(1).select("span span").text().replace("报名截止：", "").trim();
//                String[] split = detailUrl.split("/");
//                detailProcess(split[split.length - 1]);
//                System.out.printf("赛事名称: %s, 日期:%s , 地点: %s, 报名时间: %s, id：%s%n", title, eventDate, eventLocation, registrationDeadline, detailUrl);
//                break;
//            }
//            String href = doc.select("li.next a").attr("href");
//            System.out.println(href);
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return "success";
//    }
//
//    private void detailProcess(String id){
//        Document doc =Jsoup.parse(courseClient.zuicoolDetail(id));
//        String description = doc.select("meta[name=description]").attr("content");
//        // 提取 event-contact 联系信息
//        Element contactElement = doc.select(".event-contact").first();
//        String contactInfo = contactElement != null ? contactElement.text() : "未找到联系信息";
//        System.out.printf("详情内容: %s, 联系人:%s%n", description,contactInfo);
//    }
//
//    public static void main(String[] args) {
//            String url = "https://zuicool.com/event/59067"; // 要解析的 URL
//            String htmlContent = fetchHtml(url);
//
//            if (htmlContent != null) {
//                System.out.println("获取的 HTML 内容：");
//                System.out.println(htmlContent);
//            } else {
//                System.out.println("未能获取 HTML 内容。");
//            }
//        }
//
//        public static String fetchHtml(String url) {
//            OkHttpClient client = new OkHttpClient();
//
//            // 创建请求对象
//            Request request = new Request.Builder()
//                    .url(url)
//                    .build();
//
//            try (Response response = client.newCall(request).execute()) {
//                // 检查响应是否成功
//                if (!response.isSuccessful()) {
//                    throw new IOException("Unexpected code " + response);
//                }
//                // 返回 HTML 内容
//                return response.body().string();
//            } catch (IOException e) {
//                e.printStackTrace();
//                return null;
//            }
//        }
//
//
//
//    private static String extractUrls(String text) {
//        if (StringUtils.isBlank(text)) {
//            return "";
//        }
//        // 正则表达式匹配网址
//        String urlRegex = "https?://[\\w.-]+(:\\d+)?(/[^\\s]*)?";
//        Pattern pattern = Pattern.compile(urlRegex);
//        Matcher matcher = pattern.matcher(text);
//        // 查找并输出所有网址
//        if (matcher.find()) {
//            return matcher.group();
//        }
//        return "";
//    }
//}

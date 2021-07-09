package com.lcyanxi.reptile;

import com.lcyanxi.model.JobInfo;
import org.apache.commons.collections.CollectionUtils;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.processor.PageProcessor;
import us.codecraft.webmagic.scheduler.BloomFilterDuplicateRemover;
import us.codecraft.webmagic.scheduler.PriorityScheduler;
import us.codecraft.webmagic.selector.Html;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author lichang
 * Date: 2021/07/07/6:14 下午
 */
public class GithubRepoPageProcessor implements PageProcessor {

    private Site site = Site.me().setRetryTimes(3).setSleepTime(0);



    @Override
    public void process(Page page) {
        List<String> stringList = page.getHtml().$("#main > div.course-list > div.list.max-1152.clearfix > a", "href").all();
        // 详情页
        if (CollectionUtils.isEmpty(stringList)){
            detailPageInfo(page);
        }else {
            List<String> detailUrl = stringList.stream().map(item -> "http:" + item).collect(Collectors.toList());
            page.addTargetRequests(detailUrl);

            //获取下一页的超链接
            List<String> nextHrefs = page.getHtml().$("#main > div.course-list > div.page > a", "href").all();
            //判断倒数第二个超链接是否为空，也就是下一页的超链接，不为空则进入下一页
            if (CollectionUtils.isNotEmpty(nextHrefs)) {
                String s = nextHrefs.get(nextHrefs.size() - 2);
                System.out.println("nextHrefs : " + s);
                page.addTargetRequest(s);
            }
        }
    }

    private void detailPageInfo(Page page){
        Html html = page.getHtml();
        String title = html.$("title","text").get();
        String teacherName = html.$("#main > div.course-infos > div.w.pr > div.statics.clearfix > div.teacher-info.l > span.tit > a", "text").get();
        String job = html.$("#main > div.course-infos > div.w.pr > div.statics.clearfix > div.teacher-info.l > span.job","text").get();
        JobInfo jobInfo = JobInfo.builder().job(job).teacherName(teacherName).title(title).build();
        page.putField("jobInfo",jobInfo);
    }

    @Override
    public Site getSite() {
        return site;
    }

    public static void main(String[] args) {
        Spider.create(new GithubRepoPageProcessor())
                .setScheduler(new PriorityScheduler().setDuplicateRemover(new BloomFilterDuplicateRemover(10000000)))
                .addPipeline(new JobInfoIsDataBasePipeline())
                .addUrl("http://www.imooc.com/course/list").thread(5).run();
    }
}

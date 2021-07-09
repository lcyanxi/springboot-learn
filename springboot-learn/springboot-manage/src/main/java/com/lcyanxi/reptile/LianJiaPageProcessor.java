package com.lcyanxi.reptile;

import com.google.common.collect.Lists;
import com.lcyanxi.model.LianInfo;
import com.lcyanxi.model.LianJiaInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.processor.PageProcessor;
import us.codecraft.webmagic.scheduler.BloomFilterDuplicateRemover;
import us.codecraft.webmagic.scheduler.PriorityScheduler;
import us.codecraft.webmagic.selector.Html;
import us.codecraft.webmagic.selector.Selectable;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author lichang
 * Date: 2021/07/08/11:21 上午
 */
@Slf4j
public class LianJiaPageProcessor implements PageProcessor {

    private Site site = Site.me().setRetryTimes(3).setSleepTime(1000);

    @Override
    public void process(Page page) {
        Html html = page.getHtml();
        List<String> stringList = html.$("a.content__list--item--aside", "href").all();
        // 详情页
        if (CollectionUtils.isEmpty(stringList)){
            detailPageInfo(page);
        }else {
            // 获取 区域 和 街道
            List<Selectable> nodes = html.$("div.content__list--item--main").nodes();
            if (CollectionUtils.isNotEmpty(nodes)){
                List<LianInfo> infos = Lists.newArrayList();
                for (Selectable node : nodes){
                    String href = node.$("a.twoline", "href").get();
                    List<String> addressList = node.$("p.content__list--item--des > a", "text").all();
                    String address  = StringUtils.join(addressList,",");
                    LianInfo info = LianInfo.builder().address(address).hoursUrl(href).build();
                    infos.add(info);
                }
                page.putField("lianInfo",infos);
            }

            List<String> detailUrl = stringList.stream().map(item -> "https:" + item).collect(Collectors.toList());
            page.addTargetRequests(detailUrl);

            //获取下一页的超链接
            String nextUrl = html.$("div.content__pg", "data-url").get();
            String totalpage = html.$("div.content__pg","data-totalpage").get();
            String curpage = html.$("div.content__pg","data-curpage").get();
            //判断倒数第二个超链接是否为空，也就是下一页的超链接，不为空则进入下一页
            if (StringUtils.isNotBlank(nextUrl)) {
                if (Integer.parseInt(totalpage) > Integer.parseInt(curpage)){
                    int nextNum = Integer.parseInt(curpage) + 1;
                    String param = nextUrl.split("\\{")[0] + nextNum;
                    String url =  param + "rt200600000001";
                    page.addTargetRequest(url);
                }
            }
        }
    }

    @Override
    public Site getSite() {
        return site;
    }

    private void detailPageInfo(Page page){
        Html html = page.getHtml();
        // 房屋名称
        String title = html.$("p.content__title","text").get();
        // 发布时间
        String pushTimeStr = html.$("div.content__subtitle","text").get();
        String pushTime = "";
        if (StringUtils.isNotBlank(pushTimeStr)){
            String[] split = pushTimeStr.split("：");
            if (split.length > 1){
                pushTime = split[1].trim();
            }
        }
        // 价格
        String price = html.$("#aside > div.content__aside--title > span","text").get();
        // 房屋信息
        String houseInfo = html.$("#aside > ul > li:nth-child(2)","text").get();
        String[] strings = dataCovert(houseInfo);
        String houseType = "";
        String area = "";
        if (Objects.nonNull(strings)){
            houseType = strings[0];
            area = strings[1];
        }
        // 楼层
        String floorStr = html.$("#aside > ul > li.floor > span:nth-child(2)","text").get();
        String floor = "";
        String orientation = "";
        String[] strings2 = dataCovert(floorStr);
        if (Objects.nonNull(strings2)){
            floor = strings[0];
            orientation = strings[1];
        }
        // 出租类型
        String rentType = html.$("#aside > ul > li:nth-child(1)","text").get();
        // 房屋链接
        String url = page.getUrl().get();
        // 电梯
        String elevator = html.$("#info > ul:nth-child(2) > li:nth-child(9)","text").get();
        LianJiaInfo info = LianJiaInfo.builder().houseUrl(url).title(title).pushTime(pushTime).price(price)
                .houseType(houseType).area(area).floor(floor).rentType(rentType).orientation(orientation)
                .elevator(elevator).build();
        page.putField("info",info);
    }

    private String[] dataCovert(String data){
        String[] split = null;
        if (StringUtils.isNotBlank(data)){
            split = data.split("\\s+");
        }
        return split;
    }

    public static void main(String[] args) {
        Spider.create(new LianJiaPageProcessor())
                .setScheduler(new PriorityScheduler().setDuplicateRemover(new BloomFilterDuplicateRemover(10000000)))
                .addPipeline(new LianJiaDataPipeline())
                .addUrl("https://bj.lianjia.com/zufang/").thread(5).run();
    }
}

package com.wangzaiplus.test.util;

import cn.hutool.core.util.RandomUtil;
import com.wangzaiplus.test.pojo.Content;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.platform.commons.logging.LoggerFactory;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author Mr.Xu
 * @Description 网页解析
 * @date 2021年09月16日 16:02
 */
@Slf4j
public class DocumentParseUtil {

  private static final String cookie =
      "__jdu=1626399418806696950052; shshshfpa=dda9b047-06ee-3a96-8d55-89c73c921ce2-1626399509; shshshfpb=kFUSUCUmUo6ESuW7NFqYmDg%3D%3D; autoOpenApp_downCloseDate_auto=1630908817369_1800000; downloadAppPlugIn_downCloseDate=1630909226294_1800000; warehistory=\"100025671272,100005207515,100019034176,\"; __jdv=76161171|baidu|-|organic|not set|1631776953142; areaId=19; ipLoc-djd=19-1607-3155-0; PCSYCityID=CN_440000_440300_440305; TrackID=121TY29CeME3-YREKk5TFMPJaMKzMBTD-u-GcIwKu199TxDVo-AsMl9tAnI62kF95Xj31uTFLCO59eq3-C7hgCfuA4Yt9kHjlM_wPlOmSKDI; pinId=c384-PC_Fn4lF40oJyGeeQ; pin=jd_hynlrbHTFoJA; unick=jd_hynlrbHTFoJA; ceshi3.com=000; _tp=QnVT7xlP%2Bvy7NM3h%2FIwGOg%3D%3D; _pst=jd_hynlrbHTFoJA; __jdc=122270672; shshshfp=558b22d2a92c2a23a189a5c80f85c8bc; user-key=0e0e82ff-906f-4a87-b39b-33399145ced2; cn=8; thor=868DF4FBDA1B83037AE949D89997E451122FFB31BF17609429780DD25F18930E37FE823AF11AD075157E54103468EAAC5F03C9BAD2696DE9E05CD5261DB40272F56F0BB1FA32E5E4BB942CB7A5EC25BF48601B2EB671851F819EA4D15CF2F1B7309029A92535FD90EF57A393A8B7E11F810D04BF597910769ADF20E9C0B35BD5A2982CBCCE4632291761166D3F204485791612123F4168C382590E8EEBF16D7F; 3AB9D23F7A4B3C9B=QWCQOYD6N3BKG36K3ZDFFEKP2GCUCT25HJO6HY4RKHDKRLME7XTZKL3DPRAHCFQW3RPVBME2XIUZ4M3LAN4HZF7MOY; __jda=122270672.1626399418806696950052.1626399418.1631859596.1631869256.10; __jdb=122270672.1.1626399418806696950052|10.1631869256; shshshsID=9c57449ad17a292139dd816d6c34fae1_1_1631869256300";
    private static final AtomicInteger ai = new AtomicInteger(0);

    public static List<Content> params(String keywords) {
        ArrayList<Content> goodList = new ArrayList<>();
        try{
            //获取请求 https://search.jd.com/Search?keyword=java
            //前提 需要联网
            int page = RandomUtil.randomInt(0, 10);
            String url = "https://search.jd.com/Search?keyword="+keywords+"&enc=utf-8&page="+page;
            //解析网页
            Connection connect = Jsoup.connect(url);
            Document document = connect.header("cookie", cookie).get();
            //Document document = Jsoup.parse(new URL(url), 30000);
            //所有你在js中可以使用的方法 这里都能用
            Element element = document.getElementById("J_goodsList");
            if (element == null){
                ai.getAndAdd(1);
                log.warn("========================          element为null,失败次数：{}",ai.get());
                return goodList;
            }
            //获取所有的li元素
            Elements elements = element.getElementsByTag("li");
            for (Element el : elements) {
                //关于图片特别多的网站  所有图片都是延迟加载的data-lazy-img
                String img = el.getElementsByTag("img").eq(0).attr("data-lazy-img");
                String price1 = el.getElementsByClass("p-price").select("strong").text();
                String price2 = el.getElementsByClass("p-price").select("span").text();
                String price = price1;
                if (StringUtils.isNotBlank(price2)){
                    price += "折" + price2;
                }
                String title = el.getElementsByClass("p-name").eq(0).text();
                String shopName = el.getElementsByClass("curr-shop hd-shopname").attr("title");
                Content content = new Content();
                content.setImg(img);
                content.setTitle(title);
                content.setPrice(price);
                content.setShopname(shopName);
                goodList.add(content);
            }
        }catch (Exception e){
            log.error("爬取jd数据失败！"+e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("爬取jd数据失败!");
        }
        return goodList;
    }

    public static void main(String[] args) throws Exception {
        new DocumentParseUtil().params("氨基酸洗面奶男").forEach(System.out::println);
    }

}

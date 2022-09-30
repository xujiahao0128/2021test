package com.wangzaiplus.test.task;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpStatus;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.wangzaiplus.test.common.Constant;
import com.wangzaiplus.test.mq.MessageHelper;
import com.wangzaiplus.test.pojo.Mail;
import com.wangzaiplus.test.pojo.MsgLog;
import com.wangzaiplus.test.pojo.juejin.BugDTO;
import com.wangzaiplus.test.pojo.juejin.BugResultDTO;
import com.wangzaiplus.test.pojo.juejin.Check;
import com.wangzaiplus.test.service.MsgLogService;
import com.wangzaiplus.test.util.JedisUtil;
import com.wangzaiplus.test.util.MotivationalPhraseUtils;
import lombok.extern.slf4j.Slf4j;
import lombok.extern.slf4j.XSlf4j;
import org.checkerframework.checker.units.qual.A;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import com.wangzaiplus.test.util.MailUtil;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@Component
public class ResendMsg {

    @Autowired
    private MsgLogService msgLogService;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private JedisUtil jedisUtil;

    @Autowired
    private MailUtil mailUtil;

    // 最大投递次数
    private static final int MAX_TRY_COUNT = 3;

    /** 签到网址 */
    private static final String CHECK_URL = "https://api.juejin.cn/growth_api/v1/check_in";
    /** 抽奖 */
    private static final String DRAW_URL= "https://api.juejin.cn/growth_api/v1/lottery/draw";
    /** 喜气列表 */
    private static final String LOTTERY_URL = "https://api.juejin.cn/growth_api/v1/lottery_history/global_big";
    /** 沾喜气 */
    private static final String HAPPY_URL ="https://api.juejin.cn/growth_api/v1/lottery_lucky/dip_lucky";
    /** bug列表请求地址 */
    private static final String NOT_COLLECT_URL = "https://api.juejin.cn/user_api/v1/bugfix/not_collect";
    /** 收集bug地址 */
    private static final String COLLECT_URL = "https://api.juejin.cn/user_api/v1/bugfix/collect";
    /** 我的签名 */
    private static final String SIGNATURE =
      "_02B4Z6wo00101QCzUJQAAIDBgLGq1zKdXH0At1QAACIzXcjisF5CFAOLoPGtQsFD0Aoonq1fYXsTatmxFJDn1zjwt8Fhrj6b4rvm6n-mPnzvfq3viBpLZfCyVdVq73jw5ZLNIoLjYUG456M-13";
  /** 我的cookie */
  private static final String COOKIE =
      "__tea_cookie_tokens_2608=%7B%22user_unique_id%22%3A%226990528876548769318%22%2C%22web_id%22%3A%226990528876548769318%22%2C%22timestamp%22%3A1646012465415%7D; _ga=GA1.2.1154959302.1646013488; MONITOR_WEB_ID=744f3e62-b512-4b5b-bbd2-f91ce447d659; _tea_utm_cache_2608={\"utm_source\":\"notice2\",\"utm_medium\":\"push\",\"utm_campaign\":\"gengwen202204\"}; _gid=GA1.2.1250598156.1650942505; passport_csrf_token=74d617d89945b8203522e81bbfc28681; passport_csrf_token_default=74d617d89945b8203522e81bbfc28681; _tea_utm_cache_2018=undefined; n_mh=eSsrFQ8vZyQLVqoTx6rApVJ9f6b74ThBMg7Li6gtMKs; uid_tt=80a1b8fc0b7f3d11925dc499fc7a4153; uid_tt_ss=80a1b8fc0b7f3d11925dc499fc7a4153; sid_tt=ef02c01c5ff6f4f4244bd8917b255028; sessionid=ef02c01c5ff6f4f4244bd8917b255028; sessionid_ss=ef02c01c5ff6f4f4244bd8917b255028; sid_guard=ef02c01c5ff6f4f4244bd8917b255028|1651198286|31536000|Sat,+29-Apr-2023+02:11:26+GMT; sid_ucp_v1=1.0.0-KGU2MWFiMjk3Nzg3ODcwYjUwMGM2NWQ4MzE3YmVjNTliOGUyYjhkOTkKFwjYy9DvsYyOBBDOkq2TBhiwFDgCQO8HGgJsZiIgZWYwMmMwMWM1ZmY2ZjRmNDI0NGJkODkxN2IyNTUwMjg; ssid_ucp_v1=1.0.0-KGU2MWFiMjk3Nzg3ODcwYjUwMGM2NWQ4MzE3YmVjNTliOGUyYjhkOTkKFwjYy9DvsYyOBBDOkq2TBhiwFDgCQO8HGgJsZiIgZWYwMmMwMWM1ZmY2ZjRmNDI0NGJkODkxN2IyNTUwMjg";

    /**
     * 每30s拉取投递失败的消息, 重新投递
     */
    //@Scheduled(cron = "0/20 * * * * ?")
    public void resend() {
        log.info("开始执行定时任务(重新投递消息)");

        List<MsgLog> msgLogs = msgLogService.selectTimeoutMsg();
        msgLogs.forEach(msgLog -> {
            String msgId = msgLog.getMsgId();
            if (msgLog.getTryCount() >= MAX_TRY_COUNT) {
                msgLogService.updateStatus(msgId, Constant.MsgLogStatus.DELIVER_FAIL);
                log.info("超过最大重试次数, 消息投递失败, msgId: {}", msgId);
            } else {
                msgLogService.updateTryCount(msgId, msgLog.getNextTryTime());// 投递次数+1

                CorrelationData correlationData = new CorrelationData(msgId);
                rabbitTemplate.convertAndSend(msgLog.getExchange(), msgLog.getRoutingKey(), MessageHelper.objToMsg(msgLog.getMsg()), correlationData);// 重新投递

                log.info("第 " + (msgLog.getTryCount() + 1) + " 次重新投递消息");
            }
        });

        log.info("定时任务执行结束(重新投递消息)");
    }

    @Test
    public void test(){
        AtomicInteger count = new AtomicInteger(1);

        HashMap<String, Object> paramData = new HashMap<>();
        paramData.put("aid",2608);
        paramData.put("uuid",6990528876548769318L);

        HashMap<String, String> headerData = new HashMap<>();
        headerData.put("cookie",COOKIE);

        StringBuilder drawStr = new StringBuilder("【掘金抽奖】");

        HttpRequest httpRequest = HttpRequest.post(DRAW_URL)
                .form(paramData)
                .headerMap(headerData, true);

        boolean flag =true;
        while(flag){
            if ((count.get() > 500)) {
                flag = false;
            }
            HttpResponse response = httpRequest.execute();
            Check draw = JSON.parseObject(response.body(), new TypeReference<Check>() {});
            if(response.isOk()){
                if (draw != null){
                    String errMsg = draw.getErr_msg();
                    if (StrUtil.isNotBlank(errMsg)){
                        if (errMsg.equals("success")){
                            int andAdd = count.getAndAdd(1);
                            //本次抽奖获取的物品名称
                            String lotteryName = (String) (draw.getData().get("lottery_name")==null?0:draw.getData().get("lottery_name"));
                            System.out.println("抽奖成功~ 【"+lotteryName+"】   第【"+andAdd+"】次");
                        }else {
                            flag = false;
                            System.out.println("失败~"+draw.getErr_msg());
                        }
                    }
                }
            }
        }
    }

    /** 掘金签到、抽奖定时任务，每天凌晨执行 */
    //@Scheduled(cron = "0/30 * * * * ?")
    @Scheduled(cron = "0 0 12 * * ?")
    public void check() {
        try{
            SimpleDateFormat sdf = new SimpleDateFormat("YYYY-MM-dd HH:mm:ss");
            SimpleDateFormat sdf2 = new SimpleDateFormat("YYYY-MM-dd");
            String date = sdf.format(new Date());
            String date2 = sdf2.format(new Date());
            HashMap<String, Object> paramData = new HashMap<>();
            paramData.put("aid",2608);
            paramData.put("uuid",6990528876548769318L);

            HashMap<String, String> headerData = new HashMap<>();
            headerData.put("cookie",COOKIE);

            paramData.put("_signature",SIGNATURE);
            //每天一次签到
            HttpResponse checkResponse = HttpRequest.post(CHECK_URL)
                    .form(paramData)
                    .headerMap(headerData, true)
                    .execute();

            StringBuilder checkStr = new StringBuilder("【掘金签到】");
            Check check = JSON.parseObject(checkResponse.body(), new TypeReference<Check>() {});
            if(checkResponse.isOk()){
                if (check != null){
                    String errMsg = check.getErr_msg();
                    if (StrUtil.isNotBlank(errMsg)){
                        if (errMsg.equals("success")){
                            //本次签到获得矿石数
                            Integer incrPoint = (Integer) (check.getData().get("incr_point")==null?0:check.getData().get("incr_point"));
                            //当前矿石总数
                            Integer sumpoint = (Integer) (check.getData().get("sum_point")==null?0:check.getData().get("sum_point"));
                            checkStr.append("\n 签到成功~【日期:"+date+"】"+"本次签到获取矿石：=>["+incrPoint+"],当前总矿石数：=>["+sumpoint+"]");
                        }else if (errMsg.equals("您今日已完成签到，请勿重复签到")){
                            checkStr.append("\n 您今日已完成签到~【日期:"+date+"】");
                        }else {
                            checkStr.append("\n 签到失败~【日期:"+date+"】"+"\n【错误信息:】"+check.getErr_msg());
                        }
                    }
                }
            }else {
                String errMsg = check.getErr_msg();
                checkStr.append("\n 签到失败~【日期:"+date+"】"+"\n【错误信息:】"+errMsg);
                if (errMsg.equals("must login")){
                    checkStr.append("\n 登录过期了~");
                }
            }
            log.info("\n==========================================\n"+checkStr.toString()+"\n==========================================");
            //沾喜气
            lottery();

            //收集bug
            collectBug();

            StringBuilder drawStr = new StringBuilder("【掘金抽奖】");

            //每天白嫖一次抽奖
            HttpResponse drawResponse = HttpRequest.post(DRAW_URL)
                    .form(paramData)
                    .headerMap(headerData, true)
                    .execute();
            Check draw = JSON.parseObject(drawResponse.body(), new TypeReference<Check>() {});
            if(drawResponse.isOk()){
                if (draw != null){
                    String errMsg = draw.getErr_msg();
                    if (StrUtil.isNotBlank(errMsg)){
                        if (errMsg.equals("success")){
                            //本次抽奖获取的物品名称
                            String lotteryName = (String) (draw.getData().get("lottery_name")==null?0:draw.getData().get("lottery_name"));
                            drawStr.append("\n 抽奖成功~【日期:"+date+"】"+"本次抽奖获取：=>["+lotteryName+"]");
                        }else {
                            drawStr.append("\n 抽奖失败~【日期:"+date+"】"+"\n【错误信息:】"+draw.getErr_msg());
                        }
                    }
                }
            }else {
                String errMsg = draw.getErr_msg();
                drawStr.append("\n 抽奖失败~【日期:"+date+"】"+"\n【错误信息:】"+errMsg);
                if (errMsg.equals("must login")){
                    drawStr.append("\n 登录过期了~");
                }
            }

            log.info("\n==========================================\n"+drawStr.toString()+"\n==========================================");
/*            Mail mail = Mail.builder()
                    .to("3179445633@qq.com")
                    .title("掘金自动签到,日期:" + date)
                    .content(drawStr.toString()+"\n"+checkStr.toString())
                    .build();
            mailUtil.send(mail);*/
        }catch (Exception e){
            log.error("掘金签到脚本执行失败~",e.getMessage());
            e.printStackTrace();
        }
    }

    public void collectBug(){
        SimpleDateFormat sdf = new SimpleDateFormat("YYYY-MM-dd HH:mm:ss");
        SimpleDateFormat sdf2 = new SimpleDateFormat("YYYY-MM-dd");
        String date = sdf.format(new Date());
        HashMap<String, Object> paramData = new HashMap<>();
        paramData.put("aid",2608);
        paramData.put("uuid",6990528876548769318L);

        HashMap<String, String> headerData = new HashMap<>();
        headerData.put("cookie",COOKIE);

        paramData.put("_signature",SIGNATURE);

        //bug列表
        HttpResponse collectBugListResponse = HttpRequest.post(NOT_COLLECT_URL)
                .form(paramData)
                .headerMap(headerData, true)
                .execute();

        StringBuilder checkStr = new StringBuilder("【掘金bug列表】");
        BugResultDTO bugResultDTO = JSON.parseObject(collectBugListResponse.body(), new TypeReference<BugResultDTO>() {});

        if(collectBugListResponse.isOk()){
            if (bugResultDTO != null){
                String errMsg = bugResultDTO.getErr_msg();
                if (StrUtil.isNotBlank(errMsg)){
                    if (errMsg.equals("success")){
                        String jsonString = JSON.toJSONString(bugResultDTO.getData());

                        List<BugDTO> bugDTOS = JSON.parseArray(jsonString, BugDTO.class);
                        if (CollUtil.isNotEmpty(bugDTOS)){
                            for (BugDTO bugDTO : bugDTOS) {
                                HashMap<String, Object> bodyData = new HashMap<>();
                                bodyData.put("bug_type",bugDTO.getBugType());
                                bodyData.put("bug_time",bugDTO.getBugTime());

                                //收集bug
                                HttpResponse collectBugResponse = HttpRequest.post(COLLECT_URL)
                                        .form(paramData).body(JSON.toJSONString(bodyData))
                                        .headerMap(headerData, true)
                                        .execute();

                                Check collectBug = JSON.parseObject(collectBugResponse.body(), new TypeReference<Check>() {});
                                if (collectBugResponse.isOk()){
                                    if (collectBug != null){
                                        checkStr.append("\n 收集bug成功~【日期:"+date+"");
                                    }
                                }
                            }
                        }
                    }else {
                        checkStr.append("\n 收集bug失败~【日期:"+date+"】"+"\n【错误信息:】"+bugResultDTO.getErr_msg());
                    }
                }
            }
        }else {
            String errMsg = bugResultDTO.getErr_msg();
            checkStr.append("\n 收集bug失败~【日期:"+date+"】"+"\n【错误信息:】"+errMsg);
            if (errMsg.equals("must login")){
                checkStr.append("\n 登录过期了~");
            }
        }
        log.info("\n==========================================\n"+checkStr.toString()+"\n==========================================");
    }

    /** 沾喜气 */
    public void lottery(){
        SimpleDateFormat sdf = new SimpleDateFormat("YYYY-MM-dd HH:mm:ss");
        SimpleDateFormat sdf2 = new SimpleDateFormat("YYYY-MM-dd");
        String date = sdf.format(new Date());
        String date2 = sdf2.format(new Date());
        HashMap<String, Object> paramData = new HashMap<>();
        paramData.put("aid",2608);
        paramData.put("uuid",6990528876548769318L);

        HashMap<String, String> headerData = new HashMap<>();
        headerData.put("cookie",COOKIE);

        paramData.put("_signature",SIGNATURE);

        HashMap<String, Integer> bodyData = new HashMap<>();
        bodyData.put("page_no",1);
        bodyData.put("page_size",5);
        //喜气列表
        HttpResponse lotteryResponse = HttpRequest.post(LOTTERY_URL)
                .form(paramData).body(JSON.toJSONString(bodyData))
                .headerMap(headerData, true)
                .execute();

        StringBuilder checkStr = new StringBuilder("【掘金沾喜气】");
        Check check = JSON.parseObject(lotteryResponse.body(), new TypeReference<Check>() {});
        if(lotteryResponse.isOk()){
            if (check != null){
                String errMsg = check.getErr_msg();
                if (StrUtil.isNotBlank(errMsg)){
                    if (errMsg.equals("success")){
                        List<Map<String, Object>> lotteries = JSONObject.parseObject(check.getData().get("lotteries").toString(), new TypeReference<List<Map<String, Object>>>() {
                        });
                        if (CollUtil.isNotEmpty(lotteries)){
                            //喜气id
                            String historyId= lotteries.get(0).get("history_id").toString();
                            HashMap<String, String> body = new HashMap<>();
                            body.put("lottery_history_id",historyId);
                            HttpResponse happyResponse = HttpRequest.post(HAPPY_URL)
                                    .form(paramData).body(JSON.toJSONString(body))
                                    .headerMap(headerData, true)
                                    .execute();
                            Check happy = JSON.parseObject(happyResponse.body(), new TypeReference<Check>() {});
                            if (happyResponse.isOk()){
                                if (happy != null){
                                    String totalValue = happy.getData().get("total_value").toString();
                                    checkStr.append("\n 沾喜气成功~【日期:"+date+"】"+"当前喜气值：=>["+totalValue+"]");
                                }
                            }
                        }
                    }else {
                        checkStr.append("\n 沾喜气失败~【日期:"+date+"】"+"\n【错误信息:】"+check.getErr_msg());
                    }
                }
            }
        }else {
            String errMsg = check.getErr_msg();
            checkStr.append("\n 签到失败~【日期:"+date+"】"+"\n【错误信息:】"+errMsg);
            if (errMsg.equals("must login")){
                checkStr.append("\n 登录过期了~");
            }
        }
        log.info("\n==========================================\n"+checkStr.toString()+"\n==========================================");
    }

}

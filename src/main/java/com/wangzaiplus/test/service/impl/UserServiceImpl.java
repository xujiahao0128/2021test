package com.wangzaiplus.test.service.impl;

import com.wangzaiplus.test.common.Constant;
import com.wangzaiplus.test.common.ResponseCode;
import com.wangzaiplus.test.common.ServerResponse;
import com.wangzaiplus.test.config.RabbitConfig;
import com.wangzaiplus.test.mapper.MsgLogMapper;
import com.wangzaiplus.test.mapper.UserMapper;
import com.wangzaiplus.test.mq.MessageHelper;
import com.wangzaiplus.test.pojo.LoginLog;
import com.wangzaiplus.test.pojo.MsgLog;
import com.wangzaiplus.test.pojo.User;
import com.wangzaiplus.test.service.UserService;
import com.wangzaiplus.test.service.batch.mapperproxy.MapperProxy;
import com.wangzaiplus.test.util.JedisUtil;
import com.wangzaiplus.test.util.JodaTimeUtil;
import com.wangzaiplus.test.util.RandomUtil;
import com.wangzaiplus.test.util.RedissonUtil;
import jodd.util.CollectionUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.redisson.api.RLock;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private MsgLogMapper msgLogMapper;

    @Autowired
    private JedisUtil jedisUtil;

    @Override
    public String testLock() {
        /*RLock rLock = null;
            try{
                rLock = RedissonUtil.lock("test_getAll_users", TimeUnit.SECONDS, 60);
                log.info("加锁成功");
                return userMapper.selectAll();
            }catch (Exception e){
                log.info("获取锁失败"+e.getMessage());
            }finally{
                rLock.unlock();
            }*/
        /*boolean lock = RedissonUtil.tryLock("test_getAll_users", TimeUnit.SECONDS, 10, 60);
        log.info("获取锁：{},当前线程：{}",lock,Thread.currentThread().getName());
        if (lock){
            return "获取锁成功";
        }*/
        return "获取锁失败";
    }

    @Override
    @Async("myTaskExecutor")
    public List<User> getAll() {
        log.info("当前线程：{}",Thread.currentThread().getName());
        return userMapper.selectAll();
    }

    @Override
    public User getOne(Integer id) {
        return userMapper.selectOne(id);
    }

    @Override
    public void add(User user) {
        userMapper.insert(user);
    }

    @Override
    public void update(User user) {
        userMapper.update(user);
    }

    @Override
    public void delete(Integer id) {
        userMapper.delete(id);
    }

    @Override
    public User getByUsernameAndPassword(String username, String password) {
        return userMapper.selectByUsernameAndPassword(username, password);
    }

    @Override
    public ServerResponse login(String username, String password) {
        if (StringUtils.isBlank(username) || StringUtils.isBlank(password)) {
            return ServerResponse.error(ResponseCode.USERNAME_OR_PASSWORD_EMPTY.getMsg());
        }

        User user = userMapper.selectByUsernameAndPassword(username, password);
        if (null == user) {
            return ServerResponse.error(ResponseCode.USERNAME_OR_PASSWORD_WRONG.getMsg());
        }

        saveAndSendMsg(user);

        return ServerResponse.success();
    }
    @Override
    public ServerResponse loginOut(String username) {
        if (StringUtils.isBlank(username)) {
            return ServerResponse.error(ResponseCode.USERNAME_OR_PASSWORD_EMPTY.getMsg());
        }

        User user = userMapper.getUserByName(username);
        if (null == user) {
            return ServerResponse.error(ResponseCode.USER_NOT_EXISTS.getMsg());
        }

        saveAndSendMsg(user);

        return ServerResponse.success();
    }
    private void saveAndSendMsgLoginOut(User user) {
        String msgId = RandomUtil.UUID32();
        LoginLog loginLog = new LoginLog();
        loginLog.setUserId(user.getId());
        loginLog.setType(Constant.LogType.LOGOUT);
        Date date = new Date();
        loginLog.setDescription(user.getUsername() + "在" + JodaTimeUtil.dateToStr(date) + "登出系统");
        loginLog.setCreateTime(date);
        loginLog.setUpdateTime(date);
        loginLog.setMsgId(msgId);

        CorrelationData correlationData = new CorrelationData(msgId);
        //转换消息且发送               交换机                                 路由                                     消息转换                           唯一id和消息message绑定、将msgId和message关系保存后, 在confirmListener或者returnListener中,就可以根据msgId获取message进行对于操作.
        rabbitTemplate.convertAndSend(RabbitConfig.LOGIN_LOG_EXCHANGE_NAME, RabbitConfig.LOGIN_LOG_ROUTING_KEY_NAME, MessageHelper.objToMsg(loginLog), correlationData);

        MsgLog msgLog = new MsgLog(msgId, loginLog, RabbitConfig.LOGIN_LOG_EXCHANGE_NAME, RabbitConfig.LOGIN_LOG_ROUTING_KEY_NAME);
        msgLogMapper.insert(msgLog);
    }
    /**
     * 保存并发送消息
     * @param user
     */
    private void saveAndSendMsg(User user) {
        String msgId = RandomUtil.UUID32();

        LoginLog loginLog = new LoginLog();
        loginLog.setUserId(user.getId());
        loginLog.setType(Constant.LogType.LOGIN);
        Date date = new Date();
        loginLog.setDescription(user.getUsername() + "在" + JodaTimeUtil.dateToStr(date) + "登录系统");
        loginLog.setCreateTime(date);
        loginLog.setUpdateTime(date);
        loginLog.setMsgId(msgId);

        CorrelationData correlationData = new CorrelationData(msgId);
        //转换消息且发送               交换机                                 路由                                     消息转换                           唯一id和消息message绑定、将msgId和message关系保存后, 在confirmListener或者returnListener中,就可以根据msgId获取message进行对于操作.
        rabbitTemplate.convertAndSend(RabbitConfig.LOGIN_LOG_EXCHANGE_NAME, RabbitConfig.LOGIN_LOG_ROUTING_KEY_NAME, MessageHelper.objToMsg(loginLog), correlationData);

        MsgLog msgLog = new MsgLog(msgId, loginLog, RabbitConfig.LOGIN_LOG_EXCHANGE_NAME, RabbitConfig.LOGIN_LOG_ROUTING_KEY_NAME);
        msgLogMapper.insert(msgLog);
    }

    @Override
    public void batchInsert(List<User> list) {
        new MapperProxy<User>(userMapper).batchInsert(list);
    }

    @Override
    public void batchUpdate(List<User> list) {
        new MapperProxy<User>(userMapper).batchUpdate(list);
    }

}

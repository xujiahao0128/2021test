package com.wangzaiplus.test.interceptor;

import com.wangzaiplus.test.annotation.AccessLimit;
import com.wangzaiplus.test.common.Constant;
import com.wangzaiplus.test.common.ResponseCode;
import com.wangzaiplus.test.exception.ServiceException;
import com.wangzaiplus.test.util.IpUtil;
import com.wangzaiplus.test.util.JedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

/**
 * 接口防刷限流拦截器
 */
public class AccessLimitInterceptor implements HandlerInterceptor {

    @Autowired
    private JedisUtil jedisUtil;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        if (!(handler instanceof HandlerMethod)) {
            return true;
        }
        HandlerMethod handlerMethod = (HandlerMethod) handler;
        Method method = handlerMethod.getMethod();
        AccessLimit annotation = method.getAnnotation(AccessLimit.class);
        if (annotation != null && notFilter(annotation,method)) {
            check(annotation, request);
        }else {
            annotation = method.getDeclaringClass().getAnnotation(AccessLimit.class);
            if (annotation != null && notFilter(annotation,method)){
                check(annotation, request);
            }
        }
        return true;
    }

    /** 跳过不需要限流的方法 */
    private boolean notFilter(AccessLimit annotation,Method method){
        List<String> ignoreMethod = Arrays.asList(annotation.ignoreMethod());
        if (ignoreMethod.contains(method.getName())) {
            return false;
        }
        return true;
    }

    private void check(AccessLimit annotation, HttpServletRequest request) {
        // 最大访问次数
        int maxCount = annotation.maxCount();
        // 固定时间, 单位: s
        int seconds = annotation.seconds();

        StringBuilder sb = new StringBuilder();
        //根据IP进行限流控制访问
        sb.append(Constant.Redis.ACCESS_LIMIT_PREFIX).append(IpUtil.getIpAddress(request)).append(request.getRequestURI());
        String key = sb.toString();

        Boolean exists = jedisUtil.exists(key);
        if (!exists) {
            jedisUtil.set(key, String.valueOf(1), seconds);
        } else {
            int count = Integer.valueOf(jedisUtil.get(key));
            if (count < maxCount) {
                Long ttl = jedisUtil.ttl(key);
                if (ttl <= 0) {
                    jedisUtil.set(key, String.valueOf(1), seconds);
                } else {
                    jedisUtil.set(key, String.valueOf(++count), ttl.intValue());
                }
            } else {
                throw new ServiceException(ResponseCode.ACCESS_LIMIT.getMsg());
            }
        }
    }

    @Override
    public void postHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, ModelAndView modelAndView) throws Exception {
    }

    @Override
    public void afterCompletion(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, Exception e) throws Exception {
    }
}

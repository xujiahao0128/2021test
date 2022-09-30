package com.wangzaiplus.test.elasticsearch.service;

import com.wangzaiplus.test.pojo.ElasticUser;
import com.wangzaiplus.test.pojo.EsUser;

import java.util.List;
import java.util.concurrent.ExecutorService;

/**
 * @author Mr.Xu
 * @Description TODO
 * @date 2021年09月18日 11:35
 */
public interface TestElasticSearchRepositoryService {

    /** 根据id获取user */
    EsUser findById(Long id);

    void save(EsUser esUser);

}

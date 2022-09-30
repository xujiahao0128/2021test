package com.wangzaiplus.test.elasticsearch.service.impl;

import com.wangzaiplus.test.elasticsearch.dao.TestElasticSearchRepositoryDao;
import com.wangzaiplus.test.elasticsearch.service.TestElasticSearchRepositoryService;
import com.wangzaiplus.test.pojo.ElasticUser;
import com.wangzaiplus.test.pojo.EsUser;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * @author Mr.Xu
 * @Description TODO
 * @date 2021年09月18日 11:35
 */
@Slf4j
@Service
public class TestElasticSearchRepositoryServiceImpl implements TestElasticSearchRepositoryService {

    @Autowired
    private TestElasticSearchRepositoryDao esResipotoryDao;

    @Override
    public EsUser findById(Long id) {
        EsUser esUser = esResipotoryDao.findById(id);
        return esUser;
    }

    @Override
    public void save(EsUser esUser) {
        Object save = esResipotoryDao.save(esUser);
        log.info("====>:{}",((EsUser)save).toString());
    }
}

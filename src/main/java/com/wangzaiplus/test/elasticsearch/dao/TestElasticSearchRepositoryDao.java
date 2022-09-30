package com.wangzaiplus.test.elasticsearch.dao;

import com.wangzaiplus.test.pojo.EsUser;
import org.springframework.data.elasticsearch.annotations.Highlight;
import org.springframework.data.elasticsearch.annotations.HighlightField;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import java.util.Optional;

/**
 * @author Mr.Xu
 * @Description TODO
 * @date 2021年09月18日 11:31
 */
public interface TestElasticSearchRepositoryDao extends ElasticsearchRepository<EsUser,String> {

    /** 根据id获取user */
    EsUser findById(Long id);

}

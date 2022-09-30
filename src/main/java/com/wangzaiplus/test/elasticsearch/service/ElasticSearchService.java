package com.wangzaiplus.test.elasticsearch.service;

import com.wangzaiplus.test.dto.QueryContentDTO;
import com.wangzaiplus.test.pojo.Content;
import org.springframework.lang.Nullable;

import java.io.IOException;
import java.util.List;

/**
 * @author Mr.Xu
 * @Description TODO
 * @date 2021年09月16日 16:12
 */
public interface ElasticSearchService {

    boolean copyEsData(String index) throws IOException;

    boolean saveFor(String index);

    /** 批量插入数据到es */
    boolean saveBatch(List<Content> contents,String index);

    /** 单条插入数据到es */
    boolean save(Content content,String index);

    /** 条件查询 */
    List<Content> getContents(QueryContentDTO queryContentDTO);

    /** 删除索引下所有es数据 */
    boolean deleteByIndex(String index);

}

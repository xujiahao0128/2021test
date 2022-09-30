package com.wangzaiplus.test.elasticsearch.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ArrayUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.wangzaiplus.test.common.ServerResponse;
import com.wangzaiplus.test.dto.QueryContentDTO;
import com.wangzaiplus.test.elasticsearch.service.ElasticSearchService;
import com.wangzaiplus.test.pojo.Content;
import com.wangzaiplus.test.pojo.ElasticUser;
import com.wangzaiplus.test.util.DocumentParseUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.support.master.AcknowledgedResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.text.Text;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.MatchQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.rest.RestStatus;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author Mr.Xu
 * @Description TODO
 * @date 2021年09月16日 16:12
 */
@Slf4j
@Service
public class ElasticSearchServiceImpl implements ElasticSearchService {

    @Autowired
    private RestHighLevelClient client;

    @Autowired
    private Executor myTaskExecutor;

    @Override
    public boolean copyEsData(String index) throws IOException {
        //构造查询条件，全量查询
        SearchRequest searchRequest = new SearchRequest(index);
        QueryBuilder queryBuilder = QueryBuilders.matchAllQuery();
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.size(100000).from(0).timeout(new TimeValue(100, TimeUnit.SECONDS));
        searchSourceBuilder.query(queryBuilder);
        searchRequest.source(searchSourceBuilder);

        //批量新增
        BulkRequest bulkRequest = new BulkRequest(index);

        AtomicInteger count = new AtomicInteger(0);
        //查询es数据
        SearchResponse searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);
        SearchHit[] hits = searchResponse.getHits().getHits();
        if (hits != null && hits.length != 0){
            while (count.get() < 1000000){
                for (SearchHit hit : hits) {
                    count.incrementAndGet();
                    IndexRequest indexRequest = new IndexRequest();
                    indexRequest.source(hit.getSourceAsMap(),XContentType.JSON);
                    bulkRequest.add(indexRequest);
                }
                BulkResponse bulk = client.bulk(bulkRequest, RequestOptions.DEFAULT);
                log.info("批量新增是否成功：{},插入条数：{}",!bulk.hasFailures(),hits.length);
            }
        }
        return false;
    }

    @Override
    public boolean saveFor(String index) {
        myTaskExecutor.execute(() ->{
            List<Content> contents = DocumentParseUtil.params(index);
            this.saveBatch(contents,index);
        });
        return true;
    }

    @Override
    public boolean saveBatch(List<Content> contents,String index) {
        if (CollectionUtils.isEmpty(contents)){
            return false;
        }
        BulkResponse bulkItemResponses = null;
        List<IndexRequest> indexRequests = new ArrayList<>();
        try {
        contents.forEach(content -> {
            IndexRequest indexRequest = new IndexRequest(index);
            indexRequest.source(JSON.toJSONString(content), XContentType.JSON);
            indexRequests.add(indexRequest);
        });
        BulkRequest bulkRequest = new BulkRequest();
        bulkRequest.add(indexRequests.toArray(new IndexRequest[indexRequests.size()]));
        bulkItemResponses = client.bulk(bulkRequest, RequestOptions.DEFAULT);
        } catch (IOException e) {
            log.error("添加数据到es失败！"+e.getMessage());
            e.printStackTrace();
        }
        return !bulkItemResponses.hasFailures();
    }

    @Override
    public boolean save(Content content,String index) {
        return this.saveBatch(Collections.singletonList(content),index);
    }

    @Override
    public List<Content> getContents(QueryContentDTO queryContentDTO) {
        List<Content> contents = new ArrayList<>();
        int from = 0;
        if ((queryContentDTO.getPageNo()-1) > 0){
            from = (queryContentDTO.getPageNo()-1);
        }
        try {
        SearchRequest getRequest = new SearchRequest(queryContentDTO.getIndex());
        //高亮
        HighlightBuilder highlightBuilder = new HighlightBuilder();
        highlightBuilder.requireFieldMatch(false);//多个高亮显示
        highlightBuilder.field("title");
        highlightBuilder.preTags("<span style='color:red'>");
        highlightBuilder.postTags("</span>");
        //条件构造
        MatchQueryBuilder matchQueryBuilder = QueryBuilders.matchQuery("title", queryContentDTO.getTitle());
        SearchSourceBuilder searchRequestBuilder = new SearchSourceBuilder();
        searchRequestBuilder.query(matchQueryBuilder)
                .highlighter(highlightBuilder)
                .size(queryContentDTO.getPageSize())
                .from(queryContentDTO.getPageSize()*from);
        //请求添加搜索条件
        getRequest.source(searchRequestBuilder);
        SearchResponse searchResponse = client.search(getRequest, RequestOptions.DEFAULT);
        SearchHit[] hits = searchResponse.getHits().getHits();
        for (SearchHit hit : hits) {
            Content content = JSON.parseObject(JSON.toJSONString(hit.getSourceAsMap()), Content.class);
            Text[] titles = hit.getHighlightFields().get("title").getFragments();
            if (titles != null){
                StringBuilder title_new = new StringBuilder("");
                for (Text title : titles) {
                    title_new.append(title);
                }
                content.setTitle(title_new.toString());
            }
            contents.add(content);
        }
        } catch (IOException e) {
            log.error("获取es数据失败~");
            e.printStackTrace();
        }
        return contents;
    }

    @Override
    public boolean deleteByIndex(String index) {
        DeleteIndexRequest deleteRequest = new DeleteIndexRequest(index);
        AcknowledgedResponse acknowledgedResponse = null;
        try {
            acknowledgedResponse = client.indices().delete(deleteRequest, RequestOptions.DEFAULT);
        } catch (IOException e) {
            log.error("删除索引：{}下数据失败，原因：{}",index,e.getMessage());
            e.printStackTrace();
        }
        return acknowledgedResponse.isAcknowledged();
    }
}

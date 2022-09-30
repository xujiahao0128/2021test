package com.wangzaiplus.test;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.wangzaiplus.test.pojo.ElasticUser;
import lombok.SneakyThrows;
import org.elasticsearch.action.DocWriteRequest;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.bulk.BulkItemResponse;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.support.master.AcknowledgedResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.Cancellable;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.*;
import org.elasticsearch.cluster.metadata.MappingMetaData;
import org.elasticsearch.common.document.DocumentField;
import org.elasticsearch.common.transport.TransportAddress;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.MatchQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.TermsQueryBuilder;
import org.elasticsearch.rest.RestStatus;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.FetchSourceContext;
import org.elasticsearch.search.profile.ProfileShardResult;
import org.elasticsearch.search.sort.SortOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;

@RunWith(SpringRunner.class)
@SpringBootTest
public class TestApplicationTests {

	private static final String INDEX="spring_boot_test";

	@Value("${elasticsearch.port}")
	private String port;

	@Value("${elasticsearch.ip}")
	private String ip;

	@Autowired
	private RestHighLevelClient client;

	@SneakyThrows
	@Test
	public void test1() {
		GetIndexRequest request = new GetIndexRequest(INDEX);
		/** 判断索引是否存在 */
		boolean exists = client.indices().exists(request, RequestOptions.DEFAULT);
		if (!exists){
			/** 创建索引 */
			CreateIndexRequest createIndexRequest = new CreateIndexRequest(INDEX);
			CreateIndexResponse createIndexResponse = client.indices().create(createIndexRequest, RequestOptions.DEFAULT);
			System.out.println("创建索引成功~\n" +
							String.format("index:%s,ip:%s,port:%s",INDEX,ip,port)
					);
		}else {
			/** 获取索引 */
			GetIndexResponse getIndexResponse = client.indices().get(request, RequestOptions.DEFAULT);
			Map<String, MappingMetaData> mappings = getIndexResponse.getMappings();
			System.out.println("查询索引成功~~" +mappings.toString());
		}

		/** 删除索引 */
/*		DeleteIndexRequest deleteRequest = new DeleteIndexRequest(INDEX);
		AcknowledgedResponse acknowledgedResponse = client.indices().delete(deleteRequest, RequestOptions.DEFAULT);
		System.out.println("删除索引成功~\n" +
						String.format("ip:%s,port:%s",ip,port));*/
	}

	@SneakyThrows
	@Test
	public void test2(){
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("YYYY-MM-dd");
		ElasticUser elasticUser = new ElasticUser(4,"张三",20,simpleDateFormat.parse("2000-02-28"));
		IndexRequest indexRequest = new IndexRequest(INDEX);
		indexRequest
				.id("2")
				.timeout(TimeValue.timeValueSeconds(1))
		.source(JSON.toJSONString(elasticUser), XContentType.JSON);

		IndexResponse indexResponse = client.index(indexRequest, RequestOptions.DEFAULT);
		System.out.println(indexResponse.toString());
		System.out.println(indexResponse.status());
	}

	@SneakyThrows
	@Test
	public void test3(){
		GetRequest getRequest = new GetRequest(INDEX, "1");
		//不获取返回的 _source的上下文
/*		getRequest.fetchSourceContext(new FetchSourceContext(false));
		getRequest.storedFields("_none_");*/
		boolean exists = client.exists(getRequest, RequestOptions.DEFAULT);
		if (exists){
			GetResponse getResponse = client.get(getRequest, RequestOptions.DEFAULT);
			ElasticUser elasticUser = JSON.parseObject(JSON.toJSONString(getResponse.getSource()),ElasticUser.class);
			System.out.println("elasticUser = " + elasticUser.toString());
		}
	}

	@SneakyThrows
	@Test
	public void test4(){
		DeleteRequest deleteRequest = new DeleteRequest(INDEX,"2");
		DeleteResponse deleteResponse = client.delete(deleteRequest, RequestOptions.DEFAULT);
        System.out.println("deleteResponse.status() = " + deleteResponse.status());
	}

	@SneakyThrows
	@Test
	public void test5(){
		UpdateRequest updateRequest = new UpdateRequest(INDEX,"1");
		ElasticUser elasticUser = new ElasticUser(1, "徐家豪1");
		updateRequest.doc(JSON.toJSONString(elasticUser),XContentType.JSON);
		UpdateResponse updateResponse = client.update(updateRequest, RequestOptions.DEFAULT);
    	System.out.println("updateResponse.status() = " + updateResponse.status());
	}

	@SneakyThrows
	@Test
	public void test6(){
		IndexRequest [] users = new IndexRequest[10];
		for (int i = 0; i< 10; i++) {
			ElasticUser elasticUser = new ElasticUser(i+10, "徐家豪"+i,i,new Date());
			IndexRequest indexRequest = new IndexRequest(INDEX);
			indexRequest.id(10+i+"")
					.source(JSON.toJSONString(elasticUser),XContentType.JSON);
			users[i]=indexRequest;
		}
		BulkRequest bulkRequest = new BulkRequest();
		bulkRequest.add(users);
		BulkResponse bulkItemResponses = client.bulk(bulkRequest, RequestOptions.DEFAULT);
    	System.out.println("bulkItemResponses.hasFailures() = " + bulkItemResponses.hasFailures());
		Iterator<BulkItemResponse> iterator = bulkItemResponses.iterator();
		while (iterator.hasNext()) {
     		 System.out.println("iterator.next() = " + iterator.next());
		}
	}

	@SneakyThrows
	@Test
	public void test7(){
		SearchRequest searchRequest = new SearchRequest(INDEX);
		SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
		/** 精确匹配 */
		TermsQueryBuilder termsQueryBuilder = QueryBuilders.termsQuery("name", "徐");
		/** 模糊匹配 */
		MatchQueryBuilder matchQueryBuilder = QueryBuilders.matchQuery("name", "徐");
		//设置条件
		searchSourceBuilder
				.query(matchQueryBuilder)
				.highlighter(SearchSourceBuilder.highlight())
				.sort("age", SortOrder.DESC)
				.from(2)
				.size(5);
		Set set = new HashSet();
		//设置条件构造器
		searchRequest.source(searchSourceBuilder);
		SearchResponse searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);
		RestStatus status = searchResponse.status();
		if (RestStatus.OK.getStatus()==status.getStatus()){
			SearchHit[] hits = searchResponse.getHits().getHits();
			for (SearchHit hit : hits) {
				System.out.println(hit.toString());
			}
		}
	}

  /**
   * 全局设置解除索引最大查询数的限制 put _all/_settings { "index.max_result_window":200000 }
   * 单个索引设置，“settings”:{"index":{ "max_result_window": 500000 } }
   */
  @Test
  public void test8() throws IOException {
		SearchRequest searchRequest = new SearchRequest("study_java");
		SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
		searchSourceBuilder.query(QueryBuilders.matchAllQuery());
		searchSourceBuilder.size(1000000).from(0).timeout(new TimeValue(60, TimeUnit.SECONDS));
		//返回真实条数，不设置超过10000也只显示10000
		searchSourceBuilder.trackTotalHits(true);
		searchRequest.source(searchSourceBuilder);
		SearchResponse search = client.search(searchRequest, RequestOptions.DEFAULT);
		SearchHit[] hits = search.getHits().getHits();
        System.out.println("hits = " + hits);



	}

}

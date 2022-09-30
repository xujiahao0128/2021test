package com.wangzaiplus.test.controller;

import cn.hutool.core.util.RandomUtil;
import com.sun.org.apache.bcel.internal.generic.NEW;
import com.wangzaiplus.test.common.ServerResponse;
import com.wangzaiplus.test.dto.QueryContentDTO;
import com.wangzaiplus.test.elasticsearch.dao.TestElasticSearchRepositoryDao;
import com.wangzaiplus.test.elasticsearch.service.ElasticSearchService;
import com.wangzaiplus.test.elasticsearch.service.TestElasticSearchRepositoryService;
import com.wangzaiplus.test.pojo.Content;
import com.wangzaiplus.test.pojo.EsItem;
import com.wangzaiplus.test.pojo.EsUser;
import com.wangzaiplus.test.util.DocumentParseUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author Mr.Xu
 * @Description TODO
 * @date 2021年09月16日 16:58
 */
@RestController
@RequestMapping(value = "/es")
public class ElasticSearchController {

    private static final String INDEX="study_java";

    @Autowired
    private ElasticSearchService elasticSearchService;

    @Autowired
    private TestElasticSearchRepositoryService esTestService;

    @Autowired
    private TestElasticSearchRepositoryDao testElasticSearchRepositoryDao;

    @GetMapping("/copyEsData/{index}")
    public ServerResponse copyEsData(@PathVariable(value = "index")String index) throws Exception {
        return ServerResponse.success(elasticSearchService.copyEsData(index));
    }

    @GetMapping("/search/{keyword}")
    public ServerResponse findCommentById(@PathVariable(value = "keyword")String keyword) throws Exception {
        //List<Content> contents = DocumentParseUtil.params(keyword);
        return ServerResponse.success(elasticSearchService.saveFor(INDEX));
    }

    @GetMapping("/deleteByIndex/{index}")
    public ServerResponse deleteByIndex(@PathVariable(value = "index")String index) throws Exception {
        return ServerResponse.success(elasticSearchService.deleteByIndex(index));
    }

    @GetMapping("/search/{title}/{pageSize}/{pageNo}")
    public List<Content> findCommentById(@PathVariable(value = "title")String title
                                         ,@PathVariable(value = "pageSize")Integer pageSize
                                         ,@PathVariable(value = "pageNo")Integer pageNo) throws Exception {
        QueryContentDTO queryContentDTO = new QueryContentDTO();
        queryContentDTO.setPageSize(pageSize);
        queryContentDTO.setPageNo(pageNo);
        queryContentDTO.setTitle(title);
        return elasticSearchService.getContents(queryContentDTO);
    }

    @GetMapping("/getElasticUserById/{id}")
    public ServerResponse getElasticUserById(@PathVariable(value = "id")Long id) throws Exception {
        return ServerResponse.success(esTestService.findById(id));
    }

    @GetMapping("/save")
    public ServerResponse save() throws Exception {
        Long id = Long.valueOf(RandomUtil.randomInt(1, 100000));
        List<EsItem> esItemList = new ArrayList<>();
        for (int i = 0; i < 2; i++) {
            EsItem esItem = new EsItem();
            esItem.setCreatedDate(LocalDate.of(2021,9,18));
            esItem.setId(id+i);
            esItem.setDesc("desc="+id+i);
            esItemList.add(esItem);
        }
        EsUser esUser = new EsUser();
        esUser.setId(id);
        esUser.setCreatedDate(LocalDate.of(2021,9,18));
        esUser.setName(id+"name");
        esUser.setEsItemList(esItemList);
        esTestService.save(esUser);
        return ServerResponse.success();
    }


}

package app.web;

import app.pojo.EsPage;
import app.repositories.EsPageRepository;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.SearchQuery;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;

/**
 * @author faith.huan 2019-08-03 15:02
 */
@RestController
@RequestMapping("/test")
@Slf4j
public class TestController {

    @Autowired
    private RestHighLevelClient restHighLevelClient;

    @Autowired
    private EsPageRepository esPageRepository;

    @RequestMapping("/search")
    public void search() throws IOException {
        SearchQuery query = new NativeSearchQueryBuilder().
                withQuery(QueryBuilders.matchAllQuery()).build();
        //List<EsPage> esPages = elasticsearchTemplate.queryForList(query, EsPage.class);
        SearchRequest searchRequest = new SearchRequest();
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        sourceBuilder.query(QueryBuilders.termQuery("user", "kimchy"));
        sourceBuilder.from(0);
        sourceBuilder.size(5);
        sourceBuilder.timeout(new TimeValue(60, TimeUnit.SECONDS));
        SearchResponse searchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);

        log.info("searchResponse:{}",searchResponse);

    }

    @RequestMapping("/save")
    public Object save(){
        EsPage esPage = new EsPage();
        esPage.setTitle("标题");
        esPage.setDate(LocalDateTime.now().toString());
        esPage.setUrl("http://www.baidu.com");
        esPage.setContent("这是内容");

        EsPage save = esPageRepository.save(esPage);

        return save;
    }

}

package app.repositories;

import org.apache.lucene.util.automaton.RegExp;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.terms.IncludeExclude;
import org.elasticsearch.search.aggregations.bucket.terms.ParsedStringTerms;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author faith.huan 2019-08-05 14:37
 */
@Component
public class CustomizedPdmDocRepositoryImpl implements CustomizedPdmDocRepository {

    private final RestHighLevelClient restHighLevelClient;

    public CustomizedPdmDocRepositoryImpl(RestHighLevelClient restHighLevelClient) {
        this.restHighLevelClient = restHighLevelClient;
    }


    @Override
    public Map<String, Long> countTerms(String termPattern) {
        Map<String, Long> result = new HashMap<>(200);
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        TermsAggregationBuilder aggregation = AggregationBuilders.terms("topTerms")
                .field("content").includeExclude(new IncludeExclude(new RegExp(termPattern), null))
                .size(5000).minDocCount(1000);
        sourceBuilder.aggregation(aggregation);
        sourceBuilder.size(0);
        sourceBuilder.timeout(TimeValue.timeValueSeconds(60));
        SearchRequest searchRequest = new SearchRequest(new String[]{"pdm_doc"}, sourceBuilder);

        try {
            SearchResponse searchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
            ParsedStringTerms topTerms = searchResponse.getAggregations().get("topTerms");
            List<ParsedStringTerms.ParsedBucket> buckets = (List<ParsedStringTerms.ParsedBucket>) topTerms.getBuckets();
            for (int i = 0; i < buckets.size(); i++) {
                ParsedStringTerms.ParsedBucket bucket = buckets.get(i);
                String key = bucket.getKeyAsString();
                Long count = bucket.getDocCount();
                result.put(key, count);
            }

            System.out.println(searchResponse);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return result;
    }
}

package app.service;

import app.vo.EsPageVO;
import app.vo.PdmAggregatedPage;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.common.text.Text;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.Operator;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.SearchResultMapper;
import org.springframework.data.elasticsearch.core.aggregation.AggregatedPage;
import org.springframework.data.elasticsearch.core.query.FetchSourceFilterBuilder;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.SearchQuery;
import org.springframework.data.elasticsearch.core.query.SourceFilter;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * @author faith.huan 2018-12-24 8:34
 */
@Service
@Slf4j
public class SearchService {

    private final ElasticsearchRestTemplate elasticsearchRestTemplate;

    /**
     * 高亮字段
     */
    private final HighlightBuilder.Field[] highlightFields = new HighlightBuilder.Field[]{
            new HighlightBuilder.Field("content").fragmentSize(500).numOfFragments(1).noMatchSize(500).preTags("<font color='red'>").postTags("</font>"),
            new HighlightBuilder.Field("title").fragmentSize(150).numOfFragments(1).noMatchSize(150).preTags("<font color='red'>").postTags("</font>")
    };
    /**
     * 显示字段筛选
     */
    private final SourceFilter sourceFilter = new FetchSourceFilterBuilder().withExcludes("content").build();

    private List<String> outerConditionsFields = Arrays.asList("name", "number", "createStamp", "modifyStamp", "versionId");

    @Autowired
    public SearchService(ElasticsearchRestTemplate elasticsearchRestTemplate) {
        this.elasticsearchRestTemplate = elasticsearchRestTemplate;

    }

    /**
     * 全文检索
     *
     * @param keyword 关键词
     * @param type    文档类型
     */
    public AggregatedPage<EsPageVO> fullTextSearch(String keyword, String type,
                                                   String sortFields, int page, int pageSize, String userId) {
        try {
            String index = "es_page";

            BoolQueryBuilder boolQueryBuilder = buildKeywordQuery(keyword);

            // 类型筛选
            if (!StringUtils.isBlank(type)) {
                boolQueryBuilder.filter().add(QueryBuilders.termsQuery("fullType", type.split(",")));
            }


            SearchQuery searchQuery = new NativeSearchQueryBuilder().withIndices(index).withTypes("_doc")
                    .withQuery(boolQueryBuilder)
                    // 设置字段筛选
                    .withSourceFilter(sourceFilter)
                    // 设置高亮字段
                    .withHighlightFields(highlightFields)
                    // 聚合
                    //.addAggregation(AggregationBuilders.terms("containerType").field("containerType").showTermDocCountError(true).size(10))
                    //.addAggregation(AggregationBuilders.terms("containerReference").field("containerReference").showTermDocCountError(true).size(10))
                    .addAggregation(AggregationBuilders.terms("fileType").field("fileType").showTermDocCountError(true).size(10))
                    .addAggregation(AggregationBuilders.count("count").field("_id"))
                    // 设置分页
                    .withPageable(PageRequest.of(page, pageSize)).build();


            return processQuery(searchQuery);

        } catch (Exception e) {
            PdmAggregatedPage<EsPageVO> result = new PdmAggregatedPage<EsPageVO>(Collections.emptyList());
            log.error("高级查询发生异常", e);
            return result;
        }
    }

    /**
     * 执行es查询
     *
     * @param searchQuery 查询语句
     * @return 查询结果
     */
    private AggregatedPage<EsPageVO> processQuery(SearchQuery searchQuery) {

        AggregatedPage<EsPageVO> page = elasticsearchRestTemplate.queryForPage(searchQuery, EsPageVO.class, new SearchResultMapper() {
            @Override
            public <T> AggregatedPage<T> mapResults(SearchResponse response, Class<T> clazz, Pageable pageable) {
                List<EsPageVO> list = new ArrayList<>();
                SearchHits hits = response.getHits();
                for (SearchHit searchHit : hits) {
                    EsPageVO doc = new EsPageVO();
                    Map<String, Object> sourceMap = searchHit.getSourceAsMap();
                    doc.setScore(searchHit.getScore());
                    doc.setUrl(MapUtils.getString(sourceMap, "url"));
                    doc.setDate(MapUtils.getString(sourceMap, "date"));
                    /*
                    doc.setMsgId(MapUtils.getString(sourceMap, "msgId"));
                    doc.setTitle(MapUtils.getString(sourceMap, "title"));
                    doc.setNumber(MapUtils.getString(sourceMap, "number"));
                    doc.setName(MapUtils.getString(sourceMap, "name"));
                    doc.setType(MapUtils.getString(sourceMap, "type"));
                    doc.setOid(MapUtils.getString(sourceMap, "oid"));
                    doc.setVersionId(MapUtils.getString(sourceMap, "versionId"));
                    doc.setRevision(MapUtils.getString(sourceMap, "revision"));
                    doc.setIteration(MapUtils.getString(sourceMap, "iteration"));
                    doc.setCreateStamp(MapUtils.getString(sourceMap, "createStamp"));
                    doc.setModifyStamp(MapUtils.getString(sourceMap, "modifyStamp"));
                    doc.setCreator(MapUtils.getString(sourceMap, "creator"));
                    doc.setContainerReference(MapUtils.getString(sourceMap, "containerReference"));
                    doc.setContainerType(MapUtils.getString(sourceMap, "containerType"));
                    doc.setDisplayState(MapUtils.getString(sourceMap, "displayState"));
                    doc.setRowCount(MapUtils.getInteger(sourceMap, "rowCount"));
                    doc.setWordCount(MapUtils.getInteger(sourceMap, "wordCount"));
                    doc.setToEsTime(MapUtils.getString(sourceMap, "toEsTime"));
                    doc.setViewCount(MapUtils.getInteger(sourceMap, "viewCount"));
                    doc.setViewCountToEsTime(MapUtils.getString(sourceMap, "viewCountToEsTime"));
                    doc.setLastViewTime(MapUtils.getString(sourceMap, "lastViewTime"));
                    doc.setFileType(MapUtils.getString(sourceMap, "fileType"));
                    doc.setDisplayType(MapUtils.getString(sourceMap, "displayType"));
                    doc.setFullType(MapUtils.getString(sourceMap, "fullType"));
                    doc.setModifierFullName(MapUtils.getString(sourceMap, "modifierFullName"));
                    doc.setCreatorFullName(MapUtils.getString(sourceMap, "creatorFullName"));
                    doc.setLockerFullName(MapUtils.getString(sourceMap, "lockerFullName"));*/

                    // 高亮字段处理
                    Map<String, HighlightField> highlightFields = searchHit.getHighlightFields();
                    // 标题高亮
                    if (highlightFields.containsKey("title")) {
                        Text[] titles = highlightFields.get("title").getFragments();
                        doc.setTitle(titles[0].string());
                    } else {
                        log.warn("未找到标题高亮内容");
                        doc.setTitle(MapUtils.getString(sourceMap, "title"));
                    }
                    // 正文高亮
                    if (highlightFields.containsKey("content")) {
                        Text[] contents = highlightFields.get("content").getFragments();
                        doc.setContent(contents[0].string());
                    } else {
                        log.warn("未找到正文高亮内容");
                        doc.setContent("无正文内容");
                    }
                    list.add(doc);
                }

                processScore(list);
                // (List<T> content, Pageable pageable, long total, Aggregations aggregations, float maxScore  //
                PdmAggregatedPage pdmAggregatedPage = new PdmAggregatedPage<T>((List<T>) list, searchQuery.getPageable(), hits.getTotalHits(), response.getAggregations(), hits.getMaxScore());
                pdmAggregatedPage.setTook(response.getTook().toString());
                return pdmAggregatedPage;

            }

            @Override
            public <T> T mapSearchHit(SearchHit searchHit, Class<T> type) {
                return null;
            }
        });

        return page;
    }

    private void processScore(List<EsPageVO> list) {
        if (!list.isEmpty()) {
            Float max = list.stream().map(EsPageVO::getScore).max(Float::compareTo).get() / 5;
            list.forEach(page -> page.setScore(page.getScore() / max));
        }
    }

    /**
     * 构建关键词QueryBuilder
     * 只匹配正文
     * boolQueryBuilder.must().add(QueryBuilders.matchQuery("content", keyword).operator(operator));
     *
     * @param keyword 关键词
     * @return QueryBuilder
     */
    private BoolQueryBuilder buildKeywordQuery(String keyword) {
        String doubleQuote = "\"";
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        // + and ,其他or
        Operator operator = keyword.contains("+") ? Operator.AND : Operator.OR;
        keyword = processKeyword(keyword);

        // 匹配正文和标题,如果存在双引号,则使用phase匹配
        if (keyword.contains(doubleQuote)) {
            log.debug("关键字:[{}]中含有双引号,走matchPhraseQuery逻辑.", keyword);
            String[] keys = keyword.split(doubleQuote);
            StringUtils.split(doubleQuote);
            for (String key : keys) {
                if (StringUtils.isNotBlank(key)) {
                    BoolQueryBuilder builder = QueryBuilders.boolQuery();
                    builder.should().add(QueryBuilders.matchPhraseQuery("title", key));
                    builder.should().add(QueryBuilders.matchPhraseQuery("content", key));
                    if (operator == Operator.AND) {
                        boolQueryBuilder.must().add(builder);
                    } else {
                        boolQueryBuilder.should().add(builder);
                    }
                }
            }
        } else {
            log.debug("关键字:[{}]中不含双引号,走multiMatchQuery逻辑.", keyword);
            boolQueryBuilder.must().add(QueryBuilders.multiMatchQuery(keyword, "content", "title").operator(operator));
        }
        return boolQueryBuilder;
    }

    /**
     * 处理keyword中的特殊字符
     * 1. + * 替换成空白
     * 2. &quot; 替换成 "
     */
    private String processKeyword(String keyword) {

        keyword = keyword.replace("+", "");
        keyword = keyword.replace("*", "");
        keyword = keyword.replace("&quot;", "\"");
        return keyword;
    }
}

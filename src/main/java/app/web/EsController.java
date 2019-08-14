package app.web;

import app.pojo.EsPage;
import app.repositories.EsPageRepository;
import app.service.SearchService;
import app.service.StopWordService;
import app.vo.EsPageVO;
import com.alibaba.fastjson.JSON;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.filefilter.TrueFileFilter;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.elasticsearch.core.aggregation.AggregatedPage;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author faith.huan 2019-08-05 8:47
 */
@RestController
@RequestMapping("/es")
@Slf4j
public class EsController {

    private final EsPageRepository esPageRepository;
    private final StopWordService stopWordService;
    private final SearchService searchService;


    public EsController(EsPageRepository esPageRepository, StopWordService stopWordService,
                        SearchService searchService) {
        this.esPageRepository = esPageRepository;
        this.stopWordService = stopWordService;
        this.searchService = searchService;
    }

    @RequestMapping("/saveToEs")
    public Map<String, Object> saveToEs() {

        Map<String, Object> result = new HashMap<>(4);

        @SuppressWarnings("unchecked")
        Collection<File> collection = FileUtils.listFiles(new File("D:\\es-doc"), TrueFileFilter.TRUE, TrueFileFilter.TRUE);
        AtomicInteger count = new AtomicInteger();
        collection.forEach(file -> {
            try {
                String content = IOUtils.toString(new FileInputStream(file));
                EsPage esPage = JSON.parseObject(content, EsPage.class);
                log.info("file:{},esPage:{}", file.getPath(), esPage.getTitle());
                if (!StringUtils.isAnyBlank(esPage.getContent(), esPage.getTitle())) {
                    esPage.setFileName(file.getName());
                }
                EsPage save = esPageRepository.save(esPage);
                log.info("保存esPage结果:{}", save);
                count.getAndIncrement();
            } catch (IOException e) {
                log.error("保存异常", e);
            }
        });

        result.put("count", count.get());
        return result;
    }

    @RequestMapping("/number")
    public Map<String, Long> number() {
        return stopWordService.getNumberStopWord();
    }

    @RequestMapping("/length1")
    public Map<String, Long> length1() {
        return stopWordService.getLength1StopWord();
    }

    /**
     * 全文检索
     *
     * @param keyword            关键词
     * @param type               文档类型
     */
    @CrossOrigin
    @ApiOperation(value = "全文查询")
    @PostMapping("/fullTextSearch")
    public AggregatedPage<EsPageVO> fullTextSearch(@ApiParam(value = "关键词", example = "中国") @RequestParam(value = "keyword", required = false, defaultValue = "") String keyword,
                                                   @ApiParam(value = "文档类型") @RequestParam(value = "type", required = false, defaultValue = "") String type,
                                                   @ApiParam(value = "排序字段,格式: field1:asc,field2:desc") @RequestParam(value = "sortFields", required = false, defaultValue = "") String sortFields,
                                                   @ApiParam(value = "页码", example = "0") @RequestParam(value = "page", required = false, defaultValue = "0") int page,
                                                   @ApiParam(value = "每页大小", example = "10") @RequestParam(value = "pageSize", required = false, defaultValue = "10") int pageSize,
                                                   @ApiParam(value = "用户Id", example = "zhangsan") @RequestParam(value = "userId", required = false) String userId
    ) {

        log.info("keyword:{}, type:{},sortFields:{},page:{},pageSize:{},userId:{}",
                keyword, type, sortFields, page, pageSize, userId);

        return searchService.fullTextSearch(keyword, type,  sortFields, page, pageSize, userId);
    }


}

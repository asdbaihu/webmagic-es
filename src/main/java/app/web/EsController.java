package app.web;

import app.pojo.EsPage;
import app.repositories.EsPageRepository;
import app.service.StopWordService;
import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.filefilter.TrueFileFilter;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
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


    public EsController(EsPageRepository esPageRepository, StopWordService stopWordService) {
        this.esPageRepository = esPageRepository;
        this.stopWordService = stopWordService;
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
        return  stopWordService.getNumberStopWord();
    }

    @RequestMapping("/length1")
    public Map<String, Long> length1() {
        return  stopWordService.getLength1StopWord();
    }


}

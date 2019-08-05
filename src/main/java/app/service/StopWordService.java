package app.service;

import app.repositories.PdmDocRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Map;
import java.util.TreeMap;

/**
 * @author faith.huan 2019-08-05 16:09
 */
@Service
@Slf4j
public class StopWordService {

    @Autowired
    private PdmDocRepository pdmDocRepository;

    /**
     * 获取数字stop word
     */
    public Map<String, Long> getNumberStopWord() {
        Map<String, Long> treeMap = new TreeMap<>();
        for (int i = 0; i < 10; i++) {
            treeMap.putAll(pdmDocRepository.countTerms(i + ".{0,10}"));
        }
        File file = new File("number.txt");
        writeToFile(file, treeMap);
        return treeMap;
    }


    /**
     * 获取数字stop word
     */
    public Map<String, Long> getLength1StopWord() {
        Map<String, Long> treeMap = new TreeMap<>();

        treeMap.putAll(pdmDocRepository.countTerms(".{1}"));

        File file = new File("length1.txt");
        writeToFile(file, treeMap);
        return treeMap;
    }


    private void writeToFile(File file, Map<String, Long> treeMap) {
        StringBuilder stringBuilder = new StringBuilder();
        treeMap.forEach((key, value) -> {
            stringBuilder.append(key).append("\n");
        });
        try {
            IOUtils.write(stringBuilder.toString().getBytes(), new FileOutputStream(file));
            log.info("写入文件成功");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}

package app.pojo;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;

/**
 * @author faith.huan 2019-07-21 11:01
 */
@Data
@Document(indexName = "es_page",type = "_doc" ,createIndex = false)
public class EsPage {

    @Id
    private String fileName;

    private String title;

    private String content;

    private String  url;

    private String date;

}

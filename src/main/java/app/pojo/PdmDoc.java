package app.pojo;

import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;

/**
 * @author faith.huan 2019-08-05 14:08
 */
@Document(indexName = "pdm_doc",type = "_doc" ,createIndex = false)
public class PdmDoc {

    @Id
    private String oid;

    private String content;

}

package app.repositories;

import app.pojo.PdmDoc;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * @author faith.huan 2019-08-05 14:29
 */
public interface PdmDocRepository extends ElasticsearchRepository<PdmDoc,String>,CustomizedPdmDocRepository  {
}

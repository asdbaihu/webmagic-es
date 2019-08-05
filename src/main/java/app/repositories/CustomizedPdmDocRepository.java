package app.repositories;

import java.util.Map;

/**
 * 自定义接口
 *
 * @author faith.huan 2019-08-05 02:35:31
 */
public interface CustomizedPdmDocRepository {

    /**
     * 计算包含term的文档数
     *
     * @param termPattern 模式
     * @return 数量
     */
    Map<String, Long> countTerms(String termPattern);

}

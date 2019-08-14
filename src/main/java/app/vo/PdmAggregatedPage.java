package app.vo;

import app.util.MdcUtil;
import lombok.Data;
import org.elasticsearch.search.aggregations.Aggregations;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.aggregation.impl.AggregatedPageImpl;

import java.util.List;

/**
 * PdmAggregatedPage继承AggregatedPageImpl,用于扩展自定义属性
 *
 * @author faith.huan 2018-12-28 14:50
 */
@Data
public class PdmAggregatedPage<T> extends AggregatedPageImpl<T> {

    /**
     * 耗时信息
     */
    private String took;

    /**
     * 追踪ID
     */
    private String traceId;

    /**
     * 异常信息
     */
    private String error;

    public PdmAggregatedPage(List content) {
        super(content);
        this.traceId = MdcUtil.getMdc();
    }

    public PdmAggregatedPage(List content, Pageable pageable, long total, Aggregations aggregations, float maxScore) {
        super(content, pageable, total, aggregations, maxScore);
        this.traceId = MdcUtil.getMdc();
    }


}

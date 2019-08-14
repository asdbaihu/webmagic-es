package app.util;

import org.apache.commons.lang3.RandomStringUtils;
import org.slf4j.MDC;

import java.util.UUID;

/**
 * @author faith.huan 2019-02-26 17:48
 */
public class MdcUtil {

    /**
     * 将TID设置为新的UUID
     */
    public static void setMdcUUID() {
        MDC.put("TID", UUID.randomUUID().toString().replace("-", ""));
    }

    /**
     * 将TID设置为10位(0-9)随机数
     */
    public static void setMdc() {
        MDC.put("TID", RandomStringUtils.random(10, "0123456789"));
    }

    /**
     * 将TID设置为指定位数length的(0-9)随机数
     */
    public static void setMdc(int length) {
        MDC.put("TID", RandomStringUtils.random(length, "0123456789"));
    }

    /**
     * 将TID设置为指定值
     */
    public static void setMdc(String tid) {
        MDC.put("TID", tid);
    }

    /**
     * 将TID设置为前缀为prefix, length位(0-9)随机数
     * @param length    位数
     * @param prefix    前缀
     */
    public static void setMdc(int length, String prefix) {
        MDC.put("TID", prefix + RandomStringUtils.random(length, "0123456789"));
    }

    /**
     * 将TID设置为前缀为prefix,后缀为subfix, length位(0-9)随机数
     * @param length    位数
     * @param prefix    前缀
     * @param subfix    后缀
     */
    public static void setMdc(int length, String prefix, String subfix) {
        MDC.put("TID", prefix + RandomStringUtils.random(length, "0123456789") + subfix);
    }

    /**
     * 清空TID
     */
    public static void clearMdc() {
        MDC.put("TID", null);
    }

    /**
     * 获取TID
     */
    public static String getMdc() {
        return MDC.get("TID");
    }

}

package app;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.TrueFileFilter;
import org.junit.Test;

import java.io.File;
import java.util.Collection;

/**
 * @author faith.huan 2019-08-05 8:41
 */
public class FileUtilsTest {

    @Test
    public void listFile() {
        Collection collection = FileUtils.listFiles(new File("D:\\es-doc"), TrueFileFilter.TRUE, TrueFileFilter.TRUE);

        collection.forEach(e -> {
            File file = (File) e;
            System.out.println(file.getPath());
            System.out.println(file.getName());
        });

    }
}

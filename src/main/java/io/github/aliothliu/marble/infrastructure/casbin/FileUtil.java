package io.github.aliothliu.marble.infrastructure.casbin;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

/**
 * @author fangzhengjin
 * @version V1.0
 * @date 2019/9/24 17:06
 **/
@Slf4j
public class FileUtil {

    private static String removePrefix(String str, String prefix) {
        if (str.startsWith(prefix)) {
            return str.substring(prefix.length());
        }
        return str;
    }

    public static File getFile(String filePath) {
        File template = new File(removePrefix(filePath, "classpath:"));
        if (template.exists()) {
            return template;
        } else {
            return null;
        }
    }

    public static InputStream getFileAsInputStream(String filePath) {
        File file = getFile(filePath);
        try {
            if (file != null && file.exists()) {
                return new FileInputStream(file);
            }
            Resource resource = new DefaultResourceLoader().getResource(filePath);
            if (resource.exists()) {
                return resource.getInputStream();
            }
            return null;
        } catch (Exception e) {
            log.error("load file $filePath by inputStream error", e);
            return null;
        }
    }

    public static String getFileAsText(String filePath) {
        try (InputStream inputStream = getFileAsInputStream(filePath);) {
            if (inputStream != null) {
                return IOUtils.toString(inputStream, StandardCharsets.UTF_8);
            }
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
        return null;
    }
}

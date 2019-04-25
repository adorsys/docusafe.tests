package org.adorsys.docusafe.rest.adapter;

import de.adorsys.common.exceptions.BaseExceptionHandler;
import org.apache.commons.io.IOUtils;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by peter on 27.02.19 07:45.
 */
public class JsonConversionTest {
    private final static Logger LOGGER = LoggerFactory.getLogger(JsonConversionTest.class);

    @Test
    public void a() {
        String all = readFile("swaggerinput.txt");
    }
    private String readFile(String fileName) {
        ClassLoader classLoader = getClass().getClassLoader();
        try {
            return IOUtils.toString(classLoader.getResourceAsStream(fileName));
        } catch (Exception e) {
            throw BaseExceptionHandler.handle(e);
        }
    }
}

package de.adorsys.docusafe.rest;

import de.adorsys.common.exceptions.BaseExceptionHandler;
import de.adorsys.common.utils.ShowProperties;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration;
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration;

import java.lang.reflect.Field;
import java.security.Security;
import java.util.Arrays;

/**
 * Created by peter on 10.01.18.
 */

@SpringBootApplication(exclude = {JacksonAutoConfiguration.class, MongoAutoConfiguration.class})
/**
 * Die EnableAutoConfiguration(exclude jackson) Annotion ist sehr wichtig, denn sonst
 * ziehen die TypeAdapter für Json nicht.
 * Dann erscheint
 * "documentKeyID": {value": "123"} statt
 * "documentKeyID": "123"
 */
@EnableAutoConfiguration(exclude = {JacksonAutoConfiguration.class, MongoAutoConfiguration.class})
public class RestApplication {
    private final static Logger LOGGER = LoggerFactory.getLogger(RestApplication.class);

    public static void main(String[] args) {
        Arrays.stream(args).forEach(arg -> {
                    LOGGER.debug("Application runtime argument:" + arg);
                    if (arg.equalsIgnoreCase("-TurnOffEncPolicy") || arg.equalsIgnoreCase("-EncOff")) {
                        try {
                            Field field = Class.forName("javax.crypto.JceSecurity").getDeclaredField("isRestricted");
                            field.setAccessible(true);
                            field.set(null, Boolean.FALSE);
                            LOGGER.warn("************************************************");
                            LOGGER.warn("*                                              *");
                            LOGGER.warn("*  ******************************************  *");
                            LOGGER.warn("*  *                                        *  *");
                            LOGGER.warn("*  *  JAVA ENCRYPTION POLICY SWITCHED OFF   *  *");
                            LOGGER.warn("*  *                                        *  *");
                            LOGGER.warn("*  ******************************************  *");
                            LOGGER.warn("*                                              *");
                            LOGGER.warn("************************************************");
                        } catch (Exception e) {
                            throw BaseExceptionHandler.handle(e);
                        }
                    } else {
                        LOGGER.error("Parameter " + arg + " is unknown and will be ignored.");
                        LOGGER.error("Knwon Parameters are: " +
                                "-TurnOffEncPolicy, " +
                                "-EncOff ");
                    }
                }
        );
        LOGGER.debug("add bouncy castle provider");

        Security.addProvider(new BouncyCastleProvider());
        ShowProperties.log();
        try {
            SpringApplication.run(RestApplication.class, args);
        } catch (Exception e) {
            throw BaseExceptionHandler.handle(e);
        }
    }
}

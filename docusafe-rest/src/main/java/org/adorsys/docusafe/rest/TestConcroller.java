package org.adorsys.docusafe.rest;

import org.adorsys.docusafe.business.types.UserID;
import org.adorsys.docusafe.rest.types.CacheType;
import org.adorsys.docusafe.rest.types.DocusafeLayer;
import org.adorsys.docusafe.rest.types.TestCase;
import org.adorsys.docusafe.rest.types.TestParameter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by peter on 15.08.18 at 15:57.
 */
@RestController
public class TestConcroller {
    private final static Logger LOGGER = LoggerFactory.getLogger(TestConcroller.class);
    private final static String APPLICATION_JSON = "application/json";

    @RequestMapping(
            value = "/test",
            method = {RequestMethod.PUT},
            consumes = {APPLICATION_JSON},
            produces = {APPLICATION_JSON}
    )
    public
    @ResponseBody
    ResponseEntity<String> test(@RequestBody TestParameter testParameter) {
        LOGGER.info("received test reqeuest with " + testParameter);
        String response = "I wish you a happy new year";
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @RequestMapping(
            value = "/affe",
            method = {RequestMethod.GET},
            consumes = {APPLICATION_JSON},
            produces = {APPLICATION_JSON}
    )
    public
    @ResponseBody
    ResponseEntity<TestParameter> affe() {
        LOGGER.debug("affe");
        TestParameter tp = new TestParameter();
        tp.cacheType = CacheType.NO_CACHE;
        tp.documentsPerDirectory = new Integer(3);
        tp.docusafeLayer = DocusafeLayer.DOCUSAFE_BASE;
        tp.numberOfThreads = new Integer(1);
        tp.sizeOfDocument = new Integer(300000);
        tp.testcase = TestCase.CREATE_DOCUMENTS;
        tp.userid = new UserID("peter01");
        return new ResponseEntity<>(tp, HttpStatus.OK);
    }
}

package org.adorsys.docusafe.rest;

import org.adorsys.cryptoutils.exceptions.BaseException;
import org.adorsys.cryptoutils.storeconnectionfactory.ExtendedStoreConnectionFactory;
import org.adorsys.docusafe.business.DocumentSafeService;
import org.adorsys.docusafe.business.impl.DocumentSafeServiceImpl;
import org.adorsys.docusafe.business.impl.WithCache;
import org.adorsys.docusafe.business.types.UserID;
import org.adorsys.docusafe.business.types.complex.DSDocument;
import org.adorsys.docusafe.business.types.complex.DocumentDirectoryFQN;
import org.adorsys.docusafe.business.types.complex.DocumentFQN;
import org.adorsys.docusafe.business.types.complex.UserIDAuth;
import org.adorsys.docusafe.rest.types.CacheType;
import org.adorsys.docusafe.rest.types.DocusafeLayer;
import org.adorsys.docusafe.rest.types.TestCase;
import org.adorsys.docusafe.rest.types.TestParameter;
import org.adorsys.docusafe.service.types.DocumentContent;
import org.adorsys.encobject.domain.ReadKeyPassword;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StopWatch;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Formatter;
import java.util.Locale;

/**
 * Created by peter on 15.08.18 at 15:57.
 */
@RestController
public class TestConcroller {
    private final static Logger LOGGER = LoggerFactory.getLogger(TestConcroller.class);
    private final static String APPLICATION_JSON = "application/json";
    private static int counter = 0;
    private DocumentSafeService[] documentSafeService = null;

    public TestConcroller() {
        counter++;
        if (counter > 1) {
            throw new BaseException("did not expect to get more than one controller");
        }
        documentSafeService = new DocumentSafeService[3];

        documentSafeService[0] = new DocumentSafeServiceImpl(WithCache.FALSE, ExtendedStoreConnectionFactory.get());
        documentSafeService[1] = new DocumentSafeServiceImpl(WithCache.TRUE, ExtendedStoreConnectionFactory.get());
        documentSafeService[2] = new DocumentSafeServiceImpl(WithCache.TRUE_HASH_MAP, ExtendedStoreConnectionFactory.get());
    }

    @RequestMapping(
            value = "/test",
            method = {RequestMethod.PUT},
            consumes = {APPLICATION_JSON},
            produces = {APPLICATION_JSON}
    )
    public
    @ResponseBody
    ResponseEntity<String> test(@RequestBody TestParameter testParameter) {
        int index = 0;
        switch (testParameter.cacheType) {
            case NO_CACHE:
                index = 0;
                break;
            case GUAVA:
                index = 1;
                break;
            case HASH_MAP:
                index = 2;
                break;
            default:
                throw new BaseException("cacheType not known: " + testParameter.cacheType);
        }
        UserIDAuth userIDAuth = new UserIDAuth(testParameter.userid, new ReadKeyPassword("password for " + testParameter.userid.getValue()));
        StopWatch stopWatch = new StopWatch();
        StringBuilder resultString = new StringBuilder();
        resultString.append(testParameter.toString());
        resultString.append("\n");
        switch (testParameter.testcase) {
            case CREATE_DOCUMENTS: {
                switch (testParameter.docusafeLayer) {
                    case DOCUSAFE_BASE: {
                        documentSafeService[index].createUser(userIDAuth);
                        int folderIndex = 1;
                        for (int i = 1; i <= testParameter.numberOfDocuments; i++) {
                            DocumentDirectoryFQN folder = new DocumentDirectoryFQN("folder-" + String.format("%03d", folderIndex));
                            DocumentFQN documentFQN = folder.addName("file-" + String.format("%03d", i));
                            if (i % testParameter.documentsPerDirectory == 0) {
                                folderIndex++;
                            }
                            StringBuilder sb = new StringBuilder();
                            Formatter formatter = new Formatter(sb, Locale.GERMAN);
                            formatter.format("%1$" + testParameter.sizeOfDocument + "s", documentFQN.getValue());
                            DSDocument dsDocument = new DSDocument(documentFQN, new DocumentContent(sb.toString().getBytes()), null);
                            LOGGER.info("create document " + documentFQN.getValue());
                            stopWatch.start();
                            documentSafeService[index].storeDocument(userIDAuth, dsDocument);
                            stopWatch.stop();
                        }
                    }
                }
            }
            resultString.append(stopWatch.prettyPrint());
        }
        LOGGER.info(resultString.toString());
        return new ResponseEntity<>(resultString.toString(), HttpStatus.OK);
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

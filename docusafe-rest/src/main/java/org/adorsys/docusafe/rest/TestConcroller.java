package org.adorsys.docusafe.rest;

import org.adorsys.cryptoutils.exceptions.BaseException;
import org.adorsys.cryptoutils.storeconnectionfactory.ExtendedStoreConnectionFactory;
import org.adorsys.docusafe.business.DocumentSafeService;
import org.adorsys.docusafe.business.impl.DocumentSafeServiceImpl;
import org.adorsys.docusafe.business.impl.WithCache;
import org.adorsys.docusafe.business.types.complex.DSDocument;
import org.adorsys.docusafe.business.types.complex.DocumentDirectoryFQN;
import org.adorsys.docusafe.business.types.complex.DocumentFQN;
import org.adorsys.docusafe.business.types.complex.UserIDAuth;
import org.adorsys.docusafe.cached.transactional.CachedTransactionalDocumentSafeService;
import org.adorsys.docusafe.cached.transactional.impl.CachedTransactionalDocumentSafeServiceImpl;
import org.adorsys.docusafe.rest.impl.SimpleRequestMemoryContextImpl;
import org.adorsys.docusafe.rest.types.TestParameter;
import org.adorsys.docusafe.service.types.DocumentContent;
import org.adorsys.docusafe.transactional.NonTransactionalDocumentSafeService;
import org.adorsys.docusafe.transactional.RequestMemoryContext;
import org.adorsys.docusafe.transactional.TransactionalDocumentSafeService;
import org.adorsys.docusafe.transactional.impl.NonTransactionalDocumentSafeServiceImpl;
import org.adorsys.docusafe.transactional.impl.TransactionalDocumentSafeServiceImpl;
import org.adorsys.docusafe.transactional.types.TxID;
import org.adorsys.encobject.domain.ReadKeyPassword;
import org.adorsys.encobject.service.api.ExtendedStoreConnection;
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

import java.util.Date;
import java.util.Formatter;
import java.util.Locale;
import java.util.Random;

/**
 * Created by peter on 15.08.18 at 15:57.
 */
@RestController
public class TestConcroller {


    private final static Logger LOGGER = LoggerFactory.getLogger(TestConcroller.class);
    private final static String APPLICATION_JSON = "application/json";
    private static int counter = 0;
    private DocumentSafeService[] documentSafeService = null;
    private NonTransactionalDocumentSafeService[] nonTransactionalDocumentSafeServices = null;
    private TransactionalDocumentSafeService[] transactionalDocumentSafeServices = null;
    private CachedTransactionalDocumentSafeService[] cachedTransactionalDocumentSafeServices = null;
    private RequestMemoryContext requestMemoryContext = new SimpleRequestMemoryContextImpl();

    private ExtendedStoreConnection extendedStoreConnection = null;

    public TestConcroller() {
        counter++;
        if (counter > 1) {
            throw new BaseException("did not expect to get more than one controller");
        }
        extendedStoreConnection = ExtendedStoreConnectionFactory.get();

        documentSafeService = new DocumentSafeService[3];
        nonTransactionalDocumentSafeServices = new NonTransactionalDocumentSafeService[3];
        transactionalDocumentSafeServices = new TransactionalDocumentSafeService[3];
        cachedTransactionalDocumentSafeServices = new CachedTransactionalDocumentSafeService[3];

        initServices();
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
        resultString.append(extendedStoreConnection.getClass().getName());
        resultString.append("\n");
        resultString.append(testParameter.toString());
        resultString.append("\n");
        TxID txID = null;
        switch (testParameter.testcase) {
            case CREATE_DOCUMENTS: {
                switch (testParameter.docusafeLayer) {
                    case DOCUSAFE_BASE:
                        documentSafeService[index].createUser(userIDAuth);
                        break;
                    case NON_TRANSACTIONAL:
                        nonTransactionalDocumentSafeServices[index].createUser(userIDAuth);
                        break;
                    case TRANSACTIONAL:
                        transactionalDocumentSafeServices[index].createUser(userIDAuth);
                        stopWatch.start("beginTransaction");
                        txID = transactionalDocumentSafeServices[index].beginTransaction(userIDAuth);
                        stopWatch.stop();
                        break;
                    case CACHED_TRANSACTIONAL:
                        cachedTransactionalDocumentSafeServices[index].createUser(userIDAuth);
                        stopWatch.start("beginTransaction");
                        txID = cachedTransactionalDocumentSafeServices[index].beginTransaction(userIDAuth);
                        stopWatch.stop();
                        break;
                    default:
                        throw new BaseException("missing switch");
                }

                int folderIndex = 1;
                for (int i = 1; i <= testParameter.numberOfDocuments; i++) {
                    DocumentDirectoryFQN folder = new DocumentDirectoryFQN("folder-" + String.format("%03d", folderIndex));
                    DocumentFQN documentFQN = folder.addName("file-" + String.format("%03d", i));
                    if (i % testParameter.documentsPerDirectory == 0) {
                        folderIndex++;
                    }
                    StringBuilder sb = new StringBuilder();

                    DSDocument dsDocument = new DSDocument(documentFQN, createDocumentContent(testParameter.sizeOfDocument, documentFQN), null);
                    stopWatch.start("create document " + documentFQN.getValue());
                    switch (testParameter.docusafeLayer) {
                        case DOCUSAFE_BASE:
                            documentSafeService[index].storeDocument(userIDAuth, dsDocument);
                            break;
                        case NON_TRANSACTIONAL:
                            nonTransactionalDocumentSafeServices[index].nonTxStoreDocument(userIDAuth, dsDocument);
                            break;
                        case TRANSACTIONAL:
                            transactionalDocumentSafeServices[index].txStoreDocument(txID, userIDAuth, dsDocument);
                            break;
                        case CACHED_TRANSACTIONAL:
                            cachedTransactionalDocumentSafeServices[index].txStoreDocument(txID, userIDAuth, dsDocument);
                            break;
                        default:
                            throw new BaseException("missing switch");
                    }
                    stopWatch.stop();
                }
            }
            switch (testParameter.docusafeLayer) {
                case TRANSACTIONAL:
                    stopWatch.start("endTransaction");
                    transactionalDocumentSafeServices[index].endTransaction(txID, userIDAuth);
                    stopWatch.stop();
                    break;
                case CACHED_TRANSACTIONAL:
                    stopWatch.start("endTransaction");
                    cachedTransactionalDocumentSafeServices[index].endTransaction(txID, userIDAuth);
                    stopWatch.stop();
                    break;
            }
            resultString.append(stopWatch.prettyPrint());
            resultString.append("Total Time:" + stopWatch.getTotalTimeMillis());
            resultString.append("\n");
        }
        LOGGER.info(resultString.toString());
        return new ResponseEntity<>(resultString.toString(), HttpStatus.OK);
    }

    private DocumentContent createDocumentContent(Integer sizeOfDocument, DocumentFQN documentFQN) {
        byte[] bytes = new byte[sizeOfDocument];
        new Random().nextBytes(bytes);
        return new DocumentContent(bytes);
    }

    @RequestMapping(
            value = "/deleteDB",
            method = {RequestMethod.GET},
            consumes = {APPLICATION_JSON},
            produces = {APPLICATION_JSON}
    )
    public void deleteDB() {
        LOGGER.info("all buckets will be deleted - but caches not");
        extendedStoreConnection.listAllBuckets().forEach(b -> extendedStoreConnection.deleteContainer(b));
    }

    @RequestMapping(
            value = "/deleteDBAndCaches",
            method = {RequestMethod.GET},
            consumes = {APPLICATION_JSON},
            produces = {APPLICATION_JSON}
    )
    public void deleteDBAndCaches() {
        LOGGER.info("all buckets will be deleted");
        extendedStoreConnection.listAllBuckets().forEach(b -> extendedStoreConnection.deleteContainer(b));
        LOGGER.info("all caches will be deleted");
        initServices();
    }

    @RequestMapping(
            value = "/testContext",
            method = {RequestMethod.GET},
            consumes = {APPLICATION_JSON},
            produces = {APPLICATION_JSON}
    )
    public
    @ResponseBody
    ResponseEntity<String> testContext() {
        LOGGER.info("testContext");
        String value = (String) requestMemoryContext.get("affe");
        LOGGER.info("value for affe is " + value);
        if (value != null) {
            return new ResponseEntity<String>("affe ist schon belegt mit !" + value, HttpStatus.OK);
        }
        requestMemoryContext.put("affe", new Date().toString());
        value = (String) requestMemoryContext.get("affe");
        LOGGER.info("value for affe is " + value);
        if (value == null) {
            return new ResponseEntity<String>("affe ist immer noch null", HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<String>("affe ist " + value, HttpStatus.OK);
    }

    private void initServices() {
        documentSafeService[0] = new DocumentSafeServiceImpl(WithCache.FALSE, extendedStoreConnection);
        documentSafeService[1] = new DocumentSafeServiceImpl(WithCache.TRUE, extendedStoreConnection);
        documentSafeService[2] = new DocumentSafeServiceImpl(WithCache.TRUE_HASH_MAP, extendedStoreConnection);

        nonTransactionalDocumentSafeServices[0] = new NonTransactionalDocumentSafeServiceImpl(requestMemoryContext, documentSafeService[0]);
        nonTransactionalDocumentSafeServices[1] = new NonTransactionalDocumentSafeServiceImpl(requestMemoryContext, documentSafeService[1]);
        nonTransactionalDocumentSafeServices[2] = new NonTransactionalDocumentSafeServiceImpl(requestMemoryContext, documentSafeService[2]);

        transactionalDocumentSafeServices[0] = new TransactionalDocumentSafeServiceImpl(requestMemoryContext, documentSafeService[0]);
        transactionalDocumentSafeServices[1] = new TransactionalDocumentSafeServiceImpl(requestMemoryContext, documentSafeService[1]);
        transactionalDocumentSafeServices[2] = new TransactionalDocumentSafeServiceImpl(requestMemoryContext, documentSafeService[2]);

        cachedTransactionalDocumentSafeServices[0] = new CachedTransactionalDocumentSafeServiceImpl(requestMemoryContext, transactionalDocumentSafeServices[0]);
        cachedTransactionalDocumentSafeServices[1] = new CachedTransactionalDocumentSafeServiceImpl(requestMemoryContext, transactionalDocumentSafeServices[1]);
        cachedTransactionalDocumentSafeServices[2] = new CachedTransactionalDocumentSafeServiceImpl(requestMemoryContext, transactionalDocumentSafeServices[2]);
    }


}

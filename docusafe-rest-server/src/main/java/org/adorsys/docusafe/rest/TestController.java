package org.adorsys.docusafe.rest;

import org.adorsys.cryptoutils.exceptions.BaseException;
import org.adorsys.cryptoutils.exceptions.BaseExceptionHandler;
import org.adorsys.docusafe.business.DocumentSafeService;
import org.adorsys.docusafe.business.impl.DocumentSafeServiceImpl;
import org.adorsys.docusafe.business.impl.WithCache;
import org.adorsys.docusafe.business.types.UserID;
import org.adorsys.docusafe.business.types.complex.DSDocument;
import org.adorsys.docusafe.business.types.complex.DocumentDirectoryFQN;
import org.adorsys.docusafe.business.types.complex.DocumentFQN;
import org.adorsys.docusafe.business.types.complex.UserIDAuth;
import org.adorsys.docusafe.cached.transactional.CachedTransactionalDocumentSafeService;
import org.adorsys.docusafe.cached.transactional.impl.CachedTransactionalDocumentSafeServiceImpl;
import org.adorsys.docusafe.rest.impl.SimpleRequestMemoryContextImpl;
import org.adorsys.docusafe.rest.lombokstuff.DataHolderToTestLombokAndAspects;
import org.adorsys.docusafe.rest.types.DocumentInfo;
import org.adorsys.docusafe.rest.types.ReadDocumentResult;
import org.adorsys.docusafe.rest.types.ReadResult;
import org.adorsys.docusafe.rest.types.TestAction;
import org.adorsys.docusafe.rest.types.TestParameter;
import org.adorsys.docusafe.rest.types.TestsResult;
import org.adorsys.docusafe.service.types.DocumentContent;
import org.adorsys.docusafe.transactional.NonTransactionalDocumentSafeService;
import org.adorsys.docusafe.transactional.RequestMemoryContext;
import org.adorsys.docusafe.transactional.TransactionalDocumentSafeService;
import org.adorsys.docusafe.transactional.impl.NonTransactionalDocumentSafeServiceImpl;
import org.adorsys.docusafe.transactional.impl.TransactionalDocumentSafeServiceImpl;
import org.adorsys.encobject.domain.ReadKeyPassword;
import org.adorsys.encobject.service.api.ExtendedStoreConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StopWatch;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.UUID;

/**
 * Created by peter on 15.08.18 at 15:57.
 */
@RestController
public class TestController {


    private final static Logger LOGGER = LoggerFactory.getLogger(TestController.class);
    private final static String APPLICATION_JSON = "application/json";
    private static int counter = 0;
    private DocumentSafeService[] documentSafeService = null;
    private NonTransactionalDocumentSafeService[] nonTransactionalDocumentSafeServices = null;
    private TransactionalDocumentSafeService[] transactionalDocumentSafeServices = null;
    private CachedTransactionalDocumentSafeService[] cachedTransactionalDocumentSafeServices = null;
    private RequestMemoryContext requestMemoryContext = new SimpleRequestMemoryContextImpl();

    @Autowired
    private ExtendedStoreConnection extendedStoreConnection;

    public TestController() {
        counter++;
        if (counter > 1) {
            throw new BaseException("did not expect to get more than one controller");
        }

        documentSafeService = new DocumentSafeService[3];
        nonTransactionalDocumentSafeServices = new NonTransactionalDocumentSafeService[3];
        transactionalDocumentSafeServices = new TransactionalDocumentSafeService[3];
        cachedTransactionalDocumentSafeServices = new CachedTransactionalDocumentSafeService[3];

        initServices();
    }

    @CrossOrigin
    @RequestMapping(
            value = "/test",
            method = {RequestMethod.PUT},
            consumes = {APPLICATION_JSON},
            produces = {APPLICATION_JSON}
    )
    public
    @ResponseBody
    ResponseEntity<TestsResult> test(@RequestBody TestParameter testParameter) {
        DataHolderToTestLombokAndAspects d = new DataHolderToTestLombokAndAspects();
        d.setName(testParameter.testAction.name());
        TestsResult testsResult = new TestsResult();
        testsResult.extendedStoreConnection = extendedStoreConnection.getClass().getName();
        LOGGER.info("START TEST " + d.getName());
        try {
            switch (testParameter.testAction) {
                case READ_DOCUMENTS:
                case CREATE_DOCUMENTS:
                case DOCUMENT_EXISTS:
                    return regularTest(testParameter, testsResult);
                case DELETE_DATABASE_AND_CACHES:
                case DELETE_CACHES:
                case DELETE_DATABASE:
                    return deleteDB(testParameter, testsResult);
                default:
                    throw new BaseException("testCase not expected:" + testParameter.testAction);
            }
        } catch (Exception e) {
            throw BaseExceptionHandler.handle(e);
        } finally {
            LOGGER.info("FINISED TEST " + testParameter.testAction + testsResult);
        }
    }

    private ResponseEntity<TestsResult> regularTest(TestParameter testParameter, TestsResult testsResult) {
        if (testParameter.userid == null || testParameter.userid.getValue().length() == 0) {
            if (testParameter.testAction.equals(TestAction.READ_DOCUMENTS)) {
                throw new BaseException("programming error. request must contain userid");
            }
            testParameter.userid = new UserID(UUID.randomUUID().toString());
        }
        testsResult.userID = testParameter.userid;
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
        List<DocumentInfo> createdDocuments = new ArrayList<>();
        List<ReadDocumentResult> readDocuments = new ArrayList<>();
        switch (testParameter.testAction) {
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
                        transactionalDocumentSafeServices[index].beginTransaction(userIDAuth);
                        stopWatch.stop();
                        break;
                    case CACHED_TRANSACTIONAL:
                        cachedTransactionalDocumentSafeServices[index].createUser(userIDAuth);
                        stopWatch.start("beginTransaction");
                        cachedTransactionalDocumentSafeServices[index].beginTransaction(userIDAuth);
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
                    String uniqueToken = getUniqueStringForDocument(documentFQN, userIDAuth.getUserID());
                    {
                        DocumentInfo testResultCreatedDocument = new DocumentInfo();
                        testResultCreatedDocument.documentFQN = documentFQN;
                        testResultCreatedDocument.uniqueToken = uniqueToken;
                        testResultCreatedDocument.size = testParameter.sizeOfDocument;
                        createdDocuments.add(testResultCreatedDocument);
                    }
                    DSDocument dsDocument = new DSDocument(documentFQN, createDocumentContent(testParameter.sizeOfDocument, documentFQN, uniqueToken), null);
                    stopWatch.start("create document " + documentFQN.getValue());
                    switch (testParameter.docusafeLayer) {
                        case DOCUSAFE_BASE:
                            documentSafeService[index].storeDocument(userIDAuth, dsDocument);
                            break;
                        case NON_TRANSACTIONAL:
                            nonTransactionalDocumentSafeServices[index].nonTxStoreDocument(userIDAuth, dsDocument);
                            break;
                        case TRANSACTIONAL:
                            transactionalDocumentSafeServices[index].txStoreDocument(userIDAuth, dsDocument);
                            break;
                        case CACHED_TRANSACTIONAL:
                            cachedTransactionalDocumentSafeServices[index].txStoreDocument(userIDAuth, dsDocument);
                            break;
                        default:
                            throw new BaseException("missing switch");
                    }
                    stopWatch.stop();
                }
                break;
            }
            case READ_DOCUMENTS:
            case DOCUMENT_EXISTS: {
                switch (testParameter.docusafeLayer) {
                    case TRANSACTIONAL:
                        stopWatch.start("beginTransaction");
                        transactionalDocumentSafeServices[index].beginTransaction(userIDAuth);
                        stopWatch.stop();
                        break;
                    case CACHED_TRANSACTIONAL:
                        stopWatch.start("beginTransaction");
                        cachedTransactionalDocumentSafeServices[index].beginTransaction(userIDAuth);
                        stopWatch.stop();
                        break;
                }

                switch (testParameter.testAction) {
                    case READ_DOCUMENTS: {
                        for (DocumentInfo documentInfo : testParameter.documentsToRead) {
                            DocumentFQN documentFQN = documentInfo.documentFQN;
                            String uniqueToken = documentInfo.uniqueToken;
                            int size = documentInfo.size;
                            DSDocument dsDocument = null;
                            stopWatch.start("read document " + documentFQN.getValue());
                            try {
                                switch (testParameter.docusafeLayer) {
                                    case DOCUSAFE_BASE:
                                        dsDocument = documentSafeService[index].readDocument(userIDAuth, documentFQN);
                                        break;
                                    case NON_TRANSACTIONAL:
                                        dsDocument = nonTransactionalDocumentSafeServices[index].nonTxReadDocument(userIDAuth, documentFQN);
                                        break;
                                    case TRANSACTIONAL:
                                        dsDocument = transactionalDocumentSafeServices[index].txReadDocument(userIDAuth, documentFQN);
                                        break;
                                    case CACHED_TRANSACTIONAL:
                                        dsDocument = cachedTransactionalDocumentSafeServices[index].txReadDocument(userIDAuth, documentFQN);
                                        break;
                                    default:
                                        throw new BaseException("missing switch");
                                }
                            } catch (BaseException e) {
                                // TODO genauer Typ muss hier noch geprüft werden, nur die FileNotFoundException wird erwartet....
                            }
                            stopWatch.stop();
                            readDocuments.add(checkDocumentWasRead(dsDocument, documentInfo));
                        }
                        break;
                    }
                    case DOCUMENT_EXISTS: {
                        for (DocumentInfo documentInfo : testParameter.documentsToRead) {
                            DocumentFQN documentFQN = documentInfo.documentFQN;
                            boolean exists;
                            stopWatch.start("document exists" + documentFQN.getValue());
                            switch (testParameter.docusafeLayer) {
                                case DOCUSAFE_BASE:
                                    exists = documentSafeService[index].documentExists(userIDAuth, documentFQN);
                                    break;
                                case NON_TRANSACTIONAL:
                                    exists = nonTransactionalDocumentSafeServices[index].nonTxDocumentExists(userIDAuth, documentFQN);
                                    break;
                                case TRANSACTIONAL:
                                    exists = transactionalDocumentSafeServices[index].txDocumentExists(userIDAuth, documentFQN);
                                    break;
                                case CACHED_TRANSACTIONAL:
                                    exists = cachedTransactionalDocumentSafeServices[index].txDocumentExists(userIDAuth, documentFQN);
                                    break;
                                default:
                                    throw new BaseException("missing switch");
                            }
                            stopWatch.stop();
                            readDocuments.add(checkDocumentExsits(exists, documentInfo));
                        }
                        break;
                    }
                }

                break;
            }
            default:
                throw new BaseException("missing switch for " + testParameter.testAction);

        }
        switch (testParameter.docusafeLayer) {
            case TRANSACTIONAL:
                stopWatch.start("endTransaction");
                transactionalDocumentSafeServices[index].endTransaction(userIDAuth);
                stopWatch.stop();
                break;
            case CACHED_TRANSACTIONAL:
                stopWatch.start("endTransaction");
                cachedTransactionalDocumentSafeServices[index].endTransaction(userIDAuth);
                stopWatch.stop();
                break;
        }
        addStopWatchToTestsResult(stopWatch, testsResult);
        addCreatedDocumentsToTestResults(createdDocuments, testsResult);
        addReadDocumentsToTestResults(readDocuments, testsResult);
        return new ResponseEntity<>(testsResult, HttpStatus.OK);
    }

    private ReadDocumentResult checkDocumentWasRead(DSDocument dsDocument, DocumentInfo documentInfo) {
        ReadDocumentResult readDocumentResult = new ReadDocumentResult();
        readDocumentResult.documentFQN = documentInfo.documentFQN;
        if (dsDocument == null) {
            readDocumentResult.readResult = ReadResult.NOT_FOUND;
        } else {
            byte[] foundBytes = dsDocument.getDocumentContent().getValue();
            if (foundBytes.length != documentInfo.size && documentInfo.size > documentInfo.uniqueToken.length()) {
                readDocumentResult.readResult = ReadResult.WRONG_SIZE;
            } else {
                byte[] expectedBytes = documentInfo.uniqueToken.getBytes();
                readDocumentResult.readResult = ReadResult.OK;
                for (int i = 0; i < expectedBytes.length; i++) {
                    if (expectedBytes[i] != foundBytes[i]) {
                        readDocumentResult.readResult = ReadResult.WRONG_CONTENT;
                    }
                }
            }
        }
        if (readDocumentResult.readResult == null) {
            throw new BaseException("Programming Error. readResult must not be null");
        }
        return readDocumentResult;
    }

    private ReadDocumentResult checkDocumentExsits(Boolean exists, DocumentInfo documentInfo) {
        ReadDocumentResult readDocumentResult = new ReadDocumentResult();
        readDocumentResult.documentFQN = documentInfo.documentFQN;
        readDocumentResult.readResult = exists ? ReadResult.OK : ReadResult.NOT_FOUND;
        return readDocumentResult;
    }

    private ResponseEntity<TestsResult> deleteDB(TestParameter testParameter, TestsResult testsResult) {
        StopWatch stopWatch = new StopWatch();
        switch (testParameter.testAction) {
            case DELETE_DATABASE:
            case DELETE_DATABASE_AND_CACHES: {
                LOGGER.info("delete database");
                stopWatch.start("delete database");
                extendedStoreConnection.listAllBuckets().forEach(b -> extendedStoreConnection.deleteContainer(b));
                stopWatch.stop();
                break;
            }
        }
        switch (testParameter.testAction) {
            case DELETE_CACHES:
            case DELETE_DATABASE_AND_CACHES: {
                LOGGER.info("delete caches");
                stopWatch.start("delete caches");
                initServices();
                stopWatch.stop();
                break;
            }
        }
        addStopWatchToTestsResult(stopWatch, testsResult);
        return new ResponseEntity<>(testsResult, HttpStatus.OK);
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


    private void addStopWatchToTestsResult(StopWatch stopWatch, TestsResult testsResult) {
        StopWatch.TaskInfo[] stopWatchTaskInfos = stopWatch.getTaskInfo();
        testsResult.tasks = new TestsResult.TaskInfo[stopWatchTaskInfos.length];
        for (int i = 0; i < stopWatchTaskInfos.length; i++) {
            StopWatch.TaskInfo stopWatchTaskInfo = stopWatchTaskInfos[i];
            testsResult.tasks[i] = new TestsResult.TaskInfo();
            testsResult.tasks[i].name = stopWatchTaskInfo.getTaskName();
            testsResult.tasks[i].time = stopWatchTaskInfo.getTimeMillis();
        }
        testsResult.totalTime = stopWatch.getTotalTimeMillis();
    }

    private void addCreatedDocumentsToTestResults(List<DocumentInfo> createdDocuments, TestsResult testsResult) {
        testsResult.listOfCreatedDocuments = createdDocuments.toArray(new DocumentInfo[createdDocuments.size()]);
    }

    private void addReadDocumentsToTestResults(List<ReadDocumentResult> readDocuments, TestsResult testsResult) {
        testsResult.listOfReadDocuments = readDocuments.toArray(new ReadDocumentResult[readDocuments.size()]);
    }

    private DocumentContent createDocumentContent(Integer sizeOfDocument, DocumentFQN documentFQN, String uniqueToken) {
        byte[] uniqueTokenBytes = uniqueToken.getBytes();
        int uniqueTokenLength = uniqueTokenBytes.length;
        if (sizeOfDocument < uniqueTokenLength) {
            sizeOfDocument = uniqueTokenLength;
        }
        byte[] bytes = new byte[sizeOfDocument];
        new Random().nextBytes(bytes);

        for (int i = 0; i < uniqueTokenLength; i++) {
            bytes[i] = uniqueTokenBytes[i];
        }
        return new DocumentContent(bytes);
    }

    private String getUniqueStringForDocument(DocumentFQN documentFQN, UserID userID) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss.SSS");
        String timestamp = sdf.format(new Date());
        String fullName = documentFQN.getValue();
        String uniqueToken = wrap(userID.getValue()) + wrap(fullName) + wrap(timestamp);
        return uniqueToken;
    }

    private String wrap(String content) {
        return "(" + content + ")";
    }


}

package de.adorsys.docusafe.rest.controller;

import de.adorsys.common.exceptions.BaseException;
import de.adorsys.common.exceptions.BaseExceptionHandler;
import de.adorsys.datasafe.simple.adapter.api.SimpleDatasafeService;
import de.adorsys.datasafe.simple.adapter.api.types.AmazonS3DFSCredentials;
import de.adorsys.datasafe.simple.adapter.api.types.FilesystemDFSCredentials;
import de.adorsys.datasafe.simple.adapter.impl.SimpleDatasafeServiceImpl;
import de.adorsys.dfs.connection.api.filesystem.FilesystemConnectionPropertiesImpl;
import de.adorsys.dfs.connection.api.service.api.DFSConnection;
import de.adorsys.dfs.connection.api.types.ListRecursiveFlag;
import de.adorsys.dfs.connection.api.types.connection.AmazonS3AccessKey;
import de.adorsys.dfs.connection.api.types.connection.AmazonS3RootBucketName;
import de.adorsys.dfs.connection.api.types.connection.AmazonS3SecretKey;
import de.adorsys.dfs.connection.api.types.connection.FilesystemRootBucketName;
import de.adorsys.dfs.connection.api.types.properties.ConnectionProperties;
import de.adorsys.dfs.connection.impl.amazons3.AmazonS3ConnectionProperitesImpl;
import de.adorsys.dfs.connection.impl.factory.DFSConnectionFactory;
import de.adorsys.docusafe.business.DocumentSafeService;
import de.adorsys.docusafe.business.impl.DocumentSafeServiceImpl;
import de.adorsys.docusafe.business.types.DFSCredentials;
import de.adorsys.docusafe.business.types.DSDocument;
import de.adorsys.docusafe.business.types.DocumentDirectoryFQN;
import de.adorsys.docusafe.business.types.DocumentFQN;
import de.adorsys.docusafe.cached.transactional.CachedTransactionalDocumentSafeService;
import de.adorsys.docusafe.cached.transactional.impl.CachedTransactionalDocumentSafeServiceImpl;
import de.adorsys.docusafe.rest.types.*;
import de.adorsys.docusafe.service.api.keystore.types.ReadKeyPassword;
import de.adorsys.docusafe.service.api.types.DocumentContent;
import de.adorsys.docusafe.service.api.types.UserID;
import de.adorsys.docusafe.service.api.types.UserIDAuth;
import de.adorsys.docusafe.spring.SimpleRequestMemoryContextImpl;
import de.adorsys.docusafe.spring.factory.SpringDFSConnectionFactory;
import de.adorsys.docusafe.transactional.RequestMemoryContext;
import de.adorsys.docusafe.transactional.impl.TransactionalDocumentSafeServiceImpl;
import de.adorsys.docusafe.transactional.types.TxBucketContentFQN;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StopWatch;
import org.springframework.web.bind.annotation.*;

import javax.annotation.PostConstruct;
import java.nio.file.FileSystems;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Created by peter on 15.08.18 at 15:57.
 */
@RestController
public class TestController {
    private final static Logger LOGGER = LoggerFactory.getLogger(TestController.class);
    private final static String APPLICATION_JSON = "application/json";
    private static int counter = 0;
    private DocumentSafeService plainDocumentSafeService = null;
    private SimpleDatasafeService simpleDatasafeService = null;
    private CachedTransactionalDocumentSafeService cachedTransactionalDocumentSafeServices = null;

    private RequestMemoryContext requestMemoryContext = new SimpleRequestMemoryContextImpl();

    @Autowired
    SpringDFSConnectionFactory factory;

    private DFSConnection docusafePlainDFSConnection = null;
    private DFSConnection datasafePlainDFSConnection = null;
    private DFSConnection docusafeCachedTransactionalDFSConnection = null;



    @PostConstruct
    public void postconstruct() {
        counter++;
        if (counter > 1) {
            throw new BaseException("did not expect to get more than one controller");
        }

        docusafePlainDFSConnection = factory.getDFSConnectionWithSubDir("docusafe/plainfolder");
        datasafePlainDFSConnection = factory.getDFSConnectionWithSubDir("datasafe/plainfolder");
        docusafeCachedTransactionalDFSConnection = factory.getDFSConnectionWithSubDir("docusafe/cachedtxfolder");

        plainDocumentSafeService = null;
        simpleDatasafeService = null;
        cachedTransactionalDocumentSafeServices = null;

        initServices();
    }

    @CrossOrigin
    @RequestMapping(
            value = "/config/dfs",
            method = {RequestMethod.GET},
            produces = {APPLICATION_JSON}
    )
    public
    @ResponseBody
    ResponseEntity<DFSCredentials> getDfsConfiguration() {
        DFSCredentials credentials = new DFSCredentials(docusafePlainDFSConnection.getConnectionProperties());
        String r;
        if (credentials.getAmazons3() != null) {
            r = credentials.getAmazons3().getAmazonS3RootBucketName().getValue();
        } else {
            r = credentials.getFilesystem().getFilesystemRootBucketName().getValue();
        }

        if (! r.endsWith("plainfolder")) {
            throw new BaseException("can not return credentials due to path problem");
        }
        r = r.substring(0, r.length()- "plainfolder".length() -1);
        if (credentials.getAmazons3() != null) {
            credentials.getAmazons3().setAmazonS3RootBucketName(new AmazonS3RootBucketName(r));

            // encrypt credentials
            String p = hide(credentials.getAmazons3().getAmazonS3AccessKey().getValue());
            credentials.getAmazons3().setAmazonS3AccessKey(new AmazonS3AccessKey(p));

            p = hide((credentials.getAmazons3().getAmazonS3SecretKey().getValue()));
            credentials.getAmazons3().setAmazonS3SecretKey(new AmazonS3SecretKey(p));

        } else {
            credentials.getFilesystem().setFilesystemRootBucketName(new FilesystemRootBucketName(r));
        }


        LOGGER.debug("return  " + credentials);
        return new ResponseEntity<>(credentials, HttpStatus.OK);
    }

    private de.adorsys.datasafe.simple.adapter.api.types.DFSCredentials getDatasafeDFSCredentials(ConnectionProperties properties) {
        if (properties instanceof AmazonS3ConnectionProperitesImpl) {
            AmazonS3ConnectionProperitesImpl props = (AmazonS3ConnectionProperitesImpl) properties;
            return AmazonS3DFSCredentials.builder()
                    .url(props.getUrl().toString())
                    .accessKey(props.getAmazonS3AccessKey().getValue())
                    .secretKey(props.getAmazonS3SecretKey().getValue())
                    .region(props.getAmazonS3Region().getValue())
                    .rootBucket(props.getAmazonS3RootBucketName().getValue())
                    .build();
        }
        if (properties instanceof FilesystemConnectionPropertiesImpl) {
            FilesystemConnectionPropertiesImpl props = (FilesystemConnectionPropertiesImpl) properties;
            return FilesystemDFSCredentials.builder()
                    .root(FileSystems.getDefault().getPath(props.getFilesystemRootBucketName().getValue()))
                    .build();
        }
        throw new BaseException("missing type for ConnectionProperties:" + properties.getClass().getCanonicalName());
    }

    private static final String hide(String value) {
        return value.length() > 4 ? value.substring(0, 2) + "***" + value.substring(value.length() - 2) : "***";
    }

    @CrossOrigin
    @RequestMapping(
            value = "/config/dfs",
            method = {RequestMethod.PUT},
            consumes = {APPLICATION_JSON},
            produces = {APPLICATION_JSON}
    )
    public
    @ResponseBody
    ResponseEntity<String> setDfsConfiguration(@RequestBody DFSCredentials dfsCredentials) {
        LOGGER.info(dfsCredentials.toString());
        {
            DFSCredentials plainCredentials = new DFSCredentials(dfsCredentials);
            plainCredentials.addSubDirToRoot("plainfolder");
            DFSConnection dfsConnection = DFSConnectionFactory.get(plainCredentials.getProperties());
            // if an exception has raised here, the old connection is still available
            docusafePlainDFSConnection = dfsConnection;
        }
        {
            DFSCredentials plainCredentials = new DFSCredentials(dfsCredentials);
            plainCredentials.addSubDirToRoot("cachedtxfolder");
            docusafeCachedTransactionalDFSConnection = DFSConnectionFactory.get(plainCredentials.getProperties());
        }
        initServices();
        return new ResponseEntity<>("ok", HttpStatus.OK);
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
        TestsResult testsResult = new TestsResult();
        testsResult.dfsConnectionString = docusafePlainDFSConnection.getClass().getName() + new DFSCredentials(docusafePlainDFSConnection.getConnectionProperties()).toString();
        LOGGER.info("START TEST " + testParameter.testAction + " requestID: " + testParameter.dynamicClientInfo.requestID);
        try {
            switch (testParameter.testAction) {
                case READ_DOCUMENTS:
                case CREATE_DOCUMENTS:
                case DOCUMENT_EXISTS:
                case LIST_DOCUMENTS:
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
            LOGGER.info("FINISHED TEST " + testParameter.testAction + " requestID: " + testParameter.dynamicClientInfo.requestID + " " + testsResult);
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
        UserIDAuth userIDAuth = new UserIDAuth(testParameter.userid, new ReadKeyPassword("password for " + testParameter.userid.getValue()));
        StopWatch stopWatch = new StopWatch();
        List<DocumentInfo> createdDocuments = new ArrayList<>();
        List<ReadDocumentResult> readDocuments = new ArrayList<>();
        List<DocumentFQN> foundDocuments = new ArrayList<>();
        switch (testParameter.testAction) {
            case CREATE_DOCUMENTS: {

                switch (testParameter.docusafeLayer) {
                    case DOCUSAFE_BASE:
                        if (!plainDocumentSafeService.userExists(userIDAuth.getUserID())) {
                            plainDocumentSafeService.createUser(userIDAuth);
                        }
                        break;
                    case SIMPLE_DATASAFE_ADAPTER:
                        if (!simpleDatasafeService.userExists(c(userIDAuth.getUserID()))) {
                            simpleDatasafeService.createUser(c(userIDAuth));
                        }
                        break;
                    case CACHED_TRANSACTIONAL:
                        if (!cachedTransactionalDocumentSafeServices.userExists(userIDAuth.getUserID())) {
                            cachedTransactionalDocumentSafeServices.createUser(userIDAuth);
                        }
                        stopWatch.start("beginTransaction");
                        cachedTransactionalDocumentSafeServices.beginTransaction(userIDAuth);
                        stopWatch.stop();
                        break;
                    default:
                        throw new BaseException("missing switch");
                }

                int folderIndex = 1;
                for (int i = 1; i <= testParameter.numberOfDocuments; i++) {
                    DocumentFQN documentFQN;
                    if (testParameter.createDeterministicDocuments != null && testParameter.createDeterministicDocuments.equals(Boolean.TRUE)) {
                        DocumentDirectoryFQN folder = new DocumentDirectoryFQN("folder-" + String.format("%03d", folderIndex));
                        documentFQN = folder.addName("file-" + String.format("%03d", i));
                    } else {
                        DocumentDirectoryFQN folder = new DocumentDirectoryFQN("folder-" + String.format("%03d", folderIndex) + "-" + UUID.randomUUID().toString());
                        documentFQN = folder.addName("file-" + String.format("%03d", i) + "-" + UUID.randomUUID().toString());
                    }
                    if (i % testParameter.documentsPerDirectory == 0) {
                        folderIndex++;
                    }
                    String uniqueToken = TestUtil.getUniqueStringForDocument(documentFQN, userIDAuth.getUserID());
                    {
                        DocumentInfo testResultCreatedDocument = new DocumentInfo();
                        testResultCreatedDocument.documentFQN = documentFQN;
                        testResultCreatedDocument.uniqueToken = uniqueToken;
                        testResultCreatedDocument.size = testParameter.sizeOfDocument;
                        createdDocuments.add(testResultCreatedDocument);
                    }
                    DSDocument dsDocument = new DSDocument(documentFQN, TestUtil.createDocumentContent(testParameter.sizeOfDocument, documentFQN, uniqueToken));
                    stopWatch.start("create document " + documentFQN.getValue());
                    switch (testParameter.docusafeLayer) {
                        case DOCUSAFE_BASE:
                            plainDocumentSafeService.storeDocument(userIDAuth, dsDocument);
                            break;
                        case SIMPLE_DATASAFE_ADAPTER:
                            simpleDatasafeService.storeDocument(c(userIDAuth), c(dsDocument));
                            break;
                        case CACHED_TRANSACTIONAL:
                            cachedTransactionalDocumentSafeServices.txStoreDocument(userIDAuth, dsDocument);
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
                    case CACHED_TRANSACTIONAL:
                        stopWatch.start("beginTransaction");
                        cachedTransactionalDocumentSafeServices.beginTransaction(userIDAuth);
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
                                        dsDocument = plainDocumentSafeService.readDocument(userIDAuth, documentFQN);
                                        break;
                                    case SIMPLE_DATASAFE_ADAPTER:
                                        dsDocument = c(simpleDatasafeService.readDocument(c(userIDAuth), c(documentFQN)));
                                        break;
                                    case CACHED_TRANSACTIONAL:
                                        dsDocument = cachedTransactionalDocumentSafeServices.txReadDocument(userIDAuth, documentFQN);
                                        break;
                                    default:
                                        throw new BaseException("missing switch");
                                }
                            } catch (BaseException e) {
                                // TODO genauer Typ muss hier noch geprüft werden, nur die FileNotFoundException wird erwartet....
                            }
                            stopWatch.stop();
                            readDocuments.add(TestUtil.checkDocumentWasRead(dsDocument, documentInfo));
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
                                    exists = plainDocumentSafeService.documentExists(userIDAuth, documentFQN);
                                    break;
                                case SIMPLE_DATASAFE_ADAPTER:
                                    exists = simpleDatasafeService.documentExists(c(userIDAuth), c(documentFQN));
                                    break;
                                case CACHED_TRANSACTIONAL:
                                    exists = cachedTransactionalDocumentSafeServices.txDocumentExists(userIDAuth, documentFQN);
                                    break;
                                default:
                                    throw new BaseException("missing switch");
                            }
                            stopWatch.stop();
                            readDocuments.add(TestUtil.checkDocumentExsits(exists, documentInfo));
                        }
                        break;
                    }
                }
                break;
            }
            case LIST_DOCUMENTS: {
                switch (testParameter.docusafeLayer) {
                    case CACHED_TRANSACTIONAL:
                        stopWatch.start("beginTransaction");
                        cachedTransactionalDocumentSafeServices.beginTransaction(userIDAuth);
                        stopWatch.stop();
                        break;
                }

                DocumentDirectoryFQN documentDirectoryFQN = new DocumentDirectoryFQN("/");
                List<DocumentFQN> list = null;
                TxBucketContentFQN txBucketContentFQN = null;
                stopWatch.start("list all documents of " + documentDirectoryFQN);
                try {
                    switch (testParameter.docusafeLayer) {
                        case DOCUSAFE_BASE:
                            list = plainDocumentSafeService.list(userIDAuth, documentDirectoryFQN, ListRecursiveFlag.TRUE);
                            break;
                        case SIMPLE_DATASAFE_ADAPTER:
                            list = c(simpleDatasafeService.list(c(userIDAuth), c(documentDirectoryFQN), c(ListRecursiveFlag.TRUE)));
                            break;
                        case CACHED_TRANSACTIONAL:
                            txBucketContentFQN = cachedTransactionalDocumentSafeServices.txListDocuments(userIDAuth, documentDirectoryFQN, ListRecursiveFlag.TRUE);
                            break;
                        default:
                            throw new BaseException("missing switch");
                    }
                } catch (BaseException e) {
                    // TODO genauer Typ muss hier noch geprüft werden, nur die FileNotFoundException wird erwartet....
                }
                stopWatch.stop();
                if (list != null) {
                    foundDocuments.addAll(list);
                }
                if (txBucketContentFQN != null) {
                    foundDocuments.addAll(txBucketContentFQN.getFiles());
                }
                break;
            }
        }
        switch (testParameter.docusafeLayer) {
            case CACHED_TRANSACTIONAL:
                stopWatch.start("endTransaction");
                cachedTransactionalDocumentSafeServices.endTransaction(userIDAuth);
                stopWatch.stop();
                break;
        }
        TestUtil.addStopWatchToTestsResult(stopWatch, testsResult);
        TestUtil.addCreatedDocumentsToTestResults(createdDocuments, testsResult);
        TestUtil.addReadDocumentsToTestResults(readDocuments, testsResult);
        TestUtil.addFoundDocumentsToTestResults(foundDocuments, testsResult);
        return new ResponseEntity<>(testsResult, HttpStatus.OK);
    }

    private List<DocumentFQN> c(List<de.adorsys.datasafe.simple.adapter.api.types.DocumentFQN> list) {
        return list.stream().map(d -> c(d)).collect(Collectors.toList());
    }

    private de.adorsys.datasafe.simple.adapter.api.types.ListRecursiveFlag c(ListRecursiveFlag aTrue) {
        return de.adorsys.datasafe.simple.adapter.api.types.ListRecursiveFlag.valueOf(aTrue.name());
    }

    private de.adorsys.datasafe.simple.adapter.api.types.DocumentDirectoryFQN c(DocumentDirectoryFQN documentDirectoryFQN) {
        return new de.adorsys.datasafe.simple.adapter.api.types.DocumentDirectoryFQN(documentDirectoryFQN.getValue());
    }

    private DSDocument c(de.adorsys.datasafe.simple.adapter.api.types.DSDocument readDocument) {
        return new DSDocument(c(readDocument.getDocumentFQN()), c(readDocument.getDocumentContent()));
    }

    private DocumentContent c(de.adorsys.datasafe.simple.adapter.api.types.DocumentContent documentContent) {
        return new DocumentContent(documentContent.getValue());
    }

    private DocumentFQN c(de.adorsys.datasafe.simple.adapter.api.types.DocumentFQN documentFQN) {
        return new DocumentFQN(documentFQN.getValue());
    }

    private de.adorsys.datasafe.simple.adapter.api.types.DSDocument c(DSDocument dsDocument) {
        return new de.adorsys.datasafe.simple.adapter.api.types.DSDocument(c(dsDocument.getDocumentFQN()), c(dsDocument.getDocumentContent()));
    }

    private de.adorsys.datasafe.simple.adapter.api.types.DocumentContent c(DocumentContent documentContent) {
        return new de.adorsys.datasafe.simple.adapter.api.types.DocumentContent(documentContent.getValue());
    }

    private de.adorsys.datasafe.simple.adapter.api.types.DocumentFQN c(DocumentFQN documentFQN) {
        return new de.adorsys.datasafe.simple.adapter.api.types.DocumentFQN(documentFQN.getValue());
    }

    private de.adorsys.datasafe.encrypiton.api.types.UserIDAuth c(UserIDAuth userIDAuth) {
        return new de.adorsys.datasafe.encrypiton.api.types.UserIDAuth(c(userIDAuth.getUserID()), c(userIDAuth.getReadKeyPassword()));
    }

    private de.adorsys.datasafe.encrypiton.api.types.keystore.ReadKeyPassword c(ReadKeyPassword readKeyPassword) {
        return new de.adorsys.datasafe.encrypiton.api.types.keystore.ReadKeyPassword(readKeyPassword.getValue());
    }

    private de.adorsys.datasafe.encrypiton.api.types.UserID c(UserID userID) {
        return new de.adorsys.datasafe.encrypiton.api.types.UserID(userID.getValue());
    }


    private ResponseEntity<TestsResult> deleteDB(TestParameter testParameter, TestsResult testsResult) {
        StopWatch stopWatch = new StopWatch();
        switch (testParameter.testAction) {
            case DELETE_DATABASE:
            case DELETE_DATABASE_AND_CACHES: {
                LOGGER.info("delete database");
                stopWatch.start("delete database");
                docusafePlainDFSConnection.deleteDatabase();
                docusafeCachedTransactionalDFSConnection.deleteDatabase();
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
        TestUtil.addStopWatchToTestsResult(stopWatch, testsResult);
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
        plainDocumentSafeService = new DocumentSafeServiceImpl(docusafePlainDFSConnection);
        DocumentSafeServiceImpl dss1 = new DocumentSafeServiceImpl(docusafeCachedTransactionalDFSConnection);
        cachedTransactionalDocumentSafeServices = new CachedTransactionalDocumentSafeServiceImpl(requestMemoryContext, new TransactionalDocumentSafeServiceImpl(requestMemoryContext, dss1), dss1);
        simpleDatasafeService = new SimpleDatasafeServiceImpl(getDatasafeDFSCredentials(datasafePlainDFSConnection.getConnectionProperties()));
    }
}

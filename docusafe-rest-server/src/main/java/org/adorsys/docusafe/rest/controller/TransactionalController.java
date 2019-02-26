package org.adorsys.docusafe.rest.controller;

import org.adorsys.cryptoutils.exceptions.BaseException;
import org.adorsys.cryptoutils.exceptions.BaseExceptionHandler;
import org.adorsys.docusafe.business.types.UserID;
import org.adorsys.docusafe.business.types.complex.DSDocument;
import org.adorsys.docusafe.business.types.complex.DocumentDirectoryFQN;
import org.adorsys.docusafe.business.types.complex.DocumentFQN;
import org.adorsys.docusafe.business.types.complex.UserIDAuth;
import org.adorsys.docusafe.cached.transactional.CachedTransactionalDocumentSafeService;
import org.adorsys.docusafe.rest.configs.DocusafeConfig;
import org.adorsys.docusafe.rest.types.DocumentInfo;
import org.adorsys.docusafe.rest.types.ReadDocumentResult;
import org.adorsys.docusafe.rest.types.TestAction;
import org.adorsys.docusafe.rest.types.TestParameter;
import org.adorsys.docusafe.rest.types.TestUtil;
import org.adorsys.docusafe.rest.types.TestsResult;
import org.adorsys.encobject.domain.ReadKeyPassword;
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

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by peter on 27.11.18 15:27.
 */
@RestController
public class TransactionalController {
    private final static Logger LOGGER = LoggerFactory.getLogger(TransactionalController.class);
    private final static String APPLICATION_JSON = "application/json";

    @Autowired
    CachedTransactionalDocumentSafeService cachedTransactionalDocumentSafeService1;
    @Autowired
    DocusafeConfig.WrapperA cachedTransactionalDocumentSafeService2;
    @Autowired
    DocusafeConfig.WrapperB cachedTransactionalDocumentSafeService3;

    @CrossOrigin
    @RequestMapping(
            value = "/testtx",
            method = {RequestMethod.PUT},
            consumes = {APPLICATION_JSON},
            produces = {APPLICATION_JSON}
    )
    public
    @ResponseBody
    ResponseEntity<TestsResult> testtx(@RequestBody TestParameter testParameter) {
        TestsResult testsResult = new TestsResult();
        testsResult.extendedStoreConnection = cachedTransactionalDocumentSafeService1.getClass().getName();
        LOGGER.info("START TEST " + testParameter.testAction);
        try {
            switch (testParameter.testAction) {
                case READ_DOCUMENTS:
                case CREATE_DOCUMENTS:
                case DOCUMENT_EXISTS:
                    return regularTest(testParameter, testsResult);
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
        UserIDAuth userIDAuth = new UserIDAuth(testParameter.userid, new ReadKeyPassword("password for " + testParameter.userid.getValue()));
        StopWatch stopWatch = new StopWatch();
        List<DocumentInfo> createdDocuments = new ArrayList<>();
        List<ReadDocumentResult> readDocuments = new ArrayList<>();

        stopWatch.start("beginTransaction");
        cachedTransactionalDocumentSafeService1.beginTransaction(userIDAuth);
        stopWatch.stop();

        switch (testParameter.testAction) {
            case CREATE_DOCUMENTS: {

                cachedTransactionalDocumentSafeService1.createUser(userIDAuth);

                int folderIndex = 1;
                for (int i = 1; i <= testParameter.numberOfDocuments; i++) {
                    DocumentDirectoryFQN folder = new DocumentDirectoryFQN("folder-" + String.format("%03d", folderIndex));
                    DocumentFQN documentFQN = folder.addName("file-" + String.format("%03d", i));
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
                    DSDocument dsDocument = new DSDocument(documentFQN, TestUtil.createDocumentContent(testParameter.sizeOfDocument, documentFQN, uniqueToken), null);
                    stopWatch.start("create document " + documentFQN.getValue());
                    cachedTransactionalDocumentSafeService1.txStoreDocument(userIDAuth, dsDocument);
                    stopWatch.stop();
                }
                break;
            }
            case READ_DOCUMENTS: {
                for (DocumentInfo documentInfo : testParameter.documentsToRead) {
                    DocumentFQN documentFQN = documentInfo.documentFQN;
                    String uniqueToken = documentInfo.uniqueToken;
                    int size = documentInfo.size;
                    DSDocument dsDocument = null;
                    stopWatch.start("read document " + documentFQN.getValue());
                    dsDocument = cachedTransactionalDocumentSafeService1.txReadDocument(userIDAuth, documentFQN);
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
                    exists = cachedTransactionalDocumentSafeService1.txDocumentExists(userIDAuth, documentFQN);
                    stopWatch.stop();
                    readDocuments.add(TestUtil.checkDocumentExsits(exists, documentInfo));
                }
                break;
            }
            default:
                throw new

                        BaseException("missing switch for " + testParameter.testAction);

        }
        stopWatch.start("endTransaction");
        cachedTransactionalDocumentSafeService1.endTransaction(userIDAuth);
        stopWatch.stop();

        TestUtil.addStopWatchToTestsResult(stopWatch, testsResult);
        TestUtil.addCreatedDocumentsToTestResults(createdDocuments, testsResult);
        TestUtil.addReadDocumentsToTestResults(readDocuments, testsResult);
        return new ResponseEntity<>(testsResult, HttpStatus.OK);
    }

}

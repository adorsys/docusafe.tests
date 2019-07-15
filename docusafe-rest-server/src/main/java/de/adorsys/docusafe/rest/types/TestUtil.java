package de.adorsys.docusafe.rest.types;

import de.adorsys.common.exceptions.BaseException;
import de.adorsys.docusafe.business.types.DSDocument;
import de.adorsys.docusafe.business.types.DocumentFQN;
import de.adorsys.docusafe.service.api.types.DocumentContent;
import de.adorsys.docusafe.service.api.types.UserID;
import org.springframework.util.StopWatch;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Random;

/**
 * Created by peter on 27.11.18 15:39.
 */
public class TestUtil {
    public static String getUniqueStringForDocument(DocumentFQN documentFQN, UserID userID) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss.SSS");
        String timestamp = sdf.format(new Date());
        String fullName = documentFQN.getValue();
        String uniqueToken = wrap(userID.getValue()) + wrap(fullName) + wrap(timestamp);
        return uniqueToken;
    }

    public static DocumentContent createDocumentContent(Integer sizeOfDocument, DocumentFQN documentFQN, String uniqueToken) {
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

    public static ReadDocumentResult checkDocumentWasRead(DSDocument dsDocument, DocumentInfo documentInfo) {
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

    public static ReadDocumentResult checkDocumentExsits(Boolean exists, DocumentInfo documentInfo) {
        ReadDocumentResult readDocumentResult = new ReadDocumentResult();
        readDocumentResult.documentFQN = documentInfo.documentFQN;
        readDocumentResult.readResult = exists ? ReadResult.OK : ReadResult.NOT_FOUND;
        return readDocumentResult;
    }

    public static ReadDocumentResult checkDocumentDeleted(DocumentInfo documentInfo) {
        ReadDocumentResult readDocumentResult = new ReadDocumentResult();
        readDocumentResult.documentFQN = documentInfo.documentFQN;
        readDocumentResult.readResult = ReadResult.OK;
        return readDocumentResult;
    }

    public static  void addStopWatchToTestsResult(StopWatch stopWatch, TestsResult testsResult) {
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

    public static  void addCreatedDocumentsToTestResults(List<DocumentInfo> createdDocuments, TestsResult testsResult) {
        testsResult.listOfCreatedDocuments = createdDocuments.toArray(new DocumentInfo[createdDocuments.size()]);
    }

    public static  void addReadDocumentsToTestResults(List<ReadDocumentResult> readDocuments, TestsResult testsResult) {
        testsResult.listOfReadDocuments = readDocuments.toArray(new ReadDocumentResult[readDocuments.size()]);
    }

    public static  void addFoundDocumentsToTestResults(List<DocumentFQN> foundDocuments, TestsResult testsResult) {
        testsResult.listOfFoundDocuments = foundDocuments.toArray(new DocumentFQN[foundDocuments.size()]);
    }

    private static String wrap(String content) {
        return "(" + content + ")";
    }


}

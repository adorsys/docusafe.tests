package de.adorsys.docusafe.rest.types;


import de.adorsys.docusafe.business.types.DocumentFQN;
import de.adorsys.docusafe.service.api.types.UserID;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;

/**
 * Created by peter on 22.08.18 at 08:27.
 */
public class TestsResult {
    public String date;
    public String dfsConnectionString;
    public String threadName;
    public Long totalTime = null;
    public TaskInfo[] tasks = null;
    public UserID userID = null;
    public DocumentInfo[] listOfCreatedDocuments = null;
    public ReadDocumentResult[] listOfReadDocuments = null;
    public DocumentFQN[] listOfFoundDocuments = null;

    public TestsResult() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd-HH:mm:ss.SSS");
        this.date = sdf.format(new Date());
        this.threadName = Thread.currentThread().getName();
    }

    public static class TaskInfo {
        public String name;
        public Long time;

        @Override
        public String toString() {
            return "TaskInfo{" +
                    "name='" + name + '\'' +
                    ", time=" + time +
                    '}';
        }
    }

    @Override
    public String toString() {
        return "TestsResult{" +
                "date='" + date + '\'' +
                ", extendedStoreConnection='" + dfsConnectionString + '\'' +
                ", threadName='" + threadName + '\'' +
                ", totalTime=" + totalTime +
                ", tasks=" + Arrays.toString(tasks) +
                ", userID=" + userID +
                ", listOfCreatedDocuments=" + Arrays.toString(listOfCreatedDocuments) +
                ", listOfReadDocuments=" + Arrays.toString(listOfReadDocuments) +
                ", listOfFoundDocuments=" + Arrays.toString(listOfFoundDocuments) +
                '}';
    }
}

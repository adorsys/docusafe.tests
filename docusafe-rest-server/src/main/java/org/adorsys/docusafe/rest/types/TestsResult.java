package org.adorsys.docusafe.rest.types;

import org.adorsys.docusafe.business.types.UserID;
import org.adorsys.docusafe.business.types.complex.DocumentFQN;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by peter on 22.08.18 at 08:27.
 */
public class TestsResult {
    public String date;
    public String extendedStoreConnection;
    public long totalTime;
    public TaskInfo[] tasks;
    public UserID userID;
    public CreatedDocument[] listOfCreatedDocuments;


    public TestsResult() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd-HH:mm:ss");
        this.date = sdf.format(new Date());
    }

    public static class TaskInfo {
        public String name;
        public long time;

        @Override
        public String toString() {
            return "TaskInfo{" +
                    "name='" + name + '\'' +
                    ", time=" + time +
                    '}';
        }
    }

    public static class CreatedDocument {
        public DocumentFQN documentFQN;
        public String uniqueToken;
        public int size;
    }

    @Override
    public String toString() {
        return "TestsResult{" +
                ", extendedStoreConnection='" + extendedStoreConnection + '\'' +
                ", totalTime=" + totalTime +
                // ", tasks=" + Arrays.toString(tasks) +
                ", # of tasks=" + tasks.length +
                '}';
    }
}

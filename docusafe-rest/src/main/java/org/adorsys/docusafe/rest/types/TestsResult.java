package org.adorsys.docusafe.rest.types;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by peter on 22.08.18 at 08:27.
 */
public class TestsResult {
    public String date;
    public TestParameter request;
    public String extendedStoreConnection;
    public long totalTime;
    public TaskInfo[] tasks;

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

    @Override
    public String toString() {
        return "TestsResult{" +
                "request=" + request +
                ", extendedStoreConnection='" + extendedStoreConnection + '\'' +
                ", totalTime=" + totalTime +
                // ", tasks=" + Arrays.toString(tasks) +
                ", # of tasks=" + tasks.length +
                '}';
    }
}

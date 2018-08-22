package org.adorsys.docusafe.rest.types;

import java.util.Arrays;

/**
 * Created by peter on 22.08.18 at 08:27.
 */
public class TestsResult {
    public TestParameter request;
    public String extendedStoreConnection;
    public long totalTime;
    public TaskInfo[] tasks;


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
                ", tasks=" + Arrays.toString(tasks) +
                '}';
    }
}

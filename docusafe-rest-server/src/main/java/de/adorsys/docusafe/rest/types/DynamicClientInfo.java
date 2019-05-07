package de.adorsys.docusafe.rest.types;

public class DynamicClientInfo {
    public int repetitionNumber;
    public String testID;
    public String requestID;

    @Override
    public String toString() {
        return "DynamicClientInfo{" +
                "repetitionNumber=" + repetitionNumber +
                ", testID='" + testID + '\'' +
                ", requestID='" + requestID + '\'' +
                '}';
    }
}

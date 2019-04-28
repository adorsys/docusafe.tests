package de.adorsys.docusafe.rest.types;


import de.adorsys.docusafe.business.types.DocumentFQN;

/**
 * Created by peter on 06.09.18 at 08:59.
 */
public class DocumentInfo {
        public DocumentFQN documentFQN;
        public String uniqueToken;
        public Integer size;

        @Override
        public String toString() {
                return "DocumentInfo{" +
                        "documentFQN=" + documentFQN +
                        ", uniqueToken='" + uniqueToken + '\'' +
                        ", size=" + size +
                        '}';
        }
}

package de.adorsys.docusafe.rest.types;

import de.adorsys.docusafe.business.types.DFSCredentials;

import java.util.HashMap;
import java.util.Map;

public class AvailableDFSConfigs {
    Map<String, DFSCredentials> map = new HashMap<>();

    public void addDFSConfig(String name, DFSCredentials dfsCredentials) {
        map.put(name, dfsCredentials);
    }

    public Map<String, DFSCredentials> getMap() {
        return map;
    }
}

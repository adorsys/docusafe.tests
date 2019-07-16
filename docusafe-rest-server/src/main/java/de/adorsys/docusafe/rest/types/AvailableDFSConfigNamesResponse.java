package de.adorsys.docusafe.rest.types;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class AvailableDFSConfigNamesResponse {
    List<String> avalailabeNames = new ArrayList<>();

    public void addDFSName(String name) {
        avalailabeNames.add(name);
    }
}

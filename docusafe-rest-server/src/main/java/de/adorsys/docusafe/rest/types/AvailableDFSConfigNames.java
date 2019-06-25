package de.adorsys.docusafe.rest.types;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class AvailableDFSConfigNames {
    List<String> avalialbeNames = new ArrayList<>();

    public void addDFSName(String name) {
        avalialbeNames.add(name);
    }
}

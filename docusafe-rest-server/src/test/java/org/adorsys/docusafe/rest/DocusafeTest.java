package org.adorsys.docusafe.rest;


import org.adorsys.docusafe.rest.lombokstuff.DataHolderToTestLombokAndAspects;
import org.junit.Assert;
import org.junit.Test;

/**
 * Created by peter on 12.11.18 08:53.
 */

public class DocusafeTest {
    @Test
    public void a() {
        DataHolderToTestLombokAndAspects d = new DataHolderToTestLombokAndAspects();
        d.setName("a");
        Assert.assertEquals("a", d.getName());
    }
}

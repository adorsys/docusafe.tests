package org.adorsys.docusafe.rest.controller;

import de.adorsys.dfs.connection.api.complextypes.BucketPathUtil;
import org.adorsys.docusafe.cached.transactional.CachedTransactionalDocumentSafeService;
import org.adorsys.docusafe.transactional.TransactionalDocumentSafeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by peter on 02.10.18.
 */
@RestController
public class DocusafeWithSpringAnnotationController {
    private final static Logger LOGGER = LoggerFactory.getLogger(DocusafeWithSpringAnnotationController.class);
    private final static String APPLICATION_JSON = "application/json";

    @Autowired
    de.adorsys.dfs.connection.api.service.api.DFSConnection DFSConnection;

    @Autowired
    CachedTransactionalDocumentSafeService cachedTransactionalDocumentSafeService;

    @Autowired
    TransactionalDocumentSafeService transactionalDocumentSafeService;

    @RequestMapping(
            value = "/springget",
            method = {RequestMethod.GET},
            produces = {APPLICATION_JSON}
    )
    public
    @ResponseBody
    ResponseEntity<String> springGet() {
        LOGGER.info("springGet");
        StringBuilder sb = new StringBuilder();
        sb.append("extended store connection found :");
        sb.append(DFSConnection != null ? true : false);
        sb.append("\n");

        sb.append("cacehd transactional docusafe found :");
        sb.append(cachedTransactionalDocumentSafeService != null ? true : false);
        sb.append("\n");

        sb.append("transactional docusafe found :");
        sb.append(transactionalDocumentSafeService != null ? true : false);
        sb.append("\n");

        if (DFSConnection != null) {
            sb.append("\n");
            sb.append(DFSConnection.getClass().getName());
            sb.append("\n");
        }
        if (cachedTransactionalDocumentSafeService != null) {
            sb.append(cachedTransactionalDocumentSafeService.getClass().getName());
            sb.append("\n");
        }
        if (transactionalDocumentSafeService != null) {
            sb.append(transactionalDocumentSafeService.getClass().getName());
            sb.append("\n");
        }

        String message = sb.toString();
        LOGGER.debug(message);
        return new ResponseEntity<String>(message, HttpStatus.OK);
    }

}

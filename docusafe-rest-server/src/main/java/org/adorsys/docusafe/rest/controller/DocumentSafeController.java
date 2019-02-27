package org.adorsys.docusafe.rest.controller;

import io.swagger.annotations.ApiOperation;
import org.adorsys.cryptoutils.exceptions.BaseExceptionHandler;
import org.adorsys.docusafe.business.DocumentSafeService;
import org.adorsys.docusafe.business.impl.DocumentSafeServiceImpl;
import org.adorsys.docusafe.business.types.UserID;
import org.adorsys.docusafe.business.types.complex.DSDocument;
import org.adorsys.docusafe.business.types.complex.DSDocumentStream;
import org.adorsys.docusafe.business.types.complex.DocumentDirectoryFQN;
import org.adorsys.docusafe.business.types.complex.DocumentFQN;
import org.adorsys.docusafe.business.types.complex.UserIDAuth;
import org.adorsys.docusafe.rest.types.MoveFromInbox;
import org.adorsys.docusafe.rest.types.MoveToInboxOfUser;
import org.adorsys.docusafe.spring.annotation.UseDocusafeSpringConfiguration;
import org.adorsys.encobject.complextypes.BucketPath;
import org.adorsys.encobject.domain.ReadKeyPassword;
import org.adorsys.encobject.service.api.ExtendedStoreConnection;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.HandlerMapping;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by peter on 22.01.18 at 19:27.
 * UserIDAuth wird natürlich kein expliziter Parameter sein. Aber die JWT Logik kommt
 * erst im zweiten Schritt. Jetzt erst mal loslegen mit explizitem Parameter.
 */
@RestController
@UseDocusafeSpringConfiguration
public class DocumentSafeController {
    private final static String APPLICATION_JSON = "application/json";
    private final static String APPLICATION_OCTET_STREAM = "application/octet-stream";

    private final static Logger LOGGER = LoggerFactory.getLogger(DocumentSafeController.class);

    @Autowired
    private ExtendedStoreConnection connection;
    private DocumentSafeService service;

    @PostConstruct
    private void postconstruction() {
        service = new DocumentSafeServiceImpl(connection);
    }
    /**
     * USER
     * ===========================================================================================
     */
    @RequestMapping(
            value = "/internal/user",
            method = {RequestMethod.PUT},
            consumes = {APPLICATION_JSON},
            produces = {APPLICATION_JSON}
    )
    public void createUser(@RequestBody UserIDAuth userIDAuth) {
        LOGGER.debug("create user " + userIDAuth.getUserID());
        service.createUser(userIDAuth);
    }

    @RequestMapping(
            value = "/internal/user",
            method = {RequestMethod.DELETE}
    )
    public void destroyUser(@RequestHeader("userid") String userid,
                            @RequestHeader("password") String password) {
        LOGGER.info("************************************************");
        LOGGER.debug("delete user " + userid);
        service.destroyUser(new UserIDAuth(new UserID(userid), new ReadKeyPassword(password)));
    }

    @RequestMapping(
            value = "/internal/user/{UserID}",
            method = {RequestMethod.GET}
    )
    public
    @ResponseBody
    ResponseEntity<Boolean> userExists(@PathVariable("UserID") String userIDString) {
        UserID userID = new UserID(userIDString);
        LOGGER.debug("get user exists: " + userID);
        if (!service.userExists(userID)) {
            LOGGER.debug(userID + " does not exist");
            return new ResponseEntity(HttpStatus.NOT_FOUND);
        }
        LOGGER.debug(userID + " exists");
        return new ResponseEntity<>(Boolean.TRUE, HttpStatus.OK);
    }

    /**
     * DOCUMENT
     * ===========================================================================================
     */

    /**
     * -- byte orientiert --
     */
    @ApiOperation(value="creates a document", notes = "The DocumentContent is a string but must be in HEX Digits, et \"AFFE\" is valid, but \"Hello\" isnt.")
    @RequestMapping(
            value = "/document",
            method = {RequestMethod.PUT},
            consumes = {APPLICATION_JSON}
    )
    public void storeDocument(@RequestHeader("userid") String userid,
                              @RequestHeader("password") String password,
                              @RequestBody DSDocument dsDocument) {
        UserIDAuth userIDAuth = new UserIDAuth(new UserID(userid), new ReadKeyPassword(password));
        service.storeDocument(userIDAuth, dsDocument);
    }

    @ApiOperation(value="reads a document", notes = "the swagger api does not allow a path variable after the document path yet - will be fixed soon.")
    @RequestMapping(
            value = "/document/**",
            method = {RequestMethod.GET},
            consumes = {APPLICATION_JSON},
            produces = {APPLICATION_JSON}
    )
    public
    @ResponseBody
    ResponseEntity<DSDocument> readDocument(@RequestHeader("userid") String userid,
                                            @RequestHeader("password") String password,
                                            HttpServletRequest request
    ) {
        LOGGER.debug("get document request arrived");
        UserIDAuth userIDAuth = new UserIDAuth(new UserID(userid), new ReadKeyPassword(password));
        DocumentFQN documentFQN = new DocumentFQN(getFQN(request));
        if (!service.documentExists(userIDAuth, documentFQN)) {
            LOGGER.debug("document " + documentFQN + " does not exist");
            return new ResponseEntity(HttpStatus.NOT_FOUND);
        }
        LOGGER.debug("document " + documentFQN + " exists");
        return new ResponseEntity<>(service.readDocument(userIDAuth, documentFQN), HttpStatus.OK);
    }

    /**
     * -- stream orientiert --
     */
    @RequestMapping(
            value = "/documentstream",
            method = {RequestMethod.PUT},
            consumes = {APPLICATION_OCTET_STREAM}
    )
    public void storeDocumentStream(@RequestHeader("userid") String userid,
                                    @RequestHeader("password") String password,
                                    @RequestHeader("documentFQN") String documentFQNString,
                                    InputStream inputStream) {
        UserIDAuth userIDAuth = new UserIDAuth(new UserID(userid), new ReadKeyPassword(password));
        DocumentFQN documentFQN = new DocumentFQN(documentFQNString);
        LOGGER.debug("input auf document/stream for " + userIDAuth);
        service.storeDocumentStream(userIDAuth, new DSDocumentStream(documentFQN, inputStream));
    }

    @RequestMapping(
            value = "/documentstream/**",
            method = {RequestMethod.GET},
            produces = {APPLICATION_OCTET_STREAM}

    )
    public ResponseEntity readDocumentStream(@RequestHeader("userid") String userid,
                                             @RequestHeader("password") String password,
                                             HttpServletRequest request,
                                             HttpServletResponse response
    ) {
        try {
            LOGGER.debug("get stream request arrived1");
            UserIDAuth userIDAuth = new UserIDAuth(new UserID(userid), new ReadKeyPassword(password));
            DocumentFQN documentFQN = new DocumentFQN(getFQN(request));
            LOGGER.debug("received:" + userIDAuth + " and " + documentFQN);

            if (!service.documentExists(userIDAuth, documentFQN)) {
                LOGGER.debug("documentstream " + documentFQN + " does not exist");
                return new ResponseEntity(HttpStatus.NOT_FOUND);
            }

            DSDocumentStream stream = service.readDocumentStream(userIDAuth, documentFQN);
            InputStream is = stream.getDocumentStream();
            OutputStream os = response.getOutputStream();
            LOGGER.debug("start copy imputstream to outputstream");
            IOUtils.copy(is, os);
            LOGGER.debug("finished copy imputstream to outputstream");
            IOUtils.closeQuietly(is);
            IOUtils.closeQuietly(os);
            LOGGER.debug("return outputstream to sender");
            return new ResponseEntity(HttpStatus.OK);
        } catch (Exception e) {
            throw BaseExceptionHandler.handle(e);
        }
    }

    /**
     * -- content art unabhängig --
     */
    @RequestMapping(
            value = "/document/**",
            method = {RequestMethod.DELETE},
            consumes = {APPLICATION_JSON},
            produces = {APPLICATION_JSON}
    )
    public void destroyDocument(@RequestHeader("userid") String userid,
                                @RequestHeader("password") String password,
                                HttpServletRequest request
    ) {
        LOGGER.debug("destroy document request arrived");
        UserIDAuth userIDAuth = new UserIDAuth(new UserID(userid), new ReadKeyPassword(password));
        String pathToDelete = getFQN(request);
        if (pathToDelete.endsWith(BucketPath.BUCKET_SEPARATOR)) {
            DocumentDirectoryFQN documentDirectoryFQN = new DocumentDirectoryFQN(pathToDelete.substring(0,pathToDelete.length()-1));
            LOGGER.debug("destroy document folder " + documentDirectoryFQN);
            service.deleteFolder(userIDAuth, documentDirectoryFQN);
        } else {
            service.deleteDocument(userIDAuth, new DocumentFQN(pathToDelete));
        }
        LOGGER.debug("destroy document request finished");
    }


    /**
     * INBOX STUFF
     * ===========================================================================================
     */
    @RequestMapping(
            value = "/inbox/in",
            method = {RequestMethod.PUT},
            consumes = {APPLICATION_JSON},
            produces = {APPLICATION_JSON}
    )
    public void moveDocumnetToInboxOfUser(@RequestHeader("userid") String userid,
                            @RequestHeader("password") String password,
                            @RequestBody MoveToInboxOfUser moveRequest) {
        UserIDAuth userIDAuth = new UserIDAuth(new UserID(userid), new ReadKeyPassword(password));
        service.moveDocumnetToInboxOfUser(userIDAuth, moveRequest.getReceivingUser(), moveRequest.getSourceFQN(), moveRequest.getInboxFQN(), moveRequest.getMoveType());
    }

    @RequestMapping(
            value = "/inbox/out",
            method = {RequestMethod.PUT},
            consumes = {APPLICATION_JSON},
            produces = {APPLICATION_JSON}
    )
    public void moveDocumentFromInbox(@RequestHeader("userid") String userid,
                                     @RequestHeader("password") String password,
                                     @RequestBody MoveFromInbox moveRequest) {
        UserIDAuth userIDAuth = new UserIDAuth(new UserID(userid), new ReadKeyPassword(password));
        service.moveDocumentFromInbox(userIDAuth, moveRequest.getInboxFQN(), moveRequest.getDestFQN(), moveRequest.getOverwriteFlag());
    }

    @RequestMapping(
            value = "/inbox/list",
            method = {RequestMethod.GET},
            consumes = {APPLICATION_JSON},
            produces = {APPLICATION_JSON}
    )
    public ResponseEntity moveDocumentFromInbox(@RequestHeader("userid") String userid,
                                      @RequestHeader("password") String password) {
        UserIDAuth userIDAuth = new UserIDAuth(new UserID(userid), new ReadKeyPassword(password));
        return new ResponseEntity<>(service.listInbox(userIDAuth), HttpStatus.OK);
    }

    private String getFQN(HttpServletRequest request) {
        final String path = request.getAttribute(HandlerMapping.PATH_WITHIN_HANDLER_MAPPING_ATTRIBUTE).toString();
        final String bestMatchingPattern = request.getAttribute(HandlerMapping.BEST_MATCHING_PATTERN_ATTRIBUTE).toString();
        final String documentFQNStringWithQuotes = new AntPathMatcher().extractPathWithinPattern(bestMatchingPattern, path);
        return documentFQNStringWithQuotes.replaceAll("\"", "");
    }
}

package org.adorsys.docusafe.rest.exceptions;

import de.adorsys.common.exceptions.BaseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

/**
 * Created by peter on 05.02.18 at 12:10.
 * see https://restpatterns.mindtouch.us/HTTP_Status_Codes
 *
 */
@ControllerAdvice
public class SpringRestExceptionHandler  extends ResponseEntityExceptionHandler {
    private final static Logger LOGGER = LoggerFactory.getLogger(SpringRestExceptionHandler.class);

    @ExceptionHandler(value = { BaseException.class })
    protected ResponseEntity<Object> handleConflict(BaseException ex, WebRequest request) {
        return handleExceptionInternal(ex, new RestError(ex.getClass().getSimpleName() + " " + ex.getMessage()),
                new HttpHeaders(), HttpStatus.CONFLICT, request);
    }

    @ExceptionHandler(value = { Exception.class })
    protected ResponseEntity<Object> handleConflict(Exception ex, WebRequest request) {
        BaseException e = new BaseException("CATCHED IN SpringRestExceptionHandler", ex);
        return handleExceptionInternal(ex, new RestError(ex.getClass().getSimpleName() + " " + e.getMessage()),
                new HttpHeaders(), HttpStatus.CONFLICT, request);
    }

}

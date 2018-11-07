package org.adorsys.docusafe.rest.aspects;

import org.adorsys.docusafe.business.exceptions.UserIDAlreadyExistsException;
import org.adorsys.docusafe.business.types.complex.UserIDAuth;
import org.aspectj.lang.ProceedingJoinPoint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by peter on 07.11.18 16:13.
 */
public aspect AspectWrapperDocumentSafe {
    private final static Logger LOGGER = LoggerFactory.getLogger(AspectWrapperDocumentSafe.class);
    pointcut serviceMethods(): execution(* *..CachedTransactionalDocumentSafeService.*(..));
    Object around(ProceedingJoinPoint joinPoint): serviceMethods() {
        UserIDAuth userIDAuth = null;
        Object[] args = joinPoint.getArgs();
        for (Object arg : args) {
            if (arg instanceof UserIDAuth) {
                userIDAuth = (UserIDAuth) arg;
            }
        }
        String user = userIDAuth != null ? userIDAuth.getUserID().getValue() : "<unknown>";
        LOGGER.info(String.format("============================================= \"%s\" %s", thisJoinPointStaticPart.getSignature(), user));
        long start = System.currentTimeMillis();
        Object result = null;
        RuntimeException throwable = null;

        try {
            result = proceed();
        } catch (RuntimeException t) {
            throwable = t;
        }
        long end = System.currentTimeMillis();
        if (throwable != null) {
            LOGGER.info(String.format("============================================= \"%s\" time: %d ms finished with exception: %s message: %s", thisJoinPointStaticPart.getSignature(),
                    (end - start),
                    throwable.getClass(), throwable.getMessage()));
            throw throwable;
        }
        LOGGER.info(String.format("============================================= \"%s\" time: %d ms finished", thisJoinPointStaticPart.getSignature(),
                (end - start)));
        return result;
    }

}

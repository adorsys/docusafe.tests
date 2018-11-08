package org.adorsys.docusafe.rest.aspects;

import org.adorsys.cryptoutils.exceptions.BaseException;
import org.adorsys.docusafe.business.types.complex.UserIDAuth;
import org.adorsys.docusafe.rest.lombokstuff.DataHolderToTestLombokAndAspects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by peter on 07.11.18 16:13.
 */
public aspect AspectWrapperDocumentSafe {
    private final static Logger LOGGER = LoggerFactory.getLogger(AspectWrapperDocumentSafe.class);
    pointcut serviceMethods(): execution(* org.adorsys.docusafe.cached.transactional.CachedTransactionalDocumentSafeService.*(..));
    Object around(): serviceMethods() {
        DataHolderToTestLombokAndAspects d = new DataHolderToTestLombokAndAspects();
        Object[] args = thisJoinPoint.getArgs();
        for (Object arg : args) {
            if (arg instanceof UserIDAuth) {
                d.setName(((UserIDAuth) arg).getUserID().getValue());
            }
        }

        LOGGER.info(String.format("============================================= (%s) \"%s\"", d.getName(), thisJoinPointStaticPart.getSignature()));
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
            LOGGER.info(String.format("============================================= (%s) \"%s\" time: %d ms finished with exception: %s message: %s",d.getName(), thisJoinPointStaticPart.getSignature(),
                    (end - start),
                    throwable.getClass(), throwable.getMessage()));
            throw throwable;
        }
        LOGGER.info(String.format("============================================= (%s) \"%s\" time: %d ms finished", d.getName(), thisJoinPointStaticPart.getSignature(),
                (end - start)));
        return result;
    }
}

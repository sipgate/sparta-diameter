package com.sipgate.sparta.diameter.core.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation for Diameter response/answer message classes to specify their command code.
 * This annotation is retained at runtime to enable auto-discovery of answer classes.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface DiameterResponse {
    /**
     * The Diameter command code for this response message.
     *
     * @return the command code
     */
    int value();
}

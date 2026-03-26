package com.sipgate.sparta.diameter.base.core.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation for Diameter request message classes to specify their command code.
 * This annotation is retained at runtime to enable auto-discovery of request classes.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface DiameterRequest {
    /**
     * The Diameter command code for this request message.
     *
     * @return the command code
     */
    int value();
}

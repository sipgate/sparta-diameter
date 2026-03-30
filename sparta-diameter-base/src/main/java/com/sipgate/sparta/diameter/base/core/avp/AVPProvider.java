package com.sipgate.sparta.diameter.base.core.avp;

import java.util.Collection;

/**
 * Interface for providing AVP definitions to the AVP factory.
 * Implementations can register their protocol-specific AVPs by providing definitions.
 */
public interface AVPProvider {

    /**
     * Returns all AVP definitions provided by this provider.
     *
     * @return Collection of AVP definitions
     */
    Collection<AVPDefinition> getDefinitions();

}

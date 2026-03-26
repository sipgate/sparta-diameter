package com.sipgate.sparta.diameter.base.core.avp.mixins;

import com.sipgate.sparta.diameter.base.core.avp.AVP;
import com.sipgate.sparta.diameter.base.core.DiameterConstants;
import com.sipgate.sparta.diameter.base.core.avp.AVPContainer;

/**
 * Interface for Diameter messages that include Product-Name AVP.
 * <p>
 * This interface provides default implementations for handling the Product-Name AVP
 * as defined in RFC 6733. The Product-Name AVP contains the name of the Diameter stack.
 * </p>
 */
public interface HasProductNameAVP<T extends HasProductNameAVP<T>> extends AVPContainer<T> {

    /**
     * Sets the Product-Name AVP.
     *
     * @param productName The product name to set.
     */
    default T setProductName(final String productName) {
        setAVP(AVP.create(DiameterConstants.AVP_PRODUCT_NAME, productName));
        return self();
    }

    /**
     * Gets the Product-Name from this message.
     *
     * @return The product name, or null if not found.
     */
    default String getProductName() {
        final AVP productNameAVP = findAVP(DiameterConstants.AVP_PRODUCT_NAME);
        if (productNameAVP != null) {
            return productNameAVP.getDataAsString();
        }
        return null;
    }
}

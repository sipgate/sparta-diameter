package com.sipgate.sparta.diameter.session;

import com.sipgate.sparta.diameter.core.DiameterConstants;
import com.sipgate.sparta.diameter.core.avp.AVP;
import com.sipgate.sparta.diameter.core.avp.AVPContainer;
import com.sipgate.sparta.diameter.messages.rfc6733.CapabilitiesExchangeAnswer;
import com.sipgate.sparta.diameter.messages.rfc6733.CapabilitiesExchangeRequest;
import com.sipgate.sparta.diameter.transport.DiameterConnectionListener;
import com.sipgate.sparta.diameter.transport.DiameterPeer;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;

/**
 * Shared state and helpers for initiator- and responder-side Diameter sessions.
 * <p>
 * Holds the fields common to both sides (config, peer, peer state, watchdog state,
 * capability negotiator) and provides utility methods for building CER/CEA messages
 * and extracting AVP values. Concrete subclasses implement the side-specific
 * connection lifecycle.
 * </p>
 */
abstract class DiameterSession implements DiameterConnectionListener {

    protected final DiameterNodeConfig config;
    protected final CapabilityNegotiator negotiator;

    protected PeerState peerState;
    protected WatchdogState watchdogState;
    protected DiameterPeer peer;

    DiameterSession(final DiameterNodeConfig config) {
        this.config = config;
        this.negotiator = new CapabilityNegotiator();
        this.peerState = PeerState.CLOSED;
        this.watchdogState = WatchdogState.INITIAL;
    }

    @Override
    public void onDisconnected(final DiameterPeer peer) {
        this.peerState = PeerState.CLOSED;
        this.watchdogState = WatchdogState.DOWN;
    }

    PeerState getPeerState() {
        return peerState;
    }

    WatchdogState getWatchdogState() {
        return watchdogState;
    }

    /**
     * Populates origin, host IP, vendor, product, and application AVPs on a CER
     * from the local node config.
     */
    protected void populateCapabilityAvps(final CapabilitiesExchangeRequest cer) {
        cer.setOriginHost(config.getOriginHost());
        cer.setOriginRealm(config.getOriginRealm());
        cer.setVendorId(config.getVendorId());
        cer.setProductName(config.getProductName());
        addMultiValueAvps(cer);
    }

    /**
     * Populates origin, host IP, vendor, product, and application AVPs on a CEA
     * from the local node config.
     */
    protected void populateCapabilityAvps(final CapabilitiesExchangeAnswer cea) {
        cea.setOriginHost(config.getOriginHost());
        cea.setOriginRealm(config.getOriginRealm());
        cea.setVendorId(config.getVendorId());
        cea.setProductName(config.getProductName());
        addMultiValueAvps(cea);
    }

    private void addMultiValueAvps(final AVPContainer<?> msg) {
        for (final InetAddress addr : config.getHostIpAddresses()) {
            msg.addAVP(AVP.create(DiameterConstants.AVP_HOST_IP_ADDRESS, addr));
        }

        for (final Long vendorId : config.getCapabilities().getSupportedVendorIds()) {
            msg.addAVP(AVP.create(DiameterConstants.AVP_SUPPORTED_VENDOR_ID, vendorId));
        }

        for (final Integer authId : config.getCapabilities().getAuthApplicationIds()) {
            msg.addAVP(AVP.create(DiameterConstants.AVP_AUTH_APPLICATION_ID, Integer.toUnsignedLong(authId)));
        }

        for (final Integer acctId : config.getCapabilities().getAcctApplicationIds()) {
            msg.addAVP(AVP.create(DiameterConstants.AVP_ACCT_APPLICATION_ID, Integer.toUnsignedLong(acctId)));
        }
    }

    /**
     * Reads all AVPs with the given code and returns their values as unsigned 32-bit integers.
     */
    protected static List<Long> extractUnsignedInts(final List<AVP> avps) {
        final List<Long> result = new ArrayList<>();
        for (final AVP avp : avps) {
            result.add(avp.getDataAsUnsignedInt());
        }
        return result;
    }
}

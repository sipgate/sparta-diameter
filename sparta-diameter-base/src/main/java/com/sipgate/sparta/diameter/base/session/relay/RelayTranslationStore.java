package com.sipgate.sparta.diameter.base.session.relay;

import com.sipgate.sparta.diameter.base.core.HopByHopId;

import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * The relay translation table of one egress session: outbound hop-by-hop identifier →
 * {@link RelayTranslation}.
 *
 * <p>Keyed by the hop-by-hop identifier the egress session minted for the forwarded
 * request, so lookups on the answer path are a single map access. Every entry is
 * paired with an entry in the egress session's pending-request map under the same
 * key; consequently no reserved hop-by-hop identifier range is needed (identifiers
 * are unique per connection by construction, RFC 6733 §3) and no extra cleanup timer
 * is required: the per-request timeout task that the session already schedules on the
 * peer's event loop fails the pending request on expiry, which removes the
 * translation as well. Write failures and connection loss clean up the same way.</p>
 */
public final class RelayTranslationStore {

    private final ConcurrentHashMap<HopByHopId, RelayTranslation> translations = new ConcurrentHashMap<>();

    /**
     * Registers the translation for a forwarded request.
     *
     * @param outboundHopByHop the fresh hop-by-hop identifier of the forwarded request
     * @param translation      what to restore when the downstream answer arrives
     * @throws NullPointerException if an argument is {@code null}
     */
    public void put(final HopByHopId outboundHopByHop, final RelayTranslation translation) {
        Objects.requireNonNull(outboundHopByHop, "outboundHopByHop must not be null");
        Objects.requireNonNull(translation, "translation must not be null");
        translations.put(outboundHopByHop, translation);
    }

    /**
     * Returns the translation for the given identifier without removing it
     * (diagnostics and tests); use {@link #remove(HopByHopId)} on the answer path so
     * a duplicate answer cannot trigger a second restoration.
     *
     * @return the translation, or {@code null} if none is registered
     */
    public RelayTranslation lookup(final HopByHopId outboundHopByHop) {
        Objects.requireNonNull(outboundHopByHop, "outboundHopByHop must not be null");
        return translations.get(outboundHopByHop);
    }

    /**
     * Atomically removes and returns the translation for the given identifier.
     *
     * @return the removed translation, or {@code null} if none was registered
     */
    public RelayTranslation remove(final HopByHopId outboundHopByHop) {
        Objects.requireNonNull(outboundHopByHop, "outboundHopByHop must not be null");
        return translations.remove(outboundHopByHop);
    }

    /**
     * @return {@code true} if a translation is registered for the given identifier
     */
    public boolean contains(final HopByHopId outboundHopByHop) {
        Objects.requireNonNull(outboundHopByHop, "outboundHopByHop must not be null");
        return translations.containsKey(outboundHopByHop);
    }

    /**
     * @return the number of registered translations
     */
    public int size() {
        return translations.size();
    }
}

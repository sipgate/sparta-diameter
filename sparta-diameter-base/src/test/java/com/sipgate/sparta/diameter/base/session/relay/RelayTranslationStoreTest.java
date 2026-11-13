package com.sipgate.sparta.diameter.base.session.relay;

import com.sipgate.sparta.diameter.base.core.EndToEndId;
import com.sipgate.sparta.diameter.base.core.HopByHopId;
import com.sipgate.sparta.diameter.base.transport.DiameterPeer;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

class RelayTranslationStoreTest {

    private static RelayTranslation translation() {
        return new RelayTranslation(mock(DiameterPeer.class), new HopByHopId(1), new EndToEndId(2), 42L);
    }

    @Test
    void it_looks_up_a_translation_by_outbound_hop_by_hop() {
        // GIVEN
        final RelayTranslationStore store = new RelayTranslationStore();
        final HopByHopId outbound = new HopByHopId(1234);
        final RelayTranslation registered = translation();

        // WHEN
        store.put(outbound, registered);

        // THEN — lookup is a peek: the entry stays registered
        assertThat(store.lookup(outbound)).isSameAs(registered);
        assertThat(store.contains(outbound)).isTrue();
        assertThat(store.size()).isEqualTo(1);
    }

    @Test
    void it_distinguishes_entries_by_outbound_hop_by_hop() {
        // GIVEN
        final RelayTranslationStore store = new RelayTranslationStore();
        final RelayTranslation first = translation();
        final RelayTranslation second = translation();
        store.put(new HopByHopId(1), first);
        store.put(new HopByHopId(2), second);

        // WHEN / THEN
        assertThat(store.lookup(new HopByHopId(1))).isSameAs(first);
        assertThat(store.lookup(new HopByHopId(2))).isSameAs(second);
        assertThat(store.size()).isEqualTo(2);
    }

    @Test
    void it_removes_destructively_on_the_answer_path() {
        // GIVEN
        final RelayTranslationStore store = new RelayTranslationStore();
        final HopByHopId outbound = new HopByHopId(99);
        final RelayTranslation registered = translation();
        store.put(outbound, registered);

        // WHEN — the downstream answer arrived
        final RelayTranslation removed = store.remove(outbound);

        // THEN — restored once; a duplicate answer finds nothing
        assertThat(removed).isSameAs(registered);
        assertThat(store.remove(outbound)).isNull();
        assertThat(store.lookup(outbound)).isNull();
        assertThat(store.contains(outbound)).isFalse();
        assertThat(store.size()).isZero();
    }

    @Test
    void it_reports_empty_for_unknown_identifiers() {
        // GIVEN
        final RelayTranslationStore store = new RelayTranslationStore();

        // WHEN / THEN
        assertThat(store.lookup(new HopByHopId(7))).isNull();
        assertThat(store.remove(new HopByHopId(7))).isNull();
        assertThat(store.contains(new HopByHopId(7))).isFalse();
    }
}

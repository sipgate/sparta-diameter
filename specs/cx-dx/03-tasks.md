# Cx/Dx Interface Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Implement the Cx/Dx Diameter interface (SAR/SAA, MAR/MAA, RTR/RTA) in sparta-diameter so sparta-hss can serve IMS registration, multimedia authentication and location info.

**Architecture:** Mirror the SGd/Gdd module pattern (`Constants` → `AVPProvider` → `messages/` → `MessageFactory` → `mixins/`). Foreign-namespace AVPs (RFC 5090 Digest, RFC 7155 Framed, ETSI ES 283 035 Line-Identifier) get their own definition-only modules. Grouped AVPs stay flat (no child accessors); only message-direct AVPs get mixins. Providers/factories are auto-discovered via the Reflections scan over `com.sipgate.sparta.diameter`.

**Tech Stack:** Java 17, Maven (no wrapper), JUnit 5, AssertJ. See `specs/cx-dx/01-requirements.md` and `02-design.md`; ETSI/RFC sources in `docs/specs/etsi/` and rfc-editor.org.

**Conventions (AGENTS.md):** `final` on every field/param/local; for-loops only in production code; tests named `it_<behavior>`, GIVEN/WHEN/THEN blocks, AssertJ only, instance named by role (`provider`, `factory`, `command`).

**Reference files to copy patterns from:**
- Module skeleton: `sparta-diameter-ietf-drmp/` (pom, Constants, AVPProvider, test)
- Provider: `sparta-diameter-3gpp-sgdgdd/.../SgdGddAVPProvider.java`
- Leaf mixin: `.../sgdgdd/mixins/HasScAddressAVP.java`; Grouped mixin: `HasSmDeliveryFailureCauseAVP.java`; Repeatable grouped: `.../3gpp/common/mixins/HasSupportedFeaturesAVPs.java`
- Messages: `.../sgdgdd/messages/MtForwardShortMessageRequest.java` + `MoForwardShortMessageAnswer.java`
- Factory: `.../sgdgdd/messages/SgdGddMessageFactory.java`
- Provider test: `.../sgdgdd/SgdGddAVPProviderTest.java`

**AVP type → `AVP.create` overload / `getDataAs*` accessor:**

| Diameter type | `AVPDefinition` dataType | setter param + `create` | getter accessor | absent default |
|---|---|---|---|---|
| OctetString | `byte[].class` | `byte[]` | `getDataAsOctetString()` | `null` |
| UTF8String | `String.class` | `String` | `getDataAsString()` | `null` |
| DiameterURI | `String.class` | `String` | `getDataAsDiameterURI()` | `null` |
| DiameterIdentity | `String.class` | `String` | `getDataAsDiameterIdentity()` | `null` |
| Unsigned32 | `Long.class` | `long` | `getDataAsUnsignedInt()` | `0L` |
| Unsigned64 | `BigInteger.class` | `BigInteger` | `getDataAsUnsignedLong()` | `null` |
| Enumerated | `Integer.class` | `int` | `getDataAsInt()` | `-1` |
| Time | `Date.class` | `Date` | `getDataAsTime()` | `null` |
| Address | `InetAddress.class` | `InetAddress` | `getDataAsIPAddress()` | `null` |
| Grouped | `GroupedAVP.class` | `List<AVP>` | `instanceof GroupedAVP` | `null` |

`AVPDefinition(int code, String name, Class<?> dataType, boolean mandatory, boolean vendorSpecific, int vendorId)`.
Vendor-0 AVPs use `vendorSpecific=false, vendorId=0`. Vendor-specific AVPs must have a non-zero vendorId.

---

## Task 1: Module `sparta-diameter-ietf-radius-digest-authentication` (RFC 5090 Digest AVPs)

**Files:**
- Modify: `pom.xml` (add to `<modules>`)
- Create: `sparta-diameter-ietf-radius-digest-authentication/pom.xml`
- Create: `sparta-diameter-ietf-radius-digest-authentication/src/main/java/com/sipgate/sparta/diameter/ietf/radiusdigestauthentication/RadiusDigestAuthenticationConstants.java`
- Create: `.../radiusdigestauthentication/RadiusDigestAuthenticationAVPProvider.java`
- Test: `.../src/test/java/com/sipgate/sparta/diameter/ietf/radiusdigestauthentication/RadiusDigestAuthenticationAVPProviderTest.java`

- [ ] **Step 1: Register the module in the parent POM**

In `pom.xml`, inside `<modules>`, add after `<module>sparta-diameter-ietf-drmp</module>`:

```xml
        <module>sparta-diameter-ietf-radius-digest-authentication</module>
```

- [ ] **Step 2: Create the module POM**

`sparta-diameter-ietf-radius-digest-authentication/pom.xml` (copy of `ietf-drmp/pom.xml` with new artifactId/name/description):

```xml
<project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>
    <parent>
        <groupId>com.sipgate</groupId>
        <artifactId>sparta-diameter</artifactId>
        <version>0.1.11-SNAPSHOT</version>
    </parent>
    <artifactId>sparta-diameter-ietf-radius-digest-authentication</artifactId>
    <name>sparta-diameter-ietf-radius-digest-authentication</name>
    <description>RFC 5090 — RADIUS Extension for Digest Authentication (Digest-* AVPs)</description>

    <dependencies>
        <dependency>
            <groupId>com.sipgate</groupId>
            <artifactId>sparta-diameter-base</artifactId>
        </dependency>
        <dependency>
            <groupId>org.junit.jupiter</groupId>
            <artifactId>junit-jupiter</artifactId>
            <scope>test</scope>
        </dependency>
        <dependency>
            <groupId>org.assertj</groupId>
            <artifactId>assertj-core</artifactId>
            <scope>test</scope>
        </dependency>
    </dependencies>
</project>
```

- [ ] **Step 3: Write the Constants**

`RadiusDigestAuthenticationConstants.java`:

```java
package com.sipgate.sparta.diameter.ietf.radiusdigestauthentication;

/**
 * Digest authentication AVP codes (IETF RFC 5090, which obsoletes RFC 4590).
 * <p>
 * Imported into the Diameter Cx/Dx application by 3GPP TS 29.229 §6.3.37–6.3.41.
 * All are vendor 0 (IETF namespace), type UTF8String, M-bit set.
 * </p>
 */
public final class RadiusDigestAuthenticationConstants {

    public static final int AVP_DIGEST_REALM = 104;
    public static final int AVP_DIGEST_QOP = 110;
    public static final int AVP_DIGEST_ALGORITHM = 111;
    public static final int AVP_DIGEST_HA1 = 121;

    private RadiusDigestAuthenticationConstants() {}
}
```

- [ ] **Step 4: Write the failing provider test**

`RadiusDigestAuthenticationAVPProviderTest.java`:

```java
package com.sipgate.sparta.diameter.ietf.radiusdigestauthentication;

import com.sipgate.sparta.diameter.base.core.avp.AVPDefinition;
import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

class RadiusDigestAuthenticationAVPProviderTest {

    private final RadiusDigestAuthenticationAVPProvider provider = new RadiusDigestAuthenticationAVPProvider();

    @Test
    void it_defines_the_four_digest_avps_as_vendor_zero_utf8_strings() {
        // GIVEN the RFC 5090 digest provider
        // WHEN the definitions are collected by code
        final Map<Integer, AVPDefinition> byCode = provider.getDefinitions().stream()
            .collect(Collectors.toMap(AVPDefinition::code, Function.identity()));

        // THEN all four digest AVPs are present, UTF8String, vendor 0, mandatory
        assertThat(byCode.keySet()).containsExactlyInAnyOrder(104, 110, 111, 121);
        assertThat(byCode.values()).allSatisfy(def -> {
            assertThat(def.dataType()).isEqualTo(String.class);
            assertThat(def.vendorSpecific()).isFalse();
            assertThat(def.vendorId()).isZero();
            assertThat(def.mandatory()).isTrue();
        });
        assertThat(byCode.get(104).name()).isEqualTo("Digest-Realm");
    }
}
```

- [ ] **Step 5: Run the test, verify it fails**

Run: `mvn -q -pl sparta-diameter-ietf-radius-digest-authentication -am test`
Expected: compilation failure — `RadiusDigestAuthenticationAVPProvider` does not exist.

- [ ] **Step 6: Write the provider**

`RadiusDigestAuthenticationAVPProvider.java`:

```java
package com.sipgate.sparta.diameter.ietf.radiusdigestauthentication;

import com.sipgate.sparta.diameter.base.core.avp.AVPDefinition;
import com.sipgate.sparta.diameter.base.core.avp.AVPProvider;

import java.util.Collection;
import java.util.List;

/**
 * AVP definitions for the RFC 5090 digest authentication AVPs used by Cx/Dx
 * inside SIP-Digest-Authenticate (3GPP TS 29.229 §6.3.36). Vendor 0, UTF8String, M-bit set.
 */
public final class RadiusDigestAuthenticationAVPProvider implements AVPProvider {

    @Override
    public Collection<AVPDefinition> getDefinitions() {
        return List.of(
            new AVPDefinition(RadiusDigestAuthenticationConstants.AVP_DIGEST_REALM, "Digest-Realm", String.class, true, false, 0),
            new AVPDefinition(RadiusDigestAuthenticationConstants.AVP_DIGEST_QOP, "Digest-QoP", String.class, true, false, 0),
            new AVPDefinition(RadiusDigestAuthenticationConstants.AVP_DIGEST_ALGORITHM, "Digest-Algorithm", String.class, true, false, 0),
            new AVPDefinition(RadiusDigestAuthenticationConstants.AVP_DIGEST_HA1, "Digest-HA1", String.class, true, false, 0)
        );
    }
}
```

- [ ] **Step 7: Run the test, verify it passes**

Run: `mvn -q -pl sparta-diameter-ietf-radius-digest-authentication -am test`
Expected: PASS.

- [ ] **Step 8: Commit**

```bash
git add pom.xml sparta-diameter-ietf-radius-digest-authentication
git commit -m "feat(cxdx): add RFC 5090 digest authentication AVP module"
```

---

## Task 2: Module `sparta-diameter-ietf-diameter-nas` (RFC 7155 Framed AVPs)

**Files:**
- Modify: `pom.xml` (`<modules>`)
- Create: `sparta-diameter-ietf-diameter-nas/pom.xml`
- Create: `.../com/sipgate/sparta/diameter/ietf/diameternas/DiameterNasConstants.java`
- Create: `.../diameternas/DiameterNasAVPProvider.java`
- Test: `.../diameternas/DiameterNasAVPProviderTest.java`

- [ ] **Step 1: Register in parent POM** — add `<module>sparta-diameter-ietf-diameter-nas</module>` after the Task 1 module line.

- [ ] **Step 2: Create the module POM** — identical to Task 1 Step 2 but:
  - `<artifactId>sparta-diameter-ietf-diameter-nas</artifactId>`
  - `<name>sparta-diameter-ietf-diameter-nas</name>`
  - `<description>RFC 7155 — Diameter Network Access Server Application (Framed-* AVPs)</description>`
  - same three dependencies (base, junit-jupiter, assertj-core).

- [ ] **Step 3: Write the Constants**

`DiameterNasConstants.java`:

```java
package com.sipgate.sparta.diameter.ietf.diameternas;

/**
 * Framed-* AVP codes (IETF RFC 7155, which obsoletes RFC 4005).
 * <p>
 * Imported into Cx/Dx by 3GPP TS 29.229 §6.3.53–6.3.55 inside SIP-Auth-Data-Item.
 * Vendor 0. Note Framed-Interface-Id is Unsigned64 (RFC 7155 §4.4.10.5.5).
 * </p>
 */
public final class DiameterNasConstants {

    public static final int AVP_FRAMED_IP_ADDRESS = 8;     // OctetString
    public static final int AVP_FRAMED_INTERFACE_ID = 96;  // Unsigned64
    public static final int AVP_FRAMED_IPV6_PREFIX = 97;   // OctetString

    private DiameterNasConstants() {}
}
```

- [ ] **Step 4: Write the failing provider test**

`DiameterNasAVPProviderTest.java`:

```java
package com.sipgate.sparta.diameter.ietf.diameternas;

import com.sipgate.sparta.diameter.base.core.avp.AVPDefinition;
import org.junit.jupiter.api.Test;

import java.math.BigInteger;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

class DiameterNasAVPProviderTest {

    private final DiameterNasAVPProvider provider = new DiameterNasAVPProvider();

    @Test
    void it_defines_framed_avps_with_interface_id_as_unsigned64() {
        // GIVEN the RFC 7155 framed provider
        // WHEN definitions are collected by code
        final Map<Integer, AVPDefinition> byCode = provider.getDefinitions().stream()
            .collect(Collectors.toMap(AVPDefinition::code, Function.identity()));

        // THEN Framed-IP-Address/IPv6-Prefix are OctetString and Framed-Interface-Id is Unsigned64
        assertThat(byCode.keySet()).containsExactlyInAnyOrder(8, 96, 97);
        assertThat(byCode.get(8).dataType()).isEqualTo(byte[].class);
        assertThat(byCode.get(97).dataType()).isEqualTo(byte[].class);
        assertThat(byCode.get(96).dataType()).isEqualTo(BigInteger.class);
        assertThat(byCode.values()).allSatisfy(def -> assertThat(def.vendorId()).isZero());
    }
}
```

- [ ] **Step 5: Run, verify it fails** — `mvn -q -pl sparta-diameter-ietf-diameter-nas -am test` → compilation failure.

- [ ] **Step 6: Write the provider**

`DiameterNasAVPProvider.java`:

```java
package com.sipgate.sparta.diameter.ietf.diameternas;

import com.sipgate.sparta.diameter.base.core.avp.AVPDefinition;
import com.sipgate.sparta.diameter.base.core.avp.AVPProvider;

import java.math.BigInteger;
import java.util.Collection;
import java.util.List;

/**
 * AVP definitions for the RFC 7155 Framed-* AVPs used by Cx/Dx inside SIP-Auth-Data-Item
 * (3GPP TS 29.229 §6.3.13). Vendor 0.
 */
public final class DiameterNasAVPProvider implements AVPProvider {

    @Override
    public Collection<AVPDefinition> getDefinitions() {
        return List.of(
            new AVPDefinition(DiameterNasConstants.AVP_FRAMED_IP_ADDRESS, "Framed-IP-Address", byte[].class, true, false, 0),
            new AVPDefinition(DiameterNasConstants.AVP_FRAMED_INTERFACE_ID, "Framed-Interface-Id", BigInteger.class, true, false, 0),
            new AVPDefinition(DiameterNasConstants.AVP_FRAMED_IPV6_PREFIX, "Framed-IPv6-Prefix", byte[].class, true, false, 0)
        );
    }
}
```

- [ ] **Step 7: Run, verify it passes** — `mvn -q -pl sparta-diameter-ietf-diameter-nas -am test` → PASS.

- [ ] **Step 8: Commit**

```bash
git add pom.xml sparta-diameter-ietf-diameter-nas
git commit -m "feat(cxdx): add RFC 7155 Diameter NAS Framed-* AVP module"
```

---

## Task 3: Module `sparta-diameter-etsi-e2` (ES 283 035 Line-Identifier)

**Files:**
- Modify: `pom.xml` (`<modules>`)
- Create: `sparta-diameter-etsi-e2/pom.xml`
- Create: `.../com/sipgate/sparta/diameter/etsi/e2/E2Constants.java`
- Create: `.../etsi/e2/E2AVPProvider.java`
- Test: `.../etsi/e2/E2AVPProviderTest.java`

- [ ] **Step 1: Register in parent POM** — add `<module>sparta-diameter-etsi-e2</module>` after the Task 2 module line.

- [ ] **Step 2: Create the module POM** — like Task 1 Step 2 but:
  - `<artifactId>sparta-diameter-etsi-e2</artifactId>`, `<name>sparta-diameter-etsi-e2</name>`
  - `<description>ETSI ES 283 035 — TISPAN NASS e2 interface (Line-Identifier AVP)</description>`
  - same three dependencies.

- [ ] **Step 3: Write the Constants**

`E2Constants.java`:

```java
package com.sipgate.sparta.diameter.etsi.e2;

/**
 * AVP definitions for the ETSI NASS e2 interface (ETSI ES 283 035 v3.2.1).
 * Used by Cx/Dx inside SIP-Auth-Data-Item for NASS-Bundled authentication
 * (referenced by 3GPP TS 29.229 §6.3.42).
 */
public final class E2Constants {

    /** ETSI vendor id. */
    public static final int VENDOR_ID_ETSI = 13019;

    /** Line-Identifier AVP (ES 283 035 §7.3.5). OctetString, V flag only (M-bit MUST NOT be set). */
    public static final int AVP_LINE_IDENTIFIER = 500;

    private E2Constants() {}
}
```

- [ ] **Step 4: Write the failing provider test**

`E2AVPProviderTest.java`:

```java
package com.sipgate.sparta.diameter.etsi.e2;

import com.sipgate.sparta.diameter.base.core.avp.AVPDefinition;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class E2AVPProviderTest {

    private final E2AVPProvider provider = new E2AVPProvider();

    @Test
    void it_defines_line_identifier_as_etsi_vendor_octetstring_without_m_bit() {
        // GIVEN the ETSI e2 provider
        // WHEN its single definition is read
        final AVPDefinition def = provider.getDefinitions().iterator().next();

        // THEN Line-Identifier is OctetString, ETSI vendor, V only (mandatory=false)
        assertThat(def.code()).isEqualTo(500);
        assertThat(def.name()).isEqualTo("Line-Identifier");
        assertThat(def.dataType()).isEqualTo(byte[].class);
        assertThat(def.vendorSpecific()).isTrue();
        assertThat(def.vendorId()).isEqualTo(13019);
        assertThat(def.mandatory()).isFalse();
    }
}
```

- [ ] **Step 5: Run, verify it fails** — `mvn -q -pl sparta-diameter-etsi-e2 -am test` → compilation failure.

- [ ] **Step 6: Write the provider**

`E2AVPProvider.java`:

```java
package com.sipgate.sparta.diameter.etsi.e2;

import com.sipgate.sparta.diameter.base.core.avp.AVPDefinition;
import com.sipgate.sparta.diameter.base.core.avp.AVPProvider;

import java.util.Collection;
import java.util.List;

/**
 * AVP definition for the ETSI ES 283 035 Line-Identifier AVP (code 500, vendor 13019).
 */
public final class E2AVPProvider implements AVPProvider {

    @Override
    public Collection<AVPDefinition> getDefinitions() {
        return List.of(
            new AVPDefinition(E2Constants.AVP_LINE_IDENTIFIER, "Line-Identifier", byte[].class, false, true, E2Constants.VENDOR_ID_ETSI)
        );
    }
}
```

- [ ] **Step 7: Run, verify it passes** — `mvn -q -pl sparta-diameter-etsi-e2 -am test` → PASS.

- [ ] **Step 8: Commit**

```bash
git add pom.xml sparta-diameter-etsi-e2
git commit -m "feat(cxdx): add ETSI ES 283 035 e2 Line-Identifier AVP module"
```

---

## Task 4: Wire dependencies into the cxdx module POM

**Files:**
- Modify: `sparta-diameter-3gpp-cxdx/pom.xml`

- [ ] **Step 1: Replace the `<dependencies>` block**

`sparta-diameter-3gpp-cxdx/pom.xml` `<dependencies>` becomes:

```xml
    <dependencies>
        <dependency>
            <groupId>com.sipgate</groupId>
            <artifactId>sparta-diameter-3gpp-common</artifactId>
        </dependency>
        <dependency>
            <groupId>com.sipgate</groupId>
            <artifactId>sparta-diameter-ietf-drmp</artifactId>
        </dependency>
        <dependency>
            <groupId>com.sipgate</groupId>
            <artifactId>sparta-diameter-ietf-radius-digest-authentication</artifactId>
        </dependency>
        <dependency>
            <groupId>com.sipgate</groupId>
            <artifactId>sparta-diameter-ietf-diameter-nas</artifactId>
        </dependency>
        <dependency>
            <groupId>com.sipgate</groupId>
            <artifactId>sparta-diameter-etsi-e2</artifactId>
        </dependency>
        <dependency>
            <groupId>org.junit.jupiter</groupId>
            <artifactId>junit-jupiter</artifactId>
            <scope>test</scope>
        </dependency>
        <dependency>
            <groupId>org.assertj</groupId>
            <artifactId>assertj-core</artifactId>
            <scope>test</scope>
        </dependency>
    </dependencies>
```

- [ ] **Step 2: Verify it builds** — `mvn -q -pl sparta-diameter-3gpp-cxdx -am test` → PASS (no new sources yet; the existing `CxDxConstants` stub still compiles).

- [ ] **Step 3: Commit**

```bash
git add sparta-diameter-3gpp-cxdx/pom.xml
git commit -m "build(cxdx): add module dependencies for Cx/Dx AVP sources"
```

---

## Task 5: Fill `CxDxConstants`

**Files:**
- Modify: `sparta-diameter-3gpp-cxdx/src/main/java/com/sipgate/sparta/diameter/_3gpp/cxdx/CxDxConstants.java`

- [ ] **Step 1: Replace the stub with the constants**

```java
package com.sipgate.sparta.diameter._3gpp.cxdx;

/**
 * Constants for the Cx/Dx Diameter interface (3GPP TS 29.229 / ETSI TS 129 229 v18.1.0).
 */
public final class CxDxConstants {

    /** Diameter application id for the Cx/Dx interface (TS 29.229 §6, IANA-allocated). */
    public static final int APP_ID_CX_DX = 16777216;

    // Command codes (TS 29.229 Table 6.1.1)
    public static final int CMD_SERVER_ASSIGNMENT = 301;
    public static final int CMD_MULTIMEDIA_AUTH = 303;
    public static final int CMD_REGISTRATION_TERMINATION = 304;

    // Cx/Dx-specific AVP codes (TS 29.229 Table 6.3.0.1). Vendor 3GPP (10415).
    public static final int AVP_PUBLIC_IDENTITY = 601;
    public static final int AVP_SERVER_NAME = 602;
    public static final int AVP_USER_DATA = 606;
    public static final int AVP_SIP_NUMBER_AUTH_ITEMS = 607;
    public static final int AVP_SIP_AUTHENTICATION_SCHEME = 608;
    public static final int AVP_SIP_AUTHENTICATE = 609;
    public static final int AVP_SIP_AUTHORIZATION = 610;
    public static final int AVP_SIP_AUTHENTICATION_CONTEXT = 611;
    public static final int AVP_SIP_AUTH_DATA_ITEM = 612;
    public static final int AVP_SIP_ITEM_NUMBER = 613;
    public static final int AVP_SERVER_ASSIGNMENT_TYPE = 614;
    public static final int AVP_DEREGISTRATION_REASON = 615;
    public static final int AVP_REASON_CODE = 616;
    public static final int AVP_REASON_INFO = 617;
    public static final int AVP_CHARGING_INFORMATION = 618;
    public static final int AVP_PRIMARY_EVENT_CHARGING_FUNCTION_NAME = 619;
    public static final int AVP_SECONDARY_EVENT_CHARGING_FUNCTION_NAME = 620;
    public static final int AVP_PRIMARY_CHARGING_COLLECTION_FUNCTION_NAME = 621;
    public static final int AVP_SECONDARY_CHARGING_COLLECTION_FUNCTION_NAME = 622;
    public static final int AVP_USER_DATA_ALREADY_AVAILABLE = 624;
    public static final int AVP_CONFIDENTIALITY_KEY = 625;
    public static final int AVP_INTEGRITY_KEY = 626;
    public static final int AVP_ASSOCIATED_IDENTITIES = 632;
    public static final int AVP_WILDCARDED_PUBLIC_IDENTITY = 634;
    public static final int AVP_SIP_DIGEST_AUTHENTICATE = 635;
    public static final int AVP_LOOSE_ROUTE_INDICATION = 638;
    public static final int AVP_SCSCF_RESTORATION_INFO = 639;
    public static final int AVP_PATH = 640;
    public static final int AVP_CONTACT = 641;
    public static final int AVP_SUBSCRIPTION_INFO = 642;
    public static final int AVP_CALL_ID_SIP_HEADER = 643;
    public static final int AVP_FROM_SIP_HEADER = 644;
    public static final int AVP_TO_SIP_HEADER = 645;
    public static final int AVP_RECORD_ROUTE = 646;
    public static final int AVP_ASSOCIATED_REGISTERED_IDENTITIES = 647;
    public static final int AVP_MULTIPLE_REGISTRATION_INDICATION = 648;
    public static final int AVP_RESTORATION_INFO = 649;
    public static final int AVP_SESSION_PRIORITY = 650;
    public static final int AVP_IDENTITY_WITH_EMERGENCY_REGISTRATION = 651;
    public static final int AVP_PRIVILEDGED_SENDER_INDICATION = 652;
    public static final int AVP_INITIAL_CSEQ_SEQUENCE_NUMBER = 654;
    public static final int AVP_SAR_FLAGS = 655;
    public static final int AVP_ALLOWED_WAF_WWSF_IDENTITIES = 656;
    public static final int AVP_WEBRTC_AUTHENTICATION_FUNCTION_NAME = 657;
    public static final int AVP_WEBRTC_WEB_SERVER_FUNCTION_NAME = 658;
    public static final int AVP_RTR_FLAGS = 659;
    public static final int AVP_PCSCF_SUBSCRIPTION_INFO = 660;
    public static final int AVP_REGISTRATION_TIME_OUT = 661;
    public static final int AVP_ALTERNATE_DIGEST_ALGORITHM = 662;
    public static final int AVP_ALTERNATE_DIGEST_HA1 = 663;
    public static final int AVP_FAILED_PCSCF = 664;
    public static final int AVP_PCSCF_FQDN = 665;
    public static final int AVP_PCSCF_IP_ADDRESS = 666;

    // Server-Assignment-Type values (TS 29.229 §6.3.15)
    public static final int SERVER_ASSIGNMENT_NO_ASSIGNMENT = 0;
    public static final int SERVER_ASSIGNMENT_REGISTRATION = 1;
    public static final int SERVER_ASSIGNMENT_RE_REGISTRATION = 2;
    public static final int SERVER_ASSIGNMENT_UNREGISTERED_USER = 3;
    public static final int SERVER_ASSIGNMENT_TIMEOUT_DEREGISTRATION = 4;
    public static final int SERVER_ASSIGNMENT_USER_DEREGISTRATION = 5;
    public static final int SERVER_ASSIGNMENT_TIMEOUT_DEREGISTRATION_STORE_SERVER_NAME = 6;
    public static final int SERVER_ASSIGNMENT_USER_DEREGISTRATION_STORE_SERVER_NAME = 7;
    public static final int SERVER_ASSIGNMENT_ADMINISTRATIVE_DEREGISTRATION = 8;
    public static final int SERVER_ASSIGNMENT_AUTHENTICATION_FAILURE = 9;
    public static final int SERVER_ASSIGNMENT_AUTHENTICATION_TIMEOUT = 10;
    public static final int SERVER_ASSIGNMENT_DEREGISTRATION_TOO_MUCH_DATA = 11;

    // Reason-Code values (TS 29.229 §6.3.17)
    public static final int REASON_CODE_PERMANENT_TERMINATION = 0;
    public static final int REASON_CODE_NEW_SERVER_ASSIGNED = 1;
    public static final int REASON_CODE_SERVER_CHANGE = 2;
    public static final int REASON_CODE_REMOVE_SCSCF = 3;

    // User-Data-Already-Available values (TS 29.229 §6.3.26)
    public static final int USER_DATA_NOT_AVAILABLE = 0;
    public static final int USER_DATA_ALREADY_AVAILABLE = 1;

    // Experimental-Result values (TS 29.229 §6.2). 5001 (USER_UNKNOWN) is in _3gppConstants.
    public static final long EXP_RES_DIAMETER_FIRST_REGISTRATION = 2001L;
    public static final long EXP_RES_DIAMETER_SUBSEQUENT_REGISTRATION = 2002L;
    public static final long EXP_RES_DIAMETER_UNREGISTERED_SERVICE = 2003L;
    public static final long EXP_RES_DIAMETER_SUCCESS_SERVER_NAME_NOT_STORED = 2004L;
    public static final long EXP_RES_DIAMETER_ERROR_IDENTITIES_DONT_MATCH = 5002L;
    public static final long EXP_RES_DIAMETER_ERROR_IDENTITY_NOT_REGISTERED = 5003L;
    public static final long EXP_RES_DIAMETER_ERROR_ROAMING_NOT_ALLOWED = 5004L;
    public static final long EXP_RES_DIAMETER_ERROR_IDENTITY_ALREADY_REGISTERED = 5005L;
    public static final long EXP_RES_DIAMETER_ERROR_AUTH_SCHEME_NOT_SUPPORTED = 5006L;
    public static final long EXP_RES_DIAMETER_ERROR_IN_ASSIGNMENT_TYPE = 5007L;
    public static final long EXP_RES_DIAMETER_ERROR_TOO_MUCH_DATA = 5008L;
    public static final long EXP_RES_DIAMETER_ERROR_NOT_SUPPORTED_USER_DATA = 5009L;
    public static final long EXP_RES_DIAMETER_ERROR_FEATURE_UNSUPPORTED = 5011L;
    public static final long EXP_RES_DIAMETER_ERROR_SERVING_NODE_FEATURE_UNSUPPORTED = 5012L;

    private CxDxConstants() {}
}
```

- [ ] **Step 2: Verify it compiles** — `mvn -q -pl sparta-diameter-3gpp-cxdx -am test-compile` → PASS.

- [ ] **Step 3: Commit**

```bash
git add sparta-diameter-3gpp-cxdx/src/main/java/com/sipgate/sparta/diameter/_3gpp/cxdx/CxDxConstants.java
git commit -m "feat(cxdx): add Cx/Dx command, AVP, enum and result-code constants"
```

---

## Task 6: `CxDxAVPProvider` (all Cx/Dx-specific AVP definitions)

**Files:**
- Create: `.../_3gpp/cxdx/CxDxAVPProvider.java`
- Test: `.../_3gpp/cxdx/CxDxAVPProviderTest.java`

- [ ] **Step 1: Write the failing provider test**

`CxDxAVPProviderTest.java`:

```java
package com.sipgate.sparta.diameter._3gpp.cxdx;

import com.sipgate.sparta.diameter._3gpp.common._3gppConstants;
import com.sipgate.sparta.diameter.base.core.avp.AVPDefinition;
import com.sipgate.sparta.diameter.base.core.avp.GroupedAVP;
import org.junit.jupiter.api.Test;

import java.util.Date;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

class CxDxAVPProviderTest {

    private final CxDxAVPProvider provider = new CxDxAVPProvider();

    private Map<Integer, AVPDefinition> byCode() {
        return provider.getDefinitions().stream()
            .collect(Collectors.toMap(AVPDefinition::code, Function.identity()));
    }

    @Test
    void it_defines_all_53_cxdx_avps_as_3gpp_vendor() {
        // GIVEN the Cx/Dx provider
        // WHEN definitions are collected
        final var defs = provider.getDefinitions();

        // THEN there are 53 definitions, all vendor 3GPP
        assertThat(defs).hasSize(53);
        assertThat(defs).allSatisfy(def -> {
            assertThat(def.vendorSpecific()).isTrue();
            assertThat(def.vendorId()).isEqualTo(_3gppConstants.VENDOR_ID_3GPP);
        });
    }

    @Test
    void it_maps_representative_avps_to_their_spec_types_and_flags() {
        // GIVEN the definitions by code
        final Map<Integer, AVPDefinition> byCode = byCode();

        // THEN types and M-bit match TS 29.229 Table 6.3.0.1
        assertThat(byCode.get(601).dataType()).isEqualTo(String.class);   // Public-Identity UTF8String
        assertThat(byCode.get(601).mandatory()).isTrue();                 // M,V
        assertThat(byCode.get(606).dataType()).isEqualTo(byte[].class);   // User-Data OctetString
        assertThat(byCode.get(612).dataType()).isEqualTo(GroupedAVP.class); // SIP-Auth-Data-Item
        assertThat(byCode.get(614).dataType()).isEqualTo(Integer.class);  // Server-Assignment-Type Enumerated
        assertThat(byCode.get(619).dataType()).isEqualTo(String.class);   // charging fn name DiameterURI
        assertThat(byCode.get(655).dataType()).isEqualTo(Long.class);     // SAR-Flags Unsigned32
        assertThat(byCode.get(655).mandatory()).isFalse();                // V only
        assertThat(byCode.get(661).dataType()).isEqualTo(Date.class);     // Registration-Time-Out Time
        assertThat(byCode.get(665).dataType()).isEqualTo(String.class);   // PCSCF-FQDN DiameterIdentity
        assertThat(byCode.get(666).dataType()).isEqualTo(java.net.InetAddress.class); // PCSCF-IP-Address Address
    }
}
```

- [ ] **Step 2: Run, verify it fails** — `mvn -q -pl sparta-diameter-3gpp-cxdx -am test` → compilation failure (`CxDxAVPProvider` missing).

- [ ] **Step 3: Write the provider**

`CxDxAVPProvider.java` (codes 601–626 are `M,V` → `mandatory=true`; 632–666 are `V` only → `mandatory=false`):

```java
package com.sipgate.sparta.diameter._3gpp.cxdx;

import com.sipgate.sparta.diameter._3gpp.common._3gppConstants;
import com.sipgate.sparta.diameter.base.core.avp.AVPDefinition;
import com.sipgate.sparta.diameter.base.core.avp.AVPProvider;
import com.sipgate.sparta.diameter.base.core.avp.GroupedAVP;

import java.net.InetAddress;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;

/**
 * Provides AVP definitions for the Cx/Dx Diameter interface (3GPP TS 29.229 §6.3).
 */
public final class CxDxAVPProvider implements AVPProvider {

    private static final int V = _3gppConstants.VENDOR_ID_3GPP;

    @Override
    public Collection<AVPDefinition> getDefinitions() {
        return Arrays.asList(
            // Codes 601-626: M,V (mandatory=true)
            new AVPDefinition(CxDxConstants.AVP_PUBLIC_IDENTITY, "Public-Identity", String.class, true, true, V),
            new AVPDefinition(CxDxConstants.AVP_SERVER_NAME, "Server-Name", String.class, true, true, V),
            new AVPDefinition(CxDxConstants.AVP_USER_DATA, "User-Data", byte[].class, true, true, V),
            new AVPDefinition(CxDxConstants.AVP_SIP_NUMBER_AUTH_ITEMS, "SIP-Number-Auth-Items", Long.class, true, true, V),
            new AVPDefinition(CxDxConstants.AVP_SIP_AUTHENTICATION_SCHEME, "SIP-Authentication-Scheme", String.class, true, true, V),
            new AVPDefinition(CxDxConstants.AVP_SIP_AUTHENTICATE, "SIP-Authenticate", byte[].class, true, true, V),
            new AVPDefinition(CxDxConstants.AVP_SIP_AUTHORIZATION, "SIP-Authorization", byte[].class, true, true, V),
            new AVPDefinition(CxDxConstants.AVP_SIP_AUTHENTICATION_CONTEXT, "SIP-Authentication-Context", byte[].class, true, true, V),
            new AVPDefinition(CxDxConstants.AVP_SIP_AUTH_DATA_ITEM, "SIP-Auth-Data-Item", GroupedAVP.class, true, true, V),
            new AVPDefinition(CxDxConstants.AVP_SIP_ITEM_NUMBER, "SIP-Item-Number", Long.class, true, true, V),
            new AVPDefinition(CxDxConstants.AVP_SERVER_ASSIGNMENT_TYPE, "Server-Assignment-Type", Integer.class, true, true, V),
            new AVPDefinition(CxDxConstants.AVP_DEREGISTRATION_REASON, "Deregistration-Reason", GroupedAVP.class, true, true, V),
            new AVPDefinition(CxDxConstants.AVP_REASON_CODE, "Reason-Code", Integer.class, true, true, V),
            new AVPDefinition(CxDxConstants.AVP_REASON_INFO, "Reason-Info", String.class, true, true, V),
            new AVPDefinition(CxDxConstants.AVP_CHARGING_INFORMATION, "Charging-Information", GroupedAVP.class, true, true, V),
            new AVPDefinition(CxDxConstants.AVP_PRIMARY_EVENT_CHARGING_FUNCTION_NAME, "Primary-Event-Charging-Function-Name", String.class, true, true, V),
            new AVPDefinition(CxDxConstants.AVP_SECONDARY_EVENT_CHARGING_FUNCTION_NAME, "Secondary-Event-Charging-Function-Name", String.class, true, true, V),
            new AVPDefinition(CxDxConstants.AVP_PRIMARY_CHARGING_COLLECTION_FUNCTION_NAME, "Primary-Charging-Collection-Function-Name", String.class, true, true, V),
            new AVPDefinition(CxDxConstants.AVP_SECONDARY_CHARGING_COLLECTION_FUNCTION_NAME, "Secondary-Charging-Collection-Function-Name", String.class, true, true, V),
            new AVPDefinition(CxDxConstants.AVP_USER_DATA_ALREADY_AVAILABLE, "User-Data-Already-Available", Integer.class, true, true, V),
            new AVPDefinition(CxDxConstants.AVP_CONFIDENTIALITY_KEY, "Confidentiality-Key", byte[].class, true, true, V),
            new AVPDefinition(CxDxConstants.AVP_INTEGRITY_KEY, "Integrity-Key", byte[].class, true, true, V),

            // Codes 632-666: V only (mandatory=false)
            new AVPDefinition(CxDxConstants.AVP_ASSOCIATED_IDENTITIES, "Associated-Identities", GroupedAVP.class, false, true, V),
            new AVPDefinition(CxDxConstants.AVP_WILDCARDED_PUBLIC_IDENTITY, "Wildcarded-Public-Identity", String.class, false, true, V),
            new AVPDefinition(CxDxConstants.AVP_SIP_DIGEST_AUTHENTICATE, "SIP-Digest-Authenticate", GroupedAVP.class, false, true, V),
            new AVPDefinition(CxDxConstants.AVP_LOOSE_ROUTE_INDICATION, "Loose-Route-Indication", Integer.class, false, true, V),
            new AVPDefinition(CxDxConstants.AVP_SCSCF_RESTORATION_INFO, "SCSCF-Restoration-Info", GroupedAVP.class, false, true, V),
            new AVPDefinition(CxDxConstants.AVP_PATH, "Path", byte[].class, false, true, V),
            new AVPDefinition(CxDxConstants.AVP_CONTACT, "Contact", byte[].class, false, true, V),
            new AVPDefinition(CxDxConstants.AVP_SUBSCRIPTION_INFO, "Subscription-Info", GroupedAVP.class, false, true, V),
            new AVPDefinition(CxDxConstants.AVP_CALL_ID_SIP_HEADER, "Call-ID-SIP-Header", byte[].class, false, true, V),
            new AVPDefinition(CxDxConstants.AVP_FROM_SIP_HEADER, "From-SIP-Header", byte[].class, false, true, V),
            new AVPDefinition(CxDxConstants.AVP_TO_SIP_HEADER, "To-SIP-Header", byte[].class, false, true, V),
            new AVPDefinition(CxDxConstants.AVP_RECORD_ROUTE, "Record-Route", byte[].class, false, true, V),
            new AVPDefinition(CxDxConstants.AVP_ASSOCIATED_REGISTERED_IDENTITIES, "Associated-Registered-Identities", GroupedAVP.class, false, true, V),
            new AVPDefinition(CxDxConstants.AVP_MULTIPLE_REGISTRATION_INDICATION, "Multiple-Registration-Indication", Integer.class, false, true, V),
            new AVPDefinition(CxDxConstants.AVP_RESTORATION_INFO, "Restoration-Info", GroupedAVP.class, false, true, V),
            new AVPDefinition(CxDxConstants.AVP_SESSION_PRIORITY, "Session-Priority", Integer.class, false, true, V),
            new AVPDefinition(CxDxConstants.AVP_IDENTITY_WITH_EMERGENCY_REGISTRATION, "Identity-with-Emergency-Registration", GroupedAVP.class, false, true, V),
            new AVPDefinition(CxDxConstants.AVP_PRIVILEDGED_SENDER_INDICATION, "Priviledged-Sender-Indication", Integer.class, false, true, V),
            new AVPDefinition(CxDxConstants.AVP_INITIAL_CSEQ_SEQUENCE_NUMBER, "Initial-CSeq-Sequence-Number", Long.class, false, true, V),
            new AVPDefinition(CxDxConstants.AVP_SAR_FLAGS, "SAR-Flags", Long.class, false, true, V),
            new AVPDefinition(CxDxConstants.AVP_ALLOWED_WAF_WWSF_IDENTITIES, "Allowed-WAF-WWSF-Identities", GroupedAVP.class, false, true, V),
            new AVPDefinition(CxDxConstants.AVP_WEBRTC_AUTHENTICATION_FUNCTION_NAME, "WebRTC-Authentication-Function-Name", String.class, false, true, V),
            new AVPDefinition(CxDxConstants.AVP_WEBRTC_WEB_SERVER_FUNCTION_NAME, "WebRTC-Web-Server-Function-Name", String.class, false, true, V),
            new AVPDefinition(CxDxConstants.AVP_RTR_FLAGS, "RTR-Flags", Long.class, false, true, V),
            new AVPDefinition(CxDxConstants.AVP_PCSCF_SUBSCRIPTION_INFO, "P-CSCF-Subscription-Info", GroupedAVP.class, false, true, V),
            new AVPDefinition(CxDxConstants.AVP_REGISTRATION_TIME_OUT, "Registration-Time-Out", Date.class, false, true, V),
            new AVPDefinition(CxDxConstants.AVP_ALTERNATE_DIGEST_ALGORITHM, "Alternate-Digest-Algorithm", String.class, false, true, V),
            new AVPDefinition(CxDxConstants.AVP_ALTERNATE_DIGEST_HA1, "Alternate-Digest-HA1", String.class, false, true, V),
            new AVPDefinition(CxDxConstants.AVP_FAILED_PCSCF, "Failed-PCSCF", GroupedAVP.class, false, true, V),
            new AVPDefinition(CxDxConstants.AVP_PCSCF_FQDN, "PCSCF-FQDN", String.class, false, true, V),
            new AVPDefinition(CxDxConstants.AVP_PCSCF_IP_ADDRESS, "PCSCF-IP-Address", InetAddress.class, false, true, V)
        );
    }
}
```

- [ ] **Step 4: Run, verify it passes** — `mvn -q -pl sparta-diameter-3gpp-cxdx -am test` → PASS (both tests green; count is 53).

- [ ] **Step 5: Commit**

```bash
git add sparta-diameter-3gpp-cxdx/src/main/java/com/sipgate/sparta/diameter/_3gpp/cxdx/CxDxAVPProvider.java \
        sparta-diameter-3gpp-cxdx/src/test/java/com/sipgate/sparta/diameter/_3gpp/cxdx/CxDxAVPProviderTest.java
git commit -m "feat(cxdx): add CxDxAVPProvider with all Cx/Dx AVP definitions"
```

---

## Task 7: Message-direct mixins

Create one mixin per **message-direct** AVP only (nested-only AVPs are intentionally mixin-less; they round-trip via their `AVPDefinition`). All live in `sparta-diameter-3gpp-cxdx/src/main/java/com/sipgate/sparta/diameter/_3gpp/cxdx/mixins/`.

**Three templates** — substitute `<Name>`, `<CONST>`, accessor per the table:

Leaf template (here: String / `getDataAsString`):
```java
package com.sipgate.sparta.diameter._3gpp.cxdx.mixins;

import com.sipgate.sparta.diameter._3gpp.common._3gppConstants;
import com.sipgate.sparta.diameter._3gpp.cxdx.CxDxConstants;
import com.sipgate.sparta.diameter.base.core.avp.AVP;
import com.sipgate.sparta.diameter.base.core.avp.AVPContainer;
import com.sipgate.sparta.diameter.base.core.avp.AVPKey;

/** Mixin for messages carrying a Public-Identity AVP (3GPP TS 29.229 §6.3.2, code 601). */
public interface HasPublicIdentityAVP extends AVPContainer {

    default void setPublicIdentity(final String value) {
        setAVP(AVP.create(new AVPKey(CxDxConstants.AVP_PUBLIC_IDENTITY, _3gppConstants.VENDOR_ID_3GPP), value));
    }

    default String getPublicIdentity() {
        final var avp = findAVP(new AVPKey(CxDxConstants.AVP_PUBLIC_IDENTITY, _3gppConstants.VENDOR_ID_3GPP));
        return avp != null ? avp.getDataAsString() : null;
    }
}
```

Single grouped template (set `List<AVP>` / get `AVPContainer`):
```java
package com.sipgate.sparta.diameter._3gpp.cxdx.mixins;

import com.sipgate.sparta.diameter._3gpp.common._3gppConstants;
import com.sipgate.sparta.diameter._3gpp.cxdx.CxDxConstants;
import com.sipgate.sparta.diameter.base.core.avp.AVP;
import com.sipgate.sparta.diameter.base.core.avp.AVPContainer;
import com.sipgate.sparta.diameter.base.core.avp.AVPKey;
import com.sipgate.sparta.diameter.base.core.avp.GroupedAVP;

import java.util.List;

/** Mixin for messages carrying a Deregistration-Reason grouped AVP (TS 29.229 §6.3.16, code 615). */
public interface HasDeregistrationReasonAVP extends AVPContainer {

    default void setDeregistrationReason(final List<AVP> avps) {
        setAVP(AVP.create(new AVPKey(CxDxConstants.AVP_DEREGISTRATION_REASON, _3gppConstants.VENDOR_ID_3GPP), avps));
    }

    default AVPContainer getDeregistrationReason() {
        final var avp = findAVP(new AVPKey(CxDxConstants.AVP_DEREGISTRATION_REASON, _3gppConstants.VENDOR_ID_3GPP));
        return avp instanceof final GroupedAVP grouped ? grouped : null;
    }
}
```

Repeatable grouped template (add `List<AVP>` / get `List<AVPContainer>`) — model on `HasSupportedFeaturesAVPs`:
```java
package com.sipgate.sparta.diameter._3gpp.cxdx.mixins;

import com.sipgate.sparta.diameter._3gpp.common._3gppConstants;
import com.sipgate.sparta.diameter._3gpp.cxdx.CxDxConstants;
import com.sipgate.sparta.diameter.base.core.avp.AVP;
import com.sipgate.sparta.diameter.base.core.avp.AVPContainer;
import com.sipgate.sparta.diameter.base.core.avp.AVPKey;
import com.sipgate.sparta.diameter.base.core.avp.GroupedAVP;

import java.util.ArrayList;
import java.util.List;

/** Mixin for messages carrying zero or more SIP-Auth-Data-Item grouped AVPs (TS 29.229 §6.3.13, code 612). */
public interface HasSipAuthDataItemAVPs extends AVPContainer {

    default void addSipAuthDataItem(final List<AVP> avps) {
        addAVP(AVP.create(new AVPKey(CxDxConstants.AVP_SIP_AUTH_DATA_ITEM, _3gppConstants.VENDOR_ID_3GPP), avps));
    }

    default List<AVPContainer> getSipAuthDataItems() {
        final List<AVPContainer> result = new ArrayList<>();
        for (final AVP avp : findAVPs(new AVPKey(CxDxConstants.AVP_SIP_AUTH_DATA_ITEM, _3gppConstants.VENDOR_ID_3GPP))) {
            if (avp instanceof final GroupedAVP grouped) {
                result.add(grouped);
            }
        }
        return result;
    }
}
```

- [ ] **Step 1: Create all leaf mixins** — one file each, from the leaf template, substituting the accessor for the type:

| Mixin / file | AVP const | type | setter param | getter accessor + default |
|---|---|---|---|---|
| `HasPublicIdentityAVP` | `AVP_PUBLIC_IDENTITY` | String | `String` | `getDataAsString()` / null |
| `HasServerNameAVP` | `AVP_SERVER_NAME` | String | `String` | `getDataAsString()` / null |
| `HasUserDataAVP` | `AVP_USER_DATA` | byte[] | `byte[]` | `getDataAsOctetString()` / null |
| `HasSipNumberAuthItemsAVP` | `AVP_SIP_NUMBER_AUTH_ITEMS` | Unsigned32 | `long` | `getDataAsUnsignedInt()` / 0L |
| `HasServerAssignmentTypeAVP` | `AVP_SERVER_ASSIGNMENT_TYPE` | Enumerated | `int` | `getDataAsInt()` / -1 |
| `HasUserDataAlreadyAvailableAVP` | `AVP_USER_DATA_ALREADY_AVAILABLE` | Enumerated | `int` | `getDataAsInt()` / -1 |
| `HasWildcardedPublicIdentityAVP` | `AVP_WILDCARDED_PUBLIC_IDENTITY` | String | `String` | `getDataAsString()` / null |
| `HasLooseRouteIndicationAVP` | `AVP_LOOSE_ROUTE_INDICATION` | Enumerated | `int` | `getDataAsInt()` / -1 |
| `HasMultipleRegistrationIndicationAVP` | `AVP_MULTIPLE_REGISTRATION_INDICATION` | Enumerated | `int` | `getDataAsInt()` / -1 |
| `HasSessionPriorityAVP` | `AVP_SESSION_PRIORITY` | Enumerated | `int` | `getDataAsInt()` / -1 |
| `HasPriviledgedSenderIndicationAVP` | `AVP_PRIVILEDGED_SENDER_INDICATION` | Enumerated | `int` | `getDataAsInt()` / -1 |
| `HasSarFlagsAVP` | `AVP_SAR_FLAGS` | Unsigned32 | `long` | `getDataAsUnsignedInt()` / 0L |
| `HasRtrFlagsAVP` | `AVP_RTR_FLAGS` | Unsigned32 | `long` | `getDataAsUnsignedInt()` / 0L |

Method names follow the AVP: e.g. `HasServerNameAVP` → `setServerName`/`getServerName`;
`HasSipNumberAuthItemsAVP` → `setSipNumberAuthItems`/`getSipNumberAuthItems`.

- [ ] **Step 2: Create all single-grouped mixins** — from the single grouped template:

| Mixin / file | AVP const | methods |
|---|---|---|
| `HasSipAuthDataItemAVP` | `AVP_SIP_AUTH_DATA_ITEM` | `setSipAuthDataItem(List<AVP>)` / `getSipAuthDataItem()` |
| `HasDeregistrationReasonAVP` | `AVP_DEREGISTRATION_REASON` | `setDeregistrationReason` / `getDeregistrationReason` |
| `HasChargingInformationAVP` | `AVP_CHARGING_INFORMATION` | `setChargingInformation` / `getChargingInformation` |
| `HasAssociatedIdentitiesAVP` | `AVP_ASSOCIATED_IDENTITIES` | `setAssociatedIdentities` / `getAssociatedIdentities` |
| `HasScscfRestorationInfoAVP` | `AVP_SCSCF_RESTORATION_INFO` | `setScscfRestorationInfo` / `getScscfRestorationInfo` |
| `HasAssociatedRegisteredIdentitiesAVP` | `AVP_ASSOCIATED_REGISTERED_IDENTITIES` | `setAssociatedRegisteredIdentities` / `getAssociatedRegisteredIdentities` |
| `HasAllowedWafWwsfIdentitiesAVP` | `AVP_ALLOWED_WAF_WWSF_IDENTITIES` | `setAllowedWafWwsfIdentities` / `getAllowedWafWwsfIdentities` |
| `HasFailedPcscfAVP` | `AVP_FAILED_PCSCF` | `setFailedPcscf` / `getFailedPcscf` |

- [ ] **Step 3: Create the repeatable-grouped mixins** — from the repeatable template:

| Mixin / file | AVP const | methods |
|---|---|---|
| `HasSipAuthDataItemAVPs` | `AVP_SIP_AUTH_DATA_ITEM` | `addSipAuthDataItem(List<AVP>)` / `getSipAuthDataItems()` |
| `HasIdentityWithEmergencyRegistrationAVPs` | `AVP_IDENTITY_WITH_EMERGENCY_REGISTRATION` | `addIdentityWithEmergencyRegistration(List<AVP>)` / `getIdentityWithEmergencyRegistrations()` |
| `HasScscfRestorationInfoAVPs` | `AVP_SCSCF_RESTORATION_INFO` | `addScscfRestorationInfo(List<AVP>)` / `getScscfRestorationInfos()` |

- [ ] **Step 3b: Create the repeatable-leaf mixin** — model on base `HasRouteRecordAVPs`
  (`add(String)` via `addAVP`, getter returning `List<String>`):

| Mixin / file | AVP const | methods |
|---|---|---|
| `HasPublicIdentityAVPs` | `AVP_PUBLIC_IDENTITY` | `addPublicIdentity(String)` / `getPublicIdentities()` |

> **Single vs list (per ABNF qualifier).** Some AVPs appear `{X}`/`[X]` in one message and `*[X]`
> in another, so they get **both** a single and a list mixin: Public-Identity → single in MAR/MAA,
> list (`HasPublicIdentityAVPs`) in SAR/RTR; SCSCF-Restoration-Info → single in SAR, list
> (`HasScscfRestorationInfoAVPs`) in SAA; SIP-Auth-Data-Item → single in MAR, list in MAA. Verify
> each AVP's qualifier in the command ABNF (`*` / `1*` prefix ⇒ list).

- [ ] **Step 4: Verify compilation** — `mvn -q -pl sparta-diameter-3gpp-cxdx -am test-compile` → PASS.

- [ ] **Step 5: Commit**

```bash
git add sparta-diameter-3gpp-cxdx/src/main/java/com/sipgate/sparta/diameter/_3gpp/cxdx/mixins
git commit -m "feat(cxdx): add message-direct AVP mixins"
```

---

## Task 8: Messages SAR / SAA

> **Amendment (post-review).** Per the ABNF, **SAR** carries `*[ Public-Identity ]` and **SAA**
> carries `*[ SCSCF-Restoration-Info ]` — both repeatable. Use the list mixins in the code below:
> SAR `extends … HasPublicIdentityAVPs …` (keep single `HasScscfRestorationInfoAVP` — SAR's is `[X]`);
> SAA `extends … HasScscfRestorationInfoAVPs …`. See Task 7 Step 3/3b.

**Files:**
- Create: `.../cxdx/messages/ServerAssignmentRequest.java`
- Create: `.../cxdx/messages/ServerAssignmentAnswer.java`

- [ ] **Step 1: Write `ServerAssignmentAnswer`** (define the answer first so the request can reference it)

```java
package com.sipgate.sparta.diameter._3gpp.cxdx.messages;

import com.sipgate.sparta.diameter._3gpp.common._3gppAnswer;
import com.sipgate.sparta.diameter._3gpp.cxdx.CxDxConstants;
import com.sipgate.sparta.diameter._3gpp.cxdx.mixins.HasAllowedWafWwsfIdentitiesAVP;
import com.sipgate.sparta.diameter._3gpp.cxdx.mixins.HasAssociatedIdentitiesAVP;
import com.sipgate.sparta.diameter._3gpp.cxdx.mixins.HasAssociatedRegisteredIdentitiesAVP;
import com.sipgate.sparta.diameter._3gpp.cxdx.mixins.HasChargingInformationAVP;
import com.sipgate.sparta.diameter._3gpp.cxdx.mixins.HasLooseRouteIndicationAVP;
import com.sipgate.sparta.diameter._3gpp.cxdx.mixins.HasPriviledgedSenderIndicationAVP;
import com.sipgate.sparta.diameter._3gpp.cxdx.mixins.HasScscfRestorationInfoAVP;
import com.sipgate.sparta.diameter._3gpp.cxdx.mixins.HasServerNameAVP;
import com.sipgate.sparta.diameter._3gpp.cxdx.mixins.HasUserDataAVP;
import com.sipgate.sparta.diameter._3gpp.cxdx.mixins.HasWildcardedPublicIdentityAVP;
import com.sipgate.sparta.diameter.base.core.EndToEndId;
import com.sipgate.sparta.diameter.base.core.HopByHopId;
import com.sipgate.sparta.diameter.base.core.IncomingAnswer;
import com.sipgate.sparta.diameter.base.core.OutgoingAnswer;
import com.sipgate.sparta.diameter.base.core.avp.mixins.HasRouteRecordAVPs;
import com.sipgate.sparta.diameter.base.core.avp.mixins.HasUserNameAVP;
import com.sipgate.sparta.diameter.ietf.drmp.mixins.HasDrmpAVP;

/** Server-Assignment-Answer (SAA) — 3GPP TS 29.229 §6.1.4. */
public interface ServerAssignmentAnswer
        extends _3gppAnswer,
                HasDrmpAVP, HasUserNameAVP, HasUserDataAVP, HasChargingInformationAVP,
                HasAssociatedIdentitiesAVP, HasLooseRouteIndicationAVP, HasScscfRestorationInfoAVP,
                HasAssociatedRegisteredIdentitiesAVP, HasServerNameAVP, HasWildcardedPublicIdentityAVP,
                HasPriviledgedSenderIndicationAVP, HasAllowedWafWwsfIdentitiesAVP,
                HasRouteRecordAVPs {

    final class In extends IncomingAnswer implements ServerAssignmentAnswer {
        In(final HopByHopId hopByHop, final EndToEndId endToEnd) {
            super(CxDxConstants.CMD_SERVER_ASSIGNMENT, true, CxDxConstants.APP_ID_CX_DX, hopByHop, endToEnd);
        }
    }

    final class Out extends OutgoingAnswer implements ServerAssignmentAnswer {
        Out(final HopByHopId hopByHop, final EndToEndId endToEnd) {
            super(CxDxConstants.CMD_SERVER_ASSIGNMENT, true, CxDxConstants.APP_ID_CX_DX, hopByHop, endToEnd);
        }
    }
}
```

- [ ] **Step 2: Write `ServerAssignmentRequest`**

```java
package com.sipgate.sparta.diameter._3gpp.cxdx.messages;

import com.sipgate.sparta.diameter._3gpp.common._3gppRequest;
import com.sipgate.sparta.diameter._3gpp.common.mixins.HasSupportedFeaturesAVPs;
import com.sipgate.sparta.diameter._3gpp.cxdx.CxDxConstants;
import com.sipgate.sparta.diameter._3gpp.cxdx.mixins.HasFailedPcscfAVP;
import com.sipgate.sparta.diameter._3gpp.cxdx.mixins.HasMultipleRegistrationIndicationAVP;
import com.sipgate.sparta.diameter._3gpp.cxdx.mixins.HasPublicIdentityAVP;
import com.sipgate.sparta.diameter._3gpp.cxdx.mixins.HasSarFlagsAVP;
import com.sipgate.sparta.diameter._3gpp.cxdx.mixins.HasScscfRestorationInfoAVP;
import com.sipgate.sparta.diameter._3gpp.cxdx.mixins.HasServerAssignmentTypeAVP;
import com.sipgate.sparta.diameter._3gpp.cxdx.mixins.HasServerNameAVP;
import com.sipgate.sparta.diameter._3gpp.cxdx.mixins.HasSessionPriorityAVP;
import com.sipgate.sparta.diameter._3gpp.cxdx.mixins.HasUserDataAlreadyAvailableAVP;
import com.sipgate.sparta.diameter._3gpp.cxdx.mixins.HasWildcardedPublicIdentityAVP;
import com.sipgate.sparta.diameter.base.core.EndToEndId;
import com.sipgate.sparta.diameter.base.core.HopByHopId;
import com.sipgate.sparta.diameter.base.core.IncomingRequest;
import com.sipgate.sparta.diameter.base.core.OutgoingRequest;
import com.sipgate.sparta.diameter.base.core.avp.mixins.HasDestinationHostAVP;
import com.sipgate.sparta.diameter.base.core.avp.mixins.HasDestinationRealmAVP;
import com.sipgate.sparta.diameter.base.core.avp.mixins.HasProxyInfoAVPs;
import com.sipgate.sparta.diameter.base.core.avp.mixins.HasRouteRecordAVPs;
import com.sipgate.sparta.diameter.base.core.avp.mixins.HasUserNameAVP;
import com.sipgate.sparta.diameter.ietf.drmp.mixins.HasDrmpAVP;

/** Server-Assignment-Request (SAR) — 3GPP TS 29.229 §6.1.3. */
public interface ServerAssignmentRequest
        extends _3gppRequest,
                HasDrmpAVP, HasDestinationHostAVP, HasDestinationRealmAVP, HasUserNameAVP,
                HasSupportedFeaturesAVPs, HasPublicIdentityAVP, HasWildcardedPublicIdentityAVP,
                HasServerNameAVP, HasServerAssignmentTypeAVP, HasUserDataAlreadyAvailableAVP,
                HasScscfRestorationInfoAVP, HasMultipleRegistrationIndicationAVP, HasSessionPriorityAVP,
                HasSarFlagsAVP, HasFailedPcscfAVP,
                HasProxyInfoAVPs, HasRouteRecordAVPs {

    final class In extends IncomingRequest<ServerAssignmentAnswer.Out> implements ServerAssignmentRequest {
        In(final HopByHopId hopByHop, final EndToEndId endToEnd, final boolean retransmitted) {
            super(CxDxConstants.CMD_SERVER_ASSIGNMENT, true, retransmitted, CxDxConstants.APP_ID_CX_DX, hopByHop, endToEnd);
        }
    }

    final class Out extends OutgoingRequest<ServerAssignmentAnswer.In> implements ServerAssignmentRequest {
        public Out() {
            super(CxDxConstants.CMD_SERVER_ASSIGNMENT, true, CxDxConstants.APP_ID_CX_DX);
        }
    }
}
```

- [ ] **Step 3: Verify compilation** — `mvn -q -pl sparta-diameter-3gpp-cxdx -am test-compile` → PASS.

- [ ] **Step 4: Commit**

```bash
git add sparta-diameter-3gpp-cxdx/src/main/java/com/sipgate/sparta/diameter/_3gpp/cxdx/messages/ServerAssignment*.java
git commit -m "feat(cxdx): add SAR/SAA messages"
```

---

## Task 9: Messages MAR / MAA

**Files:**
- Create: `.../cxdx/messages/MultimediaAuthAnswer.java`
- Create: `.../cxdx/messages/MultimediaAuthRequest.java`

- [ ] **Step 1: Write `MultimediaAuthAnswer`** (uses repeatable `HasSipAuthDataItemAVPs`)

```java
package com.sipgate.sparta.diameter._3gpp.cxdx.messages;

import com.sipgate.sparta.diameter._3gpp.common._3gppAnswer;
import com.sipgate.sparta.diameter._3gpp.cxdx.CxDxConstants;
import com.sipgate.sparta.diameter._3gpp.cxdx.mixins.HasPublicIdentityAVP;
import com.sipgate.sparta.diameter._3gpp.cxdx.mixins.HasSipAuthDataItemAVPs;
import com.sipgate.sparta.diameter._3gpp.cxdx.mixins.HasSipNumberAuthItemsAVP;
import com.sipgate.sparta.diameter.base.core.EndToEndId;
import com.sipgate.sparta.diameter.base.core.HopByHopId;
import com.sipgate.sparta.diameter.base.core.IncomingAnswer;
import com.sipgate.sparta.diameter.base.core.OutgoingAnswer;
import com.sipgate.sparta.diameter.base.core.avp.mixins.HasRouteRecordAVPs;
import com.sipgate.sparta.diameter.base.core.avp.mixins.HasUserNameAVP;
import com.sipgate.sparta.diameter.ietf.drmp.mixins.HasDrmpAVP;

/** Multimedia-Auth-Answer (MAA) — 3GPP TS 29.229 §6.1.8. */
public interface MultimediaAuthAnswer
        extends _3gppAnswer,
                HasDrmpAVP, HasUserNameAVP, HasPublicIdentityAVP, HasSipNumberAuthItemsAVP,
                HasSipAuthDataItemAVPs, HasRouteRecordAVPs {

    final class In extends IncomingAnswer implements MultimediaAuthAnswer {
        In(final HopByHopId hopByHop, final EndToEndId endToEnd) {
            super(CxDxConstants.CMD_MULTIMEDIA_AUTH, true, CxDxConstants.APP_ID_CX_DX, hopByHop, endToEnd);
        }
    }

    final class Out extends OutgoingAnswer implements MultimediaAuthAnswer {
        Out(final HopByHopId hopByHop, final EndToEndId endToEnd) {
            super(CxDxConstants.CMD_MULTIMEDIA_AUTH, true, CxDxConstants.APP_ID_CX_DX, hopByHop, endToEnd);
        }
    }
}
```

- [ ] **Step 2: Write `MultimediaAuthRequest`** (single `HasSipAuthDataItemAVP`)

```java
package com.sipgate.sparta.diameter._3gpp.cxdx.messages;

import com.sipgate.sparta.diameter._3gpp.common._3gppRequest;
import com.sipgate.sparta.diameter._3gpp.common.mixins.HasSupportedFeaturesAVPs;
import com.sipgate.sparta.diameter._3gpp.cxdx.CxDxConstants;
import com.sipgate.sparta.diameter._3gpp.cxdx.mixins.HasPublicIdentityAVP;
import com.sipgate.sparta.diameter._3gpp.cxdx.mixins.HasServerNameAVP;
import com.sipgate.sparta.diameter._3gpp.cxdx.mixins.HasSipAuthDataItemAVP;
import com.sipgate.sparta.diameter._3gpp.cxdx.mixins.HasSipNumberAuthItemsAVP;
import com.sipgate.sparta.diameter.base.core.EndToEndId;
import com.sipgate.sparta.diameter.base.core.HopByHopId;
import com.sipgate.sparta.diameter.base.core.IncomingRequest;
import com.sipgate.sparta.diameter.base.core.OutgoingRequest;
import com.sipgate.sparta.diameter.base.core.avp.mixins.HasDestinationHostAVP;
import com.sipgate.sparta.diameter.base.core.avp.mixins.HasDestinationRealmAVP;
import com.sipgate.sparta.diameter.base.core.avp.mixins.HasProxyInfoAVPs;
import com.sipgate.sparta.diameter.base.core.avp.mixins.HasRouteRecordAVPs;
import com.sipgate.sparta.diameter.base.core.avp.mixins.HasUserNameAVP;
import com.sipgate.sparta.diameter.ietf.drmp.mixins.HasDrmpAVP;

/** Multimedia-Auth-Request (MAR) — 3GPP TS 29.229 §6.1.7. */
public interface MultimediaAuthRequest
        extends _3gppRequest,
                HasDrmpAVP, HasDestinationRealmAVP, HasDestinationHostAVP, HasUserNameAVP,
                HasSupportedFeaturesAVPs, HasPublicIdentityAVP, HasSipAuthDataItemAVP,
                HasSipNumberAuthItemsAVP, HasServerNameAVP,
                HasProxyInfoAVPs, HasRouteRecordAVPs {

    final class In extends IncomingRequest<MultimediaAuthAnswer.Out> implements MultimediaAuthRequest {
        In(final HopByHopId hopByHop, final EndToEndId endToEnd, final boolean retransmitted) {
            super(CxDxConstants.CMD_MULTIMEDIA_AUTH, true, retransmitted, CxDxConstants.APP_ID_CX_DX, hopByHop, endToEnd);
        }
    }

    final class Out extends OutgoingRequest<MultimediaAuthAnswer.In> implements MultimediaAuthRequest {
        public Out() {
            super(CxDxConstants.CMD_MULTIMEDIA_AUTH, true, CxDxConstants.APP_ID_CX_DX);
        }
    }
}
```

- [ ] **Step 3: Verify compilation** — `mvn -q -pl sparta-diameter-3gpp-cxdx -am test-compile` → PASS.

- [ ] **Step 4: Commit**

```bash
git add sparta-diameter-3gpp-cxdx/src/main/java/com/sipgate/sparta/diameter/_3gpp/cxdx/messages/MultimediaAuth*.java
git commit -m "feat(cxdx): add MAR/MAA messages"
```

---

## Task 10: Messages RTR / RTA

> **Amendment (post-review).** Per the ABNF, **RTR** carries `*[ Public-Identity ]` (repeatable).
> Use `HasPublicIdentityAVPs` (not the single `HasPublicIdentityAVP`) in `RegistrationTerminationRequest`.
> See Task 7 Step 3/3b.

**Files:**
- Create: `.../cxdx/messages/RegistrationTerminationAnswer.java`
- Create: `.../cxdx/messages/RegistrationTerminationRequest.java`

- [ ] **Step 1: Write `RegistrationTerminationAnswer`** (RTR is HSS→client, so the answer's `In`/`Out` direction is the same shape as others)

```java
package com.sipgate.sparta.diameter._3gpp.cxdx.messages;

import com.sipgate.sparta.diameter._3gpp.common._3gppAnswer;
import com.sipgate.sparta.diameter._3gpp.cxdx.CxDxConstants;
import com.sipgate.sparta.diameter._3gpp.cxdx.mixins.HasAssociatedIdentitiesAVP;
import com.sipgate.sparta.diameter._3gpp.cxdx.mixins.HasIdentityWithEmergencyRegistrationAVPs;
import com.sipgate.sparta.diameter.base.core.EndToEndId;
import com.sipgate.sparta.diameter.base.core.HopByHopId;
import com.sipgate.sparta.diameter.base.core.IncomingAnswer;
import com.sipgate.sparta.diameter.base.core.OutgoingAnswer;
import com.sipgate.sparta.diameter.base.core.avp.mixins.HasRouteRecordAVPs;
import com.sipgate.sparta.diameter.ietf.drmp.mixins.HasDrmpAVP;

/** Registration-Termination-Answer (RTA) — 3GPP TS 29.229 §6.1.10. */
public interface RegistrationTerminationAnswer
        extends _3gppAnswer,
                HasDrmpAVP, HasAssociatedIdentitiesAVP, HasIdentityWithEmergencyRegistrationAVPs,
                HasRouteRecordAVPs {

    final class In extends IncomingAnswer implements RegistrationTerminationAnswer {
        In(final HopByHopId hopByHop, final EndToEndId endToEnd) {
            super(CxDxConstants.CMD_REGISTRATION_TERMINATION, true, CxDxConstants.APP_ID_CX_DX, hopByHop, endToEnd);
        }
    }

    final class Out extends OutgoingAnswer implements RegistrationTerminationAnswer {
        Out(final HopByHopId hopByHop, final EndToEndId endToEnd) {
            super(CxDxConstants.CMD_REGISTRATION_TERMINATION, true, CxDxConstants.APP_ID_CX_DX, hopByHop, endToEnd);
        }
    }
}
```

- [ ] **Step 2: Write `RegistrationTerminationRequest`**

```java
package com.sipgate.sparta.diameter._3gpp.cxdx.messages;

import com.sipgate.sparta.diameter._3gpp.common._3gppRequest;
import com.sipgate.sparta.diameter._3gpp.common.mixins.HasSupportedFeaturesAVPs;
import com.sipgate.sparta.diameter._3gpp.cxdx.CxDxConstants;
import com.sipgate.sparta.diameter._3gpp.cxdx.mixins.HasAssociatedIdentitiesAVP;
import com.sipgate.sparta.diameter._3gpp.cxdx.mixins.HasDeregistrationReasonAVP;
import com.sipgate.sparta.diameter._3gpp.cxdx.mixins.HasPublicIdentityAVP;
import com.sipgate.sparta.diameter._3gpp.cxdx.mixins.HasRtrFlagsAVP;
import com.sipgate.sparta.diameter.base.core.EndToEndId;
import com.sipgate.sparta.diameter.base.core.HopByHopId;
import com.sipgate.sparta.diameter.base.core.IncomingRequest;
import com.sipgate.sparta.diameter.base.core.OutgoingRequest;
import com.sipgate.sparta.diameter.base.core.avp.mixins.HasDestinationRealmAVP;
import com.sipgate.sparta.diameter.base.core.avp.mixins.HasProxyInfoAVPs;
import com.sipgate.sparta.diameter.base.core.avp.mixins.HasRouteRecordAVPs;
import com.sipgate.sparta.diameter.base.core.avp.mixins.HasUserNameAVP;
import com.sipgate.sparta.diameter.ietf.drmp.mixins.HasDrmpAVP;

/** Registration-Termination-Request (RTR) — 3GPP TS 29.229 §6.1.9. */
public interface RegistrationTerminationRequest
        extends _3gppRequest,
                HasDrmpAVP, HasDestinationRealmAVP, HasUserNameAVP, HasAssociatedIdentitiesAVP,
                HasSupportedFeaturesAVPs, HasPublicIdentityAVP, HasDeregistrationReasonAVP, HasRtrFlagsAVP,
                HasProxyInfoAVPs, HasRouteRecordAVPs {

    final class In extends IncomingRequest<RegistrationTerminationAnswer.Out> implements RegistrationTerminationRequest {
        In(final HopByHopId hopByHop, final EndToEndId endToEnd, final boolean retransmitted) {
            super(CxDxConstants.CMD_REGISTRATION_TERMINATION, true, retransmitted, CxDxConstants.APP_ID_CX_DX, hopByHop, endToEnd);
        }
    }

    final class Out extends OutgoingRequest<RegistrationTerminationAnswer.In> implements RegistrationTerminationRequest {
        public Out() {
            super(CxDxConstants.CMD_REGISTRATION_TERMINATION, true, CxDxConstants.APP_ID_CX_DX);
        }
    }
}
```

- [ ] **Step 3: Verify compilation** — `mvn -q -pl sparta-diameter-3gpp-cxdx -am test-compile` → PASS.

- [ ] **Step 4: Commit**

```bash
git add sparta-diameter-3gpp-cxdx/src/main/java/com/sipgate/sparta/diameter/_3gpp/cxdx/messages/RegistrationTermination*.java
git commit -m "feat(cxdx): add RTR/RTA messages"
```

---

## Task 11: `CxDxMessageFactory`

**Files:**
- Create: `.../cxdx/messages/CxDxMessageFactory.java`
- Test: `.../cxdx/messages/CxDxMessageFactoryTest.java`

- [ ] **Step 1: Write the failing factory test**

`CxDxMessageFactoryTest.java`:

```java
package com.sipgate.sparta.diameter._3gpp.cxdx.messages;

import com.sipgate.sparta.diameter._3gpp.cxdx.CxDxConstants;
import com.sipgate.sparta.diameter.base.core.DiameterConstants;
import com.sipgate.sparta.diameter.base.core.EndToEndId;
import com.sipgate.sparta.diameter.base.core.HopByHopId;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class CxDxMessageFactoryTest {

    private final CxDxMessageFactory factory = new CxDxMessageFactory();
    private final HopByHopId hopByHop = new HopByHopId(1);
    private final EndToEndId endToEnd = new EndToEndId(2);

    @Test
    void it_parses_each_command_code_to_the_matching_incoming_command() {
        // GIVEN/WHEN/THEN requests
        assertThat(factory.createForParsing(CxDxConstants.CMD_SERVER_ASSIGNMENT, CxDxConstants.APP_ID_CX_DX, true, hopByHop, endToEnd, false))
            .isInstanceOf(ServerAssignmentRequest.In.class);
        assertThat(factory.createForParsing(CxDxConstants.CMD_MULTIMEDIA_AUTH, CxDxConstants.APP_ID_CX_DX, false, hopByHop, endToEnd, false))
            .isInstanceOf(MultimediaAuthAnswer.In.class);
        assertThat(factory.createForParsing(CxDxConstants.CMD_REGISTRATION_TERMINATION, CxDxConstants.APP_ID_CX_DX, true, hopByHop, endToEnd, false))
            .isInstanceOf(RegistrationTerminationRequest.In.class);
    }

    @Test
    void it_returns_null_for_an_unknown_command_code() {
        // GIVEN an unknown command code / WHEN parsing / THEN null
        assertThat(factory.createForParsing(9999, CxDxConstants.APP_ID_CX_DX, true, hopByHop, endToEnd, false)).isNull();
        assertThat(factory.createAnswer(9999, CxDxConstants.APP_ID_CX_DX, hopByHop, endToEnd)).isNull();
    }

    @Test
    void it_builds_answers_with_auth_session_state_not_maintained() {
        // GIVEN/WHEN an answer is created
        final var saa = factory.createAnswer(CxDxConstants.CMD_SERVER_ASSIGNMENT, CxDxConstants.APP_ID_CX_DX, hopByHop, endToEnd);

        // THEN it is an SAA carrying NO_STATE_MAINTAINED (TS 29.229 §5.3)
        assertThat(saa).isInstanceOf(ServerAssignmentAnswer.Out.class);
        assertThat(((ServerAssignmentAnswer.Out) saa).getAuthSessionState())
            .isEqualTo(DiameterConstants.AUTH_SESSION_STATE_NOT_MAINTAINED);
    }
}
```

- [ ] **Step 2: Run, verify it fails** — `mvn -q -pl sparta-diameter-3gpp-cxdx -am test` → compilation failure (`CxDxMessageFactory` missing).

- [ ] **Step 3: Write the factory**

`CxDxMessageFactory.java`:

```java
package com.sipgate.sparta.diameter._3gpp.cxdx.messages;

import com.sipgate.sparta.diameter._3gpp.cxdx.CxDxConstants;
import com.sipgate.sparta.diameter.base.core.*;

public final class CxDxMessageFactory implements DiameterPackageFactory {

    @Override
    public IncomingCommand createForParsing(final int commandCode, final int applicationId,
                                            final boolean isRequest,
                                            final HopByHopId hopByHop, final EndToEndId endToEnd,
                                            final boolean retransmitted) {
        return switch (commandCode) {
            case CxDxConstants.CMD_SERVER_ASSIGNMENT -> isRequest
                    ? new ServerAssignmentRequest.In(hopByHop, endToEnd, retransmitted)
                    : new ServerAssignmentAnswer.In(hopByHop, endToEnd);
            case CxDxConstants.CMD_MULTIMEDIA_AUTH -> isRequest
                    ? new MultimediaAuthRequest.In(hopByHop, endToEnd, retransmitted)
                    : new MultimediaAuthAnswer.In(hopByHop, endToEnd);
            case CxDxConstants.CMD_REGISTRATION_TERMINATION -> isRequest
                    ? new RegistrationTerminationRequest.In(hopByHop, endToEnd, retransmitted)
                    : new RegistrationTerminationAnswer.In(hopByHop, endToEnd);
            default -> null;
        };
    }

    @Override
    public OutgoingAnswer createAnswer(final int commandCode, final int applicationId,
                                       final HopByHopId hopByHop, final EndToEndId endToEnd) {
        final var outgoingAnswer = switch (commandCode) {
            case CxDxConstants.CMD_SERVER_ASSIGNMENT -> new ServerAssignmentAnswer.Out(hopByHop, endToEnd);
            case CxDxConstants.CMD_MULTIMEDIA_AUTH -> new MultimediaAuthAnswer.Out(hopByHop, endToEnd);
            case CxDxConstants.CMD_REGISTRATION_TERMINATION -> new RegistrationTerminationAnswer.Out(hopByHop, endToEnd);
            default -> null;
        };

        if (outgoingAnswer != null) {
            /// 3GPP TS 29.229 §5.2/§5.3: accounting is not used on Cx/Dx and sessions are implicitly
            /// terminated; the client/server include Auth-Session-State = NO_STATE_MAINTAINED.
            outgoingAnswer.setAuthSessionState(DiameterConstants.AUTH_SESSION_STATE_NOT_MAINTAINED);
        }

        return outgoingAnswer;
    }
}
```

- [ ] **Step 4: Run, verify it passes** — `mvn -q -pl sparta-diameter-3gpp-cxdx -am test` → PASS.

- [ ] **Step 5: Commit**

```bash
git add sparta-diameter-3gpp-cxdx/src/main/java/com/sipgate/sparta/diameter/_3gpp/cxdx/messages/CxDxMessageFactory.java \
        sparta-diameter-3gpp-cxdx/src/test/java/com/sipgate/sparta/diameter/_3gpp/cxdx/messages/CxDxMessageFactoryTest.java
git commit -m "feat(cxdx): add CxDxMessageFactory"
```

---

## Task 12: Cross-module grouped-AVP round-trip test

Proves the highest-risk property: every AVP reachable inside the deepest grouped tree —
spanning all four modules (cxdx, radius-digest-authentication, diameter-nas, etsi-e2) — is
registered, so the recursive decoder resolves it instead of throwing `AVPParseException` (5001).

`Command.writeTo(..)` is package-private to `base.core`, so a full message cannot be serialized
from this package. But `AVP.writeTo(DataOutputStream)` and `AVP.readFrom(ByteBuffer)` are **public**
and exercise the exact same recursive grouped encode/decode path. We round-trip the grouped AVP
directly. (Message-level wire encoding is covered by the transport layer's integration tests;
message construction + the factory are covered by Tasks 6 and 11.)

**Files:**
- Test: `.../_3gpp/cxdx/CxDxGroupedAvpRoundTripTest.java`

- [ ] **Step 1: Write the round-trip test**

```java
package com.sipgate.sparta.diameter._3gpp.cxdx;

import com.sipgate.sparta.diameter._3gpp.common._3gppConstants;
import com.sipgate.sparta.diameter.base.core.avp.AVP;
import com.sipgate.sparta.diameter.base.core.avp.AVPKey;
import com.sipgate.sparta.diameter.base.core.avp.GroupedAVP;
import com.sipgate.sparta.diameter.etsi.e2.E2Constants;
import com.sipgate.sparta.diameter.ietf.diameternas.DiameterNasConstants;
import com.sipgate.sparta.diameter.ietf.radiusdigestauthentication.RadiusDigestAuthenticationConstants;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

class CxDxGroupedAvpRoundTripTest {

    private static final int V = _3gppConstants.VENDOR_ID_3GPP;

    @Test
    void it_round_trips_a_sip_auth_data_item_nesting_avps_from_all_four_modules() throws Exception {
        // GIVEN a SIP-Auth-Data-Item (cxdx) nesting: a SIP-Digest-Authenticate (cxdx grouped)
        //       holding Digest-Realm (RFC 5090, vendor 0) + Alternate-Digest-Algorithm (cxdx);
        //       a Framed-Interface-Id (RFC 7155, vendor 0, Unsigned64); a Line-Identifier (ETSI 13019)
        final List<AVP> digest = List.of(
            AVP.create(new AVPKey(RadiusDigestAuthenticationConstants.AVP_DIGEST_REALM, 0), "ims.example.org"),
            AVP.create(new AVPKey(CxDxConstants.AVP_ALTERNATE_DIGEST_ALGORITHM, V), "SHA-256"));
        final List<AVP> sipAuthDataItem = List.of(
            AVP.create(new AVPKey(CxDxConstants.AVP_SIP_AUTHENTICATION_SCHEME, V), "Digest-AKAv1-MD5"),
            AVP.create(new AVPKey(CxDxConstants.AVP_SIP_DIGEST_AUTHENTICATE, V), digest),
            AVP.create(new AVPKey(DiameterNasConstants.AVP_FRAMED_INTERFACE_ID, 0), BigInteger.valueOf(0x0102030405060708L)),
            AVP.create(new AVPKey(E2Constants.AVP_LINE_IDENTIFIER, E2Constants.VENDOR_ID_ETSI),
                       "noc=deTGV;lac=00ab".getBytes(StandardCharsets.US_ASCII)));
        final AVP top = AVP.create(new AVPKey(CxDxConstants.AVP_SIP_AUTH_DATA_ITEM, V), sipAuthDataItem);

        // WHEN it is serialized and parsed back via the public AVP encode/decode path
        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        top.writeTo(new DataOutputStream(baos));
        final AVP[] parsed = new AVP[1];
        assertThatCode(() -> parsed[0] = AVP.readFrom(ByteBuffer.wrap(baos.toByteArray())))
            .doesNotThrowAnyException();

        // THEN every nested AVP resolved (no 5001) and the structure survived
        assertThat(parsed[0]).isInstanceOf(GroupedAVP.class);
        final GroupedAVP item = (GroupedAVP) parsed[0];
        assertThat(item.findAVP(new AVPKey(CxDxConstants.AVP_SIP_DIGEST_AUTHENTICATE, V)))
            .isInstanceOf(GroupedAVP.class);
        assertThat(item.findAVP(new AVPKey(DiameterNasConstants.AVP_FRAMED_INTERFACE_ID, 0)).getDataAsUnsignedLong())
            .isEqualTo(BigInteger.valueOf(0x0102030405060708L));
        assertThat(item.findAVP(new AVPKey(E2Constants.AVP_LINE_IDENTIFIER, E2Constants.VENDOR_ID_ETSI)))
            .isNotNull();
        final GroupedAVP digestOut = (GroupedAVP) item.findAVP(new AVPKey(CxDxConstants.AVP_SIP_DIGEST_AUTHENTICATE, V));
        assertThat(digestOut.findAVP(new AVPKey(RadiusDigestAuthenticationConstants.AVP_DIGEST_REALM, 0)).getDataAsString())
            .isEqualTo("ims.example.org");
    }
}
```

- [ ] **Step 2: Run it** — `mvn -q -pl sparta-diameter-3gpp-cxdx -am test` → PASS. A failure here with
`AVPParseException` / `Unknown AVP key` means a nested AVP's module is missing from the cxdx
dependencies (Task 4) or its definition is absent from a provider (Tasks 1–3, 6).

- [ ] **Step 3: Run the whole reactor to confirm nothing else broke** — `mvn -q test` → PASS.

- [ ] **Step 4: Commit**

```bash
git add sparta-diameter-3gpp-cxdx/src/test/java/com/sipgate/sparta/diameter/_3gpp/cxdx/CxDxGroupedAvpRoundTripTest.java
git commit -m "test(cxdx): round-trip cross-module nested grouped AVP (registration proof)"
```

---

## Done-when

- `mvn test` passes for the whole reactor.
- The five new/updated modules build and are listed in the parent `pom.xml`.
- `CxDxMessageFactory` maps all three command pairs and sets `NO_STATE_MAINTAINED` on answers (Task 11).
- The deepest grouped AVP (SIP-Auth-Data-Item → SIP-Digest-Authenticate → Digest-Realm, plus
  Framed-Interface-Id and Line-Identifier) round-trips encode→decode without `AVPParseException`,
  proving every AVP across all four modules is registered (Task 12).
- No AVP duplicates: base/drmp/3gpp-common AVPs are referenced, not redefined.

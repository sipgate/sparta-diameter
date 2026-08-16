# Sparta Diameter

[![Build](https://github.com/sipgate/sparta-diameter/actions/workflows/publish-snapshot.yml/badge.svg?branch=main)](https://github.com/sipgate/sparta-diameter/actions/workflows/publish-snapshot.yml)
[![Maven Central](https://img.shields.io/maven-central/v/com.sipgate/sparta-diameter-base)](https://central.sonatype.com/namespace/com.sipgate)
[![License: MIT](https://img.shields.io/badge/License-MIT-blue.svg)](LICENSE)

> ⚠️ **Work in Progress**
>
> This library is in active development. API surfaces may change between releases.

A Java library for the Diameter protocol (RFC 6733): message parsing and serialization, peer lifecycle management (CER/CEA, DWR/DWA, DPR/DPA), and a Netty-based transport layer for both server and client roles.

## Installation

Pick the modules you need. For example, to use the SGd/Gdd interface:

Maven:

```xml
<dependency>
    <groupId>com.sipgate</groupId>
    <artifactId>sparta-diameter-3gpp-sgdgdd</artifactId>
    <version>0.1.12</version>
</dependency>
```

Gradle (Kotlin DSL):

```kotlin
implementation("com.sipgate:sparta-diameter-3gpp-sgdgdd:0.1.12")
```

Gradle (Groovy DSL):

```groovy
implementation 'com.sipgate:sparta-diameter-3gpp-sgdgdd:0.1.12'
```

See the [Modules](#modules) table for the full list of artifacts.

## Java Version

Java 17 or newer works; see [verify-pull-request.yml](.github/workflows/verify-pull-request.yml) for
the versions CI verifies against.

## Project Goals

- RFC 6733 compliant Diameter message handling
- Implement anything that is needed by sipgate's HSS (`sparta-hss`) to perform its function
- Unknown messages are handled as `GenericCommand` and can still be processed
- Framework for building Diameter applications, including a potential DRA
- Open-sourced alongside sipgate's `sparta-hss` project

## Modules

Foundation:

| Module | Contents |
|--------|----------|
| `sparta-diameter-spec` | RFC 6733 §3.2 Command Code Format (CCF) parser |
| `sparta-diameter-base` | RFC 6733 base protocol: core, AVPs, transport, session, base messages |

IETF / ETSI extensions — AVP definitions only, no commands:

| Module | Contents |
|--------|----------|
| `sparta-diameter-ietf-doic` | RFC 7683 — Diameter Overload Indication Conveyance (DOIC) |
| `sparta-diameter-ietf-drmp` | RFC 7944 — Diameter Routing Message Priority (DRMP) |
| `sparta-diameter-ietf-load` | RFC 8583 — Diameter Load Information Conveyance |
| `sparta-diameter-ietf-mip6-integrated` | RFC 5778 — Diameter Mobile IPv6, integrated scenario |
| `sparta-diameter-ietf-mip6-split` | RFC 5447 — Diameter Mobile IPv6, split scenario |
| `sparta-diameter-ietf-nas` | RFC 7155 — Network Access Server application (`Framed-*` AVPs) |
| `sparta-diameter-etsi-e2` | ETSI ES 283 035 — TISPAN NASS e2 (`Line-Identifier` AVP) |

3GPP:

| Module | Contents |
|--------|----------|
| `sparta-diameter-3gpp-common` | Shared 3GPP AVPs, constants, mixins and result codes |
| `sparta-diameter-3gpp-s6a` | TS 29.272 — S6a/S6d (MME/SGSN to HSS); also the aggregator module |
| `sparta-diameter-3gpp-cxdx` | TS 29.228/29.229 — Cx/Dx (I-CSCF/S-CSCF to HSS) |
| `sparta-diameter-3gpp-swx` | TS 29.273 §8 — SWx (3GPP AAA Server to HSS) |
| `sparta-diameter-3gpp-sgdgdd` | TS 29.338 §6 — SGd/Gdd (MME/SGSN to SMS-SC) |
| `sparta-diameter-3gpp-s6c` | TS 29.338 §5 — S6c (HSS to SMS-SC/Router); constants only |
| `sparta-diameter-3gpp-gx` | TS 29.212 — Gx (PCEF/PCRF) AVPs reused by other interfaces |
| `sparta-diameter-3gpp-rx` | TS 29.214 — Rx (AF/PCRF) AVPs reused by other interfaces |
| `sparta-diameter-3gpp-s6t` | TS 29.336 — AVPs reused by other interfaces, no commands |
| `sparta-diameter-3gpp-slh` | TS 29.173 — SLh AVPs reused by other interfaces |

`sparta-diameter-spec-extractor` is a development-only tool that downloads 3GPP/ETSI `.docx`
specifications and extracts their AVP tables to JSON. It is not published to Maven Central.

## Module Dependencies

```mermaid
graph TD
    subgraph foundation["Foundation"]
        spec["spec<br/>(CCF parser, AVP table model)"]
        base["base<br/>(core, avp, transport, session, base messages)"]
    end

    subgraph ietf["IETF / ETSI extensions (RFCs)"]
        doic["ietf-doic"]
        drmp["ietf-drmp"]
        load["ietf-load"]
        mip6i["ietf-mip6-integrated"]
        mip6s["ietf-mip6-split"]
        nas["ietf-nas"]
        e2["etsi-e2"]
    end

    common["3gpp-common<br/>(shared 3GPP AVPs, constants, mixins)"]

    subgraph protocols["3GPP protocol interfaces"]
        gx["3gpp-gx"]
        rx["3gpp-rx"]
        s6t["3gpp-s6t"]
        s6c["3gpp-s6c"]
        sgdgdd["3gpp-sgdgdd"]
        slh["3gpp-slh"]
        cxdx["3gpp-cxdx"]
        swx["3gpp-swx"]


        subgraph aggregator["aggregator"]
            s6a["3gpp-s6a"]
        end
    end


    base --> spec
    common --> base
    doic --> base
    drmp --> base
    load --> base
    mip6i --> base
    mip6s --> base
    nas --> base
    e2 --> base

    gx --> common
    rx --> common
    s6t --> common
    s6c --> common
    slh --> common
    sgdgdd --> common
    sgdgdd --> drmp
    cxdx --> common
    cxdx --> doic
    cxdx --> drmp
    cxdx --> load
    cxdx --> nas
    cxdx --> e2
    swx --> common
    swx --> doic
    swx --> drmp
    swx --> load
    swx --> mip6i
    swx --> mip6s
    swx --> nas
    swx --> e2
    swx --> gx
    swx --> s6a
    swx --> cxdx

    s6a --> common
    s6a --> cxdx
    s6a --> gx
    s6a --> rx
    s6a --> s6t
    s6a --> slh
    s6a --> doic
    s6a --> drmp
    s6a --> load
    s6a --> mip6i
    s6a --> mip6s
```

- **Foundation:** `spec` (CCF parser, AVP-table model) → `base` (core, avp, transport, session, base messages).
- **IETF/ETSI extensions** each depend only on `base`.
- **`3gpp-common`** is the single shared 3GPP layer (shared AVPs, constants, mixins) on `base`.
- **Protocol modules** depend on `common` (and pick IETF modules as their command-code format requires). `swx` additionally depends on `gx`, `s6a`, and `cxdx` and reuses their shared AVP accessors rather than duplicating them.
- **`3gpp-s6a` is an aggregator:** it pulls `cxdx`, `gx`, `rx`, `s6t`, `slh` + IETF + mip6 because an S6a/S6d HSS typically co-deploys them.

## Development Status

- ✅ Core infrastructure: message parsing and serialization
- ✅ RFC 6733 base messages: CER/CEA, DWR/DWA, DPR/DPA, ACR/ACA, STR/STA, ASR/ASA, RAR/RAA
- ✅ Netty-based transport (`DiameterNode`, `DiameterPeer`)
- ✅ Session layer with capability negotiation, watchdog, reconnect timer (Tc)
- ✅ SGd/Gdd: MO-Forward-Short-Message, MT-Forward-Short-Message
- ✅ S6a/S6d: AIR/AIA, ULR/ULA, CLR/CLA, IDR/IDA, DSR/DSA, PUR/PUA, RSR/RSA, NOR/NOA
- ✅ Cx/Dx: MAR/MAA, SAR/SAA, RTR/RTA
- ✅ SWx: MAR/MAA, SAR/SAA, RTR/RTA, PPR/PPA
- ✅ AVP definitions for Gx, Rx, S6t, SLh and the IETF/ETSI extensions above
- ✅ Comprehensive test coverage of the implemented features
- 🚧 S6c: constants only, no commands yet
- 🚧 Full AVP coverage of the referenced 3GPP/ETSI specs (the spec-conformance tests skip unimplemented AVPs — currently about half)

## Usage

### Building a Server (Responder)

The server accepts inbound connections and handles requests. Use `DiameterNode.listen` with a `DiameterResponderSession` factory.

```java
final var config = new DiameterNodeConfig(
    "hss.example.com",
    "example.com",
    List.of(InetAddress.getByName("192.168.1.100")),
    10415L, // 3GPP vendor ID
    "sparta-hss",
    new DiameterNodeConfig.Capabilities(
        List.of(),
        List.of(),
        List.of((long) _3gppConstants.VENDOR_ID_3GPP),
        List.of(new DiameterNodeConfig.VendorSpecificApp(
            _3gppConstants.VENDOR_ID_3GPP,
            SgdGddConstants.APP_ID_SGD_GDD))
    )
);

try (final var node = new DiameterNode()) {
    final var serverFuture = node.listen(3868, () -> {
        final var session = new DiameterResponderSession(config);

        session.setHandler(MoForwardShortMessageRequest.In.class, request -> {
            final var answer = DiameterMessageFactory.createAnswer(
                request, DiameterConstants.RES_DIAMETER_SUCCESS);
            // populate answer AVPs here
            return CompletableFuture.completedFuture(answer);
        });

        return session;
    });

    serverFuture.sync(); // wait until the port is bound
    // keep running ...
}
```

### Building a Client (Initiator)

The client opens an outbound connection and can send requests. Use `DiameterNode.connect` with a `DiameterInitiatorSession` factory. The session automatically reconnects after the Tc timer fires when the connection drops.

```java
final var config = new DiameterNodeConfig(
    "smsc.example.com",
    "example.com",
    List.of(InetAddress.getByName("10.0.0.1")),
    10415L,
    "sparta-smsc",
    new DiameterNodeConfig.Capabilities(
        List.of(),
        List.of(),
        List.of((long) _3gppConstants.VENDOR_ID_3GPP),
        List.of(new DiameterNodeConfig.VendorSpecificApp(
            _3gppConstants.VENDOR_ID_3GPP,
            SgdGddConstants.APP_ID_SGD_GDD))
    )
);

try (final var node = new DiameterNode()) {
    node.connect("dra.example.com", 3868, reconnect -> {
        final var session = new DiameterInitiatorSession(config, reconnect);

        session.setHandler(MoForwardShortMessageRequest.In.class, request -> {
            final var userIdentifier = request.getUserIdentifier();
            final var smRpUi = request.getSmRpUi();
            // process the inbound MO SMS ...

            final var answer = DiameterMessageFactory.createAnswer(
                request, DiameterConstants.RES_DIAMETER_SUCCESS);
            return CompletableFuture.completedFuture(answer);
        });

        return session;
    }).sync();

    // Send a request once the session reaches I_OPEN:
    // final var future = session.send(outgoingRequest);
    // final var answer = future.get();
}
```

### Sending a Request

```java
final var request = new MtForwardShortMessageRequest.Out();
request.setDestinationHost("hss.example.com");
request.setDestinationRealm("example.com");
request.setSmRpUi(encodedPdu);

final var answer = session.send(request).get();
```

### Working with AVPs

```java
// Typed access via message mixins
final var smRpUi = request.getSmRpUi();           // byte[]
final var originHost = request.getOriginHost();   // String
final var resultCode = answer.getResultCode();    // long

// Grouped AVP access
final var userIdentifier = request.getUserIdentifier(); // AVPContainer, null if absent
final var avp = userIdentifier.findAVP(
    new AVPKey(_3gppConstants.AVP_MSISDN, _3gppConstants.VENDOR_ID_3GPP));
```

## Key Classes

| Class | Role |
|-------|------|
| `DiameterNode` | Netty-based transport; `listen(port, factory)` / `connect(host, port, factory)` |
| `DiameterPeer` | Wraps a Netty channel; `send(answer)` / `send(request, h2h, e2e)` |
| `DiameterResponderSession` | Inbound session: handles CER, watchdog, routes requests to handlers |
| `DiameterInitiatorSession` | Outbound session: sends CER, handles reconnect via Tc timer |
| `DiameterNodeConfig` | Node identity, declared capabilities, protocol timers (TWINIT, Tc) |
| `DiameterMessageFactory` | Parses incoming messages, creates answers and error answers; auto-discovers message factories |
| `DiameterRequestHandler` | `CompletableFuture<Answer> handle(IncomingRequest)` — registered via `session.setHandler` |
| `GenericCommand` | Fallback for unknown command codes or application IDs |

## Metrics

See [docs/metrics.md](docs/metrics.md) for the full list of meters and their tags.

## Building

There is no Maven wrapper — use your own `mvn`. The toolchain is pinned in
[`.sdkmanrc`](.sdkmanrc) (Java 17.0.18-zulu, Maven 3.9.14); with
[SDKMAN!](https://sdkman.io/) installed, run `sdk env` to activate it.

```shell
mvn clean verify
```

For a local peer to talk to, the repository ships a Python-based Diameter server:

```shell
docker compose up diameter-server   # listens on localhost:3868
```

## Contributing

See [CONTRIBUTING.md](CONTRIBUTING.md) for conventions and the development workflow, and
[AGENTS.md](AGENTS.md) for the coding rules enforced in review. Architecture decisions live in
[`ADR/`](ADR); requirements for planned features live in [`specs/`](specs).

To report a security issue, see [SECURITY.md](SECURITY.md).

## Preparing a new release

We're using the [Maven release plugin](https://maven.apache.org/maven-release/maven-release-plugin/index.html).
When ready, run `mvn release:prepare` and follow the instructions. This will create, tag and push a new release.

We skip `release:perform` — the actual build and deploy to Maven Central happens in GitHub Actions when the tag lands (see `.github/workflows/publish-release.yml`). After a successful prepare, run `mvn release:clean` to remove `release.properties` and POM backups; otherwise the next `release:prepare` will try to resume the previous run.

Pushes to `main` publish a `-SNAPSHOT` via `.github/workflows/publish-snapshot.yml`.

## License

MIT. See the [LICENSE](LICENSE) file for details.

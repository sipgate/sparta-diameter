# Sparta Diameter

> ⚠️ **Initial Draft - Work in Progress**
> 
> This library is currently in early development and is not yet complete. The goal is to use this library in our sparta-HSS to replace YATE's TCP-XML interface with a native Java Diameter protocol implementation.

A Java library for implementing Diameter protocol (RFC 6733) messages and communication.

## Project Goals

- Provide RFC 6733 compliant Diameter message handling
- Implement only the messages needed for our HSS 
- All other messages will be interpreted as `GenericCommand` and can still be processed
- Future server/client implementation will be provided using Netty
- This shall serve as a framework for building Diameter applications, maybe even a DRA
- This library shall be open sourced alongside with our sparta-HSS project

## Development Status

- ✅ Core infrastructure and base classes
- ✅ RFC 6733 message parsing and serialization
- ✅ Base protocol messages (CER/CEA, DWR/DWA)
- 🚧 Additional Diameter applications for the HSS (S6a, Cx)
- 🚧 State machine for Diameter
- ✅ Netty-based transport layer (`DiameterNode`, `DiameterPeer`)
- 🚧 Comprehensive test coverage

## Package Structure

The project follows the following package structure:

### Core Infrastructure (`com.sipgate.sparta.diameter.core`)
- **`Command`** - Abstract base class for all Diameter messages with header handling and AVP management
- **`Request`** - Abstract base for request messages (R-bit set)
- **`Answer`** - Abstract base for answer messages with Result-Code support
- **`DiameterMessageFactory`** - Central factory for creating request and answer messages
- **`AVP`** - Attribute-Value Pair implementation with encoding/decoding
- **`GroupedAVP`** - Support for nested AVP structures
- **`GenericCommand`** - Fallback for unknown message types
- **`DiameterConstants`** - Protocol constants (command codes, AVP codes, result codes)

### Core Mixins (`com.sipgate.sparta.diameter.core.mixins`)
- **`DiameterMessage`** - Base interface providing fundamental AVP operations
- **`OriginStateAware`** - Mixin for messages supporting Origin-State-Id AVP

### Message Implementations (`com.sipgate.sparta.diameter.messages`)

#### Base Protocol Messages (`messages.base`)
Implementation of fundamental Diameter protocol messages:
- **`CapabilitiesExchangeRequest/Answer`** - CER/CEA for peer capability negotiation
- **`DeviceWatchdogRequest/Answer`** - DWR/DWA for connection health monitoring

#### Message-Specific Mixins (`messages.base.mixins`)
- **`CapabilitiesExchange`** - Common functionality for CER/CEA messages

### Utilities
- **`DiameterMessageParser`** - Binary message parsing and serialization
- **`DiameterException`** - Protocol-specific exception handling

## Message Examples

```java
// Create a Device Watchdog Request
DeviceWatchdogRequest dwr = DiameterMessageFactory.create(
        DeviceWatchdogRequest.class, hopByHop, endToEnd);
dwr.setOriginHost("hss.example.com");
dwr.setOriginRealm("example.com");
dwr.setOriginStateId(12345);

// Create a retransmitted request
DeviceWatchdogRequest retransmit = DiameterMessageFactory.createRetransmitted(
        DeviceWatchdogRequest.class, hopByHop, endToEnd);

// Create Capabilities Exchange Request
CapabilitiesExchangeRequest cer = DiameterMessageFactory.create(
        CapabilitiesExchangeRequest.class, hopByHop, endToEnd);
cer.setOriginHost("hss.example.com");
cer.setOriginRealm("example.com");
cer.setVendorId(10415); // 3GPP
cer.setProductName("Sparta HSS");
cer.addHostIPAddress(InetAddress.getByName("192.168.1.100"));

// Create an answer for a received request (echoes identifiers; copies Origin-Host/Realm
// from the request to Destination-Host/Realm on the answer)
DeviceWatchdogAnswer dwa = DiameterMessageFactory.createAnswer(dwr, DiameterConstants.RES_DIAMETER_SUCCESS);

// Create a bare answer with explicit identifiers (for testing or peer simulation)
CapabilitiesExchangeAnswer cea = DiameterMessageFactory.createAnswer(
        CapabilitiesExchangeAnswer.class, hopByHop, endToEnd);

// Parse incoming message
byte[] messageData = // ... received from network
Command command = Command.parseMessage(ByteBuffer.wrap(messageData));
if (command instanceof final DeviceWatchdogRequest received) {
    DeviceWatchdogAnswer answer = DiameterMessageFactory.createAnswer(
            received, DiameterConstants.RES_DIAMETER_SUCCESS);
}
```

## License

This project is MIT licensed. See the [LICENSE](LICENSE) file for details.

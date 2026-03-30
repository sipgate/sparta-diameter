# Metrics — Tasks

## Implementation

- [ ] Add `io.micrometer:micrometer-core` compile-scope dependency to `sparta-diameter-base/pom.xml`
- [ ] Add `getCommandCode()` to `IncomingCommand` interface; delegate to `Command.getCommandCode()`
- [ ] Implement `DiameterTransportMeters` (decode errors, connection counters, active Gauge)
- [ ] Implement `DiameterSessionMeters` (sent/received counters, error counter, request + handler timers)
- [ ] Add two-constructor pattern to `DiameterNode`; thread `MeterRegistry` into `ChannelInitializer`
- [ ] Instrument `DiameterMessageDecoder` with `DiameterTransportMeters.recordDecodeError()`
- [ ] Instrument `DiameterPeerHandler` with `recordConnected()` / `recordDisconnected()`
- [ ] Add two-constructor pattern to `DiameterSession`, `DiameterInitiatorSession`, `DiameterResponderSession`
- [ ] Extend `PendingRequest` with `timerSample` and `commandCode` fields
- [ ] Instrument `sendAndTrack()`: sent counter + timer start
- [ ] Instrument `complete()`: received counter + timer stop + error counter for E-bit answers
- [ ] Instrument `timeout()` and write-failure listener: error counter
- [ ] Instrument `dispatchInboundRequest()`: received counter + handler timer start/stop + sent counter on answer

## Manual (human only)

- [ ] Author `specs/metrics/grafana-dashboard.json` after meters are confirmed running against real traffic — use [Node Exporter Full](https://grafana.com/grafana/dashboards/1860) as layout reference

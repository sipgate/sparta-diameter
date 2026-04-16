# Metrics

All meters use the `diameter.` prefix and follow [Micrometer naming conventions](https://docs.micrometer.io/micrometer/reference/concepts/naming.html).

| Meter | Type | `application_id` | `command_code` | `command_type` | `direction` | `cause` | Description |
|---|---|:---:|:---:|:---:|:---:|:---:|---|
| `diameter.commands.received` | Counter | ✓ | ✓ | ✓ | | | Incremented for every Diameter command received from the peer after successful decode. |
| `diameter.commands.sent` | Counter | ✓ | ✓ | ✓ | | | Incremented only on confirmed write success; write failures go to `diameter.requests.errors`. |
| `diameter.connections` | Counter | | | | ✓ | | Counts completed TCP handshakes (SYN/ACK). Does NOT indicate a successful Diameter CER/CEA exchange. |
| `diameter.connections.active` | Gauge | | | | | | Total number of currently open TCP connections. |
| `diameter.connections.active.application` | Gauge | ✓ | | | | | Number of currently open TCP connections by application. |
| `diameter.connections.active.direction` | Gauge | | | | ✓ | | Number of currently open TCP connections by direction. |
| `diameter.decode.errors` | Counter | | | | | | Counts messages that could not be parsed. |
| `diameter.disconnections` | Counter | | | | ✓ | | Counts TCP disconnections. Does NOT indicate a clean Diameter DPR/DPA exchange. |
| `diameter.handler.duration` | Timer | ✓ | ✓ | | | | Time from receiving an inbound request to the handler future completing. |
| `diameter.request.duration` | Timer | ✓ | ✓ | | | | Round-trip time for an outbound request, measured until the answer is received. Excludes timed-out and write-failed requests. |
| `diameter.requests.errors` | Counter | ✓ | ✓ | | | ✓ | Error events for outbound requests. |
| `diameter.handler.errors` | Counter | ✓ | ✓ | | | ✓ | Error events for inbound handler failures. |

> Malicious peers can inflate `application_id` and `command_code` values to generate high-cardinality label sets. Defending against this is the application's responsibility via Micrometer's `MeterFilter`. See also ADR-0010 for the design rationale.

## Tag values

| Tag | Values |
|---|---|
| `application_id` | numeric Diameter application ID, e.g. `16777251` |
| `command_code` | numeric command code, e.g. `257`, `280` |
| `command_type` | `request`, `answer` |
| `direction` | `inbound`, `outbound` |
| `cause` | Class.getSimpleName() |

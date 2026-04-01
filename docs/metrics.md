# Metrics

All meters use the `diameter.` prefix and follow [Micrometer naming conventions](https://docs.micrometer.io/micrometer/reference/concepts/naming.html).

| Meter | Type | `application_id` | `command_code` | `command_type` | `direction` | `error_type` |
|---|---|:---:|:---:|:---:|:---:|:---:|
| `diameter.commands.received` | Counter | ✓ | ✓ | ✓ | | |
| `diameter.commands.sent` | Counter | ✓ | ✓ | ✓ | | |
| `diameter.connections` | Counter | | | | ✓ | |
| `diameter.connections.active` | Gauge | (✓) | | | | |
| `diameter.decode.errors` | Counter | | | | | |
| `diameter.disconnections` | Counter | | | | ✓ | |
| `diameter.handler.duration` | Timer | ✓ | ✓ | ✓ | | |
| `diameter.request.duration` | Timer | ✓ | ✓ | ✓ | | |
| `diameter.requests.errors` | Counter | ✓ | ✓ | | | ✓ |

`diameter.connections.active` is a global gauge (no tags) **and** a per-application-id gauge (with `application_id`).

## Tag values

| Tag | Values |
|---|---|
| `application_id` | numeric Diameter application ID, e.g. `16777251` |
| `command_code` | numeric command code, e.g. `257`, `280` |
| `command_type` | `request`, `answer` |
| `direction` | `inbound`, `outbound` |
| `error_type` | `timeout`, `write_failure`, `error_answer` |

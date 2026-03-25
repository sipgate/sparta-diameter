# Vendor-Specific Application-Id — Requirements

## Context

RFC 6733 §5.3 requires that vendor-specific applications (e.g. 3GPP SGd, application-id
`16777313`, vendor `10415`) be advertised via the `Vendor-Specific-Application-Id` grouped AVP
(`Auth-Application-Id` + `Vendor-Id` together), not as bare top-level `Auth-Application-Id` AVPs.
Strict DRAs and 3GPP nodes reject CERs that omit the correct grouping.

The current `DiameterNodeConfig.Capabilities` model holds `authApplicationIds` and
`acctApplicationIds` as flat `List<Integer>` values, which cannot represent vendor-specific
applications correctly.

## Acceptance criteria

- `DiameterNodeConfig.Capabilities` accepts vendor-specific application declarations with at
  least a vendor ID and an application ID
- CER and CEA built from a config that includes vendor-specific apps contain one
  `Vendor-Specific-Application-Id` grouped AVP per declared entry
- Capability negotiation computes the intersection including vendor-specific applications from
  the remote CER
- A node configured with only vendor-specific apps (no bare `authApplicationIds`) sends
  `DIAMETER_NO_COMMON_APPLICATION` when the remote peer declares no matching vendor-specific app

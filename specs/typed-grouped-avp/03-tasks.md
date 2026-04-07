# Typed Grouped AVP — Tasks

## 1. Make GroupedAVP a full AVPContainer

- Add `setAVP(AVP avp)` to `GroupedAVP` — replace-or-add by key, same semantics as `Command`
- Declare `GroupedAVP implements AVPContainer<GroupedAVP>`
- Unit tests for `setAVP`: replaces existing, adds new

## 2. Broaden AVP.readFrom() to instantiate typed subclasses

- Change condition from `definition.dataType().equals(GroupedAVP.class)` to
  `GroupedAVP.class.isAssignableFrom(definition.dataType())`
- Replace `new GroupedAVP(...)` with reflective instantiation via the six-argument
  constructor on `definition.dataType()`
- Unit test: register a typed subclass, decode a matching AVP, assert the returned
  instance is of the typed subclass

## 3. Add LMSI constant and HasLmsiAVP mixin

- Add `AVP_LMSI = 2400` to `_3gppConstants` (TS 29.173)
- Add LMSI to `_3gppAVPProvider` as `byte[].class` (same encoding as MSISDN/MSC-Number)
- Create `HasLmsiAVP<T>` in `_3gpp-common` mixins

## 4. Implement UserIdentifierAVP

- Create `UserIdentifierAVP extends GroupedAVP` implementing
  `HasUserNameAVP`, `HasMsisdnAVP`, `HasExternalIdentifierAVP`, `HasLmsiAVP`
- Change `_3gppAVPProvider` registration for `User-Identifier` from `GroupedAVP.class`
  to `UserIdentifierAVP.class`
- Update `HasUserIdentifierAVP`:
  - `getUserIdentifier()` returns `UserIdentifierAVP`
  - `setUserIdentifier()` accepts `UserIdentifierAVP`
- Unit tests: construct, set each inner field, read back; decode from bytes, assert typed instance

## 5. Implement remaining typed subclasses

One task per candidate from the design; each follows the same checklist as task 4:

- `SmDeliveryFailureCauseAVP` (`sgdgdd`, AVP 3304) — `SM-Enumerated-Delivery-Failure-Cause` + `SM-Diagnostic-Info`
- `SmsMiCorrelationIdAVP` (`sgdgdd`, AVP 3324) — `HasHssIdAVP`, `HasOriginatingSipUriAVP`, `HasDestinationSipUriAVP`
- `SmDeliveryOutcomeAVP` (`_3gpp-common`, AVP 3316) — one of the six node-specific outcome AVPs
- `ServingNodeAVP` (`_3gpp-common`, AVP 2401) — full inner mixin list per TS 29.173
- `ProxyInfoAVP` (`base`, AVP 284) — `HasProxyHostAVP`, `HasProxyStateAVP`
- `VendorSpecificApplicationIdAVP` (`base`, AVP 260) — `HasVendorIdAVP`, `HasAuthApplicationIdAVP`, `HasAcctApplicationIdAVP`
- `ExperimentalResultAVP` (`base`, AVP 297) — `HasVendorIdAVP`, `HasExperimentalResultCodeAVP`

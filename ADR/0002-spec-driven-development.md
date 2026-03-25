---
title: "ADR-0001: Spec-Driven Development"
description: "Feature specifications live in specs/<feature>/ with requirements, design, and tasks files"
owner: "sipgate-uhlig"
status: draft
tags:
  - adr
  - process
  - documentation
  - specs
created: "2026-03-25"
---

## Context

`PLAN.md` grew to mix three distinct concerns:

1. **Completed work** — implementation steps with strikethrough, kept as a historical record
2. **Architectural decisions** — type hierarchies, rejected alternatives, design rationale
3. **Feature specifications** — descriptions of what still needs to be built, with acceptance criteria and implementation steps

These serve different readers at different times. A contributor working on relay support does not need the watchdog state machine history in their context. An AI agent implementing identifier types does not need the reconnect logic in scope.

Architectural decisions belong in `ADR/` (see ADR-0001). Feature specifications are a different artifact — mutable until implemented, then archived. They answer "what does X do and how should it be built?", not "why did we choose X over Y?"

The Kiro pattern (popularized by Amazon's Kiro IDE and documented by Martin Fowler) has become the community standard for organizing feature specifications for agentic development: a per-feature directory with three focused files.

## Decision

Feature specifications live in `specs/<feature-name>/`. Each feature directory contains up to three files:

| File | Purpose |
|---|---|
| `requirements.md` | What the feature must do — behaviour, acceptance criteria, edge cases |
| `design.md` | How it works internally — class design, sequence diagrams, API shape |
| `tasks.md` | Ordered implementation steps, tracked by an agent during execution |

Not every feature needs all three files. Small, well-understood features may have only `tasks.md`. Large features with non-obvious design choices get all three.

> **Guardrail:** `specs/` is for implementation specifications. Architectural decisions — those that document a trade-off made at a crossroads, with rejected alternatives — go in `ADR/` per ADR-0001. When in doubt: if it has a "Rejected alternatives" section, it is an ADR.

`PLAN.md` is reduced to a lightweight index: project context and a status table linking to `specs/` and `ADR/` entries. Completed work is removed — git history is the authoritative record.

## Consequences

- Each feature specification is independently scoped; an agent working on one feature loads only what is relevant
- `PLAN.md` stays short enough to be useful as a session context file
- The distinction between decision records (immutable, in `ADR/`) and specs (mutable, in `specs/`) is explicit and enforced by location
- Completed specs must be explicitly archived or deleted — they do not disappear automatically

## Related ADRs

- **See also:** ADR-0001 (defines the ADR format and the boundary between ADRs and other documentation)

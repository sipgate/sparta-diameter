---
title: "ADR-0001: ADR Format"
description: "Architecture Decision Records in Markdown as the standard for documenting technical decisions"
owner: "sipgate-uhlig"
status: accepted
tags:
  - adr
  - format
  - documentation
created: "2026-03-25"
---

## Context

Technical decisions need to be documented, traceable, and discoverable — by humans reading the history and by AI tools using them as context. Without a canonical format, documentation scatters across commit messages, issues, and memory.

A single format with one authoritative source per decision prevents contradictions and makes onboarding cheaper.

## Decision

Architecture Decision Records (ADRs) in Markdown are the standard for documenting technical decisions in this project.

### Template

Every ADR begins with YAML frontmatter for structured metadata:

```yaml
---
title: "ADR-NNNN: Title"
description: "Core decision in one sentence (max 120 characters)"
owner: "github-handle"
status: draft
tags:
  - adr
  - topic
created: "YYYY-MM-DD"
---
```

There is no `updated` field — git history captures changes.

Title, status, and owner come from the frontmatter and are not repeated in prose. The body follows immediately:

```markdown
## Context
[What is the situation? What problem are we solving?]

## Decision
[What did we decide?]

## Consequences
[What becomes easier? What becomes harder?]
```

### Rules

- **Numbering:** 4-digit, sequential (0001, 0002, …)
- **Location:** `/ADR/` in the repository root
- **Filename:** `NNNN-short-title.md`
- **Owner:** Every ADR has one owner (a GitHub handle), responsible for keeping it current
- **Audience:** Engineering — technical decisions only
- **Language:** English (this project is open source)
- **Changes:** ADRs are never deleted; outdated ones are marked `deprecated` or `superseded`

### Process

1. Submit a new ADR as a pull request
2. Merge and set status to `accepted` after review

### Lifecycle

- **Review cycle:** Owners review their ADRs at least annually
- **Deprecation:** Only the owner marks an ADR as `deprecated`
- **Superseding:** When a new ADR replaces an old one, the old ADR gets status `superseded by ADR-NNNN`
- **Deletion:** Never

### Linking

ADRs may reference each other:

```markdown
## Related ADRs
- **Extends:** ADR-NNNN (this ADR builds on another)
- **Supersedes:** ADR-NNNN (this ADR replaces another)
- **See also:** ADR-NNNN (thematically related)
```

Linking is optional but recommended when a meaningful relationship exists.

Forward references — linking to an ADR with a higher number — are best avoided. If an ADR needs to reference a decision that hasn't been made yet, the current ADR is likely premature or structurally incomplete. This is not a guardrail, but a forward reference is usually a signal to pause and reconsider the scope.

### Guardrails

Non-negotiable rules are highlighted as blockquotes with a **Guardrail:** prefix:

```markdown
> **Guardrail:** Feature specs go in `specs/`, not in `ADR/`.
```

Not every statement in an ADR is a guardrail. The format is reserved for the few rules that are binding and not up for debate.

## Consequences

- Technical decisions have a single canonical source
- New contributors can understand the reasoning behind choices
- AI tools can use ADRs as unambiguous project context
- Owners are responsible for keeping their ADRs accurate

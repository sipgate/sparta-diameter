## What and why

<!-- What changes, and what problem it solves. Link the issue, ADR or spec if there is one. -->

## Checklist

- [ ] `mvn clean verify` passes locally
- [ ] The change is covered by tests
- [ ] `final` on every field, parameter and local; no streams in production code
- [ ] Tests follow `it_<behavior>` naming with GIVEN/WHEN/THEN blocks and AssertJ assertions
- [ ] Javadoc updated on any method or type whose behaviour changed
- [ ] Structural change: relevant ADR read, and a new ADR added if this was a trade-off
- [ ] Breaking API change is called out below

## Breaking changes

<!-- None, or describe what breaks and how consumers migrate. -->

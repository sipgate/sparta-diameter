# Releasing

Maintainers cut releases with [Release: cut a new version](.github/workflows/release.yml) — *Run workflow*
on `main`, giving it the version to publish (`0.1.14`) and the version `main` continues on
(`0.1.15-SNAPSHOT`).

It handles:

- **`CHANGELOG.md`** — moves `[Unreleased]` into a dated section and opens a fresh one. Fails if
  `[Unreleased]` is empty.
- **`pom.xml`** — sets the release version across all modules, and the development version afterwards.
- **git tag** — annotated tag on the release commit.
- **publishing** — pushing the tag triggers
  [publish-release.yml](.github/workflows/publish-release.yml) for Maven Central.

The workflow pushes with the `DEPLOY_KEY` secret, so a deploy key with write access must be
configured for the repository. Contributors add entries to the `[Unreleased]` section of
[CHANGELOG.md](CHANGELOG.md) with their pull request.

Pushes to `main` publish a `-SNAPSHOT` via [publish-snapshot.yml](.github/workflows/publish-snapshot.yml).

# Tasks: Multi-Module Maven Structure

## 1. Restructure root POM

- [ ] Change `<groupId>` from `com.sipgate` to `com.sipgate.sparta`.
- [ ] Change `<packaging>` to `pom`.
- [ ] Add `<modules>` listing all four sub-modules.
- [ ] Move all `<dependencies>` into `<dependencyManagement>`.
- [ ] Move all `<plugin>` config into `<pluginManagement>`.
- [ ] Remove `<build><sourceDirectory>` / test source directory if explicitly set (Maven
  defaults apply to sub-modules).

## 2. Create sparta-diameter-base module

- [ ] Create `sparta-diameter-base/pom.xml` with:
  - `<parent>` pointing to root POM.
  - `<artifactId>sparta-diameter-base</artifactId>`.
  - `<dependencies>` on `reflections` and `netty-all` (no version — inherited from parent
    `<dependencyManagement>`).
  - Test dependencies on `junit-jupiter`, `assertj-core`, `mockito-junit-jupiter`.
- [ ] Move `src/main/java/` and `src/test/java/` from project root into
  `sparta-diameter-base/`.
- [ ] Move `src/test/resources/` if present.

## 3. Rename packages in sparta-diameter-base

- [ ] Add `.base` segment to every package declaration and import in the moved source:
  `com.sipgate.sparta.diameter` → `com.sipgate.sparta.diameter.base`
  (all sub-packages follow: `.core`, `.core.avp`, `.core.avp.mixins`,
  `.messages.rfc6733`, `.session`, `.transport`).
- [ ] Verify no `com.sipgate.sparta.diameter` (without `.base`) references remain in
  `sparta-diameter-base/src/`.

## 4. Create sparta-diameter-3gpp-common module

- [ ] Create `sparta-diameter-3gpp-common/pom.xml` with:
  - `<parent>` pointing to root POM.
  - `<artifactId>sparta-diameter-3gpp-common</artifactId>`.
  - `<dependency>` on `sparta-diameter-base`.
- [ ] Create `sparta-diameter-3gpp-common/src/main/java/.gitkeep`.

## 5. Create sparta-diameter-3gpp-s6c module

- [ ] Create `sparta-diameter-3gpp-s6c/pom.xml` with:
  - `<parent>` pointing to root POM.
  - `<artifactId>sparta-diameter-3gpp-s6c</artifactId>`.
  - `<dependency>` on `sparta-diameter-3gpp-common`.
- [ ] Create `sparta-diameter-3gpp-s6c/src/main/java/.gitkeep`.

## 6. Create sparta-diameter-3gpp-sgdgdd module

- [ ] Create `sparta-diameter-3gpp-sgdgdd/pom.xml` with:
  - `<parent>` pointing to root POM.
  - `<artifactId>sparta-diameter-3gpp-sgdgdd</artifactId>`.
  - `<dependency>` on `sparta-diameter-3gpp-common`.
- [ ] Create `sparta-diameter-3gpp-sgdgdd/src/main/java/.gitkeep`.

## 7. Verify

- [ ] `mvn verify` from the project root passes with no compilation errors and no test
  failures.
- [ ] Confirm `sparta-diameter-base` has no dependency on any `3gpp-*` module
  (check with `mvn dependency:tree`).

## 8. Housekeeping

- [ ] Update `AGENTS.md` package structure example to reflect the `.base` sub-package.
- [ ] Update any specs that reference the old `com.sipgate.sparta.diameter` package root
  without the `.base` segment (check `specs/identifier-types/design.md` in particular).

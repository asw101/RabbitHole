# Text Migration Registry Characterization

The text migration registry safety lane protects old Alice project compatibility
while the migration table is kept maintainable.

This reference describes the hardened feature contract. In that end-state, normal
tests are read-only, JSON regeneration is an explicit maintainer action, and the
legacy Java registries remain the oracle for compatibility.

## Purpose

Alice projects can contain old class names, resource enum names, field names, and
XML fragments. `ProjectMigrationManager` applies `TextMigration` entries in
version order so those projects still open in RabbitHole. The runtime registry is
loaded from:

```text
core/story-api-migration/src/main/resources/migrations/text-migrations.json
```

The legacy Java registry classes remain the regeneration oracle. The
characterization tests prove that the JSON table, legacy registries, and runtime
registry all describe the same ordered migrations before any structural refactor
is accepted.

## Contents

- [Artifacts](#artifacts)
- [Runtime contract](#runtime-contract)
- [JSON format](#json-format)
- [Configuration](#configuration)
- [Run the safety lane](#run-the-safety-lane)
- [Regenerate the JSON table](#regenerate-the-json-table)
- [Changing text migrations](#changing-text-migrations)
- [Representative behavior cases](#representative-behavior-cases)
- [Approval gate](#approval-gate)

## Artifacts

| Artifact | Role |
| --- | --- |
| `TextMigrationRegistry` | Package-private registry assembler used by `ProjectMigrationManager`. It loads JSON by default and can use legacy registries for regeneration/parity tests. |
| `TextMigrationJsonLoader` | Package-private classpath loader for `migrations/text-migrations.json`. |
| `text-migrations.json` | Committed generated migration table used by normal runtime loading. |
| `TextMigrationJsonGeneratorTest` | Deterministic JSON parity test. By default it serializes the legacy registry oracle in memory and compares the result exactly with the committed JSON resource. It writes the resource only when the explicit regeneration property is set. |
| `TextMigrationJsonLoaderTest` | Definition-level parity test between legacy registries and JSON-loaded migrations. It checks index, version, pair order, regex pattern, replacement string, and null/no-replacement semantics. |
| `TextMigrationRegistryTest` | Registry assembly and representative behavior characterization. It protects ordering, version boundaries, fresh arrays, known versions, and selected old-project text rewrites. |
| Test helper code | Optional test-only helper code, such as `TextMigrationTestHelper` if introduced, may consolidate legacy registry assembly, deterministic serialization, migration pair extraction, and behavior comparison. It is not production API. |

## Runtime contract

`TextMigrationRegistry.createAll()` is the single package-local entry point for
assembling text migrations.

By default, it loads `migrations/text-migrations.json` through
`TextMigrationJsonLoader.load()`. If the JSON resource is missing or malformed,
loading fails; there is no silent fallback to legacy registries.

When the system property
`org.lgna.project.migration.TextMigrationRegistry.useLegacyRegistries=true` is
set, `createAll()` assembles the preserved Java registry classes instead:

```text
TextMigrationRegistrySmallVersions.createEarly()
TextMigrationRegistryV3134.create()
TextMigrationRegistrySmallVersions.createMid()
TextMigrationRegistryV3159.create()
TextMigrationRegistryLateVersions.create()
```

That property is for regeneration and characterization only. Production loading
uses the committed JSON table.

## JSON format

The migration JSON is an ordered array. Each migration has a target version and
an ordered replacement list:

```json
[ {
  "version" : "3.1.9.0.0",
  "replacements" : [ {
    "pattern" : "ARMOIRE_CLOTHING",
    "replacement" : null
  }, {
    "pattern" : "org.lgna.story.resources.armoire.ArmoireArtNouveau",
    "replacement" : null
  } ]
}, {
  "version" : "3.1.34.0.0",
  "replacements" : [ {
    "pattern" : "org.lgna.story.Program",
    "replacement" : "org.lgna.story.SProgram"
  } ]
} ]
```

The order is part of the compatibility contract:

1. Migrations are applied in array order.
2. Versions are strictly increasing.
3. Replacement pairs are applied in their listed order within each migration.
4. Regex pattern text is compared exactly, including escaping.
5. Replacement text is compared exactly.
6. `null` replacement values remain Java `null` values and match
   `MigrationManager.NO_REPLACEMENT`. They are not omitted, converted to an empty
   string, or serialized as the literal text `"null"`.

## Configuration

| Setting | Default | Use |
| --- | --- | --- |
| `org.lgna.project.migration.TextMigrationRegistry.useLegacyRegistries` | `false` | Test-only switch that makes `TextMigrationRegistry.createAll()` use legacy Java registries instead of JSON. Generator and parity tests restore the previous property value after use. |
| `org.lgna.project.migration.TextMigrationJsonGenerator.write` | `false` | Maintainer-only regeneration switch for `TextMigrationJsonGeneratorTest`. Without this property, the test compares generated JSON in memory and must not modify `text-migrations.json`. |
| `org.lgna.project.migration.TextMigration.isSanityCheckingDesired` | `false` | Existing diagnostic switch for duplicate-pattern logging inside `TextMigration`. It is not required for the characterization lane. |

No application configuration is required to use the JSON registry at runtime.

## Run the safety lane

Initialize the Tweedle grammar submodule before broad Maven validation:

```bash
git submodule update --init tweedle-lang
```

Run the focused text migration registry characterization tests:

```bash
mvn -pl core/story-api-migration -am \
  -DincludeSims=false \
  -Dinstall4j.skip \
  -Dcheckstyle.skip \
  -Djava.awt.headless=true \
  -Dtest=TextMigrationJsonGeneratorTest,TextMigrationJsonLoaderTest,TextMigrationRegistryTest \
  -Dsurefire.failIfNoSpecifiedTests=false \
  test
```

Run the full `core/story-api-migration` module after changing migration code,
registry classes, loader behavior, or the generated JSON resource:

```bash
mvn -pl core/story-api-migration -am \
  -DincludeSims=false \
  -Dinstall4j.skip \
  -Dcheckstyle.skip \
  -Djava.awt.headless=true \
  test
```

## Regenerate the JSON table

Normal validation is read-only. `TextMigrationJsonGeneratorTest` must compare the
legacy-registry serialization with the committed
`core/story-api-migration/src/main/resources/migrations/text-migrations.json`
without writing the file.

When a migration-table change is intentional, regenerate the committed resource
with the explicit write opt-in:

```bash
git submodule update --init tweedle-lang
mvn -pl core/story-api-migration -am \
  -DincludeSims=false \
  -Dinstall4j.skip \
  -Dcheckstyle.skip \
  -Djava.awt.headless=true \
  -Dtest=TextMigrationJsonGeneratorTest \
  -Dsurefire.failIfNoSpecifiedTests=false \
  -Dorg.lgna.project.migration.TextMigrationJsonGenerator.write=true \
  test
```

The write path must use the same deterministic serializer as the parity test.
Hand-editing `text-migrations.json` is not an accepted regeneration path.

## Changing text migrations

Use this workflow for any intentional migration-table change:

1. Add or strengthen characterization first. The test must show the old-project
   text fragment, the source version boundary, the expected migrated output, and
   any obsolete or intermediate names that must be absent.
2. Change the legacy registry oracle, not the generated JSON by hand.
3. Regenerate JSON only with the explicit write command above. Normal tests
   compare generated bytes in memory and do not write `text-migrations.json`.
4. Review the JSON diff as generated data. A valid diff changes only the entries
   implied by the legacy registry change and keeps formatting deterministic.
5. Run the focused safety lane, then the full `core/story-api-migration` module.
6. Open the pull request against `develop`.
7. Merge only after the local validation and CI merge-ready checks are green.

If the intended behavior is unchanged, `TextMigrationJsonGeneratorTest` must
compare the in-memory regenerated JSON exactly to the committed
`text-migrations.json`. A mismatch means the committed table drifted, the
serialization path changed, or the legacy oracle changed without a reviewed JSON
update.

## Representative behavior cases

Registry hardening must keep at least these representative cases covered:

| Case | What the test proves |
| --- | --- |
| Null/no-replacement | A legacy `MigrationManager.NO_REPLACEMENT` pair remains a JSON `null` and reloads as Java `null`. |
| Simple rename | A direct class rename such as `org.lgna.story.Program` to `org.lgna.story.SProgram` survives JSON generation and loading unchanged. |
| Regex/whitespace rewrite | A regex pattern that spans XML whitespace keeps exact pattern text and replacement text. |
| Sequential pair-order-sensitive rewrite | Replacements that depend on earlier replacements run in the same pair order in JSON-loaded and legacy registries. |
| Version-gated non-application | A rewrite applies to source versions before the migration result version and does not apply at or after that boundary. |

## Examples

### Version-gated rewrite

A project saved before `3.1.20.0.0` that contains:

```text
org.lgna.story.resources.dresser.DresserCentralAsian
```

migrates through the `3.1.20.0.0` entry to:

```text
org.lgna.story.resources.prop.DresserCentralAsian
```

The same rewrite must not be back-applied to projects whose saved version is
already at or after the migration result version.

### JSON and legacy registry parity

The JSON-loaded `3.1.34.0.0` migration and the legacy Java
`TextMigrationRegistryV3134` migration both contain this pair at the same pair
index:

```text
pattern: org.lgna.story.Program
replacement: org.lgna.story.SProgram
```

Changing the pair order is a behavior change because `TextMigration.migrate`
applies replacements sequentially.

### Null/no-replacement entry

The JSON-loaded `3.1.9.0.0` migration keeps this pair as a null replacement:

```text
pattern: ARMOIRE_CLOTHING
replacement: null
```

This matches the legacy `MigrationManager.NO_REPLACEMENT` sentinel exactly. It is
not equivalent to an empty replacement string.

## Approval gate

The review gate is intentionally strict:

| Change | Required proof |
| --- | --- |
| Migration semantics change | Characterization test that documents the old-project fragment, source version, expected output, and intentional difference. |
| JSON table change | Deterministic regeneration from the legacy registry oracle and exact parity proof against the committed resource. |
| Registry structure change | Existing characterization remains green, plus targeted parity cases for any moved or split registry surface. |
| Manual JSON edit | Rejected. Generated migration JSON must come from the explicit write command above. |
| Broad refactor in this lane | Rejected. This lane covers safety, characterization, and parity only. |

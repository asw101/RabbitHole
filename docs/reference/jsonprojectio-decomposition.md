# JsonProjectIo Decomposition

This reference describes the internal decomposition of `JsonProjectIo.java`
(798 lines) into a thin orchestrator plus five package-private helper classes:
`JsonProjectManifest`, `JsonTypeResolver`, `JsonResourceEntryWriter`,
`JsonProjectResourceEntries`, and `ResourceExportNames`.

The decomposition is a pure internal refactor. The public API surface —
`JsonProjectIo.reader(container)` and `JsonProjectIo.writer()` — is unchanged.
All existing read/write behavior, error messages, and exception types are
preserved identically.

## Contents

- [Motivation](#motivation)
- [Architecture](#architecture)
- [Class responsibilities](#class-responsibilities)
  - [JsonProjectIo (orchestrator)](#jsonprojectio-orchestrator)
  - [JsonProjectManifest](#jsonprojectmanifest)
  - [JsonTypeResolver](#jsontyperesolver)
  - [JsonResourceEntryWriter](#jsonresourceentrywriter)
  - [JsonProjectResourceEntries](#jsonprojectresourceentries)
  - [ResourceExportNames](#resourceexportnames)
- [Public API](#public-api)
- [Package-private collaboration](#package-private-collaboration)
- [Security boundary](#security-boundary)
- [Error handling contract](#error-handling-contract)
- [Legacy archive recovery](#legacy-archive-recovery)
- [Configuration](#configuration)
- [Validation](#validation)
- [Acceptance criteria](#acceptance-criteria)
- [Claim boundaries](#claim-boundaries)

## Motivation

The original `JsonProjectIo.java` contained 798 lines mixing five distinct
concerns: manifest parsing, Tweedle type reading/verification, resource entry
writing, resource name sanitization, and project read/write orchestration. This
made the class difficult to navigate, review, and extend safely.

RabbitHole issue #569 decomposes JsonProjectIo into focused helpers without
changing observable behavior. Each helper is small enough to understand in
isolation and extend independently.

## Architecture

```text
JsonProjectIo (public facade — unchanged, 333 lines)
├── JsonProjectReader (inner class, implements ProjectReader)
│   ├── uses JsonProjectManifest (static manifest read/query)
│   ├── uses JsonTypeResolver (instance, type reading/verification)
│   └── reads resources directly (readResources, readResource)
├── JsonProjectWriter (inner class, implements ProjectWriter)
│   ├── uses JsonResourceEntryWriter (instance, entry creation)
│   ├── uses JsonProjectManifest (static manifest/version data sources)
│   └── delegates model I/O to JsonModelIo / JsonPersonIo
├── JsonProjectManifest (package-private, static utilities, 105 lines)
│   └── Manifest read, name query, version/manifest data sources
├── JsonTypeResolver (package-private, 338 lines)
│   ├── TypeReadResult (inner class, decode result accumulator)
│   ├── TypeReadPlan (inner class, reference/terminal collection)
│   └── Tweedle type reading, verification, fallback resolution
├── JsonResourceEntryWriter (package-private, 239 lines)
│   ├── ResourceNames (inner record-like class)
│   └── Type/resource entry creation, name sanitization
├── JsonProjectResourceEntries (package-private, 72 lines)
│   └── Resource archive entry generation with collision avoidance
└── ResourceExportNames (package-private, 139 lines)
    └── File name sanitization, entry name validation, path safety
```

All classes live in `org.lgna.project.io`. The helper classes are
package-private with no public constructors. They are instantiated only by
`JsonProjectReader` and `JsonProjectWriter`.

## Class responsibilities

### JsonProjectIo (orchestrator)

| Responsibility | Methods |
| --- | --- |
| Factory methods | `reader(ZipEntryContainer)`, `writer()` |
| Read orchestration | `JsonProjectReader.readProject(boolean)`, `readType()`, `checkForFutureVersion()` |
| Write orchestration | `JsonProjectWriter.writeProject(OutputStream, Project, DataSource...)`, `writeType(OutputStream, NamedUserType, DataSource...)` |
| Legacy recovery | `isUnsupportedLegacyProgramArchive(...)`, `isLegacyProgramArchive(...)`, `hasExactlyOneRecoverableImageReference(...)`, `hasExactlyOneRecoveredImageResource(...)`, `unsupportedLegacyJsonProjectArchive(...)` |
| Resource reading | `readResources(Manifest)`, `readResource(ResourceReference)` |
| Version reading | `readSourceProgramVersion()` |
| Resource deserialization | `requireUuid(...)`, `applyResourceReference(...)` |

The orchestrator owns legacy recovery logic because it requires access to both
manifest names and resource reading — a cross-cutting concern that does not
belong in any single helper. Resource reading stays in the reader because it
needs direct `container.getInputStream` access with full error context.

### JsonProjectManifest

| Responsibility | Methods |
| --- | --- |
| Read manifest from archive | `readManifest(ZipEntryContainer, Class<M>)` |
| Manifest name access | `manifestName(Manifest)` |
| Manifest name guard | `hasNoManifestName(Manifest)` |
| Scene camera type | `sceneCameraType(ProjectManifest)` |
| Version data source | `versionDataSource()` |
| Manifest data source | `manifestDataSource(Manifest)` |

All methods are `static`. The class has a private constructor and no state.
`readManifest` is generic — it reads either `ProjectManifest` or `TypeManifest`
depending on the caller's archive context.

```java
// Reading a project manifest
ProjectManifest manifest = JsonProjectManifest.readManifest(container, ProjectManifest.class);

// Querying the manifest name
String name = JsonProjectManifest.manifestName(manifest);

// Creating a manifest entry for writing
DataSource manifestEntry = JsonProjectManifest.manifestDataSource(manifest);
DataSource versionEntry = JsonProjectManifest.versionDataSource();
```

### JsonTypeResolver

| Responsibility | Methods |
| --- | --- |
| Type reading | `readTypes(Manifest, boolean)` |
| Single type decode | `readTweedleType(TypeReference, Set<AbstractDeclaration>, boolean)` (private) |
| Unnamed manifest fallback | `fallbackTypeForUnnamedManifest(Manifest, TypeReadResult)` |
| Type archive verification | `verifyTypeArchiveHasExpectedType(Manifest, TypeReadResult)` |
| Project archive verification | `verifyProjectArchiveHasExpectedProgramType(Manifest, TypeReadResult)` |
| Unsupported type check | `verifyArchiveHasNoUnsupportedManifestTypes(String, TypeReadResult)` |
| Read plan construction | `typeReadPlan(Manifest)` (private) |
| Error formatting | `decodedTypeNames(...)`, `unsupportedTypeNamesClause(...)`, `unsupportedDecodeReasonsClause(...)`, `typeReferenceContext(...)` (private) |

Constructor takes `ZipEntryContainer` and `TweedleEncoderDecoder`. The
verification methods are `static` — they operate on `TypeReadResult` without
needing instance state.

**TypeReadResult** accumulates successfully decoded types and records
unsupported Tweedle decode failures by type name. It provides:

| Method | Purpose |
| --- | --- |
| `add(NamedUserType)` | Record a successfully decoded type |
| `findByName(String)` | Look up a decoded type by name |
| `addUnsupportedTweedleType(TypeReference, UnsupportedTweedleDecodeException)` | Record an unsupported decode failure |
| `hasUnsupportedTweedleDecodeFor(String)` | Check if a specific type failed decoding |
| `unsupportedTweedleDecodeCauseFor(String)` | Get the exception for a failed type |
| `hasUnsupportedTweedleTypes()` | Any unsupported types present? |
| `unsupportedTweedleTypeNames()` | Sorted bracket-delimited list of failed names |
| `unsupportedTweedleDecodeReasons()` | Sorted bracket-delimited name:reason pairs |

Unsupported Tweedle decode reason messages are truncated to 512 characters
(`MAX_UNSUPPORTED_TWEEDLE_REASON_LENGTH`) and collapsed to single-line form.

**TypeReadPlan** collects `TypeReference` entries and pre-creates terminal
`NamedUserType` stubs for each named reference, used by the decoder for
forward-reference resolution.

```java
// Reading types from a project archive
JsonTypeResolver resolver = new JsonTypeResolver(container, new TweedleEncoderDecoder());
JsonTypeResolver.TypeReadResult result = resolver.readTypes(manifest, true);
NamedUserType programType = result.findByName("MyProgram");

// Verification after reading
JsonTypeResolver.verifyProjectArchiveHasExpectedProgramType(manifest, result);
JsonTypeResolver.verifyArchiveHasNoUnsupportedManifestTypes("Project archive", result);
```

### JsonResourceEntryWriter

| Responsibility | Methods |
| --- | --- |
| Type manifest creation | `createTypeManifest(AbstractType)` |
| Type entry creation | `createEntriesForTypes(Manifest, Set<NamedUserType>, Set<String>)` |
| Single type entry | `dataSourceForType(Manifest, NamedUserType)`, `dataSourceForType(Manifest, NamedUserType, Set<String>)` |
| Resource type entries | `createEntriesForResourceTypes(Manifest, Set<JointedModelResource>)` |
| Dynamic resource entry | `createEntryForResourceTypes(Manifest, DynamicResource)` |
| Single resource entry | `dataSourceForResource(Manifest, JointedModelResource)` |
| Name sanitization | `temporarilySanitizeResourceNames(NamedUserType)`, `restoreResourceNames(Map)` |
| Resource collection | `getResources(AbstractType, CrawlPolicy)` |
| Entry collection | `collectEntries(Manifest, Set<Resource>, DataSource[])` |
| Manifest name tracking | `manifestResourceNames(Manifest)` |
| Resource comparison | `compareResources(Set<Resource>, Set<Resource>)` |

Constructor takes `TweedleEncoderDecoder`. Instance methods that encode types
use the coder; static methods handle collection and comparison logic.

**ResourceNames** is a package-private value holder preserving the original
`name` and `originalFileName` of a resource during temporary sanitization.

**Name sanitization** works by crawling a `NamedUserType` for
`ResourceExpression` nodes, replacing absolute-path names with safe alternatives
via `ResourceExportNames`, encoding the type, then restoring original names.
This ensures archive entries contain safe file names without mutating the
in-memory model permanently.

```java
// Writing a single type archive
JsonResourceEntryWriter entryWriter = new JsonResourceEntryWriter(new TweedleEncoderDecoder());
TypeManifest manifest = JsonResourceEntryWriter.createTypeManifest(type);
Set<Resource> resources = JsonResourceEntryWriter.getResources(type, CrawlPolicy.EXCLUDE_REFERENCES_ENTIRELY);

List<DataSource> entries = JsonResourceEntryWriter.collectEntries(manifest, resources, dataSources);
entries.add(entryWriter.dataSourceForType(manifest, type));
entries.add(JsonProjectManifest.manifestDataSource(manifest));
```

### JsonProjectResourceEntries

| Responsibility | Methods |
| --- | --- |
| Resource entry generation | `addResources(Manifest, List<DataSource>, Set<Resource>)` |
| Reference creation | `addResourceReference(Manifest, Resource, String)` (private) |
| Type dispatch | `resourceReference(Resource)` (private) |
| Name collision avoidance | `generateEntryName(Resource, Set<String>, Map<String, Integer>)` (private) |
| Entry naming | `potentialEntryName(String, int)` (private) |

All methods are `static`. Resource entries are placed under `resources/` with
automatic collision avoidance: `resources/image.png`, `resources2/image.png`,
`resources3/image.png`, etc.

### ResourceExportNames

| Responsibility | Methods |
| --- | --- |
| Entry file name | `entryFileName(Resource)` |
| Metadata name sanitization | `metadataName(String, String)` |
| Original file name sanitization | `metadataOriginalFileName(String, String)` |
| File name from entry | `fileNameFromEntry(String)` |
| Diagnostic display name | `diagnosticName(Resource)` |
| Resource entry validation | `isResourceEntryName(String)` |
| Source entry validation | `isSourceEntryName(String)` |

All methods are `static`. Path validation rejects absolute paths, backslashes,
`.` / `..` segments, and Windows drive prefixes. This is the security-critical
layer for archive entry name validation.

## Public API

The public API is exclusively `JsonProjectIo` with its two factory methods.
No API changes are made by this decomposition.

```java
public class JsonProjectIo extends DataSourceIo implements ProjectIo {
  public static JsonProjectReader reader(ZipEntryContainer container);
  public static JsonProjectWriter writer();
}
```

`JsonProjectReader` implements `ProjectReader`:

```java
static class JsonProjectReader implements ProjectReader {
  Project readProject(boolean makeVrReady) throws IOException;
  TypeResourcesPair readType() throws IOException;
  Version checkForFutureVersion() throws IOException;
  void setResourceTypeHelper(ResourceTypeHelper typeHelper);
}
```

`JsonProjectWriter` implements `ProjectWriter`:

```java
static class JsonProjectWriter implements ProjectWriter {
  void writeType(OutputStream os, NamedUserType type, DataSource... dataSources) throws IOException;
  void writeProject(OutputStream os, Project project, DataSource... dataSources) throws IOException;
}
```

All five helper classes are package-private. Callers never see them.

## Package-private collaboration

The helpers access each other via package-private visibility. Cross-class
method calls:

| Caller | Callee | Methods |
| --- | --- | --- |
| `JsonProjectReader` | `JsonProjectManifest` | `readManifest`, `manifestName`, `sceneCameraType` |
| `JsonProjectReader` | `JsonTypeResolver` | `readTypes`, `verifyProjectArchiveHasExpectedProgramType`, `verifyTypeArchiveHasExpectedType`, `verifyArchiveHasNoUnsupportedManifestTypes`, `fallbackTypeForUnnamedManifest` |
| `JsonProjectWriter` | `JsonResourceEntryWriter` | `createTypeManifest`, `getResources`, `collectEntries`, `compareResources`, `manifestResourceNames`, `createEntriesForTypes`, `createEntriesForResourceTypes`, `createEntryForResourceTypes`, `dataSourceForType` |
| `JsonProjectWriter` | `JsonProjectManifest` | `manifestDataSource` |
| `JsonResourceEntryWriter` | `JsonProjectManifest` | `versionDataSource` |
| `JsonResourceEntryWriter` | `JsonProjectResourceEntries` | `addResources` |
| `JsonResourceEntryWriter` | `ResourceExportNames` | `entryFileName`, `metadataName`, `metadataOriginalFileName`, `diagnosticName` |
| `JsonTypeResolver` | `JsonProjectManifest` | `manifestName`, `hasNoManifestName` |
| `JsonProjectReader` (resource reading) | `ResourceExportNames` | `isResourceEntryName` |
| `JsonTypeResolver` | `ResourceExportNames` | `isSourceEntryName` |

No interfaces or inheritance are introduced between helpers. All collaboration
uses direct static or instance method calls within the same package.

## Security boundary

Archive entry name validation is centralized in `ResourceExportNames`. Both
read and write paths use this class to enforce safe entry names:

- **Read path**: `isResourceEntryName(entry)` validates resource entries are
  under `resources/` with no path traversal. `isSourceEntryName(entry)`
  validates type entries are under `src/`.
- **Write path**: `metadataName` and `metadataOriginalFileName` sanitize
  resource names before encoding, stripping absolute paths and replacing
  path separators.
- **Path traversal prevention**: `isSafeRelativeEntryName` rejects absolute
  paths, backslashes, `.`/`..` segments, and Windows drive prefixes.

These validation methods stay centralized in `ResourceExportNames` and are
never duplicated in other classes.

## Error handling contract

All `IOException` messages and exception chains are preserved identically
from the original 798-line class. Error-producing methods moved with their
context:

| Error scenario | Class | Method |
| --- | --- | --- |
| Unparseable manifest entry | `JsonProjectManifest` | `readManifest` |
| Unsupported type format | `JsonTypeResolver` | `readTweedleType` |
| Missing type archive entry | `JsonTypeResolver` | `readTweedleType` |
| Entry outside `src/` | `JsonTypeResolver` | `readTweedleType` |
| Null decode result | `JsonTypeResolver` | `readTweedleType` |
| Non-user-type decode | `JsonTypeResolver` | `readTweedleType` |
| Tweedle decode failure | `JsonTypeResolver` | `readTweedleType` (wraps RuntimeException) |
| Version not supported | `JsonTypeResolver` | `readTweedleType` (wraps VersionNotSupportedException) |
| Expected type missing | `JsonTypeResolver` | `verifyArchiveHasExpectedType` |
| Unsupported manifest types | `JsonTypeResolver` | `verifyArchiveHasNoUnsupportedManifestTypes` |
| Missing resource UUID | `JsonProjectIo` | `requireUuid` |
| Missing resource file | `JsonProjectIo` | `readResource` |
| Entry outside `resources/` | `JsonProjectIo` | `readResource` |
| Missing resource data | `JsonProjectIo` | `readResource` |
| Unsupported legacy archive | `JsonProjectIo` | `unsupportedLegacyJsonProjectArchive` |
| Missing version entry | `JsonProjectIo` | `readSourceProgramVersion` |

Characterization tests written before the extraction enforce that all error
messages remain identical after the refactor.

## Legacy archive recovery

Legacy `.a3p` archives that declare a `Program` type with an unsupported
Tweedle decode receive special handling. The recovery path:

1. Check `isUnsupportedLegacyProgramArchive` — manifest declares `Program`,
   file type is export, and the type has an `UnsupportedTweedleDecodeException`.
2. Check `hasExactlyOneRecoverableImageReference` — manifest contains exactly
   one `TypeReference` named "Program" and exactly one `ImageReference`.
3. Attempt to read resources. If exactly one `ImageResource` is recovered,
   return a `Project` with `null` program type and the recovered resources.
4. If any check fails, throw `IOException` with the unsupported legacy message
   and the original decode exception as cause.

This recovery logic stays in `JsonProjectReader` because it combines manifest
inspection, resource reading, and project construction — a cross-cutting
concern that would create circular dependencies if split across helpers.

## Configuration

There is no runtime configuration for the JsonProjectIo decomposition. It uses
the existing Tweedle grammar, Maven reactor, and JUnit configuration.

From a fresh checkout or worktree, initialize the Tweedle grammar submodule
before Maven validation:

```bash
git submodule update --init tweedle-lang
test -d tweedle-lang/Grammar
```

## Validation

Run the story-api-migration tests from the repository root:

```bash
mvn -pl core/story-api-migration -am \
  -DfailIfNoTests=false \
  -Dcheckstyle.skip \
  test
```

The existing `IoUtilitiesTest` suite (212 tests) exercises project reading,
type reading, resource round-tripping, and archive validation. All tests pass
without modification — the extraction is behavior-preserving.

## Acceptance criteria

| Criterion | Status |
| --- | --- |
| `JsonProjectIo.java` under 500 lines | ✅ 333 lines |
| `JsonProjectManifest` extracted | ✅ 105 lines |
| `JsonTypeResolver` extracted | ✅ 338 lines |
| `JsonResourceEntryWriter` extracted | ✅ 239 lines |
| `JsonProjectResourceEntries` extracted | ✅ 72 lines |
| `ResourceExportNames` extracted | ✅ 139 lines |
| No public API changes | ✅ Same `reader()` / `writer()` factory methods |
| All helper classes package-private | ✅ No `public` classes added |
| All existing tests pass | ✅ `mvn -pl core/story-api-migration -am test` |
| Error messages preserved | ✅ Verified by characterization tests |
| Security validations preserved | ✅ `ResourceExportNames` centralized |

## Claim boundaries

This decomposition **does not**:

- Change the public API of `JsonProjectIo`, `ProjectReader`, or `ProjectWriter`
- Add any new public classes, methods, or fields
- Modify manifest format, archive layout, or Tweedle encoding
- Alter error messages, exception types, or exception chains
- Introduce interfaces or inheritance between helper classes
- Add runtime configuration options
- Change legacy archive recovery behavior
- Modify any classes outside `org.lgna.project.io`

This decomposition **does**:

- Reduce `JsonProjectIo.java` from 798 to 333 lines (58% reduction)
- Create five focused package-private helper classes
- Make each concern independently testable and reviewable
- Centralize archive entry name validation in `ResourceExportNames`
- Preserve all existing behavior verified by 212 existing tests

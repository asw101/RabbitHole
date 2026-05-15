# SecureXmlParser Extraction from XmlProjectIo

This reference describes the extraction of XXE-hardened XML parsing and resource
creation utilities from `XmlProjectIo.java` (566 lines) into a new
package-private helper class `SecureXmlParser.java` (169 lines), reducing
`XmlProjectIo` to 466 lines.

The extraction is a pure internal refactor. The public API surface —
`XmlProjectIo` — is unchanged. All existing read/write behavior, error
messages, and security properties are preserved identically.

## Contents

- [Motivation](#motivation)
- [Architecture](#architecture)
- [Class responsibilities](#class-responsibilities)
  - [XmlProjectIo (coordinator)](#xmlprojectio-coordinator)
  - [SecureXmlParser (extracted helper)](#securexmlparser-extracted-helper)
- [Public API](#public-api)
- [Package-private collaboration](#package-private-collaboration)
- [Security boundary](#security-boundary)
- [Error handling contract](#error-handling-contract)
- [Configuration](#configuration)
- [Validation](#validation)
- [Examples](#examples)
- [Acceptance criteria](#acceptance-criteria)
- [Claim boundaries](#claim-boundaries)

## Motivation

The original `XmlProjectIo.java` contained 566 lines mixing four distinct
concerns: archive-level project read/write orchestration, XXE-hardened XML
parsing, version-aware character encoding, and reflective resource
instantiation. The XML parsing concern is security-critical (7-layer XXE
defense) and deserves a single-purpose home where it can be reviewed, tested,
and audited in isolation.

RabbitHole issue #656 extracts the parsing and resource utilities into
`SecureXmlParser`, mirroring the delegate extraction pattern used in the
[Decoder delegate decomposition](./decoder-delegate-decomposition.md) and
[Encoder delegate decomposition](./encoder-delegate-decomposition.md).

## Architecture

```text
XmlProjectIo (package-private coordinator, 466 lines)
├── XmlProjectReader (inner class)
│   ├── readProject(), readType(), readResources()
│   ├── readResourceData(), bindResourceExpressions()
│   └── delegates XML parsing to SecureXmlParser
├── XmlProjectWriter (inner class)
│   └── writeVersion(), writeXML(), writeType(), writeResources()
└── SecureXmlParser (package-private static helper, 169 lines)
    ├── readArchiveXml()       — XXE-hardened DocumentBuilder
    ├── readXML()              — version-aware migration + parse
    ├── removeWhitespaceNodes() — DOM whitespace cleanup
    ├── isXmlWhitespace()      — whitespace classification
    ├── getCharsetForVersion() — UTF-8/UTF-16 version switch
    ├── resourceContext()      — diagnostic string builder
    └── createResource()       — reflective Resource instantiation
```

Both classes live in `org.lgna.project.io`. `SecureXmlParser` is
package-private with a private constructor. It is never instantiated — all
methods are static.

## Class responsibilities

### XmlProjectIo (coordinator)

| Responsibility | Methods |
| --- | --- |
| Archive reading | `XmlProjectReader.readProject()` |
| Type reading | `XmlProjectReader.readType()` |
| Resource reading | `XmlProjectReader.readResources()`, `readResourceData()` |
| Resource binding | `XmlProjectReader.bindResourceExpressions()` |
| Version reading | `XmlProjectReader.readSourceProgramVersion()` |
| Archive writing | `XmlProjectWriter.writeProject()` |
| Type writing | `XmlProjectWriter.writeType()` |
| Resource writing | `XmlProjectWriter.writeResources()` |
| Entry-name generation | `generateEntryName()` |
| Constants | `RESOURCES_ENTRY_NAME`, `XML_RESOURCE_*` attribute names |

The coordinator owns archive-level orchestration: opening zip entries, reading
version headers, sequencing type + resource reads, and writing zip entries. It
delegates all XML document parsing to `SecureXmlParser`.

### SecureXmlParser (extracted helper)

| Responsibility | Method | Lines |
| --- | --- | --- |
| XXE-hardened XML parsing | `readArchiveXml(InputStream, String)` | 78–96 |
| Whitespace node removal | `removeWhitespaceNodes(Element)` | 98–108 |
| Whitespace classification | `isXmlWhitespace(String)` | 110–118 |
| Version-aware migration + parse | `readXML(InputStream, String, MigrationManager, Version)` | 120–132 |
| Charset selection | `getCharsetForVersion(Version)` | 135–137 |
| Diagnostic context string | `resourceContext(Element, String)` | 139–152 |
| Reflective resource creation | `createResource(Class, String)` | 154–168 |

All methods are `static` and package-private. No mutable state.

## Public API

The public API is exclusively `XmlProjectIo` and its `XmlProjectReader` /
`XmlProjectWriter` inner classes (implementing the `ProjectReader` /
`ProjectWriter` interfaces, returned through factory methods on
`XmlProjectIo`). No API changes are made by this extraction.

```java
// Read path — unchanged
ProjectIo.readProject(File) → calls XmlProjectReader internally
ProjectIo.readType(File)    → calls XmlProjectReader internally

// Write path — unchanged
ProjectIo.writeProject(File, Project) → calls XmlProjectWriter internally
```

Callers never see `SecureXmlParser`. It has no public constructors, no public
methods, and is not exported.

## Package-private collaboration

`XmlProjectIo.XmlProjectReader` calls `SecureXmlParser` static methods
directly. The following call sites exist:

| Call site in XmlProjectReader | SecureXmlParser method |
| --- | --- |
| `readXML(entryName, migrationManager, decodedVersion)` | `SecureXmlParser.readXML(...)` |
| `readResources()` — XML document parse | `SecureXmlParser.readArchiveXml(...)` |
| `readResources()` — resource instantiation | `SecureXmlParser.createResource(...)` |
| `readResources()` — error context | `SecureXmlParser.resourceContext(...)` |
| `readResourceData()` — error context (4 call sites) | `SecureXmlParser.resourceContext(...)` |

No interfaces or inheritance are introduced. All collaboration uses direct
static method calls within the same package.

## Security boundary

The 7-layer XXE defense in `readArchiveXml` is the single XML parsing entry
point for the entire `org.lgna.project.io` package. The defense consists of:

```java
// 1. Secure processing mode
documentBuilderFactory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
// 2. Disallow DOCTYPE declarations entirely
documentBuilderFactory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
// 3. Disable external general entities
documentBuilderFactory.setFeature("http://xml.org/sax/features/external-general-entities", false);
// 4. Disable external parameter entities
documentBuilderFactory.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
// 5. Disable external DTD loading
documentBuilderFactory.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
// 6. Disable XInclude processing
documentBuilderFactory.setXIncludeAware(false);
// 7. Disable entity reference expansion
documentBuilderFactory.setExpandEntityReferences(false);
```

These seven layers are copied character-for-character from the original
`XmlProjectReader` inner class. They must not be simplified, reordered, or
removed individually. The `SecureXmlParserTest.readArchiveXml_rejectsXxePayload`
test verifies that a malicious DOCTYPE with a `file:///etc/passwd` entity
reference is rejected with an `IOException`.

The `createResource` method uses `constructor.setAccessible(true)` for
reflective instantiation. This is preserved as-is from the original code and
is necessary because `Resource` subclasses have package-private constructors
taking a `UUID` parameter.

## Error handling contract

All error messages are preserved character-for-character from the original
`XmlProjectIo`. The following error messages exist in `SecureXmlParser`:

| Method | Error condition | Message pattern |
| --- | --- | --- |
| `readArchiveXml` | Parse failure | `"Unable to read " + entryName` |
| `createResource` | Invalid UUID | `"Invalid resource UUID " + uuidText` |
| `createResource` | Reflective failure | `"Unable to create resource " + className + " with UUID " + uuidText` |
| `resourceContext` | (diagnostic helper) | `"resource 'name' at archive entry 'entry'"` |

Error messages in `XmlProjectReader` that reference `SecureXmlParser.resourceContext()`
are also preserved exactly:

| Call site | Message pattern |
| --- | --- |
| `readResources()` | `"Unknown resource class '" + className + "' for " + resourceContext(...)` |
| `readResourceData()` | `"Resource data entry outside resources directory for " + resourceContext(...)` |
| `readResourceData()` | `"Missing resource data for " + resourceContext(...)` |
| `readResourceData()` | `"Unable to read resource data for " + resourceContext(...)` |

## Configuration

There is no runtime configuration for the SecureXmlParser extraction. It uses
the existing Maven reactor, JUnit configuration, and XML parsing libraries
already on the classpath.

From a fresh checkout or worktree, initialize the Tweedle grammar submodule
before Maven validation:

```bash
git submodule update --init tweedle-lang
test -d tweedle-lang/Grammar
```

## Validation

Run the focused SecureXmlParser unit tests:

```bash
NODE_OPTIONS=--max-old-space-size=32768 mvn -pl core/story-api-migration -am \
  -DfailIfNoTests=false \
  -Dsurefire.failIfNoSpecifiedTests=false \
  -Dtest=SecureXmlParserTest \
  test
```

Run the full story-api-migration test suite (includes characterization and
round-trip tests):

```bash
NODE_OPTIONS=--max-old-space-size=32768 mvn -pl core/story-api-migration -am \
  -DfailIfNoTests=false \
  test
```

Run the existing starter-project XML fallback readability test:

```bash
NODE_OPTIONS=--max-old-space-size=32768 mvn -pl core/story-api-migration -am \
  -DfailIfNoTests=false \
  -Dsurefire.failIfNoSpecifiedTests=false \
  -Dtest=StarterProjectXmlFallbackReadabilityTest \
  test
```

All three suites must pass with identical results before and after the
extraction.

## Examples

### Reading an Alice project archive (internal call flow)

When a user opens an `.a3p` project file, the read path flows through
`SecureXmlParser` transparently:

```text
ProjectIo.readProject(file)
  → XmlProjectIo.XmlProjectReader.readProject()
    → readXML("program.xml", migrationManager, version)
      → SecureXmlParser.readXML(inputStream, "program.xml", mgr, version)
        → SecureXmlParser.getCharsetForVersion(version)   // UTF-8 or UTF-16
        → migrationManager.migrate(xmlText, version)      // text migrations
        → SecureXmlParser.readArchiveXml(migratedStream)   // XXE-safe parse
          → SecureXmlParser.removeWhitespaceNodes(root)    // DOM cleanup
    → readResources()
      → SecureXmlParser.readArchiveXml(resourcesStream)
      → SecureXmlParser.createResource(cls, uuid)          // per resource
```

### Version-aware charset selection

Project XML encoding changed from UTF-8 to UTF-16 in Alice 3.7:

```text
Version 3.6.x → SecureXmlParser.getCharsetForVersion() → UTF-8
Version 3.7.0 → SecureXmlParser.getCharsetForVersion() → UTF-16
Version 3.8.x → SecureXmlParser.getCharsetForVersion() → UTF-16
```

### XXE rejection

Any XML document containing a DOCTYPE declaration is rejected immediately,
before entity expansion occurs:

```text
Input:  <?xml version="1.0"?><!DOCTYPE foo [<!ENTITY xxe SYSTEM "file:///etc/passwd">]><root>&xxe;</root>
Result: IOException("Unable to read test.xml")
Cause:  SAXException from disallow-doctype-decl feature
```

### Resource diagnostic context

When resource loading fails, `SecureXmlParser.resourceContext()` builds a
human-readable diagnostic string:

```text
Element with name="myTexture", uuid="abc-123", entry="resources/myTexture.png"
→ "resource 'myTexture' at archive entry 'resources/myTexture.png'"

Element with name="", uuid="abc-123", entry="resources/file.png"
→ "resource with UUID 'abc-123' at archive entry 'resources/file.png'"
```

## Acceptance criteria

| Criterion | Verification |
| --- | --- |
| `XmlProjectIo.java` ≤ 500 lines | `wc -l XmlProjectIo.java` → 466 |
| `SecureXmlParser.java` created | exists at `core/story-api-migration/src/main/java/org/lgna/project/io/SecureXmlParser.java` |
| `SecureXmlParserTest.java` created | exists at `core/story-api-migration/src/test/java/org/lgna/project/io/SecureXmlParserTest.java` |
| `SecureXmlParser` is package-private | No `public` keyword on class declaration |
| All methods are static package-private | No `public` methods; all `static` |
| No public API changes to `XmlProjectIo` | `XmlProjectIo.java` public/protected method signatures unchanged |
| XXE rejection tested | `readArchiveXml_rejectsXxePayload` passes |
| Whitespace removal tested | `readArchiveXml_stripsWhitespaceNodes` passes |
| Charset version boundary tested | `getCharsetForVersion_*` tests pass |
| Resource context tested | `resourceContext_*` tests pass |
| Invalid UUID tested | `createResource_rejectsInvalidUuid` passes |
| `mvn -pl core/story-api-migration -am test` passes | Zero test failures |
| 7-layer XXE defense preserved verbatim | Code review: character-for-character match |

## Claim boundaries

This extraction proves:

- The 566-line `XmlProjectIo` can be reduced to 466 lines by extracting
  XML parsing into a focused, testable helper class.
- The 7-layer XXE defense is preserved identically in `SecureXmlParser`.
- All existing `StarterProjectXmlFallbackReadabilityTest` assertions pass.
- All existing `HistoricalArchiveRoundTripCharacterizationTest` assertions pass.
- All existing `core/story-api-migration` tests pass identically.
- Error messages and exception types are preserved.
- XXE payloads are rejected by the extracted parser.

This extraction does **not** prove:

| Non-claim | Reason |
| --- | --- |
| New parsing capabilities | No new XML features are supported. |
| Performance improvement | Extraction is structural, not algorithmic. |
| Thread safety | `XmlProjectIo` was not thread-safe before; extraction does not change this. |
| Public API expansion | No new public methods or classes are introduced. |
| Write-path changes | `XmlProjectWriter` is not modified. |
| Migration logic changes | `MigrationManager` behavior is unchanged. |

Adjacent claims are owned by their own documents:

| Claim | Document |
| --- | --- |
| Project I/O corpus characterization | [Project IO Corpus Characterization](./project-io-corpus-characterization.md) |
| Project migration manager characterization | [Project Migration Manager Characterization](./project-migration-manager-characterization.md) |
| Project backup and recovery | [Project Backup Recovery IO](./project-backup-recovery-io.md) |
| Save/export operations | [Project Save Export Operations](./project-save-export-operations.md) |

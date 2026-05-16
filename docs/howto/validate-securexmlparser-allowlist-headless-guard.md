# Validate SecureXmlParser Allowlist and Headless Guard

Use this guide to verify the allowlist regression fix (issue #664) and the
WindowStack/Frame headless guard after checkout or merge.

## Contents

- [Prerequisites](#prerequisites)
- [Run the SecureXmlParser unit tests](#run-the-securexmlparser-unit-tests)
- [Run the previously-failing integration tests](#run-the-previously-failing-integration-tests)
- [Verify headless behavior manually](#verify-headless-behavior-manually)
- [Verify allowlist coverage](#verify-allowlist-coverage)
- [Review the result](#review-the-result)

## Prerequisites

Run commands from the repository root. Initialize the grammar submodule before
Maven validation:

```bash
git submodule update --init tweedle-lang
test -d tweedle-lang/Grammar
```

## Run the SecureXmlParser unit tests

This suite includes the allowlist assertion for `org.alice.*`, the XXE rejection
test, and the malformed-XML test:

```bash
mvn -pl core/story-api-migration -am \
  -DfailIfNoTests=false \
  -Dsurefire.failIfNoSpecifiedTests=false \
  -Dtest=SecureXmlParserTest \
  test
```

Expected: all tests pass including `createResource_rejectsDisallowedPackage`
which asserts `isAllowedResourcePackage("org.alice.ide.SomeResource")` returns
`true`.

## Run the previously-failing integration tests

These tests load real temporary `.a3p` project files containing `org.alice.*`
resources. Before the fix, they threw `IOException` because the allowlist
rejected `org.alice.*` classes:

```bash
mvn -pl core/ide -am \
  -DfailIfNoTests=false \
  -Dsurefire.failIfNoSpecifiedTests=false \
  -Dtest=ProjectBackupRecoveryIoTest \
  test
```

```bash
mvn -pl core/ide -am \
  -DfailIfNoTests=false \
  -Dsurefire.failIfNoSpecifiedTests=false \
  -Dtest=ProjectFileUtilitiesTest \
  test
```

Expected: both suites pass (BUILD SUCCESS).

## Verify headless behavior manually

To confirm the headless guard prevents `NoClassDefFoundError` on the
`WindowStack` static initializer, run with explicit headless mode:

```bash
mvn -pl core/util -am \
  -DfailIfNoTests=false \
  -Djava.awt.headless=true \
  test
```

Expected: no `HeadlessException` or `NoClassDefFoundError` from `WindowStack`
or `Frame` static initialization. `WindowStack.getRootFrame()` returns `null`
and `Frame.getApplicationRootFrame()` returns `null` without error.

## Verify allowlist coverage

Inspect the allowlist in `SecureXmlParser.java`:

```bash
grep -A 7 'ALLOWED_RESOURCE_PACKAGES' \
  core/story-api-migration/src/main/java/org/lgna/project/io/SecureXmlParser.java
```

The output should show five prefixes:

```
"org.lgna.common."
"org.lgna.common.resources."
"org.lgna.story.resources."
"org.lgna.project."
"org.alice."
```

Verify the test assertion exists:

```bash
grep 'org.alice' \
  core/story-api-migration/src/test/java/org/lgna/project/io/SecureXmlParserTest.java
```

Expected: at least one line containing
`isAllowedResourcePackage("org.alice.ide.SomeResource")`.

## Review the result

| Check | Pass criteria |
| --- | --- |
| `SecureXmlParserTest` | All assertions pass, including `org.alice.` allowlist check |
| `ProjectBackupRecoveryIoTest` | BUILD SUCCESS (was failing before fix) |
| `ProjectFileUtilitiesTest` | BUILD SUCCESS (was failing before fix) |
| Headless initializer | No `HeadlessException` from `WindowStack` or `Frame` |
| Rejection tests | `java.lang.Runtime` and `com.evil.*` still rejected |
| XXE test | `readArchiveXml_rejectsXxePayload` still passes |

If all checks pass, the hotfix is validated. The allowlist covers all known
first-party resource packages, and the headless guard prevents CI failures on
macOS runners.

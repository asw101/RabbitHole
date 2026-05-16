# SecureXmlParser Allowlist and Headless Guard Hotfix

This reference describes the hotfix for the SecureXmlParser allowlist regression
introduced by PR #718, and the companion WindowStack/Frame headless guard that
prevents `NoClassDefFoundError` on macOS CI runners.

## Contents

- [Scope](#scope)
- [Problem](#problem)
- [Allowlist fix](#allowlist-fix)
- [Headless guard](#headless-guard)
- [Security analysis](#security-analysis)
- [API reference](#api-reference)
- [Configuration](#configuration)
- [Validation](#validation)
- [Examples](#examples)
- [Compatibility rules](#compatibility-rules)

## Scope

Issue #664 tracks two coupled failures on the `develop` branch after PR #718
merged the SecureXmlParser extraction:

| Failure | Root cause |
| --- | --- |
| `ProjectBackupRecoveryIoTest` and `ProjectFileUtilitiesTest` fail with `IOException: Resource class 'org.alice.ide.SomeResource' is not in an allowed package` | `ALLOWED_RESOURCE_PACKAGES` in `SecureXmlParser` did not include the `org.alice.` prefix. The original `XmlProjectIo` had no allowlist — it was added as defense-in-depth during extraction but missed `org.alice.*` resources. |
| `NoClassDefFoundError` in `WindowStack` on macOS CI runners | `WindowStack.rootFrame` is eagerly initialized via `new JFrame()`, which fails when `java.awt.headless=true` because AWT cannot create native peers. The `Frame.applicationRootFrame` static field chains off `WindowStack.getRootFrame()`, propagating the failure. |

Both failures are CI-only. Desktop users are unaffected because they always run
in a graphical environment and their projects use `org.lgna.*` resources.

## Problem

### Allowlist regression

The SecureXmlParser extraction (PR #718, issue #656) introduced
`ALLOWED_RESOURCE_PACKAGES` as defense-in-depth for the reflective
`createResource` method. The allowlist was seeded with four prefixes:

```java
Set.of(
    "org.lgna.common.",
    "org.lgna.common.resources.",
    "org.lgna.story.resources.",
    "org.lgna.project."
)
```

This missed `org.alice.*` — a legitimate package namespace used by IDE-level
resource classes. Tests that load projects containing `org.alice.*` resources
(`ProjectBackupRecoveryIoTest`, `ProjectFileUtilitiesTest`) began failing on
`develop`.

### Headless static initializer

`WindowStack.java` line 59 eagerly constructs a `JFrame`:

```java
private static final JFrame rootFrame = new JFrame();
```

On macOS CI runners with `java.awt.headless=true`, this throws
`java.awt.HeadlessException` (wrapped in `NoClassDefFoundError` because it
occurs during static initialization). `Frame.java` line 76 chains the same
failure:

```java
private static final Frame applicationRootFrame = new Frame(WindowStack.getRootFrame());
```

## Allowlist fix

`ALLOWED_RESOURCE_PACKAGES` now includes `"org.alice."`:

```java
private static final Set<String> ALLOWED_RESOURCE_PACKAGES = Set.of(
    "org.lgna.common.",
    "org.lgna.common.resources.",
    "org.lgna.story.resources.",
    "org.lgna.project.",
    "org.alice."
);
```

The trailing dot in `"org.alice."` prevents matching unrelated namespaces like
`org.alicefoo.*`. The `isAllowedResourcePackage` method uses
`className::startsWith`, so `"org.alice."` covers all subpackages:
`org.alice.ide.*`, `org.alice.stageide.*`, `org.alice.imageeditor.*`, etc.

### Test coverage

`SecureXmlParserTest.createResource_rejectsDisallowedPackage` includes an
assertion verifying the `org.alice.` prefix:

```java
assertTrue("Allowlist should cover org.alice.",
    SecureXmlParser.isAllowedResourcePackage("org.alice.ide.SomeResource"));
```

The existing rejection assertions (`java.lang.Runtime`, `com.evil.*`) remain
unchanged.

## Headless guard

### WindowStack.java

The `rootFrame` field uses a conditional ternary guarded by
`GraphicsEnvironment.isHeadless()`:

```java
private static final JFrame rootFrame =
    GraphicsEnvironment.isHeadless() ? null : new JFrame();
```

`getRootFrame()` returns `null` in headless environments. The `peek()` method
already returns `rootFrame` as its fallback when the stack is empty — returning
`null` in headless is safe because no UI rendering occurs in CI.

### Frame.java

`Frame.getApplicationRootFrame()` uses a helper method to guard against the
`null` JFrame returned by `WindowStack.getRootFrame()` in headless mode:

```java
private static final Frame applicationRootFrame = createApplicationRootFrame();

private static Frame createApplicationRootFrame() {
  JFrame rootFrame = WindowStack.getRootFrame();
  return (rootFrame != null) ? new Frame(rootFrame) : null;
}
```

Callers of `Frame.getApplicationRootFrame()` receive `null` in headless
environments. This is safe because:

1. The `Frame(JFrame)` constructor delegates to `AbstractWindow(JFrame)`, which
   stores the reference — null storage would be harmless, but we avoid it
   entirely by returning `null` from the factory.
2. All production code paths that call `getApplicationRootFrame()` are UI-only
   and never execute in headless CI.

## Security analysis

The allowlist expansion has minimal security impact:

| Layer | Status |
| --- | --- |
| **7-layer XXE defense** | Untouched. All seven `DocumentBuilderFactory` features remain in `readArchiveXml`. |
| **Type parameter constraint** | `createResource` requires `Class<? extends Resource>` — only `Resource` subclasses can be instantiated regardless of the allowlist. |
| **Package allowlist** | Expanded from 4 to 5 prefixes. `"org.alice."` covers first-party IDE resources. The trailing dot prevents matching `org.alicefoo.*`. |
| **`setAccessible(true)`** | Unchanged. Required for package-private `Resource(UUID)` constructors. |
| **Rejection tests** | `java.lang.Runtime` and `com.evil.*` continue to be rejected. |

No new `Class.forName` calls, no new `setAccessible` calls, and no new
reflection entry points are introduced.

## API reference

### SecureXmlParser (package-private)

| Method | Signature | Change |
| --- | --- | --- |
| `isAllowedResourcePackage` | `static boolean isAllowedResourcePackage(String className)` | Now returns `true` for `org.alice.*` classes |
| `createResource` | `static Resource createResource(Class<? extends Resource>, String)` | No signature change; accepts `org.alice.*` resources |

### WindowStack (public)

| Method | Signature | Change |
| --- | --- | --- |
| `getRootFrame()` | `public static JFrame getRootFrame()` | Returns `null` in headless environments |
| `peek()` | `public static Window peek()` | Returns `null` when stack is empty and headless |

### Frame (public)

| Method | Signature | Change |
| --- | --- | --- |
| `getApplicationRootFrame()` | `public static Frame getApplicationRootFrame()` | Returns `null` in headless environments |

## Configuration

No new configuration flags, system properties, or environment variables are
introduced. The headless guard reads `java.awt.headless` through the standard
`GraphicsEnvironment.isHeadless()` API, which is already set by Maven Surefire
on CI runners.

From a fresh checkout, initialize the Tweedle grammar submodule before Maven
validation:

```bash
git submodule update --init tweedle-lang
test -d tweedle-lang/Grammar
```

## Validation

### Focused SecureXmlParser tests

```bash
mvn -pl core/story-api-migration -am \
  -DfailIfNoTests=false \
  -Dsurefire.failIfNoSpecifiedTests=false \
  -Dtest=SecureXmlParserTest \
  test
```

### Previously-failing integration tests

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

All three test suites must pass on both Linux headless CI runners and macOS CI
runners.

## Examples

### Allowlist prefix matching

The `isAllowedResourcePackage` method matches any class whose fully qualified
name starts with an allowed prefix:

```text
org.alice.ide.SomeResource          → matches "org.alice."     → ALLOWED
org.alice.stageide.resource.MyRes   → matches "org.alice."     → ALLOWED
org.lgna.common.resources.AudioRes  → matches "org.lgna.common.resources." → ALLOWED
org.lgna.story.resources.prop.Box   → matches "org.lgna.story.resources."  → ALLOWED
java.lang.Runtime                   → no prefix match          → REJECTED
com.evil.MaliciousResource          → no prefix match          → REJECTED
org.alicefoo.Trick                  → no prefix match          → REJECTED (trailing dot prevents partial match)
```

### Headless vs graphical behavior

```text
Headless (CI):
  WindowStack.getRootFrame()        → null
  Frame.getApplicationRootFrame()   → null
  WindowStack.peek()                → null (stack empty, rootFrame is null)

Graphical (desktop):
  WindowStack.getRootFrame()        → JFrame instance (unchanged behavior)
  Frame.getApplicationRootFrame()   → Frame instance (unchanged behavior)
  WindowStack.peek()                → top of stack or JFrame fallback
```

## Compatibility rules

1. **Desktop behavior is unchanged.** The headless guard only activates when
   `GraphicsEnvironment.isHeadless()` returns `true`. Normal Alice desktop
   usage always runs in a graphical environment.
2. **Existing security tests pass.** The XXE rejection test
   (`readArchiveXml_rejectsXxePayload`), the disallowed-package rejection test,
   and the malformed-XML test are all preserved.
3. **No new public API is added.** All changes are to existing method behavior
   (returning `null` in headless) or to an existing private constant
   (`ALLOWED_RESOURCE_PACKAGES`).
4. **The `org.alice.` prefix covers all first-party IDE resources.** If a new
   top-level package is introduced for Resource subclasses, it must be added to
   `ALLOWED_RESOURCE_PACKAGES`.

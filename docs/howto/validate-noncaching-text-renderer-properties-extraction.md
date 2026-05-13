# Validate NonCachingTextRenderer Properties Extraction

Use this guide to verify the extraction of color state, property accessors,
dispose/cleanup, and query utilities from `NonCachingTextRenderer` into
`TextRendererProperties`.

For the full contract, see the [NonCachingTextRenderer Properties Extraction
reference](../reference/noncaching-text-renderer-properties-extraction.md).

## When to use this guide

Use this guide when:

- Reviewing changes that extract property fields and methods from
  `NonCachingTextRenderer` (issue #543)
- Modifying `TextRendererProperties` or the thin delegators it replaces
- Updating two-level field paths in `Manager` or `TextRendererPipeline`
- Changing the line-count threshold in `InnerClassExtractionContractTest`

## Before you start

Run commands from the repository root:

```bash
git submodule update --init tweedle-lang
test -d tweedle-lang/Grammar
export NODE_OPTIONS=--max-old-space-size=32768
```

## Step 1: Verify compilation

```bash
NODE_OPTIONS=--max-old-space-size=32768 \
mvn -pl core/glrender -am -DfailIfNoTests=false -Dcheckstyle.skip compile
```

All files must compile without errors. Pay attention to `Manager.java` and
`TextRendererPipeline.java` — these have updated field paths.

## Step 2: Verify TextRendererProperties exists

```bash
test -f core/glrender/src/main/java/edu/cmu/cs/dennisc/render/joglrenderer/TextRendererProperties.java \
  && echo "OK" || echo "MISSING"
```

## Step 3: Verify line counts

```bash
wc -l core/glrender/src/main/java/edu/cmu/cs/dennisc/render/joglrenderer/NonCachingTextRenderer.java
wc -l core/glrender/src/main/java/edu/cmu/cs/dennisc/render/joglrenderer/TextRendererProperties.java
```

- `NonCachingTextRenderer.java` must be under 500 lines.
- `TextRendererProperties.java` should be approximately 120 lines.

## Step 4: Verify fields moved

Check that color state fields are no longer on `NonCachingTextRenderer`:

```bash
grep -n 'haveCachedColor\|cachedR\|cachedG\|cachedB\|cachedA\|cachedColor\|needToResetColor' \
  core/glrender/src/main/java/edu/cmu/cs/dennisc/render/joglrenderer/NonCachingTextRenderer.java
```

Expected: no field declarations found. Only the `properties` field
declaration and thin delegator methods should appear.

Check that the fields exist on `TextRendererProperties`:

```bash
grep -n 'haveCachedColor\|cachedR\|cachedG\|cachedB\|cachedA\|cachedColor\|needToResetColor\|smoothing\|useVertexArrays' \
  core/glrender/src/main/java/edu/cmu/cs/dennisc/render/joglrenderer/TextRendererProperties.java
```

Expected: all 9 fields declared.

## Step 5: Verify caller path updates

Check that `Manager.java` uses two-level paths:

```bash
grep -n 'properties\.' \
  core/glrender/src/main/java/edu/cmu/cs/dennisc/render/joglrenderer/Manager.java
```

Expected: 9 occurrences of `textRenderer.properties.fieldName`.

Check that `TextRendererPipeline.java` uses two-level paths:

```bash
grep -n 'properties\.' \
  core/glrender/src/main/java/edu/cmu/cs/dennisc/render/joglrenderer/TextRendererPipeline.java
```

Expected: 9 occurrences of `renderer.properties.fieldName`.

## Step 6: Run the contract tests

```bash
NODE_OPTIONS=--max-old-space-size=32768 \
mvn -pl core/glrender -am \
  -DfailIfNoTests=false \
  -Dsurefire.failIfNoSpecifiedTests=false \
  -Dtest=InnerClassExtractionContractTest \
  -Dcheckstyle.skip test
```

This verifies:

- `TextRendererProperties` exists as a package-private class
- `TextRendererProperties` has a `NonCachingTextRenderer` constructor parameter
- `NonCachingTextRenderer` has a `properties` field of type
  `TextRendererProperties`
- 9 color/property fields are on `TextRendererProperties`
- 10 methods are delegated through properties
- Line count is under 500

## Step 7: Run the characterization tests

```bash
NODE_OPTIONS=--max-old-space-size=32768 \
mvn -pl core/glrender -am \
  -DfailIfNoTests=false \
  -Dsurefire.failIfNoSpecifiedTests=false \
  -Dtest=NonCachingTextRendererCharacterizationTest \
  -Dcheckstyle.skip test
```

The characterization tests verify that public API behavior is unchanged
by the properties extraction.

## Step 8: Run the full module test suite

```bash
NODE_OPTIONS=--max-old-space-size=32768 \
mvn -pl core/glrender -am -DfailIfNoTests=false -Dcheckstyle.skip test
```

This catches any compilation, linking, or behavioral regressions.

## Step 9: Verify dead code removal

```bash
grep -n 'emzic' \
  core/glrender/src/main/java/edu/cmu/cs/dennisc/render/joglrenderer/NonCachingTextRenderer.java
```

Expected: no results. The stale `//emzic: added boolean flag` comments
should be removed.

## Step 10: Spot-check the delegate pattern

Verify the properties field and constructor initialization:

```bash
grep -n 'properties' \
  core/glrender/src/main/java/edu/cmu/cs/dennisc/render/joglrenderer/NonCachingTextRenderer.java | head -15
```

Expected: a `TextRendererProperties properties` field declaration and
`this.properties = new TextRendererProperties(this)` in the constructor.

## Troubleshooting

### "Cannot find symbol: TextRendererProperties"

The file `TextRendererProperties.java` must be in the same package directory:
`core/glrender/src/main/java/edu/cmu/cs/dennisc/render/joglrenderer/`.

### "Cannot find symbol: textRenderer.properties.cachedR"

Verify that the color fields on `TextRendererProperties` are package-private
(no `private` modifier), and that the `properties` field on
`NonCachingTextRenderer` is also package-private.

### Line count exceeds 500

Check that all 10 method bodies were moved to `TextRendererProperties` and
that only thin one-line delegators remain on `NonCachingTextRenderer`. Verify
that 9 field declarations and associated dead comments were removed.

### Test failure in contract test line count

The threshold in `nonCachingTextRenderer_lineCount_under500` must be updated
from `< 650` to `< 500` to reflect the properties extraction target.

# Validate NonCachingTextRenderer Pipeline Extraction

Use this guide to verify the extraction of 6 rendering pipeline methods from
`NonCachingTextRenderer` into `TextRendererPipeline`.

For the full contract, see the [NonCachingTextRenderer Pipeline Extraction
reference](../reference/noncaching-text-renderer-pipeline-extraction.md).

## When to use this guide

Use this guide when:

- Reviewing changes that extract pipeline methods from `NonCachingTextRenderer`
- Modifying `TextRendererPipeline` or the thin delegators it replaces
- Changing visibility of fields accessed by the pipeline delegate
- Updating the line-count threshold in `InnerClassExtractionContractTest`

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

Both `NonCachingTextRenderer.java` and `TextRendererPipeline.java` must
compile without errors. Checkstyle warnings are suppressed by design.

## Step 2: Verify TextRendererPipeline exists

```bash
test -f core/glrender/src/main/java/edu/cmu/cs/dennisc/render/joglrenderer/TextRendererPipeline.java \
  && echo "OK" || echo "MISSING"
```

## Step 3: Verify line counts

```bash
wc -l core/glrender/src/main/java/edu/cmu/cs/dennisc/render/joglrenderer/NonCachingTextRenderer.java
wc -l core/glrender/src/main/java/edu/cmu/cs/dennisc/render/joglrenderer/TextRendererPipeline.java
```

- `NonCachingTextRenderer.java` must be under 650 lines (target: ~626).
- `TextRendererPipeline.java` should be approximately 253 lines.

## Step 4: Run the contract tests

```bash
NODE_OPTIONS=--max-old-space-size=32768 \
mvn -pl core/glrender -am \
  -DfailIfNoTests=false \
  -Dsurefire.failIfNoSpecifiedTests=false \
  -Dtest=InnerClassExtractionContractTest \
  -Dcheckstyle.skip test
```

This verifies:
- `TextRendererPipeline` exists as a package-private class
- `TextRendererPipeline` has a `NonCachingTextRenderer` constructor parameter
- `NonCachingTextRenderer` has a `pipeline` field of type `TextRendererPipeline`
- 5 additional fields are widened to package-private (`haveMaxSize`,
  `numRenderCycles`, `dbgFrame`, `debugged`, `CYCLES_PER_FLUSH`)
- `flushGlyphPipeline` and `draw3D_ROBUST` remain as package-private methods
- Line count is under 650

## Step 5: Run the characterization tests

```bash
NODE_OPTIONS=--max-old-space-size=32768 \
mvn -pl core/glrender -am \
  -DfailIfNoTests=false \
  -Dsurefire.failIfNoSpecifiedTests=false \
  -Dtest=NonCachingTextRendererCharacterizationTest \
  -Dcheckstyle.skip test
```

Expected: 49 tests, 42 pass, 7 skipped, 0 failures, 0 errors.

The characterization tests verify that the public API behavior (buffer
constants, glyph iteration, text data, render delegate, character cache) is
unchanged by the pipeline extraction.

## Step 6: Run the full module test suite

```bash
NODE_OPTIONS=--max-old-space-size=32768 \
mvn -pl core/glrender -am -DfailIfNoTests=false -Dcheckstyle.skip test
```

Expected: 159 tests run, 0 failures, 0 errors, 7 skipped.

This catches any compilation, linking, or behavioral regressions introduced
by the extraction.

## Step 7: Spot-check the delegate pattern

Verify the pipeline field and constructor initialization:

```bash
grep -n 'pipeline' core/glrender/src/main/java/edu/cmu/cs/dennisc/render/joglrenderer/NonCachingTextRenderer.java | head -10
```

Expected: a `TextRendererPipeline pipeline` field declaration and
`this.pipeline = new TextRendererPipeline(this)` in the constructor.

## Step 8: Spot-check thin delegators

Verify `flushGlyphPipeline` and `draw3D_ROBUST` delegate to the pipeline:

```bash
grep -A2 'void flushGlyphPipeline' core/glrender/src/main/java/edu/cmu/cs/dennisc/render/joglrenderer/NonCachingTextRenderer.java
grep -A2 'void draw3D_ROBUST' core/glrender/src/main/java/edu/cmu/cs/dennisc/render/joglrenderer/NonCachingTextRenderer.java
```

Each should contain a single-line body calling `pipeline.flushGlyphPipeline()`
or `pipeline.draw3D_ROBUST(...)` respectively.

## Troubleshooting

### "Cannot find symbol: TextRendererPipeline"

The file `TextRendererPipeline.java` must be in the same package directory:
`core/glrender/src/main/java/edu/cmu/cs/dennisc/render/joglrenderer/`.

### Line count exceeds 650

Check that all 6 methods were moved to `TextRendererPipeline` and that only
thin delegator stubs remain for `flushGlyphPipeline` and `draw3D_ROBUST`.
The `beginRendering(4-arg)`, `endRendering(boolean)`, `internal_draw3D`, and
`debug(GL)` methods should not have any body remaining in
`NonCachingTextRenderer`.

### Test failure in contract test line count

The threshold in `nonCachingTextRenderer_lineCount_under500` (misnamed for
historical reasons) must be updated from `< 850` to `< 650` to reflect the
pipeline extraction target.

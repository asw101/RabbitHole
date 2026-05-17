# Validate the ArgumentEncoder Extraction

How to verify that the `ArgumentEncoder` extraction from `TweedleEncoder` is
complete and correct. Use this after merging the extraction or when reviewing
the PR.

## Prerequisites

- Java 17+ and Maven installed
- Tweedle grammar submodule initialized:
  ```bash
  git submodule update --init tweedle-lang
  ```

## Step 1: Confirm the new file exists

```bash
ls core/ast/src/main/java/org/alice/serialization/tweedle/ArgumentEncoder.java
```

Expected: file listed with no errors.

## Step 2: Verify package-private visibility

```bash
grep '^class ' core/ast/src/main/java/org/alice/serialization/tweedle/ArgumentEncoder.java
```

Expected: `class ArgumentEncoder {` — no `public` modifier.

## Step 3: Verify constructor takes TweedleEncoder

```bash
grep 'ArgumentEncoder(TweedleEncoder' \
  core/ast/src/main/java/org/alice/serialization/tweedle/ArgumentEncoder.java
```

Expected: `ArgumentEncoder(TweedleEncoder encoder)` or similar.

## Step 4: Verify extracted methods exist on ArgumentEncoder

```bash
grep -E 'appendArgument|processKeyedArgument|processArgument|appendOneArgument|appendWrappedArg|getParameterLabel|parameterIndex' \
  core/ast/src/main/java/org/alice/serialization/tweedle/ArgumentEncoder.java
```

Expected: at least seven method signatures — `appendArgument`,
`processKeyedArgument`, `processArgument`, `appendOneArgument`,
`appendWrappedArg`, `getParameterLabel`, `parameterIndex`.

## Step 5: Verify forwardIdentifierName bridge on TweedleEncoder

```bash
grep 'forwardIdentifierName' \
  core/ast/src/main/java/org/alice/serialization/tweedle/TweedleEncoder.java
```

Expected: one method declaration — `forwardIdentifierName(AbstractDeclaration)`.

## Step 6: Verify TweedleEncoder delegates to ArgumentEncoder

```bash
grep 'argumentEncoder\.' \
  core/ast/src/main/java/org/alice/serialization/tweedle/TweedleEncoder.java
```

Expected: delegation calls in `appendArgument`, `processKeyedArgument`, and
`processArgument`.

## Step 7: Verify private methods removed from TweedleEncoder

```bash
grep -E 'private.*appendOneArgument|private.*appendWrappedArg|private.*getParameterLabel|private.*parameterIndex' \
  core/ast/src/main/java/org/alice/serialization/tweedle/TweedleEncoder.java
```

Expected: **zero matches**. All four private helper methods have moved entirely
to `ArgumentEncoder`.

## Step 8: Verify argument.process(encoder) pattern

```bash
grep 'process(encoder)' \
  core/ast/src/main/java/org/alice/serialization/tweedle/ArgumentEncoder.java
```

Expected: at least one match showing `argument.process(encoder)` — the
delegate passes the `TweedleEncoder` reference for correct visitor dispatch.

## Step 9: Verify TweedleEncoder line count

```bash
wc -l core/ast/src/main/java/org/alice/serialization/tweedle/TweedleEncoder.java
```

Expected: 365 lines or fewer (under the 400-line target).

## Step 10: Verify TweedleEncoderDecoder is unchanged

```bash
git diff HEAD~1 -- core/ast/src/main/java/org/alice/serialization/tweedle/TweedleEncoderDecoder.java
```

Expected: no changes. The public facade is unmodified by this extraction.
Replace `HEAD~1` with the appropriate merge base if your branch diverged.

## Step 11: Run ExpressionArgumentEncoderExtractionTest

```bash
NODE_OPTIONS=--max-old-space-size=32768 \
mvn -pl core/ast -am -DfailIfNoTests=false \
  -Dsurefire.failIfNoSpecifiedTests=false \
  -Dtest=ExpressionArgumentEncoderExtractionTest \
  test -q
```

Expected: all 43 characterization tests pass, exit code 0.

## Step 12: Run StatementEncoderExtractionTest (regression)

```bash
NODE_OPTIONS=--max-old-space-size=32768 \
mvn -pl core/ast -am -DfailIfNoTests=false \
  -Dsurefire.failIfNoSpecifiedTests=false \
  -Dtest=StatementEncoderExtractionTest \
  test -q
```

Expected: all tests pass, exit code 0. Prior extraction contracts preserved.

## Step 13: Run core/ast encoder tests

```bash
NODE_OPTIONS=--max-old-space-size=32768 \
mvn -pl core/ast -am -DfailIfNoTests=false \
  -Dsurefire.failIfNoSpecifiedTests=false \
  -Dtest=TweedleEncoderTest,TweedleEncoderRenameContractTest,TweedleEncoderDecoderTest,SourceCodeGeneratorTest \
  test -q
```

Expected: all tests pass, exit code 0.

## Step 14: Run story-api-migration round-trip tests

```bash
NODE_OPTIONS=--max-old-space-size=32768 \
mvn -pl core/story-api-migration -am -DfailIfNoTests=false test -q
```

Expected: all tests pass, exit code 0.

## Checklist

- [ ] `ArgumentEncoder.java` exists
- [ ] `ArgumentEncoder` is package-private (no `public` keyword)
- [ ] Constructor takes `TweedleEncoder` reference
- [ ] 7 methods present: `appendArgument`, `processKeyedArgument`, `processArgument`, `appendOneArgument`, `appendWrappedArg`, `getParameterLabel`, `parameterIndex`
- [ ] `forwardIdentifierName` bridge method on `TweedleEncoder`
- [ ] `TweedleEncoder` delegates via `argumentEncoder.` calls
- [ ] Private helpers removed from `TweedleEncoder` (4 methods)
- [ ] `argument.process(encoder)` uses `TweedleEncoder` reference
- [ ] `TweedleEncoder` ≤ 365 lines
- [ ] `TweedleEncoderDecoder.java` is unchanged
- [ ] `ExpressionArgumentEncoderExtractionTest` passes (43 tests)
- [ ] `StatementEncoderExtractionTest` passes
- [ ] `TweedleEncoderTest` passes
- [ ] `TweedleEncoderRenameContractTest` passes
- [ ] `TweedleEncoderDecoderTest` passes
- [ ] `SourceCodeGeneratorTest` passes
- [ ] `core/story-api-migration` tests pass

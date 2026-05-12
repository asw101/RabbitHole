# Validate the ResourceEncoder Extraction

How to verify that the `ResourceEncoder` extraction from `TweedleEncoder` is
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
ls core/ast/src/main/java/org/alice/serialization/tweedle/ResourceEncoder.java
```

Expected: file listed with no errors.

## Step 2: Verify package-private visibility

```bash
grep '^class ' core/ast/src/main/java/org/alice/serialization/tweedle/ResourceEncoder.java
```

Expected: `class ResourceEncoder {` — no `public` modifier.

## Step 3: Verify constructor takes TweedleEncoder

```bash
grep 'ResourceEncoder(TweedleEncoder' \
  core/ast/src/main/java/org/alice/serialization/tweedle/ResourceEncoder.java
```

Expected: `ResourceEncoder(TweedleEncoder encoder)` or similar.

## Step 4: Verify extracted methods exist on ResourceEncoder

```bash
grep -E 'processResourceType|processDynamicResource|getUserJointIdentifier|appendResourceConstructor|appendResourceInstances|appendResourceInstance|appendResourceFields|appendAddedJoints|appendStaticField|appendNewJointId|appendNewJointArrayId|getFieldReference|appendNewPose|appendNewJointTransformation' \
  core/ast/src/main/java/org/alice/serialization/tweedle/ResourceEncoder.java
```

Expected: at least 15 method signatures (14 methods including one overload of
`appendStaticField`).

## Step 5: Verify USER_PREFIX is package-private on TweedleEncoder

```bash
grep 'USER_PREFIX' \
  core/ast/src/main/java/org/alice/serialization/tweedle/TweedleEncoder.java
```

Expected: `static final String USER_PREFIX = "u_";` — no `private` modifier.

## Step 6: Verify 3 new bridge methods on TweedleEncoder

```bash
grep -E 'forwardGetCodeStringBuilder|forwardOpenBlock|forwardAppendClassFooter' \
  core/ast/src/main/java/org/alice/serialization/tweedle/TweedleEncoder.java
```

Expected: three method declarations — `forwardGetCodeStringBuilder()`,
`forwardOpenBlock()`, `forwardAppendClassFooter(String)`.

## Step 7: Verify TweedleEncoder delegates to ResourceEncoder

```bash
grep 'resourceEncoder\.' \
  core/ast/src/main/java/org/alice/serialization/tweedle/TweedleEncoder.java
```

Expected: delegation calls in `processResourceType`, `processDynamicResource`,
`getUserJointIdentifier`, `appendNewJointId`, `appendNewJointArrayId`,
`getFieldReference`, `appendNewPose`, and `appendNewJointTransformation`.

## Step 8: Verify private resource methods are removed from TweedleEncoder

```bash
grep -E 'private.*appendResourceConstructor|private.*appendResourceInstances|private.*appendResourceInstance|private.*appendResourceFields|private.*appendAddedJoints' \
  core/ast/src/main/java/org/alice/serialization/tweedle/TweedleEncoder.java
```

Expected: **zero matches**. All five private resource methods have moved
entirely to `ResourceEncoder`.

## Step 9: Verify 10 methods widened to package-private on TweedleEncoder

```bash
grep -cE '^\s+void appendInstantiation|^\s+void appendArg|^\s+void appendAnotherArg|^\s+void quoteString|^\s+void appendAssignmentOperator|^\s+void appendSingleCodeLine|^\s+void appendVisibilityTag|^\s+<T> void appendList|^\s+String getListSeparator' \
  core/ast/src/main/java/org/alice/serialization/tweedle/TweedleEncoder.java
```

Expected: match count ≥ 10. None should have `private` modifier.

## Step 10: Verify unused imports are removed

```bash
grep -E 'import.*Tuple3|import.*UnitQuaternion|import.*java.lang.reflect.Field|import.*IdentifiableTweedleNode' \
  core/ast/src/main/java/org/alice/serialization/tweedle/TweedleEncoder.java
```

Expected: **zero matches**. These four imports are no longer needed in
`TweedleEncoder` after the extraction.

## Step 11: Verify TweedleEncoder line count

```bash
wc -l core/ast/src/main/java/org/alice/serialization/tweedle/TweedleEncoder.java
```

Expected: approximately 820 lines (was 988 before extraction).

## Step 12: Verify TweedleEncoderDecoder is unchanged

```bash
git diff HEAD~1 -- core/ast/src/main/java/org/alice/serialization/tweedle/TweedleEncoderDecoder.java
```

Expected: no changes. The public facade is unmodified by this extraction.
Replace `HEAD~1` with the appropriate merge base if your branch diverged.

## Step 13: Verify StatementEncoder and ExpressionEncoder are unchanged

```bash
git diff HEAD~1 -- \
  core/ast/src/main/java/org/alice/serialization/tweedle/StatementEncoder.java \
  core/ast/src/main/java/org/alice/serialization/tweedle/ExpressionEncoder.java
```

Expected: no changes. Steps 1 and 2 are unaffected by step 3.

## Step 14: Run core/ast encoder tests

```bash
NODE_OPTIONS=--max-old-space-size=32768 \
mvn -pl core/ast -am -DfailIfNoTests=false \
  -Dsurefire.failIfNoSpecifiedTests=false \
  -Dtest=TweedleEncoderTest,TweedleEncoderRenameContractTest,TweedleEncoderDecoderTest,SourceCodeGeneratorTest,DecoderDelegateDecompositionCharacterizationTest,SilverThreadTweedleDecoderRoundTripTest \
  -Dcheckstyle.skip \
  test -q
```

Expected: all tests pass, exit code 0.

## Step 15: Run story-api-migration round-trip tests

```bash
NODE_OPTIONS=--max-old-space-size=32768 \
mvn -pl core/story-api-migration -am -DfailIfNoTests=false test -q
```

Expected: all tests pass, exit code 0.

## Checklist

- [ ] `ResourceEncoder.java` exists
- [ ] `ResourceEncoder` is package-private (no `public` keyword)
- [ ] Constructor takes `TweedleEncoder` reference
- [ ] 14 methods present (15 signatures including `appendStaticField` overload)
- [ ] `USER_PREFIX` is package-private on `TweedleEncoder`
- [ ] 3 new bridge methods on `TweedleEncoder`: `forwardGetCodeStringBuilder`, `forwardOpenBlock`, `forwardAppendClassFooter`
- [ ] `TweedleEncoder` delegates via `resourceEncoder.` calls (8 delegation stubs)
- [ ] Private resource methods removed from `TweedleEncoder`
- [ ] 10 private methods widened to package-private
- [ ] 4 unused imports removed from `TweedleEncoder`
- [ ] `TweedleEncoder` is ~820 lines (was 988)
- [ ] `TweedleEncoderDecoder.java` is unchanged
- [ ] `StatementEncoder.java` is unchanged
- [ ] `ExpressionEncoder.java` is unchanged
- [ ] `TweedleEncoderTest` passes
- [ ] `TweedleEncoderRenameContractTest` passes
- [ ] `TweedleEncoderDecoderTest` passes
- [ ] `SourceCodeGeneratorTest` passes
- [ ] `DecoderDelegateDecompositionCharacterizationTest` passes
- [ ] `SilverThreadTweedleDecoderRoundTripTest` passes
- [ ] `core/story-api-migration` tests pass

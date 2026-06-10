# Generated Source Validation

This document describes the generated-source validation split. Compiler and
story tests own Java source shape, while NetBeans tests own exported project
packaging and runtime integration.

## Ownership

| Area | Test owner | Owns | Does not own |
| --- | --- | --- | --- |
| Core AST Java generation | `core/ast/src/test/java/org/lgna/project/ast/SourceCodeGeneratorTest.java`, `JavaCodeGeneratorExtendedTest.java`, `JavaCodeGeneratorDelegationTest.java` | Java syntax snippets for AST constructs, escaping, imports, delegation boundaries, field/method/class emission, loop and expression source shape | NetBeans project layout, launcher generation, archive export |
| Story API generated source | `core/story-api-migration/src/test/java/org/alice/stageide/storyapi/StoryApiGeneratedSourceTest.java` | Story API call source shape, listener registration syntax, generated Story API method invocation forms, source emitted by `JavaCodeUtilities.createJavaCodeGeneratorBuilder()` | NetBeans packaging, launcher files, runtime class loading |
| Alice IDE student-program codegen | `core/ide/src/test/java/org/alice/ide/SilverThreadStudentProgramCodegenTest.java` | Source generation after Alice project save/readback and student-program semantic structure | Pure compiler syntax that can be tested directly in `core/ast` |
| NetBeans project generation | `netbeans/src/test/java/org/alice/netbeans/project/ProjectCodeGeneratorGeneratedSourceTest.java`, `ProjectCodeGeneratorStoryApiGeneratedSourceTest.java`, `ProjectCodeGeneratorTest.java`, `ProjectCodeGeneratorStandaloneProjectTest.java` | Project export layout, generated file placement, launcher generation, resource copying, NetBeans preferences, compile smoke, runtime handoff, NetBeans-specific formatting/folding/access behavior | Standalone assertions about Java syntax snippets |
| RabbitHole baseline parity | `netbeans/src/test/java/org/alice/netbeans/project/RabbitHoleBaselineParityTest.java` | Archive/export/package parity for generated `.a3p`, `.a3w`, resources, manifests, and generated-source file presence or hashes when they prove package parity | Pure Java source snapshots that duplicate compiler-owned tests |

The NetBeans owner path uses the current `org/alice/netbeans/project` test
package. Older design notes may mention `org/alice/ide/projecturi/views/components`;
that package is not the target for this feature.

## Public API

There is no user-facing runtime API for this split. It is a test organization
contract.

The test-facing generation entry points are:

| API | Scope | Use |
| --- | --- | --- |
| `new JavaCodeGenerator.Builder()` | `core/ast` | Direct AST source-shape characterization. |
| `JavaCodeUtilities.createJavaCodeGeneratorBuilder()` | Story API migration tests | Story API source generation with the same builder settings used by Alice code paths. |
| `ProjectCodeGenerator.generateCode(...)` | NetBeans tests | End-to-end NetBeans project source generation, file layout, and compile/runtime integration. |
| `IoUtilities.writeProject(...)` and `IoUtilities.exportProject(...)` | Baseline parity and IDE lifecycle tests | Save/export archive shape, manifest, resource, and round-trip behavior. |

## Configuration

Initialize the Tweedle grammar submodule before Maven lanes that reach
`core/tweedle`:

```bash
git submodule update --init tweedle-lang
```

Use these Maven flags for local headless generated-source validation:

| Option | Value | Purpose |
| --- | --- | --- |
| `-DincludeSims` | `false` | Runs the no-Sims validation lane. |
| `-Dinstall4j.skip` | present | Skips installer generation. |
| `-Dcheckstyle.skip` | present for targeted tests | Keeps focused validation on generated-source behavior. |
| `-Djava.awt.headless` | `true` | Prevents GUI startup during compiler and packaging tests. |
| `-Dsurefire.failIfNoSpecifiedTests` | `false` when using `-pl ... -am` | Allows reactor modules without matching focused tests. |

`RabbitHoleBaselineParityTest` also supports snapshot-update properties described
in [RabbitHole baseline parity](../rabbithole-baseline-parity.md). Snapshot
updates are for intentional archive/package parity changes, not for moving pure
Java syntax assertions.

## Validation commands

Run the compiler-owned generated-source tests:

```bash
git submodule update --init tweedle-lang
mvn -pl core/ast -am \
  -DincludeSims=false \
  -Dinstall4j.skip \
  -Dcheckstyle.skip \
  -Djava.awt.headless=true \
  -Dtest=SourceCodeGeneratorTest,JavaCodeGeneratorExtendedTest,JavaCodeGeneratorDelegationTest \
  -Dsurefire.failIfNoSpecifiedTests=false \
  test
```

Run the Story API generated-source tests:

```bash
mvn -pl core/story-api-migration -am \
  -DincludeSims=false \
  -Dinstall4j.skip \
  -Dcheckstyle.skip \
  -Djava.awt.headless=true \
  -Dtest=StoryApiGeneratedSourceTest \
  -Dsurefire.failIfNoSpecifiedTests=false \
  test
```

Run the Alice IDE lifecycle codegen test:

```bash
mvn -pl core/ide -am \
  -DincludeSims=false \
  -Dinstall4j.skip \
  -Dcheckstyle.skip \
  -Djava.awt.headless=true \
  -Dtest=SilverThreadStudentProgramCodegenTest \
  -Dsurefire.failIfNoSpecifiedTests=false \
  test
```

Run the NetBeans integration and parity tests:

```bash
mvn -pl netbeans -am \
  -DincludeSims=false \
  -Dinstall4j.skip \
  -Dcheckstyle.skip \
  -Djava.awt.headless=true \
  -Dtest=ProjectCodeGeneratorGeneratedSourceTest,ProjectCodeGeneratorStoryApiGeneratedSourceTest,ProjectCodeGeneratorTest,ProjectCodeGeneratorStandaloneProjectTest,RabbitHoleBaselineParityTest \
  -Dsurefire.failIfNoSpecifiedTests=false \
  test
```

## Review rules

1. Add focused generated-source coverage before narrowing a NetBeans assertion.
2. Keep NetBeans assertions when the behavior depends on exported files, launchers,
   resources, class loading, runtime handoff, project layout, or NetBeans-specific
   formatting/folding/access behavior.
3. Keep RabbitHole parity snapshots focused on archive/export/package summaries.
   Generated-source file presence and hashes are allowed only as package parity
   evidence, not as ownership of imports, declarations, or snippet syntax.
4. Treat generated-source snippet changes in `core/ast` or
   `core/story-api-migration` as compiler/story behavior changes.
5. Open generated-source validation refactors against `develop` and merge only
   after required CI checks are green.

# Verify Generated Source Validation

Use this workflow to maintain the generated-source validation split. It
separates pure Java source-shape assertions from NetBeans project generation
tests.

## Classify the assertion

Decide what the assertion proves before moving it.

| Assertion proves | Put it in |
| --- | --- |
| A Java AST node emits a specific syntax shape | `core/ast/src/test/java/org/lgna/project/ast/SourceCodeGeneratorTest.java` |
| Java generator imports, comments, or concurrency helpers are delegated correctly | `core/ast/src/test/java/org/lgna/project/ast/JavaCodeGeneratorDelegationTest.java` |
| Story API calls or listeners emit the expected Java form | `core/story-api-migration/src/test/java/org/alice/stageide/storyapi/StoryApiGeneratedSourceTest.java` |
| Reopened Alice student projects still generate meaningful Java source | `core/ide/src/test/java/org/alice/ide/SilverThreadStudentProgramCodegenTest.java` |
| NetBeans writes the right files, compiles exported sources, copies resources, creates launchers, or hands off to runtime code | `netbeans/src/test/java/org/alice/netbeans/project/` |
| `.a3p` or `.a3w` archive summaries match package parity expectations | `netbeans/src/test/java/org/alice/netbeans/project/RabbitHoleBaselineParityTest.java` |

## Move a pure source-shape assertion

1. Add or update the focused compiler/story test first.
2. Generate source through the narrowest API that proves the behavior.
3. Assert the exact source shape in the focused test.
4. Remove the duplicate snippet assertion from the NetBeans test.
5. Leave a NetBeans assertion only for packaging, compile smoke, file layout, or
   runtime behavior.

Example: a NetBeans test that only checks the generated for-each loop text moves
to `SourceCodeGeneratorTest`:

```java
assertTrue(source.contains("for(String itemA : new String[]{\"red\", \"blue\"})"));
assertFalse(source.contains("COUNT__"));
```

The NetBeans test keeps the integration proof:

```java
Path sourceDirectory = generateProgramSource("synthetic-for-each-loop.a3p", programType, "generated-src");
compileProgramAndLauncher("generated-classes", sourceDirectory.resolve("Program.java"), sourceDirectory);
```

## Keep a NetBeans assertion

Keep the assertion in `netbeans` when it depends on generated project artifacts,
not just generated Java syntax.

Common examples:

| Keep in NetBeans | Reason |
| --- | --- |
| `Files.exists(sourceDirectory.resolve("Program.java"))` | Project file layout. |
| `compileProgramAndLauncher(...)` | Exported source and launcher compile together. |
| Resource files copied under generated project directories | Packaging/resource integration. |
| Generated listener class is loaded and invoked headlessly | Runtime handoff. |
| Baseline archive entry summaries | `.a3p`/`.a3w` package parity. |

## Run focused validation

Run the generated-source lanes from the repository root.

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

mvn -pl core/story-api-migration -am \
  -DincludeSims=false \
  -Dinstall4j.skip \
  -Dcheckstyle.skip \
  -Djava.awt.headless=true \
  -Dtest=StoryApiGeneratedSourceTest \
  -Dsurefire.failIfNoSpecifiedTests=false \
  test

mvn -pl core/ide -am \
  -DincludeSims=false \
  -Dinstall4j.skip \
  -Dcheckstyle.skip \
  -Djava.awt.headless=true \
  -Dtest=SilverThreadStudentProgramCodegenTest \
  -Dsurefire.failIfNoSpecifiedTests=false \
  test

mvn -pl netbeans -am \
  -DincludeSims=false \
  -Dinstall4j.skip \
  -Dcheckstyle.skip \
  -Djava.awt.headless=true \
  -Dtest=ProjectCodeGeneratorGeneratedSourceTest,ProjectCodeGeneratorStoryApiGeneratedSourceTest,ProjectCodeGeneratorTest,ProjectCodeGeneratorStandaloneProjectTest,RabbitHoleBaselineParityTest \
  -Dsurefire.failIfNoSpecifiedTests=false \
  test
```

## Review the diff

Before opening the pull request:

1. Confirm every removed NetBeans source snippet has equivalent focused coverage.
2. Confirm every remaining NetBeans assertion proves packaging, project layout,
   launcher generation, resources, compile smoke, runtime handoff, or
   NetBeans-specific formatting/folding/access behavior.
3. Confirm `RabbitHoleBaselineParityTest` snapshots use source file presence or
   hashes only as package parity evidence, not as ownership of imports,
   declarations, or snippet syntax.
4. Open the pull request against `develop`.
5. Merge only after required CI checks are green.

See [Generated Source Validation](../reference/generated-source-validation.md)
for the ownership matrix and configuration reference.

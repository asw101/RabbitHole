# Validate the JavaCodeGenerator Delegate Extraction

How to verify that the `JavaCodeGenerator` delegate extraction (issue #649) is
complete and correct. Use this after merging the extraction or when reviewing
the PR.

## Prerequisites

- Java 17+ and Maven installed
- Tweedle grammar submodule initialized:
  ```bash
  git submodule update --init tweedle-lang
  ```

## Step 1: Confirm the new files exist

```bash
ls core/ast/src/main/java/org/lgna/project/ast/JavaImportCollector.java \
   core/ast/src/main/java/org/lgna/project/ast/JavaCommentFormatter.java \
   core/ast/src/main/java/org/lgna/project/ast/JavaConcurrencyEmitter.java
```

Expected: all three files listed with no errors.

## Step 2: Verify package-private visibility

```bash
grep '^class ' core/ast/src/main/java/org/lgna/project/ast/JavaImportCollector.java
grep '^class ' core/ast/src/main/java/org/lgna/project/ast/JavaCommentFormatter.java
grep '^class ' core/ast/src/main/java/org/lgna/project/ast/JavaConcurrencyEmitter.java
```

Expected output for each:
- `class JavaImportCollector {`
- `class JavaCommentFormatter {`
- `class JavaConcurrencyEmitter {`

No `public` modifier on any of them.

## Step 3: Verify JavaCodeGenerator line count

```bash
wc -l core/ast/src/main/java/org/lgna/project/ast/JavaCodeGenerator.java
```

Expected: ≤ 500 lines (target ~475).

## Step 4: Verify protected methods retained on JavaCodeGenerator

```bash
grep -n 'protected.*getLocalizedMultiLineComment' \
  core/ast/src/main/java/org/lgna/project/ast/JavaCodeGenerator.java

grep -n 'protected.*getImportsPrefix\|protected.*getImportsPostfix' \
  core/ast/src/main/java/org/lgna/project/ast/JavaCodeGenerator.java

grep -n 'protected.*appendSectionPrefix\|protected.*appendSectionPostfix' \
  core/ast/src/main/java/org/lgna/project/ast/JavaCodeGenerator.java
```

Expected:
- `getLocalizedMultiLineComment` is `protected`
- `getImportsPrefix` and `getImportsPostfix` are `protected`
- `appendSectionPrefix` and `appendSectionPostfix` are `protected`

## Step 5: Verify JavaImportCollector constructor takes configured import lists

```bash
grep 'JavaImportCollector(' \
  core/ast/src/main/java/org/lgna/project/ast/JavaImportCollector.java
```

Expected: constructor signature, no `public` modifier.

## Step 6: Verify JavaCommentFormatter constructor takes bundle name

```bash
grep 'JavaCommentFormatter(' \
  core/ast/src/main/java/org/lgna/project/ast/JavaCommentFormatter.java
```

Expected: constructor accepts `String commentsLocalizationBundleName`.

## Step 7: Verify JavaConcurrencyEmitter methods

```bash
grep -E 'processDoTogether|processEachInTogether' \
  core/ast/src/main/java/org/lgna/project/ast/JavaConcurrencyEmitter.java
```

Expected: at least two method signatures — one `processDoTogether`, one
`processEachInTogether`.

## Step 8: Verify delegation on JavaCodeGenerator

```bash
grep -c 'importCollector\.\|commentFormatter\.\|concurrencyEmitter\.' \
  core/ast/src/main/java/org/lgna/project/ast/JavaCodeGenerator.java
```

Expected: at least 8 delegation calls (import build, type tracking, static
method tracking, member prefix × 5, member postfix × 5, section comments × 2,
localized comment, do together, each in together).

## Step 9: Verify NetbeansJavaCodeGenerator is unchanged

```bash
git diff -- netbeans/src/main/java/org/alice/netbeans/project/NetbeansJavaCodeGenerator.java
```

Expected: no output (no changes).

## Step 10: Compile core/ast module

```bash
mvn -pl core/ast compile -q
```

Expected: `BUILD SUCCESS`.

## Step 11: Compile netbeans module

```bash
mvn -pl netbeans compile -q
```

Expected: `BUILD SUCCESS`. This confirms `NetbeansJavaCodeGenerator` still
compiles against the refactored `JavaCodeGenerator`.

## Step 12: Full project compile

```bash
mvn compile -q
```

Expected: `BUILD SUCCESS`. This confirms no other module is broken by the
extraction.

## Step 13: Verify import sets moved to JavaImportCollector

```bash
grep -n 'packagesToImportOnDemand\|typesToImport\|methodsToImportStatic' \
  core/ast/src/main/java/org/lgna/project/ast/JavaImportCollector.java | head -6
```

Expected: three `Set<...>` field declarations in `JavaImportCollector`.

```bash
grep -c 'packagesToImportOnDemand\|typesToImport\|methodsToImportStatic' \
  core/ast/src/main/java/org/lgna/project/ast/JavaCodeGenerator.java
```

Expected: 0 — these fields no longer exist on `JavaCodeGenerator`.

## Troubleshooting

| Symptom | Cause | Fix |
| --- | --- | --- |
| `cannot find symbol: splitIntoLines` | `JavaCommentFormatter` not in same package | Verify file is in `org.lgna.project.ast` |
| `getLocalizedMultiLineComment has private access` | Method visibility reduced | Restore `protected` on `JavaCodeGenerator` |
| NetBeans compile failure | `getImportsPrefix`/`getImportsPostfix` signature changed | Restore original `protected String` signatures |
| Import block empty | `importCollector` not wired in `processTypeName` | Verify `trackType` call in `processTypeName` override |
| Lambda/anonymous mismatch in concurrency | `isLambdaSupported` not forwarded | Verify `JavaConcurrencyEmitter` calls `generator.isLambdaSupported()` |

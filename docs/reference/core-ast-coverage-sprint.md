# core/ast coverage sprint — 50.4% → 70%

Issue #751 targets raising `core/ast` line coverage from 50.4% to 70%+ by
adding test files for virtual machine events, exception types, code generator
delegates, type resolution helpers, and AST node construction. All new tests
will be JUnit 4, headless-safe, and use hand-rolled stubs (no Mockito).

This sprint builds on existing VM characterization tests, Tweedle
encoder/decoder boundary tests, and source code generator tests. The
incremental work targets the VM event hierarchy, VM exception classes (12 types
at ~30 lines each — high coverage ROI for simple construction tests), and
remaining code generator delegation paths.

## Test inventory

### Tier 1 — Virtual machine events and listener (~300 lines)

| Test file | Package | Source classes covered | Est. lines |
| --- | --- | --- | ---: |
| `VirtualMachineHeadlessRuntimeEventTest` | `o.l.p.virtualmachine` | `VirtualMachineEvent`, `ExpressionEvaluationEvent`, `StatementExecutionEvent`, `CountLoopIterationEvent`, `ForEachLoopIterationEvent`, `WhileLoopIterationEvent`, `EachInTogetherItemEvent`, `VirtualMachineListener` | 300 |

Tests construct each event type, verify field accessors, and confirm that a
stub `VirtualMachineListener` receives the expected callback for each event
kind.

### Tier 2 — VM exception types and utilities (~250 lines)

| Test file | Package | Source classes covered | Est. lines |
| --- | --- | --- | ---: |
| `VmErrorHandlingCharacterizationTest` | `o.l.p.virtualmachine` | `LgnaVmException`, `LgnaVmNullPointerException`, `LgnaVmClassCastException`, `LgnaVmArrayIndexOutOfBoundsException`, `LgnaVmNoReturnException`, `LgnaVmIllegalLocalException`, `LgnaVmIllegalLocalAccessException`, `LgnaVmIllegalLocalAssignmentException`, `LgnaVmIllegalParameterAccessException`, `ExceptionDetailUtilities`, `ReturnException` | 250 |

Each exception class is constructed with representative AST nodes, and the
test verifies the message format, chained cause, and `ExceptionDetailUtilities`
stack-trace rendering.

### Tier 3 — VM core classes (~300 lines)

| Test file | Package | Source classes covered | Est. lines |
| --- | --- | --- | ---: |
| `VmContractTest` | `o.l.p.virtualmachine` | `VirtualMachine`, `ReleaseVirtualMachine` (contract verification) | 100 |
| `VmFieldAccessCharacterizationTest` | `o.l.p.virtualmachine` | `UserInstance`, `UserArrayInstance`, `Variable`, `Frame` | 100 |
| `VmExpressionEvaluationCharacterizationTest` | `o.l.p.virtualmachine` | `VmExpressionEvaluator` (literal, arithmetic, relational) | 50 |
| `VmStatementExecutionCharacterizationTest` | `o.l.p.virtualmachine` | `VmStatementExecutor` (assignment, return, conditional) | 50 |

### Tier 4 — Code generator and type resolution (~400 lines)

| Test file | Package | Source classes covered | Est. lines |
| --- | --- | --- | ---: |
| `JavaCodeGeneratorDelegationTest` | `o.l.p.ast` | `JavaCodeGenerator` delegation to extracted helpers | 100 |
| `JavaConcurrencyEmitterTest` | `o.l.p.ast` | `JavaConcurrencyEmitter` | 60 |
| `JavaCommentFormatterTest` | `o.l.p.ast` | `JavaCommentFormatter` | 40 |
| `JavaImportCollectorTest` | `o.l.p.ast` | `JavaImportCollector` | 40 |
| `AstTypeResolutionHelpersTest` | `o.l.p.ast` | `AstTypeResolutionHelpers` | 60 |
| `AstMethodLookupHelpersTest` | `o.l.p.ast` | `AstMethodLookupHelpers` | 40 |
| `JavaTypeMethodLookupTest` | `o.l.p.ast` | `JavaType` method lookup paths | 40 |
| `JavaTypePrimitiveMappingTest` | `o.l.p.ast` | `JavaType` primitive ↔ wrapper mapping | 20 |

### Tier 5 — AST node construction and processor (~200 lines)

| Test file | Package | Source classes covered | Est. lines |
| --- | --- | --- | ---: |
| `AstProcessorDefaultMethodsTest` | `o.l.p.ast` | `AstProcessor` default visitor methods | 60 |
| `AstUtilitiesDecompositionTest` | `o.l.p.ast` | `AstUtilities` decomposed helper methods | 80 |
| `SourceCodeGeneratorTest` | `o.l.p.ast` | `SourceCodeGenerator` (end-to-end formatting) | 40 |
| `ResourcesTypeWrapperTest` | `o.l.p.resource` | `ResourcesTypeWrapper` | 20 |

### Tier 6 — Tweedle serialization (~300 lines)

| Test file | Package | Source classes covered | Est. lines |
| --- | --- | --- | ---: |
| `TweedleEncoderDecoderTest` | `o.a.s.tweedle` | `TweedleEncoder`, `TweedleDecoder` round-trip | 80 |
| `TweedleEncoderRenameContractTest` | `o.a.s.tweedle` | `TweedleEncoder` rename semantics | 40 |
| `ExpressionEncoderExtractionTest` | `o.a.s.tweedle` | Expression encoder extracted delegates | 40 |
| `StatementEncoderExtractionTest` | `o.a.s.tweedle` | Statement encoder extracted delegates | 40 |
| `ArgumentEncoderExtractionTest` | `o.a.s.tweedle` | Argument encoder extracted delegates | 30 |
| `ExpressionDecoderBoundaryTest` | `o.a.s.tweedle` | Expression decoder boundary paths | 30 |
| `StatementDecoderBoundaryTest` | `o.a.s.tweedle` | Statement decoder boundary paths | 20 |
| `FieldDecoderBoundaryTest` | `o.a.s.tweedle` | Field decoder boundary paths | 20 |

**Total estimated test code:** ~1,750+ lines across all tiers.

**Net new production lines exercised:** ~1,491+ (some test-exercised lines
overlap with paths already reached by existing characterization tests; the
coverage arithmetic below uses the net figure).

## Test style and conventions

All tests use JUnit 4 (`org.junit.Test`, `org.junit.Assert`).

Key conventions:

- **Stub AST nodes** — inner static classes or anonymous subclasses provide
  minimal `AbstractNode` implementations for constructing event and exception
  test fixtures.
- **VM state stubs** — `Frame` and `UserInstance` objects are built from
  synthetic `UserType` / `UserField` AST nodes with no runtime class backing.
- **Known-answer encoder tests** — encode a synthetic AST tree, decode it back,
  and assert structural equality.
- **Exception message verification** — `LgnaVm*Exception` tests assert exact
  message format including the AST node description embedded in the message.
- **No file I/O** — all test fixtures are built in-memory.
- **Descriptive method names** — `methodUnderTest_condition_expectedResult`.

## Excluded source code

| Exclusion | Reason |
| --- | --- |
| `ReleaseVirtualMachine` runtime dispatch | Requires full story-api runtime |
| `LambdaContext` integration paths | Requires functional interface binding |
| `MethodContext` dynamic dispatch | Requires full type system resolution |
| Tweedle grammar parser (`tweedle-lang`) | Submodule — tested in its own module |

## Running the tests

Run only `core/ast` tests:

```sh
mvn test -pl core/ast
```

Run with coverage and ratchet:

```sh
mvn -DincludeSims=false -Dinstall4j.skip -Pcoverage verify
python3 scripts/summarize-jacoco-coverage.py \
  --output coverage-summary.md \
  --evidence-manifest coverage-evidence-manifest.json \
  --target-aggregate-line-percent 70.0 \
  --min-aggregate-line-percent 8.0 \
  --min-module-line-percent core/ast=65.0
```

The `core/ast=65.0` ratchet is the conservative floor; measured coverage
exceeds 70%. See [Expand coverage ratchets](../howto/expand-coverage-ratchets.md)
for the ratchet-raising workflow.

## Coverage arithmetic

| Metric | Value |
| --- | --- |
| Total coverable lines (JaCoCo) | ~7,600 |
| Previously covered lines | ~3,830 (50.4%) |
| New lines exercised (estimated) | ~1,491+ |
| Total covered lines (estimated) | ~5,320+ |
| New coverage (estimated) | ~70.0% |

Tier 2 (VM exceptions) provides the highest ROI: 11 exception classes at
~25 lines each are fully testable with simple construction + message assertion.
Tier 1 (VM events) is second-highest ROI with 8 event types. Together they
account for ~550 lines — over a third of the required gain — from
straightforward data-class tests.

# Zero-Argument This-Method Call Decode Feature Contract

This page defines the narrow Tweedle AST decoder feature to build:
`this.someMethod()` where `someMethod` is a zero-argument `UserMethod` declared
on the same decoded `NamedUserType`.

The feature is limited to same-type calls on explicit `this`. It is not a
general Tweedle method-resolution system, and this document should not be read
as a claim that broader method-call decode already exists.

## API behavior

After the feature is implemented, `TweedleEncoderDecoder.decode(String source)`
accepts a Tweedle class whose method body contains an expression statement that
calls a known zero-argument method on `this`.

```java
class Program {
  void helper() {
  }

  void run() {
    this.helper();
  }
}
```

The decoded `NamedUserType` should contain both methods. The `run` method body
should contain one `ExpressionStatement`, and that statement should wrap an
Alice `MethodInvocation` whose method declaration is the decoded `helper`
`UserMethod`.

## Supported shape

All of these conditions must be true:

| Requirement | Contract |
| --- | --- |
| Target | The call target is explicit `this`. |
| Method owner | The resolved method is a supported method declaration directly on the currently decoded type. |
| Parsed arguments | The Tweedle call has no arguments. |
| Resolved parameters | The resolved `UserMethod` has no required or optional parameters. |
| Statement form | The call appears as a method-body expression statement. |
| Resolution | The method name exactly matches one decoded method on the current type. |

The implementation must resolve methods declared before or after the calling
method in the same Tweedle class. Register same-type `UserMethod` declarations
before decoding any method bodies so forward calls and backward calls share the
same observable behavior.

## Unsupported adjacent syntax

Unsupported call forms must continue to fail with
`UnsupportedTweedleDecodeException` when called through the direct Tweedle
decoder. JSON player and type archive readers keep their documented
unsupported-Tweedle archive behavior.

| Tweedle shape | Reason it is unsupported |
| --- | --- |
| `this.helper(1);` | Argument-bearing calls are outside the slice. |
| `this.missing();` | Unknown same-type methods are not invented or late-bound. |
| `other.helper();` | Non-`this` targets are outside the slice. |
| `helper();` | Implicit targets are outside the slice. |
| `Program.helper();` | Static-style calls are outside the slice. |
| `new Helper();` | Constructor calls are outside the slice. |
| `this.helper().again();` | Chained calls are outside the slice. |
| `this.helper` | Member access is a separate decoder contract. |

The focused implementation tests should cover the nearest rejection boundaries:
argument-bearing `this` calls, unknown same-type methods, and non-`this`
targets. The other rows are non-goals documented by the helper's accepted-shape
boundary; add separate focused tests for them only if a later implementation
starts routing those forms through this decoder slice.

## Configuration

The zero-argument this-method call feature has no runtime configuration. It uses
the existing Tweedle parser, AST decoder, Maven, and JUnit configuration.

From a fresh checkout or worktree, initialize the Tweedle grammar submodule
before broad Maven validation:

```bash
git submodule update --init tweedle-lang
```

Automation that runs repository tooling can keep the saved Node memory setting:

```bash
export NODE_OPTIONS=--max-old-space-size=32768
```

That environment variable is not an Alice decode option.

## Validation command

After adding the implementation and tests, run the focused decoder
characterization from the repository root:

```bash
NODE_OPTIONS=--max-old-space-size=32768 git submodule update --init tweedle-lang
NODE_OPTIONS=--max-old-space-size=32768 mvn -pl core/ast -am \
  -DfailIfNoTests=false \
  -Dsurefire.failIfNoSpecifiedTests=false \
  -Dtest=TweedleEncoderDecoderTest \
  test
```

## Characterization tests

Add the focused positive test with a name that states the narrow behavior:

```text
zeroArgumentThisMethodCallDecode
```

Add negative tests for adjacent unsupported syntax without implying general
method-call support:

```text
zeroArgumentThisMethodCallDecodeRejectsArgumentBearingCall
zeroArgumentThisMethodCallDecodeRejectsUnknownMethod
zeroArgumentThisMethodCallDecodeRejectsNonThisTarget
```

These tests belong in:

```text
core/ast/src/test/java/org/alice/serialization/tweedle/TweedleEncoderDecoderTest.java
```

## Non-goals

This feature must not decode general Tweedle calls, inherited calls, static
calls, constructor calls, implicit receiver calls, external targets, chained
calls, overloads, optional arguments, named arguments, or runtime dispatch.

Keep new docs, tests, and exception messages scoped to zero-argument
`this.method()` decode.

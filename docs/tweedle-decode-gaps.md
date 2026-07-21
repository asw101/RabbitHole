# Tweedle decoder-gap backlog

This backlog records the **Tweedle decode gaps** that currently force the hybrid
`.a3c`/`.a3p` reader to fall back to the legacy XML AST payload. It is the
Phase 0 census for the within-archive hybrid export/import work (see
`alice-cmu/tweedle-export/PLAN.md`) and drives the incremental decoder-closure
work (PLAN Phase 5).

Each gap below is a `NamedUserType` that **encodes** to Tweedle source but does
**not** yet **decode** back to an equivalent AST. Until a gap is closed, any
archive containing that type is read via the XML fallback — i.e. exports/imports
are *never worse than today*, but they also don't yet benefit from the bounded,
name-only Tweedle representation.

## How this census is produced

The census is the printed output of the headless characterization test
`SilverThreadTweedleDecoderRoundTripTest`
(`core/story-api-migration/src/test/java/org/lgna/project/io/SilverThreadTweedleDecoderRoundTripTest.java`),
which loads a real `.a3p`, encodes every `NamedUserType` to Tweedle, attempts to
decode it back, and documents each failure with its exact exception message. The
test passes green while gaps remain (it is a characterization test); as gaps are
closed, more types are automatically verified for structural round-trip equality.

Reproduce (headless, offline):

```bash
export JAVA_HOME=/usr/lib/jvm/java-21-openjdk-<arch> GIT_LFS_SKIP_SMUDGE=1
mvn -o -Djavafx.platform=linux -Dinstall4j.skip -Dcheckstyle.skip \
    -Djava.awt.headless=true \
    -pl core/story-api-migration surefire:test \
    -Dtest=SilverThreadTweedleDecoderRoundTripTest
```

## Census — `starters/indiaMinimum.a3p`

`0 / 6` types decode via Tweedle today. All six therefore route through the XML
fallback.

| Type | Gap category | Decoder message |
| --- | --- | --- |
| `Program` | Program type parse | `Unable to parse Tweedle type.` |
| `Scene` | Non-literal field initializer | `Non-literal Tweedle field initializers are not yet supported by the AST decoder: ground` |
| `ElephantStable` | Constructor body | `Tweedle constructor bodies are not yet supported by the AST decoder: ElephantStable` |
| `Prop` | Constructor body | `Tweedle constructor bodies are not yet supported by the AST decoder: Prop` |
| `SandDunes` | Unsupported method parameter | `Unsupported Tweedle method parameter: TerrainResource` |
| `WaterTank` | Unsupported method parameter | `Unsupported Tweedle method parameter: WaterTankResource` |

### Grouped by decoder capability to add (Phase 5 backlog)

1. **Program type parse** (`Program`). The top-level program type does not parse.
   This is the highest-leverage gap: closing it lets whole *projects* prefer the
   Tweedle read path instead of always falling back to XML.
2. **Non-literal field initializers** (`Scene.ground`). The decoder only accepts
   literal field initializers; field initializers that are expressions (e.g. a
   reference to another declaration) are rejected.
3. **Constructor bodies** (`ElephantStable`, `Prop`). The decoder does not yet
   reconstruct statements inside a user-type constructor body.
4. **Resource-typed method parameters** (`SandDunes` → `TerrainResource`,
   `WaterTank` → `WaterTankResource`). Method parameters whose type is a resource
   enum/type are not yet resolved by the decoder.
5. **Comments** (any type containing a `Comment` statement). The Tweedle grammar
   routes `//` line comments and `/* … */` block comments to the lexer's hidden
   channel, so a `Comment` node the encoder emits is dropped on parse. Rather than
   decode to an AST that is silently missing the comment, the decoder now rejects
   comment-bearing source. This gap is not exercised by `indiaMinimum.a3p`; it is
   characterized separately by `TweedleCommentDecodeGapTest`
   (`core/ast/src/test/java/org/alice/serialization/tweedle/TweedleCommentDecodeGapTest.java`).
   Closing it requires a first-class comment representation in
   `org.alice.tweedle.ast` (and grammar support in the `tweedle-lang` submodule),
   which is out of scope for the hybrid export work.

## Next fixtures to add

Per PLAN Phase 0, extend the census beyond `indiaMinimum.a3p` with representative
**curriculum `.a3p`** samples (see `../alice-3-curriculum-md/`) once a licensed,
checked-in sample set is available (PLAN §10 open question). Each new fixture
should be run through the same characterization test and its gaps folded into the
table above.

## Relationship to the hybrid reader

The hybrid reader (`core/story-api-migration/.../io/HybridProjectIo.java`) prefers
the Tweedle representation and falls back to the in-archive XML payload whenever:

- a manifest-declared type hits any of the decode gaps above (the Tweedle read
  throws), or
- the Tweedle read succeeds but does not recover every resource the archive's
  `resources.xml` declares (e.g. generic, non-image/audio resources that the
  Tweedle side does not persist).

The fallback is whole-archive rather than per-type, because mixing
Tweedle-decoded and XML-decoded types into a single project cannot preserve the
object identity that cross-type references rely on. As the gaps in this backlog
are closed, more archives will be read entirely from Tweedle with no behavior
change required in the reader.

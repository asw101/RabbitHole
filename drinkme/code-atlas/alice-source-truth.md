# Alice Code-Atlas Source-Truth Contract

This artifact is generated from the current checked-out source for `rysweet/alice3-modernization`. It defines the durable `drinkme/code-atlas` source-truth contract for the Alice code-atlas bug-hunt lane.

The atlas output is for bug-hunt and refactor planning. It exposes structural risks, stale documentation risks, dead-path candidates, class and module hotspots, and refactor seams without changing Alice runtime behavior.

## Feature contract

| Requirement | Documentation behavior |
| --- | --- |
| Artifact-only lane | Write durable atlas output only under `drinkme/code-atlas/` |
| Source truth first | Derive claims from checked-in files, Maven metadata, tests, and repository docs |
| No runtime changes | Do not modify Alice source or generated runtime output while refreshing atlas docs |
| No source copying | Do not copy Alice source into `drinkme`; cite paths and line ranges instead |
| Profile-aware architecture | Keep default reactor, `includeSims`, and `buildInstaller` boundaries visually distinct |
| Conservative findings | Keep candidates and needs-attention risks separate from confirmed bugs |
| Repository-local tracking | If an issue is later justified, file only against `rysweet/alice3-modernization` |

## How to use these artifacts

| Need | Artifact |
| --- | --- |
| Understand Maven reactor and profile boundaries | [alice-module-graph.mmd](./alice-module-graph.mmd) or [alice-module-graph.dot](./alice-module-graph.dot) |
| Identify large classes and modules before refactoring | [alice-hotspots.md](./alice-hotspots.md) |
| Check stale docs, TODOs, HACKs, and compatibility markers | [alice-staleness-map.md](./alice-staleness-map.md) |
| Review candidate bug-hunt findings | [alice-bughunt-findings.md](./alice-bughunt-findings.md) |

## Source truth

| Source | Role |
| --- | --- |
| `pom.xml` | Root Maven reactor, default modules, profile-gated modules, dependency-management source |
| Module `pom.xml` files | Per-module packaging, plugin, dependency, launch, and generation source |
| Java source under `core/`, `alice-ide/`, `netbeans/`, `installer/`, `external/`, and `core-nonfree/` | Structural and hotspot source |
| `README.md` and checked-in docs | Documentation-drift source |
| `tweedle-lang/Grammar` | Required grammar submodule for broad Maven validation |
| Existing tests | Characterization and integration evidence |

Generated `target/` output is excluded from hotspot metrics unless a finding explicitly discusses generated-code risk.

## Repository and scan metadata

| Field | Value |
| --- | --- |
| Repository | `rysweet/alice3-modernization` |
| Atlas scope | Source-truth architecture and bug-hunt documentation |
| Durable artifact root | `drinkme/code-atlas/` |
| Upstream tracking | Not used for modernization findings |
| Merge policy | No merge without explicit approval |

## Root reactor

The default root reactor is defined in `pom.xml:101-126`.

| Module | Evidence | Notes |
| --- | --- | --- |
| `core` | `pom.xml:102` | Parent/aggregator for core modules |
| `core/ast` | `pom.xml:103` | AST, serialization, VM-related code |
| `core/croquet` | `pom.xml:104` | UI framework |
| `core/i18n` | `pom.xml:105` | Internationalization support |
| `core/ide` | `pom.xml:106` | Main IDE implementation |
| `core/image-editor` | `pom.xml:107` | Image editor module |
| `core/issue-reporting` | `pom.xml:108` | Issue reporting module |
| `core/resources` | `pom.xml:109` | Runtime resources and distribution content |
| `core/scenegraph` | `pom.xml:110` | Scenegraph model |
| `core/glrender` | `pom.xml:111` | OpenGL rendering |
| `core/story-api-migration` | `pom.xml:112` | Project migration logic |
| `core/story-api` | `pom.xml:113` | Story API |
| `core/util` | `pom.xml:114` | Shared utilities |
| `core/model-loading` | `pom.xml:115` | Model loading/export |
| `core/tweedle` | `pom.xml:116` | Tweedle language integration |
| `core/models` | `pom.xml:117` | Built-in model resources |
| `external` | `pom.xml:119` | External parent/aggregator |
| `external/collada` | `pom.xml:120` | Collada support |
| `external/collada-schema-1-4-1` | `pom.xml:121` | Collada schema |
| `external/wrapped-flow-layout` | `pom.xml:122` | Wrapped flow layout dependency |
| `alice-ide` | `pom.xml:124` | Alice IDE launcher module |
| `netbeans` | `pom.xml:125` | NetBeans plugin and project template |

## Profile-gated boundaries

| Boundary | Source truth | Behavior |
| --- | --- | --- |
| `includeSims` | `pom.xml:681-696` | Active by default unless `-DincludeSims=false`; adds `core-nonfree` modules |
| `buildInstaller` | `pom.xml:765-776` | Active only with `-DbuildInstaller=true`; adds `installer` |
| `only-eclipse` | `pom.xml:724-763` | Stores Eclipse m2e lifecycle mapping only; no Maven build behavior change |

The `includeSims` modules are:

| Module | Evidence |
| --- | --- |
| `core-nonfree` | `pom.xml:691` |
| `core-nonfree/ide-nonfree` | `pom.xml:692` |
| `core-nonfree/resources-nonfree` | `pom.xml:693` |
| `core-nonfree/story-api-nonfree` | `pom.xml:694` |
| `core-nonfree/models-nonfree` | `pom.xml:695` |

## Build and validation preconditions

Alice builds that reach `core/tweedle` require the Tweedle grammar submodule. The build enforces `tweedle-lang/Grammar/TweedleLexer.g4` and `tweedle-lang/Grammar/TweedleParser.g4` in `core/tweedle/pom.xml:61-73`.

Use these checks before broad Maven validation:

```bash
git submodule status tweedle-lang
test -d tweedle-lang/Grammar
```

If the grammar directory is missing:

```bash
git submodule update --init tweedle-lang
```

The README documents the same checkout diagnostic in `README.md:26-43`.

## Runtime and packaging seams

| Seam | Evidence | Atlas treatment |
| --- | --- | --- |
| Alice IDE launch root | `alice-ide/pom.xml:172-180` sets `org.alice.ide.rootDirectory` to `../core/resources/target/distribution` | Show as a runtime-root dependency in both module graphs |
| ApplicationRoot hard failure | `core/util/src/main/java/edu/cmu/cs/dennisc/app/ApplicationRoot.java:61-87` exits when `org.alice.ide.rootDirectory` is missing or invalid | Track as a refactor-risk seam, not a bug by itself |
| NetBeans NBM packaging | `netbeans/pom.xml:430-441` uses `nbm-maven-plugin` and `installerPack200Enable=true` | Track as a packaging compatibility seam |
| NetBeans Ant smoke template | `netbeans/src/test/java/org/alice/netbeans/Alice3ProjectTemplateAntSmokeTest.java:33-67` validates packaged template classpath and jar output | Use as characterization evidence for NetBeans template behavior |
| Optional nonfree classpath outputs | `netbeans/src/test/java/org/alice/netbeans/Alice3ProjectTemplateAntSmokeTest.java:215-223` maps expected module outputs | Keep as a candidate finding until focused NetBeans smoke validation reproduces the mismatch |

## Configuration reference

| Command | Purpose |
| --- | --- |
| `mvn compile install` | Builds and installs default reactor artifacts |
| `mvn -DincludeSims=false compile install` | Builds without default-active nonfree Sims modules |
| `mvn -DbuildInstaller=true install` | Includes the installer module and Install4J path |
| `mvn test` | Runs repository tests after required submodules and dependencies are present |

Install4J is only required for installer builds, matching `README.md:17` and `README.md:55-59`.

## Refresh procedure

Regenerate atlas output from source truth, not external issue trackers:

```bash
drinkme/code-atlas/bin/build-alice-code-atlas --repo-root . --output-dir drinkme/code-atlas --format both
```

After refreshing, confirm:

1. Every cited path exists in the repository.
2. Default, `includeSims`, and `buildInstaller` boundaries remain visually distinct in both graphs.
3. Hotspot metrics exclude generated output under `target/`.
4. Candidate findings remain labeled as candidate or needs-attention until verified.
5. Any issue filing targets only `rysweet/alice3-modernization`.

## Reporting standard

| Status | Meaning |
| --- | --- |
| `confirmed` | Source/build/test evidence proves a current defect or stale statement |
| `candidate` | Evidence suggests a defect, but focused reproduction is still required |
| `needs-attention` | Risk is real enough to track but not necessarily a defect |
| `pass` | Checked area currently aligns with source truth |
| `speculative` | Observation is useful for future review but not validated |

Do not report speculative observations as bugs. File repository-scoped issues only when the evidence is strong enough and the scope belongs to `rysweet/alice3-modernization`.

# Alice Code-Atlas Bug-Hunt Findings

This generated findings register is scoped only to `rysweet/alice3-modernization`. Candidate and needs-attention items guide future validation, but they are not implementation changes or issue filings by themselves.

## Status vocabulary

| Status | Meaning |
| --- | --- |
| `confirmed` | Current source/build/test evidence proves a defect or stale statement |
| `candidate` | Evidence indicates a likely defect, but focused reproduction is still required |
| `needs-attention` | Real structural or modernization risk; not necessarily a defect |
| `pass` | Checked surface aligns with source truth |
| `speculative` | Useful observation without enough evidence for action |

## Findings

### Finding: NetBeans optional nonfree module output paths appear inconsistent

Status: candidate

| Field | Value |
| --- | --- |
| Severity | Medium |
| Status | candidate |
| Category | Structural bug candidate, packaging/test seam |
| Evidence | `netbeans/src/test/java/org/alice/netbeans/Alice3ProjectTemplateAntSmokeTest.java:222-223` maps `models-nonfree` to `../core-nonfree/models/...` and `story-api-nonfree` to `../core-nonfree/story-api/...`; root `includeSims` modules are `core-nonfree/models-nonfree` and `core-nonfree/story-api-nonfree` in `pom.xml:691-695` |
| Impact | When optional nonfree module output directories exist, the smoke test may fail to resolve them from module classes and may rely on jar fallback instead. If jar fallback is unavailable, NetBeans project-template classpath coverage can miss optional nonfree artifacts. |
| Suggested next action | Run a focused NetBeans Ant smoke validation with `includeSims` outputs present. If the mismatch reproduces, file a repository issue with label `code-atlas-bughunt` and update the path mapping to the `*-nonfree` module directories as separate implementation work. |

### Finding: Alice IDE launch root is a hard runtime seam

Status: needs-attention

| Field | Value |
| --- | --- |
| Severity | Medium |
| Status | needs-attention |
| Category | Runtime seam, refactor risk |
| Evidence | `alice-ide/pom.xml:172-180` sets `org.alice.ide.rootDirectory` to `../core/resources/target/distribution`; `core/util/src/main/java/edu/cmu/cs/dennisc/app/ApplicationRoot.java:61-87` shows an error dialog and exits when the property is missing or invalid |
| Impact | Launch behavior depends on a generated resources distribution path. Refactors to launch, resources, or build output can break startup without touching IDE code directly. |
| Suggested next action | Keep this seam explicit in diagrams and validation. Add launch characterization before changing resource distribution, `exec-maven-plugin`, or `ApplicationRoot` behavior. |

### Finding: Tweedle grammar submodule is a required validation precondition

Status: pass

| Field | Value |
| --- | --- |
| Severity | Medium |
| Status | pass |
| Category | Build precondition |
| Evidence | `core/tweedle/pom.xml:61-73` enforces `tweedle-lang/Grammar/TweedleLexer.g4` and `TweedleParser.g4`; `README.md:26-43` documents submodule initialization and diagnostics |
| Impact | Broad Maven validation fails early and clearly when the grammar submodule is missing. The current README and Maven configuration agree. |
| Suggested next action | Keep this precondition in atlas refresh and validation instructions. If Maven reports missing generated Tweedle parser classes, check submodule status before debugging parser generation. |

### Finding: NetBeans Pack200 packaging flag is a modernization compatibility seam

Status: needs-attention

| Field | Value |
| --- | --- |
| Severity | Low |
| Status | needs-attention |
| Category | Packaging compatibility risk |
| Evidence | `netbeans/pom.xml:430-441` configures `nbm-maven-plugin` version `14.2` and `installerPack200Enable=true` |
| Impact | Pack200-era packaging assumptions may become fragile during NetBeans plugin or JDK modernization. This is not a confirmed bug in the current build by itself. |
| Suggested next action | Revalidate during NetBeans tooling upgrades. Do not remove or change packaging flags without NetBeans plugin build/install characterization. |

### Finding: XML decoder compatibility maps are explicitly hack-labeled

Status: needs-attention

| Field | Value |
| --- | --- |
| Severity | Medium |
| Status | needs-attention |
| Category | Serialization refactor risk |
| Evidence | `core/ast/src/main/java/org/alice/serialization/xml/Decoder.java:170`, `:264`, `:266`, `:349-365`, and `:452-454` reference `EPIC_HACK` maps for array, getter, and setter key compatibility |
| Impact | These maps likely preserve compatibility with legacy serialized projects. Simplifying them without fixtures can break loading older Alice projects. |
| Suggested next action | Add legacy project load fixtures before touching XML decoder compatibility paths. Treat cleanup as behavior-sensitive implementation work, not atlas work. |

### Finding: Hotspot concentration is high in IDE, story API, Croquet, and migration code

Status: needs-attention

| Field | Value |
| --- | --- |
| Severity | Medium |
| Status | needs-attention |
| Category | Refactor risk |
| Evidence | `core/ide` is the largest Java module by source LOC; `core/story-api`, `core/croquet`, and `core-nonfree/story-api-nonfree` are also large; `core/story-api-migration/src/main/java/org/lgna/project/migration/ProjectMigrationManager.java` is the largest Java source file |
| Impact | Large modules and files are more likely to hide implicit coupling, broad behavior dependencies, and insufficient characterization. Size alone is not a bug. |
| Suggested next action | Use [alice-hotspots.md](./alice-hotspots.md) to choose characterization tests before refactoring hotspot files or modules. |

## Passes

| Surface | Status | Evidence |
| --- | --- | --- |
| Root reactor represented in atlas | pass | `pom.xml:101-126`; [alice-module-graph.mmd](./alice-module-graph.mmd); [alice-module-graph.dot](./alice-module-graph.dot) |
| `includeSims` boundary represented separately | pass | `pom.xml:681-696`; both module graphs |
| `buildInstaller` boundary represented separately | pass | `pom.xml:765-776`; both module graphs |
| Generated output excluded from hotspot metrics | pass | [alice-hotspots.md](./alice-hotspots.md) metric method |
| Upstream issue database excluded | pass | This findings file is scoped only to `rysweet/alice3-modernization` |

## Issue filing rule

The feature must support issue-ready evidence, but issue filing is not automatic. File an issue only when the finding is `confirmed` or when a `candidate` has focused reproduction evidence. Issues must be opened only against `rysweet/alice3-modernization` and should use the `code-atlas-bughunt` label when available.

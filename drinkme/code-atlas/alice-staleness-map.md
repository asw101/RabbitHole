# Alice Staleness Map

This generated staleness map classifies stale documentation risks, compatibility markers, TODO/FIXME/HACK clusters, and dead-path candidates discovered by source-truth scans.

Unverified stale-looking text remains `candidate`, `needs-attention`, or `not-a-bug`; it is not promoted to `confirmed` without focused evidence.

## Documentation drift checks

| Area | Status | Evidence | Notes |
| --- | --- | --- | --- |
| Repository identity | pass | `README.md:21-24` clones `rysweet/alice3-modernization` | README points at the modernization repository |
| Java and Maven build tools | pass | `README.md:11-18` lists Java 21, Maven 3.9.9+, git, git-lfs, Install4J for installers | Aligns with current build expectations |
| Tweedle submodule precondition | pass | `README.md:26-43`; `core/tweedle/pom.xml:61-73` | README and Maven enforcer both document grammar requirement |
| Installer profile | pass | `README.md:55-59`; `pom.xml:765-776` | README uses `-DbuildInstaller=true`, matching root profile |
| NetBeans build output | pass | `README.md:49-54`; `netbeans/pom.xml:430-441` | README states default install builds NetBeans plugin |
| Alice IDE execution | needs-attention | `README.md:63-67`; `alice-ide/pom.xml:172-180`; `ApplicationRoot.java:61-87` | Launch depends on generated `core/resources/target/distribution` and fails hard if missing |
| docs freshness | needs-attention | checked-in docs plus atlas refresh evidence | Architecture docs should be refreshed when Maven reactor, launcher, NetBeans packaging, or major module ownership changes |

## Build and packaging compatibility markers

| Marker | Status | Evidence | Impact |
| --- | --- | --- | --- |
| `includeSims` active by default | needs-attention | `pom.xml:681-696` | Builds include nonfree modules unless `-DincludeSims=false`; atlas diagrams show this separately |
| Installer isolated behind profile | pass | `pom.xml:765-776` | Install4J path is not part of default reactor |
| NetBeans Pack200 enabled | needs-attention | `netbeans/pom.xml:430-441` | Pack200 is a legacy packaging seam; validate when upgrading NetBeans tooling or JDK assumptions |
| Generated parser output under target/ | pass | `core/tweedle/pom.xml:90-107` | Generated sources are intentionally excluded from hotspot metrics |

## Marker inventory

The source-truth marker scan found approximately 609 matching lines across source, Maven XML, README, and docs files after excluding generated build output.

| Marker | Status | Classification |
| --- | --- | --- |
| TODO | needs-attention | Review concrete implementation TODOs before related refactors |
| FIXME | needs-attention | Treat any occurrence as a focused follow-up, not automatic proof of a defect |
| HACK | needs-attention | Preserve compatibility unless a fixture proves the workaround is obsolete |
| XXX | needs-attention | Review as modernization debt when touching nearby code |
| Deprecated | needs-attention | Distinguish API compatibility from dead code before removal |
| deprecated | needs-attention | Same as `Deprecated`; do not remove solely by text match |

## TODO/FIXME/HACK clusters

| Cluster | Status | Evidence | Interpretation |
| --- | --- | --- | --- |
| NetBeans project wizard I18n TODOs | needs-attention | `netbeans/src/main/java/org/alice/netbeans/Alice3ProjectTemplatePanelVisual.java:176`, `:218`, `:285`, `:292`, `:299`, `:305`, `:317`, `:323`, `:331`; `Alice3ProjectTemplateWizardIterator.java:136`, `:141`, `:263`, `:270` | Localized UI polish and modernization debt |
| NetBeans wizard API TODOs | needs-attention | `Alice3ProjectTemplatePanelVisual.java:298`; `Alice3ProjectTemplateWizardIterator.java:185` | Legacy NetBeans API comments should be rechecked during NetBeans plugin modernization |
| Alice IDE entrypoint setting TODO | needs-attention | `alice-ide/src/main/java/org/alice/stageide/EntryPoint.java:91` | Runtime setting or OS auto-detection behavior is intentionally unresolved |
| AST Tweedle serialization TODOs | needs-attention | `core/ast/src/main/java/org/alice/serialization/tweedle/Decoder.java:21`, `:26`; `Encoder.java:459`, `:681` | Serialization refactors require fixtures before cleanup |
| XML decoder `EPIC_HACK` compatibility maps | needs-attention | `core/ast/src/main/java/org/alice/serialization/xml/Decoder.java:170`, `:264`, `:266`, `:349-365`, `:452-454` | Naming signals compatibility workaround; do not remove without legacy project fixtures |
| NetBeans generated template comments | not-a-bug | `netbeans/src/main/resources/ProjectTemplate/nbproject/build-impl.xml:671` | Template-generated or compatibility text; do not file as source defect by itself |
| Palette snippet `TODO: Code goes here` | not-a-bug | `Alice3CompletionProvider.java:61`; `DoTogether.java:67`; localized bundle hints | User-facing code templates, not implementation TODOs |
| Logger `TODO` level | not-a-bug | `core/util/src/main/java/edu/cmu/cs/dennisc/java/util/logging/Logger.java:63`, `:98`, `:191`, `:195` | Log-level naming, not stale implementation marker |

## Dead-path candidates

| Candidate | Status | Evidence | Next check |
| --- | --- | --- | --- |
| NetBeans optional nonfree module-output paths | candidate | `Alice3ProjectTemplateAntSmokeTest.java:222-223` maps `models-nonfree` to `../core-nonfree/models/...` and `story-api-nonfree` to `../core-nonfree/story-api/...`; root profile modules are `core-nonfree/models-nonfree` and `core-nonfree/story-api-nonfree` in `pom.xml:691-695` | Run focused NetBeans Ant smoke validation with `includeSims` outputs present; confirm whether classpath falls back to jars or misses module outputs |
| Alice IDE root fallback path | needs-attention | `ApplicationRoot.java:61-87` exits when the system property is missing or invalid; comment at `ApplicationRoot.java:62` asks about fallback | Treat as intentional fail-fast until launch behavior is explicitly redesigned and tested |
| NetBeans optional artifacts ignored when absent | needs-attention | `Alice3ProjectTemplateAntSmokeTest.java:36`, `:123-133` | Confirm optional classification still matches intended plugin behavior |

## Source-derived refresh checks

Use a source scan equivalent to:

```bash
rg -n "TODO|FIXME|HACK|XXX|@Deprecated|deprecated|unused|dead code|not used|stale|legacy" \
  core alice-ide netbeans installer external core-nonfree README.md docs \
  --glob '*.{java,md,xml,properties}' \
  --glob '!**/target/**'
```

Classify each result before reporting it. Template example text, localized example snippets, and logger level names are usually not bugs.

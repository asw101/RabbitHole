# Pass 2 Cross-Check

## Independent contradictions found before reading Pass 1 reports

- **Save/export split is real, not a single XML lane.** `docs/atlas/api-contracts/README.md:11` and `docs/atlas/data-flow/README.md:11` describe one XML-backed save/export path, but `core/ide/src/main/java/org/alice/ide/ProjectFileUtilities.java:121-123,219-221` and `core/story-api-migration/src/main/java/org/lgna/project/io/IoUtilities.java:103-110,191-197` split save (`writeProject` -> `XmlProjectIo`) from export (`exportProject` -> `JsonProjectIo`).
- **Journey 5 overstates CI behavior.** `docs/atlas/user-journeys/README.md:315-320` presents test and coverage workflows as unconditional after a push/PR, but `.github/workflows/alice-test-ci.yml:3-7,141-174` and `.github/workflows/alice-coverage-ci.yml:3-7,138-200` both restrict triggers to `develop` and can no-op PRs with docs/QA/tests/scripts-only changes.
- **The Tweedle VM is drawn against the wrong listener surface.** `docs/atlas/api-contracts/README.md:51-52,97` links `org.alice.tweedle.run.VirtualMachine` to `VirtualMachineListener`, but `core/tweedle/src/main/java/org/alice/tweedle/run/VirtualMachine.java:48-104` has no listener API; the listener surface lives on `core/ast/src/main/java/org/lgna/project/virtualmachine/VirtualMachine.java:455-463` and `core/ast/src/main/java/org/lgna/project/virtualmachine/events/VirtualMachineListener.java:48-69`.
- **`story-api-migration` is more central than the dependency atlas shows.** `docs/atlas/compile-deps/README.md:40-59` omits direct `ide`/`netbeans` edges, while `core/ide/pom.xml:91-94`, `netbeans/pom.xml:206-209`, and project-I/O imports in `core/ide/src/main/java/org/alice/ide/uricontent/AbstractFileProjectLoader.java:51-52` and `netbeans/src/main/java/org/alice/netbeans/project/ProjectCodeGenerator.java:53` show those edges are direct and on active code paths.

## Pass 1 findings

### `pass1-ci-trigger-scope-overstated.md` — **CONFIRMED**
`docs/atlas/user-journeys/README.md:315-317` says a developer can `push branch / PR` and get both workflows, but `.github/workflows/alice-test-ci.yml:3-7` and `.github/workflows/alice-coverage-ci.yml:3-7` only trigger on pushes to `develop` or PRs targeting `develop`. Pass 1 is correct that the atlas overstates the trigger scope.

### `pass1-export-json-path-missing.md` — **CONFIRMED**
Pass 1 correctly identified a collapsed save/export lane. `core/ide/src/main/java/org/alice/ide/ProjectFileUtilities.java:121-123` sends export through `IoUtilities.exportProject(...)`, while `core/ide/src/main/java/org/alice/ide/ProjectFileUtilities.java:219-221` sends save through `IoUtilities.writeProject(...)`. `core/story-api-migration/src/main/java/org/lgna/project/io/IoUtilities.java:108-110,196-197` shows export uses `JsonProjectIo.writer()`, while `core/story-api-migration/src/main/java/org/lgna/project/io/IoUtilities.java:99-105,191-193` shows save uses `XmlProjectIo.writer()`.

### `pass1-hidden-story-api-migration-edge.md` — **CONFIRMED**
`docs/atlas/compile-deps/README.md:40-59` leaves `story-api-migration` hidden behind `support`, but `core/ide/pom.xml:91-94` and `netbeans/pom.xml:206-209` declare direct dependencies on it. Those edges are exercised on live code paths via `core/ide/src/main/java/org/alice/ide/uricontent/AbstractFileProjectLoader.java:51-52` and `netbeans/src/main/java/org/alice/netbeans/project/ProjectCodeGenerator.java:53`. Pass 1 correctly called this out as a real compile-time omission.

### `pass1-stale-createaperson-reference.md` — **CONFIRMED**
The stale name is still present in both `drinkme/dragadapter-selection-extraction.md:162` and `core/story-api/src/main/java/org/alice/interact/DragAdapter.java:84-85`. Current drag-adapter subclasses visible in source include `core/story-api/src/main/java/org/alice/interact/RuntimeDragAdapter.java:63`, `core/ide/src/main/java/org/alice/stageide/sceneeditor/interact/GlobalDragAdapter.java:81`, `core/ide/src/main/java/org/alice/interact/PoserAnimatorDragAdapter.java:66`, `core/ide/src/main/java/org/alice/stageide/modelviewer/SingleViewerDragAdapter.java:54`, and `core/ide/src/main/java/edu/cmu/cs/dennisc/ui/lookingglass/OnscreenLookingGlassDragAdapter.java:53`; a repo-wide search found no `CreateAPersonDragAdapter` definition.

### `pass1-public-internals-unreferenced.md` — **CONFIRMED**
The five classes are still public in `core/story-api`: `core/story-api/src/main/java/org/alice/interact/manipulator/SnapUtilities.java:71`, `core/story-api/src/main/java/org/alice/interact/manipulator/MouseRelativeObjectDragManipulator.java:67`, `core/story-api/src/main/java/org/alice/interact/manipulator/ObjectTranslateDragManipulator.java:68`, `core/story-api/src/main/java/org/lgna/story/implementation/visualization/GlrJointedModelVisualization.java:70`, and `core/story-api/src/main/java/org/lgna/story/implementation/eventhandling/EventManager.java:75`. A search across non-`core/story-api` production modules returned no matches, while internal callers remain (`core/story-api/src/main/java/org/lgna/story/implementation/SceneImp.java:103-104`, `core/story-api/src/main/java/org/lgna/story/SScene.java:143-156`, `core/story-api/src/main/java/org/lgna/story/implementation/visualization/JointedModelVisualization.java:53-56`, `core/story-api/src/main/java/org/alice/interact/RuntimeDragAdapter.java:90-94`). The public-but-intra-module-only surface is real, though remediation may need package moves/refactoring rather than a trivial visibility flip.

## New bugs Pass 1 missed

### New bug 1 — API contracts misstate the Tweedle VM listener path
**Layer**: api-contracts x ast-lsp-bindings  
**Severity**: Medium

**Evidence**
- `docs/atlas/api-contracts/README.md:48-56,97` draws `tweedlevm` as an alternate interpreter surface feeding `VirtualMachineListener`.
- `core/tweedle/src/main/java/org/alice/tweedle/run/VirtualMachine.java:48-104` defines `ENTRY_POINT_evaluate`, `ENTRY_POINT_invoke`, `ENTRY_POINT_createInstance`, and statement execution helpers, but no listener registration methods.
- `core/ast/src/main/java/org/lgna/project/virtualmachine/events/VirtualMachineListener.java:48-69` and `core/ast/src/main/java/org/lgna/project/virtualmachine/VirtualMachine.java:455-463` show that listener registration belongs to the AST VM family, not the Tweedle VM.

**Impact**
A reader tracing statement/expression listener behavior from the atlas can be sent into `core/tweedle` even though the listener contract is implemented by a different VM stack.

**Fix**
Remove the `tweedlevm -> vmlisten` edge or relabel it so the listener surface is attached only to `org.lgna.project.virtualmachine.VirtualMachine`/`ReleaseVirtualMachine`.

### New bug 2 — Journey 5 omits the PR no-op branch that skips Maven and coverage
**Layer**: user-journeys x runtime-topology of CI  
**Severity**: Medium

**Evidence**
- `docs/atlas/user-journeys/README.md:315-320` shows the triggered CI path always running Maven tests, JaCoCo, summary generation, and artifact upload.
- `.github/workflows/alice-test-ci.yml:141-174` sets `maven-required=false` for docs/QA/tests/scripts/license-only PRs and then reports `Maven validation skipped` instead of running `clean test`.
- `.github/workflows/alice-coverage-ci.yml:138-200` applies the same `maven-required=false` branch and skips `-Pcoverage verify`, summary generation, and artifact upload for those PRs.

**Impact**
The atlas currently implies that every eligible PR CI run produces test/coverage evidence, but docs-only and other non-Java PRs intentionally short-circuit before Maven.

**Fix**
Add an alternate Journey 5 branch for `pull_request` runs where change classification marks `maven-required=false`, and show that those runs report a no-op instead of building coverage artifacts.

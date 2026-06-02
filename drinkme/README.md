# drinkme — Alice 3 Modernization Status & Artifacts

## Current Status (May 2026)

### Java Desktop — [rysweet/RabbitHole](https://github.com/rysweet/RabbitHole)

| Metric | Value |
|--------|-------|
| Branch | `develop` |
| Test coverage | **74%** (JaCoCo aggregate: 54,796 / 74,012 lines) |
| Test files added | 150+ new behavioral test files |
| CI | Green on Ubuntu + macOS, headless |
| Key areas tested | AST, story-api (entities, events, animation, interaction), scenegraph (transforms, geometry, IO), model-loading (Collada/glTF parsers, exporters), Tweedle (parser, VM), IDE (project IO, scene editor, code editor), Croquet (UI framework), math (Jama linear algebra) |

### TypeScript Web Port — [rysweet/alice-web-prototype](https://github.com/rysweet/alice-web-prototype)

| Metric | Value |
|--------|-------|
| Branch | `main` |
| Version | `0.16.0` |
| Source files | 335 |
| Source lines | 76,118 |
| Tests | 3,127 (Vitest, 244 test files) |
| Build | `npm run build:server` clean |
| Key subsystems | Tweedle (parser, VM, compiler, type system, stdlib, debugger), AST (80+ node types, serialization, manipulation, editor), Story API (29 entity types with named joint accessors, camera/scene API, properties, animations, behaviors, events, methods, lifecycle, movement, joints, vehicles, listener convenience methods), Scenegraph (transforms, hierarchy, scene management, setup), Renderer (pipeline, materials, shaders, textures, effects, text, mesh, animation, picking), IDE (code editor, procedure editor, declaration editor, type browser, gallery, debugging, dialogs, code completion, keyboard shortcuts, layout, drag-drop, perspectives, state management), Croquet (state machine, operations, codecs, composites), Audio (WebAudioPlayer, SayOutLoud TTS), Project I/O (backup/revert, recent projects, DynamicResource), Infrastructure (A3P parse/write, project system, collaboration, persistence, plugin system, export, accessibility, web runtime, state sync, network layer, performance monitoring) |

### End-to-End Test Suite — [rysweet/eatme](https://github.com/rysweet/eatme)

| Metric | Value |
|--------|-------|
| Branch | `main` |
| Total tests | 1,393 |
| Curriculum scenarios | 52 (YAML definitions covering full Alice.org curriculum) |
| Web platform scenarios | 26 (run same curriculum against TS web port REST API) |
| Coverage | Scene building, procedures, functions, parameters, variables, loops, conditionals, events, collision, proximity, doInOrder/doTogether, arrays, comments, inheritance/OOP, camera, audio, vehicles, joints/IK, drag-drop, debugging, project IO, accessibility, performance, instructor tools, student workflows |

### What's left

- **Java**: Coverage at 74%, well above 70% target. Remaining uncovered code is mostly Swing GUI rendering (paint, mouse handlers, OpenGL adapters) that can't run headless. Zero open issues or PRs.
- **TypeScript**: Feature parity achieved across all major Java subsystems (PRs #79, #83, #85 closed 15 parity issues). TS is inherently more concise (~3-5x fewer lines for equivalent functionality). Three structural blockers remain as reference issues (#86–#88): proprietary 3D model assets, scene graph design differences (AWT/Swing vs Three.js), and Croquet UI layer (Swing vs HTML/browser).
- **Eatme**: Full curriculum coverage with dual-platform support. Desktop tests gated behind `EATME_REAL_ALICE=1`, web tests behind `EATME_WEB_PLATFORM=1`. Two open enhancement issues: #264 (step block composition for gadugi adapter) and #276 (desktop save-reopen integration tests).

---

## Investigation & Refactoring Artifacts

This directory also contains design documents and investigation notes
produced during the modernization effort. Each file records a specific
refactoring that was analyzed, planned, or completed.

**These are reference documents, not source code.** They exist so that future
contributors can understand *why* specific refactorings were done and what
trade-offs were considered.

## Contents

| Document | What it covers |
|----------|---------------|
| [asgencoder-decomposition](asgencoder-decomposition.md) | Extracting `BinaryArrayEncoder` from the scene graph serializer |
| [ast-utilities-decomposition](ast-utilities-decomposition.md) | Breaking `AstUtilities` into focused helper classes |
| [awtcomponentview-hierarchy-extraction](awtcomponentview-hierarchy-extraction.md) | Extracting UI hierarchy lifecycle into `AwtHierarchyHandler` |
| [dragadapter-selection-extraction](dragadapter-selection-extraction.md) | Extracting selection logic from `DragAdapter` into `DragSelectionController` |
| [formal-spec-save-load-export-evaluation](formal-spec-save-load-export-evaluation.md) | Formal specification for Alice project save/load/export |
| [glrender-coverage-push-test-suite](glrender-coverage-push-test-suite.md) | Test suite design for the OpenGL rendering module |
| [ide-scene-setup-extraction](ide-scene-setup-extraction.md) | Extracting scene setup logic from `IDE.java` into `SceneSetupManager` |
| [ikprogram-dead-code-extraction](ikprogram-dead-code-extraction.md) | Removing dead code from `IkProgram` and extracting `IkProgramLauncher` |
| [javatype-decomposition](javatype-decomposition.md) | Breaking `JavaType` into focused helper classes |
| [singleselectliststate-decomposition](singleselectliststate-decomposition.md) | Breaking `SingleSelectListState` into focused helper classes |

## How to use these

- **Before refactoring a large class**, check if there's already an
  investigation document here. It may save you hours of analysis.
- **After completing a refactoring**, add a document here recording what
  was done and why, so the next person doesn't have to reverse-engineer it.
- **Do not put source code here.** Source code belongs in `src/`. This
  directory is for investigation artifacts only.

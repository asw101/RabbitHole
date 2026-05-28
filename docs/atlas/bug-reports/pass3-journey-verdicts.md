# Pass 3 Journey Verdicts

## Journey 1: Student opens Alice, loads project, edits procedure, runs world
**Verdict**: PASS
**Trace**:
- ✅ Journey sequence is documented end-to-end in `docs/atlas/user-journeys/README.md:14-46`.
- ✅ Launch/load path matches the atlas and code: `docs/atlas/runtime-topology/README.md:16-45`, `docs/atlas/api-contracts/README.md:79-90`, and `docs/atlas/data-flow/README.md:19-25` align with `alice-ide/src/main/java/org/alice/stageide/EntryPoint.java:104-149`, `core/ide/src/main/java/org/alice/ide/uricontent/AbstractFileProjectLoader.java:71-81`, and `core/story-api-migration/src/main/java/org/lgna/project/io/IoUtilities.java:83-130`.
- ✅ Editor/navigation path matches component layers: `docs/atlas/service-components/README.md:15-59,117-159` and `docs/atlas/runtime-topology/README.md:24-37` match `core/ide/src/main/java/org/alice/ide/ProjectDocumentFrame.java:244-334`, `core/ide/src/main/java/org/alice/ide/declarationseditor/DeclarationsEditorComposite.java:55-82`, `core/ide/src/main/java/org/alice/ide/declarationseditor/TypeMenu.java:91-127`, and `core/ide/src/main/java/org/alice/ide/codeeditor/CodeEditor.java:74-106`.
- ✅ Run/render path matches the atlas and code: `docs/atlas/api-contracts/README.md:48-76,92-99`, `docs/atlas/data-flow/README.md:22-25`, and `docs/atlas/runtime-topology/README.md:30-45` align with `core/ide/src/main/java/org/alice/stageide/run/RunComposite.java:121-139`, `core/ide/src/main/java/org/alice/stageide/program/ProgramContext.java:97-159`, `core/story-api/src/main/java/org/lgna/story/SProgram.java:73-80`, and `core/story-api/src/main/java/org/lgna/story/implementation/ProgramImp.java:245-299`.
- ✅ Layer-8 coverage is coarse but consistent on this path: `docs/atlas/ast-lsp-bindings/README.md:10-13,40-58` still shows `core/ide` bound to `ast`, `story-api`, and `scenegraph`, which matches the checked code path.
**Issues found**: None material on the traced path.

## Journey 2: Student adds entity to scene, positions it, saves
**Verdict**: PASS
**Trace**:
- ✅ Journey sequence is documented in `docs/atlas/user-journeys/README.md:90-120`.
- ✅ Setup-scene topology matches code: `docs/atlas/runtime-topology/README.md:20-33` and `docs/atlas/service-components/README.md:117-159` align with `core/ide/src/main/java/org/alice/stageide/perspectives/SetupScenePerspective.java:66-88`, `core/ide/src/main/java/org/alice/stageide/gallerybrowser/GalleryComposite.java:63-106`, and `core/ide/src/main/java/org/alice/stageide/sceneeditor/StorytellingSceneEditor.java:129-135,479-480`.
- ✅ Gallery drop path is real: `docs/atlas/api-contracts/README.md:31-33,72-76,95` and `docs/atlas/data-flow/README.md:32-34` align with `core/ide/src/main/java/org/alice/stageide/sceneeditor/SceneEditorDropReceptor.java:73-118`, `core/ide/src/main/java/org/alice/stageide/sceneeditor/interact/GlobalDragAdapter.java:147-158,266-268`, and `core/ide/src/main/java/org/alice/stageide/gallerybrowser/uri/UriGalleryDragModel.java:268-289`.
- ✅ The drop mutates the scene AST and live scene state: `core/ide/src/main/java/org/alice/stageide/modelresource/AddFieldCascade.java:56-68`, `core/ide/src/main/java/org/alice/ide/croquet/edits/ast/DeclareGalleryFieldEdit.java:63-97`, `core/ide/src/main/java/org/alice/ide/sceneeditor/AbstractSceneEditor.java:250-265`, and `core/ide/src/main/java/org/alice/stageide/sceneeditor/StorytellingSceneEditor.java:338-349` match the atlas' scene-field/instance step.
- ✅ Positioning path lands on scenegraph transforms: `docs/atlas/api-contracts/README.md:72-76,98-99` matches `core/ide/src/main/java/org/alice/stageide/sceneeditor/interact/GlobalDragAdapter.java:154-180`, `core/story-api/src/main/java/org/alice/interact/manipulator/OmniDirectionalDragManipulator.java:284-286`, and `core/scenegraph/src/main/java/edu/cmu/cs/dennisc/scenegraph/AbstractTransformable.java:108-156`.
- ✅ Save path matches the save (not export) lane: `docs/atlas/data-flow/README.md:36-40` and `docs/atlas/api-contracts/README.md:79-89` align with `core/ide/src/main/java/org/alice/ide/ProjectApplication.java:330-376`, `core/ide/src/main/java/org/alice/ide/ProjectFileUtilities.java:57-63,219-222`, and `core/story-api-migration/src/main/java/org/lgna/project/io/IoUtilities.java:99-105`.
**Issues found**: None material on the traced path.

## Journey 3: Student creates custom class, adds methods, uses it in scene
**Verdict**: NEEDS_ATTENTION
**Trace**:
- ✅ Journey sequence is documented in `docs/atlas/user-journeys/README.md:162-189`.
- ⚠️ The custom-type creation pieces exist, but the exact atlas handoff is only partially evidenced. `core/ide/src/main/java/org/alice/stageide/type/croquet/OtherTypeDialog.java:188-253` shows the dialog returning a selected type from the project/type tree, and `core/ide/src/main/java/org/alice/ide/typemanager/TypeManager.java:73-120,426-446` shows `TypeManager` creating a `NamedUserType` via `createTypeFor(...)` when needed. But the direct `OtherTypeDialog -> TypeManager` edge drawn in `docs/atlas/user-journeys/README.md:178-180` is not visible in the checked production callers.
- ⚠️ The nearest production callers are split across separate surfaces: `core/ide/src/main/java/org/alice/ide/ast/declaration/DeclarationLikeSubstanceComposite.java:196-214` uses `OtherTypeDialog`, while `core/ide/src/main/java/org/alice/ide/ast/declaration/AddPredeterminedValueTypeManagedFieldComposite.java:82-90` uses `TypeManager.getNamedUserTypeFromSuperType(...)`. That supports the capability set, but not the exact direct sequence claimed by the journey.
- ✅ Method creation and editing are real: `docs/atlas/service-components/README.md:21-29,43-58` matches `core/ide/src/main/java/org/alice/ide/ast/declaration/AddProcedureComposite.java:56-71`, `core/ide/src/main/java/org/alice/ide/ast/declaration/AddFunctionComposite.java:55-70`, `core/ide/src/main/java/org/alice/ide/ast/declaration/AddMethodComposite.java:97-109`, `core/ide/src/main/java/org/alice/ide/croquet/edits/ast/DeclareMethodEdit.java:136-146`, and `core/ide/src/main/java/org/alice/ide/codeeditor/CodeEditor.java:78-106`.
- ✅ Using the custom type as a scene field is real: `core/ide/src/main/java/org/alice/ide/ast/declaration/AddUnmanagedFieldComposite.java:62-104`, `core/ide/src/main/java/org/alice/ide/ast/declaration/AddFieldComposite.java:120-158`, and `core/ide/src/main/java/org/alice/ide/croquet/edits/ast/DeclareNonGalleryFieldEdit.java:79-91` do create and attach `UserField` declarations.
- ✅ Package-level layer coverage exists in `docs/atlas/service-components/README.md:15-59,117-159` and `docs/atlas/ast-lsp-bindings/README.md:40-58`, so the method/field/editor pieces are present even though the first custom-type handoff is underspecified.
**Issues found**: The journey currently overstates a direct `OtherTypeDialog -> TypeManager` flow; the underlying capabilities exist, but the end-to-end code trace is stitched across separate helpers rather than one verified call chain.

## Journey 4: Instructor opens student project, reviews code, grades
**Verdict**: PASS
**Trace**:
- ✅ Journey sequence is documented in `docs/atlas/user-journeys/README.md:228-254`.
- ✅ Launch/load path matches Journey 1's verified desktop/project path: `docs/atlas/runtime-topology/README.md:16-45`, `docs/atlas/api-contracts/README.md:79-90`, `alice-ide/src/main/java/org/alice/stageide/EntryPoint.java:104-149`, `core/ide/src/main/java/org/alice/ide/uricontent/AbstractFileProjectLoader.java:71-81`, and `core/story-api-migration/src/main/java/org/lgna/project/io/IoUtilities.java:83-130`.
- ✅ Review/navigation surfaces match the atlas: `docs/atlas/api-contracts/README.md:19-46` and `docs/atlas/service-components/README.md:15-59` align with `core/ide/src/main/java/org/alice/ide/ProjectDocumentFrame.java:244-334`, `core/ide/src/main/java/org/alice/ide/declarationseditor/DeclarationsEditorComposite.java:55-82`, and `core/ide/src/main/java/org/alice/ide/declarationseditor/TypeMenu.java:91-135`.
- ✅ Printable review path is real and more precise than the journey shorthand: `core/ide/src/main/java/org/alice/ide/croquet/models/print/PrintAllOperation.java:58-63` writes HTML with `HtmlProjectWriter`, `core/ide/src/main/java/org/alice/ide/croquet/models/html/HtmlProjectWriter.java:78-87` emits project HTML, and `core/ide/src/main/java/org/alice/ide/croquet/models/print/PrintPdfOperation.java:67-118` converts that HTML to PDF and sends it to the native print flow.
- ✅ The grading boundary is intentionally external, matching `docs/atlas/user-journeys/README.md:8-10`; nothing in code claims an internal gradebook subsystem.
**Issues found**: None material on the traced path.

## Journey 5: Developer runs tests, measures coverage, pushes to CI
**Verdict**: FAIL
**Trace**:
- ✅ Journey sequence is documented in `docs/atlas/user-journeys/README.md:294-320`.
- ✅ The local coverage commands are real: `README.md:73-92` documents `mvn -DincludeSims=false -Dinstall4j.skip -Pcoverage verify` plus `scripts/summarize-jacoco-coverage.py`, matching the coverage half of the journey.
- ❌ The CI trigger step is overstated. `docs/atlas/user-journeys/README.md:315-320` says a developer can `push branch / PR` and get both workflows, but `.github/workflows/alice-test-ci.yml:3-7` and `.github/workflows/alice-coverage-ci.yml:3-7` only trigger on pushes to `develop` or PRs targeting `develop`.
- ❌ The CI execution path is not unconditional. `.github/workflows/alice-test-ci.yml:165-176` and `.github/workflows/alice-coverage-ci.yml:162-200` show a `maven-required=false` branch that skips Maven/coverage work and reports a no-op instead of always running tests, summaries, and artifact upload.
- ❌ The required cross-layer trace is incomplete because the checked layers are desktop-centric, not CI-centric: `docs/atlas/runtime-topology/README.md:1-4` describes the Alice desktop launch path, `docs/atlas/service-components/README.md:3-11` scopes package structure for `core/ide` and `core/story-api`, and `docs/atlas/ast-lsp-bindings/README.md:5-13` scopes Java module imports. Those layers do not model GitHub Actions lanes end-to-end.
**Issues found**: Journey 5 currently misstates CI trigger scope, omits the PR no-op branch, and lacks consistent representation in the runtime-topology/service-components/ast-lsp layers it is supposed to traverse.

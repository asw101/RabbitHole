# Alice Hotspots

This source-derived hotspot report is generated for the Alice code-atlas lane. Use it before refactoring, test planning, or ownership discussions.

Hotspots are risk indicators, not bugs. The atlas presents them as characterization priorities unless source, build, or test evidence proves a concrete defect.

## Usage

1. Find the file or module you plan to touch.
2. Check the risk class and characterization guidance.
3. Add characterization tests before refactor work in a hotspot.
4. Keep generated output out of source metrics.

## Metric method

The hotspot tables are generated from Java source under `core`, `alice-ide`, `netbeans`, `installer`, `external`, and `core-nonfree`. Generated files under build output directories are excluded.

## Largest Java files

| Rank | Lines | File | Risk class | Characterization guidance |
| ---: | ---: | --- | --- | --- |
| 1 | 5914 | `core/story-api-migration/src/main/java/org/lgna/project/migration/ProjectMigrationManager.java` | Migration behavior concentration | Use project migration fixtures before changing conversion logic |
| 2 | 1842 | `core/glrender/src/main/java/edu/cmu/cs/dennisc/render/joglrenderer/NonCachingTextRenderer.java` | Rendering compatibility | Add visual, GL-path, or output-shape checks before renderer changes |
| 3 | 1766 | `core/model-loading/src/main/java/org/lgna/story/resourceutilities/ModelResourceExporter.java` | Model import/export behavior | Use asset fixture imports or exports before refactoring |
| 4 | 1328 | `core/story-api/src/main/java/org/lgna/ik/core/enforcer/TightPositionalIkEnforcer.java` | IK/math behavior | Characterize positional IK outcomes before algorithm changes |
| 5 | 1259 | `core/ide/src/main/java/org/alice/stageide/sceneeditor/StorytellingSceneEditor.java` | IDE scene editing and lifecycle | Cover editor actions and project state transitions before UI refactors |
| 6 | 1241 | `core/scenegraph/src/main/java/edu/cmu/cs/dennisc/scenegraph/io/ASG.java` | Scenegraph serialization | Use import/export round-trip fixtures before serialization changes |
| 7 | 1225 | `core/glrender/src/main/java/edu/cmu/cs/dennisc/render/gl/imp/Graphics2D.java` | Rendering compatibility | Add visual, GL-path, or output-shape checks before renderer changes |
| 8 | 1202 | `core/model-loading/src/main/java/org/lgna/story/resourceutilities/JointedModelColladaExporter.java` | Model import/export behavior | Use asset fixture imports or exports before refactoring |
| 9 | 1193 | `core/ast/src/main/java/org/lgna/project/virtualmachine/VirtualMachine.java` | Runtime semantics | Add execution characterization tests before VM changes |
| 10 | 1113 | `core/croquet/src/main/java/org/lgna/croquet/AbstractComposite.java` | UI framework behavior | Verify Croquet lifecycle and composition behavior before extraction |
| 11 | 1055 | `core/story-api/src/main/java/Jama/Matrix.java` | Math dependency behavior | Avoid behavior changes unless replacing with a validated equivalent |
| 12 | 1039 | `core/story-api/src/main/java/org/alice/interact/DragAdapter.java` | Size hotspot | Add focused characterization around current behavior before extraction |
| 13 | 977 | `core/story-api/src/main/java/org/lgna/story/implementation/AbstractTransformableImp.java` | Size hotspot | Add focused characterization around current behavior before extraction |
| 14 | 959 | `core/ast/src/main/java/org/alice/serialization/tweedle/Encoder.java` | Tweedle serialization | Use encode/decode fixtures before modifying serialization |
| 15 | 956 | `core/story-api/src/main/java/Jama/EigenvalueDecomposition.java` | Math dependency behavior | Avoid behavior changes unless replacing with a validated equivalent |
| 16 | 955 | `core/story-api/src/main/java/org/lgna/story/implementation/JointedModelImp.java` | Size hotspot | Add focused characterization around current behavior before extraction |
| 17 | 938 | `core/tweedle/src/main/java/org/alice/tweedle/run/VirtualMachine.java` | Runtime semantics | Add execution characterization tests before VM changes |
| 18 | 916 | `core/story-api/src/main/java/org/lgna/story/implementation/alice/AliceResourceUtilities.java` | Size hotspot | Add focused characterization around current behavior before extraction |
| 19 | 790 | `core/story-api-migration/src/test/java/org/lgna/project/io/IoUtilitiesTest.java` | Size hotspot | Add focused characterization around current behavior before extraction |
| 20 | 789 | `core-nonfree/ide-nonfree/src/main/java/org/alice/stageide/personresource/IngredientsComposite.java` | Nonfree UI/resource behavior | Confirm resource UI flows before refactoring |
| 21 | 786 | `core/story-api/src/main/java/org/lgna/story/implementation/EntityImp.java` | Size hotspot | Add focused characterization around current behavior before extraction |
| 22 | 780 | `core/ide/src/main/java/org/alice/media/audio/FloatSampleBuffer.java` | Size hotspot | Add focused characterization around current behavior before extraction |
| 23 | 761 | `core/ide/src/main/java/org/alice/ide/ProjectApplication.java` | IDE scene editing and lifecycle | Cover editor actions and project state transitions before UI refactors |
| 24 | 748 | `core/ast/src/main/java/org/lgna/project/ast/SourceCodeGenerator.java` | Size hotspot | Add focused characterization around current behavior before extraction |
| 25 | 733 | `core/model-loading/src/main/java/org/lgna/story/resourceutilities/JointedModelGltfExporter.java` | Model import/export behavior | Use asset fixture imports or exports before refactoring |

## Largest Java modules

| Rank | Approx module Java LOC | Module | Risk class | Notes |
| ---: | ---: | --- | --- | --- |
| 1 | 166945 | `core/ide` | IDE behavior concentration | Highest module hotspot; prefer narrow characterization before UI changes |
| 2 | 59931 | `core/story-api` | Public story API/runtime | Preserve baseline compatibility |
| 3 | 46297 | `core/croquet` | UI framework | High fan-out framework risk |
| 4 | 41497 | `core-nonfree/story-api-nonfree` | Nonfree module | Default-active through includeSims unless disabled |
| 5 | 40982 | `core/util` | Shared utilities | Broad dependency surface |
| 6 | 24773 | `core/ast` | AST and serialization | Runtime and persistence semantics |
| 7 | 24094 | `core/models` | Model resources | Asset/resource compatibility risk |
| 8 | 19017 | `core/glrender` | Rendering | Platform and GL compatibility risk |
| 9 | 16338 | `core/scenegraph` | Scenegraph | Serialization/import-export risk |
| 10 | 10930 | `core/story-api-migration` | Migration | Project compatibility risk |
| 11 | 9451 | `core-nonfree/ide-nonfree` | Nonfree module | Default-active through includeSims unless disabled |
| 12 | 6951 | `core/model-loading` | Module size | Use module-local characterization before structural changes |
| 13 | 6819 | `core-nonfree/models-nonfree` | Nonfree module | Default-active through includeSims unless disabled |
| 14 | 5732 | `core/tweedle` | Language integration | Requires initialized grammar submodule |
| 15 | 5706 | `external/collada` | Module size | Use module-local characterization before structural changes |
| 16 | 5639 | `netbeans` | NetBeans plugin/template | Packaging compatibility risk |
| 17 | 2089 | `core/image-editor` | Module size | Use module-local characterization before structural changes |
| 18 | 1010 | `core/issue-reporting` | Module size | Use module-local characterization before structural changes |
| 19 | 795 | `alice-ide` | Module size | Use module-local characterization before structural changes |
| 20 | 193 | `external/wrapped-flow-layout` | Module size | Use module-local characterization before structural changes |

## Refactor-risk rules

| Hotspot type | Rule |
| --- | --- |
| Migration code | Add fixture-based before/after migration tests first |
| Serialization code | Add round-trip fixtures first |
| Rendering code | Add visual, GL-path, or output-shape checks first |
| Public story API code | Preserve source and behavior compatibility unless the behavior change is explicit and tested |
| IDE/UI framework code | Cover user-visible workflow and state transitions before extraction |
| Nonfree modules | Keep `includeSims` profile behavior visible in validation and docs |
| Tweedle code | Confirm `tweedle-lang/Grammar` before broad Maven validation |

## Pass/fail interpretation

Large files are not bugs by themselves. They are refactor-risk indicators. A hotspot becomes a bug-hunt finding only when source/build/test evidence shows stale behavior, unreachable behavior, packaging mismatch, or a compatibility hazard.

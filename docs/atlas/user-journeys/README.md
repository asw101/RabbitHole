# User Journeys

This layer traces five high-value paths through Alice's behavioral stack. The first four stay inside the IDE until they hit an explicit system boundary; the fifth follows the repository's documented local/CI validation path.

## Journey notes

- Journeys 1 and 2 stay inside the desktop authoring loop: load/edit/run and add/position/save.
- Journey 3 uses `TypeManager`, `OtherTypeDialog`, and declaration editors to show how custom types and methods become scene-usable AST declarations.
- Journey 4 stops at the review/export boundary because grading itself happens outside Alice; the codebase exposes review surfaces (`DeclarationsEditorComposite`, `PrintAllOperation`, `HtmlProjectWriter`) rather than an internal grading subsystem.
- Journey 5 is grounded in the repository's documented headless Maven and coverage workflows plus the two GitHub Actions lanes.

Derived from: `alice-ide/src/main/java/org/alice/stageide/EntryPoint.java`, `core/ide/src/main/java/org/alice/ide/IDE.java`, `core/ide/src/main/java/org/alice/ide/uricontent/AbstractFileProjectLoader.java`, `core/story-api-migration/src/main/java/org/lgna/project/io/IoUtilities.java`, `core/ide/src/main/java/org/alice/ide/declarationseditor/TypeMenu.java`, `core/ide/src/main/java/org/alice/ide/codeeditor/CodeEditor.java`, `core/ide/src/main/java/org/alice/stageide/run/RunComposite.java`, `core/ide/src/main/java/org/alice/stageide/program/ProgramContext.java`, `core/ide/src/main/java/org/alice/stageide/gallerybrowser/GalleryComposite.java`, `core/ide/src/main/java/org/alice/stageide/sceneeditor/SceneEditorDropReceptor.java`, `core/ide/src/main/java/org/alice/stageide/sceneeditor/interact/GlobalDragAdapter.java`, `core/ide/src/main/java/org/alice/ide/ProjectApplication.java`, `core/ide/src/main/java/org/alice/ide/ProjectFileUtilities.java`, `core/ide/src/main/java/org/alice/ide/typemanager/TypeManager.java`, `core/ide/src/main/java/org/alice/stageide/type/croquet/OtherTypeDialog.java`, `core/ide/src/main/java/org/alice/ide/ast/declaration/AddProcedureComposite.java`, `core/ide/src/main/java/org/alice/ide/ast/declaration/AddFunctionComposite.java`, `core/ide/src/main/java/org/alice/ide/ast/declaration/AddUnmanagedFieldComposite.java`, `core/ide/src/main/java/org/alice/ide/croquet/models/print/PrintAllOperation.java`, `core/ide/src/main/java/org/alice/ide/croquet/models/html/HtmlProjectWriter.java`, `README.md`, `.github/workflows/alice-test-ci.yml`, and `.github/workflows/alice-coverage-ci.yml`.

## 1. Student opens Alice, loads project, edits procedure, runs world

### Mermaid

```mermaid
sequenceDiagram
  actor Student
  participant EntryPoint as EntryPoint.main
  participant IDE as StageIDE
  participant Loader as AbstractFileProjectLoader
  participant IO as IoUtilities / XmlProjectIo
  participant Frame as ProjectDocumentFrame
  participant Editor as DeclarationsEditorComposite / CodeEditor
  participant AST as Project AST
  participant Run as RunComposite
  participant VM as RunProgramContext / ReleaseVirtualMachine
  participant World as SProgram / SceneImp
  participant Render as OnscreenRenderTarget

  Student->>EntryPoint: launch Alice
  EntryPoint->>IDE: initialize(args)
  Student->>IDE: open .a3p project
  IDE->>Loader: load project file
  Loader->>IO: projectReader / readProject
  IO-->>AST: decode XML into NamedUserType
  IDE->>Frame: set project + choose perspective
  Student->>Editor: select procedure tab
  Editor->>AST: mutate UserMethod body
  Student->>Run: click Run
  Run->>VM: initializeInContainer + setActiveScene()
  VM->>World: ENTRY_POINT_createInstance / invoke
  World->>Render: activate scene and render world
```

Source: [`journey-1-open-edit-run.mmd`](./journey-1-open-edit-run.mmd)

### Graphviz DOT

```dot
digraph journey_1_open_edit_run {
  rankdir=LR;
  graph [fontname="Helvetica", labelloc=t, label="Journey 1: open project, edit procedure, run world"];
  node [shape=box, style="rounded,filled", fillcolor="#F8F9FA", color="#34495E", fontname="Helvetica"];
  edge [color="#5D6D7E", arrowsize=0.8];

  Student [shape=oval, fillcolor="#FCF3CF"];
  EntryPoint [label="EntryPoint.main", fillcolor="#D6EAF8"];
  IDE [label="StageIDE", fillcolor="#D6EAF8"];
  Loader [label="AbstractFileProjectLoader", fillcolor="#D5F5E3"];
  IO [label="IoUtilities / XmlProjectIo", fillcolor="#D5F5E3"];
  Frame [label="ProjectDocumentFrame", fillcolor="#D6EAF8"];
  Editor [label="DeclarationsEditorComposite / CodeEditor", fillcolor="#FADBD8"];
  AST [label="Project AST", fillcolor="#FADBD8"];
  Run [label="RunComposite", fillcolor="#EBDEF0"];
  VM [label="RunProgramContext / ReleaseVirtualMachine", fillcolor="#EBDEF0"];
  World [label="SProgram / SceneImp", fillcolor="#D2B4DE"];
  Render [label="OnscreenRenderTarget", fillcolor="#D2B4DE"];

  Student -> EntryPoint [label="1 launch Alice"];
  EntryPoint -> IDE [label="2 initialize(args)"];
  Student -> IDE [label="3 open .a3p"];
  IDE -> Loader [label="4 load project"];
  Loader -> IO [label="5 projectReader/readProject"];
  IO -> AST [label="6 decode XML -> NamedUserType"];
  IDE -> Frame [label="7 set project + perspective"];
  Student -> Editor [label="8 select procedure"];
  Editor -> AST [label="9 mutate method body"];
  Student -> Run [label="10 click Run"];
  Run -> VM [label="11 init + setActiveScene()"];
  VM -> World [label="12 create/invoke program"];
  World -> Render [label="13 activate + render"];
}
```

Source: [`journey-1-open-edit-run.dot`](./journey-1-open-edit-run.dot)

## 2. Student adds entity to scene, positions it, saves

### Mermaid

```mermaid
sequenceDiagram
  actor Student
  participant Setup as SetupScenePerspective
  participant Gallery as GalleryComposite
  participant Drop as SceneEditorDropReceptor
  participant Drag as GlobalDragAdapter
  participant Scene as StorytellingSceneEditor
  participant AST as SceneType / UserField AST
  participant Xform as AbstractTransformable
  participant Save as ProjectApplication
  participant Files as ProjectFileUtilities
  participant IO as IoUtilities / XmlProjectIo

  Student->>Setup: switch to scene setup
  Student->>Gallery: drag gallery item
  Gallery->>Drop: GalleryDragModel
  Drop->>Drag: dragUpdated / SceneDropSite
  Drag->>Scene: drop target + selection
  Scene->>AST: add managed scene field / instance
  Student->>Scene: drag object to position it
  Scene->>Drag: manipulator input
  Drag->>Xform: setTransformation()
  Student->>Save: save project
  Save->>Files: saveCopyOfProjectTo
  Files->>IO: writeProject / writeType / writeResources
```

Source: [`journey-2-add-position-save.mmd`](./journey-2-add-position-save.mmd)

### Graphviz DOT

```dot
digraph journey_2_add_position_save {
  rankdir=LR;
  graph [fontname="Helvetica", labelloc=t, label="Journey 2: add entity, position it, save project"];
  node [shape=box, style="rounded,filled", fillcolor="#F8F9FA", color="#34495E", fontname="Helvetica"];
  edge [color="#5D6D7E", arrowsize=0.8];

  Student [shape=oval, fillcolor="#FCF3CF"];
  Setup [label="SetupScenePerspective", fillcolor="#D6EAF8"];
  Gallery [label="GalleryComposite", fillcolor="#D6EAF8"];
  Drop [label="SceneEditorDropReceptor", fillcolor="#D5F5E3"];
  Drag [label="GlobalDragAdapter", fillcolor="#D5F5E3"];
  Scene [label="StorytellingSceneEditor", fillcolor="#FADBD8"];
  AST [label="SceneType / UserField AST", fillcolor="#FADBD8"];
  Xform [label="AbstractTransformable", fillcolor="#EBDEF0"];
  Save [label="ProjectApplication", fillcolor="#D2B4DE"];
  Files [label="ProjectFileUtilities", fillcolor="#D2B4DE"];
  IO [label="IoUtilities / XmlProjectIo", fillcolor="#D2B4DE"];

  Student -> Setup [label="1 switch to scene setup"];
  Student -> Gallery [label="2 drag gallery item"];
  Gallery -> Drop [label="3 GalleryDragModel"];
  Drop -> Drag [label="4 SceneDropSite"];
  Drag -> Scene [label="5 target + selection"];
  Scene -> AST [label="6 add scene field / instance"];
  Student -> Scene [label="7 drag to position"];
  Scene -> Drag [label="8 manipulator input"];
  Drag -> Xform [label="9 setTransformation()"];
  Student -> Save [label="10 save project"];
  Save -> Files [label="11 saveCopyOfProjectTo"];
  Files -> IO [label="12 write XML/resources"];
}
```

Source: [`journey-2-add-position-save.dot`](./journey-2-add-position-save.dot)

## 3. Student creates custom class, adds methods, uses it in scene

### Mermaid

```mermaid
sequenceDiagram
  actor Student
  participant TypeMgr as TypeManager
  participant TypeMenu as TypeMenu / DeclarationsEditorComposite
  participant OtherType as OtherTypeDialog
  participant TypeAST as NamedUserType AST
  participant AddProc as AddProcedureComposite / AddFunctionComposite
  participant Code as CodeEditor
  participant AddField as AddUnmanagedFieldComposite
  participant Scene as SceneType AST

  Student->>OtherType: choose base type for custom class
  OtherType->>TypeMgr: resolve assignable type
  TypeMgr->>TypeAST: createTypeFor(...) if missing
  Student->>TypeMenu: open custom class tab
  TypeMenu->>TypeAST: select class declaration
  Student->>AddProc: add procedure / function
  AddProc->>TypeAST: declare UserMethod
  Student->>Code: edit method body
  Code->>TypeAST: mutate BlockStatement
  Student->>AddField: add custom-class field to scene
  AddField->>Scene: declare UserField / initializer
```

Source: [`journey-3-custom-class.mmd`](./journey-3-custom-class.mmd)

### Graphviz DOT

```dot
digraph journey_3_custom_class {
  rankdir=LR;
  graph [fontname="Helvetica", labelloc=t, label="Journey 3: create custom class, add methods, use it in scene"];
  node [shape=box, style="rounded,filled", fillcolor="#F8F9FA", color="#34495E", fontname="Helvetica"];
  edge [color="#5D6D7E", arrowsize=0.8];

  Student [shape=oval, fillcolor="#FCF3CF"];
  TypeMgr [label="TypeManager", fillcolor="#D6EAF8"];
  TypeMenu [label="TypeMenu / DeclarationsEditorComposite", fillcolor="#D6EAF8"];
  OtherType [label="OtherTypeDialog", fillcolor="#D5F5E3"];
  TypeAST [label="NamedUserType AST", fillcolor="#FADBD8"];
  AddProc [label="AddProcedureComposite / AddFunctionComposite", fillcolor="#FADBD8"];
  Code [label="CodeEditor", fillcolor="#FADBD8"];
  AddField [label="AddUnmanagedFieldComposite", fillcolor="#EBDEF0"];
  Scene [label="SceneType AST", fillcolor="#EBDEF0"];

  Student -> OtherType [label="1 choose base type"];
  OtherType -> TypeMgr [label="2 resolve assignable type"];
  TypeMgr -> TypeAST [label="3 createTypeFor(...) if missing"];
  Student -> TypeMenu [label="4 open class tab"];
  TypeMenu -> TypeAST [label="5 select declaration"];
  Student -> AddProc [label="6 add procedure/function"];
  AddProc -> TypeAST [label="7 declare UserMethod"];
  Student -> Code [label="8 edit method body"];
  Code -> TypeAST [label="9 mutate BlockStatement"];
  Student -> AddField [label="10 use class in scene"];
  AddField -> Scene [label="11 declare UserField + initializer"];
}
```

Source: [`journey-3-custom-class.dot`](./journey-3-custom-class.dot)

## 4. Instructor opens student project, reviews code, grades

### Mermaid

```mermaid
sequenceDiagram
  actor Instructor
  participant EntryPoint as EntryPoint.main
  participant IDE as StageIDE
  participant Loader as AbstractFileProjectLoader
  participant IO as IoUtilities / XmlProjectIo
  participant Frame as ProjectDocumentFrame
  participant Review as DeclarationsEditorComposite / TypeMenu
  participant Print as PrintAllOperation
  participant Html as HtmlProjectWriter
  participant Gradebook as External gradebook / rubric

  Instructor->>EntryPoint: launch Alice
  EntryPoint->>IDE: initialize desktop shell
  Instructor->>IDE: open student project
  IDE->>Loader: load .a3p
  Loader->>IO: readProject
  IO-->>Frame: project AST loaded
  Instructor->>Review: inspect classes, methods, and scene code
  Instructor->>Print: export printable review
  Print->>Html: writeProject(...) as HTML/PDF source
  Instructor->>Gradebook: record assessment outside Alice
```

Source: [`journey-4-instructor-review-grade.mmd`](./journey-4-instructor-review-grade.mmd)

### Graphviz DOT

```dot
digraph journey_4_instructor_review_grade {
  rankdir=LR;
  graph [fontname="Helvetica", labelloc=t, label="Journey 4: instructor opens project, reviews code, grades"];
  node [shape=box, style="rounded,filled", fillcolor="#F8F9FA", color="#34495E", fontname="Helvetica"];
  edge [color="#5D6D7E", arrowsize=0.8];

  Instructor [shape=oval, fillcolor="#FCF3CF"];
  EntryPoint [label="EntryPoint.main", fillcolor="#D6EAF8"];
  IDE [label="StageIDE", fillcolor="#D6EAF8"];
  Loader [label="AbstractFileProjectLoader", fillcolor="#D5F5E3"];
  IO [label="IoUtilities / XmlProjectIo", fillcolor="#D5F5E3"];
  Frame [label="ProjectDocumentFrame", fillcolor="#FADBD8"];
  Review [label="DeclarationsEditorComposite / TypeMenu", fillcolor="#FADBD8"];
  Print [label="PrintAllOperation", fillcolor="#EBDEF0"];
  Html [label="HtmlProjectWriter", fillcolor="#EBDEF0"];
  Gradebook [label="External gradebook / rubric", fillcolor="#D2B4DE"];

  Instructor -> EntryPoint [label="1 launch Alice"];
  EntryPoint -> IDE [label="2 initialize shell"];
  Instructor -> IDE [label="3 open student project"];
  IDE -> Loader [label="4 load .a3p"];
  Loader -> IO [label="5 readProject"];
  IO -> Frame [label="6 project AST loaded"];
  Instructor -> Review [label="7 inspect code"];
  Instructor -> Print [label="8 export printable review"];
  Print -> Html [label="9 writeProject(...) HTML"];
  Instructor -> Gradebook [label="10 record grade outside Alice"];
}
```

Source: [`journey-4-instructor-review-grade.dot`](./journey-4-instructor-review-grade.dot)

## 5. Developer runs tests, measures coverage, pushes to CI

### Mermaid

```mermaid
sequenceDiagram
  actor Developer
  participant Maven as local mvn
  participant Surefire as surefire tests
  participant JaCoCo as Pcoverage verify
  participant Summary as summarize-jacoco-coverage.py
  participant TestCI as alice-test-ci.yml
  participant CoverageCI as alice-coverage-ci.yml
  participant Artifacts as uploaded evidence artifact

  Developer->>Maven: mvn -DincludeSims=false ... clean test
  Maven->>Surefire: run headless no-Sims tests
  Surefire-->>Developer: test results
  Developer->>JaCoCo: mvn -DincludeSims=false ... -Pcoverage verify
  JaCoCo->>Summary: generate aggregate + module reports
  Summary-->>Developer: coverage-summary.md
  Developer->>TestCI: push branch / PR
  TestCI->>Maven: git submodule update, setup-java, clean test
  Developer->>CoverageCI: same push triggers coverage workflow
  CoverageCI->>JaCoCo: verify + coverage reports
  CoverageCI->>Summary: summarize and gate coverage
  CoverageCI->>Artifacts: upload coverage evidence
```

Source: [`journey-5-dev-test-coverage-ci.mmd`](./journey-5-dev-test-coverage-ci.mmd)

### Graphviz DOT

```dot
digraph journey_5_dev_test_coverage_ci {
  rankdir=LR;
  graph [fontname="Helvetica", labelloc=t, label="Journey 5: developer runs tests, measures coverage, pushes to CI"];
  node [shape=box, style="rounded,filled", fillcolor="#F8F9FA", color="#34495E", fontname="Helvetica"];
  edge [color="#5D6D7E", arrowsize=0.8];

  Developer [shape=oval, fillcolor="#FCF3CF"];
  Maven [label="local mvn", fillcolor="#D6EAF8"];
  Surefire [label="surefire tests", fillcolor="#D6EAF8"];
  JaCoCo [label="-Pcoverage verify", fillcolor="#D5F5E3"];
  Summary [label="summarize-jacoco-coverage.py", fillcolor="#D5F5E3"];
  TestCI [label="alice-test-ci.yml", fillcolor="#FADBD8"];
  CoverageCI [label="alice-coverage-ci.yml", fillcolor="#FADBD8"];
  Artifacts [label="uploaded evidence artifact", fillcolor="#EBDEF0"];

  Developer -> Maven [label="1 clean test"];
  Maven -> Surefire [label="2 run headless no-Sims tests"];
  Surefire -> Developer [label="3 results"];
  Developer -> JaCoCo [label="4 coverage verify"];
  JaCoCo -> Summary [label="5 aggregate + module reports"];
  Summary -> Developer [label="6 coverage summary"];
  Developer -> TestCI [label="7 push branch / PR"];
  TestCI -> Maven [label="8 submodule + setup-java + clean test"];
  Developer -> CoverageCI [label="9 same push triggers coverage"];
  CoverageCI -> JaCoCo [label="10 verify + coverage reports"];
  CoverageCI -> Summary [label="11 summarize and gate"];
  CoverageCI -> Artifacts [label="12 upload evidence"];
}
```

Source: [`journey-5-dev-test-coverage-ci.dot`](./journey-5-dev-test-coverage-ci.dot)

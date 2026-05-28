# Service Components

This layer splits the two largest behavioral modules into readable sub-diagrams instead of forcing one dense graph.

## Split rationale

- `core/ide` spans both `org.alice.ide.*` platform code and `org.alice.stageide.*` story-authoring code, so it is split into **ide-platform** and **stageide-authoring**.
- `core/story-api` mixes the user-facing facade hierarchy with runtime/event/interaction internals, so it is split into **story-api-facade** and **story-api-runtime**.
- Each sub-diagram stays within a readable density envelope while preserving the dominant package couplings seen in the codebase.

Derived from package sweeps and representative classes in: `core/ide/src/main/java/org/alice/ide/**`, `core/ide/src/main/java/org/alice/stageide/**`, `core/story-api/src/main/java/org/lgna/story/**`, and `core/story-api/src/main/java/org/alice/interact/**`.

## core/ide — ide platform

The platform side owns project lifecycle, menu/croquet wiring, declaration tabs, AST actions, and save/load plumbing.

### Mermaid

```mermaid
graph TD
  app["ProjectApplication<br/>project lifecycle + save/export"]
  frame["ProjectDocumentFrame<br/>menus + perspectives + active document"]
  croquet["org.alice.ide.croquet.*<br/>menu/operation framework"]
  perspectives["org.alice.ide.perspectives.*<br/>shared perspective shell"]
  projecturi["projecturi.* + uricontent.*<br/>URI loaders and save targets"]
  decls["declarationseditor.*<br/>type tabs + declaration navigation"]
  codeeditor["codeeditor.*<br/>statement/body editor surfaces"]
  astpkg["ast.*<br/>rename/import/export and AST actions"]
  member["member.* + members.*<br/>member menus and collections"]
  props["properties.*<br/>property controllers and adapters"]
  inst["instancefactory.*<br/>selected-instance model"]
  resource["resource.*<br/>resource management"]
  clipboard["clipboard.*<br/>copy/paste edits"]
  recent["recentprojects.*<br/>recent/open history"]
  issue["issue.*<br/>issue reporting + attachments"]

  app --> frame
  app --> projecturi
  app --> recent
  app --> issue
  frame --> croquet
  frame --> perspectives
  frame --> decls
  frame --> inst
  croquet --> astpkg
  croquet --> projecturi
  croquet --> resource
  perspectives --> decls
  perspectives --> codeeditor
  decls --> codeeditor
  decls --> member
  decls --> astpkg
  member --> astpkg
  props --> inst
  props --> astpkg
  clipboard --> codeeditor
  clipboard --> astpkg
  resource --> astpkg
```

Source: [`ide-platform.mmd`](./ide-platform.mmd)

### Graphviz DOT

```dot
digraph ide_platform {
  rankdir=LR;
  graph [fontname="Helvetica", labelloc=t, label="core/ide: org.alice.ide.* platform components"];
  node [shape=box, style="rounded,filled", fillcolor="#F8F9FA", color="#34495E", fontname="Helvetica"];
  edge [color="#5D6D7E", arrowsize=0.8];

  app [label="ProjectApplication\nproject lifecycle + save/export", fillcolor="#D6EAF8"];
  frame [label="ProjectDocumentFrame\nmenus + perspectives + active document", fillcolor="#D6EAF8"];
  croquet [label="org.alice.ide.croquet.*\nmenu/operation framework", fillcolor="#D6EAF8"];
  perspectives [label="org.alice.ide.perspectives.*\nshared perspective shell", fillcolor="#D6EAF8"];
  projecturi [label="projecturi.* + uricontent.*\nURI loaders and save targets", fillcolor="#D5F5E3"];
  decls [label="declarationseditor.*\ntype tabs + declaration navigation", fillcolor="#FADBD8"];
  codeeditor [label="codeeditor.*\nstatement/body editor surfaces", fillcolor="#FADBD8"];
  astpkg [label="ast.*\nrename/import/export and AST actions", fillcolor="#FCF3CF"];
  member [label="member.* + members.*\nmember menus and collections", fillcolor="#FCF3CF"];
  props [label="properties.*\nproperty controllers and adapters", fillcolor="#EBDEF0"];
  inst [label="instancefactory.*\nselected-instance model", fillcolor="#EBDEF0"];
  resource [label="resource.*\nresource management", fillcolor="#D2B4DE"];
  clipboard [label="clipboard.*\ncopy/paste edits", fillcolor="#D2B4DE"];
  recent [label="recentprojects.*\nrecent/open history", fillcolor="#F9E79F"];
  issue [label="issue.*\nissue reporting + attachments", fillcolor="#F9E79F"];

  app -> frame;
  app -> projecturi;
  app -> recent;
  app -> issue;
  frame -> croquet;
  frame -> perspectives;
  frame -> decls;
  frame -> inst;
  croquet -> astpkg;
  croquet -> projecturi;
  croquet -> resource;
  perspectives -> decls;
  perspectives -> codeeditor;
  decls -> codeeditor;
  decls -> member;
  decls -> astpkg;
  member -> astpkg;
  props -> inst;
  props -> astpkg;
  clipboard -> codeeditor;
  clipboard -> astpkg;
  resource -> astpkg;
}
```

Source: [`ide-platform.dot`](./ide-platform.dot)

## core/ide — stageide authoring

The story-authoring side layers scene editing, gallery drag/drop, story-specific AST bootstrap code, run-window execution, and stage-specific croquet flows on top of the generic IDE shell.

### Mermaid

```mermaid
graph TD
  stage["StageIDE<br/>story-specific IDE shell"]
  persp["perspectives.*<br/>Code + SetupScene perspectives"]
  scene["sceneeditor.*<br/>looking glass + selection + lifecycle"]
  gallery["gallerybrowser.*<br/>gallery tabs + drag models"]
  modelres["modelresource.*<br/>resource keys + type mapping"]
  run["run.* + program.*<br/>RunComposite + RunProgramContext"]
  ast["ast.*<br/>bootstrap scene/program methods"]
  apis["apis.*<br/>story/event adapters for VM"]
  cascade["cascade.*<br/>story expression cascades"]
  croquet["croquet.*<br/>stageide dialogs and operations"]
  member["member.*<br/>story-specific member UI"]
  props["properties.*<br/>property adapters + controllers"]
  typepkg["type.*<br/>other-type dialog + type filters"]
  inst["instancefactory.*<br/>joint and scene instance factories"]
  oneshot["oneshot.*<br/>one-shot animation menus"]

  stage --> persp
  stage --> scene
  stage --> run
  stage --> ast
  stage --> apis
  persp --> scene
  persp --> gallery
  persp --> run
  gallery --> modelres
  gallery --> ast
  scene --> inst
  scene --> props
  scene --> ast
  scene --> apis
  croquet --> cascade
  croquet --> oneshot
  member --> ast
  typepkg --> modelres
  typepkg --> ast
  inst --> modelres
```

Source: [`stageide-authoring.mmd`](./stageide-authoring.mmd)

### Graphviz DOT

```dot
digraph stageide_authoring {
  rankdir=LR;
  graph [fontname="Helvetica", labelloc=t, label="core/ide: org.alice.stageide.* authoring components"];
  node [shape=box, style="rounded,filled", fillcolor="#F8F9FA", color="#34495E", fontname="Helvetica"];
  edge [color="#5D6D7E", arrowsize=0.8];

  stage [label="StageIDE\nstory-specific IDE shell", fillcolor="#D6EAF8"];
  persp [label="perspectives.*\nCode + SetupScene perspectives", fillcolor="#D6EAF8"];
  scene [label="sceneeditor.*\nlooking glass + selection + lifecycle", fillcolor="#D5F5E3"];
  gallery [label="gallerybrowser.*\ngallery tabs + drag models", fillcolor="#D5F5E3"];
  modelres [label="modelresource.*\nresource keys + type mapping", fillcolor="#D5F5E3"];
  run [label="run.* + program.*\nRunComposite + RunProgramContext", fillcolor="#FADBD8"];
  ast [label="ast.*\nbootstrap scene/program methods", fillcolor="#FCF3CF"];
  apis [label="apis.*\nstory/event adapters for VM", fillcolor="#FCF3CF"];
  cascade [label="cascade.*\nstory expression cascades", fillcolor="#EBDEF0"];
  croquet [label="croquet.*\nstageide dialogs and operations", fillcolor="#EBDEF0"];
  member [label="member.*\nstory-specific member UI", fillcolor="#EBDEF0"];
  props [label="properties.*\nproperty adapters + controllers", fillcolor="#D2B4DE"];
  typepkg [label="type.*\nother-type dialog + type filters", fillcolor="#D2B4DE"];
  inst [label="instancefactory.*\njoint and scene instance factories", fillcolor="#F9E79F"];
  oneshot [label="oneshot.*\none-shot animation menus", fillcolor="#F9E79F"];

  stage -> persp;
  stage -> scene;
  stage -> run;
  stage -> ast;
  stage -> apis;
  persp -> scene;
  persp -> gallery;
  persp -> run;
  gallery -> modelres;
  gallery -> ast;
  scene -> inst;
  scene -> props;
  scene -> ast;
  scene -> apis;
  croquet -> cascade;
  croquet -> oneshot;
  member -> ast;
  typepkg -> modelres;
  typepkg -> ast;
  inst -> modelres;
}
```

Source: [`stageide-authoring.dot`](./stageide-authoring.dot)

## core/story-api — facade hierarchy

This view stays on the public `org.lgna.story.*` hierarchy that student-authored code targets.

### Mermaid

```mermaid
graph TD
  sprog["SProgram<br/>active scene + simulation speed"]
  sscene["SScene<br/>world root + listener hooks"]
  sthing["SThing<br/>base entity facade"]
  sturn["STurnable<br/>turn / orient"]
  smove["SMovableTurnable<br/>move / place / moveTo"]
  smodel["SModel<br/>say / paint / opacity"]
  sjointed["SJointedModel<br/>joint access + poses"]
  sbiped["SBiped"]
  squad["SQuadruped"]
  sfly["SFlyer"]
  sswim["SSwimmer"]
  eventpkg["event.*<br/>listener interfaces + event types"]
  res["resources.*<br/>model resource catalogs"]

  sprog --> sscene
  sthing --> sturn
  sturn --> smove
  smove --> smodel
  smodel --> sjointed
  sjointed --> sbiped
  sjointed --> squad
  sjointed --> sfly
  sjointed --> sswim
  sscene --> eventpkg
  sbiped --> res
  squad --> res
  sfly --> res
  sswim --> res
```

Source: [`story-api-facade.mmd`](./story-api-facade.mmd)

### Graphviz DOT

```dot
digraph story_api_facade {
  rankdir=LR;
  graph [fontname="Helvetica", labelloc=t, label="core/story-api: org.lgna.story.* facade hierarchy"];
  node [shape=box, style="rounded,filled", fillcolor="#F8F9FA", color="#34495E", fontname="Helvetica"];
  edge [color="#5D6D7E", arrowsize=0.8];

  sprog [label="SProgram\nactive scene + simulation speed", fillcolor="#D6EAF8"];
  sscene [label="SScene\nworld root + listener hooks", fillcolor="#D6EAF8"];
  sthing [label="SThing\nbase entity facade", fillcolor="#D5F5E3"];
  sturn [label="STurnable\nturn / orient", fillcolor="#D5F5E3"];
  smove [label="SMovableTurnable\nmove / place / moveTo", fillcolor="#D5F5E3"];
  smodel [label="SModel\nsay / paint / opacity", fillcolor="#FADBD8"];
  sjointed [label="SJointedModel\njoint access + poses", fillcolor="#FADBD8"];
  sbiped [label="SBiped", fillcolor="#FCF3CF"];
  squad [label="SQuadruped", fillcolor="#FCF3CF"];
  sfly [label="SFlyer", fillcolor="#FCF3CF"];
  sswim [label="SSwimmer", fillcolor="#FCF3CF"];
  eventpkg [label="event.*\nlistener interfaces + event types", fillcolor="#EBDEF0"];
  res [label="resources.*\nmodel resource catalogs", fillcolor="#D2B4DE"];

  sprog -> sscene;
  sthing -> sturn;
  sturn -> smove;
  smove -> smodel;
  smodel -> sjointed;
  sjointed -> sbiped;
  sjointed -> squad;
  sjointed -> sfly;
  sjointed -> sswim;
  sscene -> eventpkg;
  sbiped -> res;
  squad -> res;
  sfly -> res;
  sswim -> res;
}
```

Source: [`story-api-facade.dot`](./story-api-facade.dot)

## core/story-api — runtime, events, and interaction

This view follows the runtime bridge from the facade into implementations, event dispatch, and the interaction/manipulator stack that lives in the same module.

### Mermaid

```mermaid
graph TD
  facade["org.lgna.story.*<br/>facade methods"]
  impl["implementation.*<br/>ProgramImp + EntityImp + SceneImp"]
  sceneimp["SceneImp<br/>scene activation + listener wiring"]
  entityimp["EntityImp / AbstractTransformableImp<br/>scenegraph bridge"]
  eventmgr["implementation.eventhandling.EventManager"]
  events["event.*<br/>activation/collision/view/timer contracts"]
  resources["resources.*<br/>resource enumerations"]
  resutil["resourceutilities.*<br/>Collada/JSON/export helpers"]
  interact["org.alice.interact.*<br/>DragAdapter + input state"]
  manipulators["interact.manipulator.*<br/>translate/rotate/resize logic"]
  handles["interact.handle.*<br/>3D handles + pick hints"]
  scenegraph["scenegraph.*<br/>Composite + AbstractTransformable"]

  facade --> impl
  impl --> sceneimp
  impl --> entityimp
  sceneimp --> eventmgr
  eventmgr --> events
  sceneimp --> interact
  interact --> manipulators
  manipulators --> handles
  entityimp --> scenegraph
  impl --> resources
  impl --> resutil
  interact --> scenegraph
```

Source: [`story-api-runtime.mmd`](./story-api-runtime.mmd)

### Graphviz DOT

```dot
digraph story_api_runtime {
  rankdir=LR;
  graph [fontname="Helvetica", labelloc=t, label="core/story-api runtime, events, and interaction"];
  node [shape=box, style="rounded,filled", fillcolor="#F8F9FA", color="#34495E", fontname="Helvetica"];
  edge [color="#5D6D7E", arrowsize=0.8];

  facade [label="org.lgna.story.*\nfacade methods", fillcolor="#D6EAF8"];
  impl [label="implementation.*\nProgramImp + EntityImp + SceneImp", fillcolor="#D5F5E3"];
  sceneimp [label="SceneImp\nscene activation + listener wiring", fillcolor="#D5F5E3"];
  entityimp [label="EntityImp / AbstractTransformableImp\nscenegraph bridge", fillcolor="#D5F5E3"];
  eventmgr [label="implementation.eventhandling.EventManager", fillcolor="#FADBD8"];
  events [label="event.*\nactivation/collision/view/timer contracts", fillcolor="#FADBD8"];
  resources [label="resources.*\nresource enumerations", fillcolor="#FCF3CF"];
  resutil [label="resourceutilities.*\nCollada/JSON/export helpers", fillcolor="#FCF3CF"];
  interact [label="org.alice.interact.*\nDragAdapter + input state", fillcolor="#EBDEF0"];
  manipulators [label="interact.manipulator.*\ntranslate/rotate/resize logic", fillcolor="#EBDEF0"];
  handles [label="interact.handle.*\n3D handles + pick hints", fillcolor="#EBDEF0"];
  scenegraph [label="scenegraph.*\nComposite + AbstractTransformable", fillcolor="#D2B4DE"];

  facade -> impl;
  impl -> sceneimp;
  impl -> entityimp;
  sceneimp -> eventmgr;
  eventmgr -> events;
  sceneimp -> interact;
  interact -> manipulators;
  manipulators -> handles;
  entityimp -> scenegraph;
  impl -> resources;
  impl -> resutil;
  interact -> scenegraph;
}
```

Source: [`story-api-runtime.dot`](./story-api-runtime.dot)

# Compile-time Dependencies

This layer maps Maven POM dependencies between the main Alice modules and the key third-party technologies they pull in.
The graph follows declared dependencies first, with one extra structural note for `Jama`, which is vendored inside `core/story-api` rather than declared as a Maven artifact.

## Dependency notes

- `util` is the base library for JavaFX-enabled desktop support.
- `story-api` is the main convergence point: it depends on `ast`, `tweedle`, `scenegraph`, `glrender`, and `util`, while also carrying the bundled `Jama` math package.
- `glrender` is the JOGL/GlueGen bridge; `alice-ide` and `netbeans` reach JavaFX through launcher/plugin wiring rather than only through direct Java code imports.
- `story-api-migration` is shown as its own direct edge because both `ide` and `netbeans` declare it explicitly for project I/O, while the remaining support modules stay grouped as `i18n`, `image-editor`, `issue-reporting`, `resources`, and `models`.

## Mermaid

```mermaid
flowchart TD
  util["util<br/>JavaFX 21.0.7"]
  tweedle["tweedle<br/>ANTLR4 + Jackson"]
  ast["ast<br/>commons-text"]
  scenegraph["scenegraph"]
  glrender["glrender<br/>JOGL 2.5.0 + GlueGen 2.5.0"]
  storyapi["story-api<br/>jsvg + FlatLaf<br/>vendored Jama.*"]
  croquet["croquet<br/>wrapped-flow-layout + FlatLaf"]
  support["support modules<br/>i18n + image-editor + issue-reporting<br/>resources + models"]
  migration["story-api-migration<br/>project I/O bridge"]
  ide["ide"]
  modelloading["model-loading<br/>Collada + glTF + JAXB"]
  aliceide["alice-ide<br/>EntryPoint launcher"]
  netbeans["netbeans<br/>NetBeans platform plugin"]

  tweedle --> util
  ast --> util
  ast --> tweedle
  scenegraph --> util
  glrender --> scenegraph
  storyapi --> util
  storyapi --> scenegraph
  storyapi --> glrender
  storyapi --> tweedle
  storyapi --> ast
  croquet --> util
  ide --> support
  ide --> migration
  ide --> util
  ide --> ast
  ide --> croquet
  ide --> scenegraph
  ide --> glrender
  ide --> storyapi
  modelloading --> util
  modelloading --> ast
  modelloading --> scenegraph
  modelloading --> glrender
  modelloading --> storyapi
  aliceide --> ide
  netbeans --> support
  netbeans --> migration
  netbeans --> util
  netbeans --> ast
  netbeans --> scenegraph
  netbeans --> glrender
  netbeans --> storyapi
  netbeans --> tweedle

  glrender --> jogl["JOGL 2.5.0"]
  glrender --> gluegen["GlueGen 2.5.0"]
  util --> javafx["JavaFX 21.0.7"]
  netbeans --> javafx
  aliceide -.exec plugin module-path.-> javafx
  storyapi -.bundled source package.-> jama["Jama<br/>core/story-api/src/main/java/Jama"]
```

Source: [`compile-deps.mmd`](./compile-deps.mmd)

## Graphviz DOT

```dot
digraph compile_deps {
  rankdir=LR;
  graph [fontname="Helvetica", labelloc=t, label="Compile-time dependencies from Maven POMs"];
  node [shape=box, style="rounded,filled", fillcolor="#F8F9FA", color="#34495E", fontname="Helvetica"];
  edge [color="#5D6D7E", arrowsize=0.8];

  util [label="util
JavaFX 21.0.7", fillcolor="#D6EAF8"];
  tweedle [label="tweedle
ANTLR4 + Jackson", fillcolor="#FCF3CF"];
  ast [label="ast
commons-text", fillcolor="#D6EAF8"];
  scenegraph [label="scenegraph", fillcolor="#D6EAF8"];
  glrender [label="glrender
JOGL 2.5.0 + GlueGen 2.5.0", fillcolor="#D5F5E3"];
  storyapi [label="story-api
jsvg + FlatLaf
vendored Jama.*", fillcolor="#D5F5E3"];
  croquet [label="croquet
wrapped-flow-layout + FlatLaf"];
  support [label="support modules
i18n + image-editor + issue-reporting
resources + models", fillcolor="#EBF5FB"];
  migration [label="story-api-migration
project I/O bridge", fillcolor="#EBF5FB"];
  ide [label="ide", fillcolor="#FADBD8"];
  modelloading [label="model-loading
Collada + glTF + JAXB", fillcolor="#FADBD8"];
  aliceide [label="alice-ide
EntryPoint launcher", fillcolor="#F9E79F"];
  netbeans [label="netbeans
NetBeans platform plugin", fillcolor="#F9E79F"];

  jogl [label="JOGL 2.5.0", fillcolor="#FEF5E7"];
  gluegen [label="GlueGen 2.5.0", fillcolor="#FEF5E7"];
  javafx [label="JavaFX 21.0.7", fillcolor="#FEF5E7"];
  jama [label="Jama
core/story-api/src/main/java/Jama", fillcolor="#FEF5E7"];

  tweedle -> util;
  ast -> util;
  ast -> tweedle;
  scenegraph -> util;
  glrender -> scenegraph;
  storyapi -> util;
  storyapi -> scenegraph;
  storyapi -> glrender;
  storyapi -> tweedle;
  storyapi -> ast;
  croquet -> util;
  ide -> support;
  ide -> migration;
  ide -> util;
  ide -> ast;
  ide -> croquet;
  ide -> scenegraph;
  ide -> glrender;
  ide -> storyapi;
  modelloading -> util;
  modelloading -> ast;
  modelloading -> scenegraph;
  modelloading -> glrender;
  modelloading -> storyapi;
  aliceide -> ide;
  netbeans -> support;
  netbeans -> migration;
  netbeans -> util;
  netbeans -> ast;
  netbeans -> scenegraph;
  netbeans -> glrender;
  netbeans -> storyapi;
  netbeans -> tweedle;

  glrender -> jogl;
  glrender -> gluegen;
  util -> javafx;
  netbeans -> javafx;
  aliceide -> javafx [style=dashed, label="exec plugin module-path"];
  storyapi -> jama [style=dashed, label="bundled source package"];
}
```

Source: [`compile-deps.dot`](./compile-deps.dot)

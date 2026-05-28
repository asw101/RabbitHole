# Repository Surface

This layer maps the parent Maven structure and the operational top-level directories around it.
The diagram is derived from the root `pom.xml`, the first `find . -name "pom.xml"` scan, and the `core/*/` directory sweep requested in the task.

## Structural notes

- `pom.xml` is the parent aggregator for `core/`, `external/`, `alice-ide/`, `netbeans/`, `installer/`, and preserved `core-nonfree/*` modules.
- `core/` splits into foundations (`util`, `croquet`, `tweedle`, `ast`) and runtime/UI modules (`scenegraph`, `glrender`, `story-api`, `ide`, `model-loading`).
- `drinkme/`, `qa/`, `scripts/`, and `.github/` are outside the production module graph but materially affect investigation, validation, and automation.

## Mermaid

```mermaid
flowchart TD
  root["alice/"] --> rootpom["pom.xml parent"]
  root --> docs["docs/"]
  root --> gh[".github/"]
  root --> scripts["scripts/"]
  root --> qa["qa/"]
  root --> drinkme["drinkme/"]

  rootpom --> aliceide["alice-ide/<br/>JavaFX + Swing desktop launcher"]
  rootpom --> netbeans["netbeans/<br/>NetBeans module"]
  rootpom --> installer["installer/"]
  rootpom --> corenonfree["core-nonfree/*<br/>preserved nonfree mirrors"]
  rootpom --> corepom["core/pom.xml aggregator"]
  rootpom --> externalpom["external/pom.xml aggregator"]

  subgraph coremods["core/ modules"]
    corepom --> util["util"]
    corepom --> croquet["croquet"]
    corepom --> tweedle["tweedle"]
    corepom --> ast["ast"]
    corepom --> scenegraph["scenegraph"]
    corepom --> glrender["glrender"]
    corepom --> storyapi["story-api"]
    corepom --> ide["ide"]
    corepom --> modelloading["model-loading"]
    corepom --> imageeditor["image-editor"]
    corepom --> issuereporting["issue-reporting"]
    corepom --> aux["i18n + resources + models<br/>story-api-migration + core/core"]
  end

  subgraph externalmods["external/ modules"]
    externalpom --> collada["collada"]
    externalpom --> colladaschema["collada-schema-1-4-1"]
    externalpom --> wrapped["wrapped-flow-layout"]
  end

  aliceide --> ide
  netbeans --> storyapi
  netbeans --> glrender
  netbeans --> ast
  modelloading --> collada
  modelloading --> colladaschema
```

Source: [`repo-surface.mmd`](./repo-surface.mmd)

## Graphviz DOT

```dot
digraph repo_surface {
  rankdir=LR;
  graph [fontname="Helvetica", labelloc=t, label="Alice repository surface"];
  node [shape=box, style="rounded,filled", fillcolor="#F8F9FA", color="#34495E", fontname="Helvetica"];
  edge [color="#5D6D7E", arrowsize=0.8];

  root [label="alice/"];
  rootpom [label="pom.xml parent", fillcolor="#D6EAF8"];
  docs [label="docs/", fillcolor="#FCF3CF"];
  gh [label=".github/", fillcolor="#FCF3CF"];
  scripts [label="scripts/", fillcolor="#FCF3CF"];
  qa [label="qa/", fillcolor="#FCF3CF"];
  drinkme [label="drinkme/", fillcolor="#FCF3CF"];
  aliceide [label="alice-ide/
JavaFX + Swing launcher", fillcolor="#D5F5E3"];
  netbeans [label="netbeans/
NetBeans module", fillcolor="#D5F5E3"];
  installer [label="installer/", fillcolor="#FADBD8"];
  corenonfree [label="core-nonfree/*
preserved nonfree mirrors", fillcolor="#FADBD8"];

  root -> rootpom;
  root -> docs;
  root -> gh;
  root -> scripts;
  root -> qa;
  root -> drinkme;
  rootpom -> aliceide;
  rootpom -> netbeans;
  rootpom -> installer;
  rootpom -> corenonfree;

  subgraph cluster_core {
    label="core/ modules";
    color="#85C1E9";
    style="rounded";
    corepom [label="core/pom.xml aggregator", fillcolor="#D6EAF8"];
    util [label="util"];
    croquet [label="croquet"];
    tweedle [label="tweedle"];
    ast [label="ast"];
    scenegraph [label="scenegraph"];
    glrender [label="glrender"];
    storyapi [label="story-api"];
    ide [label="ide"];
    modelloading [label="model-loading"];
    imageeditor [label="image-editor"];
    issuereporting [label="issue-reporting"];
    aux [label="i18n + resources + models
story-api-migration + core/core", fillcolor="#EBF5FB"];

    corepom -> util;
    corepom -> croquet;
    corepom -> tweedle;
    corepom -> ast;
    corepom -> scenegraph;
    corepom -> glrender;
    corepom -> storyapi;
    corepom -> ide;
    corepom -> modelloading;
    corepom -> imageeditor;
    corepom -> issuereporting;
    corepom -> aux;
  }

  subgraph cluster_external {
    label="external/ modules";
    color="#A9DFBF";
    style="rounded";
    externalpom [label="external/pom.xml aggregator", fillcolor="#D5F5E3"];
    collada [label="collada"];
    colladaschema [label="collada-schema-1-4-1"];
    wrapped [label="wrapped-flow-layout"];

    externalpom -> collada;
    externalpom -> colladaschema;
    externalpom -> wrapped;
  }

  rootpom -> corepom;
  rootpom -> externalpom;
  aliceide -> ide;
  netbeans -> storyapi;
  netbeans -> glrender;
  netbeans -> ast;
  modelloading -> collada;
  modelloading -> colladaschema;
}
```

Source: [`repo-surface.dot`](./repo-surface.dot)

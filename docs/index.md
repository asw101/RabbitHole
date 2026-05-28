# Alice 3 Modernization

Alice 3 is a teaching tool for building 3D stories, animations, and simple games.
RabbitHole keeps that classroom experience working while the codebase is modernized,
tested, and broken into smaller pieces that are easier to change safely.

## Current status

- Build system: **22 Maven modules** on **Java 21**
- Main validation lanes: **Checkstyle**, **headless no-Sims tests**, and **JaCoCo coverage**

## Quick links

- [Getting started](./getting-started.md)
- [Architecture](./architecture.md)
- [Testing](./testing.md)
- [Contributing](./contributing.md)
- [Concepts](#concepts)
- [Architecture Atlas](#architecture-atlas)

## What this site covers

- how to clone, build, test, and package Alice 3
- how the Maven modules fit together
- how characterization tests protect refactors
- where to find architecture diagrams for each layer of the codebase

## Documentation map

### Start here

- [Getting started](./getting-started.md) — clone, build, test, and run
- [Architecture](./architecture.md) — modules, layers, and build lanes
- [Testing](./testing.md) — test lanes, patterns, and coverage
- [Contributing](./contributing.md) — process, checks, and documentation policy

### Concepts

- [Formal Specification Lane](./concepts/formal-spec-lane.md) — save, load, export, and backup recovery contracts
- [Migration Hotspot Characterization](./concepts/migration-hotspot-characterization.md) — protecting the project migration pipeline

### Architecture Atlas

Machine-generated architecture diagrams in Graphviz DOT and Mermaid format,
organized by layer:

- [Atlas Overview](./atlas/index.md)
- [Repository Surface](./atlas/repo-surface/README.md)
- [Symbol Bindings](./atlas/ast-lsp-bindings/README.md)
- [Compile Dependencies](./atlas/compile-deps/README.md)
- [Runtime Topology](./atlas/runtime-topology/README.md)
- [API Contracts](./atlas/api-contracts/README.md)
- [Data Flow](./atlas/data-flow/README.md)
- [Service Components](./atlas/service-components/README.md)
- [User Journeys](./atlas/user-journeys/README.md)

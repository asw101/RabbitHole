## Bug: Hidden story-api-migration dependency on the save/load path

**Layer**: compile-deps x service-components
**Severity**: Medium
**Pass**: 1

**Evidence**:

- `docs/atlas/compile-deps/README.md:40-46` shows `ide` depending on `support`, `util`, `ast`, `croquet`, `scenegraph`, `glrender`, and `storyapi`, but not directly on `story-api-migration`.
- `core/ide/pom.xml:91-94` declares a direct `story-api-migration` dependency, and `core/ide/src/main/java/org/alice/ide/uricontent/AbstractFileProjectLoader.java:51-52` imports `org.lgna.project.io.IoUtilities` and `ProjectIo` on the documented save/load path.
- `docs/atlas/compile-deps/README.md:53-59` also omits a direct `netbeans -> story-api-migration` edge.
- `netbeans/pom.xml:206-209` declares the same dependency, and `netbeans/src/main/java/org/alice/netbeans/project/ProjectCodeGenerator.java:53` imports `org.lgna.project.io.IoUtilities`.
- code_quote: `core/ide/pom.xml:91-94`
  ```xml
  <dependency>
    <groupId>org.alice</groupId>
    <artifactId>story-api-migration</artifactId>
  </dependency>
  ```
- code_quote: `core/ide/src/main/java/org/alice/ide/uricontent/AbstractFileProjectLoader.java:51-52`
  ```java
  import org.lgna.project.io.IoUtilities;
  import org.lgna.project.io.ProjectIo;
  ```

**Impact**: The compile-deps atlas hides a real compile-time edge that the IDE and NetBeans generator both use for project I/O. That makes the save/load/export path look less coupled than it really is and can mislead refactors or dependency cleanup work.

**Fix**: Add explicit `ide -> story-api-migration` and `netbeans -> story-api-migration` edges in `docs/atlas/compile-deps/*`, or split the `support` aggregate so project-I/O dependencies remain visible on the main graph.

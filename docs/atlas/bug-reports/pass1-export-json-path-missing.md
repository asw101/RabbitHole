## Bug: Export path is documented as XML, but code uses JsonProjectIo

**Layer**: data-flow x api-contracts
**Severity**: High
**Pass**: 1

**Evidence**:

- `docs/atlas/api-contracts/README.md:11` says project load/save/export contracts flow through `XmlProjectIo`.
- `docs/atlas/data-flow/README.md:11` says save/export turns the AST back into XML in a ZIP-backed `.a3p` archive.
- `core/ide/src/main/java/org/alice/ide/ProjectFileUtilities.java:121-123` routes export through `IoUtilities.exportProject(...)`.
- `core/story-api-migration/src/main/java/org/lgna/project/io/IoUtilities.java:108-110` sends export to `playerWriter().writeProject(...)`, and `core/story-api-migration/src/main/java/org/lgna/project/io/IoUtilities.java:196-197` defines `playerWriter()` as `JsonProjectIo.writer()`.
- code_quote: `core/ide/src/main/java/org/alice/ide/ProjectFileUtilities.java:121-123`
  ```java
  void exportCopyOfProjectTo(File file) throws IOException {
    Project project = getForcedUpToDateProject();
    IoUtilities.exportProject(file, project, thumbnailDataSources());
  }
  ```
- code_quote: `core/story-api-migration/src/main/java/org/lgna/project/io/IoUtilities.java:108-110`
  ```java
  public static void exportProject(File file, Project project, DataSource... dataSources) throws IOException {
    FileUtilities.createParentDirectoriesIfNecessary(file);
    playerWriter().writeProject(new FileOutputStream(file), project, dataSources);
  }
  ```
- code_quote: `core/story-api-migration/src/main/java/org/lgna/project/io/IoUtilities.java:196-197`
  ```java
  private static ProjectIo.ProjectWriter playerWriter() {
    return JsonProjectIo.writer();
  }
  ```

**Impact**: The atlas collapses save and export into one XML `.a3p` path, but the real export lane is a separate JSON-backed `.a3w` writer. Anyone tracing export bugs from the atlas will inspect the wrong serializer and miss manifest/file-type behavior unique to player exports.

**Fix**: Split the atlas into two explicit branches: save (`IoUtilities.writeProject` -> `XmlProjectIo` -> `.a3p`) and export (`IoUtilities.exportProject` -> `JsonProjectIo` -> `.a3w`). Update both `api-contracts` and `data-flow`, and adjust the related user-journey wording.

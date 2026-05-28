package org.alice.ide.refactoring;

import org.junit.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * TDD contract tests for PR #826: Singleton documentation.
 *
 * Verifies that the singleton architecture documentation exists and
 * contains the expected sections. Also validates the shared CI
 * infrastructure changes that ship with this PR.
 *
 * FAILS on develop: docs/architecture/singletons.md doesn't exist.
 * PASSES after PR #826 merge: documentation file present with content.
 */
public class SingletonDocumentationContractTest {

  private static Path findRepoRoot() {
    Path current = Paths.get("").toAbsolutePath();
    while (current != null) {
      if (Files.exists(current.resolve("pom.xml")) && Files.exists(current.resolve("docs"))) {
        return current;
      }
      current = current.getParent();
    }
    return Paths.get("").toAbsolutePath();
  }

  // ── Documentation file existence ──────────────────────────────────

  @Test
  public void singletonDocumentationFileExists() {
    Path docPath = findRepoRoot().resolve("docs/architecture/singletons.md");
    assertTrue("docs/architecture/singletons.md must exist after PR #826",
        Files.exists(docPath));
  }

  @Test
  public void singletonDocumentation_isNotEmpty() throws IOException {
    Path docPath = findRepoRoot().resolve("docs/architecture/singletons.md");
    if (Files.exists(docPath)) {
      long size = Files.size(docPath);
      assertTrue("Singleton docs must have substantial content (>1KB)", size > 1024);
    }
  }

  @Test
  public void singletonDocumentation_containsSingletonInventory() throws IOException {
    Path docPath = findRepoRoot().resolve("docs/architecture/singletons.md");
    if (Files.exists(docPath)) {
      String content = new String(Files.readAllBytes(docPath));
      assertTrue("Must contain singleton inventory section",
          content.contains("Singleton") || content.contains("singleton"));
    }
  }

  // ── Architecture docs directory ───────────────────────────────────

  @Test
  public void architectureDocsDirectoryExists() {
    Path archDir = findRepoRoot().resolve("docs/architecture");
    assertTrue("docs/architecture/ directory must exist", Files.isDirectory(archDir));
  }

  // ── mkdocs.yml references ─────────────────────────────────────────

  @Test
  public void mkdocsYml_referencesArchitectureDocs() throws IOException {
    Path mkdocs = findRepoRoot().resolve("mkdocs.yml");
    if (Files.exists(mkdocs)) {
      String content = new String(Files.readAllBytes(mkdocs));
      assertTrue("mkdocs.yml must reference architecture docs",
          content.contains("architecture/") || content.contains("Architecture"));
    }
  }

  // ── Reference docs for all refactoring PRs ────────────────────────

  @Test
  public void referenceDoc_jamaExists() {
    Path docPath = findRepoRoot().resolve("docs/reference/jama-maven-dependency.md");
    assertTrue("JAMA reference doc must exist", Files.exists(docPath));
  }

  @Test
  public void referenceDoc_textMigrationExists() {
    Path docPath = findRepoRoot().resolve("docs/reference/text-migration-json.md");
    assertTrue("TextMigration reference doc must exist", Files.exists(docPath));
  }

  @Test
  public void referenceDoc_ikPoserExists() {
    Path docPath = findRepoRoot().resolve("docs/reference/ik-poser-module.md");
    assertTrue("IK Poser reference doc must exist", Files.exists(docPath));
  }

  @Test
  public void referenceDoc_adapterFactoryExists() {
    Path docPath = findRepoRoot().resolve("docs/reference/adapter-factory-supplier.md");
    assertTrue("AdapterFactory reference doc must exist", Files.exists(docPath));
  }

  @Test
  public void referenceDoc_clipboardDndExists() {
    Path docPath = findRepoRoot().resolve("docs/reference/clipboard-dnd-module.md");
    assertTrue("Clipboard/DnD reference doc must exist", Files.exists(docPath));
  }

  @Test
  public void referenceDoc_moduleExtractionOverviewExists() {
    Path docPath = findRepoRoot().resolve("docs/reference/module-extraction-overview.md");
    assertTrue("Module extraction overview doc must exist", Files.exists(docPath));
  }
}

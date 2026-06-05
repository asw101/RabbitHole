package org.lgna.project.io.compat;

import org.junit.Test;
import org.lgna.common.Resource;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.*;

public class ReplayCaseFactoryTest {
  @Test
  public void createsExpectedDeterministicReplayCasesInStableOrder() {
    ReplayCaseFactory factory = new ReplayCaseFactory();

    List<ReplayCase> first = factory.createCases();
    List<ReplayCase> second = factory.createCases();

    assertEquals(List.of(
        "empty-project",
        "project-with-text-resource",
        "project-with-multiple-resources",
        "project-with-generated-source-shape"),
        ids(first));
    assertEquals(ids(first), ids(second));
    assertEquals(projectNames(first), projectNames(second));
    assertEquals(resourceNames(first), resourceNames(second));
    assertThrows(UnsupportedOperationException.class, () -> first.add(factory.caseNamed("empty-project")));
  }

  @Test
  public void generatedCasesDoNotDependOnCommittedBinaryFixtures() {
    for (ReplayCase replayCase : new ReplayCaseFactory().createCases()) {
      assertSafeCaseId(replayCase.id());
      assertNotNull(replayCase.project());
      assertNotNull(replayCase.project().getProgramType());
      assertFalse("Replay case ids must not be binary fixture names: " + replayCase.id(),
          replayCase.id().endsWith(".a3p") || replayCase.id().endsWith(".a3w") || replayCase.id().endsWith(".a3c"));

      for (ReplaySourceInput sourceInput : replayCase.sourceInputs()) {
        assertSafeRelativePath(sourceInput.path());
        assertTrue(sourceInput.path().endsWith(".twe") || sourceInput.path().endsWith(".java"));
        assertTrue("Generated source text should end with a newline.", sourceInput.text().endsWith("\n"));
        assertFalse(sourceInput.text().contains("\r"));
      }

      for (Resource resource : replayCase.project().getResources()) {
        assertNotNull(resource.getName());
        assertFalse(resource.getName().isBlank());
        assertNotNull(resource.getOriginalFileName());
        assertFalse(resource.getOriginalFileName().isBlank());
        assertTrue("Generated resource payloads should stay tiny and reviewable.",
            resource.getData().length <= 256);
      }
    }
  }

  @Test
  public void sourceShapeCaseIncludesOnlyNormalizedGeneratedSources() {
    ReplayCase replayCase = new ReplayCaseFactory().caseNamed("project-with-generated-source-shape");

    assertEquals("project-with-generated-source-shape", replayCase.id());
    assertFalse(replayCase.sourceInputs().isEmpty());
    for (ReplaySourceInput sourceInput : replayCase.sourceInputs()) {
      assertSafeRelativePath(sourceInput.path());
      assertFalse(sourceInput.text().contains("\r"));
      assertFalse(sourceInput.text().contains(System.getProperty("java.io.tmpdir")));
      assertTrue(sourceInput.text().contains(replayCase.project().getProgramType().getName()));
    }
  }

  @Test
  public void rejectsUnknownCaseIds() {
    IllegalArgumentException thrown = assertThrows(
        IllegalArgumentException.class,
        () -> new ReplayCaseFactory().caseNamed("missing-case"));

    assertTrue(thrown.getMessage().contains("missing-case"));
  }

  private static List<String> ids(List<ReplayCase> cases) {
    List<String> ids = new ArrayList<>();
    for (ReplayCase replayCase : cases) {
      ids.add(replayCase.id());
    }
    return ids;
  }

  private static List<String> projectNames(List<ReplayCase> cases) {
    List<String> names = new ArrayList<>();
    for (ReplayCase replayCase : cases) {
      names.add(replayCase.project().getProgramType().getName());
    }
    return names;
  }

  private static Set<String> resourceNames(List<ReplayCase> cases) {
    Set<String> names = new HashSet<>();
    for (ReplayCase replayCase : cases) {
      for (Resource resource : replayCase.project().getResources()) {
        names.add(resource.getName() + ":" + resource.getOriginalFileName() + ":" + resource.getContentType());
      }
    }
    return names;
  }

  private static void assertSafeCaseId(String id) {
    assertTrue("Unsafe replay case id: " + id, id.matches("[a-z0-9]+(-[a-z0-9]+)*"));
  }

  private static void assertSafeRelativePath(String path) {
    assertFalse("Path must be relative: " + path, path.startsWith("/"));
    assertFalse("Path must not contain traversal: " + path, path.contains(".."));
    assertFalse("Path must use slash separators: " + path, path.contains("\\"));
    assertFalse("Path must not be a Windows drive path: " + path, path.matches("^[A-Za-z]:.*"));
  }
}

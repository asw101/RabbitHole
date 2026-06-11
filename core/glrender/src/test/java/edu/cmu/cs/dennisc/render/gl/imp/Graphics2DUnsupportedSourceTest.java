package edu.cmu.cs.dennisc.render.gl.imp;

import org.junit.Test;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class Graphics2DUnsupportedSourceTest {
  @Test
  public void graphics2DUsesCentralUnsupportedFactory() throws Exception {
    Path sourceFile = findSourceFile(
        "core/glrender/src/main/java/edu/cmu/cs/dennisc/render/gl/imp/Graphics2D.java");
    assertNotNull("Must find Graphics2D.java", sourceFile);
    String content = new String(Files.readAllBytes(sourceFile));

    assertFalse("Intentional unsupported paths should use Graphics2DUnsupported",
        content.contains("new RuntimeException(\"not implemented\")"));
    assertFalse("Attributed text compatibility path should use Graphics2DUnsupported",
        content.contains("new RuntimeException(\"todo: use drawString( String, float, float ) for now\")"));
    assertTrue("Graphics2D should call the unsupported helper",
        content.contains("Graphics2DUnsupported."));
  }

  @Test
  public void unsupportedFactoryPreservesBaselineMessages() throws Exception {
    Path sourceFile = findSourceFile(
        "core/glrender/src/main/java/edu/cmu/cs/dennisc/render/gl/imp/Graphics2DUnsupported.java");
    assertNotNull("Must find Graphics2DUnsupported.java", sourceFile);
    String content = new String(Files.readAllBytes(sourceFile));

    assertTrue(content.contains("\"not implemented\""));
    assertTrue(content.contains("\"todo: use drawString( String, float, float ) for now\""));
  }

  private Path findSourceFile(String relativePath) {
    Path cwd = Paths.get(System.getProperty("user.dir"));
    for (Path p = cwd; p != null; p = p.getParent()) {
      Path candidate = p.resolve(relativePath);
      if (Files.exists(candidate)) {
        return candidate;
      }
      if (p.equals(p.getRoot())) {
        break;
      }
    }
    return null;
  }
}

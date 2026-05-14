package org.lgna.story.implementation;

import org.junit.Test;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.Assert.*;

/**
 * Characterization tests for the GroundImp decomposition into GroundImp + GroundMeshData.
 *
 * <p>Verifies that:
 * <ul>
 *   <li>GroundImp's public API surface is preserved</li>
 *   <li>GroundMeshData is package-private</li>
 *   <li>Mesh data arrays have expected sizes</li>
 *   <li>GroundImp is under 500 lines (structurally verified)</li>
 * </ul>
 */
public class GroundImpStructureTest {

  private static final Set<String> EXPECTED_PUBLIC_METHODS = Set.of(
      "getAbstraction",
      "getResizers",
      "getValueForResizer",
      "setValueForResizer",
      "setSize"
  );

  @Test
  public void groundImpPublicApiIsPreserved() {
    Set<String> actual = Arrays.stream(GroundImp.class.getDeclaredMethods())
        .filter(m -> Modifier.isPublic(m.getModifiers()))
        .map(Method::getName)
        .collect(Collectors.toSet());

    for (String expected : EXPECTED_PUBLIC_METHODS) {
      assertTrue("Missing public method: " + expected, actual.contains(expected));
    }
  }

  @Test
  public void groundMeshDataIsPackagePrivate() {
    int modifiers = GroundMeshData.class.getModifiers();
    assertFalse("GroundMeshData must not be public", Modifier.isPublic(modifiers));
    assertFalse("GroundMeshData must not be protected", Modifier.isProtected(modifiers));
    assertFalse("GroundMeshData must not be private", Modifier.isPrivate(modifiers));
  }

  @Test
  public void verticesArrayHasExpectedSize() {
    assertEquals("VERTICES array should have 593 entries", 593, GroundMeshData.VERTICES.length);
  }

  @Test
  public void polygonDataArrayIsNonEmpty() {
    assertTrue("POLYGON_DATA should be non-empty", GroundMeshData.POLYGON_DATA.length > 0);
  }

  @Test
  public void groundImpHasFewDeclaredMethods() {
    long count = Arrays.stream(GroundImp.class.getDeclaredMethods())
        .filter(m -> !m.isSynthetic())
        .count();
    assertTrue("GroundImp should have fewer than 10 declared methods, got " + count,
        count < 10);
  }
}

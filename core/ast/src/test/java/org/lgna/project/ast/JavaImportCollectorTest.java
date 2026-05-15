package org.lgna.project.ast;

import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;

/**
 * TDD tests for JavaImportCollector — the delegate extracted from
 * JavaCodeGenerator that manages import tracking sets and builds
 * the import block text.
 *
 * These tests define the contract; they will FAIL until the
 * JavaImportCollector class is implemented.
 */
public class JavaImportCollectorTest {

  private JavaImportCollector collector;

  @Before
  public void setUp() {
    collector = new JavaImportCollector(
        List.of(JavaPackage.getInstance(org.lgna.common.ThreadUtilities.class.getPackage())),
        List.of()
    );
  }

  // --- trackType: on-demand vs explicit ---

  @Test
  public void tracksTypeFromOnDemandPackageAsPackageImport() {
    JavaType threadUtilType = JavaType.getInstance(org.lgna.common.ThreadUtilities.class);
    collector.trackType(threadUtilType);

    String imports = collector.buildImports("", "");
    assertTrue("on-demand package import expected",
        imports.contains("import org.lgna.common.*;"));
    assertFalse("explicit type import should not appear for on-demand package",
        imports.contains("import org.lgna.common.ThreadUtilities;"));
  }

  @Test
  public void tracksTypeFromNonOnDemandPackageAsExplicitImport() {
    JavaType listType = JavaType.getInstance(java.util.List.class);
    collector.trackType(listType);

    String imports = collector.buildImports("", "");
    assertTrue("explicit import expected",
        imports.contains("import java.util.List;"));
  }

  @Test
  public void skipsJavaLangTypesInExplicitImports() {
    JavaType stringType = JavaType.getInstance(String.class);
    collector.trackType(stringType);

    String imports = collector.buildImports("", "");
    assertFalse("java.lang types should not be imported",
        imports.contains("import java.lang.String;"));
  }

  @Test
  public void skipsPrimitiveTypes() {
    JavaType intType = JavaType.getInstance(int.class);
    collector.trackType(intType);

    String imports = collector.buildImports("", "");
    assertEquals("no imports for primitives", "", imports);
  }

  @Test
  public void tracksEnclosingTypeInImportPath() {
    // Map.Entry is an enclosed type — import should be java.util.Map.Entry
    JavaType mapEntryType = JavaType.getInstance(java.util.Map.Entry.class);
    collector.trackType(mapEntryType);

    String imports = collector.buildImports("", "");
    assertTrue("enclosed type import must include enclosing type",
        imports.contains("import java.util.Map.Entry;"));
  }

  @Test
  public void enclosingTypeAlwaysUsesExplicitImportEvenWhenPackageIsOnDemand() {
    // Even if java.util were on-demand, enclosed types get explicit imports
    JavaImportCollector collectorWithUtil = new JavaImportCollector(
        List.of(JavaPackage.getInstance(java.util.List.class.getPackage())),
        List.of()
    );
    JavaType mapEntryType = JavaType.getInstance(java.util.Map.Entry.class);
    collectorWithUtil.trackType(mapEntryType);

    String imports = collectorWithUtil.buildImports("", "");
    assertTrue("enclosed type must use explicit import",
        imports.contains("import java.util.Map.Entry;"));
  }

  // --- trackStaticMethod ---

  @Test
  public void tracksStaticMethodImport() {
    JavaMethod valueOfMethod = JavaMethod.getInstance(String.class, "valueOf", int.class);

    JavaImportCollector collectorWithStaticImport = new JavaImportCollector(
        List.of(),
        List.of(valueOfMethod)
    );
    collectorWithStaticImport.trackStaticMethod(valueOfMethod);

    String imports = collectorWithStaticImport.buildImports("", "");
    assertTrue("static import expected",
        imports.contains("import static java.lang.String.valueOf;"));
  }

  @Test
  public void doesNotTrackStaticMethodNotInMarkedList() {
    JavaMethod valueOfMethod = JavaMethod.getInstance(String.class, "valueOf", int.class);

    // collector has no marked static methods
    JavaImportCollector emptyCollector = new JavaImportCollector(List.of(), List.of());
    emptyCollector.trackStaticMethod(valueOfMethod);

    String imports = emptyCollector.buildImports("", "");
    assertFalse("unmarked static method should not be imported",
        imports.contains("import static"));
  }

  // --- buildImports with prefix/postfix ---

  @Test
  public void buildImportsIncludesPrefixAndPostfix() {
    JavaType listType = JavaType.getInstance(java.util.List.class);
    collector.trackType(listType);

    String imports = collector.buildImports("PREFIX\n", "\nPOSTFIX");
    assertTrue("prefix expected", imports.startsWith("PREFIX\n"));
    assertTrue("postfix expected", imports.endsWith("\nPOSTFIX"));
  }

  @Test
  public void buildImportsReturnsEmptyPrefixPostfixWhenNoImports() {
    String imports = collector.buildImports("PRE", "POST");
    assertEquals("empty imports should still wrap prefix/postfix", "PREPOST", imports);
  }

  // --- deduplication ---

  @Test
  public void deduplicatesRepeatedTypeTracking() {
    JavaType listType = JavaType.getInstance(java.util.List.class);
    collector.trackType(listType);
    collector.trackType(listType);

    String imports = collector.buildImports("", "");
    int firstIdx = imports.indexOf("import java.util.List;");
    int secondIdx = imports.indexOf("import java.util.List;", firstIdx + 1);
    assertEquals("duplicate import should not appear", -1, secondIdx);
  }

  @Test
  public void deduplicatesRepeatedOnDemandPackageTracking() {
    JavaType type1 = JavaType.getInstance(org.lgna.common.ThreadUtilities.class);
    JavaType type2 = JavaType.getInstance(org.lgna.common.EachInTogetherRunnable.class);
    collector.trackType(type1);
    collector.trackType(type2);

    String imports = collector.buildImports("", "");
    int firstIdx = imports.indexOf("import org.lgna.common.*;");
    int secondIdx = imports.indexOf("import org.lgna.common.*;", firstIdx + 1);
    assertEquals("duplicate on-demand import should not appear", -1, secondIdx);
  }

  // --- edge cases ---

  @Test
  public void handlesNullPackageTypeGracefully() {
    // UserTypes may have no package — trackType must not throw
    JavaType intPrimitive = JavaType.getInstance(int.class);
    collector.trackType(intPrimitive);
    // No exception expected; no import generated
    String imports = collector.buildImports("", "");
    assertNotNull(imports);
  }

  @Test
  public void emptyCollectorProducesEmptyImports() {
    String imports = collector.buildImports("", "");
    assertEquals("", imports);
  }
}

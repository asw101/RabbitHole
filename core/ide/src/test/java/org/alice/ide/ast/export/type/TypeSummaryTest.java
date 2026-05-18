package org.alice.ide.ast.export.type;

import org.junit.Test;
import org.lgna.project.VersionNotSupportedException;
import org.w3c.dom.Document;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Tests for {@link TypeSummary} and its constituent data classes
 * ({@link FieldInfo}, {@link FunctionInfo}, {@link ResourceInfo}).
 * Exercises construction, getters, and XML encode/decode round-trips.
 */
public class TypeSummaryTest {

  // ---- TypeSummary constructor and getters ----

  @Test
  public void typeSummary_fullConstructor_allGettersWork() {
    List<String> hierarchy = Arrays.asList("Parent", "GrandParent", "java.lang.Object");
    ResourceInfo resource = new ResourceInfo("com.example.MyResource", "DEFAULT_VARIANT");
    List<String> procedures = Arrays.asList("walk", "talk", "jump");
    List<FunctionInfo> functions = Arrays.asList(
        new FunctionInfo("Boolean", "isHappy"),
        new FunctionInfo("Integer", "getAge"));
    List<FieldInfo> fields = Arrays.asList(
        new FieldInfo("String", "name"),
        new FieldInfo("Double", "speed"),
        new FieldInfo("Integer", "health"));

    TypeSummary ts = new TypeSummary(3.1, "Hero", hierarchy, resource, procedures, functions, fields);

    assertEquals(3.1, ts.getVersion(), 0.0);
    assertEquals("Hero", ts.getTypeName());
    assertEquals(3, ts.getHierarchyClassNames().size());
    assertEquals("Parent", ts.getHierarchyClassNames().get(0));
    assertEquals("java.lang.Object", ts.getHierarchyClassNames().get(2));
    assertNotNull(ts.getResourceInfo());
    assertEquals("com.example.MyResource", ts.getResourceInfo().getClassName());
    assertEquals("DEFAULT_VARIANT", ts.getResourceInfo().getFieldName());
    assertEquals(3, ts.getProcedureNames().size());
    assertEquals(2, ts.getFunctionInfos().size());
    assertEquals(3, ts.getFieldInfos().size());
  }

  @Test
  public void typeSummary_nullResource_getterReturnsNull() {
    TypeSummary ts = new TypeSummary(3.1, "Simple",
        Collections.emptyList(), null,
        Collections.emptyList(), Collections.emptyList(), Collections.emptyList());
    assertNull(ts.getResourceInfo());
  }

  @Test
  public void typeSummary_emptyCollections() {
    TypeSummary ts = new TypeSummary(3.1, "Empty",
        Collections.emptyList(), null,
        Collections.emptyList(), Collections.emptyList(), Collections.emptyList());
    assertTrue(ts.getHierarchyClassNames().isEmpty());
    assertTrue(ts.getProcedureNames().isEmpty());
    assertTrue(ts.getFunctionInfos().isEmpty());
    assertTrue(ts.getFieldInfos().isEmpty());
  }

  // ---- FieldInfo ----

  @Test
  public void fieldInfo_constructorAndGetters() {
    FieldInfo fi = new FieldInfo("java.lang.Integer", "count");
    assertEquals("java.lang.Integer", fi.getValueClassName());
    assertEquals("count", fi.getName());
  }

  @Test
  public void fieldInfo_nullValues() {
    FieldInfo fi = new FieldInfo(null, null);
    assertNull(fi.getValueClassName());
    assertNull(fi.getName());
  }

  // ---- FunctionInfo ----

  @Test
  public void functionInfo_constructorAndGetters() {
    FunctionInfo fi = new FunctionInfo("java.lang.Boolean", "isReady");
    assertEquals("java.lang.Boolean", fi.getReturnClassName());
    assertEquals("isReady", fi.getName());
  }

  @Test
  public void functionInfo_emptyStrings() {
    FunctionInfo fi = new FunctionInfo("", "");
    assertEquals("", fi.getReturnClassName());
    assertEquals("", fi.getName());
  }

  // ---- ResourceInfo ----

  @Test
  public void resourceInfo_constructorAndGetters() {
    ResourceInfo ri = new ResourceInfo("my.Resource", "VARIANT_A");
    assertEquals("my.Resource", ri.getClassName());
    assertEquals("VARIANT_A", ri.getFieldName());
  }

  @Test
  public void resourceInfo_nullFieldName() {
    ResourceInfo ri = new ResourceInfo("my.Resource", null);
    assertEquals("my.Resource", ri.getClassName());
    assertNull(ri.getFieldName());
  }

  // ---- Constants ----

  @Test
  public void currentVersion_is3_1() {
    assertEquals(3.1, TypeSummary.CURRENT_VERSION, 0.0);
  }

  @Test
  public void minimumVersion_is3_1() {
    assertEquals(3.1, TypeSummary.MINIMUM_ACCEPTABLE_VERSION, 0.0);
  }

  // ---- XML round-trip via TypeXmlUtitlities ----

  @Test
  public void encodeDecode_fullSummary_preservesAllFields() throws VersionNotSupportedException {
    List<String> hierarchy = Arrays.asList("SBiped", "SJointedModel", "SThing", "org.lgna.story.SThing");
    ResourceInfo resource = new ResourceInfo("org.lgna.story.resources.biped.AlienResource", "DEFAULT");
    List<String> procedures = Arrays.asList("dance", "wave", "bow");
    List<FunctionInfo> functions = Arrays.asList(
        new FunctionInfo("SJoint", "getHead"),
        new FunctionInfo("SJoint", "getLeftHand"),
        new FunctionInfo("Boolean", "isMoving"));
    List<FieldInfo> fields = Arrays.asList(
        new FieldInfo("Double", "movementSpeed"),
        new FieldInfo("Color", "skinColor"));

    TypeSummary original = new TypeSummary(TypeSummary.CURRENT_VERSION, "Alien",
        hierarchy, resource, procedures, functions, fields);

    Document doc = TypeXmlUtitlities.encode(original);
    assertNotNull(doc);

    TypeSummary decoded = TypeXmlUtitlities.decode(doc);
    assertEquals(original.getVersion(), decoded.getVersion(), 0.0);
    assertEquals("Alien", decoded.getTypeName());
    assertEquals(hierarchy.size(), decoded.getHierarchyClassNames().size());
    for (int i = 0; i < hierarchy.size(); i++) {
      assertEquals(hierarchy.get(i), decoded.getHierarchyClassNames().get(i));
    }
    assertEquals("org.lgna.story.resources.biped.AlienResource", decoded.getResourceInfo().getClassName());
    assertEquals("DEFAULT", decoded.getResourceInfo().getFieldName());
    assertEquals(3, decoded.getProcedureNames().size());
    assertEquals("dance", decoded.getProcedureNames().get(0));
    assertEquals(3, decoded.getFunctionInfos().size());
    assertEquals("getHead", decoded.getFunctionInfos().get(0).getName());
    assertEquals("SJoint", decoded.getFunctionInfos().get(0).getReturnClassName());
    assertEquals(2, decoded.getFieldInfos().size());
    assertEquals("movementSpeed", decoded.getFieldInfos().get(0).getName());
    assertEquals("Double", decoded.getFieldInfos().get(0).getValueClassName());
  }

  @Test
  public void encodeDecode_noResourceNoProcedures_preservesEmpty() throws VersionNotSupportedException {
    TypeSummary original = new TypeSummary(TypeSummary.CURRENT_VERSION, "Minimal",
        Collections.singletonList("java.lang.Object"),
        null,
        Collections.emptyList(),
        Collections.emptyList(),
        Collections.emptyList());

    Document doc = TypeXmlUtitlities.encode(original);
    TypeSummary decoded = TypeXmlUtitlities.decode(doc);

    assertEquals("Minimal", decoded.getTypeName());
    assertNull(decoded.getResourceInfo());
    assertEquals(1, decoded.getHierarchyClassNames().size());
    assertTrue(decoded.getProcedureNames().isEmpty());
    assertTrue(decoded.getFunctionInfos().isEmpty());
    assertTrue(decoded.getFieldInfos().isEmpty());
  }

  @Test(expected = VersionNotSupportedException.class)
  public void decode_tooOldVersion_throws() throws VersionNotSupportedException {
    TypeSummary ts = new TypeSummary(2.0, "Old",
        Collections.emptyList(), null,
        Collections.emptyList(), Collections.emptyList(), Collections.emptyList());
    Document doc = TypeXmlUtitlities.encode(ts);
    TypeXmlUtitlities.decode(doc);
  }

  @Test
  public void encode_documentHasCorrectRootTag() {
    TypeSummary ts = new TypeSummary(TypeSummary.CURRENT_VERSION, "Test",
        Collections.emptyList(), null,
        Collections.emptyList(), Collections.emptyList(), Collections.emptyList());
    Document doc = TypeXmlUtitlities.encode(ts);
    assertEquals("typeSummary", doc.getDocumentElement().getTagName());
  }

  @Test
  public void encodeDecode_resourceWithoutFieldName_preservesNull() throws VersionNotSupportedException {
    ResourceInfo ri = new ResourceInfo("my.DynamicResource", null);
    TypeSummary original = new TypeSummary(TypeSummary.CURRENT_VERSION, "Dynamic",
        Arrays.asList("SModel"), ri,
        Collections.singletonList("move"),
        Collections.emptyList(),
        Collections.emptyList());

    Document doc = TypeXmlUtitlities.encode(original);
    TypeSummary decoded = TypeXmlUtitlities.decode(doc);

    assertNotNull(decoded.getResourceInfo());
    assertEquals("my.DynamicResource", decoded.getResourceInfo().getClassName());
    assertNull(decoded.getResourceInfo().getFieldName());
  }
}

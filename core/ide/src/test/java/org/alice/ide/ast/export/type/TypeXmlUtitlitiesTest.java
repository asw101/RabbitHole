package org.alice.ide.ast.export.type;

import org.junit.Test;
import org.lgna.project.VersionNotSupportedException;
import org.w3c.dom.Document;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Tests for {@link TypeXmlUtitlities} — XML encode/decode round-trip
 * covering TypeSummary, ResourceInfo, FunctionInfo, FieldInfo data classes.
 */
public class TypeXmlUtitlitiesTest {

  // ---- encode → decode round-trip with full TypeSummary ----

  @Test
  public void roundTrip_fullTypeSummary() throws VersionNotSupportedException {
    List<String> hierarchy = Arrays.asList("Biped", "org.lgna.story.SBiped");
    ResourceInfo resource = new ResourceInfo("org.lgna.story.resources.biped.BunnyResource", "DEFAULT");
    List<String> procedures = Arrays.asList("hop", "skip", "jump");
    List<FunctionInfo> functions = Arrays.asList(
        new FunctionInfo("java.lang.Boolean", "isHappy"),
        new FunctionInfo("Hurdle", "getClosestHurdle"));
    List<FieldInfo> fields = Arrays.asList(
        new FieldInfo("java.lang.Integer", "coinCount"),
        new FieldInfo("java.lang.String", "playerName"));

    TypeSummary original = new TypeSummary(
        TypeSummary.CURRENT_VERSION, "Bunny", hierarchy, resource, procedures, functions, fields);

    Document xmlDocument = TypeXmlUtitlities.encode(original);
    assertNotNull(xmlDocument);

    TypeSummary decoded = TypeXmlUtitlities.decode(xmlDocument);

    assertEquals(original.getVersion(), decoded.getVersion(), 0.0);
    assertEquals("Bunny", decoded.getTypeName());
    assertEquals(hierarchy, decoded.getHierarchyClassNames());
    assertNotNull(decoded.getResourceInfo());
    assertEquals("org.lgna.story.resources.biped.BunnyResource", decoded.getResourceInfo().getClassName());
    assertEquals("DEFAULT", decoded.getResourceInfo().getFieldName());
    assertEquals(procedures, decoded.getProcedureNames());
    assertEquals(2, decoded.getFunctionInfos().size());
    assertEquals("isHappy", decoded.getFunctionInfos().get(0).getName());
    assertEquals("java.lang.Boolean", decoded.getFunctionInfos().get(0).getReturnClassName());
    assertEquals("getClosestHurdle", decoded.getFunctionInfos().get(1).getName());
    assertEquals(2, decoded.getFieldInfos().size());
    assertEquals("coinCount", decoded.getFieldInfos().get(0).getName());
    assertEquals("java.lang.Integer", decoded.getFieldInfos().get(0).getValueClassName());
  }

  // ---- encode → decode without resource ----

  @Test
  public void roundTrip_noResource() throws VersionNotSupportedException {
    TypeSummary original = new TypeSummary(
        TypeSummary.CURRENT_VERSION, "EmptyType",
        Collections.singletonList("java.lang.Object"),
        null, // no resource
        Collections.emptyList(),
        Collections.emptyList(),
        Collections.emptyList());

    Document xmlDocument = TypeXmlUtitlities.encode(original);
    TypeSummary decoded = TypeXmlUtitlities.decode(xmlDocument);

    assertEquals("EmptyType", decoded.getTypeName());
    assertNull(decoded.getResourceInfo());
    assertTrue(decoded.getProcedureNames().isEmpty());
    assertTrue(decoded.getFunctionInfos().isEmpty());
    assertTrue(decoded.getFieldInfos().isEmpty());
  }

  // ---- encode → decode with resource without field name ----

  @Test
  public void roundTrip_resourceWithoutFieldName() throws VersionNotSupportedException {
    ResourceInfo resource = new ResourceInfo("org.lgna.story.resources.SomeResource", null);
    TypeSummary original = new TypeSummary(
        TypeSummary.CURRENT_VERSION, "Widget",
        Arrays.asList("Base"),
        resource,
        Collections.singletonList("spin"),
        Collections.emptyList(),
        Collections.emptyList());

    Document xmlDocument = TypeXmlUtitlities.encode(original);
    TypeSummary decoded = TypeXmlUtitlities.decode(xmlDocument);

    assertNotNull(decoded.getResourceInfo());
    assertEquals("org.lgna.story.resources.SomeResource", decoded.getResourceInfo().getClassName());
    assertNull(decoded.getResourceInfo().getFieldName());
    assertEquals(1, decoded.getProcedureNames().size());
    assertEquals("spin", decoded.getProcedureNames().get(0));
  }

  // ---- encode produces valid XML Document ----

  @Test
  public void encode_producesDocument() {
    TypeSummary ts = new TypeSummary(TypeSummary.CURRENT_VERSION, "Test",
        Collections.emptyList(), null, Collections.emptyList(),
        Collections.emptyList(), Collections.emptyList());
    Document doc = TypeXmlUtitlities.encode(ts);
    assertNotNull(doc);
    assertNotNull(doc.getDocumentElement());
    assertEquals("typeSummary", doc.getDocumentElement().getTagName());
  }

  // ---- decode rejects unsupported version ----

  @Test(expected = VersionNotSupportedException.class)
  public void decode_rejectsOldVersion() throws Exception {
    TypeSummary ts = new TypeSummary(1.0, "OldType",
        Collections.emptyList(), null, Collections.emptyList(),
        Collections.emptyList(), Collections.emptyList());
    Document doc = TypeXmlUtitlities.encode(ts);
    // Will fail because version 1.0 < MINIMUM_ACCEPTABLE_VERSION (3.1)
    TypeXmlUtitlities.decode(doc);
  }

  // ---- TypeSummary data class ----

  @Test
  public void typeSummary_currentVersion() {
    assertEquals(3.1, TypeSummary.CURRENT_VERSION, 0.0);
  }

  @Test
  public void typeSummary_minimumVersion() {
    assertEquals(3.1, TypeSummary.MINIMUM_ACCEPTABLE_VERSION, 0.0);
  }

  @Test
  public void typeSummary_constructorSetsFields() {
    TypeSummary ts = new TypeSummary(3.1, "MyType",
        Arrays.asList("A", "B"),
        new ResourceInfo("cls", "fld"),
        Arrays.asList("proc1"),
        Arrays.asList(new FunctionInfo("ret", "fn1")),
        Arrays.asList(new FieldInfo("val", "f1")));
    assertEquals(3.1, ts.getVersion(), 0.0);
    assertEquals("MyType", ts.getTypeName());
    assertEquals(2, ts.getHierarchyClassNames().size());
    assertEquals(1, ts.getProcedureNames().size());
    assertEquals(1, ts.getFunctionInfos().size());
    assertEquals(1, ts.getFieldInfos().size());
  }

  // ---- ResourceInfo data class ----

  @Test
  public void resourceInfo_accessors() {
    ResourceInfo ri = new ResourceInfo("my.Class", "myField");
    assertEquals("my.Class", ri.getClassName());
    assertEquals("myField", ri.getFieldName());
  }

  @Test
  public void resourceInfo_nullFieldName() {
    ResourceInfo ri = new ResourceInfo("my.Class", null);
    assertNull(ri.getFieldName());
  }

  // ---- FunctionInfo data class ----

  @Test
  public void functionInfo_accessors() {
    FunctionInfo fi = new FunctionInfo("java.lang.Boolean", "isActive");
    assertEquals("java.lang.Boolean", fi.getReturnClassName());
    assertEquals("isActive", fi.getName());
  }

  // ---- FieldInfo data class ----

  @Test
  public void fieldInfo_accessors() {
    FieldInfo fi = new FieldInfo("java.lang.Double", "speed");
    assertEquals("java.lang.Double", fi.getValueClassName());
    assertEquals("speed", fi.getName());
  }

  // ---- multiple procedures and functions ----

  @Test
  public void roundTrip_manyProceduresAndFunctions() throws VersionNotSupportedException {
    List<String> procedures = Arrays.asList("walk", "run", "jump", "swim", "fly", "climb");
    List<FunctionInfo> functions = Arrays.asList(
        new FunctionInfo("Double", "getSpeed"),
        new FunctionInfo("Boolean", "isFlying"),
        new FunctionInfo("Integer", "getHealth"));
    List<FieldInfo> fields = Arrays.asList(
        new FieldInfo("Double", "x"),
        new FieldInfo("Double", "y"),
        new FieldInfo("Double", "z"));

    TypeSummary original = new TypeSummary(TypeSummary.CURRENT_VERSION, "Hero",
        Arrays.asList("Character", "Entity"),
        new ResourceInfo("my.HeroResource", "KNIGHT"),
        procedures, functions, fields);

    Document doc = TypeXmlUtitlities.encode(original);
    TypeSummary decoded = TypeXmlUtitlities.decode(doc);

    assertEquals(6, decoded.getProcedureNames().size());
    assertEquals(3, decoded.getFunctionInfos().size());
    assertEquals(3, decoded.getFieldInfos().size());
    assertEquals("walk", decoded.getProcedureNames().get(0));
    assertEquals("fly", decoded.getProcedureNames().get(4));
    assertEquals("getSpeed", decoded.getFunctionInfos().get(0).getName());
    assertEquals("z", decoded.getFieldInfos().get(2).getName());
  }
}

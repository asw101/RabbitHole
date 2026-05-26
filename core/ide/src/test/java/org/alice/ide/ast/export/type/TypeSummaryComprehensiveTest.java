package org.alice.ide.ast.export.type;

import static org.alice.ide.testing.JacocoReflectionSupport.declaredMethods;

import org.junit.Test;
import org.lgna.project.ast.BlockStatement;
import org.lgna.project.ast.JavaType;
import org.lgna.project.ast.ManagementLevel;
import org.lgna.project.ast.NamedUserType;
import org.lgna.project.ast.NullLiteral;
import org.lgna.project.ast.UserField;
import org.lgna.project.ast.UserMethod;
import org.lgna.project.ast.UserParameter;

import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.*;

public class TypeSummaryComprehensiveTest {

  private NamedUserType createType(String name) {
    NamedUserType type = new NamedUserType();
    type.name.setValue(name);
    return type;
  }

  private NamedUserType createSummarizedType() {
    NamedUserType parent = createType("ParentType");
    parent.superType.setValue(JavaType.OBJECT_TYPE);
    NamedUserType type = createType("HeroType");
    type.superType.setValue(parent);
    type.methods.add(new UserMethod("jump", JavaType.VOID_TYPE, new UserParameter[0], new BlockStatement()));
    type.methods.add(new UserMethod("getScore", JavaType.INTEGER_OBJECT_TYPE, new UserParameter[0], new BlockStatement()));
    UserMethod generated = new UserMethod("generatedOnly", JavaType.VOID_TYPE, new UserParameter[0], new BlockStatement());
    generated.managementLevel.setValue(ManagementLevel.GENERATED);
    type.methods.add(generated);
    type.fields.add(new UserField("coins", JavaType.INTEGER_OBJECT_TYPE, new NullLiteral()));
    return type;
  }

  private TypeSummary createManualSummary() {
    return new TypeSummary(
        3.1,
        "Hero",
        new ArrayList<>(Arrays.asList("Parent", "java.lang.Object")),
        new ResourceInfo("org.example.HeroResource", "DEFAULT"),
        new ArrayList<>(Arrays.asList("walk", "jump")),
        new ArrayList<>(Arrays.asList(new FunctionInfo("java.lang.Integer", "getScore"))),
        new ArrayList<>(Arrays.asList(new FieldInfo("java.lang.String", "name"))));
  }

  @Test
  public void classIsPublic() {
    assertTrue(Modifier.isPublic(TypeSummary.class.getModifiers()));
  }

  @Test
  public void classIsFinal() {
    assertTrue(Modifier.isFinal(TypeSummary.class.getModifiers()));
  }

  @Test
  public void simpleNameMatchesSource() {
    assertEquals("TypeSummary", TypeSummary.class.getSimpleName());
  }

  @Test
  public void packageNameMatchesSource() {
    assertEquals("org.alice.ide.ast.export.type", TypeSummary.class.getPackage().getName());
  }

  @Test
  public void currentVersionConstantMatchesSource() {
    assertEquals(3.1, TypeSummary.CURRENT_VERSION, 0.0);
  }

  @Test
  public void minimumVersionConstantMatchesSource() {
    assertEquals(3.1, TypeSummary.MINIMUM_ACCEPTABLE_VERSION, 0.0);
  }

  @Test
  public void manualConstructorExists() throws Exception {
    assertNotNull(TypeSummary.class.getDeclaredConstructor(double.class, String.class, List.class, ResourceInfo.class, List.class, List.class, List.class));
  }

  @Test
  public void namedUserTypeConstructorExists() throws Exception {
    assertNotNull(TypeSummary.class.getDeclaredConstructor(NamedUserType.class));
  }

  @Test
  public void manualConstructorRetainsVersion() {
    assertEquals(3.1, createManualSummary().getVersion(), 0.0);
  }

  @Test
  public void manualConstructorRetainsTypeName() {
    assertEquals("Hero", createManualSummary().getTypeName());
  }

  @Test
  public void manualConstructorRetainsHierarchyInstance() {
    List<String> hierarchy = new ArrayList<>(Collections.singletonList("Parent"));
    TypeSummary summary = new TypeSummary(3.1, "Manual", hierarchy, null, new ArrayList<String>(), new ArrayList<FunctionInfo>(), new ArrayList<FieldInfo>());
    assertSame(hierarchy, summary.getHierarchyClassNames());
  }

  @Test
  public void manualConstructorRetainsResourceInfoInstance() {
    ResourceInfo resourceInfo = new ResourceInfo("org.example.Resource", null);
    TypeSummary summary = new TypeSummary(3.1, "Manual", new ArrayList<String>(), resourceInfo, new ArrayList<String>(), new ArrayList<FunctionInfo>(), new ArrayList<FieldInfo>());
    assertSame(resourceInfo, summary.getResourceInfo());
  }

  @Test
  public void manualConstructorRetainsProcedureAndMemberLists() {
    List<String> procedures = new ArrayList<>(Collections.singletonList("spin"));
    List<FunctionInfo> functions = new ArrayList<>(Collections.singletonList(new FunctionInfo("java.lang.Boolean", "isReady")));
    List<FieldInfo> fields = new ArrayList<>(Collections.singletonList(new FieldInfo("java.lang.Integer", "score")));
    TypeSummary summary = new TypeSummary(3.1, "Manual", new ArrayList<String>(), null, procedures, functions, fields);
    assertSame(procedures, summary.getProcedureNames());
    assertSame(functions, summary.getFunctionInfos());
    assertSame(fields, summary.getFieldInfos());
  }

  @Test
  public void namedUserTypeConstructorUsesCurrentVersion() {
    assertEquals(TypeSummary.CURRENT_VERSION, new TypeSummary(createSummarizedType()).getVersion(), 0.0);
  }

  @Test
  public void namedUserTypeConstructorUsesTypeName() {
    assertEquals("HeroType", new TypeSummary(createSummarizedType()).getTypeName());
  }

  @Test
  public void namedUserTypeConstructorBuildsHierarchyFromUserAndJavaSupertypes() {
    List<String> hierarchy = new TypeSummary(createSummarizedType()).getHierarchyClassNames();
    assertEquals("ParentType", hierarchy.get(0));
    assertTrue(hierarchy.contains("java.lang.Object"));
  }

  @Test
  public void namedUserTypeConstructorCapturesProcedures() {
    assertTrue(new TypeSummary(createSummarizedType()).getProcedureNames().contains("jump"));
  }

  @Test
  public void namedUserTypeConstructorCapturesFunctions() {
    assertEquals("getScore", new TypeSummary(createSummarizedType()).getFunctionInfos().get(0).getName());
  }

  @Test
  public void namedUserTypeConstructorCapturesFields() {
    assertEquals("coins", new TypeSummary(createSummarizedType()).getFieldInfos().get(0).getName());
  }

  @Test
  public void generatedMethodsAreExcludedFromProcedureNames() {
    assertFalse(new TypeSummary(createSummarizedType()).getProcedureNames().contains("generatedOnly"));
  }

  @Test
  public void resourceInfoIsNullForPlainUserTypes() {
    assertNull(new TypeSummary(createSummarizedType()).getResourceInfo());
  }

  @Test
  public void unicodeTypeNamesAreRetainedByManualConstructor() {
    TypeSummary summary = new TypeSummary(3.1, "类型🚀", new ArrayList<String>(), null, new ArrayList<String>(), new ArrayList<FunctionInfo>(), new ArrayList<FieldInfo>());
    assertEquals("类型🚀", summary.getTypeName());
  }

  @Test
  public void negativeVersionsAreRetainedByManualConstructor() {
    assertEquals(-2.5, new TypeSummary(-2.5, "Manual", new ArrayList<String>(), null, new ArrayList<String>(), new ArrayList<FunctionInfo>(), new ArrayList<FieldInfo>()).getVersion(), 0.0);
  }

  @Test
  public void emptyListsRemainEmpty() {
    TypeSummary summary = new TypeSummary(3.1, "Empty", new ArrayList<String>(), null, new ArrayList<String>(), new ArrayList<FunctionInfo>(), new ArrayList<FieldInfo>());
    assertTrue(summary.getProcedureNames().isEmpty());
    assertTrue(summary.getFunctionInfos().isEmpty());
    assertTrue(summary.getFieldInfos().isEmpty());
  }

  @Test
  public void declaredConstructorCountIsTwo() {
    assertEquals(2, TypeSummary.class.getDeclaredConstructors().length);
  }

  @Test
  public void declaredMethodCountMatchesGetterSurface() {
    assertEquals(8, declaredMethods(TypeSummary.class).length);
  }
}

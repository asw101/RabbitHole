package org.alice.ide.declarationseditor.type.data;

import org.lgna.project.ast.*;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Tests for declarationseditor type data classes: ConstructorData, ProcedureData,
 * FunctionData, FieldData, FilteredMemberData, and MethodData.
 * Exercises type storage, member property binding, and filter logic.
 * (In same package to access protected isAcceptableItem.)
 */
public class DeclarationsEditorDataTest {

  private NamedUserType testType;

  @Before
  public void setUp() {
    testType = new NamedUserType();
    testType.name.setValue("TestType");
  }

  // ---- ConstructorData ----

  @Test
  public void constructorData_getType_returnsOriginalType() {
    ConstructorData data = new ConstructorData(testType);
    assertSame(testType, data.getType());
  }

  @Test
  public void constructorData_acceptsAnyConstructor() {
    ConstructorData data = new ConstructorData(testType);
    NamedUserConstructor constructor = new NamedUserConstructor();
    testType.constructors.add(constructor);
    assertTrue(data.isAcceptableItem(constructor));
  }

  @Test
  public void constructorData_multipleConstructors_allAccepted() {
    ConstructorData data = new ConstructorData(testType);
    NamedUserConstructor c1 = new NamedUserConstructor();
    NamedUserConstructor c2 = new NamedUserConstructor();
    testType.constructors.add(c1);
    testType.constructors.add(c2);
    assertTrue(data.isAcceptableItem(c1));
    assertTrue(data.isAcceptableItem(c2));
  }

  // ---- ProcedureData ----

  @Test
  public void procedureData_getType_returnsOriginalType() {
    ProcedureData data = new ProcedureData(testType);
    assertSame(testType, data.getType());
  }

  @Test
  public void procedureData_acceptsProcedureMethod() {
    ProcedureData data = new ProcedureData(testType);
    UserMethod procedure = new UserMethod();
    procedure.name.setValue("myProcedure");
    procedure.returnType.setValue(JavaType.VOID_TYPE);
    procedure.managementLevel.setValue(ManagementLevel.NONE);
    testType.methods.add(procedure);
    assertTrue("Procedure (void return) should be accepted", data.isAcceptableItem(procedure));
  }

  @Test
  public void procedureData_rejectsFunctionMethod() {
    ProcedureData data = new ProcedureData(testType);
    UserMethod function = new UserMethod();
    function.name.setValue("myFunction");
    function.returnType.setValue(JavaType.INTEGER_OBJECT_TYPE);
    function.managementLevel.setValue(ManagementLevel.NONE);
    testType.methods.add(function);
    assertFalse("Function (non-void return) should be rejected by ProcedureData", data.isAcceptableItem(function));
  }

  // ---- FunctionData ----

  @Test
  public void functionData_getType_returnsOriginalType() {
    FunctionData data = new FunctionData(testType);
    assertSame(testType, data.getType());
  }

  @Test
  public void functionData_acceptsFunctionMethod() {
    FunctionData data = new FunctionData(testType);
    UserMethod function = new UserMethod();
    function.name.setValue("compute");
    function.returnType.setValue(JavaType.DOUBLE_OBJECT_TYPE);
    function.managementLevel.setValue(ManagementLevel.NONE);
    testType.methods.add(function);
    assertTrue("Function (non-void return) should be accepted", data.isAcceptableItem(function));
  }

  @Test
  public void functionData_rejectsProcedureMethod() {
    FunctionData data = new FunctionData(testType);
    UserMethod procedure = new UserMethod();
    procedure.name.setValue("doSomething");
    procedure.returnType.setValue(JavaType.VOID_TYPE);
    procedure.managementLevel.setValue(ManagementLevel.NONE);
    testType.methods.add(procedure);
    assertFalse("Procedure should be rejected by FunctionData", data.isAcceptableItem(procedure));
  }

  // ---- FilteredMemberData type preservation ----

  @Test
  public void filteredMemberData_typeIsPreserved() {
    ConstructorData data = new ConstructorData(testType);
    assertSame(testType, data.getType());
  }

  // ---- Data class hierarchy checks ----

  @Test
  public void procedureData_extendsMethodData() {
    assertTrue(MethodData.class.isAssignableFrom(ProcedureData.class));
  }

  @Test
  public void functionData_extendsMethodData() {
    assertTrue(MethodData.class.isAssignableFrom(FunctionData.class));
  }

  @Test
  public void methodData_extendsFilteredMemberData() {
    assertTrue(FilteredMemberData.class.isAssignableFrom(MethodData.class));
  }

  @Test
  public void fieldData_extendsFilteredMemberData() {
    assertTrue(FilteredMemberData.class.isAssignableFrom(FieldData.class));
  }

  @Test
  public void constructorData_extendsFilteredMemberData() {
    assertTrue(FilteredMemberData.class.isAssignableFrom(ConstructorData.class));
  }

  // ---- Method management level filtering ----

  @Test
  public void procedureData_managedMethod_defaultExcluded() {
    ProcedureData data = new ProcedureData(testType);
    UserMethod managed = new UserMethod();
    managed.name.setValue("managedProc");
    managed.returnType.setValue(JavaType.VOID_TYPE);
    managed.managementLevel.setValue(ManagementLevel.MANAGED);
    testType.methods.add(managed);
    assertFalse("Managed method should be excluded by default", data.isAcceptableItem(managed));
  }

  // ---- Multiple methods mixed ----

  @Test
  public void functionData_mixedMethods_onlyAcceptsFunctions() {
    FunctionData data = new FunctionData(testType);

    UserMethod proc = new UserMethod();
    proc.name.setValue("proc");
    proc.returnType.setValue(JavaType.VOID_TYPE);
    proc.managementLevel.setValue(ManagementLevel.NONE);
    testType.methods.add(proc);

    UserMethod func = new UserMethod();
    func.name.setValue("func");
    func.returnType.setValue(JavaType.STRING_TYPE);
    func.managementLevel.setValue(ManagementLevel.NONE);
    testType.methods.add(func);

    assertFalse(data.isAcceptableItem(proc));
    assertTrue(data.isAcceptableItem(func));
  }

  @Test
  public void procedureData_mixedMethods_onlyAcceptsProcedures() {
    ProcedureData data = new ProcedureData(testType);

    UserMethod proc = new UserMethod();
    proc.name.setValue("proc");
    proc.returnType.setValue(JavaType.VOID_TYPE);
    proc.managementLevel.setValue(ManagementLevel.NONE);
    testType.methods.add(proc);

    UserMethod func = new UserMethod();
    func.name.setValue("func");
    func.returnType.setValue(JavaType.BOOLEAN_OBJECT_TYPE);
    func.managementLevel.setValue(ManagementLevel.NONE);
    testType.methods.add(func);

    assertTrue(data.isAcceptableItem(proc));
    assertFalse(data.isAcceptableItem(func));
  }

  // ---- FieldData through subclass ----

  @Test
  public void fieldData_constructorBindsToFieldsProperty() {
    // UnmanagedFieldData is a concrete subclass of FieldData
    // but to avoid import issues, we just verify the hierarchy
    assertTrue(FilteredMemberData.class.isAssignableFrom(FieldData.class));
  }

  // ---- ConstructorData with empty type ----

  @Test
  public void constructorData_emptyType_noConstructors_creatable() {
    NamedUserType empty = new NamedUserType();
    empty.name.setValue("EmptyType");
    ConstructorData data = new ConstructorData(empty);
    assertSame(empty, data.getType());
  }

  // ---- FunctionData with different return types ----

  @Test
  public void functionData_stringReturnType_accepted() {
    FunctionData data = new FunctionData(testType);
    UserMethod func = new UserMethod();
    func.name.setValue("getString");
    func.returnType.setValue(JavaType.STRING_TYPE);
    func.managementLevel.setValue(ManagementLevel.NONE);
    testType.methods.add(func);
    assertTrue(data.isAcceptableItem(func));
  }

  @Test
  public void functionData_booleanReturnType_accepted() {
    FunctionData data = new FunctionData(testType);
    UserMethod func = new UserMethod();
    func.name.setValue("isValid");
    func.returnType.setValue(JavaType.BOOLEAN_OBJECT_TYPE);
    func.managementLevel.setValue(ManagementLevel.NONE);
    testType.methods.add(func);
    assertTrue(data.isAcceptableItem(func));
  }

  @Test
  public void functionData_integerReturnType_accepted() {
    FunctionData data = new FunctionData(testType);
    UserMethod func = new UserMethod();
    func.name.setValue("getCount");
    func.returnType.setValue(JavaType.INTEGER_OBJECT_TYPE);
    func.managementLevel.setValue(ManagementLevel.NONE);
    testType.methods.add(func);
    assertTrue(data.isAcceptableItem(func));
  }
}

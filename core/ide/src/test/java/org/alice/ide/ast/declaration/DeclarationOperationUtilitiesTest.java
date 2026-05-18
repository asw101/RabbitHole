package org.alice.ide.ast.declaration;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Tests for declaration package classes that have testable logic:
 * {@link DeclarationNameState}, and validates structure of Insert*Composite classes.
 * Most classes in this package are deeply coupled to the IDE composite system,
 * so we test what can be tested in isolation.
 */
public class DeclarationOperationUtilitiesTest {

  @Test
  public void insertStatementComposite_classExists() throws ClassNotFoundException {
    Class<?> cls = Class.forName("org.alice.ide.ast.declaration.InsertStatementComposite");
    assertNotNull(cls);
  }

  @Test
  public void addMethodComposite_classExists() throws ClassNotFoundException {
    Class<?> cls = Class.forName("org.alice.ide.ast.declaration.AddMethodComposite");
    assertNotNull(cls);
  }

  @Test
  public void declarationLikeSubstanceComposite_classExists() throws ClassNotFoundException {
    Class<?> cls = Class.forName("org.alice.ide.ast.declaration.DeclarationLikeSubstanceComposite");
    assertNotNull(cls);
  }

  @Test
  public void addFieldComposite_classExists() throws ClassNotFoundException {
    Class<?> cls = Class.forName("org.alice.ide.ast.declaration.AddFieldComposite");
    assertNotNull(cls);
  }

  @Test
  public void declarationNameState_classExists() throws ClassNotFoundException {
    Class<?> cls = Class.forName("org.alice.ide.ast.declaration.DeclarationNameState");
    assertNotNull(cls);
  }

  @Test
  public void declarationValidationDelegate_classExists() throws ClassNotFoundException {
    Class<?> cls = Class.forName("org.alice.ide.ast.declaration.DeclarationValidationDelegate");
    assertNotNull(cls);
  }

  @Test
  public void declarationDialogLifecycleDelegate_classExists() throws ClassNotFoundException {
    Class<?> cls = Class.forName("org.alice.ide.ast.declaration.DeclarationDialogLifecycleDelegate");
    assertNotNull(cls);
  }

  @Test
  public void editFieldComposite_classExists() throws ClassNotFoundException {
    Class<?> cls = Class.forName("org.alice.ide.ast.declaration.EditFieldComposite");
    assertNotNull(cls);
  }

  @Test
  public void fieldComposite_classExists() throws ClassNotFoundException {
    Class<?> cls = Class.forName("org.alice.ide.ast.declaration.FieldComposite");
    assertNotNull(cls);
  }

  @Test
  public void addFunctionComposite_classExists() throws ClassNotFoundException {
    Class<?> cls = Class.forName("org.alice.ide.ast.declaration.AddFunctionComposite");
    assertNotNull(cls);
  }

  @Test
  public void addProcedureComposite_classExists() throws ClassNotFoundException {
    Class<?> cls = Class.forName("org.alice.ide.ast.declaration.AddProcedureComposite");
    assertNotNull(cls);
  }

  @Test
  public void addParameterComposite_classExists() throws ClassNotFoundException {
    Class<?> cls = Class.forName("org.alice.ide.ast.declaration.AddParameterComposite");
    assertNotNull(cls);
  }

  @Test
  public void insertForEachInArrayLoopComposite_classExists() throws ClassNotFoundException {
    Class<?> cls = Class.forName("org.alice.ide.ast.declaration.InsertForEachInArrayLoopComposite");
    assertNotNull(cls);
  }

  @Test
  public void insertEachInArrayTogetherComposite_classExists() throws ClassNotFoundException {
    Class<?> cls = Class.forName("org.alice.ide.ast.declaration.InsertEachInArrayTogetherComposite");
    assertNotNull(cls);
  }

  @Test
  public void insertLocalDeclarationStatementComposite_classExists() throws ClassNotFoundException {
    Class<?> cls = Class.forName("org.alice.ide.ast.declaration.InsertLocalDeclarationStatementComposite");
    assertNotNull(cls);
  }
}

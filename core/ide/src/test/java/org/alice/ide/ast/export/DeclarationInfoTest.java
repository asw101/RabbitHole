package org.alice.ide.ast.export;

import org.junit.Test;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import static org.junit.Assert.*;

public class DeclarationInfoTest {
  @Test
  public void isAbstract() {
    assertTrue(Modifier.isAbstract(DeclarationInfo.class.getModifiers()));
  }
  @Test
  public void hasProjectInfoField() throws Exception {
    Field f = DeclarationInfo.class.getDeclaredField("projectInfo");
    assertTrue(Modifier.isPrivate(f.getModifiers()));
    assertTrue(Modifier.isFinal(f.getModifiers()));
  }
  @Test
  public void hasDeclarationField() throws Exception {
    Field f = DeclarationInfo.class.getDeclaredField("declaration");
    assertTrue(Modifier.isPrivate(f.getModifiers()));
    assertTrue(Modifier.isFinal(f.getModifiers()));
  }
  @Test
  public void hasGetProjectInfoMethod() throws Exception {
    Method m = DeclarationInfo.class.getMethod("getProjectInfo");
    assertTrue(Modifier.isPublic(m.getModifiers()));
  }
  @Test
  public void hasGetDeclarationMethod() throws Exception {
    Method m = DeclarationInfo.class.getMethod("getDeclaration");
    assertTrue(Modifier.isPublic(m.getModifiers()));
  }
  @Test
  public void hasGetCheckBoxMethod() throws Exception {
    Method m = DeclarationInfo.class.getMethod("getCheckBox");
    assertTrue(Modifier.isPublic(m.getModifiers()));
  }
  @Test
  public void hasResetRequiredMethod() throws Exception {
    Method m = DeclarationInfo.class.getMethod("resetRequired");
    assertTrue(Modifier.isPublic(m.getModifiers()));
  }
  @Test
  public void hasUpdateRequiredMethod() throws Exception {
    Method m = DeclarationInfo.class.getMethod("updateRequired", java.util.Set.class);
    assertTrue(Modifier.isPublic(m.getModifiers()));
    assertTrue(Modifier.isFinal(m.getModifiers()));
  }
  @Test
  public void hasUpdateSwingMethod() throws Exception {
    Method m = DeclarationInfo.class.getMethod("updateSwing");
    assertTrue(Modifier.isPublic(m.getModifiers()));
  }
}

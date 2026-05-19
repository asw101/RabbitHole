package org.alice.ide.ast.export;

import org.junit.Test;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import static org.junit.Assert.*;

public class ProjectInfoTest {
  @Test
  public void isPublicConcrete() {
    int mods = ProjectInfo.class.getModifiers();
    assertTrue(Modifier.isPublic(mods));
    assertFalse(Modifier.isAbstract(mods));
  }
  @Test
  public void hasGetTypeInfosMethod() throws Exception {
    Method m = ProjectInfo.class.getMethod("getTypeInfos");
    assertTrue(Modifier.isPublic(m.getModifiers()));
  }
  @Test
  public void hasGetInfoForTypeMethod() throws Exception {
    Method m = ProjectInfo.class.getMethod("getInfoForType", org.lgna.project.ast.UserType.class);
    assertTrue(Modifier.isPublic(m.getModifiers()));
  }
  @Test
  public void hasGetTypeInfosAsTreeMethod() throws Exception {
    Method m = ProjectInfo.class.getMethod("getTypeInfosAsTree");
    assertTrue(Modifier.isPublic(m.getModifiers()));
  }
  @Test
  public void hasIsInTheMidstOfChangeMethod() throws Exception {
    Method m = ProjectInfo.class.getMethod("isInTheMidstOfChange");
    assertTrue(Modifier.isPublic(m.getModifiers()));
  }
  @Test
  public void hasUpdateMethod() throws Exception {
    Method m = ProjectInfo.class.getMethod("update");
    assertTrue(Modifier.isPublic(m.getModifiers()));
  }
}

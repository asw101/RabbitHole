package org.alice.stageide;

import org.junit.BeforeClass;
import org.junit.Test;
import org.lgna.project.reflect.ClassInfoManager;

import java.lang.reflect.Method;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

public class EntryPointTest {
  @BeforeClass
  public static void forceHeadlessMode() {
    System.setProperty("java.awt.headless", "true");
  }

  @Test
  public void mainFailsFastBeforeStartingDesktopUiInHeadlessMode() {
    try {
      EntryPoint.main(new String[0]);
      fail("Headless launch should fail before Swing or JavaFX startup");
    } catch (IllegalStateException ise) {
      assertEquals("Alice desktop launch requires a graphical environment.", ise.getMessage());
    }
  }

  @Test
  public void loadClassInfosRegistersKnownClassInfoEntries() throws Exception {
    Method method = EntryPoint.class.getDeclaredMethod("loadClassInfos");
    method.setAccessible(true);

    method.invoke(null);

    assertNotNull(ClassInfoManager.getInstance("org.lgna.story.SQuadruped"));
    assertNotNull(ClassInfoManager.getInstance("org.lgna.story.StraightenOutJoints"));
  }

  @Test
  public void startAllowsNullPrimaryStageBecauseItIsCurrentlyANoOp() throws Exception {
    new EntryPoint().start(null);
  }
}

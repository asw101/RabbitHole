package org.alice.stageide;

import org.junit.BeforeClass;
import org.junit.Test;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class EntryPointDetailTest {

  @BeforeClass
  public static void forceHeadless() {
    System.setProperty("java.awt.headless", "true");
  }

  @Test
  public void requireGraphicalEnvironmentThrowsWhenHeadless() {
    try {
      EntryPoint.requireGraphicalEnvironmentForDesktopLaunch(true);
      fail("Should throw for headless");
    } catch (IllegalStateException e) {
      assertEquals("Alice desktop launch requires a graphical environment.", e.getMessage());
    }
  }

  @Test
  public void requireGraphicalEnvironmentPassesWhenNotHeadless() {
    EntryPoint.requireGraphicalEnvironmentForDesktopLaunch(false);
  }

  @Test
  public void mainMethodIsPublicStaticVoid() throws NoSuchMethodException {
    Method main = EntryPoint.class.getMethod("main", String[].class);
    assertTrue(Modifier.isStatic(main.getModifiers()));
    assertTrue(Modifier.isPublic(main.getModifiers()));
    assertEquals(void.class, main.getReturnType());
  }

  @Test
  public void entryPointExtendsJavaFxApplication() {
    assertEquals("javafx.application.Application", EntryPoint.class.getSuperclass().getName());
  }

  @Test
  public void loadClassInfosIsPrivateStatic() throws NoSuchMethodException {
    Method m = EntryPoint.class.getDeclaredMethod("loadClassInfos");
    assertTrue(Modifier.isPrivate(m.getModifiers()));
    assertTrue(Modifier.isStatic(m.getModifiers()));
  }

  @Test
  public void requireGraphicalEnvironmentIsPackagePrivateStatic() throws NoSuchMethodException {
    Method m = EntryPoint.class.getDeclaredMethod("requireGraphicalEnvironmentForDesktopLaunch", boolean.class);
    assertTrue(Modifier.isStatic(m.getModifiers()));
    assertNotNull(m);
  }

  @Test
  public void classInfosJsonResourceExists() {
    assertNotNull(EntryPoint.class.getResourceAsStream("classinfos.json"));
  }

  @Test
  public void mainWithEmptyArgsThrowsInHeadless() {
    try {
      EntryPoint.main(new String[0]);
      fail("Expected IllegalStateException in headless mode");
    } catch (IllegalStateException e) {
      assertTrue(e.getMessage().contains("graphical environment"));
    }
  }

  @Test
  public void mainWithArgsThrowsInHeadless() {
    try {
      EntryPoint.main(new String[]{"project.a3p"});
      fail("Expected IllegalStateException in headless mode");
    } catch (IllegalStateException e) {
      assertTrue(e.getMessage().contains("graphical environment"));
    }
  }
}

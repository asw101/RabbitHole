package edu.cmu.cs.dennisc.java.util;

import org.junit.Assert;
import org.junit.Test;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Locale;
import java.util.ResourceBundle;

public class ResourceBundleUtilitiesTest {
  private static final String BASE_NAME = "edu.cmu.cs.dennisc.java.util.resourcebundleutilitiestest";

  private static class ParentType {
  }

  private static class ChildType extends ParentType {
  }

  @Test
  public void classIsAbstractWithPrivateConstructor() throws Exception {
    Constructor<ResourceBundleUtilities> constructor = ResourceBundleUtilities.class.getDeclaredConstructor();

    Assert.assertTrue(java.lang.reflect.Modifier.isAbstract(ResourceBundleUtilities.class.getModifiers()));
    Assert.assertTrue(java.lang.reflect.Modifier.isPrivate(constructor.getModifiers()));
  }

  @Test
  public void getUtf8BundleLoadsUtf8Text() {
    ResourceBundle bundle = ResourceBundleUtilities.getUtf8Bundle(BASE_NAME, Locale.ROOT);
    Assert.assertEquals("Grüße", bundle.getString("greeting"));
  }

  @Test
  public void innerControlSupportsReloadAndNonReloadPaths() throws Exception {
    Class<?> controlClass = Class.forName("edu.cmu.cs.dennisc.java.util.ResourceBundleUtilities$Utf8ResourceBundleControl");
    Constructor<?> constructor = controlClass.getDeclaredConstructor();
    constructor.setAccessible(true);
    ResourceBundle.Control control = (ResourceBundle.Control) constructor.newInstance();

    ResourceBundle cached = control.newBundle(BASE_NAME, Locale.ROOT, "java.properties", getClass().getClassLoader(), false);
    ResourceBundle reloaded = control.newBundle(BASE_NAME, Locale.ROOT, "java.properties", getClass().getClassLoader(), true);

    Assert.assertEquals("Grüße", cached.getString("greeting"));
    Assert.assertEquals("Grüße", reloaded.getString("greeting"));
  }

  @Test
  public void getStringForKeyReturnsKeyWhenMissing() {
    Assert.assertEquals("missing-key", ResourceBundleUtilities.getStringForKey("missing-key", BASE_NAME));
  }

  @Test
  public void getStringFromSimpleNamesFallsBackToSuperclass() {
    Assert.assertEquals("parent-value", ResourceBundleUtilities.getStringFromSimpleNames(ChildType.class, BASE_NAME));
  }
}

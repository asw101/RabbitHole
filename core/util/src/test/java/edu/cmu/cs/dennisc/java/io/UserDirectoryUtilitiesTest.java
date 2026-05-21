package edu.cmu.cs.dennisc.java.io;

import edu.cmu.cs.dennisc.java.lang.SystemUtilities;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;

public class UserDirectoryUtilitiesTest {
  @Test
  public void constructorThrowsAssertionError() throws Exception {
    Constructor<UserDirectoryUtilities> constructor = UserDirectoryUtilities.class.getDeclaredConstructor();
    constructor.setAccessible(true);

    try {
      constructor.newInstance();
      Assert.fail("Expected constructor to throw");
    } catch (InvocationTargetException ite) {
      Assert.assertTrue(ite.getCause() instanceof AssertionError);
    }
  }

  @Test
  public void getBestGuessPicturesDirectoryReturnsPicturesForLinuxLikeLayout() throws Exception {
    Field defaultDirectoryField = FileUtilities.class.getDeclaredField("s_defaultDirectory");
    defaultDirectoryField.setAccessible(true);
    Object previousDirectory = defaultDirectoryField.get(null);
    Object previousPlatform = getPlatformField().get(null);

    try {
      File defaultDirectory = new File("target/test-artifacts/UserDirectoryUtilitiesTest/linux-default");
      File pictures = new File(defaultDirectory, "Pictures");
      pictures.mkdirs();
      defaultDirectoryField.set(null, defaultDirectory);
      setPlatform("LINUX");

      Assert.assertEquals(pictures.getCanonicalFile(), UserDirectoryUtilities.getBestGuessPicturesDirectory().getCanonicalFile());
    } finally {
      defaultDirectoryField.set(null, previousDirectory);
      getPlatformField().set(null, previousPlatform);
    }
  }

  @Test
  public void getBestGuessPicturesDirectoryUsesParentOnWindows() throws Exception {
    Field defaultDirectoryField = FileUtilities.class.getDeclaredField("s_defaultDirectory");
    defaultDirectoryField.setAccessible(true);
    Object previousDirectory = defaultDirectoryField.get(null);
    Object previousPlatform = getPlatformField().get(null);

    try {
      File userDirectory = new File("target/test-artifacts/UserDirectoryUtilitiesTest/windows-user");
      File defaultDirectory = new File(userDirectory, "Documents");
      File pictures = new File(userDirectory, "Pictures");
      defaultDirectory.mkdirs();
      pictures.mkdirs();
      defaultDirectoryField.set(null, defaultDirectory);
      setPlatform("WINDOWS");

      Assert.assertEquals(pictures.getCanonicalFile(), UserDirectoryUtilities.getBestGuessPicturesDirectory().getCanonicalFile());
    } finally {
      defaultDirectoryField.set(null, previousDirectory);
      getPlatformField().set(null, previousPlatform);
    }
  }

  @Test
  public void getBestGuessPicturesDirectoryFallsBackToDefaultDirectory() throws Exception {
    Field defaultDirectoryField = FileUtilities.class.getDeclaredField("s_defaultDirectory");
    defaultDirectoryField.setAccessible(true);
    Object previousDirectory = defaultDirectoryField.get(null);
    Object previousPlatform = getPlatformField().get(null);

    try {
      File defaultDirectory = new File("target/test-artifacts/UserDirectoryUtilitiesTest/no-pictures");
      defaultDirectory.mkdirs();
      defaultDirectoryField.set(null, defaultDirectory);
      setPlatform("LINUX");

      Assert.assertEquals(defaultDirectory.getCanonicalFile(), UserDirectoryUtilities.getBestGuessPicturesDirectory().getCanonicalFile());
    } finally {
      defaultDirectoryField.set(null, previousDirectory);
      getPlatformField().set(null, previousPlatform);
    }
  }

  private static Field getPlatformField() throws Exception {
    Field field = SystemUtilities.class.getDeclaredField("platform");
    field.setAccessible(true);
    return field;
  }

  private static void setPlatform(String name) throws Exception {
    Class<?> platformClass = Class.forName("edu.cmu.cs.dennisc.java.lang.SystemUtilities$Platform");
    @SuppressWarnings({"unchecked", "rawtypes"})
    Object value = Enum.valueOf((Class<Enum>) platformClass, name);
    getPlatformField().set(null, value);
  }
}

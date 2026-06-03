package edu.cmu.cs.dennisc.app;

import edu.cmu.cs.dennisc.java.lang.SystemUtilities;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;

import static org.junit.Assert.*;

public class ApplicationRootTest {
  private static final String ROOT_PROPERTY = "org.alice.ide.rootDirectory";
  private String originalRootProperty;

  @Before
  public void setUp() throws Exception {
    this.originalRootProperty = System.getProperty(ROOT_PROPERTY);
    resetRootDirectory();
  }

  @After
  public void tearDown() throws Exception {
    if (this.originalRootProperty != null) {
      System.setProperty(ROOT_PROPERTY, this.originalRootProperty);
    } else {
      System.clearProperty(ROOT_PROPERTY);
    }
    resetRootDirectory();
  }

  private void resetRootDirectory() throws Exception {
    Field field = ApplicationRoot.class.getDeclaredField("rootDirectory");
    field.setAccessible(true);
    field.set(null, null);
  }

  private File createRoot(String name) {
    File directory = new File("target/test-artifacts/ApplicationRootTest/" + name);
    directory.mkdirs();
    return directory.getAbsoluteFile();
  }

  private String expectedArchitectureSegment() {
    if (SystemUtilities.isMac()) {
      return "macosx";
    }
    Integer bitCount = SystemUtilities.getBitCount();
    if (SystemUtilities.isWindows()) {
      return "win" + bitCount;
    }
    if (SystemUtilities.isAarch64Architecture()) {
      return "linux-aarch64";
    }
    if (SystemUtilities.isArmArchitecture()) {
      return "linux-armv6hf";
    }
    return bitCount == 32 ? "linux-i586" : "linux-amd64";
  }

  private String expectedJoglSubDirectory() {
    if (SystemUtilities.isMac()) {
      return "natives/macosx-universal/";
    }
    if (SystemUtilities.isAarch64Architecture()) {
      return "natives/linux-aarch64/";
    }
    if (SystemUtilities.isArmArchitecture()) {
      return "natives/linux-armv6hf/";
    }
    if (SystemUtilities.isWindows()) {
      return SystemUtilities.is32Bit() ? "natives/windows-i586/" : "natives/windows-amd64/";
    }
    return SystemUtilities.is32Bit() ? "natives/linux-i586/" : "natives/linux-amd64/";
  }

  @Test
  public void constructorThrowsAssertionError() throws Exception {
    Constructor<ApplicationRoot> constructor = ApplicationRoot.class.getDeclaredConstructor();
    constructor.setAccessible(true);

    try {
      constructor.newInstance();
      fail();
    } catch (InvocationTargetException ite) {
      assertTrue(ite.getCause() instanceof AssertionError);
    }
  }

  @Test
  public void getRootDirectoryUsesConfiguredProperty() {
    File root = createRoot("configured-root");
    System.setProperty(ROOT_PROPERTY, root.getAbsolutePath());
    assertEquals(root, ApplicationRoot.getRootDirectory());
  }

  @Test
  public void getPlatformDirectoryIsUnderRootDirectory() {
    File root = createRoot("platform-root");
    System.setProperty(ROOT_PROPERTY, root.getAbsolutePath());
    assertEquals(new File(root, "platform"), ApplicationRoot.getPlatformDirectory());
  }

  @Test
  public void getArchitectureSpecificDirectoryIsUnderPlatformDirectory() {
    File root = createRoot("architecture-root");
    System.setProperty(ROOT_PROPERTY, root.getAbsolutePath());
    File directory = ApplicationRoot.getArchitectureSpecificDirectory();

    assertTrue(directory.getPath().startsWith(new File(root, "platform").getPath()));
    assertTrue(directory.getPath().contains(expectedArchitectureSegment()));
  }

  @Test
  public void getArchitectureSpecificJoglSubDirectoryMatchesCurrentPlatform() {
    assertEquals(expectedJoglSubDirectory(), ApplicationRoot.getArchitectureSpecificJoglSubDirectory());
  }

  @Test
  public void getRootDirectoryCachesFirstInitialization() {
    File first = createRoot("first-root");
    File second = createRoot("second-root");
    System.setProperty(ROOT_PROPERTY, first.getAbsolutePath());
    assertEquals(first, ApplicationRoot.getRootDirectory());

    System.setProperty(ROOT_PROPERTY, second.getAbsolutePath());
    assertEquals(first, ApplicationRoot.getRootDirectory());
  }

  @Test
  public void initializeIfNecessarySetsRootDirectory() {
    File root = createRoot("initialize-root");
    System.setProperty(ROOT_PROPERTY, root.getAbsolutePath());
    ApplicationRoot.initializeIfNecessary();
    assertEquals(root, ApplicationRoot.getRootDirectory());
  }

  @Test
  public void architectureSpecificDirectoryUsesExpectedLeafName() {
    File root = createRoot("leaf-root");
    System.setProperty(ROOT_PROPERTY, root.getAbsolutePath());
    assertTrue(ApplicationRoot.getArchitectureSpecificDirectory().getPath().contains(expectedArchitectureSegment()));
  }

  private String expectedMissingPropertyMessage() {
    return "system property: " + ROOT_PROPERTY + " is not set.\nAlice will not work until this is addressed.";
  }

  private String expectedInvalidPathMessage(File path) {
    return "system property: " + ROOT_PROPERTY + " is incorrectly set.\n"
        + path.getAbsoluteFile()
        + " does not exist.\nAlice will not work until this is addressed.";
  }

  @Test
  public void initializeIfNecessaryWithMissingPropertyThrowsInitializationException() throws Exception {
    System.clearProperty(ROOT_PROPERTY);
    resetRootDirectory();

    ApplicationRootInitializationException exception = assertThrows(ApplicationRootInitializationException.class,
        ApplicationRoot::initializeIfNecessary);

    Field field = ApplicationRoot.class.getDeclaredField("rootDirectory");
    field.setAccessible(true);
    assertNull(field.get(null));
    assertEquals(expectedMissingPropertyMessage(), exception.getMessage());
  }

  @Test
  public void initializeIfNecessaryWithInvalidPathThrowsInitializationException() throws Exception {
    File missingRoot = new File("target/test-artifacts/ApplicationRootTest/does-not-exist").getAbsoluteFile();
    System.setProperty(ROOT_PROPERTY, missingRoot.getAbsolutePath());
    resetRootDirectory();

    ApplicationRootInitializationException exception = assertThrows(ApplicationRootInitializationException.class,
        ApplicationRoot::initializeIfNecessary);

    Field field = ApplicationRoot.class.getDeclaredField("rootDirectory");
    field.setAccessible(true);
    assertNull(field.get(null));
    assertEquals(expectedInvalidPathMessage(missingRoot), exception.getMessage());
  }
}

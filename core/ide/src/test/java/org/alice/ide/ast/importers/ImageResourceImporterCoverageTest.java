package org.alice.ide.ast.importers;

import edu.cmu.cs.dennisc.image.ImageUtilities;
import edu.cmu.cs.dennisc.java.io.FileUtilities;
import org.junit.Test;
import org.lgna.croquet.importer.Importer;

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Set;

import static org.junit.Assert.*;

public class ImageResourceImporterCoverageTest {
  @Test
  public void getInstance_returnsSingleton() {
    assertSame(ImageResourceImporter.getInstance(), ImageResourceImporter.getInstance());
  }

  @Test
  public void extendsImporter() {
    assertTrue(Importer.class.isAssignableFrom(ImageResourceImporter.class));
  }

  @Test
  public void constructorIsPrivate() throws Exception {
    Constructor<?> constructor = ImageResourceImporter.class.getDeclaredConstructor();
    assertTrue(Modifier.isPrivate(constructor.getModifiers()));
  }

  @Test
  public void initialDirectory_matchesDefaultDirectory() throws Exception {
    Field field = Importer.class.getDeclaredField("initialDirectory");
    field.setAccessible(true);
    File initialDirectory = (File) field.get(ImageResourceImporter.getInstance());
    assertEquals(FileUtilities.getDefaultDirectory(), initialDirectory);
  }

  @Test
  public void lowerCaseExtensions_matchImageUtilitiesExtensions() throws Exception {
    Field field = Importer.class.getDeclaredField("lowerCaseExtensions");
    field.setAccessible(true);
    Set<?> extensions = (Set<?>) field.get(ImageResourceImporter.getInstance());
    assertEquals(ImageUtilities.getFileExtensions(), extensions);
  }
}

package org.alice.ide.ast.importers;

import org.junit.Test;
import org.lgna.common.resources.AudioResource;
import org.lgna.croquet.importer.Importer;
import org.lgna.story.implementation.StoryApiDirectoryUtilities;

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Set;

import static org.junit.Assert.*;

public class AudioResourceImporterCoverageTest {
  @Test
  public void getInstance_returnsSingleton() {
    assertSame(AudioResourceImporter.getInstance(), AudioResourceImporter.getInstance());
  }

  @Test
  public void extendsImporter() {
    assertTrue(Importer.class.isAssignableFrom(AudioResourceImporter.class));
  }

  @Test
  public void constructorIsPrivate() throws Exception {
    Constructor<?> constructor = AudioResourceImporter.class.getDeclaredConstructor();
    assertTrue(Modifier.isPrivate(constructor.getModifiers()));
  }

  @Test
  public void initialDirectory_matchesSoundGalleryDirectory() throws Exception {
    Field field = Importer.class.getDeclaredField("initialDirectory");
    field.setAccessible(true);
    File initialDirectory = (File) field.get(AudioResourceImporter.getInstance());
    assertEquals(StoryApiDirectoryUtilities.getSoundGalleryDirectory(), initialDirectory);
  }

  @Test
  public void lowerCaseExtensions_matchAudioResourceExtensions() throws Exception {
    Field field = Importer.class.getDeclaredField("lowerCaseExtensions");
    field.setAccessible(true);
    Set<?> extensions = (Set<?>) field.get(AudioResourceImporter.getInstance());
    assertEquals(AudioResource.getFileExtensions(), extensions);
  }
}

package org.alice.ide.croquet.models.gallerybrowser;

import org.junit.Test;

import java.lang.reflect.Modifier;

import static org.junit.Assert.*;

public class GalleryBrowserPackageTest {
  @Test
  public void representativeClassesRemainInPackage() {
    assertEquals("org.alice.ide.croquet.models.gallerybrowser", GalleryDragModel.class.getPackage().getName());
    assertEquals("org.alice.ide.croquet.models.gallerybrowser", GalleryDragModel.class.getPackage().getName());
  }

  @Test
  public void representativeClassesRemainConcreteTypes() {
    assertFalse(Modifier.isInterface(GalleryDragModel.class.getModifiers()));
    assertFalse(Modifier.isInterface(GalleryDragModel.class.getModifiers()));
  }
}

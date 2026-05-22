package org.alice.ide.typemanager;

import org.junit.Test;
import org.lgna.project.ast.JavaType;
import org.lgna.project.ast.NamedUserType;
import org.lgna.story.SBiped;
import org.lgna.story.SCamera;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class TypeManagerSuperTypeCollectionTest {
  @Test
  public void getNamedUserTypesFromSuperTypesPreservesOrderAndNames() {
    List<NamedUserType> types = TypeManager.getNamedUserTypesFromSuperTypes(Arrays.asList(
        JavaType.getInstance(SCamera.class),
        JavaType.getInstance(SBiped.class)));

    assertEquals(2, types.size());
    assertEquals("Camera", types.get(0).getName());
    assertEquals("Biped", types.get(1).getName());
  }
}

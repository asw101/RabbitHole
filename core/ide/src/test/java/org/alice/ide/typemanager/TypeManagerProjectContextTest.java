package org.alice.ide.typemanager;

import org.alice.ide.testing.ProjectContextTestCase;
import org.junit.Test;
import org.lgna.project.ast.JavaType;
import org.lgna.project.ast.NamedUserType;
import org.lgna.story.SScene;

import java.util.List;

import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

public class TypeManagerProjectContextTest extends ProjectContextTestCase {
  @Test
  public void getNamedUserTypeFromSuperTypeReusesLoadedSceneType() {
    NamedUserType type = TypeManager.getNamedUserTypeFromSuperType(JavaType.getInstance(SScene.class));
    assertSame(fixture.sceneType, type);
  }

  @Test
  public void getNamedUserTypesFromSuperTypesIncludesLoadedSceneType() {
    List<NamedUserType> types = TypeManager.getNamedUserTypesFromSuperTypes(List.of(JavaType.getInstance(SScene.class)));
    assertTrue(types.contains(fixture.sceneType));
  }
}

package org.alice.ide.project;

import org.alice.ide.ProjectDocument;
import org.junit.Test;
import org.lgna.croquet.ItemState;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.util.List;

import static org.junit.Assert.*;

public class ProjectDocumentStateStructureTest {
  @Test
  public void classExtendsItemStateOfProjectDocument() {
    ParameterizedType type = (ParameterizedType) ProjectDocumentState.class.getGenericSuperclass();
    assertEquals(ItemState.class, type.getRawType());
    assertEquals(ProjectDocument.class, type.getActualTypeArguments()[0]);
  }

  @Test
  public void constructorIsPrivateSingletonStyle() throws Exception {
    Constructor<ProjectDocumentState> constructor = ProjectDocumentState.class.getDeclaredConstructor();
    assertTrue(Modifier.isPrivate(constructor.getModifiers()));
  }

  @Test
  public void keyMethodsMatchExpectedSignaturesAndSingletonBehavior() throws Exception {
    Method getInstance = ProjectDocumentState.class.getMethod("getInstance");
    Method getPotentialPrepModelPaths = ProjectDocumentState.class.getMethod("getPotentialPrepModelPaths", org.lgna.croquet.edits.Edit.class);

    assertTrue(Modifier.isStatic(getInstance.getModifiers()));
    assertEquals(ProjectDocumentState.class, getInstance.getReturnType());
    assertEquals(List.class, getPotentialPrepModelPaths.getReturnType());

    ProjectDocumentState state = ProjectDocumentState.getInstance();
    assertSame(state, ProjectDocumentState.getInstance());
    assertTrue(state.getPotentialPrepModelPaths(null).isEmpty());
  }
}

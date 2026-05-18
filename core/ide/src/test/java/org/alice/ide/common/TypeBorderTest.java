package org.alice.ide.common;

import org.junit.Assume;
import org.junit.Test;
import org.lgna.project.ast.AstUtilities;
import org.lgna.project.ast.JavaType;
import org.lgna.project.ast.NamedUserType;
import org.lgna.story.SScene;

import java.awt.GraphicsEnvironment;
import java.awt.Insets;

import static org.junit.Assert.*;

public class TypeBorderTest {

  private static NamedUserType createUserType() {
    return AstUtilities.createType("Scene", JavaType.getInstance(SScene.class));
  }

  @Test
  public void getSingletonFor_returnsStableNullSingleton() {
    Assume.assumeFalse(GraphicsEnvironment.isHeadless());
    assertSame(TypeBorder.getSingletonFor(null), TypeBorder.getSingletonFor(null));
  }

  @Test
  public void getSingletonForUserType_matchesDedicatedUserSingleton() {
    Assume.assumeFalse(GraphicsEnvironment.isHeadless());
    assertSame(TypeBorder.getSingletonForUserType(), TypeBorder.getSingletonFor(createUserType()));
  }

  @Test
  public void getSingletonFor_distinguishesUserAndJavaTypes() {
    Assume.assumeFalse(GraphicsEnvironment.isHeadless());
    assertNotSame(TypeBorder.getSingletonFor(createUserType()), TypeBorder.getSingletonFor(JavaType.getInstance(String.class)));
  }

  @Test
  public void getBorderInsets_returnsExpectedInsets() {
    Assume.assumeFalse(GraphicsEnvironment.isHeadless());
    Insets insets = TypeBorder.getSingletonFor(JavaType.getInstance(String.class)).getBorderInsets(null);
    assertEquals(new Insets(2, 8, 2, 8), insets);
  }

  @Test
  public void isBorderOpaque_returnsFalse() {
    Assume.assumeFalse(GraphicsEnvironment.isHeadless());
    assertFalse(TypeBorder.getSingletonFor(JavaType.getInstance(String.class)).isBorderOpaque());
  }
}

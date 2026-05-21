package org.alice.ide.templates;

import org.junit.Test;

import java.lang.reflect.Modifier;

import static org.junit.Assert.*;

/**
 * Structural tests for template classes.
 */
public class TemplateStructureTest {

  @Test
  public void statementTemplate_classIsAccessible() throws ClassNotFoundException {
    assertNotNull(Class.forName("org.alice.ide.templates.StatementTemplate"));
  }

  @Test
  public void statementTemplate_isPublic() throws ClassNotFoundException {
    assertTrue(Modifier.isPublic(Class.forName("org.alice.ide.templates.StatementTemplate").getModifiers()));
  }

  @Test
  public void statementTemplate_isAbstract() throws ClassNotFoundException {
    assertTrue(Modifier.isAbstract(Class.forName("org.alice.ide.templates.StatementTemplate").getModifiers()));
  }

  @Test
  public void expressionTemplate_classIsAccessible() throws ClassNotFoundException {
    assertNotNull(Class.forName("org.alice.ide.templates.ExpressionTemplate"));
  }

  @Test
  public void expressionTemplate_isPublic() throws ClassNotFoundException {
    assertTrue(Modifier.isPublic(Class.forName("org.alice.ide.templates.ExpressionTemplate").getModifiers()));
  }

  @Test
  public void expressionTemplate_isAbstract() throws ClassNotFoundException {
    assertTrue(Modifier.isAbstract(Class.forName("org.alice.ide.templates.ExpressionTemplate").getModifiers()));
  }

  @Test
  public void templates_areDistinctClasses() throws ClassNotFoundException {
    assertNotEquals(
      Class.forName("org.alice.ide.templates.StatementTemplate"),
      Class.forName("org.alice.ide.templates.ExpressionTemplate"));
  }

  @Test
  public void allTemplates_arePublic() throws ClassNotFoundException {
    assertTrue(Modifier.isPublic(Class.forName("org.alice.ide.templates.StatementTemplate").getModifiers()));
    assertTrue(Modifier.isPublic(Class.forName("org.alice.ide.templates.ExpressionTemplate").getModifiers()));
  }
}

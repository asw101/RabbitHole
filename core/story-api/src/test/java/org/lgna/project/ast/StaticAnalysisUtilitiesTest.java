package org.lgna.project.ast;

import org.junit.Test;
import org.lgna.common.Resource;
import org.lgna.project.Project;

import java.util.Collections;
import java.util.UUID;

import static org.junit.Assert.*;

public class StaticAnalysisUtilitiesTest {
  @Test
  public void validIdentifierChecksCommonCases() {
    assertFalse(StaticAnalysisUtilities.isValidIdentifier(null));
    assertFalse(StaticAnalysisUtilities.isValidIdentifier(""));
    assertFalse(StaticAnalysisUtilities.isValidIdentifier("1abc"));
    assertFalse(StaticAnalysisUtilities.isValidIdentifier("bad-name"));
    assertTrue(StaticAnalysisUtilities.isValidIdentifier("_good9"));
  }

  @Test
  public void conventionalClassNameNormalizesWhitespacePunctuationAndDigits() {
    assertEquals("HelloWorld", StaticAnalysisUtilities.getConventionalClassName("hello world"));
    assertEquals("_123GoNow", StaticAnalysisUtilities.getConventionalClassName("123 go-now"));
    assertEquals("CamelCase", StaticAnalysisUtilities.getConventionalClassName("camel_case"));
  }

  @Test
  public void availableResourceNameRejectsCaseInsensitiveCollisionsButAllowsSelf() {
    NamedUserType programType = new NamedUserType();
    programType.name.setValue("Program");
    programType.superType.setValue(JavaType.OBJECT_TYPE);

    TestResource alpha = new TestResource("Alpha.png");
    TestResource beta = new TestResource("Beta.png");
    Project project = new Project(programType, Collections.singleton(programType), Collections.singleton(alpha), Project.SceneCameraType.WindowCamera);
    project.addResource(beta);

    assertFalse(StaticAnalysisUtilities.isAvailableResourceName(project, "alpha.png", null));
    assertTrue(StaticAnalysisUtilities.isAvailableResourceName(project, "alpha.png", alpha));
    assertTrue(StaticAnalysisUtilities.isAvailableResourceName(project, "gamma.png", null));
    assertTrue(StaticAnalysisUtilities.isAvailableResourceName(null, "anything", null));
  }

  @Test
  public void availableFieldNameRejectsDuplicatesButAllowsSelf() {
    NamedUserType declaringType = new NamedUserType();
    declaringType.name.setValue("Program");
    declaringType.superType.setValue(JavaType.OBJECT_TYPE);
    UserField existing = new UserField("score", JavaType.getInstance(Integer.class), null);
    UserField other = new UserField("name", JavaType.getInstance(String.class), null);
    declaringType.fields.add(existing);
    declaringType.fields.add(other);

    assertFalse(StaticAnalysisUtilities.isAvailableFieldName("score", declaringType));
    assertTrue(StaticAnalysisUtilities.isAvailableFieldName("score", existing));
    assertTrue(StaticAnalysisUtilities.isAvailableFieldName("level", declaringType));
  }

  @Test
  public void availableMethodNameRejectsDuplicatesButAllowsSelf() {
    NamedUserType declaringType = new NamedUserType();
    declaringType.name.setValue("Program");
    declaringType.superType.setValue(JavaType.OBJECT_TYPE);
    UserMethod existing = new UserMethod("move", Void.TYPE, new UserParameter[0], new BlockStatement());
    UserMethod other = new UserMethod("turn", Void.TYPE, new UserParameter[0], new BlockStatement());
    declaringType.methods.add(existing);
    declaringType.methods.add(other);

    assertFalse(StaticAnalysisUtilities.isAvailableMethodName("move", declaringType));
    assertTrue(StaticAnalysisUtilities.isAvailableMethodName("move", existing));
    assertTrue(StaticAnalysisUtilities.isAvailableMethodName("say", declaringType));
  }

  @Test
  public void userTypeDepthCountsOnlyUserTypes() {
    NamedUserType base = new NamedUserType();
    base.name.setValue("Base");
    base.superType.setValue(JavaType.OBJECT_TYPE);

    NamedUserType child = new NamedUserType();
    child.name.setValue("Child");
    child.superType.setValue(base);

    assertEquals(-1, StaticAnalysisUtilities.getUserTypeDepth(null));
    assertEquals(-1, StaticAnalysisUtilities.getUserTypeDepth(JavaType.getInstance(String.class)));
    assertEquals(0, StaticAnalysisUtilities.getUserTypeDepth(base));
    assertEquals(1, StaticAnalysisUtilities.getUserTypeDepth(child));
  }

  private static final class TestResource extends Resource {
    private TestResource(String name) {
      super(UUID.randomUUID());
      setOriginalFileName(name);
      setName(name);
      setContent("text/plain", new byte[] {1});
    }
  }
}

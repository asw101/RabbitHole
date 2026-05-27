package org.lgna.project.ast;

import edu.cmu.cs.dennisc.java.lang.ParameterAnnotation;
import edu.cmu.cs.dennisc.property.StringProperty;
import org.junit.Test;
import org.lgna.common.Resource;
import org.lgna.project.annotations.ValueDetails;
import org.lgna.project.annotations.ValueTemplate;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.UUID;

import static org.junit.Assert.*;

public class Round88AstCoverageTest {
  private enum NumberDetails implements ValueDetails<Number> {
    DEFAULT;

    @Override
    public Class<Number> getSupportedCls() {
      return Number.class;
    }
  }

  @SuppressWarnings("unused")
  private static final class ParameterFixture {
    public void templated(@ParameterAnnotation(isVariable = true) @ValueTemplate(detailsEnumCls = NumberDetails.class) Number value, String plain) {
    }
  }

  @SuppressWarnings("unused")
  private static final class BeanFixture {
    public int getValue() {
      return 1;
    }

    public void setValue(int value) {
    }
  }

  private static final class TestJavaParameter extends JavaParameter {
    private final String name;
    private final AbstractType<?, ?, ?> valueType;

    private TestJavaParameter(String name, Annotation[] annotations, AbstractType<?, ?, ?> valueType) {
      super(annotations);
      this.name = name;
      this.valueType = valueType;
    }

    @Override
    public Code getCode() {
      return null;
    }

    @Override
    public String getName() {
      return this.name;
    }

    @Override
    public AbstractType<?, ?, ?> getValueType() {
      return this.valueType;
    }

    @Override
    public boolean isEquivalentTo(Object other) {
      return other instanceof TestJavaParameter parameter
          && this.name.equals(parameter.name)
          && this.valueType.equals(parameter.valueType);
    }
  }

  private static final class TestDeclaration extends AbstractDeclaration {
    public final StringProperty name = new StringProperty(this, null);

    private TestDeclaration(String name) {
      this.name.setValue(name);
    }

    @Override
    public boolean isUserAuthored() {
      return true;
    }

    @Override
    public StringProperty getNamePropertyIfItExists() {
      return this.name;
    }
  }

  public static final class PropertyOwner extends AbstractNode {
    public final NodeProperty<Comment> child = new NodeProperty<>(this);
    public final ExpressionListProperty expressions = new ExpressionListProperty(this);
    public final ResourceProperty resource = new ResourceProperty(this);
    public final DeclarationProperty<TestDeclaration> reference = DeclarationProperty.createReferenceInstance(this);
  }

  private static final class TestResource extends Resource {
    private TestResource(String name) {
      super(UUID.randomUUID());
      this.setName(name);
      this.setOriginalFileName(name);
      this.setContent("text/plain", new byte[] {1, 2, 3});
    }
  }

  private static Method declaredMethod(Class<?> type, String name, Class<?>... parameterTypes) {
    try {
      return type.getDeclaredMethod(name, parameterTypes);
    } catch (NoSuchMethodException exception) {
      throw new AssertionError(exception);
    }
  }

  @Test
  public void decodeIdPolicyReportsWhetherIdsArePreserved() {
    assertTrue(DecodeIdPolicy.PRESERVE_IDS.isIdPreserved());
    assertFalse(DecodeIdPolicy.NEW_IDS.isIdPreserved());
  }

  @Test
  public void javaGetterSetterPairReturnsConfiguredAccessors() {
    JavaType beanType = JavaType.getInstance(BeanFixture.class);
    JavaMethod getter = beanType.getDeclaredMethod("getValue");
    JavaMethod setter = beanType.getDeclaredMethod("setValue", Integer.TYPE);

    JavaGetterSetterPair pair = new JavaGetterSetterPair(getter, setter);

    assertSame(getter, pair.getGetter());
    assertSame(setter, pair.getSetter());
  }

  @Test
  public void javaParameterReadsValueTemplateMetadataAndVarArgsMarker() {
    Annotation[][] annotations = declaredMethod(ParameterFixture.class, "templated", Number.class, String.class).getParameterAnnotations();
    TestJavaParameter templated = new TestJavaParameter("value", annotations[0], JavaType.getInstance(Number.class));
    TestJavaParameter plain = new TestJavaParameter("plain", annotations[1], JavaType.STRING_TYPE);

    assertFalse(templated.isUserAuthored());
    assertNull(templated.getNamePropertyIfItExists());
    assertTrue(templated.isVariableLength());
    assertSame(NumberDetails.DEFAULT, templated.getDetails());
    assertEquals(Number.class, templated.getDetails().getSupportedCls());

    assertFalse(plain.isVariableLength());
    assertNull(plain.getDetails());
  }

  @Test
  public void nodePropertyAssignsAndClearsContainedNodeParents() {
    PropertyOwner owner = new PropertyOwner();
    Comment child = new Comment("hello");

    assertEquals("child", owner.child.getName());
    owner.child.setValue(child);
    assertSame(child, owner.child.getValue());
    assertSame(owner, child.getParent());

    owner.child.setValue(null);
    assertNull(child.getParent());
  }

  @Test
  public void declarationPropertyReferenceInstancePreservesReferenceSemantics() {
    PropertyOwner owner = new PropertyOwner();
    TestDeclaration declaration = new TestDeclaration("target");

    assertEquals("reference", owner.reference.getName());
    assertTrue(owner.reference.isReference());

    owner.reference.setValue(declaration);
    assertSame(declaration, owner.reference.getValue());
    assertNull(declaration.getParent());
  }

  @Test
  public void expressionListPropertyMaintainsParentRelationshipsAcrossMutations() {
    PropertyOwner owner = new PropertyOwner();
    StringLiteral first = new StringLiteral("first");
    StringLiteral second = new StringLiteral("second");

    assertEquals("expressions", owner.expressions.getName());
    owner.expressions.add(first, second);
    assertEquals(2, owner.expressions.size());
    assertSame(owner, first.getParent());
    assertSame(owner, second.getParent());

    owner.expressions.clear();
    assertTrue(owner.expressions.isEmpty());
    assertNull(first.getParent());
    assertNull(second.getParent());
  }

  @Test
  public void resourcePropertyStoresResourcesWithoutChangingNodeRelationships() {
    PropertyOwner owner = new PropertyOwner();
    TestResource resource = new TestResource("asset.txt");

    assertEquals("resource", owner.resource.getName());
    assertNull(owner.resource.getValue());

    owner.resource.setValue(resource);
    assertSame(resource, owner.resource.getValue());
    assertEquals("asset.txt", owner.resource.getValue().getName());
    assertEquals("text/plain", owner.resource.getValue().getContentType());
  }
}

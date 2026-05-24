package org.alice.stageide.type.croquet;

import org.junit.Test;
import org.lgna.croquet.ItemCodec;
import org.lgna.croquet.data.MutableListData;
import org.lgna.project.ast.*;
import org.lgna.story.SThing;

import static org.junit.Assert.*;

public class OtherTypeDialogLogicTest {
  private static final ItemCodec<UserField> USER_FIELD_CODEC = new ItemCodec<>() {
    @Override
    public Class<UserField> getValueClass() {
      return UserField.class;
    }

    @Override
    public void encodeValue(edu.cmu.cs.dennisc.codec.BinaryEncoder binaryEncoder, UserField value) {
      throw new AssertionError();
    }

    @Override
    public UserField decodeValue(edu.cmu.cs.dennisc.codec.BinaryDecoder binaryDecoder) {
      throw new AssertionError();
    }

    @Override
    public void appendRepresentation(StringBuilder sb, UserField value) {
      sb.append(value != null ? value.getName() : null);
    }
  };

  private static UserMethod method(String name, Class<?> returnType) {
    return new UserMethod(name, returnType, new UserParameter[0], new BlockStatement());
  }

  @Test
  public void getNotAssignableErrorTextIncludesRootTypeName() {
    assertEquals("Select class assignable to SThing",
        OtherTypeDialogLogic.getNotAssignableErrorText(JavaType.getInstance(SThing.class)));
  }

  @Test
  public void createDescriptionHtmlIncludesSelectedAndInheritedMembers() {
    NamedUserType parent = new NamedUserType("Parent", new UserPackage("test"), Object.class,
        new NamedUserConstructor[0], new UserMethod[]{method("measure", String.class)}, new UserField[]{new UserField("color", String.class)});
    NamedUserType child = new NamedUserType("Child", new UserPackage("test"), parent,
        new NamedUserConstructor[0], new UserMethod[]{method("move", void.class)}, new UserField[]{new UserField("size", String.class)});

    String html = OtherTypeDialogLogic.createDescriptionHtml(child);

    assertTrue(html.contains("class Child"));
    assertTrue(html.contains("move"));
    assertTrue(html.contains("size"));
    assertTrue(html.contains("class Parent <em>(inherit)</em>"));
    assertTrue(html.contains("measure"));
    assertTrue(html.contains("color"));
  }

  @Test
  public void filterAssignableFieldsReturnsOnlyCompatibleFields() {
    UserField thingField = new UserField("thing", SThing.class);
    UserField stringField = new UserField("label", String.class);
    MutableListData<UserField> data = new MutableListData<>(USER_FIELD_CODEC, java.util.List.of(thingField, stringField));

    java.util.List<UserField> filtered = OtherTypeDialogLogic.filterAssignableFields(JavaType.getInstance(SThing.class), data);

    assertEquals(java.util.List.of(thingField), filtered);
  }

  @Test
  public void getSharedTypeNodeReturnsLowestCommonAncestor() {
    JavaType objectType = JavaType.getInstance(Object.class);
    JavaType numberType = JavaType.getInstance(Number.class);
    JavaType integerType = JavaType.getInstance(Integer.class);
    JavaType doubleType = JavaType.getInstance(Double.class);

    TypeNode objectNode = new TypeNode(objectType);
    TypeNode numberNode = new TypeNode(numberType);
    TypeNode integerNode = new TypeNode(integerType);
    TypeNode doubleNode = new TypeNode(doubleType);
    objectNode.add(numberNode);
    numberNode.add(integerNode);
    numberNode.add(doubleNode);

    java.util.Map<AbstractType<?, ?, ?>, TypeNode> map = new java.util.HashMap<>();
    map.put(integerType, integerNode);
    map.put(doubleType, doubleNode);

    UserField integerField = new UserField();
    integerField.name.setValue("count");
    integerField.valueType.setValue(integerType);
    UserField doubleField = new UserField();
    doubleField.name.setValue("ratio");
    doubleField.valueType.setValue(doubleType);

    TypeNode shared = OtherTypeDialogLogic.getSharedTypeNode(java.util.List.of(integerField, doubleField), map);

    assertSame(numberNode, shared);
  }

  @Test
  public void isSelectionAssignableRejectsIncompatibleTypes() {
    TypeNode stringNode = new TypeNode(JavaType.getInstance(String.class));

    assertFalse(OtherTypeDialogLogic.isSelectionAssignable(JavaType.getInstance(SThing.class), stringNode));
    assertTrue(OtherTypeDialogLogic.isSelectionAssignable(null, stringNode));
  }
}

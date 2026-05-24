package org.alice.stageide.type.croquet;

import org.junit.Test;
import org.lgna.croquet.ItemCodec;
import org.lgna.croquet.data.MutableListData;
import org.lgna.project.annotations.Visibility;
import org.lgna.project.ast.BlockStatement;
import org.lgna.project.ast.JavaField;
import org.lgna.project.ast.JavaMethod;
import org.lgna.project.ast.JavaType;
import org.lgna.project.ast.UserField;
import org.lgna.project.ast.UserParameter;
import org.lgna.story.SThing;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class OtherTypeDialogLogicBehaviorTest {
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

  private static final class FieldSamples {
    public static String STATIC_LABEL = "static";
    public String label = "value";
  }

  private static void assertConstructorThrowsAssertionError(Class<?> type) throws Exception {
    Constructor<?> constructor = type.getDeclaredConstructor();
    constructor.setAccessible(true);
    try {
      constructor.newInstance();
      fail("Expected constructor to reject instantiation");
    } catch (InvocationTargetException ite) {
      assertTrue(ite.getCause() instanceof AssertionError);
    }
  }

  @Test
  public void utilityConstructorRejectsInstantiation() throws Exception {
    assertConstructorThrowsAssertionError(OtherTypeDialogLogic.class);
  }

  @Test
  public void getNotAssignableErrorTextAndCreateDescriptionHtmlHandleNullSelections() {
    assertEquals("Select class assignable to ", OtherTypeDialogLogic.getNotAssignableErrorText(null));
    assertTrue(OtherTypeDialogLogic.createDescriptionHtml(null).contains("no class selected"));
  }

  @Test
  public void isSelectionAssignableAndFilterAssignableFieldsHandleMissingFilters() {
    MutableListData<UserField> data = new MutableListData<>(USER_FIELD_CODEC, java.util.List.of(new UserField("thing", SThing.class)));

    assertFalse(OtherTypeDialogLogic.isSelectionAssignable(JavaType.getInstance(SThing.class), null));
    assertTrue(OtherTypeDialogLogic.filterAssignableFields(null, data).isEmpty());
  }

  @Test
  public void isInclusionDesiredRejectsStaticAndHiddenMembers() throws Exception {
    JavaMethod staticMethod = JavaMethod.getInstance(Math.class.getDeclaredMethod("abs", int.class));
    JavaField staticField = JavaField.getInstance(FieldSamples.class.getField("STATIC_LABEL"));
    UserField hiddenField = new UserField("hidden", String.class);
    hiddenField.setVisibility(Visibility.COMPLETELY_HIDDEN);
    UserField visibleField = new UserField("visible", String.class);

    assertFalse(OtherTypeDialogLogic.isInclusionDesired(staticMethod));
    assertFalse(OtherTypeDialogLogic.isInclusionDesired(staticField));
    assertFalse(OtherTypeDialogLogic.isInclusionDesired(hiddenField));
    assertTrue(OtherTypeDialogLogic.isInclusionDesired(visibleField));
  }
}

package org.alice.tweedle;

import org.alice.tweedle.ast.TweedleExpression;
import org.alice.tweedle.run.Frame;
import org.alice.tweedle.run.TweedleObject;
import org.junit.Test;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

public class TweedleCoreTypesTest {

  private static final class DummyValue extends TweedleValue {
    private DummyValue(TweedleType type) {
      super(type);
    }
  }

  private static final class RecordingMethod extends TweedleMethod {
    private boolean invoked;
    private Frame frame;
    private TweedleObject target;
    private TweedleValue[] arguments;

    private RecordingMethod(String name) {
      super(TweedleVoidType.VOID, name, Collections.emptyList(), Collections.emptyList(), Collections.emptyList());
    }

    @Override
    public TweedleValue invoke(Frame frame, TweedleObject target, TweedleValue[] arguments) {
      this.invoked = true;
      this.frame = frame;
      this.target = target;
      this.arguments = arguments;
      return TweedleNull.NULL;
    }
  }

  @Test
  public void tweedleTypeAcceptsImpliedTypes() {
    TweedleType base = new TweedleType("Base");
    TweedleType child = new TweedleType("Child", base);
    TweedleType grandchild = new TweedleType("Grandchild", child);

    assertTrue(base.willAcceptValueOfType(base));
    assertTrue(base.willAcceptValueOfType(child));
    assertTrue(base.willAcceptValueOfType(grandchild));
    assertFalse(child.willAcceptValueOfType(base));
    assertEquals("Base", base.getName());
    assertEquals("aBase", base.valueToString(new DummyValue(base)));
  }

  @Test
  public void primitiveTypeCreatesFormatsAndComparesValues() {
    TweedlePrimitiveValue<Integer> three = TweedleTypes.WHOLE_NUMBER.createValue(3);
    TweedlePrimitiveValue<Integer> anotherThree = TweedleTypes.WHOLE_NUMBER.createValue(3);
    TweedlePrimitiveValue<Integer> four = TweedleTypes.WHOLE_NUMBER.createValue(4);

    assertEquals(Integer.valueOf(3), three.getPrimitiveValue());
    assertEquals("WholeNumber", TweedleTypes.WHOLE_NUMBER.toString());
    assertEquals("3", TweedleTypes.WHOLE_NUMBER.valueToString(three));
    assertEquals("3", three.toTextString());
    assertEquals("Value(3 : WholeNumber)", three.toString());
    assertEquals(three, anotherThree);
    assertEquals(three.hashCode(), anotherThree.hashCode());
    assertNotEquals(three, four);
  }

  @Test
  public void arrayTypeAndArraySupportCompatibilityAndEquality() {
    TweedleArrayType anyArray = new TweedleArrayType();
    TweedleArrayType numberArray = new TweedleArrayType(TweedleTypes.NUMBER);
    TweedleArrayType wholeArray = new TweedleArrayType(TweedleTypes.WHOLE_NUMBER);
    TweedleArrayType decimalArray = new TweedleArrayType(TweedleTypes.DECIMAL_NUMBER);

    assertNull(anyArray.getValueType());
    assertEquals(TweedleTypes.WHOLE_NUMBER, wholeArray.getValueType());
    assertTrue(anyArray.willAcceptValueOfType(wholeArray));
    assertTrue(numberArray.willAcceptValueOfType(wholeArray));
    assertTrue(numberArray.willAcceptValueOfType(decimalArray));
    assertFalse(wholeArray.willAcceptValueOfType(numberArray));

    TweedleArray array = new TweedleArray(wholeArray, List.of(TweedleTypes.WHOLE_NUMBER.createValue(1), TweedleTypes.WHOLE_NUMBER.createValue(2)));
    TweedleArray equalArray = new TweedleArray(wholeArray, List.of(TweedleTypes.WHOLE_NUMBER.createValue(1), TweedleTypes.WHOLE_NUMBER.createValue(2)));
    TweedleArray differentArray = new TweedleArray(wholeArray, List.of(TweedleTypes.WHOLE_NUMBER.createValue(2)));

    assertEquals(2, array.length());
    assertEquals(Integer.valueOf(1), ((TweedlePrimitiveValue<Integer>) array.getAt(0)).getPrimitiveValue());
    assertEquals(array, equalArray);
    assertEquals(array.hashCode(), equalArray.hashCode());
    assertNotEquals(array, differentArray);
  }

  @Test
  public void classInvokesOwnedMethodsAndDelegatesUnknownMethods() {
    RecordingMethod method = new RecordingMethod("go");
    TweedleClass tweedleClass = new TweedleClass("Scene", List.of(), List.of(method), List.of());
    TweedleObject target = new TweedleObject(tweedleClass);
    Frame frame = new Frame(target);
    TweedleValue argument = TweedleTypes.TEXT_STRING.createValue("hello");

    tweedleClass.invoke(frame, target, method, new TweedleValue[]{argument});

    assertTrue(method.invoked);
    assertSame(frame, method.frame);
    assertSame(target, method.target);
    assertArrayEquals(new TweedleValue[]{argument}, method.arguments);
    assertNull(tweedleClass.getSuperclassName());
    assertSame(method, tweedleClass.getMethods().getFirst());
    assertTrue(tweedleClass.getProperties().isEmpty());
    assertTrue(tweedleClass.getConstructors().isEmpty());
    assertSame(tweedleClass, tweedleClass.instantiate(frame, new TweedleValue[0]).getType());

    TweedleClass subclass = new TweedleClass("Child", "Parent", List.of(), List.of(), List.of());
    TweedleLinkException exception = assertThrows(TweedleLinkException.class,
        () -> subclass.invoke(frame, target, method, new TweedleValue[0]));
    assertNull(exception.getMessage());
    assertEquals("Parent", subclass.getSuperclassName());
  }

  @Test
  public void fieldsMethodsAndParametersExposeTheirState() {
    TweedleExpression initializer = TweedleTypes.TEXT_STRING.createValue("value");
    TweedleField plainField = new TweedleField(List.of("static"), TweedleTypes.WHOLE_NUMBER, "count");
    TweedleField initializedField = new TweedleField(List.of("static"), TweedleTypes.TEXT_STRING, "label", initializer);
    TweedleOptionalParameter optional = new TweedleOptionalParameter(TweedleTypes.TEXT_STRING, "message", initializer);
    TweedleRequiredParameter required = new TweedleRequiredParameter(TweedleTypes.NUMBER, "amount");
    TweedleMethod instanceMethod = new TweedleMethod(TweedleTypes.TEXT_STRING, "describe", List.of(required), List.of(optional), Collections.emptyList());
    TweedleMethod staticMethod = new TweedleMethod(List.of("static"), TweedleTypes.WHOLE_NUMBER, "build", List.of(), List.of(), Collections.emptyList());
    TweedleConstructor constructor = new TweedleConstructor(new TweedleTypeReference("Scene"), "Scene", List.of(required), List.of(optional), Collections.emptyList());

    assertFalse(plainField.hasInitializer());
    assertTrue(initializedField.hasInitializer());
    assertSame(initializer, initializedField.getInitializer());
    assertEquals("count", plainField.getName());
    assertSame(TweedleTypes.WHOLE_NUMBER, plainField.getType());
    assertEquals("message", optional.getName());
    assertSame(TweedleTypes.TEXT_STRING, optional.getType());
    assertEquals("amount", required.getName());
    assertSame(TweedleTypes.NUMBER, required.getType());
    assertEquals("describe", instanceMethod.getName());
    assertSame(TweedleTypes.TEXT_STRING, instanceMethod.getType());
    assertEquals(1, instanceMethod.getRequiredParameters().size());
    assertEquals(1, instanceMethod.getOptionalParameters().size());
    assertFalse(instanceMethod.isStatic());
    assertTrue(staticMethod.isStatic());
    assertNull(instanceMethod.invoke(new Frame(TweedleNull.NULL), null, new TweedleValue[0]));
    assertTrue(constructor instanceof TweedleMethod);
    assertEquals("Scene", constructor.getName());
  }

  @Test
  public void enumAndEnumValueExposeNamesAndStringFormatting() {
    TweedleEnumValue north = new TweedleEnumValue(null, "NORTH", Collections.emptyMap());
    TweedleEnumValue south = new TweedleEnumValue(null, "SOUTH", Collections.emptyMap());
    TweedleEnum direction = new TweedleEnum("Direction", Map.of("NORTH", north, "SOUTH", south), List.of(), List.of(), List.of());
    TweedleEnumValue linkedNorth = new TweedleEnumValue(direction, "NORTH", Collections.emptyMap());

    assertEquals(2, direction.getValues().size());
    assertSame(north, direction.getValue("NORTH"));
    assertNull(direction.getValue("EAST"));
    assertEquals("NORTH", linkedNorth.getName());
    assertEquals("Direction.NORTH", direction.valueToString(linkedNorth));
  }

  @Test
  public void specialSingletonsAndReferencesCharacterizeCurrentBehavior() {
    assertSame(TweedleVoidType.VOID, TweedleNull.NULL.getType());
    assertEquals("void", TweedleVoidType.VOID.toString());
    assertEquals("null", TweedleVoidType.VOID.valueToString(TweedleNull.NULL));

    TweedleTypeReference ref = new TweedleTypeReference("TargetType");
    TweedleTypeReference sameName = new TweedleTypeReference("TargetType");
    TweedleTypeReference other = new TweedleTypeReference("OtherType");
    RecordingMethod method = new RecordingMethod("jump");

    assertEquals(ref, sameName);
    assertEquals(ref.hashCode(), sameName.hashCode());
    assertNotEquals(ref, other);
    assertTrue(ref.willAcceptValueOfType(sameName));
    assertThrows(TweedleLinkException.class, () -> ref.willAcceptValueOfType(other));
    assertThrows(TweedleLinkException.class, () -> ref.invoke(new Frame(TweedleNull.NULL), null, method, new TweedleValue[0]));
    assertEquals(5, TweedleTypes.PRIMITIVE_TYPES.length);
    assertSame(TweedleTypes.NUMBER, TweedleTypes.commonNumberType(null, TweedleTypes.WHOLE_NUMBER.createValue(1)));
    assertSame(TweedleTypes.WHOLE_NUMBER, TweedleTypes.commonNumberType(TweedleTypes.WHOLE_NUMBER.createValue(1), TweedleTypes.WHOLE_NUMBER.createValue(2)));
    assertSame(TweedleTypes.DECIMAL_NUMBER, TweedleTypes.commonNumberType(TweedleTypes.WHOLE_NUMBER.createValue(1), TweedleTypes.DECIMAL_NUMBER.createValue(2.5)));
  }

  @Test
  public void statementsLibraryAndResourceKeepCurrentNoOpBehavior() {
    TweedleStatement statement = new TweedleStatement();
    statement.execute(new Frame(TweedleNull.NULL));
    assertTrue(statement.isEnabled());
    statement.disable();
    assertFalse(statement.isEnabled());

    TweedleLibrary library = new TweedleLibrary(List.of(new TweedleType("Scene")));
    assertNull(library.getTypeNamed("Scene"));
    assertNotNull(new TweedleResource());
  }
}

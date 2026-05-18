package org.alice.serialization.tweedle;

import org.junit.Test;
import org.lgna.common.Resource;
import org.lgna.project.annotations.ClassTemplate;
import org.lgna.project.annotations.FieldTemplate;
import org.lgna.project.annotations.Visibility;
import org.lgna.project.ast.AbstractParameter;
import org.lgna.project.ast.Comment;
import org.lgna.project.ast.DoubleLiteral;
import org.lgna.project.ast.Expression;
import org.lgna.project.ast.InstanceCreation;
import org.lgna.project.ast.IntegerLiteral;
import org.lgna.project.ast.JavaConstructor;
import org.lgna.project.ast.JavaConstructorParameter;
import org.lgna.project.ast.JavaKeyedArgument;
import org.lgna.project.ast.JavaMethod;
import org.lgna.project.ast.JavaMethodParameter;
import org.lgna.project.ast.JavaType;
import org.lgna.project.ast.MethodInvocation;
import org.lgna.project.ast.NullLiteral;
import org.lgna.project.ast.ResourceExpression;
import org.lgna.project.ast.SimpleArgument;
import org.lgna.project.ast.StringLiteral;
import org.lgna.project.ast.TypeExpression;
import org.lgna.project.ast.UserParameter;

import java.lang.reflect.Field;
import java.util.UUID;
import java.util.function.Consumer;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class TweedleSubEncoderDeepTest {

  @Test
  public void appendVisibilityTagIgnoresNullAnnotation() {
    assertEquals("", render(encoder -> encoder.appendVisibilityTag(null)));
  }

  @Test
  public void appendVisibilityTagWritesPrimeTime() throws Exception {
    FieldTemplate template = fieldTemplate("prime");
    assertEquals("@PrimeTime ", render(encoder -> encoder.appendVisibilityTag(template)));
  }

  @Test
  public void appendVisibilityTagWritesTuckedAway() throws Exception {
    FieldTemplate template = fieldTemplate("tucked");
    assertEquals("@TuckedAway ", render(encoder -> encoder.appendVisibilityTag(template)));
  }

  @Test
  public void appendVisibilityTagWritesCompletelyHidden() throws Exception {
    FieldTemplate template = fieldTemplate("hidden");
    assertEquals("@CompletelyHidden ", render(encoder -> encoder.appendVisibilityTag(template)));
  }

  @Test
  public void appendInstantiationParenthesizesArguments() {
    assertEquals("new Widget(size: 3)", render(encoder ->
        encoder.appendInstantiation("Widget", () -> encoder.appendArg("size", "3"))));
  }

  @Test
  public void appendArgWithStringValueFormatsLabelAndValue() {
    assertEquals("name: Ada", render(encoder -> encoder.appendArg("name", "Ada")));
  }

  @Test
  public void appendArgWithRunnableValueFormatsLabelAndValue() {
    assertEquals("name: \"Ada\"", render(encoder -> encoder.appendArg("name", () -> encoder.quoteString("Ada"))));
  }

  @Test
  public void appendAnotherArgWithStringPrependsSeparator() {
    assertEquals("first: 1, second: 2", render(encoder -> {
      encoder.appendArg("first", "1");
      encoder.appendAnotherArg("second", "2");
    }));
  }

  @Test
  public void appendAnotherArgWithRunnablePrependsSeparator() {
    assertEquals("first: 1, second: \"two\"", render(encoder -> {
      encoder.appendArg("first", "1");
      encoder.appendAnotherArg("second", () -> encoder.quoteString("two"));
    }));
  }

  @Test
  public void appendListWithNoValuesProducesBraces() {
    assertEquals("{}", render(encoder -> encoder.appendList(new String[0], encoder::forwardAppendString, " | ")));
  }

  @Test
  public void appendListWithOneValueOmitsSeparator() {
    assertEquals("{solo}", render(encoder -> encoder.appendList(new String[] {"solo"}, encoder::forwardAppendString, " | ")));
  }

  @Test
  public void appendListWithSeveralValuesUsesCustomSeparator() {
    assertEquals("{a | b | c}", render(encoder ->
        encoder.appendList(new String[] {"a", "b", "c"}, encoder::forwardAppendString, " | ")));
  }

  @Test
  public void quoteStringWrapsValueInQuotes() {
    assertEquals("\"hello\"", render(encoder -> encoder.quoteString("hello")));
  }

  @Test
  public void appendIndentAtZeroLevelIsEmpty() {
    assertEquals("", render(TweedleEncoder::appendIndent));
  }

  @Test
  public void openBlockFollowedByAppendIndentUsesCachedIndent() {
    assertEquals(" {\n  ", render(encoder -> {
      encoder.openBlock();
      encoder.appendIndent();
    }));
  }

  @Test
  public void deepOpenBlocksUseUncachedIndentString() {
    String text = render(encoder -> {
      for (int i = 0; i < 17; i++) {
        encoder.openBlock();
      }
      encoder.appendIndent();
    });
    assertTrue(text.endsWith("  ".repeat(17)));
  }

  @Test
  public void closeBlockInlineUnindentsBeforeBrace() {
    assertEquals(" {\n}", render(encoder -> {
      encoder.openBlock();
      encoder.closeBlockInline();
    }));
  }

  @Test
  public void closeBlockAppendsTrailingNewline() {
    assertEquals(" {\n}\n", render(encoder -> {
      encoder.openBlock();
      encoder.closeBlock();
    }));
  }

  @Test
  public void appendSingleCodeLineUsesCurrentIndent() {
    assertEquals(" {\n  line;\n", render(encoder -> {
      encoder.openBlock();
      encoder.appendSingleCodeLine(() -> encoder.forwardAppendString("line"));
    }));
  }

  @Test
  public void processSingleStatementEnabledUsesCurrentIndent() {
    assertEquals(" {\n  line;\n", render(encoder -> {
      encoder.openBlock();
      Comment stmt = new Comment("enabled");
      encoder.processSingleStatement(stmt, () -> encoder.forwardAppendString("line"));
    }));
  }

  @Test
  public void processSingleStatementDisabledUsesPreviousIndentAndEnableMarker() {
    assertEquals(" {\nline; >*\n", render(encoder -> {
      encoder.openBlock();
      Comment stmt = new Comment("disabled");
      stmt.isEnabled.setValue(false);
      encoder.processSingleStatement(stmt, () -> encoder.forwardAppendString("line"));
    }));
  }

  @Test
  public void processInstantiationEncodesPersonResourceSummary() throws Exception {
    InstanceCreation creation = creation(SamplePersonResource.class, new Class<?>[] {String.class}, new String[] {"name"}, new StringLiteral("Ada"));
    assertEquals("new PersonResource(name: \"Person/Ada\")", encode(creation));
  }

  @Test
  public void processInstantiationEncodesSingleArgumentDoubleAsDecimalFactory() throws Exception {
    InstanceCreation creation = creation(Double.class, new Class<?>[] {Integer.TYPE}, new String[] {"wholeNumber"}, new IntegerLiteral(5));
    assertEquals("$DecimalNumber.from(wholeNumber: 5)", encode(creation));
  }

  @Test
  public void processInstantiationFallsBackForMultiArgumentDouble() throws Exception {
    InstanceCreation creation = creation(Double.class, new Class<?>[] {Integer.TYPE, Integer.TYPE}, new String[] {"first", "second"}, new IntegerLiteral(1), new IntegerLiteral(2));
    String text = encode(creation);
    assertTrue(text.contains("new DecimalNumber("));
    assertTrue(text.contains("first: 1"));
    assertTrue(text.contains("second: 2"));
  }

  @Test
  public void processInstantiationEncodesDynamicResourceDefault() throws Exception {
    InstanceCreation creation = creation(DynamicWidgetResource.class, new Class<?>[] {String.class, String.class}, new String[] {"modelName", "resourceName"}, new StringLiteral("Widget"), new StringLiteral("Blue"));
    assertEquals("BlueResource.DEFAULT", encode(creation));
  }

  @Test
  public void processInstantiationFallsBackForDynamicResourceNonStringVariant() throws Exception {
    InstanceCreation creation = creation(DynamicWidgetResource.class, new Class<?>[] {String.class, Object.class}, new String[] {"modelName", "resourceName"}, new StringLiteral("Widget"), new IntegerLiteral(3));
    String text = encode(creation);
    assertTrue(text.contains("new DynamicWidgetResource("));
    assertTrue(text.contains("resourceName: 3"));
  }

  @Test
  public void processInstantiationFallsBackForOrdinaryType() throws Exception {
    InstanceCreation creation = creation(PlainThing.class, new Class<?>[] {String.class}, new String[] {"value"}, new StringLiteral("hi"));
    String text = encode(creation);
    assertTrue(text.contains("new PlainThing("));
    assertTrue(text.contains("value: \"hi\""));
  }

  @Test
  public void appendTargetAndMemberUsesWholeNumberModuleForMathInts() {
    assertEquals("$WholeNumber.abs", render(encoder ->
        encoder.appendTargetAndMember(new TypeExpression(Math.class), "abs", JavaType.getInstance(Integer.TYPE))));
  }

  @Test
  public void appendTargetAndMemberUsesAngleModuleForAngleMembers() {
    assertEquals("$Angle.sin", render(encoder ->
        encoder.appendTargetAndMember(new TypeExpression(Math.class), "sin", JavaType.getInstance(java.lang.Double.TYPE))));
  }

  @Test
  public void appendTargetAndMemberUsesDecimalModuleForNonAngleMathMembers() {
    assertEquals("$DecimalNumber.sqrt", render(encoder ->
        encoder.appendTargetAndMember(new TypeExpression(Math.class), "sqrt", JavaType.getInstance(java.lang.Double.TYPE))));
  }

  @Test
  public void appendTargetAndMemberRenamesMembers() {
    assertEquals("$DecimalNumber.ceiling", render(encoder ->
        encoder.appendTargetAndMember(new TypeExpression(Math.class), "ceil", JavaType.getInstance(java.lang.Double.TYPE))));
  }

  @Test
  public void appendTargetAndMemberProcessesNonMathTarget() {
    assertEquals("\"value\".length", render(encoder ->
        encoder.appendTargetAndMember(new StringLiteral("value"), "length", JavaType.getInstance(Integer.TYPE))));
  }

  @Test
  public void processResourceExpressionEscapesResourceName() {
    ResourceExpression expression = new ResourceExpression(JavaType.getInstance(TestResource.class), new TestResource("say \"hi\""));
    assertEquals("\"say \\\"hi\\\"\"", encode(expression));
  }

  @Test
  public void processKeyedArgumentRelabelsKeywordFactoryMethod() throws Exception {
    UserParameter parameter = new UserParameter("values", KeywordValue[].class);
    JavaMethod keyMethod = JavaMethod.getInstance(KeywordFactory.class, "multipleEventPolicy", KeywordValue.class);
    String text = encode(new JavaKeyedArgument(parameter, keyMethod, new NullLiteral()));
    assertEquals("overlappingEventPolicy: null", text);
  }

  @Test
  public void processKeyedArgumentWrapsDurationKeywordValue() throws Exception {
    UserParameter parameter = new UserParameter("values", KeywordValue[].class);
    JavaMethod keyMethod = JavaMethod.getInstance(KeywordFactory.class, "duration", KeywordValue.class);
    String text = encode(new JavaKeyedArgument(parameter, keyMethod, new NullLiteral()));
    assertEquals("duration: new Duration(seconds: null)", text);
  }

  @Test
  public void processKeyedArgumentFallsBackForLiteralExpression() {
    JavaKeyedArgument argument = new JavaKeyedArgument();
    argument.expression.setValue(new StringLiteral("raw"));
    assertEquals("\"raw\"", encode(argument));
  }

  @Test
  public void processKeyedArgumentFallsBackWhenParameterIsNotKeyworded() throws Exception {
    UserParameter parameter = new UserParameter("values", String[].class);
    JavaMethod keyMethod = JavaMethod.getInstance(KeywordFactory.class, "colorless");
    JavaKeyedArgument argument = new JavaKeyedArgument();
    argument.parameter.setValue(parameter);
    argument.expression.setValue(new MethodInvocation(new TypeExpression(KeywordFactory.class), keyMethod));
    String text = encode(argument);
    assertTrue(text.contains("KeywordFactory.colorless"));
  }

  @Test
  public void processArgumentUsesParameterNameWhenAvailable() throws Exception {
    JavaMethodParameter parameter = methodParameter(ArgumentFixtures.class, "plain", new Class<?>[] {String.class}, 0, "value");
    assertEquals("value: \"hello\"", renderArgument(parameter, new StringLiteral("hello")));
  }

  @Test
  public void processArgumentWrapsDelayDurationParameter() throws Exception {
    JavaMethodParameter parameter = methodParameter(ArgumentFixtures.class, "delay", new Class<?>[] {java.lang.Double.TYPE}, 0, "duration");
    assertEquals("duration: new Duration(seconds: 1.5)", renderArgument(parameter, new DoubleLiteral(1.5)));
  }

  @Test
  public void processArgumentRelabelsMultipleEventPolicyParameter() throws Exception {
    JavaMethodParameter parameter = methodParameter(ArgumentFixtures.class, "listener", new Class<?>[] {String.class}, 0, "multipleEventPolicy");
    assertEquals("overlappingEventPolicy: \"keep\"", renderArgument(parameter, new StringLiteral("keep")));
  }

  @Test
  public void processArgumentUsesMissingNamesTableForMathPowFirstParameter() throws Exception {
    JavaMethodParameter parameter = methodParameter(Math.class, "pow", new Class<?>[] {java.lang.Double.TYPE, java.lang.Double.TYPE}, 0, null);
    assertEquals("b: 2.0", renderArgument(parameter, new DoubleLiteral(2.0)));
  }

  @Test
  public void processArgumentUsesMissingNamesTableForMathPowSecondParameter() throws Exception {
    JavaMethodParameter parameter = methodParameter(Math.class, "pow", new Class<?>[] {java.lang.Double.TYPE, java.lang.Double.TYPE}, 1, null);
    assertEquals("power: 3.0", renderArgument(parameter, new DoubleLiteral(3.0)));
  }

  @Test
  public void processArgumentRelabelsSizeConstructorParameters() throws Exception {
    JavaConstructorParameter parameter = constructorParameter(Size.class, new Class<?>[] {java.lang.Double.TYPE, java.lang.Double.TYPE, java.lang.Double.TYPE}, 0, "leftToRight");
    assertEquals("width: 4.0", renderArgument(parameter, new DoubleLiteral(4.0)));
  }

  @Test
  public void processArgumentRelabelsPositionConstructorParameters() throws Exception {
    JavaConstructorParameter parameter = constructorParameter(Position.class, new Class<?>[] {java.lang.Double.TYPE, java.lang.Double.TYPE, java.lang.Double.TYPE}, 0, "right");
    assertEquals("x: 5.0", renderArgument(parameter, new DoubleLiteral(5.0)));
  }

  @Test
  public void processArgumentRelabelsImageSourceConstructorParameters() throws Exception {
    JavaConstructorParameter parameter = constructorParameter(ImageSource.class, new Class<?>[] {String.class}, 0, "imageResource");
    assertEquals("resource: \"clouds\"", renderArgument(parameter, new StringLiteral("clouds")));
  }

  @Test
  public void processArgumentUsesWholeNumberForDoubleConstructorParameter() throws Exception {
    JavaConstructorParameter parameter = constructorParameter(java.lang.Double.class, new Class<?>[] {java.lang.Double.TYPE}, 0, null);
    assertEquals("wholeNumber: 7.0", renderArgument(parameter, new DoubleLiteral(7.0)));
  }

  private String encode(org.lgna.project.code.ProcessableNode node) {
    return new TweedleEncoder().encode(node);
  }

  private String render(Consumer<TweedleEncoder> action) {
    TweedleEncoder encoder = new TweedleEncoder();
    action.accept(encoder);
    return encoder.getText();
  }

  private String renderArgument(AbstractParameter parameter, Expression expression) {
    return render(encoder -> encoder.processArgument(parameter, new SimpleArgument(parameter, expression)));
  }

  private FieldTemplate fieldTemplate(String fieldName) throws Exception {
    return VisibilityFixture.class.getDeclaredField(fieldName).getAnnotation(FieldTemplate.class);
  }

  private InstanceCreation creation(Class<?> type, Class<?>[] parameterTypes, String[] parameterNames, Expression... expressions) throws Exception {
    JavaConstructor constructor = JavaConstructor.getInstance(type, parameterTypes);
    SimpleArgument[] arguments = new SimpleArgument[expressions.length];
    for (int i = 0; i < expressions.length; i++) {
      JavaConstructorParameter parameter = constructor.getRequiredParameters().get(i);
      forceName(parameter, parameterNames[i]);
      arguments[i] = new SimpleArgument(parameter, expressions[i]);
    }
    return new InstanceCreation(constructor, arguments);
  }

  private JavaMethodParameter methodParameter(Class<?> type, String methodName, Class<?>[] parameterTypes, int index, String forcedName) throws Exception {
    JavaMethodParameter parameter = JavaMethod.getInstance(type, methodName, parameterTypes).getRequiredParameters().get(index);
    forceName(parameter, forcedName);
    return parameter;
  }

  private JavaConstructorParameter constructorParameter(Class<?> type, Class<?>[] parameterTypes, int index, String forcedName) throws Exception {
    JavaConstructorParameter parameter = JavaConstructor.getInstance(type, parameterTypes).getRequiredParameters().get(index);
    forceName(parameter, forcedName);
    return parameter;
  }

  private void forceName(Object parameter, String forcedName) throws Exception {
    Field field = parameter.getClass().getDeclaredField("name");
    field.setAccessible(true);
    field.set(parameter, forcedName);
  }

  private static final class VisibilityFixture {
    @FieldTemplate(visibility = Visibility.PRIME_TIME)
    private int prime;
    @FieldTemplate(visibility = Visibility.TUCKED_AWAY)
    private int tucked;
    @FieldTemplate(visibility = Visibility.COMPLETELY_HIDDEN)
    private int hidden;
  }

  public static final class SamplePersonResource {
    private final String summary;

    public SamplePersonResource(String summary) {
      this.summary = summary;
    }

    @Override
    public String toString() {
      return this.summary;
    }
  }

  public static final class Double {
    public Double(int wholeNumber) {
    }

    public Double(int first, int second) {
    }
  }

  public static final class DynamicWidgetResource {
    public DynamicWidgetResource(String modelName, String resourceName) {
    }

    public DynamicWidgetResource(String modelName, Object resourceName) {
    }
  }

  public static final class PlainThing {
    public PlainThing(String value) {
    }
  }

  public static final class Size {
    public Size(double leftToRight, double bottomToTop, double frontToBack) {
    }
  }

  public static final class Position {
    public Position(double right, double up, double backward) {
    }
  }

  public static final class ImageSource {
    public ImageSource(String imageResource) {
    }
  }

  public static final class ArgumentFixtures {
    public static void plain(String value) {
    }

    public static void delay(double duration) {
    }

    public static void listener(String multipleEventPolicy) {
    }
  }

  public static final class KeywordFactory {
    public static KeywordValue multipleEventPolicy(KeywordValue multipleEventPolicy) {
      return multipleEventPolicy;
    }

    public static KeywordValue duration(KeywordValue duration) {
      return duration;
    }

    public static KeywordValue color(KeywordValue color) {
      return color;
    }

    public static KeywordValue colorless() {
      return null;
    }
  }

  @ClassTemplate(keywordFactoryCls = KeywordFactory.class)
  public static final class KeywordValue {
  }

  public static final class TestResource extends Resource {
    public TestResource(String name) {
      super(UUID.randomUUID());
      setName(name);
      setOriginalFileName(name);
      setContent("text/plain", new byte[0]);
    }
  }
}

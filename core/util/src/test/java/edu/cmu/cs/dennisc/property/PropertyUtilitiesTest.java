package edu.cmu.cs.dennisc.property;

import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.*;

class PropertyUtilitiesTest {

  // Test helper classes
  @SuppressWarnings("unused")
  static class SimpleBean {
    private String name;
    private int value;
    private boolean active;
    private Double score;

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public int getValue() { return value; }
    public void setValue(int value) { this.value = value; }

    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }

    public Boolean isVisible() { return true; }
    public void setVisible(Boolean visible) { }

    public Double getScore() { return score; }
    public void setScore(Number score) { this.score = score.doubleValue(); }

    public double getRatio() { return 0.5; }
    public void setRatio(Number ratio) { }

    public String doSomething() { return ""; }
    public void notAGetter(String arg) { }
  }

  // --- getPropertyNameForGetter: standard getXxx ---

  @Test
  void getPropertyNameForGetter_standardGetter() throws Exception {
    Method getter = SimpleBean.class.getMethod("getName");
    String name = PropertyUtilities.getPropertyNameForGetter(getter);
    assertEquals("Name", name);
  }

  @Test
  void getPropertyNameForGetter_intGetter() throws Exception {
    Method getter = SimpleBean.class.getMethod("getValue");
    String name = PropertyUtilities.getPropertyNameForGetter(getter);
    assertEquals("Value", name);
  }

  // --- getPropertyNameForGetter: boolean isXxx ---

  @Test
  void getPropertyNameForGetter_booleanPrimitiveIsPrefix() throws Exception {
    Method getter = SimpleBean.class.getMethod("isActive");
    String name = PropertyUtilities.getPropertyNameForGetter(getter);
    assertEquals("IsActive", name);
  }

  @Test
  void getPropertyNameForGetter_booleanWrapperIsPrefix() throws Exception {
    Method getter = SimpleBean.class.getMethod("isVisible");
    String name = PropertyUtilities.getPropertyNameForGetter(getter);
    assertEquals("IsVisible", name);
  }

  // --- getPropertyNameForGetter: non-getter returns null ---

  @Test
  void getPropertyNameForGetter_nonGetterMethodReturnsNull() throws Exception {
    Method method = SimpleBean.class.getMethod("doSomething");
    String name = PropertyUtilities.getPropertyNameForGetter(method);
    assertNull(name);
  }

  // --- getSetterForGetter: standard property ---

  @Test
  void getSetterForGetter_standardProperty() throws Exception {
    Method getter = SimpleBean.class.getMethod("getName");
    Method setter = PropertyUtilities.getSetterForGetter(getter);
    assertNotNull(setter);
    assertEquals("setName", setter.getName());
  }

  @Test
  void getSetterForGetter_intProperty() throws Exception {
    Method getter = SimpleBean.class.getMethod("getValue");
    Method setter = PropertyUtilities.getSetterForGetter(getter);
    assertNotNull(setter);
    assertEquals("setValue", setter.getName());
  }

  // --- getSetterForGetter: boolean property ---

  @Test
  void getSetterForGetter_booleanPrimitiveProperty() throws Exception {
    Method getter = SimpleBean.class.getMethod("isActive");
    Method setter = PropertyUtilities.getSetterForGetter(getter);
    assertNotNull(setter);
    assertEquals("setActive", setter.getName());
  }

  @Test
  void getSetterForGetter_booleanWrapperProperty() throws Exception {
    Method getter = SimpleBean.class.getMethod("isVisible");
    Method setter = PropertyUtilities.getSetterForGetter(getter);
    assertNotNull(setter);
    assertEquals("setVisible", setter.getName());
  }

  // --- getSetterForGetter: Number fallback for Double ---

  @Test
  void getSetterForGetter_doublePropertyWithNumberSetter() throws Exception {
    Method getter = SimpleBean.class.getMethod("getScore");
    Method setter = PropertyUtilities.getSetterForGetter(getter);
    assertNotNull(setter);
    assertEquals("setScore", setter.getName());
  }

  @Test
  void getSetterForGetter_primitiveDoubleWithNumberSetter() throws Exception {
    Method getter = SimpleBean.class.getMethod("getRatio");
    Method setter = PropertyUtilities.getSetterForGetter(getter);
    assertNotNull(setter);
    assertEquals("setRatio", setter.getName());
  }

  // --- getSetterForGetter with explicit class ---

  @Test
  void getSetterForGetter_withExplicitClass() throws Exception {
    Method getter = SimpleBean.class.getMethod("getName");
    Method setter = PropertyUtilities.getSetterForGetter(getter, SimpleBean.class);
    assertNotNull(setter);
    assertEquals("setName", setter.getName());
  }

  // --- getPropertyNameForGetter: Double getter ---

  @Test
  void getPropertyNameForGetter_doubleGetter() throws Exception {
    Method getter = SimpleBean.class.getMethod("getScore");
    String name = PropertyUtilities.getPropertyNameForGetter(getter);
    assertEquals("Score", name);
  }

  // --- Setter parameter types ---

  @Test
  void getSetterForGetter_setterHasCorrectParamType() throws Exception {
    Method getter = SimpleBean.class.getMethod("getName");
    Method setter = PropertyUtilities.getSetterForGetter(getter);
    assertNotNull(setter);
    assertEquals(1, setter.getParameterCount());
    assertEquals(String.class, setter.getParameterTypes()[0]);
  }

  @Test
  void getSetterForGetter_booleanSetterHasCorrectParamType() throws Exception {
    Method getter = SimpleBean.class.getMethod("isActive");
    Method setter = PropertyUtilities.getSetterForGetter(getter);
    assertNotNull(setter);
    assertEquals(1, setter.getParameterCount());
    assertEquals(boolean.class, setter.getParameterTypes()[0]);
  }

  // --- VarArgs setter support ---

  @SuppressWarnings("unused")
  static class VarArgsBean {
    private String label;

    public String getLabel() { return label; }
    public void setLabel(String label, String... extras) { this.label = label; }
  }

  @Test
  void getSetterForGetter_findsVarArgsSetter() throws Exception {
    Method getter = VarArgsBean.class.getMethod("getLabel");
    Method setter = PropertyUtilities.getSetterForGetter(getter, VarArgsBean.class);
    assertNotNull(setter);
    assertEquals("setLabel", setter.getName());
    assertTrue(setter.isVarArgs());
  }
}

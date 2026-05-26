package org.alice.ide.issue.swing.views;

import org.junit.Test;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

public class IssueReportPaneHelperTest {
  @Test
  public void constructorThrowsAssertionError() throws Exception {
    java.lang.reflect.Constructor<IssueReportPaneHelper> constructor = IssueReportPaneHelper.class.getDeclaredConstructor();
    constructor.setAccessible(true);

    try {
      constructor.newInstance();
      fail("Expected AssertionError");
    } catch (java.lang.reflect.InvocationTargetException exception) {
      assertTrue(exception.getCause() instanceof AssertionError);
    }
  }

  @Test
  public void buildEnvironmentLongDescriptionIncludesKeysAndValues() {
    Map<String, String> values = values();

    assertEquals("java.version: 21\nos.name: Linux",
        IssueReportPaneHelper.buildEnvironmentLongDescription(Arrays.asList("java.version", "os.name"), values::get));
  }

  @Test
  public void buildEnvironmentShortDescriptionUsesSemicolons() {
    Map<String, String> values = values();

    assertEquals("21;Linux",
        IssueReportPaneHelper.buildEnvironmentShortDescription(Arrays.asList("java.version", "os.name"), values::get, false, "ignored"));
  }

  @Test
  public void buildEnvironmentShortDescriptionAppendsMacVersion() {
    Map<String, String> values = values();

    assertEquals("21;Linux;14.5",
        IssueReportPaneHelper.buildEnvironmentShortDescription(Arrays.asList("java.version", "os.name"), values::get, true, "14.5"));
  }

  @Test
  public void buildEnvironmentDescriptionsHandleEmptyPropertyLists() {
    Map<String, String> values = values();

    assertEquals("", IssueReportPaneHelper.buildEnvironmentLongDescription(List.of(), values::get));
    assertEquals("", IssueReportPaneHelper.buildEnvironmentShortDescription(List.of(), values::get, false, "ignored"));
    assertEquals(";14.5", IssueReportPaneHelper.buildEnvironmentShortDescription(List.of(), values::get, true, "14.5"));
  }

  private static Map<String, String> values() {
    Map<String, String> values = new HashMap<>();
    values.put("java.version", "21");
    values.put("os.name", "Linux");
    return values;
  }
}

package org.alice.ide.issue.swing.views;

import org.junit.Test;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;

public class IssueReportPaneHelperTest {
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

  private static Map<String, String> values() {
    Map<String, String> values = new HashMap<>();
    values.put("java.version", "21");
    values.put("os.name", "Linux");
    return values;
  }
}

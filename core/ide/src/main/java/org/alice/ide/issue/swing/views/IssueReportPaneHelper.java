package org.alice.ide.issue.swing.views;

import java.util.List;
import java.util.function.Function;

final class IssueReportPaneHelper {
  private IssueReportPaneHelper() {
    throw new AssertionError();
  }

  static String buildEnvironmentLongDescription(List<String> propertyNames, Function<String, String> propertyLookup) {
    StringBuilder sb = new StringBuilder();
    String interstitial = "";
    for (String propertyName : propertyNames) {
      sb.append(interstitial);
      sb.append(propertyName);
      sb.append(": ");
      sb.append(propertyLookup.apply(propertyName));
      interstitial = "\n";
    }
    return sb.toString();
  }

  static String buildEnvironmentShortDescription(List<String> propertyNames, Function<String, String> propertyLookup, boolean isMac, String osVersion) {
    StringBuilder sb = new StringBuilder();
    String interstitial = "";
    for (String propertyName : propertyNames) {
      sb.append(interstitial);
      sb.append(propertyLookup.apply(propertyName));
      interstitial = ";";
    }
    if (isMac) {
      sb.append(";");
      sb.append(osVersion);
    }
    return sb.toString();
  }
}

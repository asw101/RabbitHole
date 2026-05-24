package org.alice.ide.instancefactory.croquet;

final class InstanceFactoryStateLogic {
  private InstanceFactoryStateLogic() {
    throw new AssertionError();
  }

  static String buildParametersVariablesConstantsText(String codeText, boolean hasParameters, boolean hasVariables, boolean hasConstants) {
    StringBuilder sb = new StringBuilder();
    sb.append(codeText);
    sb.append(" ");
    String prefix = "";
    if (hasParameters) {
      sb.append("parameters");
      prefix = ", ";
    }
    if (hasVariables) {
      sb.append(prefix);
      sb.append("variables");
      prefix = ", ";
    }
    if (hasConstants) {
      sb.append(prefix);
      sb.append("constants");
    }
    return sb.toString();
  }
}

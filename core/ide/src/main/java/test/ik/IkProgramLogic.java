package test.ik;

final class IkProgramLogic {
  private IkProgramLogic() {
    throw new AssertionError();
  }

  static String buildInfoText(String jointIdText, String jointTransformText, String targetTransformText) {
    StringBuilder sb = new StringBuilder(256);
    if (jointIdText != null) {
      sb.append(jointIdText);
      sb.append(":\n");
      if (jointTransformText != null) {
        sb.append(jointTransformText);
      }
    }
    sb.append("\n");
    sb.append("target:\n");
    if (targetTransformText != null) {
      sb.append(targetTransformText);
    }
    sb.append("\n");
    return sb.toString();
  }
}

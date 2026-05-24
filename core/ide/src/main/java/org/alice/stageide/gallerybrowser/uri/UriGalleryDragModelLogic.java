package org.alice.stageide.gallerybrowser.uri;

import org.alice.ide.ast.export.type.FieldInfo;
import org.alice.ide.ast.export.type.FunctionInfo;
import org.alice.ide.ast.export.type.TypeSummary;

import java.util.List;

final class UriGalleryDragModelLogic {
  private UriGalleryDragModelLogic() {
    throw new AssertionError();
  }

  static void appendStartIfNecessary(StringBuilder sb, String fileName) {
    if (sb.isEmpty()) {
      sb.append("<html>");
      if (fileName != null) {
        sb.append("add from file: <strong>");
        sb.append(fileName);
        sb.append("</strong><p><p>");
      }
    }
  }

  static String buildTypeSummaryToolTipText(TypeSummary typeSummary, String fileName) {
    if (typeSummary == null) {
      return "unknown";
    }
    StringBuilder sb = new StringBuilder();
    List<String> procedureNames = typeSummary.getProcedureNames();
    if (!procedureNames.isEmpty()) {
      appendStartIfNecessary(sb, fileName);
      sb.append("<em>procedures:</em><ul>");
      for (String procedureName : procedureNames) {
        sb.append("<li><strong>");
        sb.append(procedureName);
        sb.append("</strong>");
      }
      sb.append("</ul>");
    }

    List<FunctionInfo> functionInfos = typeSummary.getFunctionInfos();
    if (!functionInfos.isEmpty()) {
      appendStartIfNecessary(sb, fileName);
      sb.append("<em>functions:</em><ul>");
      for (FunctionInfo functionInfo : functionInfos) {
        sb.append("<li>");
        sb.append(functionInfo.getReturnClassName());
        sb.append(" <strong>");
        sb.append(functionInfo.getName());
        sb.append("</strong>");
      }
      sb.append("</ul>");
    }

    List<FieldInfo> fieldInfos = typeSummary.getFieldInfos();
    if (!fieldInfos.isEmpty()) {
      appendStartIfNecessary(sb, fileName);
      sb.append("<em>properties:</em><ul>");
      for (FieldInfo fieldInfo : fieldInfos) {
        sb.append("<li>");
        sb.append(fieldInfo.getValueClassName());
        sb.append(" <strong>");
        sb.append(fieldInfo.getName());
        sb.append("</strong>");
      }
      sb.append("</ul>");
    }
    if (sb.isEmpty()) {
      sb.append("<html>nothing of note");
    }
    sb.append("</html>");
    return sb.toString();
  }

  static String createLocalizedText(String typeName, String localizedCreationText, boolean resourceClassIsInterface,
                                    String fallbackText, String fileName, String baseName, String fromFormat) {
    String text;
    if (localizedCreationText != null) {
      text = resourceClassIsInterface ? typeName : localizedCreationText;
    } else {
      text = fallbackText;
    }

    if ((typeName != null) && (fileName != null) && !typeName.contentEquals(baseName)) {
      text = "<html>" + text + " <em>" + fromFormat + "</em></html>";
    }
    return text;
  }
}

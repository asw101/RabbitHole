package org.alice.ide.ast.type.merge.croquet.views;

import java.awt.Component;
import java.awt.Rectangle;
import java.util.List;

final class MembersViewLogic {
  private MembersViewLogic() {
    throw new AssertionError();
  }

  static String createDifferentSignatureToolText(String titleText, String memberName, boolean isMethod) {
    StringBuilder sb = new StringBuilder();
    sb.append("<html>\"");
    sb.append(memberName);
    sb.append("\" ");
    sb.append(titleText);
    sb.append(isMethod ? " have different signatures." : " have different value classes.");
    sb.append("<p><strong>You must change at least one of their names.</strong></html>");
    return sb.toString();
  }

  static String createDifferentImplementationToolText(String titleText, String memberName, boolean isMethod) {
    StringBuilder sb = new StringBuilder();
    sb.append("<html>\"");
    sb.append(memberName);
    sb.append("\" ");
    sb.append(titleText);
    sb.append(isMethod ? " have different implementations." : " have different initializers.");
    sb.append("<p><strong>You must change at least one of their names.</strong></html>");
    return sb.toString();
  }

  static String createIdenticalToolText(String titleText, String memberName) {
    return "<html>\"" + memberName + "\" " + titleText + "  are identical.<p>No action is required.</html>";
  }

  static Rectangle getRowBounds(List<? extends Component> row) {
    if (!row.isEmpty()) {
      Rectangle rv = null;
      for (Component awtComponent : row) {
        Rectangle bounds = awtComponent.getBounds();
        rv = rv != null ? rv.union(bounds) : bounds;
      }
      return rv;
    }
    return new Rectangle(0, 0, 0, 0);
  }
}

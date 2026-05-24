package org.alice.stageide.type.croquet;

import edu.cmu.cs.dennisc.java.util.Lists;
import org.lgna.croquet.data.ListData;
import org.lgna.project.annotations.Visibility;
import org.lgna.project.ast.AbstractField;
import org.lgna.project.ast.AbstractMember;
import org.lgna.project.ast.AbstractMethod;
import org.lgna.project.ast.AbstractType;
import org.lgna.project.ast.JavaType;
import org.lgna.project.ast.UserField;

import java.util.List;
import java.util.Map;

final class OtherTypeDialogLogic {
  private OtherTypeDialogLogic() {
    throw new AssertionError();
  }

  static String getNotAssignableErrorText(JavaType rootFilterType) {
    StringBuilder sb = new StringBuilder();
    sb.append("Select class assignable to ");
    if (rootFilterType != null) {
      sb.append(rootFilterType.getName());
    }
    return sb.toString();
  }

  static boolean isSelectionAssignable(JavaType rootFilterType, TypeNode typeNode) {
    if (typeNode == null) {
      return false;
    }
    AbstractType<?, ?, ?> type = typeNode.getType();
    return (rootFilterType == null) || rootFilterType.isAssignableFrom(type);
  }

  static TypeNode getSharedTypeNode(List<UserField> fields, Map<AbstractType<?, ?, ?>, TypeNode> typeNodeMap) {
    TypeNode sharedNode = null;
    for (UserField field : fields) {
      TypeNode typeNode = typeNodeMap.get(field.getValueType());
      if (sharedNode != null) {
        sharedNode = (TypeNode) sharedNode.getSharedAncestor(typeNode);
      } else {
        sharedNode = typeNode;
      }
    }
    return sharedNode;
  }

  static boolean isInclusionDesired(AbstractMember member) {
    if (member instanceof AbstractMethod method) {
      if (method.isStatic()) {
        return false;
      }
    } else if (member instanceof AbstractField field) {
      if (field.isStatic()) {
        return false;
      }
    }
    if (member.isPublicAccess() || member.isUserAuthored()) {
      Visibility visibility = member.getVisibility();
      return (visibility == null) || visibility.equals(Visibility.PRIME_TIME);
    } else {
      return false;
    }
  }

  static void appendMembers(StringBuilder sb, AbstractType<?, ?, ?> type, boolean isSelected) {
    sb.append("<h2>");
    sb.append("class ");
    sb.append(type.getName());
    if (isSelected) {
      sb.append("</h2>");
    } else {
      sb.append(" <em>(inherit)</em></h2>");
    }

    List<? extends AbstractMethod> methods = type.getDeclaredMethods();

    boolean isFirst = true;
    for (AbstractMethod method : methods) {
      if (isInclusionDesired(method) && method.isProcedure()) {
        if (isFirst) {
          sb.append("<em>procedures</em><ul>");
          isFirst = false;
        }
        sb.append("<li>");
        sb.append(method.getName());
        sb.append("</li>");
      }
    }
    if (!isFirst) {
      sb.append("</ul>");
    }
    isFirst = true;
    for (AbstractMethod method : methods) {
      if (isInclusionDesired(method) && method.isFunction()) {
        if (isFirst) {
          sb.append("<em>functions</em><ul>");
          isFirst = false;
        }
        sb.append("<li>");
        sb.append(method.getName());
        sb.append("</li>");
      }
    }
    if (!isFirst) {
      sb.append("</ul>");
    }

    isFirst = true;
    for (AbstractField field : type.getDeclaredFields()) {
      if (isInclusionDesired(field)) {
        if (isFirst) {
          sb.append("<em>properties</em><ul>");
          isFirst = false;
        }
        sb.append("<li>");
        sb.append(field.getName());
        sb.append("</li>");
      }
    }
    if (!isFirst) {
      sb.append("</ul>");
    }

    if (type.isFollowToSuperClassDesired() && (type.getSuperType() != null)) {
      appendMembers(sb, type.getSuperType(), false);
    }
  }

  static String createDescriptionHtml(AbstractType<?, ?, ?> type) {
    StringBuilder sb = new StringBuilder();
    sb.append("<html><body bgcolor=\"#FFFFFF\">");
    if (type != null) {
      appendMembers(sb, type, true);
    } else {
      sb.append("<em>no class selected</em>");
    }
    sb.append("</body></html>");
    return sb.toString();
  }

  static List<UserField> filterAssignableFields(AbstractType<?, ?, ?> type, ListData<UserField> data) {
    List<UserField> fields = Lists.newLinkedList();
    if (type != null) {
      synchronized (data) {
        final int N = data.getItemCount();
        for (int i = 0; i < N; i++) {
          UserField item = data.getItemAt(i);
          if (type.isAssignableFrom(item.getValueType())) {
            fields.add(item);
          }
        }
      }
    }
    return fields;
  }
}

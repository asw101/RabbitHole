package org.alice.ide.croquet.models.project.find.croquet.tree;

import org.alice.ide.croquet.models.project.find.core.SearchResult;
import org.alice.ide.croquet.models.project.find.croquet.tree.nodes.SearchTreeNode;
import org.lgna.project.ast.AbstractDeclaration;
import org.lgna.project.ast.Expression;
import org.lgna.project.ast.UserLambda;
import org.lgna.project.ast.UserMethod;

import java.util.ArrayList;
import java.util.List;

final class FindReferencesTreeStateLogic {
  static final class ReferenceGroup {
    private final AbstractDeclaration declaration;
    private final List<Expression> references = new ArrayList<>();

    ReferenceGroup(AbstractDeclaration declaration) {
      this.declaration = declaration;
    }

    AbstractDeclaration getDeclaration() {
      return declaration;
    }

    List<Expression> getReferences() {
      return references;
    }
  }

  static final class TreeCoordinate {
    private final int a;
    private final int b;

    TreeCoordinate(int a, int b) {
      this.a = a;
      this.b = b;
    }

    int getA() {
      return a;
    }

    int getB() {
      return b;
    }
  }

  private FindReferencesTreeStateLogic() {
    throw new AssertionError();
  }

  static List<ReferenceGroup> groupReferences(SearchResult searchObject) {
    List<ReferenceGroup> groups = new ArrayList<>();
    if (searchObject == null) {
      return groups;
    }
    for (Expression reference : searchObject.getReferences()) {
      AbstractDeclaration parentObject = reference.getFirstAncestorAssignableTo(UserLambda.class);
      if (parentObject == null) {
        parentObject = reference.getFirstAncestorAssignableTo(UserMethod.class);
      }
      if (parentObject == null) {
        continue;
      }
      getOrCreateGroup(groups, parentObject).getReferences().add(reference);
    }
    return groups;
  }

  private static ReferenceGroup getOrCreateGroup(List<ReferenceGroup> groups, AbstractDeclaration declaration) {
    for (ReferenceGroup group : groups) {
      if (group.getDeclaration().equals(declaration)) {
        return group;
      }
    }
    ReferenceGroup group = new ReferenceGroup(declaration);
    groups.add(group);
    return group;
  }

  static SearchTreeNode moveSelectedUpOne(SearchTreeNode root, SearchTreeNode selected) {
    if (selected.getParent() == root) {
      if (selected.getLocationAmongstSiblings() > 0) {
        SearchTreeNode olderSibling = selected.getOlderSibling();
        return olderSibling.getChildren().get(olderSibling.getChildren().size() - 1);
      }
    } else if (selected.getLocationAmongstSiblings() > 0) {
      return selected.getOlderSibling();
    } else {
      return selected.getParent();
    }
    return selected;
  }

  static SearchTreeNode moveSelectedDownOne(SearchTreeNode root, SearchTreeNode selected) {
    if (selected.getParent() == root) {
      return selected.getChildren().getFirst();
    }
    if (selected.getLocationAmongstSiblings() < (selected.getParent().getChildren().size() - 1)) {
      return selected.getYoungerSibling();
    }
    if (selected.getParent().getLocationAmongstSiblings() < (selected.getParent().getParent().getChildren().size() - 1)) {
      return selected.getParent().getYoungerSibling();
    }
    return selected;
  }

  static SearchTreeNode selectAtCoordinates(SearchTreeNode root, int a, int b) {
    if (b == -1) {
      return root.getChildren().get(a);
    }
    return root.getChildren().get(a).getChildren().get(b);
  }

  static boolean hasRows(SearchTreeNode root) {
    return !root.getChildren().isEmpty();
  }

  static SearchTreeNode getTopValue(SearchTreeNode root) {
    return root.getChildren().getFirst();
  }

  static TreeCoordinate getSelectedCoordinates(SearchTreeNode root, SearchTreeNode value) {
    if (value.getParent() == root) {
      return new TreeCoordinate(value.getLocationAmongstSiblings(), -1);
    }
    return new TreeCoordinate(value.getParent().getLocationAmongstSiblings(), value.getLocationAmongstSiblings());
  }
}

package edu.cmu.cs.dennisc.pattern;

import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class VisitUtilitiesTest {
  private static class Node implements Visitable {
    protected final String name;
    private final List<Node> children = new ArrayList<>();

    private Node(String name, Node... children) {
      this.name = name;
      java.util.Collections.addAll(this.children, children);
    }

    @Override
    public void accept(Visitor visitor) {
      visitor.visit(this);
      for (Node child : this.children) {
        child.accept(visitor);
      }
    }
  }

  private static class SpecialNode extends Node {
    private SpecialNode(String name, Node... children) {
      super(name, children);
    }
  }

  @Test
  public void getAllReturnsEveryMatchingNode() {
    Node root = new Node("root", new SpecialNode("first"), new Node("middle", new SpecialNode("second")));

    List<SpecialNode> matches = new ArrayList<>();
    for (SpecialNode node : VisitUtilities.getAll(root, SpecialNode.class)) {
      matches.add(node);
    }

    Assert.assertEquals(2, matches.size());
    Assert.assertEquals("first", matches.get(0).name);
    Assert.assertEquals("second", matches.get(1).name);
  }

  @Test
  public void getFirstReturnsFirstMatchingNodeOrNull() {
    Node root = new Node("root", new Node("middle", new SpecialNode("second")), new SpecialNode("third"));

    SpecialNode first = VisitUtilities.getFirst(root, SpecialNode.class);
    Node missing = VisitUtilities.getFirst(root, VisitUtilitiesTest.Node.class);

    Assert.assertEquals("second", first.name);
    Assert.assertSame(root, missing);
    Assert.assertNull(VisitUtilities.getFirst(new Node("plain"), SpecialNode.class));
  }
}

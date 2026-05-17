package edu.cmu.cs.dennisc.pattern;

import org.junit.Test;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.*;

public class IsInstanceCrawlerTest {

  // Simple Crawlable implementation for testing
  static class TestNode implements Crawlable {
    private final String name;
    private final List<TestNode> children;

    TestNode(String name, TestNode... children) {
      this.name = name;
      this.children = List.of(children);
    }

    String getName() {
      return name;
    }

    @Override
    public void accept(Crawler crawler, Set<Crawlable> visited) {
      if (visited.add(this)) {
        crawler.visit(this);
        for (TestNode child : children) {
          child.accept(crawler, visited);
        }
      }
    }
  }

  static class SpecialNode extends TestNode {
    SpecialNode(String name, TestNode... children) {
      super(name, children);
    }
  }

  @Test
  public void createInstance_findsMatchingType() {
    IsInstanceCrawler<TestNode> crawler = IsInstanceCrawler.createInstance(TestNode.class);
    TestNode node = new TestNode("root");
    node.accept(crawler, new HashSet<>());
    List<TestNode> found = crawler.getList();
    assertEquals(1, found.size());
    assertEquals("root", found.get(0).getName());
  }

  @Test
  public void createInstance_findsMultipleNodes() {
    TestNode child1 = new TestNode("child1");
    TestNode child2 = new TestNode("child2");
    TestNode root = new TestNode("root", child1, child2);
    IsInstanceCrawler<TestNode> crawler = IsInstanceCrawler.createInstance(TestNode.class);
    root.accept(crawler, new HashSet<>());
    assertEquals(3, crawler.getList().size());
  }

  @Test
  public void createInstance_findsSubtypes() {
    SpecialNode special = new SpecialNode("special");
    TestNode root = new TestNode("root", special);
    IsInstanceCrawler<TestNode> crawler = IsInstanceCrawler.createInstance(TestNode.class);
    root.accept(crawler, new HashSet<>());
    assertEquals(2, crawler.getList().size());
  }

  @Test
  public void createInstance_filtersByExactType() {
    SpecialNode special = new SpecialNode("special");
    TestNode regular = new TestNode("regular");
    TestNode root = new TestNode("root", special, regular);
    IsInstanceCrawler<SpecialNode> crawler = IsInstanceCrawler.createInstance(SpecialNode.class);
    root.accept(crawler, new HashSet<>());
    assertEquals(1, crawler.getList().size());
    assertEquals("special", crawler.getList().get(0).getName());
  }

  @Test
  public void visit_null_isIgnored() {
    IsInstanceCrawler<TestNode> crawler = IsInstanceCrawler.createInstance(TestNode.class);
    crawler.visit(null);
    assertTrue(crawler.getList().isEmpty());
  }

  @Test
  public void visit_nonMatchingType_isIgnored() {
    IsInstanceCrawler<SpecialNode> crawler = IsInstanceCrawler.createInstance(SpecialNode.class);
    crawler.visit(new TestNode("plain"));
    assertTrue(crawler.getList().isEmpty());
  }

  @Test
  public void getList_initiallyEmpty() {
    IsInstanceCrawler<TestNode> crawler = IsInstanceCrawler.createInstance(TestNode.class);
    assertNotNull(crawler.getList());
    assertTrue(crawler.getList().isEmpty());
  }

  @Test
  public void customCrawler_withFilter() {
    IsInstanceCrawler<TestNode> crawler = new IsInstanceCrawler<TestNode>(TestNode.class) {
      @Override
      protected boolean isAcceptable(TestNode e) {
        return e.getName().startsWith("c");
      }
    };
    TestNode child1 = new TestNode("child1");
    TestNode child2 = new TestNode("child2");
    TestNode root = new TestNode("root", child1, child2);
    root.accept(crawler, new HashSet<>());
    assertEquals(2, crawler.getList().size());
  }

  @Test
  public void customCrawler_rejectsAll() {
    IsInstanceCrawler<TestNode> crawler = new IsInstanceCrawler<TestNode>(TestNode.class) {
      @Override
      protected boolean isAcceptable(TestNode e) {
        return false;
      }
    };
    TestNode root = new TestNode("root", new TestNode("a"), new TestNode("b"));
    root.accept(crawler, new HashSet<>());
    assertTrue(crawler.getList().isEmpty());
  }
}

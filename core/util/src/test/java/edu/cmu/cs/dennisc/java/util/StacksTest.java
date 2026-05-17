package edu.cmu.cs.dennisc.java.util;

import org.junit.Test;

import static org.junit.Assert.*;

public class StacksTest {

  @Test
  public void newStack_empty() {
    DStack<String> stack = Stacks.newStack();
    assertNotNull(stack);
    assertTrue(stack.isEmpty());
    assertEquals(0, stack.size());
  }

  @Test
  public void newStack_fromVarargs() {
    DStack<String> stack = Stacks.newStack("a", "b", "c");
    assertFalse(stack.isEmpty());
    assertEquals(3, stack.size());
  }

  @Test
  public void push_and_peek() {
    DStack<Integer> stack = Stacks.newStack();
    stack.push(42);
    assertEquals(Integer.valueOf(42), stack.peek());
    assertEquals(1, stack.size());
  }

  @Test
  public void push_and_pop() {
    DStack<String> stack = Stacks.newStack();
    stack.push("first");
    stack.push("second");
    assertEquals("second", stack.pop());
    assertEquals("first", stack.pop());
    assertTrue(stack.isEmpty());
  }

  @Test
  public void pop_returns_lifo_order() {
    DStack<String> stack = Stacks.newStack("a", "b", "c");
    assertEquals("c", stack.pop());
    assertEquals("b", stack.pop());
    assertEquals("a", stack.pop());
  }

  @Test
  public void get_byIndex() {
    DStack<String> stack = Stacks.newStack("x", "y", "z");
    assertEquals("x", stack.get(0));
    assertEquals("y", stack.get(1));
    assertEquals("z", stack.get(2));
  }

  @Test
  public void clear_emptiesStack() {
    DStack<Integer> stack = Stacks.newStack(1, 2, 3);
    assertEquals(3, stack.size());
    stack.clear();
    assertTrue(stack.isEmpty());
    assertEquals(0, stack.size());
  }

  @Test
  public void setSize_truncates() {
    DStack<String> stack = Stacks.newStack("a", "b", "c", "d");
    assertEquals(4, stack.size());
    stack.setSize(2);
    assertEquals(2, stack.size());
    assertEquals("a", stack.get(0));
    assertEquals("b", stack.get(1));
  }

  @Test
  public void peek_doesNotRemove() {
    DStack<String> stack = Stacks.newStack("only");
    assertEquals("only", stack.peek());
    assertEquals(1, stack.size());
    assertEquals("only", stack.peek());
  }

  @Test
  public void push_after_clear() {
    DStack<Integer> stack = Stacks.newStack(1, 2);
    stack.clear();
    stack.push(99);
    assertEquals(1, stack.size());
    assertEquals(Integer.valueOf(99), stack.peek());
  }
}

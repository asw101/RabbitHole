package edu.cmu.cs.dennisc.java.util;

import org.junit.Test;

import java.util.Arrays;
import java.util.concurrent.ConcurrentLinkedQueue;

import static org.junit.Assert.*;

public class QueuesDeepTest {

  @Test
  public void newConcurrentLinkedQueue_empty() {
    ConcurrentLinkedQueue<String> q = Queues.newConcurrentLinkedQueue();
    assertNotNull(q);
    assertTrue(q.isEmpty());
    assertEquals(0, q.size());
  }

  @Test
  public void newConcurrentLinkedQueue_varargs_single() {
    ConcurrentLinkedQueue<String> q = Queues.newConcurrentLinkedQueue("a");
    assertEquals(1, q.size());
    assertEquals("a", q.poll());
  }

  @Test
  public void newConcurrentLinkedQueue_varargs_multiple() {
    ConcurrentLinkedQueue<Integer> q = Queues.newConcurrentLinkedQueue(1, 2, 3);
    assertEquals(3, q.size());
    assertEquals(Integer.valueOf(1), q.poll());
    assertEquals(Integer.valueOf(2), q.poll());
    assertEquals(Integer.valueOf(3), q.poll());
    assertTrue(q.isEmpty());
  }

  @Test
  public void newConcurrentLinkedQueue_fromCollection() {
    ConcurrentLinkedQueue<String> q = Queues.newConcurrentLinkedQueue(Arrays.asList("x", "y", "z"));
    assertEquals(3, q.size());
    assertEquals("x", q.poll());
    assertEquals("y", q.poll());
    assertEquals("z", q.poll());
  }

  @Test
  public void newConcurrentLinkedQueue_fromEmptyCollection() {
    ConcurrentLinkedQueue<Integer> q = Queues.newConcurrentLinkedQueue(Arrays.asList());
    assertTrue(q.isEmpty());
  }

  @Test
  public void newConcurrentLinkedQueue_isConcurrent() {
    ConcurrentLinkedQueue<String> q = Queues.newConcurrentLinkedQueue("a", "b");
    assertTrue(q instanceof ConcurrentLinkedQueue);
  }

  @Test
  public void newConcurrentLinkedQueue_preservesOrder() {
    ConcurrentLinkedQueue<String> q = Queues.newConcurrentLinkedQueue("first", "second", "third");
    assertEquals("first", q.peek());
    q.poll();
    assertEquals("second", q.peek());
    q.poll();
    assertEquals("third", q.peek());
  }
}

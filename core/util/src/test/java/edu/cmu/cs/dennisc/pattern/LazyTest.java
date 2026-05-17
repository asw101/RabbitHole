package edu.cmu.cs.dennisc.pattern;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Tests for Lazy — lazy initialization, memoization,
 * and peek before/after get.
 */
public class LazyTest {

  private static class StringLazy extends Lazy<String> {
    private int createCount = 0;

    @Override
    protected String create() {
      createCount++;
      return "lazy-value-" + createCount;
    }

    public int getCreateCount() {
      return createCount;
    }
  }

  private static class IntegerLazy extends Lazy<Integer> {
    private final int value;

    IntegerLazy(int value) {
      this.value = value;
    }

    @Override
    protected Integer create() {
      return value;
    }
  }

  // --- get() ---

  @Test
  public void get_returnsCreatedValue() {
    StringLazy lazy = new StringLazy();
    String result = lazy.get();
    assertEquals("lazy-value-1", result);
  }

  @Test
  public void get_memoizes() {
    StringLazy lazy = new StringLazy();
    String first = lazy.get();
    String second = lazy.get();
    assertSame(first, second);
    assertEquals(1, lazy.getCreateCount());
  }

  @Test
  public void get_callsCreateOnlyOnce() {
    StringLazy lazy = new StringLazy();
    lazy.get();
    lazy.get();
    lazy.get();
    assertEquals(1, lazy.getCreateCount());
  }

  // --- peek() ---

  @Test
  public void peek_beforeGet_returnsNull() {
    StringLazy lazy = new StringLazy();
    assertNull(lazy.peek());
  }

  @Test
  public void peek_afterGet_returnsValue() {
    StringLazy lazy = new StringLazy();
    lazy.get();
    assertNotNull(lazy.peek());
    assertEquals("lazy-value-1", lazy.peek());
  }

  @Test
  public void peek_doesNotTriggerCreation() {
    StringLazy lazy = new StringLazy();
    lazy.peek();
    assertEquals(0, lazy.getCreateCount());
  }

  @Test
  public void peek_afterGet_returnsSameAsGet() {
    StringLazy lazy = new StringLazy();
    String gotten = lazy.get();
    String peeked = lazy.peek();
    assertSame(gotten, peeked);
  }

  // --- Value types ---

  @Test
  public void get_integerValue() {
    IntegerLazy lazy = new IntegerLazy(42);
    assertEquals(Integer.valueOf(42), lazy.get());
  }

  @Test
  public void get_nullValue() {
    Lazy<String> nullLazy = new Lazy<String>() {
      @Override
      protected String create() {
        return null;
      }
    };
    assertNull(nullLazy.get());
  }

  @Test
  public void get_nullValue_memoized() {
    final int[] count = {0};
    Lazy<String> nullLazy = new Lazy<String>() {
      @Override
      protected String create() {
        count[0]++;
        return null;
      }
    };
    nullLazy.get();
    nullLazy.get();
    // create should still only be called once even for null
    assertEquals(1, count[0]);
  }

  // --- Thread safety (basic check) ---

  @Test
  public void get_threadSafety() throws InterruptedException {
    final IntegerLazy lazy = new IntegerLazy(99);
    final Integer[] results = new Integer[10];
    Thread[] threads = new Thread[10];

    for (int i = 0; i < 10; i++) {
      final int idx = i;
      threads[i] = new Thread(() -> results[idx] = lazy.get());
      threads[i].start();
    }

    for (Thread t : threads) {
      t.join();
    }

    for (Integer result : results) {
      assertEquals(Integer.valueOf(99), result);
    }
  }
}

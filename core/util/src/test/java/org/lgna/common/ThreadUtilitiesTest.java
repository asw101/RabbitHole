package org.lgna.common;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.Assert.*;

public class ThreadUtilitiesTest {

  private Runnable[] createCountingRunnables(int count, AtomicInteger counter) {
    Runnable[] runnables = new Runnable[count];
    for (int i = 0; i < count; i++) {
      runnables[i] = new Runnable() {
        @Override
        public void run() {
          counter.incrementAndGet();
        }
      };
    }
    return runnables;
  }

  private Integer[] integers(int count) {
    Integer[] values = new Integer[count];
    for (int i = 0; i < count; i++) {
      values[i] = i;
    }
    return values;
  }

  private long arithmeticSum(int count) {
    return ((long) count * (count - 1)) / 2L;
  }

  @Test
  public void doTogetherWith0RunnablesExecutesExpectedCount() {
    AtomicInteger counter = new AtomicInteger(0);
    Runnable[] runnables = createCountingRunnables(0, counter);
    ThreadUtilities.doTogether(runnables);
    assertEquals(0, counter.get());
    assertEquals(0, runnables.length);
  }

  @Test
  public void doTogetherWith1RunnablesExecutesExpectedCount() {
    AtomicInteger counter = new AtomicInteger(0);
    Runnable[] runnables = createCountingRunnables(1, counter);
    ThreadUtilities.doTogether(runnables);
    assertEquals(1, counter.get());
    assertEquals(1, runnables.length);
  }

  @Test
  public void doTogetherWith2RunnablesExecutesExpectedCount() {
    AtomicInteger counter = new AtomicInteger(0);
    Runnable[] runnables = createCountingRunnables(2, counter);
    ThreadUtilities.doTogether(runnables);
    assertEquals(2, counter.get());
    assertEquals(2, runnables.length);
  }

  @Test
  public void doTogetherWith3RunnablesExecutesExpectedCount() {
    AtomicInteger counter = new AtomicInteger(0);
    Runnable[] runnables = createCountingRunnables(3, counter);
    ThreadUtilities.doTogether(runnables);
    assertEquals(3, counter.get());
    assertEquals(3, runnables.length);
  }

  @Test
  public void doTogetherWith4RunnablesExecutesExpectedCount() {
    AtomicInteger counter = new AtomicInteger(0);
    Runnable[] runnables = createCountingRunnables(4, counter);
    ThreadUtilities.doTogether(runnables);
    assertEquals(4, counter.get());
    assertEquals(4, runnables.length);
  }

  @Test
  public void doTogetherWith5RunnablesExecutesExpectedCount() {
    AtomicInteger counter = new AtomicInteger(0);
    Runnable[] runnables = createCountingRunnables(5, counter);
    ThreadUtilities.doTogether(runnables);
    assertEquals(5, counter.get());
    assertEquals(5, runnables.length);
  }

  @Test
  public void doTogetherWith7RunnablesExecutesExpectedCount() {
    AtomicInteger counter = new AtomicInteger(0);
    Runnable[] runnables = createCountingRunnables(7, counter);
    ThreadUtilities.doTogether(runnables);
    assertEquals(7, counter.get());
    assertEquals(7, runnables.length);
  }

  @Test
  public void doTogetherWith10RunnablesExecutesExpectedCount() {
    AtomicInteger counter = new AtomicInteger(0);
    Runnable[] runnables = createCountingRunnables(10, counter);
    ThreadUtilities.doTogether(runnables);
    assertEquals(10, counter.get());
    assertEquals(10, runnables.length);
  }

  @Test
  public void doTogetherWith15RunnablesExecutesExpectedCount() {
    AtomicInteger counter = new AtomicInteger(0);
    Runnable[] runnables = createCountingRunnables(15, counter);
    ThreadUtilities.doTogether(runnables);
    assertEquals(15, counter.get());
    assertEquals(15, runnables.length);
  }

  @Test
  public void doTogetherWith20RunnablesExecutesExpectedCount() {
    AtomicInteger counter = new AtomicInteger(0);
    Runnable[] runnables = createCountingRunnables(20, counter);
    ThreadUtilities.doTogether(runnables);
    assertEquals(20, counter.get());
    assertEquals(20, runnables.length);
  }

  @Test
  public void doTogetherWith30RunnablesExecutesExpectedCount() {
    AtomicInteger counter = new AtomicInteger(0);
    Runnable[] runnables = createCountingRunnables(30, counter);
    ThreadUtilities.doTogether(runnables);
    assertEquals(30, counter.get());
    assertEquals(30, runnables.length);
  }

  @Test
  public void doTogetherWith50RunnablesExecutesExpectedCount() {
    AtomicInteger counter = new AtomicInteger(0);
    Runnable[] runnables = createCountingRunnables(50, counter);
    ThreadUtilities.doTogether(runnables);
    assertEquals(50, counter.get());
    assertEquals(50, runnables.length);
  }

  @Test
  public void doTogetherWith100RunnablesExecutesExpectedCount() {
    AtomicInteger counter = new AtomicInteger(0);
    Runnable[] runnables = createCountingRunnables(100, counter);
    ThreadUtilities.doTogether(runnables);
    assertEquals(100, counter.get());
    assertEquals(100, runnables.length);
  }

  @Test
  public void doTogetherSingleRunnableRunsOnCallingThread() {
    AtomicReference<Thread> threadReference = new AtomicReference<Thread>();
    ThreadUtilities.doTogether(new Runnable() {
      @Override
      public void run() {
        threadReference.set(Thread.currentThread());
      }
    });
    assertSame(Thread.currentThread(), threadReference.get());
  }

  @Test
  public void doTogetherRunsTwoRunnablesConcurrently() {
    CountDownLatch started = new CountDownLatch(2);
    AtomicBoolean overlapped = new AtomicBoolean(false);
    Runnable runnable = new Runnable() {
      @Override
      public void run() {
        started.countDown();
        try {
          if (started.await(2, TimeUnit.SECONDS)) {
            overlapped.set(true);
          }
        } catch (InterruptedException interruptedException) {
          throw new RuntimeException(interruptedException);
        }
      }
    };
    ThreadUtilities.doTogether(runnable, runnable);
    assertTrue(overlapped.get());
  }

  @Test
  public void doTogetherRunsThreeRunnablesConcurrently() {
    CountDownLatch started = new CountDownLatch(3);
    AtomicBoolean overlapped = new AtomicBoolean(false);
    Runnable runnable = new Runnable() {
      @Override
      public void run() {
        started.countDown();
        try {
          if (started.await(2, TimeUnit.SECONDS)) {
            overlapped.set(true);
          }
        } catch (InterruptedException interruptedException) {
          throw new RuntimeException(interruptedException);
        }
      }
    };
    ThreadUtilities.doTogether(runnable, runnable, runnable);
    assertTrue(overlapped.get());
  }

  @Test
  public void doTogetherRunsFiveRunnablesConcurrently() {
    CountDownLatch started = new CountDownLatch(5);
    AtomicBoolean overlapped = new AtomicBoolean(false);
    Runnable runnable = new Runnable() {
      @Override
      public void run() {
        started.countDown();
        try {
          if (started.await(2, TimeUnit.SECONDS)) {
            overlapped.set(true);
          }
        } catch (InterruptedException interruptedException) {
          throw new RuntimeException(interruptedException);
        }
      }
    };
    ThreadUtilities.doTogether(runnable, runnable, runnable, runnable, runnable);
    assertTrue(overlapped.get());
  }

  @Test
  public void doTogetherPropagatesRuntimeExceptionMessage() {
    try {
      ThreadUtilities.doTogether(new Runnable() {
        @Override
        public void run() {
          throw new RuntimeException("boom");
        }
      }, new Runnable() {
        @Override
        public void run() {
        }
      });
      fail("Expected RuntimeException");
    } catch (RuntimeException runtimeException) {
      assertEquals("boom", runtimeException.getMessage());
    }
  }

  @Test
  public void doTogetherRunsOtherRunnablesEvenWhenOneFails() {
    AtomicBoolean otherRan = new AtomicBoolean(false);
    try {
      ThreadUtilities.doTogether(new Runnable() {
        @Override
        public void run() {
          throw new RuntimeException("boom");
        }
      }, new Runnable() {
        @Override
        public void run() {
          otherRan.set(true);
        }
      });
      fail("Expected RuntimeException");
    } catch (RuntimeException runtimeException) {
      assertEquals("boom", runtimeException.getMessage());
    }
    assertTrue(otherRan.get());
  }

  @Test
  public void doTogetherSupportsSharedStateAcrossRunnables() {
    AtomicLong sum = new AtomicLong(0L);
    ThreadUtilities.doTogether(new Runnable() {
      @Override
      public void run() {
        sum.addAndGet(10L);
      }
    }, new Runnable() {
      @Override
      public void run() {
        sum.addAndGet(20L);
      }
    }, new Runnable() {
      @Override
      public void run() {
        sum.addAndGet(30L);
      }
    });
    assertEquals(60L, sum.get());
  }

  @Test
  public void doTogetherTimingShowsConcurrentExecution() {
    long startNanos = System.nanoTime();
    Runnable sleeper = new Runnable() {
      @Override
      public void run() {
        edu.cmu.cs.dennisc.java.lang.ThreadUtilities.sleep(100L);
      }
    };
    ThreadUtilities.doTogether(sleeper, sleeper, sleeper, sleeper, sleeper);
    long elapsedMillis = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startNanos);
    assertTrue(elapsedMillis + 30L >= 100L);
    assertTrue(elapsedMillis < 400L);
  }

  @Test
  public void doTogetherSupportsNestedInvocation() {
    AtomicInteger counter = new AtomicInteger(0);
    ThreadUtilities.doTogether(new Runnable() {
      @Override
      public void run() {
        ThreadUtilities.doTogether(new Runnable() {
          @Override
          public void run() {
            counter.incrementAndGet();
          }
        }, new Runnable() {
          @Override
          public void run() {
            counter.incrementAndGet();
          }
        });
      }
    }, new Runnable() {
      @Override
      public void run() {
        counter.incrementAndGet();
      }
    });
    assertEquals(3, counter.get());
  }

  @Test
  public void doTogetherAtomicLongSumSupportsManyValues() {
    AtomicLong sum = new AtomicLong(0L);
    Runnable[] runnables = new Runnable[10];
    for (int i = 0; i < runnables.length; i++) {
      final int value = i;
      runnables[i] = new Runnable() {
        @Override
        public void run() {
          sum.addAndGet(value);
        }
      };
    }
    ThreadUtilities.doTogether(runnables);
    assertEquals(45L, sum.get());
  }

  @Test
  public void doTogetherCanBeRepeatedAcrossFiveCalls() {
    AtomicInteger counter = new AtomicInteger(0);
    for (int i = 0; i < 5; i++) {
      ThreadUtilities.doTogether(new Runnable() {
        @Override
        public void run() {
          counter.incrementAndGet();
        }
      }, new Runnable() {
        @Override
        public void run() {
          counter.incrementAndGet();
        }
      });
    }
    assertEquals(10, counter.get());
  }

  @Test
  public void doTogetherCanPopulateSharedList() {
    List<String> results = Collections.synchronizedList(new ArrayList<String>());
    ThreadUtilities.doTogether(new Runnable() {
      @Override
      public void run() {
        results.add("one");
      }
    }, new Runnable() {
      @Override
      public void run() {
        results.add("two");
      }
    }, new Runnable() {
      @Override
      public void run() {
        results.add("three");
      }
    });
    assertEquals(3, results.size());
    assertTrue(results.contains("one"));
    assertTrue(results.contains("two"));
    assertTrue(results.contains("three"));
  }

  @Test
  public void doTogetherSupportsMapPopulation() {
    Map<String, Integer> map = new ConcurrentHashMap<String, Integer>();
    ThreadUtilities.doTogether(new Runnable() {
      @Override
      public void run() {
        map.put("alpha", 1);
      }
    }, new Runnable() {
      @Override
      public void run() {
        map.put("beta", 2);
      }
    }, new Runnable() {
      @Override
      public void run() {
        map.put("gamma", 3);
      }
    });
    assertEquals(Integer.valueOf(2), map.get("beta"));
    assertEquals(3, map.size());
  }

  @Test
  public void eachInTogetherWith0ItemsProcessesExpectedCount() {
    AtomicInteger counter = new AtomicInteger(0);
    Integer[] items = integers(0);
    ThreadUtilities.eachInTogether(new EachInTogetherRunnable<Integer>() {
      @Override
      public void run(Integer item) {
        counter.incrementAndGet();
      }
    }, items);
    assertEquals(0, counter.get());
    assertEquals(0, items.length);
  }

  @Test
  public void eachInTogetherWith1ItemsProcessesExpectedCount() {
    AtomicInteger counter = new AtomicInteger(0);
    Integer[] items = integers(1);
    ThreadUtilities.eachInTogether(new EachInTogetherRunnable<Integer>() {
      @Override
      public void run(Integer item) {
        counter.incrementAndGet();
      }
    }, items);
    assertEquals(1, counter.get());
    assertEquals(1, items.length);
  }

  @Test
  public void eachInTogetherWith2ItemsProcessesExpectedCount() {
    AtomicInteger counter = new AtomicInteger(0);
    Integer[] items = integers(2);
    ThreadUtilities.eachInTogether(new EachInTogetherRunnable<Integer>() {
      @Override
      public void run(Integer item) {
        counter.incrementAndGet();
      }
    }, items);
    assertEquals(2, counter.get());
    assertEquals(2, items.length);
  }

  @Test
  public void eachInTogetherWith3ItemsProcessesExpectedCount() {
    AtomicInteger counter = new AtomicInteger(0);
    Integer[] items = integers(3);
    ThreadUtilities.eachInTogether(new EachInTogetherRunnable<Integer>() {
      @Override
      public void run(Integer item) {
        counter.incrementAndGet();
      }
    }, items);
    assertEquals(3, counter.get());
    assertEquals(3, items.length);
  }

  @Test
  public void eachInTogetherWith5ItemsProcessesExpectedCount() {
    AtomicInteger counter = new AtomicInteger(0);
    Integer[] items = integers(5);
    ThreadUtilities.eachInTogether(new EachInTogetherRunnable<Integer>() {
      @Override
      public void run(Integer item) {
        counter.incrementAndGet();
      }
    }, items);
    assertEquals(5, counter.get());
    assertEquals(5, items.length);
  }

  @Test
  public void eachInTogetherWith10ItemsProcessesExpectedCount() {
    AtomicInteger counter = new AtomicInteger(0);
    Integer[] items = integers(10);
    ThreadUtilities.eachInTogether(new EachInTogetherRunnable<Integer>() {
      @Override
      public void run(Integer item) {
        counter.incrementAndGet();
      }
    }, items);
    assertEquals(10, counter.get());
    assertEquals(10, items.length);
  }

  @Test
  public void eachInTogetherWith15ItemsProcessesExpectedCount() {
    AtomicInteger counter = new AtomicInteger(0);
    Integer[] items = integers(15);
    ThreadUtilities.eachInTogether(new EachInTogetherRunnable<Integer>() {
      @Override
      public void run(Integer item) {
        counter.incrementAndGet();
      }
    }, items);
    assertEquals(15, counter.get());
    assertEquals(15, items.length);
  }

  @Test
  public void eachInTogetherWith20ItemsProcessesExpectedCount() {
    AtomicInteger counter = new AtomicInteger(0);
    Integer[] items = integers(20);
    ThreadUtilities.eachInTogether(new EachInTogetherRunnable<Integer>() {
      @Override
      public void run(Integer item) {
        counter.incrementAndGet();
      }
    }, items);
    assertEquals(20, counter.get());
    assertEquals(20, items.length);
  }

  @Test
  public void eachInTogetherWith50ItemsProcessesExpectedCount() {
    AtomicInteger counter = new AtomicInteger(0);
    Integer[] items = integers(50);
    ThreadUtilities.eachInTogether(new EachInTogetherRunnable<Integer>() {
      @Override
      public void run(Integer item) {
        counter.incrementAndGet();
      }
    }, items);
    assertEquals(50, counter.get());
    assertEquals(50, items.length);
  }

  @Test
  public void eachInTogetherSingleItemRunsOnCallingThread() {
    AtomicReference<Thread> threadReference = new AtomicReference<Thread>();
    ThreadUtilities.eachInTogether(new EachInTogetherRunnable<String>() {
      @Override
      public void run(String item) {
        threadReference.set(Thread.currentThread());
      }
    }, "only");
    assertSame(Thread.currentThread(), threadReference.get());
  }

  @Test
  public void eachInTogetherComputesIntegerSum() {
    AtomicInteger sum = new AtomicInteger(0);
    ThreadUtilities.eachInTogether(new EachInTogetherRunnable<Integer>() {
      @Override
      public void run(Integer item) {
        sum.addAndGet(item);
      }
    }, 1, 2, 3, 4, 5);
    assertEquals(15, sum.get());
  }

  @Test
  public void eachInTogetherProcessesStrings() {
    List<String> results = Collections.synchronizedList(new ArrayList<String>());
    ThreadUtilities.eachInTogether(new EachInTogetherRunnable<String>() {
      @Override
      public void run(String item) {
        results.add(item.toUpperCase());
      }
    }, "alpha", "beta", "gamma");
    assertTrue(results.contains("ALPHA"));
    assertTrue(results.contains("BETA"));
    assertTrue(results.contains("GAMMA"));
  }

  @Test
  public void eachInTogetherPropagatesRuntimeExceptionMessage() {
    try {
      ThreadUtilities.eachInTogether(new EachInTogetherRunnable<Integer>() {
        @Override
        public void run(Integer item) {
          if (item == 2) {
            throw new RuntimeException("bad item");
          }
        }
      }, 1, 2, 3);
      fail("Expected RuntimeException");
    } catch (RuntimeException runtimeException) {
      assertEquals("bad item", runtimeException.getMessage());
    }
  }

  @Test
  public void eachInTogetherRunsTwoItemsConcurrently() {
    CountDownLatch started = new CountDownLatch(2);
    AtomicBoolean overlapped = new AtomicBoolean(false);
    ThreadUtilities.eachInTogether(new EachInTogetherRunnable<Integer>() {
      @Override
      public void run(Integer item) {
        started.countDown();
        try {
          if (started.await(2, TimeUnit.SECONDS)) {
            overlapped.set(true);
          }
        } catch (InterruptedException interruptedException) {
          throw new RuntimeException(interruptedException);
        }
      }
    }, 1, 2);
    assertTrue(overlapped.get());
  }

  @Test
  public void eachInTogetherRunsThreeItemsConcurrently() {
    CountDownLatch started = new CountDownLatch(3);
    AtomicBoolean overlapped = new AtomicBoolean(false);
    ThreadUtilities.eachInTogether(new EachInTogetherRunnable<Integer>() {
      @Override
      public void run(Integer item) {
        started.countDown();
        try {
          if (started.await(2, TimeUnit.SECONDS)) {
            overlapped.set(true);
          }
        } catch (InterruptedException interruptedException) {
          throw new RuntimeException(interruptedException);
        }
      }
    }, 1, 2, 3);
    assertTrue(overlapped.get());
  }

  @Test
  public void eachInTogetherRunsFiveItemsConcurrently() {
    CountDownLatch started = new CountDownLatch(5);
    AtomicBoolean overlapped = new AtomicBoolean(false);
    ThreadUtilities.eachInTogether(new EachInTogetherRunnable<Integer>() {
      @Override
      public void run(Integer item) {
        started.countDown();
        try {
          if (started.await(2, TimeUnit.SECONDS)) {
            overlapped.set(true);
          }
        } catch (InterruptedException interruptedException) {
          throw new RuntimeException(interruptedException);
        }
      }
    }, 1, 2, 3, 4, 5);
    assertTrue(overlapped.get());
  }

  @Test
  public void eachInTogetherTransformsValuesIntoMap() {
    Map<Integer, Integer> values = new ConcurrentHashMap<Integer, Integer>();
    ThreadUtilities.eachInTogether(new EachInTogetherRunnable<Integer>() {
      @Override
      public void run(Integer item) {
        values.put(item, item * item);
      }
    }, 1, 2, 3, 4);
    assertEquals(Integer.valueOf(16), values.get(4));
    assertEquals(4, values.size());
  }

  @Test
  public void eachInTogetherTimingShowsConcurrentExecution() {
    long startNanos = System.nanoTime();
    ThreadUtilities.eachInTogether(new EachInTogetherRunnable<Integer>() {
      @Override
      public void run(Integer item) {
        edu.cmu.cs.dennisc.java.lang.ThreadUtilities.sleep(100L);
      }
    }, 1, 2, 3, 4, 5);
    long elapsedMillis = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startNanos);
    assertTrue(elapsedMillis + 30L >= 100L);
    assertTrue(elapsedMillis < 400L);
  }

  @Test
  public void eachInTogetherSupportsNestedInvocation() {
    AtomicInteger counter = new AtomicInteger(0);
    ThreadUtilities.eachInTogether(new EachInTogetherRunnable<Integer>() {
      @Override
      public void run(Integer item) {
        ThreadUtilities.doTogether(new Runnable() {
          @Override
          public void run() {
            counter.incrementAndGet();
          }
        }, new Runnable() {
          @Override
          public void run() {
            counter.incrementAndGet();
          }
        });
      }
    }, 1, 2);
    assertEquals(4, counter.get());
  }

  @Test
  public void eachInTogetherSupportsNullItems() {
    List<String> seen = Collections.synchronizedList(new ArrayList<String>());
    ThreadUtilities.eachInTogether(new EachInTogetherRunnable<String>() {
      @Override
      public void run(String item) {
        seen.add(String.valueOf(item));
      }
    }, new String[] {null, "alpha", null});
    assertEquals(3, seen.size());
    assertEquals(2, Collections.frequency(seen, "null"));
  }

  @Test
  public void eachInTogetherSupportsDoubleValues() {
    AtomicReference<Double> sum = new AtomicReference<Double>(0.0);
    ThreadUtilities.eachInTogether(new EachInTogetherRunnable<Double>() {
      @Override
      public void run(Double item) {
        synchronized (sum) {
          sum.set(sum.get() + item);
        }
      }
    }, 1.5, 2.5, 3.0);
    assertEquals(7.0, sum.get(), 0.0001);
  }

  @Test
  public void eachInTogetherCanBeRepeatedAcrossFiveCalls() {
    AtomicInteger counter = new AtomicInteger(0);
    for (int i = 0; i < 5; i++) {
      ThreadUtilities.eachInTogether(new EachInTogetherRunnable<Integer>() {
        @Override
        public void run(Integer item) {
          counter.incrementAndGet();
        }
      }, 1, 2, 3);
    }
    assertEquals(15, counter.get());
  }

  @Test
  public void eachInTogetherCanBeRepeatedAcrossTenCalls() {
    AtomicInteger counter = new AtomicInteger(0);
    for (int i = 0; i < 10; i++) {
      ThreadUtilities.eachInTogether(new EachInTogetherRunnable<Integer>() {
        @Override
        public void run(Integer item) {
          counter.addAndGet(item);
        }
      }, 1, 2);
    }
    assertEquals(30, counter.get());
  }

  @Test
  public void eachInTogetherComputesSquaresForManyItems() {
    Map<Integer, Integer> values = new ConcurrentHashMap<Integer, Integer>();
    Integer[] items = integers(10);
    ThreadUtilities.eachInTogether(new EachInTogetherRunnable<Integer>() {
      @Override
      public void run(Integer item) {
        values.put(item, item * item);
      }
    }, items);
    assertEquals(Integer.valueOf(81), values.get(9));
    assertEquals(10, values.size());
  }

  @Test
  public void eachInTogetherArithmeticSumMatchesFormula() {
    AtomicLong sum = new AtomicLong(0L);
    Integer[] items = integers(20);
    ThreadUtilities.eachInTogether(new EachInTogetherRunnable<Integer>() {
      @Override
      public void run(Integer item) {
        sum.addAndGet(item);
      }
    }, items);
    assertEquals(arithmeticSum(20), sum.get());
  }

  @Test
  public void mixedWorkflowDoTogetherThenEachThenDoTogether() {
    AtomicInteger counter = new AtomicInteger(0);
    ThreadUtilities.doTogether(new Runnable() {
      @Override
      public void run() {
        counter.incrementAndGet();
      }
    }, new Runnable() {
      @Override
      public void run() {
        counter.incrementAndGet();
      }
    });
    ThreadUtilities.eachInTogether(new EachInTogetherRunnable<Integer>() {
      @Override
      public void run(Integer item) {
        counter.addAndGet(item);
      }
    }, 1, 2, 3);
    ThreadUtilities.doTogether(new Runnable() {
      @Override
      public void run() {
        counter.incrementAndGet();
      }
    });
    assertEquals(9, counter.get());
  }

  @Test
  public void mixedWorkflowPopulatesMapWithProcessedValues() {
    Map<String, Integer> map = new ConcurrentHashMap<String, Integer>();
    ThreadUtilities.eachInTogether(new EachInTogetherRunnable<String>() {
      @Override
      public void run(String item) {
        map.put(item, item.length());
      }
    }, "one", "three", "seven");
    ThreadUtilities.doTogether(new Runnable() {
      @Override
      public void run() {
        map.put("total", map.get("one") + map.get("three") + map.get("seven"));
      }
    });
    assertEquals(Integer.valueOf(13), map.get("total"));
  }

}

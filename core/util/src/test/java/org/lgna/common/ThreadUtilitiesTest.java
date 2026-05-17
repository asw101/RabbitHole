package org.lgna.common;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.Assert.*;

public class ThreadUtilitiesTest {

  // --- doTogether ---

  @Test
  public void doTogether_zeroRunnables() {
    ThreadUtilities.doTogether();
  }

  @Test
  public void doTogether_singleRunnable() {
    AtomicBoolean ran = new AtomicBoolean(false);
    ThreadUtilities.doTogether(() -> ran.set(true));
    assertTrue(ran.get());
  }

  @Test
  public void doTogether_multipleRunnables() throws Exception {
    AtomicInteger counter = new AtomicInteger(0);
    Runnable r1 = counter::incrementAndGet;
    Runnable r2 = counter::incrementAndGet;
    Runnable r3 = counter::incrementAndGet;

    ThreadUtilities.doTogether(r1, r2, r3);
    assertEquals(3, counter.get());
  }

  @Test
  public void doTogether_allRunConcurrently() throws Exception {
    List<String> threadNames = Collections.synchronizedList(new ArrayList<>());
    CountDownLatch allStarted = new CountDownLatch(2);

    Runnable r1 = () -> {
      threadNames.add(Thread.currentThread().getName());
      allStarted.countDown();
      try { allStarted.await(5, TimeUnit.SECONDS); } catch (InterruptedException e) { }
    };
    Runnable r2 = () -> {
      threadNames.add(Thread.currentThread().getName());
      allStarted.countDown();
      try { allStarted.await(5, TimeUnit.SECONDS); } catch (InterruptedException e) { }
    };

    ThreadUtilities.doTogether(r1, r2);
    assertEquals(2, threadNames.size());
  }

  @Test(expected = RuntimeException.class)
  public void doTogether_propagatesException() {
    ThreadUtilities.doTogether(
        () -> {},
        () -> { throw new RuntimeException("test error"); }
    );
  }

  // --- eachInTogether ---

  @Test
  public void eachInTogether_zeroItems() {
    AtomicInteger counter = new AtomicInteger(0);
    ThreadUtilities.eachInTogether(item -> counter.incrementAndGet());
  }

  @Test
  public void eachInTogether_singleItem() {
    List<String> results = Collections.synchronizedList(new ArrayList<>());
    ThreadUtilities.eachInTogether(results::add, "alpha");
    assertEquals(1, results.size());
    assertTrue(results.contains("alpha"));
  }

  @Test
  public void eachInTogether_multipleItems() {
    List<String> results = Collections.synchronizedList(new ArrayList<>());
    ThreadUtilities.eachInTogether(results::add, "a", "b", "c");
    assertEquals(3, results.size());
    assertTrue(results.contains("a"));
    assertTrue(results.contains("b"));
    assertTrue(results.contains("c"));
  }

  @Test
  public void eachInTogether_processesIntegerItems() {
    AtomicInteger sum = new AtomicInteger(0);
    ThreadUtilities.eachInTogether(sum::addAndGet, 10, 20, 30);
    assertEquals(60, sum.get());
  }
}

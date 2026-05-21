package edu.cmu.cs.dennisc.pattern;

import org.junit.Assert;
import org.junit.Test;

import java.util.concurrent.atomic.AtomicInteger;

public class AbstractPoolTest {
  public static class ReusableStub implements Reusable {
    private static final AtomicInteger NEXT_ID = new AtomicInteger();
    private final int id = NEXT_ID.incrementAndGet();
  }

  private static class StubPool extends AbstractPool<ReusableStub> {
    private int createCount;

    @Override
    protected ReusableStub createInstance() {
      this.createCount++;
      return new ReusableStub();
    }
  }

  @Test
  public void acquireCreatesNewInstanceWhenPoolIsEmpty() {
    StubPool pool = new StubPool();

    ReusableStub value = pool.acquire();

    Assert.assertNotNull(value);
    Assert.assertEquals(1, pool.createCount);
  }

  @Test
  public void releaseMakesInstanceAvailableForReuse() {
    StubPool pool = new StubPool();
    ReusableStub first = pool.acquire();
    pool.release(first);

    ReusableStub second = pool.acquire();

    Assert.assertSame(first, second);
    Assert.assertEquals(1, pool.createCount);
  }

  @Test
  public void defaultPoolUsesReflectionToCreateInstances() {
    DefaultPool<ReusableStub> pool = new DefaultPool<>(ReusableStub.class);

    ReusableStub first = pool.acquire();
    pool.release(first);
    ReusableStub second = pool.acquire();

    Assert.assertNotNull(first);
    Assert.assertSame(first, second);
    Assert.assertEquals(first.id, second.id);
  }
}

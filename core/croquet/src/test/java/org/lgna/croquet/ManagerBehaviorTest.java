package org.lgna.croquet;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.Assert.assertEquals;

public class ManagerBehaviorTest {
  @Before
  @After
  public void clearRegisteredModels() throws Exception {
    Field mapField = Manager.class.getDeclaredField("mapIdToModels");
    mapField.setAccessible(true);
    @SuppressWarnings("unchecked")
    Map<UUID, ?> map = (Map<UUID, ?>) mapField.get(null);
    map.clear();
  }

  @Test
  public void relocalizeAllElements_relocalizesEveryRegisteredModelOnce() {
    UUID sharedId = UUID.fromString("00000000-0000-0000-0000-000000000111");
    TestModel alpha = new TestModel(sharedId);
    TestModel beta = new TestModel(sharedId);
    TestModel gamma = new TestModel(UUID.fromString("00000000-0000-0000-0000-000000000222"));

    Manager.registerModel(alpha);
    Manager.registerModel(beta);
    Manager.registerModel(gamma);

    Manager.relocalizeAllElements();

    assertEquals(1, alpha.relocalizeCount.get());
    assertEquals(1, beta.relocalizeCount.get());
    assertEquals(1, gamma.relocalizeCount.get());
  }

  @Test
  public void unregisterModel_stopsFutureRelocalization_andRegistrationIsDeduplicated() {
    TestModel model = new TestModel(UUID.fromString("00000000-0000-0000-0000-000000000333"));

    Manager.registerModel(model);
    Manager.registerModel(model);
    Manager.relocalizeAllElements();
    assertEquals(1, model.relocalizeCount.get());

    Manager.unregisterModel(model);
    Manager.relocalizeAllElements();
    assertEquals(1, model.relocalizeCount.get());
  }

  private static final class TestModel implements Model {
    private final UUID migrationId;
    private final AtomicInteger relocalizeCount = new AtomicInteger();
    private boolean enabled = true;

    private TestModel(UUID migrationId) {
      this.migrationId = migrationId;
    }

    @Override
    public UUID getMigrationId() {
      return this.migrationId;
    }

    @Override
    public void relocalize() {
      this.relocalizeCount.incrementAndGet();
    }

    @Override
    public boolean isEnabled() {
      return this.enabled;
    }

    @Override
    public void setEnabled(boolean isEnabled) {
      this.enabled = isEnabled;
    }

    @Override
    public void initializeIfNecessary() {
    }

    @Override
    public void appendUserRepr(StringBuilder sb) {
      sb.append(this.migrationId);
    }
  }
}

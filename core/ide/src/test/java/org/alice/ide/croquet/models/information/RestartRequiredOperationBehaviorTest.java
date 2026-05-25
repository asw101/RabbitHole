package org.alice.ide.croquet.models.information;

import org.junit.Test;

import java.net.URL;
import java.util.UUID;

import static org.junit.Assert.*;

public class RestartRequiredOperationBehaviorTest {
  @Test
  public void restartRequiredOperation_getInstance_returnsMemoizedSingletonWithStableMigrationId() {
    RestartRequiredOperation first = RestartRequiredOperation.getInstance();
    RestartRequiredOperation second = RestartRequiredOperation.getInstance();

    assertSame(first, second);
    assertEquals(UUID.fromString("b3a861cb-1253-429c-b233-66209c1f4f65"), first.getMigrationId());
  }

  @Test
  public void restartRequiredOperation_iconResource_isPackagedNextToOperation() {
    URL resource = RestartRequiredOperation.class.getResource("images/restartRequired.png");

    assertNotNull(resource);
    assertTrue(resource.toExternalForm().endsWith("restartRequired.png"));
  }
}

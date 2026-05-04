package org.alice.nonfree;

import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;

public class NebulousStoryApiTest {
  @Test
  public void fallbackReportsNonfreeDisabledWhenExtensionIsAbsent() {
    assertFalse(NebulousStoryApi.nonfree.isNonFreeEnabled());
    assertNull(NebulousStoryApi.nonfree.getNebulousResourceInstallPath());
    assertNull(NebulousStoryApi.nonfree.getFactory(null));
  }
}

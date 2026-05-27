package org.lgna.project.ast;

import org.junit.Test;

import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertEquals;

public class UserPackageTest {
  @Test
  public void defaultConstructorLeavesNameUnset() {
    assertNull(new UserPackage().getName());
  }

  @Test
  public void namedConstructorStoresPackageName() {
    assertEquals("org.lgna.story", new UserPackage("org.lgna.story").getName());
  }

  @Test
  public void namePropertyExposesUnderlyingMutableProperty() {
    UserPackage userPackage = new UserPackage();

    assertSame(userPackage.name, userPackage.getNamePropertyIfItExists());
  }

  @Test
  public void userPackagesAreAlwaysUserAuthored() {
    assertTrue(new UserPackage("demo").isUserAuthored());
  }
}

package org.alice.ide.testing;

import org.junit.After;
import org.junit.Before;

public abstract class ProjectContextTestCase {
  protected ProjectContextFixture fixture;

  @Before
  public void setUpProjectContext() {
    fixture = ProjectContextFixture.load();
  }

  @After
  public void tearDownProjectContext() {
    TestIdeBootstrap.reset();
  }
}

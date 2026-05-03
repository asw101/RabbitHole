package edu.cmu.cs.dennisc.jira.rest;

import org.junit.Test;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThrows;

public class RestUtilitiesTest {
  @Test
  public void credentialsRequireUsername() {
    assertThrows(IllegalStateException.class, () -> RestUtilities.createCredentials("", "password"));
  }

  @Test
  public void credentialsRequirePassword() {
    assertThrows(IllegalStateException.class, () -> RestUtilities.createCredentials("user", ""));
  }

  @Test
  public void credentialsAcceptConfiguredValues() {
    assertNotNull(RestUtilities.createCredentials("user", "password"));
  }
}

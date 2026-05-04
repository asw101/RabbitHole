package edu.cmu.cs.dennisc.jira.rest;

import edu.cmu.cs.dennisc.issue.IssueSubmissionConfigurationException;
import org.junit.Test;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;

public class RestUtilitiesTest {
  @Test
  public void credentialsRequireUsername() {
    IssueSubmissionConfigurationException exception = assertThrows(
        IssueSubmissionConfigurationException.class,
        () -> RestUtilities.createCredentials("", "password"));

    assertTrue(exception.getMessage().contains(RestUtilities.JIRA_USERNAME_PROPERTY));
    assertTrue(exception.getMessage().contains(RestUtilities.JIRA_USERNAME_ENV));
  }

  @Test
  public void credentialsRequirePassword() {
    IssueSubmissionConfigurationException exception = assertThrows(
        IssueSubmissionConfigurationException.class,
        () -> RestUtilities.createCredentials("user", ""));

    assertTrue(exception.getMessage().contains(RestUtilities.JIRA_PASSWORD_PROPERTY));
    assertTrue(exception.getMessage().contains(RestUtilities.JIRA_PASSWORD_ENV));
  }

  @Test
  public void credentialsAcceptConfiguredValues() throws Exception {
    assertNotNull(RestUtilities.createCredentials("user", "password"));
  }
}

package edu.cmu.cs.dennisc.jira.rest;

import edu.cmu.cs.dennisc.issue.IssueSubmissionConfigurationException;
import edu.cmu.cs.dennisc.jira.JIRAReport;
import net.rcarz.jiraclient.*;

import java.net.URI;
import java.util.Collections;

public class RestUtilities {
  static final String JIRA_USERNAME_PROPERTY = "alice.jira.username";
  static final String JIRA_PASSWORD_PROPERTY = "alice.jira.password";
  static final String JIRA_USERNAME_ENV = "ALICE_JIRA_USERNAME";
  static final String JIRA_PASSWORD_ENV = "ALICE_JIRA_PASSWORD";

  private static final String STEPS_FIELD_ID = "customfield_10000";
  private static final String EXCEPTION_FIELD_ID = "customfield_10001";
  private static final String ENVIRONMENT_FIELD_ID = "environment";

  static BasicCredentials createConfiguredCredentials() throws IssueSubmissionConfigurationException {
    return createCredentials(
        firstConfiguredValue(JIRA_USERNAME_PROPERTY, JIRA_USERNAME_ENV),
        firstConfiguredValue(JIRA_PASSWORD_PROPERTY, JIRA_PASSWORD_ENV));
  }

  static BasicCredentials createCredentials(String username, String password) throws IssueSubmissionConfigurationException {
    if (isBlank(username) || isBlank(password)) {
      throw new IssueSubmissionConfigurationException("JIRA issue reporting is not configured. Set system properties "
          + JIRA_USERNAME_PROPERTY + "/" + JIRA_PASSWORD_PROPERTY + " or environment variables "
          + JIRA_USERNAME_ENV + "/" + JIRA_PASSWORD_ENV + " to enable direct issue submission.");
    }
    return new BasicCredentials(username, password);
  }

  private static String firstConfiguredValue(String propertyName, String environmentName) {
    String propertyValue = System.getProperty(propertyName);
    if (!isBlank(propertyValue)) {
      return propertyValue;
    }
    return System.getenv(environmentName);
  }

  private static boolean isBlank(String value) {
    return (value == null) || value.trim().isEmpty();
  }

  public static Issue createIssue(URI jiraServer, JIRAReport jiraReport) throws IssueSubmissionConfigurationException {
    BasicCredentials creds = createConfiguredCredentials();
    JiraClient jira = new JiraClient(jiraServer.toString(), creds);

    try {
      Version ver = Version.get(jira.getRestClient(), jiraReport.getAffectsVersionText());
      return jira.createIssue(jiraReport.getProjectKey(), jiraReport.getType().toString())
          .field(Field.SUMMARY, jiraReport.getTruncatedSummary())
          .field(Field.DESCRIPTION, jiraReport.getCreditedDescription())
          .field(Field.VERSIONS, Collections.singletonList(ver))
          .field(ENVIRONMENT_FIELD_ID, jiraReport.getEnvironment())
          .field(EXCEPTION_FIELD_ID, jiraReport.getException())
          .field(STEPS_FIELD_ID, jiraReport.getSteps())
          .execute();
    } catch (JiraException e) {
      throw new RuntimeException(e);
    }
  }
}

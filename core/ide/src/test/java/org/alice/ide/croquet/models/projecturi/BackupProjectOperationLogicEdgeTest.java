package org.alice.ide.croquet.models.projecturi;

import org.junit.Test;

import java.time.format.DateTimeFormatter;

import static org.alice.ide.ProjectFileUtilities.ORDER_FORMAT;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThrows;

public class BackupProjectOperationLogicEdgeTest {
  private static final DateTimeFormatter READABLE = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

  @Test
  public void getDateStringFromBackupNameUsesDefaultFormatterAndIgnoresPrefixText() {
    assertEquals("2024-01-02 03:04:05", BackupProjectOperationLogic.getDateStringFromBackupName("copy20240102_030405.a3p", READABLE));
  }

  @Test
  public void getDateStringFromBackupNameReturnsNullAtShortestInvalidBoundary() {
    assertNull(BackupProjectOperationLogic.getDateStringFromBackupName("12345678", ORDER_FORMAT, READABLE));
  }

  @Test
  public void getDateStringFromBackupNameRejectsNullName() {
    assertThrows(NullPointerException.class,
        () -> BackupProjectOperationLogic.getDateStringFromBackupName(null, ORDER_FORMAT, READABLE));
  }
}

package org.alice.ide.croquet.models.projecturi;

import org.junit.Test;

import java.time.format.DateTimeFormatter;

import static org.alice.ide.ProjectFileUtilities.ORDER_FORMAT;
import static org.junit.Assert.*;

public class BackupProjectOperationLogicTest {
  private static final DateTimeFormatter READABLE = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

  @Test
  public void constructorThrowsAssertionError() throws Exception {
    java.lang.reflect.Constructor<BackupProjectOperationLogic> constructor = BackupProjectOperationLogic.class.getDeclaredConstructor();
    constructor.setAccessible(true);

    try {
      constructor.newInstance();
      fail("Expected AssertionError");
    } catch (java.lang.reflect.InvocationTargetException exception) {
      assertTrue(exception.getCause() instanceof AssertionError);
    }
  }

  @Test
  public void getDateStringFromBackupName_parsesBackupTimestamp() {
    assertEquals("2024-05-06 07:08:09", BackupProjectOperationLogic.getDateStringFromBackupName("save20240506_070809.a3p", ORDER_FORMAT, READABLE));
  }

  @Test
  public void getDateStringFromBackupName_returnsNullForInvalidNames() {
    assertNull(BackupProjectOperationLogic.getDateStringFromBackupName("bad", ORDER_FORMAT, READABLE));
    assertNull(BackupProjectOperationLogic.getDateStringFromBackupName("savebroken_name.a3p", ORDER_FORMAT, READABLE));
  }

  @Test
  public void getDateStringFromBackupNameDefaultOverloadReturnsNullForTooShortNames() {
    assertNull(BackupProjectOperationLogic.getDateStringFromBackupName("save", READABLE));
  }
}

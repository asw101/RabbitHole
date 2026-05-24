package org.alice.ide.croquet.models.projecturi;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

import static org.alice.ide.ProjectFileUtilities.ORDER_FORMAT;

final class BackupProjectOperationLogic {
  private BackupProjectOperationLogic() {
    throw new AssertionError();
  }

  static String getDateStringFromBackupName(String name, DateTimeFormatter readableFormatter) {
    return getDateStringFromBackupName(name, ORDER_FORMAT, readableFormatter);
  }

  static String getDateStringFromBackupName(String name, DateTimeFormatter orderFormatter, DateTimeFormatter readableFormatter) {
    if (name.length() < 8) {
      return null;
    }

    try {
      String datetime = name.substring(4, name.length() - 4);
      LocalDateTime date = LocalDateTime.parse(datetime, orderFormatter);
      return date.format(readableFormatter);
    } catch (DateTimeParseException pe) {
      return null;
    }
  }
}

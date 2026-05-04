package org.lgna.project.io;

import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

class ZipEntryContainer {
  private final ZipFile zipFile;

  ZipEntryContainer(ZipFile file) {
    zipFile = file;
  }

  public InputStream getInputStream(String name) throws IOException {
    validateSafeEntryName(name);
    ZipEntry zipEntry = zipFile.getEntry(name);
    return zipEntry == null ? null : zipFile.getInputStream(zipEntry);
  }

  private static void validateSafeEntryName(String name) throws IOException {
    if ((name == null) || name.isEmpty()) {
      throw new IOException("Unsafe archive entry " + name);
    }
    if (name.startsWith("/") || name.startsWith("\\") || name.contains("\\") || hasWindowsDrivePrefix(name)) {
      throw new IOException("Unsafe archive entry " + name);
    }
    for (int i = 0; i < name.length(); i++) {
      char ch = name.charAt(i);
      if ((ch == '\0') || Character.isISOControl(ch)) {
        throw new IOException("Unsafe archive entry " + name);
      }
    }
    for (String segment : name.split("/", -1)) {
      if (segment.isEmpty() || segment.equals(".") || segment.equals("..")) {
        throw new IOException("Unsafe archive entry " + name);
      }
    }
  }

  private static boolean hasWindowsDrivePrefix(String name) {
    return (name.length() >= 2)
        && (name.charAt(1) == ':')
        && Character.isLetter(name.charAt(0));
  }
}

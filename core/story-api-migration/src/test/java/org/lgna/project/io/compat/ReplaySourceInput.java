package org.lgna.project.io.compat;

import java.util.Objects;

record ReplaySourceInput(String path, String text) {
  ReplaySourceInput {
    path = requireSafeRelativePath(path, "source path");
    text = normalizeText(Objects.requireNonNull(text, "text"));
  }

  static String normalizeText(String value) {
    return value.replace("\r\n", "\n").replace('\r', '\n');
  }

  static String requireSafeRelativePath(String path, String label) {
    Objects.requireNonNull(path, label);
    if (path.isBlank()) {
      throw new IllegalArgumentException(label + " must not be blank");
    }
    if ((path.charAt(0) == '/') || (path.charAt(0) == '\\') || path.contains("\\") || hasWindowsDrivePrefix(path)) {
      throw new IllegalArgumentException(label + " must be a slash-separated relative path: " + path);
    }
    int segmentStart = 0;
    while (segmentStart <= path.length()) {
      int segmentEnd = path.indexOf('/', segmentStart);
      if (segmentEnd < 0) {
        segmentEnd = path.length();
      }
      String segment = path.substring(segmentStart, segmentEnd);
      if (segment.isEmpty() || ".".equals(segment) || "..".equals(segment) || hasWindowsDrivePrefix(segment)) {
        throw new IllegalArgumentException(label + " contains an unsafe segment: " + path);
      }
      if (segmentEnd == path.length()) {
        return path;
      }
      segmentStart = segmentEnd + 1;
    }
    throw new IllegalArgumentException(label + " is unsafe: " + path);
  }

  static String requireSafeIdentifier(String id, String label) {
    Objects.requireNonNull(id, label);
    if (!id.matches("[a-z0-9]+(-[a-z0-9]+)*")) {
      throw new IllegalArgumentException(label + " must match [a-z0-9]+(-[a-z0-9]+)*: " + id);
    }
    return id;
  }

  private static boolean hasWindowsDrivePrefix(String value) {
    return (value.length() >= 2) && (value.charAt(1) == ':') && Character.isLetter(value.charAt(0));
  }
}

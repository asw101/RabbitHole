package org.lgna.project.migration;

import java.util.Objects;

public final class TextMigrationRule {
  private final String pattern;
  private final String replacement;

  public static TextMigrationRule replace(String pattern, String replacement) {
    return new TextMigrationRule(pattern, replacement);
  }

  private TextMigrationRule(String pattern, String replacement) {
    this.pattern = Objects.requireNonNull(pattern, "pattern");
    if (this.pattern.isEmpty()) {
      throw new IllegalArgumentException("pattern must not be empty");
    }
    this.replacement = replacement;
  }

  String getPattern() {
    return this.pattern;
  }

  String getReplacement() {
    return this.replacement;
  }
}

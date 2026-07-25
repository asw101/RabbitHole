package org.alice.serialization.tweedle;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * Validates user-authored names against the Tweedle grammar's {@code IDENTIFIER} rule so the
 * encoder can refuse to emit names that cannot round-trip through Tweedle source. When a name is
 * rejected the encoder throws, which lets {@code HybridProjectWriter} degrade to an XML-only
 * archive whose authoritative XML payload preserves the original name.
 */
final class TweedleIdentifiers {
  private TweedleIdentifiers() {
  }

  // Words that the Tweedle grammar reserves; a user-authored name emitted verbatim (a type or
  // method name) that collides with one of these cannot round-trip through Tweedle source.
  private static final Set<String> RESERVED_WORDS = Collections.unmodifiableSet(new HashSet<>(Arrays.asList(
      "Boolean", "class", "countUpTo", "constant", "DecimalNumber", "doInOrder", "doTogether",
      "each", "else", "enum", "extends", "forEach", "eachTogether", "CompletelyHidden", "if", "in",
      "loop", "models", "new", "Number", "PrimeTime", "return", "static", "super", "this",
      "TextString", "TuckedAway", "void", "while", "WholeNumber", "true", "false", "null", "as",
      "instanceof")));

  private static boolean isIdentifierStart(int cp) {
    // Grammar Letter fragment: [a-zA-Z$_] plus any code point above 0x7F.
    return cp == '$' || cp == '_' || (cp >= 'a' && cp <= 'z') || (cp >= 'A' && cp <= 'Z') || cp > 0x7F;
  }

  private static boolean isIdentifierPart(int cp) {
    return isIdentifierStart(cp) || (cp >= '0' && cp <= '9');
  }

  static boolean isIdentifier(String name) {
    if (name == null || name.isEmpty()) {
      return false;
    }
    final int first = name.codePointAt(0);
    if (!isIdentifierStart(first)) {
      return false;
    }
    for (int i = Character.charCount(first); i < name.length(); ) {
      final int cp = name.codePointAt(i);
      if (!isIdentifierPart(cp)) {
        return false;
      }
      i += Character.charCount(cp);
    }
    return true;
  }

  /**
   * Guards a user-authored name emitted verbatim into Tweedle source (a type or method name).
   * Names that are not valid Tweedle identifiers, or that collide with reserved words, cannot be
   * represented in Tweedle.
   */
  static void requireEncodable(String name, String kind) {
    if (!isIdentifier(name) || RESERVED_WORDS.contains(name)) {
      throw new UnsupportedTweedleDecodeException(
          "Cannot encode " + kind + " name '" + name + "' as a Tweedle identifier");
    }
  }

  /**
   * Guards a user-authored variable name (field, parameter, or local). These are emitted with a
   * prefix, so reserved-word collisions are impossible; only the identifier character set needs to
   * be valid for the prefixed name to be a legal Tweedle identifier.
   */
  static void requirePrefixable(String name, String kind) {
    if (!isIdentifier(name)) {
      throw new UnsupportedTweedleDecodeException(
          "Cannot encode " + kind + " name '" + name + "' as a Tweedle identifier");
    }
  }
}

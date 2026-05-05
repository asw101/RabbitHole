package org.alice.serialization.tweedle;

import org.alice.tweedle.TweedleClass;
import org.alice.tweedle.TweedleLinkException;
import org.alice.tweedle.TweedleField;
import org.alice.tweedle.TweedleType;
import org.alice.tweedle.unlinked.TweedleUnlinkedParser;
import org.lgna.project.ast.AbstractDeclaration;
import org.lgna.project.ast.AbstractNode;
import org.lgna.project.ast.AbstractType;
import org.lgna.project.ast.JavaType;
import org.lgna.project.ast.NamedUserType;
import org.lgna.project.ast.UserField;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Decoder {
  private final Set<AbstractDeclaration> terminalNodes;
  private static final List<String> JAVA_TYPE_PACKAGES = List.of(
      "org.lgna.story.",
      "org.lgna.story.resources.",
      "org.lgna.common.resources.",
      "java.lang.");
  private static final Map<String, Class<?>> TWEEDLE_TYPE_ALIASES = Map.of(
      "WholeNumber", Integer.class,
      "DecimalNumber", Double.class,
      "TextString", String.class,
      "Boolean", Boolean.class,
      "Number", Number.class);

  Decoder(Set<AbstractDeclaration> terminals) {
    terminalNodes = terminals;
  }

  Decoder() {
    terminalNodes = new HashSet<>();
  }

  public AbstractNode decode(String document) {
    TweedleType tweedleType;
    try {
      tweedleType = new TweedleUnlinkedParser().parseType(document);
    } catch (TweedleLinkException e) {
      throw new UnsupportedTweedleDecodeException(
          "Tweedle type uses linked members that the AST decoder does not support.",
          e);
    } catch (RuntimeException e) {
      throw new IllegalArgumentException("Unable to parse Tweedle type.", e);
    }
    if (tweedleType instanceof TweedleClass tweedleClass) {
      return decodeClass(tweedleClass);
    }
    throw new UnsupportedTweedleDecodeException("Only Tweedle class declarations can be decoded to AST nodes.");
  }

  public AbstractNode copy(String document) {
    return decode(document);
  }

  private NamedUserType decodeClass(TweedleClass tweedleClass) {
    if (!tweedleClass.getMethods().isEmpty() || !tweedleClass.getConstructors().isEmpty()) {
      throw new UnsupportedTweedleDecodeException("Tweedle class methods and constructors are not yet supported by the AST decoder.");
    }

    NamedUserType type = new NamedUserType();
    type.name.setValue(tweedleClass.getName());
    type.superType.setValue(resolveType(tweedleClass.getSuperclassName(), "superclass"));
    for (TweedleField property : tweedleClass.getProperties()) {
      type.fields.add(decodeField(property));
    }
    return type;
  }

  private UserField decodeField(TweedleField property) {
    if (property.hasInitializer()) {
      throw new UnsupportedTweedleDecodeException(
          "Tweedle field initializers are not yet supported by the AST decoder: " + property.getName());
    }
    return new UserField(property.getName(), resolveType(property.getType().getName(), "field"), null);
  }

  private AbstractType<?, ?, ?> resolveType(String typeName, String usage) {
    if (typeName == null) {
      return null;
    }
    Class<?> aliasedClass = TWEEDLE_TYPE_ALIASES.get(typeName);
    if (aliasedClass != null) {
      return JavaType.getInstance(aliasedClass);
    }
    for (String packageName : JAVA_TYPE_PACKAGES) {
      try {
        return JavaType.getInstance(Class.forName(packageName + typeName));
      } catch (ClassNotFoundException ignored) {
      }
    }
    throw new UnsupportedTweedleDecodeException("Unsupported Tweedle " + usage + ": " + typeName);
  }
}

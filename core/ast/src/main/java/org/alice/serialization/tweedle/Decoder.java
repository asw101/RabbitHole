package org.alice.serialization.tweedle;

import org.alice.tweedle.TweedleClass;
import org.alice.tweedle.TweedleType;
import org.alice.tweedle.unlinked.TweedleUnlinkedParser;
import org.lgna.project.ast.AbstractDeclaration;
import org.lgna.project.ast.AbstractNode;
import org.lgna.project.ast.AbstractType;
import org.lgna.project.ast.JavaType;
import org.lgna.project.ast.NamedUserType;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Decoder {
  private final Set<AbstractDeclaration> terminalNodes;
  private static final List<String> JAVA_TYPE_PACKAGES = List.of(
      "org.lgna.story.",
      "org.lgna.story.resources.",
      "org.lgna.common.resources.",
      "java.lang.");

  Decoder(Set<AbstractDeclaration> terminals) {
    terminalNodes = terminals;
  }

  Decoder() {
    terminalNodes = new HashSet<>();
  }

  public AbstractNode decode(String document) {
    TweedleType tweedleType = new TweedleUnlinkedParser().parseType(document);
    if (tweedleType instanceof TweedleClass tweedleClass) {
      return decodeClass(tweedleClass);
    }
    throw new UnsupportedOperationException("Only Tweedle class declarations can be decoded to AST nodes.");
  }

  public AbstractNode copy(String document) {
    return decode(document);
  }

  private NamedUserType decodeClass(TweedleClass tweedleClass) {
    if (!tweedleClass.getProperties().isEmpty()
        || !tweedleClass.getMethods().isEmpty()
        || !tweedleClass.getConstructors().isEmpty()) {
      throw new UnsupportedOperationException("Tweedle class members are not yet supported by the AST decoder.");
    }

    NamedUserType type = new NamedUserType();
    type.name.setValue(tweedleClass.getName());
    type.superType.setValue(resolveSuperType(tweedleClass.getSuperclassName()));
    return type;
  }

  private AbstractType<?, ?, ?> resolveSuperType(String superclassName) {
    if (superclassName == null) {
      return null;
    }
    for (String packageName : JAVA_TYPE_PACKAGES) {
      try {
        return JavaType.getInstance(Class.forName(packageName + superclassName));
      } catch (ClassNotFoundException ignored) {
      }
    }
    throw new UnsupportedOperationException("Unsupported Tweedle superclass: " + superclassName);
  }
}

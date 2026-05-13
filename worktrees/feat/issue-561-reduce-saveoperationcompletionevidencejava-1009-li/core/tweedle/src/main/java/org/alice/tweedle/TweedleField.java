package org.alice.tweedle;

import org.alice.tweedle.ast.TweedleExpression;

import java.util.List;

public class TweedleField extends TweedleValueHolderDeclaration {
  private List<String> modifiers;
  private TweedleExpression initializer;
  private final boolean hasInitializer;

  public TweedleField(List<String> modifiers, TweedleType type, String name) {
    super(type, name);
    this.modifiers = modifiers;
    this.hasInitializer = false;
  }

  public TweedleField(List<String> modifiers, TweedleType type, String name, TweedleExpression initializer) {
    super(type, name);
    this.modifiers = modifiers;
    this.initializer = initializer;
    this.hasInitializer = true;
  }

  public boolean hasInitializer() {
    return hasInitializer;
  }

  public TweedleExpression getInitializer() {
    return initializer;
  }
}

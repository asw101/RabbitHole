package org.alice.tweedle.unlinked;

import org.alice.tweedle.ast.BinaryExpression;
import org.alice.tweedle.ast.TweedleExpression;

@FunctionalInterface
interface BinaryConstructor {
  BinaryExpression newBinExp(TweedleExpression lhs, TweedleExpression rhs);
}

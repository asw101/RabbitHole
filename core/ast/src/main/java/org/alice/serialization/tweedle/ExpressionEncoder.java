package org.alice.serialization.tweedle;

import org.lgna.project.ast.*;
import org.lgna.project.virtualmachine.ReleaseVirtualMachine;

import java.util.ArrayList;

/**
 * Companion class that encapsulates expression-encoding logic extracted from
 * {@link TweedleEncoder}. Delegates back to the owning encoder for
 * super-class calls and protected formatting methods via package-private
 * bridge methods.
 */
class ExpressionEncoder {

  private final TweedleEncoder encoder;

  ExpressionEncoder(TweedleEncoder encoder) {
    this.encoder = encoder;
  }

  void processInstantiation(InstanceCreation creation) {
    String className = getDeclaringJavaClassName(creation);
    if (className != null) {
      if (className.endsWith("PersonResource")) {
        ReleaseVirtualMachine vm = new ReleaseVirtualMachine();
        final Object summary = creation.evaluate(vm);
        if (summary != null) {
          encoder.appendInstantiation("PersonResource", () -> encoder.appendArg("name", () -> encoder.forwardAppendEscapedString("Person/" + summary)));
          return;
        }
      }
      if (className.equals("Double")) {
        final ArrayList<SimpleArgument> requiredArgs = creation.requiredArguments.getValue();
        if (requiredArgs.size() == 1) {
          encoder.forwardAppendString("$DecimalNumber.from");
          Expression arg = requiredArgs.getFirst().expression.getValue();
          encoder.forwardParenthesize(() -> encoder.appendArg("wholeNumber", () -> arg.process(encoder)));
          return;
        }
      }
      if (className.startsWith("Dynamic") && className.endsWith("Resource")) {
        final ArrayList<SimpleArgument> requiredArgs = creation.requiredArguments.getValue();
        if (requiredArgs.size() == 2) {
          Expression variantNameExp = requiredArgs.get(1).expression.getValue();
          if (variantNameExp instanceof StringLiteral literal) {
            encoder.forwardAppendString(literal.value.getValue());
            encoder.forwardAppendString("Resource.DEFAULT");
            return;
          }
        }
      }
    }
    encoder.superProcessInstantiation(creation);
  }

  private String getDeclaringJavaClassName(InstanceCreation creation) {
    final AbstractConstructor constructor = creation.constructor.getValue();
    if (constructor instanceof JavaConstructor javaConstructor) {
      return javaConstructor.getConstructorReflectionProxy().getDeclaringClassReflectionProxy().getSimpleName();
    }
    return null;
  }

  void appendTargetAndMember(Expression target, String member, AbstractType<?, ?, ?> returnType) {
    if (targetIsMath(target)) {
      encoder.forwardAppendString(tweedleModuleForMath(member, returnType));
    } else {
      encoder.processExpression(target);
    }
    encoder.forwardAppendAccessSeparator();
    String tweedleName = TweedleEncoderData.membersToRename.get(member);
    encoder.forwardAppendString(tweedleName == null ? member : tweedleName);
  }

  private boolean targetIsMath(Expression target) {
    if (target instanceof TypeExpression expression) {
      AbstractType<?, ?, ?> innerType = expression.value.getValue();
      return innerType instanceof JavaType && "Math".equals(innerType.getName());
    }
    return false;
  }

  private String tweedleModuleForMath(String member, AbstractType<?, ?, ?> returnType) {
    if (returnType != null && "int".equals(returnType.getName())) {
      return "$WholeNumber";
    }
    if (TweedleEncoderData.angleMembers.contains(member)) {
      return "$Angle";
    }
    return "$DecimalNumber";
  }

  void processResourceExpression(ResourceExpression resourceExpression) {
    encoder.forwardAppendEscapedString(resourceExpression.resource.getValue().getName());
  }
}

package org.alice.stageide.sceneeditor;

import org.lgna.project.ast.AbstractStatementWithBody;
import org.lgna.project.ast.Expression;
import org.lgna.project.ast.ExpressionStatement;
import org.lgna.project.ast.FieldAccess;
import org.lgna.project.ast.MethodInvocation;
import org.lgna.project.ast.SimpleArgument;
import org.lgna.project.ast.Statement;
import org.lgna.project.ast.UserField;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

final class SceneFieldCodeGeneratorLogic {
  private SceneFieldCodeGeneratorLogic() {
    throw new AssertionError();
  }

  static void stripCopyStateStatements(Statement stateCodeStatement) {
    List<org.lgna.project.ast.BlockStatement> blockStatements = new LinkedList<>();
    if (stateCodeStatement instanceof org.lgna.project.ast.BlockStatement statement) {
      blockStatements.add(statement);
    } else if (stateCodeStatement instanceof AbstractStatementWithBody body) {
      blockStatements.add(body.body.getValue());
    }
    while (!blockStatements.isEmpty()) {
      org.lgna.project.ast.BlockStatement bs = blockStatements.removeFirst();
      Statement setVehicleStatement = null;
      Statement setPositionStatement = null;
      Statement setOrientationStatement = null;
      for (Statement s : bs.statements.getValue()) {
        if (s instanceof org.lgna.project.ast.BlockStatement block) {
          blockStatements.add(block);
        } else if (s instanceof AbstractStatementWithBody body) {
          blockStatements.add(body.body.getValue());
        } else if (s instanceof ExpressionStatement expressionStatement) {
          Expression expression = expressionStatement.expression.getValue();
          if (expression instanceof MethodInvocation mi) {
            var method = mi.method.getValue();
            if (method.getName().equalsIgnoreCase("setVehicle") && (mi.expression.getValue() instanceof FieldAccess)) {
              setVehicleStatement = s;
            } else if (method.getName().equalsIgnoreCase("setOrientationRelativeToVehicle") && (mi.expression.getValue() instanceof FieldAccess)) {
              setOrientationStatement = s;
            } else if (method.getName().equalsIgnoreCase("setPositionRelativeToVehicle") && (mi.expression.getValue() instanceof FieldAccess)) {
              setPositionStatement = s;
            }
          }
        }
      }
      if (setVehicleStatement != null) {
        bs.statements.getValue().remove(setVehicleStatement);
      }
      if (setPositionStatement != null) {
        bs.statements.getValue().remove(setPositionStatement);
      }
      if (setOrientationStatement != null) {
        bs.statements.getValue().remove(setOrientationStatement);
      }
    }
  }

  static boolean doesSetVehicleImplyVehicle(MethodInvocation setVehicleCall, UserField vehicle) {
    ArrayList<SimpleArgument> args = setVehicleCall.requiredArguments.getValue();
    if (args.size() == 1 && setVehicleCall.expression.getValue() instanceof FieldAccess) {
      Expression vehicleExpr = args.getFirst().expression.getValue();
      return isDirectRider(vehicle, vehicleExpr) || isJointRider(vehicle, vehicleExpr);
    }
    return false;
  }

  static boolean isDirectRider(UserField vehicle, Expression vehicleExpr) {
    return vehicleExpr instanceof FieldAccess fa && fa.field.getValue() == vehicle;
  }

  static boolean isJointRider(UserField vehicle, Expression vehicleExpr) {
    if (vehicleExpr instanceof MethodInvocation vehicleMethod && vehicleMethod.expression.getValue() instanceof FieldAccess target) {
      return target.field.getValue() == vehicle;
    }
    return false;
  }

  static MethodInvocation asSetVehicleCall(Statement statement) {
    if (statement instanceof ExpressionStatement expressionStatement) {
      Expression expression = expressionStatement.expression.getValue();
      if (expression instanceof MethodInvocation mi) {
        var method = mi.method.getValue();
        if (method.getName().equalsIgnoreCase("setVehicle")) {
          return mi;
        }
      }
    }
    return null;
  }
}

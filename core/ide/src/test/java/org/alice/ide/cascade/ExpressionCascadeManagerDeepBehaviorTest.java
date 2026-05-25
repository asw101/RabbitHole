package org.alice.ide.cascade;

import edu.cmu.cs.dennisc.java.util.Lists;
import org.junit.Test;
import org.lgna.project.ast.BlockStatement;
import org.lgna.project.ast.ExpressionStatement;
import org.lgna.project.ast.IntegerLiteral;
import org.lgna.project.ast.LocalDeclarationStatement;
import org.lgna.project.ast.ThisExpression;
import org.lgna.project.ast.UserLocal;

import java.util.LinkedList;

import static org.junit.Assert.*;

public class ExpressionCascadeManagerDeepBehaviorTest {
  @Test
  public void collectAccessibleLocalsTrimsIndexesBeyondBlockSize() {
    UserLocal local = new UserLocal("count", Object.class, false);
    BlockStatement block = new BlockStatement();
    block.statements.add(new ExpressionStatement(new ThisExpression()));
    block.statements.add(new LocalDeclarationStatement(local, null));

    LinkedList<UserLocal> locals = ExpressionCascadeManagerLogic.collectAccessibleLocalsForBlockAndIndex(Lists.newLinkedList(), block, 10);

    assertEquals(1, locals.size());
    assertSame(local, locals.getFirst());
  }

  @Test
  public void resolveFillInDescriptorFallsBackForLiterals() {
    assertEquals(ExpressionCascadeManagerLogic.Kind.SIMPLE,
        ExpressionCascadeManagerLogic.resolveFillInDescriptor(new IntegerLiteral(5)).getKind());
  }
}

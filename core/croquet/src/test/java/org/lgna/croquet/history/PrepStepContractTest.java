package org.lgna.croquet.history;
import org.lgna.croquet.triggers.IterationTrigger;
import org.lgna.croquet.triggers.Trigger;
import org.junit.Test;
import static org.junit.Assert.*;
public class PrepStepContractTest {
  private EmptyPrepStep mk(String l) { UserActivity a=new UserActivity(); return new EmptyPrepStep(IterationTrigger.createUserInstance(a), l); }
  @Test public void getTrigger_nonNull() { assertNotNull(mk("t").getTrigger()); }
  @Test public void getTrigger_instanceOfTrigger() { assertTrue(mk("t").getTrigger() instanceof Trigger); }
  @Test public void getOwner_nonNull() { assertNotNull(mk("t").getOwner()); }
  @Test public void getOwner_instanceOfUserActivity() { assertTrue(mk("t").getOwner() instanceof UserActivity); }
  @Test public void getModel_canBeNull() { assertNull(mk("t").getModel()); }
  @Test public void independentActivities() { assertNotSame(mk("a").getOwner(), mk("b").getOwner()); }
  @Test public void independentTriggers() { assertNotSame(mk("a").getTrigger(), mk("b").getTrigger()); }
  @Test public void triggerAndOwnerConnected() { EmptyPrepStep s=mk("t"); assertSame(s.getTrigger().getUserActivity(), s.getOwner()); }
}

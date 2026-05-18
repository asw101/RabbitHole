package org.lgna.croquet.history;
import org.lgna.croquet.triggers.IterationTrigger;
import org.junit.Test;
import static org.junit.Assert.*;
public class EmptyPrepStepTest {
  @Test public void constructor() { UserActivity a=new UserActivity(); IterationTrigger t=IterationTrigger.createUserInstance(a); assertNotNull(new EmptyPrepStep(t, "label")); }
  @Test public void getTrigger() { UserActivity a=new UserActivity(); IterationTrigger t=IterationTrigger.createUserInstance(a); assertSame(t, new EmptyPrepStep(t, "l").getTrigger()); }
  @Test public void getModel_null() { UserActivity a=new UserActivity(); IterationTrigger t=IterationTrigger.createUserInstance(a); assertNull(new EmptyPrepStep(t, "l").getModel()); }
  @Test public void getOwner() { UserActivity a=new UserActivity(); IterationTrigger t=IterationTrigger.createUserInstance(a); assertSame(a, new EmptyPrepStep(t, "l").getOwner()); }
  @Test public void extendsPrepStep() { UserActivity a=new UserActivity(); IterationTrigger t=IterationTrigger.createUserInstance(a); assertTrue(new EmptyPrepStep(t, "l") instanceof PrepStep); }
  @Test public void differentSteps() { UserActivity a1=new UserActivity(),a2=new UserActivity(); assertNotSame(new EmptyPrepStep(IterationTrigger.createUserInstance(a1),"l1"), new EmptyPrepStep(IterationTrigger.createUserInstance(a2),"l2")); }
  @Test public void triggerActivityMatchesOwner() { UserActivity a=new UserActivity(); IterationTrigger t=IterationTrigger.createUserInstance(a); assertSame(t.getUserActivity(), new EmptyPrepStep(t,"l").getOwner()); }
}

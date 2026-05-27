package org.lgna.story.implementation;

import edu.cmu.cs.dennisc.scenegraph.Transformable;
import org.junit.Test;
import org.lgna.story.SBox;

import static org.junit.Assert.assertSame;

public class ReferenceFrameTest {
  private static class StubReferenceFrame implements ReferenceFrame {
    private final edu.cmu.cs.dennisc.scenegraph.ReferenceFrame sgReferenceFrame;
    private final EntityImp actualEntityImplementation;

    StubReferenceFrame(edu.cmu.cs.dennisc.scenegraph.ReferenceFrame sgReferenceFrame, EntityImp actualEntityImplementation) {
      this.sgReferenceFrame = sgReferenceFrame;
      this.actualEntityImplementation = actualEntityImplementation;
    }

    @Override
    public edu.cmu.cs.dennisc.scenegraph.ReferenceFrame getSgReferenceFrame() {
      return this.sgReferenceFrame;
    }

    @Override
    public EntityImp getActualEntityImplementation(EntityImp ths) {
      return this.actualEntityImplementation;
    }
  }

  @Test
  public void getSgReferenceFrameReturnsProvidedReferenceFrame() {
    Transformable transformable = new Transformable();
    EntityImp entity = new SBox().getImplementation();
    ReferenceFrame frame = new StubReferenceFrame(transformable, entity);

    assertSame(transformable, frame.getSgReferenceFrame());
  }

  @Test
  public void getActualEntityImplementationReturnsProvidedEntity() {
    EntityImp provided = new SBox().getImplementation();
    EntityImp ignored = new SBox().getImplementation();
    ReferenceFrame frame = new StubReferenceFrame(new Transformable(), provided);

    assertSame(provided, frame.getActualEntityImplementation(ignored));
  }
}

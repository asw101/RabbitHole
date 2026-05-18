package org.alice.imageeditor.croquet;

import org.alice.imageeditor.croquet.views.SaveOverPane;
import org.junit.Test;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;

public class SaveOverCompositeTest {
  @Test
  public void gettersAndCreateViewReturnExpectedObjects() throws Exception {
    TestSupport.onEdt(() -> {
      TestSupport.HeadlessImageEditorFrame frame = new TestSupport.HeadlessImageEditorFrame();
      SaveOperation owner = new SaveOperation(frame);
      SaveOverComposite composite = new SaveOverComposite(owner);

      assertSame(owner, composite.getOwner());
      assertNotNull(composite.getPrevHeader());
      assertNotNull(composite.getNextHeader());

      SaveOverPane pane = composite.createView();
      assertSame(composite, pane.getComposite());
    });
  }
}

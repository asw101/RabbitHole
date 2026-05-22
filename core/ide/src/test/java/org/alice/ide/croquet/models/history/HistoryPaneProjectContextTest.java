package org.alice.ide.croquet.models.history;

import org.alice.ide.IDE;
import org.alice.ide.testing.ProjectContextTestCase;
import org.junit.Test;
import org.lgna.croquet.views.SwingComponentView;

import javax.swing.JList;
import java.lang.reflect.Field;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

public class HistoryPaneProjectContextTest extends ProjectContextTestCase {
  @Test
  public void constructorBindsLoadedProjectDocumentHistory() throws Exception {
    HistoryPane pane = new HistoryPane(IDE.PROJECT_GROUP);
    Field listField = HistoryPane.class.getDeclaredField("list");
    listField.setAccessible(true);
    JList<?> list = (JList<?>) listField.get(pane);

    assertNotNull(list.getModel());
    assertSame(IDE.getActiveInstance().getDocumentFrame().getDocument().getUndoHistory(IDE.PROJECT_GROUP),
        readPrivateField(pane, "projectHistory"));
    assertTrue(list.getModel().getSize() >= 0);
  }

  private static Object readPrivateField(Object target, String name) throws Exception {
    Field field = target.getClass().getDeclaredField(name);
    field.setAccessible(true);
    return field.get(target);
  }
}

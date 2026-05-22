package org.alice.ide;

import org.alice.ide.codedrop.CodePanelWithDropReceptor;
import org.alice.ide.croquet.models.IdeDragModel;
import org.alice.ide.perspectives.ProjectPerspective;
import org.junit.Test;
import org.lgna.croquet.Composite;
import org.lgna.croquet.DropReceptor;
import org.lgna.croquet.ToolBarComposite;
import org.lgna.croquet.views.TrackableShape;

import java.util.List;
import java.util.Locale;
import java.util.UUID;

import static org.junit.Assert.*;

public class IdePreferencesTest {
  private final IdePreferences preferences = new IdePreferences();

  @Test
  public void forcedLocaleUsesConfiguredPropertyValue() {
    assertEquals(Locale.of("es"), preferences.getForcedLocale("es"));
    assertNull(preferences.getForcedLocale(null));
  }

  @Test
  public void defaultPerspectivePrefersConfiguredView() {
    TestProjectPerspective setup = new TestProjectPerspective();
    TestProjectPerspective code = new TestProjectPerspective();

    assertSame(setup, preferences.getDefaultPerspective(true, setup, code));
    assertSame(code, preferences.getDefaultPerspective(false, setup, code));
  }

  private static final class TestProjectPerspective extends ProjectPerspective {
    private TestProjectPerspective() {
      super(UUID.randomUUID(), null, null);
    }

    @Override
    public TrackableShape getRenderWindow() {
      return null;
    }

    @Override
    public CodePanelWithDropReceptor getCodeDropReceptorInFocus() {
      return null;
    }

    @Override
    protected void addPotentialDropReceptors(List<DropReceptor> out, IdeDragModel dragModel) {
    }

    @Override
    public ToolBarComposite getToolBarComposite() {
      return null;
    }

    @Override
    public Composite<?> getMainComposite() {
      return null;
    }
  }
}

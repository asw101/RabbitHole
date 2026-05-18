package org.alice.stageide.perspectives;

import org.alice.ide.codedrop.CodePanelWithDropReceptor;
import org.alice.ide.croquet.models.IdeDragModel;
import org.alice.ide.perspectives.ProjectPerspective;
import org.alice.ide.perspectives.codecs.IdePerspectiveCodec;
import org.junit.Test;
import org.lgna.croquet.Composite;
import org.lgna.croquet.DropReceptor;
import org.lgna.croquet.MenuBarComposite;
import org.lgna.croquet.ToolBarComposite;
import org.lgna.croquet.views.TrackableShape;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.junit.Assert.*;

public class PerspectiveStateTest {

  private static final class TestPerspective extends ProjectPerspective {
    private final String name;

    private TestPerspective(String name) {
      super(UUID.randomUUID(), null, (MenuBarComposite) null);
      this.name = name;
    }

    @Override
    public String getName() {
      return this.name;
    }

    @Override
    public ToolBarComposite getToolBarComposite() {
      return null;
    }

    @Override
    public Composite<?> getMainComposite() {
      return null;
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
  }

  @Test
  public void constructor_withNoPerspectives_hasNoSelection() {
    PerspectiveState state = new PerspectiveState();

    assertNull(state.getValue());
    assertEquals(0, state.getItemCount());
  }

  @Test
  public void constructor_selectsFirstPerspective() {
    TestPerspective first = new TestPerspective("first");
    TestPerspective second = new TestPerspective("second");
    PerspectiveState state = new PerspectiveState(first, second);

    assertSame(first, state.getValue());
  }

  @Test
  public void getItemCodec_usesIdePerspectiveCodecSingleton() {
    PerspectiveState state = new PerspectiveState(new TestPerspective("only"));

    assertSame(IdePerspectiveCodec.SINGLETON, state.getItemCodec());
  }

  @Test
  public void toArray_preservesPerspectiveOrder() {
    TestPerspective first = new TestPerspective("first");
    TestPerspective second = new TestPerspective("second");
    PerspectiveState state = new PerspectiveState(first, second);

    assertArrayEquals(new ProjectPerspective[] {first, second}, state.toArray());
  }

  @Test
  public void iterator_returnsPerspectivesInOrder() {
    TestPerspective first = new TestPerspective("first");
    TestPerspective second = new TestPerspective("second");
    PerspectiveState state = new PerspectiveState(first, second);

    assertEquals(Arrays.asList(first, second), Arrays.asList(state.toArray()));
  }
}

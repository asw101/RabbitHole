package org.alice.ide.ast.type.merge.croquet;

import edu.cmu.cs.dennisc.javax.swing.ColorCustomizer;
import org.junit.Test;
import org.lgna.project.ast.BlockStatement;
import org.lgna.project.ast.JavaType;
import org.lgna.project.ast.UserMethod;
import org.lgna.project.ast.UserParameter;

import javax.swing.UIManager;
import java.awt.Color;
import java.net.URI;

import static org.junit.Assert.*;

public class PotentialNameChangerTest {
  private static final class TestPotentialNameChanger extends PotentialNameChanger<UserMethod> {
    private final MemberHubWithNameState<UserMethod> importHub;
    private final MemberHubWithNameState<UserMethod> projectHub;
    private final boolean renameRequired;

    private TestPotentialNameChanger(URI uri, boolean renameRequired) {
      super(uri);
      this.renameRequired = renameRequired;
      UserMethod importMethod = new UserMethod("step", JavaType.VOID_TYPE, new UserParameter[0], new BlockStatement());
      UserMethod projectMethod = new UserMethod("step", JavaType.VOID_TYPE, new UserParameter[0], new BlockStatement());
      this.importHub = new MemberHubWithNameState<UserMethod>(importMethod, false) {
        @Override
        public ActionStatus getActionStatus() {
          return ActionStatus.OMIT;
        }
      };
      this.projectHub = new MemberHubWithNameState<UserMethod>(projectMethod, false) {
        @Override
        public ActionStatus getActionStatus() {
          return ActionStatus.OMIT;
        }
      };
    }

    @Override
    public MemberHubWithNameState<UserMethod> getImportHub() {
      return this.importHub;
    }

    @Override
    public MemberHubWithNameState<UserMethod> getProjectHub() {
      return this.projectHub;
    }

    @Override
    protected boolean isRenameRequired() {
      return this.renameRequired;
    }
  }

  @Test
  public void constructorStoresUriForDescriptionPurposesOnly() {
    URI uri = URI.create("file:/merge");
    PotentialNameChanger<UserMethod> changer = new TestPotentialNameChanger(uri, false);

    assertEquals(uri, changer.getUriForDescriptionPurposesOnly());
  }

  @Test
  public void foregroundCustomizerReturnsDefaultColorWhenRenameIsNotRequired() {
    Color defaultColor = Color.BLUE;
    ColorCustomizer customizer = new TestPotentialNameChanger(URI.create("file:/merge"), false).getForegroundCustomizer();

    assertSame(defaultColor, customizer.changeColorIfAppropriate(defaultColor));
  }

  @Test
  public void foregroundCustomizerReturnsAlertColorWhenRenameIsRequired() {
    Object previous = UIManager.get("Alice.Alert.color");
    Color alertColor = Color.MAGENTA;
    try {
      UIManager.put("Alice.Alert.color", alertColor);
      ColorCustomizer customizer = new TestPotentialNameChanger(URI.create("file:/merge"), true).getForegroundCustomizer();

      assertEquals(alertColor, customizer.changeColorIfAppropriate(Color.BLUE));
    } finally {
      if (previous != null) {
        UIManager.put("Alice.Alert.color", previous);
      } else {
        UIManager.put("Alice.Alert.color", null);
      }
    }
  }
}

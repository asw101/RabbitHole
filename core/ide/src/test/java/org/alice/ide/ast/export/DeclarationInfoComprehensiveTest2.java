package org.alice.ide.ast.export;

import org.junit.Test;
import org.lgna.project.Project;
import org.lgna.project.ast.Declaration;
import org.lgna.project.ast.JavaType;
import org.lgna.project.ast.NamedUserType;
import org.lgna.project.ast.NullLiteral;
import org.lgna.project.ast.UserField;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.*;

public class DeclarationInfoComprehensiveTest2 {

  private static class CountingProjectInfo extends ProjectInfo {
    private boolean inMidst;
    private int updateCount;

    private CountingProjectInfo() {
      super(new Project(new NamedUserType(), Project.SceneCameraType.WindowCamera));
    }

    @Override
    public boolean isInTheMidstOfChange() {
      return this.inMidst;
    }

    @Override
    public void update() {
      this.updateCount++;
    }
  }

  private static class StubDeclarationInfo extends DeclarationInfo<Declaration> {
    private StubDeclarationInfo(ProjectInfo projectInfo, Declaration declaration) {
      super(projectInfo, declaration);
    }

    private void addRequiredDirectly(Set<DeclarationInfo<?>> visited) {
      super.addRequired(visited);
    }
  }

  private UserField createField(String name) {
    return new UserField(name, JavaType.STRING_TYPE, new NullLiteral());
  }

  @Test
  public void classIsPublic() {
    assertTrue(Modifier.isPublic(DeclarationInfo.class.getModifiers()));
  }

  @Test
  public void classIsAbstract() {
    assertTrue(Modifier.isAbstract(DeclarationInfo.class.getModifiers()));
  }

  @Test
  public void simpleNameMatchesSource() {
    assertEquals("DeclarationInfo", DeclarationInfo.class.getSimpleName());
  }

  @Test
  public void packageNameMatchesSource() {
    assertEquals("org.alice.ide.ast.export", DeclarationInfo.class.getPackage().getName());
  }

  @Test
  public void constructorExists() throws Exception {
    assertNotNull(DeclarationInfo.class.getDeclaredConstructor(ProjectInfo.class, Declaration.class));
  }

  @Test
  public void constructorIsPublic() throws Exception {
    Constructor<?> constructor = DeclarationInfo.class.getDeclaredConstructor(ProjectInfo.class, Declaration.class);
    assertTrue(Modifier.isPublic(constructor.getModifiers()));
  }

  @Test
  public void getProjectInfoReturnsProvidedProjectInfo() {
    CountingProjectInfo projectInfo = new CountingProjectInfo();
    StubDeclarationInfo info = new StubDeclarationInfo(projectInfo, createField("fieldOne"));
    assertSame(projectInfo, info.getProjectInfo());
  }

  @Test
  public void getDeclarationReturnsProvidedDeclaration() {
    UserField field = createField("fieldTwo");
    StubDeclarationInfo info = new StubDeclarationInfo(new CountingProjectInfo(), field);
    assertSame(field, info.getDeclaration());
  }

  @Test
  public void getCheckBoxReturnsSameInstanceEachTime() {
    StubDeclarationInfo info = new StubDeclarationInfo(new CountingProjectInfo(), createField("fieldThree"));
    assertSame(info.getCheckBox(), info.getCheckBox());
  }

  @Test
  public void checkboxTextMatchesDeclarationName() {
    StubDeclarationInfo info = new StubDeclarationInfo(new CountingProjectInfo(), createField("fieldFour"));
    assertEquals("fieldFour", info.getCheckBox().getText());
  }

  @Test
  public void appendDesiredSkipsUndesiredDeclarations() {
    StubDeclarationInfo info = new StubDeclarationInfo(new CountingProjectInfo(), createField("fieldFive"));
    List<DeclarationInfo<?>> desired = new ArrayList<>();
    info.appendDesired(desired);
    assertTrue(desired.isEmpty());
  }

  @Test
  public void selectingCheckboxMakesAppendDesiredIncludeInfo() {
    StubDeclarationInfo info = new StubDeclarationInfo(new CountingProjectInfo(), createField("fieldSix"));
    info.getCheckBox().getModel().setSelected(true);
    List<DeclarationInfo<?>> desired = new ArrayList<>();
    info.appendDesired(desired);
    assertEquals(1, desired.size());
  }

  @Test
  public void deselectingCheckboxRemovesDesiredState() {
    StubDeclarationInfo info = new StubDeclarationInfo(new CountingProjectInfo(), createField("fieldSeven"));
    info.getCheckBox().getModel().setSelected(true);
    info.getCheckBox().getModel().setSelected(false);
    List<DeclarationInfo<?>> desired = new ArrayList<>();
    info.appendDesired(desired);
    assertTrue(desired.isEmpty());
  }

  @Test
  public void updateRequiredAddsInstanceToVisitedSet() {
    StubDeclarationInfo info = new StubDeclarationInfo(new CountingProjectInfo(), createField("fieldEight"));
    Set<DeclarationInfo<?>> visited = new HashSet<>();
    info.updateRequired(visited);
    assertTrue(visited.contains(info));
  }

  @Test
  public void resetRequiredClearsRequiredOnlySelection() {
    StubDeclarationInfo info = new StubDeclarationInfo(new CountingProjectInfo(), createField("fieldNine"));
    Set<DeclarationInfo<?>> visited = new HashSet<>();
    info.updateRequired(visited);
    info.resetRequired();
    info.updateSwing();
    assertFalse(info.getCheckBox().getModel().isSelected());
  }

  @Test
  public void updateSwingSelectsDesiredDeclarations() {
    StubDeclarationInfo info = new StubDeclarationInfo(new CountingProjectInfo(), createField("fieldTen"));
    info.getCheckBox().getModel().setSelected(true);
    info.updateSwing();
    assertTrue(info.getCheckBox().getModel().isSelected());
  }

  @Test
  public void updateSwingSelectsRequiredDeclarations() {
    StubDeclarationInfo info = new StubDeclarationInfo(new CountingProjectInfo(), createField("fieldEleven"));
    Set<DeclarationInfo<?>> visited = new HashSet<>();
    info.updateRequired(visited);
    info.updateSwing();
    assertTrue(info.getCheckBox().getModel().isSelected());
  }

  @Test
  public void updateSwingDisablesRequiredOnlyDeclarations() {
    CountingProjectInfo projectInfo = new CountingProjectInfo();
    StubDeclarationInfo info = new StubDeclarationInfo(projectInfo, createField("fieldTwelve"));
    Set<DeclarationInfo<?>> visited = new HashSet<>();
    info.updateRequired(visited);
    projectInfo.inMidst = true;
    info.updateSwing();
    projectInfo.inMidst = false;
    assertFalse(info.getCheckBox().getModel().isEnabled());
  }

  @Test
  public void selectingCheckboxTriggersProjectUpdateWhenNotInMidst() {
    CountingProjectInfo projectInfo = new CountingProjectInfo();
    StubDeclarationInfo info = new StubDeclarationInfo(projectInfo, createField("fieldThirteen"));
    info.getCheckBox().getModel().setSelected(true);
    assertEquals(1, projectInfo.updateCount);
  }

  @Test
  public void selectingCheckboxDoesNotTriggerProjectUpdateDuringProjectChange() {
    CountingProjectInfo projectInfo = new CountingProjectInfo();
    projectInfo.inMidst = true;
    StubDeclarationInfo info = new StubDeclarationInfo(projectInfo, createField("fieldFourteen"));
    info.getCheckBox().getModel().setSelected(true);
    assertEquals(0, projectInfo.updateCount);
  }

  @Test
  public void protectedAddRequiredCanBeReachedFromSubclass() {
    StubDeclarationInfo info = new StubDeclarationInfo(new CountingProjectInfo(), createField("fieldFifteen"));
    Set<DeclarationInfo<?>> visited = new HashSet<>();
    info.addRequiredDirectly(visited);
    info.updateSwing();
    assertTrue(info.getCheckBox().getModel().isSelected());
  }

  @Test
  public void updateRequiredIsIdempotentForVisitedSet() {
    StubDeclarationInfo info = new StubDeclarationInfo(new CountingProjectInfo(), createField("fieldSixteen"));
    Set<DeclarationInfo<?>> visited = new HashSet<>();
    info.updateRequired(visited);
    info.updateRequired(visited);
    assertEquals(1, visited.size());
  }

  @Test
  public void surfaceMethodsExist() throws Exception {
    assertEquals(ProjectInfo.class, DeclarationInfo.class.getMethod("getProjectInfo").getReturnType());
    assertEquals(Declaration.class, DeclarationInfo.class.getMethod("getDeclaration").getReturnType());
    assertEquals(javax.swing.JCheckBox.class, DeclarationInfo.class.getMethod("getCheckBox").getReturnType());
    assertEquals(void.class, DeclarationInfo.class.getMethod("resetRequired").getReturnType());
    assertEquals(void.class, DeclarationInfo.class.getMethod("appendDesired", List.class).getReturnType());
    assertEquals(void.class, DeclarationInfo.class.getMethod("updateRequired", Set.class).getReturnType());
    assertEquals(void.class, DeclarationInfo.class.getMethod("updateSwing").getReturnType());
  }
}

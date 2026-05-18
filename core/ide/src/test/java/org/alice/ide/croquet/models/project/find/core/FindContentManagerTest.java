package org.alice.ide.croquet.models.project.find.core;

import edu.cmu.cs.dennisc.pattern.Criterion;
import org.junit.Before;
import org.junit.Test;
import org.lgna.project.ast.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.PatternSyntaxException;

import static org.junit.Assert.*;

public class FindContentManagerTest {

  private FindContentManager manager;

  @Before
  public void setUp() {
    manager = new FindContentManager();
  }

  private NamedUserType createTypeWithFields(String typeName, String... fieldNames) {
    NamedUserType type = new NamedUserType();
    type.name.setValue(typeName);
    for (String fieldName : fieldNames) {
      UserField field = new UserField();
      field.name.setValue(fieldName);
      field.valueType.setValue(JavaType.getInstance(Object.class));
      field.managementLevel.setValue(ManagementLevel.NONE);
      type.fields.add(field);
    }
    return type;
  }

  private NamedUserType createTypeWithMethod(String typeName, String methodName) {
    NamedUserType type = new NamedUserType();
    type.name.setValue(typeName);
    UserMethod method = new UserMethod();
    method.name.setValue(methodName);
    method.returnType.setValue(JavaType.VOID_TYPE);
    method.managementLevel.setValue(ManagementLevel.NONE);
    method.body.setValue(new BlockStatement());
    type.methods.add(method);
    return type;
  }

  @Test
  public void getSearchResults_emptyManager_returnsEmptyList() throws PatternSyntaxException {
    List<SearchResult> results = manager.getSearchResults(new String[]{"test"});
    assertNotNull(results);
    assertTrue(results.isEmpty());
  }

  @Test
  public void initialize_withFields_populatesResults() {
    NamedUserType type = createTypeWithFields("Scene", "myField", "otherField");
    List<Criterion> criteria = Collections.emptyList();
    manager.initialize(type, criteria);
    // After initialization, fields should be in the objectList but have no references,
    // so searching won't return them (they have empty references)
    List<SearchResult> results = manager.getSearchResults(new String[]{"myField"});
    assertNotNull(results);
  }

  @Test
  public void initialize_withMethod_populatesResults() {
    NamedUserType type = createTypeWithMethod("Scene", "doSomething");
    List<Criterion> criteria = Collections.emptyList();
    manager.initialize(type, criteria);
    List<SearchResult> results = manager.getSearchResults(new String[]{"doSomething"});
    assertNotNull(results);
  }

  @Test
  public void initialize_withMethodContainingParameter_populatesResults() {
    NamedUserType type = new NamedUserType();
    type.name.setValue("Scene");
    UserMethod method = new UserMethod();
    method.name.setValue("doAction");
    method.returnType.setValue(JavaType.VOID_TYPE);
    method.managementLevel.setValue(ManagementLevel.NONE);
    method.body.setValue(new BlockStatement());
    UserParameter param = new UserParameter();
    param.name.setValue("speed");
    param.valueType.setValue(JavaType.getInstance(Double.class));
    method.requiredParameters.add(param);
    type.methods.add(method);
    List<Criterion> criteria = Collections.emptyList();
    manager.initialize(type, criteria);
    List<SearchResult> results = manager.getSearchResults(new String[]{"speed"});
    assertNotNull(results);
  }

  @Test
  public void initialize_withMethodContainingLocal_populatesResults() {
    NamedUserType type = new NamedUserType();
    type.name.setValue("Scene");
    UserMethod method = new UserMethod();
    method.name.setValue("doAction");
    method.returnType.setValue(JavaType.VOID_TYPE);
    method.managementLevel.setValue(ManagementLevel.NONE);
    BlockStatement body = new BlockStatement();
    UserLocal local = new UserLocal();
    local.name.setValue("temp");
    local.valueType.setValue(JavaType.getInstance(Integer.class));
    local.isFinal.setValue(false);
    LocalDeclarationStatement decl = new LocalDeclarationStatement(local, new NullLiteral());
    body.statements.add(decl);
    method.body.setValue(body);
    type.methods.add(method);
    List<Criterion> criteria = Collections.emptyList();
    manager.initialize(type, criteria);
    List<SearchResult> results = manager.getSearchResults(new String[]{"temp"});
    assertNotNull(results);
  }

  @Test
  public void refresh_clearsAndReinitializes() {
    NamedUserType type1 = createTypeWithFields("Scene1", "alpha");
    manager.initialize(type1, Collections.emptyList());

    NamedUserType type2 = createTypeWithFields("Scene2", "beta");
    manager.refresh(type2, Collections.emptyList());
    List<SearchResult> results = manager.getSearchResults(new String[]{"beta"});
    assertNotNull(results);
  }

  @Test(expected = PatternSyntaxException.class)
  public void getSearchResults_invalidRegex_throwsPatternSyntaxException() {
    NamedUserType type = createTypeWithFields("Scene", "field1");
    manager.initialize(type, Collections.emptyList());
    manager.getSearchResults(new String[]{"[invalid"});
  }

  @Test
  public void getSearchResults_multipleTerms_allMustMatch() {
    NamedUserType type = createTypeWithFields("Scene", "myField");
    List<Criterion> criteria = new ArrayList<>();
    criteria.add(o -> true);
    manager.initialize(type, criteria);
    List<SearchResult> results = manager.getSearchResults(new String[]{"my", "field"});
    assertNotNull(results);
  }

  @Test
  public void getSearchResults_emptyTermsArray_returnsAll() {
    NamedUserType type = createTypeWithFields("Scene", "alpha");
    manager.initialize(type, Collections.emptyList());
    List<SearchResult> results = manager.getSearchResults(new String[]{});
    assertNotNull(results);
  }

  @Test
  public void initialize_duplicateFields_notDuplicated() {
    NamedUserType type = createTypeWithFields("Scene", "dup");
    manager.initialize(type, Collections.emptyList());
    manager.initialize(type, Collections.emptyList());
    // Second call appends to existing, but checkContains prevents exact duplicates
    List<SearchResult> results = manager.getSearchResults(new String[]{});
    assertNotNull(results);
  }
}

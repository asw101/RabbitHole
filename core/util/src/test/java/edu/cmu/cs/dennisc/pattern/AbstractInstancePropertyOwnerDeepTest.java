package edu.cmu.cs.dennisc.pattern;

import edu.cmu.cs.dennisc.property.BooleanProperty;
import edu.cmu.cs.dennisc.property.InstanceProperty;
import edu.cmu.cs.dennisc.property.ListProperty;
import edu.cmu.cs.dennisc.property.StringProperty;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class AbstractInstancePropertyOwnerDeepTest {

  @Test
  public void getPropertiesFindsPublicInstanceProperties() {
    TestOwner owner = new TestOwner();

    List<InstanceProperty<?>> properties = owner.getProperties();

    assertEquals(3, properties.size());
    assertTrue(properties.contains(owner.name));
    assertTrue(properties.contains(owner.enabled));
    assertTrue(properties.contains(owner.values));
  }

  @Test
  public void propertyLookupAndNameResolutionUsePublicFields() {
    TestOwner owner = new TestOwner();

    assertSame(owner.name, owner.getPropertyNamed("Name"));
    assertEquals("name", owner.lookupNameFor(owner.name));
  }

  @Test
  public void equivalentOwnersCompareByPropertyValues() {
    TestOwner a = new TestOwner();
    TestOwner b = new TestOwner();

    assertTrue(a.isEquivalentTo(b));

    b.name.setValue("different");
    assertFalse(a.isEquivalentTo(b));
  }

  public static final class TestOwner extends AbstractInstancePropertyOwner {
    public final StringProperty name = new StringProperty(this, "name");
    public final BooleanProperty enabled = new BooleanProperty(this, true);
    public final ListProperty<String> values = new ListProperty<>(this);
    public final String ignoredStatic = "ignored";
  }
}

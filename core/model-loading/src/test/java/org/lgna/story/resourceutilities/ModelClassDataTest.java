package org.lgna.story.resourceutilities;

import org.junit.Test;
import org.lgna.story.SJointedModel;
import org.lgna.story.implementation.BasicJointedModelImp;
import org.lgna.story.resources.PropResource;
import org.lgna.story.resources.SwimmerResource;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

public class ModelClassDataTest {
  @Test
  public void baseModelClassDataCopyConstructorPreservesTypes() {
    BaseModelClassData original = new BaseModelClassData(SJointedModel.class, BasicJointedModelImp.class);

    BaseModelClassData copy = new BaseModelClassData(original);

    assertSame(original.abstractionClass, copy.abstractionClass);
    assertSame(original.implementationClass, copy.implementationClass);
  }

  @Test
  public void predefinedModelClassDataExposeExpectedPackagesAndBaseTypes() {
    assertSame(PropResource.class, ModelClassData.PROP_CLASS_DATA.superClass);
    assertEquals("org.lgna.story.resources.prop", ModelClassData.PROP_CLASS_DATA.packageString);
    assertSame(SJointedModel.class, ModelClassData.PROP_CLASS_DATA.abstractionClass);
    assertSame(BasicJointedModelImp.class, ModelClassData.PROP_CLASS_DATA.implementationClass);

    assertSame(SwimmerResource.class, ModelClassData.SWIMMER_CLASS_DATA.superClass);
    assertEquals("org.lgna.story.resources.swimmer", ModelClassData.SWIMMER_CLASS_DATA.packageString);
    assertSame(ModelClassData.SWIMMER_BASE_CLASS_DATA.abstractionClass, ModelClassData.SWIMMER_CLASS_DATA.abstractionClass);
    assertSame(ModelClassData.SWIMMER_BASE_CLASS_DATA.implementationClass, ModelClassData.SWIMMER_CLASS_DATA.implementationClass);
  }
}

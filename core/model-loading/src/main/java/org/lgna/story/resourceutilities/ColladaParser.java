package org.lgna.story.resourceutilities;

import edu.cmu.cs.dennisc.color.Color4f;
import edu.cmu.cs.dennisc.scenegraph.SkeletonVisual;
import edu.cmu.cs.dennisc.scenegraph.TexturedAppearance;
import org.lgna.story.resourceutilities.exporterutils.collada.*;
import org.lgna.story.resourceutilities.exporterutils.collada.Asset.Unit;

import javax.xml.bind.JAXBElement;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.namespace.QName;
import java.time.ZonedDateTime;
import java.util.Map;
import java.util.function.Function;

/**
 * Assembles COLLADA XML document structure: assets, textures, materials, effects.
 */
class ColladaParser {

  private final ObjectFactory factory;

  ColladaParser(ObjectFactory factory) {
    this.factory = factory;
  }

  Asset createAsset() {
    Asset asset = factory.createAsset();

    ZonedDateTime ct = ZonedDateTime.now();
    int zoneOffsetMinutes = ct.getOffset().getTotalSeconds() / 60;
    XMLGregorianCalendar createdDateTime;
    try {
      createdDateTime = DatatypeFactory.newInstance().newXMLGregorianCalendar(ct.getYear(), ct.getMonth().getValue(), ct.getDayOfMonth(), ct.getHour(), ct.getMinute(), ct.getSecond(), 0, zoneOffsetMinutes);
      asset.setCreated(createdDateTime);
      asset.setModified(createdDateTime);
    } catch (DatatypeConfigurationException e) {
      e.printStackTrace();
    }

    Unit unit = factory.createAssetUnit();
    unit.setMeter(1.0);
    unit.setName("meter");
    asset.setUnit(unit);
    asset.setUpAxis(UpAxisType.Y_UP);

    return asset;
  }

  Effect createEffect(TexturedAppearance texturedAppearance, Map<Integer, String> materialNameMap) {
    Integer materialIndex = texturedAppearance.textureId.getValue();
    Effect effect = factory.createEffect();
    final String effectId = materialNameMap.get(materialIndex) + "_fx";
    effect.setId(effectId);
    effect.setName(effectId);
    ProfileCOMMON profile = factory.createProfileCOMMON();
    ProfileCOMMON.Technique technique = factory.createProfileCOMMONTechnique();
    technique.setSid("standard");

    ProfileCOMMON.Technique.Lambert lambert = factory.createProfileCOMMONTechniqueLambert();
    lambert.setEmission(createCommonColorType("emission", 0, 0, 0, 1));
    lambert.setAmbient(createCommonColorType("ambient", 0, 0, 0, 1));
    if (texturedAppearance.diffuseColorTexture.getValue() != null) {
      CommonNewparamType surfaceParam = createSurfaceParam(materialIndex, materialNameMap);
      profile.getImageOrNewparam().add(surfaceParam);
      CommonNewparamType samplerParam = createSamplerParam(materialIndex, surfaceParam.getSid(), materialNameMap);
      profile.getImageOrNewparam().add(samplerParam);
      CommonColorOrTextureType diffuse = factory.createCommonColorOrTextureType();
      CommonColorOrTextureType.Texture texture = factory.createCommonColorOrTextureTypeTexture();
      texture.setTexture(samplerParam.getSid());
      texture.setTexcoord("UVMap");
      diffuse.setTexture(texture);
      lambert.setDiffuse(diffuse);
      if (texturedAppearance.isDiffuseColorTextureAlphaBlended.getValue()) {
        CommonTransparentType transparent = factory.createCommonTransparentType();
        transparent.setTexture(texture);
        lambert.setTransparent(transparent);
      }
    } else {
      Color4f diffuseColor = texturedAppearance.diffuseColor.getValue();
      lambert.setDiffuse(createCommonColorType("diffuse", diffuseColor.red, diffuseColor.green, diffuseColor.blue, diffuseColor.alpha));
    }
    technique.setLambert(lambert);
    profile.setTechnique(technique);
    JAXBElement<ProfileCOMMON> profileCOMMONJAXBElement = new JAXBElement<>(new QName("http://www.collada.org/2005/11/COLLADASchema", "profile_COMMON"), ProfileCOMMON.class, profile);
    effect.getFxProfileAbstract().add(profileCOMMONJAXBElement);

    return effect;
  }

  void createAndAddTextureComponents(COLLADA collada, SkeletonVisual visual,
                                     Map<Integer, String> materialNameMap,
                                     Function<Integer, String> imageNameFn,
                                     Function<Integer, String> externalImageNameFn,
                                     Function<Integer, String> imageFileNameFn,
                                     Function<Integer, String> imageIdFn,
                                     Function<Integer, String> materialIdFn,
                                     Function<Integer, String> effectIdFn) {
    LibraryImages libraryImages = factory.createLibraryImages();
    LibraryMaterials libraryMaterials = factory.createLibraryMaterials();
    LibraryEffects libraryEffects = factory.createLibraryEffects();
    for (TexturedAppearance texture : visual.textures.getValue()) {
      Integer materialIndex = texture.textureId.getValue();
      if (texture.diffuseColorTexture.getValue() != null) {
        Image image = factory.createImage();
        image.setName(imageNameFn.apply(materialIndex));
        image.setId(imageIdFn.apply(materialIndex));
        image.setInitFrom(imageFileNameFn.apply(materialIndex));
        libraryImages.getImage().add(image);
      }

      Material material = factory.createMaterial();
      final String materialId = materialIdFn.apply(materialIndex);
      material.setId(materialId);
      material.setName(materialId);
      InstanceEffect instanceEffect = factory.createInstanceEffect();
      instanceEffect.setUrl("#" + effectIdFn.apply(materialIndex));
      material.setInstanceEffect(instanceEffect);
      libraryMaterials.getMaterial().add(material);

      Effect effect = createEffect(texture, materialNameMap);
      libraryEffects.getEffect().add(effect);
    }
    collada.getLibraryAnimationsOrLibraryAnimationClipsOrLibraryCameras().add(libraryImages);
    collada.getLibraryAnimationsOrLibraryAnimationClipsOrLibraryCameras().add(libraryMaterials);
    collada.getLibraryAnimationsOrLibraryAnimationClipsOrLibraryCameras().add(libraryEffects);
  }

  private CommonColorOrTextureType.Color createCommonColor(String sid, double r, double g, double b, double a) {
    CommonColorOrTextureType.Color color = factory.createCommonColorOrTextureTypeColor();
    color.setSid(sid);
    color.getValue().add(r);
    color.getValue().add(g);
    color.getValue().add(b);
    color.getValue().add(a);
    return color;
  }

  private CommonColorOrTextureType createCommonColorType(String sid, double r, double g, double b, double a) {
    CommonColorOrTextureType colorType = factory.createCommonColorOrTextureType();
    colorType.setColor(createCommonColor(sid, r, g, b, a));
    return colorType;
  }

  private CommonNewparamType createSurfaceParam(Integer materialIndex, Map<Integer, String> materialNameMap) {
    String imageNameForIndex = materialNameMap.get(materialIndex) + "_diffuseMap";
    String surfaceParamId = imageNameForIndex + "-surface";
    CommonNewparamType surfaceParam = factory.createCommonNewparamType();
    surfaceParam.setSid(surfaceParamId);
    FxSurfaceCommon surface = factory.createFxSurfaceCommon();
    surface.setType("2D");
    FxSurfaceInitFromCommon surfaceInit = factory.createFxSurfaceInitFromCommon();
    Image image = factory.createImage();
    image.setName(imageNameForIndex);
    image.setId(imageNameForIndex + "-image");
    surfaceInit.setValue(image);
    surface.getInitFrom().add(surfaceInit);
    surfaceParam.setSurface(surface);
    return surfaceParam;
  }

  private CommonNewparamType createSamplerParam(Integer materialIndex, String surfaceParamId, Map<Integer, String> materialNameMap) {
    String imageNameForIndex = materialNameMap.get(materialIndex) + "_diffuseMap";
    CommonNewparamType samplerParam = factory.createCommonNewparamType();
    final String samplerParamSid = imageNameForIndex + "-sampler";
    samplerParam.setSid(samplerParamSid);
    FxSampler2DCommon sampler = factory.createFxSampler2DCommon();
    sampler.setSource(surfaceParamId);
    samplerParam.setSampler2D(sampler);
    return samplerParam;
  }
}

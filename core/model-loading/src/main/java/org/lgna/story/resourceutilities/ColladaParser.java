package org.lgna.story.resourceutilities;

import edu.cmu.cs.dennisc.scenegraph.SkeletonVisual;
import edu.cmu.cs.dennisc.scenegraph.TexturedAppearance;
import org.lgna.story.resourceutilities.exporterutils.collada.*;

import java.util.Map;
import java.util.function.Function;

/**
 * Assembles COLLADA XML document structure: assets, textures, materials, effects.
 * Stub — implementation pending in Step 8.
 */
class ColladaParser {

  ColladaParser(ObjectFactory factory) {
    throw new UnsupportedOperationException("Not yet implemented");
  }

  Asset createAsset() {
    throw new UnsupportedOperationException("Not yet implemented");
  }

  Effect createEffect(TexturedAppearance texturedAppearance, Map<Integer, String> materialNameMap) {
    throw new UnsupportedOperationException("Not yet implemented");
  }

  void createAndAddTextureComponents(COLLADA collada, SkeletonVisual visual,
                                     Map<Integer, String> materialNameMap,
                                     Function<Integer, String> imageNameFn,
                                     Function<Integer, String> externalImageNameFn,
                                     Function<Integer, String> imageFileNameFn,
                                     Function<Integer, String> imageIdFn,
                                     Function<Integer, String> materialIdFn,
                                     Function<Integer, String> effectIdFn) {
    throw new UnsupportedOperationException("Not yet implemented");
  }
}

/*******************************************************************************
 * Copyright (c) 2006, 2018, Carnegie Mellon University. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * 3. Products derived from the software may not be called "Alice", nor may
 *    "Alice" appear in their name, without prior written permission of
 *    Carnegie Mellon University.
 *
 * 4. All advertising materials mentioning features or use of this software must
 *    display the following acknowledgement: "This product includes software
 *    developed by Carnegie Mellon University"
 *
 * 5. The gallery of art assets and animations provided with this software is
 *    contributed by Electronic Arts Inc. and may be used for personal,
 *    non-commercial, and academic use only. Redistributions of any program
 *    source code that utilizes The Sims 2 Assets must also retain the copyright
 *    notice, list of conditions and the disclaimer contained in
 *    The Alice 3.0 Art Gallery License.
 *
 * DISCLAIMER:
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND.
 * ANY AND ALL EXPRESS, STATUTORY OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY,  FITNESS FOR A
 * PARTICULAR PURPOSE, TITLE, AND NON-INFRINGEMENT ARE DISCLAIMED. IN NO EVENT
 * SHALL THE AUTHORS, COPYRIGHT OWNERS OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT,
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, PUNITIVE OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING FROM OR OTHERWISE RELATING TO
 * THE USE OF OR OTHER DEALINGS WITH THE SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 *******************************************************************************/
package org.lgna.story.resourceutilities;

import de.javagl.jgltf.impl.v2.GlTF;
import de.javagl.jgltf.impl.v2.Image;
import de.javagl.jgltf.impl.v2.Material;
import de.javagl.jgltf.impl.v2.MaterialPbrMetallicRoughness;
import de.javagl.jgltf.impl.v2.Texture;
import de.javagl.jgltf.impl.v2.TextureInfo;
import de.javagl.jgltf.model.Optionals;
import edu.cmu.cs.dennisc.color.Color4f;
import edu.cmu.cs.dennisc.scenegraph.TexturedAppearance;
import edu.cmu.cs.dennisc.texture.BufferedImageTexture;

import javax.imageio.ImageIO;
import java.awt.Transparency;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

/**
 * Handles GLTF material and texture creation.
 * Extracted from {@link JointedModelGltfExporter}.
 */
class GltfMaterialWriter {

  private static final String IMAGE_EXTENSION = "png";

  private final String fullResourceName;

  GltfMaterialWriter(String fullResourceName) {
    this.fullResourceName = fullResourceName;
  }

  Map<Integer, Integer> createAndAddTextureComponents(Path tempDir, GlTF gltf, TexturedAppearance[] textures) throws IOException {
    Map<Integer, Integer> textureMaterialMap = new HashMap<>();
    for (TexturedAppearance texture : textures) {
      Integer textureId = texture.textureId.getValue();
      MaterialPbrMetallicRoughness pbrMetallicRoughness = new MaterialPbrMetallicRoughness();
      pbrMetallicRoughness.setMetallicFactor(0.0f);

      boolean alphaBlend = false;

      edu.cmu.cs.dennisc.texture.Texture diffuseColorTexture = texture.diffuseColorTexture.getValue();

      // Embed diffuse texture
      if (diffuseColorTexture instanceof BufferedImageTexture bufferedImageTexture) {
        Image image = new Image();
        final String uri = getImageFileName(textureId);
        image.setUri(uri);
        final Path imageFile = tempDir.resolve(uri);
        BufferedImage bufferedImage = bufferedImageTexture.getBufferedImage();
        writeTexture(bufferedImage, Files.newOutputStream(imageFile));

        int imageIndex = Optionals.of(gltf.getImages()).size();
        gltf.addImages(image);

        Texture textureOut = new Texture();
        textureOut.setSource(imageIndex);
        int textureIndex = Optionals.of(gltf.getTextures()).size();
        gltf.addTextures(textureOut);

        TextureInfo baseColorTexture = new TextureInfo();
        baseColorTexture.setIndex(textureIndex);
        pbrMetallicRoughness.setBaseColorTexture(baseColorTexture);

        alphaBlend = texture.isDiffuseColorTextureAlphaBlended.getValue() || bufferedImage.getTransparency() == Transparency.TRANSLUCENT;
      }

      // TODO: Transform bump map to normal map

      float opacity = texture.opacity.getValue() != null ? texture.opacity.getValue() : 1.0f;
      float[] baseColorFactor = null;

      // Apply base diffuse color
      if (texture.diffuseColor.getValue() != null) {
        Color4f baseColor = texture.diffuseColor.getValue();
        // Multiply the texture's opacity with the alpha channel of the base color
        baseColorFactor = new float[]{baseColor.red, baseColor.green, baseColor.blue, baseColor.alpha * opacity};
        alphaBlend = alphaBlend || baseColorFactor[3] < 1.0f;
      } else if (opacity != 1.0f) {
        // Just apply opacity
        baseColorFactor = new float[]{1.0f, 1.0f, 1.0f, opacity};
        alphaBlend = true;
      }

      pbrMetallicRoughness.setBaseColorFactor(baseColorFactor);

      Material material = new Material();
      material.setPbrMetallicRoughness(pbrMetallicRoughness);

      // Apply emissive color
      if (texture.emissiveColor.getValue() != null) {
        Color4f emissiveColor = texture.emissiveColor.getValue();
        float[] emissiveFactor = new float[]{emissiveColor.red, emissiveColor.green, emissiveColor.blue};
        material.setEmissiveFactor(emissiveFactor);
      }

      // If we have transparency in our material we need to activate alpha blending which is off by default
      material.setAlphaMode(alphaBlend ? "BLEND" : material.defaultAlphaMode());

      int materialIndex = Optionals.of(gltf.getMaterials()).size();
      gltf.addMaterials(material);
      textureMaterialMap.put(textureId, materialIndex);
    }
    return textureMaterialMap;
  }

  String getImageFileName(Integer textureId) {
    return fullResourceName + "_material_" + textureId + "_diffuseMap." + IMAGE_EXTENSION;
  }

  static void writeTexture(BufferedImage image, OutputStream os) throws IOException {
    ImageIO.write(image, IMAGE_EXTENSION, os);
  }
}

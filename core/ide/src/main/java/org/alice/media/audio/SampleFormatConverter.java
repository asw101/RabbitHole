/*******************************************************************************
 * Copyright (c) 2006, 2015, Carnegie Mellon University. All rights reserved.
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

package org.alice.media.audio;

import java.util.Random;

/**
 * Package-private delegate that handles byte/float PCM conversion, quantization
 * with optional dithering, and format-type encoding for {@link FloatSampleBuffer}.
 *
 * @author Florian Bomers (original), extracted during modernization
 */
class SampleFormatConverter {

  // sample width (must be in order)
  static final int F_8 = 1;
  static final int F_16 = 2;
  static final int F_24 = 3;
  static final int F_32 = 4;
  static final int F_SAMPLE_WIDTH_MASK = F_8 | F_16 | F_24 | F_32;
  // format bit-flags
  static final int F_SIGNED = 8;
  static final int F_BIGENDIAN = 16;

  // supported formats
  static final int CT_8S = F_8 | F_SIGNED;
  static final int CT_8U = F_8;
  static final int CT_16SB = F_16 | F_SIGNED | F_BIGENDIAN;
  static final int CT_16SL = F_16 | F_SIGNED;
  static final int CT_24SB = F_24 | F_SIGNED | F_BIGENDIAN;
  static final int CT_24SL = F_24 | F_SIGNED;
  static final int CT_32SB = F_32 | F_SIGNED | F_BIGENDIAN;
  static final int CT_32SL = F_32 | F_SIGNED;

  private static final float twoPower7 = 128.0f;
  private static final float twoPower15 = 32768.0f;
  private static final float twoPower23 = 8388608.0f;
  private static final float twoPower31 = 2147483648.0f;

  private static final float invTwoPower7 = 1 / twoPower7;
  private static final float invTwoPower15 = 1 / twoPower15;
  private static final float invTwoPower23 = 1 / twoPower23;
  private static final float invTwoPower31 = 1 / twoPower31;

  private static Random random = null;
  private float ditherBits = 0.8f;
  private boolean doDither = false;
  private int ditherMode = FloatSampleBuffer.DITHER_MODE_AUTOMATIC;
  int originalFormatType = 0;

  // ---- dither accessors ----

  void setDitherBits(float ditherBits) {
    if (ditherBits <= 0) {
      throw new IllegalArgumentException("DitherBits must be greater than 0");
    }
    this.ditherBits = ditherBits;
  }

  float getDitherBits() {
    return ditherBits;
  }

  void setDitherMode(int mode) {
    if ((mode != FloatSampleBuffer.DITHER_MODE_AUTOMATIC)
        && (mode != FloatSampleBuffer.DITHER_MODE_ON)
        && (mode != FloatSampleBuffer.DITHER_MODE_OFF)) {
      throw new IllegalArgumentException("Illegal DitherMode");
    }
    this.ditherMode = mode;
  }

  int getDitherMode() {
    return ditherMode;
  }

  // ---- format type ----

  int getFormatType(int ssib, boolean signed, boolean bigEndian) {
    int bytesPerSample = ssib / 8;
    int res = 0;
    if (ssib == 8) {
      res = F_8;
    } else if (ssib == 16) {
      res = F_16;
    } else if (ssib == 24) {
      res = F_24;
    } else if (ssib == 32) {
      res = F_32;
    }
    if (res == 0) {
      throw new IllegalArgumentException("FloatSampleBuffer: unsupported sample size of " + ssib + " bits per sample.");
    }
    if (!signed && (bytesPerSample > 1)) {
      throw new IllegalArgumentException("FloatSampleBuffer: unsigned samples larger than 8 bit are not supported");
    }
    if (signed) {
      res |= F_SIGNED;
    }
    if (bigEndian && (ssib != 8)) {
      res |= F_BIGENDIAN;
    }
    return res;
  }

  // ---- byte → float ----

  static void convertByteToFloat(byte[] input, int offset, int sampleCount,
      float[] output, int bytesPerFrame, int formatType) {
    for (int sample = 0; sample < sampleCount; sample++) {
      switch (formatType) {
      case CT_8S:
        output[sample] = ((float) input[offset]) * invTwoPower7;
        break;
      case CT_8U:
        output[sample] = ((float) ((input[offset] & 0xFF) - 128)) * invTwoPower7;
        break;
      case CT_16SB:
        output[sample] = ((float) ((input[offset] << 8) | (input[offset + 1] & 0xFF))) * invTwoPower15;
        break;
      case CT_16SL:
        output[sample] = ((float) ((input[offset + 1] << 8) | (input[offset] & 0xFF))) * invTwoPower15;
        break;
      case CT_24SB:
        output[sample] = ((float) ((input[offset] << 16) | ((input[offset + 1] & 0xFF) << 8) | (input[offset + 2] & 0xFF))) * invTwoPower23;
        break;
      case CT_24SL:
        output[sample] = ((float) ((input[offset + 2] << 16) | ((input[offset + 1] & 0xFF) << 8) | (input[offset] & 0xFF))) * invTwoPower23;
        break;
      case CT_32SB:
        output[sample] = ((float) ((input[offset] << 24) | ((input[offset + 1] & 0xFF) << 16) | ((input[offset + 2] & 0xFF) << 8) | (input[offset + 3] & 0xFF))) * invTwoPower31;
        break;
      case CT_32SL:
        output[sample] = ((float) ((input[offset + 3] << 24) | ((input[offset + 2] & 0xFF) << 16) | ((input[offset + 1] & 0xFF) << 8) | (input[offset] & 0xFF))) * invTwoPower31;
        break;
      default:
        throw new IllegalArgumentException("Unsupported formatType=" + formatType);
      }
      offset += bytesPerFrame;
    }
  }

  // ---- quantize helpers ----

  private byte quantize8(float sample) {
    if (doDither) {
      sample += random.nextFloat() * ditherBits;
    }
    if (sample >= 127.0f) {
      return (byte) 127;
    } else if (sample <= -128) {
      return (byte) -128;
    } else {
      return (byte) (sample < 0 ? (sample - 0.5f) : (sample + 0.5f));
    }
  }

  private int quantize16(float sample) {
    if (doDither) {
      sample += random.nextFloat() * ditherBits;
    }
    if (sample >= 32767.0f) {
      return 32767;
    } else if (sample <= -32768.0f) {
      return -32768;
    } else {
      return (int) (sample < 0 ? (sample - 0.5f) : (sample + 0.5f));
    }
  }

  private int quantize24(float sample) {
    if (doDither) {
      sample += random.nextFloat() * ditherBits;
    }
    if (sample >= 8388607.0f) {
      return 8388607;
    } else if (sample <= -8388608.0f) {
      return -8388608;
    } else {
      return (int) (sample < 0 ? (sample - 0.5f) : (sample + 0.5f));
    }
  }

  private int quantize32(float sample) {
    if (doDither) {
      sample += random.nextFloat() * ditherBits;
    }
    if (sample >= 2147483647.0f) {
      return 2147483647;
    } else if (sample <= -2147483648.0f) {
      return -2147483648;
    } else {
      return (int) (sample < 0 ? (sample - 0.5f) : (sample + 0.5f));
    }
  }

  // ---- float → byte ----

  void convertFloatToByte(float[] input, int sampleCount, byte[] output,
      int offset, int bytesPerFrame, int formatType) {
    switch (ditherMode) {
    case FloatSampleBuffer.DITHER_MODE_AUTOMATIC:
      doDither = (originalFormatType & F_SAMPLE_WIDTH_MASK) > (formatType & F_SAMPLE_WIDTH_MASK);
      break;
    case FloatSampleBuffer.DITHER_MODE_ON:
      doDither = true;
      break;
    case FloatSampleBuffer.DITHER_MODE_OFF:
      doDither = false;
      break;
    }
    if (doDither && (random == null)) {
      random = new Random();
    }
    int iSample;
    for (int inIndex = 0; inIndex < sampleCount; inIndex++) {
      switch (formatType) {
      case CT_8S:
        output[offset] = quantize8(input[inIndex] * twoPower7);
        break;
      case CT_8U:
        output[offset] = (byte) (quantize8(input[inIndex] * twoPower7) + 128);
        break;
      case CT_16SB:
        iSample = quantize16(input[inIndex] * twoPower15);
        output[offset] = (byte) (iSample >> 8);
        output[offset + 1] = (byte) (iSample & 0xFF);
        break;
      case CT_16SL:
        iSample = quantize16(input[inIndex] * twoPower15);
        output[offset + 1] = (byte) (iSample >> 8);
        output[offset] = (byte) (iSample & 0xFF);
        break;
      case CT_24SB:
        iSample = quantize24(input[inIndex] * twoPower23);
        output[offset] = (byte) (iSample >> 16);
        output[offset + 1] = (byte) ((iSample >>> 8) & 0xFF);
        output[offset + 2] = (byte) (iSample & 0xFF);
        break;
      case CT_24SL:
        iSample = quantize24(input[inIndex] * twoPower23);
        output[offset + 2] = (byte) (iSample >> 16);
        output[offset + 1] = (byte) ((iSample >>> 8) & 0xFF);
        output[offset] = (byte) (iSample & 0xFF);
        break;
      case CT_32SB:
        iSample = quantize32(input[inIndex] * twoPower31);
        output[offset] = (byte) (iSample >> 24);
        output[offset + 1] = (byte) ((iSample >>> 16) & 0xFF);
        output[offset + 2] = (byte) ((iSample >>> 8) & 0xFF);
        output[offset + 3] = (byte) (iSample & 0xFF);
        break;
      case CT_32SL:
        iSample = quantize32(input[inIndex] * twoPower31);
        output[offset + 3] = (byte) (iSample >> 24);
        output[offset + 2] = (byte) ((iSample >>> 16) & 0xFF);
        output[offset + 1] = (byte) ((iSample >>> 8) & 0xFF);
        output[offset] = (byte) (iSample & 0xFF);
        break;
      default:
        throw new IllegalArgumentException("Unsupported formatType=" + formatType);
      }
      offset += bytesPerFrame;
    }
  }
}

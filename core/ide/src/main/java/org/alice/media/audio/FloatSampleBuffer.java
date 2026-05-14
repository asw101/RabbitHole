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

import javax.sound.sampled.AudioFormat;
import java.util.ArrayList;

/**
 * Buffer of float samples normalized to [-1.0, 1.0] with lazy channel/sample
 * management and optional dithering. Byte/float PCM conversion is delegated
 * to {@link SampleFormatConverter}.
 *
 * @author Florian Bomers
 */
public class FloatSampleBuffer {

  /** Whether the functions without lazy parameter are lazy or not. */
  private static final boolean LAZY_DEFAULT = true;

  private ArrayList channels = new ArrayList();
  private int sampleCount = 0;
  private int channelCount = 0;
  private float sampleRate = 0;

  /** Constant for setDitherMode: dithering will be enabled if sample size is decreased */
  public static final int DITHER_MODE_AUTOMATIC = 0;
  /** Constant for setDitherMode: dithering will be done */
  public static final int DITHER_MODE_ON = 1;
  /** Constant for setDitherMode: dithering will not be done */
  public static final int DITHER_MODE_OFF = 2;

  private final SampleFormatConverter converter = new SampleFormatConverter();

  //////////////////////////////// initialization /////////////////////////////////

  public FloatSampleBuffer() {
    this(0, 0, 1);
  }

  public FloatSampleBuffer(int channelCount, int sampleCount, float sampleRate) {
    init(channelCount, sampleCount, sampleRate, LAZY_DEFAULT);
  }

  public FloatSampleBuffer(byte[] buffer, int offset, int byteCount, AudioFormat format) {
    this(format.getChannels(), byteCount / ((format.getSampleSizeInBits() / 8) * format.getChannels()), format.getSampleRate());
    initFromByteArray(buffer, offset, byteCount, format);
  }

  protected void init(int channelCount, int sampleCount, float sampleRate) {
    init(channelCount, sampleCount, sampleRate, LAZY_DEFAULT);
  }

  protected void init(int channelCount, int sampleCount, float sampleRate, boolean lazy) {
    if ((channelCount < 0) || (sampleCount < 0)) {
      throw new IllegalArgumentException("Invalid parameters in initialization of FloatSampleBuffer.");
    }
    setSampleRate(sampleRate);
    if ((getSampleCount() != sampleCount) || (getChannelCount() != channelCount)) {
      createChannels(channelCount, sampleCount, lazy);
    }
  }

  private void createChannels(int channelCount, int sampleCount, boolean lazy) {
    this.sampleCount = sampleCount;
    // lazy delete of all channels. Intentionally lazy !
    this.channelCount = 0;
    for (int ch = 0; ch < channelCount; ch++) {
      insertChannel(ch, false, lazy);
    }
    if (!lazy) {
      // remove hidden channels
      while (channels.size() > channelCount) {
        channels.removeLast();
      }
    }
  }

  public void initFromByteArray(byte[] buffer, int offset, int byteCount, AudioFormat format) {
    initFromByteArray(buffer, offset, byteCount, format, LAZY_DEFAULT);
  }

  public void initFromByteArray(byte[] buffer, int offset, int byteCount, AudioFormat format, boolean lazy) {
    if ((offset + byteCount) > buffer.length) {
      throw new IllegalArgumentException("FloatSampleBuffer.initFromByteArray: buffer too small.");
    }
    boolean signed = format.getEncoding().equals(AudioFormat.Encoding.PCM_SIGNED);
    if (!signed && !format.getEncoding().equals(AudioFormat.Encoding.PCM_UNSIGNED)) {
      throw new IllegalArgumentException("FloatSampleBuffer: only PCM samples are possible.");
    }
    int bytesPerSample = format.getSampleSizeInBits() / 8;
    int bytesPerFrame = bytesPerSample * format.getChannels();
    int thisSampleCount = byteCount / bytesPerFrame;
    init(format.getChannels(), thisSampleCount, format.getSampleRate(), lazy);
    int formatType = converter.getFormatType(format.getSampleSizeInBits(), signed, format.isBigEndian());
    converter.originalFormatType = formatType;
    for (int ch = 0; ch < format.getChannels(); ch++) {
      SampleFormatConverter.convertByteToFloat(buffer, offset, sampleCount, getChannel(ch), bytesPerFrame, formatType);
      offset += bytesPerSample; // next channel
    }

  }

  public void initFromFloatSampleBuffer(FloatSampleBuffer source) {
    init(source.getChannelCount(), source.getSampleCount(), source.getSampleRate());
    for (int ch = 0; ch < getChannelCount(); ch++) {
      System.arraycopy(source.getChannel(ch), 0, getChannel(ch), 0, sampleCount);
    }
  }

  /**
   * deletes all channels, frees memory... This also removes hidden channels
   * by lazy remove.
   */
  public void reset() {
    init(0, 0, 1, false);
  }

  /**
   * destroys any existing data and creates new channels. It also destroys
   * lazy removed channels and samples.
   * @param channels new count
   * @param sampleCount number of samples per channel
   * @param sampleRate to interpret channels
   */
  public void reset(int channels, int sampleCount, float sampleRate) {
    init(channels, sampleCount, sampleRate, false);
  }

  //////////////////////////////// conversion back to bytes /////////////////////////////////

  /**
   * @param format audio encoding details
   * @return the required size of the buffer when convertToByteArray(..) is called
   */
  public int getByteArrayBufferSize(AudioFormat format) {
    if (!format.getEncoding().equals(AudioFormat.Encoding.PCM_SIGNED) && !format.getEncoding().equals(AudioFormat.Encoding.PCM_UNSIGNED)) {
      throw new IllegalArgumentException("FloatSampleBuffer: only PCM samples are possible.");
    }
    int bytesPerSample = format.getSampleSizeInBits() / 8;
    int bytesPerFrame = bytesPerSample * format.getChannels();
    return bytesPerFrame * getSampleCount();
  }

  /**
   * @param buffer to read from
   * @param offset into buffer
   * @param format audio encoding details
   * @exception IllegalArgumentException when buffer is too small or <code>format</code> doesn't  match
   */
  public void convertToByteArray(byte[] buffer, int offset, AudioFormat format) {
    int byteCount = getByteArrayBufferSize(format);
    if ((offset + byteCount) > buffer.length) {
      throw new IllegalArgumentException("FloatSampleBuffer.convertToByteArray: buffer too small.");
    }
    boolean signed = format.getEncoding().equals(AudioFormat.Encoding.PCM_SIGNED);
    if (!signed && !format.getEncoding().equals(AudioFormat.Encoding.PCM_UNSIGNED)) {
      throw new IllegalArgumentException("FloatSampleBuffer.convertToByteArray: only PCM samples are allowed.");
    }
    if (format.getSampleRate() != getSampleRate()) {
      throw new IllegalArgumentException("FloatSampleBuffer.convertToByteArray: different samplerates.");
    }
    if (format.getChannels() != getChannelCount()) {
      throw new IllegalArgumentException("FloatSampleBuffer.convertToByteArray: different channel count.");
    }
    int bytesPerSample = format.getSampleSizeInBits() / 8;
    int bytesPerFrame = bytesPerSample * format.getChannels();
    int formatType = converter.getFormatType(format.getSampleSizeInBits(), signed, format.isBigEndian());
    for (int ch = 0; ch < format.getChannels(); ch++) {
      converter.convertFloatToByte(getChannel(ch), sampleCount, buffer, offset, bytesPerFrame, formatType);
      offset += bytesPerSample; // next channel
    }

  }

  /**
   * Resizes this buffer.
   * <p>
   * If {@code keepOldSamples} is true, as many samples as possible are
   * retained. If the buffer is enlarged, silence is added at the end.
   * If {@code keepOldSamples} is false, existing samples are discarded
   * and the buffer contains random samples.
   * @param newSampleCount resulting number of samples
   * @param keepOldSamples use existing or silence
   */
  public void changeSampleCount(int newSampleCount, boolean keepOldSamples) {
    int oldSampleCount = getSampleCount();
    Object[] oldChannels = null;
    if (keepOldSamples) {
      oldChannels = getAllChannels();
    }
    init(getChannelCount(), newSampleCount, getSampleRate());
    if (keepOldSamples) {
      // copy old channels and eventually silence out new samples
      int copyCount = newSampleCount < oldSampleCount ? newSampleCount : oldSampleCount;
      for (int ch = 0; ch < getChannelCount(); ch++) {
        float[] oldSamples = (float[]) oldChannels[ch];
        float[] newSamples = (float[]) getChannel(ch);
        if (oldSamples != newSamples) {
          // if this sample array was not object of lazy delete
          System.arraycopy(oldSamples, 0, newSamples, 0, copyCount);
        }
        if (oldSampleCount < newSampleCount) {
          // silence out new samples
          for (int i = oldSampleCount; i < newSampleCount; i++) {
            newSamples[i] = 0.0f;
          }
        }
      }
    }
  }

  public void makeSilence() {
    // silence all channels
    if (getChannelCount() > 0) {
      makeSilence(0);
      for (int ch = 1; ch < getChannelCount(); ch++) {
        copyChannel(0, ch);
      }
    }
  }

  public void makeSilence(int channel) {
    float[] samples = getChannel(0);
    for (int i = 0; i < getSampleCount(); i++) {
      samples[i] = 0.0f;
    }
  }

  public void addChannel(boolean silent) {
    // creates new, silent channel
    insertChannel(getChannelCount(), silent);
  }

  /**
   * lazy insert of a (silent) channel at position <code>index</code>.
   * @param index position
   * @param silent random or no data
   */
  public void insertChannel(int index, boolean silent) {
    insertChannel(index, silent, LAZY_DEFAULT);
  }

  /**
   * Inserts a channel at position <code>index</code>.
   * <p>
   * If <code>silent</code> is true, the new channel will be silent. Otherwise
   * it will contain random data.
   * <p>
   * If <code>lazy</code> is true, hidden channels which have at least
   * getSampleCount() elements will be examined for reusage as inserted
   * channel.<br>
   * If <code>lazy</code> is false, still hidden channels are reused, but it
   * is assured that the inserted channel has exactly getSampleCount()
   * elements, thus not wasting memory.
   * @param index position
   * @param silent random or no data
   * @param lazy should trust sample count
   */
  public void insertChannel(int index, boolean silent, boolean lazy) {
    int physSize = channels.size();
    int virtSize = getChannelCount();
    float[] newChannel = null;
    if (physSize > virtSize) {
      // there are hidden channels. Try to use one.
      for (int ch = virtSize; ch < physSize; ch++) {
        float[] thisChannel = (float[]) channels.get(ch);
        if ((lazy && (thisChannel.length >= getSampleCount())) || (!lazy && (thisChannel.length == getSampleCount()))) {
          // we found a matching channel. Use it !
          newChannel = thisChannel;
          channels.remove(ch);
          break;
        }
      }
    }
    if (newChannel == null) {
      newChannel = new float[getSampleCount()];
    }
    channels.add(index, newChannel);
    this.channelCount++;
    if (silent) {
      makeSilence(index);
    }
  }

  /** performs a lazy remove of the channel
   * @param channel to remove
   */
  public void removeChannel(int channel) {
    removeChannel(channel, LAZY_DEFAULT);
  }

  /**
   * Removes a channel. If lazy is true, the channel is not physically
   * removed, but only hidden. These hidden channels are reused by subsequent
   * calls to addChannel or insertChannel.
   * @param channel to remove
   * @param lazy remove or simply hide
   */
  public void removeChannel(int channel, boolean lazy) {
    if (!lazy) {
      channels.remove(channel);
    } else if (channel < (getChannelCount() - 1)) {
      // if not already, move this channel at the end
      channels.add(channels.remove(channel));
    }
    channelCount--;
  }

  /**
   * both source and target channel have to exist. targetChannel will be
   * overwritten
   * @param sourceChannel to copy from
   * @param targetChannel to overwrite
   */
  public void copyChannel(int sourceChannel, int targetChannel) {
    float[] source = getChannel(sourceChannel);
    float[] target = getChannel(targetChannel);
    System.arraycopy(source, 0, target, 0, getSampleCount());
  }

  //////////////////////////////// properties /////////////////////////////////

  public int getChannelCount() {
    return channelCount;
  }

  public int getSampleCount() {
    return sampleCount;
  }

  public float getSampleRate() {
    return sampleRate;
  }

  /**
   * Sets the sample rate of this buffer. NOTE: no conversion is done. The
   * samples are only re-interpreted.
   * @param sampleRate to interpret channels
   */
  public void setSampleRate(float sampleRate) {
    if (sampleRate <= 0) {
      throw new IllegalArgumentException("Invalid samplerate for FloatSampleBuffer.");
    }
    this.sampleRate = sampleRate;
  }

  /**
   * NOTE: the returned array may be larger than sampleCount. So in any case,
   * sampleCount is to be respected.
   * @param channel to be returned
   * @return channel content
   */
  public float[] getChannel(int channel) {
    if ((channel < 0) || (channel >= getChannelCount())) {
      throw new IllegalArgumentException("FloatSampleBuffer: invalid channel number.");
    }
    return (float[]) channels.get(channel);
  }

  public Object[] getAllChannels() {
    Object[] res = new Object[getChannelCount()];
    for (int ch = 0; ch < getChannelCount(); ch++) {
      res[ch] = getChannel(ch);
    }
    return res;
  }

  public void setDitherBits(float ditherBits) {
    converter.setDitherBits(ditherBits);
  }

  public float getDitherBits() {
    return converter.getDitherBits();
  }

  /**
   * Sets the mode for dithering. This can be one of:
   * DITHER_MODE_AUTOMATIC, DITHER_MODE_ON, or DITHER_MODE_OFF.
   * @param mode auto, on, or off
   */
  public void setDitherMode(int mode) {
    converter.setDitherMode(mode);
  }

  public int getDitherMode() {
    return converter.getDitherMode();
  }

  public int getFormatType(int ssib, boolean signed, boolean bigEndian) {
    return converter.getFormatType(ssib, signed, bigEndian);
  }
}

package org.alice.serialization.tweedle;

public class UnsupportedTweedleDecodeException extends RuntimeException {
  public UnsupportedTweedleDecodeException(String message) {
    super(message);
  }

  public UnsupportedTweedleDecodeException(String message, Throwable cause) {
    super(message, cause);
  }
}

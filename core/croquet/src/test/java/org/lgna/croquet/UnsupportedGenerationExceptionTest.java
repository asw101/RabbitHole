package org.lgna.croquet;
import org.junit.Test;
import static org.junit.Assert.*;
public class UnsupportedGenerationExceptionTest {
  @Test public void noArgConstructor() { assertNull(new UnsupportedGenerationException().getMessage()); }
  @Test public void noArgConstructor_causeNull() { assertNull(new UnsupportedGenerationException().getCause()); }
  @Test public void messageConstructor() { assertEquals("msg", new UnsupportedGenerationException("msg").getMessage()); }
  @Test public void causeConstructor() { RuntimeException c=new RuntimeException("r"); assertSame(c, new UnsupportedGenerationException(c).getCause()); }
  @Test public void fullConstructor() { RuntimeException c=new RuntimeException(); UnsupportedGenerationException e=new UnsupportedGenerationException("m",c); assertEquals("m",e.getMessage()); assertSame(c,e.getCause()); }
  @Test public void extendsException() { assertTrue(new UnsupportedGenerationException() instanceof Exception); }
  @Test public void isChecked() { assertFalse(RuntimeException.class.isAssignableFrom(UnsupportedGenerationException.class)); }
  @Test public void stackTrace() { assertTrue(new UnsupportedGenerationException().getStackTrace().length>0); }
  @Test public void emptyMessage() { assertEquals("", new UnsupportedGenerationException("").getMessage()); }
  @Test public void nestedCause() { Exception r=new Exception("r"); RuntimeException m=new RuntimeException("m",r); UnsupportedGenerationException e=new UnsupportedGenerationException("t",m); assertSame(m,e.getCause()); assertSame(r,e.getCause().getCause()); }
}

package org.alice.serialization.tweedle;

import org.junit.Test;
import org.lgna.project.ast.BlockStatement;
import org.lgna.project.ast.Comment;
import org.lgna.project.ast.DoInOrder;
import org.lgna.project.ast.JavaType;
import org.lgna.project.ast.NamedUserType;
import org.lgna.project.ast.UserMethod;
import org.lgna.project.ast.UserParameter;
import org.lgna.project.VersionNotSupportedException;

import java.util.HashSet;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThrows;

/**
 * Characterizes the Tweedle decoder gap for {@link Comment} statements.
 *
 * <p>The Tweedle grammar routes {@code //} and {@code /* *&#47;} comments to the
 * hidden channel, so a {@link Comment} that the encoder emits is silently dropped
 * when the source is parsed back. Rather than decode to an AST that is missing the
 * comment, the decoder rejects comment-bearing source with an
 * {@link UnsupportedTweedleDecodeException}. In the hybrid {@code .a3c}/{@code .a3p}
 * reader this routes the whole archive through the legacy XML AST fallback, which
 * preserves comments — keeping the round-trip "never worse than XML".
 */
public class TweedleCommentDecodeGapTest {

  private static NamedUserType voidMethodType(String typeName, BlockStatement body) {
    NamedUserType type = new NamedUserType();
    type.name.setValue(typeName);
    type.superType.setValue(JavaType.getInstance(Object.class));
    UserMethod method = new UserMethod("performStep", Void.TYPE, new UserParameter[0], body);
    method.isStatic.setValue(true);
    type.methods.add(method);
    return type;
  }

  private static NamedUserType decode(String tweedleSource) throws VersionNotSupportedException {
    return (NamedUserType) new TweedleEncoderDecoder().decode(tweedleSource, new HashSet<>());
  }

  @Test
  public void encodedCommentStatementFailsToDecode() {
    String source = new TweedleEncoderDecoder()
        .encode(voidMethodType("Foo", new BlockStatement(new Comment("student comment tile"))));
    assertThrows(UnsupportedTweedleDecodeException.class, () -> decode(source));
  }

  @Test
  public void commentNestedInDoInOrderFailsToDecode() {
    String source = new TweedleEncoderDecoder()
        .encode(voidMethodType("Foo", new BlockStatement(new DoInOrder(new BlockStatement(new Comment("nested"))))));
    assertThrows(UnsupportedTweedleDecodeException.class, () -> decode(source));
  }

  @Test
  public void rawLineCommentInSourceFailsToDecode() {
    String source = "class Foo extends Object models Foo {\n"
        + "  static void performStep() {\n"
        + "    // a line comment\n"
        + "  }\n"
        + "}\n";
    assertThrows(UnsupportedTweedleDecodeException.class, () -> decode(source));
  }

  @Test
  public void rawBlockCommentInSourceFailsToDecode() {
    String source = "class Foo extends Object models Foo {\n"
        + "  static void performStep() {\n"
        + "    /* a block comment */\n"
        + "  }\n"
        + "}\n";
    assertThrows(UnsupportedTweedleDecodeException.class, () -> decode(source));
  }

  @Test
  public void commentFreeTypeStillDecodes() throws VersionNotSupportedException {
    String source = new TweedleEncoderDecoder().encode(voidMethodType("Bar", new BlockStatement()));
    NamedUserType decoded = decode(source);
    assertNotNull(decoded);
    assertEquals("Bar", decoded.getName());
  }

  @Test
  public void slashesInsideStringLiteralAreNotTreatedAsComments() {
    // "//" inside a string literal must not be mistaken for a line comment.
    org.alice.tweedle.unlinked.TweedleUnlinkedParser parser =
        new org.alice.tweedle.unlinked.TweedleUnlinkedParser();
    org.junit.Assert.assertFalse(
        parser.sourceContainsComments(
            "class Bar extends Object models Bar {\n"
                + "  static TextString performStep() {\n"
                + "    return \"http://example.com/path\";\n"
                + "  }\n"
                + "}\n"));
    org.junit.Assert.assertTrue(parser.sourceContainsComments("class Bar { // note\n}"));
  }
}

package com.dddviewr.collada;

import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class ColladaValueTypesTest {
  @Test
  public void baseCreateIndentReturnsRequestedNumberOfSpaces() {
    TestBase base = new TestBase();

    assertEquals("", base.createIndent(0));
    assertEquals(3, base.createIndent(3).length());
    assertEquals(20, base.createIndent(20).length());
    assertTrue(base.createIndent(20).chars().allMatch(ch -> ch == ' '));
  }

  @Test
  public void inputStoresAccessorsAndDumpsOptionalFields() {
    Input input = new Input("POSITION", "#positions");
    input.setSemantic("NORMAL");
    input.setSource("#normals");
    input.setOffset(2);
    input.setSet(1);

    assertEquals("NORMAL", input.getSemantic());
    assertEquals("#normals", input.getSource());
    assertEquals(2, input.getOffset());
    assertEquals(1, input.getSet());
    assertEquals(
        "  Input (semantic: NORMAL, source: #normals, offset: 2, set: 1)" + System.lineSeparator(),
        dump(input, 2));
  }

  @Test
  public void paramStoresAccessorsAndDumpOutput() {
    Param param = new Param("surface", "float4");
    param.setName("emission");
    param.setType("float3");

    assertEquals("emission", param.getName());
    assertEquals("float3", param.getType());
    assertEquals(
        " Param (name: emission, type: float3)" + System.lineSeparator(),
        dump(param, 1));
  }

  @Test
  public void unitStoresAccessorsAndCharacterizesCurrentDumpFormat() {
    Unit unit = new Unit(1.0f, "meters");
    unit.setMeter(0.01f);
    unit.setName("centimeters");

    assertEquals(0.01f, unit.getMeter(), 0.0f);
    assertEquals("centimeters", unit.getName());
    assertEquals("  Unit (meter: 0.01, name: centimeters", dump(unit, 2));
  }

  @Test
  public void floatArrayParsesUpToDeclaredCountAndDumpsValues() {
    FloatArray floatArray = new FloatArray("coords", 3);
    floatArray.parse(new StringBuilder("1.5 2.25 3.75 9.0"));

    assertEquals(3, floatArray.getCount());
    assertEquals(1.5f, floatArray.get(0), 0.0f);
    assertArrayEquals(new float[] {1.5f, 2.25f, 3.75f}, floatArray.getData(), 0.0f);
    String dump = dump(floatArray, 2);
    assertTrue(dump.contains("FloatArray (id: coords, count: 3)"));
    assertTrue(dump.contains("1.5 2.25 3.75"));
  }

  @Test
  public void nameArrayParsesTokensAndDumpsData() {
    NameArray nameArray = new NameArray("names", 2);
    nameArray.parse(new StringBuilder("  Alice   Rabbit  Extra"));

    assertEquals(2, nameArray.getCount());
    assertArrayEquals(new String[] {"Alice", "Rabbit"}, nameArray.getData());
    String dump = dump(nameArray, 1);
    assertTrue(dump.contains("NameArray (id: names, count: 2)"));
    assertTrue(dump.contains("Alice Rabbit"));
  }

  @Test
  public void idrefArrayParsesTokensAndDumpsData() {
    IdrefArray idrefArray = new IdrefArray("ids", 2);
    idrefArray.parse(new StringBuilder(" node-1   node-2 node-3"));

    assertEquals(2, idrefArray.getCount());
    assertArrayEquals(new String[] {"node-1", "node-2"}, idrefArray.getData());
    String dump = dump(idrefArray, 1);
    assertTrue(dump.contains("IdrefArray (id: ids, count: 2)"));
    assertTrue(dump.contains("node-1 node-2"));
  }

  @Test
  public void vcountParsesIntegerSequenceAndDumpsData() {
    Vcount vcount = new Vcount();
    vcount.parse(new StringBuilder("1 2 3"));

    assertArrayEquals(new int[] {1, 2, 3}, vcount.getData());
    String dump = dump(vcount, 2);
    assertTrue(dump.contains("  Vcount"));
    assertTrue(dump.contains("1 2 3"));
  }

  private static String dump(Base base, int indent) {
    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    PrintStream printStream = new PrintStream(outputStream, true, StandardCharsets.UTF_8);
    base.dump(printStream, indent);
    printStream.flush();
    return outputStream.toString(StandardCharsets.UTF_8);
  }

  private static final class TestBase extends Base {
    @Override
    public void dump(PrintStream out, int indent) {
      out.print(createIndent(indent));
    }
  }
}

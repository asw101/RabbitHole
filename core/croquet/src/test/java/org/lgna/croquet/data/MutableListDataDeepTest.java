package org.lgna.croquet.data;

import org.lgna.croquet.CroquetTestUtils;
import org.lgna.croquet.ItemCodec;
import org.junit.Before;
import org.junit.Test;

import javax.swing.event.ListDataEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.Assert.*;

public class MutableListDataDeepTest {

  private MutableListData<String> data;

  @Before
  public void setUp() {
    data = new MutableListData<>(CroquetTestUtils.STRING_CODEC, new String[]{"alpha", "bravo", "charlie"});
  }

  @Test
  public void toArray_usesCodecValueClassForComponentType() {
    MutableListData<Label> labels = new MutableListData<>(LABEL_CODEC, Arrays.asList(new Label("a"), new Label("b")));

    Object array = labels.toArray();

    assertEquals(Label[].class, array.getClass());
    assertEquals("a", ((Label[]) array)[0].text);
  }

  @Test
  public void internalAddItem_middle_preservesRelativeOrdering() {
    data.internalAddItem(1, "between");

    assertArrayEquals(new String[]{"alpha", "between", "bravo", "charlie"}, data.toArray());
  }

  @Test
  public void iterator_snapshot_isUnaffectedByLaterAppend() {
    Iterator<String> iterator = data.iterator();

    data.internalAddItem("delta");

    assertEquals(Arrays.asList("alpha", "bravo", "charlie"), collect(iterator));
    assertArrayEquals(new String[]{"alpha", "bravo", "charlie", "delta"}, data.toArray());
  }

  @Test
  public void multipleListeners_areAllNotifiedForSingleMutation() {
    AtomicInteger first = new AtomicInteger();
    AtomicInteger second = new AtomicInteger();
    data.addListener(new TestListDataListener() {
      @Override
      public void contentsChanged(ListDataEvent e) {
        first.incrementAndGet();
      }
    });
    data.addListener(new TestListDataListener() {
      @Override
      public void contentsChanged(ListDataEvent e) {
        second.incrementAndGet();
      }
    });

    data.internalAddItem("delta");

    assertEquals(1, first.get());
    assertEquals(1, second.get());
  }

  @Test
  public void internalAddItem_firesContentsChangedForEntirePostMutationRange() {
    AtomicReference<ListDataEvent> captured = new AtomicReference<>();
    data.addListener(new TestListDataListener() {
      @Override
      public void contentsChanged(ListDataEvent e) {
        captured.set(e);
      }
    });

    data.internalAddItem(1, "between");

    assertNotNull(captured.get());
    assertEquals(ListDataEvent.CONTENTS_CHANGED, captured.get().getType());
    assertEquals(0, captured.get().getIndex0());
    assertEquals(3, captured.get().getIndex1());
  }

  @Test
  public void internalRemoveItem_missingValue_stillFiresContentsChanged() {
    AtomicInteger fireCount = new AtomicInteger();
    data.addListener(new TestListDataListener() {
      @Override
      public void contentsChanged(ListDataEvent e) {
        fireCount.incrementAndGet();
      }
    });

    data.internalRemoveItem("missing");

    assertEquals(1, fireCount.get());
    assertEquals(3, data.getItemCount());
  }

  @Test
  public void internalSetAllItems_emptyCollection_clearsDataAndReportsNegativeEndIndex() {
    AtomicReference<ListDataEvent> captured = new AtomicReference<>();
    data.addListener(new TestListDataListener() {
      @Override
      public void contentsChanged(ListDataEvent e) {
        captured.set(e);
      }
    });

    data.internalSetAllItems(Collections.emptyList());

    assertEquals(0, data.getItemCount());
    assertNotNull(captured.get());
    assertEquals(-1, captured.get().getIndex0());
    assertEquals(0, captured.get().getIndex1());
  }

  @Test
  public void internalSetAllItems_duplicateValues_arePreservedAndIndexedFromFirstMatch() {
    data.internalSetAllItems(Arrays.asList("alpha", "alpha", "beta"));

    assertArrayEquals(new String[]{"alpha", "alpha", "beta"}, data.toArray());
    assertEquals(0, data.indexOf("alpha"));
  }

  private static List<String> collect(Iterator<String> iterator) {
    List<String> values = new ArrayList<>();
    while (iterator.hasNext()) {
      values.add(iterator.next());
    }
    return values;
  }

  private static final ItemCodec<Label> LABEL_CODEC = new ItemCodec<Label>() {
    @Override
    public Class<Label> getValueClass() {
      return Label.class;
    }

    @Override
    public Label decodeValue(edu.cmu.cs.dennisc.codec.BinaryDecoder binaryDecoder) {
      return new Label(binaryDecoder.decodeString());
    }

    @Override
    public void encodeValue(edu.cmu.cs.dennisc.codec.BinaryEncoder binaryEncoder, Label value) {
      binaryEncoder.encode(value.text);
    }

    @Override
    public void appendRepresentation(StringBuilder sb, Label value) {
      sb.append(value.text);
    }
  };

  private static final class Label {
    private final String text;

    private Label(String text) {
      this.text = text;
    }
  }
}

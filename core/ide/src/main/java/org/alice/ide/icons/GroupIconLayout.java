package org.alice.ide.icons;

final class GroupIconLayout {
  private static final int MINIMUM_COMPOSITE_WIDTH = 64;

  private GroupIconLayout() {
    throw new AssertionError();
  }

  static boolean shouldRenderComposite(int width) {
    return width > MINIMUM_COMPOSITE_WIDTH;
  }

  static Layout createLayout(int iconCount) {
    return switch (iconCount) {
      case 0 -> new Layout(new int[] {}, new int[] {});
      case 1 -> new Layout(new int[] {2}, new int[] {2});
      case 2 -> new Layout(new int[] {1, 3}, new int[] {1, 3});
      case 3 -> new Layout(new int[] {0, 4, 2}, new int[] {0, 2, 4});
      case 4 -> new Layout(new int[] {0, 4, 1, 3}, new int[] {0, 1, 3, 4});
      default -> new Layout(new int[] {0, 4, 1, 3, 2}, new int[] {0, 1, 2, 3, 4});
    };
  }

  static final class Layout {
    private final int[] drawOrder;
    private final int[] sourceSlots;

    private Layout(int[] drawOrder, int[] sourceSlots) {
      this.drawOrder = drawOrder;
      this.sourceSlots = sourceSlots;
    }

    int[] getDrawOrder() {
      return this.drawOrder.clone();
    }

    int getSlotForSourceIndex(int sourceIndex) {
      return this.sourceSlots[sourceIndex];
    }
  }
}

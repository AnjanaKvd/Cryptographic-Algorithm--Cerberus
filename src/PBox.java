public class PBox {
    private final int[] pBox;
    private final int[] inversePBox;

    public PBox() {
        this.pBox = new int[]{4, 7, 1, 6, 3, 0, 5, 2};
        this.inversePBox = generateInverse(this.pBox);
    }

    private int[] generateInverse(int[] box) {
        int[] inverse = new int[box.length];
        for (int i = 0; i < box.length; i++) {
            inverse[box[i]] = i;
        }
        return inverse;
    }

    public String permute(String block) {
        char[] output = new char[block.length()];
        char[] input = block.toCharArray();
        for (int i = 0; i < input.length; i++) {
            output[this.pBox[i]] = input[i];
        }
        return new String(output);
    }

    public String inversePermute(String block) {
        char[] output = new char[block.length()];
        char[] input = block.toCharArray();
        for (int i = 0; i < input.length; i++) {
            output[this.inversePBox[i]] = input[i];
        }
        return new String(output);
    }
}
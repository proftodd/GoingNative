package org.jtodd.ffm;

public class JRashunalMatrix {
    private final int height;
    private final int width;
    private final JRashunal[] data;

    public JRashunalMatrix(int height, int width, JRashunal[] data) {
        this.height = height;
        this.width = width;
        this.data = data;
    }

    public JRashunalMatrix(int[][][] data) {
        int height = data.length;
        int width = data[0].length;
        JRashunal jdata[] = new JRashunal[height * width];
        for (int i = 0; i < height; ++i) {
            for (int j = 0; j < width; ++j) {
                int[] cell = data[i][j];
                int numerator = cell[0];
                int denominator = cell.length == 1 ? 1 : cell[1];
                jdata[i * width + j] = new JRashunal(numerator, denominator);
            }
        }
        this.height = height;
        this.width = width;
        this.data = jdata;
    }
    
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < height; ++i) {
            builder.append("[ ");
            for (int j = 0; j < width; ++j) {
                builder.append(data[i * width + j]);
                builder.append(" ");
            }
            builder.append("]\n");
        }
        return builder.toString();
    }
}

package org.jtodd.ffm;

public class JRashunalMatrix {
    private int height;
    private int width;
    private JRashunal[] data;

    public JRashunalMatrix(int height, int width, JRashunal[] data) {
        this.height = height;
        this.width = width;
        this.data = data;
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

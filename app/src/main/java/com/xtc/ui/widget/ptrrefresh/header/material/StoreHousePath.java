package com.xtc.ui.widget.ptrrefresh.header.material;

import android.util.SparseArray;
import java.util.ArrayList;

/** 字形点阵表：把字符映射为若干线段，供 StoreHouseHeader 绘制文字。 */
public class StoreHousePath {
    private static final SparseArray<float[]> sPointList = new SparseArray<>();

    static {
        float[][] uppercasePaths = {
                {24.0f, 0.0f, 1.0f, 22.0f, 1.0f, 22.0f, 1.0f, 72.0f, 24.0f, 0.0f, 47.0f, 22.0f, 47.0f, 22.0f, 47.0f, 72.0f, 1.0f, 48.0f, 47.0f, 48.0f},
                {0.0f, 0.0f, 0.0f, 72.0f, 0.0f, 0.0f, 37.0f, 0.0f, 37.0f, 0.0f, 47.0f, 11.0f, 47.0f, 11.0f, 47.0f, 26.0f, 47.0f, 26.0f, 38.0f, 36.0f, 38.0f, 36.0f, 0.0f, 36.0f, 38.0f, 36.0f, 47.0f, 46.0f, 47.0f, 46.0f, 47.0f, 61.0f, 47.0f, 61.0f, 38.0f, 71.0f, 37.0f, 72.0f, 0.0f, 72.0f},
                {47.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 72.0f, 0.0f, 72.0f, 47.0f, 72.0f},
                {0.0f, 0.0f, 0.0f, 72.0f, 0.0f, 0.0f, 24.0f, 0.0f, 24.0f, 0.0f, 47.0f, 22.0f, 47.0f, 22.0f, 47.0f, 48.0f, 47.0f, 48.0f, 23.0f, 72.0f, 23.0f, 72.0f, 0.0f, 72.0f},
                {0.0f, 0.0f, 0.0f, 72.0f, 0.0f, 0.0f, 47.0f, 0.0f, 0.0f, 36.0f, 37.0f, 36.0f, 0.0f, 72.0f, 47.0f, 72.0f},
                {0.0f, 0.0f, 0.0f, 72.0f, 0.0f, 0.0f, 47.0f, 0.0f, 0.0f, 36.0f, 37.0f, 36.0f},
                {47.0f, 23.0f, 47.0f, 0.0f, 47.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 72.0f, 0.0f, 72.0f, 47.0f, 72.0f, 47.0f, 72.0f, 47.0f, 48.0f, 47.0f, 48.0f, 24.0f, 48.0f},
                {0.0f, 0.0f, 0.0f, 72.0f, 0.0f, 36.0f, 47.0f, 36.0f, 47.0f, 0.0f, 47.0f, 72.0f},
                {0.0f, 0.0f, 47.0f, 0.0f, 24.0f, 0.0f, 24.0f, 72.0f, 0.0f, 72.0f, 47.0f, 72.0f},
                {47.0f, 0.0f, 47.0f, 72.0f, 47.0f, 72.0f, 24.0f, 72.0f, 24.0f, 72.0f, 0.0f, 48.0f},
                {0.0f, 0.0f, 0.0f, 72.0f, 47.0f, 0.0f, 3.0f, 33.0f, 3.0f, 38.0f, 47.0f, 72.0f},
                {0.0f, 0.0f, 0.0f, 72.0f, 0.0f, 72.0f, 47.0f, 72.0f},
                {0.0f, 0.0f, 0.0f, 72.0f, 0.0f, 0.0f, 24.0f, 23.0f, 24.0f, 23.0f, 47.0f, 0.0f, 47.0f, 0.0f, 47.0f, 72.0f},
                {0.0f, 0.0f, 0.0f, 72.0f, 0.0f, 0.0f, 47.0f, 72.0f, 47.0f, 72.0f, 47.0f, 0.0f},
                {0.0f, 0.0f, 0.0f, 72.0f, 0.0f, 72.0f, 47.0f, 72.0f, 47.0f, 72.0f, 47.0f, 0.0f, 47.0f, 0.0f, 0.0f, 0.0f},
                {0.0f, 0.0f, 0.0f, 72.0f, 0.0f, 0.0f, 47.0f, 0.0f, 47.0f, 0.0f, 47.0f, 36.0f, 47.0f, 36.0f, 0.0f, 36.0f},
                {0.0f, 0.0f, 0.0f, 72.0f, 0.0f, 72.0f, 23.0f, 72.0f, 23.0f, 72.0f, 47.0f, 48.0f, 47.0f, 48.0f, 47.0f, 0.0f, 47.0f, 0.0f, 0.0f, 0.0f, 24.0f, 28.0f, 47.0f, 71.0f},
                {0.0f, 0.0f, 0.0f, 72.0f, 0.0f, 0.0f, 47.0f, 0.0f, 47.0f, 0.0f, 47.0f, 36.0f, 47.0f, 36.0f, 0.0f, 36.0f, 0.0f, 37.0f, 47.0f, 72.0f},
                {47.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 36.0f, 0.0f, 36.0f, 47.0f, 36.0f, 47.0f, 36.0f, 47.0f, 72.0f, 47.0f, 72.0f, 0.0f, 72.0f},
                {0.0f, 0.0f, 47.0f, 0.0f, 24.0f, 0.0f, 24.0f, 72.0f},
                {0.0f, 0.0f, 0.0f, 72.0f, 0.0f, 72.0f, 47.0f, 72.0f, 47.0f, 72.0f, 47.0f, 0.0f},
                {0.0f, 0.0f, 24.0f, 72.0f, 24.0f, 72.0f, 47.0f, 0.0f},
                {0.0f, 0.0f, 0.0f, 72.0f, 0.0f, 72.0f, 24.0f, 49.0f, 24.0f, 49.0f, 47.0f, 72.0f, 47.0f, 72.0f, 47.0f, 0.0f},
                {0.0f, 0.0f, 47.0f, 72.0f, 47.0f, 0.0f, 0.0f, 72.0f},
                {0.0f, 0.0f, 24.0f, 23.0f, 47.0f, 0.0f, 24.0f, 23.0f, 24.0f, 23.0f, 24.0f, 72.0f},
                {0.0f, 0.0f, 47.0f, 0.0f, 47.0f, 0.0f, 0.0f, 72.0f, 0.0f, 72.0f, 47.0f, 72.0f}};
        float[][] digitPaths = {
                {0.0f, 0.0f, 0.0f, 72.0f, 0.0f, 72.0f, 47.0f, 72.0f, 47.0f, 72.0f, 47.0f, 0.0f, 47.0f, 0.0f, 0.0f, 0.0f},
                {24.0f, 0.0f, 24.0f, 72.0f},
                {0.0f, 0.0f, 47.0f, 0.0f, 47.0f, 0.0f, 47.0f, 36.0f, 47.0f, 36.0f, 0.0f, 36.0f, 0.0f, 36.0f, 0.0f, 72.0f, 0.0f, 72.0f, 47.0f, 72.0f},
                {0.0f, 0.0f, 47.0f, 0.0f, 47.0f, 0.0f, 47.0f, 36.0f, 47.0f, 36.0f, 0.0f, 36.0f, 47.0f, 36.0f, 47.0f, 72.0f, 47.0f, 72.0f, 0.0f, 72.0f},
                {0.0f, 0.0f, 0.0f, 36.0f, 0.0f, 36.0f, 47.0f, 36.0f, 47.0f, 0.0f, 47.0f, 72.0f},
                {0.0f, 0.0f, 0.0f, 36.0f, 0.0f, 36.0f, 47.0f, 36.0f, 47.0f, 36.0f, 47.0f, 72.0f, 47.0f, 72.0f, 0.0f, 72.0f, 0.0f, 0.0f, 47.0f, 0.0f},
                {0.0f, 0.0f, 0.0f, 72.0f, 0.0f, 72.0f, 47.0f, 72.0f, 47.0f, 72.0f, 47.0f, 36.0f, 47.0f, 36.0f, 0.0f, 36.0f},
                {0.0f, 0.0f, 47.0f, 0.0f, 47.0f, 0.0f, 47.0f, 72.0f},
                {0.0f, 0.0f, 0.0f, 72.0f, 0.0f, 72.0f, 47.0f, 72.0f, 47.0f, 72.0f, 47.0f, 0.0f, 47.0f, 0.0f, 0.0f, 0.0f, 0.0f, 36.0f, 47.0f, 36.0f},
                {47.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 36.0f, 0.0f, 36.0f, 47.0f, 36.0f, 47.0f, 0.0f, 47.0f, 72.0f}};
        for (int index = 0; index < uppercasePaths.length; index++) {
            sPointList.append(index + 65, uppercasePaths[index]);
        }
        for (int index = 0; index < uppercasePaths.length; index++) {
            sPointList.append(index + 65 + 32, uppercasePaths[index]);
        }
        for (int index = 0; index < digitPaths.length; index++) {
            sPointList.append(index + 48, digitPaths[index]);
        }
        addChar(' ', new float[0]);
        addChar('-', new float[]{0.0f, 36.0f, 47.0f, 36.0f});
        addChar('.', new float[]{24.0f, 60.0f, 24.0f, 72.0f});
    }

    public static void addChar(char character, float[] points) {
        sPointList.append(character, points);
    }

    public static ArrayList<float[]> getPath(String text) {
        return getPath(text, 1.0f, 14);
    }

    public static ArrayList<float[]> getPath(String text, float scale, int charSpacing) {
        ArrayList<float[]> result = new ArrayList<>();
        float offsetX = 0.0f;
        for (int index = 0; index < text.length(); index++) {
            char character = text.charAt(index);
            if (sPointList.indexOfKey(character) != -1) {
                float[] points = sPointList.get(character);
                int segmentCount = points.length / 4;
                for (int segment = 0; segment < segmentCount; segment++) {
                    float[] line = new float[4];
                    for (int pointIndex = 0; pointIndex < 4; pointIndex++) {
                        float value = points[(segment * 4) + pointIndex];
                        if (pointIndex % 2 == 0) {
                            line[pointIndex] = (value + offsetX) * scale;
                        } else {
                            line[pointIndex] = value * scale;
                        }
                    }
                    result.add(line);
                }
                offsetX += charSpacing + 57;
            }
        }
        return result;
    }
}
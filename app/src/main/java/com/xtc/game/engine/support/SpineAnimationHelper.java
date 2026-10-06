package com.xtc.game.engine.support;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;

import java.util.ArrayList;
import java.util.List;

/**
 * 骨骼动画辅助工具：解析骨骼文件中的动画名称列表。
 */
public class SpineAnimationHelper {

    public static List<String> parseAnimationNames(FileHandle fileHandle) {
        List<String> names = new ArrayList<>();
        if (fileHandle != null && fileHandle.exists()) {
            JsonValue animation = new JsonReader().parse(fileHandle).get("animations");
            while (animation != null) {
                names.add(animation.name);
                animation = animation.next;
            }
        }
        return names;
    }
}
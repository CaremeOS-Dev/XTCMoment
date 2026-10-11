# -*- coding: utf-8 -*-
"""生成「搜索动态」入口用的放大镜图标（纯 PIL 绘制，无外部依赖）。

用法（在仓库根目录执行）：
    & C:\\Python314\\python.exe tools\\icons\\make_search_icon.py

产物（覆盖写入，可重复执行）：
    app/src/main/res/drawable-xhdpi/ic_moment_search.png   48x48（24dp @2x）
    app/src/main/res/drawable-hdpi/ic_moment_search.png    36x36（24dp @1.5x）

设计：白色描边放大镜、透明底；圆环偏左上，手柄沿 45° 伸向右下。
以 8 倍超采样绘制后再用 LANCZOS 降采样，得到平滑边缘（PIL 的 line 无圆头，
两端各补一个实心圆当圆头）。
"""

import math
import os
import sys

from PIL import Image, ImageDraw

# 设计尺寸（dp 单位下的图标画布，按 24dp 设计，48px @2x）
DESIGN_SIZE = 24
SUPERSAMPLE = 8
RING_CENTER = (10.0, 10.0)
RING_RADIUS = 5.75
STROKE = 2.0
HANDLE_END = (19.25, 19.25)

# 输出：(相对仓库根的路径, 像素边长)
OUTPUTS = [
    (os.path.join("app", "src", "main", "res", "drawable-xhdpi", "ic_moment_search.png"), 48),
    (os.path.join("app", "src", "main", "res", "drawable-hdpi", "ic_moment_search.png"), 36),
]

WHITE = (255, 255, 255, 255)
TRANSPARENT = (0, 0, 0, 0)


def render(canvas_size):
    """按 8 倍超采样绘制放大镜，再降采样到 canvas_size。"""
    scale = SUPERSAMPLE * canvas_size / float(DESIGN_SIZE)
    canvas = int(round(canvas_size * SUPERSAMPLE))
    image = Image.new("RGBA", (canvas, canvas), TRANSPARENT)
    draw = ImageDraw.Draw(image)

    cx, cy = RING_CENTER[0] * scale, RING_CENTER[1] * scale
    radius = RING_RADIUS * scale
    stroke = STROKE * scale

    # 圆环
    draw.ellipse([cx - radius, cy - radius, cx + radius, cy + radius],
                 outline=WHITE, width=int(round(stroke)))

    # 手柄：从圆环 45° 方向的环边延伸到右下方
    angle = math.radians(45.0)
    start_x = cx + (radius - stroke / 2.0) * math.cos(angle)
    start_y = cy + (radius - stroke / 2.0) * math.sin(angle)
    end_x, end_y = HANDLE_END[0] * scale, HANDLE_END[1] * scale
    draw.line([start_x, start_y, end_x, end_y], fill=WHITE, width=int(round(stroke)))

    # 两端圆头
    half = stroke / 2.0
    for point_x, point_y in ((start_x, start_y), (end_x, end_y)):
        draw.ellipse([point_x - half, point_y - half, point_x + half, point_y + half], fill=WHITE)

    return image.resize((canvas_size, canvas_size), Image.LANCZOS)


def check(icon):
    """自检：RGBA、四角全透明、笔迹占比合理。"""
    if icon.mode != "RGBA":
        raise AssertionError("icon mode should be RGBA, got %s" % icon.mode)
    width, height = icon.size
    pixels = icon.load()
    corners = [pixels[x, y][3] for x, y in ((0, 0), (width - 1, 0), (0, height - 1), (width - 1, height - 1))]
    if any(alpha != 0 for alpha in corners):
        raise AssertionError("corners must be transparent, got %s" % corners)
    ink = sum(1 for y in range(height) for x in range(width) if pixels[x, y][3] > 32)
    ratio = ink / float(width * height)
    if not 0.08 < ratio < 0.30:
        raise AssertionError("ink ratio out of range: %.3f" % ratio)
    return corners, ratio


def main():
    repo_root = os.path.abspath(os.path.join(os.path.dirname(os.path.abspath(__file__)), "..", ".."))
    for relative_path, size in OUTPUTS:
        icon = render(size)
        corners, ratio = check(icon)
        target = os.path.join(repo_root, relative_path)
        os.makedirs(os.path.dirname(target), exist_ok=True)
        icon.save(target, "PNG")
        print("%s  %dx%d  ink=%.1f%%  corners=%s" % (relative_path, size, size, ratio * 100, corners))
    return 0


if __name__ == "__main__":
    sys.exit(main())
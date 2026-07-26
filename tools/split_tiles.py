"""
Split a horizontal tileset strip into N separate tile PNGs.

terrain-v3.png packs 7 tiles in a row with no gaps, so this cuts it into equal
columns and writes each tile as its own file. The game then just loads finished
tile images.

Run from the project root:  python3 tools/split_tiles.py
"""

from PIL import Image

SRC = "src/images/terrain-v3.png"
# Output name per tile index (left to right)
NAMES = [
    "terrain_cave_left",    # 1
    "terrain_stone",        # 2
    "terrain_cave_upper",   # 3
    "terrain_cave_lower",   # 4
    "terrain_cave_middle",  # 5
    "terrain_cave_right",   # 6
    "terrain_cave_mid",     # 7
]


# Trim this many pixels off every tile edge to drop the template's grid border
INSET = 4


def main():
    im = Image.open(SRC).convert("RGBA")
    w, h = im.size
    n = len(NAMES)
    for i, name in enumerate(NAMES):
        x0 = round(i * w / n)
        x1 = round((i + 1) * w / n)
        tile = im.crop((x0 + INSET, INSET, x1 - INSET, h - INSET))
        out = f"src/images/{name}.png"
        tile.save(out)
        print(f"{out} ({tile.width}x{tile.height})")


if __name__ == "__main__":
    main()

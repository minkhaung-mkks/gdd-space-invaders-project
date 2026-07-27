#!/usr/bin/env python3
"""Builds animated GIFs of each character for the README, plus pickup icons.

Frames come straight off the sprite sheets, so what the README shows is what
the game shows. Every frame of one animation is cut with the same box, which
keeps the character from jumping around inside the GIF. Frames are blown up
with nearest neighbour so the pixels stay sharp.

Results go in docs/.

Run from the project root:  python3 tools/make_readme_images.py
"""

from PIL import Image
import os

OUT = "docs"
# Characters are drawn to fit inside this box. A box rather than a fixed
# height, because the ship is long and flat while the boss is nearly square.
MAX_W = 180
MAX_H = 120
ICON_H = 72  # how tall each pickup icon is

# name, sheet, cols, rows, row, columns, game frames per picture, mirrored
# Mirrored matches what the game does: the enemy sheets are flipped so they
# face left towards the player, the player sheet is used as drawn.
CHARACTERS = [
    ("player", "src/images/player_sheet.png", 6, 4, 0, [0, 1], 6, False),
    ("alien1", "src/images/alien1_packed.png", 4, 3, 1, [0, 1, 2, 3], 6, True),
    ("alien2", "src/images/alien_2.png", 2, 2, 0, [0, 1], 8, True),
    ("mage", "src/images/mage.png", 6, 4, 0, [0, 1, 2, 3], 6, True),
    ("boss", "src/images/boss_packed.png", 4, 4, 3, [0, 1, 2, 3], 14, True),
]

PICKUPS = ["powerup_speed", "powerup_multi", "powerup_split",
           "powerup_big", "powerup_heal"]


def blow_up(img, height):
    width = max(1, round(img.width * height / img.height))
    return img.resize((width, height), Image.NEAREST)


def fit(img, max_w, max_h):
    scale = min(max_w / img.width, max_h / img.height)
    size = (max(1, round(img.width * scale)), max(1, round(img.height * scale)))
    return img.resize(size, Image.NEAREST)


def to_palette(img):
    """RGBA -> palette image with a see-through background, ready for GIF."""
    see_through = img.getchannel("A").point(lambda a: 255 if a <= 128 else 0)
    out = img.convert("RGB").convert("P", palette=Image.ADAPTIVE, colors=255)
    out.paste(255, see_through)          # index 255 is the see-through one
    out.info["transparency"] = 255
    return out


def character_frames(path, cols, rows, row, columns, mirrored):
    sheet = Image.open(path).convert("RGBA")
    fw = sheet.width / cols
    fh = sheet.height / rows
    y0 = round(row * fh)
    ch = int(fh)

    # One box for the whole animation, not one per frame
    boxes = []
    for col in columns:
        x0 = round(col * fw)
        cell = sheet.crop((x0, y0, x0 + int(fw), y0 + ch))
        box = cell.getbbox()
        if box is None:
            raise SystemExit("%s: cell r%dc%d is empty" % (path, row, col))
        boxes.append(box)

    bx0 = min(b[0] for b in boxes)
    by0 = min(b[1] for b in boxes)
    bx1 = max(b[2] for b in boxes)
    by1 = max(b[3] for b in boxes)

    frames = []
    for col in columns:
        x0 = round(col * fw)
        frame = sheet.crop((x0 + bx0, y0 + by0, x0 + bx1, y0 + by1))
        if mirrored:
            frame = frame.transpose(Image.FLIP_LEFT_RIGHT)
        frames.append(fit(frame, MAX_W, MAX_H))
    return frames


def main():
    os.makedirs(OUT, exist_ok=True)

    for name, path, cols, rows, row, columns, hold, mirrored in CHARACTERS:
        frames = character_frames(path, cols, rows, row, columns, mirrored)
        shown = [to_palette(f) for f in frames]

        ms = round(hold * 1000 / 60)  # the game runs at 60 frames a second
        out = os.path.join(OUT, name + ".gif")
        shown[0].save(out, save_all=True, append_images=shown[1:],
                      duration=ms, loop=0, disposal=2, transparency=255)
        print("%-20s %3d x %3d  %d frames  %dms each"
              % (out, frames[0].width, frames[0].height, len(frames), ms))

    for name in PICKUPS:
        icon = blow_up(Image.open("src/images/%s.png" % name).convert("RGBA"), ICON_H)
        out = os.path.join(OUT, name + ".png")
        icon.save(out)
        print("%-20s %3d x %3d" % (out, icon.width, icon.height))


if __name__ == "__main__":
    main()

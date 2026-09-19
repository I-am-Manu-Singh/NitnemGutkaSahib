from PIL import Image, ImageDraw, ImageFont
import os

# Create 512x512 adaptive foreground and legacy icons
size = 512
img = Image.new('RGBA', (size, size), (0, 0, 0, 0))
draw = ImageDraw.Draw(img)

# Try loading system font for Gurmukhi or fallback
font_paths = [
    "/System/Library/Fonts/Supplemental/Gurmukhi MN.ttc",
    "/System/Library/Fonts/Supplemental/Gurmukhi Sangam MN.ttc",
    "/Library/Fonts/Gurmukhi.ttf",
    "/System/Library/Fonts/Helvetica.ttc"
]

font = None
for p in font_paths:
    if os.path.exists(p):
        try:
            font = ImageFont.truetype(p, 230)
            break
        except Exception as e:
            continue

print("Selected font:", font)

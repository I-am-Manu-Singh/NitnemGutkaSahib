import os
from PIL import Image, ImageDraw, ImageFont, ImageFilter

res_dir = "composeApp/src/androidMain/res"

font_path = "/System/Library/Fonts/Supplemental/Gurmukhi MN.ttc"
if not os.path.exists(font_path):
    font_path = "/System/Library/Fonts/Supplemental/Gurmukhi Sangam MN.ttc"

def create_adaptive_foreground(size=432):
    # Standard adaptive icon viewport: 108dp * 4 = 432px
    # Safe zone: inner 66dp = ~264px centered (from 84 to 348)
    img = Image.new('RGBA', (size, size), (0, 0, 0, 0))
    draw = ImageDraw.Draw(img)
    
    # Outer gentle gold aura in safe zone
    center = size / 2
    r_outer = 130
    draw.ellipse([center - r_outer, center - r_outer, center + r_outer, center + r_outer], fill=(255, 193, 7, 30), outline=(255, 213, 79, 70), width=3)
    
    # Draw Ek Onkar
    font = ImageFont.truetype(font_path, 160)
    text = "ੴ"
    
    # Measure text bounding box
    bbox = font.getbbox(text)
    w = bbox[2] - bbox[0]
    h = bbox[3] - bbox[1]
    
    # Center text perfectly
    x = center - (bbox[0] + bbox[2]) / 2
    y = center - (bbox[1] + bbox[3]) / 2 - 4 # optical adjustment
    
    # Subtle soft glow behind glyph
    glow = Image.new('RGBA', (size, size), (0, 0, 0, 0))
    glow_draw = ImageDraw.Draw(glow)
    glow_draw.text((x, y), text, font=font, fill=(255, 224, 130, 180))
    glow = glow.filter(ImageFilter.GaussianBlur(8))
    img.alpha_composite(glow)
    
    # Sharp gold text
    draw.text((x, y), text, font=font, fill=(255, 193, 7, 255))
    
    return img

def create_full_icon(size, is_round=False):
    img = Image.new('RGBA', (size, size), (0, 0, 0, 0))
    draw = ImageDraw.Draw(img)
    
    # Background: Rich spiritual deep dark gradient
    bg = Image.new('RGBA', (size, size), (0, 0, 0, 0))
    bg_draw = ImageDraw.Draw(bg)
    
    if is_round:
        # Circular mask
        mask = Image.new('L', (size, size), 0)
        mask_draw = ImageDraw.Draw(mask)
        mask_draw.ellipse([0, 0, size, size], fill=255)
        
        # Gradient
        for y in range(size):
            ratio = y / float(size)
            r = int(37 * (1 - ratio) + 15 * ratio)
            g = int(31 * (1 - ratio) + 14 * ratio)
            b = int(26 * (1 - ratio) + 13 * ratio)
            bg_draw.line([(0, y), (size, y)], fill=(r, g, b, 255))
        
        # Border
        bg_draw.ellipse([1, 1, size - 2, size - 2], outline=(255, 193, 7, 100), width=max(2, size // 48))
        img.paste(bg, (0, 0), mask)
    else:
        # Rounded rectangle
        corner = max(8, int(size * 0.22))
        for y in range(size):
            ratio = y / float(size)
            r = int(37 * (1 - ratio) + 15 * ratio)
            g = int(31 * (1 - ratio) + 14 * ratio)
            b = int(26 * (1 - ratio) + 13 * ratio)
            bg_draw.line([(0, y), (size, y)], fill=(r, g, b, 255))
            
        mask = Image.new('L', (size, size), 0)
        mask_draw = ImageDraw.Draw(mask)
        mask_draw.rounded_rectangle([0, 0, size, size], radius=corner, fill=255)
        img.paste(bg, (0, 0), mask)
        
        draw.rounded_rectangle([1, 1, size - 2, size - 2], radius=corner, outline=(255, 193, 7, 100), width=max(2, size // 48))
    
    # Center aura
    center = size / 2
    r_outer = size * 0.38
    draw.ellipse([center - r_outer, center - r_outer, center + r_outer, center + r_outer], fill=(255, 193, 7, 25), outline=(255, 213, 79, 60), width=max(1, size // 96))
    
    # Draw Ek Onkar
    font_size = int(size * 0.48)
    font = ImageFont.truetype(font_path, font_size)
    text = "ੴ"
    
    bbox = font.getbbox(text)
    x = center - (bbox[0] + bbox[2]) / 2
    y = center - (bbox[1] + bbox[3]) / 2 - int(size * 0.015)
    
    # Soft glow
    glow = Image.new('RGBA', (size, size), (0, 0, 0, 0))
    glow_draw = ImageDraw.Draw(glow)
    glow_draw.text((x, y), text, font=font, fill=(255, 224, 130, 180))
    glow = glow.filter(ImageFilter.GaussianBlur(max(2, size // 32)))
    img.alpha_composite(glow)
    
    # Sharp gold text
    draw.text((x, y), text, font=font, fill=(255, 193, 7, 255))
    
    return img

# Save adaptive icon foreground
fg_img = create_adaptive_foreground(432)
os.makedirs(f"{res_dir}/drawable-nodpi", exist_ok=True)
fg_img.save(f"{res_dir}/drawable-nodpi/ic_launcher_foreground_bitmap.png")

# Densities for mipmap
densities = {
    "mipmap-mdpi": 48,
    "mipmap-hdpi": 72,
    "mipmap-xhdpi": 96,
    "mipmap-xxhdpi": 144,
    "mipmap-xxxhdpi": 192,
}

for folder, s in densities.items():
    d_path = f"{res_dir}/{folder}"
    os.makedirs(d_path, exist_ok=True)
    icon_square = create_full_icon(s, is_round=False)
    icon_round = create_full_icon(s, is_round=True)
    icon_square.save(f"{d_path}/ic_launcher.png")
    icon_round.save(f"{d_path}/ic_launcher_round.png")

# Also play store icon (512x512)
playstore_icon = create_full_icon(512, is_round=False)
playstore_icon.save(f"{res_dir}/drawable-nodpi/ic_launcher_playstore.png")

print("Generated all mipmap and adaptive launcher icons successfully!")

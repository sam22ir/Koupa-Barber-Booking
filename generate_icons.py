#!/usr/bin/env python3
"""Generate Android launcher icons from source image."""

from PIL import Image, ImageDraw
import os

# Source image path
SOURCE_IMAGE = r"D:\koupa\photo_2026-03-21_01-21-19.jpg"

# Android mipmap densities and sizes
DENSITIES = {
    "mdpi": 48,
    "hdpi": 72,
    "xhdpi": 96,
    "xxhdpi": 144,
    "xxxhdpi": 192,
}

# Base path for Android resources
BASE_PATH = r"D:\koupa\android\app\src\main\res"


def create_circular_mask(size):
    """Create a circular mask for round icons."""
    mask = Image.new("L", (size, size), 0)
    draw = ImageDraw.Draw(mask)
    draw.ellipse((0, 0, size, size), fill=255)
    return mask


def create_round_icon(image, size):
    """Create a round version of the icon."""
    # Resize image
    resized = image.resize((size, size), Image.Resampling.LANCZOS)
    
    # Create circular mask
    mask = create_circular_mask(size)
    
    # Create output image with transparency
    output = Image.new("RGBA", (size, size), (0, 0, 0, 0))
    
    # Paste resized image using mask
    output.paste(resized, (0, 0))
    output.putalpha(mask)
    
    return output


def main():
    """Main function to generate all icons."""
    print(f"Opening source image: {SOURCE_IMAGE}")
    
    # Open and convert source image to RGBA
    source = Image.open(SOURCE_IMAGE)
    if source.mode != "RGBA":
        source = source.convert("RGBA")
    
    # Make the image square by cropping to center
    width, height = source.size
    if width != height:
        # Crop to square from center
        size = min(width, height)
        left = (width - size) // 2
        top = (height - size) // 2
        right = left + size
        bottom = top + size
        source = source.crop((left, top, right, bottom))
        print(f"Cropped image to {size}x{size} from center")
    
    total_icons = 0
    
    for density, size in DENSITIES.items():
        # Create directory if it doesn't exist
        mipmap_dir = os.path.join(BASE_PATH, f"mipmap-{density}")
        os.makedirs(mipmap_dir, exist_ok=True)
        
        # Create standard icon
        standard_path = os.path.join(mipmap_dir, "ic_launcher.png")
        standard_icon = source.resize((size, size), Image.Resampling.LANCZOS)
        standard_icon.save(standard_path, "PNG")
        print(f"Created: {standard_path} ({size}x{size})")
        total_icons += 1
        
        # Create round icon
        round_path = os.path.join(mipmap_dir, "ic_launcher_round.png")
        round_icon = create_round_icon(source, size)
        round_icon.save(round_path, "PNG")
        print(f"Created: {round_path} ({size}x{size})")
        total_icons += 1
    
    print(f"\nSuccess! Created {total_icons} icons total.")
    return 0


if __name__ == "__main__":
    exit(main())

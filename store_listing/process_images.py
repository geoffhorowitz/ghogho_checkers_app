import sys
import os
try:
    from PIL import Image, ImageOps
except ImportError:
    import subprocess
    subprocess.check_call([sys.executable, "-m", "pip", "install", "Pillow"])
    from PIL import Image, ImageOps

def process_image(src, dst, target_size, aspect_ratio=None):
    if not os.path.exists(src):
        print(f"File not found: {src}")
        return
    
    with Image.open(src) as img:
        img = img.convert("RGBA")
        if target_size:
            # Resize and crop to exactly target_size
            img = ImageOps.fit(img, target_size, method=Image.Resampling.LANCZOS)
        elif aspect_ratio:
            # Just ensure aspect ratio, crop if necessary (e.g. 1080x1920)
            target_w, target_h = aspect_ratio
            w, h = img.size
            ratio_w = target_w / target_h
            if w / h > ratio_w:
                new_w = int(h * ratio_w)
                new_h = h
            else:
                new_w = w
                new_h = int(w / ratio_w)
            
            left = (w - new_w) / 2
            top = (h - new_h) / 2
            right = (w + new_w) / 2
            bottom = (h + new_h) / 2
            img = img.crop((left, top, right, bottom))
            # resize to target resolution
            img = img.resize(aspect_ratio, Image.Resampling.LANCZOS)
            
        # Convert back to RGB for JPEG, or just save as PNG
        if dst.lower().endswith('.png'):
            img.save(dst, "PNG")
        else:
            img = img.convert("RGB")
            img.save(dst, "JPEG")
        print(f"Saved {dst}")

def main():
    brain_dir = r"C:\Users\geoff\.gemini\antigravity\brain\affe6d82-dd2f-4034-8814-ff2f283bbf11"
    out_dir = r"C:\Users\geoff\github_repos\Android Apps\CheckersApp\store_listing"
    os.makedirs(out_dir, exist_ok=True)
    
    # We will search the brain_dir for the raw images
    app_icon_raw = [f for f in os.listdir(brain_dir) if f.startswith("app_icon_raw") and f.endswith(".png")]
    feature_graphic_raw = [f for f in os.listdir(brain_dir) if f.startswith("feature_graphic_raw") and f.endswith(".png")]
    scr_1_raw = [f for f in os.listdir(brain_dir) if f.startswith("scr_1_raw") and f.endswith(".png")]
    scr_2_raw = [f for f in os.listdir(brain_dir) if f.startswith("scr_2_raw") and f.endswith(".png")]

    if app_icon_raw:
        process_image(os.path.join(brain_dir, app_icon_raw[-1]), os.path.join(out_dir, "app_icon.png"), (512, 512))
    if feature_graphic_raw:
        process_image(os.path.join(brain_dir, feature_graphic_raw[-1]), os.path.join(out_dir, "feature_graphic.png"), (1024, 500))
    
    # For screenshots, target 1080x1920 (9:16 ratio)
    if scr_1_raw:
        process_image(os.path.join(brain_dir, scr_1_raw[-1]), os.path.join(out_dir, "phone_screenshot_1.png"), None, (1080, 1920))
        process_image(os.path.join(brain_dir, scr_1_raw[-1]), os.path.join(out_dir, "tablet_7inch_screenshot_1.png"), None, (1080, 1920))
        process_image(os.path.join(brain_dir, scr_1_raw[-1]), os.path.join(out_dir, "tablet_10inch_screenshot_1.png"), None, (1080, 1920))
    if scr_2_raw:
        process_image(os.path.join(brain_dir, scr_2_raw[-1]), os.path.join(out_dir, "phone_screenshot_2.png"), None, (1080, 1920))
        process_image(os.path.join(brain_dir, scr_2_raw[-1]), os.path.join(out_dir, "tablet_7inch_screenshot_2.png"), None, (1080, 1920))
        process_image(os.path.join(brain_dir, scr_2_raw[-1]), os.path.join(out_dir, "tablet_10inch_screenshot_2.png"), None, (1080, 1920))

if __name__ == "__main__":
    main()

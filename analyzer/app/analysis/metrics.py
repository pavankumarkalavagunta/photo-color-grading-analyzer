"""Core image analysis metrics using OpenCV and NumPy."""
import cv2
import numpy as np

def calculate_luminance(rgb_image: np.ndarray) -> np.ndarray:
    """
    Calculate per-pixel luminance using ITU-R BT.709 formula:
    Y = 0.2126*R + 0.7152*G + 0.0722*B
    
    Args:
        rgb_image: RGB image array (H, W, 3), uint8 [0-255]
    Returns:
        Luminance array (H, W), float64 [0-255]
    """
    r = rgb_image[:, :, 0].astype(np.float64)
    g = rgb_image[:, :, 1].astype(np.float64)
    b = rgb_image[:, :, 2].astype(np.float64)
    return 0.2126 * r + 0.7152 * g + 0.0722 * b


def calculate_brightness(rgb_image: np.ndarray) -> dict:
    """
    Calculate mean brightness (luminance).
    
    Returns:
        dict with mean_brightness (0-255), normalized (0-100)
    """
    luminance = calculate_luminance(rgb_image)
    mean_val = float(np.mean(luminance))
    return {
        "mean_brightness": round(mean_val, 2),
        "normalized": round((mean_val / 255) * 100, 2),
        "label": _brightness_label(mean_val),
    }


def _brightness_label(mean_brightness: float) -> str:
    if mean_brightness < 60:
        return "Dark"
    elif mean_brightness < 85:
        return "Slightly Dark"
    elif mean_brightness < 170:
        return "Normal"
    elif mean_brightness < 200:
        return "Slightly Bright"
    else:
        return "Bright"


def calculate_contrast(rgb_image: np.ndarray) -> dict:
    """
    Calculate contrast as the standard deviation of luminance.
    Higher std-dev means more contrast.
    
    Returns:
        dict with std_deviation, normalized (0-100), label
    """
    luminance = calculate_luminance(rgb_image)
    std_val = float(np.std(luminance))
    # Normalize: typical max std-dev for 8-bit is ~127.5
    normalized = min(round((std_val / 127.5) * 100, 2), 100)
    return {
        "std_deviation": round(std_val, 2),
        "normalized": normalized,
        "label": _contrast_label(std_val),
    }


def _contrast_label(std_dev: float) -> str:
    if std_dev < 30:
        return "Low Contrast"
    elif std_dev < 50:
        return "Slightly Low"
    elif std_dev < 75:
        return "Normal"
    elif std_dev < 95:
        return "Slightly High"
    else:
        return "High Contrast"


def calculate_saturation(bgr_image: np.ndarray) -> dict:
    """
    Calculate saturation statistics from HSV color space.
    
    Returns:
        dict with mean_saturation (0-255), normalized (0-100), label, distribution
    """
    hsv = cv2.cvtColor(bgr_image, cv2.COLOR_BGR2HSV)
    saturation_channel = hsv[:, :, 1].astype(np.float64)
    
    mean_sat = float(np.mean(saturation_channel))
    std_sat = float(np.std(saturation_channel))
    
    # Distribution buckets
    total_pixels = saturation_channel.size
    low = float(np.sum(saturation_channel < 64)) / total_pixels * 100
    mid = float(np.sum((saturation_channel >= 64) & (saturation_channel < 192))) / total_pixels * 100
    high = float(np.sum(saturation_channel >= 192)) / total_pixels * 100
    
    return {
        "mean_saturation": round(mean_sat, 2),
        "normalized": round((mean_sat / 255) * 100, 2),
        "std_deviation": round(std_sat, 2),
        "label": _saturation_label(mean_sat),
        "distribution": {
            "low_pct": round(low, 2),
            "mid_pct": round(mid, 2),
            "high_pct": round(high, 2),
        },
    }


def _saturation_label(mean_sat: float) -> str:
    if mean_sat < 40:
        return "Muted / Desaturated"
    elif mean_sat < 80:
        return "Slightly Muted"
    elif mean_sat < 150:
        return "Normal"
    elif mean_sat < 200:
        return "Vibrant"
    else:
        return "Highly Saturated"


def calculate_rgb_histogram(rgb_image: np.ndarray) -> dict:
    """
    Calculate RGB histograms (256 bins each channel).
    
    Returns:
        dict with red, green, blue arrays (each 256 values)
    """
    red_hist = cv2.calcHist([rgb_image], [0], None, [256], [0, 256]).flatten().tolist()
    green_hist = cv2.calcHist([rgb_image], [1], None, [256], [0, 256]).flatten().tolist()
    blue_hist = cv2.calcHist([rgb_image], [2], None, [256], [0, 256]).flatten().tolist()
    
    return {
        "red": [int(v) for v in red_hist],
        "green": [int(v) for v in green_hist],
        "blue": [int(v) for v in blue_hist],
    }


def calculate_luminance_histogram(rgb_image: np.ndarray) -> dict:
    """
    Calculate luminance histogram (256 bins).
    
    Returns:
        dict with luminance array (256 values)
    """
    luminance = calculate_luminance(rgb_image).astype(np.uint8)
    lum_hist = cv2.calcHist([luminance], [0], None, [256], [0, 256]).flatten().tolist()
    return {
        "luminance": [int(v) for v in lum_hist],
    }


def calculate_shadows_highlights(rgb_image: np.ndarray) -> dict:
    """
    Calculate shadow and highlight pixel percentages.
    
    Thresholds (luminance 0-255):
        - Deep shadows: < 30
        - Shadows: < 60
        - Midtones: 60-195
        - Highlights: > 195
        - Bright highlights: > 225
    
    Clipping:
        - Black clipping: pixels at 0-5
        - White clipping: pixels at 250-255
    """
    luminance = calculate_luminance(rgb_image)
    total = luminance.size
    
    deep_shadows = float(np.sum(luminance < 30)) / total * 100
    shadows = float(np.sum(luminance < 60)) / total * 100
    midtones = float(np.sum((luminance >= 60) & (luminance <= 195))) / total * 100
    highlights = float(np.sum(luminance > 195)) / total * 100
    bright_highlights = float(np.sum(luminance > 225)) / total * 100
    
    black_clipping = float(np.sum(luminance <= 5)) / total * 100
    white_clipping = float(np.sum(luminance >= 250)) / total * 100
    
    return {
        "deep_shadows_pct": round(deep_shadows, 2),
        "shadows_pct": round(shadows, 2),
        "midtones_pct": round(midtones, 2),
        "highlights_pct": round(highlights, 2),
        "bright_highlights_pct": round(bright_highlights, 2),
        "black_clipping_pct": round(black_clipping, 2),
        "white_clipping_pct": round(white_clipping, 2),
    }


def calculate_dominant_colors(rgb_image: np.ndarray, n_colors: int = 5) -> list:
    """
    Extract dominant colors using K-Means clustering with OpenCV.
    
    Args:
        rgb_image: RGB image (H, W, 3)
        n_colors: number of dominant colors to extract
    
    Returns:
        list of dicts with hex, rgb, percentage, sorted by percentage descending
    """
    pixels = rgb_image.reshape(-1, 3).astype(np.float32)
    
    # Safe guard for tiny images or single-color arrays
    if len(pixels) <= n_colors:
        colors = []
        unique_pixels, counts = np.unique(pixels.astype(int), axis=0, return_counts=True)
        total = len(pixels)
        for i in range(len(unique_pixels)):
            r = int(np.clip(unique_pixels[i][0], 0, 255))
            g = int(np.clip(unique_pixels[i][1], 0, 255))
            b = int(np.clip(unique_pixels[i][2], 0, 255))
            pct = round((counts[i] / total) * 100.0, 2)
            colors.append({
                "hex": f"#{r:02x}{g:02x}{b:02x}",
                "rgb": {"r": r, "g": g, "b": b},
                "percentage": pct,
            })
        colors.sort(key=lambda x: x["percentage"], reverse=True)
        return colors
    
    # Sample up to 25000 pixels for fast analysis
    if len(pixels) > 25000:
        indices = np.random.choice(len(pixels), 25000, replace=False)
        pixels = pixels[indices]
    
    criteria = (cv2.TERM_CRITERIA_EPS + cv2.TERM_CRITERIA_MAX_ITER, 10, 1.0)
    flags = cv2.KMEANS_PP_CENTERS
    try:
        _, labels, centers = cv2.kmeans(pixels, n_colors, None, criteria, 5, flags)
    except Exception:
        _, labels, centers = cv2.kmeans(pixels, n_colors, None, criteria, 5, cv2.KMEANS_RANDOM_CENTERS)
    
    labels = labels.flatten()
    total = len(labels)
    
    colors = []
    for i in range(len(centers)):
        center = np.clip(centers[i], 0, 255).astype(int)
        r, g, b = int(center[0]), int(center[1]), int(center[2])
        count = int(np.sum(labels == i))
        percentage = round((count / total) * 100, 2)
        hex_code = f"#{r:02x}{g:02x}{b:02x}"
        colors.append({
            "hex": hex_code,
            "rgb": {"r": r, "g": g, "b": b},
            "percentage": percentage,
        })
    
    colors.sort(key=lambda x: x["percentage"], reverse=True)
    return colors


def estimate_color_temperature(rgb_image: np.ndarray) -> dict:
    """
    Estimate approximate color temperature (warm/cool) based on 
    average red vs blue channel balance.
    
    This is a simplified heuristic, not a calibrated measurement.
    
    Warm images tend to have higher red relative to blue.
    Cool images tend to have higher blue relative to red.
    """
    mean_r = float(np.mean(rgb_image[:, :, 0]))
    mean_g = float(np.mean(rgb_image[:, :, 1]))
    mean_b = float(np.mean(rgb_image[:, :, 2]))
    
    # Ratio approach
    warmth = mean_r - mean_b  # positive = warm, negative = cool
    
    if warmth > 25:
        label = "Warm"
        description = "The image has a noticeable warm cast with more red/orange tones."
    elif warmth > 10:
        label = "Slightly Warm"
        description = "The image leans slightly warm with gentle red/yellow tones."
    elif warmth > -10:
        label = "Neutral"
        description = "The image has a relatively neutral color temperature."
    elif warmth > -25:
        label = "Slightly Cool"
        description = "The image leans slightly cool with subtle blue tones."
    else:
        label = "Cool"
        description = "The image has a noticeable cool cast with more blue tones."
    
    return {
        "warmth_index": round(warmth, 2),
        "label": label,
        "description": description,
        "mean_rgb": {
            "r": round(mean_r, 2),
            "g": round(mean_g, 2),
            "b": round(mean_b, 2),
        },
    }


def calculate_image_quality(bgr_image: np.ndarray) -> dict:
    """
    Evaluate technical image quality: Sharpness, Noise/Grain level, and Detail Clarity.
    
    Methods:
    - Sharpness: Variance of the Laplacian of the grayscale image.
    - Noise Level: Standard deviation of residual between image and Gaussian blur.
    - Detail Clarity: Normalized score based on high-frequency edge response.
    """
    h, w = bgr_image.shape[:2]
    if h < 5 or w < 5:
        return {
            "sharpness_variance": 0.0,
            "sharpness_score": 50.0,
            "sharpness_label": "N/A (Tiny Image)",
            "noise_sigma": 0.0,
            "noise_score": 0.0,
            "noise_label": "Clean / N/A",
            "clarity_score": 50.0,
            "clarity_label": "Standard",
        }

    gray = cv2.cvtColor(bgr_image, cv2.COLOR_BGR2GRAY)
    
    # 1. Sharpness via Laplacian variance
    laplacian = cv2.Laplacian(gray, cv2.CV_64F)
    sharpness_var = float(laplacian.var())
    
    if sharpness_var < 80:
        sharpness_label = "Soft / Blurry"
    elif sharpness_var < 250:
        sharpness_label = "Moderate Focus"
    elif sharpness_var < 800:
        sharpness_label = "Sharp & Crisp"
    else:
        sharpness_label = "Ultra Sharp / High Detail"
        
    sharpness_score = min(100.0, round((sharpness_var / 500.0) * 100.0, 1))

    # 2. Noise / Grain Estimation via Gaussian residual
    blurred = cv2.GaussianBlur(gray, (5, 5), 0)
    residual = np.abs(gray.astype(np.float64) - blurred.astype(np.float64))
    noise_sigma = float(np.std(residual))
    
    if noise_sigma < 2.5:
        noise_label = "Clean / Minimal Grain"
    elif noise_sigma < 5.0:
        noise_label = "Subtle / Fine Film Grain"
    elif noise_sigma < 8.5:
        noise_label = "Moderate Noise"
    else:
        noise_label = "Noticeable Noise / Heavy Grain"
        
    noise_score = min(100.0, round((noise_sigma / 10.0) * 100.0, 1))

    # 3. Overall Clarity Score (0 - 100)
    clarity_score = round(max(10.0, min(100.0, (sharpness_score * 0.75) - (noise_score * 0.25) + 25.0)), 1)
    if clarity_score >= 80:
        clarity_label = "Excellent Clarity"
    elif clarity_score >= 60:
        clarity_label = "Good Quality"
    elif clarity_score >= 40:
        clarity_label = "Fair / Acceptable"
    else:
        clarity_label = "Low Clarity"

    return {
        "sharpness_variance": round(sharpness_var, 1),
        "sharpness_score": sharpness_score,
        "sharpness_label": sharpness_label,
        "noise_sigma": round(noise_sigma, 2),
        "noise_score": noise_score,
        "noise_label": noise_label,
        "clarity_score": clarity_score,
        "clarity_label": clarity_label,
    }


def calculate_lighting_analysis(rgb_image: np.ndarray) -> dict:
    """
    Comprehensive lighting and exposure analysis:
    - Key Style (High-Key vs Low-Key vs Balanced)
    - Dynamic Range (Tonal Span and approximate EV stops)
    - Vignette / Corner Falloff
    """
    luminance = calculate_luminance(rgb_image)
    h, w = luminance.shape
    mean_lum = float(np.mean(luminance))
    
    if h < 5 or w < 5:
        return {
            "key_style": "Balanced Key",
            "key_description": "Evenly distributed lighting across image.",
            "tonal_span": 0.0,
            "ev_stops": 0.0,
            "dynamic_range_label": "Standard Dynamic Range",
            "center_luminance": round(mean_lum, 1),
            "corner_luminance": round(mean_lum, 1),
            "vignette_falloff_pct": 0.0,
            "vignette_label": "Uniform Illumination",
        }
    
    # Dynamic Range / Tonal Span (1st to 99th percentile)
    p1 = float(np.percentile(luminance, 1))
    p99 = float(np.percentile(luminance, 99))
    tonal_span = round(p99 - p1, 1)
    ev_stops = round(np.log2(max(1.0, tonal_span / 2.0)), 1)
    
    if tonal_span > 200:
        dr_label = "Wide Dynamic Range"
    elif tonal_span > 130:
        dr_label = "Standard Dynamic Range"
    else:
        dr_label = "Compressed / Low Dynamic Range"

    # Key Style Identification
    deep_shadow_ratio = float(np.sum(luminance < 40)) / luminance.size
    bright_highlight_ratio = float(np.sum(luminance > 210)) / luminance.size
    
    if mean_lum > 170 and bright_highlight_ratio > 0.25 and deep_shadow_ratio < 0.08:
        key_style = "High-Key"
        key_description = "Bright, airy aesthetic with soft shadows and luminous tones."
    elif mean_lum < 85 and deep_shadow_ratio > 0.30:
        key_style = "Low-Key"
        key_description = "Moody, dramatic, chiaroscuro aesthetic with prominent deep shadows."
    elif mean_lum < 110 and tonal_span < 120:
        key_style = "Muted / Low Contrast"
        key_description = "Flat, filmic look with subdued lighting across midtones."
    else:
        key_style = "Balanced Key"
        key_description = "Evenly distributed lighting across shadows, midtones, and highlights."

    # Vignetting / Falloff analysis
    cy1, cy2 = int(h * 0.35), int(h * 0.65)
    cx1, cx2 = int(w * 0.35), int(w * 0.65)
    center_lum = float(np.mean(luminance[cy1:cy2, cx1:cx2])) if cy2 > cy1 and cx2 > cx1 else mean_lum
    
    cw, ch = max(1, int(w * 0.15)), max(1, int(h * 0.15))
    corner_samples = [
        luminance[:ch, :cw],
        luminance[:ch, -cw:],
        luminance[-ch:, :cw],
        luminance[-ch:, -cw:]
    ]
    corners_lum = float(np.mean([np.mean(c) for c in corner_samples]))
    
    falloff_pct = round(((corners_lum - center_lum) / max(center_lum, 1.0)) * 100.0, 1)
    
    if falloff_pct < -18:
        vignette_label = f"Heavy Vignette (-{abs(falloff_pct)}%)"
    elif falloff_pct < -8:
        vignette_label = f"Subtle Vignette (-{abs(falloff_pct)}%)"
    elif falloff_pct > 15:
        vignette_label = f"Center Falloff (+{falloff_pct}%)"
    else:
        vignette_label = "Uniform Illumination"

    return {
        "key_style": key_style,
        "key_description": key_description,
        "tonal_span": tonal_span,
        "ev_stops": ev_stops,
        "dynamic_range_label": dr_label,
        "center_luminance": round(center_lum, 1),
        "corner_luminance": round(corners_lum, 1),
        "vignette_falloff_pct": falloff_pct,
        "vignette_label": vignette_label,
    }


def calculate_color_harmony(dominant_colors: list) -> dict:
    """
    Determine color wheel harmony relationships among dominant colors.
    (Complementary, Analogous, Triadic, Monochromatic, or Polychromatic).
    """
    if not dominant_colors or len(dominant_colors) < 2:
        return {
            "harmony_type": "Monochromatic",
            "description": "Single primary color tone.",
            "color_hues": []
        }
        
    hues = []
    for c in dominant_colors[:4]:
        r, g, b = c["rgb"]["r"], c["rgb"]["g"], c["rgb"]["b"]
        px = np.uint8([[[b, g, r]]])
        hsv = cv2.cvtColor(px, cv2.COLOR_BGR2HSV)[0][0]
        hues.append(int(hsv[0]) * 2)

    max_dist = 0
    has_complementary = False
    
    for i in range(len(hues)):
        for j in range(i + 1, len(hues)):
            d = min(abs(hues[i] - hues[j]), 360 - abs(hues[i] - hues[j]))
            max_dist = max(max_dist, d)
            if 140 <= d <= 220:
                has_complementary = True

    if max_dist < 35:
        harmony = "Monochromatic"
        desc = "Variations of a single hue family with varying shades and saturation."
    elif max_dist < 75:
        harmony = "Analogous"
        desc = "Adjacent hues on the color wheel creating serene, natural visual flow."
    elif has_complementary:
        harmony = "Complementary"
        desc = "Opposite hues on the color wheel (e.g. Teal & Orange) creating striking contrast."
    elif 100 <= max_dist <= 140:
        harmony = "Triadic Spread"
        desc = "Evenly spaced hues providing balanced, vibrant color contrast."
    else:
        harmony = "Polychromatic"
        desc = "Diverse color palette with rich tonal variation across multiple spectrums."

    return {
        "harmony_type": harmony,
        "description": desc,
        "color_hues": hues,
    }

"""Rule-based editing recommendation engine."""

def generate_recommendations(analysis: dict) -> dict:
    """
    Generate editing recommendations based on computed metrics.
    
    This is a simple rule-based system, not an AI/ML model.
    Each recommendation explains its basis.
    """
    recommendations = []
    style_labels = []
    
    brightness = analysis.get("brightness", {})
    contrast = analysis.get("contrast", {})
    saturation = analysis.get("saturation", {})
    shadows_highlights = analysis.get("shadows_highlights", {})
    temperature = analysis.get("color_temperature", {})
    
    # === Brightness recommendations ===
    brightness_norm = brightness.get("normalized", 50)
    if brightness_norm < 35:
        recommendations.append({
            "category": "Brightness",
            "suggestion": "Consider increasing exposure or brightness",
            "reason": f"The image is dark with a brightness score of {brightness_norm}/100. "
                      f"Lifting shadows or increasing exposure could reveal more detail.",
            "priority": "high",
        })
        style_labels.append("Dark / Shadow-heavy")
    elif brightness_norm > 75:
        recommendations.append({
            "category": "Brightness",
            "suggestion": "Consider reducing exposure or brightness",
            "reason": f"The image is bright with a brightness score of {brightness_norm}/100. "
                      f"Reducing exposure could help recover highlight detail.",
            "priority": "medium",
        })
        style_labels.append("Bright / Highlight-heavy")
    
    # === Contrast recommendations ===
    contrast_norm = contrast.get("normalized", 50)
    if contrast_norm < 25:
        recommendations.append({
            "category": "Contrast",
            "suggestion": "Consider increasing contrast",
            "reason": f"The luminance variation is low ({contrast_norm}/100). "
                      f"Adding contrast could make the image more dynamic.",
            "priority": "medium",
        })
        style_labels.append("Low Contrast")
    elif contrast_norm > 75:
        recommendations.append({
            "category": "Contrast",
            "suggestion": "Review contrast levels — some detail may be lost",
            "reason": f"The contrast is high ({contrast_norm}/100). "
                      f"This can look dramatic but may clip shadows or highlights.",
            "priority": "low",
        })
        style_labels.append("High Contrast")
    
    # === Saturation recommendations ===
    sat_norm = saturation.get("normalized", 50)
    if sat_norm < 20:
        recommendations.append({
            "category": "Saturation",
            "suggestion": "Consider adding some saturation or vibrance",
            "reason": f"The image has low saturation ({sat_norm}/100). "
                      f"Boosting vibrance could add life while keeping skin tones natural.",
            "priority": "medium",
        })
        style_labels.append("Muted Colors")
    elif sat_norm > 70:
        recommendations.append({
            "category": "Saturation",
            "suggestion": "Check for oversaturation in specific color ranges",
            "reason": f"Saturation is high ({sat_norm}/100). "
                      f"Some colors may appear unnatural. Consider reducing saturation selectively.",
            "priority": "low",
        })
        style_labels.append("High Saturation")
    
    # === Shadow/Highlight recommendations ===
    shadows_pct = shadows_highlights.get("shadows_pct", 0)
    highlights_pct = shadows_highlights.get("highlights_pct", 0)
    black_clip = shadows_highlights.get("black_clipping_pct", 0)
    white_clip = shadows_highlights.get("white_clipping_pct", 0)
    
    if shadows_pct > 40:
        recommendations.append({
            "category": "Shadows",
            "suggestion": "Review shadow detail",
            "reason": f"{shadows_pct:.1f}% of the image is in shadow (luminance < 60). "
                      f"Lifting shadows could reveal hidden detail.",
            "priority": "medium",
        })
    
    if highlights_pct > 40:
        recommendations.append({
            "category": "Highlights",
            "suggestion": "Review highlight areas",
            "reason": f"{highlights_pct:.1f}% of the image is in highlights (luminance > 195). "
                      f"Pulling highlights down could recover detail.",
            "priority": "medium",
        })
    
    if black_clip > 3:
        recommendations.append({
            "category": "Clipping",
            "suggestion": "Check for crushed blacks",
            "reason": f"{black_clip:.1f}% of pixels are near pure black (luminance 0-5). "
                      f"This may indicate lost shadow detail.",
            "priority": "high",
        })
    
    if white_clip > 3:
        recommendations.append({
            "category": "Clipping",
            "suggestion": "Check for blown highlights",
            "reason": f"{white_clip:.1f}% of pixels are near pure white (luminance 250-255). "
                      f"This may indicate lost highlight detail.",
            "priority": "high",
        })
    
    # === Temperature recommendations ===
    warmth = temperature.get("warmth_index", 0)
    temp_label = temperature.get("label", "Neutral")
    if temp_label == "Warm":
        style_labels.append("Warm Color Cast")
        recommendations.append({
            "category": "Color Temperature",
            "suggestion": "Image has a warm cast — adjust white balance if unintended",
            "reason": f"The red-blue warmth index is {warmth:.1f}, suggesting a warm color cast. "
                      f"This may be intentional for golden-hour photos, or it may need white balance correction.",
            "priority": "low",
        })
    elif temp_label == "Cool":
        style_labels.append("Cool Color Cast")
        recommendations.append({
            "category": "Color Temperature",
            "suggestion": "Image has a cool cast — adjust white balance if unintended",
            "reason": f"The red-blue warmth index is {warmth:.1f}, suggesting a cool/blue color cast. "
                      f"This may be intentional for moody shots, or it may need warming.",
            "priority": "low",
        })
    
    # === Quality & Sharpness recommendations ===
    quality = analysis.get("quality", {})
    sharpness_var = quality.get("sharpness_variance", 300)
    noise_sigma = quality.get("noise_sigma", 3.0)
    
    if sharpness_var < 80:
        recommendations.append({
            "category": "Sharpness & Focus",
            "suggestion": "Image appears soft or out of focus — apply subtle unsharp mask",
            "reason": f"Laplacian focus variance is {sharpness_var:.1f} (below 80 threshold). "
                      f"Consider gentle sharpening, high-pass filter, or clarity enhancement.",
            "priority": "medium",
        })
        style_labels.append("Soft Focus")
    elif sharpness_var > 600:
        style_labels.append("Crisp Detail")
        
    if noise_sigma > 7.0:
        recommendations.append({
            "category": "Noise & Grain",
            "suggestion": "Elevated noise detected — apply luminance noise reduction",
            "reason": f"Noise variance sigma is {noise_sigma:.2f}. "
                      f"Consider mild chroma/luminance noise reduction while preserving edge texture.",
            "priority": "medium",
        })
        style_labels.append("Grainy / Textured")

    # === Lighting & Vignette recommendations ===
    lighting = analysis.get("lighting", {})
    key_style = lighting.get("key_style", "Balanced Key")
    vignette_falloff = lighting.get("vignette_falloff_pct", 0)
    tonal_span = lighting.get("tonal_span", 150)
    
    if key_style == "High-Key":
        style_labels.append("High-Key Lighting")
        recommendations.append({
            "category": "Lighting Style",
            "suggestion": "High-Key lighting detected — preserve gentle shadow roll-off",
            "reason": "Dominant bright tones with luminous highlights. Avoid over-deepening shadows to maintain the airy aesthetic.",
            "priority": "low",
        })
    elif key_style == "Low-Key":
        style_labels.append("Low-Key Lighting")
        recommendations.append({
            "category": "Lighting Style",
            "suggestion": "Low-Key lighting detected — protect shadow delineation",
            "reason": "Dramatic chiaroscuro aesthetic. Ensure subject separation remains distinct from crushed background blacks.",
            "priority": "low",
        })
        
    if vignette_falloff < -18:
        recommendations.append({
            "category": "Vignette & Lens Falloff",
            "suggestion": f"Noticeable corner falloff ({vignette_falloff:.1f}%) — apply lens profile correction or keep for artistic framing",
            "reason": "Corner luminance drops significantly relative to the center frame. Correct via lens vignetting slider if unintended.",
            "priority": "low",
        })
        style_labels.append("Corner Vignette")

    # === Color Harmony style label ===
    harmony = analysis.get("color_harmony", {})
    harmony_type = harmony.get("harmony_type")
    if harmony_type and harmony_type != "Polychromatic":
        style_labels.append(f"{harmony_type} Harmony")

    # If no issues found
    if not recommendations:
        recommendations.append({
            "category": "Overall",
            "suggestion": "Image looks well-balanced",
            "reason": "The brightness, contrast, saturation, and color temperature are all within normal ranges. "
                      "No significant adjustments are recommended.",
            "priority": "low",
        })
    
    if not style_labels:
        style_labels.append("Balanced")
    
    return {
        "style_labels": style_labels,
        "recommendations": recommendations,
    }

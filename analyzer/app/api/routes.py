"""FastAPI routes for image analysis."""
from fastapi import APIRouter, UploadFile, File, HTTPException
from ..core.image_loader import load_image, ImageLoadError
from ..analysis.metrics import (
    calculate_brightness,
    calculate_contrast,
    calculate_saturation,
    calculate_rgb_histogram,
    calculate_luminance_histogram,
    calculate_shadows_highlights,
    calculate_dominant_colors,
    estimate_color_temperature,
    calculate_image_quality,
    calculate_lighting_analysis,
    calculate_color_harmony,
)
from ..recommendations.engine import generate_recommendations
import time

router = APIRouter()

@router.get("/health")
async def health_check():
    return {"status": "ok", "service": "Photo Color Grading Analyzer - Python Engine"}

@router.post("/analyze")
async def analyze_image(file: UploadFile = File(...)):
    """
    Analyze an uploaded image and return computed metrics.
    
    Accepts: JPEG, PNG, WebP images up to 20MB.
    Returns: JSON with brightness, contrast, saturation, histograms,
             dominant colors, color temperature, and recommendations.
    """
    start_time = time.time()
    
    # Read file bytes
    try:
        file_bytes = await file.read()
    except Exception as e:
        raise HTTPException(status_code=400, detail=f"Failed to read uploaded file: {str(e)}")
    
    if not file_bytes:
        raise HTTPException(status_code=400, detail="Empty file uploaded")
    
    # Load and validate image
    try:
        img_data = load_image(file_bytes, filename=file.filename or "unknown")
    except ImageLoadError as e:
        raise HTTPException(status_code=400, detail=str(e))
    except Exception as e:
        raise HTTPException(status_code=500, detail=f"Unexpected error loading image: {str(e)}")
    
    rgb_image = img_data["rgb_image"]
    bgr_image = img_data["bgr_image"]
    
    # Run all analysis
    try:
        brightness = calculate_brightness(rgb_image)
        contrast = calculate_contrast(rgb_image)
        saturation = calculate_saturation(bgr_image)
        rgb_histogram = calculate_rgb_histogram(rgb_image)
        luminance_histogram = calculate_luminance_histogram(rgb_image)
        shadows_highlights = calculate_shadows_highlights(rgb_image)
        dominant_colors = calculate_dominant_colors(rgb_image, n_colors=5)
        color_temperature = estimate_color_temperature(rgb_image)
        quality = calculate_image_quality(bgr_image)
        lighting = calculate_lighting_analysis(rgb_image)
        color_harmony = calculate_color_harmony(dominant_colors)
    except Exception as e:
        import traceback
        traceback.print_exc()
        raise HTTPException(status_code=500, detail=f"Analysis error: {str(e)}")
    
    # Build analysis result
    analysis = {
        "brightness": brightness,
        "contrast": contrast,
        "saturation": saturation,
        "shadows_highlights": shadows_highlights,
        "color_temperature": color_temperature,
        "quality": quality,
        "lighting": lighting,
        "color_harmony": color_harmony,
    }
    
    # Generate recommendations
    try:
        recs = generate_recommendations(analysis)
    except Exception as e:
        recs = {"style_labels": ["Unknown"], "recommendations": []}
    
    processing_time = round(time.time() - start_time, 3)
    
    result = {
        "metadata": {
            "filename": file.filename or "unknown",
            "width": img_data["width"],
            "height": img_data["height"],
            "format": img_data["format"],
            "file_size": img_data["file_size"],
            "has_alpha": img_data["has_alpha"],
            "is_grayscale": img_data["is_grayscale"],
        },
        "brightness": brightness,
        "contrast": contrast,
        "saturation": saturation,
        "rgb_histogram": rgb_histogram,
        "luminance_histogram": luminance_histogram,
        "shadows_highlights": shadows_highlights,
        "dominant_colors": dominant_colors,
        "color_temperature": color_temperature,
        "quality": quality,
        "lighting": lighting,
        "color_harmony": color_harmony,
        "style_labels": recs["style_labels"],
        "recommendations": recs["recommendations"],
        "processing_time_seconds": processing_time,
    }
    
    return result

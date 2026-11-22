"""Tests for image analysis functions."""
import pytest
import numpy as np
from app.analysis.metrics import (
    calculate_brightness,
    calculate_contrast,
    calculate_saturation,
    calculate_rgb_histogram,
    calculate_luminance_histogram,
    calculate_shadows_highlights,
    calculate_dominant_colors,
    estimate_color_temperature,
    calculate_luminance,
    calculate_image_quality,
    calculate_lighting_analysis,
    calculate_color_harmony,
)
import cv2

def _make_rgb(r, g, b, size=100):
    """Create a solid color RGB image."""
    img = np.zeros((size, size, 3), dtype=np.uint8)
    img[:, :, 0] = r
    img[:, :, 1] = g
    img[:, :, 2] = b
    return img

def _make_bgr(r, g, b, size=100):
    """Create a solid color BGR image."""
    return cv2.cvtColor(_make_rgb(r, g, b, size), cv2.COLOR_RGB2BGR)

class TestBrightness:
    def test_black_image(self):
        result = calculate_brightness(_make_rgb(0, 0, 0))
        assert result["mean_brightness"] == 0
        assert result["normalized"] == 0
        assert result["label"] == "Dark"
    
    def test_white_image(self):
        result = calculate_brightness(_make_rgb(255, 255, 255))
        assert result["mean_brightness"] == 255.0
        assert result["normalized"] == 100.0
        assert result["label"] == "Bright"
    
    def test_midgray(self):
        result = calculate_brightness(_make_rgb(128, 128, 128))
        assert 45 < result["normalized"] < 55

class TestContrast:
    def test_flat_image_low_contrast(self):
        result = calculate_contrast(_make_rgb(128, 128, 128))
        assert result["std_deviation"] == 0
        assert result["label"] == "Low Contrast"
    
    def test_checker_high_contrast(self):
        img = np.zeros((100, 100, 3), dtype=np.uint8)
        img[:50, :] = 255
        result = calculate_contrast(img)
        assert result["std_deviation"] > 80

class TestSaturation:
    def test_grayscale_zero_saturation(self):
        bgr = _make_bgr(128, 128, 128)
        result = calculate_saturation(bgr)
        assert result["mean_saturation"] == 0
    
    def test_pure_red_high_saturation(self):
        bgr = _make_bgr(255, 0, 0)
        result = calculate_saturation(bgr)
        assert result["mean_saturation"] > 200

class TestHistograms:
    def test_rgb_histogram_shape(self):
        result = calculate_rgb_histogram(_make_rgb(100, 150, 200))
        assert len(result["red"]) == 256
        assert len(result["green"]) == 256
        assert len(result["blue"]) == 256
    
    def test_solid_red_histogram(self):
        result = calculate_rgb_histogram(_make_rgb(200, 0, 0))
        assert result["red"][200] == 10000  # 100x100
        assert result["green"][0] == 10000
    
    def test_luminance_histogram_shape(self):
        result = calculate_luminance_histogram(_make_rgb(128, 128, 128))
        assert len(result["luminance"]) == 256

class TestShadowsHighlights:
    def test_dark_image(self):
        result = calculate_shadows_highlights(_make_rgb(10, 10, 10))
        assert result["shadows_pct"] == 100
        assert result["highlights_pct"] == 0
    
    def test_bright_image(self):
        result = calculate_shadows_highlights(_make_rgb(250, 250, 250))
        assert result["highlights_pct"] == 100
        assert result["shadows_pct"] == 0
    
    def test_clipping(self):
        result = calculate_shadows_highlights(_make_rgb(0, 0, 0))
        assert result["black_clipping_pct"] == 100
        result2 = calculate_shadows_highlights(_make_rgb(255, 255, 255))
        assert result2["white_clipping_pct"] == 100

class TestDominantColors:
    def test_single_color(self):
        result = calculate_dominant_colors(_make_rgb(255, 0, 0), n_colors=3)
        assert len(result) == 3
        # All clusters should converge to the same color
        for c in result:
            assert c["rgb"]["r"] == 255
    
    def test_returns_five_colors(self):
        # Create an image with varied colors
        img = np.random.randint(0, 256, (200, 200, 3), dtype=np.uint8)
        result = calculate_dominant_colors(img, n_colors=5)
        assert len(result) == 5
        total_pct = sum(c["percentage"] for c in result)
        assert abs(total_pct - 100) < 1

class TestColorTemperature:
    def test_warm_image(self):
        result = estimate_color_temperature(_make_rgb(200, 100, 50))
        assert result["warmth_index"] > 0
        assert "Warm" in result["label"]
    
    def test_cool_image(self):
        result = estimate_color_temperature(_make_rgb(50, 100, 200))
        assert result["warmth_index"] < 0
        assert "Cool" in result["label"]
    
    def test_neutral_image(self):
        result = estimate_color_temperature(_make_rgb(128, 128, 128))
        assert result["label"] == "Neutral"


class TestImageQuality:
    def test_sharp_image(self):
        # Create high-contrast alternating checkerboard lines
        img = np.zeros((100, 100, 3), dtype=np.uint8)
        img[::2, :] = 255
        bgr = cv2.cvtColor(img, cv2.COLOR_RGB2BGR)
        result = calculate_image_quality(bgr)
        assert result["sharpness_variance"] > 100
        assert "sharpness_score" in result
        assert "noise_score" in result
        assert "clarity_score" in result

    def test_smooth_image_low_noise(self):
        img = np.full((100, 100, 3), 128, dtype=np.uint8)
        bgr = cv2.cvtColor(img, cv2.COLOR_RGB2BGR)
        result = calculate_image_quality(bgr)
        assert result["noise_sigma"] == 0.0
        assert result["sharpness_variance"] == 0.0


class TestLightingAnalysis:
    def test_high_key_lighting(self):
        img = np.full((100, 100, 3), 230, dtype=np.uint8)
        result = calculate_lighting_analysis(img)
        assert result["key_style"] == "High-Key"
        assert "tonal_span" in result
        assert "vignette_falloff_pct" in result

    def test_low_key_lighting(self):
        img = np.full((100, 100, 3), 30, dtype=np.uint8)
        result = calculate_lighting_analysis(img)
        assert result["key_style"] == "Low-Key"


class TestColorHarmony:
    def test_monochromatic_harmony(self):
        colors = [
            {"rgb": {"r": 200, "g": 50, "b": 50}},
            {"rgb": {"r": 180, "g": 40, "b": 40}},
        ]
        result = calculate_color_harmony(colors)
        assert result["harmony_type"] == "Monochromatic"

    def test_complementary_harmony(self):
        # Red (H~0) and Cyan/Teal (H~180)
        colors = [
            {"rgb": {"r": 240, "g": 30, "b": 30}},
            {"rgb": {"r": 30, "g": 220, "b": 240}},
        ]
        result = calculate_color_harmony(colors)
        assert result["harmony_type"] == "Complementary"


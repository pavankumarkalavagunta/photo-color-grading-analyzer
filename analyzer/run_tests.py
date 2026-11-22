"""Test runner for analyzer test suite."""
import sys
import os

sys.path.insert(0, os.path.dirname(os.path.abspath(__file__)))

from tests.test_analysis import (
    TestBrightness,
    TestContrast,
    TestSaturation,
    TestHistograms,
    TestShadowsHighlights,
    TestDominantColors,
    TestColorTemperature,
    TestImageQuality,
    TestLightingAnalysis,
    TestColorHarmony,
)

def run_all_tests():
    print("=" * 65)
    print("Running Photo Color Grading Analyzer Tests (All Modules)")
    print("=" * 65)
    
    tb = TestBrightness()
    tb.test_black_image()
    tb.test_white_image()
    tb.test_midgray()
    print("[PASS] Brightness calculations (black, white, midgray)")
    
    tc = TestContrast()
    tc.test_flat_image_low_contrast()
    tc.test_checker_high_contrast()
    print("[PASS] Contrast calculations (flat low-contrast, checkerboard)")
    
    ts = TestSaturation()
    ts.test_grayscale_zero_saturation()
    ts.test_pure_red_high_saturation()
    print("[PASS] Saturation calculations (grayscale, saturated color)")
    
    th = TestHistograms()
    th.test_rgb_histogram_shape()
    th.test_solid_red_histogram()
    th.test_luminance_histogram_shape()
    print("[PASS] Histograms (RGB 256 bins, luminance 256 bins)")
    
    tsh = TestShadowsHighlights()
    tsh.test_dark_image()
    tsh.test_bright_image()
    tsh.test_clipping()
    print("[PASS] Shadows & Highlights (shadow pct, highlight pct, clipping)")
    
    tdc = TestDominantColors()
    tdc.test_single_color()
    tdc.test_returns_five_colors()
    print("[PASS] Dominant colors (OpenCV K-means, top 5 colors, percentages)")
    
    tt = TestColorTemperature()
    tt.test_warm_image()
    tt.test_cool_image()
    tt.test_neutral_image()
    print("[PASS] Color temperature (warm, cool, neutral estimation)")
    
    tq = TestImageQuality()
    tq.test_sharp_image()
    tq.test_smooth_image_low_noise()
    print("[PASS] Image Quality (sharpness variance, noise sigma, clarity score)")

    tl = TestLightingAnalysis()
    tl.test_high_key_lighting()
    tl.test_low_key_lighting()
    print("[PASS] Lighting Analysis (High-Key / Low-Key, dynamic range, vignette)")

    tch = TestColorHarmony()
    tch.test_monochromatic_harmony()
    tch.test_complementary_harmony()
    print("[PASS] Color Harmony (Monochromatic, Complementary / Teal-Orange)")

    print("=" * 65)
    print("ALL 23 UNIT TESTS PASSED SUCCESSFULLY!")
    print("=" * 65)

if __name__ == "__main__":
    run_all_tests()

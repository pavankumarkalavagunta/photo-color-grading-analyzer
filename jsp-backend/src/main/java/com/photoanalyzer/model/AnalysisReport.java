package com.photoanalyzer.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class AnalysisReport implements Serializable {
    private static final long serialVersionUID = 1L;

    private String id;
    private String filename;
    private int width;
    private int height;
    private String format;
    private long fileSize;
    private boolean hasAlpha;
    private boolean isGrayscale;

    // Brightness
    private double meanBrightness;
    private double normalizedBrightness;
    private String brightnessLabel;

    // Contrast
    private double contrastStdDev;
    private double normalizedContrast;
    private String contrastLabel;

    // Saturation
    private double meanSaturation;
    private double normalizedSaturation;
    private double saturationStdDev;
    private String saturationLabel;
    private double saturationLowPct;
    private double saturationMidPct;
    private double saturationHighPct;

    // Color Temperature
    private double warmthIndex;
    private String temperatureLabel;
    private String temperatureDescription;
    private double meanR;
    private double meanG;
    private double meanB;

    // Histograms (256 bins each)
    private int[] redHistogram = new int[256];
    private int[] greenHistogram = new int[256];
    private int[] blueHistogram = new int[256];
    private int[] luminanceHistogram = new int[256];

    // Shadows & Highlights
    private double deepShadowsPct;
    private double shadowsPct;
    private double midtonesPct;
    private double highlightsPct;
    private double brightHighlightsPct;
    private double blackClippingPct;
    private double whiteClippingPct;

    // Quality Metrics
    private double sharpnessVariance;
    private double sharpnessScore;
    private String sharpnessLabel;
    private double noiseSigma;
    private double noiseScore;
    private String noiseLabel;
    private double clarityScore;
    private String clarityLabel;

    // Lighting Metrics
    private String keyStyle;
    private String keyDescription;
    private double tonalSpan;
    private double evStops;
    private String dynamicRangeLabel;
    private double centerLuminance;
    private double cornerLuminance;
    private double vignetteFalloffPct;
    private String vignetteLabel;

    // Harmony
    private String harmonyType;
    private String harmonyDescription;

    // Lists
    private List<DominantColor> dominantColors = new ArrayList<>();
    private List<String> styleLabels = new ArrayList<>();
    private List<Recommendation> recommendations = new ArrayList<>();

    private double processingTimeSeconds;
    private String createdAt;
    private String rawJson;
    private String imageDataUrl;

    public AnalysisReport() {}

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getFilename() { return filename; }
    public void setFilename(String filename) { this.filename = filename; }

    public int getWidth() { return width; }
    public void setWidth(int width) { this.width = width; }

    public int getHeight() { return height; }
    public void setHeight(int height) { this.height = height; }

    public String getFormat() { return format; }
    public void setFormat(String format) { this.format = format; }

    public long getFileSize() { return fileSize; }
    public void setFileSize(long fileSize) { this.fileSize = fileSize; }

    public boolean isHasAlpha() { return hasAlpha; }
    public void setHasAlpha(boolean hasAlpha) { this.hasAlpha = hasAlpha; }

    public boolean isGrayscale() { return isGrayscale; }
    public void setGrayscale(boolean grayscale) { isGrayscale = grayscale; }

    public double getMeanBrightness() { return meanBrightness; }
    public void setMeanBrightness(double meanBrightness) { this.meanBrightness = meanBrightness; }

    public double getNormalizedBrightness() { return normalizedBrightness; }
    public void setNormalizedBrightness(double normalizedBrightness) { this.normalizedBrightness = normalizedBrightness; }

    public String getBrightnessLabel() { return brightnessLabel; }
    public void setBrightnessLabel(String brightnessLabel) { this.brightnessLabel = brightnessLabel; }

    public double getContrastStdDev() { return contrastStdDev; }
    public void setContrastStdDev(double contrastStdDev) { this.contrastStdDev = contrastStdDev; }

    public double getNormalizedContrast() { return normalizedContrast; }
    public void setNormalizedContrast(double normalizedContrast) { this.normalizedContrast = normalizedContrast; }

    public String getContrastLabel() { return contrastLabel; }
    public void setContrastLabel(String contrastLabel) { this.contrastLabel = contrastLabel; }

    public double getMeanSaturation() { return meanSaturation; }
    public void setMeanSaturation(double meanSaturation) { this.meanSaturation = meanSaturation; }

    public double getNormalizedSaturation() { return normalizedSaturation; }
    public void setNormalizedSaturation(double normalizedSaturation) { this.normalizedSaturation = normalizedSaturation; }

    public double getSaturationStdDev() { return saturationStdDev; }
    public void setSaturationStdDev(double saturationStdDev) { this.saturationStdDev = saturationStdDev; }

    public String getSaturationLabel() { return saturationLabel; }
    public void setSaturationLabel(String saturationLabel) { this.saturationLabel = saturationLabel; }

    public double getSaturationLowPct() { return saturationLowPct; }
    public void setSaturationLowPct(double saturationLowPct) { this.saturationLowPct = saturationLowPct; }

    public double getSaturationMidPct() { return saturationMidPct; }
    public void setSaturationMidPct(double saturationMidPct) { this.saturationMidPct = saturationMidPct; }

    public double getSaturationHighPct() { return saturationHighPct; }
    public void setSaturationHighPct(double saturationHighPct) { this.saturationHighPct = saturationHighPct; }

    public double getWarmthIndex() { return warmthIndex; }
    public void setWarmthIndex(double warmthIndex) { this.warmthIndex = warmthIndex; }

    public String getTemperatureLabel() { return temperatureLabel; }
    public void setTemperatureLabel(String temperatureLabel) { this.temperatureLabel = temperatureLabel; }

    public String getTemperatureDescription() { return temperatureDescription; }
    public void setTemperatureDescription(String temperatureDescription) { this.temperatureDescription = temperatureDescription; }

    public double getMeanR() { return meanR; }
    public void setMeanR(double meanR) { this.meanR = meanR; }

    public double getMeanG() { return meanG; }
    public void setMeanG(double meanG) { this.meanG = meanG; }

    public double getMeanB() { return meanB; }
    public void setMeanB(double meanB) { this.meanB = meanB; }

    public int[] getRedHistogram() { return redHistogram; }
    public void setRedHistogram(int[] redHistogram) { this.redHistogram = redHistogram; }

    public int[] getGreenHistogram() { return greenHistogram; }
    public void setGreenHistogram(int[] greenHistogram) { this.greenHistogram = greenHistogram; }

    public int[] getBlueHistogram() { return blueHistogram; }
    public void setBlueHistogram(int[] blueHistogram) { this.blueHistogram = blueHistogram; }

    public int[] getLuminanceHistogram() { return luminanceHistogram; }
    public void setLuminanceHistogram(int[] luminanceHistogram) { this.luminanceHistogram = luminanceHistogram; }

    public double getDeepShadowsPct() { return deepShadowsPct; }
    public void setDeepShadowsPct(double deepShadowsPct) { this.deepShadowsPct = deepShadowsPct; }

    public double getShadowsPct() { return shadowsPct; }
    public void setShadowsPct(double shadowsPct) { this.shadowsPct = shadowsPct; }

    public double getMidtonesPct() { return midtonesPct; }
    public void setMidtonesPct(double midtonesPct) { this.midtonesPct = midtonesPct; }

    public double getHighlightsPct() { return highlightsPct; }
    public void setHighlightsPct(double highlightsPct) { this.highlightsPct = highlightsPct; }

    public double getBrightHighlightsPct() { return brightHighlightsPct; }
    public void setBrightHighlightsPct(double brightHighlightsPct) { this.brightHighlightsPct = brightHighlightsPct; }

    public double getBlackClippingPct() { return blackClippingPct; }
    public void setBlackClippingPct(double blackClippingPct) { this.blackClippingPct = blackClippingPct; }

    public double getWhiteClippingPct() { return whiteClippingPct; }
    public void setWhiteClippingPct(double whiteClippingPct) { this.whiteClippingPct = whiteClippingPct; }

    public double getSharpnessVariance() { return sharpnessVariance; }
    public void setSharpnessVariance(double sharpnessVariance) { this.sharpnessVariance = sharpnessVariance; }

    public double getSharpnessScore() { return sharpnessScore; }
    public void setSharpnessScore(double sharpnessScore) { this.sharpnessScore = sharpnessScore; }

    public String getSharpnessLabel() { return sharpnessLabel; }
    public void setSharpnessLabel(String sharpnessLabel) { this.sharpnessLabel = sharpnessLabel; }

    public double getNoiseSigma() { return noiseSigma; }
    public void setNoiseSigma(double noiseSigma) { this.noiseSigma = noiseSigma; }

    public double getNoiseScore() { return noiseScore; }
    public void setNoiseScore(double noiseScore) { this.noiseScore = noiseScore; }

    public String getNoiseLabel() { return noiseLabel; }
    public void setNoiseLabel(String noiseLabel) { this.noiseLabel = noiseLabel; }

    public double getClarityScore() { return clarityScore; }
    public void setClarityScore(double clarityScore) { this.clarityScore = clarityScore; }

    public String getClarityLabel() { return clarityLabel; }
    public void setClarityLabel(String clarityLabel) { this.clarityLabel = clarityLabel; }

    public String getKeyStyle() { return keyStyle; }
    public void setKeyStyle(String keyStyle) { this.keyStyle = keyStyle; }

    public String getKeyDescription() { return keyDescription; }
    public void setKeyDescription(String keyDescription) { this.keyDescription = keyDescription; }

    public double getTonalSpan() { return tonalSpan; }
    public void setTonalSpan(double tonalSpan) { this.tonalSpan = tonalSpan; }

    public double getEvStops() { return evStops; }
    public void setEvStops(double evStops) { this.evStops = evStops; }

    public String getDynamicRangeLabel() { return dynamicRangeLabel; }
    public void setDynamicRangeLabel(String dynamicRangeLabel) { this.dynamicRangeLabel = dynamicRangeLabel; }

    public double getCenterLuminance() { return centerLuminance; }
    public void setCenterLuminance(double centerLuminance) { this.centerLuminance = centerLuminance; }

    public double getCornerLuminance() { return cornerLuminance; }
    public void setCornerLuminance(double cornerLuminance) { this.cornerLuminance = cornerLuminance; }

    public double getVignetteFalloffPct() { return vignetteFalloffPct; }
    public void setVignetteFalloffPct(double vignetteFalloffPct) { this.vignetteFalloffPct = vignetteFalloffPct; }

    public String getVignetteLabel() { return vignetteLabel; }
    public void setVignetteLabel(String vignetteLabel) { this.vignetteLabel = vignetteLabel; }

    public String getHarmonyType() { return harmonyType; }
    public void setHarmonyType(String harmonyType) { this.harmonyType = harmonyType; }

    public String getHarmonyDescription() { return harmonyDescription; }
    public void setHarmonyDescription(String harmonyDescription) { this.harmonyDescription = harmonyDescription; }

    public List<DominantColor> getDominantColors() { return dominantColors; }
    public void setDominantColors(List<DominantColor> dominantColors) { this.dominantColors = dominantColors; }

    public List<String> getStyleLabels() { return styleLabels; }
    public void setStyleLabels(List<String> styleLabels) { this.styleLabels = styleLabels; }

    public List<Recommendation> getRecommendations() { return recommendations; }
    public void setRecommendations(List<Recommendation> recommendations) { this.recommendations = recommendations; }

    public double getProcessingTimeSeconds() { return processingTimeSeconds; }
    public void setProcessingTimeSeconds(double processingTimeSeconds) { this.processingTimeSeconds = processingTimeSeconds; }

    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }

    public String getRawJson() { return rawJson; }
    public void setRawJson(String rawJson) { this.rawJson = rawJson; }

    public String getImageDataUrl() { return imageDataUrl; }
    public void setImageDataUrl(String imageDataUrl) { this.imageDataUrl = imageDataUrl; }
}

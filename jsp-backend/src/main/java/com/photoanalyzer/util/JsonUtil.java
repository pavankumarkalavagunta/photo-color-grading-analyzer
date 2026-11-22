package com.photoanalyzer.util;

import com.photoanalyzer.model.AnalysisReport;
import com.photoanalyzer.model.DominantColor;
import com.photoanalyzer.model.Recommendation;

import java.util.*;

/**
 * Lightweight, zero-dependency JSON parser and serializer.
 * Supports standard JSON (objects, arrays, strings with escapes, numbers, booleans, null).
 */
public class JsonUtil {

    // ==========================================
    // PARSER IMPLEMENTATION (Recursive Descent)
    // ==========================================

    public static Object parse(String json) {
        if (json == null) return null;
        json = json.trim();
        if (json.isEmpty()) return null;
        Parser parser = new Parser(json);
        return parser.parseValue();
    }

    private static class Parser {
        private final String src;
        private int idx = 0;
        private final int len;

        Parser(String src) {
            this.src = src;
            this.len = src.length();
        }

        private void skipWhitespace() {
            while (idx < len) {
                char c = src.charAt(idx);
                if (c == ' ' || c == '\t' || c == '\n' || c == '\r') {
                    idx++;
                } else {
                    break;
                }
            }
        }

        Object parseValue() {
            skipWhitespace();
            if (idx >= len) return null;
            char c = src.charAt(idx);
            if (c == '{') return parseObject();
            if (c == '[') return parseArray();
            if (c == '"') return parseString();
            if (c == 't' || c == 'f') return parseBoolean();
            if (c == 'n') return parseNull();
            if (c == '-' || (c >= '0' && c <= '9')) return parseNumber();
            throw new IllegalArgumentException("Unexpected character '" + c + "' at position " + idx);
        }

        Map<String, Object> parseObject() {
            Map<String, Object> map = new LinkedHashMap<>();
            idx++; // skip '{'
            skipWhitespace();
            if (idx < len && src.charAt(idx) == '}') {
                idx++;
                return map;
            }

            while (idx < len) {
                skipWhitespace();
                if (src.charAt(idx) != '"') {
                    throw new IllegalArgumentException("Expected string key at position " + idx);
                }
                String key = parseString();
                skipWhitespace();
                if (idx >= len || src.charAt(idx) != ':') {
                    throw new IllegalArgumentException("Expected ':' after key at position " + idx);
                }
                idx++; // skip ':'
                Object val = parseValue();
                map.put(key, val);

                skipWhitespace();
                if (idx < len && src.charAt(idx) == ',') {
                    idx++; // skip ','
                } else if (idx < len && src.charAt(idx) == '}') {
                    idx++; // skip '}'
                    break;
                } else {
                    throw new IllegalArgumentException("Expected ',' or '}' at position " + idx);
                }
            }
            return map;
        }

        List<Object> parseArray() {
            List<Object> list = new ArrayList<>();
            idx++; // skip '['
            skipWhitespace();
            if (idx < len && src.charAt(idx) == ']') {
                idx++;
                return list;
            }

            while (idx < len) {
                Object val = parseValue();
                list.add(val);
                skipWhitespace();
                if (idx < len && src.charAt(idx) == ',') {
                    idx++; // skip ','
                } else if (idx < len && src.charAt(idx) == ']') {
                    idx++; // skip ']'
                    break;
                } else {
                    throw new IllegalArgumentException("Expected ',' or ']' at position " + idx);
                }
            }
            return list;
        }

        String parseString() {
            idx++; // skip opening '"'
            StringBuilder sb = new StringBuilder();
            while (idx < len) {
                char c = src.charAt(idx++);
                if (c == '"') {
                    return sb.toString();
                } else if (c == '\\') {
                    if (idx >= len) break;
                    char esc = src.charAt(idx++);
                    switch (esc) {
                        case '"': sb.append('"'); break;
                        case '\\': sb.append('\\'); break;
                        case '/': sb.append('/'); break;
                        case 'b': sb.append('\b'); break;
                        case 'f': sb.append('\f'); break;
                        case 'n': sb.append('\n'); break;
                        case 'r': sb.append('\r'); break;
                        case 't': sb.append('\t'); break;
                        case 'u':
                            if (idx + 4 <= len) {
                                String hex = src.substring(idx, idx + 4);
                                sb.append((char) Integer.parseInt(hex, 16));
                                idx += 4;
                            }
                            break;
                        default: sb.append(esc); break;
                    }
                } else {
                    sb.append(c);
                }
            }
            return sb.toString();
        }

        Boolean parseBoolean() {
            if (src.startsWith("true", idx)) {
                idx += 4;
                return Boolean.TRUE;
            } else if (src.startsWith("false", idx)) {
                idx += 5;
                return Boolean.FALSE;
            }
            throw new IllegalArgumentException("Invalid boolean literal at position " + idx);
        }

        Object parseNull() {
            if (src.startsWith("null", idx)) {
                idx += 4;
                return null;
            }
            throw new IllegalArgumentException("Invalid null literal at position " + idx);
        }

        Number parseNumber() {
            int start = idx;
            if (src.charAt(idx) == '-') idx++;
            boolean isFloating = false;
            while (idx < len) {
                char c = src.charAt(idx);
                if (c >= '0' && c <= '9') {
                    idx++;
                } else if (c == '.' || c == 'e' || c == 'E' || c == '+' || c == '-') {
                    isFloating = true;
                    idx++;
                } else {
                    break;
                }
            }
            String numStr = src.substring(start, idx);
            try {
                if (isFloating) {
                    return Double.parseDouble(numStr);
                } else {
                    long l = Long.parseLong(numStr);
                    if (l >= Integer.MIN_VALUE && l <= Integer.MAX_VALUE) {
                        return (int) l;
                    }
                    return l;
                }
            } catch (NumberFormatException e) {
                return Double.parseDouble(numStr);
            }
        }
    }

    // ==========================================
    // HELPER GETTERS FOR MAPS
    // ==========================================

    @SuppressWarnings("unchecked")
    public static Map<String, Object> asMap(Object obj) {
        if (obj instanceof Map) return (Map<String, Object>) obj;
        return Collections.emptyMap();
    }

    @SuppressWarnings("unchecked")
    public static List<Object> asList(Object obj) {
        if (obj instanceof List) return (List<Object>) obj;
        return Collections.emptyList();
    }

    public static String getString(Map<String, Object> map, String key, String def) {
        if (map == null) return def;
        Object val = map.get(key);
        return val != null ? String.valueOf(val) : def;
    }

    public static double getDouble(Map<String, Object> map, String key, double def) {
        if (map == null) return def;
        Object val = map.get(key);
        if (val instanceof Number) return ((Number) val).doubleValue();
        if (val != null) {
            try { return Double.parseDouble(String.valueOf(val)); } catch (Exception ignored) {}
        }
        return def;
    }

    public static int getInt(Map<String, Object> map, String key, int def) {
        if (map == null) return def;
        Object val = map.get(key);
        if (val instanceof Number) return ((Number) val).intValue();
        if (val != null) {
            try { return Integer.parseInt(String.valueOf(val)); } catch (Exception ignored) {}
        }
        return def;
    }

    public static long getLong(Map<String, Object> map, String key, long def) {
        if (map == null) return def;
        Object val = map.get(key);
        if (val instanceof Number) return ((Number) val).longValue();
        if (val != null) {
            try { return Long.parseLong(String.valueOf(val)); } catch (Exception ignored) {}
        }
        return def;
    }

    public static boolean getBoolean(Map<String, Object> map, String key, boolean def) {
        if (map == null) return def;
        Object val = map.get(key);
        if (val instanceof Boolean) return (Boolean) val;
        if (val != null) return Boolean.parseBoolean(String.valueOf(val));
        return def;
    }

    // ==========================================
    // SERIALIZER IMPLEMENTATION
    // ==========================================

    public static String toJson(Object obj) {
        StringBuilder sb = new StringBuilder();
        serialize(obj, sb);
        return sb.toString();
    }

    @SuppressWarnings("unchecked")
    private static void serialize(Object obj, StringBuilder sb) {
        if (obj == null) {
            sb.append("null");
        } else if (obj instanceof String) {
            sb.append('"').append(escapeString((String) obj)).append('"');
        } else if (obj instanceof Number || obj instanceof Boolean) {
            sb.append(obj);
        } else if (obj instanceof Map) {
            Map<?, ?> map = (Map<?, ?>) obj;
            sb.append("{");
            boolean first = true;
            for (Map.Entry<?, ?> entry : map.entrySet()) {
                if (!first) sb.append(",");
                first = false;
                sb.append('"').append(escapeString(String.valueOf(entry.getKey()))).append("\":");
                serialize(entry.getValue(), sb);
            }
            sb.append("}");
        } else if (obj instanceof Collection) {
            Collection<?> col = (Collection<?>) obj;
            sb.append("[");
            boolean first = true;
            for (Object item : col) {
                if (!first) sb.append(",");
                first = false;
                serialize(item, sb);
            }
            sb.append("]");
        } else if (obj instanceof int[]) {
            int[] arr = (int[]) obj;
            sb.append("[");
            for (int i = 0; i < arr.length; i++) {
                if (i > 0) sb.append(",");
                sb.append(arr[i]);
            }
            sb.append("]");
        } else if (obj instanceof AnalysisReport) {
            serializeReport((AnalysisReport) obj, sb);
        } else if (obj instanceof DominantColor) {
            DominantColor c = (DominantColor) obj;
            sb.append("{\"hex\":\"").append(c.getHex())
              .append("\",\"r\":").append(c.getR())
              .append(",\"g\":").append(c.getG())
              .append(",\"b\":").append(c.getB())
              .append(",\"percentage\":").append(c.getPercentage()).append("}");
        } else if (obj instanceof Recommendation) {
            Recommendation r = (Recommendation) obj;
            sb.append("{\"category\":\"").append(escapeString(r.getCategory()))
              .append("\",\"suggestion\":\"").append(escapeString(r.getSuggestion()))
              .append("\",\"reason\":\"").append(escapeString(r.getReason()))
              .append("\",\"priority\":\"").append(escapeString(r.getPriority())).append("\"}");
        } else {
            sb.append('"').append(escapeString(String.valueOf(obj))).append('"');
        }
    }

    private static String escapeString(String s) {
        if (s == null) return "";
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            switch (c) {
                case '"': sb.append("\\\""); break;
                case '\\': sb.append("\\\\"); break;
                case '\b': sb.append("\\b"); break;
                case '\f': sb.append("\\f"); break;
                case '\n': sb.append("\\n"); break;
                case '\r': sb.append("\\r"); break;
                case '\t': sb.append("\\t"); break;
                default:
                    if (c < 32 || c > 126) {
                        sb.append(String.format("\\u%04x", (int) c));
                    } else {
                        sb.append(c);
                    }
                    break;
            }
        }
        return sb.toString();
    }

    private static void serializeReport(AnalysisReport r, StringBuilder sb) {
        sb.append("{");
        sb.append("\"id\":\"").append(r.getId() != null ? r.getId() : "").append("\",");
        sb.append("\"filename\":\"").append(escapeString(r.getFilename())).append("\",");
        sb.append("\"width\":").append(r.getWidth()).append(",");
        sb.append("\"height\":").append(r.getHeight()).append(",");
        sb.append("\"format\":\"").append(escapeString(r.getFormat())).append("\",");
        sb.append("\"fileSize\":").append(r.getFileSize()).append(",");
        sb.append("\"hasAlpha\":").append(r.isHasAlpha()).append(",");
        sb.append("\"isGrayscale\":").append(r.isGrayscale()).append(",");

        // Brightness
        sb.append("\"brightness\":{");
        sb.append("\"mean\":").append(r.getMeanBrightness()).append(",");
        sb.append("\"normalized\":").append(r.getNormalizedBrightness()).append(",");
        sb.append("\"label\":\"").append(escapeString(r.getBrightnessLabel())).append("\"");
        sb.append("},");

        // Contrast
        sb.append("\"contrast\":{");
        sb.append("\"std_dev\":").append(r.getContrastStdDev()).append(",");
        sb.append("\"normalized\":").append(r.getNormalizedContrast()).append(",");
        sb.append("\"label\":\"").append(escapeString(r.getContrastLabel())).append("\"");
        sb.append("},");

        // Saturation
        sb.append("\"saturation\":{");
        sb.append("\"mean\":").append(r.getMeanSaturation()).append(",");
        sb.append("\"normalized\":").append(r.getNormalizedSaturation()).append(",");
        sb.append("\"std_dev\":").append(r.getSaturationStdDev()).append(",");
        sb.append("\"label\":\"").append(escapeString(r.getSaturationLabel())).append("\",");
        sb.append("\"distribution\":{");
        sb.append("\"low_pct\":").append(r.getSaturationLowPct()).append(",");
        sb.append("\"mid_pct\":").append(r.getSaturationMidPct()).append(",");
        sb.append("\"high_pct\":").append(r.getSaturationHighPct());
        sb.append("}},");

        // Color temperature
        sb.append("\"color_temperature\":{");
        sb.append("\"warmth_index\":").append(r.getWarmthIndex()).append(",");
        sb.append("\"label\":\"").append(escapeString(r.getTemperatureLabel())).append("\",");
        sb.append("\"description\":\"").append(escapeString(r.getTemperatureDescription())).append("\",");
        sb.append("\"mean_rgb\":{");
        sb.append("\"r\":").append(r.getMeanR()).append(",");
        sb.append("\"g\":").append(r.getMeanG()).append(",");
        sb.append("\"b\":").append(r.getMeanB());
        sb.append("}},");

        // Shadows & Highlights
        sb.append("\"shadows_highlights\":{");
        sb.append("\"deep_shadows_pct\":").append(r.getDeepShadowsPct()).append(",");
        sb.append("\"shadows_pct\":").append(r.getShadowsPct()).append(",");
        sb.append("\"midtones_pct\":").append(r.getMidtonesPct()).append(",");
        sb.append("\"highlights_pct\":").append(r.getHighlightsPct()).append(",");
        sb.append("\"bright_highlights_pct\":").append(r.getBrightHighlightsPct()).append(",");
        sb.append("\"black_clipping_pct\":").append(r.getBlackClippingPct()).append(",");
        sb.append("\"white_clipping_pct\":").append(r.getWhiteClippingPct());
        sb.append("},");

        // Quality
        sb.append("\"quality\":{");
        sb.append("\"sharpness\":{");
        sb.append("\"variance\":").append(r.getSharpnessVariance()).append(",");
        sb.append("\"score\":").append(r.getSharpnessScore()).append(",");
        sb.append("\"label\":\"").append(escapeString(r.getSharpnessLabel())).append("\"");
        sb.append("},");
        sb.append("\"noise\":{");
        sb.append("\"sigma\":").append(r.getNoiseSigma()).append(",");
        sb.append("\"score\":").append(r.getNoiseScore()).append(",");
        sb.append("\"label\":\"").append(escapeString(r.getNoiseLabel())).append("\"");
        sb.append("},");
        sb.append("\"clarity\":{");
        sb.append("\"score\":").append(r.getClarityScore()).append(",");
        sb.append("\"label\":\"").append(escapeString(r.getClarityLabel())).append("\"");
        sb.append("}},");

        // Lighting
        sb.append("\"lighting\":{");
        sb.append("\"key_style\":\"").append(escapeString(r.getKeyStyle())).append("\",");
        sb.append("\"description\":\"").append(escapeString(r.getKeyDescription())).append("\",");
        sb.append("\"dynamic_range\":{");
        sb.append("\"tonal_span\":").append(r.getTonalSpan()).append(",");
        sb.append("\"ev_stops\":").append(r.getEvStops()).append(",");
        sb.append("\"label\":\"").append(escapeString(r.getDynamicRangeLabel())).append("\"");
        sb.append("},");
        sb.append("\"vignette\":{");
        sb.append("\"center_luminance\":").append(r.getCenterLuminance()).append(",");
        sb.append("\"corner_luminance\":").append(r.getCornerLuminance()).append(",");
        sb.append("\"falloff_pct\":").append(r.getVignetteFalloffPct()).append(",");
        sb.append("\"label\":\"").append(escapeString(r.getVignetteLabel())).append("\"");
        sb.append("}},");

        // Harmony
        sb.append("\"color_harmony\":{");
        sb.append("\"harmony_type\":\"").append(escapeString(r.getHarmonyType())).append("\",");
        sb.append("\"description\":\"").append(escapeString(r.getHarmonyDescription())).append("\"");
        sb.append("},");

        // Dominant Colors
        sb.append("\"dominant_colors\":[");
        if (r.getDominantColors() != null) {
            for (int i = 0; i < r.getDominantColors().size(); i++) {
                if (i > 0) sb.append(",");
                serialize(r.getDominantColors().get(i), sb);
            }
        }
        sb.append("],");

        // Style labels
        sb.append("\"style_labels\":[");
        if (r.getStyleLabels() != null) {
            for (int i = 0; i < r.getStyleLabels().size(); i++) {
                if (i > 0) sb.append(",");
                sb.append('"').append(escapeString(r.getStyleLabels().get(i))).append('"');
            }
        }
        sb.append("],");

        // Recommendations
        sb.append("\"recommendations\":[");
        if (r.getRecommendations() != null) {
            for (int i = 0; i < r.getRecommendations().size(); i++) {
                if (i > 0) sb.append(",");
                serialize(r.getRecommendations().get(i), sb);
            }
        }
        sb.append("],");

        // Histograms
        sb.append("\"rgb_histogram\":{");
        sb.append("\"red\":"); serialize(r.getRedHistogram(), sb); sb.append(",");
        sb.append("\"green\":"); serialize(r.getGreenHistogram(), sb); sb.append(",");
        sb.append("\"blue\":"); serialize(r.getBlueHistogram(), sb);
        sb.append("},");

        sb.append("\"luminance_histogram\":");
        serialize(r.getLuminanceHistogram(), sb);
        sb.append(",");

        sb.append("\"processing_time_seconds\":").append(r.getProcessingTimeSeconds()).append(",");
        sb.append("\"created_at\":\"").append(escapeString(r.getCreatedAt())).append("\",");
        sb.append("\"image_data_url\":\"").append(escapeString(r.getImageDataUrl())).append("\"");
        sb.append("}");
    }

    // ==========================================
    // MAP PYTHON JSON TO ANALYSIS REPORT MODEL
    // ==========================================

    @SuppressWarnings("unchecked")
    public static AnalysisReport mapToReport(String rawJson) {
        Object parsed = parse(rawJson);
        if (!(parsed instanceof Map)) {
            throw new IllegalArgumentException("Expected JSON object");
        }
        Map<String, Object> root = (Map<String, Object>) parsed;
        AnalysisReport report = new AnalysisReport();
        report.setRawJson(rawJson);
        report.setId(UUID.randomUUID().toString());
        report.setCreatedAt(new java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'").format(new Date()));

        // Metadata
        Map<String, Object> meta = asMap(root.get("metadata"));
        report.setFilename(getString(meta, "filename", "photo.jpg"));
        report.setWidth(getInt(meta, "width", 0));
        report.setHeight(getInt(meta, "height", 0));
        report.setFormat(getString(meta, "format", "JPEG"));
        report.setFileSize(getLong(meta, "file_size", 0L));
        report.setHasAlpha(getBoolean(meta, "has_alpha", false));
        report.setGrayscale(getBoolean(meta, "is_grayscale", false));

        // Brightness
        Map<String, Object> b = asMap(root.get("brightness"));
        report.setMeanBrightness(getDouble(b, "mean_brightness", getDouble(b, "mean", 0.0)));
        report.setNormalizedBrightness(getDouble(b, "normalized", 0.0));
        report.setBrightnessLabel(getString(b, "label", "Normal"));

        // Contrast
        Map<String, Object> c = asMap(root.get("contrast"));
        report.setContrastStdDev(getDouble(c, "std_deviation", getDouble(c, "std_dev", 0.0)));
        report.setNormalizedContrast(getDouble(c, "normalized", 0.0));
        report.setContrastLabel(getString(c, "label", "Moderate Contrast"));

        // Saturation
        Map<String, Object> s = asMap(root.get("saturation"));
        report.setMeanSaturation(getDouble(s, "mean_saturation", getDouble(s, "mean", 0.0)));
        report.setNormalizedSaturation(getDouble(s, "normalized", 0.0));
        report.setSaturationStdDev(getDouble(s, "std_deviation", getDouble(s, "std_dev", 0.0)));
        report.setSaturationLabel(getString(s, "label", "Natural / Balanced"));
        Map<String, Object> sDist = asMap(s.get("distribution"));
        report.setSaturationLowPct(getDouble(sDist, "low_pct", 0.0));
        report.setSaturationMidPct(getDouble(sDist, "mid_pct", 0.0));
        report.setSaturationHighPct(getDouble(sDist, "high_pct", 0.0));

        // Color Temperature
        Map<String, Object> ct = asMap(root.get("color_temperature"));
        report.setWarmthIndex(getDouble(ct, "warmth_index", 0.0));
        report.setTemperatureLabel(getString(ct, "label", "Neutral"));
        report.setTemperatureDescription(getString(ct, "description", ""));
        Map<String, Object> mrgb = asMap(ct.get("mean_rgb"));
        report.setMeanR(getDouble(mrgb, "r", 0.0));
        report.setMeanG(getDouble(mrgb, "g", 0.0));
        report.setMeanB(getDouble(mrgb, "b", 0.0));

        // Shadows & Highlights
        Map<String, Object> sh = asMap(root.get("shadows_highlights"));
        report.setDeepShadowsPct(getDouble(sh, "deep_shadows_pct", 0.0));
        report.setShadowsPct(getDouble(sh, "shadows_pct", 0.0));
        report.setMidtonesPct(getDouble(sh, "midtones_pct", 0.0));
        report.setHighlightsPct(getDouble(sh, "highlights_pct", 0.0));
        report.setBrightHighlightsPct(getDouble(sh, "bright_highlights_pct", 0.0));
        report.setBlackClippingPct(getDouble(sh, "black_clipping_pct", 0.0));
        report.setWhiteClippingPct(getDouble(sh, "white_clipping_pct", 0.0));

        // Quality
        Map<String, Object> q = asMap(root.get("quality"));
        Map<String, Object> qSharp = asMap(q.get("sharpness"));
        report.setSharpnessVariance(getDouble(qSharp, "variance", getDouble(q, "sharpness_variance", 0.0)));
        report.setSharpnessScore(getDouble(qSharp, "score", getDouble(q, "sharpness_score", 0.0)));
        report.setSharpnessLabel(getString(qSharp, "label", getString(q, "sharpness_label", "Moderate Focus")));

        Map<String, Object> qNoise = asMap(q.get("noise"));
        report.setNoiseSigma(getDouble(qNoise, "sigma", getDouble(q, "noise_sigma", 0.0)));
        report.setNoiseScore(getDouble(qNoise, "score", getDouble(q, "noise_score", 0.0)));
        report.setNoiseLabel(getString(qNoise, "label", getString(q, "noise_label", "Clean")));

        Map<String, Object> qClarity = asMap(q.get("clarity"));
        report.setClarityScore(getDouble(qClarity, "score", getDouble(q, "clarity_score", 0.0)));
        report.setClarityLabel(getString(qClarity, "label", getString(q, "clarity_label", "Standard Clarity")));

        // Lighting
        Map<String, Object> lit = asMap(root.get("lighting"));
        report.setKeyStyle(getString(lit, "key_style", "Balanced Key"));
        report.setKeyDescription(getString(lit, "key_description", getString(lit, "description", "")));

        Map<String, Object> lDr = asMap(lit.get("dynamic_range"));
        report.setTonalSpan(getDouble(lDr, "tonal_span", getDouble(lit, "tonal_span", 0.0)));
        report.setEvStops(getDouble(lDr, "ev_stops", getDouble(lit, "ev_stops", 0.0)));
        report.setDynamicRangeLabel(getString(lDr, "label", getString(lit, "dynamic_range_label", "Standard Dynamic Range")));

        Map<String, Object> lVig = asMap(lit.get("vignette"));
        report.setCenterLuminance(getDouble(lVig, "center_luminance", getDouble(lit, "center_luminance", 0.0)));
        report.setCornerLuminance(getDouble(lVig, "corner_luminance", getDouble(lit, "corner_luminance", 0.0)));
        report.setVignetteFalloffPct(getDouble(lVig, "falloff_pct", getDouble(lit, "vignette_falloff_pct", 0.0)));
        report.setVignetteLabel(getString(lVig, "label", getString(lit, "vignette_label", "No Vignette")));

        // Harmony
        Map<String, Object> harm = asMap(root.get("color_harmony"));
        report.setHarmonyType(getString(harm, "harmony_type", "Versatile Palette"));
        report.setHarmonyDescription(getString(harm, "description", ""));

        // Dominant Colors
        List<Object> dcList = asList(root.get("dominant_colors"));
        List<DominantColor> domColors = new ArrayList<>();
        for (Object dObj : dcList) {
            Map<String, Object> dm = asMap(dObj);
            Map<String, Object> rgbMap = asMap(dm.get("rgb"));
            int rVal = getInt(rgbMap, "r", getInt(dm, "r", 0));
            int gVal = getInt(rgbMap, "g", getInt(dm, "g", 0));
            int bVal = getInt(rgbMap, "b", getInt(dm, "b", 0));
            domColors.add(new DominantColor(
                getString(dm, "hex", "#000000"),
                rVal,
                gVal,
                bVal,
                getDouble(dm, "percentage", 0.0)
            ));
        }
        report.setDominantColors(domColors);

        // Style labels
        List<Object> slList = asList(root.get("style_labels"));
        List<String> styleLabels = new ArrayList<>();
        for (Object slObj : slList) {
            if (slObj != null) styleLabels.add(String.valueOf(slObj));
        }
        report.setStyleLabels(styleLabels);

        // Recommendations
        List<Object> recList = asList(root.get("recommendations"));
        List<Recommendation> recommendations = new ArrayList<>();
        for (Object rObj : recList) {
            Map<String, Object> rm = asMap(rObj);
            recommendations.add(new Recommendation(
                getString(rm, "category", "General"),
                getString(rm, "suggestion", ""),
                getString(rm, "reason", ""),
                getString(rm, "priority", "medium")
            ));
        }
        report.setRecommendations(recommendations);

        // Histograms
        Map<String, Object> rgbH = asMap(root.get("rgb_histogram"));
        report.setRedHistogram(toIntArray(asList(rgbH.get("red"))));
        report.setGreenHistogram(toIntArray(asList(rgbH.get("green"))));
        report.setBlueHistogram(toIntArray(asList(rgbH.get("blue"))));
        Object lumObj = root.get("luminance_histogram");
        if (lumObj instanceof Map) {
            report.setLuminanceHistogram(toIntArray(asList(((Map<?, ?>) lumObj).get("luminance"))));
        } else {
            report.setLuminanceHistogram(toIntArray(asList(lumObj)));
        }

        report.setProcessingTimeSeconds(getDouble(root, "processing_time_seconds", 0.0));
        report.setImageDataUrl(getString(root, "image_data_url", null));

        return report;
    }

    private static int[] toIntArray(List<Object> list) {
        if (list == null || list.isEmpty()) return new int[256];
        int[] arr = new int[list.size()];
        for (int i = 0; i < list.size(); i++) {
            Object o = list.get(i);
            if (o instanceof Number) {
                arr[i] = ((Number) o).intValue();
            } else if (o != null) {
                try { arr[i] = Integer.parseInt(String.valueOf(o)); } catch (Exception ignored) {}
            }
        }
        return arr;
    }
}

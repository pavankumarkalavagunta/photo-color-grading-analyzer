"""Image loading and validation utilities."""
import cv2
import numpy as np
from PIL import Image
import io

SUPPORTED_FORMATS = {"JPEG", "PNG", "WEBP", "JPG"}
MAX_DIMENSION = 8000
MAX_FILE_SIZE = 20 * 1024 * 1024  # 20MB

class ImageLoadError(Exception):
    """Custom exception for image loading failures."""
    pass

def load_image(file_bytes: bytes, filename: str = "unknown") -> dict:
    """
    Load and validate an image from bytes.
    
    Returns dict with:
        - bgr_image: OpenCV BGR numpy array
        - rgb_image: RGB numpy array
        - has_alpha: bool
        - width: int
        - height: int
        - format: str
        - file_size: int
    """
    if len(file_bytes) > MAX_FILE_SIZE:
        raise ImageLoadError(f"File size {len(file_bytes)} exceeds maximum {MAX_FILE_SIZE} bytes")
    
    # Validate with Pillow first (checks actual content, not just extension)
    try:
        pil_image = Image.open(io.BytesIO(file_bytes))
        pil_image.verify()
        # Re-open after verify (verify consumes the image)
        pil_image = Image.open(io.BytesIO(file_bytes))
    except Exception as e:
        raise ImageLoadError(f"Invalid or corrupted image: {str(e)}")
    
    img_format = pil_image.format
    if img_format not in SUPPORTED_FORMATS:
        raise ImageLoadError(f"Unsupported format: {img_format}. Supported: {SUPPORTED_FORMATS}")
    
    width, height = pil_image.size
    if width > MAX_DIMENSION or height > MAX_DIMENSION:
        raise ImageLoadError(f"Image dimensions {width}x{height} exceed maximum {MAX_DIMENSION}x{MAX_DIMENSION}")
    
    has_alpha = pil_image.mode in ("RGBA", "LA", "PA")
    
    # Convert to RGB (handle transparency by compositing onto white background)
    if has_alpha:
        background = Image.new("RGB", pil_image.size, (255, 255, 255))
        if pil_image.mode == "RGBA":
            background.paste(pil_image, mask=pil_image.split()[3])
        else:
            pil_image = pil_image.convert("RGBA")
            background.paste(pil_image, mask=pil_image.split()[3])
        rgb_array = np.array(background)
    elif pil_image.mode == "L":
        # Grayscale - convert to RGB
        rgb_array = np.array(pil_image.convert("RGB"))
    else:
        rgb_array = np.array(pil_image.convert("RGB"))
    
    bgr_array = cv2.cvtColor(rgb_array, cv2.COLOR_RGB2BGR)
    
    return {
        "bgr_image": bgr_array,
        "rgb_image": rgb_array,
        "has_alpha": has_alpha,
        "width": width,
        "height": height,
        "format": img_format,
        "file_size": len(file_bytes),
        "is_grayscale": pil_image.mode in ("L", "LA"),
    }

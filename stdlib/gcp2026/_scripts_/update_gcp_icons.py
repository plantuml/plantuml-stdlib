#!/usr/bin/env python3
"""
Official Google Cloud Architecture Icons updater for PlantUML Standard Library (stdlib/gcp2026).

Zero manual category sorting:
- Automatically scans all icons in the 3 official Google Cloud icon ZIP archives from
  https://cloud.google.com/icons in priority order:
  1. `core-products-icons.zip` (4-color flagship product icons)
  2. `category-icons.zip` (2-color product category icons)
  3. `google-cloud-legacy-icons.zip` (all 216 service icons)
- Generates a flat structure in `stdlib/gcp2026/<CamelCaseName>.puml` and `stdlib/gcp2026/all.puml`
  (matching the v2 flat layout from https://github.com/plantuml/plantuml-stdlib/issues/170).
- Defines both CamelCase macros (`CloudRun`, `ComputeEngine`, `CloudStorage`, ...) and
  Snake_Case aliases (`Cloud_Run`, `Compute_Engine`, `Cloud_Storage`, `cloud_run`, ...).
- Supports triple-mode icons in every `.puml` file:
  1. Full-color inline `<svg>` sprite (`sprite $<CamelName> <svg ...>`, packed into `svg.spm`)
  2. Full-color 64x64 quantized PNG (`!function $<CamelName>IMG($scale=1)`, packed into `image.spm`)
  3. Monochrome 16-level grayscale sprite (`sprite $<CamelName>_png [64x64/16z]`, packed into `sprite.spm`)
"""

import argparse
import base64
import datetime
import hashlib
import io
import json
import os
import re
import shutil
import struct
import sys
import urllib.request
import zipfile
import zlib

try:
    from PIL import Image
    HAS_PILLOW = True
except ImportError:
    HAS_PILLOW = False

ICONS_PAGE_URL = "https://cloud.google.com/icons"

DEFAULT_SOURCES = {
    "core-products-icons.zip": "https://services.google.com/fh/files/misc/core-products-icons.zip",
    "category-icons.zip": "https://services.google.com/fh/files/misc/category-icons.zip",
    "google-cloud-legacy-icons.zip": "https://services.google.com/fh/files/misc/google-cloud-legacy-icons.zip",
}

PUML_B64_ALPHABET = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz-_"

# Common GCP acronyms preserved in uppercase when converting snake_case folder names to CamelCase
ACRONYMS = {
    "ai": "AI",
    "api": "API",
    "apis": "APIs",
    "cdn": "CDN",
    "cos": "COS",
    "db": "DB",
    "dlp": "DLP",
    "dns": "DNS",
    "ekm": "EKM",
    "gce": "GCE",
    "gcp": "GCP",
    "gke": "GKE",
    "gpu": "GPU",
    "hsm": "HSM",
    "iam": "IAM",
    "iap": "IAP",
    "ids": "IDS",
    "iot": "IoT",
    "ip": "IP",
    "kms": "KMS",
    "ml": "ML",
    "nat": "NAT",
    "nlp": "NLP",
    "os": "OS",
    "qna": "QnA",
    "sdk": "SDK",
    "sql": "SQL",
    "ssd": "SSD",
    "tpu": "TPU",
    "vm": "VM",
    "vmware": "VMware",
    "vpc": "VPC",
    "vpn": "VPN",
}

RESERVED_ROOT_PUML = {
    "GCPCommon.puml",
    "GCPRaw.puml",
    "GCPSimplified.puml",
    "GCPC4Integration.puml",
}


def derive_names_from_raw(raw_label: str) -> tuple[str, str]:
    """
    Automatically derives (CamelCaseName, Snake_Case_Name) from a folder/file name inside a GCP ZIP archive.
    Examples:
      'Cloud Run' -> ('CloudRun', 'Cloud_Run')
      'cloud_storage' -> ('CloudStorage', 'Cloud_Storage')
      'AI _ Machine Learning' -> ('AIMachineLearning', 'AI_Machine_Learning')
      'Maps & Geospatial' -> ('MapsAndGeospatial', 'Maps_And_Geospatial')
    """
    s = raw_label.replace("&", " and ")
    # Split camelCase words if already concatenated (e.g. 'VertexAI' -> 'Vertex_AI')
    s = re.sub(r"([a-z0-9])([A-Z])", r"\1_\2", s)
    tokens = [t for t in re.split(r"[^A-Za-z0-9]+", s) if t]
    camel_parts = []
    for t in tokens:
        low = t.lower()
        if low in ACRONYMS:
            camel_parts.append(ACRONYMS[low])
        elif t[0].islower():
            camel_parts.append(t[0].upper() + t[1:])
        else:
            camel_parts.append(t)
    camel_name = "".join(camel_parts)
    snake_name = "_".join(camel_parts)
    if camel_name[0].isdigit():
        camel_name = "GCP" + camel_name
        snake_name = "GCP_" + snake_name
    return camel_name, snake_name


def now_iso() -> str:
    return datetime.datetime.now(datetime.timezone.utc).replace(microsecond=0).isoformat().replace("+00:00", "Z")


def clean_svg_for_plantuml(svg_text: str) -> str:
    """
    Sanitizes and minifies an official Google Cloud SVG icon into a single-line `<svg>`
    compatible with PlantUML's SVG SAX parser and SpmBuilder's SvgAppender.
    """
    class_fills = {}
    for style_match in re.finditer(r"<style[^>]*>(.*?)</style>", svg_text, re.DOTALL | re.IGNORECASE):
        css = style_match.group(1)
        for rule_match in re.finditer(r"([^{]+)\{([^}]+)\}", css):
            selectors = rule_match.group(1)
            body = rule_match.group(2)
            fill_m = re.search(r"fill\s*:\s*([^;\s}]+)", body)
            if fill_m:
                fill_val = fill_m.group(1).strip()
                for sel in selectors.split(","):
                    sel = sel.strip()
                    if sel.startswith("."):
                        class_fills[sel[1:]] = fill_val

    s = re.sub(r"<\?xml[^>]*\?>", "", svg_text)
    s = re.sub(r"<!--.*?-->", "", s, flags=re.DOTALL)
    s = re.sub(r"<defs[^>]*>.*?</defs>", "", s, flags=re.DOTALL | re.IGNORECASE)
    s = re.sub(r"<title[^>]*>.*?</title>", "", s, flags=re.DOTALL | re.IGNORECASE)
    s = re.sub(r'<g[^>]*id="bounding_box"[^>]*>.*?</g>', "", s, flags=re.DOTALL | re.IGNORECASE)

    def replace_class(match):
        cls_name = match.group(1).strip()
        fill_val = class_fills.get(cls_name)
        if not fill_val:
            return ""
        return f'fill="{fill_val}"'

    s = re.sub(r'class="([^"]+)"', replace_class, s)
    s = re.sub(r'<rect[^>]*fill="none"[^>]*/>', "", s)

    def polygon_to_path(match):
        attrs = match.group(1)
        pts_m = re.search(r'points="([^"]+)"', attrs)
        if not pts_m:
            return match.group(0)
        nums = re.findall(r"[-+]?\d*\.?\d+(?:[eE][-+]?\d+)?", pts_m.group(1))
        if len(nums) < 4:
            return ""
        coords = [f"{nums[i]} {nums[i+1]}" for i in range(0, len(nums) - 1, 2)]
        d = "M " + " L ".join(coords) + " Z"
        other_attrs = re.sub(r'points="[^"]*"', "", attrs).strip()
        return f'<path {other_attrs} d="{d}"/>'

    s = re.sub(r"<polygon\s+([^>]+)/>", polygon_to_path, s)
    s = re.sub(r'\s+(?:fill-rule|clip-rule|data-name|id|version|xmlns(?::xlink)?)="[^"]*"', "", s)
    vb_m = re.search(r'viewBox="([^"]+)"', s)
    viewbox = vb_m.group(1) if vb_m else "0 0 24 24"
    s = re.sub(r"<svg[^>]*>", f'<svg viewBox="{viewbox}" width="64" height="64">', s, count=1)
    s = re.sub(r">\s+<", "><", s)
    s = re.sub(r"\s+", " ", s).strip()
    return s


def encode_puml_base64(data: bytes) -> str:
    res = []
    n = len(data)
    for i in range(0, n, 3):
        b1 = data[i]
        b2 = data[i + 1] if i + 1 < n else 0
        b3 = data[i + 2] if i + 2 < n else 0
        c1 = b1 >> 2
        c2 = ((b1 & 0x03) << 4) | (b2 >> 4)
        c3 = ((b2 & 0x0F) << 2) | (b3 >> 6)
        c4 = b3 & 0x3F
        res.append(PUML_B64_ALPHABET[c1])
        res.append(PUML_B64_ALPHABET[c2])
        if i + 1 < n:
            res.append(PUML_B64_ALPHABET[c3])
        if i + 2 < n:
            res.append(PUML_B64_ALPHABET[c4])
    return "".join(res)


def encode_sprite_16z(rgba_64x64: bytes) -> str:
    gray_levels = bytearray(64 * 64)
    for i in range(64 * 64):
        r = rgba_64x64[i * 4]
        g = rgba_64x64[i * 4 + 1]
        b = rgba_64x64[i * 4 + 2]
        a = rgba_64x64[i * 4 + 3]
        alpha = a / 255.0
        rw = int(r * alpha + 255 * (1.0 - alpha))
        gw = int(g * alpha + 255 * (1.0 - alpha))
        bw = int(b * alpha + 255 * (1.0 - alpha))
        lum = (299 * rw + 587 * gw + 114 * bw) // 1000
        level = min(15, max(0, (255 - lum) // 16))
        gray_levels[i] = level

    comp_obj = zlib.compressobj(level=9, method=zlib.DEFLATED, wbits=-15)
    compressed = comp_obj.compress(bytes(gray_levels)) + comp_obj.flush()
    encoded = encode_puml_base64(compressed)
    lines = [encoded[i:i + 120] for i in range(0, len(encoded), 120)]
    return "\n".join(lines)


def make_png_chunk(chunk_type: bytes, data: bytes) -> bytes:
    crc = zlib.crc32(chunk_type + data) & 0xFFFFFFFF
    return struct.pack(">I", len(data)) + chunk_type + data + struct.pack(">I", crc)


def encode_png_64x64_rgba(rgba_64x64: bytes) -> bytes:
    ihdr = struct.pack(">IIBBBBB", 64, 64, 8, 6, 0, 0, 0)
    raw_rows = bytearray()
    for y in range(64):
        raw_rows.append(0)
        raw_rows.extend(rgba_64x64[y * 256:(y + 1) * 256])
    compressed = zlib.compress(bytes(raw_rows), 9)
    return (
        b"\x89PNG\r\n\x1a\n"
        + make_png_chunk(b"IHDR", ihdr)
        + make_png_chunk(b"IDAT", compressed)
        + make_png_chunk(b"IEND", b"")
    )


def convert_png_bytes_to_64x64_rgba(png_data: bytes) -> bytes:
    if HAS_PILLOW:
        with Image.open(io.BytesIO(png_data)) as im:
            im = im.convert("RGBA")
            if im.size != (64, 64):
                im = im.resize((64, 64), Image.Resampling.LANCZOS)
            raw = im.tobytes()
            out = bytearray(64 * 64 * 4)
            for i in range(0, len(raw), 4):
                r, g, b, a = raw[i], raw[i + 1], raw[i + 2], raw[i + 3]
                if a < 16:
                    out[i:i + 4] = b"\xff\xff\xff\x00"
                else:
                    out[i] = min(255, ((r + 8) // 17) * 17)
                    out[i + 1] = min(255, ((g + 8) // 17) * 17)
                    out[i + 2] = min(255, ((b + 8) // 17) * 17)
                    out[i + 3] = 255 if a > 200 else ((a // 64) * 64)
            return bytes(out)

    pos = 8
    w = h = bd = ct = 0
    plte = b""
    trns = b""
    idat = bytearray()
    while pos < len(png_data):
        length, ctype = struct.unpack(">I4s", png_data[pos:pos + 8])
        chunk = png_data[pos + 8:pos + 8 + length]
        pos += 12 + length
        if ctype == b"IHDR":
            w, h, bd, ct, _, _, _ = struct.unpack(">IIBBBBB", chunk)
        elif ctype == b"PLTE":
            plte = chunk
        elif ctype == b"tRNS":
            trns = chunk
        elif ctype == b"IDAT":
            idat.extend(chunk)
        elif ctype == b"IEND":
            break

    raw = zlib.decompress(bytes(idat))
    if ct == 6:
        bpp = 4
        stride = w * 4
    elif ct == 2:
        bpp = 3
        stride = w * 3
    elif ct == 3:
        bpp = 1
        stride = (w * bd + 7) // 8
    else:
        raise ValueError(f"Unsupported PNG color type: {ct}")

    recon = bytearray(h * stride)
    in_pos = 0
    for y in range(h):
        ftype = raw[in_pos]
        in_pos += 1
        row_start = y * stride
        prev_start = (y - 1) * stride
        if ftype == 0:
            recon[row_start:row_start + stride] = raw[in_pos:in_pos + stride]
            in_pos += stride
        elif ftype == 1:
            for x in range(stride):
                a = recon[row_start + x - bpp] if x >= bpp else 0
                recon[row_start + x] = (raw[in_pos + x] + a) & 0xFF
            in_pos += stride
        elif ftype == 2:
            for x in range(stride):
                b = recon[prev_start + x] if y > 0 else 0
                recon[row_start + x] = (raw[in_pos + x] + b) & 0xFF
            in_pos += stride
        elif ftype == 3:
            for x in range(stride):
                a = recon[row_start + x - bpp] if x >= bpp else 0
                b = recon[prev_start + x] if y > 0 else 0
                recon[row_start + x] = (raw[in_pos + x] + ((a + b) >> 1)) & 0xFF
            in_pos += stride
        elif ftype == 4:
            for x in range(stride):
                a = recon[row_start + x - bpp] if x >= bpp else 0
                b = recon[prev_start + x] if y > 0 else 0
                c = recon[prev_start + x - bpp] if (y > 0 and x >= bpp) else 0
                p = a + b - c
                pa, pb, pc = abs(p - a), abs(p - b), abs(p - c)
                pr = a if (pa <= pb and pa <= pc) else (b if pb <= pc else c)
                recon[row_start + x] = (raw[in_pos + x] + pr) & 0xFF
            in_pos += stride

    palette = []
    if ct == 3:
        num_colors = len(plte) // 3
        for i in range(num_colors):
            r, g, b = plte[i * 3], plte[i * 3 + 1], plte[i * 3 + 2]
            al = trns[i] if i < len(trns) else 255
            palette.append((r, g, b, al))

    out_rgba = bytearray(64 * 64 * 4)
    block_w = max(1, w // 64)
    block_h = max(1, h // 64)
    sample_offsets_x = [block_w // 4, (block_w * 3) // 4] if block_w >= 4 else [0]
    sample_offsets_y = [block_h // 4, (block_h * 3) // 4] if block_h >= 4 else [0]
    num_samples = len(sample_offsets_x) * len(sample_offsets_y)

    for ty in range(64):
        base_y = ty * block_h
        for tx in range(64):
            base_x = tx * block_w
            r_sum = g_sum = b_sum = a_sum = weight_sum = 0
            for oy in sample_offsets_y:
                sy = base_y + oy
                row_off = sy * stride
                for ox in sample_offsets_x:
                    sx = base_x + ox
                    if ct == 6:
                        p = row_off + sx * 4
                        r, g, b, al = recon[p], recon[p + 1], recon[p + 2], recon[p + 3]
                    elif ct == 2:
                        p = row_off + sx * 3
                        r, g, b, al = recon[p], recon[p + 1], recon[p + 2], 255
                    else:
                        if bd == 8:
                            idx = recon[row_off + sx]
                        elif bd == 4:
                            byte_val = recon[row_off + (sx >> 1)]
                            idx = (byte_val >> 4) if (sx & 1) == 0 else (byte_val & 0x0F)
                        else:
                            byte_val = recon[row_off + (sx >> 2)]
                            shift = (3 - (sx & 3)) * 2
                            idx = (byte_val >> shift) & 0x03
                        r, g, b, al = palette[idx] if idx < len(palette) else (255, 255, 255, 0)
                    a_sum += al
                    r_sum += r * al
                    g_sum += g * al
                    b_sum += b * al
                    weight_sum += al

            avg_a = a_sum // num_samples
            out_idx = (ty * 64 + tx) * 4
            if avg_a < 16 or weight_sum == 0:
                out_rgba[out_idx:out_idx + 4] = b"\xff\xff\xff\x00"
            else:
                r_avg = r_sum // weight_sum
                g_avg = g_sum // weight_sum
                b_avg = b_sum // weight_sum
                out_rgba[out_idx] = min(255, ((r_avg + 8) // 17) * 17)
                out_rgba[out_idx + 1] = min(255, ((g_avg + 8) // 17) * 17)
                out_rgba[out_idx + 2] = min(255, ((b_avg + 8) // 17) * 17)
                out_rgba[out_idx + 3] = 255 if avg_a > 200 else ((avg_a // 64) * 64)

    return bytes(out_rgba)


def compute_gcp_uid(gcp_dir: str) -> str:
    digest = hashlib.sha1()

    def process_dir(current_dir: str):
        entries = sorted(os.listdir(current_dir))
        for name in entries:
            full_path = os.path.join(current_dir, name)
            if os.path.isfile(full_path):
                if name.startswith("_"):
                    continue
                if name.endswith(".puml"):
                    rel_path = os.path.relpath(full_path, gcp_dir).replace("\\", "/")
                    digest.update(rel_path.encode("utf-8"))
                    with open(full_path, "r", encoding="utf-8") as f:
                        in_block = False
                        for line in f.read().splitlines():
                            trimmed = line.strip()
                            if in_block:
                                if "'/" in trimmed:
                                    in_block = False
                                continue
                            if trimmed.startswith("/'"):
                                in_block = not trimmed.endswith("'/")
                                continue
                            if trimmed in ("@startuml", "@enduml") or trimmed.startswith("'"):
                                continue
                            digest.update(line.encode("utf-8"))
                            digest.update(b"\n")
                elif name.endswith(".json"):
                    rel_path = os.path.relpath(full_path, gcp_dir).replace("\\", "/")
                    digest.update(rel_path.encode("utf-8"))
                    with open(full_path, "rb") as f:
                        digest.update(f.read())
            elif os.path.isdir(full_path):
                if name.startswith("_") and name.endswith("_"):
                    continue
                process_dir(full_path)

    process_dir(gcp_dir)
    return digest.hexdigest()


def render_puml_block(camel_name: str, snake_name: str, svg_inline: str, sprite_16z: str, png_b64: str) -> str:
    lines = [
        f"sprite ${camel_name} {svg_inline}",
        "",
        f"sprite ${camel_name}_png [64x64/16z] {{",
        sprite_16z,
        "}",
        "",
        f"!function ${camel_name}IMG($scale=1)",
        f'!return "<img data:image/png;base64,{png_b64}{{scale="+$scale+"}}>"',
        "!endfunction",
        "",
        f"GCPEntityColoring({camel_name})",
        f"!define {camel_name}(e_alias, e_label, e_techn) GCPEntity(e_alias, e_label, e_techn, #4285F4, {camel_name}, {camel_name})",
        f"!define {camel_name}(e_alias, e_label, e_techn, e_descr) GCPEntity(e_alias, e_label, e_techn, e_descr, #4285F4, {camel_name}, {camel_name})",
        f"!define {camel_name}Participant(p_alias, p_label, p_techn) GCPParticipant(p_alias, p_label, p_techn, #4285F4, {camel_name}, {camel_name})",
        f"!define {camel_name}Participant(p_alias, p_label, p_techn, p_descr) GCPParticipant(p_alias, p_label, p_techn, p_descr, #4285F4, {camel_name}, {camel_name})",
    ]
    if snake_name != camel_name:
        lines.extend([
            f"GCPEntityColoring({snake_name})",
            f"!define {snake_name}(e_alias, e_label, e_techn) GCPEntity(e_alias, e_label, e_techn, #4285F4, {camel_name}, {snake_name})",
            f"!define {snake_name}(e_alias, e_label, e_techn, e_descr) GCPEntity(e_alias, e_label, e_techn, e_descr, #4285F4, {camel_name}, {snake_name})",
            f"!define {snake_name}Participant(p_alias, p_label, p_techn) GCPParticipant(p_alias, p_label, p_techn, #4285F4, {camel_name}, {snake_name})",
            f"!define {snake_name}Participant(p_alias, p_label, p_techn, p_descr) GCPParticipant(p_alias, p_label, p_techn, p_descr, #4285F4, {camel_name}, {snake_name})",
        ])
    return "\n".join(lines) + "\n"


def generate_readme(gcp_dir: str, uid: str, catalogue: list):
    today = datetime.datetime.now(datetime.timezone.utc).strftime("%Y-%m-%d")
    version = datetime.datetime.now(datetime.timezone.utc).strftime("%Y.%m")
    lines = [
        "---",
        "name: gcp2026",
        "display_name: Google Cloud Platform (2026)",
        "description: Official Google Cloud architecture and product icons for PlantUML",
        "author: Google Cloud / PlantUML Stdlib Contributors",
        f"version: {version}",
        f"release: {today}",
        "license: Apache-2.0",
        "source: https://github.com/plantuml/plantuml-stdlib",
        "origin: https://cloud.google.com/icons",
        f"uid: {uid}",
        "---",
        "",
        "# Google Cloud Platform (`gcp2026`) Standard Library",
        "",
        "Contains PlantUML sprites, macros, and stereotypes for creating architecture diagrams with official [Google Cloud Icons](https://cloud.google.com/icons).",
        "All icons are discovered and generated automatically from Google Cloud's official icon archives without manual category sorting.",
        "",
        "## Usage Example",
        "",
        "```plantuml",
        "@startuml",
        "!include <gcp2026/GCPCommon>",
        "!include <gcp2026/CloudRun>",
        "!include <gcp2026/CloudStorage>",
        "!include <gcp2026/CloudSQL>",
        "",
        'GCPEntityColored(api, "Cloud Run", "Runs containerised workloads") {',
        '  CloudRun(service, "API Service", "us-central1")',
        "}",
        "",
        'CloudStorage(bucket, "Asset Bucket", "Standard storage")',
        'CloudSQL(db, "Orders DB", "PostgreSQL 15")',
        "",
        "service --> bucket",
        "service --> db",
        "@enduml",
        "```",
        "",
        "### Rendering Modes",
        "- **Default (Full-color PNG)**: Uses `$<Service>IMG()` automatically when calling service macros.",
        "- **Full-color Vector SVG Sprites**: Define `!$GCP_USE_SVG = true` (or `!define GCP_USE_SVG`) before including `<gcp2026/GCPCommon>`, or reference `<$<Service>>` directly.",
        "- **Monochrome Grayscale `[64x64/16z]` Sprites**: Reference `<$<Service>_png>` directly.",
        "- **CamelCase & Snake_Case Support**: Both `CloudRun(...)` and `Cloud_Run(...)` macros are supported.",
        "",
        "## Icon Catalogue",
        "",
        "| Include Path | CamelCase Macro / Sprite | Snake_Case Alias | Official Source Archive |",
        "|---|---|---|---|",
    ]
    for camel_name, snake_name, zip_name in sorted(catalogue):
        alias_col = f"`{snake_name}`" if snake_name != camel_name else "—"
        lines.append(
            f"| `!include <gcp2026/{camel_name}>` | `{camel_name}` (`<${camel_name}>`) | {alias_col} | `{zip_name}` |"
        )

    readme_path = os.path.join(gcp_dir, "README.md")
    with open(readme_path, "w", encoding="utf-8", newline="\n") as f:
        f.write("\n".join(lines) + "\n")


def find_svg_entry(zf: zipfile.ZipFile, png_entry: str) -> str:
    names = set(zf.namelist())
    candidate1 = png_entry.replace("/PNG/", "/SVG/").replace(".png", ".svg")
    if candidate1 in names:
        return candidate1
    candidate2 = candidate1.replace("-color.svg", "-color-rgb.svg")
    if candidate2 in names:
        return candidate2
    candidate3 = candidate1.replace("-color-rgb.svg", "-color.svg")
    if candidate3 in names:
        return candidate3
    folder = os.path.dirname(os.path.dirname(png_entry))
    for n in names:
        if n.startswith(folder) and n.endswith(".svg") and "__MACOSX" not in n:
            return n
    raise KeyError(f"No matching SVG found for {png_entry}")


def discover_zip_urls() -> dict:
    urls = dict(DEFAULT_SOURCES)
    try:
        req = urllib.request.Request(ICONS_PAGE_URL, headers={"User-Agent": "Mozilla/5.0"})
        with urllib.request.urlopen(req, timeout=20) as resp:
            html = resp.read().decode("utf-8", "ignore")
        found = set(re.findall(r"https?://[A-Za-z0-9_./-]+\.zip", html))
        for u in found:
            fname = os.path.basename(u)
            if fname in urls:
                urls[fname] = u
    except Exception as exc:
        print(f"Note: using default ZIP URLs ({exc})", file=sys.stderr)
    return urls


def fetch_or_load_zips(local_dir: str = None) -> tuple[dict, dict]:
    zips_bytes = {}
    sources_meta = {}
    urls = discover_zip_urls() if not local_dir else DEFAULT_SOURCES
    checked_time = now_iso()

    for name, url in urls.items():
        if local_dir and os.path.exists(os.path.join(local_dir, name)):
            path = os.path.join(local_dir, name)
            with open(path, "rb") as f:
                data = f.read()
            etag = ""
            last_mod = ""
            meta_file = os.path.join(local_dir, "downloaded_meta.json")
            if os.path.exists(meta_file):
                with open(meta_file, "r", encoding="utf-8") as mf:
                    mdata = json.load(mf).get(name, {})
                    etag = mdata.get("etag", "")
                    last_mod = mdata.get("last_modified", "")
        else:
            req = urllib.request.Request(url, headers={"User-Agent": "Mozilla/5.0"})
            with urllib.request.urlopen(req, timeout=60) as resp:
                data = resp.read()
                etag = resp.headers.get("ETag", "")
                last_mod = resp.headers.get("Last-Modified", "")

        sha256 = hashlib.sha256(data).hexdigest()
        zips_bytes[name] = data
        sources_meta[name] = {
            "url": url,
            "sha256": sha256,
            "size_bytes": len(data),
            "etag": etag,
            "last_modified": last_mod,
            "checked_at": checked_time,
        }
    return zips_bytes, sources_meta


def discover_all_icons_from_zips(zips_bytes: dict) -> tuple[dict, dict]:
    """
    Automatically scans all 3 official GCP ZIP archives in priority order:
      1. core-products-icons.zip
      2. category-icons.zip
      3. google-cloud-legacy-icons.zip
    Returns (zf_map, discovered_icons) where discovered_icons maps
    camel_name -> (snake_name, zip_name, png_entry).
    """
    core_zf = zipfile.ZipFile(io.BytesIO(zips_bytes["core-products-icons.zip"]))
    cat_zf = zipfile.ZipFile(io.BytesIO(zips_bytes["category-icons.zip"]))
    leg_zf = zipfile.ZipFile(io.BytesIO(zips_bytes["google-cloud-legacy-icons.zip"]))

    zf_map = {
        "core-products-icons.zip": core_zf,
        "category-icons.zip": cat_zf,
        "google-cloud-legacy-icons.zip": leg_zf,
    }

    discovered = {}

    # 1. Core Product Icons (Unique Icons/<Product Name>/PNG/*.png)
    for entry in sorted(core_zf.namelist()):
        if entry.lower().endswith(".png") and "__MACOSX" not in entry:
            parts = entry.split("/")
            raw_label = parts[1] if len(parts) >= 3 else os.path.splitext(os.path.basename(entry))[0]
            camel_name, snake_name = derive_names_from_raw(raw_label)
            discovered[camel_name] = (snake_name, "core-products-icons.zip", entry)

    # 2. Category Icons (Category Icons/<Category Name>/PNG/*.png)
    for entry in sorted(cat_zf.namelist()):
        if entry.lower().endswith(".png") and "__MACOSX" not in entry:
            parts = entry.split("/")
            raw_label = parts[1] if len(parts) >= 3 else os.path.splitext(os.path.basename(entry))[0]
            camel_name, snake_name = derive_names_from_raw(raw_label)
            if camel_name not in discovered:
                discovered[camel_name] = (snake_name, "category-icons.zip", entry)

    # 3. Legacy Icons (<service_folder>/<service>.png)
    for entry in sorted(leg_zf.namelist()):
        if entry.lower().endswith(".png") and "__MACOSX" not in entry:
            raw_label = os.path.splitext(os.path.basename(entry))[0]
            camel_name, snake_name = derive_names_from_raw(raw_label)
            if camel_name not in discovered:
                discovered[camel_name] = (snake_name, "google-cloud-legacy-icons.zip", entry)

    return zf_map, discovered


def clean_generated_files(gcp_dir: str):
    """Removes old category subdirectories and previously generated icon .puml files."""
    for name in os.listdir(gcp_dir):
        full_path = os.path.join(gcp_dir, name)
        if os.path.isdir(full_path):
            if name.startswith("_") and name.endswith("_"):
                continue
            shutil.rmtree(full_path)
        elif os.path.isfile(full_path) and name.endswith(".puml"):
            if name not in RESERVED_ROOT_PUML:
                os.remove(full_path)


def update_library(gcp_dir: str, manifest_path: str, local_dir: str = None, check_only: bool = False) -> bool:
    existing_manifest = {}
    if os.path.exists(manifest_path):
        with open(manifest_path, "r", encoding="utf-8") as f:
            existing_manifest = json.load(f)

    created_at = existing_manifest.get("created_at", now_iso())
    prev_sources = existing_manifest.get("sources", {})
    prev_icons = existing_manifest.get("icons", {})

    zips_bytes, sources_meta = fetch_or_load_zips(local_dir=local_dir)
    has_changes = False
    for name, meta in sources_meta.items():
        if prev_sources.get(name, {}).get("sha256") != meta["sha256"]:
            has_changes = True
            break

    if check_only:
        if os.path.exists(manifest_path):
            existing_manifest["last_checked_at"] = now_iso()
            with open(manifest_path, "w", encoding="utf-8") as f:
                json.dump(existing_manifest, f, indent=2, ensure_ascii=False)
        print(f"UPDATED={'true' if has_changes else 'false'}")
        return has_changes

    clean_generated_files(gcp_dir)
    zf_map, discovered = discover_all_icons_from_zips(zips_bytes)

    icons_manifest_entries = {}
    all_blocks = []
    catalogue = []
    timestamp = now_iso()

    for camel_service in sorted(discovered.keys()):
        snake_service, zip_name, zip_entry = discovered[camel_service]
        zf = zf_map[zip_name]
        raw_png = zf.read(zip_entry)
        svg_entry = find_svg_entry(zf, zip_entry)
        raw_svg = zf.read(svg_entry).decode("utf-8", "ignore")
        svg_inline = clean_svg_for_plantuml(raw_svg)
        src_sha256 = hashlib.sha256(raw_png + raw_svg.encode("utf-8")).hexdigest()
        rgba_64 = convert_png_bytes_to_64x64_rgba(raw_png)
        png_64_bytes = encode_png_64x64_rgba(rgba_64)
        png_b64 = base64.b64encode(png_64_bytes).decode("ascii")
        sprite_16z = encode_sprite_16z(rgba_64)

        block = render_puml_block(camel_service, snake_service, svg_inline, sprite_16z, png_b64)
        puml_path = os.path.join(gcp_dir, f"{camel_service}.puml")
        with open(puml_path, "w", encoding="utf-8", newline="\n") as pf:
            pf.write(block)

        all_blocks.append(block)
        catalogue.append((camel_service, snake_service, zip_name))

        prev_entry = prev_icons.get(camel_service, {})
        icon_updated_at = (
            prev_entry.get("updated_at", timestamp)
            if prev_entry.get("sha256") == src_sha256
            else timestamp
        )
        icons_manifest_entries[camel_service] = {
            "service": camel_service,
            "snake_alias": snake_service if snake_service != camel_service else "",
            "source_archive": zip_name,
            "source_png": zip_entry,
            "source_svg": svg_entry,
            "sha256": src_sha256,
            "updated_at": icon_updated_at,
        }

    all_path = os.path.join(gcp_dir, "all.puml")
    with open(all_path, "w", encoding="utf-8", newline="\n") as af:
        af.write("\n".join(all_blocks))

    new_manifest = {
        "library": "gcp2026",
        "origin": ICONS_PAGE_URL,
        "created_at": created_at,
        "updated_at": timestamp,
        "last_checked_at": timestamp,
        "total_icons": len(icons_manifest_entries),
        "sources": sources_meta,
        "icons": icons_manifest_entries,
    }
    os.makedirs(os.path.dirname(manifest_path), exist_ok=True)
    with open(manifest_path, "w", encoding="utf-8", newline="\n") as mf:
        json.dump(new_manifest, mf, indent=2, ensure_ascii=False)
        mf.write("\n")

    uid = compute_gcp_uid(gcp_dir)
    generate_readme(gcp_dir, uid, catalogue)
    print(f"Automatically discovered and generated {len(icons_manifest_entries)} flat GCP icons in stdlib/gcp2026 (uid={uid}).")
    return True


def main():
    parser = argparse.ArgumentParser(description="Update stdlib/gcp2026 with official Google Cloud icons.")
    parser.add_argument("--check", action="store_true", help="Only check if official GCP icon ZIPs changed.")
    parser.add_argument("--local-zips", type=str, default=None, help="Directory with pre-downloaded ZIP files.")
    args = parser.parse_args()

    script_dir = os.path.dirname(os.path.abspath(__file__))
    gcp_dir = os.path.dirname(script_dir)
    manifest_path = os.path.join(script_dir, "icons_manifest.json")

    update_library(
        gcp_dir=gcp_dir,
        manifest_path=manifest_path,
        local_dir=args.local_zips,
        check_only=args.check,
    )


if __name__ == "__main__":
    main()

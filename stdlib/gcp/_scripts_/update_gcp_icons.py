#!/usr/bin/env python3
"""
Official Google Cloud Architecture Icons updater for PlantUML Standard Library (stdlib/gcp).

Features:
- Discovers and downloads official icon ZIP packages from https://cloud.google.com/icons:
  1. Core Product Icons (4-color flagship product icons)
  2. Product Category Icons (2-color category icons)
  3. Legacy Console Icons (service-level icons for backward compatibility)
- Compares SHA-256 checksums against `icons_manifest.json` (--check mode for CI/CD).
- Converts official PNG icons to 64x64 quantized RGBA PNG (Base64) + [64x64/16z] monochrome
  PlantUML sprites (using Pillow if installed, or a fast built-in pure-Python PNG processor).
- Generates `stdlib/gcp/<Category>/<Service>.puml` and `stdlib/gcp/<Category>/all.puml`.
- Tracks `created_at`, `updated_at`, and `last_checked_at` timestamps in `icons_manifest.json`.
"""

import argparse
import base64
import datetime
import hashlib
import io
import json
import os
import re
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

# Priority 1: Official 4-color Core Product Icons (core-products-icons.zip)
CORE_ICON_MAP = {
    # Existing services upgraded to new 4-color Core Product Icons
    ("Compute", "Compute_Engine"): "Unique Icons/Compute Engine/PNG/ComputeEngine-512-color.png",
    ("Compute", "Cloud_Run"): "Unique Icons/Cloud Run/PNG/CloudRun-512-color-rgb.png",
    ("Compute", "Kubernetes_Engine"): "Unique Icons/GKE/PNG/GKE-512-color.png",
    ("Databases", "Cloud_SQL"): "Unique Icons/Cloud SQL/PNG/CloudSQL-512-color.png",
    ("Databases", "Cloud_Spanner"): "Unique Icons/Cloud Spanner/PNG/CloudSpanner-512-color.png",
    ("Data_Analytics", "BigQuery"): "Unique Icons/BigQuery/PNG/BigQuery-512-color.png",
    ("API_Management", "Apigee_API_Platform"): "Unique Icons/Apigee/PNG/Apigee-512-color.png",
    ("Storage", "Cloud_Storage"): "Unique Icons/Cloud Storage/PNG/Cloud_Storage-512-color.png",
    ("Storage", "Persistent_Disk"): "Unique Icons/Hyperdisk/PNG/Hyperdisk-512-color.png",
    ("Security", "Cloud_Security_Command_Center"): "Unique Icons/Security Command Center/PNG/SecurityCommandCenter-512-color.png",
    ("AI_and_Machine_Learning", "AI_Platform"): "Unique Icons/Vertex AI/PNG/VertexAI-512-color.png",
    # New Core Product Icons
    ("AI_and_Machine_Learning", "Vertex_AI"): "Unique Icons/Vertex AI/PNG/VertexAI-512-color.png",
    ("AI_and_Machine_Learning", "AI_Hypercomputer"): "Unique Icons/AI Hypercomputer/PNG/AIHypercomputer-512-color.png",
    ("Databases", "AlloyDB"): "Unique Icons/AlloyDB/PNG/AlloyDB-512-color.png",
    ("Storage", "Hyperdisk"): "Unique Icons/Hyperdisk/PNG/Hyperdisk-512-color.png",
    ("Data_Analytics", "Looker"): "Unique Icons/Looker/PNG/Looker-512-color.png",
    ("API_Management", "Apigee"): "Unique Icons/Apigee/PNG/Apigee-512-color.png",
    ("Hybrid_and_Multi_Cloud", "Anthos"): "Unique Icons/Anthos/PNG/Anthos-512-color.png",
    ("Hybrid_and_Multi_Cloud", "Google_Distributed_Cloud"): "Unique Icons/Distributed Cloud/PNG/DistributedCloud-512-color.png",
    ("Security", "Security_Command_Center"): "Unique Icons/Security Command Center/PNG/SecurityCommandCenter-512-color.png",
    ("Security", "Google_SecOps"): "Unique Icons/Security Operations/PNG/SecOps-512-color-rgb.png",
    ("Security", "Mandiant"): "Unique Icons/Mandiant/PNG/Mandiant-512-color.png",
    ("Security", "Google_Threat_Intelligence"): "Unique Icons/Threat Intelligence/PNG/ThreatIntelligence-512-color.png",
}

# Priority 2: Official 2-color Product Category Icons (category-icons.zip)
CATEGORY_ICON_MAP = {
    ("AI_and_Machine_Learning", "AI_Machine_Learning"): "Category Icons/AI _ Machine Learning/PNG/AIMachineLearning-512-color.png",
    ("AI_and_Machine_Learning", "Agents"): "Category Icons/Agents/PNG/Agents-512-color.png",
    ("AI_and_Machine_Learning", "Gemini"): "Category Icons/Agents/PNG/Agents-512-color.png",
    ("Data_Analytics", "Business_Intelligence"): "Category Icons/Business Intelligence/PNG/BusinessIntelligence-512-color.png",
    ("Data_Analytics", "Data_Analytics"): "Category Icons/Data Analytics/PNG/DataAnalytics-512-color.png",
    ("Compute", "Compute"): "Category Icons/Compute/PNG/Compute-512-color.png",
    ("Compute", "Containers"): "Category Icons/Containers/PNG/Containers-512-color.png",
    ("Compute", "Serverless_Computing"): "Category Icons/Serverless Computing/PNG/ServerlessComputing-512-color.png",
    ("Databases", "Databases"): "Category Icons/Databases/PNG/Databases-512-color.png",
    ("Developer_Tools", "DevOps"): "Category Icons/DevOps/PNG/DevOps-512-color.png",
    ("Developer_Tools", "Developer_Tools"): "Category Icons/Developer Tools/PNG/Developer_Tools-512-color.png",
    ("Hybrid_and_Multi_Cloud", "Hybrid_Multicloud"): "Category Icons/Hybrid & Multicloud/PNG/HybridMulticloud-512-color.png",
    ("API_Management", "Integration_Services"): "Category Icons/Integration Services/PNG/IntegrationServices-512-color.png",
    ("Management_Tools", "Management_Tools"): "Category Icons/Management Tools/PNG/ManagementTools-512-color.png",
    ("Management_Tools", "Observability"): "Category Icons/Observability/PNG/Observability-512-color.png",
    ("Management_Tools", "Operations"): "Category Icons/Operations/PNG/Operations-512-color.png",
    ("Management_Tools", "Collaboration"): "Category Icons/Collaboration/PNG/Collaboration-512-color.png",
    ("Management_Tools", "Maps_and_Geospatial"): "Category Icons/Maps & Geospatial/PNG/MapsGeospatial-512-color.png",
    ("Management_Tools", "Marketplace"): "Category Icons/Marketplace/PNG/Marketplace-512-color.png",
    ("Management_Tools", "Media_Services"): "Category Icons/Media Services/PNG/MediaServices-512-color.png",
    ("Management_Tools", "Mixed_Reality"): "Category Icons/Mixed Reality/PNG/MixedReality-512-color.png",
    ("Management_Tools", "Web_and_Mobile"): "Category Icons/Web & Mobile/PNG/WebMobile-512-color.png",
    ("Management_Tools", "Web3"): "Category Icons/Web3/PNG/Web3-512-color.png",
    ("Migration", "Migration"): "Category Icons/Migration/PNG/Migration-512-color.png",
    ("Networking", "Networking"): "Category Icons/Networking/PNG/Networking-512-color.png",
    ("Security", "Security_Identity"): "Category Icons/Security Identity/PNG/SecurityIdentity-512-color.png",
    ("Storage", "Storage"): "Category Icons/Storage/PNG/Storage-512-color.png",
    # Existing generic Developer_Tools / Management_Tools entries mapped to official category icons
    ("Developer_Tools", "Cloud_Code_for_IntelliJ"): "Category Icons/Developer Tools/PNG/Developer_Tools-512-color.png",
    ("Developer_Tools", "Cloud_SDK"): "Category Icons/Developer Tools/PNG/Developer_Tools-512-color.png",
    ("Developer_Tools", "Cloud_Source_Repositories"): "Category Icons/Developer Tools/PNG/Developer_Tools-512-color.png",
    ("Developer_Tools", "Cloud_Tools_for_Eclipse"): "Category Icons/Developer Tools/PNG/Developer_Tools-512-color.png",
    ("Developer_Tools", "Gradle_App_Engine_Plugin"): "Category Icons/Developer Tools/PNG/Developer_Tools-512-color.png",
    ("Developer_Tools", "Maven_App_Engine_Plugin"): "Category Icons/Developer Tools/PNG/Developer_Tools-512-color.png",
    ("Management_Tools", "Cloud_Console"): "Category Icons/Management Tools/PNG/ManagementTools-512-color.png",
    ("Management_Tools", "Cloud_Mobile_App"): "Category Icons/Management Tools/PNG/ManagementTools-512-color.png",
}

# Priority 3: Service-level icons in google-cloud-legacy-icons.zip
LEGACY_NAME_OVERRIDES = {
    "AI_Platform_Data_Labeling_Service": "data_labeling",
    "Cloud_AutoML": "automl",
    "Cloud_Speech_to_Text": "speech-to-text",
    "Cloud_Text_to_Speech": "text-to-speech",
    "Cloud_Video_Intelligence_API": "video_intelligence_api",
    "Dialog_Flow_Enterprise_Edition": "dialogflow",
    "GKE_On_Prem": "gke_on-prem",
    "GPU": "cloud_gpu",
    "Cloud_Bigtable": "bigtable",
    "Cloud_Datastore": "datastore",
    "Cloud_Firestore": "firestore",
    "Cloud_Memorystore": "memorystore",
    "Cloud_Data_Catalog": "data_catalog",
    "Cloud_Dataflow": "dataflow",
    "Cloud_Datalab": "datalab",
    "Cloud_Dataprep": "dataprep",
    "Cloud_Dataproc": "dataproc",
    "Cloud_PubSub": "pubsub",
    "Cloud_Tools_for_PowerShell": "tools_for_powershell",
    "Cloud_Tools_for_Visual_Studio": "cloud_code",
    "IDE_Plugins": "cloud_code",
    "Cloud_IoT_Core": "iot_core",
    "Cloud_Billing_API": "billing",
    "Cloud_Service_Mesh": "anthos_service_mesh",
    "Logging": "cloud_logging",
    "Monitoring": "cloud_monitoring",
    "Dedicated_Interconnect": "cloud_interconnect",
    "Cloud_IAM": "identity_and_access_management",
    "Cloud_Resource_Manager": "project",
    "Cloud_Filestore": "filestore",
}

# Additional official services from google-cloud-legacy-icons.zip to enrich stdlib/gcp
ADDITIONAL_LEGACY_SERVICES = {
    ("AI_and_Machine_Learning", "Document_AI"): "document_ai",
    ("AI_and_Machine_Learning", "Contact_Center_AI"): "contact_center_ai",
    ("API_Management", "Cloud_API_Gateway"): "cloud_api_gateway",
    ("Compute", "Batch"): "batch",
    ("Compute", "VMware_Engine"): "vmware_engine",
    ("Databases", "Datastream"): "datastream",
    ("Databases", "Database_Migration_Service"): "database_migration_service",
    ("Data_Analytics", "Dataplex"): "dataplex",
    ("Data_Analytics", "Analytics_Hub"): "analytics_hub",
    ("Developer_Tools", "Artifact_Registry"): "artifact_registry",
    ("Developer_Tools", "Cloud_Deploy"): "cloud_deploy",
    ("Management_Tools", "Workflows"): "workflows",
    ("Management_Tools", "Eventarc"): "eventarc",
    ("Security", "Secret_Manager"): "secret_manager",
    ("Security", "Certificate_Manager"): "certificate_manager",
    ("Security", "Cloud_IDS"): "cloud_ids",
    ("Security", "BeyondCorp"): "beyondcorp",
}


def now_iso() -> str:
    return datetime.datetime.now(datetime.timezone.utc).replace(microsecond=0).isoformat().replace("+00:00", "Z")


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
    """
    Encodes a 64x64 RGBA buffer into a PlantUML [64x64/16z] compressed monochrome sprite
    compatible with net.sourceforge.plantuml.klimt.sprite.SpriteGrayLevel.GRAY_16.
    """
    gray_levels = bytearray(64 * 64)
    for i in range(64 * 64):
        r = rgba_64x64[i * 4]
        g = rgba_64x64[i * 4 + 1]
        b = rgba_64x64[i * 4 + 2]
        a = rgba_64x64[i * 4 + 3]
        # Composite over white background (#FFFFFF)
        alpha = a / 255.0
        rw = int(r * alpha + 255 * (1.0 - alpha))
        gw = int(g * alpha + 255 * (1.0 - alpha))
        bw = int(b * alpha + 255 * (1.0 - alpha))
        lum = (299 * rw + 587 * gw + 114 * bw) // 1000
        # 0 = white, 15 = black
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
    """Encodes a 64x64 RGBA byte buffer into a compact standard PNG."""
    ihdr = struct.pack(">IIBBBBB", 64, 64, 8, 6, 0, 0, 0)
    raw_rows = bytearray()
    for y in range(64):
        raw_rows.append(0)  # Filter type 0 (None)
        raw_rows.extend(rgba_64x64[y * 256:(y + 1) * 256])
    compressed = zlib.compress(bytes(raw_rows), 9)
    return (
        b"\x89PNG\r\n\x1a\n"
        + make_png_chunk(b"IHDR", ihdr)
        + make_png_chunk(b"IDAT", compressed)
        + make_png_chunk(b"IEND", b"")
    )


def convert_png_bytes_to_64x64_rgba(png_data: bytes) -> bytes:
    """
    Resizes an official GCP PNG icon to 64x64 RGBA and quantizes color levels
    so that the total color table across the entire library stays well below
    ImageAppender's 0xFFFF (65,535) color limit.
    """
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

    # Pure-Python fast PNG decoder + 2x2 supersampled area downsampler
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
    """
    Computes the exact SHA-1 UID for stdlib/gcp matching com.plantuml.stdlibencoder.uid.UidBuilder.
    """
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


def update_readme_uid(gcp_dir: str, uid: str):
    readme_path = os.path.join(gcp_dir, "README.md")
    if not os.path.exists(readme_path):
        return
    with open(readme_path, "r", encoding="utf-8") as f:
        content = f.read()
    today = datetime.datetime.now(datetime.timezone.utc).strftime("%Y-%m-%d")
    version = datetime.datetime.now(datetime.timezone.utc).strftime("%Y.%m")
    content = re.sub(r"(?m)^version:.*$", f"version: {version}", content)
    content = re.sub(r"(?m)^release:.*$", f"release: {today}", content)
    content = re.sub(r"(?m)^uid:.*$", f"uid: {uid}", content)
    with open(readme_path, "w", encoding="utf-8", newline="\n") as f:
        f.write(content)


def render_puml_block(service: str, sprite_16z: str, png_b64: str) -> str:
    return (
        f"sprite ${service} [64x64/16z] {{\n"
        f"{sprite_16z}\n"
        f"}}\n\n"
        f"!function ${service}IMG($scale=1)\n"
        f'!return "<img data:image/png;base64,{png_b64}{{scale="+$scale+"}}>"\n'
        f"!endfunction\n\n"
        f"EntityColoring({service})\n"
        f"!define {service}(e_alias, e_label, e_techn) Entity(e_alias, e_label, e_techn, #036ffc, {service}, {service})\n"
        f"!define {service}(e_alias, e_label, e_techn, e_descr) Entity(e_alias, e_label, e_techn, e_descr, #036ffc, {service}, {service})\n"
        f"!define {service}Participant(p_alias, p_label, p_techn) Participant(p_alias, p_label, p_techn, #036ffc, {service}, {service})\n"
        f"!define {service}Participant(p_alias, p_label, p_techn, p_descr) Participant(p_alias, p_label, p_techn, p_descr, #036ffc, {service}, {service})\n"
    )


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

    core_zf = zipfile.ZipFile(io.BytesIO(zips_bytes["core-products-icons.zip"]))
    cat_zf = zipfile.ZipFile(io.BytesIO(zips_bytes["category-icons.zip"]))
    leg_zf = zipfile.ZipFile(io.BytesIO(zips_bytes["google-cloud-legacy-icons.zip"]))

    legacy_by_key = {}
    for entry in leg_zf.namelist():
        if entry.endswith(".png") and "__MACOSX" not in entry:
            k = os.path.splitext(os.path.basename(entry))[0].lower()
            legacy_by_key[k] = entry

    # Collect all target (Category, Service) pairs
    targets = {}

    # 1. Existing .puml files in stdlib/gcp/<Category>/<Service>.puml
    for root, _, files in os.walk(gcp_dir):
        rel = os.path.relpath(root, gcp_dir)
        if rel == "." or rel.startswith("_") or os.sep in rel or "/" in rel:
            continue
        for fname in sorted(files):
            if fname.endswith(".puml") and fname != "all.puml":
                service = fname[:-5]
                targets[(rel, service)] = None

    # 2. Add all Core Product Icons, Category Icons, and Additional Legacy Icons
    for key in CORE_ICON_MAP:
        targets[key] = None
    for key in CATEGORY_ICON_MAP:
        targets[key] = None
    for key in ADDITIONAL_LEGACY_SERVICES:
        targets[key] = None

    # Resolve source ZIP and entry path for each target
    resolved = {}
    for cat, service in sorted(targets.keys()):
        if (cat, service) in CORE_ICON_MAP:
            resolved[(cat, service)] = ("core-products-icons.zip", CORE_ICON_MAP[(cat, service)])
        elif (cat, service) in CATEGORY_ICON_MAP:
            resolved[(cat, service)] = ("category-icons.zip", CATEGORY_ICON_MAP[(cat, service)])
        elif (cat, service) in ADDITIONAL_LEGACY_SERVICES:
            leg_key = ADDITIONAL_LEGACY_SERVICES[(cat, service)]
            resolved[(cat, service)] = ("google-cloud-legacy-icons.zip", legacy_by_key[leg_key])
        elif service in LEGACY_NAME_OVERRIDES:
            leg_key = LEGACY_NAME_OVERRIDES[service]
            resolved[(cat, service)] = ("google-cloud-legacy-icons.zip", legacy_by_key[leg_key])
        elif service.lower() in legacy_by_key:
            resolved[(cat, service)] = ("google-cloud-legacy-icons.zip", legacy_by_key[service.lower()])
        else:
            resolved[(cat, service)] = ("google-cloud-legacy-icons.zip", legacy_by_key["cloud_generic"])

    zf_map = {
        "core-products-icons.zip": core_zf,
        "category-icons.zip": cat_zf,
        "google-cloud-legacy-icons.zip": leg_zf,
    }

    # Cache converted PNG & sprite per (zip_name, zip_entry)
    conversion_cache = {}
    icons_manifest_entries = {}
    by_category = {}
    timestamp = now_iso()

    for (cat, service), (zip_name, zip_entry) in sorted(resolved.items()):
        cache_key = (zip_name, zip_entry)
        if cache_key not in conversion_cache:
            raw_png = zf_map[zip_name].read(zip_entry)
            src_sha256 = hashlib.sha256(raw_png).hexdigest()
            rgba_64 = convert_png_bytes_to_64x64_rgba(raw_png)
            png_64_bytes = encode_png_64x64_rgba(rgba_64)
            png_b64 = base64.b64encode(png_64_bytes).decode("ascii")
            sprite_16z = encode_sprite_16z(rgba_64)
            conversion_cache[cache_key] = (src_sha256, sprite_16z, png_b64)

        src_sha256, sprite_16z, png_b64 = conversion_cache[cache_key]
        block = render_puml_block(service, sprite_16z, png_b64)

        cat_dir = os.path.join(gcp_dir, cat)
        os.makedirs(cat_dir, exist_ok=True)
        puml_path = os.path.join(cat_dir, f"{service}.puml")
        with open(puml_path, "w", encoding="utf-8", newline="\n") as pf:
            pf.write(block)

        by_category.setdefault(cat, []).append(block)
        icon_id = f"{cat}/{service}"
        prev_entry = prev_icons.get(icon_id, {})
        icon_updated_at = (
            prev_entry.get("updated_at", timestamp)
            if prev_entry.get("sha256") == src_sha256
            else timestamp
        )
        icons_manifest_entries[icon_id] = {
            "category": cat,
            "service": service,
            "source_archive": zip_name,
            "source_entry": zip_entry,
            "sha256": src_sha256,
            "updated_at": icon_updated_at,
        }

    # Write <Category>/all.puml files
    for cat, blocks in sorted(by_category.items()):
        all_path = os.path.join(gcp_dir, cat, "all.puml")
        with open(all_path, "w", encoding="utf-8", newline="\n") as af:
            af.write("\n".join(blocks))

    new_manifest = {
        "library": "gcp",
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
    update_readme_uid(gcp_dir, uid)
    print(f"Updated {len(icons_manifest_entries)} GCP icons across {len(by_category)} categories (uid={uid}).")
    return True


def main():
    parser = argparse.ArgumentParser(description="Update stdlib/gcp with official Google Cloud icons.")
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

# Changelog — `gcp2026` Standard Library

All notable changes to the `gcp2026` PlantUML Standard Library are documented in this file.

## [2026.11] - 2026-11-17

### Added
- Created `stdlib/gcp2026` following [plantuml/plantuml-stdlib#170](https://github.com/plantuml/plantuml-stdlib/issues/170).
- Preserved legacy `gcp` v6.0.0 library in `stdlib/gcp1` with `stdlib/gcp` acting as a symbolic redirect (`link: gcp1`).
- Removed manual category taxonomy in `stdlib/gcp2026` in favor of a **100% automatic flat layout** (`!include <gcp2026/CloudRun>`, `!include <gcp2026/CloudStorage>`, `!include <gcp2026/CloudSQL>`, etc.):
  - Automatically discovers and converts **253 icons** directly from the three official [Google Cloud Icon Library](https://cloud.google.com/icons) archives in priority order:
    1. `core-products-icons.zip` (19 4-color flagship Core Product icons)
    2. `category-icons.zip` (26 2-color Product Category icons)
    3. `google-cloud-legacy-icons.zip` (216 service-specific icons)
- Adopted **CamelCase** file and macro names (`CloudRun`, `ComputeEngine`, `CloudStorage`, `CloudSQL`, `VertexAI`, etc.) while keeping **Snake_Case** macro aliases (`Cloud_Run`, `Compute_Engine`, `Cloud_Storage`, `Cloud_SQL`, `Vertex_AI`, etc.).
- Added triple-mode icon definitions in every `.puml` file:
  1. Full-color inline `<svg>` sprite (`sprite $<Name> <svg ...>`, packed into `svg.spm`) with CSS classes inlined and `<polygon>` converted to `<path>` for PlantUML SVG SAX parser compatibility.
  2. Full-color 64x64 quantized PNG (`!function $<Name>IMG($scale=1)`, packed into `image.spm`).
  3. Monochrome 16-level grayscale sprite (`sprite $<Name>_png [64x64/16z]`, packed into `sprite.spm`).
- Added automated updater and SHA-256 manifest tracker in `stdlib/gcp2026/_scripts_/`.

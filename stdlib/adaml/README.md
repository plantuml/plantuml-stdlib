---
name: adaml
display_name: AdaML
description:
author: Francesc Rocher
version: 0.1.2
release:
license: GPL
source: https://github.com/rocher/AdaML
origin:
uid: 8ddd75a4983ab531963a6edb796b37302a56361d
---
# Quick Introduction

### AdaML Diagram
This AdaML diagram describes AdaML itself, including relationships between
PlatUML, UML and Ada *packages*. This diagram has been generated, in turn, with
AdaML. See below the AdaML source code.

<p align="center">
[![](https://img.plantuml.biz/plantuml/svg/RP1D3i8W48Nt9Dm1kxA9nWiOK_UQk67Nn8nbh4GwD43Jwql_8ZLk6FZUyEP1uGEuq3M6iuscodGAnV6iu7evZEk9CyvQgDvGupORKPOdv6Q0Wlo1iMrrddNER4z-STc9rMM5BP9ArS4bbW3QM8VgVvESYdBG-sa6sR0a5ICMSRzVN4gI0zTGEsYyU8179IoLb6RXpE0pt9skNo5vuZmkNylocpOgHSmNV_C3)](https://editor.plantuml.com/uml/RP1D3i8W48Nt9Dm1kxA9nWiOK_UQk67Nn8nbh4GwD43Jwql_8ZLk6FZUyEP1uGEuq3M6iuscodGAnV6iu7evZEk9CyvQgDvGupORKPOdv6Q0Wlo1iMrrddNER4z-STc9rMM5BP9ArS4bbW3QM8VgVvESYdBG-sa6sR0a5ICMSRzVN4gI0zTGEsYyU8179IoLb6RXpE0pt9skNo5vuZmkNylocpOgHSmNV_C3)
</p>

<details>
<summary><b>AdaML Source Code</b> &nbsp; &mdash; <i>click to expand</i> &mdash;</summary>

```plantuml
@startuml
!include <AdaML/AdaML>

package("AdaML")
package("PlantUML")
package("Ada")
package("UML")

owns("AdaML", "PlantUML")
depends("AdaML", "Ada", "tailored")
depends("AdaML", "UML", "draws")

note("AdaML draws Ada-tailored\nUML diagrams based on\nPlantUML")

left_right("PlantUML", "UML")
left_right("UML", "Ada")
@enduml
```
</details>

### Doc
For more information and examples, download
[AdaML.pdf](https://raw.github.com/rocher/AdaML/release/0.1.2/doc/AdaML.pdf) or
[read online](https://github.com/rocher/AdaML/blob/release/0.1.2/doc/AdaML.pdf)
in GitHub.

# Introduction

### What is AdaML?
- UML tailored for Ada 2012 programming language
- A modeling language to draw UML diagrams, implemented in PlantUML
- An easy way to learn Ada through the use of UML and OOP concepts

### Features
- UML tailored to use and show particular Ada language characteristics
- Coherent set of functions to design software components for Ada
- Generates high quality drawings (ps, eps) easy to embedded in other docs
- Easy to learn by example, both AdaML and Ada language

### What is /not/ AdaML?
- A model-based tool to generate Ada code
- A reverse engineering tool to draw UML diagrams from Ada code
- An interactive UML modeling or drawing tool

# Local Usage
AdaML must be used locally to get quality graphics. You can generate diagrams in
`eps` format that can be embedded in LaTeX docs.

### Requirements
- [PlantUML](https://plantuml.com) installed and working in your system
- The AdaML files, check [AdaML](https://github.com/rocher/AdaML) installation
  instructions
- Your favorite text editor, preferably with PlantUML support (e.g. Emacs)
- Check the list of [supported editors](http://plantuml.com/running)
- For better visualization and integration with LaTeX, [computer
  modern](https://www.fontsquirrel.com/fonts/computer-modern) fonts

# Online Usage
### Online Version - Quick Start
- Open [PlantUML Previewer](http://sujoyu.github.io/plantuml-previewer) or
  [PlantText](https://www.planttext.com/) editor
- Remove default lines and paste the following code:

```plantuml
@startuml
!include <AdaML/AdaML>

begin_type("Pan_Dimensional")
  procedure("Ask_The_Question", "in out Natural")
end()

begin_package("Deep_Thought")
  function("Answer_The_Question", "", "Natural")
private()
  variable("The_Answer", "Natural", 42)
end()

depends("Pan_Dimensional", "Deep_Thought", "ask >")
@enduml
```

### Embed Diagrams in GitHub
This is an example of the AdaML documentation:

<p align="center">
[![](https://img.plantuml.biz/plantuml/svg/PP3D2e90483lWk-mUL8GWkWOOU2neg2xJEwaYpek-sFqzerwoF0mommxtpUpcra7nlck3OEDehBr4iNX9E5otaxdCGp2u8cLei9zDCRH3QZ8LOTaLK_GHaaO2A5DNwBqXkzFjYaUDHPtZzPnIvIAI97elHDNSDxC29ACavzQGzb0nNIEg1dlVLMxMVtoL8uUDfDzerd9nrYOjL43E8mdU02ZuDcoUAHcmR8_5VlTSXk9cdExVYKpVwjn3ROHnt5YnWX_u1S0)](https://editor.plantuml.com/uml/PP3D2e90483lWk-mUL8GWkWOOU2neg2xJEwaYpek-sFqzerwoF0mommxtpUpcra7nlck3OEDehBr4iNX9E5otaxdCGp2u8cLei9zDCRH3QZ8LOTaLK_GHaaO2A5DNwBqXkzFjYaUDHPtZzPnIvIAI97elHDNSDxC29ACavzQGzb0nNIEg1dlVLMxMVtoL8uUDfDzerd9nrYOjL43E8mdU02ZuDcoUAHcmR8_5VlTSXk9cdExVYKpVwjn3ROHnt5YnWX_u1S0)
</p>

- Check the [raw version](https://raw.github.com/rocher/AdaML/develop/README.md)
  of this `README.md` file to see the image link used (plus how to easily center
  images in GitHub)
- The AdaML source code can be found in GitHub:
  [deep-thought-02.aml](https://raw.github.com/rocher/AdaML/develop/diagram/deep-thought-02.aml)

# ChangeLog
### Release 0.1.2

+ Adapt to new PlantUML interface

### Release 0.1.1

+ Scaled images of `REAME.md` file
+ AdaML file extension changed to `*.aml`

<details>
<summary><b>Older Releases</b> &nbsp; &mdash; <i>click to expand</i> &mdash;</summary>

### Release 0.1.0

+ First public release

</details>

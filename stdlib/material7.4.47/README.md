---
name: material7.4.47
display_name: Material
description: SVG Material Design Icon
author: Austin Andrews
version: 7.4.47
release: 
license: Apache License, Version 2.0
source: https://github.com/Templarian/MaterialDesign-SVG/
origin: 
---

# Information about the `material7.4.47` Standard Library.

## Sprites without additional dependencies

The sprites are imported from https://pictogrammers.com/library/mdi/ and organized in folders based on the given category names.

The sprites can be included via the (normalized) collection name and the (normalized) icon name. In both cases is the normalization an upper camel case notation without any special characters.

**Example:** the original icon `account-alert` belongs to the category ´Account/User´ therefore the corresponding sprite can be included via `!include <material7.4.47/AccountUser/AccountAlert>` (only this icon/sprite) or `!include <material7.4.47/AccountUser/all>` (with all other icons/sprites of the collection).  
The icon `account-alert` has the category ´Alert/Error´ too, therefore it could be loaded via `!include <material7.4.47/AccountUser/AlertError>` too.

The sprites itself has the (normalized) icon name with the prefix `mdi`.

**Example:** the original icon `account-alert` can be integrated in a PlantUML diagram with `<$mdiAccountAlert>`.

```plantuml
@startuml

' no common.puml loaded, text alignment remains left

!include <material7.4.47/AccountUser/AccountAlert>
!include <material7.4.47/Emoji/all>

package "Possible image samples\nwithout additional dependencies" {
  label "explicit loaded sprite in text\n<$mdiAccountAlert>" as accountAlertSprite

  label "sprite loaded via collection Emoji/all.puml in text\n<$mdiEmoticonLol>" as emoticonLol
}

@enduml
```
(The source of the sample can be found in [stdlib/material7.4.47/\_examples\_/OnlySpriteSample.puml](https://github.com/plantuml/plantuml-stdlib/tree/master/stdlib/material7.4.47/_examples_/OnlySpriteSample.puml))
[![PleaseOpenLinkIfImageCannotBeDisplayed](https://www.plantuml.com/plantuml/svg/TL2_Jq913DxlhwWl9Yu65X8MGc1mSp0nRYpbhcArrrxk-fJ4-B_xWAV8uDP-pVVZwwevLk-pX703QX0jPzD96H0GmqJf5fps3YYyqqpgK2aZQmEXPm_XYZLAdmWM6PqgeymdiybiFbt7QBtwKwFwCw-5gY__fznbU-KfYYn3A1ZVS4VGFLXhl1K2peUzOIv2RQCVx2_MEs1Ax6oA0eaAQIADJAs3pm0Wk2M1ZlP5EBAFbQ2Loax0UkosqSLrJdmHiWDiW7-GnoCbd3L7ZL7ndH6YYL0yP87V9gTFNXeDH-TeUczoyg4p4Bv2M96cWVKD)](https://www.plantuml.com/plantuml/uml/TL2_Jq913DxlhwWl9Yu65X8MGc1mSp0nRYpbhcArrrxk-fJ4-B_xWAV8uDP-pVVZwwevLk-pX703QX0jPzD96H0GmqJf5fps3YYyqqpgK2aZQmEXPm_XYZLAdmWM6PqgeymdiybiFbt7QBtwKwFwCw-5gY__fznbU-KfYYn3A1ZVS4VGFLXhl1K2peUzOIv2RQCVx2_MEs1Ax6oA0eaAQIADJAs3pm0Wk2M1ZlP5EBAFbQ2Loax0UkosqSLrJdmHiWDiW7-GnoCbd3L7ZL7ndH6YYL0yP87V9gTFNXeDH-TeUczoyg4p4Bv2M96cWVKD)

## Extended functionality with `!include <material7.4.47/common>`

If the common file is included, the following additional functionalities are provided per sprite:

`$Mdi<SpriteName>Img($color = $MDI_DEFAULT, $scale = "1")`: Returns the sprite as an image with the given color and scale:.
Example `MdiAccountAlertImg(red, 2)` produces an AccountAlert sprite in red with a scale factor of 2.


`$Mdi<SpriteName>($alias, $label=$MDI_DEFAULT, $shape=$MDI_DEFAULT,$color=$MDI_DEFAULT, $scale=1, $stereo=$MDI_DEFAULT)`: Returns a PlantUML element with the given alias, label, shape, color, scale and stereotype.
Example `MdiAccountAlert("myAlias", "My Label", rectangle, blue, 2, "My Stereo")` produces a blue rectangle with an AccountAlert sprite scaled by a factor of 2, the label "My Label", the alias "myAlias" and the stereotype "My Stereo".

Additionally, contains the common file some argument specific predefined constants and default values which can be changed to customize the appearance of all icons/sprites.

```plantuml
' ==========================================================
' MDI (default) values which can be used by the macros =====
'
' The default values can be overwritten before the common.puml is included
' Details of the values see below:
' !$MDI_ALIGNMENT = (""|"left"|"center"|"right")
' !$MDI_DEFAULT_LABEL = (""|"*")
' !$MDI_DEFAULT_SHAPE = ("label"|"rectangle"|...)
' !$MDI_DEFAULT_COLOR = (""|"red"|"#FF0000"|...)
' !$MDI_DEFAULT_STEREOTYPE = (""|"*"|"MDIElement"|...)
' ----------------------------------------------------------
' uses the default value which can be defined via the $MDI_DEFAULT_...=.... calls
' ($MDI_DEFAULT_LABEL, $MDI_DEFAULT_SHAPE, $MDI_DEFAULT_COLOR, $MDI_DEFAULT_STEREOTYPE)
!global $MDI_DEFAULT = "uSe_DeFaUlT"

' if no label is given don't use a label at all 
!global $MDI_LABEL_NONE = ""
' if no label is given use the alias as label 
!global $MDI_LABEL_ALIAS = "*"
!global $MDI_DEFAULT_LABEL ?= $MDI_LABEL_ALIAS

' don't display a shape (eg. rectangle) at all, around the sprite and the label
!global $MDI_SHAPE_NONE = "label"
' can be any other plantuml element too (like database, package, folder, etc.)
!global $MDI_DEFAULT_SHAPE ?= "rectangle"

!global $MDI_COLOR_NONE = ""
' can be any html color name or hex value like "red", "#FF0000", etc.
!global $MDI_DEFAULT_COLOR ?= $MDI_COLOR_NONE

' don't add a stereotype
!global $MDI_STEREOTYPE_NONE = ""
' add a stereotype with the name of the used sprite
!global $MDI_STEREOTYPE_SPRITE = "*" 
' can be any other concrete stereotype too like $MDI_DEFAULT_STEREOTYPE = "MDIElement"
' all used stereotypes/styles must be explicitly defined 
!global $MDI_DEFAULT_STEREOTYPE ?= $MDI_STEREOTYPE_NONE

' don't change the alignment of the text (label + stereotype)
!global $MDI_ALIGNMENT_NONE = ""
' left alignment of the text (label + stereotype)
!global $MDI_ALIGNMENT_LEFT = "left"
' center alignment of the text (label + stereotype)
!global $MDI_ALIGNMENT_CENTER = "center"
' right alignment of the text (label + stereotype)
!global $MDI_ALIGNMENT_RIGHT = "right"
!global $MDI_ALIGNMENT ?= $MDI_ALIGNMENT_CENTER

!if ($MDI_ALIGNMENT != $MDI_ALIGNMENT_NONE)
  skinparam defaultTextAlignment $MDI_ALIGNMENT
!endif
```

TODO: add samples from _examples_, ...


## Spites in combination with other standard libraries (e.g. C4-PlantUML)

The sprites can be used in combination with other standard libraries like C4-PlantUML too.
The usage is similar to the above described usage without additional dependencies and the sprites can be used directly as $sprite argument of the C4-PlantUML elements.

**Material sprites in combination with C4-PlantUML:**

```plantuml
@startuml

!include <C4/C4_Container>

' No common.puml loaded, text alignment remains left
'   If common.puml should be included (which is typically not required in C4 context) 
'   then any text alignment of ../common.puml can be avoided via `!$MDI_ALIGNMENT=""` 
'   before the include
' !$MDI_ALIGNMENT=""
' !include <material7.4.47/common>

!include <material7.4.47/AccountUser/AccountAlert>
!include <material7.4.47/Emoji/all>

package "Image sample\nwith C4" {

  Person(A, $sprite="mdiAccountAlert", $label="Person A")
  Person(B, $sprite="mdiEmoticonNeutral", $label="Person B")

  Rel(A, B, "Calls")
}

@enduml
```

[![PleaseOpenLinkIfImageCannotBeDisplayed](https://www.plantuml.com/plantuml/svg/TP1DQzn038Rl_XNA46W2mNinvD8DSTnG5fgbbFPMIBHZRJojHkFEo4cNqlzUkKcRZv2JZUPztaVej2XcdI9NrLuGprDFyBPh5brpsILH34BvfAhUm3g1Jp4cgKTB0oVigJy2fPyAoE5Q8eb2fcX80QQj6WImsZx1of0cxc53S2_huU1s27w0K41tO_38l0D9SzMFAMGB186kiHQPNOVmjrO74a3PFVUdBTJruh7Heymwl4bXjjq4XAkz_Ojtgylsm-hz-k9y_NdftDLTxOQsAPEr_z_FnY_JDdmuLaIb792FwwPkZk_TTh9N4wttQHBzKYZ_-s-PifwyZfp7z2qiaDbwH_JVyPh0hUByAHX7fgzo6tImEpduLLK07ocN90Vj4UoNCGUbfOjzU6npziIu8Lwwkp2qxl21F7jAsW8QV98rJPgHNy9d1XlzYNYM6kqwP2us_LrLfoJz5FaF)](https://www.plantuml.com/plantuml/uml/TP1DQzn038Rl_XNA46W2mNinvD8DSTnG5fgbbFPMIBHZRJojHkFEo4cNqlzUkKcRZv2JZUPztaVej2XcdI9NrLuGprDFyBPh5brpsILH34BvfAhUm3g1Jp4cgKTB0oVigJy2fPyAoE5Q8eb2fcX80QQj6WImsZx1of0cxc53S2_huU1s27w0K41tO_38l0D9SzMFAMGB186kiHQPNOVmjrO74a3PFVUdBTJruh7Heymwl4bXjjq4XAkz_Ojtgylsm-hz-k9y_NdftDLTxOQsAPEr_z_FnY_JDdmuLaIb792FwwPkZk_TTh9N4wttQHBzKYZ_-s-PifwyZfp7z2qiaDbwH_JVyPh0hUByAHX7fgzo6tImEpduLLK07ocN90Vj4UoNCGUbfOjzU6npziIu8Lwwkp2qxl21F7jAsW8QV98rJPgHNy9d1XlzYNYM6kqwP2us_LrLfoJz5FaF)

**Material sprites in combination with C4-PlantUML and its new `!NEW_C4_STYLE = 1`**

```plantuml
@startuml

' Activate new C4 style
!NEW_C4_STYLE = 1
' (stdlib <C4/C4_Container> does not yet support new C4 style, it has to be included via orig URL)
!include https://raw.githubusercontent.com/plantuml-stdlib/C4-PlantUML/master/C4_Container.puml

' No common.puml loaded, text alignment remains left
'   If common.puml should be included (which is typically not required in C4 context) 
'   then any text alignment of ../common.puml can be avoided via `!$MDI_ALIGNMENT=""` 
'   before the include
' !$MDI_ALIGNMENT=""
' !include <material7.4.47/common>

!include <material7.4.47/AccountUser/AccountAlert>
!include <material7.4.47/Emoji/all>

!$text="Image sample\nwith C4 and new Style (!NEW"+"_C4_STYLE = 1)"
package "$text" {

  Person(A, $sprite="mdiAccountAlert", $label="Person A")
  Person(B, $sprite="mdiEmoticonNeutral", $label="Person B")

  Rel(A, B, "Calls")
}

@enduml
```

[![PleaseOpenLinkIfImageCannotBeDisplayed](https://www.plantuml.com/plantuml/svg/TP9zQnf14CR_lqynMOGgDNSKX429aekL8gY4l50A1JFUZTws-tBTdTDAwNVlhYyqrv3_bjdvpVFCmznuHiUDLado3lA2vHQPmD0EnWFml5UKN2mcNvRZmVBXyUji0aFu43gxdaibLt0z7cJXQsmDepJaHb1Qyc0imvuOV5FNrd5hNXyaGuKUsCAAG9f2DIMLi9K8riaDFDtFUid5gGuLS-q_Pfd3NRgHN3MhnfChWY0PJWkhirgXYHjS7Zq5FvTtiVGqdsKQFPDhMKphqxOB2m7NrXmge2m66trW-iM0IcwC3WhWI0VCWw8r1mXWkcvXlhADAbkRT7ULB2gG7dXVomALsXyISVIpaIuqI1FJY3i4hHuSnd951j3i_zUtQqZJxALYWIRAuTRASsxF5vtvf-aodqq_B-QJnUDGYEVZs1MjhQCu_Umlb5ztn-8vyMkDJ4wYkae7wU3gf3rAaZSxygAmJGZSapk_SqMEHsyZ4ss_omoLYdCxSUUXc6hS47ZKjQ9lPYUvYZ6XAG_tyn2F1xhn6iLxqJh8daXgB7v4MXnc2VYT901tvBmrtRmF7LyxoJGKkfGl7OhmfN15QYYEpP2BtZ_ojaq6qomBQnRKi4Fr6huDSA3lIKNHG8inAkL3zK-It90f6wt-0W00)](https://www.plantuml.com/plantuml/uml/TP9zQnf14CR_lqynMOGgDNSKX429aekL8gY4l50A1JFUZTws-tBTdTDAwNVlhYyqrv3_bjdvpVFCmznuHiUDLado3lA2vHQPmD0EnWFml5UKN2mcNvRZmVBXyUji0aFu43gxdaibLt0z7cJXQsmDepJaHb1Qyc0imvuOV5FNrd5hNXyaGuKUsCAAG9f2DIMLi9K8riaDFDtFUid5gGuLS-q_Pfd3NRgHN3MhnfChWY0PJWkhirgXYHjS7Zq5FvTtiVGqdsKQFPDhMKphqxOB2m7NrXmge2m66trW-iM0IcwC3WhWI0VCWw8r1mXWkcvXlhADAbkRT7ULB2gG7dXVomALsXyISVIpaIuqI1FJY3i4hHuSnd951j3i_zUtQqZJxALYWIRAuTRASsxF5vtvf-aodqq_B-QJnUDGYEVZs1MjhQCu_Umlb5ztn-8vyMkDJ4wYkae7wU3gf3rAaZSxygAmJGZSapk_SqMEHsyZ4ss_omoLYdCxSUUXc6hS47ZKjQ9lPYUvYZ6XAG_tyn2F1xhn6iLxqJh8daXgB7v4MXnc2VYT901tvBmrtRmF7LyxoJGKkfGl7OhmfN15QYYEpP2BtZ_ojaq6qomBQnRKi4Fr6huDSA3lIKNHG8inAkL3zK-It90f6wt-0W00)


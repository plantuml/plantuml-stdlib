# plantuml-stdlib
Contains official Standard Library for PlantUML

This Standard Library is included in official release of PlantUML.
Following the C convention for "C standard library" (see https://en.wikipedia.org/wiki/C_standard_library )

## AWS library

The AWS library consists of Amazon AWS icons, it provides icons of two different sizes.

Use it by including the file that contains the sprite, eg: `!include <aws/Storage/AmazonS3/AmazonS3>`.
When imported, you can use the sprite as normally you would, using `<$sprite_name>`.

You may also include the `common.puml` file, eg: `!include <aws/common>`, which contains helper macros defined.
With the `common.puml` imported, you can use the `NAME_OF_SPRITE(parameters...)` macro.

Example of usage:
```
@startuml
!include <aws/common>
!include <aws/Storage/AmazonS3/AmazonS3>
!include <aws/Storage/AmazonS3/bucket/bucket>

AMAZONS3(s3_internal)
AMAZONS3(s3_partner,"Vendor's S3")
s3_internal <- s3_partner
@enduml
```

This example renders the following image:

![Example](http://www.plantuml.com/plantuml/png/SoWkIImgAStDuLBCp4lEAKr9LR19B2_MJyxFpStFiqCJ3Ix9BqfCJzLtp4sioiyBDeOp22fCAatEJYs1KdPSN8w-Zb7-Vi766iN6yPbv9Qb5UOavcYYY1K1tvQKMwIY5fUQbv1Uf5oi46ojfSY6fLx3HLK0ev780gWDw1000 "Example")


## Devicons and Font Awesome library

These two library consists respectively of Devicons and Font Awesome libraries of icons.

Use it by including the file that contains the sprite, eg: `!include <font-awesome/align_center>`.
When imported, you can use the sprite as normally you would, using `<$sprite_name>`.

You may also include the `common.puml` file, eg: `!include <font-awesome/common>`, which contains helper macros defined.
With the `common.puml` imported, you can use the `NAME_OF_SPRITE(parameters...)` macro.

Example of usage:
```
@startuml
!include <font-awesome/common>
!include <font-awesome/server>
!include <font-awesome/database>

title Styling example

FA_SERVER(web1,web1) #Green
FA_SERVER(web2,web2) #Yellow
FA_SERVER(web3,web3) #Blue
FA_SERVER(web4,web4) #YellowGreen

FA_DATABASE(db1,LIVE,database,white) #RoyalBlue
FA_DATABASE(db2,SPARE,database) #Red

db1 <--> db2

web1 <--> db1
web2 <--> db1
web3 <--> db1
web4 <--> db1
@enduml
```

This example renders the following image:
![Example](http://www.plantuml.com/plantuml/png/TP5VIyCm5CNVyodkyC49hMNjdcIKTTZ9OC9eTU2JfCjr2wH9QMwj-_OcpZdxi9V0oU-SS_DdRectrDMAtS96SKslpskm99IiG9iUfDwfJg25rSxeS6aqGTcqaj2Ee4I40r5Z7w9eB-dGLVSxKqVKDLoqITJuEjSSIX_zSD7GdgACZNvxOVQpptkqfiR8nMkZqslOOlk9xLKiEF6AMqmP8qaAeQIZadeF-CNhHY5Zo-ozp8jjNenxh2Q1N-xWvgb5r4CMUnOxzePAcNu84myJ1nUgmo6QUZJzovsQlUKnUyaMMPcFXPkxNctpu7pQe3-uDt6HmXovEbV-yyT1kScAIy1RKJ3ccc0MXYau2sF-BcSvyIeUg6IWfXV5vgY5__zl "Example")

## Google Material Icons

This library consists of a free Material style icons from Google and other artists.

Use it by including the file that contains the sprite, eg: `!include <material/ma_folder_move>`.
When imported, you can use the sprite as normally you would, using `<$ma_sprite_name>`.
Notice that this library requires an `ma_` preffix on sprites names, this is to avoid clash of names if multiple sprites have the same name on different libraries.

You may also include the `common.puml` file, eg: `!include <material/common>`, which contains helper macros defined.
With the `common.puml` imported, you can use the `MA_NAME_OF_SPRITE(parameters...)` macro, note again the use of the prefix `MA_`.

Example of usage:
```
@startuml
!include <material/common>
' To import the sprite file you DON'T need to place a prefix!
!include <material/folder_move>

MA_FOLDER_MOVE(Red, 2, dir, rectangle, "A label")
@enduml
```

This example renders the following image:
![Example](http://www.plantuml.com/plantuml/png/xPTXJzim48Q_-rVS8YIe557QCMgXK15O8Y71G8nj7xOfyXgNHYHnvQIC2l7VvpXncZ1AxGt4f94lLOzxxbxxtjXqFyi9psT9ZB8fZt8AQma9nYmEAGyITatXwpQ-sSRTtivtk4LRXiyhy8n0p_MR3ru5z3pVroKauFg-DY41tzSdbW6_FmTzGy3tUeQ0xve2_afWO0fi3Ko1UuG6q2PqW1QX1JI9fK3R30DJO47e0ZMX3IX27y2603QKX0qNZGq7XmqdZGsjWGtDXmtjZGrV86pwYYuz15pJOwRlQHra_QN7_SFr-w-twBy4t27q9gJZAALmuWR-wV5xxpmuEVtiRGGZ5ZFUWLNFnxFpemilEFjqiR4o5zh_kjbGDNbSWqZBHYIcdUVKK3VHqo8-IHoHhBDCY_UXq58csQ8MD6hPpwEb-3uXqyO-FQ6MklYVQHAVCVb1uqv3Kt2yiKnJcMnhQygkedHAE4cWJ3yWewjBpcPfU5W0ZjDkC1p2buduBO5GTnqLQp_Zx3eAQIOQZZZB86SCACcYU0ua3A7yvP51b48OZSUKqpGNgMbAUORMkqZMsTHy47B3C0DILOMVKJu1kIuWgUXLh0oXjo3NG_EYXTH8Wmg0YmbLSCwaGF1kTY6PLnbLUbcpbQv2HVev3LNTecKxKOMAnAtDTqX_TM8xb4CQhwKIRzUwzi0wYCMOBBjM848zgmEEJDYj4eVEce9lPTfUbNutTBfb1Z8Tm26df9WdIK4Qa8OW9Xhbyt8OzMZaFCG4ACldKp6KdGU68WjKcrSLKI60UZ0ofBPRLcmHYoQBKIv2gyVVwAHh0J2n0JIiK2QNMbkBK26LF6B9bANYZt_bdkBmekgSK9jdWxwXRALMVEDqb9FqCgR2PbNKChQR1MAwQl2EepGDdyI7yk3JzUFWHVqefRRzeaADJZBqlpjON7KFUhYwvbxGpD-ual6OhU_TzMB7RUZPuZ8KTt4jmmRBBHrWTT0-JSDP4ly2 "Example")

## Notes

When mixing sprites macros with other elements you may get a syntax error if, for example, trying to add a rectangle along with classes.
In those cases, add `{` and `}` after the macro to create the empty rectangle.

Example of usage:
```
@startuml
!include <material/common>
' To import the sprite file you DON'T need to place a prefix!
!include <material/folder_move>

MA_FOLDER_MOVE(Red, 2, dir, rectangle, "A label") {
}

class foo {
    bar
}
@enduml
```

This example renders the following image:
![Example](http://www.plantuml.com/plantuml/png/xPVHJjim58Rl_HHd5H9KYYZj6BLGg0WiaIR1G8nj5ziKcSIb4KbSEIcZGhpx72TE4qQflO6OD7nJvVHynxzz_jZjVfOJdi-I66LJ7kKKrXBYZraSKkudx9h2jsryiurx_PqVS8ks3CShy8n0t_6Q0wy2-gxdwH8IS3nFcv20v-aJou3Vny704F3SlY7WEQQ0jn8OcW9RGrFW7g41j0aTe4Le0KrYAT0smz0KM12wG4re0uhG1x0XW0qbOSD5OyF6OSDEOqDhO4FpOKDxOyCN29k-eaiF0SVqc1cuMWVPOEbn_t3zmUij-Yy1TmYz2UauIYaSExvtSlJUFVEFJxwu6tx0OiQxi6fyEZtxSExwfv_FDrRcGlkdcmNL98zh46bPG6BQVKuDzIHwMiGdYIEITPTfSJyMMieaIzI2HYthURGKpnCoRUpJ4sgfY_-P9l4PamiQTnkQ_AEDPPhAP4jRKtOLfLF2IG9b-W49hYuvcwNXOG7OTdk2qGY-JiH_2OHwwwXO-obdrr58Cp5Xm5a6EMD0IHR5So1X2EK_ZmoY5C9eFAQSfhb8JLFACxJUGxBEfkP0o0d334XL5Nv6-GJake2aOgvYPGYz1RaUcXTJI8tKhm0udr05vqmA1FTc5v9vbL6bbpLRwIfKf9_HKDKjfcmdgb2HkBNv3kclJco7SaZZjLJYhLhN7dGEOj6cZbKh4A5-fmksJDYj4aVsce9lPTfUbNutidjb1Z9jm26dfEWdIK4Qa8OWEXhbyx8PTMjaFqG7ACldKz6KdGUQ8WjKcrSLKI60kZ4ofBPRLcmHYqaMhLo4LhU_CPEk1S341j2mGfbSQcqjGeLKSi2IAKl5bt_bdkBmekgSK9jdWRwXBALMF74Qv2IzZAcmMHNj6Dkj0wAxgl6shJIDdiI7yk3JzUFmHVqefRRzeaADcMJeVtUmkEeUz71rpRsWcRzp9UCnMz-xwyMEMz2tn6Kexk9QXWKTftI047HR_BGCOf9buafcuh7Oqml2HNYVfk4iYNy1 "Example")


## Sources

* **aws**: made by https://github.com/milo-minderbinder/AWS-PlantUML
* **devicons** and **font-awesome**: made by https://github.com/tupadr3/plantuml-icon-font-sprites
* **Material Icons**: from https://github.com/Templarian/MaterialDesign

You can create Pull Request to update or add some library here if you find it relevant.

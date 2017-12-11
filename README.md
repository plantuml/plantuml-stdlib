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

MA_FOLDER_MOVE(Red, 1, dir, rectangle, "A label")
@enduml
```

This example renders the following image:
![Example](http://www.plantuml.com/plantuml/png/PSn12i8m40NGVK_nsqqL0k9U2eNMbRLGYjiIawa69faGKz7RUm3V0LxfWk7D4avUPqfEyy68znAQeiOiS3vAoiXFmYicbmchOy9NDdJZjPuHY2oo8B8s18sOQ7MViYZ_urNOKbgylAafYg5TpkEbwwTb66_zRYAhS5ImBYaaCbc71vD2rOBrdRZQ_m00 "Example")

## Notes

When mixing sprites macros with other elements you may get a syntax error if, for example, trying to add a rectangle along with classes.
In those cases, add `{` and `}` after the macro to create the empty rectangle.

Example of usage:
```
@startuml
!include <material/common>
' To import the sprite file you DON'T need to place a prefix!
!include <material/folder_move>

MA_FOLDER_MOVE(Red, 1, dir, rectangle, "A label") {
}

class foo {
    bar
}
@enduml
```

This example renders the following image:
![Example](http://www.plantuml.com/plantuml/png/ROz1Yi9044NtVOgl6sSW8Eu7KT1PJGm4SHlAxeesL7U5IaUKOUu-EO1_zzxYmT-DXQnCITmYPYzJO7mbAcoHPEqr9SrRjy9P4TEWLb3kZ76mM1Xz5CPB9noQq-gCp1nG58EGPn06upu-5-_2lKfWwv8-UEjSlU--cv_3iUtgvdByQ3bKs5G8qIeO-qBv9bnXkOVGbMNvYL_tvvmN6aVqgZDYtfLirZlEORxp3m00 "Example")


## Sources

* **aws**: made by https://github.com/milo-minderbinder/AWS-PlantUML
* **devicons** and **font-awesome**: made by https://github.com/tupadr3/plantuml-icon-font-sprites
* **Material Icons**: from https://github.com/Templarian/MaterialDesign

You can create Pull Request to update or add some library here if you find it relevant.

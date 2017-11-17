# plantuml-stdlib
Contains official Standard Library for PlantUML

This Standard Library is included in official release of PlantUML.
Following the C convention for "C standard library" (see https://en.wikipedia.org/wiki/C_standard_library ), it allows users to do something like

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

Currently, it's composed of the following libraries:

* **aws**: made by https://github.com/milo-minderbinder/AWS-PlantUML
* **devicons** and **font-awesome**: made by https://github.com/tupadr3/plantuml-icon-font-sprites

You can create Pull Request to update or add some library here if you find it relevant.

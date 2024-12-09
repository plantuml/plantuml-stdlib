# plantuml-stdlib
Contains official Standard Library for PlantUML.
_See http://plantuml.com/stdlib for more information._

This Standard Library is included in official release of PlantUML.
Following the C convention for "C standard library" _(see https://en.wikipedia.org/wiki/C_standard_library)_

## Amazon Web Services

### AWS Icons

PlantUML images, sprites, macros, and other includes for Amazon Web Services (AWS) services and resources. Used to create PlantUML diagrams with AWS components. All elements are generated from the official [AWS Architecture Icons](https://aws.amazon.com/architecture/icons/) and when combined with [PlantUML](http://plantuml.com/) and the [C4 model](https://c4model.com/), are a great way to communicate your design, deployment, and topology as code.

Besides usage as custom sprites on PlantUML components, different types of diagrams can quickly and easily be created with the icons.

More info on [awslabs github account](https://github.com/awslabs/aws-icons-for-plantuml).

### AWS library [aws]

The AWS library consists of Amazon AWS icons, it provides icons of two different sizes.

Use it by including the file that contains the sprite, eg: `!include <aws/Storage/AmazonS3/AmazonS3>`.
When imported, you can use the sprite as normally you would, using `<$sprite_name>`.

You may also include the `common.puml` file, eg: `!include <aws/common>`, which contains helper macros defined.
With the `common.puml` imported, you can use the `NAME_OF_SPRITE(parameters...)` macro.

Example of usage:
```plantuml
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

### Amazon Labs AWS Library [awslib]

_Todo: TBC..._

## Azure library [azure]

The Azure library consists of Microsoft Azure icons.

Use it by including the file that contains the sprite, eg: `!include <azure/Analytics/AzureEventHub.puml>`.
When imported, you can use the sprite as normally you would, using `<$sprite_name>`.

You may also include the `AzureCommon.puml` file, eg: `!include <azure/AzureCommon.puml>`, which contains helper macros defined.
With the `azure/AzureCommon.puml` imported, you can use the `NAME_OF_SPRITE(parameters...)` macro.

Example of usage:
```plantuml
@startuml
    !include <azure/AzureCommon>
    !include <azure/Analytics/AzureEventHub>
    !include <azure/Analytics/AzureStreamAnalyticsJob>
    !include <azure/Databases/AzureCosmosDb>

    left to right direction

    agent "Device Simulator" as devices #fff

    AzureEventHub(fareDataEventHub, "Fare Data", "PK: Medallion HackLicense VendorId; 3 TUs")
    AzureEventHub(tripDataEventHub, "Trip Data", "PK: Medallion HackLicense VendorId; 3 TUs")
    AzureStreamAnalyticsJob(streamAnalytics, "Stream Processing", "6 SUs")
    AzureCosmosDb(outputCosmosDb, "Output Database", "1,000 RUs")

    devices --> fareDataEventHub
    devices --> tripDataEventHub
    fareDataEventHub --> streamAnalytics
    tripDataEventHub --> streamAnalytics
    streamAnalytics --> outputCosmosDb
@enduml
```

This example renders the following image:

![Example](http://www.plantuml.com/plantuml/proxy?idx=0&src=https%3A%2F%2Fraw.githubusercontent.com%2FRicardoNiepel%2FAzure-PlantUML%2Fmaster%2Fsamples%2FBasic%2520usage%2520-%2520Stream%2520processing%2520with%2520Azure%2520Stream%2520Analytics.puml "Example")

## C4 library (C4-PlantUML) [C4]

The C4 library enables a simple way of describing and communicate software architectures with an intuitive language.

It is the PlantUML integrated version of [C4-PlantUML](https://github.com/plantuml-stdlib/C4-PlantUML) and has the big advantage that it can be used without additional external includes.
(E.g. container diagrams can be drawn with `!include <C4/C4_Container>` and no `!include https://raw.githubusercontent.com/plantuml-stdlib/C4-PlantUML/master/C4_Container.puml` is required.)

Example of usage:

```plantuml
@startuml
!include <C4/C4_Container>
LAYOUT_LEFT_RIGHT()

Person(admin, "Administrator")
System_Boundary(c1, "Sample System") {
    Container(web_app, "Web Application", "C#, ASP.NET Core 2.1 MVC", "Allows users to compare multiple Twitter timelines")
}
System(twitter, "Twitter")

Rel(admin, web_app, "Uses", "HTTPS")
Rel(web_app, twitter, "Gets tweets from", "HTTPS")

SHOW_LEGEND()
@enduml
```

This example renders the following image:

[![Example](https://www.plantuml.com/plantuml/png/JL1TQy9047o_Nx5DNn8GYyN7KanJgmMhOivAdyAPRE7WFiBT1f7I_zvDjTfxMUvcPcTk9f5KeCuQSQDTRRe6uQ4OtnNZgl2Eb7OO7iKY_rXjPRMOliXgypgRopGJOeqXUfUgncetW2JlfuuK5FcGPA8yHa9RFVdEDIeSqth4f5BPrY2Si2I3Bm5yBaxf0VULQbjcxd0FUTiQNIlItYNyLDmE82_Nm-LKiYGWt0z7yFPUz5XkZ3z4w2A62EIXzhPLJB6T8TrRoeCcmW2aBHhsYXpn-nmofHF8Uyuq1iK6pT_dhh6saPKyvrAkooJx9LtGwvePKkGhzkCpUFjV8ihvQiTTpgRBP-vnWgxX-dy0)](https://www.plantuml.com/plantuml/uml/JL1TQy9047o_Nx5DNn8GYyN7KanJgmMhOivAdyAPRE7WFiBT1f7I_zvDjTfxMUvcPcTk9f5KeCuQSQDTRRe6uQ4OtnNZgl2Eb7OO7iKY_rXjPRMOliXgypgRopGJOeqXUfUgncetW2JlfuuK5FcGPA8yHa9RFVdEDIeSqth4f5BPrY2Si2I3Bm5yBaxf0VULQbjcxd0FUTiQNIlItYNyLDmE82_Nm-LKiYGWt0z7yFPUz5XkZ3z4w2A62EIXzhPLJB6T8TrRoeCcmW2aBHhsYXpn-nmofHF8Uyuq1iK6pT_dhh6saPKyvrAkooJx9LtGwvePKkGhzkCpUFjV8ihvQiTTpgRBP-vnWgxX-dy0)

## Classy library [classy]

The Classy library allows for using an Object Oriented approach to diagramming
in PlantUML.

That is to say that it allows you to define and instantiate your own classes as
well as allow you to call methods defined on those classes. You can also inherit
from one or more classes where desired.

Use it by including the file that contains the Classy class that you want to use
within your diagram. Alternatively, if you want to define your own type, you can
just include the `core.puml` file, eg: `!include <classy/core>`, which contains
all of the necessary functions.

Example of usage:
```plantuml
@startuml

    !include <classy/core>

    $class(HelloWorld)
        $classVar(msg, string, "Hello World!")

        $classMethod(getMessage)
            !function HelloWorld__getMessage($this)
                !return $getInstanceVar($this, 'msg')
            !endfunction
        $endclassMethod(getMessage)

        $classMethod(setMessage)
            !function HelloWorld__setMessage($this, $args)
                $setInstanceVar($this, 'msg', $call($args, 'each'))
                !return $this
            !endfunction
        $endclassMethod(setMessage)
        $endclass(HelloWorld)

        !$hello = $new(HelloWorld)
        Alice -> Bob : $call($hello, 'getMessage')

        $call($hello, 'setMessage', array($new(array), '2nd message!'), $void=%true())
        Alice -> Bob : $call($hello, 'getMessage')

@enduml
```

This example renders the following image:

![Example](http://www.plantuml.com/plantuml/png/SoWkIImgAStDuNBAJrBGjLDmpCbCJbMmKl18pSd9LmZFByf9KKINCyfBKSXDBIvEJ4zLKEHoICrB0He00000)

## Classy C4 library [classy-c4]

The Classy C4 library combines the Classy and C4 libraries by defining Classy
classes that wrap the C4 macros.

Example of usage:
```plantuml
@startuml

    !include <classy-c4/container>
    !include <classy-c4/person>
    !include <classy-c4/system>

    !$system = $new(System)
    $call($system, 'setName', 'Label', $void=%true())
    $call($system, 'setDescription', 'Optional Description', $void=%true())

    !$person = $new(Person)
    $call($person, 'setName', 'Label', $void=%true())
    $call($person, 'setDescription', 'Optional Description', $void=%true())

    !$container = $new(Container)
    $call($container, 'setName', 'Label', $void=%true())
    $call($container, 'setDescription', 'Optional Description', $void=%true())
    $call($container, 'setTechnology', 'Technology', $void=%true())

    !$personAlias = $call($person, 'render')
    !$containerAlias = $call($container, 'render')
    $call($system, 'render', $void=%true())

    Rel($personAlias, $containerAlias, "Label", "Optional Technology")

@enduml
```

This example renders the following image:

![Example](http://www.plantuml.com/plantuml/png/ZOvFIyGm4CNl-HIrfowupMLFdbQgjnKN_vnbcWxRm6GICXDalxtPWaKHnTDxCypxpTkBGjOIg1bsR_U40Ld5N7bsL2PiPjKaDzPcUEzFNkSo5i7i8YkozYu6cmZuaj-AJkH7E-osnylgzU5W0uXYjfKyr0HunjodUclC4RD4xj8Yj-H1hfls02DIMyrZKXyPgBb3STalKxinAwHpd-v7z0NTp97YwVm7wFaiYg6JHVxxtJmXVI-yjlWTyQNEnkoHfnBe0m00)

## Edgy Enterprise Design [edgy]
Edgy is an Open Source tool for collaborative Enterprise Design. The library is an adaption of the EDGY Tools for PlantUML usage.

The Sprites are based on the [Enterprise Design with EDGY](https://www.enterprise.design/). They'll be rendered as SVG sprites, so you need a quite actual PlantUML distribution.

Use it by including the file that contains the sprite, eg: `!include <edgy/edgy>`.
There are two visual sets:
* `!include <edgy/edgy>` is the visualization according to the open source set of edgy.
* `!include <edgy/edgy2>` is the one which describes with stereotypes what kind of element it is. If you have color blinded people, this one could be the better one.
All procedures are compatible between both sets. So you can begin with one and switch to the other just to have a look which of the one fits visually the best.

The library is not complete yet, but it's a good start.

In general, the procedures for elements and facets do have the form of
```plantuml
$elementorfacet("label", alias, lighterColor)
```
* ```"label"``` is the name of the element or facet. It is mandatory.
* ```alias``` is used for linking elements and facets. It is optional.
* ```lighterColor``` is used as a boolean value (!) to use a lighter color for the element or facet. It is optional.
  * Facets are drawn with lighter colors than Elements as default.
  * ```0``` is false. Any other value is true.  Or use ```%true()``` and ```%false()``` (see [Boolean expression in Preprocessing](https://plantuml.com/de/preprocessing#55eb000153a81c72)).

### Facets
Facets do have the edgy facet name followed with ```Facet``` as tail, to distinguish them from elements.
The following facets are implemented: ```$baseFacet```,```$identityFacet```,```$brandFacet```,```$experienceFacet```,```$productFacet```,```$architectureFacet```,```$organisationFacet```

### Elements
Elements do have the edgy element name.
The following elements are implemented:
Base: ```$people```,```$outcome```,```$activity```,```$object```
Identity: ```$purpose```,```$story```,```$content```
Brand: ```$brand```
Experience: ```$task```,```$channel```,```$journey```
Product: ```$product```
Architecture: ```$capability```,```$asset```,```$process```
Organisation: ```$organisation```

Elements can be nested into facets:
```plantuml
$architectureFacet("Architecture") {
  $capability("Capability")
}
```

However, it is also possible to nest elements if needed:
```plantuml
$capability("Customer Interaction") {
    $capability("Passenger Services", pass, 1) {
        $capability("Passenger Information", info)
    }
}
```

### Links
Links do have the form of
```plantuml
$link(fromAlias, toAlias, "label")
```
* ```fromAlias``` is the starting element or facet. It is mandatory.
* ```toAlias``` is the target element or facet. It is mandatory.
* ```"label""``` is a label written on the connection. This is optional.

There are only three sort of links:
* ```$link``` is a unidirectional link between elements or facets.
* ```$flow``` is a directed flow between elements or facets.
* ```$tree``` is a hierarchical representation of elements as a tree relationship.

Generally, all links do have the possibility to give the rendere a hint in which direction the link should be drawn. This is done by adding ```Up```, ```Down```, ```Left```, ```Right``` as a tail to the link name (e.g. ```$linkUp```, ```$linkDown```, ```$linkLeft```, ```$linkRight```).

### Concrete example of usage:
```plantuml
@startuml
    !include <edgy/edgy>

    $identityFacet("This is the Identity facet", identity) {
        $content(" This is the Content element", content)
        $purpose("This is the Purpose element", purpose)
        $story("This is the Story element", story)
    }

    $link(content, purpose)
    $link(content, story)
    $link(purpose, story)
@enduml
```

## EIP-PlantUML [eip]

EIP-PlantUML provides [Enterprise Integrations Patterns](https://www.enterpriseintegrationpatterns.com/) elements to [PlantUML](http://plantuml.com/) to provide easy support of designing EIP architectures for both, up-front design as well as development-time automated documentation generation.
Example of usage:
```plantuml
@startuml
    !include <eip/EIP-PlantUML>
    !theme mars
    skinparam linetype ortho
    left to right direction
    folder "RabbitMQ" #line.dashed {
        Message(msg0, Message)
        Message(msg1, Message)
        Message(msg2, Message)
        MsgChannel(ch0, inQueue)
        MsgChannel(ch1, outQueue)
        MsgChannel(ch2, outQueue)
        MessageRouter(rt0, Router)
    }
    Send(msg0, ch0)
    Send(ch0, rt0)
    Send(rt0, msg1)
    Send(rt0, msg2)
    Send(msg1, ch1)
    Send(msg2, ch2)
@enduml
```
This example renders the following image:
![Example](https://cdn-0.plantuml.com/plantuml/png/VP3FIiD04CRlFiMsNbgmYURegI0U10DjnGVOPAVPnVqJPcOH4T_TtOrY2x5p29dVzsM-cMo99N9qzcfXV6-Z0g6PHxglQvJlrM1OnouIO1yyW-Ug3wuUhVJvcmsnigQh7vzscrrch-rpxQJnjJB4PxWQSuP8jM0D3eIJIAMdD-D7YT89QppmnmWY8EjGH0j75Xm4ca6pK0QXPnDyqOx1Aa2nFCYkCzpkb-8wJwYK90rAV6PJgHQ8v00hHyFjpM-tdfFlBilDd4p3WvRUWrtrEeqtVXyX_cT80I7o9KSpwvXI3qa1N26ddEbzidoLvmjuzNDZscJznyfUoAUeZCW7px3cX4t_9ErzpfhCac-R-kZiDm00)

## Elastic library [elastic]

The Elastic library consists of [Elastic](https://www.elastic.co) icons.
It is similar in use to the AWS and Azure libraries (it used the same tool to create them).

Use it by including the file that contains the sprite, eg: `!include <elastic/elasticsearch/elasticsearch.puml>`.
When imported, you can use the sprite as normally you would, using `<$sprite_name>`.

You may also include the `common.puml` file, eg: `!include <elastic/common>`, which contains helper macros defined.
With the `common.puml` imported, you can use the `NAME_OF_SPRITE(parameters...)` macro.

Example of usage:
```plantuml
@startuml
    !include <elastic/common>
    !include <elastic/elasticsearch/elasticsearch>
    !include <elastic/logstash/logstash>
    !include <elastic/kibana/kibana>

    ELASTICSEARCH(ElasticSearch, "Search and Analyze",database)
    LOGSTASH(Logstash, "Parse and Transform",node)
    KIBANA(Kibana, "Visualize",agent)

    Logstash -right-> ElasticSearch: Transformed Data
    ElasticSearch -right-> Kibana: Data to View

@enduml
```

This example renders the following image:
![Example](http://www.plantuml.com/plantuml/png/TOxFQiCm38VlUGejfnHITYyZrEl2MXgsCOVUrLXBpFm7R8UnFVrI9oNa41yi6N-VVjhxW2xqMYKmd0Tf6jKBWYTIw8Di7XkhjJN5okzKFQ5hkkLhJL6szG5zTszMmMzvHODJAP98bHNZzUd0I_PvE6RbIFAObqCwDe1603EeVlyepGK6lAAdJVIhzrTUCtxCgYbyi3xGUOfIxT3uB-jqcXih9kLyUcPlB3l7DGRy8dsFIjvcOqicR21YyRfFXQsJRHUs1InMtCq99E050qPhmSpgcBYB70GB5qa_IR8d8tgj_W40)

## Google Cloud Platform [gcp]

```plantuml
@startuml
    !include <gcp/GCPCommon>
    !include <gcp/Compute/Cloud_Functions>
    !include <gcp/Networking/Cloud_Firewall_Rules>
    !include <gcp/Compute/Compute_Engine>
    !include <gcp/Storage/Cloud_Storage>

    Cloud_Functions(Cloud_FunctionsStart, "Start Server", "Cloud Functions")
    Cloud_Functions(Cloud_FunctionsStop, "Stop Server", "Cloud Functions")
    Cloud_Functions(Cloud_FunctionAdd, "Add a Friend", "Cloud Functions")
    Compute_Engine(Compute_Engine, "MineCraft Server", "Compute Engine")
    Cloud_Storage(Cloud_Storage, "MineCraft Backups", "Cloud Storage")
    Cloud_Firewall_Rules(Cloud_Firewall_Rules_Starter,"Minecraft Backups", "Cloud Firewall Rules")
    Cloud_Firewall_Rules(Cloud_Firewall_Rules_Friend,"Minecraft Backups", "Cloud Firewall Rules")
@enduml
```
This example renders the following image:

![Example](http://cdn-0.plantuml.com/plantuml/png/lP9FJm8n4CNlV8hAQKs0tZsX6_4YXaWyR-fsN1hwPpETojTttBgQ4X9335wOFFBUxz4Nbf4KKd9sScLyQvC6iIFguusKg0wBpj0klQO8s0PFu6dH1YThL747sXclvSOgJo_hnxbXHvm_r1lfL2H0gKqaoReEpWM_w2zNmWrz8f2r3Katg-HRCkoxPEKJq27WtlXkR3K81sLjyvmin7zvRRxDlU-CXqisRYcWwiPzlrGkd4oEHg-Ez9OKqanCXoksWE-0KzQ3JNpRfjU_WqBFkS_p1yojrfpYJw742WruVPfIZbkLaXDhFhpWM_6aR1BPn91YhwfG1U9EjVlKnvzVaaqZulZFLPtwiXa61fmDtFOqTuo88N8MFezr7dt9YUJi1m00)

## K8S [k8s]

These are the PlantUML sprites, macros and stereotypes for creating PlantUML diagrams with the Kubernetes components. The official Kubernetes Icons Set (where this work is based) can be found here
This repo is heavily influenced by the awesome work from Ricardo Niepel on Azure-PlantUML
Example of usage:
```plantuml
@startuml
    !include <k8s/Common>
    !include <k8s/Simplified>
    !include <k8s/OSS/all>
    footer Kubernetes Plant-UML
    scale max 1024 width
    skinparam {
        nodesep 10
        ranksep 10
    }
    actor "User" as userAlias
    left to right direction
    Cluster_Boundary(cluster, "Kubernetes Cluster") {
        Namespace_Boundary(ns, "Web") {
            KubernetesSvc(svc, "service", "")
            KubernetesPod(pod1, "web-pod1", "")
            KubernetesPod(pod2, "web-pod2", "")
        }
    }
    Rel(userAlias,svc,"get HTTP/1.1 index.html", "1")
    Rel(svc,pod1,"load Balances to Pods", "2")
    Rel(svc,pod2,"load Balances to Pods", "2")
    Rel_U(pod1, svc, "serves content", "3")
    Rel(svc, userAlias, "return content to", "4")
@enduml
```
This example renders the following image:

![Example](http://cdn-0.plantuml.com/plantuml/png/ZPDFYzim4CNl-HIpFcLX4zVk7afFszrBeNyQcWqzXeaqYKNqnqYZ9ANiTw_aDR6RKXeTZ9ZtUyFe3Nu8Z9wZqPCRILjb2VRl0ZJCRNXVbXwFyvtY9cvY82-SPR8y5ywKKc10LkK-RiXROWgpXKRBgo-VIuE1oPTI1Pv0EZVA2XqbHQzpzv5d_UICSNRUvW7-Yrgc4r_5BfLfjTegal_6losNvQUpPNaGrw8BD-gwTOx9mw12Ze5dAOTE3m8rWS4JL6_gUpWgoSshi5UsHOy6VasWFzP92jGcz5poQFUZqaltHS7EGx4Av0l007av7xJ2qAcQjWpimAjTmo2L9y7AsKvxqZ7lPlteefNeVqx5Q-4EYj4JUggu7Sts5Gs55WKDNXkIxGTj1gu_VyGw3GUHk43-e0GLwLhSNj93ijFMoIenHzhCylKQlXxu-e9_cGpVxwIdvwJkybJ5ZXW-FZylocfUWRAIJlE6ZSvDgkBsxCfiDrUX7KfuH8rMKCWXfn52nkk_yVewVBtgtppabDZ-NylisulMmxgJw8cZjpsUrvuTzydnG5P6ety3)


## Tupadr3 library [tupadr3]

This library contains several libraries of icons (including Devicons and Font Awesome )

Use it by including the file that contains the sprite, eg: `!include <tupadr3/font-awesome/align_center>`.
When imported, you can use the sprite as normally you would, using `<$sprite_name>`.

You may also include the `common.puml` file, eg: `!include <tupadr3/common>`, which contains helper macros defined.
With the `common.puml` imported, you can use the `NAME_OF_SPRITE(parameters...)` macro.

Example of usage:
```plantuml
@startuml
    !include <tupadr3/common>
    !include <tupadr3/font-awesome/server>
    !include <tupadr3/font-awesome/database>

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

![Example](http://www.plantuml.com/plantuml/png/XOvHIyCm58NVyolkyC49hMNjdcICTTZ9OC9eTU2JfCjr2wH9QMwj-_ScpbagWY-1mtU-axkmn1jgAyMkOQkufkV73LWIIfQWJGTIxrKhqC9wRtIuCfgWg1j9Q4TG8CAHgBPtKNIGT6pBsxsf8cfhBfeagjsSNmwbLz-S6jgpojZeUnTcbxOpAwFdVv0latTeJOMHnUOTctzhWXClkSKvOoH98HHqKb8V03zuLIjaR9M-5bc-o_9nX-KayCyDN3qqY7h8OizYnrvGATCDOU9Xuk1IjJX4Ku-cFzvvsLVkqwTqcHRPMBX_D-jT5bok3RgZ97HARavS-SbV_JWejcdU2xwAWZ6t1BCmd8EhCDPX7oS-nOEK3DAqJmlKegtK9m00 "Example")

## Google Material Icons [material]

This library consists of a free Material style icons from Google and other artists.

Use it by including the file that contains the sprite, eg: `!include <material/folder_move>`.
When imported, you can use the sprite as normally you would, using `<$ma_sprite_name>`.
Notice that this library requires an `ma_` preffix on sprites names, this is to avoid clash of names if multiple sprites have the same name on different libraries.

You may also include the `common.puml` file, eg: `!include <material/common>`, which contains helper macros defined.
With the `common.puml` imported, you can use the `MA_NAME_OF_SPRITE(parameters...)` macro, note again the use of the prefix `MA_`.

Example of usage:
```plantuml
@startuml
    !include <material/common>
    ' To import the sprite file you DON'T need to place a prefix!
    !include <material/folder_move>

    MA_FOLDER_MOVE(Red, 1, dir, rectangle, "A label")
@enduml
```

This example renders the following image:

![Example](http://www.plantuml.com/plantuml/png/PSn12i8m40NGVK_nsqqL0k9U2eNMbRLGYjiIawa69faGKz7RUm3V0LxfWk7D4avUPqfEyy68znAQeiOiS3vAoiXFmYicbmchOy9NDdJZjPuHY2oo8B8s18sOQ7MViYZ_urNOKbgylAafYg5TpkEbwwTb66_zRYAhS5ImBYaaCbc71vD2rOBrdRZQ_m00 "Example")

## Domain Story library (DomainStory-PlantUML) [DomainStory]

This library provides a set of macros to easily describe and document a domain story which was developed in
a [Domain Storytelling](http://www.domainstorytelling.org) workshop.

For more usage instructions see [DomainStory-PlantUML](https://github.com/johthor/DomainStory-PlantUML).

Example of usage:

```plantuml
@startuml
    !include <DomainStory/domainStory>

    Boundary(System) {
        Person(Alice)
        Conversation(weather)
        Person(Bob)
    }
    activity(1, Alice, talks about the, weather, with, Bob)
@enduml
```

This example renders the following image:

![Example](http://www.plantuml.com/plantuml/png/JSx1IWGn30RWUv-YtcPWdEBLqxfwL5XOV81C9zXg9rdQlxiCuhlR7GGtf_0bVyYkW3BgainT59_gp3O0f_BeNARB-14HwbGBPwy25enU5_Uf0K6pUz65eXoXURq_91AylxswAxdvVpAUhjVDNglCbDVkk1RmqjjlOriTE1ULxYb5p_qcpohdXeJO_CA4mBc_tTthr9iVOyWZdYFMxc6mMtwmeFHLB4rQOH4Q_ELR4n46kqLtB7DxwHS0
 "Example")

## Notes

When mixing sprites macros with other elements you may get a syntax error if, for example, trying to add a rectangle along with classes.
In those cases, add `{` and `}` after the macro to create the empty rectangle.

Example of usage:
```plantuml
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
* **classy**: made by https://github.com/james-gadrow-kr/classy-plantuml
* **classy-c4**: made by https://github.com/james-gadrow-kr/classy-c4
* **tupadr3**: made by https://github.com/tupadr3/plantuml-icon-font-sprites
* **Material Icons**: from https://github.com/Templarian/MaterialDesign
* **Elastic Icons**: from https://github.com/Crashedmind/PlantUML-Elastic-icons
* **Domain Story**: from https://github.com/johthor/DomainStory-PlantUML

You can create Pull Request to update or add some library here if you find it relevant.

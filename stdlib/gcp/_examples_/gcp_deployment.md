```plantuml
@startuml
!theme sunlust
!include <archimate/Archimate>
!include <gcp/GCPCommon>
!include <gcp/Compute/Cloud_Functions>
!include <gcp/Networking/Cloud_Firewall_Rules>
!include <gcp/Compute/Compute_Engine>
!include <gcp/Storage/Cloud_Storage>
!include <awslib/AWSSimplified>
!include <awslib/all>
!include <material/common>
!include <material/timer>

skinparam linetype ortho

package "Kid / Owner" {
    Users(Users, "Friends", " ")
    Client(Client, "Kid / Owner", " ")
}

package "MinecraftClients" {
    Client(ClientMinecraft, "", " ")
}

package "Minecraft Project" {

    together {
        Cloud_Functions(Cloud_FunctionsStart, "Start Server", "Cloud Functions")
        Cloud_Functions(Cloud_FunctionsStop, "Stop Server", "Cloud Functions")
        Cloud_Functions(Cloud_FunctionAdd, "Add a Friend", "Cloud Functions")
    }
    Compute_Engine(Compute_Engine, "MineCraft Server", "Compute Engine")
    Cloud_Storage(Cloud_Storage, "MineCraft Backups", "Cloud Storage")
    together {
        Cloud_Firewall_Rules(Cloud_Firewall_Rules_Starter,"Starter FW Entries", "Cloud Firewall Rules")
        Cloud_Firewall_Rules(Cloud_Firewall_Rules_Friend,"Friend FW Entries", "Cloud Firewall Rules")
    }

    EntityColoring("Backup")
    Entity("Backup", "Backup","Cron Task", "darkgrey", "ma_timer", "Backup")
}

Cloud_FunctionsStart -[hidden]d-> Cloud_FunctionsStop
Cloud_FunctionsStop -[hidden]d-> Cloud_FunctionAdd
Cloud_FunctionsStart -d-> Cloud_Firewall_Rules_Starter
Cloud_FunctionAdd -d-> Cloud_Firewall_Rules_Friend
Cloud_Firewall_Rules_Friend -[hidden]d-> Cloud_Firewall_Rules_Starter
Cloud_FunctionsStart -> Compute_Engine
Cloud_FunctionsStop -> Compute_Engine
Compute_Engine -d-> Cloud_Storage

Client -r-> Cloud_FunctionsStart
Client -r-> Cloud_FunctionsStop
Users -r-> Cloud_FunctionAdd
ClientMinecraft -r-> Cloud_Firewall_Rules_Friend
Backup -u-> Compute_Engine
@enduml
```

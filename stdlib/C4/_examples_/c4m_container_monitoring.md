```plantuml
@startuml
!theme sunlust
!include <office/Concepts/windows_powershell>
!include <office/Concepts/application_web>
!include <office/Concepts/application_generic>
!include <office/Concepts/properties>
!include <C4/C4_Container>
hide stereotype
left to right direction
left header [[https://c4model.com C4M: Container Diagram]]
caption Feature 'AutoSupport'

System_Boundary(app, "Application") {
    Container(service, 'Application', 'Any', 'Any supporting service', $sprite='application_generic')
    Container(exporter, 'Exporter', 'Go', 'Mertric exporter', $sprite='application_generic')
}
System_Boundary(mon, "Monitoring") {
    Container(grafana, 'Grafana', 'Go', 'Visualization web application', $sprite='application_web')
    ContainerDb(prometheus, 'Metrics', 'Prometheus', 'Storage metrics', $sprite='properties')
}
System_Boundary(itsm, "CICD") {
    Container(agent, 'Agent', 'Java', 'CICD worker', $sprite='windows_powershell')
    Container(jenkins, 'Jenkins', 'Java', 'CICD control', $sprite='application_web')
}
System_Boundary(scm, "SCM") {
    Container(git, 'Gitea', 'Go', 'Source controle manager', $sprite='application_web')
}
Rel_U(grafana, prometheus, 'Metrics')
Rel_R(prometheus, exporter, 'Metrics')
Rel_L(service,  exporter, 'Volumes')
Rel_R(grafana, jenkins, 'Metrics')
Rel_R(jenkins,   agent,  'Run')
Rel(jenkins,   git,  'Get script')
Rel_U(agent,   service,  'Suport')
@enduml
```

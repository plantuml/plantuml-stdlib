---
name: gcp2026
display_name: Google Cloud Platform (2026)
description: Official Google Cloud architecture and product icons for PlantUML
author: Google Cloud / PlantUML Stdlib Contributors
version: 2026.11
release: 2026-11-17
license: Apache-2.0
source: https://github.com/plantuml/plantuml-stdlib
origin: https://cloud.google.com/icons
uid: e83723128ddd86f0581b2a0b97c7e78c145b83c0
---

# Google Cloud Platform (`gcp2026`) Standard Library

Contains PlantUML sprites, macros, and stereotypes for creating architecture diagrams with official [Google Cloud Icons](https://cloud.google.com/icons).
All icons are discovered and generated automatically from Google Cloud's official icon archives without manual category sorting.

## Usage Example

```plantuml
@startuml
!include <gcp2026/GCPCommon>
!include <gcp2026/CloudRun>
!include <gcp2026/CloudStorage>
!include <gcp2026/CloudSQL>

GCPEntityColored(api, "Cloud Run", "Runs containerised workloads") {
  CloudRun(service, "API Service", "us-central1")
}

CloudStorage(bucket, "Asset Bucket", "Standard storage")
CloudSQL(db, "Orders DB", "PostgreSQL 15")

service --> bucket
service --> db
@enduml
```

### Rendering Modes
- **Default (Full-color PNG)**: Uses `$<Service>IMG()` automatically when calling service macros.
- **Full-color Vector SVG Sprites**: Define `!$GCP_USE_SVG = true` (or `!define GCP_USE_SVG`) before including `<gcp2026/GCPCommon>`, or reference `<$<Service>>` directly.
- **Monochrome Grayscale `[64x64/16z]` Sprites**: Reference `<$<Service>_png>` directly.
- **CamelCase & Snake_Case Support**: Both `CloudRun(...)` and `Cloud_Run(...)` macros are supported.

## Icon Catalogue

| Include Path | CamelCase Macro / Sprite | Snake_Case Alias | Official Source Archive |
|---|---|---|---|
| `!include <gcp2026/AIHub>` | `AIHub` (`<$AIHub>`) | `AI_Hub` | `google-cloud-legacy-icons.zip` |
| `!include <gcp2026/AIHypercomputer>` | `AIHypercomputer` (`<$AIHypercomputer>`) | `AI_Hypercomputer` | `core-products-icons.zip` |
| `!include <gcp2026/AIMachineLearning>` | `AIMachineLearning` (`<$AIMachineLearning>`) | `AI_Machine_Learning` | `category-icons.zip` |
| `!include <gcp2026/AIPlatform>` | `AIPlatform` (`<$AIPlatform>`) | `AI_Platform` | `google-cloud-legacy-icons.zip` |
| `!include <gcp2026/AIPlatformUnified>` | `AIPlatformUnified` (`<$AIPlatformUnified>`) | `AI_Platform_Unified` | `google-cloud-legacy-icons.zip` |
| `!include <gcp2026/API>` | `API` (`<$API>`) | — | `google-cloud-legacy-icons.zip` |
| `!include <gcp2026/APIAnalytics>` | `APIAnalytics` (`<$APIAnalytics>`) | `API_Analytics` | `google-cloud-legacy-icons.zip` |
| `!include <gcp2026/APIMonetization>` | `APIMonetization` (`<$APIMonetization>`) | `API_Monetization` | `google-cloud-legacy-icons.zip` |
| `!include <gcp2026/AccessContextManager>` | `AccessContextManager` (`<$AccessContextManager>`) | `Access_Context_Manager` | `google-cloud-legacy-icons.zip` |
| `!include <gcp2026/Administration>` | `Administration` (`<$Administration>`) | — | `google-cloud-legacy-icons.zip` |
| `!include <gcp2026/AdvancedAgentModeling>` | `AdvancedAgentModeling` (`<$AdvancedAgentModeling>`) | `Advanced_Agent_Modeling` | `google-cloud-legacy-icons.zip` |
| `!include <gcp2026/AdvancedSolutionsLab>` | `AdvancedSolutionsLab` (`<$AdvancedSolutionsLab>`) | `Advanced_Solutions_Lab` | `google-cloud-legacy-icons.zip` |
| `!include <gcp2026/AgentAssist>` | `AgentAssist` (`<$AgentAssist>`) | `Agent_Assist` | `google-cloud-legacy-icons.zip` |
| `!include <gcp2026/Agents>` | `Agents` (`<$Agents>`) | — | `category-icons.zip` |
| `!include <gcp2026/AlloyDB>` | `AlloyDB` (`<$AlloyDB>`) | `Alloy_DB` | `core-products-icons.zip` |
| `!include <gcp2026/AnalyticsHub>` | `AnalyticsHub` (`<$AnalyticsHub>`) | `Analytics_Hub` | `google-cloud-legacy-icons.zip` |
| `!include <gcp2026/Anthos>` | `Anthos` (`<$Anthos>`) | — | `core-products-icons.zip` |
| `!include <gcp2026/AnthosConfigManagement>` | `AnthosConfigManagement` (`<$AnthosConfigManagement>`) | `Anthos_Config_Management` | `google-cloud-legacy-icons.zip` |
| `!include <gcp2026/AnthosServiceMesh>` | `AnthosServiceMesh` (`<$AnthosServiceMesh>`) | `Anthos_Service_Mesh` | `google-cloud-legacy-icons.zip` |
| `!include <gcp2026/Apigee>` | `Apigee` (`<$Apigee>`) | — | `core-products-icons.zip` |
| `!include <gcp2026/ApigeeAPIPlatform>` | `ApigeeAPIPlatform` (`<$ApigeeAPIPlatform>`) | `Apigee_API_Platform` | `google-cloud-legacy-icons.zip` |
| `!include <gcp2026/ApigeeSense>` | `ApigeeSense` (`<$ApigeeSense>`) | `Apigee_Sense` | `google-cloud-legacy-icons.zip` |
| `!include <gcp2026/AppEngine>` | `AppEngine` (`<$AppEngine>`) | `App_Engine` | `google-cloud-legacy-icons.zip` |
| `!include <gcp2026/ArtifactRegistry>` | `ArtifactRegistry` (`<$ArtifactRegistry>`) | `Artifact_Registry` | `google-cloud-legacy-icons.zip` |
| `!include <gcp2026/AssetInventory>` | `AssetInventory` (`<$AssetInventory>`) | `Asset_Inventory` | `google-cloud-legacy-icons.zip` |
| `!include <gcp2026/AssuredWorkloads>` | `AssuredWorkloads` (`<$AssuredWorkloads>`) | `Assured_Workloads` | `google-cloud-legacy-icons.zip` |
| `!include <gcp2026/Automl>` | `Automl` (`<$Automl>`) | — | `google-cloud-legacy-icons.zip` |
| `!include <gcp2026/AutomlNaturalLanguage>` | `AutomlNaturalLanguage` (`<$AutomlNaturalLanguage>`) | `Automl_Natural_Language` | `google-cloud-legacy-icons.zip` |
| `!include <gcp2026/AutomlTables>` | `AutomlTables` (`<$AutomlTables>`) | `Automl_Tables` | `google-cloud-legacy-icons.zip` |
| `!include <gcp2026/AutomlTranslation>` | `AutomlTranslation` (`<$AutomlTranslation>`) | `Automl_Translation` | `google-cloud-legacy-icons.zip` |
| `!include <gcp2026/AutomlVideoIntelligence>` | `AutomlVideoIntelligence` (`<$AutomlVideoIntelligence>`) | `Automl_Video_Intelligence` | `google-cloud-legacy-icons.zip` |
| `!include <gcp2026/AutomlVision>` | `AutomlVision` (`<$AutomlVision>`) | `Automl_Vision` | `google-cloud-legacy-icons.zip` |
| `!include <gcp2026/BareMetalSolutions>` | `BareMetalSolutions` (`<$BareMetalSolutions>`) | `Bare_Metal_Solutions` | `google-cloud-legacy-icons.zip` |
| `!include <gcp2026/Batch>` | `Batch` (`<$Batch>`) | — | `google-cloud-legacy-icons.zip` |
| `!include <gcp2026/Beyondcorp>` | `Beyondcorp` (`<$Beyondcorp>`) | — | `google-cloud-legacy-icons.zip` |
| `!include <gcp2026/BigQuery>` | `BigQuery` (`<$BigQuery>`) | `Big_Query` | `core-products-icons.zip` |
| `!include <gcp2026/Bigquery>` | `Bigquery` (`<$Bigquery>`) | — | `google-cloud-legacy-icons.zip` |
| `!include <gcp2026/Bigtable>` | `Bigtable` (`<$Bigtable>`) | — | `google-cloud-legacy-icons.zip` |
| `!include <gcp2026/Billing>` | `Billing` (`<$Billing>`) | — | `google-cloud-legacy-icons.zip` |
| `!include <gcp2026/BinaryAuthorization>` | `BinaryAuthorization` (`<$BinaryAuthorization>`) | `Binary_Authorization` | `google-cloud-legacy-icons.zip` |
| `!include <gcp2026/BusinessIntelligence>` | `BusinessIntelligence` (`<$BusinessIntelligence>`) | `Business_Intelligence` | `category-icons.zip` |
| `!include <gcp2026/Catalog>` | `Catalog` (`<$Catalog>`) | — | `google-cloud-legacy-icons.zip` |
| `!include <gcp2026/CertificateAuthorityService>` | `CertificateAuthorityService` (`<$CertificateAuthorityService>`) | `Certificate_Authority_Service` | `google-cloud-legacy-icons.zip` |
| `!include <gcp2026/CertificateManager>` | `CertificateManager` (`<$CertificateManager>`) | `Certificate_Manager` | `google-cloud-legacy-icons.zip` |
| `!include <gcp2026/CloudAPIGateway>` | `CloudAPIGateway` (`<$CloudAPIGateway>`) | `Cloud_API_Gateway` | `google-cloud-legacy-icons.zip` |
| `!include <gcp2026/CloudAPIs>` | `CloudAPIs` (`<$CloudAPIs>`) | `Cloud_APIs` | `google-cloud-legacy-icons.zip` |
| `!include <gcp2026/CloudArmor>` | `CloudArmor` (`<$CloudArmor>`) | `Cloud_Armor` | `google-cloud-legacy-icons.zip` |
| `!include <gcp2026/CloudAssetInventory>` | `CloudAssetInventory` (`<$CloudAssetInventory>`) | `Cloud_Asset_Inventory` | `google-cloud-legacy-icons.zip` |
| `!include <gcp2026/CloudAuditLogs>` | `CloudAuditLogs` (`<$CloudAuditLogs>`) | `Cloud_Audit_Logs` | `google-cloud-legacy-icons.zip` |
| `!include <gcp2026/CloudBuild>` | `CloudBuild` (`<$CloudBuild>`) | `Cloud_Build` | `google-cloud-legacy-icons.zip` |
| `!include <gcp2026/CloudCDN>` | `CloudCDN` (`<$CloudCDN>`) | `Cloud_CDN` | `google-cloud-legacy-icons.zip` |
| `!include <gcp2026/CloudCode>` | `CloudCode` (`<$CloudCode>`) | `Cloud_Code` | `google-cloud-legacy-icons.zip` |
| `!include <gcp2026/CloudComposer>` | `CloudComposer` (`<$CloudComposer>`) | `Cloud_Composer` | `google-cloud-legacy-icons.zip` |
| `!include <gcp2026/CloudDNS>` | `CloudDNS` (`<$CloudDNS>`) | `Cloud_DNS` | `google-cloud-legacy-icons.zip` |
| `!include <gcp2026/CloudDataFusion>` | `CloudDataFusion` (`<$CloudDataFusion>`) | `Cloud_Data_Fusion` | `google-cloud-legacy-icons.zip` |
| `!include <gcp2026/CloudDeploy>` | `CloudDeploy` (`<$CloudDeploy>`) | `Cloud_Deploy` | `google-cloud-legacy-icons.zip` |
| `!include <gcp2026/CloudDeploymentManager>` | `CloudDeploymentManager` (`<$CloudDeploymentManager>`) | `Cloud_Deployment_Manager` | `google-cloud-legacy-icons.zip` |
| `!include <gcp2026/CloudDomains>` | `CloudDomains` (`<$CloudDomains>`) | `Cloud_Domains` | `google-cloud-legacy-icons.zip` |
| `!include <gcp2026/CloudEKM>` | `CloudEKM` (`<$CloudEKM>`) | `Cloud_EKM` | `google-cloud-legacy-icons.zip` |
| `!include <gcp2026/CloudEndpoints>` | `CloudEndpoints` (`<$CloudEndpoints>`) | `Cloud_Endpoints` | `google-cloud-legacy-icons.zip` |
| `!include <gcp2026/CloudExternalIPAddresses>` | `CloudExternalIPAddresses` (`<$CloudExternalIPAddresses>`) | `Cloud_External_IP_Addresses` | `google-cloud-legacy-icons.zip` |
| `!include <gcp2026/CloudFirewallRules>` | `CloudFirewallRules` (`<$CloudFirewallRules>`) | `Cloud_Firewall_Rules` | `google-cloud-legacy-icons.zip` |
| `!include <gcp2026/CloudForMarketing>` | `CloudForMarketing` (`<$CloudForMarketing>`) | `Cloud_For_Marketing` | `google-cloud-legacy-icons.zip` |
| `!include <gcp2026/CloudFunctions>` | `CloudFunctions` (`<$CloudFunctions>`) | `Cloud_Functions` | `google-cloud-legacy-icons.zip` |
| `!include <gcp2026/CloudGPU>` | `CloudGPU` (`<$CloudGPU>`) | `Cloud_GPU` | `google-cloud-legacy-icons.zip` |
| `!include <gcp2026/CloudGeneric>` | `CloudGeneric` (`<$CloudGeneric>`) | `Cloud_Generic` | `google-cloud-legacy-icons.zip` |
| `!include <gcp2026/CloudHSM>` | `CloudHSM` (`<$CloudHSM>`) | `Cloud_HSM` | `google-cloud-legacy-icons.zip` |
| `!include <gcp2026/CloudHealthcareAPI>` | `CloudHealthcareAPI` (`<$CloudHealthcareAPI>`) | `Cloud_Healthcare_API` | `google-cloud-legacy-icons.zip` |
| `!include <gcp2026/CloudHealthcareMarketplace>` | `CloudHealthcareMarketplace` (`<$CloudHealthcareMarketplace>`) | `Cloud_Healthcare_Marketplace` | `google-cloud-legacy-icons.zip` |
| `!include <gcp2026/CloudIDS>` | `CloudIDS` (`<$CloudIDS>`) | `Cloud_IDS` | `google-cloud-legacy-icons.zip` |
| `!include <gcp2026/CloudInferenceAPI>` | `CloudInferenceAPI` (`<$CloudInferenceAPI>`) | `Cloud_Inference_API` | `google-cloud-legacy-icons.zip` |
| `!include <gcp2026/CloudInterconnect>` | `CloudInterconnect` (`<$CloudInterconnect>`) | `Cloud_Interconnect` | `google-cloud-legacy-icons.zip` |
| `!include <gcp2026/CloudJobsAPI>` | `CloudJobsAPI` (`<$CloudJobsAPI>`) | `Cloud_Jobs_API` | `google-cloud-legacy-icons.zip` |
| `!include <gcp2026/CloudLoadBalancing>` | `CloudLoadBalancing` (`<$CloudLoadBalancing>`) | `Cloud_Load_Balancing` | `google-cloud-legacy-icons.zip` |
| `!include <gcp2026/CloudLogging>` | `CloudLogging` (`<$CloudLogging>`) | `Cloud_Logging` | `google-cloud-legacy-icons.zip` |
| `!include <gcp2026/CloudMediaEdge>` | `CloudMediaEdge` (`<$CloudMediaEdge>`) | `Cloud_Media_Edge` | `google-cloud-legacy-icons.zip` |
| `!include <gcp2026/CloudMonitoring>` | `CloudMonitoring` (`<$CloudMonitoring>`) | `Cloud_Monitoring` | `google-cloud-legacy-icons.zip` |
| `!include <gcp2026/CloudNAT>` | `CloudNAT` (`<$CloudNAT>`) | `Cloud_NAT` | `google-cloud-legacy-icons.zip` |
| `!include <gcp2026/CloudNaturalLanguageAPI>` | `CloudNaturalLanguageAPI` (`<$CloudNaturalLanguageAPI>`) | `Cloud_Natural_Language_API` | `google-cloud-legacy-icons.zip` |
| `!include <gcp2026/CloudNetwork>` | `CloudNetwork` (`<$CloudNetwork>`) | `Cloud_Network` | `google-cloud-legacy-icons.zip` |
| `!include <gcp2026/CloudOps>` | `CloudOps` (`<$CloudOps>`) | `Cloud_Ops` | `google-cloud-legacy-icons.zip` |
| `!include <gcp2026/CloudOptimizationAI>` | `CloudOptimizationAI` (`<$CloudOptimizationAI>`) | `Cloud_Optimization_AI` | `google-cloud-legacy-icons.zip` |
| `!include <gcp2026/CloudOptimizationAIFleetRoutingAPI>` | `CloudOptimizationAIFleetRoutingAPI` (`<$CloudOptimizationAIFleetRoutingAPI>`) | `Cloud_Optimization_AI_Fleet_Routing_API` | `google-cloud-legacy-icons.zip` |
| `!include <gcp2026/CloudRouter>` | `CloudRouter` (`<$CloudRouter>`) | `Cloud_Router` | `google-cloud-legacy-icons.zip` |
| `!include <gcp2026/CloudRoutes>` | `CloudRoutes` (`<$CloudRoutes>`) | `Cloud_Routes` | `google-cloud-legacy-icons.zip` |
| `!include <gcp2026/CloudRun>` | `CloudRun` (`<$CloudRun>`) | `Cloud_Run` | `core-products-icons.zip` |
| `!include <gcp2026/CloudRunForAnthos>` | `CloudRunForAnthos` (`<$CloudRunForAnthos>`) | `Cloud_Run_For_Anthos` | `google-cloud-legacy-icons.zip` |
| `!include <gcp2026/CloudSQL>` | `CloudSQL` (`<$CloudSQL>`) | `Cloud_SQL` | `core-products-icons.zip` |
| `!include <gcp2026/CloudScheduler>` | `CloudScheduler` (`<$CloudScheduler>`) | `Cloud_Scheduler` | `google-cloud-legacy-icons.zip` |
| `!include <gcp2026/CloudSecurityScanner>` | `CloudSecurityScanner` (`<$CloudSecurityScanner>`) | `Cloud_Security_Scanner` | `google-cloud-legacy-icons.zip` |
| `!include <gcp2026/CloudShell>` | `CloudShell` (`<$CloudShell>`) | `Cloud_Shell` | `google-cloud-legacy-icons.zip` |
| `!include <gcp2026/CloudSpanner>` | `CloudSpanner` (`<$CloudSpanner>`) | `Cloud_Spanner` | `core-products-icons.zip` |
| `!include <gcp2026/CloudStorage>` | `CloudStorage` (`<$CloudStorage>`) | `Cloud_Storage` | `core-products-icons.zip` |
| `!include <gcp2026/CloudTPU>` | `CloudTPU` (`<$CloudTPU>`) | `Cloud_TPU` | `google-cloud-legacy-icons.zip` |
| `!include <gcp2026/CloudTasks>` | `CloudTasks` (`<$CloudTasks>`) | `Cloud_Tasks` | `google-cloud-legacy-icons.zip` |
| `!include <gcp2026/CloudTestLab>` | `CloudTestLab` (`<$CloudTestLab>`) | `Cloud_Test_Lab` | `google-cloud-legacy-icons.zip` |
| `!include <gcp2026/CloudTranslationAPI>` | `CloudTranslationAPI` (`<$CloudTranslationAPI>`) | `Cloud_Translation_API` | `google-cloud-legacy-icons.zip` |
| `!include <gcp2026/CloudVPN>` | `CloudVPN` (`<$CloudVPN>`) | `Cloud_VPN` | `google-cloud-legacy-icons.zip` |
| `!include <gcp2026/CloudVisionAPI>` | `CloudVisionAPI` (`<$CloudVisionAPI>`) | `Cloud_Vision_API` | `google-cloud-legacy-icons.zip` |
| `!include <gcp2026/Collaboration>` | `Collaboration` (`<$Collaboration>`) | — | `category-icons.zip` |
| `!include <gcp2026/Compute>` | `Compute` (`<$Compute>`) | — | `category-icons.zip` |
| `!include <gcp2026/ComputeEngine>` | `ComputeEngine` (`<$ComputeEngine>`) | `Compute_Engine` | `core-products-icons.zip` |
| `!include <gcp2026/ConfigurationManagement>` | `ConfigurationManagement` (`<$ConfigurationManagement>`) | `Configuration_Management` | `google-cloud-legacy-icons.zip` |
| `!include <gcp2026/ConnectivityTest>` | `ConnectivityTest` (`<$ConnectivityTest>`) | `Connectivity_Test` | `google-cloud-legacy-icons.zip` |
| `!include <gcp2026/Connectors>` | `Connectors` (`<$Connectors>`) | — | `google-cloud-legacy-icons.zip` |
| `!include <gcp2026/ContactCenterAI>` | `ContactCenterAI` (`<$ContactCenterAI>`) | `Contact_Center_AI` | `google-cloud-legacy-icons.zip` |
| `!include <gcp2026/ContainerOptimizedOS>` | `ContainerOptimizedOS` (`<$ContainerOptimizedOS>`) | `Container_Optimized_OS` | `google-cloud-legacy-icons.zip` |
| `!include <gcp2026/ContainerRegistry>` | `ContainerRegistry` (`<$ContainerRegistry>`) | `Container_Registry` | `google-cloud-legacy-icons.zip` |
| `!include <gcp2026/Containers>` | `Containers` (`<$Containers>`) | — | `category-icons.zip` |
| `!include <gcp2026/DataAnalytics>` | `DataAnalytics` (`<$DataAnalytics>`) | `Data_Analytics` | `category-icons.zip` |
| `!include <gcp2026/DataCatalog>` | `DataCatalog` (`<$DataCatalog>`) | `Data_Catalog` | `google-cloud-legacy-icons.zip` |
| `!include <gcp2026/DataLabeling>` | `DataLabeling` (`<$DataLabeling>`) | `Data_Labeling` | `google-cloud-legacy-icons.zip` |
| `!include <gcp2026/DataLayers>` | `DataLayers` (`<$DataLayers>`) | `Data_Layers` | `google-cloud-legacy-icons.zip` |
| `!include <gcp2026/DataLossPreventionAPI>` | `DataLossPreventionAPI` (`<$DataLossPreventionAPI>`) | `Data_Loss_Prevention_API` | `google-cloud-legacy-icons.zip` |
| `!include <gcp2026/DataQnA>` | `DataQnA` (`<$DataQnA>`) | `Data_QnA` | `google-cloud-legacy-icons.zip` |
| `!include <gcp2026/DataStudio>` | `DataStudio` (`<$DataStudio>`) | `Data_Studio` | `google-cloud-legacy-icons.zip` |
| `!include <gcp2026/DataTransfer>` | `DataTransfer` (`<$DataTransfer>`) | `Data_Transfer` | `google-cloud-legacy-icons.zip` |
| `!include <gcp2026/DatabaseMigrationService>` | `DatabaseMigrationService` (`<$DatabaseMigrationService>`) | `Database_Migration_Service` | `google-cloud-legacy-icons.zip` |
| `!include <gcp2026/Databases>` | `Databases` (`<$Databases>`) | — | `category-icons.zip` |
| `!include <gcp2026/Dataflow>` | `Dataflow` (`<$Dataflow>`) | — | `google-cloud-legacy-icons.zip` |
| `!include <gcp2026/Datalab>` | `Datalab` (`<$Datalab>`) | — | `google-cloud-legacy-icons.zip` |
| `!include <gcp2026/Dataplex>` | `Dataplex` (`<$Dataplex>`) | — | `google-cloud-legacy-icons.zip` |
| `!include <gcp2026/Datapol>` | `Datapol` (`<$Datapol>`) | — | `google-cloud-legacy-icons.zip` |
| `!include <gcp2026/Dataprep>` | `Dataprep` (`<$Dataprep>`) | — | `google-cloud-legacy-icons.zip` |
| `!include <gcp2026/Dataproc>` | `Dataproc` (`<$Dataproc>`) | — | `google-cloud-legacy-icons.zip` |
| `!include <gcp2026/DataprocMetastore>` | `DataprocMetastore` (`<$DataprocMetastore>`) | `Dataproc_Metastore` | `google-cloud-legacy-icons.zip` |
| `!include <gcp2026/Datashare>` | `Datashare` (`<$Datashare>`) | — | `google-cloud-legacy-icons.zip` |
| `!include <gcp2026/Datastore>` | `Datastore` (`<$Datastore>`) | — | `google-cloud-legacy-icons.zip` |
| `!include <gcp2026/Datastream>` | `Datastream` (`<$Datastream>`) | — | `google-cloud-legacy-icons.zip` |
| `!include <gcp2026/Debugger>` | `Debugger` (`<$Debugger>`) | — | `google-cloud-legacy-icons.zip` |
| `!include <gcp2026/DevOps>` | `DevOps` (`<$DevOps>`) | `Dev_Ops` | `category-icons.zip` |
| `!include <gcp2026/DeveloperPortal>` | `DeveloperPortal` (`<$DeveloperPortal>`) | `Developer_Portal` | `google-cloud-legacy-icons.zip` |
| `!include <gcp2026/DeveloperTools>` | `DeveloperTools` (`<$DeveloperTools>`) | `Developer_Tools` | `category-icons.zip` |
| `!include <gcp2026/Dialogflow>` | `Dialogflow` (`<$Dialogflow>`) | — | `google-cloud-legacy-icons.zip` |
| `!include <gcp2026/DialogflowCx>` | `DialogflowCx` (`<$DialogflowCx>`) | `Dialogflow_Cx` | `google-cloud-legacy-icons.zip` |
| `!include <gcp2026/DialogflowInsights>` | `DialogflowInsights` (`<$DialogflowInsights>`) | `Dialogflow_Insights` | `google-cloud-legacy-icons.zip` |
| `!include <gcp2026/DistributedCloud>` | `DistributedCloud` (`<$DistributedCloud>`) | `Distributed_Cloud` | `core-products-icons.zip` |
| `!include <gcp2026/DocumentAI>` | `DocumentAI` (`<$DocumentAI>`) | `Document_AI` | `google-cloud-legacy-icons.zip` |
| `!include <gcp2026/EarlyAccessCenter>` | `EarlyAccessCenter` (`<$EarlyAccessCenter>`) | `Early_Access_Center` | `google-cloud-legacy-icons.zip` |
| `!include <gcp2026/ErrorReporting>` | `ErrorReporting` (`<$ErrorReporting>`) | `Error_Reporting` | `google-cloud-legacy-icons.zip` |
| `!include <gcp2026/Eventarc>` | `Eventarc` (`<$Eventarc>`) | — | `google-cloud-legacy-icons.zip` |
| `!include <gcp2026/Filestore>` | `Filestore` (`<$Filestore>`) | — | `google-cloud-legacy-icons.zip` |
| `!include <gcp2026/FinancialServicesMarketplace>` | `FinancialServicesMarketplace` (`<$FinancialServicesMarketplace>`) | `Financial_Services_Marketplace` | `google-cloud-legacy-icons.zip` |
| `!include <gcp2026/Firestore>` | `Firestore` (`<$Firestore>`) | — | `google-cloud-legacy-icons.zip` |
| `!include <gcp2026/FleetEngine>` | `FleetEngine` (`<$FleetEngine>`) | `Fleet_Engine` | `google-cloud-legacy-icons.zip` |
| `!include <gcp2026/FreeTrial>` | `FreeTrial` (`<$FreeTrial>`) | `Free_Trial` | `google-cloud-legacy-icons.zip` |
| `!include <gcp2026/GCESystemsManagement>` | `GCESystemsManagement` (`<$GCESystemsManagement>`) | `GCE_Systems_Management` | `google-cloud-legacy-icons.zip` |
| `!include <gcp2026/GKE>` | `GKE` (`<$GKE>`) | — | `core-products-icons.zip` |
| `!include <gcp2026/GKEOnPrem>` | `GKEOnPrem` (`<$GKEOnPrem>`) | `GKE_On_Prem` | `google-cloud-legacy-icons.zip` |
| `!include <gcp2026/GameServers>` | `GameServers` (`<$GameServers>`) | `Game_Servers` | `google-cloud-legacy-icons.zip` |
| `!include <gcp2026/Genomics>` | `Genomics` (`<$Genomics>`) | — | `google-cloud-legacy-icons.zip` |
| `!include <gcp2026/GoogleCloudMarketplace>` | `GoogleCloudMarketplace` (`<$GoogleCloudMarketplace>`) | `Google_Cloud_Marketplace` | `google-cloud-legacy-icons.zip` |
| `!include <gcp2026/GoogleKubernetesEngine>` | `GoogleKubernetesEngine` (`<$GoogleKubernetesEngine>`) | `Google_Kubernetes_Engine` | `google-cloud-legacy-icons.zip` |
| `!include <gcp2026/GoogleMapsPlatform>` | `GoogleMapsPlatform` (`<$GoogleMapsPlatform>`) | `Google_Maps_Platform` | `google-cloud-legacy-icons.zip` |
| `!include <gcp2026/HealthcareNLPAPI>` | `HealthcareNLPAPI` (`<$HealthcareNLPAPI>`) | `Healthcare_NLP_API` | `google-cloud-legacy-icons.zip` |
| `!include <gcp2026/Home>` | `Home` (`<$Home>`) | — | `google-cloud-legacy-icons.zip` |
| `!include <gcp2026/HybridAndMulticloud>` | `HybridAndMulticloud` (`<$HybridAndMulticloud>`) | `Hybrid_And_Multicloud` | `category-icons.zip` |
| `!include <gcp2026/Hyperdisk>` | `Hyperdisk` (`<$Hyperdisk>`) | — | `core-products-icons.zip` |
| `!include <gcp2026/IdentityAndAccessManagement>` | `IdentityAndAccessManagement` (`<$IdentityAndAccessManagement>`) | `Identity_And_Access_Management` | `google-cloud-legacy-icons.zip` |
| `!include <gcp2026/IdentityAwareProxy>` | `IdentityAwareProxy` (`<$IdentityAwareProxy>`) | `Identity_Aware_Proxy` | `google-cloud-legacy-icons.zip` |
| `!include <gcp2026/IdentityPlatform>` | `IdentityPlatform` (`<$IdentityPlatform>`) | `Identity_Platform` | `google-cloud-legacy-icons.zip` |
| `!include <gcp2026/IntegrationServices>` | `IntegrationServices` (`<$IntegrationServices>`) | `Integration_Services` | `category-icons.zip` |
| `!include <gcp2026/IoTCore>` | `IoTCore` (`<$IoTCore>`) | `IoT_Core` | `google-cloud-legacy-icons.zip` |
| `!include <gcp2026/IoTEdge>` | `IoTEdge` (`<$IoTEdge>`) | `IoT_Edge` | `google-cloud-legacy-icons.zip` |
| `!include <gcp2026/KeyAccessJustifications>` | `KeyAccessJustifications` (`<$KeyAccessJustifications>`) | `Key_Access_Justifications` | `google-cloud-legacy-icons.zip` |
| `!include <gcp2026/KeyManagementService>` | `KeyManagementService` (`<$KeyManagementService>`) | `Key_Management_Service` | `google-cloud-legacy-icons.zip` |
| `!include <gcp2026/Kuberun>` | `Kuberun` (`<$Kuberun>`) | — | `google-cloud-legacy-icons.zip` |
| `!include <gcp2026/Launcher>` | `Launcher` (`<$Launcher>`) | — | `google-cloud-legacy-icons.zip` |
| `!include <gcp2026/LocalSSD>` | `LocalSSD` (`<$LocalSSD>`) | `Local_SSD` | `google-cloud-legacy-icons.zip` |
| `!include <gcp2026/Looker>` | `Looker` (`<$Looker>`) | — | `core-products-icons.zip` |
| `!include <gcp2026/ManagedServiceForMicrosoftActiveDirectory>` | `ManagedServiceForMicrosoftActiveDirectory` (`<$ManagedServiceForMicrosoftActiveDirectory>`) | `Managed_Service_For_Microsoft_Active_Directory` | `google-cloud-legacy-icons.zip` |
| `!include <gcp2026/ManagementTools>` | `ManagementTools` (`<$ManagementTools>`) | `Management_Tools` | `category-icons.zip` |
| `!include <gcp2026/Mandiant>` | `Mandiant` (`<$Mandiant>`) | — | `core-products-icons.zip` |
| `!include <gcp2026/MapsAndGeospatial>` | `MapsAndGeospatial` (`<$MapsAndGeospatial>`) | `Maps_And_Geospatial` | `category-icons.zip` |
| `!include <gcp2026/Marketplace>` | `Marketplace` (`<$Marketplace>`) | — | `category-icons.zip` |
| `!include <gcp2026/MediaServices>` | `MediaServices` (`<$MediaServices>`) | `Media_Services` | `category-icons.zip` |
| `!include <gcp2026/MediaTranslationAPI>` | `MediaTranslationAPI` (`<$MediaTranslationAPI>`) | `Media_Translation_API` | `google-cloud-legacy-icons.zip` |
| `!include <gcp2026/Memorystore>` | `Memorystore` (`<$Memorystore>`) | — | `google-cloud-legacy-icons.zip` |
| `!include <gcp2026/MigrateForAnthos>` | `MigrateForAnthos` (`<$MigrateForAnthos>`) | `Migrate_For_Anthos` | `google-cloud-legacy-icons.zip` |
| `!include <gcp2026/MigrateForComputeEngine>` | `MigrateForComputeEngine` (`<$MigrateForComputeEngine>`) | `Migrate_For_Compute_Engine` | `google-cloud-legacy-icons.zip` |
| `!include <gcp2026/Migration>` | `Migration` (`<$Migration>`) | — | `category-icons.zip` |
| `!include <gcp2026/MixedReality>` | `MixedReality` (`<$MixedReality>`) | `Mixed_Reality` | `category-icons.zip` |
| `!include <gcp2026/MyCloud>` | `MyCloud` (`<$MyCloud>`) | `My_Cloud` | `google-cloud-legacy-icons.zip` |
| `!include <gcp2026/NetworkConnectivityCenter>` | `NetworkConnectivityCenter` (`<$NetworkConnectivityCenter>`) | `Network_Connectivity_Center` | `google-cloud-legacy-icons.zip` |
| `!include <gcp2026/NetworkIntelligenceCenter>` | `NetworkIntelligenceCenter` (`<$NetworkIntelligenceCenter>`) | `Network_Intelligence_Center` | `google-cloud-legacy-icons.zip` |
| `!include <gcp2026/NetworkSecurity>` | `NetworkSecurity` (`<$NetworkSecurity>`) | `Network_Security` | `google-cloud-legacy-icons.zip` |
| `!include <gcp2026/NetworkTiers>` | `NetworkTiers` (`<$NetworkTiers>`) | `Network_Tiers` | `google-cloud-legacy-icons.zip` |
| `!include <gcp2026/NetworkTopology>` | `NetworkTopology` (`<$NetworkTopology>`) | `Network_Topology` | `google-cloud-legacy-icons.zip` |
| `!include <gcp2026/Networking>` | `Networking` (`<$Networking>`) | — | `category-icons.zip` |
| `!include <gcp2026/OSConfigurationManagement>` | `OSConfigurationManagement` (`<$OSConfigurationManagement>`) | `OS_Configuration_Management` | `google-cloud-legacy-icons.zip` |
| `!include <gcp2026/OSInventoryManagement>` | `OSInventoryManagement` (`<$OSInventoryManagement>`) | `OS_Inventory_Management` | `google-cloud-legacy-icons.zip` |
| `!include <gcp2026/OSPatchManagement>` | `OSPatchManagement` (`<$OSPatchManagement>`) | `OS_Patch_Management` | `google-cloud-legacy-icons.zip` |
| `!include <gcp2026/Observability>` | `Observability` (`<$Observability>`) | — | `category-icons.zip` |
| `!include <gcp2026/Onboarding>` | `Onboarding` (`<$Onboarding>`) | — | `google-cloud-legacy-icons.zip` |
| `!include <gcp2026/Operations>` | `Operations` (`<$Operations>`) | — | `category-icons.zip` |
| `!include <gcp2026/PartnerInterconnect>` | `PartnerInterconnect` (`<$PartnerInterconnect>`) | `Partner_Interconnect` | `google-cloud-legacy-icons.zip` |
| `!include <gcp2026/PartnerPortal>` | `PartnerPortal` (`<$PartnerPortal>`) | `Partner_Portal` | `google-cloud-legacy-icons.zip` |
| `!include <gcp2026/PerformanceDashboard>` | `PerformanceDashboard` (`<$PerformanceDashboard>`) | `Performance_Dashboard` | `google-cloud-legacy-icons.zip` |
| `!include <gcp2026/Permissions>` | `Permissions` (`<$Permissions>`) | — | `google-cloud-legacy-icons.zip` |
| `!include <gcp2026/PersistentDisk>` | `PersistentDisk` (`<$PersistentDisk>`) | `Persistent_Disk` | `google-cloud-legacy-icons.zip` |
| `!include <gcp2026/PhishingProtection>` | `PhishingProtection` (`<$PhishingProtection>`) | `Phishing_Protection` | `google-cloud-legacy-icons.zip` |
| `!include <gcp2026/PolicyAnalyzer>` | `PolicyAnalyzer` (`<$PolicyAnalyzer>`) | `Policy_Analyzer` | `google-cloud-legacy-icons.zip` |
| `!include <gcp2026/PremiumNetworkTier>` | `PremiumNetworkTier` (`<$PremiumNetworkTier>`) | `Premium_Network_Tier` | `google-cloud-legacy-icons.zip` |
| `!include <gcp2026/PrivateConnectivity>` | `PrivateConnectivity` (`<$PrivateConnectivity>`) | `Private_Connectivity` | `google-cloud-legacy-icons.zip` |
| `!include <gcp2026/PrivateServiceConnect>` | `PrivateServiceConnect` (`<$PrivateServiceConnect>`) | `Private_Service_Connect` | `google-cloud-legacy-icons.zip` |
| `!include <gcp2026/ProducerPortal>` | `ProducerPortal` (`<$ProducerPortal>`) | `Producer_Portal` | `google-cloud-legacy-icons.zip` |
| `!include <gcp2026/Profiler>` | `Profiler` (`<$Profiler>`) | — | `google-cloud-legacy-icons.zip` |
| `!include <gcp2026/Project>` | `Project` (`<$Project>`) | — | `google-cloud-legacy-icons.zip` |
| `!include <gcp2026/Pubsub>` | `Pubsub` (`<$Pubsub>`) | — | `google-cloud-legacy-icons.zip` |
| `!include <gcp2026/QuantumEngine>` | `QuantumEngine` (`<$QuantumEngine>`) | `Quantum_Engine` | `google-cloud-legacy-icons.zip` |
| `!include <gcp2026/Quotas>` | `Quotas` (`<$Quotas>`) | — | `google-cloud-legacy-icons.zip` |
| `!include <gcp2026/RealWorldInsights>` | `RealWorldInsights` (`<$RealWorldInsights>`) | `Real_World_Insights` | `google-cloud-legacy-icons.zip` |
| `!include <gcp2026/RecommendationsAI>` | `RecommendationsAI` (`<$RecommendationsAI>`) | `Recommendations_AI` | `google-cloud-legacy-icons.zip` |
| `!include <gcp2026/ReleaseNotes>` | `ReleaseNotes` (`<$ReleaseNotes>`) | `Release_Notes` | `google-cloud-legacy-icons.zip` |
| `!include <gcp2026/RetailAPI>` | `RetailAPI` (`<$RetailAPI>`) | `Retail_API` | `google-cloud-legacy-icons.zip` |
| `!include <gcp2026/RiskManager>` | `RiskManager` (`<$RiskManager>`) | `Risk_Manager` | `google-cloud-legacy-icons.zip` |
| `!include <gcp2026/RuntimeConfig>` | `RuntimeConfig` (`<$RuntimeConfig>`) | `Runtime_Config` | `google-cloud-legacy-icons.zip` |
| `!include <gcp2026/SecretManager>` | `SecretManager` (`<$SecretManager>`) | `Secret_Manager` | `google-cloud-legacy-icons.zip` |
| `!include <gcp2026/Security>` | `Security` (`<$Security>`) | — | `google-cloud-legacy-icons.zip` |
| `!include <gcp2026/SecurityCommandCenter>` | `SecurityCommandCenter` (`<$SecurityCommandCenter>`) | `Security_Command_Center` | `core-products-icons.zip` |
| `!include <gcp2026/SecurityHealthAdvisor>` | `SecurityHealthAdvisor` (`<$SecurityHealthAdvisor>`) | `Security_Health_Advisor` | `google-cloud-legacy-icons.zip` |
| `!include <gcp2026/SecurityIdentity>` | `SecurityIdentity` (`<$SecurityIdentity>`) | `Security_Identity` | `category-icons.zip` |
| `!include <gcp2026/SecurityKeyEnforcement>` | `SecurityKeyEnforcement` (`<$SecurityKeyEnforcement>`) | `Security_Key_Enforcement` | `google-cloud-legacy-icons.zip` |
| `!include <gcp2026/SecurityOperations>` | `SecurityOperations` (`<$SecurityOperations>`) | `Security_Operations` | `core-products-icons.zip` |
| `!include <gcp2026/ServerlessComputing>` | `ServerlessComputing` (`<$ServerlessComputing>`) | `Serverless_Computing` | `category-icons.zip` |
| `!include <gcp2026/ServiceDiscovery>` | `ServiceDiscovery` (`<$ServiceDiscovery>`) | `Service_Discovery` | `google-cloud-legacy-icons.zip` |
| `!include <gcp2026/SpeechToText>` | `SpeechToText` (`<$SpeechToText>`) | `Speech_To_Text` | `google-cloud-legacy-icons.zip` |
| `!include <gcp2026/Stackdriver>` | `Stackdriver` (`<$Stackdriver>`) | — | `google-cloud-legacy-icons.zip` |
| `!include <gcp2026/StandardNetworkTier>` | `StandardNetworkTier` (`<$StandardNetworkTier>`) | `Standard_Network_Tier` | `google-cloud-legacy-icons.zip` |
| `!include <gcp2026/Storage>` | `Storage` (`<$Storage>`) | — | `category-icons.zip` |
| `!include <gcp2026/StreamSuite>` | `StreamSuite` (`<$StreamSuite>`) | `Stream_Suite` | `google-cloud-legacy-icons.zip` |
| `!include <gcp2026/Support>` | `Support` (`<$Support>`) | — | `google-cloud-legacy-icons.zip` |
| `!include <gcp2026/TensorflowEnterprise>` | `TensorflowEnterprise` (`<$TensorflowEnterprise>`) | `Tensorflow_Enterprise` | `google-cloud-legacy-icons.zip` |
| `!include <gcp2026/TextToSpeech>` | `TextToSpeech` (`<$TextToSpeech>`) | `Text_To_Speech` | `google-cloud-legacy-icons.zip` |
| `!include <gcp2026/ThreatIntelligence>` | `ThreatIntelligence` (`<$ThreatIntelligence>`) | `Threat_Intelligence` | `core-products-icons.zip` |
| `!include <gcp2026/ToolsForPowershell>` | `ToolsForPowershell` (`<$ToolsForPowershell>`) | `Tools_For_Powershell` | `google-cloud-legacy-icons.zip` |
| `!include <gcp2026/Trace>` | `Trace` (`<$Trace>`) | — | `google-cloud-legacy-icons.zip` |
| `!include <gcp2026/TrafficDirector>` | `TrafficDirector` (`<$TrafficDirector>`) | `Traffic_Director` | `google-cloud-legacy-icons.zip` |
| `!include <gcp2026/Transfer>` | `Transfer` (`<$Transfer>`) | — | `google-cloud-legacy-icons.zip` |
| `!include <gcp2026/TransferAppliance>` | `TransferAppliance` (`<$TransferAppliance>`) | `Transfer_Appliance` | `google-cloud-legacy-icons.zip` |
| `!include <gcp2026/UserPreferences>` | `UserPreferences` (`<$UserPreferences>`) | `User_Preferences` | `google-cloud-legacy-icons.zip` |
| `!include <gcp2026/VMwareEngine>` | `VMwareEngine` (`<$VMwareEngine>`) | `VMware_Engine` | `google-cloud-legacy-icons.zip` |
| `!include <gcp2026/VertexAI>` | `VertexAI` (`<$VertexAI>`) | `Vertex_AI` | `core-products-icons.zip` |
| `!include <gcp2026/Vertexai>` | `Vertexai` (`<$Vertexai>`) | — | `google-cloud-legacy-icons.zip` |
| `!include <gcp2026/VideoIntelligenceAPI>` | `VideoIntelligenceAPI` (`<$VideoIntelligenceAPI>`) | `Video_Intelligence_API` | `google-cloud-legacy-icons.zip` |
| `!include <gcp2026/VirtualPrivateCloud>` | `VirtualPrivateCloud` (`<$VirtualPrivateCloud>`) | `Virtual_Private_Cloud` | `google-cloud-legacy-icons.zip` |
| `!include <gcp2026/VisualInspection>` | `VisualInspection` (`<$VisualInspection>`) | `Visual_Inspection` | `google-cloud-legacy-icons.zip` |
| `!include <gcp2026/Web3>` | `Web3` (`<$Web3>`) | — | `category-icons.zip` |
| `!include <gcp2026/WebAndMobile>` | `WebAndMobile` (`<$WebAndMobile>`) | `Web_And_Mobile` | `category-icons.zip` |
| `!include <gcp2026/WebRisk>` | `WebRisk` (`<$WebRisk>`) | `Web_Risk` | `google-cloud-legacy-icons.zip` |
| `!include <gcp2026/WebSecurityScanner>` | `WebSecurityScanner` (`<$WebSecurityScanner>`) | `Web_Security_Scanner` | `google-cloud-legacy-icons.zip` |
| `!include <gcp2026/Workflows>` | `Workflows` (`<$Workflows>`) | — | `google-cloud-legacy-icons.zip` |
| `!include <gcp2026/WorkloadIdentityPool>` | `WorkloadIdentityPool` (`<$WorkloadIdentityPool>`) | `Workload_Identity_Pool` | `google-cloud-legacy-icons.zip` |

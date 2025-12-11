![](https://dev.lutece.paris.fr/jenkins/buildStatus/icon?job=wf-plugin-workflow-deploy)
[![Alerte](https://dev.lutece.paris.fr/sonar/api/project_badges/measure?project=fr.paris.lutece.plugins%3Aplugin-workflow&metric=alert_status)](https://dev.lutece.paris.fr/sonar/dashboard?id=fr.paris.lutece.plugins%3Aplugin-workflow)
[![Line of code](https://dev.lutece.paris.fr/sonar/api/project_badges/measure?project=fr.paris.lutece.plugins%3Aplugin-workflow&metric=ncloc)](https://dev.lutece.paris.fr/sonar/dashboard?id=fr.paris.lutece.plugins%3Aplugin-workflow)
[![Coverage](https://dev.lutece.paris.fr/sonar/api/project_badges/measure?project=fr.paris.lutece.plugins%3Aplugin-workflow&metric=coverage)](https://dev.lutece.paris.fr/sonar/dashboard?id=fr.paris.lutece.plugins%3Aplugin-workflow)

# Plugin Workflow

## Introduction

Le plugin Workflow fournit un moteur de workflow complet. Il gère le cycle de vie des ressources en définissant des états, des transitions etles actions associées.

Le moteur de workflow offre les principales fonctionnalités suivantes :

 
* Définition de workflows avec états et transitions configurables
* Gestion des actions manuelles et automatiques sur les ressources
* Système de tâches extensible (notifications, commentaires, affectation, etc.)
* Historique complet des actions et des changements d’état
* Gestion des droits d’accès par état et action (RBAC)
* Affectation à des groupes de travail et contraintes d’accès
* Exécution automatique des actions via un démon
* API REST pour l’intégration externe

## Configuration

 **Propriétés de configuration** 

Le fichier de propriétés du plugin (workflow.properties) permet de configurer les paramètres suivants :

 
*  `daemon.automaticActionDaemon.interval` : Intervalle d’exécution du démon de traitement des actions automatiques (en secondes)
*  `daemon.automaticActionDaemon.onstartup` : Active (1) ou désactive (0) l’exécution du démon au démarrage
*  `daemon.archiveDaemon.interval` : Intervalle d’exécution du démon d’archivage (en secondes)
*  `daemon.archiveDaemon.onstartup` : Active (1) ou désactive (0) l’exécution du démon au démarrage
*  `workflow.itemsPerPage` : valeur par défaut du pagineur

 **Démons à activer** 

Le plugin fournit les démons suivants qui doivent être activés dans l’interface d’administration :

 
*  `AutomaticActionDaemon` : Démon qui traite les actions automatiques en attente. Il parcourt les ressources éligibles et exécute les actions automatiques configurées dans les workflows.

 **Caches** 

Le plugin utilise plusieurs caches pour optimiser les performances :

 
* Cache des workflows (WorkflowCache)
* Cache des états (StateCache)
* Cache des actions (ActionCache)

Ces caches sont gérés automatiquement et peuvent être activés ou vidés via l’interface d’administration si nécessaire.

## Utilisation

 **Droits d’administration** 

Le droit suivant doit être accordé pour accéder à la gestion des workflows :

 
*  `WORKFLOW_MANAGEMENT` : Gestion des workflows

 **RBAC d’administration (pour les rôles)** 

Les droits RBAC suivants sont disponibles et doivent être associés à des rôles personnalisés pour gérer chaque workflow séparément :

 
*  `WORKFLOW_STATE_TYPE` : Gestion des états
*  `WORKFLOW_ACTION_TYPE` : Gestion des actions

 **Rôles** 

Les rôles sont définis dans les tables CORE_ADMIN_ROLE et CORE_ADMIN_ROLE_RESOURCE pour contrôler l’accès aux différentes fonctionnalités du workflow.

 **Groupes de travail** 

Les workflows peuvent être affectés à des groupes de travail.

 **Services Java** 

Principaux services exposés par le plugin :

 
*  **WorkflowService** 
 
*  `getState(int idResource, String resourceType, int idWorkflow, Integer externalParentId)` : Récupère l’état actuel d’une ressource (ou initialise la ressource dans l’état initial lors du premier appel)
*  `doProcessAction(int idResource, String resourceType, int idAction, Integer externalParentId, HttpServletRequest request, Locale locale, boolean isAutomatic)` : Exécute une action sur une ressource
*  `getActions(int idResource, String resourceType, int idWorkflow, AdminUser user)` : Récupère les actions disponibles pour une ressource
*  `getDisplayDocumentHistory(int idResource, String resourceType, int idWorkflow, HttpServletRequest request, Locale locale)` : Affiche l’historique des actions
*  `getResourceIdListByIdState(int idState, String resourceType)` : Récupère la liste des ressources dans un état donné
* ...

*  **StateService** 
 
*  `findByResource(int idResource, String resourceType, int idWorkflow)` : Récupère l’état d’une ressource
*  `getStatesListByIdWorkflow(int idWorkflow)` : Liste les états d’un workflow
* ...

*  **ActionService** 
 
*  `getListActionByIdStateBefore(int idStateBefore, int idWorkflow)` : Liste les actions disponibles depuis un état

*  **TaskService** 
 
*  `getListTaskByIdAction(int idAction, Locale locale)` : Liste les tâches d’une action

*  **ResourceHistoryService** 
 
*  `getAllHistoryByResource(int idResource, String resourceType, int idWorkflow)` : Récupère l’historique complet d’une ressource
*  `getLastHistoryResource(int idResource, String resourceType, int idWorkflow)` : Récupère la dernière entrée de l’historique


 **Tâches du workflow** 

Le plugin fournit plusieurs types de tâches extensibles pouvant être associées aux actions :

 
*  `TaskAssignment` : Affectation de ressources aux utilisateurs ou groupes de travail
*  `TaskComment` : Ajout de commentaires aux ressources
*  `TaskNotification` : Envoi de notifications par email
*  `taskArchive` Archivage
*  `taskConfirmAction` Message de confirmation
*  `taskChoice` : Choix permettant de définir l'étape suivante

Chaque tâche peut être configurée via l’interface d’administration et possède son propre ensemble de paramètres. Il est possible de créer de nouvelles tâches personnalisées en étendant laclasse abstraite Task de la bibliothèque library-workflow-core.

 **Intégration dans un plugin** 

Pour intégrer le workflow dans un plugin personnalisé, il suffit d’utiliser le WorkflowService en fournissant :

 
* Un identifiant unique de ressource (idResource)
* Un type de ressource sous forme de chaîne (resourceType)
* L’identifiant du workflow à utiliser (idWorkflow)

Exemple d’intégration :

```

import fr.paris.lutece.portal.service.workflow.WorkflowService;

// Initialiser l’état de la ressource
WorkflowService.getInstance().getState(idResource, "MY_RESOURCE_TYPE", idWorkflow, -1);

// Récupérer les actions disponibles
Collection<Action> actions = WorkflowService.getInstance()
    .getActions(idResource, "MY_RESOURCE_TYPE", idWorkflow, adminUser);

// Exécuter une action
WorkflowService.getInstance().doProcessAction(idResource, "MY_RESOURCE_TYPE", 
    idAction, -1, request, locale, false);

```


[Maven documentation and reports](https://dev.lutece.paris.fr/plugins/plugin-workflow/)



 *generated by [xdoc2md](https://github.com/lutece-platform/tools-maven-xdoc2md-plugin) - do not edit directly.*
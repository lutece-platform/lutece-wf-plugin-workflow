![](https://dev.lutece.paris.fr/jenkins/buildStatus/icon?job=wf-plugin-workflow-deploy)
[![Alerte](https://dev.lutece.paris.fr/sonar/api/project_badges/measure?project=fr.paris.lutece.plugins%3Aplugin-workflow&metric=alert_status)](https://dev.lutece.paris.fr/sonar/dashboard?id=fr.paris.lutece.plugins%3Aplugin-workflow)
[![Line of code](https://dev.lutece.paris.fr/sonar/api/project_badges/measure?project=fr.paris.lutece.plugins%3Aplugin-workflow&metric=ncloc)](https://dev.lutece.paris.fr/sonar/dashboard?id=fr.paris.lutece.plugins%3Aplugin-workflow)
[![Coverage](https://dev.lutece.paris.fr/sonar/api/project_badges/measure?project=fr.paris.lutece.plugins%3Aplugin-workflow&metric=coverage)](https://dev.lutece.paris.fr/sonar/dashboard?id=fr.paris.lutece.plugins%3Aplugin-workflow)

# Plugin Workflow

## Introduction

The Workflow plugin provides a complete workflow engine. It manages the lifecycle of resources by defining states, transitions, andassociated actions.

The workflow engine offers the following main features:

 
* Definition of workflows with configurable states and transitions
* Management of manual and automatic actions on resources
* Extensible task system (notification, comments, assignment, etc.)
* Complete history tracking of actions and state changes
* Access rights management by state and action (RBAC)
* Workgroup assignment and access constraints
* Automatic action execution via daemon
* REST API for external integration

## Configuration

 **Configuration Properties** 

The plugin properties file (workflow.properties) allows configuration of the following parameters:

 
*  `daemon.automaticActionDaemon.interval` : Execution interval for the automatic action processing daemon (in seconds)
*  `daemon.automaticActionDaemon.onstartup` : Enables (1) or disables (0) daemon execution at startup
*  `daemon.archiveDaemon.interval` : : Execution interval for the archive daemon (in seconds)
*  `daemon.archiveDaemon.onstartup` : : Enables (1) or disables (0) daemon execution at startup
*  `workflow.itemsPerPage` : default paginator value

 **Daemons to Activate** 

The plugin provides the following daemons that must be activated in the administration interface:

 
*  `AutomaticActionDaemon` : Daemon that processes pending automatic actions. It traverses eligible resources and executes automatic actions configured in workflows.

 **Caches** 

The plugin uses several caches to optimize performance:

 
* Workflow cache (WorkflowCache)
* State cache (StateCache)
* Action cache (ActionCache)

These caches are automatically managed and can be activated or cleared through the administration interface if necessary.

## Usage

 **Administration Rights** 

The following right should be granted to access to the workflow management feature :

 
*  `WORKFLOW_MANAGEMENT` : Workflow management

 **Administration RBAC (for roles)** 

The following RBAC rights are available, and should be associated to custom roles to manage each workflow separately :

 
*  `WORKFLOW_APP` : Workflow management (creation, modification, deletion)
*  `WORKFLOW_STATE_TYPE` : State management
*  `WORKFLOW_ACTION_TYPE` : Action management

 **Roles** 

Roles are defined in the CORE_ADMIN_ROLE and CORE_ADMIN_ROLE_RESOURCE tables to control access to various workflow features.

 **Workgroups** 

Workflow can be assigned to workgroups.

 **Java Services** 

Main services exposed by the plugin:

 
*  **WorkflowService** 
 
*  `getState(int idResource, String resourceType, int idWorkflow, Integer externalParentId)` : Retrieves the current state of a resource (or init the resource in the initial state on first call)
*  `doProcessAction(int idResource, String resourceType, int idAction, Integer externalParentId, HttpServletRequest request, Locale locale, boolean isAutomatic)` : Executes an action on a resource
*  `getActions(int idResource, String resourceType, int idWorkflow, AdminUser user)` : Retrieves available actions for a resource
*  `getDisplayDocumentHistory(int idResource, String resourceType, int idWorkflow, HttpServletRequest request, Locale locale)` : Displays action history
*  `getResourceIdListByIdState(int idState, String resourceType)` : Retrieves the list of resources in a given state
* ...

*  **StateService** 
 
*  `findByResource(int idResource, String resourceType, int idWorkflow)` : Retrieves the state of a resource
*  `getStatesListByIdWorkflow(int idWorkflow)` : Lists workflow states
* ...

*  **ActionService** 
 
*  `getListActionByIdStateBefore(int idStateBefore, int idWorkflow)` : Lists actions available from a state

*  **TaskService** 
 
*  `getListTaskByIdAction(int idAction, Locale locale)` : Lists tasks for an action

*  **ResourceHistoryService** 
 
*  `getAllHistoryByResource(int idResource, String resourceType, int idWorkflow)` : Retrieves complete resource history
*  `getLastHistoryResource(int idResource, String resourceType, int idWorkflow)` : Retrieves the last history entry


 **REST API** 

The plugin exposes a REST API for external integration. Main endpoints are:

 
*  `GET /rest/workflow/workflow/{id}` : Retrieves a workflow by its ID
*  `GET /rest/workflow/workflow` : Lists all workflows
*  `GET /rest/workflow/state/{id_resource}/{resource_type}/{id_workflow}` : Retrieves the state of a resource
*  `GET /rest/workflow/actions/{id_resource}/{resource_type}/{id_workflow}` : Lists available actions for a resource
*  `POST /rest/workflow/action/{id_action}/{id_resource}/{resource_type}` : Executes an action on a resource
*  `GET /rest/workflow/history/{id_resource}/{resource_type}/{id_workflow}` : Retrieves resource history

The REST API requires appropriate authentication and respects the access rights defined in the workflow.

 **Workflow Tasks** 

The plugin provides several types of extensible tasks that can be associated with actions:

 
*  `TaskNotification` : Sending email notifications
*  `TaskComment` : Adding comments to resources
*  `TaskAssignment` : Assigning resources to users or workgroups
*  `TaskChangeState` : Automatic state change
*  `TaskLinkedAction` : Executing linked actions

Each task can be configured through the administration interface and has its own set of parameters. It is possible to create new custom tasks by extending theabstract Task class from the library-workflow-core library.

 **Integration in a Plugin** 

To integrate the workflow into a custom plugin, simply use the WorkflowService by providing:

 
* A unique resource identifier (idResource)
* A resource type as a string (resourceType)
* The workflow identifier to use (idWorkflow)

Integration example:

```

import fr.paris.lutece.portal.service.workflow.WorkflowService;

// Initialize resource state
WorkflowService.getInstance().getState(idResource, "MY_RESOURCE_TYPE", idWorkflow, -1);

// Get available actions
Collection<Action> actions = WorkflowService.getInstance()
    .getActions(idResource, "MY_RESOURCE_TYPE", idWorkflow, adminUser);

// Execute an action
WorkflowService.getInstance().doProcessAction(idResource, "MY_RESOURCE_TYPE", 
    idAction, -1, request, locale, false);

```


[Maven documentation and reports](https://dev.lutece.paris.fr/plugins/plugin-workflow/)



 *generated by [xdoc2md](https://github.com/lutece-platform/tools-maven-xdoc2md-plugin) - do not edit directly.*
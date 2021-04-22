package fr.paris.lutece.plugins.workflow.modules.state.service;

public interface IResourceController
{

    /**
     * The label key of the service.
     * 
     * @return
     */
    String getLabelKey( );

    /**
     * The help key of the service.
     * 
     * @return
     */
    String getHelpKey( );

    /**
     * The name of the service.
     * 
     * @return
     */
    String getName( );

    /**
     * controls the result of the control.
     * 
     * @return
     */
    boolean control( int nIdResource, String strResourceType );
}

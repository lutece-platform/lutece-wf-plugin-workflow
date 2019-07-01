package fr.paris.lutece.plugins.workflow.service.prerequisite;

import fr.paris.lutece.plugins.workflowcore.business.prerequisite.IPrerequisiteConfig;
import fr.paris.lutece.plugins.workflowcore.service.prerequisite.IAutomaticActionPrerequisiteService;
import fr.paris.lutece.portal.business.user.AdminUser;

/**
 * Interface for prerequisite services of manual actions.<br />
 * Before displaying an action, every prerequisite of the action are checked. If one of them failed, the action is not displayed.<br />
 * <br />
 * <b>Implementations of this interface must be declared as Spring beans.</b>
 */
public interface IManualActionPrerequisiteService extends IAutomaticActionPrerequisiteService {

	/**
     * Check if a resource matches this prerequisite.
     * 
     * @param user
     * 		   The user trying to do the action
     * @param nIdResource
     *            The id of the resource
     * @param strResourceType
     *            The resource type
     * @param config
     *            The configuration of the prerequisite, or null if the prerequisite has no configuration
     * @param nIdAction
     *            The id of the action that will be performed if the resource matches every prerequisites
     * @return True if the resource matches this prerequisite, false otherwise
     */
    default boolean canManualActionBePerformed( AdminUser user, int nIdResource, String strResourceType, IPrerequisiteConfig config, int nIdAction )
    {
    	return canActionBePerformed( nIdResource, strResourceType, config, nIdAction );
    }
}

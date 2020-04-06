package fr.paris.lutece.plugins.workflow.business.action;

import java.util.List;

import fr.paris.lutece.plugins.workflow.business.icon.IconDAO;
import fr.paris.lutece.plugins.workflow.business.state.StateDAO;
import fr.paris.lutece.plugins.workflow.business.workflow.WorkflowDAO;
import fr.paris.lutece.plugins.workflow.utils.WorkflowUtils;
import fr.paris.lutece.plugins.workflowcore.business.action.Action;
import fr.paris.lutece.plugins.workflowcore.business.action.ActionFilter;
import fr.paris.lutece.plugins.workflowcore.business.icon.Icon;
import fr.paris.lutece.plugins.workflowcore.business.state.State;
import fr.paris.lutece.plugins.workflowcore.business.workflow.Workflow;
import fr.paris.lutece.test.LuteceTestCase;

public class ActionDaoTest extends LuteceTestCase
{
    private ActionDAO _dao = new ActionDAO( );
    private IconDAO iconDAO = new IconDAO( );
    private StateDAO stateDAO = new StateDAO( );
    private WorkflowDAO workflowDAO = new WorkflowDAO( );
    
    private Icon icon;
    private Workflow wf;
    private State stateBefore;
    private State stateAfter;
    
    @Override
    protected void setUp( ) throws Exception
    {
        super.setUp( );
        
        icon = new Icon( );
        icon.setName( "icon" );
        iconDAO.insert( icon );
        
        wf = new Workflow( );
        wf.setName( "wf1" );
        wf.setCreationDate( WorkflowUtils.getCurrentTimestamp( ) );
        wf.setDescription( "strDescription" );
        wf.setEnabled( true );
        wf.setWorkgroup( "group" );
        workflowDAO.insert( wf );
        
        stateBefore = new State( );
        stateBefore.setWorkflow( wf );
        stateBefore.setInitialState( false );
        stateBefore.setRequiredWorkgroupAssigned( false );
        stateDAO.insert( stateBefore );
        stateAfter = new State( );
        stateAfter.setWorkflow( wf );
        stateAfter.setInitialState( false );
        stateAfter.setRequiredWorkgroupAssigned( false );
        stateDAO.insert( stateAfter );
    }
    
    @Override
    protected void tearDown( ) throws Exception
    {
        iconDAO.delete( icon.getId( ) );
        stateDAO.delete( stateBefore.getId( ) );
        stateDAO.delete( stateAfter.getId( ) );
        workflowDAO.delete( wf.getId( ) );
        
        super.tearDown( );
    }
    
    public void testCRUD( )
    {
        Action action = new Action( );
        action.setName( "name" );
        action.setDescription( "strDescription" );
        action.setIcon( icon );
        action.setStateBefore( stateBefore );
        action.setStateAfter( stateAfter );
        action.setWorkflow( wf );
        action.setOrder( 0 );
        
        _dao.insert( action );
        
        Action load = _dao.load( action.getId( ) );
        assertEquals( action.getName( ), load.getName( ) );
        
        action.setName( "new name" );
        _dao.store( action );
        load = _dao.load( action.getId( ) );
        assertEquals( action.getName( ), load.getName( ) );
        
        _dao.delete( action.getId( ) );
        load = _dao.load( action.getId( ) );
        assertNull( load );
    }
    
    public void testLoadWithIcon( )
    {
        Action action = new Action( );
        action.setName( "name" );
        action.setDescription( "strDescription" );
        action.setIcon( icon );
        action.setStateBefore( stateBefore );
        action.setStateAfter( stateAfter );
        action.setWorkflow( wf );
        action.setOrder( 0 );
        
        _dao.insert( action );
        
        Action load = _dao.loadWithIcon( action.getId( ) );
        assertEquals( icon.getName( ), load.getIcon( ).getName( ) );
        
        _dao.delete( action.getId( ) );
    }
    
    public void testSelectActionsByFilter( )
    {
        Action action = new Action( );
        action.setName( "name" );
        action.setDescription( "strDescription" );
        action.setIcon( icon );
        action.setStateBefore( stateBefore );
        action.setStateAfter( stateAfter );
        action.setWorkflow( wf );
        action.setOrder( 0 );
        
        _dao.insert( action );
        
        ActionFilter filter = new ActionFilter( );
        filter.setIdStateAfter( stateAfter.getId( ) );
        filter.setIdStateBefore( stateBefore.getId( ) );
        filter.setIdWorkflow( wf.getId( ) );
        filter.setIdIcon( icon.getId( ) );
        filter.setAutomaticReflexiveAction( false );
        filter.setIsMassAction( false );
        
        List<Action> list = _dao.selectActionsByFilter( filter );
        assertEquals( 1, list.size( ) );
        
        _dao.delete( action.getId( ) );
    }
    
    public void testFindStatesBetweenOrders( )
    {
        Action action = new Action( );
        action.setName( "name" );
        action.setDescription( "strDescription" );
        action.setIcon( icon );
        action.setStateBefore( stateBefore );
        action.setStateAfter( stateAfter );
        action.setWorkflow( wf );
        action.setOrder( 3 );
        
        _dao.insert( action );
        
        List<Action> list = _dao.findStatesBetweenOrders( 2, 4, wf.getId( ) );
        assertEquals( 1, list.size( ) );
        
        _dao.delete( action.getId( ) );
    }
    
    public void testFindStatesAfterOrder( )
    {
        Action action = new Action( );
        action.setName( "name" );
        action.setDescription( "strDescription" );
        action.setIcon( icon );
        action.setStateBefore( stateBefore );
        action.setStateAfter( stateAfter );
        action.setWorkflow( wf );
        action.setOrder( 3 );
        
        _dao.insert( action );
        
        List<Action> list = _dao.findStatesAfterOrder( 2, wf.getId( ) );
        assertEquals( 1, list.size( ) );
        
        _dao.delete( action.getId( ) );
    }
}

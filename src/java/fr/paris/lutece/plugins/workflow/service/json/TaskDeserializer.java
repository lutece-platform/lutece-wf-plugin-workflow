package fr.paris.lutece.plugins.workflow.service.json;

import java.io.IOException;
import java.util.Locale;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;

import fr.paris.lutece.plugins.workflow.service.task.TaskFactory;
import fr.paris.lutece.plugins.workflowcore.business.action.Action;
import fr.paris.lutece.plugins.workflowcore.service.task.ITask;
import fr.paris.lutece.plugins.workflowcore.service.task.ITaskFactory;
import fr.paris.lutece.portal.service.spring.SpringContextService;

public class TaskDeserializer extends JsonDeserializer<ITask>
{

    
    @Override
    public ITask deserialize( JsonParser p, DeserializationContext ctxt ) throws IOException
    {
        ObjectCodec oc = p.getCodec();
        JsonNode node = oc.readTree(p);
        
        ITaskFactory taskfactory = SpringContextService.getBean( TaskFactory.BEAN_SERVICE );
        
        String taskType = node.get( "taskType" ).asText( );
        int order = node.get( "order" ).asInt( );
        int actionId = node.get( "action" ).asInt( );
        int id  = node.get( "id" ).asInt( );
        
        Action action = new Action( );
        action.setId( actionId );
        
        ITask task = taskfactory.newTask( taskType, Locale.getDefault( ) );
        task.setOrder( order );
        task.setAction( action );
        task.setId( id );
        
        return task;
    }

}

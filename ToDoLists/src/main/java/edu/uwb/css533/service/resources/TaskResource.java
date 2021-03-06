package edu.uwb.css533.service.resources;

import edu.uwb.css533.service.db.TaskServiceDB;
import edu.uwb.css533.service.resources.RequestObjects.AddTask;
import edu.uwb.css533.service.resources.RequestObjects.UpdateTaskContent;
import edu.uwb.css533.service.resources.RequestObjects.UpdateTaskStatus;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/tasks")
@Produces(MediaType.APPLICATION_JSON)
public class TaskResource {
    TaskServiceDB databaseConnection;

    public TaskResource() {
        this.databaseConnection = new TaskServiceDB();
    }
    public TaskResource(String db) {
        this.databaseConnection = new TaskServiceDB(db);
    }

    @POST
    @Path("/addTask")
    public Response addTask(AddTask task) {

        String taskName = task.getTaskname();
        String taskDescription = task.getContent();
        String listId = task.getListid();
        String userName = task.getUsername();

        String msg = databaseConnection.addTask(taskName,taskDescription,listId,userName);

        if(msg.contains("Successfully")){
            System.out.println(msg);
            return Response.ok(msg).build();
        }
        else{
            String message = " Adding " +taskName +" of  list " +listId + "for user: "+ userName + " has failed";
            System.out.println(message);
            System.out.println(msg);
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(msg)
                    .build();
        }
    }

    @GET
    @Path("/deleteTask")
    public Response deleteTask(
            @QueryParam("username") String userName,
            @QueryParam("listid") String listId,
            @QueryParam("taskid") String taskId) {
        String msg = "";
        msg = databaseConnection.deleteTask(taskId,listId,userName);
        if(msg.contains("Successfully")){

            System.out.println(msg);
            return Response.ok(msg).build();
        }
        else{
            System.out.println(msg);
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(msg)
                    .build();
        }
    }

    @GET
    @Path("/deleteAllTasks")
    public Response deleteAllTasks(
            @QueryParam("username") String userName,
            @QueryParam("listid") String listId) {

        String msg= databaseConnection.deleteAllTasks(userName,listId);
        if(msg.contains("Successfully")){
            System.out.println(msg);
            return Response.ok(msg).build();
        }
        else{
            System.out.println(msg);
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(msg)
                    .build();
        }
    }
    @GET
    @Path("/displayAllTasksNames")
    public Response displayAllTasksNames(
            @QueryParam("username") String userName,
            @QueryParam("listid") String listId) {
        String msg = "";
        msg= databaseConnection.displayAllTaskNames(userName,listId);
        if(!msg.contains("Error")){
            System.out.println( "All task of list" + listId + " has been displayed for user: " + userName);
            return Response.ok(msg).build();
        }
        else{
            System.out.println("Displaying all tasks of  list " +listId + " for user: "+ userName + " has failed");
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(msg)
                    .build();
        }
    }

    @GET
    @Path("/getTask")
    public Response getTask(@QueryParam("username") String username,
                            @QueryParam("listid") String listid,
                            @QueryParam("taskid") String taskid) {
        String msg = databaseConnection.getTask(username, listid, taskid);
        if (msg.contains("Error")) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(msg).build();
        }
        return Response.ok(msg).build();


    }


    @PUT
    @Path("/updateTaskContent")
    public Response updateTaskContent(UpdateTaskContent task) {
        String msg = "";
        String userName = task.getUsername();
        String listId = task.getListid();
        String taskContent = task.getContent();
        String taskId = task.getTaskid();
        msg= databaseConnection.updateTaskContent(userName,listId,taskContent,taskId);
        if(msg.contains("Successfully")){
            return Response.ok(msg).build();
        }
        else{
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(msg)
                    .build();
        }
    }
    @PUT
    @Path("/updateTaskStatus")
    public Response updateTaskStatus(UpdateTaskStatus task) {

        String msg = "";
        String userName = task.getUsername();
        String listId = task.getListid();
        String taskStatus = task.getStatus();
        String taskId = task.getTaskid();
        msg= databaseConnection.updateTaskStatus(userName,listId,taskStatus,taskId);
        if(msg.contains("Successfully")){
            return Response.ok(msg).build();
        }
        else{
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(msg)
                    .build();
        }
    }
}

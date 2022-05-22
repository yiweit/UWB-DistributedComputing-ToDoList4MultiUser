package edu.uwb.css533.service.db;

import org.json.JSONArray;
import org.json.JSONObject;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

public class TaskServiceDB extends ListServiceDB {
    public boolean checkExist(String taskId){
        String sql = "SELECT TASKID FROM TASKS WHERE TASKID= ?;";
        try {

            PreparedStatement stmt = connection.prepareStatement(sql);
            stmt.setInt(1,Integer.parseInt(taskId));
            ResultSet rs = stmt.executeQuery();
            if (rs == null){
                System.out.println("No data found.");
                return false;
            }
            while (rs.next()) {
                String compare = Integer.toString(rs.getInt("TASKID"));
                if(taskId.equals(compare)) {
                    return true;
                }
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return false;
        }
        return false;
    }
    public boolean checkDuplicate(String taskName, String listId){
        String sql = "SELECT TASKNAME FROM TASKS WHERE LISTID= ?;";
        try {
            PreparedStatement stmt = connection.prepareStatement(sql);
            stmt.setString(1,listId);
            ResultSet rs = stmt.executeQuery();
            if (rs == null){
                System.out.println("No data found.");
                return false;
            }
            while (rs.next()) {
                if(taskName.equals(rs.getString("TASKNAME"))) {
                    return true;
                }
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return false;
        }
        return false;
    }


    public String[] addTask(String task_name, String task_description, String list_id, String user_name){
        String[] returnValue = {};
        int taskId = 0;
        boolean notAllowed = checkAccess(user_name,list_id).contains("Error");
        if(notAllowed){
            System.out.println("Invalid user");
            returnValue[0] = "Invalid user";
            return returnValue;
        }
        if (connection == null) {
            connect();
        }
        if (connection == null) {
            System.out.println("Unable to connect to database.");
            returnValue[0] = "Unable to connect to database.";
            return returnValue;
        }

        boolean taskNameExist = checkDuplicate(task_name,list_id);
        if(taskNameExist){
            System.out.println("taskName has already exists for current list");
            returnValue[0] = "taskName has already exists for current list";
            return returnValue;
        }

        String sql = "INSERT INTO TASKS (TASKNAME, CONTENT, LISTID) VALUES ( ?, ?, ?) RETURNING TASKID;";
        try {
            PreparedStatement stmt = connection.prepareStatement(sql);
            stmt.setString(1, task_name);
            stmt.setString(2, task_description);

            stmt.setString(3, list_id);
//            java.util.Date date = new Date();
//            Timestamp ts = new Timestamp(date.getTime());
//            stmt.setTimestamp(5, ts);
            stmt.execute();
            ResultSet id = stmt.getResultSet();
            id.next();
            taskId = id.getInt(1);
        } catch (SQLException e) {
            e.printStackTrace();
            returnValue[0] = e.getMessage();
            return returnValue;
        }

        returnValue[0] = "Successfully insert the task: " + task_name+ "of current list: "+list_id;
        returnValue[1] = Integer.toString(taskId);
        return returnValue;
    }
    public String deleteTask(String task_id, String list_id,String userName){
        boolean notAllowed = checkAccess(userName,list_id).contains("Error");
        if(notAllowed){
            System.out.println("Invalid user");
            return "Invalid user";
        }
        if (connection == null) {
            connect();
        }
        if (connection == null) {
            System.out.println("Unable to connect to database.");
            return "Unable to connect to database.";
        }
        boolean taskNameExist = checkExist(task_id);

        if(!taskNameExist){
            System.out.println("taskName does not exists for current list");
            return "taskName does not exists for current list";
        }

        String sql = "DELETE FROM TASKS WHERE TASKID= ?;";
        try {
            PreparedStatement stmt = connection.prepareStatement(sql);
            stmt.setString(1,task_id);
            int rows = stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            return e.getMessage();
        }
        return " Successfully deleted task " + task_id +"of list " + list_id + " for user: " + userName;
    }

    public String deleteAllTasks(String user_name, String list_id){
        boolean notAllowed = checkAccess(user_name,list_id).contains("Error");
        if(notAllowed){
            System.out.println("Invalid user");
            return "Invalid user";
        }
        if (connection == null) {
            connect();
        }
        if (connection == null) {
            System.out.println(("Unable to connect to database."));
            return "Unable to connect database.";
        }
        String sql = "DELETE FROM TASKS WHERE LISTID=?;";
        try {
            PreparedStatement stmt = connection.prepareStatement(sql);
            stmt.setString(1,list_id);
            stmt.executeUpdate();
        }catch(SQLException e) {
            e.printStackTrace();
            return e.getMessage();
        }
        return "Successfully deleted all tasks of  list " +list_id;
    }

    public String displayAllTaskNames(String user_name, String list_id){
        String msg="";
        boolean notAllowed = checkAccess(user_name,list_id).contains("Error");
        if(notAllowed){
            System.out.println("Invalid user");
            return "Invalid user";
        }
        if (connection == null) {
            connect();
        }
        if (connection == null) {
            System.out.println(("Unable to connect to database."));
            return "Unable to connect to database.";
        }
        String sql_get_task_name = "SELECT TASKID, TASKNAME FROM TASKS WHERE LISTID=?;";
        try {
            PreparedStatement stmt = connection.prepareStatement(sql_get_task_name);
            stmt.setString(1,list_id);
            ResultSet rs = stmt.executeQuery();
            if (rs == null){
                System.out.println("No task found for current list.");
                return "No task found for current list.";
            }
            msg = msg+ "Successfully display all tasks. Here is all your tasks:"+"\n";
            System.out.println("Here is all your tasks:");
            JSONArray result = new JSONArray();
            while (rs.next()) {
                msg = msg+"Task Name: " + rs.getString("task_name")+"\n";
                JSONObject row = new JSONObject();
                row.put("taskid", rs.getInt("taskid"));
                row.put("taskname", rs.getString("taskname"));
                result.put(row);

                System.out.println("Task Name: " + rs.getString("task_name"));
            }
            return result.toString();
        }catch(SQLException e){
            e.printStackTrace();
            return e.getMessage();
        }

    }



    public String updateTaskContent(String user_name, String list_id, String content, String task_id){
        boolean notAllowed = checkAccess(user_name,list_id).contains("Error");
        if(notAllowed){
            System.out.println("Invalid user");
            return "Invalid user";
        }
        if (connection == null) {
            connect();
        }
        if (connection == null) {
            System.out.println("Unable to connect database.");
            return "Unable to connect to database.";
        }
//        Date date = new Date();
//        Timestamp ts = new Timestamp(date.getTime());
//        String update_task_content_sql = "UPDATE TASKS SET CONTENT=?,LAST_MODIFIED_DATE=? WHERE TASKID=?;";
//        try{
//            PreparedStatement stmt = connection.prepareStatement(update_task_content_sql);
//            stmt.setString(1,content);
//            stmt.setTimestamp(2,ts);
//            stmt.setString(3,task_id);
//            stmt.executeUpdate();
//        } catch (SQLException e) {
//            e.printStackTrace();
//            return e.getMessage();
//        }
        return "Successfully updated task content:\n "+content+"\nfor task "+task_id;
    }


    public String updateTaskStatus(String user_name, String list_id,String status, String task_id){
        boolean notAllowed = checkAccess(user_name,list_id).contains("Error");
        if(notAllowed){
            System.out.println("Invalid user");
            return "Invalid user";
        }
        if (connection == null) {
            connect();
        }
        if (connection == null) {
            System.out.println("Unable to connect to database.");
            return "Unable to connect to database.";
        }
        if(! (status.equals("Not Started") || status.equals("Completed" )||status.equals("In-Progress" ))){
            System.out.println("Task type can only be Completed, Not Started or In-Progress");
            return "Task type can only be Completed, Not Started or In-Progress";
        }
//        Date date = new Date();
//        Timestamp ts = new Timestamp(date.getTime());
//        String update_task_status_sql = "UPDATE Task SET STATUS=?,LAST_MODIFIED_DATE=? WHERE TASKID=?;";
//        try{
//            PreparedStatement stmt = connection.prepareStatement(update_task_status_sql);
//            stmt.setString(1,status);
//            stmt.setTimestamp(2,ts);
//            stmt.setString(3,task_id);
//            stmt.executeUpdate();
//        } catch (SQLException e) {
//            e.printStackTrace();
//            return e.getMessage();
//        }
        return "Successfully updated task status: "+status+"for task "+task_id;
    }
}

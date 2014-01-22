package collabrativewhiteboard;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ConcurrentMap;

import javax.servlet.ServletException;
import javax.servlet.http.*;



import java.util.Queue;
import com.google.appengine.labs.repackaged.org.json.JSONException;
import com.google.appengine.labs.repackaged.org.json.JSONObject;
import java.util.Map.Entry;
@SuppressWarnings("serial")
public class CollabrativewhiteboardServlet extends HttpServlet {


	@SuppressWarnings("rawtypes")
//	private ConcurrentMap<String, Queue> userIDEventsMap = new ConcurrentHashMap<String, Queue>();

	private ConcurrentMap<String, Drawing> drawingMap = new ConcurrentHashMap<String,Drawing>();

	HttpServletResponse response = null;
	public void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {

		if(req == null)
			return;
		String type = req.getParameter("type");
		if(type == null){
			resp.setContentType("text/plain");
			resp.getWriter().println("No type in the request");
			return;
		}
		
		String userID = req.getParameter("userID");
		if(userID == null){
			resp.setContentType("text/plain");
			resp.getWriter().println("No user ID in the request");
			return;
		}
		String drawingID = req.getParameter("drawingID");
		if(drawingID == null){
			resp.setContentType("text/plain");
			resp.getWriter().println("No drawing ID in the request");
			return;
		}
		if(type.equals("join")){
			
			String res = handleJoinRequest(userID,drawingID);
			// check if the user is there in the invited list of the drawing ID and return the app canvas as string
			resp.setContentType("text/plain");
			resp.getWriter().println(res);
//			return;
		}
		
		else if(type.equals("new")){
			
			// create a new drawing and add it to the map
			String res = handleNewRequest(userID,drawingID);
			resp.setContentType("text/plain");
			resp.getWriter().println(res);
		//	return;
		}
		else if(type.equals("invite")){
			
			String invitedUserID = req.getParameter("type");;
			String res = handleInviteRequest(userID,drawingID,invitedUserID );
			resp.setContentType("text/plain");
			resp.getWriter().println(res);
		}
		
		else if(type.equals("update")){
			
			handleUpdateRequest(userID,drawingID,resp);
		}


		
		
		/*if(userIDEventsMap == null)
			return;

		Queue<String> userIDEventQueue = userIDEventsMap.get(userID);

		// if the user does not exist in the map then insert it 
		if(userIDEventQueue == null){
			System.out.println("User " + userID + "User not found in the map");
			userIDEventsMap.put(userID,new ConcurrentLinkedQueue<String>());	
			return;
		}
		
		// get all the events in the queue for this user 
		if (userIDEventQueue.size() >= 1)
		{
			resp.setContentType("application/json");

			StringBuilder sb = new StringBuilder();
			sb.append( "{\"objects\":[");

			for(int i= 0; i < userIDEventQueue.size() - 1; i++){
				sb.append( userIDEventQueue.remove() + ",");
			}
			sb.append( userIDEventQueue.remove() + "]}");
			resp.getWriter().println(sb.toString());
			
		}*/

	}


	private String handleInviteRequest(String userID, String drawingID,
			String invitedUserID) {
		
		String result = null;
		// check if the drawing ID is there in the map
		Drawing drawing = drawingMap.get(drawingID);
		if(drawing == null){
			result = "Drawing " + drawingID + "not present in the map";
			return result;
		}
	
		// add the user to the invited users of the drawing
		// NOTE the user will be added during join
		drawing.inviteUser(invitedUserID);
		return result;
	
	}


	private String handleUpdateRequest(String userID, String drawingID, HttpServletResponse resp) {

		String result = null;
		// check if the drawing ID is there in the map
		Drawing drawing = drawingMap.get(drawingID);
		if(drawing == null){
			result = "Drawing " + drawingID + "not present in the map";
			return result;
		}
		
		String str = drawing.getResponseStringForUser(userID);
		resp.setContentType("application/json");
		try {
			resp.getWriter().println(str);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			resp.getWriter().println(str);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return str;
		
		// TODO Auto-generated method stub
	
		/*if(userIDEventsMap == null)
		return;

	Queue<String> userIDEventQueue = userIDEventsMap.get(userID);

	// if the user does not exist in the map then insert it 
	if(userIDEventQueue == null){
		System.out.println("User " + userID + "User not found in the map");
		userIDEventsMap.put(userID,new ConcurrentLinkedQueue<String>());	
		return;
	}
	
	// get all the events in the queue for this user 
	if (userIDEventQueue.size() >= 1)
	{
		resp.setContentType("application/json");

		StringBuilder sb = new StringBuilder();
		sb.append( "{\"objects\":[");

		for(int i= 0; i < userIDEventQueue.size() - 1; i++){
			sb.append( userIDEventQueue.remove() + ",");
		}
		sb.append( userIDEventQueue.remove() + "]}");
		resp.getWriter().println(sb.toString());
		
	}*/
		
	}


	private String handleNewRequest(String userID, String drawingID) {
		
		String result = "";
		Drawing drawing = drawingMap.get(drawingID);
		if(drawing != null){
			result = "Drawing " + drawingID + "already present in the map";
			return result;
		}
		
		// check if the drawing ID is there in the map
		drawing =  new Drawing(userID,drawingID);
		// add the drawing to the map
		drawingMap.put(drawingID, drawing);
		
		result = "New drawing " + drawingID + "created by user" + userID;
		return result;
		// TODO Auto-generated method stub
		
	}


	private String handleJoinRequest(String userID, String drawingID) {
		
		String result = "";
		
		// check if the drawing ID is there in the map
		Drawing drawing = drawingMap.get(drawingID);
		if(drawing == null){
			result = "Drawing " + drawingID + "not present in the map";
			return result;
		}
		
		// check if the user is invited to join the drawing
		String canvas = drawing.isInvited(userID);
	
		if(canvas != null){
			drawing.addUser(userID);
			return canvas;
		}
		return result;
		
	}


	// Method to handle POST method request.
	public void doPost(HttpServletRequest request,
			HttpServletResponse response)
					throws ServletException, IOException {


		System.out.println("Post receieved");
		response.setContentType("text/plain");
		response.getWriter().println(request.getHeader("Content-type"));
		response.getWriter().println(request.getHeader("Accept"));
		
		StringBuilder sb = new StringBuilder();
		BufferedReader reader = request.getReader();
		try {
			String line;
			while ((line = reader.readLine()) != null) {
				sb.append(line).append('\n');
			}
		} finally {
			reader.close();
		}


		try {
			String str = sb.toString();
			
			if(str == null)
				return;
			
			if(str.isEmpty())
				return;
			JSONObject jsonObject = new JSONObject(str);
			String userID = jsonObject.getString("userID");

			if(userID == null)
				return;

			String drawingID = jsonObject.getString("drawingID");
			if(drawingID == null)
				return;
			Drawing drawing = drawingMap.get(drawingID);
			if(drawing == null)
				return;
			drawing.updateEventsQueue(sb.toString());
			
//			// if the userID is not there in the map then insert it
//			if(userIDEventsMap.get(userID) == null)
//				userIDEventsMap.put(userID,new ConcurrentLinkedQueue<String>());

			// insert this string in all the queues of the map
//			for(Entry<String, Queue> entry : userIDEventsMap.entrySet()) {
//
//				System.out.println("Key " + entry.getKey() );
//
//				Queue<String> q = entry.getValue();
//
//				if(q == null){
//					q = new ConcurrentLinkedQueue<String>();	
//				}
//				q.add(sb.toString());
//
//			}

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}


		response.getWriter().println(sb.toString());




	}}

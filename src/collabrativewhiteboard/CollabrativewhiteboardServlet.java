package collabrativewhiteboard;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ConcurrentMap;

import javax.servlet.ServletException;
import javax.servlet.http.*;

//import org.apache.http.entity.StringEntity;

import java.util.Queue;
import com.google.appengine.labs.repackaged.org.json.JSONException;
import com.google.appengine.labs.repackaged.org.json.JSONObject;
import java.util.Map.Entry;
@SuppressWarnings("serial")
public class CollabrativewhiteboardServlet extends HttpServlet {

	@SuppressWarnings("rawtypes")
	private ConcurrentMap<String, Queue> userIDEventsMap = new ConcurrentHashMap<String, Queue>();


	HttpServletResponse response = null;
	public void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {

		if(req == null)
			return;

		String userID = req.getParameter("userID");

		if(userID == null){
			return;
		}
		
		System.out.println("Getting response for user " + userID);
		if(userIDEventsMap == null)
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
			
		}

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
			if(str.isEmpty())
				return;
			JSONObject jsonObject = new JSONObject(str);
			String userID = jsonObject.getString("userID");

			if(userID == null)
				return;

			// if the userID is not there in the map then insert it
			if(userIDEventsMap.get(userID) == null)
				userIDEventsMap.put(userID,new ConcurrentLinkedQueue<String>());

			// insert this string in all the queues of the map
			for(Entry<String, Queue> entry : userIDEventsMap.entrySet()) {

				System.out.println("Key " + entry.getKey() );

				Queue<String> q = entry.getValue();

				if(q == null){
					q = new ConcurrentLinkedQueue<String>();	
				}
				q.add(sb.toString());

		}

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}


		response.getWriter().println(sb.toString());




	}}

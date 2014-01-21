package collabrativewhiteboard;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Enumeration;
import java.util.Iterator;

import javax.servlet.ServletException;
import javax.servlet.http.*;

//import org.apache.http.entity.StringEntity;

import com.google.appengine.labs.repackaged.org.json.JSONArray;
import com.google.appengine.labs.repackaged.org.json.JSONException;
import com.google.appengine.labs.repackaged.org.json.JSONObject;

@SuppressWarnings("serial")
public class CollabrativewhiteboardServlet extends HttpServlet {

	String responseString = "defailt ";
	
	HttpServletResponse response = null;
	public void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
		
		
		
		resp.setContentType("application/json");
		resp.getWriter().println(responseString);
	 
//		resp = this.response;
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
		    
//		    try {
//		        JSONObject jsonObject = JSONObject.formObject(sb.toString());
//		      } catch (ParseException e) {
//		        // crash and burn
//		        throw new IOException("Error parsing JSON request string");
//		      }
		    response.getWriter().println(sb.toString());

		    
//		    this.response = response;
		    this.responseString = sb.toString();
		    // now make this data available to other devices
		    

}}

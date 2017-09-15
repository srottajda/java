import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.fluent.Request;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.ContentType;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.util.ISO8601DateFormat;

import java.util.List;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public class Http {
	
    public static void main(String[] args) throws ClientProtocolException, URISyntaxException, IOException {
        try {
            String hello = Request.Get("http://chitchat.andrej.com")
                                  .execute()
                                  .returnContent().asString();
            System.out.println(hello);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public static List<User> uporabniki() throws ClientProtocolException, IOException {
    	String responseBody = Request.Get("http://chitchat.andrej.com/users?stopcache=")
                                  .execute()
                                  .returnContent().asString();
        ObjectMapper mapper = new ObjectMapper();
        	mapper.setDateFormat(new ISO8601DateFormat());
        	TypeReference<List<User>> t = new TypeReference<List<User>>() { };
        	return mapper.readValue(responseBody, t);
        } 
    
    public static void login(String ime) throws URISyntaxException, ClientProtocolException, IOException {
    	 
    	URI uri = new URIBuilder("http://chitchat.andrej.com/users")
    	          .addParameter("username", ime)
    	          .build();

    	  String responseBody = Request.Post(uri)
    	                               .execute()
    	                               .returnContent()
    	                               .asString();

    	  System.out.println(responseBody);
    }
    public static void logout(String ime) throws URISyntaxException, ClientProtocolException, IOException{
    	URI uri = new URIBuilder("http://chitchat.andrej.com/users")
            .addParameter("username", ime)
            .build();

    	String responseBody = Request.Delete(uri)
                                 .execute()
                                 .returnContent()
                                 .asString();
    	
    	System.out.println(responseBody);
    }
    public static void sendMessage(String username, Boolean javno, String recipient, String msg) throws URISyntaxException, ClientProtocolException, IOException {
    	URI uri = new URIBuilder("http://chitchat.andrej.com/messages")
    	          .addParameter("username", username)
    	          .build();
    	
    	  ObjectMapper mapper = new ObjectMapper();
    	  mapper.setDateFormat(new ISO8601DateFormat());
    	  
    	  Message sporocilo;
    	  if (javno.equals(true)) {
    		  sporocilo = new Message(true, msg);
    	  } else {
    		  sporocilo = new Message(false, recipient, msg);
    	  }
    	  
    	  String sporocilo_str = mapper.writeValueAsString(sporocilo);
    	  String responseBody = Request.Post(uri)
    	          .bodyString(sporocilo_str, ContentType.APPLICATION_JSON)
    	          .execute()
    	          .returnContent()
    	          .asString();

    	  System.out.println(responseBody);
    }
    
    public static void recieveMessage (String username) throws URISyntaxException, ClientProtocolException, IOException{
    	URI uri = new URIBuilder("http://chitchat.andrej.com/messages")
    	          .addParameter("username", username)
    	          .build();

    	  String responseBody = Request.Get(uri)
    	                               .execute()
    	                               .returnContent()
    	                               .asString();

    	  System.out.println(responseBody);
    }
    
}
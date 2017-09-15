import java.io.IOException;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

//import javax.swing.text.BadLocationException;

import org.apache.http.client.fluent.Request;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.util.ISO8601DateFormat;

public class UsersRobot extends TimerTask {

    private ChatFrame chat;

    public void activate() {
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(this, 1000, 2000);
    }

    public UsersRobot(ChatFrame chat) {
        this.chat = chat;
    }
    
    @Override
    public void run(){
        try {String users = Request.Get("http://chitchat.andrej.com/users")
        	  .execute()
  	          .returnContent()
  	          .asString();
        	ObjectMapper mapper = new ObjectMapper();
        	mapper.setDateFormat(new ISO8601DateFormat());
        
        	TypeReference<List<User>> t = new TypeReference<List<User>>() { };
        	List<User> prijavljeni = mapper.readValue(users, t);
        	chat.deleteAll();      	
        	if (prijavljeni.isEmpty()) {
        		chat.noActiveUsers();        		
        	} else {        		             
            for (User person : prijavljeni) {
            	chat.addUser(person);
            }
        	}
        	} catch (IOException e) {
        		e.printStackTrace();
        	}
    }
}

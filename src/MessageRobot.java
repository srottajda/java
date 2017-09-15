import java.util.List;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.http.client.fluent.Request;
import org.apache.http.client.utils.URIBuilder;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.util.ISO8601DateFormat;

public class MessageRobot extends TimerTask {

    private ChatFrame chat;

    public void activate() {
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(this, 1000, 2000);
    }

    public MessageRobot(ChatFrame chat) {
        this.chat = chat;
    }
    
    @Override
    public void run(){
    	if (chat.getStatus()) {
    			try {URI uri = new URIBuilder("http://chitchat.andrej.com/messages")
    				.addParameter("username", chat.getName())
    				.build();
    			String responseBody = Request.Get(uri)
        			 .execute()
        			 .returnContent()
        			 .asString();
    			ObjectMapper mapper = new ObjectMapper();
    			mapper.setDateFormat(new ISO8601DateFormat());
    			TypeReference<List<Message>> t = new TypeReference<List<Message>>() { };
    			List<Message> prejetaSporocila = mapper.readValue(responseBody, t);
    			if (prejetaSporocila.isEmpty()) {
    				chat.noNewMessages();
    			} else {
    				for (Message sporocilo : prejetaSporocila) {
        			 chat.addNewMessage(sporocilo);
    			}
    			}
    		} catch (URISyntaxException | IOException e) {
				e.printStackTrace();
    		}
    }
}}
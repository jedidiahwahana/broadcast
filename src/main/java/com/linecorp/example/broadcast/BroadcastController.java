
package com.linecorp.example.broadcast;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;
import java.util.List;
import java.util.Arrays;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.*;
import com.linecorp.example.broadcast.model.*;
import com.google.gson.Gson;

import retrofit2.Response;
import com.linecorp.bot.model.*;
import com.linecorp.bot.model.message.*;
import com.linecorp.bot.model.response.BotApiResponse;
import com.linecorp.bot.client.LineSignatureValidator;
import com.linecorp.bot.client.LineMessagingServiceBuilder;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.entity.StringEntity;
import org.apache.http.HttpResponse;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.client.methods.HttpPost;

@RestController
@RequestMapping(value="/")
public class BroadcastController
{
    @Autowired
    @Qualifier("com.linecorp.channel_secret")
    String lChannelSecret;
    
    @Autowired
    @Qualifier("com.bot.user1")
    String user1;
    
    @Autowired
    @Qualifier("com.bot.user2")
    String user2;
    
    @Autowired
    @Qualifier("com.linecorp.channel_access_token")
    String lChannelAccessToken;
    
    final private static String image_url = "https://res.cloudinary.com/jedidiahwahana/image/upload/v1481606864/sample.jpg";
    
    @RequestMapping(value="/phonebook", method=RequestMethod.POST)
    public ResponseEntity<String> callback(
                    @RequestHeader("X-Line-Signature") String aXLineSignature,
                    @RequestBody String aPayload)
    {
        final String text=String.format("The Signature is: %s",
                                        (aXLineSignature!=null && aXLineSignature.length() > 0) ? aXLineSignature : "N/A");
        
        System.out.println(text);
        
        final boolean valid=new LineSignatureValidator(lChannelSecret.getBytes()).validateSignature(aPayload.getBytes(), aXLineSignature);
        
        System.out.println("The signature is: " + (valid ? "valid" : "tidak valid"));
        
        //Get events from source
        if(aPayload!=null && aPayload.length() > 0)
        {
            System.out.println("Payload: " + aPayload);
        }
        
        Gson gson = new Gson();
        Payload payload = gson.fromJson(aPayload, Payload.class);
        
        String idTarget = " ";
        if (payload.events[0].source.type.equals("group")){
            idTarget = payload.events[0].source.groupId;
        } else if (payload.events[0].source.type.equals("room")){
            idTarget = payload.events[0].source.roomId;
        } else if (payload.events[0].source.type.equals("user")){
            idTarget = payload.events[0].source.userId;
        }
        
        String msgText = " ";
        if (!payload.events[0].message.type.equals("text")){
            replyToUser(payload.events[0].replyToken, "Unknown message");
        } else {
            msgText = payload.events[0].message.text;
            broadcastToUser();
        }
        
        return new ResponseEntity<String>(HttpStatus.OK);
    }
    
    private void replyToUser(String rToken, String messageToUser){
        TextMessage textMessage = new TextMessage(messageToUser);
        ReplyMessage replyMessage = new ReplyMessage(rToken, textMessage);
        try {
            Response<BotApiResponse> response = LineMessagingServiceBuilder
            .create(lChannelAccessToken)
            .build()
            .replyMessage(replyMessage)
            .execute();
            System.out.println("Reply Message: " + response.code() + " " + response.message());
        } catch (IOException e) {
            System.out.println("Exception is raised ");
            e.printStackTrace();
        }
    }
    
    private void broadcastToUser(){
        String url = "https://api.line.me/v2/bot/message/multicast";
        HttpClient client = HttpClientBuilder.create().build();
        HttpPost post = new HttpPost(url);
        
        try{
            String emo = new String(Character.toChars(0x10008D));

            // add header
            post.setHeader("Content-Type", "application/json; charset=UTF-8");
            post.setHeader("Authorization", "Bearer " + lChannelAccessToken);
            post.setHeader("User-Agent", "line-botsdk-java/1.4.0");
            
            String jsonData = "{\"to\":[\"" + user1 + "\",\"" + user2 + "\"],\"messages\":[{\"type\":\"text\",\"text\":\"Hello with broadcast " + emo + "\"},{\"type\":\"sticker\",\"packageId\":\"2\",\"stickerId\":\"144\"}]}";

            System.out.println("Request body: " + jsonData);
            StringEntity params =new StringEntity(jsonData);
        
            post.setEntity(params);
        
            HttpResponse response = client.execute(post);
            System.out.println("Response Code : "
                                       + response.getStatusLine().getStatusCode());
        
            BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
        
            StringBuffer result = new StringBuffer();
            String line = "";
            while ((line = rd.readLine()) != null) {
                result.append(line);
            }
        } catch (IOException e){
            System.out.println("Exception is raised ");
            e.printStackTrace();
        }
    }
}

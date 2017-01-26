
package com.linecorp.example.broadcast;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.*;
import org.springframework.core.env.Environment;

@Configuration
@PropertySource("classpath:application.properties")
public class Config
{
    @Autowired
    Environment mEnv;
    
    @Bean(name="com.linecorp.channel_secret")
    public String getChannelSecret()
    {
        return mEnv.getProperty("com.linecorp.channel_secret");
    }
    
    @Bean(name="com.linecorp.channel_access_token")
    public String getChannelAccessToken()
    {
        return mEnv.getProperty("com.linecorp.channel_access_token");
    }
    
    @Bean(name="com.bot.user1")
    public String getUser1()
    {
        return mEnv.getProperty("com.bot.user1");
    }
    
    @Bean(name="com.bot.user2")
    public String getUser2()
    {
        return mEnv.getProperty("com.bot.user2");
    }
};

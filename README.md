# broadcast #

This repository demonstrates how to create a bot which can broadcast message and can send sticker with **LINE Messaging API** using **Spring Framework** and deployed in **Heroku**.

### How do I get set up? ###
* Make LINE@ Account with Messaging API enabled
> [LINE Business Center](https://business.line.me/en/)

* Register your Webhook URL
	1. Open [LINE Developer](https://developers.line.me/)
	2. Choose your channel
	3. Edit "Basic Information"

* Add `application.properties` file in *src/main/resources* directory, and fill it with your channel secret and channel access token, like the following:

	```ini
com.linecorp.channel_secret=<your_channel_secret>
com.linecorp.channel_access_token=<your_channel_access_token>
	```
	
* JSON data for broadcast a message

	```JSON
	{
		"to":
			[
			"<user1>",
			"<user2>"
			],
		"messages":
			[
				{
					"type":"text",
					"text":"Hello with broadcast"
				},
				{
					"type":"sticker",
					"packageId":"2",
					"stickerId":"144"
				}
			]
	}
	```
	
* Compile
 
    ```bash
    $ gradle clean build
    ```
* Deploy
 	
 	```bash
	$ git push heroku master
	```  

* Run Server

    ```bash
    $ heroku ps:scale web=1
    ```

### How do I contribute? ###

* Add your name and e-mail address into CONTRIBUTORS.txt

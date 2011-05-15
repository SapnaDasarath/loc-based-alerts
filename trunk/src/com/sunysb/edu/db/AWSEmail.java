package com.sunysb.edu.db;

import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedList;
import java.util.Properties;

import android.util.Log;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;

import com.amazonaws.services.simpleemail.AmazonSimpleEmailServiceClient;

import com.amazonaws.services.simpleemail.model.Body;
import com.amazonaws.services.simpleemail.model.Content;
import com.amazonaws.services.simpleemail.model.Message;
import com.amazonaws.services.simpleemail.model.Destination;
import com.amazonaws.services.simpleemail.model.SendEmailRequest;
import com.amazonaws.services.simpleemail.model.SendEmailResult;

public class AWSEmail {

	public void sendMail(String sender, LinkedList<String> recipients,
			String subject, String body) {
		Destination destination = new Destination(recipients);

		Content subjectContent = new Content(subject);
		Content bodyContent = new Content(body);
		Body msgBody = new Body(bodyContent);
		Message msg = new Message(subjectContent, msgBody);

		SendEmailRequest request = new SendEmailRequest(sender, destination,
				msg);
		AmazonSimpleEmailServiceClient sesClient = new AmazonSimpleEmailServiceClient(
				getCredentials());
		SendEmailResult result = sesClient.sendEmail(request);

		System.out.println(result);
	}

	/**
	 * load the file with access key use it to authenticate with server
	 */
	private AWSCredentials getCredentials() {
		Properties properties = new Properties();
		InputStream stream = null;
		try {
			stream = getClass()
					.getResourceAsStream("AwsCredentials.properties");
			properties.load(stream);
		} catch (IOException e) {
			Log.e("LBA", "Not able to access key files");
		} finally {
			try {
				stream.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		String accessKeyId = properties.getProperty("accessKey");
		String secretKey = properties.getProperty("secretKey");

		if ((accessKeyId == null) || (accessKeyId.equals(""))
				|| (accessKeyId.equals("CHANGEME")) || (secretKey == null)
				|| (secretKey.equals("")) || (secretKey.equals("CHANGEME"))) {
			Log.e("LBA", "Aws Credentials not configured correctly.");
		} else {
			return new BasicAWSCredentials(properties.getProperty("accessKey"),
					properties.getProperty("secretKey"));
		}
		return null;
	}
}

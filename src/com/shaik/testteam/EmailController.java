package com.shaik.testteam;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.Base64;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.GmailScopes;
import com.google.api.services.gmail.model.Message;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.Random;
import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import org.apache.log4j.Logger;

public class EmailController 
{
	protected final static Logger log= Logger.getLogger(EmailController.class);

	private static final String APPLICATION_NAME = "Gmail API";
	private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
	private static final List<String> SCOPES = Arrays.asList(GmailScopes.MAIL_GOOGLE_COM);
	private static final java.io.File DATA_STORE_DIR = new java.io.File(System.getProperty("user.home"), ".credentials/2/sheets.googleapis.com-java-quickstart.json");
	private static final String CREDENTIALS_FILE_PATH = "/credentials.json";

	/**
	 * Creates an authorized Credential object.
	 * @param HTTP_TRANSPORT The network HTTP Transport.
	 * @return An authorized Credential object.
	 * @throws IOException If the credentials.json file cannot be found.
	 */
	private static Credential getCredentials(final NetHttpTransport HTTP_TRANSPORT) throws IOException 
	{
		LocalServerReceiver receiver = null;
		GoogleAuthorizationCodeFlow flow = null;

		try {
			// Load client secrets.
			InputStream in = EmailController.class.getResourceAsStream(CREDENTIALS_FILE_PATH);
			if (in == null) {
				throw new FileNotFoundException("Resource not found: " + CREDENTIALS_FILE_PATH);
			}
			GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));

			// Build flow and trigger user authorization request.
			flow = new GoogleAuthorizationCodeFlow.Builder(
									HTTP_TRANSPORT, JSON_FACTORY, clientSecrets, SCOPES)
									.setDataStoreFactory(new FileDataStoreFactory(DATA_STORE_DIR))
									.setAccessType("offline")
									.build();
			receiver = new LocalServerReceiver.Builder().setPort(8888).build();
		}catch(Exception ex)
		{
			log.error("Class: EmailController" +"| Method: getCredentials" +"| Exception desc : " + ex.getMessage());
		}
		return new AuthorizationCodeInstalledApp(flow, receiver).authorize("user");
	}

	/**
	 * Create a MimeMessage using the parameters provided.
	 *
	 * @param TO :: Email address of the receiver.
	 * @param strToName :: Name of the Recipients
	 * @param years :: Number of Years
	 * @param Occasion :: WorkAnniversary, BirthDay
	 */
	public static MimeMessage createWorkAnniversaryEmailWithAttachment(String TO, String strToName, String Occasion, int years) 
	{
		MimeMessage email = null;
		String strSubject;
		String message;
		try {
			Properties props = new Properties();
			Session session = Session.getDefaultInstance(props, null);
			email = new MimeMessage(session);

			email.setFrom(new InternetAddress("abc@gmail.com"));
			email.addRecipient(javax.mail.Message.RecipientType.TO, new InternetAddress(TO));
			//Adding CC Email Address
			String ccEmailAddress = "xyz@abc.com" +"," +"abc@abc.com";
			//String ccEmailAddress = "abc@yahoo.com" +"," +"abc@abc.com";
			InternetAddress[] iAdressArray = InternetAddress.parse(ccEmailAddress);
			email.addRecipients(javax.mail.Message.RecipientType.CC, iAdressArray);

			if(Occasion.equalsIgnoreCase("WorkAnniversary"))
			{
				strSubject = "Congratulations "+strToName+" !!!";
			}
			else {
				strSubject = "Happy Birthday - "+strToName+" !!!";
			}
			email.setSubject(strSubject);

			MimeBodyPart messageBodyPart = new MimeBodyPart();
			if(Occasion.equalsIgnoreCase("WorkAnniversary"))
			{
				message = "Hi "+strToName+"\n"+"\n"+" Congratulations on your "+years+" years of work Anniversary";
			}
			else {
				message = "Hi "+strToName+" \n"+"\n"+" Wishing you the best on your birthday and everything good in the year ahead... ";
			}
			String htmlText = "<H1>" + message + "</H1><img src=\"cid:image\">";
			messageBodyPart.setContent(htmlText, "text/html");
			Multipart multipart = new MimeMultipart();
			multipart.addBodyPart(messageBodyPart);

			messageBodyPart = new MimeBodyPart();
			final File dir;
			if(Occasion.equalsIgnoreCase("WorkAnniversary"))
			{
				dir = new File(System.getProperty("user.dir") +"//Images//AnniversaryImages//");
			}
			else {
				dir = new File(System.getProperty("user.dir") +"//Images//BirthdayImages//");
			}

			File[] files = dir.listFiles();
			Random rand = new Random();
			File file = files[rand.nextInt(files.length)];
			String str = file.getAbsolutePath();
			DataSource fds = new FileDataSource(str);

			messageBodyPart.setDataHandler(new DataHandler(fds));
			messageBodyPart.setHeader("Content-ID", "<image>");

			multipart.addBodyPart(messageBodyPart);

			email.setContent(multipart);
		}catch(Exception ex)
		{
			log.error("Class: EmailController" +"| Method: createEmailWithAttachment" +"| Exception desc : " + ex.getMessage());
		}
		return email;
	}

	/**
	 * Create a message from an email.
	 *
	 * @param emailContent Email to be set to raw of message
	 * @return a message containing a base64url encoded email
	 * @throws IOException
	 * @throws MessagingException
	 */
	private static Message createMessageWithEmail(MimeMessage emailContent) 
	{
		Message message = null;
		try {
			ByteArrayOutputStream buffer = new ByteArrayOutputStream();
			emailContent.writeTo(buffer);
			byte[] bytes = buffer.toByteArray();
			String encodedEmail = Base64.encodeBase64URLSafeString(bytes);
			message = new Message();
			message.setRaw(encodedEmail);
		}catch(Exception ex)
		{
			log.error("Class: EmailController" +"| Method: createMessageWithEmail" +"| Exception desc : " + ex.getMessage());
		}
		return message;
	}

	/**
	 * Send an email from the user's mailbox to its recipient.
	 *
	 * @param service Authorized Gmail API instance.
	 * @param userId User's email address.
	 * can be used to indicate the authenticated user.
	 * @param emailContent Email to be sent.
	 */
	private static Message sendMessage(Gmail service, String strUserId, MimeMessage emailContent) 
	{
		Message message = null;
		try {
			message = createMessageWithEmail(emailContent);
			message = service.users().messages().send(strUserId, message).execute();
			System.out.println("Message id: " + message.getId());
			System.out.println(message.toPrettyString());
		}catch(Exception ex)
		{
			log.error("Class: EmailController" +"| Method: sendMessage" +"| Exception desc : " + ex.getMessage());
		}
		return message;
	}

	public static void sendAnniversaryEmailWithAttachments(String EmailID, String strToName, int years) 
	{
		try {
			// Build a new authorized API client service.
			final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
			Gmail service = new Gmail.Builder(HTTP_TRANSPORT, JSON_FACTORY, getCredentials(HTTP_TRANSPORT))
									.setApplicationName(APPLICATION_NAME)
									.build();
			MimeMessage  test = createWorkAnniversaryEmailWithAttachment(EmailID, strToName, "WorkAnniversary", years);
			sendMessage(service, "abc@gmail.com", test);
		}catch(Exception ex)
		{
			log.error("Class: EmailController" +"| Method: sendEmailWithAttachmentsProcess" +"| Exception desc : " + ex.getMessage());
		}
	}

	public static void sendBirthDayEmailWithAttachments(String EmailID, String strToName) 
	{
		try {
			// Build a new authorized API client service.
			final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
			Gmail service = new Gmail.Builder(HTTP_TRANSPORT, JSON_FACTORY, getCredentials(HTTP_TRANSPORT))
									.setApplicationName(APPLICATION_NAME)
									.build();
			//File file = new File("D:\\Development\\EmailNotifications\\BirthdayImages\\0.jpg");
			MimeMessage  test =  createWorkAnniversaryEmailWithAttachment(EmailID, strToName, "BirthDay", 0);
			sendMessage(service, "abc@gmail.com", test);
		}catch(Exception ex)
		{
			log.error("Class: EmailController" +"| Method: sendEmailWithAttachmentsProcess" +"| Exception desc : " + ex.getMessage());
		}
	}
}

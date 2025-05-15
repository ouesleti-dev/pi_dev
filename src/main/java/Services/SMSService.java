package Services;

import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;

public class SMSService {


    private static final String ACCOUNT_SID = "AC3f45973d044f3203e56cad6c14ef2081";
    private static final String AUTH_TOKEN = "f285cb347b87ad573e85bfbe36c5fbb6";
    private static final String FROM_NUMBER = "+19787423129";
    static {
        Twilio.init(ACCOUNT_SID, AUTH_TOKEN);
    }

    public static void sendSMS(String toNumber, String messageBody) {
        Message message = Message.creator(
                        new PhoneNumber(toNumber),
                        new PhoneNumber(FROM_NUMBER),
                        messageBody)
                .create();

        System.out.println("SMS envoyé avec l'ID: " + message.getSid());
    }
}
package com.example.udhan.voice3;

import android.os.AsyncTask;
import java.util.Properties;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.NoSuchProviderException;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.Flags;
import javax.mail.search.FlagTerm;


public class Receiver extends AsyncTask<Void,Void,Message[]> {

    private String hostval;
    private String uname ;
    private String pwd;

    public AsyncResponse delegate = null;
    public Receiver(){
        uname=Config.EMAIL;
        pwd=Config.PASSWORD;
        hostval = "imap.gmail.com" ;
    }

    @Override
    protected void onPostExecute(Message[] output) {
        delegate.getMails(output);
    }
    //Retrieve mails from inbox
    @Override
    protected Message[] doInBackground(Void... voids) {

        try {
            Properties properties = new Properties();
            properties.put("mail.store.protocol", "imaps");

            Session emailSession = Session.getInstance(properties);
            Store store = emailSession.getStore();
            store.connect(hostval, uname, pwd);

            Folder emailFolderObj = store.getFolder("INBOX");
            emailFolderObj.open(Folder.READ_ONLY);
            Flags seen = new Flags(Flags.Flag.SEEN);
            FlagTerm unseenFlagTerm = new FlagTerm(seen,false);

            Message[] messageobjs = emailFolderObj.search(unseenFlagTerm);

            for (int i = 0, n = messageobjs.length; i < n; i++) {
                Message indvidualmsg = messageobjs[i];
                System.out.print(indvidualmsg.getFrom()[0]);

            }
            emailFolderObj.close(false);
            store.close();
            return messageobjs;
        } catch (NoSuchProviderException exp) {
            exp.printStackTrace();
        } catch (MessagingException exp) {
            exp.printStackTrace();
        } catch (Exception exp) {
            exp.printStackTrace();
        }
        return null;
    }
}
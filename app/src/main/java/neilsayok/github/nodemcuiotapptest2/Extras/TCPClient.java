package neilsayok.github.nodemcuiotapptest2.Extras;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.TimeoutException;

public class TCPClient implements Runnable{

    private String ip;
    private int port;
    private Socket socket;
    private DataInputStream din;
    private DataOutputStream dout;
    private boolean mRun;
    private String serverMsg;
    private OnMessageReceivedListener msgRecievedListener;
    private Thread serverReplyThread,clientMsgThread,heartBeatThread;
    private Runnable serverReplyRunnable,clientMsgRunnable,heartBeatRunnable;
    private Queue<String> msgQueue;
    private boolean connectStat;
    Map<String, String> heartBeat;


    public TCPClient(String ip, int port, OnMessageReceivedListener msgRecievedListener) {
        this.ip = ip;
        this.port = port;
        this.msgRecievedListener = msgRecievedListener;
        mRun = true;
        connectStat = false;
        serverMsg = "";
        msgQueue = new LinkedList<>();
        heartBeat = new HashMap<>();
    }


    @Override
    public void run() {
        try {
            socket = new Socket(ip,port);
            socket.setSoTimeout(5000);
            din = new DataInputStream(socket.getInputStream());
            dout = new DataOutputStream(socket.getOutputStream());
            serverReplyRunnable = new Runnable() {
                @Override
                public void run() {
                    try {
                        while (mRun){
                            byte[] msgBytes = new byte[din.available()];
                            din.read(msgBytes);
                            serverMsg = new String(msgBytes);

                            if (!serverMsg.isEmpty() && msgRecievedListener != null){
                                if (socket.getInetAddress().isReachable(5000) ){
                                    if(serverMsg.equalsIgnoreCase("{'status':'alive'}")){
                                        connectStat = true;
                                    }else if (serverMsg.equalsIgnoreCase("{'status':'connected'}")){
                                        connectStat = true;
                                    }else{
                                        msgRecievedListener.messageReceived(serverMsg);
                                    }
                                }else {
                                    stop();
                                }


                            }
                            serverMsg = null;
                        }
                    }catch (SocketTimeoutException t){
                        stop();
                        t.printStackTrace();
                    }
                    catch (Exception e){
                        e.printStackTrace();
                    }
                }
            };
            serverReplyThread = new Thread(serverReplyRunnable);
            serverReplyThread.start();

            clientMsgRunnable = new Runnable() {
                @Override
                public void run() {
                    while (mRun){
                        if (dout != null && !msgQueue.isEmpty()){
                            try {
                                dout.write(msgQueue.poll().getBytes());
                                dout.flush();
                            } catch (IOException e) {
                                stop();
                                e.printStackTrace();
                            }

                        }
                    }
                }
            };
            clientMsgThread = new Thread(clientMsgRunnable);
            clientMsgThread.start();

            heartBeatRunnable = new Runnable() {
                @Override
                public void run() {
                    while (mRun){
                        heartBeat.put("check","heartbeat");
                        sendMessage(heartBeat);
                        heartBeat.clear();
                        new OnMessageReceivedListener(){
                            @Override
                            public void messageReceived(String message) {
                                //Log.d("Message",message);
                            }
                        };


                        //Log.d("ConnectStat",String.valueOf(connectStat));
                        try {
                            heartBeatThread.sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                    }

                }
            };
            heartBeatThread = new Thread(heartBeatRunnable);
            heartBeatThread.start();


        }catch (Exception e){
            e.printStackTrace();
        }

    }





    public void sendMessage(final String msg){
        msgQueue.add(msg);
    }

    public void sendMessage(final Map<String, String> msgMap){
        JSONObject jsonObject = new JSONObject();

        for (Map.Entry<String, String> entry : msgMap.entrySet()){
            try {
                jsonObject.put(entry.getKey(),entry.getValue());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        msgQueue.add(jsonObject.toString());

    }

    public void sendMessage(final JSONObject jsonObject){
        msgQueue.add(jsonObject.toString());
    }

    public boolean isAlive(){
        return this.connectStat;
    }

    public void stop(){
        if (msgQueue.isEmpty()){
            if (mRun) {
                mRun = false;
                connectStat = false;
                Log.d("Client", "Stopped");
            }

            try {
                if (socket != null){
                    socket.close();
                }
                if (dout != null){
                    dout.close();
                }
                if (din != null){
                    din.close();
                }
            }catch (IOException e) {
                e.printStackTrace();
            } ;
        }else {
            this.stop();
        }


    }

    public void start(){
        new Thread(this).start();
    }

    public interface OnMessageReceivedListener {
        public void messageReceived(String message);
    }






}

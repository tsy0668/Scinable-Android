package org.techtown.socket;

import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

public class MainActivity extends AppCompatActivity {

    EditText editText;

    TextView textView;
    TextView textView2;

    Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editText = findViewById(R.id.editText);
        textView = findViewById(R.id.textView);
        textView2 = findViewById(R.id.textView2);

        Button button = findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String data = editText.getText().toString();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        send(data);
                    }
                }).start();
            }
        });

        Button button2 = findViewById(R.id.button2);
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        startServer();
                    }
                }).start();
            }
        });
    }

    public void printClientLog(final String data) {
        Log.d("MainActivity", data);
        handler.post(new Runnable() {
            @Override
            public void run() {
                textView.append(data + "\n");
            }
        });
    }

    public void printServerLog(final String data) {
        Log.d("MainActivity", data);

        handler.post(new Runnable() {
            @Override
            public void run() {
                textView2.append(data + "\n");
            }
        });
    }

    public void send(String data) {
        try {
            int portNumber = 5001;
            Socket sock = new Socket("localhost", portNumber);
            printClientLog("Connect socket");
            ObjectOutputStream outstream = new ObjectOutputStream(sock.getOutputStream());
            outstream.writeObject(data);
            outstream.flush();
            printClientLog("Transmit data");

            ObjectInputStream instream = new ObjectInputStream(sock.getInputStream());
            printClientLog("Receive for server " + instream.readObject());
            sock.close();
        } catch(Exception ex) {
            ex.printStackTrace();
        }
    }

    public void startServer() {
        try {
            int portNumber = 5001;

            ServerSocket server = new ServerSocket(portNumber);
            printServerLog("Server start" + portNumber);
            while (true) {
                Socket sock = server.accept();
                InetAddress clientHost = sock.getLocalAddress();
                int clientPort = sock.getPort();
                printServerLog("Connection client " + clientHost + " : " + clientPort);

                ObjectInputStream instream = new ObjectInputStream(sock.getInputStream());
                Object obj = instream.readObject();
                printServerLog("Receive data " + obj);

                ObjectOutputStream outstream = new ObjectOutputStream(sock.getOutputStream());
                outstream.writeObject(obj + " from Server.");
                outstream.flush();
                printServerLog("Tranmit data.");

                sock.close();
            }
        }catch(Exception ex) {
                ex.printStackTrace();
        }
    }
}

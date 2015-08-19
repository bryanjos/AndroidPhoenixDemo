package com.basstype.androidphoenixdemo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.phoenixframework.channels.*;

import java.io.IOException;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {
    Socket socket;
    Channel channel;

    @Bind(R.id.messages)
    TextView messages;

    @Bind(R.id.message_box)
    EditText messageBox;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        try {
            socket = new Socket(BuildConfig.HOST);
            socket.connect();

            channel = socket.chan("rooms:lobby", null);

            channel.join()
                    .receive("ignore", new IMessageCallback() {
                        @Override
                        public void onMessage(Envelope envelope) {
                            Log.d(MainActivity.class.getName(), "auth error");
                            Log.d(MainActivity.class.getName(), envelope.toString());
                        }
                    })
                    .receive("ok", new IMessageCallback() {
                        @Override
                        public void onMessage(Envelope envelope) {
                            Log.d(MainActivity.class.getName(), "join ok");
                            Log.d(MainActivity.class.getName(), envelope.toString());
                            updateMessages("connected");
                        }
                    });

            channel.on("new:msg", new IMessageCallback() {
                @Override
                public void onMessage(Envelope envelope) {
                    Log.d(MainActivity.class.getName(), "new:msg");
                    Log.d(MainActivity.class.getName(), envelope.toString());

                    String user = envelope.getPayload().get("user").asText();

                    if(user == null || user.isEmpty()){
                        user = "anonymous";
                    }

                    String message = envelope.getPayload().get("body").asText();

                    updateMessages(user, message);
                }
            });

            channel.onClose(new IMessageCallback() {
                @Override
                public void onMessage(Envelope envelope) {
                    Log.d(MainActivity.class.getName(), "Channel Closed");
                }
            });

            channel.onError(new IErrorCallback() {
                @Override
                public void onError(String reason) {
                    Log.d(MainActivity.class.getName(), reason);
                }
            });

        } catch (IOException e) {

        }
    }


    @OnClick(R.id.send_chat)
    public void onSendChatClicked(View v){
        String message = messageBox.getText().toString();

        if (!message.isEmpty()){
            try {
                ObjectNode node = new ObjectNode(JsonNodeFactory.instance)
                        .put("name", "Android")
                        .put("body", message);

                channel.push("new:msg", node);
                messageBox.setText("");
            }catch (IOException e){

            }
        }
    }

    public void updateMessages(final String message){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                messages.setText(
                        messages.getText().toString() + "[" + message + "]\n\n"
                );

            }
        });


    }

    public void updateMessages(final String user, final String message){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                messages.setText(
                        messages.getText().toString() +
                                "[" + user + "] " + message + "\n\n"
                );

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


}

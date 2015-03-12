package com.norsemen.longrunningservice;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.os.Messenger;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.TextView;

import com.norsemen.longrunningservice.service.LongRunningService;
import com.norsemen.longrunningservice.util.Constants;


public class MainActivity extends ActionBarActivity {
    private Button startServiceButton;
    private TextView messagePresentationLabel;
    private TextView messageNumberLabel;
    private TextView messagePresentationText;
    private TextView messageNumberText;

    private boolean labelsVisible = false;

    private Messenger messenger;

    private Animation textFadeIn;
    private Animation textFadeOut;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.main);

        messenger = new Messenger(new MessageHandler(this));

        textFadeIn = AnimationUtils.loadAnimation(this, R.anim.fade_in);
        textFadeOut = AnimationUtils.loadAnimation(this, R.anim.fade_out);

        startServiceButton = (Button)findViewById(R.id.start_button);
        messagePresentationLabel = (TextView)findViewById(R.id.message_presentation_label);
        messageNumberLabel = (TextView)findViewById(R.id.message_nbr_label);
        messagePresentationText = (TextView)findViewById(R.id.message_presentation);
        messageNumberText = (TextView)findViewById(R.id.message_nbr);

        startServiceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent longRunningService = new Intent(MainActivity.this, LongRunningService.class);

                longRunningService.putExtra(Constants.MESSENGER, messenger);

                startService(longRunningService);
            }
        });
    }

    class MessageHandler extends Handler {
        private Context context;

        public MessageHandler(Context context){
            this.context = context;
        }

        @Override
        public void handleMessage(Message message) {
            if (!labelsVisible) {
                messagePresentationLabel.startAnimation(textFadeIn);
                messagePresentationLabel.setVisibility(View.VISIBLE);

                messageNumberLabel.startAnimation(textFadeIn);
                messageNumberLabel.setVisibility(View.VISIBLE);

                labelsVisible = true;
            }

            messagePresentationText.startAnimation(textFadeOut);
            messageNumberText.startAnimation(textFadeOut);

            switch (message.what) {
                case Constants.MESSAGE_ONE: {
                    messageNumberText.setText("Message 1");

                    break;
                }
                case Constants.MESSAGE_TWO: {
                    messageNumberText.setText("Message 2");

                    break;
                }
                case Constants.MESSAGE_THREE: {
                    messageNumberText.setText("Message 3");

                    break;
                }
                case Constants.MESSAGE_FOUR: {
                    messageNumberText.setText("Message 4");

                    break;
                }
                default: {
                    super.handleMessage(message);
                }
            }

            messagePresentationText.setText((String)message.obj);
            messageNumberText.startAnimation(textFadeIn);
            messagePresentationText.startAnimation(textFadeIn);
        }
    }
}

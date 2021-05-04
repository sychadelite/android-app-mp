package com.barjlazuardi.demusique;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    ImageView play, prev, next, imageView;
    TextView songTitle;
    SeekBar mSeekBarTime, mSeekBarVol;
    static MediaPlayer mMediaPlayer;
    private Runnable runnable;
    private AudioManager mAudioManager;
    int currentIndex = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);

        // initializing views

        play = findViewById(R.id.play);
        prev = findViewById(R.id.prev);
        next = findViewById(R.id.next);
        songTitle = findViewById(R.id.songTitle);
        imageView = findViewById(R.id.imageView);
        mSeekBarTime = findViewById(R.id.seekBarTime);
        mSeekBarVol = findViewById(R.id.seekBarVol);

        // creating an ArrayList to store our songs

        final ArrayList<Integer> songs = new ArrayList<>();

        songs.add(0, R.raw.garuda_pancasila);
        songs.add(1, R.raw.indonesia_pusaka);
        songs.add(2, R.raw.indonesia_raya);
        songs.add(3, R.raw.maju_tak_gentar);
        songs.add(4, R.raw.rayuan_pulau_kelapa);

        // intializing mediaplayer

        mMediaPlayer = MediaPlayer.create(getApplicationContext(), songs.get(currentIndex));

        // seekbar volume

        int maxV = mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        int curV = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        mSeekBarVol.setMax(maxV);
        mSeekBarVol.setProgress(curV);

        mSeekBarVol.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, progress, 0);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        //above seekbar volume

        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSeekBarTime.setMax(mMediaPlayer.getDuration());
                if (mMediaPlayer != null && mMediaPlayer.isPlaying()) {
                    mMediaPlayer.pause();
                    play.setImageResource(R.drawable.play);
                } else {
                    mMediaPlayer.start();
                    play.setImageResource(R.drawable.pause);
                }

                songNames();

            }
        });

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mMediaPlayer != null) {
                    play.setImageResource(R.drawable.pause);
                }

                if (currentIndex < songs.size() - 1) {
                    currentIndex++;
                } else {
                    currentIndex = 0;
                }

                if (mMediaPlayer.isPlaying()) {
                    mMediaPlayer.stop();
                }

                mMediaPlayer = MediaPlayer.create(getApplicationContext(), songs.get(currentIndex));

                mMediaPlayer.start();
                songNames();

            }
        });

        prev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (mMediaPlayer != null) {
                    play.setImageResource(R.drawable.pause);
                }

                if (currentIndex > 0) {
                    currentIndex--;
                } else {
                    currentIndex = songs.size() - 1;
                }
                if (mMediaPlayer.isPlaying()) {
                    mMediaPlayer.stop();
                }

                mMediaPlayer = MediaPlayer.create(getApplicationContext(), songs.get(currentIndex));
                mMediaPlayer.start();
                songNames();

            }
        });
    }

    private void songNames() {
        if (currentIndex == 0) {
            songTitle.setText("Sudharnoto - Garuda Pancasila");
            imageView.setImageResource(R.drawable.garuda);
        }
        if (currentIndex == 1) {
            songTitle.setText("Ismail Marzuki - Indonesia Pusaka");
            imageView.setImageResource(R.drawable.bromo);
        }
        if (currentIndex == 2) {
            songTitle.setText("W.R Supratman - Indonesia Raya");
            imageView.setImageResource(R.drawable.flag);
        }
        if (currentIndex == 3) {
            songTitle.setText("Cornel Simandjuntak - Maju Tak Gentar");
            imageView.setImageResource(R.drawable.heroes);
        }
        if (currentIndex == 4) {
            songTitle.setText("Ismail Marzuki - Rayuan Pulau Kelapa");
            imageView.setImageResource(R.drawable.kelapa);
        }

        // seekbar duration
        mMediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                mSeekBarTime.setMax(mMediaPlayer.getDuration());
                mMediaPlayer.start();
            }
        });

        mSeekBarTime.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

                if (fromUser) {
                    mMediaPlayer.seekTo(progress);
                    mSeekBarTime.setProgress(progress);
                }

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        new Thread(new Runnable() {
            @Override
            public void run() {
                while (mMediaPlayer != null) {
                    try {
                        if (mMediaPlayer.isPlaying()) {
                            Message message = new Message();
                            message.what = mMediaPlayer.getCurrentPosition();
                            handler.sendMessage(message);
                            Thread.sleep(1000);
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }

    @SuppressLint("Handler Leak")
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            mSeekBarTime.setProgress(msg.what);
        }
    };

    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(false);
        builder.setMessage("Are you sure want to exit ?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //Jika user memilih YES maka akan finish/selesai --> Keluar dari aplikasi
                finish();
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Jika user memilih NO maka akan kembali ke activity
                dialog.cancel();
            }
        });
        AlertDialog alert = builder.create();
        alert.show();
    }

}
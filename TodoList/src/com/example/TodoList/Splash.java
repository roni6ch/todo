package com.example.TodoList;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

public class Splash extends FragmentActivity {
	MediaPlayer splashSong;
	@Override
	protected void onCreate(Bundle SplashActivity) {
		// TODO Auto-generated method stub
		super.onCreate(SplashActivity);
		splashSong = MediaPlayer.create(Splash.this, R.raw.splashsound);
		splashSong.start();
		setContentView(R.layout.splash);

		Thread timer = new Thread(){

			public void run(){
				try{
					sleep(2000); 
				}
				catch(InterruptedException e){
					e.printStackTrace();
				}

				finally{
					Intent openMenu = new Intent("com.example.TodoList.MainActivity");
					startActivity(openMenu);
				}
			}
		};
		timer.start();		
	}
	

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		splashSong.release();
		finish();
	}
}
package com.example.poonamgupta2801.guessingapp;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.ExecutionException;
import java.net.HttpURLConnection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class MainActivity extends AppCompatActivity {

    ArrayList<String> celebUrls=new ArrayList<String> (  );
    ArrayList<String> celebNames=new ArrayList<String> (  );

    Button button, button2, button3,button4;
    ImageView celebImageView;
    GridLayout gridLayout;
    int locationOfCorrectAnswer=0;
    int locationofIncorrectAnswer=0;
    int chosenCeleb=0;
    Bitmap celebImage;
    String [] answers=new String[4];
    

    public class DownloadImage extends AsyncTask<String, Void, Bitmap>{

        @Override
        protected Bitmap doInBackground(String... urls) {

            try {
                URL url=new URL ( urls[0]);

                HttpURLConnection urlConnection=(HttpURLConnection) url.openConnection ();


                InputStream input= urlConnection.getInputStream ();

                Bitmap bitmap= BitmapFactory.decodeStream ( input );

                return bitmap;


            } catch (MalformedURLException e) {
                e.printStackTrace ();
            } catch (IOException e) {
                e.printStackTrace ();
            }
            return null;
        }
    }
    public class DownloadTask extends AsyncTask<String, Void, String>{

        @Override
        protected String doInBackground(String... urls) {
            String result="";
            URL url;
            HttpURLConnection urlConnection=null;

            try {
                url=new URL(urls[0]);
                urlConnection= (HttpURLConnection) url.openConnection ();
                InputStream input= urlConnection.getInputStream ();
                InputStreamReader inputReader= new InputStreamReader ( input );

                int charData=inputReader.read ();

               while(charData!=-1){
                   char currentCharData= (char) charData;

                   result+=currentCharData;

                   charData=inputReader.read ();

               }

                return result;

            } catch (Exception e) {

                e.printStackTrace ();
            }

            return null;

        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate ( savedInstanceState );
        setContentView ( R.layout.activity_main );

        button=(Button)findViewById ( R.id.button );
        button2=(Button)findViewById ( R.id.button2 );
        button3=(Button)findViewById ( R.id.button3 );
        button4=(Button)findViewById ( R.id.button4 );
        celebImageView=(ImageView)findViewById ( R.id.celebImageView );

        gridLayout=(GridLayout)findViewById ( R.id.gridLayout );

        DownloadTask downloadTask=new DownloadTask ();
        String result=null;

        try {
            result=downloadTask.execute ( "http://www.posh24.se/kandisar" ).get ();

            String[] splitResult=result.split(" < div class=\"sidebarContainer\">");

            Pattern p =Pattern.compile("<img src=(.*?) ");
            Matcher m=p.matcher (splitResult[0]);

            while(m.find()){

                Log.i("Image Link", m.group(1));
                celebUrls.add ( m.group(1) );
            }

            p =Pattern.compile("alt=(.*?)/>");
            m=p.matcher(splitResult[0]);

            while(m.find()){

                Log.i("Name of the Celeb", m.group(1));
                celebNames.add ( m.group(1) );
            }

            generateQuestion ();


        } catch (InterruptedException e) {

            e.printStackTrace ();

        } catch (ExecutionException e) {

            e.printStackTrace ();
        }


    }

    public void celebGuess(View view) {

        if (view.getTag ().toString ().equals ( Integer.toString ( locationOfCorrectAnswer ) )){

            Toast.makeText ( getApplicationContext (),"Correct", Toast.LENGTH_SHORT ).show ();
        } else {

            Toast.makeText ( getApplicationContext (),"Wrong, it was"+celebNames.get(chosenCeleb), Toast.LENGTH_SHORT ).show ();
        }

        generateQuestion ();
    }

    public void generateQuestion(){

        Random random= new Random (  );
        chosenCeleb = random.nextInt (celebUrls.size () );

        DownloadImage downloadImage=new DownloadImage ();

        try {
            celebImage=downloadImage.execute ( celebUrls.get(chosenCeleb)).get();

            celebImageView.setImageBitmap ( celebImage );
            //This code not working
            System.out.println (chosenCeleb);

            Log.i("Chosen Celeb",celebNames.get ( chosenCeleb ));
            Log.i("Image url Celeb",celebUrls.get ( chosenCeleb ));

            locationOfCorrectAnswer= random.nextInt (4);
            locationofIncorrectAnswer=random.nextInt (4);

            for(int i=0; i<4;i++) {
                if (i == locationOfCorrectAnswer) {
                    answers[i] = celebNames.get ( chosenCeleb );
                } else {
                    locationofIncorrectAnswer=random.nextInt (celebUrls.size ());

                    while(locationofIncorrectAnswer==chosenCeleb){

                        locationofIncorrectAnswer=random.nextInt (celebUrls.size ());
                    }
                    answers[i] = celebNames.get ( locationofIncorrectAnswer );

                }
            }

            button.setText ( answers[0] );
            button2.setText ( answers[1] );
            button3.setText ( answers[2] );
            button4.setText ( answers[3] );

        } catch (Exception e) {
            e.printStackTrace ();
        }


    }
}

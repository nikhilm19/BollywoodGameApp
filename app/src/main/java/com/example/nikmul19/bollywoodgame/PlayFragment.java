package com.example.nikmul19.bollywoodgame;

import android.app.Activity;
import android.content.Context;
import android.inputmethodservice.InputMethodService;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import android.support.design.widget.*;
import android.widget.Toast;

import com.example.nikmul19.bollywoodgame.models.Movie;
import com.example.nikmul19.bollywoodgame.models.Session;
import com.google.android.gms.common.util.ArrayUtils;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class PlayFragment extends Fragment implements View.OnClickListener{
    View view;

    TextView movieText,hintText,chancesLeftText,wonGame,lettersGuessed;
    EditText letterInput;
    Button playBtn,hintBtn,guessLetterBtn;
    int movieId;
    DatabaseReference mDatabase;
    Movie movie;
    String guessedLetter;
    Session cSession;
    ProgressBar progressBar;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view=inflater.inflate(R.layout.fragment_play, container, false);
        findViews();

        return view;
    }
    public void showProgress(){
        progressBar.setVisibility(View.VISIBLE);
        progressBar.setIndeterminate(true);
    }

    public void hideProgress(){
        progressBar.setVisibility(View.INVISIBLE);
        progressBar.setIndeterminate(false);
    }

    public void findViews(){
        progressBar=view.findViewById(R.id.progressBar);
        progressBar.setVisibility(View.INVISIBLE);
        mDatabase=FirebaseDatabase.getInstance().getReference();
        chancesLeftText=view.findViewById(R.id.chances_left);
        chancesLeftText.setVisibility(View.INVISIBLE);

        movieText= view.findViewById(R.id.movie_text);
        wonGame=view.findViewById(R.id.won_game);
        wonGame.setVisibility(View.INVISIBLE);

        hintText= view.findViewById(R.id.hint_text);
        hintText.setVisibility(View.INVISIBLE);

        playBtn= view.findViewById(R.id.play_game_btn);
        playBtn.setOnClickListener(this);
        hintBtn=view.findViewById(R.id.get_hint_btn);
        hintBtn.setOnClickListener(this);
        hintBtn.setVisibility(View.INVISIBLE);
        guessLetterBtn=view.findViewById(R.id.guess_letter_btn);
        guessLetterBtn.setOnClickListener(this);
        guessLetterBtn.setVisibility(View.INVISIBLE);
        letterInput=view.findViewById(R.id.input_letter);
        letterInput.setVisibility(View.INVISIBLE);
        lettersGuessed=view.findViewById(R.id.letters_guessed);
        lettersGuessed.setVisibility(View.INVISIBLE);

    }

    @Override
    public void onClick(View v){
        final int id=v.getId();
        showProgress();


        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                switch (id){


                    case R.id.play_game_btn:

                        movie= new Movie();
                        Random random= new Random();
                        movieId= random.nextInt(1200);
                        getMovie(movieId);
                        cSession= new Session(movie);

                        updateUiOnSessionStart();
                        if(movie.title !=null )hideProgress();


                        break;

                    case R.id.get_hint_btn:
                        hintText.setVisibility(View.VISIBLE);
                        switch (cSession.hintsGiven){
                            case 0: hintText.setText("One of the Actors of the movie is "+movie.actors.get(0));
                                break;
                            case 1: hintText.setText("Director of the movie is "+movie.directors.get(0));
                                break;
                            case 2:hintText.setText("Genre of the movie is "+ movie.genre.get(0));
                                break;
                            case 3:hintText.setText("Movie was released in "+movie.releaseYear);
                                break;
                            default:hintText.setText("Can't give more hints");
                                break;



                        }
                        cSession.hintsGiven+=1;
                        hideProgress();
                        break;

                    case R.id.guess_letter_btn:
                        hintText.setVisibility(View.INVISIBLE);

                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                hintText.setVisibility(View.INVISIBLE);
                                lettersGuessed.setVisibility(View.VISIBLE);
                                InputMethodManager imm= (InputMethodManager) getActivity().getSystemService(Activity.INPUT_METHOD_SERVICE);
                                if(TextUtils.isEmpty(letterInput.getText())){
                                    Toast.makeText(getActivity(),"Please guess a letter or movie",Toast.LENGTH_SHORT).show();
                                    hideProgress();
                                }
                                else {
                                    String input = letterInput.getText().toString();
                                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                                    Log.i("test", input);

                                    if (input.length() == 1 && Character.isLetter(input.charAt(0))) {

                                        char letter = input.charAt(0);

                                        letter = Character.toUpperCase(letter);
                                        Log.i("test", "letter : " + letter);

                                        if (cSession.lettersGuessed.contains(letter)) {

                                            Snackbar.make(view, "You have already guessed that letter", Snackbar.LENGTH_SHORT).show();
                                            hideProgress();
                                        } else if (cSession.vowels.contains(letter)) {
                                            Snackbar.make(view, "The vowels are already filled", Snackbar.LENGTH_SHORT).show();
                                            hideProgress();
                                        } else if (movie.title.indexOf(letter) != -1) {
                                            cSession.lettersGuessed.add(letter);

                                            hideProgress();
                                            Snackbar.make(view, "Yes the letter " + letter + " is in the movie", Snackbar.LENGTH_LONG).show();

                                            Log.i("test", "yes letter : " + letter);

                                            Log.i("test", movie.title);
                                            char[] charArray = movie.fillMovie.toCharArray();
                                            Log.i("test", "no" + Arrays.toString(charArray));
                                            for (int i = 0; i < movie.title.length(); i++) {
                                                if (movie.title.charAt(i) == letter) {
                                                    charArray[i] = letter;
                                                }

                                            }
                                            Log.i("test", "no" + Arrays.toString(charArray));

                                            movie.fillMovie = (String.valueOf(charArray));
                                            if (movie.fillMovie.compareTo(movie.title) == 0) {
                                                hideProgress();
                                                Animation anim= new AlphaAnimation(0.0f,1.0f);
                                                anim.setDuration(50); //You can manage the blinking time with this parameter
                                                anim.setStartOffset(20);
                                                anim.setRepeatMode(Animation.REVERSE);
                                                anim.setRepeatCount(20);
                                                Snackbar.make(view, "You guessed the movie in " + (cSession.noOfGuesses) + " chances", Snackbar.LENGTH_SHORT).show();
                                               updateUiAfterSessionEnd();
                                                wonGame.setText("WON!!");
                                                wonGame.startAnimation(anim);

                                            } else
                                            {

                                                Toast.makeText(getActivity(), "Updating movie", Toast.LENGTH_SHORT).show();
                                            }


                                            movieText.setText(movie.fillMovie);
                                            hideProgress();
                                        } else {
                                            cSession.noOfGuesses += 1;
                                            if (cSession.noOfGuesses == 8) {
                                                hideProgress();
                                                Snackbar.make(view, "Last guess Coming up..", Snackbar.LENGTH_SHORT).show();
                                            }
                                            if (cSession.noOfGuesses == 9) {
                                                hideProgress();
                                                Animation anim= new AlphaAnimation(0.0f,1.0f);
                                                anim.setDuration(50); //You can manage the blinking time with this parameter
                                                anim.setStartOffset(20);
                                                anim.setRepeatMode(Animation.RESTART);
                                                anim.setRepeatCount(20);
                                                wonGame.setText("LOST!!");
                                                wonGame.startAnimation(anim);
                                                movieText.setText("The movie was " + movie.title);
                                                Snackbar.make(view, "Oops. You couldn't guess the movie.", Snackbar.LENGTH_SHORT).show();
                                                updateUiAfterSessionEnd();

                                            } else {
                                                cSession.lettersGuessed.add(letter);

                                                //chancesLeftText.setVisibility(View.INVISIBLE);
                                                hideProgress();
                                                Animation anim= new AlphaAnimation(0.0f,1.0f);
                                                anim.setDuration(300); //You can manage the blinking time with this parameter
                                                anim.setStartOffset(20);
                                                anim.setRepeatMode(Animation.RESTART);
                                                anim.setRepeatCount(10);

                                                Snackbar.make(view, "The letter isn't in the movie", Snackbar.LENGTH_LONG).show();
                                                chancesLeftText.setText("Chances left : " + (9 - cSession.noOfGuesses));
                                                chancesLeftText.startAnimation(anim);
                                            }
                                        }

                                        String letters= cSession.lettersGuessed.toString();
                                        lettersGuessed.setText("Letters guessed: "+ letters);

                                    }
                                    else{

                                        if(input.toUpperCase().compareTo(movie.title)==0){
                                            hideProgress();
                                            wonGame.setText("WON!!!");
                                            Animation anim= new AlphaAnimation(0.0f,1.0f);
                                            anim.setDuration(50); //You can manage the blinking time with this parameter
                                            anim.setStartOffset(20);
                                            anim.setRepeatMode(Animation.RESTART);
                                            anim.setRepeatCount(10);
                                            movieText.setText("Yes. The movie is "+movie.title);
                                            movieText.startAnimation(anim);
                                           updateUiAfterSessionEnd();
                                        }
                                        else if (cSession.moviesGuessed.contains(input.toUpperCase())){
                                            Snackbar.make(view,"Already guessed that movie.",Snackbar.LENGTH_SHORT).show();


                                        }
                                        else{
                                            if(cSession.noOfGuesses==8){
                                                wonGame.setText("LOST!!!");
                                                Animation anim= new AlphaAnimation(0.0f,1.0f);
                                                anim.setDuration(50); //You can manage the blinking time with this parameter
                                                anim.setStartOffset(20);
                                                anim.setRepeatMode(Animation.RESTART);
                                                anim.setRepeatCount(10);
                                                movieText.setText("No! The movie was "+movie.title);
                                                movieText.startAnimation(anim);
                                                updateUiAfterSessionEnd();
                                            }
                                            else{
                                                hideProgress();
                                                Animation anim= new AlphaAnimation(0.0f,1.0f);
                                                anim.setDuration(10); //You can manage the blinking time with this parameter
                                                anim.setStartOffset(20);
                                                anim.setRepeatMode(Animation.RESTART);
                                                anim.setRepeatCount(10);


                                                Toast.makeText(getContext(),"No it's not "+ input,Toast.LENGTH_SHORT).show();
                                                cSession.moviesGuessed.add(input.toUpperCase());
                                                cSession.noOfGuesses+=1;
                                                chancesLeftText.setText("Chances left:"+(9-cSession.noOfGuesses));
                                                chancesLeftText.startAnimation(anim);
                                            }
                                        }

                                    }

                                    letterInput.getText().clear();
                                    //break;
                                }


                            }
                        },1000);

                }

            }
        },1000);


    }

    public void getMovie(final int movieId){
        Log.i("test",Integer.toString(movieId));

        mDatabase.child("movies/"+Integer.toString(movieId)).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                movie= dataSnapshot.getValue(Movie.class);

                Log.i("test","hey"+movie.title);
                movie.title=movie.title.toUpperCase();
                hideProgress();
                //setMovie();
                //Log.i("test",dataSnapshot.child("title").getValue().toString());

                for (DataSnapshot child: dataSnapshot.getChildren()){
                    Log.i("test",child.getValue().toString());
                }
                setMovie();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.i("test",databaseError.getMessage());

            }
        });




    }

    public void setMovie(){

        String title=movie.title;
        for (int i=0;i<movie.title.length();i++){
            char c=title.charAt(i);
            c=Character.toUpperCase(c);
            Log.i("test","letter:"+c);
            if(Character.isLetter(c)){
                if(!(c=='A' || c=='E' ||
                        c=='I' || c=='O' || c=='U')){
                    title=title.replace(c,'*');
                }
            }

        }
        movie.fillMovie=title;
        Log.i("test",title);
        movieText.setText("Movie is : \n" +title.toUpperCase());
        chancesLeftText.setVisibility(View.VISIBLE);

    }

    public void updateUiAfterSessionEnd(){
        playBtn.setVisibility(View.VISIBLE);
        lettersGuessed.setVisibility(View.INVISIBLE);
        hintBtn.setVisibility(View.INVISIBLE);
        guessLetterBtn.setVisibility(View.INVISIBLE);
        letterInput.setVisibility(View.INVISIBLE);
        chancesLeftText.setVisibility(View.INVISIBLE);


    }

    public void updateUiOnSessionStart(){

            wonGame.setVisibility(View.INVISIBLE);

            playBtn.setVisibility(View.INVISIBLE);
            lettersGuessed.setText("Letters Guessed: "+cSession.lettersGuessed.toString());
            lettersGuessed.setVisibility(View.VISIBLE);
            hintBtn.setVisibility(View.VISIBLE);
            guessLetterBtn.setVisibility(View.VISIBLE);
            letterInput.setVisibility(View.VISIBLE);
            chancesLeftText.setVisibility(View.VISIBLE);
            chancesLeftText.setText("Chances left:9");

    }

}

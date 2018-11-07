package com.example.nikmul19.bollywoodgame.models;

import java.util.ArrayList;
import java.util.List;

public class Session {

    public Movie movie;
    public List<Character> lettersGuessed;
    public int noOfGuesses,hintsGiven;
    public List<Character> vowels;
    public  List <String> moviesGuessed;

    public Session(Movie movie){
        this.movie=movie;
        noOfGuesses=0;
        hintsGiven=0;

        lettersGuessed= new ArrayList<Character>();
        moviesGuessed= new ArrayList<String>();
        vowels= new ArrayList<Character>();
        vowels.add('A');
        vowels.add('E');
        vowels.add('I');
        vowels.add('O');
        vowels.add('U');
    }


}

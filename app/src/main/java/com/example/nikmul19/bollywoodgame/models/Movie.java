package com.example.nikmul19.bollywoodgame.models;

import java.util.ArrayList;
import java.util.List;

public class Movie {
    public Long imdbId;

    public String title,releaseYear,releaseDate,writers,hitFlop,sequel,fillMovie;
    public List <String > actors;
    public List <String > directors;
    public List <String > genre;

    public Movie(){
        actors= new ArrayList<String>();
        directors= new ArrayList<String>();
        genre= new ArrayList<String>();



    }



}

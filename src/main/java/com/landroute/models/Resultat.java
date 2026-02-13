package com.landroute.models;


import java.util.List;
import java.util.stream.Collectors;

import lombok.Getter;


@Getter
public class Resultat {
    private Pays trouve;
    private List<Pays> chemin;
    private List<String> cheminCodes;
    private int longueur;
    
    public Resultat(Pays trouve, List<Pays> chemin) {
        this.trouve = trouve;
        this.chemin = chemin;
        this.longueur = chemin.size();
        this.cheminCodes = chemin.stream()
                                  .map(Pays::getCca3)
                                  .collect(Collectors.toList());
    }
    
}
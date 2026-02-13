package com.landroute.models;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;


@Data
@AllArgsConstructor
public class PaysAvecChemin {
    public Pays pays;
    public List<Pays> chemin;
    
}
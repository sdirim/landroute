package com.landroute.service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;

import org.springframework.stereotype.Service;

import com.landroute.models.Pays;
import com.landroute.models.PaysAvecChemin;
import com.landroute.models.Resultat;
import com.landroute.repository.PaysRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class RecherchePaysService {
	
	private static final Logger log = LoggerFactory.getLogger(RecherchePaysService.class);
    
    private final PaysRepository paysRepository;
    
    public RecherchePaysService(PaysRepository paysRepository) {
        this.paysRepository = paysRepository;
    }
    
    /**
     * BFS search with dynamic loading of children by level
     */
    public Resultat trouverChemin(String departCode, String destinationCode) {
        Pays depart = new Pays(departCode);
        return trouverObjet(depart, destinationCode);
    }
    
    /**
     * BFS method to find the shortest path
     */
    private Resultat trouverObjet(Pays depart, String cible) {
        if (depart == null) {
            return null;
        }
        
        Queue<PaysAvecChemin> file = new LinkedList<>();
        Set<String> visites = new HashSet<>();
        
        List<Pays> cheminInitial = new ArrayList<>();
        cheminInitial.add(depart);
        file.offer(new PaysAvecChemin(depart, cheminInitial));
        visites.add(depart.getCca3());
        
        while (!file.isEmpty()) {
            int tailleNiveau = file.size();  // Bloquer le niveau actuel
            
            log.info("Exploring level with " + tailleNiveau + " pays");
            
            // Traiter TOUT le niveau actuel avant de passer au suivant
            for (int i = 0; i < tailleNiveau; i++) {
                PaysAvecChemin courant = file.poll();
                
                // Check if the target has been found
                if (courant.pays.getCca3().equals(cible)) {
                    log.info("Pays fond : " + cible);
                    return new Resultat(courant.pays, courant.chemin);
                }
                
                // ← DYNAMIC LOADING from JSON
                if (!courant.pays.isEnfantsCharges()) {
                    chargerEnfantsDepuisJson(courant.pays);
                }
                
                // Add the children (next level)
                if (courant.pays.getEnfants() != null) {
                    for (Pays enfant : courant.pays.getEnfants()) {
                        if (!visites.contains(enfant.getCca3())) {
                            visites.add(enfant.getCca3());
                            List<Pays> nouveauChemin = new ArrayList<>(courant.chemin);
                            nouveauChemin.add(enfant);
                            file.offer(new PaysAvecChemin(enfant, nouveauChemin));
                        }
                    }
                }
            }
        }
        
        log.warn("Pays not found : " + cible);
        return null;
    }
    
    /**
     * Charge les enfants d'un pays depuis le JSON
     */
    private void chargerEnfantsDepuisJson(Pays pays) {
        try {
            List<String> borders = paysRepository.getBorders(pays.getCca3());
            List<Pays> enfants = new ArrayList<>();
            
            for (String codeFrontalier : borders) {
                enfants.add(new Pays(codeFrontalier));
            }
            
            pays.setEnfants(enfants);
        } catch (Exception e) {
            log.error("Erreur lors du chargement des enfants de " + pays.getCca3());
            pays.setEnfants(new ArrayList<>());
        }
    }
}

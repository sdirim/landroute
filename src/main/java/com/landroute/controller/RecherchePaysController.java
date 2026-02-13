package com.landroute.controller;

import java.util.ArrayList;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.landroute.models.Resultat;
import com.landroute.models.RouteResponseDTO;
import com.landroute.repository.PaysRepository;
import com.landroute.service.RecherchePaysService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("")
@CrossOrigin(origins = "*")
public class RecherchePaysController {
	
	private static final Logger log = LoggerFactory.getLogger(RecherchePaysController.class);
    
    private final RecherchePaysService rechercheService;
    private final PaysRepository paysRepository;
    
    public RecherchePaysController(RecherchePaysService rechercheService, 
                                   PaysRepository paysRepository) {
        this.rechercheService = rechercheService;
        this.paysRepository = paysRepository;
    }
    
    /**
     * Main endpoint: finding the path between two countries
     * GET /routing/CZE/ITA
     * 
     * Return : {"route":["CZE","AUT","ITA"]}
     * Erreur 400 if no path found
     */
    @GetMapping("/routing/{origin}/{destination}")
    public ResponseEntity<RouteResponseDTO> trouverChemin(
            @PathVariable String origin,
            @PathVariable String destination) {
        
        try {
            Resultat resultat = rechercheService.trouverChemin(origin, destination);
            
            if (resultat != null) {
                // Path found: return 200 OK
                return ResponseEntity.ok(new RouteResponseDTO(resultat.getCheminCodes()));
            } else {
                // No path found: return 400 Bad Request
                return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(new RouteResponseDTO(new ArrayList<>()));
            }
            
        } catch (Exception e) {
            // Technical error: Returning 400 Bad Request
            log.error("Erreur lors de la recherche : " + e.getMessage());
            return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new RouteResponseDTO(new ArrayList<>()));
        }
    }
    
    
    /**
     * Health endpoint
     * GET /health
     */
    @GetMapping("/health")
    public ResponseEntity<?> health() {
        return ResponseEntity.ok(Map.of(
            "status", "OK",
            "message", "Country search service operational"
        ));
    }
}
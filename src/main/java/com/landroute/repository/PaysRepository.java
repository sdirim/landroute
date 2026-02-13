package com.landroute.repository;


import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Repository;

import jakarta.annotation.PostConstruct;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Repository
public class PaysRepository {
	
	private static final Logger log = LoggerFactory.getLogger(PaysRepository.class);
    
    @Value("${pays.json.url}")
    private String jsonUrl;
    
    @Value("${pays.json.local-path}")
    private String localPath;
    
    @Value("${pays.json.cache-path}")
    private String cachePath;
    
    private final ResourceLoader resourceLoader;
    private final ObjectMapper objectMapper;
    
    private Map<String, List<String>> paysCache = new HashMap<>();
    
    public PaysRepository(ResourceLoader resourceLoader, ObjectMapper objectMapper) {
        this.resourceLoader = resourceLoader;
        this.objectMapper = objectMapper;
    }
    
    @PostConstruct
    public void initialiser() {
        if (!paysCache.isEmpty()) {
            return;
        }
        
        try {
            String contenu;
            Resource resource = resourceLoader.getResource(localPath);
            
            if (resource.exists()) {
               log.info("Lecture depuis " + localPath);
                contenu = new String(resource.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
            } else {
            	log.info("Téléchargement depuis GitHub");
                contenu = telechargerDepuisURL(jsonUrl);
                sauvegarderEnLocal(contenu);
            }
            
            // Parser le JSON et extraire UNIQUEMENT cca3 et borders
            JsonNode paysArray = objectMapper.readTree(contenu);
            
            for (JsonNode pays : paysArray) {
                if (pays.has("cca3")) {
                    String cca3 = pays.get("cca3").asText();
                    List<String> borders = new ArrayList<>();
                    
                    // Extraire les frontières si elles existent
                    if (pays.has("borders")) {
                        JsonNode bordersNode = pays.get("borders");
                        for (JsonNode border : bordersNode) {
                            borders.add(border.asText());
                        }
                    }
                    
                    // Stocker dans le cache optimisé
                    paysCache.put(cca3, borders);
                }
            }
            
            log.info(paysCache.size() + " pays chargés en mémoire (optimisé)");
            
        } catch (Exception e) {
           log.error("Erreur lors du chargement des données pays");
            e.printStackTrace();
        }
    }
    
    private String telechargerDepuisURL(String urlString) throws Exception {
        URL url = new URL(urlString);
        try (Reader reader = new InputStreamReader(url.openStream(), StandardCharsets.UTF_8)) {
            StringBuilder sb = new StringBuilder();
            char[] buffer = new char[8192];
            int read;
            while ((read = reader.read(buffer)) != -1) {
                sb.append(buffer, 0, read);
            }
            return sb.toString();
        }
    }
    
    private void sauvegarderEnLocal(String contenu) {
        try {
            Path cache = Paths.get(cachePath);
            Files.createDirectories(cache.getParent());
            Files.writeString(cache, contenu);
            log.info("Fichier sauvegardé dans " + cachePath);
        } catch (Exception e) {
           log.warn("Impossible de sauvegarder le cache local");
        }
    }
    
    /**
     * Retourne la liste des pays frontaliers pour un code pays donné
     */
    public List<String> getBorders(String cca3) {
        return paysCache.getOrDefault(cca3, new ArrayList<>());
    }
    
    /**
     * Vérifie si un pays existe dans les données
     */
    public boolean paysExiste(String cca3) {
        return paysCache.containsKey(cca3);
    }
    
    /**
     * Retourne tous les codes pays disponibles
     */
    public List<String> getTousLesPays() {
        return new ArrayList<>(paysCache.keySet());
    }
    
}
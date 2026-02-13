package com.landroute.models;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
public class Pays {
	
    private String cca3;
    private List<Pays> enfants;
    private boolean enfantsCharges = false;
    
    // Constructeur
    public Pays(String cca3) {
        this.cca3 = cca3;
        this.enfants = null;
    }
    
    // Custom setter with business logic
    public void setEnfants(List<Pays> enfants) {
        this.enfants = enfants;
        this.enfantsCharges = true;
    }
    
    // Custom method
    public void ajouterEnfant(Pays enfant) {
        if (this.enfants == null) {
            this.enfants = new ArrayList<>();
        }
        this.enfants.add(enfant);
    }
    
    @Override
    public String toString() {
        return "Pays{" +
                "cca3='" + cca3 + '\'' +
                ", nombreEnfants=" + (enfants != null ? enfants.size() : 0) +
                ", enfantsCharges=" + enfantsCharges +
                '}';
    }
    
    // Override equals et hashCode basés sur cca3
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Pays pays = (Pays) o;
        return cca3 != null ? cca3.equals(pays.cca3) : pays.cca3 == null;
    }
    
    @Override
    public int hashCode() {
        return cca3 != null ? cca3.hashCode() : 0;
    }
}
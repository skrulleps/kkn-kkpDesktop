/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

/**
 *
 * @author fadhl
 */
public class ComboBoxItem {
    private String idTempat;
    private String namaTempat;
    private String namaDosen;

    public ComboBoxItem(String idTempat, String namaTempat, String namaDosen) {
        this.idTempat = idTempat;
        this.namaTempat = namaTempat;
        this.namaDosen = namaDosen;
    }

    public String getIdTempat() {
        return idTempat;
    }

    public String getNamaTempat() {
        return namaTempat;
    }

    public String getNamaDosen() {
        return namaDosen;
    }

    @Override
    public String toString() {
        return namaTempat;
    }
}

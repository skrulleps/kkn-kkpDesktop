/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package session;

/**
 * Singleton session class to manage user session data.
 */
public class session {
    private static session instance;
    private String username; // Ini adalah email pengguna
    private String levelUser;
    private String kegiatan;
    private String status; // Status dosen

    private session() {
        // Private constructor to prevent instantiation
    }

    public static session getInstance() {
        if (instance == null) {
            instance = new session();
        }
        return instance;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getLevelUser() {
        return levelUser;
    }

    public void setLevelUser(String levelUser) {
        this.levelUser = levelUser;
    }

    public String getKegiatan() {
        return kegiatan;
    }

    public void setKegiatan(String kegiatan) {
        this.kegiatan = kegiatan;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void clearSession() {
        username = null;
        levelUser = null;
        kegiatan = null;
        status = null; // Clear status as well
    }
}


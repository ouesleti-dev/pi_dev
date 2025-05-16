package Models;

import java.util.Date;

/**
 * Classe représentant une excursion
 */
public class Excursion {
    private int id_excursion;
    private int id_event;
    private int id_user;
    private String titre;
    private String destination;
    private double prix;
    private Date date;
    private int duree;
    private String transport;
    private Event event;
    private User user;

    /**
     * Constructeur par défaut
     */
    public Excursion() {
    }

    /**
     * Constructeur avec tous les paramètres sauf les objets Event et User
     */
    public Excursion(int id_excursion, int id_event, int id_user, String titre, String destination,
                     double prix, Date date, int duree, String transport) {
        this.id_excursion = id_excursion;
        this.id_event = id_event;
        this.id_user = id_user;
        this.titre = titre;
        this.destination = destination;
        this.prix = prix;
        this.date = date;
        this.duree = duree;
        this.transport = transport;
    }

    /**
     * Constructeur avec tous les paramètres incluant les objets Event et User
     */
    public Excursion(int id_excursion, int id_event, int id_user, String titre, String destination,
                     double prix, Date date, int duree, String transport, Event event, User user) {
        this(id_excursion, id_event, id_user, titre, destination, prix, date, duree, transport);
        this.event = event;
        this.user = user;
    }

    /**
     * Constructeur sans id (pour création)
     */
    public Excursion(int id_event, int id_user, String titre, String destination,
                     double prix, Date date, int duree, String transport) {
        this.id_event = id_event;
        this.id_user = id_user;
        this.titre = titre;
        this.destination = destination;
        this.prix = prix;
        this.date = date;
        this.duree = duree;
        this.transport = transport;
    }

    // Getters et Setters
    public int getId_excursion() {
        return id_excursion;
    }

    public void setId_excursion(int id_excursion) {
        this.id_excursion = id_excursion;
    }

    public int getId_event() {
        return id_event;
    }

    public void setId_event(int id_event) {
        this.id_event = id_event;
    }

    public int getId_user() {
        return id_user;
    }

    public void setId_user(int id_user) {
        this.id_user = id_user;
    }

    public String getTitre() {
        return titre;
    }

    public void setTitre(String titre) {
        this.titre = titre;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public double getPrix() {
        return prix;
    }

    public void setPrix(double prix) {
        this.prix = prix;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public int getDuree() {
        return duree;
    }

    public void setDuree(int duree) {
        this.duree = duree;
    }

    public String getTransport() {
        return transport;
    }

    public void setTransport(String transport) {
        this.transport = transport;
    }

    public Event getEvent() {
        return event;
    }

    public void setEvent(Event event) {
        this.event = event;
        if (event != null) {
            this.id_event = event.getId();
        }
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
        if (user != null) {
            this.id_user = user.getId();
        }
    }

    @Override
    public String toString() {
        return "Excursion{" +
                "id_excursion=" + id_excursion +
                ", id_event=" + id_event +
                ", id_user=" + id_user +
                ", titre='" + titre + '\'' +
                ", destination='" + destination + '\'' +
                ", prix=" + prix +
                ", date=" + date +
                ", duree=" + duree +
                ", transport='" + transport + '\'' +
                '}';
    }
}
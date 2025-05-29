package com.example.argosapp;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

import java.util.List;
import java.util.Map;

/**
 * Interface ApiService définissant les requêtes HTTP pour l'authentification,
 * la gestion des utilisateurs, des enseignes et des notifications.
 * Utilise Retrofit pour les appels API.
 */
public interface ApiService {

    /**
     * Envoie une requête POST pour authentifier un utilisateur.
     * @param loginRequest Un Map contenant les informations de connexion (ex: email, mot de passe).
     * @return Un appel Retrofit contenant une réponse sous forme de String.
     */
    @POST("api/utilisateurs/login")
    Call<String> login(@Body Map<String, String> loginRequest);

    /**
     * Envoie une requête POST pour ajouter un nouvel utilisateur à la base de données.
     * @param utilisateur L'objet Utilisateur à ajouter.
     * @return Un appel Retrofit contenant la réponse du serveur.
     */
    @POST("api/utilisateurs/ajouter")
    Call<ResponseBody> ajouterUtilisateur(@Body Utilisateur utilisateur);

    /**
     * Envoie une requête POST pour déconnecter un utilisateur.
     * @return Un appel Retrofit contenant une réponse du serveur.
     */
    @POST("api/utilisateurs/logout")
    Call<ResponseBody> logout();

    /**
     * Envoie une requête POST pour ajouter une nouvelle enseigne.
     * @param enseigneDTO L'objet représentant l'enseigne à ajouter.
     * @return Un appel Retrofit contenant la réponse du serveur.
     */
    @POST("api/enseigne/ajouter")
    Call<ResponseBody> ajouterEnseigne(@Body Enseigne enseigneDTO);

    /**
     * Envoie une requête GET pour valider le token JWT de l'utilisateur.
     * @return Un appel Retrofit contenant un booléen indiquant si le token est valide.
     */
    @GET("api/utilisateurs/validate-token")
    Call<Boolean> validateToken();

    /**
     * Envoie une requête GET pour récupérer tous les utilisateurs.
     * @return Un appel Retrofit contenant la liste des utilisateurs.
     */
    @GET("api/utilisateurs/all")
    Call<List<User>> getAllUsers();

    /**
     * Envoie une requête GET pour récupérer toutes les enseignes non-admin.
     * @return Un appel Retrofit contenant la liste des enseignes.
     */
    @GET("api/enseigne/all")
    Call<List<Enseigne>> getEnseignesNonAdmin();

    /**
     * Envoie une requête DELETE pour supprimer une enseigne selon son nom et son adresse.
     * @param nomEnseigne Le nom de l'enseigne.
     * @param adresseEnseigne L'adresse de l'enseigne.
     * @return Un appel Retrofit contenant la réponse du serveur.
     */
    @DELETE("api/enseigne/supprimer/{nom_enseigne}/{adresse_enseigne}")
    Call<ResponseBody> supprimerEnseigne(@Path("nom_enseigne") String nomEnseigne, @Path("adresse_enseigne") String adresseEnseigne);

    /**
     * Envoie une requête DELETE pour supprimer un utilisateur selon son identifiant.
     * @param id L'identifiant de l'utilisateur à supprimer.
     * @return Un appel Retrofit contenant la réponse du serveur.
     */
    @DELETE("api/utilisateurs/supprimer/{id}")
    Call<ResponseBody> supprimerUtilisateur(@Path("id") String id);

    /**
     * Envoie une requête GET pour récupérer les informations d'un utilisateur spécifique.
     * @param userId L'identifiant de l'utilisateur.
     * @return Un appel Retrofit contenant les informations de l'utilisateur.
     */
    @GET("api/utilisateurs/infos/{id}")
    Call<ResponseBody> getInfosUtilisateur(@Path("id") String userId);

    /**
     * Envoie une requête PUT pour changer le mot de passe d'un utilisateur (admin).
     * @param requestBody Un Map contenant les anciennes et nouvelles informations du mot de passe.
     * @return Un appel Retrofit contenant la réponse du serveur.
     */
    @PUT("api/utilisateurs/changer-mdp")
    Call<ResponseBody> changerMotDePasseAdmin(@Body Map<String, String> requestBody);

    /**
     * Envoie une requête GET pour obtenir le nombre de notifications "Vol" non lues d'un utilisateur.
     * @param utilisateurId L'identifiant de l'utilisateur.
     * @return Un appel Retrofit contenant le nombre de notifications non lues.
     */
    @GET("api/notifications/vol/count")
    Call<Long> getCountNotificationsVolUnread(@Query("utilisateurId") String utilisateurId);

    /**
     * Envoie une requête GET pour obtenir le nombre de notifications "Intrusion" non lues d'un utilisateur.
     * @param utilisateurId L'identifiant de l'utilisateur.
     * @return Un appel Retrofit contenant le nombre de notifications non lues.
     */
    @GET("api/notifications/intrusion/count")
    Call<Long> getCountNotificationsIntrusionUnread(@Query("utilisateurId") String utilisateurId);


    /**
     * Envoie une requête GET pour récupérer toutes les notifications d'un utilisateur.
     * @param utilisateurId L'identifiant de l'utilisateur.
     * @return Un appel Retrofit contenant une liste de notifications.
     */
    @GET("api/notifications")
    Call<List<NotificationModel>> getAllNotifications(@Query("utilisateurId") String utilisateurId);

    /**
     * Envoie une requête POST pour marquer une notification comme lue.
     * @param notificationId L'identifiant de la notification à marquer comme lue.
     * @return Un appel Retrofit sans contenu en retour.
     */
    @POST("/api/notifications/markAsRead")
    Call<Void> markAsRead(@Query("id") Long notificationId);

    /**
     * Envoie une requête POST pour ajouter une camera.
     * @param cameraRequest L'identifiant de la notification à marquer comme lue.
     * @return Un appel Retrofit sans contenu en retour.
     */
    @POST("api/cameras/ajouter")
    Call<ResponseBody> ajouterCamera(@Body CameraRequest cameraRequest);

    @GET("api/cameras/rtsp")
    Call<List<String>> getRtspUrlsByClientId(@Query("clientId") String clientId);

    @GET("/api/enregistrements")
    Call<List<Enregistrement>> getEnregistrements(@Query("utilisateurId") String utilisateurId);

}
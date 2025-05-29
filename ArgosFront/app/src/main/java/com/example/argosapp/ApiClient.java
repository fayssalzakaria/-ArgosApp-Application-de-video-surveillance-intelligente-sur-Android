package com.example.argosapp;
import android.content.Context;
import android.content.SharedPreferences;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.util.concurrent.TimeUnit;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Classe {@code ApiClient} responsable de la configuration et de la gestion
 * de l'instance Retrofit utilisée pour communiquer avec l'API backend.
 */
public class ApiClient {

    /** URL de base par défaut de l'API backend. */
    private static String BASE_URL = "https://l3a1backend-1.onrender.com/";

    /** Instance Retrofit unique utilisée pour les requêtes réseau. */
    public static Retrofit retrofit = null;

    /**
     * Définit dynamiquement une nouvelle URL de base.
     * Utilisé notamment dans les tests unitaires pour rediriger vers un faux backend.
     *
     * @param baseUrl Nouvelle URL de base pour les requêtes.
     */
    public static void setBaseUrl(String baseUrl) {
        BASE_URL = baseUrl;
        retrofit = null; // Réinitialise Retrofit pour forcer la reconstruction avec la nouvelle URL.
    }

    /**
     * Retourne une instance Retrofit configurée avec :
     * - Une URL de base
     * - Un client HTTP personnalisé (avec timeout et gestion de token JWT)
     * - Un convertisseur Gson tolérant (lenient)
     *
     * @param context Contexte de l'application, utilisé pour récupérer le token JWT.
     * @return Une instance Retrofit prête à l'emploi.
     */
    public static Retrofit getClient(final Context context) {
        if (retrofit == null) {
            // Création d'un convertisseur JSON tolérant
            Gson gson = new GsonBuilder()
                    .setLenient()
                    .create();

            // Configuration du client HTTP avec gestion du token JWT
            OkHttpClient okHttpClient = new OkHttpClient.Builder()
                    .connectTimeout(3, TimeUnit.SECONDS)
                    .readTimeout(3, TimeUnit.SECONDS)
                    .writeTimeout(3, TimeUnit.SECONDS)
                    .addInterceptor(chain -> {
                        // Récupération du token JWT depuis les préférences
                        SharedPreferences prefs = context.getSharedPreferences("AuthPrefs", Context.MODE_PRIVATE);
                        String token = prefs.getString("jwt_token", null);

                        // Construction de la requête avec en-tête Authorization si le token existe
                        Request original = chain.request();
                        Request.Builder builder = original.newBuilder();

                        if (token != null && !token.isEmpty()) {
                            builder.header("Authorization", "Bearer " + token);
                        }

                        Request request = builder.build();
                        return chain.proceed(request);
                    })
                    .build();

            // Construction de l'objet Retrofit
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(okHttpClient)
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .build();
        }
        return retrofit;
    }
}
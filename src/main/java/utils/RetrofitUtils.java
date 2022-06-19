package utils;

import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import static okhttp3.logging.HttpLoggingInterceptor.Level.BODY;

public class RetrofitUtils {
    static Properties prop = new Properties();
    private static InputStream configFile;

    static {
        try {
            configFile = new FileInputStream("src/main/resources/my.properties");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static String getBaseUrl() {
        try {
            prop.load(configFile);
        } catch (IOException e){
            e.printStackTrace();
        }
        return prop.getProperty("url");
    }

//        public static Retrofit getRetrofit() {
//            return new Retrofit.Builder()
//                    .baseUrl(getBaseUrl())
//                    .addConverterFactory(JacksonConverterFactory.create())
//                    .build();
//        }

    static HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
    static LoggingInterceptor logging2 = new LoggingInterceptor();
    static OkHttpClient.Builder httpClient = new OkHttpClient.Builder();

    public static Retrofit getRetrofit(){
        logging.setLevel(BODY);
        httpClient.addInterceptor(logging);
        return new Retrofit.Builder()
                .baseUrl(getBaseUrl())
                .addConverterFactory(JacksonConverterFactory.create())
                .client(httpClient.build())
                .build();
    }

}

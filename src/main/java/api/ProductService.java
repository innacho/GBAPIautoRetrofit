package api;

import dto.Product;
import okhttp3.ResponseBody;
import  retrofit2.Call;
import retrofit2.http.*;

public interface ProductService {

    @GET("products")
    Call<Product[]> getProducts();

    @POST("products")
    Call<Product> addProduct(@Body Product product);

    @POST("produc")
    Call<Product> addProductWrongEndpoint(@Body Product product);

    @PUT("products")
    Call<Product> editProduct(@Body Product product);

    @GET("products/{id}")
    Call<Product> getProductById(@Path("id") int id);

    @DELETE("products/{id}")
    Call<ResponseBody> deleteProductById(@Path("id") int id);

    @DELETE("products")
    Call<ResponseBody> deleteProductWithoutId();
}

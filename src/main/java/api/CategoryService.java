package api;

import dto.GetCategoryResponse;
import okhttp3.ResponseBody;
import  retrofit2.Call;
import retrofit2.http.*;

public interface CategoryService {
    @GET("categories/{id}")
    Call<GetCategoryResponse> getCategoryById(@Path("id") int id);

    @GET("categories/{id}")
    Call<ResponseBody> getCategoryWrongId(@Path("id") String id);

    @POST("categories/{id}")
    Call<ResponseBody> getCategoryWrongMethod(@Path("id") int id);
}

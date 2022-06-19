import api.CategoryService;
import dto.GetCategoryResponse;
import okhttp3.ResponseBody;
import utils.RetrofitUtils;
import org.hamcrest.CoreMatchers;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import retrofit2.Response;

import java.io.IOException;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;

public class GetCategoryTest {
    static CategoryService categoryService;
    @BeforeAll
    static void beforeAll(){
        categoryService = RetrofitUtils.getRetrofit().create(CategoryService.class);
    }

    @ParameterizedTest
    @CsvSource({ "1, 'Food'", "2, 'Electronic'"})
    void checkExistingCategoriesTest(int id, String title){
        try {
            Response<GetCategoryResponse> response = categoryService.getCategoryById(id).execute();

            assertThat(response.isSuccessful(), CoreMatchers.is(true));
            assertThat(response.code(), equalTo(200));
            assertThat(response.body().getId(), equalTo(id));
            assertThat(response.body().getTitle(), equalTo(title));
            response.body().getProducts().forEach(product ->
                    assertThat(product.getCategoryTitle(), equalTo(title)));

        } catch (IOException e){
            e.printStackTrace();
        }
    }
//
//    @Test
//    void getUnauthorizedCode(){
//// ?? cannot get 401 unauthorized and 403 Forbidden
//    }

    @Test
    void get405Error(){
        try {
            Response<ResponseBody> response = categoryService.getCategoryWrongMethod(1).execute();

            assertThat(response.code(), equalTo(405));
            assertThat(response.errorBody().string(), containsStringIgnoringCase("Method not allowed"));

        } catch (IOException e){
            e.printStackTrace();
        }
    }

    @Test
    void getBadRequest(){
        try {
            Response<ResponseBody> response = categoryService.getCategoryWrongId("test").execute();

            assertThat(response.code(), equalTo(400));
            assertThat(response.errorBody().string(), containsString("Bad Request"));

        } catch (IOException e){
            e.printStackTrace();
        }

    }

    @Test
    void getNotFoundCode(){
      //request for nonexistent category with id = 0
        int id = 0;
        try {
            Response<GetCategoryResponse> response = categoryService.getCategoryById(id).execute();

            assertThat(response.code(), equalTo(404));
            assertThat(response.errorBody().string(), containsString("Unable to find category with id: "+id));

        } catch (IOException e){
            e.printStackTrace();
        }
    }
}

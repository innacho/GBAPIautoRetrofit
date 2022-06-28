import api.CategoryService;
import db.model.Categories;
import db.model.Products;
import dto.GetCategoryResponse;
import dto.Product;
import okhttp3.ResponseBody;
import org.junit.jupiter.params.provider.CsvFileSource;
import utils.MyBatisUtils;
import utils.RetrofitUtils;
import org.hamcrest.CoreMatchers;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import retrofit2.Response;

import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Locale;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;

public class GetCategoryTest extends AbstractTest{
    private static CategoryService categoryService = RetrofitUtils.getRetrofit().create(CategoryService.class);

    @BeforeAll
    static void beforeAll() throws IOException {
        MyBatisUtils myBatisUtils = getMyBatisUtils();
        List<Categories> categories = myBatisUtils.getAllCategories();
        FileWriter outfile = null;
        try{
            outfile = new FileWriter("src/test/resources/categoriesList.csv");
            for( Categories category : categories){
                try {
                    outfile.append(String.format(Locale.US,"%d, \"%s\"\n", category.getId(), category.getTitle()));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            outfile.flush();
        } finally {
            if(outfile != null) outfile.close();
        }
    }

    @ParameterizedTest()
    @CsvFileSource(files = "src/test/resources/categoriesList.csv")
    void checkExistingCategoriesTest(int id, String title){
        try {
            Response<GetCategoryResponse> response = categoryService.getCategoryById(id).execute();
            assertThat(response.isSuccessful(), CoreMatchers.is(true));
            assertThat(response.code(), equalTo(200));
            assertThat(response.body().getId(), equalTo(id));
            assertThat(response.body().getTitle(), equalTo(title));
            List<Product> products = response.body().getProducts();
                    products.forEach(product ->
                    assertThat(product.getCategoryTitle(), equalTo(title)));
            List<Products> productsFromDB = getMyBatisUtils().getAllProductsForCategoryId(id);
            for(int i = 0; i < productsFromDB.size(); i++){
                assertThat(productsFromDB.get(i).getId(), equalTo((long) products.get(i).getId()));
                assertThat(productsFromDB.get(i).getTitle(), equalTo(products.get(i).getTitle()));
                assertThat(productsFromDB.get(i).getPrice(), equalTo(products.get(i).getPrice()));
            }
        } catch (IOException e){
            e.printStackTrace();
        }
    }

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

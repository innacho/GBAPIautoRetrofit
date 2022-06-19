import api.ProductService;
import com.github.javafaker.Faker;
import dto.Product;
import okhttp3.ResponseBody;
import org.hamcrest.CoreMatchers;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import retrofit2.Response;
import utils.RetrofitUtils;

import java.io.IOException;

import static org.hamcrest.CoreMatchers.containsStringIgnoringCase;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

public class AddProductTest {

    static ProductService productService;
    Product product = null;
    Faker faker = new Faker();
    Integer id = null;

    @BeforeAll
    static void beforeAll(){
        productService = RetrofitUtils.getRetrofit().create(ProductService.class);
    }

    @BeforeEach
    void createProduct(){
        product = new Product()
                .addTitle(faker.food().ingredient())
                .addCategoryTitle("Food")
                .addPrice((int) (Math.random() * 10000));
        id = null;
    }

    @Test
    void addProductPositiveTest() throws IOException {
        Response<Product> response = productService.addProduct(product).execute();
        id = response.body().getId();
        assertThat(response.code(), CoreMatchers.is(201));
        assertThat(id, CoreMatchers.notNullValue());
        assertThat(response.isSuccessful(), CoreMatchers.is(true));
        assertThat(response.body().getTitle(), CoreMatchers.is(product.getTitle()));
        assertThat(response.body().getPrice(), CoreMatchers.is(product.getPrice()));
        assertThat(response.body().getCategoryTitle(), CoreMatchers.is("Food"));
    }

    @Test
    void addProductPositiveTestForElectronic() throws IOException {
        product.setCategoryTitle("Electronic");
        product.setTitle(faker.commerce().productName());
        Response<Product> response = productService.addProduct(product).execute();
        id = response.body().getId();
        assertThat(response.code(), CoreMatchers.is(201));
        assertThat(id, CoreMatchers.notNullValue());
        assertThat(response.body().getTitle(), CoreMatchers.is(product.getTitle()));
        assertThat(response.body().getPrice(), CoreMatchers.is(product.getPrice()));
        assertThat(response.body().getCategoryTitle(), CoreMatchers.is("Electronic"));
    }

    @Test
    void addTheSameProductTwice() throws IOException {
        Response<Product> response = productService.addProduct(product).execute();
        id = response.body().getId();
        assertThat(response.code(), CoreMatchers.is(201));
        assertThat(id, CoreMatchers.notNullValue());
        assertThat(response.isSuccessful(), CoreMatchers.is(true));

        // adding the same product for the second time
        Response<Product> response2 = productService.addProduct(product).execute();
        int id2 = response2.body().getId();
        assertThat(response2.code(), CoreMatchers.is(201));
        assertThat(id2, CoreMatchers.notNullValue());
        assertThat(response2.isSuccessful(), CoreMatchers.is(true));

        // checking that the product is the same
        assertThat(response.body().getTitle(), CoreMatchers.is(response2.body().getTitle()));
        assertThat(response.body().getPrice(), CoreMatchers.is(response2.body().getPrice()));
        assertThat(response.body().getCategoryTitle(), CoreMatchers.is(response2.body().getCategoryTitle()));

        // checking that id of the second entity is different
        assertThat(id, CoreMatchers.not(id2));

        // cleaning second product after test
        Response<ResponseBody> response3 = productService.deleteProductById(id2).execute();
        assertThat(response3.isSuccessful(), CoreMatchers.is(true));
    }

    @Test
    void addProductNegativeTestNonNullId() throws IOException {
        // testing Bad Request code for non null id in the request body
        product.setId(100);
        Response<Product> response = productService.addProduct(product).execute();
        String errorBody = response.errorBody().string();
        assertThat(response.code(), equalTo(400));
        assertThat(errorBody, CoreMatchers.containsString("Id must be null for new entity"));
    }

    @Test
    void addProductNegativeTestWrongCategoryTitle() throws IOException {
        // test for request body with wrong category title
        product.setCategoryTitle("Clothes");
        Response<Product> response = productService.addProduct(product).execute();
        String errorBody = response.errorBody().string();
        assertThat(response.code(), equalTo(500));
        assertThat(errorBody, CoreMatchers.containsString("Internal Server Error"));
    }

    @Test
    void addProductNegativeTestNoCategoryTitle() throws IOException {
        // testing Internal Server Error code for null category title in the request body
       product.setCategoryTitle(null);
        Response<Product> response = productService.addProduct(product).execute();
        String errorBody = response.errorBody().string();
        assertThat(response.code(), equalTo(500));
        assertThat(errorBody, CoreMatchers.containsString("Internal Server Error"));
    }

    @Test
    void addProductTestNoTitle() throws IOException {
        // add product with null title in the request body
        product.setTitle(null);
        Response<Product> response = productService.addProduct(product).execute();

        assertThat(response.code(), equalTo(201));
        id = response.body().getId();
        assertThat(id, CoreMatchers.notNullValue());
        assertThat(response.body().getPrice(), CoreMatchers.is(product.getPrice()));
    }

    @Test
    void addProductTestNoPrice() throws IOException {
        // add product with null price in the request body
        product.setPrice(null);
        Response<Product> response = productService.addProduct(product).execute();

        assertThat(response.code(), equalTo(201));
        id = response.body().getId();
        assertThat(id, CoreMatchers.notNullValue());
        assertThat(response.body().getTitle(), CoreMatchers.is(product.getTitle()));
        assertThat(response.body().getPrice(), CoreMatchers.is(0));
    }

    @Test
    void get404NotFoundError(){
        try {
            Response<Product> response = productService.addProductWrongEndpoint(product).execute();

            assertThat(response.code(), equalTo(404));
            assertThat(response.errorBody().string(), containsStringIgnoringCase("Not Found"));

        } catch (IOException e){
            e.printStackTrace();
        }
    }


    @AfterEach
    void tearDown() throws IOException {
       if (id != null) {
            Response<ResponseBody> response = productService.deleteProductById(id).execute();
            assertThat(response.isSuccessful(), CoreMatchers.is(true));
        }
    }
}

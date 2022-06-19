import api.ProductService;
import com.github.javafaker.Faker;
import dto.Product;
import okhttp3.ResponseBody;
import org.hamcrest.CoreMatchers;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import retrofit2.Response;
import utils.RetrofitUtils;

import java.io.IOException;

import static org.hamcrest.MatcherAssert.assertThat;

public class DeleteProductTest {
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
    void deleteProductPositiveTest() throws IOException {
        Response<Product> response = productService.addProduct(product).execute();
        id = response.body().getId();
        assertThat(response.code(), CoreMatchers.is(201));
        assertThat(id, CoreMatchers.notNullValue());
        Response<ResponseBody> response2 = productService.deleteProductById(id).execute();
        assertThat(response2.code(), CoreMatchers.is(200));
        assertThat(response2.isSuccessful(), CoreMatchers.is(true));
    }

    @Test
    void deleteProductNegativeTest() throws IOException {
        // trying to delete already deleted product
        Response<Product> response = productService.addProduct(product).execute();
        id = response.body().getId();
        assertThat(response.code(), CoreMatchers.is(201));
        assertThat(id, CoreMatchers.notNullValue());
        Response<ResponseBody> response2 = productService.deleteProductById(id).execute();
        assertThat(response2.code(), CoreMatchers.is(200));
        assertThat(response2.isSuccessful(), CoreMatchers.is(true));

        Response<ResponseBody> response3 = productService.deleteProductById(id).execute();
        assertThat(response3.code(), CoreMatchers.is(500));
        assertThat(response3.errorBody().string(), CoreMatchers.containsString("Internal Server Error"));
    }

    @Test
    void deleteProductNegativeTestNoId() throws IOException {
        // trying to delete product without id

        Response<ResponseBody> response = productService.deleteProductWithoutId().execute();
        assertThat(response.code(), CoreMatchers.is(405));
        assertThat(response.errorBody().string(), CoreMatchers.containsString("Method Not Allowed"));
    }
}

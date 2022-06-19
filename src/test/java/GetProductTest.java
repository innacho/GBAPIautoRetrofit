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

import static org.hamcrest.MatcherAssert.assertThat;

public class GetProductTest {
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
    void getProductPositiveTest() throws IOException {
        // getting info about existing product
        Response<Product> response = productService.addProduct(product).execute();
        id = response.body().getId();
        assertThat(response.code(), CoreMatchers.is(201));
        assertThat(id, CoreMatchers.notNullValue());
        Response<Product> response2 = productService.getProductById(id).execute();
        assertThat(response2.code(), CoreMatchers.is(200));
        assertThat(response2.body().getTitle(), CoreMatchers.is(product.getTitle()));
        assertThat(response2.body().getPrice(), CoreMatchers.is(product.getPrice()));
    }

    @Test
    void getProductNegativeTest() throws IOException {
        // getting info about nonexistent product
        Response<Product> response = productService.addProduct(product).execute();
        id = response.body().getId();
        assertThat(response.code(), CoreMatchers.is(201));
        assertThat(id, CoreMatchers.notNullValue());
        Response<Product> response2 = productService.getProductById(id+1).execute();
        assertThat(response2.code(), CoreMatchers.is(404));
        assertThat(response2.errorBody().string(), CoreMatchers.containsString("Unable to find product with id"));
    }

    @AfterEach
    void tearDown() throws IOException {
        if (id != null) {
            Response<ResponseBody> response = productService.deleteProductById(id).execute();
            assertThat(response.isSuccessful(), CoreMatchers.is(true));
        }
    }
}

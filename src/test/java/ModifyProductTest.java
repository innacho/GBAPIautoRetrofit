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

public class ModifyProductTest {
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
    void modifyProductPositiveTest() throws IOException {
        // changing title of existing product
        Response<Product> response = productService.addProduct(product).execute();
        id = response.body().getId();
        assertThat(response.code(), CoreMatchers.is(201));
        assertThat(response.body().getTitle(), CoreMatchers.is(product.getTitle()));
        assertThat(response.body().getPrice(), CoreMatchers.is(product.getPrice()));
        assertThat(response.body().getCategoryTitle(), CoreMatchers.is("Food"));

        response.body().setTitle(faker.food().ingredient());
        Response<Product> response2 = productService.editProduct(response.body()).execute();
        assertThat(response2.code(), CoreMatchers.is(200));

        //asserting that title has changed
        assertThat(response2.body().getTitle(), CoreMatchers.is(response.body().getTitle()));
        assertThat(response2.body().getTitle(), CoreMatchers.not(product.getTitle()));

        // asserting that all other fields are the same
        assertThat(response2.body().getId(), CoreMatchers.is(id));
        assertThat(response2.body().getPrice(), CoreMatchers.is(product.getPrice()));
        assertThat(response2.body().getCategoryTitle(), CoreMatchers.is(product.getCategoryTitle()));
    }

    @Test
    void modifyProductPositiveTestPrice() throws IOException {
        // changing price of existing product
        Response<Product> response = productService.addProduct(product).execute();
        id = response.body().getId();
        assertThat(response.code(), CoreMatchers.is(201));

        response.body().setPrice((int) (Math.random() * 10000));
        Response<Product> response2 = productService.editProduct(response.body()).execute();
        assertThat(response2.code(), CoreMatchers.is(200));

        //asserting that price has changed
        assertThat(response2.body().getPrice(), CoreMatchers.is(response.body().getPrice()));
        assertThat(response2.body().getPrice(), CoreMatchers.not(product.getPrice()));

        // asserting that all other fields are the same
        assertThat(response2.body().getId(), CoreMatchers.is(id));
        assertThat(response2.body().getTitle(), CoreMatchers.is(product.getTitle()));
        assertThat(response2.body().getCategoryTitle(), CoreMatchers.is(product.getCategoryTitle()));
    }

    @Test
    void modifyProductPositiveTestChangeCategory() throws IOException {
        // changing category title of existing product
        Response<Product> response = productService.addProduct(product).execute();
        id = response.body().getId();
        assertThat(response.code(), CoreMatchers.is(201));

        response.body().setCategoryTitle("Electronic");
        Response<Product> response2 = productService.editProduct(response.body()).execute();
        assertThat(response2.code(), CoreMatchers.is(200));

        //asserting that category title has changed
        assertThat(response2.body().getCategoryTitle(), CoreMatchers.is("Electronic"));

        // asserting that all other fields are the same
        assertThat(response2.body().getId(), CoreMatchers.is(id));
        assertThat(response2.body().getTitle(), CoreMatchers.is(product.getTitle()));
        assertThat(response2.body().getPrice(), CoreMatchers.is(product.getPrice()));
    }

    @Test
    void modifyProductPositiveTestChangeAll() throws IOException {
        // changing all fields of existing product
        Response<Product> response = productService.addProduct(product).execute();
        id = response.body().getId();
        assertThat(response.code(), CoreMatchers.is(201));

        response.body().setTitle(faker.food().ingredient());
        response.body().setPrice((int) (Math.random() * 10000));
        response.body().setCategoryTitle("Electronic");
        Response<Product> response2 = productService.editProduct(response.body()).execute();
        assertThat(response2.code(), CoreMatchers.is(200));

        // asserting that id is the same
        assertThat(response2.body().getId(), CoreMatchers.is(id));

        //asserting that all fields have changed
        assertThat(response2.body().getCategoryTitle(), CoreMatchers.is("Electronic"));
        assertThat(response2.body().getTitle(), CoreMatchers.not(product.getTitle()));
        assertThat(response2.body().getPrice(), CoreMatchers.not(product.getPrice()));
    }

    @Test
    void modifyProductNegativeTestWrongId() throws IOException {
        // trying to change product for not valid id
        Response<Product> response = productService.addProduct(product).execute();
        id = response.body().getId();
        assertThat(response.code(), CoreMatchers.is(201));

        response.body().setId(id+1);
        Response<Product> response2 = productService.editProduct(response.body()).execute();

        assertThat(response2.code(), CoreMatchers.is(400));
        assertThat(response2.errorBody().string(), CoreMatchers.containsString("Product with id: "+(id+1)+" doesn't exist"));

        // asserting that product hasn't changed
        Response<Product> response3 = productService.getProductById(id).execute();
        assertThat(response3.body().getCategoryTitle(), CoreMatchers.is(product.getCategoryTitle()));
        assertThat(response3.body().getTitle(), CoreMatchers.is(product.getTitle()));
        assertThat(response3.body().getPrice(), CoreMatchers.is(product.getPrice()));
    }

    @Test
    void modifyProductNegativeTestChangeCategory() throws IOException {
        // trying to change category of existing product for not valid category title value
        Response<Product> response = productService.addProduct(product).execute();
        id = response.body().getId();
        assertThat(response.code(), CoreMatchers.is(201));

        response.body().setCategoryTitle("Clothes");
        Response<Product> response2 = productService.editProduct(response.body()).execute();

        assertThat(response2.code(), CoreMatchers.is(500));
        assertThat(response2.errorBody().string(), CoreMatchers.containsString("Internal Server Error"));

        // asserting that product hasn't changed
        Response<Product> response3 = productService.getProductById(id).execute();
        assertThat(response3.body().getCategoryTitle(), CoreMatchers.is(product.getCategoryTitle()));
        assertThat(response3.body().getTitle(), CoreMatchers.is(product.getTitle()));
        assertThat(response3.body().getPrice(), CoreMatchers.is(product.getPrice()));
    }

    @Test
    void modifyProductNegativeTestNullId() throws IOException {
        // trying to change existing product for not valid category title value
        Response<Product> response = productService.addProduct(product).execute();
        id = response.body().getId();
        assertThat(response.code(), CoreMatchers.is(201));

        response.body().setId(null);
        response.body().setPrice(102);
        Response<Product> response2 = productService.editProduct(response.body()).execute();

        assertThat(response2.code(), CoreMatchers.is(400));
        assertThat(response2.errorBody().string(), CoreMatchers.containsString("Id must be not null"));

        // asserting that product hasn't changed
        Response<Product> response3 = productService.getProductById(id).execute();
        assertThat(response3.body().getCategoryTitle(), CoreMatchers.is(product.getCategoryTitle()));
        assertThat(response3.body().getTitle(), CoreMatchers.is(product.getTitle()));
        assertThat(response3.body().getPrice(), CoreMatchers.is(product.getPrice()));
    }

    @AfterEach
    void tearDown() throws IOException {
        if (id != null) {
            Response<ResponseBody> response = productService.deleteProductById(id).execute();
            assertThat(response.isSuccessful(), CoreMatchers.is(true));
        }
    }
}

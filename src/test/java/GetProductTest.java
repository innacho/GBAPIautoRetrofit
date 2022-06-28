import db.model.Products;
import dto.Product;
import org.hamcrest.CoreMatchers;
import org.junit.jupiter.api.Test;
import retrofit2.Response;

import java.io.IOException;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;

public class GetProductTest extends ProductAbstractTest{
    Integer id = null;

    @Test
    void getProductPositiveTest() throws IOException {
        // getting info about existing product
        Response<Product> response = getProductService().addProduct(getProduct()).execute();
        id = response.body().getId();
        assertThat(response.code(), CoreMatchers.is(201));
        assertThat(id, CoreMatchers.notNullValue());
        Response<Product> response2 = getProductService().getProductById(id).execute();
        assertThat(response2.code(), CoreMatchers.is(200));
        assertThat(response2.body().getTitle(), CoreMatchers.is(getProduct().getTitle()));
        assertThat(response2.body().getPrice(), CoreMatchers.is(getProduct().getPrice()));
        // asserts for DB
        List<Products> list = getMyBatisUtils().getProductById(id);
        Products productDB = list.get(0);
        assertThat(getProduct().getTitle(), CoreMatchers.is(productDB.getTitle()));
        assertThat(getProduct().getPrice(), CoreMatchers.is(productDB.getPrice()));
        assertThat(getProduct().getCategoryTitle(), CoreMatchers
                .is(getMyBatisUtils().selectFromCategoriesById(productDB.getCategory_id()).get(0).getTitle()));
    }

    @Test
    void getProductNegativeTest() throws IOException {
        // getting info about nonexistent product
        int maxId = (int)getMyBatisUtils().getMaxProductId();
        Response<Product> response2 = getProductService().getProductById(maxId+1).execute();
        assertThat(response2.code(), CoreMatchers.is(404));
        assertThat(response2.errorBody().string(), CoreMatchers.containsString("Unable to find product with id"));
    }
}

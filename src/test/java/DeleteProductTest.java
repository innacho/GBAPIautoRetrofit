import db.model.Products;
import dto.Product;
import okhttp3.ResponseBody;
import org.hamcrest.CoreMatchers;
import org.junit.jupiter.api.Test;
import retrofit2.Response;

import java.io.IOException;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;

public class DeleteProductTest extends ProductAbstractTest{
    private Integer id = null;

    @Test
    void deleteProductPositiveTest() throws IOException {
        Response<Product> response = getProductService().addProduct(getProduct()).execute();
        id = response.body().getId();
        assertThat(response.code(), CoreMatchers.is(201));
        assertThat(id, CoreMatchers.notNullValue());
        Response<ResponseBody> response2 = getProductService().deleteProductById(id).execute();
        assertThat(response2.code(), CoreMatchers.is(200));
        assertThat(response2.isSuccessful(), CoreMatchers.is(true));
        // checking there is no product for deleted id in DB
        List<Products> list = getMyBatisUtils().getProductById((long)id);
        assertThat(list.size(), CoreMatchers.is(0));
    }

    @Test
    void deleteProductNegativeTest() throws IOException {
        // trying to delete already deleted product
        Response<Product> response = getProductService().addProduct(getProduct()).execute();
        id = response.body().getId();
        assertThat(response.code(), CoreMatchers.is(201));
        assertThat(id, CoreMatchers.notNullValue());
        Response<ResponseBody> response2 = getProductService().deleteProductById(id).execute();
        assertThat(response2.code(), CoreMatchers.is(200));
        assertThat(response2.isSuccessful(), CoreMatchers.is(true));
        // checking there is no product for deleted id in DB
        List<Products> list = getMyBatisUtils().getProductById((long)id);
        assertThat(list.size(), CoreMatchers.is(0));

        // trying to delete product with the same id for the second time
        Response<ResponseBody> response3 = getProductService().deleteProductById(id).execute();
        assertThat(response3.code(), CoreMatchers.is(500));
        assertThat(response3.errorBody().string(), CoreMatchers.containsString("Internal Server Error"));
    }

    @Test
    void deleteProductNegativeTestNoId() throws IOException {
        // trying to delete product without id
        Response<ResponseBody> response = getProductService().deleteProductWithoutId().execute();
        assertThat(response.code(), CoreMatchers.is(405));
        assertThat(response.errorBody().string(), CoreMatchers.containsString("Method Not Allowed"));
    }
}

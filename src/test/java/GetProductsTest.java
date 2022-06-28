import db.model.Products;
import dto.Product;
import org.hamcrest.CoreMatchers;
import org.junit.jupiter.api.Test;
import retrofit2.Response;

import java.io.IOException;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;

public class GetProductsTest extends ProductAbstractTest{

    @Test
    void getProductsPositiveTest() throws IOException {
        Response<Product[]> response = getProductService().getProducts().execute();
        assertThat(response.code(), CoreMatchers.is(200));
        // getting all rows from products DB table
        List<Products> productsListDB = getMyBatisUtils().getAllProducts();
        // asserting that we got whole products table from DB
        for(int i = 0; i < productsListDB.size(); i ++){
            assertThat((long) response.body()[i].getId(), CoreMatchers.is(productsListDB.get(i).getId()));
            assertThat(response.body()[i].getTitle(), CoreMatchers.is(productsListDB.get(i).getTitle()));
            assertThat(response.body()[i].getPrice(), CoreMatchers.is(productsListDB.get(i).getPrice()));
            assertThat(response.body()[i].getCategoryTitle(), CoreMatchers
                    .is(getMyBatisUtils().selectFromCategoriesById(productsListDB.get(i).getCategory_id()).get(0).getTitle()));
        }
    }
}

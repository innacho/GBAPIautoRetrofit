import db.model.Categories;
import db.model.Products;
import dto.Product;
import org.hamcrest.CoreMatchers;
import org.junit.jupiter.api.Test;
import retrofit2.Response;

import java.io.IOException;
import java.util.List;

import static org.hamcrest.CoreMatchers.containsStringIgnoringCase;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

public class AddProductTest extends ProductAbstractTest {
    private Integer id;

    @Test
    void addProductPositiveTestForFoodCategory() throws IOException {
        Response<Product> response = getProductService().addProduct(getProduct()).execute();
        id = response.body().getId();
        assertThat(response.code(), CoreMatchers.is(201));
        assertThat(id, CoreMatchers.notNullValue());
        assertThat(response.isSuccessful(), CoreMatchers.is(true));
        Products productDB = getMyBatisUtils().selectFromProductsById(id);
        assertThat(response.body().getTitle(), CoreMatchers.is(productDB.getTitle()));
        assertThat(response.body().getPrice(), CoreMatchers.is(productDB.getPrice()));
        assertThat(response.body().getCategoryTitle(), CoreMatchers
                .is(getMyBatisUtils().selectFromCategoriesById(productDB.getCategory_id()).get(0).getTitle()));
    }

    @Test
    void addProductPositiveTestForElectronicCategory() throws IOException {
        getProduct().setCategoryTitle("Electronic");
        getProduct().setTitle(getFaker().commerce().productName());
        Response<Product> response = getProductService().addProduct(getProduct()).execute();
        id = response.body().getId();
        assertThat(response.code(), CoreMatchers.is(201));
        assertThat(id, CoreMatchers.notNullValue());
        Products productDB = getMyBatisUtils().selectFromProductsById(id);
        assertThat(response.body().getTitle(), CoreMatchers.is(productDB.getTitle()));
        assertThat(response.body().getPrice(), CoreMatchers.is(productDB.getPrice()));
        assertThat(response.body().getCategoryTitle(), CoreMatchers
                .is(getMyBatisUtils().selectFromCategoriesById(productDB.getCategory_id()).get(0).getTitle()));
    }

    @Test
    void addTheSameProductTwice() throws IOException {
        Response<Product> response = getProductService().addProduct(getProduct()).execute();
        id = response.body().getId();
        assertThat(response.code(), CoreMatchers.is(201));
        assertThat(id, CoreMatchers.notNullValue());
        assertThat(response.isSuccessful(), CoreMatchers.is(true));

        // adding the same product for the second time
        Response<Product> response2 = getProductService().addProduct(getProduct()).execute();
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

        // checking DB has both products
        List<Products> list = getMyBatisUtils().getProductsByTitle(getProduct().getTitle());
        assertThat(list.size(), CoreMatchers.is(2));
        assertThat(list.get(0).getId(), CoreMatchers.is((long) id));
        assertThat(list.get(1).getId(), CoreMatchers.is((long) id2));
    }

    @Test
    void addProductNegativeTestNonNullId() throws IOException {
        // testing Bad Request code for non null id in the request body
        //checking for max Id in DB
        int maxId = (int) getMyBatisUtils().getMaxProductId();
        getProduct().setId(maxId+1);
        Response<Product> response = getProductService().addProduct(getProduct()).execute();
        String errorBody = response.errorBody().string();
        assertThat(response.code(), equalTo(400));
        assertThat(errorBody, CoreMatchers.containsString("Id must be null for new entity"));
    }

    @Test
    void addProductNegativeTestWrongCategoryTitle() throws IOException {
        // test for request body with wrong category title
        // check in DB that there is no such category in categories table
        List<Categories> allCategories = getMyBatisUtils().getAllCategories();
        String nonExistentCategoryTitle;
        while(true){
         nonExistentCategoryTitle = getFaker().commerce().productName();
            System.out.println(nonExistentCategoryTitle);
        for (Categories category : allCategories){
            if(category.getTitle().equals(nonExistentCategoryTitle)) continue;
        }
        break;
        }
        getProduct().setCategoryTitle(nonExistentCategoryTitle);
        Response<Product> response = getProductService().addProduct(getProduct()).execute();
        String errorBody = response.errorBody().string();
        assertThat(response.code(), equalTo(500));
        assertThat(errorBody, CoreMatchers.containsString("Internal Server Error"));
    }

    @Test
    void addProductNegativeTestNoCategoryTitle() throws IOException {
        // testing Internal Server Error code for null category title in the request body
        getProduct().setCategoryTitle(null);
        Response<Product> response = getProductService().addProduct(getProduct()).execute();
        String errorBody = response.errorBody().string();
        assertThat(response.code(), equalTo(500));
        assertThat(errorBody, CoreMatchers.containsString("Internal Server Error"));
    }

    @Test
    void addProductTestNoTitle() throws IOException {
        // add product with null title in the request body
        getProduct().setTitle(null);
        Response<Product> response = getProductService().addProduct(getProduct()).execute();

        assertThat(response.code(), equalTo(201));
        id = response.body().getId();
        assertThat(id, CoreMatchers.notNullValue());
        assertThat(response.body().getPrice(), CoreMatchers.is(getProduct().getPrice()));
    }

    @Test
    void addProductTestNoPrice() throws IOException {
        // add product with null price in the request body
        getProduct().setPrice(null);
        Response<Product> response = getProductService().addProduct(getProduct()).execute();

        assertThat(response.code(), equalTo(201));
        id = response.body().getId();
        assertThat(id, CoreMatchers.notNullValue());
        assertThat(response.body().getTitle(), CoreMatchers.is(getProduct().getTitle()));
        assertThat(response.body().getPrice(), CoreMatchers.is(0));
    }

    @Test
    void get404NotFoundError(){
        try {
            Response<Product> response =  getProductService().addProductWrongEndpoint(getProduct()).execute();

            assertThat(response.code(), equalTo(404));
            assertThat(response.errorBody().string(), containsStringIgnoringCase("Not Found"));

        } catch (IOException e){
            e.printStackTrace();
        }
    }
}

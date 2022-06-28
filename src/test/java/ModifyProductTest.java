import db.model.Categories;
import db.model.Products;
import dto.Product;
import org.hamcrest.CoreMatchers;
import org.junit.jupiter.api.Test;
import retrofit2.Response;

import java.io.IOException;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;

public class ModifyProductTest extends ProductAbstractTest{
    Integer id = null;

    @Test
    void modifyProductPositiveTest() throws IOException {
        // changing title of existing product
        Response<Product> response = getProductService().addProduct(getProduct()).execute();
        id = response.body().getId();
        assertThat(response.code(), CoreMatchers.is(201));
        assertThat(response.body().getTitle(), CoreMatchers.is(getProduct().getTitle()));
        assertThat(response.body().getPrice(), CoreMatchers.is(getProduct().getPrice()));
        assertThat(response.body().getCategoryTitle(), CoreMatchers.is(getProduct().getCategoryTitle()));

        response.body().setTitle(getFaker().food().ingredient());
        Response<Product> response2 = getProductService().editProduct(response.body()).execute();
        assertThat(response2.code(), CoreMatchers.is(200));

        //asserting that title has changed
        assertThat(response2.body().getTitle(), CoreMatchers.is(response.body().getTitle()));
        assertThat(response2.body().getTitle(), CoreMatchers.not(getProduct().getTitle()));

        List<Products> list = getMyBatisUtils().getProductById(id);
        Products productDB = list.get(0);
        assertThat(productDB.getTitle(), CoreMatchers.is(response.body().getTitle()));

        // asserting that all other fields are the same
        assertThat(response2.body().getId(), CoreMatchers.is(id));
        assertThat(response2.body().getPrice(), CoreMatchers.is(getProduct().getPrice()));
        assertThat(response2.body().getCategoryTitle(), CoreMatchers.is(getProduct().getCategoryTitle()));

        assertThat(getProduct().getPrice(), CoreMatchers.is(productDB.getPrice()));
        assertThat(getProduct().getCategoryTitle(), CoreMatchers
                .is(getMyBatisUtils().selectFromCategoriesById(productDB.getCategory_id()).get(0).getTitle()));
    }

    @Test
    void modifyProductPositiveTestPrice() throws IOException {
        // changing price of existing product
        Response<Product> response = getProductService().addProduct(getProduct()).execute();
        id = response.body().getId();
        assertThat(response.code(), CoreMatchers.is(201));

        response.body().setPrice((int) (Math.random() * 10000));
        Response<Product> response2 = getProductService().editProduct(response.body()).execute();
        assertThat(response2.code(), CoreMatchers.is(200));

        //asserting that price has changed
        assertThat(response2.body().getPrice(), CoreMatchers.is(response.body().getPrice()));
        assertThat(response2.body().getPrice(), CoreMatchers.not(getProduct().getPrice()));

        List<Products> list = getMyBatisUtils().getProductById(id);
        Products productDB = list.get(0);
        assertThat(productDB.getPrice(), CoreMatchers.is(response.body().getPrice()));

        // asserting that all other fields are the same
        assertThat(response2.body().getId(), CoreMatchers.is(id));
        assertThat(response2.body().getTitle(), CoreMatchers.is(getProduct().getTitle()));
        assertThat(response2.body().getCategoryTitle(), CoreMatchers.is(getProduct().getCategoryTitle()));

        assertThat(getProduct().getTitle(), CoreMatchers.is(productDB.getTitle()));
        assertThat(getProduct().getCategoryTitle(), CoreMatchers
                .is(getMyBatisUtils().selectFromCategoriesById(productDB.getCategory_id()).get(0).getTitle()));
    }

    @Test
    void modifyProductPositiveTestChangeCategory() throws IOException {
        // changing category title of existing product
        Response<Product> response = getProductService().addProduct(getProduct()).execute();
        id = response.body().getId();
        assertThat(response.code(), CoreMatchers.is(201));

        response.body().setCategoryTitle("Electronic");
        Response<Product> response2 = getProductService().editProduct(response.body()).execute();
        assertThat(response2.code(), CoreMatchers.is(200));

        //asserting that category title has changed
        assertThat(response2.body().getCategoryTitle(), CoreMatchers.is("Electronic"));

        List<Products> list = getMyBatisUtils().getProductById(id);
        Products productDB = list.get(0);
        assertThat(response.body().getCategoryTitle(), CoreMatchers
                .is(getMyBatisUtils().selectFromCategoriesById(productDB.getCategory_id()).get(0).getTitle()));

        // asserting that all other fields are the same
        assertThat(response2.body().getId(), CoreMatchers.is(id));
        assertThat(response2.body().getTitle(), CoreMatchers.is(getProduct().getTitle()));
        assertThat(response2.body().getPrice(), CoreMatchers.is(getProduct().getPrice()));

        assertThat(getProduct().getTitle(), CoreMatchers.is(productDB.getTitle()));
        assertThat(getProduct().getPrice(), CoreMatchers.is(productDB.getPrice()));
    }

    @Test
    void modifyProductPositiveTestChangeAll() throws IOException {
        // changing all fields of existing product
        Response<Product> response = getProductService().addProduct(getProduct()).execute();
        id = response.body().getId();
        assertThat(response.code(), CoreMatchers.is(201));

        response.body().setTitle(getFaker().commerce().productName());
        response.body().setPrice((int) (Math.random() * 10000));
        response.body().setCategoryTitle("Electronic");
        Response<Product> response2 = getProductService().editProduct(response.body()).execute();
        assertThat(response2.code(), CoreMatchers.is(200));

        // asserting that id is the same
        assertThat(response2.body().getId(), CoreMatchers.is(id));

        //asserting that all fields have changed
        assertThat(response2.body().getCategoryTitle(), CoreMatchers.not(getProduct().getCategoryTitle()));
        assertThat(response2.body().getTitle(), CoreMatchers.not(getProduct().getTitle()));
        assertThat(response2.body().getPrice(), CoreMatchers.not(getProduct().getPrice()));

        List<Products> list = getMyBatisUtils().getProductById(id);
        Products productDB = list.get(0);
        assertThat(response.body().getTitle(), CoreMatchers.is(productDB.getTitle()));
        assertThat(response.body().getCategoryTitle(), CoreMatchers
                .is(getMyBatisUtils().selectFromCategoriesById(productDB.getCategory_id()).get(0).getTitle()));
    }

    @Test
    void modifyProductNegativeTestWrongId() throws IOException {
        // trying to change product for not valid id
        Response<Product> response = getProductService().addProduct(getProduct()).execute();
        id = response.body().getId();
        assertThat(response.code(), CoreMatchers.is(201));

        response.body().setId(id+1);
        Response<Product> response2 = getProductService().editProduct(response.body()).execute();

        assertThat(response2.code(), CoreMatchers.is(400));
        assertThat(response2.errorBody().string(), CoreMatchers.containsString("Product with id: "+(id+1)+" doesn't exist"));

        // asserting that product hasn't changed
        Response<Product> response3 = getProductService().getProductById(id).execute();
        assertThat(response3.body().getCategoryTitle(), CoreMatchers.is(getProduct().getCategoryTitle()));
        assertThat(response3.body().getTitle(), CoreMatchers.is(getProduct().getTitle()));
        assertThat(response3.body().getPrice(), CoreMatchers.is(getProduct().getPrice()));

        List<Products> list = getMyBatisUtils().getProductById(id);
        Products productDB = list.get(0);
        assertThat(getProduct().getTitle(), CoreMatchers.is(productDB.getTitle()));
        assertThat(getProduct().getCategoryTitle(), CoreMatchers
                .is(getMyBatisUtils().selectFromCategoriesById(productDB.getCategory_id()).get(0).getTitle()));
    }

    @Test
    void modifyProductNegativeTestChangeCategory() throws IOException {
        // trying to change category of existing product for not valid category title value
        Response<Product> response = getProductService().addProduct(getProduct()).execute();
        id = response.body().getId();
        assertThat(response.code(), CoreMatchers.is(201));

        // generating category title that is not in DB
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

        response.body().setCategoryTitle(nonExistentCategoryTitle);
        Response<Product> response2 = getProductService().editProduct(response.body()).execute();

        assertThat(response2.code(), CoreMatchers.is(500));
        assertThat(response2.errorBody().string(), CoreMatchers.containsString("Internal Server Error"));

        // asserting that product hasn't changed
        Response<Product> response3 = getProductService().getProductById(id).execute();
        assertThat(response3.body().getCategoryTitle(), CoreMatchers.is(getProduct().getCategoryTitle()));
        assertThat(response3.body().getTitle(), CoreMatchers.is(getProduct().getTitle()));
        assertThat(response3.body().getPrice(), CoreMatchers.is(getProduct().getPrice()));

        List<Products> list = getMyBatisUtils().getProductById(id);
        Products productDB = list.get(0);
        assertThat(getProduct().getTitle(), CoreMatchers.is(productDB.getTitle()));
        assertThat(getProduct().getCategoryTitle(), CoreMatchers
                .is(getMyBatisUtils().selectFromCategoriesById(productDB.getCategory_id()).get(0).getTitle()));
    }

    @Test
    void modifyProductNegativeTestNullId() throws IOException {
        // trying to change existing product for not valid category title value
        Response<Product> response = getProductService().addProduct(getProduct()).execute();
        id = response.body().getId();
        assertThat(response.code(), CoreMatchers.is(201));

        response.body().setId(null);
        response.body().setPrice(102);
        Response<Product> response2 = getProductService().editProduct(response.body()).execute();

        assertThat(response2.code(), CoreMatchers.is(400));
        assertThat(response2.errorBody().string(), CoreMatchers.containsString("Id must be not null"));

        // asserting that product hasn't changed
        Response<Product> response3 = getProductService().getProductById(id).execute();
        assertThat(response3.body().getCategoryTitle(), CoreMatchers.is(getProduct().getCategoryTitle()));
        assertThat(response3.body().getTitle(), CoreMatchers.is(getProduct().getTitle()));
        assertThat(response3.body().getPrice(), CoreMatchers.is(getProduct().getPrice()));

        List<Products> list = getMyBatisUtils().getProductById(id);
        Products productDB = list.get(0);
        assertThat(getProduct().getPrice(), CoreMatchers.is(productDB.getPrice()));
    }
}

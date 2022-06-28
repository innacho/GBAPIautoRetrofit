import api.ProductService;
import com.github.javafaker.Faker;
import dto.Product;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import utils.RetrofitUtils;

public abstract class ProductAbstractTest extends AbstractTest{
    private static ProductService productService;
    private Product product = null;
    private Faker faker = new Faker();


    public static ProductService getProductService() {
        return productService;
    }

    public Product getProduct() {
        return product;
    }

    public Faker getFaker() {
        return faker;
    }

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
    }
}

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import utils.MyBatisUtils;

import java.io.IOException;

public abstract class AbstractTest {
    private static MyBatisUtils myBatisUtils;
    private static long maxProductsId;
    private static long maxCategoriesId;

    public static MyBatisUtils getMyBatisUtils() {
        return myBatisUtils;
    }

    @BeforeAll
    public static void init() throws IOException {
        myBatisUtils = new MyBatisUtils();
        myBatisUtils.initSession();
        maxCategoriesId = myBatisUtils.getMaxCategoryId();
        maxProductsId = myBatisUtils.getMaxProductId();

        myBatisUtils.addNewCategoryWithTitle("TestCategory");
    }

    @AfterAll
    public static void finish() {
        myBatisUtils.deleteExtraProducts(maxProductsId);
        myBatisUtils.deleteExtraCategories(maxCategoriesId);
        myBatisUtils.closeSession();
    }
}

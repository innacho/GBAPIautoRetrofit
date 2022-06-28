package utils;

import db.dao.CategoriesMapper;
import db.dao.ProductsMapper;
import db.model.Categories;
import db.model.CategoriesExample;
import db.model.Products;
import db.model.ProductsExample;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public class MyBatisUtils {
    SqlSession session = null;
    CategoriesMapper categoriesMapper;
    ProductsMapper productsMapper;


    public void initSession() throws IOException {
            String resource = "mybatis-config.xml";
            InputStream inputStream = Resources.getResourceAsStream(resource);
            SqlSessionFactory sqlSessionFactory = new
                    SqlSessionFactoryBuilder().build(inputStream);
            session = sqlSessionFactory.openSession();

            categoriesMapper = session.getMapper(CategoriesMapper.class);
            productsMapper = session.getMapper(ProductsMapper.class);

    }

    public void closeSession(){
        session.close();
    }

    public List<Categories> selectFromCategoriesById(long id){
        CategoriesExample example = new CategoriesExample();
        example.createCriteria().andIdEqualTo(id);
        List<Categories> list = categoriesMapper.selectByExample(example);
        return list;
    }

    public Products selectFromProductsById(long id){
        ProductsExample example = new ProductsExample();
        example.createCriteria().andIdEqualTo((long) id);
        return productsMapper.selectByExample(example).get(0);
    }

    public int countCategories(){
        CategoriesExample example = new CategoriesExample();
        return (int) categoriesMapper.countByExample(example);
    }

    public List<Categories> getAllCategories(){
        CategoriesExample example = new CategoriesExample();
        List<Categories> list = categoriesMapper.selectByExample(example);
        return list;
    }

    public void addNewCategoryWithTitle(String title){
        Categories categories = new Categories();
        categories.setTitle(title);
        categoriesMapper.insert(categories);
        session.commit();
    }

    public void deleteCategoriesWithTitleLike(String title){
        CategoriesExample example = new CategoriesExample();
        example.createCriteria().andTitleLike("%"+title+"%");
        List<Categories> list = categoriesMapper.selectByExample(example);

        for (Categories category : list) {
            categoriesMapper.deleteByPrimaryKey(category.getId());
            session.commit();
        }
    }

    public void deleteExtraCategories(long maxId){
        CategoriesExample example = new CategoriesExample();
        example.createCriteria().andIdGreaterThan(maxId);
        List<Categories> list = categoriesMapper.selectByExample(example);

        for (Categories category : list) {
            categoriesMapper.deleteByPrimaryKey(category.getId());
            session.commit();
        }
    }

    public void deleteExtraProducts(long maxId){
        ProductsExample example = new ProductsExample();
        example.createCriteria().andIdGreaterThan(maxId);
        List<Products> list = productsMapper.selectByExample(example);

        for (Products products : list) {
            productsMapper.deleteByPrimaryKey(products.getId());
            session.commit();
        }
    }

    public void updateCategoriesWithTitleLike(String title){
        CategoriesExample example = new CategoriesExample();
        example.createCriteria().andTitleLike("%"+title+"%");
        List<Categories> list = categoriesMapper.selectByExample(example);

        for (Categories category : list) {
            category.setTitle("test100");
            categoriesMapper.updateByPrimaryKey(category);
            session.commit();
        }
    }

    public List<Products> getAllProducts(){
        ProductsExample example = new ProductsExample();
        List<Products> list = productsMapper.selectByExample(example);
        return list;
    }

    public List<Products> getProductsByTitle(String title){
        ProductsExample example = new ProductsExample();
        example.createCriteria().andTitleEqualTo(title);
        List<Products> list = productsMapper.selectByExample(example);
        return list;
    }

    public List<Products> getProductById(long id){
        ProductsExample example = new ProductsExample();
        example.createCriteria().andIdEqualTo(id);
        List<Products> list = productsMapper.selectByExample(example);
        return list;
    }

    public List<Products> getAllProductsForCategoryId(long id){
        ProductsExample example = new ProductsExample();
        example.createCriteria().andCategory_idEqualTo(id);
        List<Products> list = productsMapper.selectByExample(example);
        return list;
    }

    public long getMaxProductId(){
        List<Products> list = getAllProducts();
        return list.get(list.size()-1).getId();
    }

    public long getMaxCategoryId(){
        List<Categories> list = getAllCategories();
        return list.get(list.size()-1).getId();
    }

}

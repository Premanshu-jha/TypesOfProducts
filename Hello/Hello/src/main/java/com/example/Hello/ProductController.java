package com.example.Hello;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

@RestController
@RequestMapping("/products")
public class ProductController {

    //private final GreetingService greetingService;
    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;

    @Autowired
    public ProductController(GreetingService greetingService, ProductRepository productRepository, CategoryRepository categoryRepository){
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
    }

    private ProductDto convertToDto(Product product){
        ProductDto productDto = new ProductDto();
        productDto.setId(product.getId());
        productDto.setName(product.getName());
        productDto.setPrice(product.getPrice());

        CategoryDto category = new CategoryDto();
        category.setId(product.getCategory().getId());
        category.setName(product.getCategory().getName());


        productDto.setCategoryDto(category);

        return productDto;
    }

    @GetMapping
    public ResponseEntity<List<ProductDto>> getAllProducts(){
        List<Product> products = productRepository.findAll();
        List<ProductDto> productDtos = new ArrayList<>();

        for(Product product:products){
            productDtos.add(convertToDto(product));
        }
        return new ResponseEntity<>(productDtos,HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductDto> getProductById(@PathVariable Long id){
        Product product = productRepository.findById(id).orElse(null);
        if(product != null){
            ProductDto productDto = convertToDto(product);
            return new ResponseEntity<>(productDto,HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @PostMapping
    public ResponseEntity<ProductDto> postProduct(@RequestBody Product product){
        if(product.getCategory()!=null) {
            Category category = categoryRepository.findById(product.getCategory().getId()).orElse(null);
            if (category == null)
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            product.setCategory(category);
        }
        productRepository.save(product);
        ProductDto productDto = convertToDto(product);
        return new ResponseEntity<>(productDto,HttpStatus.CREATED);
    }


    @PatchMapping("/{id}")
    public ResponseEntity<?> updateProduct(@PathVariable Long id, @RequestBody Map<String,Object> updates){
        Optional<Product> existingProduct = productRepository.findById(id);
        AtomicBoolean categoryNotFound = new AtomicBoolean(false);
        if(existingProduct.isPresent()){
            Product product = existingProduct.get();
             updates.forEach((key,value)->{
                 switch(key){
                     case "name":
                         product.setName((String)value);
                         break;

                     case "price":
                         product.setPrice(((Number)value).doubleValue());
                         break;

                     case "category":
                         if(value instanceof Map){
                             Map<String,Object> categoryMap = (Map<String,Object>)value;
                             Long categoryId = null;
                             String categoryName = null;

                             if(categoryMap.containsKey("id")){
                                 categoryId = ((Number)categoryMap.get("id")).longValue();
                             }
                             if(categoryMap.containsKey("name")){
                                 categoryName = (String)categoryMap.get("name");
                             }
                             if(categoryId!=null) {
                                 Category newCategory = categoryRepository.findById(categoryId).orElse(null);
                                 if(newCategory!=null) {
                                     if (categoryName != null) newCategory.setName(categoryName);
                                     product.setCategory(newCategory);
                                 }
                                 else
                                     categoryNotFound.set(true);
                             }
                             else{
                                 Category existingCategory = product.getCategory();
                                 if(existingCategory != null) {
                                     if (categoryName != null) existingCategory.setName(categoryName);
                                     product.setCategory(existingCategory);
                                 }
                                 else
                                     categoryNotFound.set(true);



                             }

                         }
                 }
             });

            if (categoryNotFound.get()) {
                return new ResponseEntity<>("Category not found for given Product", HttpStatus.NOT_FOUND);
            }

            productRepository.save(product);
            ProductDto productDto = convertToDto(product);
             return new ResponseEntity<>(productDto,HttpStatus.OK);
        }
       return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id){
        if(productRepository.existsById(id)) {
            productRepository.deleteById(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        else
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }




}

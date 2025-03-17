package com.example.Hello;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/categories")
public class CategoryController {

    private final CategoryRepository categoryRepository;

    @Autowired
    public CategoryController(CategoryRepository categoryRepository){
        this.categoryRepository = categoryRepository;
    }

    private CategoryDto convertToDto(Category category){
        CategoryDto categoryDto = new CategoryDto();
        categoryDto.setId(category.getId());
        categoryDto.setName(category.getName());


        return categoryDto;
    }

    @GetMapping
    public ResponseEntity<List<CategoryDto>> getAllCategories(){
        List<Category> categories = categoryRepository.findAll();
        List<CategoryDto> categoryDtos = new ArrayList<>();

        for(Category category:categories){
            categoryDtos.add(convertToDto(category));
        }
        return new ResponseEntity<>(categoryDtos,HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CategoryDto> getCategoryById(@PathVariable Long id){
       Category category = categoryRepository.findById(id).orElse(null);
       if(category != null) {
           CategoryDto categoryDto = convertToDto(category);
           return new ResponseEntity<>(categoryDto, HttpStatus.OK);
       }
       else
           return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @GetMapping("/{id}/products")
    public ResponseEntity<CategoryDtoWithProducts> getCategoryByIdWithProducts(@PathVariable Long id){
        Category category = categoryRepository.findById(id).orElse(null);

        if(category != null){
            CategoryDto categoryDto = convertToDto(category);
            List<Product> products = category.getProducts();
            List<ProductDto> productDtos = new ArrayList<>();
            for(Product product:products){
                ProductDto productDto = new ProductDto();
                productDto.setId(product.getId());
                productDto.setName(product.getName());
                productDto.setPrice(product.getPrice());
                productDto.setCategoryDto(categoryDto);

                productDtos.add(productDto);
            }
            CategoryDtoWithProducts cdtoP = new CategoryDtoWithProducts();
            cdtoP.setId(category.getId());
            cdtoP.setName(category.getName());
            cdtoP.setProducts(productDtos);

            return new ResponseEntity<>(cdtoP,HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @PostMapping
    public ResponseEntity<CategoryDto> postCategory(@RequestBody Category newCategory){
        categoryRepository.save(newCategory);
        CategoryDto categoryDto = convertToDto(newCategory);
         return new ResponseEntity<>(categoryDto,HttpStatus.CREATED);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<CategoryDto> updateCategory(@PathVariable Long id,@RequestBody Map<String,Object> updates){

        Category existingCategory = categoryRepository.findById(id).orElse(null);
        if(existingCategory != null) {
            if (updates.containsKey("name") && updates.get("name")!=null && updates.get("name") instanceof String)
                existingCategory.setName((String)updates.get("name"));
            if(updates.containsKey("products") && updates.get("products")!=null && updates.get("products") instanceof List<?>)
                existingCategory.setProducts((List<Product>) updates.get("products"));


           categoryRepository.save(existingCategory);
            CategoryDto categoryDto = convertToDto(existingCategory);
            return new ResponseEntity<>(categoryDto,HttpStatus.OK);
        }
      return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCategory(@PathVariable Long id){
        if(categoryRepository.existsById(id)){
            categoryRepository.deleteById(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

}

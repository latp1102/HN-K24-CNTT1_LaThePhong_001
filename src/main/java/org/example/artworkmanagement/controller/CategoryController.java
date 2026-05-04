package org.example.artworkmanagement.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.artworkmanagement.model.Category;
import org.example.artworkmanagement.repository.CategoryRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
@RequestMapping("/categories")
public class CategoryController {
    private final CategoryRepository categoryRepository;
    
    @GetMapping
    public String listCategories(Model model) {
        model.addAttribute("categories", categoryRepository.findAll());
        return "category-list";
    }

    @GetMapping("/add")
    public String showAddForm(Model model) {
        model.addAttribute("category", new Category());
        return "category-form";
    }

    @PostMapping("/add")
    public String addCategory(@Valid @ModelAttribute Category category, BindingResult result, Model model, RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            return "category-form";
        }
        if (categoryRepository.existsByName(category.getName())) {
            model.addAttribute("errorMessage", "Tên danh mục đã tồn tại");
            return "category-form";
        }
        categoryRepository.save(category);
        redirectAttributes.addFlashAttribute("successMessage", "Thêm danh mục thành công!");
        return "redirect:/categories";
    }
    
    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        return categoryRepository.findById(id).map(category -> {
            model.addAttribute("category", category);
            return "category-form";
        }).orElseGet(() -> {
            redirectAttributes.addFlashAttribute("errorMessage", "Không tìm thấy danh mục");
            return "redirect:/categories";
        });
    }

    @PostMapping("/edit/{id}")
    public String updateCategory(@PathVariable Long id, @Valid @ModelAttribute Category category, BindingResult result, Model model, RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            return "category-form";
        }
        return categoryRepository.findById(id).map(existingCategory -> {
            if (!existingCategory.getName().equals(category.getName()) && categoryRepository.existsByName(category.getName())) {
                model.addAttribute("errorMessage", "Tên danh mục đã tồn tại");
                return "category-form";
            }
            existingCategory.setName(category.getName());
            existingCategory.setDescription(category.getDescription());
            categoryRepository.save(existingCategory);
            redirectAttributes.addFlashAttribute("successMessage", "Cập nhật danh mục thành công!");
            return "redirect:/categories";
        }).orElseGet(() -> {
            redirectAttributes.addFlashAttribute("errorMessage", "Không tìm thấy danh mục");
            return "redirect:/categories";
        });
    }
    
    @GetMapping("/delete/{id}")
    public String deleteCategory(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        return categoryRepository.findById(id).map(category -> {
            categoryRepository.delete(category);
            redirectAttributes.addFlashAttribute("successMessage", "Xóa danh mục thành công!");
            return "redirect:/categories";
        }).orElseGet(() -> {
            redirectAttributes.addFlashAttribute("errorMessage", "Không tìm thấy danh mục");
            return "redirect:/categories";
        });
    }
}
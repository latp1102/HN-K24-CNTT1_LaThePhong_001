package org.example.artworkmanagement.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.artworkmanagement.model.Artwork;
import org.example.artworkmanagement.repository.ArtworkRepository;
import org.example.artworkmanagement.repository.CategoryRepository;
import org.example.artworkmanagement.service.FileUploadService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;

@Controller
@RequiredArgsConstructor
@RequestMapping("/artworks")
public class ArtworkController {
    
    private final ArtworkRepository artworkRepository;
    private final CategoryRepository categoryRepository;
    private final FileUploadService fileUploadService;
    @GetMapping
    public String listArtworks(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "5") int size, Model model) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Artwork> artworkPage = artworkRepository.findAll(pageable);
        
        model.addAttribute("artworkPage", artworkPage);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", artworkPage.getTotalPages());
        model.addAttribute("totalItems", artworkPage.getTotalElements());
        
        return "artwork-list";
    }

    @GetMapping("/add")
    public String showAddForm(Model model) {
        model.addAttribute("artwork", new Artwork());
        model.addAttribute("categories", categoryRepository.findAll());
        return "artwork-form";
    }
    @PostMapping("/add")
    public String addArtwork(@Valid @ModelAttribute Artwork artwork, 
                            BindingResult result, 
                            Model model,
                            @RequestParam("file") MultipartFile file,
                            RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            model.addAttribute("categories", categoryRepository.findAll());
            return "artwork-form";
        }
        
        try {
            if (!file.isEmpty()) {
                String filename = fileUploadService.uploadFile(file);
                artwork.setCoverImage(filename);
            }
            
            artworkRepository.save(artwork);
            redirectAttributes.addFlashAttribute("successMessage", "Thêm tác phẩm thành công!");
            return "redirect:/artworks";
        } catch (IOException e) {
            model.addAttribute("categories", categoryRepository.findAll());
            model.addAttribute("errorMessage", "Lỗi khi upload file: " + e.getMessage());
            return "artwork-form";
        }
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        return artworkRepository.findById(id).map(artwork -> {
            model.addAttribute("artwork", artwork);
            model.addAttribute("categories", categoryRepository.findAll());
            return "artwork-form";
        }).orElseGet(() -> {
            redirectAttributes.addFlashAttribute("errorMessage", "Artwork not found");
            return "redirect:/artworks";
        });
    }

    @PostMapping("/edit/{id}")
    public String updateArtwork(@PathVariable Long id, @Valid @ModelAttribute Artwork artwork, BindingResult result, Model model, @RequestParam("file") MultipartFile file, RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            model.addAttribute("categories", categoryRepository.findAll());
            return "artwork-form";
        }
        return artworkRepository.findById(id).map(existingArtwork -> {
            try {
                if (!file.isEmpty()) {
                    if (existingArtwork.getCoverImage() != null) {
                        fileUploadService.deleteFile(existingArtwork.getCoverImage());
                    }
                    String filename = fileUploadService.uploadFile(file);
                    existingArtwork.setCoverImage(filename);
                }
                existingArtwork.setTitle(artwork.getTitle());
                existingArtwork.setArtist(artwork.getArtist());
                existingArtwork.setPrice(artwork.getPrice());
                existingArtwork.setReleaseDate(artwork.getReleaseDate());
                existingArtwork.setCategory(artwork.getCategory());
                existingArtwork.setStatus(artwork.getStatus());
                artworkRepository.save(existingArtwork);
                redirectAttributes.addFlashAttribute("successMessage", "Cập nhật tác phẩm thành công!");
                return "redirect:/artworks";
            } catch (IOException e) {
                model.addAttribute("categories", categoryRepository.findAll());
                model.addAttribute("errorMessage", "Lỗi khi upload file: " + e.getMessage());
                return "artwork-form";
            }
        }).orElseGet(() -> {
            redirectAttributes.addFlashAttribute("errorMessage", "Không tìm thấy tác phẩm");
            return "redirect:/artworks";
        });
    }

    @GetMapping("/delete/{id}")
    public String deleteArtwork(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        return artworkRepository.findById(id).map(artwork -> {
            try {
                if (artwork.getCoverImage() != null) {
                    fileUploadService.deleteFile(artwork.getCoverImage());
                }
                artworkRepository.delete(artwork);
                redirectAttributes.addFlashAttribute("successMessage", "Xóa tác phẩm thành công!");
                return "redirect:/artworks";
            } catch (IOException e) {
                redirectAttributes.addFlashAttribute("errorMessage", "Lỗi khi xóa file: " + e.getMessage());
                return "redirect:/artworks";
            }
        }).orElseGet(() -> {
            redirectAttributes.addFlashAttribute("errorMessage", "Không tìm thấy tác phẩm");
            return "redirect:/artworks";
        });
    }
}

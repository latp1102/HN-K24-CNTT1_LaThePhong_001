package org.example.artworkmanagement.config;

import org.example.artworkmanagement.model.Artwork;
import org.example.artworkmanagement.model.Category;
import org.example.artworkmanagement.repository.ArtworkRepository;
import org.example.artworkmanagement.repository.CategoryRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
public class Data implements CommandLineRunner {
    private final CategoryRepository categoryRepository;
    private final ArtworkRepository artworkRepository;
    public Data(CategoryRepository categoryRepository, ArtworkRepository artworkRepository) {
        this.categoryRepository = categoryRepository;
        this.artworkRepository = artworkRepository;
    }
    @Override
    public void run(String... args) throws Exception {
        if(categoryRepository.count()==0){
            seedCategories();
            seedArtworks();
        }
    }
    private void seedCategories() {
        Category painting = new Category();
        painting.setName("Tranh vẽ");
        painting.setDescription("Tranh truyền thống và hiện đại sử dụng các kỹ thuật khác nhau");
        categoryRepository.save(painting);

        Category sculpture = new Category();
        sculpture.setName("Điêu khắc");
        sculpture.setDescription("Tác phẩm nghệ thuật ba chiều được tạo ra bằng cách định hình vật liệu");
        categoryRepository.save(sculpture);

        Category photography = new Category();
        photography.setName("Chụp ảnh");
        photography.setDescription("Nhiếp ảnh nghệ thuật ghi lại những khoảnh khắc và cảnh vật");
        categoryRepository.save(photography);
    }

    private void seedArtworks() {
        Category painting = categoryRepository.findByName("Tranh vẽ").orElse(null);
        Category sculpture = categoryRepository.findByName("Điêu khắc").orElse(null);
        Category photography = categoryRepository.findByName("Chụp ảnh").orElse(null);

        if (painting != null) {
            artworkRepository.save(new Artwork("Đêm đầy sao", "Vincent van Gogh", 1000000.0,
                    LocalDate.of(1889, 6, 1), painting, true));
        }
        if (sculpture != null) {
            artworkRepository.save(new Artwork("David", "Michelangelo", 420000000.0,
                    LocalDate.of(1504, 1, 1), sculpture, true));
        }
        if (photography != null) {
            artworkRepository.save(new Artwork("Cô gái Afghanistan", "Steve McCurry", 25000000.0,
                    LocalDate.of(1984, 12, 1), photography, true));

        }
    }
}

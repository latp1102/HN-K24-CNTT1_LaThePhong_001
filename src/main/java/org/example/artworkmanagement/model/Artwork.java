package org.example.artworkmanagement.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
@Entity
@Table(name = "artworks")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Artwork {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotBlank(message = "tiêu đề không để trống")
    @Size(min = 5, max = 150, message = "tiêu đề từ 5 - 150 ký tự")
    private String title;
    @NotBlank(message = "tác giả không để trống")
    @Column(name = "artist")
    private String artist;
    @DecimalMin(value = "0.0", message = "giá không hợp lệ")
    @Column(name = "price")
    private Double price;
    @PastOrPresent(message = "ngày không hợp lệ")
    @Column(name = "release_date")
    private LocalDate releaseDate;
    @Column(name = "cover_image")
    private String coverImage;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;
    @Column(name = "status")
    private Boolean status;
    public Artwork(String title, String artist, Double price, LocalDate releaseDate, Category category, Boolean status) {
        this.title = title;
        this.artist = artist;
        this.price = price;
        this.releaseDate = releaseDate;
        this.category = category;
        this.status = status;
    }
}

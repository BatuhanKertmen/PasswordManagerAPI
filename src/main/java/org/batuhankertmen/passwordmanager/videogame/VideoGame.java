package org.batuhankertmen.passwordmanager.videogame;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.util.List;

@Entity
@Table(name = "video_game")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class VideoGame {

    @Id
    @Column(name = "title", length = 64, nullable = false)
    private String title;

    @Column(name = "description", length = 1024)
    private String description;

    @Column(name = "image_path", length = 256)
    private String imagePath;

    private float rating;

    private boolean deleted;

    @OneToMany(mappedBy = "videoGame", fetch = FetchType.LAZY)
    private List<VideoGameComment> comments;
}


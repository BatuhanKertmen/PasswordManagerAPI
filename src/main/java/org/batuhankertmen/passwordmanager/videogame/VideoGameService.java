package org.batuhankertmen.passwordmanager.videogame;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

@RequiredArgsConstructor
public class VideoGameService {
    private final VideoGameRepository videoGameRepository;

    Page<VideoGame> findPaginated(Specification<VideoGame> spec, Pageable page) {
        return videoGameRepository.findAllByDeletedFalse(spec, page);
    }

    VideoGame findByTitle(String title) {
        return videoGameRepository.findByTitleAndDeletedFalse(title).orElseThrow(
                new ResourceNotFou
        )
    }
}

package org.batuhankertmen.passwordmanager.videogame;

import org.springframework.data.domain.Page;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface VideoGameRepository extends JpaRepository<VideoGame, String> {
    Optional<VideoGame> findByTitleAndDeletedFalse(String title);

    Page<VideoGame> findAllByDeletedFalse(Specification<VideoGame> spec,  Pageable pageable);
}

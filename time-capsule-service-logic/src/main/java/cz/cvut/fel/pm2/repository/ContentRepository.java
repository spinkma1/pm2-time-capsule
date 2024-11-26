package cz.cvut.fel.pm2.repository;

import cz.cvut.fel.pm2.persistence.Content;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ContentRepository extends JpaRepository<Content, Long> {
}

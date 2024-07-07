package com.cursoalura.literalura.repository;

import com.cursoalura.literalura.entity.Libro;
import com.cursoalura.literalura.model.Lenguaje;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface LibroRepository extends JpaRepository<Libro, Long> {
    Optional<Libro> findByTituloContainsIgnoreCase(String titulo);

    List<Libro> findByLenguaje(Lenguaje lenguaje);

    @Query("SELECT l FROM Libro l ORDER BY l.descarga DESC LIMIT 10")
    List<Libro> top10Libros();
}

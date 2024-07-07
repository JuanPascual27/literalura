package com.cursoalura.literalura.repository;

import com.cursoalura.literalura.entity.Autor;
import com.cursoalura.literalura.entity.Libro;
import com.cursoalura.literalura.model.Lenguaje;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface AutorRepository extends JpaRepository<Autor, Long> {
    Optional<Autor> findByNombreContainsIgnoreCase(String nombre);

    List<Autor> findByNacimientoLessThanEqualAndFallecimientoIsGreaterThanEqual(Integer nacimiento,
                                                                                Integer fallecimiento);

    List<Autor> findByNacimientoEquals(Integer fecha);

    List<Autor> findByFallecimientoEquals(Integer fecha);
}

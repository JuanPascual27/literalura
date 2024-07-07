package com.cursoalura.literalura.entity;

import com.cursoalura.literalura.model.DatosLibro;
import com.cursoalura.literalura.model.Lenguaje;
import jakarta.persistence.*;

@Entity
@Table(name = "libros")
public class Libro {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique = true)
    private String titulo;
    @Enumerated(EnumType.STRING)
    private Lenguaje lenguaje;
    private String copyright;
    private Integer descarga;
    @ManyToOne
    private Autor autor;

    public Libro() {}

    public Libro(DatosLibro libro) {
        this.titulo = libro.titulo();
        this.lenguaje = Lenguaje.fromString(libro.lenguajes().stream()
                .findFirst()
                .orElse(""));
        this.copyright = libro.copyright();
        this.descarga = libro.descarga();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public Lenguaje getLenguaje() {
        return lenguaje;
    }

    public void setLenguaje(Lenguaje lenguaje) {
        this.lenguaje = lenguaje;
    }

    public String getCopyright() {
        return copyright;
    }

    public void setCopyright(String copyright) {
        this.copyright = copyright;
    }

    public Integer getDescarga() {
        return descarga;
    }

    public void setDescarga(Integer descarga) {
        this.descarga = descarga;
    }

    public Autor getAutor() {
        return autor;
    }

    public void setAutor(Autor autor) {
        this.autor = autor;
    }

    @Override
    public String toString() {
        return "id=" + id +
                "\ntitulo='" + titulo + '\'' +
                "\nautor=" + autor.getNombre() +
                "\nlenguaje=" + lenguaje +
                "\ncopyright='" + copyright + '\'' +
                "\ndescarga=" + descarga +
                "\n               ************";
    }
}

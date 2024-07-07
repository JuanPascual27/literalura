package com.cursoalura.literalura.main;

import com.cursoalura.literalura.entity.Autor;
import com.cursoalura.literalura.entity.Libro;
import com.cursoalura.literalura.model.Datos;
import com.cursoalura.literalura.model.DatosLibro;
import com.cursoalura.literalura.model.Lenguaje;
import com.cursoalura.literalura.repository.AutorRepository;
import com.cursoalura.literalura.repository.LibroRepository;
import com.cursoalura.literalura.service.ConsumoAPI;
import com.cursoalura.literalura.service.ConvierteDatos;

import java.util.*;
import java.util.stream.Collectors;

public class Main {
    private final String URL_BASE = "https://gutendex.com/books/";
    private Scanner teclado = new Scanner(System.in);
    private ConsumoAPI consumoAPI = new ConsumoAPI();
    private ConvierteDatos conversor = new ConvierteDatos();

    private AutorRepository autorRepository;
    private LibroRepository libroRepository;

    public Main(AutorRepository a, LibroRepository l){
        this.autorRepository = a;
        this.libroRepository = l;
    }

    public void mostrarMenu() {
        int opcion = -1;
        String menu = """
                **********************************************
                                  LITERALURA
                **********************************************
                1) Buscar libro por titulo
                2) Listar libros registrados
                3) Listar autores registrados
                4) Listar autores vivos en un determinado año
                5) Listar libros por idioma
                6) Generar estadisticas
                7) Top 10 libros
                8) Buscar autor por nombre
                9) Listar autores con otras consultas
                0) Salir
                Elija la opcion a realizar:""";

        while (opcion != 0) {
            System.out.println(menu);
            try {
                opcion = Integer.parseInt(teclado.nextLine());
                switch (opcion) {
                    case 1:
                        buscarLibroPorTitulo();
                        break;
                    case 2:
                        listarLibrosRegistrados();
                        break;
                    case 3:
                        listarAutoresRegistrados();
                        break;
                    case 4:
                        listarAutoresVivos();
                        break;
                    case 5:
                        listarLibrosPorIdioma();
                        break;
                    case 6:
                        generarEstadisticas();
                        break;
                    case 7:
                        top10Libros();
                        break;
                    case 8:
                        buscarAutorPorNombre();
                        break;
                    case 9:
                        listarAutoresConOtrasConsultas();
                        break;
                    case 0:
                        System.out.println("Gracias por usar Literalura");
                        System.out.println("Cerrando la aplicacion...");
                        break;
                    default:
                        System.out.println("¡Opción no valida!");
                        break;
                }
            } catch (NumberFormatException e) {
                System.out.println("Opcion no valida: " + e.getMessage());
            }
        }
    }

    public void buscarLibroPorTitulo() {
        System.out.print("""
                            **********************************************
                                       BUSCAR LIBROS POR TITULO
                            **********************************************
                            """);
        System.out.println("Introduzca el nombre del libro que desea buscar:");
        String nombre = teclado.nextLine();
        String json = consumoAPI.obtenerDatos(URL_BASE + "?search=" + nombre.replace(" ","%20"));
        var datos = conversor.obtenerDatos(json, Datos.class);
        Optional<DatosLibro> libroAPI = datos.libros().stream()
                .filter(l -> l.titulo()
                        .toUpperCase()
                        .contains(nombre.toUpperCase()))
                .findFirst();
        if (libroAPI.isPresent()) {
            Autor autor = new Autor(libroAPI.get().autores().get(0));
            Libro libro = new Libro(libroAPI.get());
            try {
                Optional<Libro> libroDB = libroRepository.findByTituloContainsIgnoreCase(libro.getTitulo());
                if (libroDB.isPresent()) {
                    System.out.println("El libro ya está guardado en la base de datos.");
                    System.out.println(libroDB.get());
                } else {
                    Optional<Autor> autorDB = autorRepository.findByNombreContainsIgnoreCase(autor.getNombre());
                    if (autorDB.isPresent()) {
                        autor = autorDB.get();
                        autor.setLibro(libro);
                        System.out.println("El autor ya esta guardado en la BD!");
                    } else {
                        autor.setLibros(Collections.singletonList(libro));
                    }
                    autorRepository.save(autor);
                    System.out.println(libro);
                }
            } catch(Exception e) {
                System.out.println("Advertencia! " + e.getMessage());
            }
        } else
            System.out.println("Libro no encontrado!");
    }

    public void listarLibrosRegistrados() {
        System.out.println("""
                            **********************************************
                                           LIBROS REGISTRADOS
                            **********************************************
                            """);
        List<Libro> libros = libroRepository.findAll();
        libros.forEach(System.out::println);
    }

    public void listarAutoresRegistrados() {
        System.out.print("""
                            **********************************************
                                          AUTORES REGISTRADOS
                            **********************************************
                            """);
        List<Autor> autores = autorRepository.findAll();
        autores.forEach(System.out::println);
    }

    public void listarAutoresVivos() {
        System.out.println("""
                            **********************************************
                                         LISTAR AUTORES VIVOS
                            **********************************************
                            """);
        System.out.println("Introduzca el año que desea buscar:");
        try {
            var fecha = Integer.valueOf(teclado.nextLine());
            List<Autor> autores = autorRepository.findByNacimientoLessThanEqualAndFallecimientoIsGreaterThanEqual(
                    fecha,
                    fecha
            );
            if (!autores.isEmpty()) {
                autores.forEach(a -> System.out.println(
                        "Autor: " + a.getNombre() +
                                "\nFecha de nacimiento: " + a.getNacimiento() +
                                "\nFecha de fallecimiento: " + a.getFallecimiento()
                ));
            } else
                System.out.println("No hay autores vivos en ese año registradoe en la BD!");
        } catch (NumberFormatException e) {
            System.out.println("introduce un año valido " + e.getMessage());
        }
    }

    public void listarLibrosPorIdioma() {
        System.out.println("""
                            **********************************************
                                      LISTAR LIBROS POR IDIOMA
                            **********************************************
                            Ingrese el idioma para buscar libros:
                            es - español
                            en - inglés
                            fr - francés
                            pt - portugués
                            """);
        String idioma = teclado.nextLine();
        if (idioma.equalsIgnoreCase("es") ||
                idioma.equalsIgnoreCase("en") ||
                idioma.equalsIgnoreCase("fr") ||
                idioma.equalsIgnoreCase("pt")) {
            Lenguaje lenguaje = Lenguaje.fromString(idioma);
            List<Libro> libros = libroRepository.findByLenguaje(lenguaje);
            if (libros.isEmpty())
                System.out.println("No hay libros registrados en ese idioma!");
            else
                libros.forEach(System.out::println);
        } else
            System.out.println("Introduce un idioma en el formato valido");
    }

    public void generarEstadisticas() {
        System.out.println("""
                            **********************************************
                                       ESTADISTICAS DE DESCARGAS
                            **********************************************
                            """);
        var json = consumoAPI.obtenerDatos(URL_BASE);
        var datos = conversor.obtenerDatos(json, Datos.class);
        IntSummaryStatistics est = datos.libros().stream()
                .filter(l -> l.descarga() > 0)
                .collect(Collectors.summarizingInt(DatosLibro::descarga));
        System.out.println("Cantidad media de descargas: " + est.getAverage());
        System.out.println("Cantidad maxima de descargas: " + est.getMax());
        System.out.println("Cantidad minima de descargas: " + est.getMin());
        System.out.println("Cantidad de registros evaluados para calcular las estadisticas: " +
                est.getCount());
    }

    public void top10Libros() {
        System.out.println("""
                            **********************************************
                                            TOP 10 LIBROS
                            **********************************************
                            """);
        List<Libro> libros = libroRepository.top10Libros();
        libros.forEach(System.out::println);
    }

    public void buscarAutorPorNombre() {
        System.out.println("""
                            **********************************************
                                       BUSCAR AUTOR POR NOMBRE
                            **********************************************
                            """);
        System.out.println("Ingrese el nombre del autor que deseas buscar:");
        String nombre = teclado.nextLine();
        Optional<Autor> autor = autorRepository.findByNombreContainsIgnoreCase(nombre);
        if (autor.isPresent())
            System.out.println(autor.get());
        else
            System.out.println("El autor no existe en la BD!");
    }

    public void listarAutoresConOtrasConsultas() {
        System.out.println("""
                            **********************************************
                                        LISTAR AUTORES POR AÑO
                            **********************************************
                            1 - Listar autor por año de nacimiento
                            2 - Listar autor por año de fallecimiento
                            Ingrese la opcion por la cual desea listar los autores:
                            """);
        try {
            var opcion = Integer.valueOf(teclado.nextLine());
            switch (opcion) {
                case 1:
                    ListarAutoresPorNacimiento();
                    break;
                case 2:
                    ListarAutoresPorFallecimiento();
                    break;
                default:
                    System.out.println("Opcion invalida!");
                    break;
            }
        } catch (NumberFormatException e) {
            System.out.println("Opcion no valida: " + e.getMessage());
        }
    }

    public void ListarAutoresPorNacimiento() {
        System.out.println("""
                            **********************************************
                                    BUSCAR AUTOR POR NACIMIENTO
                            **********************************************
                            """);
        System.out.println("Introduce el año de nacimiento que deseas buscar:");
        try {
            var fecha = Integer.valueOf(teclado.nextLine());
            List<Autor> autores = autorRepository.findByNacimientoEquals(fecha);
            if (autores.isEmpty())
                System.out.println("No existen autores con año de nacimeinto igual a " + fecha);
            else
                autores.forEach(System.out::println);
        } catch (NumberFormatException e){
            System.out.println("Año no valido: " + e.getMessage());
        }
    }

    public void ListarAutoresPorFallecimiento() {
        System.out.println("""
                            **********************************************
                                    BUSCAR AUTOR POR FALLECIMIENTO
                            **********************************************
                            """);
        System.out.println("Introduce el año de fallecimiento que deseas buscar:");
        try {
            var fallecimiento = Integer.valueOf(teclado.nextLine());
            List<Autor> autores = autorRepository.findByFallecimientoEquals(fallecimiento);
            if (autores.isEmpty())
                System.out.println("No existen autores con año de fallecimiento igual a " + fallecimiento);
            else
                autores.forEach(System.out::println);
        } catch (NumberFormatException e) {
            System.out.println("Opcion no valida: " + e.getMessage());
        }
    }
}

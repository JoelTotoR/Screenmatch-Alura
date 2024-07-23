package com.aluracursos.screenmatch.Principal;

import com.aluracursos.screenmatch.models.*;
import com.aluracursos.screenmatch.repository.SerieRepository;
import com.aluracursos.screenmatch.service.ConsumoAPI;
import com.aluracursos.screenmatch.service.ConvierteDatos;

import java.util.*;
import java.util.stream.Collectors;

public class Principal {
    private Scanner input = new Scanner(System.in);
    private ConsumoAPI consumoApi = new ConsumoAPI();
    private ConvierteDatos convertir = new ConvierteDatos();
    private List <DatosSerie> datosSeries = new ArrayList<>();
    private List <Serie> series;
    private final String API_DIRECCION = "https://www.omdbapi.com/?t=";
    private final String API_KEY = "&apikey=add33e45";
    private SerieRepository repo;
    private Optional<Serie> serieBuscada;

    public Principal(SerieRepository repository) {
        this.repo = repository;
    }

    public void mostrarMenu() {
        var opcion = -1;
        while (opcion != 0) {
            var menu = """
                    1. Buscar Serie
                    2. Buscar episodio
                    3. Mostrar series buscadas
                    4. Buscar serie
                    5. Top 5 series
                    6. Buscar serie por categoria
                    7. Buscar series por temporadas y evaluación
                    8. Buscar episodio por titulo
                    9. Top 5 episodios
                    0. Salir
                    """;
            System.out.println(menu);
            opcion = input.nextInt();
            input.nextLine();

            switch (opcion) {
                case 1:
                    buscarSerieWeb();
                    break;
                case 2:
                    buscarEpisodioPorSerie();
                    break;
                case 3:
                    mostrarSeriesBuscadas();
                    break;
                case 4:
                    buscarSeriePorTitulo();
                    break;
                case 5:
                    buscarTop5Series();
                    break;
                case 6:
                    buscarSeriePorCategoria();
                    break;
                case 7:
                    buscarSeriesDe3Temporadas();
                    break;
                case 8:
                    buscarEpisodioPorParteDelTitulo();
                case 9:
                    buscarTop5Episodios();
                    break;
                case 0:
                    System.out.println("Cerrando la aplicación...");
                    break;
                default:
                    System.out.println("Eliga una opcion valida");
            }
        }
    }

    private DatosSerie getDatosSerie(){
        System.out.println("Esribe el nombre de la serie:");
        String nombre = input.nextLine().replace(" ","+");
        var json = consumoApi.obtenerDatos(API_DIRECCION + nombre + API_KEY);
        DatosSerie datos = convertir.obtenerDatos(json, DatosSerie.class);
        return datos;
    }

    private void buscarSerieWeb(){
        DatosSerie datos = getDatosSerie();
        Serie serie = new Serie(datos);
        repo.save(serie);
        System.out.println(datos);
    }

    private void buscarEpisodioPorSerie(){
        mostrarSeriesBuscadas();
        System.out.println("Escribe el nombre de la serie para conocer sus episodios:");
        String nombreSerie = input.nextLine();

        Optional<Serie> serie = series.stream()
                .filter(e -> e.getTitulo().toLowerCase().contains(nombreSerie.toLowerCase()))
                .findFirst();

        if (serie.isPresent()){
            var serieEncontrada = serie.get();
            List<DatosTemporadas> temporadas = new ArrayList<>();

            for (int i = 1; i <= serieEncontrada.getTotalTemporadas(); i++) {
                var json = consumoApi.obtenerDatos(API_DIRECCION + serieEncontrada.getTitulo().replace(" ","+") + "&Season=" + i + API_KEY);
                DatosTemporadas datosTemporada = convertir.obtenerDatos(json, DatosTemporadas.class);
                temporadas.add(datosTemporada);
            }
            temporadas.forEach(System.out::println);

            List<Episodio> episodios = temporadas.stream()
                    .flatMap(d -> d.episodios().stream()
                            .map(e -> new Episodio(d.numeroTemporada(), e)))
                    .collect(Collectors.toList());
            serieEncontrada.setEpisodios(episodios);
            repo.save(serieEncontrada);
        }
    }

    private void mostrarSeriesBuscadas(){
        series = repo.findAll();

        series.stream()
                .sorted(Comparator.comparing(Serie::getGenero).reversed())
                .forEach(System.out::println);
    }

    private void buscarSeriePorTitulo() {
        System.out.println("Ingrese el nombre de la serie: ");
        String busqueda = input.nextLine();
        serieBuscada = repo.findByTituloContainsIgnoreCase(busqueda);
        if (serieBuscada.isPresent()){
            System.out.println("La serie es: " + serieBuscada.get());
        } else {
            System.out.println("Serie no encontrada");
        }
    }

    private void buscarTop5Series(){
        System.out.println("Las 5 mejores seres son:");
        List<Serie> topSeries = repo.findTop5ByOrderByEvaluacionDesc();
        topSeries.forEach(e -> System.out.println("Serie: " + e.getTitulo() + "-- Evaluación: " + e.getEvaluacion()));
    }

    private void buscarSeriePorCategoria(){
        System.out.println("Escribe el genero/categoria de la serie:");
        var genero = input.nextLine();
        List<Serie> seriesEncontradas = repo.findByGenero(Categoria.fromUsuario(genero));
        System.out.println("Las series de " + Categoria.fromUsuario(genero) + " son:");
        seriesEncontradas.forEach(s -> System.out.println("Titulo: " + s.getTitulo()));
    }

    private void buscarSeriesDe3Temporadas(){
        System.out.println("Ingrese el numero maximo de temporadas:");
        int numeroTemporadas = input.nextInt();
        System.out.println("Ingrese el la calificacion minima que debe tener la serie:");
        double evaluacionMinima = input.nextDouble();
        System.out.println("Las series que cumplen esas caracteristicas son:");
        List<Serie> seriesCortas = repo.seriesPorTemporadaYEvaluacion(numeroTemporadas, evaluacionMinima);
        seriesCortas.forEach(s -> System.out.println(s.getTitulo() + " con " +
                s.getTotalTemporadas() + " Temporadas y una califiación de: " + s.getEvaluacion()));
    }

    private void buscarEpisodioPorParteDelTitulo(){
        System.out.println("Ingrese parte del titulo del episodio");
        String parteTitlo = input.nextLine();
        List<Episodio> episodiosEncontrados = repo.episodiosPorTitulo(parteTitlo);
        episodiosEncontrados.forEach(e -> System.out.printf("Serie: %s, Temportada: %s, Episodio: %s, Evaluación: %s\n",
                e.getSerie().getTitulo(), e.getTemporada(), e.getNumeroDeEpisodio(), e.getEvaluacion()));
    }

    private void buscarTop5Episodios(){
        buscarSeriePorTitulo();
        if (serieBuscada.isPresent()){
            Serie serie = serieBuscada.get();
            List<Episodio> top5Episodios = repo.top5Episodios(serie);
            top5Episodios.forEach(e -> System.out.printf("Serie: %s, Temportada: %s, Episodio: %s, Evaluación: %s\n",
                    e.getTitulo(), e.getTemporada(), e.getNumeroDeEpisodio(), e.getEvaluacion()));
        }
    }

//        // Buscar informacion de todas las temporadas
//        List<DatosTemporadas> temporadas = new ArrayList<>();
//        for (int i = 1; i <= datos.totalTemporadas(); i++) {
//            json = consumoApi.obtenerDatos(API_DIRECCION + nombre + "&Season="+ i + API_KEY);
//            var datosTemporada = convertir.obtenerDatos(json, DatosTemporadas.class);
//            temporadas.add(datosTemporada);
//        }
//        temporadas.forEach(t -> t.episodios().forEach(e -> System.out.println(e.titulo())));

//        // Convertir la informacion al tipo DatosEpisodio
//        List<DatosEpisodio> datosEpisodios = temporadas.stream()
//                .flatMap(t -> t.episodios().stream())
//                .collect(Collectors.toList());

        // top 5 episodios
//        datosEpisodios.stream()
//                .filter(e -> !e.evaluacion().equalsIgnoreCase("N/A"))
//                .sorted(Comparator.comparing(DatosEpisodio::evaluacion).reversed())
//                .limit(5)
//                .forEach(System.out::println);

//        // Convirtiendo datos a la clase Episodio
//        List<Episodio> episodios = temporadas.stream()
//                .flatMap(t -> t.episodios().stream()
//                        .map(d -> new Episodio(t.numeroTemporada(), d)))
//                .collect(Collectors.toList());
//
//        episodios.forEach(System.out::println);

        // Filtrar por una fecha dada
//        System.out.println("Ingrese al año apartir del que desea filtrar los episodios");
//        var anio = input.nextInt();
//        var fechaFiltro = LocalDate.of(anio, 1,1);
//        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy");
//
//        episodios.stream()
//                .filter(e -> e.getFechaDeLanzamiento() != null && e.getFechaDeLanzamiento().isAfter(fechaFiltro))
//                .forEach(e -> System.out.println("Temporada: " + e.getTemporada() +
//                        ", Episodio: " + e.getNumeroDeEpisodio() +
//                        ", Fecha de lanzamiento: " + e.getFechaDeLanzamiento().format(dtf)));

//        // Filtrar episodios por titulo
//        System.out.println("Ingrese el nombre del episodio;");
//        var busquedaNombre = input.nextLine();
//        Optional<Episodio> resultadoBusqueda = episodios.stream()
//                .filter(e -> e.getTitulo().toUpperCase().contains(busquedaNombre.toUpperCase()))
//                .findFirst();
//
//        if(resultadoBusqueda.isPresent()){
//            System.out.println(resultadoBusqueda.get());
//        } else {
//            System.out.println("No se encontro un episodio con ese nombre");
//        }
//        // Obtener el promedio por temporada
//        Map<Integer, Double> evaluacionPorTemporada = episodios.stream()
//                .filter(e -> e.getEvaluacion() > 0.0)
//                .collect(Collectors.groupingBy(Episodio::getTemporada,
//                        Collectors.averagingDouble(Episodio::getEvaluacion)));
//        System.out.println(evaluacionPorTemporada);
//
//        DoubleSummaryStatistics est = episodios.stream()
//                .filter(e -> e.getEvaluacion() > 0.0)
//                .collect(Collectors.summarizingDouble(Episodio::getEvaluacion));
//        System.out.println("Estadisticas obtenidas:");
//        System.out.println(est);

}

package com.aluracursos.screenmatch.repository;

import com.aluracursos.screenmatch.models.Categoria;
import com.aluracursos.screenmatch.models.Episodio;
import com.aluracursos.screenmatch.models.Serie;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface SerieRepository extends JpaRepository<Serie, Long> {
    Optional<Serie> findByTituloContainsIgnoreCase(String nombreSerie);
    List<Serie> findTop5ByOrderByEvaluacionDesc();
    List<Serie> findByGenero(Categoria categoria);
    @Query("SELECT s FROM Serie s WHERE s.evaluacion >= :evaluacion AND s.totalTemporadas <= :numeroDeTemporadas")
    List<Serie> seriesPorTemporadaYEvaluacion(Integer numeroDeTemporadas, Double evaluacion);
    @Query("SELECT e FROM Serie s JOIN s.episodios e WHERE e.titulo ILIKE %:parteDelTitulo%")
    List<Episodio> episodiosPorTitulo(String parteDelTitulo);
    @Query("SELECT e FROM Serie s JOIN s.episodios e WHERE s = :serie Order BY e.evaluacion DESC LIMIT 5")
    List<Episodio> top5Episodios(Serie serie);
    @Query("SELECT s FROM Serie s JOIN s.episodios e GROUP BY s ORDER BY MAX(e.fechaDeLanzamiento) DESC LIMIT 5")
    List<Serie> lanzamientosRecientes();
    @Query("SELECT e FROM Serie s JOIN s.episodios e WHERE s.id = :id AND e.temporada = :numeroDeTemporada")
    List<Episodio> obtenerEpisodiosPorTemporada(Long id, Long numeroDeTemporada);
}

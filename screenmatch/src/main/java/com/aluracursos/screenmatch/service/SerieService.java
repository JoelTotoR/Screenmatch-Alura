package com.aluracursos.screenmatch.service;


import com.aluracursos.screenmatch.controller.EpisodioDTO;
import com.aluracursos.screenmatch.dto.SerieDTO;
import com.aluracursos.screenmatch.models.Categoria;
import com.aluracursos.screenmatch.models.Episodio;
import com.aluracursos.screenmatch.models.Serie;
import com.aluracursos.screenmatch.repository.SerieRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class SerieService {
    @Autowired
    private SerieRepository repo;

    public List<SerieDTO> obtenerTodasLasSeries(){
        return convierteDatos(repo.findAll());
    }

    public List<SerieDTO> obtenerTop5() {
        return convierteDatos(repo.findTop5ByOrderByEvaluacionDesc());
    }

    public List<SerieDTO> ultimos5Lanzamientos(){
        return convierteDatos(repo.lanzamientosRecientes());
    }

    public SerieDTO obtenerSeriePorID(Long id) {
        Optional<Serie> serie = repo.findById(id);
        if (serie.isPresent()){
            Serie s = serie.get();
            return new SerieDTO(s.getId(), s.getTitulo(), s.getTotalTemporadas(), s.getEvaluacion(),
                    s.getPoster(),s.getGenero(),s.getActores(),s.getSinopsis());
        }
        return null;
    }

    public List<SerieDTO> convierteDatos(List<Serie> listaSeries){
        return listaSeries.stream()
                .map(s -> new SerieDTO(s.getId(), s.getTitulo(), s.getTotalTemporadas(), s.getEvaluacion(),
                        s.getPoster(),s.getGenero(),s.getActores(),s.getSinopsis())).collect(Collectors.toList());
    }

    public List<EpisodioDTO> obtenerTodasLasTemporadas(Long id) {
        Optional<Serie> serie = repo.findById(id);
        if (serie.isPresent()){
            Serie s = serie.get();
            return s.getEpisodios().stream()
                    .map(e -> new EpisodioDTO(e.getTemporada(), e.getTitulo(), e.getNumeroDeEpisodio()))
                    .collect(Collectors.toList());
        }
        return null;
    }

    public List<EpisodioDTO> obtenerDatosDeTemporada(Long id, Long numeroDeTemporada) {
        return repo.obtenerEpisodiosPorTemporada(id, numeroDeTemporada).stream().
                map(e -> new EpisodioDTO(e.getTemporada(), e.getTitulo(), e.getNumeroDeEpisodio())).
                collect(Collectors.toList());
    }

    public List<SerieDTO> obtenerSeriesPorCategoria(String genero) {
        return convierteDatos(repo.findByGenero(Categoria.fromUsuario(genero)));
    }
}

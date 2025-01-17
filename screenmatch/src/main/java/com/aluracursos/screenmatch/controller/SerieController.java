package com.aluracursos.screenmatch.controller;

import com.aluracursos.screenmatch.dto.SerieDTO;
import com.aluracursos.screenmatch.service.SerieService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/series")
public class SerieController {
    @Autowired
    private SerieService servicio;

    @GetMapping()
    public List<SerieDTO> obtenerTodasLasSeries() {
        return servicio.obtenerTodasLasSeries();
    }

    @GetMapping("/top5")
    public List<SerieDTO> obtenerTop5(){
        return servicio.obtenerTop5();
    }

    @GetMapping("/lanzamientos")
    public List<SerieDTO> obtenerLanzamientosMasRecientes(){
        return servicio.ultimos5Lanzamientos();
    }

    @GetMapping("/{id}")
    public SerieDTO obtenerSeriePorID(@PathVariable Long id){
        return servicio.obtenerSeriePorID(id);
    }

    @GetMapping("/{id}/temporadas/todas")
    public List<EpisodioDTO> obtenerTemporadas(@PathVariable Long id){
        return servicio.obtenerTodasLasTemporadas(id);
    }

    @GetMapping("/{id}/temporadas/{numeroDeTemporada}")
    public List<EpisodioDTO> obtenerDatosTemporada(@PathVariable Long id, @PathVariable Long numeroDeTemporada){
        return servicio.obtenerDatosDeTemporada(id, numeroDeTemporada);
    }

    @GetMapping("/categoria/{genero}")
    public List<SerieDTO> obtenerSeriesPorCategoria(@PathVariable String genero){
        return servicio.obtenerSeriesPorCategoria(genero);
    }
}

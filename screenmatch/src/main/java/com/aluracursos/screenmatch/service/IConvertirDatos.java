package com.aluracursos.screenmatch.service;

public interface IConvertirDatos {
    <T> T obtenerDatos(String json, Class <T> clase);
}

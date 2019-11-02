package negocio;

import soporte.TSBHashtableDA;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Collection;
import java.util.Scanner;

public class Resultados {
    private Agrupaciones agrupaciones;
    private TSBHashtableDA resultados;

    public Resultados(Agrupaciones agrupaciones, String carpeta) {
        this.agrupaciones = agrupaciones;
        resultados = new TSBHashtableDA();
        resultados.put("00",agrupaciones.generarVacia());
        cargarResultados(carpeta);
    }

    public Resultados(Agrupaciones agrupaciones, Region pais, String carpeta) {
        this.agrupaciones = agrupaciones;
        resultados = new TSBHashtableDA();
        resultados.put(pais.getCodigo(),agrupaciones.generarVacia());
        cargarResultados(carpeta);
    }

    public void cargarResultados(String carpeta) {
        sumarPorAgrupacion(carpeta + "\\mesas_totales_agrp_politica.dsv");
    }

    public void sumarPorAgrupacion(String path) {
        TSBHashtableDA res;
        String linea, campos[], categoria, codAgrupacion;
        int votos;
        Scanner scanner = null;
        try {
            scanner = new Scanner(new File(path));
            while (scanner.hasNextLine()) {
                linea = scanner.nextLine();
                campos = linea.split("\\|");
                categoria = campos[4];
                if (categoria.compareTo("000100000000000") == 0) {
                    codAgrupacion = campos[5];
                    votos = Integer.parseInt(campos[6]);

                    //Pais
                    res = (TSBHashtableDA) resultados.get("00");
                    ((Agrupacion) res.get(codAgrupacion)).sumar(votos);
                    //Distrito
                    ((Agrupacion) getOrPut(campos[0]).get(codAgrupacion)).sumar(votos);
                    //Seccion
                    ((Agrupacion) getOrPut(campos[1]).get(codAgrupacion)).sumar(votos);
                    //Circuito
                    ((Agrupacion) getOrPut(campos[2]).get(codAgrupacion)).sumar(votos);
                    //Mesas
                    ((Agrupacion) getOrPut(campos[3]).get(codAgrupacion)).sumar(votos);
                }
            }
        } catch (FileNotFoundException e) {
            System.out.println("Archivo no encontrado " + e);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public Collection getResultados(String codRegion) {
        return((TSBHashtableDA) (resultados.get(codRegion))).values();
    }

    public TSBHashtableDA getOrPut (String codRegion)
    {
        TSBHashtableDA table = (TSBHashtableDA) resultados.get(codRegion);
        if(table != null)
            return table;
        else
        {
            resultados.put(codRegion,agrupaciones.generarVacia());
            return (TSBHashtableDA) resultados.get(codRegion);
        }
    }
}

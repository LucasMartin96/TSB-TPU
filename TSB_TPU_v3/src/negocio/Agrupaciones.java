package negocio;

import soporte.TSBHashtable;
import soporte.TextFile;

public class Agrupaciones {
    private TextFile descripcionPostulaciones;
    private TSBHashtable votacion;

    public Agrupaciones(String carpeta) {
        descripcionPostulaciones = new TextFile(carpeta + "\\descripcion_postulaciones.dsv");
        votacion = descripcionPostulaciones.identificarAgrupaciones();
    }

    public TSBHashtable generarVacia() {
        TSBHashtable tabla = new TSBHashtable();
        for (Object a : votacion.values()) {
            Agrupacion agrupacion = (Agrupacion) a;
            tabla.put(agrupacion.getCodigo(), new Agrupacion(agrupacion));
        }
        return tabla;
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("Agrupaciones{");
        sb.append("agrupaciones=").append(votacion);
        sb.append('}');
        return sb.toString();
    }

    public TSBHashtable getVotacion()
    {
        return votacion;
    }

}

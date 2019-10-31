package negocio;

import soporte.TSBHashtableDA;
import soporte.TextFile;

public class Agrupaciones {
    private TextFile descripcionPostulaciones;
    private TSBHashtableDA votacion;

    public Agrupaciones(String carpeta) {
        descripcionPostulaciones = new TextFile(carpeta + "\\descripcion_postulaciones.dsv");
        votacion = descripcionPostulaciones.identificarAgrupaciones();
    }

    public TSBHashtableDA generarVacia() {
        TSBHashtableDA tabla = new TSBHashtableDA();
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

    public TSBHashtableDA getVotacion()
    {
        return votacion;
    }

}

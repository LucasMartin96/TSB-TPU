package negocio;

import soporte.TSBHashtable;
import soporte.TextFile;

import java.util.Collection;

public class Region {
    private String codigo;
    private String nombre;
    private TSBHashtable subregiones;


    public Region(String codigo, String nombre) {
        this.codigo = codigo;
        this.nombre = nombre;
        subregiones = new TSBHashtable();
    }

    public Region getOrPut(String codigoSub) {
        Region region = (Region) subregiones.get(codigoSub);
        if (region != null)
            return region;
        else {
            subregiones.put(codigoSub, new Region(codigoSub, ""));
            return (Region) subregiones.get(codigoSub);
        }
    }

    public Collection getSubregiones()
    {
        return subregiones.values();
    }

    public Region getSubregion(String coidgo)
    {
        return (Region) subregiones.get(codigo);
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer(codigo+" - " + nombre);
        return sb.toString();
    }
}

package soporte;

import negocio.Agrupacion;
import negocio.Agrupaciones;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class TextFile {
    private String path;

    public TextFile(String path) {
        this.path = path;
    }


    @Override
    public String toString() {
        return "TextFile{" +
                "path='" + path + '\'' +
                '}';
    }


    public TSBHashtable identificarAgrupaciones() {
        String linea, campos[], categoria, codigo, nombre;
        TSBHashtable table = new TSBHashtable();
        Scanner scanner = null;
        try {
            scanner = new Scanner(new File(path));
            while (scanner.hasNextLine()) {
                linea = scanner.nextLine();
                campos = linea.split("\\|");
                categoria = campos[0];
                if (categoria.compareTo("000100000000000") == 0) {
                    codigo = campos[2];
                    nombre = campos[3];
                    table.put(codigo, new Agrupacion(codigo,nombre,0));
                }
            }
        } catch (FileNotFoundException e) {
            System.out.println("Archivo no encontrado " + e);
        } catch (Exception e) {
            System.out.println(e);
        }
        return table;
    }

}

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package CosmediaSpa;

/**
 *
 * @author vicom
 */
import CosmediaSpa.Interfaces.Inter_GAE;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.io.*;
import java.text.SimpleDateFormat;

/**
 * Clase Tratamiento: gestiona un tratamiento con su CRUD en .txt
 */
public class Tratamiento extends Validacion implements Inter_GAE {

    private String  codigoServicio;
    private String  descripcionServicio;
    private int     cantidadSesiones;
    private double  precioUnitario;
    private double  totalLinea;

    private static final SimpleDateFormat FORMATO_FECHA =
        new SimpleDateFormat("yyyy-MM-dd");
    private static final String PREFIX = "tratamiento_";
    private static final String EXT    = ".txt";

    public Tratamiento() {
        this("", "", 0, 0.0);
    }

    public Tratamiento(String codigoServicio,
                       String descripcionServicio,
                       int cantidadSesiones,
                       double precioUnitario) {
        this.codigoServicio      = codigoServicio;
        this.descripcionServicio = descripcionServicio;
        this.cantidadSesiones    = cantidadSesiones;
        this.precioUnitario      = precioUnitario;
        calcularTotalLinea();
    }

    // ————— Getters y setters originales —————

    public String getCodigoServicio()       { return codigoServicio; }
    public void   setCodigoServicio(String c){ codigoServicio = c;    }

    public String getDescripcionServicio()          { return descripcionServicio; }
    public void   setDescripcionServicio(String d)  { descripcionServicio = d;   }

    public int    getCantidadSesiones()          { return cantidadSesiones; }
    public void   setCantidadSesiones(int qty)  {
        cantidadSesiones = qty;
        calcularTotalLinea();
    }

    public double getPrecioUnitario()          { return precioUnitario; }
    public void   setPrecioUnitario(double p)  {
        precioUnitario = p;
        calcularTotalLinea();
    }

    public double getTotalLinea()              { return totalLinea; }

    /** Recalcula totalLinea = cantidadSesiones * precioUnitario */
    public void calcularTotalLinea() {
        this.totalLinea = this.cantidadSesiones * this.precioUnitario;
    }

    /** Valida que los datos básicos no estén vacíos y sean razonables */
    public boolean validarDatos() {
        if (codigoServicio.trim().isEmpty()
         || descripcionServicio.trim().isEmpty()) {
            JOptionPane.showMessageDialog(
                null,
                "Código y descripción no pueden estar vacíos.",
                "Validación",
                JOptionPane.WARNING_MESSAGE
            );
            return false;
        }
        if (cantidadSesiones < 1) {
            JOptionPane.showMessageDialog(
                null,
                "La cantidad de sesiones debe ser al menos 1.",
                "Validación",
                JOptionPane.WARNING_MESSAGE
            );
            return false;
        }
        if (precioUnitario < 0) {
            JOptionPane.showMessageDialog(
                null,
                "El precio unitario no puede ser negativo.",
                "Validación",
                JOptionPane.WARNING_MESSAGE
            );
            return false;
        }
        return true;
    }

    /** Comprueba si ya existe en disco un .txt para este tratamiento */
    public boolean existeRegistro() {
        File f = new File(PREFIX + codigoServicio + EXT);
        return f.exists();
    }

    // ————— Implementación de Inter_GAE —————

    public String getCodigo() {
    return codigoServicio;
}
public String getDescripcion() {
    return descripcionServicio;
}

    @Override
    public boolean mtd_guardar() {
        if (!validarDatos()) return false;

        if (existeRegistro()) {
            JOptionPane.showMessageDialog(
                null,
                "Ya existe un tratamiento con ese código.",
                "Duplicado",
                JOptionPane.WARNING_MESSAGE
            );
            return false;
        }

        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Guardar Tratamiento (.txt)");
        chooser.setFileFilter(new FileNameExtensionFilter("Texto (*.txt)", "txt"));
        if (chooser.showSaveDialog(null) != JFileChooser.APPROVE_OPTION) {
            return false;
        }

        File f = chooser.getSelectedFile();
        if (!f.getName().toLowerCase().endsWith(".txt")) {
            f = new File(f.getAbsolutePath() + ".txt");
        }

        calcularTotalLinea();
        try (PrintWriter pw = new PrintWriter(new FileWriter(f, false))) {
            pw.println("CodigoServicio="     + codigoServicio);
            pw.println("DescripcionServicio=" + descripcionServicio);
            pw.println("CantidadSesiones="    + cantidadSesiones);
            pw.println("PrecioUnitario="      + precioUnitario);
            JOptionPane.showMessageDialog(
                null,
                "Tratamiento guardado en:\n" + f.getName(),
                "Éxito",
                JOptionPane.INFORMATION_MESSAGE
            );
            return true;
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(
                null,
                "Error al guardar tratamiento:\n" + ex.getMessage(),
                "Error I/O",
                JOptionPane.ERROR_MESSAGE
            );
            return false;
        }
    }

    @Override
    public boolean mtd_buscar() {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Abrir Tratamiento (.txt)");
        chooser.setFileFilter(new FileNameExtensionFilter("Texto (*.txt)", "txt"));
        if (chooser.showOpenDialog(null) != JFileChooser.APPROVE_OPTION) {
            return false;
        }

        File f = chooser.getSelectedFile();
        if (!f.exists() || !f.getName().toLowerCase().endsWith(".txt")) {
            JOptionPane.showMessageDialog(
                null,
                "Seleccione un archivo válido.",
                "Error",
                JOptionPane.WARNING_MESSAGE
            );
            return false;
        }

        try (BufferedReader br = new BufferedReader(new FileReader(f))) {
            String linea;
            while ((linea = br.readLine()) != null) {
                String[] parts = linea.split("=", 2);
                if (parts.length != 2) continue;
                String key = parts[0].trim(), val = parts[1].trim();
                switch (key) {
                    case "CodigoServicio":
                        this.codigoServicio = val;
                        break;
                    case "DescripcionServicio":
                        this.descripcionServicio = val;
                        break;
                    case "CantidadSesiones":
                        this.cantidadSesiones = Integer.parseInt(val);
                        break;
                    case "PrecioUnitario":
                        this.precioUnitario = Double.parseDouble(val);
                        break;
                }
            }
            calcularTotalLinea();
            return true;
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(
                null,
                "Error al leer tratamiento:\n" + ex.getMessage(),
                "Error I/O",
                JOptionPane.ERROR_MESSAGE
            );
            return false;
        }
    }

    @Override
    public boolean mtd_eliminar() {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Eliminar Tratamiento (.txt)");
        chooser.setFileFilter(new FileNameExtensionFilter("Texto (*.txt)", "txt"));
        if (chooser.showOpenDialog(null) != JFileChooser.APPROVE_OPTION) {
            return false;
        }

        File f = chooser.getSelectedFile();
        if (!f.exists()) {
            JOptionPane.showMessageDialog(
                null,
                "El archivo no existe.",
                "Error",
                JOptionPane.ERROR_MESSAGE
            );
            return false;
        }

        if (f.delete()) {
            JOptionPane.showMessageDialog(
                null,
                "Tratamiento eliminado:\n" + f.getName(),
                "Éxito",
                JOptionPane.INFORMATION_MESSAGE
            );
            return true;
        } else {
            JOptionPane.showMessageDialog(
                null,
                "No se pudo eliminar el archivo.",
                "Error I/O",
                JOptionPane.ERROR_MESSAGE
            );
            return false;
        }
    }

    public double getPrecio() {
        return precioUnitario;
    }

    public double getTotal() {
        return totalLinea;
    }
}

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

/**
 * Representa una línea de detalle de factura y permite
 * guardar, buscar y eliminar su información en archivos .txt
 */
public class DetalleFactura extends Validacion implements Inter_GAE {

    private String codigoServicio;
    private String descripcionServicio;
    private int    cantidadSesiones;
    private double precioUnitario;
    private double totalLinea;

    public DetalleFactura() {
        this.codigoServicio      = "";
        this.descripcionServicio = "";
        this.cantidadSesiones    = 0;
        this.precioUnitario      = 0.0;
        this.totalLinea          = 0.0;
    }

    public DetalleFactura(String codigoServicio,
                          String descripcionServicio,
                          int cantidadSesiones,
                          double precioUnitario) {
        this.codigoServicio      = codigoServicio;
        this.descripcionServicio = descripcionServicio;
        this.cantidadSesiones    = cantidadSesiones;
        this.precioUnitario      = precioUnitario;
        calcularTotalLinea();
    }

    // — Getters / Setters —

    public String getCodigoServicio() {
        return codigoServicio;
    }

    public void setCodigoServicio(String codigoServicio) {
        this.codigoServicio = codigoServicio;
    }

    public String getDescripcionServicio() {
        return descripcionServicio;
    }

    public void setDescripcionServicio(String descripcionServicio) {
        this.descripcionServicio = descripcionServicio;
    }

    public int getCantidadSesiones() {
        return cantidadSesiones;
    }

    public void setCantidadSesiones(int cantidadSesiones) {
        this.cantidadSesiones = cantidadSesiones;
        calcularTotalLinea();
    }

    public double getPrecioUnitario() {
        return precioUnitario;
    }

    public void setPrecioUnitario(double precioUnitario) {
        this.precioUnitario = precioUnitario;
        calcularTotalLinea();
    }

    public double getTotalLinea() {
        return totalLinea;
    }

    /** Recalcula totalLinea = cantidadSesiones * precioUnitario */
    public void calcularTotalLinea() {
        this.totalLinea = this.cantidadSesiones * this.precioUnitario;
    }

    // — Aliases para Factura —

    public String getCodigoProducto()    { return getCodigoServicio();    }
    public String getNombreProducto()    { return getDescripcionServicio(); }
    public int    getCantidad()          { return getCantidadSesiones();  }
    public double getPrecio()            { return getPrecioUnitario();    }

    // — Métodos CRUD en .txt —

    @Override
    public boolean mtd_guardar() {
        // 1) Validaciones
        if (codigoServicio.trim().isEmpty() ||
            descripcionServicio.trim().isEmpty() ||
            cantidadSesiones < 1 ||
            precioUnitario < 0.0) {
            JOptionPane.showMessageDialog(null,
                "Datos inválidos. Verifique código, descripción, cantidad y precio.",
                "Atención",
                JOptionPane.WARNING_MESSAGE);
            return false;
        }

        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Guardar DetalleFactura (.txt)");
        chooser.setFileFilter(new FileNameExtensionFilter("Archivos de texto (*.txt)", "txt"));

        if (chooser.showSaveDialog(null) != JFileChooser.APPROVE_OPTION) {
            return false;
        }

        File f = chooser.getSelectedFile();
        if (!f.getName().toLowerCase().endsWith(".txt")) {
            f = new File(f.getAbsolutePath() + ".txt");
        }

        try (PrintWriter pw = new PrintWriter(new FileWriter(f, false))) {
            pw.println("CodigoServicio="    + codigoServicio);
            pw.println("DescripcionServicio=" + descripcionServicio);
            pw.println("CantidadSesiones="   + cantidadSesiones);
            pw.println("PrecioUnitario="     + precioUnitario);
            pw.println("TotalLinea="         + totalLinea);
            JOptionPane.showMessageDialog(null,
                "DetalleFactura guardado en:\n" + f.getName(),
                "Éxito",
                JOptionPane.INFORMATION_MESSAGE);
            return true;
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(null,
                "Error al guardar:\n" + ex.getMessage(),
                "Error I/O",
                JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }

    @Override
    public boolean mtd_buscar() {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Abrir DetalleFactura (.txt)");
        chooser.setFileFilter(new FileNameExtensionFilter("Archivos de texto (*.txt)", "txt"));

        if (chooser.showOpenDialog(null) != JFileChooser.APPROVE_OPTION) {
            return false;
        }
        File f = chooser.getSelectedFile();
        if (!f.exists() || !f.getName().toLowerCase().endsWith(".txt")) {
            JOptionPane.showMessageDialog(null,
                "Seleccione un archivo .txt válido.",
                "Atención",
                JOptionPane.WARNING_MESSAGE);
            return false;
        }

        try (BufferedReader br = new BufferedReader(new FileReader(f))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split("=", 2);
                if (parts.length != 2) continue;
                String key = parts[0].trim(), val = parts[1].trim();
                switch (key) {
                    case "CodigoServicio":
                        codigoServicio = val; break;
                    case "DescripcionServicio":
                        descripcionServicio = val; break;
                    case "CantidadSesiones":
                        cantidadSesiones = Integer.parseInt(val);
                        break;
                    case "PrecioUnitario":
                        precioUnitario = Double.parseDouble(val);
                        break;
                    case "TotalLinea":
                        totalLinea = Double.parseDouble(val);
                        break;
                }
            }
            return true;
        } catch (IOException | NumberFormatException ex) {
            JOptionPane.showMessageDialog(null,
                "Error al leer:\n" + ex.getMessage(),
                "Error I/O",
                JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }

    @Override
    public boolean mtd_eliminar() {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Eliminar DetalleFactura (.txt)");
        chooser.setFileFilter(new FileNameExtensionFilter("Archivos de texto (*.txt)", "txt"));

        if (chooser.showOpenDialog(null) != JFileChooser.APPROVE_OPTION) {
            return false;
        }
        File f = chooser.getSelectedFile();
        if (!f.exists()) {
            JOptionPane.showMessageDialog(null,
                "El archivo no existe.",
                "Atención",
                JOptionPane.WARNING_MESSAGE);
            return false;
        }
        if (f.delete()) {
            JOptionPane.showMessageDialog(null,
                "Archivo eliminado:\n" + f.getName(),
                "Éxito",
                JOptionPane.INFORMATION_MESSAGE);
            return true;
        } else {
            JOptionPane.showMessageDialog(null,
                "No se pudo eliminar el archivo.",
                "Error",
                JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }
}

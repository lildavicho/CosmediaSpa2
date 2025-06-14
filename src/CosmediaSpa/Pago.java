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
import javax.swing.JOptionPane;
import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.swing.table.DefaultTableModel;

public class Pago extends Validacion implements Inter_GAE {
    private String codigoPago;
    private String numeroFactura;
    private Date   fechaPago;
    private String metodoPago;
    private String estado;
    private String observaciones;
    private double monto;

    /** Archivo en el que se guardará o de donde se leerá */
    private File archivoActual;

    private static final SimpleDateFormat FORMATO_FECHA =
        new SimpleDateFormat("yyyy-MM-dd");

    public Pago() { /* inicializa con valores vacíos */ }

    public Pago(String codigoPago, String numeroFactura,
                Date fechaPago, String metodoPago,
                String estado, String observaciones,
                double monto) {
        this.codigoPago    = codigoPago;
        this.numeroFactura = numeroFactura;
        this.fechaPago     = fechaPago;
        this.metodoPago    = metodoPago;
        this.estado        = estado;
        this.observaciones = observaciones;
        this.monto         = monto;
    }

    // — Getters & setters —
    public String getCodigoPago()          { return codigoPago; }
    public void   setCodigoPago(String c)  { this.codigoPago = c; }

    public String getNumeroFactura()             { return numeroFactura; }
    public void   setNumeroFactura(String f)     { this.numeroFactura = f; }

    public Date   getFechaPago()                 { return fechaPago; }
    public void   setFechaPago(Date d)           { this.fechaPago = d; }

    public String getMetodoPago()           { return metodoPago; }
    public void   setMetodoPago(String m)   { this.metodoPago = m; }

    public String getEstado()               { return estado; }
    public void   setEstado(String e)       { this.estado = e; }

    public String getObservaciones()            { return observaciones; }
    public void   setObservaciones(String o)    { this.observaciones = o; }

    public double getMonto()               { return monto; }
    public void   setMonto(double m)       { this.monto = m; }

    /** Inyecta el File (con ruta y nombre) donde se guardará o leerá este pago */
    public void setArchivo(File archivo)   { this.archivoActual = archivo; }

    /** Validación básica de campos obligatorios */
    private boolean validarDatos() {
        if (codigoPago == null || codigoPago.trim().isEmpty()) {
            JOptionPane.showMessageDialog(null,
                "El código de pago no puede estar vacío.",
                "Validación", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        if (numeroFactura == null || numeroFactura.trim().isEmpty()) {
            JOptionPane.showMessageDialog(null,
                "El número de factura no puede estar vacío.",
                "Validación", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        if (fechaPago == null) {
            JOptionPane.showMessageDialog(null,
                "La fecha de pago no puede ser nula.",
                "Validación", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        if (metodoPago == null || metodoPago.trim().isEmpty()) {
            JOptionPane.showMessageDialog(null,
                "Seleccione un método de pago.",
                "Validación", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        if (estado == null || estado.trim().isEmpty()) {
            JOptionPane.showMessageDialog(null,
                "Seleccione un estado.",
                "Validación", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        if (monto < 0) {
            JOptionPane.showMessageDialog(null,
                "El monto no puede ser negativo.",
                "Validación", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        if (archivoActual == null) {
            JOptionPane.showMessageDialog(null,
                "No se indicó el archivo donde guardar.",
                "Validación", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        return true;
    }

    @Override
    public boolean mtd_guardar() {
        if (!validarDatos()) return false;

        try ( PrintWriter pw = new PrintWriter(new FileWriter(archivoActual)) ) {
            pw.println("CodigoPago="    + codigoPago);
            pw.println("NumeroFactura=" + numeroFactura);
            pw.println("FechaPago="     + FORMATO_FECHA.format(fechaPago));
            pw.println("MetodoPago="    + metodoPago);
            pw.println("Estado="        + estado);
            pw.println("Observaciones=" + observaciones);
            pw.println("Monto="         + monto);
            return true;
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(null,
                "Error al guardar pago:\n" + ex.getMessage(),
                "Error I/O", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }

    @Override
    public boolean mtd_buscar() {
        if (archivoActual == null) {
            JOptionPane.showMessageDialog(null,
                "No se indicó el archivo a leer.",
                "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        if (!archivoActual.exists()) {
            JOptionPane.showMessageDialog(null,
                "El archivo no existe:\n" + archivoActual.getAbsolutePath(),
                "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        try ( BufferedReader br = new BufferedReader(new FileReader(archivoActual)) ) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] kv = line.split("=", 2);
                if (kv.length != 2) continue;
                String key = kv[0].trim(), val = kv[1].trim();
                switch (key) {
                    case "CodigoPago":
                        this.codigoPago = val; break;
                    case "NumeroFactura":
                        this.numeroFactura = val; break;
                    case "FechaPago":
                        try {
                            this.fechaPago = FORMATO_FECHA.parse(val);
                        } catch (ParseException e) {
                            this.fechaPago = new Date();
                        }
                        break;
                    case "MetodoPago":
                        this.metodoPago = val; break;
                    case "Estado":
                        this.estado = val; break;
                    case "Observaciones":
                        this.observaciones = val; break;
                    case "Monto":
                        try { this.monto = Double.parseDouble(val); }
                        catch (NumberFormatException e) { this.monto = 0.0; }
                        break;
                }
            }
            return true;
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(null,
                "Error al leer pago:\n" + ex.getMessage(),
                "Error I/O", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }

    @Override
    public boolean mtd_eliminar() {
        if (archivoActual == null) {
            JOptionPane.showMessageDialog(null,
                "No se indicó el archivo a eliminar.",
                "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        if (!archivoActual.exists()) {
            JOptionPane.showMessageDialog(null,
                "El archivo no existe:\n" + archivoActual.getAbsolutePath(),
                "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        boolean ok = archivoActual.delete();
        if (!ok) {
            JOptionPane.showMessageDialog(null,
                "No se pudo eliminar el archivo.",
                "Error I/O", JOptionPane.ERROR_MESSAGE);
        }
        return ok;
    }

    /** Comprueba si ya existe un pago (mismo código) en la tabla */
    public static boolean existeRegistro(DefaultTableModel model, String codigoPago) {
        for (int i = 0; i < model.getRowCount(); i++) {
            if ( model.getValueAt(i, 0).equals(codigoPago) ) {
                return true;
            }
        }
        return false;
    }
}

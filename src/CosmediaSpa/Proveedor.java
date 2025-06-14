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
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Proveedor extends Validacion implements Inter_GAE {

    private String cedula;
    private String nombre;
    private String direccion;
    private String telefono;
    private String email;
    private Date   fechaRegistro;
    private String codigoProvincia;
    private String codigoPostal;
    private double saldo;

    private static final SimpleDateFormat FORMATO_FECHA =
        new SimpleDateFormat("yyyy-MM-dd");

    public Proveedor() {
        this.cedula           = "";
        this.nombre           = "";
        this.direccion        = "";
        this.telefono         = "";
        this.email            = "";
        this.fechaRegistro    = new Date();
        this.codigoProvincia  = "";
        this.codigoPostal     = "";
        this.saldo            = 0.0;
    }

    public Proveedor(String cedula,
                     String nombre,
                     String direccion,
                     String telefono,
                     String email,
                     Date   fechaRegistro,
                     String codigoProvincia,
                     String codigoPostal,
                     double saldo) {
        this.cedula           = cedula;
        this.nombre           = nombre;
        this.direccion        = direccion;
        this.telefono         = telefono;
        this.email            = email;
        this.fechaRegistro    = fechaRegistro;
        this.codigoProvincia  = codigoProvincia;
        this.codigoPostal     = codigoPostal;
        this.saldo            = saldo;
    }

    // ——— Getters & Setters ———
    public String getCedula()          { return cedula; }
    public void   setCedula(String c)  { cedula = c;    }

    public String getNombre()          { return nombre; }
    public void   setNombre(String n)  { nombre = n;    }

    public String getDireccion()       { return direccion; }
    public void   setDireccion(String d){ direccion = d; }

    public String getTelefono()        { return telefono; }
    public void   setTelefono(String t){ telefono = t;   }

    public String getEmail()           { return email; }
    public void   setEmail(String e)   { email = e;    }

    public Date   getFechaRegistro()   { return fechaRegistro; }
    public void   setFechaRegistro(Date f){ fechaRegistro = f; }

    public String getCodigoProvincia()       { return codigoProvincia; }
    public void   setCodigoProvincia(String cp){ codigoProvincia = cp; }

    public String getCodigoPostal()       { return codigoPostal; }
    public void   setCodigoPostal(String cp){ codigoPostal = cp; }

    public double getSaldo()            { return saldo; }
    public void   setSaldo(double s)    { saldo = s;    }

    /** Ajusta el saldo agregando (o restando) un importe. */
    public void ajustarSaldo(double delta) {
        saldo += delta;
    }

    /** Valida cédula, email y teléfono. */
    public boolean validarDatos() {
        if (!validarCedula(cedula)) {
            JOptionPane.showMessageDialog(null,
                "Cédula inválida.", "Validación",
                JOptionPane.WARNING_MESSAGE);
            return false;
        }
        if (nombre == null || nombre.trim().isEmpty()) {
            JOptionPane.showMessageDialog(null,
                "Nombre vacío.", "Validación",
                JOptionPane.WARNING_MESSAGE);
            return false;
        }
        if (!validarTelefono(telefono)) {
            JOptionPane.showMessageDialog(null,
                "Teléfono inválido.", "Validación",
                JOptionPane.WARNING_MESSAGE);
            return false;
        }
        if (!validarEmail(email)) {
            JOptionPane.showMessageDialog(null,
                "Email inválido.", "Validación",
                JOptionPane.WARNING_MESSAGE);
            return false;
        }
        return true;
    }

    @Override
    public boolean mtd_guardar() {
        if (!validarDatos()) return false;

        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Guardar Proveedor (.txt)");
        chooser.setFileFilter(
          new FileNameExtensionFilter("Archivos de texto (*.txt)", "txt")
        );
        if (chooser.showSaveDialog(null) != JFileChooser.APPROVE_OPTION) {
            return false;
        }

        File archivo = chooser.getSelectedFile();
        if (!archivo.getName().toLowerCase().endsWith(".txt")) {
            archivo = new File(archivo.getAbsolutePath() + ".txt");
        }

        try (PrintWriter pw = new PrintWriter(new FileWriter(archivo, false))) {
            pw.println("Cedula="          + cedula);
            pw.println("Nombre="          + nombre);
            pw.println("Direccion="       + direccion);
            pw.println("Telefono="        + telefono);
            pw.println("Email="           + email);
            pw.println("FechaRegistro="   + FORMATO_FECHA.format(fechaRegistro));
            pw.println("CodigoProvincia=" + codigoProvincia);
            pw.println("CodigoPostal="    + codigoPostal);
            pw.println("Saldo="           + saldo);

            JOptionPane.showMessageDialog(null,
                "Proveedor guardado en:\n" + archivo.getName(),
                "Éxito", JOptionPane.INFORMATION_MESSAGE);
            return true;
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(null,
                "Error al guardar:\n" + ex.getMessage(),
                "Error I/O", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }

    @Override
    public boolean mtd_buscar() {
        String clave = cedula;
        if (clave == null || clave.trim().isEmpty()) {
            JOptionPane.showMessageDialog(null,
                "Ingrese la cédula antes de buscar.",
                "Aviso", JOptionPane.WARNING_MESSAGE);
            return false;
        }

        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Abrir Proveedor (.txt)");
        chooser.setFileFilter(
          new FileNameExtensionFilter("Archivos de texto (*.txt)", "txt")
        );
        if (chooser.showOpenDialog(null) != JFileChooser.APPROVE_OPTION) {
            return false;
        }

        File archivo = chooser.getSelectedFile();
        if (!archivo.exists() || !archivo.getName().toLowerCase().endsWith(".txt")) {
            JOptionPane.showMessageDialog(null,
                "Seleccione un .txt válido.",
                "Aviso", JOptionPane.WARNING_MESSAGE);
            return false;
        }

        try (BufferedReader br = new BufferedReader(new FileReader(archivo))) {
            String linea;
            while ((linea = br.readLine()) != null) {
                String[] parts = linea.split("=",2);
                if (parts.length!=2) continue;
                String key = parts[0].trim(), val = parts[1].trim();
                switch(key) {
                    case "Cedula":            cedula = val; break;
                    case "Nombre":            nombre = val; break;
                    case "Direccion":         direccion = val; break;
                    case "Telefono":          telefono = val; break;
                    case "Email":             email = val; break;
                    case "FechaRegistro":
                        try {
                            fechaRegistro = FORMATO_FECHA.parse(val);
                        } catch(ParseException px) {
                            fechaRegistro = new Date();
                        }
                        break;
                    case "CodigoProvincia":   codigoProvincia = val; break;
                    case "CodigoPostal":      codigoPostal = val; break;
                    case "Saldo":
                        try {
                            saldo = Double.parseDouble(val);
                        } catch(NumberFormatException nx) {
                            saldo = 0.0;
                        }
                        break;
                    default: break;
                }
            }
            return true;
        } catch(IOException ex) {
            JOptionPane.showMessageDialog(null,
                "Error al leer:\n" + ex.getMessage(),
                "Error I/O", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }

    @Override
    public boolean mtd_eliminar() {
        String clave = cedula;
        if (clave == null || clave.trim().isEmpty()) {
            JOptionPane.showMessageDialog(null,
                "Ingrese la cédula antes de eliminar.",
                "Aviso", JOptionPane.WARNING_MESSAGE);
            return false;
        }

        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Eliminar Proveedor (.txt)");
        chooser.setFileFilter(
          new FileNameExtensionFilter("Archivos de texto (*.txt)", "txt")
        );
        if (chooser.showOpenDialog(null) != JFileChooser.APPROVE_OPTION) {
            return false;
        }

        File archivo = chooser.getSelectedFile();
        if (!archivo.exists()) {
            JOptionPane.showMessageDialog(null,
                "El archivo no existe.",
                "Aviso", JOptionPane.WARNING_MESSAGE);
            return false;
        }

        if (archivo.delete()) {
            JOptionPane.showMessageDialog(null,
                "Proveedor eliminado:\n" + archivo.getName(),
                "Éxito", JOptionPane.INFORMATION_MESSAGE);
            return true;
        } else {
            JOptionPane.showMessageDialog(null,
                "No se pudo eliminar el archivo.",
                "Error I/O", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }

    /**
     * Recorre un DefaultTableModel y devuelve true si ya existe
     * una fila cuya columna 0 coincide con la clave dada.
     */
    public static boolean existeRegistro(DefaultTableModel model, String clave) {
        for (int i = 0; i < model.getRowCount(); i++) {
            if ( model.getValueAt(i, 0).equals(clave) ) {
                return true;
            }
        }
        return false;
    }
}

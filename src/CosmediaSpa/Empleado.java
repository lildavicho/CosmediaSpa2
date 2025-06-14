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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Empleado extends Validacion implements Inter_GAE {

    private static final SimpleDateFormat FORMATO_FECHA = new SimpleDateFormat("yyyy-MM-dd");

    private String cedula;
    private String nombre;
    private String telefono;
    private String email;
    private Date fechaContratacion;

    public Empleado() {
        this.cedula            = "";
        this.nombre            = "";
        this.telefono          = "";
        this.email             = "";
        this.fechaContratacion = new Date();
    }

    public Empleado(String cedula, String nombre, String telefono, String email, Date fechaContratacion) {
        this.cedula            = cedula;
        this.nombre            = nombre;
        this.telefono          = telefono;
        this.email             = email;
        this.fechaContratacion = fechaContratacion;
    }

    // — Getters y Setters —
    public String getCedula()                { return cedula; }
    public void setCedula(String cedula)     { this.cedula = cedula; }
    public String getNombre()                { return nombre; }
    public void setNombre(String nombre)     { this.nombre = nombre; }
    public String getTelefono()              { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }
    public String getEmail()                 { return email; }
    public void setEmail(String email)       { this.email = email; }
    public Date   getFechaContratacion()     { return fechaContratacion; }
    public void setFechaContratacion(Date fechaContratacion) { this.fechaContratacion = fechaContratacion; }

    /** Comprueba si ya existe un archivo para este empleado */
    public boolean existeArchivo() {
        File f = new File("empleado_" + cedula + ".txt");
        return f.exists();
    }

    @Override
    public boolean mtd_guardar() {
        // 1) Validaciones
        if (cedula == null || cedula.trim().isEmpty() || !validarCedula(cedula)) {
            JOptionPane.showMessageDialog(null, "Cédula inválida.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        if (nombre == null || nombre.trim().isEmpty()) {
            JOptionPane.showMessageDialog(null, "El nombre no puede estar vacío.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        if (telefono == null || telefono.trim().isEmpty() || !validarTelefono(telefono)) {
            JOptionPane.showMessageDialog(null, "Teléfono inválido.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        if (email == null || email.trim().isEmpty() || !validarEmail(email)) {
            JOptionPane.showMessageDialog(null, "Email inválido.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        if (fechaContratacion == null) {
            JOptionPane.showMessageDialog(null, "Seleccione una fecha de contratación.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return false;
        }

        // 2) Diálogo para guardar
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Guardar Empleado (.txt)");
        chooser.setFileFilter(new FileNameExtensionFilter("Archivos de texto (*.txt)", "txt"));
        if (chooser.showSaveDialog(null) != JFileChooser.APPROVE_OPTION) {
            return false;
        }

        File f = chooser.getSelectedFile();
        if (!f.getName().toLowerCase().endsWith(".txt")) {
            f = new File(f.getAbsolutePath() + ".txt");
        }

        // 3) Escritura
        try (PrintWriter pw = new PrintWriter(new FileWriter(f, false))) {
            pw.println("Cedula="           + cedula);
            pw.println("Nombre="           + nombre);
            pw.println("Telefono="         + telefono);
            pw.println("Email="            + email);
            pw.println("FechaContratacion=" + FORMATO_FECHA.format(fechaContratacion));
            JOptionPane.showMessageDialog(null, "Empleado guardado en:\n" + f.getName(), "Éxito", JOptionPane.INFORMATION_MESSAGE);
            return true;
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(null, "Error al guardar:\n" + ex.getMessage(), "Error I/O", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }

    @Override
    public boolean mtd_buscar() {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Abrir Empleado (.txt)");
        chooser.setFileFilter(new FileNameExtensionFilter("Archivos de texto (*.txt)", "txt"));
        if (chooser.showOpenDialog(null) != JFileChooser.APPROVE_OPTION) {
            return false;
        }

        File f = chooser.getSelectedFile();
        if (!f.exists() || !f.getName().toLowerCase().endsWith(".txt")) {
            JOptionPane.showMessageDialog(null, "Seleccione un .txt válido.", "Atención", JOptionPane.WARNING_MESSAGE);
            return false;
        }

        try (BufferedReader br = new BufferedReader(new FileReader(f))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split("=", 2);
                if (parts.length != 2) continue;
                String key = parts[0].trim(), val = parts[1].trim();
                switch (key) {
                    case "Cedula":           cedula = val; break;
                    case "Nombre":           nombre = val; break;
                    case "Telefono":         telefono = val; break;
                    case "Email":            email = val; break;
                    case "FechaContratacion":
                        try { fechaContratacion = FORMATO_FECHA.parse(val); }
                        catch (ParseException e) { fechaContratacion = new Date(); }
                        break;
                }
            }
            return true;
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(null, "Error al leer:\n" + ex.getMessage(), "Error I/O", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }

    @Override
    public boolean mtd_eliminar() {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Eliminar Empleado (.txt)");
        chooser.setFileFilter(new FileNameExtensionFilter("Archivos de texto (*.txt)", "txt"));
        if (chooser.showOpenDialog(null) != JFileChooser.APPROVE_OPTION) {
            return false;
        }
        File f = chooser.getSelectedFile();
        if (!f.exists()) {
            JOptionPane.showMessageDialog(null, "El archivo no existe.", "Atención", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        if (f.delete()) {
            JOptionPane.showMessageDialog(null, "Archivo eliminado:\n" + f.getName(), "Éxito", JOptionPane.INFORMATION_MESSAGE);
            return true;
        } else {
            JOptionPane.showMessageDialog(null, "No se pudo eliminar.", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }
}

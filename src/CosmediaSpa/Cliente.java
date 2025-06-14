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
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;
import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Cliente extends Validacion implements Inter_GAE {

    private String cedula;
    private String nombre;
    private String direccion;
    private String telefono;
    private String email;
    private Date fechaRegistro;

    private static final SimpleDateFormat FORMATO_FECHA =
        new SimpleDateFormat("yyyy-MM-dd");

    public Cliente() {
        this("", "", "", "", "", new Date());
    }

    public Cliente(String cedula,
                   String nombre,
                   String direccion,
                   String telefono,
                   String email,
                   Date fechaRegistro) {
        this.cedula        = cedula;
        this.nombre        = nombre;
        this.direccion     = direccion;
        this.telefono      = telefono;
        this.email         = email;
        this.fechaRegistro = fechaRegistro;
    }

    // — Getters & Setters —
    public String getCedula()        { return cedula; }
    public void   setCedula(String c){ cedula = c;    }
    public String getNombre()        { return nombre; }
    public void   setNombre(String n){ nombre = n;    }
    public String getDireccion()     { return direccion; }
    public void   setDireccion(String d){ direccion = d; }
    public String getTelefono()      { return telefono; }
    public void   setTelefono(String t){ telefono = t; }
    public String getEmail()         { return email;    }
    public void   setEmail(String e) { email = e;      }
    public Date   getFechaRegistro() { return fechaRegistro; }
    public void   setFechaRegistro(Date f){ fechaRegistro = f; }

    /** Validaciones de cédula, email y teléfono via Validacion */
    public boolean validarDatos() {
        return validarCedula(cedula)
            && validarEmail(email)
            && validarTelefono(telefono);
    }

    /**
     * Guarda este cliente pidiendo ubicación y nombre al usuario.
     * El diálogo se inicializa proponiendo "cliente_{cedula}.txt".
     */
    private JFileChooser externalChooser = null;
     public void setFileChooser(JFileChooser chooser) {
        this.externalChooser = chooser;
    }
     
    @Override
    public boolean mtd_guardar() {
        if (!validarDatos()) {
            JOptionPane.showMessageDialog(null,
                "Datos inválidos. Verifique cédula, email y teléfono.");
            return false;
        }

        JFileChooser chooser = externalChooser != null
            ? externalChooser
            : new JFileChooser();

        chooser.setDialogTitle("Guardar Cliente (.txt)");
        chooser.setFileFilter(new FileNameExtensionFilter("Archivos de texto (*.txt)", "txt"));

        if (chooser.showSaveDialog(null) != JFileChooser.APPROVE_OPTION) {
            return false; // canceló
        }
        File archivo = chooser.getSelectedFile();
        if (!archivo.getName().toLowerCase().endsWith(".txt")) {
            archivo = new File(archivo.getAbsolutePath() + ".txt");
        }

        // Rest of mtd_guardar as before...
        try (PrintWriter pw = new PrintWriter(new FileWriter(archivo, false))) {
            pw.println("Cedula="        + cedula);
            pw.println("Nombre="        + nombre);
            pw.println("Direccion="     + direccion);
            pw.println("Telefono="      + telefono);
            pw.println("Email="         + email);
            pw.println("FechaRegistro=" + FORMATO_FECHA.format(fechaRegistro));
            return true;
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(null,
                "Error al guardar cliente:\n" + ex.getMessage(),
                "Error I/O", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }

    /**
     * Abre un JFileChooser para seleccionar un cliente_*.txt y carga sus datos.
     */
    @Override
    public boolean mtd_buscar() {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Buscar Cliente (.txt)");
        chooser.setFileFilter(new FileNameExtensionFilter(
            "Archivos de texto (*.txt)", "txt"));

        if (chooser.showOpenDialog(null) != JFileChooser.APPROVE_OPTION) {
            return false;
        }
        File archivo = chooser.getSelectedFile();
        return cargarDesdeArchivo(archivo);
    }

    /**
     * Lee todos los campos desde el archivo dado.
     * @return true si pudo leerse correctamente
     */
    public boolean cargarDesdeArchivo(File archivo) {
        if (archivo == null
         || !archivo.exists()
         || !archivo.getName().toLowerCase().endsWith(".txt")) {
            JOptionPane.showMessageDialog(null,
                "El archivo no es válido.",
                "Error", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        try (BufferedReader br = new BufferedReader(new FileReader(archivo))) {
            String linea;
            while ((linea = br.readLine()) != null) {
                String[] parts = linea.split("=",2);
                if (parts.length != 2) continue;
                String clave = parts[0].trim();
                String val   = parts[1].trim();
                switch (clave) {
                    case "Cedula":
                        cedula = val; break;
                    case "Nombre":
                        nombre = val; break;
                    case "Direccion":
                        direccion = val; break;
                    case "Telefono":
                        telefono = val; break;
                    case "Email":
                        email = val; break;
                    case "FechaRegistro":
                        try {
                            fechaRegistro = FORMATO_FECHA.parse(val);
                        } catch (ParseException x) {
                            fechaRegistro = new Date();
                        }
                        break;
                    default:
                        // ignorar
                }
            }
            return true;
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(null,
                "Error al leer cliente:\n" + ex.getMessage(),
                "Error I/O", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }

    /**
     * Abre un JFileChooser para eliminar un archivo .txt de cliente.
     */
    @Override
    public boolean mtd_eliminar() {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Eliminar Cliente (.txt)");
        chooser.setFileFilter(new FileNameExtensionFilter(
            "Archivos de texto (*.txt)", "txt"));

        if (chooser.showOpenDialog(null) != JFileChooser.APPROVE_OPTION) {
            return false;
        }
        File archivo = chooser.getSelectedFile();
        if (!archivo.exists()) {
            JOptionPane.showMessageDialog(null,
                "El archivo no existe.",
                "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        if (archivo.delete()) {
            JOptionPane.showMessageDialog(null,
                "Cliente eliminado:\n" + archivo.getName(),
                "Éxito", JOptionPane.INFORMATION_MESSAGE);
            return true;
        } else {
            JOptionPane.showMessageDialog(null,
                "No se pudo eliminar el archivo.",
                "Error I/O", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }

    /** Comprueba duplicados de cédula en un DefaultTableModel */
    public static boolean existeRegistro(DefaultTableModel model, String cedula) {
        for (int i = 0; i < model.getRowCount(); i++) {
            if (model.getValueAt(i,0).equals(cedula)) {
                return true;
            }
        }
        return false;
    }
}





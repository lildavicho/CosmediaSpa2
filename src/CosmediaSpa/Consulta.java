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
import java.io.*;

public class Consulta extends Validacion implements Inter_GAE {

    private String codigo;
    private String motivo;
    private double precio;
    private int duracionMinutos;
    private File archivo;

    public Consulta() {
        this("", "", 0.0, 0);
    }

    public Consulta(String codigo, String motivo, double precio, int duracionMinutos) {
        this.codigo = codigo;
        this.motivo = motivo;
        this.precio = precio;
        this.duracionMinutos = duracionMinutos;
    }

    // Getters y setters
    public String getCodigo() { return codigo; }
    public void setCodigo(String c) { this.codigo = c; }

    public String getMotivo() { return motivo; }
    public void setMotivo(String m) { this.motivo = m; }

    public double getPrecio() { return precio; }
    public void setPrecio(double p) { this.precio = p; }

    public int getDuracion() { return duracionMinutos; }
    public int getDuracionMinutos() { return duracionMinutos; }
    public void setDuracionMinutos(int d) { this.duracionMinutos = d; }

    public void setArchivo(File archivo) {
        this.archivo = archivo;
    }

    
    private JFileChooser chooser = null;

    /** Inyecta un JFileChooser configurado */
    public void setFileChooser(JFileChooser c) {
        this.chooser = c;
    }

    @Override
    public boolean mtd_guardar() {
        if (codigo.trim().isEmpty()) {
            JOptionPane.showMessageDialog(null, "El código no puede estar vacío.");
            return false;
        }
        if (motivo.trim().isEmpty()) {
            JOptionPane.showMessageDialog(null, "El motivo no puede estar vacío.");
            return false;
        }
        if (precio < 0 || duracionMinutos < 0) {
            JOptionPane.showMessageDialog(null, "Precio o duración inválidos.");
            return false;
        }

        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Guardar Consulta como .txt");
        chooser.setSelectedFile(new File("consulta_" + codigo + ".txt"));
        chooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("Archivos de texto (*.txt)", "txt"));

        int result = chooser.showSaveDialog(null);
        if (result != JFileChooser.APPROVE_OPTION) {
            return false; // Canceló
        }

        archivo = chooser.getSelectedFile();
        if (!archivo.getName().toLowerCase().endsWith(".txt")) {
            archivo = new File(archivo.getAbsolutePath() + ".txt");
        }

        try (PrintWriter pw = new PrintWriter(new FileWriter(archivo))) {
            pw.println("Codigo=" + codigo);
            pw.println("Motivo=" + motivo);
            pw.println("Precio=" + precio);
            pw.println("Duracion=" + duracionMinutos);
            return true;
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(null,
                "Error al guardar el archivo de consulta:\n" + ex.getMessage(),
                "Error I/O", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }

    @Override
    public boolean mtd_buscar() {
        throw new UnsupportedOperationException("Usa cargarDesdeArchivo(File archivo)");
    }

    /** Carga desde archivo .txt previamente seleccionado */
    public boolean cargarDesdeArchivo(File archivo) {
        if (archivo == null || !archivo.exists() || !archivo.getName().toLowerCase().endsWith(".txt")) {
            JOptionPane.showMessageDialog(null,
                "Debes seleccionar un archivo .txt válido.",
                "Archivo inválido", JOptionPane.WARNING_MESSAGE);
            return false;
        }

        try (BufferedReader br = new BufferedReader(new FileReader(archivo))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split("=", 2);
                if (parts.length != 2) continue;
                String key = parts[0].trim();
                String val = parts[1].trim();
                switch (key) {
                    case "Codigo": this.codigo = val; break;
                    case "Motivo": this.motivo = val; break;
                    case "Precio": this.precio = Double.parseDouble(val); break;
                    case "Duracion": this.duracionMinutos = Integer.parseInt(val); break;
                }
            }
            return true;
        } catch (IOException | NumberFormatException ex) {
            JOptionPane.showMessageDialog(null,
                "Error al leer el archivo:\n" + ex.getMessage(),
                "Error I/O", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }

    @Override
    public boolean mtd_eliminar() {
        if (archivo == null) {
            archivo = new File("consulta_" + codigo + ".txt");
        }

        if (!archivo.exists()) {
            JOptionPane.showMessageDialog(null,
                "El archivo no existe.",
                "Atención", JOptionPane.WARNING_MESSAGE);
            return false;
        }

        boolean ok = archivo.delete();
        JOptionPane.showMessageDialog(null,
            ok ? "Consulta eliminada correctamente." : "No se pudo eliminar el archivo.",
            ok ? "Éxito" : "Error", ok ? JOptionPane.INFORMATION_MESSAGE : JOptionPane.ERROR_MESSAGE);
        return ok;
    }

    public static boolean existeRegistro(DefaultTableModel modelo, String codigo) {
        for (int i = 0; i < modelo.getRowCount(); i++) {
            if (modelo.getValueAt(i, 0).equals(codigo)) {
                return true;
            }
        }
        return false;
    }
}



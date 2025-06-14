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

/**
 * Clase Agenda: gestiona citas guardadas en archivos .txt.
 * Formato del archivo:
 *   agenda_<codigo>.txt
 *   Codigo=<valor>
 *   CedulaCliente=<valor>
 *   CodigoServicio=<valor>
 *   FechaCita=<yyyy-MM-dd>
 *   Cantidad=<valor>
 */
public class Agenda extends Validacion implements Inter_GAE {

    private String codigo;
    private String cedulaCliente;
    private String codigoServicio;
    private Date   fechaCita;
    private int    cantidad;

    private static final SimpleDateFormat FORMATO_FECHA =
        new SimpleDateFormat("yyyy-MM-dd");

    public Agenda() {
        this.codigo         = "";
        this.cedulaCliente  = "";
        this.codigoServicio = "";
        this.fechaCita      = new Date();
        this.cantidad       = 0;
    }

    public Agenda(String codigo, String cedulaCliente,
                  String codigoServicio, Date fechaCita, int cantidad) {
        this.codigo         = codigo;
        this.cedulaCliente  = cedulaCliente;
        this.codigoServicio = codigoServicio;
        this.fechaCita      = fechaCita;
        this.cantidad       = cantidad;
    }

    // ——— Getters & Setters ———

    public String getCodigo() {
        return codigo;
    }
    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getCedulaCliente() {
        return cedulaCliente;
    }
    public void setCedulaCliente(String cedulaCliente) {
        this.cedulaCliente = cedulaCliente;
    }

    public String getCodigoServicio() {
        return codigoServicio;
    }
    public void setCodigoServicio(String codigoServicio) {
        this.codigoServicio = codigoServicio;
    }

    public Date getFechaCita() {
        return fechaCita;
    }
    public void setFechaCita(Date fechaCita) {
        this.fechaCita = fechaCita;
    }

    public int getCantidad() {
        return cantidad;
    }
    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }

    /**
     * Comprueba si ya existe un archivo .txt para este código de agenda.
     */
    public boolean existeRegistro() {
        File f = new File("agenda_" + this.codigo + ".txt");
        return f.exists();
    }

    @Override
    public boolean mtd_guardar() {
        // 1) Validar campos
        if (codigo.trim().isEmpty()
            || !validarCedula(cedulaCliente)
            || codigoServicio.trim().isEmpty()
            || fechaCita == null
            || cantidad < 1) {
            JOptionPane.showMessageDialog(
                null,
                "Datos inválidos. Verifique todos los campos.",
                "Aviso",
                JOptionPane.WARNING_MESSAGE
            );
            return false;
        }

        // 2) Prevenir duplicados en disco
        if (existeRegistro()) {
            JOptionPane.showMessageDialog(
                null,
                "Ya existe una agenda con ese código.",
                "Duplicado",
                JOptionPane.WARNING_MESSAGE
            );
            return false;
        }

        // 3) Elegir ubicación de guardado
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Guardar Agenda (.txt)");
        chooser.setFileFilter(new FileNameExtensionFilter("Texto (*.txt)", "txt"));

        if (chooser.showSaveDialog(null) != JFileChooser.APPROVE_OPTION) {
            return false;
        }

        File f = chooser.getSelectedFile();
        if (!f.getName().toLowerCase().endsWith(".txt")) {
            f = new File(f.getAbsolutePath() + ".txt");
        }

        // 4) Escribir archivo
        try (PrintWriter pw = new PrintWriter(new FileWriter(f, false))) {
            pw.println("Codigo="        + codigo);
            pw.println("CedulaCliente=" + cedulaCliente);
            pw.println("CodigoServicio="+ codigoServicio);
            pw.println("FechaCita="     + FORMATO_FECHA.format(fechaCita));
            pw.println("Cantidad="      + cantidad);

            JOptionPane.showMessageDialog(
                null,
                "Agenda guardada en:\n" + f.getName(),
                "Éxito",
                JOptionPane.INFORMATION_MESSAGE
            );
            return true;

        } catch (IOException ex) {
            JOptionPane.showMessageDialog(
                null,
                "Error al guardar:\n" + ex.getMessage(),
                "Error I/O",
                JOptionPane.ERROR_MESSAGE
            );
            return false;
        }
    }

    @Override
    public boolean mtd_buscar() {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Abrir Agenda (.txt)");
        chooser.setFileFilter(new FileNameExtensionFilter("Texto (*.txt)", "txt"));

        if (chooser.showOpenDialog(null) != JFileChooser.APPROVE_OPTION) {
            return false;
        }
        File f = chooser.getSelectedFile();
        if (!f.exists() || !f.getName().toLowerCase().endsWith(".txt")) {
            JOptionPane.showMessageDialog(
                null,
                "Seleccione un archivo .txt válido.",
                "Atención",
                JOptionPane.WARNING_MESSAGE
            );
            return false;
        }

        try (BufferedReader br = new BufferedReader(new FileReader(f))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split("=", 2);
                if (parts.length != 2) continue;
                String key = parts[0].trim();
                String val = parts[1].trim();
                switch (key) {
                    case "Codigo":
                        codigo = val; break;
                    case "CedulaCliente":
                        cedulaCliente = val; break;
                    case "CodigoServicio":
                        codigoServicio = val; break;
                    case "FechaCita":
                        try {
                            fechaCita = FORMATO_FECHA.parse(val);
                        } catch (ParseException e) {
                            fechaCita = new Date();
                        }
                        break;
                    case "Cantidad":
                        try {
                            cantidad = Integer.parseInt(val);
                        } catch (NumberFormatException e) {
                            cantidad = 0;
                        }
                        break;
                }
            }
            return true;
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(
                null,
                "Error al leer:\n" + ex.getMessage(),
                "Error I/O",
                JOptionPane.ERROR_MESSAGE
            );
            return false;
        }
    }

    @Override
    public boolean mtd_eliminar() {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Eliminar Agenda (.txt)");
        chooser.setFileFilter(new FileNameExtensionFilter("Texto (*.txt)", "txt"));

        if (chooser.showOpenDialog(null) != JFileChooser.APPROVE_OPTION) {
            return false;
        }
        File f = chooser.getSelectedFile();
        if (!f.exists()) {
            JOptionPane.showMessageDialog(
                null,
                "El archivo no existe.",
                "Atención",
                JOptionPane.WARNING_MESSAGE
            );
            return false;
        }
        if (f.delete()) {
            JOptionPane.showMessageDialog(
                null,
                "Agenda eliminada:\n" + f.getName(),
                "Éxito",
                JOptionPane.INFORMATION_MESSAGE
            );
            return true;
        } else {
            JOptionPane.showMessageDialog(
                null,
                "No se pudo eliminar el archivo.",
                "Error",
                JOptionPane.ERROR_MESSAGE
            );
            return false;
        }
    }
}








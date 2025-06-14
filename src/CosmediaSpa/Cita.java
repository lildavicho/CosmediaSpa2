/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package CosmediaSpa;

import CosmediaSpa.Interfaces.Inter_GAE;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

/**
 * Clase Cita que implementa Inter_GAE.
 * 
 * El método mtd_guardar() mostrará un JFileChooser para que el usuario
 * elija dónde guardar "cita_<codigo>.txt". 
 * El método mtd_buscar() mostrará otro JFileChooser para seleccionar un .txt y cargarlo.
 */
public class Cita extends Validacion implements Inter_GAE {

    private String codigo;          // Código único de la cita
    private String cedulaCliente;   // Cédula del cliente (debe ser válida)
    private String codigoServicio;  // Código del servicio agendado
    private Date   fechaCita;       // Fecha de la cita
    private int    cantidad;        // Cantidad (campo adicional)

    private static final SimpleDateFormat FORMATO_FECHA = new SimpleDateFormat("yyyy-MM-dd");

    public Cita() {
        this.codigo         = "";
        this.cedulaCliente  = "";
        this.codigoServicio = "";
        this.fechaCita      = new Date();
        this.cantidad       = 0;
    }

    public Cita(String codigo, String cedulaCliente, String codigoServicio, Date fechaCita) {
        this.codigo         = codigo;
        this.cedulaCliente  = cedulaCliente;
        this.codigoServicio = codigoServicio;
        this.fechaCita      = fechaCita;
        this.cantidad       = 1;  // valor por defecto si no se setea explícitamente
    }

    // --- Getters / Setters ---
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
     * Validar los datos mínimos antes de guardar:
     *  - código no vacío
     *  - cédula de cliente válida
     *  - código de servicio no vacío
     *  - fecha de cita no nula
     *  - cantidad > 0
     */
    public boolean validarDatos() {
        if (codigo == null || codigo.trim().isEmpty()) {
            return false;
        }
        if (cedulaCliente == null || !Validacion.validarCedula(cedulaCliente)) {
            return false;
        }
        if (codigoServicio == null || codigoServicio.trim().isEmpty()) {
            return false;
        }
        if (fechaCita == null) {
            return false;
        }
        if (cantidad <= 0) {
            return false;
        }
        return true;
    }

    /**
     * Abre un JFileChooser para que el usuario seleccione dónde guardar
     * los datos de la cita en un archivo de texto. El nombre sugerido
     * será "cita_<codigo>.txt", pero el usuario puede cambiar carpeta o nombre.
     * Dentro del TXT se genera algo como:
     *   Codigo=<...>
     *   CedulaCliente=<...>
     *   CodigoServicio=<...>
     *   FechaCita=<YYYY-MM-DD>
     *   Cantidad=<...>
     */
    @Override
    public boolean mtd_guardar() {
        // 1) Si faltan datos obligatorios, no continuar
        if (!validarDatos()) {
            return false;
        }

        // 2) Configurar JFileChooser
        JFileChooser selector = new JFileChooser();
        selector.setDialogTitle("Guardar Cita (.txt)");
        // Sugerir nombre de archivo por defecto:
        selector.setSelectedFile(new File("cita_" + codigo + ".txt"));

        int resultado = selector.showSaveDialog(null);
        if (resultado != JFileChooser.APPROVE_OPTION) {
            return false; // El usuario canceló
        }

        File archivo = selector.getSelectedFile();
        // Asegurarse de que termine con .txt
        if (!archivo.getName().toLowerCase().endsWith(".txt")) {
            archivo = new File(archivo.getAbsolutePath() + ".txt");
        }

        // 3) Escribir línea por línea
        try (FileWriter fw = new FileWriter(archivo, false);
             PrintWriter pw = new PrintWriter(fw)) {

            pw.println("Codigo="         + this.codigo);
            pw.println("CedulaCliente="  + this.cedulaCliente);
            pw.println("CodigoServicio=" + this.codigoServicio);
            pw.println("FechaCita="      + FORMATO_FECHA.format(this.fechaCita));
            pw.println("Cantidad="       + this.cantidad);

            return true;
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(null, "Error al guardar la cita: " + ex.getMessage());
            return false;
        }
    }

    /**
     * Abre un JFileChooser para que el usuario seleccione un archivo
     * "cita_<algo>.txt", lee sus líneas y rellena los atributos de esta instancia.
     */
    @Override
    public boolean mtd_buscar() {
        JFileChooser selector = new JFileChooser();
        selector.setDialogTitle("Buscar Cita (.txt)");
        int resultado = selector.showOpenDialog(null);
        if (resultado != JFileChooser.APPROVE_OPTION) {
            return false; // el usuario canceló
        }

        File archivoElegido = selector.getSelectedFile();
        if (!archivoElegido.exists() || !archivoElegido.getName().toLowerCase().endsWith(".txt")) {
            return false;
        }

        try (FileReader fr = new FileReader(archivoElegido);
             BufferedReader br = new BufferedReader(fr)) {

            String linea;
            while ((linea = br.readLine()) != null) {
                String[] parts = linea.split("=", 2);
                if (parts.length != 2) continue;
                String clave = parts[0].trim();
                String valor = parts[1].trim();

                switch (clave) {
                    case "Codigo":
                        this.codigo = valor;
                        break;
                    case "CedulaCliente":
                        this.cedulaCliente = valor;
                        break;
                    case "CodigoServicio":
                        this.codigoServicio = valor;
                        break;
                    case "FechaCita":
                        try {
                            this.fechaCita = FORMATO_FECHA.parse(valor);
                        } catch (Exception e) {
                            this.fechaCita = new Date();
                        }
                        break;
                    case "Cantidad":
                        try {
                            this.cantidad = Integer.parseInt(valor);
                        } catch (NumberFormatException e) {
                            this.cantidad = 1;
                        }
                        break;
                    default:
                        break;
                }
            }
            return true;
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(null, "Error al leer la cita: " + ex.getMessage());
            return false;
        }
    }

    /**
     * Elimina el archivo "cita_<codigo>.txt" relativo a esta cita.
     */
    @Override
    public boolean mtd_eliminar() {
        if (codigo == null || codigo.trim().isEmpty()) {
            return false;
        }
        File archivo = new File("cita_" + codigo + ".txt");
        if (archivo.exists()) {
            return archivo.delete();
        } else {
            return false;
        }
    }
}

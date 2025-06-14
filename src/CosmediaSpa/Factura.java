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
import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileNameExtensionFilter;

public class Factura extends Validacion implements Inter_GAE {

    private String numeroFactura;
    private Date fecha;
    private Cliente cliente;
    private List<DetalleFactura> detalles;
    private double subtotal;
    private double descuentoPct;
    private double montoDescuento;
    private double ivaPct;
    private double montoIva;
    private double total;

    private File archivoActual = null;

    private static final SimpleDateFormat FORMATO_FECHA
            = new SimpleDateFormat("yyyy-MM-dd");
    private final JFileChooser chooser = new JFileChooser();

    public Factura() {
        this.numeroFactura = "";
        this.fecha = new Date();
        this.cliente = null;
        this.detalles = new ArrayList<>();
        this.descuentoPct = 0;
        this.ivaPct = 0;
        calcularMontos();
    }

    public Factura(String numeroFactura, Date fecha, Cliente cliente,
            List<DetalleFactura> detalles,
            double descuentoPct, double ivaPct) {
        this.numeroFactura = numeroFactura;
        this.fecha = fecha;
        this.cliente = cliente;
        this.detalles = detalles != null ? detalles : new ArrayList<>();
        this.descuentoPct = descuentoPct;
        this.ivaPct = ivaPct;
        calcularMontos();
    }

    public Factura(String numeroFactura, Date fecha, Cliente cliente,
            DetalleFactura[] detallesArray,
            double descuentoPct, double ivaPct) {
        this(numeroFactura, fecha, cliente, Arrays.asList(detallesArray), descuentoPct, ivaPct);
    }

    public String getNumeroFactura() {
        return numeroFactura;
    }

    public Date getFecha() {
        return fecha;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public List<DetalleFactura> getDetalles() {
        return detalles;
    }

    public double getSubtotal() {
        return subtotal;
    }

    public double getDescuentoPct() {
        return descuentoPct;
    }

    public double getMontoDescuento() {
        return montoDescuento;
    }

    public double getIvaPct() {
        return ivaPct;
    }

    public double getMontoIva() {
        return montoIva;
    }

    public double getTotal() {
        return total;
    }

    public void setNumeroFactura(String n) {
        numeroFactura = n;
    }

    public void setFecha(Date d) {
        fecha = d;
    }

    public void setCliente(Cliente c) {
        cliente = c;
    }

    public void setDetalles(List<DetalleFactura> det) {
        detalles = det != null ? det : new ArrayList<>();
        calcularMontos();
    }

    public void setArchivo(File archivo) {
        this.archivoActual = archivo;
    }

    public void calcularMontos() {
        subtotal = detalles.stream().mapToDouble(DetalleFactura::getTotalLinea).sum();
        montoDescuento = subtotal * (descuentoPct / 100.0);
        double base = subtotal - montoDescuento;
        montoIva = base * (ivaPct / 100.0);
        total = base + montoIva;
    }

    public boolean existeRegistro() {
        return new File("factura_" + numeroFactura + ".txt").exists();
    }

    @Override
    public boolean mtd_guardar() {
        if (numeroFactura.trim().isEmpty() || cliente == null || detalles.isEmpty()) {
            JOptionPane.showMessageDialog(null,
                    "Faltan datos (número, cliente o detalle).",
                    "Aviso", JOptionPane.WARNING_MESSAGE);
            return false;
        }

        if (archivoActual == null) {
            archivoActual = new File("factura_" + numeroFactura + ".txt");
        }

        calcularMontos();

        try (PrintWriter pw = new PrintWriter(new FileWriter(archivoActual, false))) {
            pw.println("NumeroFactura=" + numeroFactura);
            pw.println("Fecha=" + FORMATO_FECHA.format(fecha));
            pw.println("Cliente=" + (cliente != null ? cliente.getCedula() : ""));
            pw.println("ClienteCedula=" + cliente.getCedula());
            pw.println("ClienteNombre=" + cliente.getNombre());

            for (DetalleFactura d : detalles) {
                pw.printf("Detalle=%s,%s,%d,%.2f,%.2f%n",
                        d.getCodigoServicio(), d.getDescripcionServicio(),
                        d.getCantidadSesiones(), d.getPrecioUnitario(), d.getTotalLinea());
            }

            pw.println("Subtotal=" + subtotal);
            pw.println("DescuentoPct=" + descuentoPct);
            pw.println("MontoDescuento=" + montoDescuento);
            pw.println("IVA=" + ivaPct);
            pw.println("MontoIVA=" + montoIva);
            pw.println("Total=" + total);

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
        chooser.setDialogTitle("Abrir Factura (.txt)");
        chooser.setFileFilter(new FileNameExtensionFilter("*.txt", "txt"));
        if (chooser.showOpenDialog(null) != JFileChooser.APPROVE_OPTION) {
            return false;
        }

        File f = chooser.getSelectedFile();
        if (!f.exists() || !f.getName().toLowerCase().endsWith(".txt")) {
            JOptionPane.showMessageDialog(null,
                    "Seleccione un archivo .txt válido.",
                    "Advertencia", JOptionPane.WARNING_MESSAGE);
            return false;
        }

        archivoActual = f;
        return cargarDesdeArchivo(f);
    }

    @Override
    public boolean mtd_eliminar() {
        chooser.setDialogTitle("Eliminar Factura (.txt)");
        chooser.setFileFilter(new FileNameExtensionFilter("*.txt", "txt"));
        if (chooser.showOpenDialog(null) != JFileChooser.APPROVE_OPTION) {
            return false;
        }

        File f = chooser.getSelectedFile();
        if (!f.exists()) {
            JOptionPane.showMessageDialog(null,
                    "El archivo no existe.",
                    "Advertencia", JOptionPane.WARNING_MESSAGE);
            return false;
        }

        boolean eliminado = f.delete();
        if (eliminado && f.equals(archivoActual)) {
            archivoActual = null;
        }

        JOptionPane.showMessageDialog(null,
                eliminado ? "Factura eliminada:\n" + f.getName()
                        : "No se pudo eliminar el archivo.",
                eliminado ? "Éxito" : "Error",
                eliminado ? JOptionPane.INFORMATION_MESSAGE : JOptionPane.ERROR_MESSAGE);

        return eliminado;
    }

    public boolean cargarDesdeArchivo(File f) {
    List<DetalleFactura> lista = new ArrayList<>();
    try (BufferedReader br = new BufferedReader(new FileReader(f))) {
        String line;
        while ((line = br.readLine()) != null) {
            String[] kv = line.split("=", 2);
            if (kv.length != 2) continue;
            String key = kv[0].trim();
            String val = kv[1].trim();

            switch (key) {
                case "NumeroFactura":
                    this.numeroFactura = val;
                    break;
                case "Fecha":
                    try {
                        this.fecha = FORMATO_FECHA.parse(val);
                    } catch (ParseException e) {
                        this.fecha = new Date();
                    }
                    break;
                case "ClienteCedula":
                    if (this.cliente == null) this.cliente = new Cliente();
                    this.cliente.setCedula(val);
                    break;
                case "ClienteNombre":
                    if (this.cliente == null) this.cliente = new Cliente();
                    this.cliente.setNombre(val);
                    break;
                case "Detalle":
                    // Formato: Detalle=codigo,descripcion,cantidad,precioUnitario,totalLinea
                    String[] campos = val.split(",", 5);
                    if (campos.length == 5) {
                        DetalleFactura d = new DetalleFactura(
                            campos[0],
                            campos[1],
                            Integer.parseInt(campos[2]),
                            Double.parseDouble(campos[3])
                        );
                        // totalLinea se recalcula internamente
                        lista.add(d);
                    }
                    break;
                case "Subtotal":
                    this.subtotal = Double.parseDouble(val);
                    break;
                case "DescuentoPct":
                    this.descuentoPct = Double.parseDouble(val);
                    break;
                case "MontoDescuento":
                    this.montoDescuento = Double.parseDouble(val);
                    break;
                case "IVA":
                    this.ivaPct = Double.parseDouble(val);
                    break;
                case "MontoIVA":
                    this.montoIva = Double.parseDouble(val);
                    break;
                case "Total":
                    this.total = Double.parseDouble(val);
                    break;
                default:
                    // Ignorar cualquier otra clave
                    break;
            }
        }
        this.detalles = lista;
        return true;
    } catch (IOException ex) {
        JOptionPane.showMessageDialog(
            null,
            "Error al leer factura:\n" + ex.getMessage(),
            "Error I/O",
            JOptionPane.ERROR_MESSAGE
        );
        return false;
    }
}
}

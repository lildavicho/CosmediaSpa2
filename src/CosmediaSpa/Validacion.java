/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package CosmediaSpa;

/**
 *
 * @author vicom
 */


public abstract class Validacion {
    // --- Atributos de instancia (opcional) ---
    private String cedula;
    private String email;
    private String telefono;

    // ----------------------------
    //  Constructores
    // ----------------------------
    public Validacion() {
        this.cedula   = "";
        this.email    = "";
        this.telefono = "";
    }

    public Validacion(String cedula, String email, String telefono) {
        this.cedula   = cedula;
        this.email    = email;
        this.telefono = telefono;
    }

    // ----------------------------
    //  Getters y Setters
    // ----------------------------
    public String getCedula() {
        return cedula;
    }

    public void setCedula(String cedula) {
        this.cedula = cedula;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    // ----------------------------
    //  Validadores de instancia (opcional)
    //  (llaman internamente a los métodos estáticos)
    // ----------------------------
    public boolean validarCedula() {
        return validarCedula(this.cedula);
    }

    public boolean validarEmail() {
        return validarEmail(this.email);
    }

    public boolean validarTelefono() {
        return validarTelefono(this.telefono);
    }

    // ----------------------------
    //  Métodos estáticos de validación
    //  (puedes invocarlos directamente como Validacion.validarXXX(...))
    // ----------------------------

    /**
     * Valida que la cédula ecuatoriana tenga 10 dígitos, 
     * el tercer dígito < 6 y el dígito verificador calculado correctamente.
     */
    public static boolean validarCedula(String ced) {
        if (ced == null || ced.length() != 10) {
            return false;
        }
        // Todos deben ser dígitos
        for (char c : ced.toCharArray()) {
            if (!Character.isDigit(c)) {
                return false;
            }
        }
        // Provincia entre 01 y 24
        int prov = Integer.parseInt(ced.substring(0, 2));
        if (prov < 1 || prov > 24) {
            return false;
        }
        // Tercer dígito < 6
        int tercerDigito = Character.getNumericValue(ced.charAt(2));
        if (tercerDigito >= 6) {
            return false;
        }
        // Cálculo del dígito verificador según coeficientes {2,1,2,1,2,1,2,1,2}
        int[] coeficientes = {2, 1, 2, 1, 2, 1, 2, 1, 2};
        int suma = 0;
        for (int i = 0; i < 9; i++) {
            int dig = Character.getNumericValue(ced.charAt(i));
            int producto = dig * coeficientes[i];
            if (producto > 9) {
                producto -= 9;
            }
            suma += producto;
        }
        int modulo = suma % 10;
        int digitoVerificador = (modulo == 0) ? 0 : 10 - modulo;
        int ultimoDigito = Character.getNumericValue(ced.charAt(9));
        return digitoVerificador == ultimoDigito;
    }

    /**
     * Valida que el email no sea null ni vacío, 
     * que contenga un solo '@', que no haya espacios,
     * y que exista al menos un '.' después del '@' sin estar al final.
     */
    public static boolean validarEmail(String mail) {
        if (mail == null || mail.isEmpty()) {
            return false;
        }
        // No puede contener espacios
        if (mail.contains(" ")) {
            return false;
        }
        int posArroba = mail.indexOf('@');
        // '@' no puede estar en primera ni última posición
        if (posArroba <= 0 || posArroba == mail.length() - 1) {
            return false;
        }
        // No puede haber un segundo '@'
        if (mail.indexOf('@', posArroba + 1) != -1) {
            return false;
        }
        // Debe haber un '.' después del '@' y no en última posición
        int posPunto = mail.indexOf('.', posArroba + 1);
        if (posPunto <= posArroba + 1 || posPunto == mail.length() - 1) {
            return false;
        }
        return true;
    }

    /**
     * Valida que el teléfono tenga entre 7 y 10 dígitos y que solo contenga números.
     */
    public static boolean validarTelefono(String tel) {
        if (tel == null || tel.isEmpty()) {
            return false;
        }
        if (tel.length() < 7 || tel.length() > 10) {
            return false;
        }
        for (char c : tel.toCharArray()) {
            if (!Character.isDigit(c)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Helper estático: devuelve true si la cadena s está compuesta únicamente por dígitos.
     * (útil para validaciones genéricas de “solo números”)
     */
    public static boolean esSoloDigitos(String s) {
        if (s == null || s.isEmpty()) {
            return false;
        }
        for (char c : s.toCharArray()) {
            if (!Character.isDigit(c)) {
                return false;
            }
        }
        return true;
    }
}

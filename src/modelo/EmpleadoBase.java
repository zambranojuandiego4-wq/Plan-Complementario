package modelo;

/**
 * Representa a un empleado general (operativo) de la empresa.
 * Es la clase "padre" de la jerarquía de empleados.
 */
public class EmpleadoBase {
    // Atributos PRIVADOS: solo esta clase puede tocarlos directamente
    private final String cedula; // final: la cédula nunca cambia
    private String nombre;
    private double salarioBase;

    // Constructor: se ejecuta automáticamente al escribir "new EmpleadoBase(...)"
    public EmpleadoBase(String cedula, String nombre, double salarioBase) {
        this.cedula = cedula;
        this.nombre = nombre;
        setSalarioBase(salarioBase); // reutilizamos la validación del setter
    }

    // ---------- Getters: permiten LEER los datos ----------
    public String getCedula() {
        return cedula;
    }

    public String getNombre() {
        return nombre;
    }

    public double getSalarioBase() {
        return salarioBase;
    }

    // ---------- Setter: permite MODIFICAR, pero con reglas ----------
    public void setSalarioBase(double salarioBase) {
        if (salarioBase >= 0) {
            this.salarioBase = salarioBase;
        } else {
            this.salarioBase = 0; // nunca aceptamos salarios negativos
        }
    }

    // Métodos que las clases hijas podrán SOBRESCRIBIR (polimorfismo)
    public double calcularSalarioTotal() {
        return salarioBase;
    }

    public String getTipo() {
        return "Operativo";
    }
}

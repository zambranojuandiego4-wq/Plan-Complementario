package modelo;

/**
 * Un empleado administrativo ES UN EmpleadoBase, pero además recibe una bonificación.
 */
public class EmpleadoAdministrativo extends EmpleadoBase {
    private double bonificacion; // atributo exclusivo de la clase hija

    public EmpleadoAdministrativo(String cedula, String nombre,
                                  double salarioBase, double bonificacion) {
        super(cedula, nombre, salarioBase); // llama al constructor del padre
        this.bonificacion = bonificacion;
    }

    public double getBonificacion() {
        return bonificacion;
    }

    @Override
    public double calcularSalarioTotal() {
        // Reutilizamos el cálculo del padre y le sumamos la bonificación
        return super.calcularSalarioTotal() + bonificacion;
    }

    @Override
    public String getTipo() {
        return "Administrativo";
    }
}

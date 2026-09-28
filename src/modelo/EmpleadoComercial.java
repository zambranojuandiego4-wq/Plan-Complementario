package modelo;

/**
 * Un empleado comercial ES UN EmpleadoBase, pero además recibe una comisión porcentual.
 */
public class EmpleadoComercial extends EmpleadoBase {
    private double porcentajeComision; // atributo exclusivo de la clase hija

    public EmpleadoComercial(String cedula, String nombre,
                             double salarioBase, double porcentajeComision) {
        super(cedula, nombre, salarioBase); // llama al constructor del padre
        this.porcentajeComision = porcentajeComision;
    }

    public double getPorcentajeComision() {
        return porcentajeComision;
    }

    @Override
    public double calcularSalarioTotal() {
        // Salario base más la comisión porcentual sobre ese salario
        return getSalarioBase() + (getSalarioBase() * (porcentajeComision / 100.0));
    }

    @Override
    public String getTipo() {
        return "Comercial";
    }
}

// Paso 1: Definición de la Interfaz Figura y Clases Anidadas
interface Figura {
    double calcularArea();
    double calcularPerimetro();

    class Circulo implements Figura {
        private double radio;

        public Circulo(double radio) {
            this.radio = radio;
        }

        @Override
        public double calcularArea() {
            return Math.PI * radio * radio;
        }

        @Override
        public double calcularPerimetro() {
            return 2 * Math.PI * radio;
        }
    }

    class Rectangulo implements Figura {
        private double base;
        private double altura;

        public Rectangulo(double base, double altura) {
            this.base = base;
            this.altura = altura;
        }

        @Override
        public double calcularArea() {
            return base * altura;
        }

        @Override
        public double calcularPerimetro() {
            return 2 * (base + altura);
        }
    }
}

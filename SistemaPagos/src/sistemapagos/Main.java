package sistemapagos;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class Main {
    private static final Scanner in = new Scanner(System.in);
    private static final ServicioPagos servicio = new ServicioPagos(new RepositorioEnMemoria()); //Instancia de la clase que nos permite guardar datos en memoria.

    public static void main(String[] args) {
        //========= Flujo principal =========
        System.out.println("=== SISTEMA DE PROCESAMIENTO DE PAGOS ===");
        boolean salir = false;
        while (!salir) {
            mostrarMenu();
            String opcion = leerLinea("Opcion: ");
            switch (opcion) {
                case "1": registrarVenta(); break;
                case "2": procesarPago(); break;
                case "3": consultarTransacciones(); break;
                case "4": System.out.println("\n" + servicio.generarResumen()); break; // Lllamamos a la clase RepositorioEnMemoria para darnos la info almacenada           
                case "0": salir = true; break;
                default: System.out.println("Opcion no valida.");
            }
        }
        System.out.println("Hasta pronto.");
    }

    //========= Menu =========
    private static void mostrarMenu() {
        System.out.println("\n--- MENU ---");
        System.out.println("1. Registrar venta");
        System.out.println("2. Procesar pago de una venta");
        System.out.println("3. Consultar transacciones");
        System.out.println("4. Generar resumen de pagos");
        System.out.println("0. Salir");
    }
    //========= Registro de venta. =========
    private static void registrarVenta() {
        Map<String, Double> items = new LinkedHashMap<>(); //Almacena <descripcion, precio> de los items
        System.out.println("Ingrese los items (deje la descripcion vacia para terminar)."); 
        while (true) {
            String descripcion = leerLinea("  Descripcion: ");
            if (descripcion.isEmpty()) { //Si no hay descripcion, rompe ciclo, sigue la siguiente linea.
                break;
            }
            double precio = leerDouble("  Precio: ");
            items.put(descripcion, precio);
        }
        if (items.isEmpty()) { //Si no hay items agregados, regresa al menu
            System.out.println("Venta cancelada: no se ingresaron items.");
            return;
        }
        Venta venta = servicio.registrarVenta(items); //Agrega venta a la lista que se esta almacenando en memoria
        System.out.println("Venta registrada -> " + venta);
    }
    ////========= Proceso de metodo de pago =========
    private static void procesarPago() {
        List<Venta> ventas = servicio.listarVentas(); // listamos las ventas realizadas
        boolean hayPendientes = false; 
        System.out.println("\nVentas pendientes de pago:"); 
        for (Venta v : ventas) { // Verificamos si en las ventas, hay alguna que este pendiente a ser pagada, de este modo la variable hayPendientes cambia a TRUE
            if (!v.isPagada()) {
                System.out.println("  " + v); //Mostramos las ventas pendientes
                hayPendientes = true;
            }
        }
        if (!hayPendientes) {
            System.out.println("  (no hay ventas pendientes)"); //Si no hay ventas pendientes, regresamos al menu
            return;
        }

        String idVenta = leerLinea("ID de la venta (ej. V-001): "); // Mostramos el formato de venta que deseeamos y leemos input
        Venta venta = null; //no crearemos otra instancia, solo usaremos este objeto
        for (Venta v : ventas) {
            if (v.getId().equalsIgnoreCase(idVenta) && !v.isPagada()) { //buscamos si el id de la venta se encuentra en los que tenemos almacenados
                venta = v; //venta encontrada. Almacenamos esta en nuestro objeto recien creado
            }
        }
        if (venta == null) {
            System.out.println("Venta no encontrada o ya pagada."); //Si no se encuentra venta, regresamos al menu 
            return;
        }

       
        System.out.println("\nMetodo de pago:"); // Seleccion de metodo de pago
        System.out.println("  1. Efectivo");
        System.out.println("  2. Tarjeta");
        System.out.println("  3. Transferencia bancaria");
        System.out.println("  4. Pago electronico");
        System.out.println("  5. Criptomoneda");
        MetodoPago metodo = crearMetodo(leerLinea("Opcion: ")); // Usaremos el metodo seleccionado
        if (metodo == null) {
            System.out.println("Metodo no valido."); // Metodo no valido
            return;
        }

        mostrarResultado(servicio.procesarPago(venta, metodo)); // Creamos transaccion. Mostramos resultado de la transaccion
    }

    // //========= Creacion de cada metodo de pago =========
    private static MetodoPago crearMetodo(String opcion) {
        switch (opcion) {
            case "1":
                return new PagoEfectivo(leerDouble("Monto recibido: "));
            case "2":
                return new PagoTarjeta( //ctrl + click izq sobre la instancia para ver el funcionamiento de clase
                        leerLinea("Numero de tarjeta (16 digitos): "),
                        leerLinea("Tipo (CREDITO/DEBITO): "));
            case "3":
                return new PagoTransferencia( //ctrl + click izq sobre la instancia para ver el funcionamiento de clase
                        leerLinea("Cuenta de origen (min. 8 caracteres): "), 
                        leerLinea("Referencia (min. 6 caracteres): "));
            case "4":
                return new PagoElectronico( //ctrl + click izq sobre la instancia para ver el funcionamiento de clase
                        leerLinea("Proveedor (ej. PayFast): "),
                        leerLinea("Token (debe iniciar con tok_): "));
            case "5":
                return new PagoCripto( //ctrl + click izq sobre la instancia para ver el funcionamiento de clase
                        leerLinea("Billetera (min. 10 caracteres): "),
                        leerLinea("Moneda (BTC/ETH/USDT): "));
            default:
                return null;
        }
    }

    //========= Mostramos resultado de transaccion, metodo ocupado en procesarPago() =========
    private static void mostrarResultado(Transaccion t) { //Metodo recibe la transaccion recien creada
        System.out.println("\nResultado del pago:");
        System.out.println("  " + t);
        if (t.getEstado() == EstadoPago.APROBADO) { //Obtenemos estado y decidimos que mostrar dependiendo
            System.out.println("  >> Pago APROBADO."); 
        } else {
            System.out.println("  >> Pago RECHAZADO. La venta sigue pendiente; puede reintentar.");
        }
    }
    //========= Lista de transacciones (APROBADAS Y RECHAZADAS) =========
    private static void consultarTransacciones() {
        List<Transaccion> lista = servicio.consultarTransacciones();
        System.out.println("\n===== TRANSACCIONES =====");
        if (lista.isEmpty()) {
            System.out.println("(no hay transacciones)");
            return;
        }
        for (Transaccion t : lista) {
            System.out.println(t); // Listamos las transacciones
        }
    }
    
    //========= Metodo para leer caracteres =========
    private static String leerLinea(String mensaje) {
        System.out.print(mensaje);
        return in.hasNextLine() ? in.nextLine().trim() : "0"; //Metodo para leer removiendo espacios, si no se escribe nada, retorna "0" (Util para el menu principal)
    }

    //========= Metodo para leer numeros =========
    private static double leerDouble(String mensaje) {
        while (true) {
            String texto = leerLinea(mensaje);
            try {
                double valor = Double.parseDouble(texto.replace(',', '.')); //Remplazar comas (,) por puntos (.)
                if (valor >= 0) { //Solo se aceptan numero enteros mayores a cero
                    return valor;
                }
            } catch (NumberFormatException e) { // Catch alguno formato de numero no correcto y se vuelve a pedir el dato
                
            }
            System.out.println("  Ingrese un numero valido (mayor o igual a 0).");
        }
    }
}

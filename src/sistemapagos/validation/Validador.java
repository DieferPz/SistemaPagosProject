package sistemapagos.validation;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.regex.Pattern;
import sistemapagos.exception.ValidacionException;

/** Validaciones comunes (RF-04). Todos los mensajes orientan al usuario sobre cómo corregir. */
public final class Validador {
    private static final Pattern EMAIL = Pattern.compile("^[\\w.+-]+@[\\w-]+(\\.[\\w-]+)+$");

    private Validador() { }

    /** Campo obligatorio: no nulo, no vacío, no solo espacios. Devuelve el texto sin espacios sobrantes. */
    public static String requerirTexto(String valor, String campo) {
        if (valor == null || valor.trim().isEmpty()) {
            throw new ValidacionException("El campo \"" + campo + "\" es obligatorio.");
        }
        return valor.trim();
    }

    /** Texto con longitud máxima (coincide con el VARCHAR de la base). */
    public static String requerirTexto(String valor, String campo, int maxLongitud) {
        String limpio = requerirTexto(valor, campo);
        if (limpio.length() > maxLongitud) {
            throw new ValidacionException("El campo \"" + campo + "\" no puede superar " + maxLongitud + " caracteres.");
        }
        return limpio;
    }

    /** Importe numérico > 0, máximo 2 decimales. Acepta coma o punto decimal. */
    public static BigDecimal parsearImporte(String texto, String campo) {
        String limpio = requerirTexto(texto, campo).replace(',', '.');
        BigDecimal valor;
        try {
            valor = new BigDecimal(limpio);
        } catch (NumberFormatException e) {
            throw new ValidacionException("El campo \"" + campo + "\" debe ser un número válido (ej. 150.50).");
        }
        return requerirMayorQueCero(valor, campo);
    }

    public static BigDecimal requerirMayorQueCero(BigDecimal valor, String campo) {
        if (valor == null || valor.signum() <= 0) {
            throw new ValidacionException("El campo \"" + campo + "\" debe ser mayor que cero.");
        }
        if (valor.scale() > 2 && valor.stripTrailingZeros().scale() > 2) {
            throw new ValidacionException("El campo \"" + campo + "\" admite como máximo 2 decimales.");
        }
        if (valor.precision() - valor.scale() > 10) {
            throw new ValidacionException("El campo \"" + campo + "\" es demasiado grande.");
        }
        return valor.setScale(2, java.math.RoundingMode.HALF_UP);
    }

    /** Fecha con formato AAAA-MM-DD. */
    public static LocalDate parsearFecha(String texto, String campo) {
        String limpio = requerirTexto(texto, campo);
        try {
            return LocalDate.parse(limpio);
        } catch (DateTimeParseException e) {
            throw new ValidacionException("El campo \"" + campo + "\" debe tener formato AAAA-MM-DD (ej. 2026-11-12).");
        }
    }

    public static String requerirEmail(String valor, String campo) {
        String limpio = requerirTexto(valor, campo);
        if (!EMAIL.matcher(limpio).matches()) {
            throw new ValidacionException("El campo \"" + campo + "\" debe ser un correo válido.");
        }
        return limpio;
    }
}

package org.example.screen;

import org.example.Connection;
import org.example.screen.components.Buttons;
import org.example.screen.components.PanelResult;
import org.example.screen.components.TextField;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.datatransfer.StringSelection;

/** Ventana principal: arma los componentes y conecta la interfaz con el motor Prolog. */
public class Window extends JFrame {

    private final TextField txtA = new TextField("Ej: 5");
    private final TextField txtB = new TextField("Ej: 3");
    private final PanelResult panelResultado = new PanelResult();
    private final JLabel lblEstado = new JLabel(" ");

    public Window(Connection motor) {
        super("Calculadora básica en Prolog");

        // Enter en el campo B = sumar
        txtB.addActionListener(e -> ejecutar(motor, "suma", "Suma"));

        JPanel contenido = new JPanel();
        contenido.setLayout(new BoxLayout(contenido, BoxLayout.Y_AXIS));
        contenido.setOpaque(false);
        agregar(contenido, etiqueta("Número A"), 6);
        agregar(contenido, txtA, 12);
        agregar(contenido, etiqueta("Número B"), 6);
        agregar(contenido, txtB, 18);
        agregar(contenido, crearBotones(motor), 22);
        agregar(contenido, panelResultado, 10);
        agregar(contenido, crearPie(), 0);

        JPanel raiz = new JPanel(new BorderLayout(0, 20));
        raiz.setBackground(Theme.BG);
        raiz.setBorder(new EmptyBorder(24, 28, 24, 28));
        raiz.add(crearEncabezado(), BorderLayout.NORTH);
        raiz.add(contenido, BorderLayout.CENTER);

        setContentPane(raiz);
        setMinimumSize(new Dimension(660, 560));
        pack();
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
    }

    // ------------------------------------------------------------------
    //  Construcción de secciones
    // ------------------------------------------------------------------

    private JPanel crearEncabezado() {
        JLabel titulo = new JLabel("Calculadora básica");
        titulo.setFont(Theme.sans(Font.BOLD, 24));
        titulo.setForeground(Theme.TEXT);

        JLabel subtitulo = new JLabel("Suma, resta, multiplicación y división resueltas con reglas en Prolog");
        subtitulo.setFont(Theme.sans(Font.PLAIN, 13));
        subtitulo.setForeground(Theme.MUTED);

        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setOpaque(false);
        p.add(titulo);
        p.add(Box.createVerticalStrut(4));
        p.add(subtitulo);
        return p;
    }

    private JPanel crearBotones(Connection motor) {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        p.setOpaque(false);
        p.add(crearBoton("+ Sumar", Theme.BLUE, motor, "suma", "Suma"));
        p.add(crearBoton("− Restar", Theme.GREEN, motor, "resta", "Resta"));
        p.add(crearBoton("× Multiplicar", Theme.AMBER, motor, "multiplicacion", "Multiplicación"));
        p.add(crearBoton("÷ Dividir", Theme.RED, motor, "division", "División"));
        return p;
    }

    private Buttons crearBoton(String texto, Color color, Connection motor,
                               String operacion, String nombre) {
        Buttons b = new Buttons(texto, color, Theme.BG, 20, 11, 14);
        b.addActionListener(e -> ejecutar(motor, operacion, nombre));
        return b;
    }

    private JPanel crearPie() {
        lblEstado.setFont(Theme.sans(Font.PLAIN, 12));
        lblEstado.setForeground(Theme.MUTED);

        Buttons btnCopiar = new Buttons("Copiar", Theme.CARD, Theme.TEXT, 16, 7, 12);
        btnCopiar.addActionListener(e -> copiarResultado());

        JPanel p = new JPanel(new BorderLayout());
        p.setOpaque(false);
        p.add(lblEstado, BorderLayout.WEST);
        p.add(btnCopiar, BorderLayout.EAST);
        return p;
    }

    // ------------------------------------------------------------------
    //  Lógica
    // ------------------------------------------------------------------

    private void ejecutar(Connection motor, String operacion, String nombre) {
        panelResultado.setTitulo(nombre);

        if (txtA.getText().trim().isEmpty() || txtB.getText().trim().isEmpty()) {
            mostrar("", "Escribí los dos números primero.", Theme.AMBER);
            return;
        }
        // Validar en Java que ambas entradas sean números ANTES de armar la consulta
        String a = aNumeroProlog(txtA.getText());
        String b = aNumeroProlog(txtB.getText());
        if (a == null || b == null) {
            mostrar("", "Ingresá dos números válidos (ej: 5, -3, 2.5).", Theme.AMBER);
            return;
        }
        try {
            String resultado = motor.consultar(operacion, a, b);
            if (resultado.startsWith("Error")) {
                mostrar("", resultado, Theme.RED);
            } else if (resultado.startsWith("Sin solución")) {
                mostrar("", resultado, Theme.AMBER);
            } else {
                mostrar(resultado, "Listo", Theme.GREEN);
            }
        } catch (Exception ex) {
            mostrar("", "Expresión inválida: " + ex.getMessage(), Theme.RED);
        }
    }

    /**
     * Convierte el texto a un número escrito en sintaxis Prolog,
     * o devuelve null si no es un número válido.
     */
    private static String aNumeroProlog(String texto) {
        double valor;
        try {
            valor = Double.parseDouble(texto.trim());
        } catch (NumberFormatException ex) {
            return null;
        }
        if (Double.isNaN(valor) || Double.isInfinite(valor)) {
            return null;
        }
        // Los enteros se envían sin ".0" para que 5 + 3 muestre 8 y no 8.0
        if (valor == Math.rint(valor) && Math.abs(valor) < 1e15) {
            return String.valueOf((long) valor);
        }
        return String.valueOf(valor);
    }

    private void mostrar(String resultado, String estado, Color colorEstado) {
        panelResultado.setTexto(resultado);
        lblEstado.setText(estado);
        lblEstado.setForeground(colorEstado);
    }

    private void copiarResultado() {
        String t = panelResultado.getTexto();
        if (t.isEmpty()) {
            return;
        }
        Toolkit.getDefaultToolkit().getSystemClipboard()
                .setContents(new StringSelection(t), null);
        lblEstado.setText("Copiado al portapapeles");
        lblEstado.setForeground(Theme.BLUE);
    }

    // ------------------------------------------------------------------
    //  Utilidades
    // ------------------------------------------------------------------

    private static void agregar(JPanel panel, JComponent c, int espacioDespues) {
        c.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(c);
        if (espacioDespues > 0) {
            panel.add(Box.createVerticalStrut(espacioDespues));
        }
    }

    private static JLabel etiqueta(String texto) {
        JLabel l = new JLabel(texto);
        l.setFont(Theme.sans(Font.BOLD, 12));
        l.setForeground(Theme.MUTED);
        return l;
    }
}

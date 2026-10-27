package org.example;

import alice.tuprolog.Prolog;
import alice.tuprolog.SolveInfo;
import alice.tuprolog.Struct;
import alice.tuprolog.Term;
import alice.tuprolog.Theory;

import java.io.InputStream;

public class Connection {
    private final Prolog engine = new Prolog();

    public Connection() throws Exception {
        try (InputStream in = getClass().getResourceAsStream("/calculo.pl")) {
            if (in == null) throw new IllegalStateException("No se encontró calculo.pl");
            engine.setTheory(new Theory(in));
        }
    }

    /** Arma la consulta "operacion(A, B, R)." y devuelve el valor de R calculado por Prolog. */
    public String consultar(String operacion, String a, String b) throws Exception {
        SolveInfo info = engine.solve(operacion + "(" + a + ", " + b + ", R).");
        if (!info.isSuccess()) {
            return "Sin solución para esa operación";
        }
        Term r = info.getTerm("R");
        // Si Prolog devuelve un átomo (mensaje de error) se toma su texto sin comillas
        return r.isAtom() ? ((Struct) r).getName() : r.toString();
    }
}

% Calculadora basica: una regla por operacion.
% Se valida con number/1 que los operandos sean numeros
% y se evalua con is/2 (= solo unificaria, sin calcular).

% Suma
suma(A, B, R) :-
    number(A), number(B), R is A + B.

% Resta
resta(A, B, R) :-
    number(A), number(B), R is A - B.

% Multiplicacion
multiplicacion(A, B, R) :-
    number(A), number(B), R is A * B.

% Division entre cero: va ANTES de la regla general.
% El corte (!) evita que luego se intente la division normal.
division(A, B, 'Error: division entre cero') :-
    number(A), number(B), B =:= 0, !.

% Division (float/1 evita la division entera de tuProlog: 9 / 2 = 4.5, no 4)
division(A, B, R) :-
    number(A), number(B), R is float(A) / B.

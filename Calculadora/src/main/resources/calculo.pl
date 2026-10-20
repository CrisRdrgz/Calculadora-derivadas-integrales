% Derivada de una constante
d(Var, Func, 0) :-
atomic(Var), number(Func).
% Derivada de una variable con respecto a si misma
d(Var, Func, 1) :-
Var == Func.
% Derivada de una variable por una constante
d(Var, N*Var, N) :-
atomic(Var), number(N).
% Derivada de una variable elevada a la N
d(Var, Var^N, N*Var^Nm1) :-
number(N), is(Nm1, N - 1).
% Derivada de una variable elevada a la N por una constante
d(Var, K*Var^N, C*Var^Nm1) :-
atomic(Var), number(K), number(N), is(C, K*N), is(Nm1, N-1).
% Derivada de un logaritmo natural
d(Var, log(Var), 1/Var) :-
atomic(Var).
% Integral de una constante
s(Var, K, K*Var) :-
number(K), atomic(Var).
% Integral de una variable con respecto a si misma
s(Var, Var, 1/2*Var^2) :-
atomic(Var).
%% Integral de una constante por una variable elevada a la N
s(Var, K*Var^N, K/M*Var^M) :-
atomic(Var), number(K), number(N), is(M, N+1).
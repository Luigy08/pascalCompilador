procedure Acceso;
var
  clave: string;                         (* Esta variable es local *)
begin
  writeln(' Bienvenido a SuperAgenda ');
  writeln('=========================='); (* Para subrayar *)
  writeln; writeln;                      (* Dos líneas en blanco *)
  writeln('Introduzca su clave de acceso');
  readln( clave );                      (* Lee un valor *)
  if clave <> ClaveCorrecta then        (* Compara con el correcto *)
    begin                               (* Si no lo es *)

    writeln('La clave no es correcta!'); (* avisa y *)
    exit                                 (* abandona el programa *)
    end
end;
program hola;
var 
    base, exponente,res: integer;

function potencia(base : integer; exponente : integer) : integer;

var
   pot      : integer;
   i        : integer;
begin
   pot:= 1;
   for i:= 1 to exponente do
    begin
      pot:= pot * base;
    end;
    potencia:= pot;
end; 
begin
    base:=2;
    exponente:=3;
    res:=potencia(base,exponente);
    write(res);
end.

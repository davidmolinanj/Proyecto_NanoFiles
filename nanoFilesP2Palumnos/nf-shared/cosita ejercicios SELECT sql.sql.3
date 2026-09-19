/*
Asignatura: BASES DE DATOS 
Curso: 2025/26
Convocatoria: enero

Practica: P2. Consultas SQL

Usuario Oracle: bd3303  <-- TU CUENTA DE USUARIO AQUI
Estudiante: Gabriel Benavides Muñoz            <-- NOMBRE Y APELLIDOS

*/

--Datos traducidos:

/*GARAJE (codigo, nombre, direccion)
PK : codigo



COCHE (matricula, marca, modelo, color, garaje, precio_alquiler)
Clave ajena: garaje → GARAJE(codigo)
PK : matricula



VIAJERO (DNI, codigo, nombre, direccion, ciudad, telefono, fecha_registro)
PK: codigo
UNIQUE: DNI


AGENCIA (codigo, zona )
PK: codigo



ALQUILER (alquiler_id, fecha_inicio, fecha_fin, viajero, agencia,
 coste_total, cerrado)
PK: alquiler_id

Claves ajenas: 
viajero → VIAJERO(codigo)
agencia → AGENCIA(codigo)

Comprobar: cerrado IN (‘N’, ‘S’)



DETALLE_ALQUILER (alquiler, coche, litros_inicio, coste_coche)
PK: (alquiler, coche)

Claves ajenas:
alquiler → ALQUILER(alquiler_id)
coche → COCHE(matricula)

Comprobar: litros_inicio BETWEEN 25 AND 100*/




-- CONSULTAS:

/*S1.1. [F] Marca, modelo y color de los coches cuyo precio de alquiler sea superior a 70 euros
(marca, modelo, color).*/

SELECT  C.marca, C.modelo, C.color 
FROM    COCHE C
WHERE   (precio_alquiler > 70);


/*
S1.2. [F] El código, nombre y dirección de los viajeros cuya dirección contiene la palabra 'RO',
ordenado por nombre de viajero (codigo, nombre, direccion).*/

SELECT  V.codigo, V.nombre, V.direccion
FROM    viajero V
WHERE   direccion LIKE '%RO%'
ORDER BY V.nombre ASC;


/*
S1.3. [F] Alquileres cuyo coste total está entre 600 y 1200€ (fecha_inicio, agencia,
viajero, coste_total), ordenado por agencia y por viajero.*/
SELECT *
FROM    alquiler Q
WHERE   (Q.coste_total >= 600 AND Q.coste_total <= 1200)
ORDER BY Q.agencia, Q.viajero;

/*
S1.4. [F] Agencias involucradas en alquileres aún sin cerrar y cuya fecha de inicio es anterior al
01/04/2025. Sin duplicados. Ordenado descendentemente (agencia).*/

SELECT DISTINCT Q.agencia
FROM alquiler Q   
WHERE ((Q.cerrado = 'N') AND (Q.fecha_inicio < TO_DATE('01/04/2025', 'dd/mm/yyyy')))
ORDER BY Q.agencia DESC;


/*
S1.5. [F] Para cada viajero cuyo nombre completo incluye el texto 'ME', mostrar su nombre
completo, teléfono y el mes (no la fecha, sino el mes en letras mayúsculas) en el que fue
registrado. (nombre, telefono, mes_registro). Ordenado por nombre.*/

SELECT  V.nombre, V.telefono, TO_CHAR(EXTRACT (MONTH from fecha_registro)) mes_ingreso
FROM    viajero V
WHERE   V.nombre LIKE '%ME%'
ORDER BY V.nombre;







/*
S1.6. [F] Alquileres ya cerrados tales que o bien corresponden a la agencia 'A1', o bien están
solicitados por el viajero 'V14' y tienen un coste total inferior a 300€. Ordenado por fecha
de finalización. (alquiler_id, fecha_fin, viajero, agencia, coste_total).
S1.7. [F] Coches asignados al garaje ‘G1’ o al ‘G5’ de un color distinto al 'PLATA', mostrando
en una sola columna la marca y el modelo (matricula, marca_modelo, color,
garaje), ordenado por marca_modelo.
S1.8. [F] Para cada alquiler ya cerrado, cuyo coste total es superior a 500€, mostrar cómo quedaría
su coste si se disminuyera un 21%. (alquiler_id, coste_total, nuevo_coste).
Ordenado por identificador de alquiler.
S1.9. [F] Para cada viajero registrado hace menos de 8 meses, considerando la fecha actual (usa la
función SYSDATE y que la resta de dos fechas obtiene el número de días entre una y otra), mostrar su
nombre, su fecha de registro y cuántos días han pasado desde que se registró. El número de
días debe redondearse a un decimal. Ordenado por nombre. (nombre, fecha_registro,
dias_registrado).
S1.10. [F] Detalle de los alquileres del coche '1234XPQ' cuyo coste es inferior a 300 euros
(alquiler, litros_inicio, coste_coche). Ordenado por alquiler.
2º Grado en Ingeniería Informática. Bases de Datos. Práctica P2. Consultas SQL - 2/3
--
S1.11. [F] Listado de alquileres solicitados por cada viajero, ordenado por código de viajero.
(codigo, nombre, alquiler_id, coste_total).
S1.12. [F] Para cada alquiler ya cerrado y cuyo coste total supere los 500 euros, mostrar su
identificador, coste total, qué coches incluye y el coste de cada uno de ellos. (alquiler_id,
coste_total, coche, coste_coche).
S1.13. [F] Alquileres realizados por los viajeros de la ciudad de 'MURCIA' cuyo coste total esté
entre 250 y 700 euros. (nombre_viajero, alquiler_id, fecha_inicio, coste_total, cerrado).
S1.14. [F] Listado de agencias y sus alquileres siempre que su duración sea superior a 7 días, que
muestre el código de la agencia, su zona, el identificador del alquiler y su duración (es decir
los días que transcurren entre su fecha de inicio y su fecha de finalización). Ordenado por el
código de la agencia. (codigo_agencia, zona, alquiler_id, cerrado,
dias_alquiler)
S1.15. [F] Listado de viajeros que han realizado alquileres a través de cada agencia. Sin duplicados.
Ordenado por zona de agencia y nombre de viajero (zona_agencia, nombre_viajero).
S1.16. [F] Para cada alquiler aún en curso (es decir, no está cerrado) correspondiente a la agencia de
la zona 'GRAN VIA' mostrar el identificador de alquiler, el del viajero, la fecha prevista de
finalización y la matrícula de cada coche que incluye. Ordenado por alquiler.
(alquiler_id, viajero, fecha_fin, coche).
S1.17. [F] Para cada alquiler solicitado por un viajero residente en MURCIA, mostrar el nombre del
viajero, la fecha de inicio del alquiler y la matrícula de cada coche alquilado. Ordenado por
nombre de viajero. (nombre_viajero, fecha_inicio, coche).
S1.18. [F] Alquileres en los que esté involucrado algún coche asignado a los garajes 'VISTALEGRE'
o 'LA FLOTA' (alquiler_id, matricula, nombre_garaje). Ordenado por nombre del
garaje y por identificador del alquiler.
S1.19. [F] Nombre de todos los viajeros que han alquilado algún coche de la marca 'AUDI',
indicando su matrícula, modelo y lo que les ha costado alquilar tal coche en cada ocasión.
Ordenado por nombre de viajero y matricula. (nombre, matricula, modelo,
coste_coche).
S1.20. [F/M] Considerando los alquileres solicitados por los viajeros de 'BENIEL' y
'SANTOMERA', indicar la ciudad del viajero, el mes del año (en letras) en el que se inicia el
alquiler, la zona de la agencia que interviene, la marca del coche alquilado y la dirección del
garaje que tiene asignado el coche. Ordenado por ciudad, mes de alquiler y zona.
(ciudad_viajero, mes_alquiler, zona_agencia, marca_coche,
direccion_garaje).
--
S1.21. [M] Listado de garajes junto con sus coches asignados. Deben aparecer todos los garajes
existentes en la base de datos. Para cada garaje que no tenga coches se debe mostrar la
palabra 'VACIO' en la columna correspondiente a la matrícula del coche, y tres guiones '--
-' en la columna del precio del alquiler. En orden descendente de código de garaje y
ascendente de matrícula (codigo_garaje, direccion_garaje, matricula_coche,
precio_coche).
S1.22. [M] Listado de alquileres realizados para los viajeros residentes en la ciudad de 'YECLA',
que muestre el código y nombre del viajero, el identificador del alquiler, si está o no cerrado,
la fecha de fin, y el coste del alquiler. Deben aparecer todos los viajeros de esa ciudad. Para
cada viajero que no ha alquilado todavía se mostrará el texto '***' en la columna
correspondiente al identificador del alquiler, un guion '-' en la columna del estado del
alquiler, un nulo en la fecha y un cero en la del coste. Ordenado por nombre del viajero
(codigo_viajero, nombre, alquiler_id, cerrado, fin_alquiler,
coste_alquiler).
2º Grado en Ingeniería Informática. Bases de Datos. Práctica P2. Consultas SQL - 3/3
S1.23. [M] Listado de alquileres realizados para los coches asignados al garaje 'G3', que muestre la
matrícula del coche, el identificador del alquiler, la fecha de inicio (con el mes en letras y los
4 dígitos para el año), y los litros de combustible con los que se entregó el coche. Deben
aparecer todos los coches de ese garaje. Para cada coche que no haya sido alquilado todavía
se debe mostrar el texto '***' en la columna correspondiente al identificador del alquiler,
tres guiones '---' en la correspondiente a la fecha y un cero en la de los litros. Ordenado
por matrícula. (matricula_coche, alquiler_id, fecha_inicio,
litros_inicio).
--
S1.24. [F] Coches asignados al garaje ‘G4’ que no han sido alquilados nunca. Hay que usar
operadores de conjuntos. (matricula).
S1.25. [M] Agencias tales que o bien participan en alquileres con un coste superior a 1200€ o bien
participan en alquileres solicitados por viajeros cuya fecha de registro es de agosto de 2025.
Hay que usar operadores de conjuntos. (agencia).
S1.26. [M] Matrícula de los coches que han sido alquilados por algún viajero de 'MURCIA' y nunca
han sido alquilados por viajeros de 'SANTOMERA'. Ordenado por coche. Hay que usar
operadores de conjuntos (coche).
S1.27. [M] Coches asignados a un garaje cuyo nombre comienza por el texto 'VISTA' y que han
sido alquilados alguna vez por un periodo de más de 6 días. Hay que usar operadores de
conjuntos. (matricula) .
S1.28. [M] Coches asignados al garaje 'G1' o al 'G3' y que siempre han sido alquilados por un
período de menos de 3 días. Hay que usar operadores de conjuntos. (matricula).
S1.29. [M] Alquileres ya cerrados y realizados por la agencia 'A1' tales que todos los coches que
incluyen tienen un coste superior a 400€. No se puede usar JOIN. Hay que usar operadores
de conjuntos. (alquiler_id).
S1.30. [M] Código de los viajeros registrados hace más de un año tales que hayan alquilado alguna
vez y que todos sus alquileres tengan un coste total inferior a 500€. No se puede usar JOIN.
Hay que usar dos operadores de conjuntos. (codigo).*/




-- Qi

-- Qj

-- Qk
INSERT IGNORE INTO tipo_comportamiento (nombre, descripcion, activo) VALUES
('Comportamiento Inseguro', 'Accion o comportamiento que puede causar un accidente', true),
('Reconocimiento', 'Reconocimiento de buena practica de seguridad', true),
('Accidente', 'Evento que resulto en lesion o dano', true),
('Casi Accidente', 'Evento que pudo haber resultado en lesion o dano', true),
('Condicion Insegura', 'Condicion del entorno que representa un peligro', true),
('Acto Inseguro', 'Acto que viola un procedimiento o norma de seguridad', true);

INSERT IGNORE INTO causa (nombre, activo) VALUES
('Error Humano', true),
('Falta de Recursos Adecuados', true),
('Falta de Estandar Seguro', true),
('Falta de Capacitacion', true),
('Condicion Ambiental', true),
('Falla de Equipos', true);

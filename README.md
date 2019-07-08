# JMTwitterBot

El código detrás de @Spyzbot (Twitter)

# Funcionamiento

Este bot está basado en cadenas de Markov. Cuando se analizan los tuits de un usuario, se almacenan relaciones bidireccionales entre una palabra, la anterior y la siguiente, con una probabilidad. Esto significa que si detrás de "yo" aparece "soy" tres veces, y aparece "me" una vez, la probabilidad de "soy" es del 75% y la de "me" es del 25%. Este método de aprendizaje es similar al usado por el teclado predictivo de los teléfonos móviles.

Generalmente las frases se tokenizan tan fácilmente como un token por palabra, pero hay excepciones, como los símbolos de puntuación. Cuando se ha analizado todo el tuit, se añade al final un token especial (token FIN) para determinar que es el final de la cadena.

A la hora de generar una frase, se parte de la raíz, el punto (.), y empieza eligiendo una palabra que pudiese ir detrás del punto (que siempre marcan el inicio de la frase). El proceso de elegir la sigiente palabra según la probabilidad (directamente proporcional a la frecuencia de aparición) se repite hasta que se decide coger el token de FIN.

Para mantener un poco la coherencia, el bot no aprende palabra por palabra, si no por pares de palabras. Por ejemplo, en la frase "Yo tengo una casa bonita", se considerará:

Yo tengo - una casa - bonita

Yo - tengo una - casa bonita

Luego, un parámetro de coherencia (entre 0 y 1) determina cuánto respetará el bot dicha coherencia. Si está a 0, el bot elegirá las palabras una a una, y si está a 1, las elegirá puramente a pares. Este valor es útil cuando el bot ha aprendido poco: se elige un valor de coherencia bajo para que no copie los tuits tal cual. Una vez que ha aprendido muchos tuits diferentes, se puede aumentar este valor de coherencia para que tenga más sentido lo que escribe y no haga frases demasiado similares a las que ha aprendido.

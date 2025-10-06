
def saluda():
    print ("Hola!")

saluda()

# Cuando el lenguaje me permite que una variable apunte a una función
variable = saluda # Solo referencio la función desde la variable

# y posteriormente invocar (ejecutar) esa función a través de la variable
variable()

def generar_saludo_formal(nombre):
   return f"Buenos días, {nombre}"

def generar_saludo_informal(nombre):
   return f"Hola, {nombre}"

print(generar_saludo_formal("Menchu"))

def imprimir_saludo(funcion_generadora_de_saludos, nombre):
   saludo = funcion_generadora_de_saludos(nombre)
   # Si os dais cuenta, ésto me permite INYECTAR LOGICA a una función
   print(saludo)

imprimir_saludo(generar_saludo_formal, "Menchu")
imprimir_saludo(generar_saludo_informal, "Menchu")
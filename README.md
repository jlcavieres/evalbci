# evalbci


# Uso de la API

API REST con 2 métodos disponibles:

<b>Sign UP</b>
POST http://localhost:8080/api/users/sign-up

Ejemplo de JSON de entrada

{
   "name":"jose cavieres 3",
   "email":"jose.cavieres3@gmail.com",
   "password":"Testbci23",
   "phones":[
      {
         "number":"11111122222",
         "citycode":46,
         "countrycode":"ccode03"
      },
	  {
         "number":"2222223333",
         "citycode":46,
         "countrycode":"ccode03"
      }
   ]
}

<br/>

<b>Login</b>
GET http://localhost:8080/api/users/login

No requiere parametros de entrada más allá de un Token que se debe declarar como Header Authorization, en modo RAW luce como sigue (no incluir comillas): 
<br/>
Authorization:Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJqb3NlLmNhdmllcmVzM0BnbWFpbC5jb20iLCJpYXQiOjE3MDEzMDk1ODQsImV4cCI6MTcwMTM5NTk4NH0.1-jw3UQV8vcbjJ9NpLyWFl4KTgYenc9hkhFaPAom5gQ

<br/>

Se trata de un token JWT que se ha generado en la operación Sign UP (es requerido llamarla previa a ejecutar /login), la imagen a continuación indica como utilizarlo en el software Postman
![login-token](https://github.com/jlcavieres/evalbci/assets/28793178/fdae17d9-816e-4929-8758-17366de15f94)




# Construcción
El proyecto está implementado con Maven 3.9.x, para ejecutar los comandos a continuación es necesario estar posicionado en la carpeta del proyecto donde está el archivo <i>pom.xml</i>

Para correr los test de manera aislada, ejecutar <i>mvn clean test</i> el resultado deberá ser como la imagen a continuación

TIP: Si es requerido obtener un informe de cobertura de código, ejecutar <i>mvn clean verify</i>, el informe quedará disponible en \target\site\jacoco\index.html

Para correr la aplicación ejecutar <i>mvn spring-boot:run"</i>, se recomienda probarla con un software como Postman









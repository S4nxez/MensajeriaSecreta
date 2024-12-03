# Mensajería Secreta

### Descripción
Aplicación de mensajería privada desarrollada en Java con Spring y JavaFX, que ofrece cifrado simétrico y asimétrico de mensajes con algoritmo de curva elíptica y encriptación asimétrica de usuarios. Utiliza un modelo de capas para mantener un código limpio, inyección de dependencias con Spring, Railway Programming para gestionar errores y tests con JUnit y Mockito. Incorpora un flujo de CI para compilar y probar automáticamente, almacenamiento seguro en Keystore y procesamiento concurrente mediante CompletableFuture para tareas en múltiples hilos.

### Características
- **Mensajes encriptados:** Encriptación simétrica y asimétrica (curva elíptica).
- **Spring Boot:** Desarrollo backend con inyección de dependencias.
- **Gestión de errores:** Implementación de Railway Programming.
- **Concurrencia:** Uso de CompletableFuture para tareas en múltiples hilos.
- **Automatización de pruebas:** Con JUnit y Mockito.
- **CI/CD automatizado:** Flujo de trabajo con GitHub Actions para compilar y probar automáticamente.
- **Almacenamiento seguro:** Keystore para datos cifrados.
- **Logs detallados** usando **Log4j2**.
- **Interfaz gráfica:** Desarrollada con JavaFX.
  
### Capturas de Pantalla

Aquí hay algunas capturas de pantalla de la aplicación:

<table style="width: 100%; border-collapse: collapse; padding: 0; margin: 0;">
<tr>
<td style="padding: 0; margin: 0;"><img src="https://github.com/user-attachments/assets/3b681c2f-d838-41b4-a105-2dfd9ffa552b" alt="Pantalla de Login" style="width: 100%; height: auto;"></td>
<td style="padding: 0; margin: 0;"><img src="https://github.com/user-attachments/assets/7d40f9d6-0ec2-4871-822a-e772cb404471" alt="Pantalla Sign Up" style="width: 100%; height: auto;"></td>
</tr>
<tr>
<td style="padding: 0; margin: 0;"><img src="https://github.com/user-attachments/assets/d86a16d3-8e0d-4968-ba7c-de79030c7738" alt="Pantalla Principal" style="width: 100%; height: auto;"></td>
<td style="padding: 0; margin: 0;"><img src="https://github.com/user-attachments/assets/6e4f9fac-db88-4192-b7ef-6daaf7795ef3" alt="Añadir Grupos" style="width: 100%; height: auto;"></td>
</tr>
</table>

### Requisitos del sistema
- **Java**: 21 (JDK 21 LTS)

### Instalación y Ejecución

1. Clona este repositorio:
    ```bash
    git clone https://github.com/S4nxez/MensajeriaSecreta.git
    ```
   
2. Navega al directorio del proyecto:
    ```bash
    cd MensajeriaSecreta
    ```

3. Ejecuta el proyecto usando **Maven**:
    ```bash
    mvn clean install
    mvn javafx:run
    ```

4. Alternativamente, puedes abrir el proyecto en **IntelliJ** y ejecutarlo directamente desde allí.

### Estructura de la Base de Datos (Temporal)

Actualmente, los datos de usuarios, grupos y mensajes que son publicos se almacenan en *JSON*, los que deben encriptarse asimetricamente como las credenciales de usuarios y los mensajes de los grupos privados se almacenan en un *KeyStore*.

- **Usuarios (`User`)**:
    ```java
    private final String name;
    private final String email;
    private String pwd; //Este atributo no se almacena en ningún momento, solo se usa en RAM.
    private final List<String> friends;

    ```

- **Grupos (`Group`)**:
    ```java
    private final String nombre;
    private final List<User> miembros;
    private String password;
    private final String usernameAdmin;
    private final boolean privateChat;
    private final LocalDateTime creationDate;
    ```

- **Mensajes (`Message`)**:
    ```java
    private final String text;
    private final LocalDateTime timestamp;
    private final String sender;
    private final String grupo;
    ```

- **Grupos (`PrivateGroup`)**:
    ```java
    private final String nombre;
    private final List<User> miembros;
    private final String usernameAdmin;
    private final LocalDateTime creationDate;
    ```

- **Mensajes (`Message`)**:
    ```java
    private final String sender;
    private final LocalDateTime timestamp;
    private String sign;
    private String encryptedMessage;
    private Map<String, byte[]> symmetricKeysEncrypted;
    private String groupName;
    ```
    
### Contribuciones

Se aceptan contribuciones para mejorar este proyecto. Si tienes ideas, encuentras errores o deseas añadir funcionalidades, ¡no dudes en crear un fork y enviar un pull request! También puedes abrir un issue para sugerencias o problemas. Tu participación es bienvenida.

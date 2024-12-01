# Mensajería Secreta

### Descripción
Aplicación de mensajería privada desarrollada en Java con Spring y JavaFX, que ofrece cifrado simétrico y asimétrico de mensajes con algoritmo de curva elíptica y encriptación asimétrica de usuarios. Utiliza un modelo de capas para mantener un código limpio, inyección de dependencias con Spring, Railway Programming para gestionar errores y tests con Mockito. Incorpora un flujo de CI para compilar y probar automáticamente, almacenamiento seguro en Keystore y procesamiento concurrente mediante CompletableFuture para tareas en múltiples hilos.

### Estado del Proyecto
🚧 **En desarrollo**: Este proyecto aún está en fase de desarrollo y algunas características podrían cambiar. 

### Características
- **Mensajes encriptados:** Encriptación simétrica y asimétrica (curva elíptica).
- **Interfaz gráfica:** Desarrollada con JavaFX.
- **Gestión de errores:** Implementación de Railway Programming.
- **CI/CD automatizado:** Flujo de trabajo con GitHub Actions para compilar y probar automáticamente.
- **Inyección de dependencias con Spring.**
- **Concurrencia:** Uso de CompletableFuture para tareas en múltiples hilos.
- **Logs detallados** usando **Log4j2**.
  
### Capturas de Pantalla

Aquí hay algunas capturas de pantalla de la aplicación:

<table style="width: 100%; border-collapse: collapse; padding: 0; margin: 0;">
<tr>
<td style="padding: 0; margin: 0;"><img src="https://github.com/user-attachments/assets/dc09673a-f9ee-43d0-95a7-38e6879de151" alt="Pantalla de Login" style="width: 100%; height: auto;"></td>
<td style="padding: 0; margin: 0;"><img src="https://github.com/user-attachments/assets/67b75eae-a61f-4e49-9e3a-52568944d589" alt="Pantalla Sign Up" style="width: 100%; height: auto;"></td>
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
    git clone https://github.com/usuario/mensajeria-secreta.git
    ```
   
2. Navega al directorio del proyecto:
    ```bash
    cd mensajeria-secreta
    ```

3. Ejecuta el proyecto usando **Maven**:
    ```bash
    mvn clean install
    mvn javafx:run
    ```

4. Alternativamente, puedes abrir el proyecto en **IntelliJ** y ejecutarlo directamente desde allí.

### Estructura de la Base de Datos (Temporal)

Actualmente, los datos de usuarios, grupos y mensajes se almacenan utilizando **GSON**, pero se migrarán a un sistema con datos hasheados en las próximas semanas.

- **Usuarios (`User`)**:
    ```java
    private final String name;
    private final String email;
    private final String pwd;
    private final List<String> friends;
    ```

- **Grupos (`Group`)**:
    ```java
    private final String nombre;
    private final ArrayList<User> miembros;
    private final String password;
    private final String usernameAdmin;
    private final boolean privateGroup;
    private final LocalDateTime creationDate;
    ```

- **Mensajes (`Message`)**:
    ```java
    private final String text;
    private final LocalDateTime timestamp;
    private final String sender;
    private final String grupo;
    ```

### Contribuciones

Este proyecto es parte de un curso y **no acepta contribuciones** en este momento.

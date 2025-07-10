package com.SistemaPedidos.pedidos;

import com.SistemaPedidos.pedidos.ExcepcionesCustom.FireBaseIniE;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import jakarta.annotation.PostConstruct;
import java.io.InputStream;

@SpringBootApplication
public class PedidosApplication {

	public static void main(String[] args) {
		SpringApplication.run(PedidosApplication.class, args);
	}

	@PostConstruct
	public void init() {
		try {
			iniciarFirebase();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void iniciarFirebase() throws FireBaseIniE {
		try {
			InputStream serviceAccount = getClass().getResourceAsStream("/firebase/serviceAccKey.json");
			if (serviceAccount == null) {
				throw new RuntimeException("No se encontró el archivo de credenciales Firebase.");
			}

			FirebaseOptions fbOptions = new FirebaseOptions.Builder()
					.setCredentials(GoogleCredentials.fromStream(serviceAccount))
					.setDatabaseUrl("https://sistemapedidos-aaf6b-default-rtdb.firebaseio.com/")
					.build();

			if (FirebaseApp.getApps().isEmpty()) {
				FirebaseApp.initializeApp(fbOptions);
				System.out.println("Firebase se cargó exitosamente.");
			}
		} catch (Exception e) {
			throw new FireBaseIniE();
		}
	}

}

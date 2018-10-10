package com.example.lenovo.webview;


import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.webkit.ValueCallback;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


public class ActividadPrincipal extends AppCompatActivity {
    WebView pagina;

    boolean primeraVez = true;
    boolean heProcesadoLaPagina = false;

    public static final String MY_PREFS_NAME = "MyPrefsFile";

    EditText usuario, contrasenia;
    Button guardar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.actividad_principal);

        // Recogemos los elementos de la vista (layouts) para poder procesarlos desde el controlador
        pagina = findViewById(R.id.wv);
        usuario = findViewById(R.id.login);
        contrasenia = findViewById(R.id.pass);

        guardar = findViewById(R.id.saveBtn);

        guardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("usuario: " + usuario.getText().toString());
                System.out.println("password: " + contrasenia.getText().toString());

                // Guardo el usuario y la contraseña en las preferencias compartidas de mi aplicación
                SharedPreferences.Editor editor = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).edit();
                editor.putString("user", usuario.getText().toString());
                editor.putString("pass", contrasenia.getText().toString());
                editor.apply();
            }
        });


        //pagina.loadUrl(getResources().getString(R.string.url));
        WebSettings webSettings = pagina.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setDomStorageEnabled(true);


        pagina.setWebViewClient(new WebViewClient() {

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);

                System.out.println("[onPageFinished]");

                if (primeraVez){
                    // 1. ¿Tenemos datos en SharedPreferences?
                    SharedPreferences prefs = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);

                    String u = "";
                    String p = "";

                    u = prefs.getString("user", "");
                    p = prefs.getString("pass", "");

                    System.out.println("He recuperado: " + u + "\t" + p);

                    // Si tenemos datos, intentamos el inicio de sesión (mediante Injeccion Js)
                    if (u != "" && p != "") {

                        view.loadUrl("javascript:var x = document.getElementById('username').value = " + u + ";");
                        view.loadUrl("javascript:var y = document.getElementById('password').value = " + p + ";");
                        view.loadUrl("javascript:var z = document.forms['login'].submit();");

                    }

                    // Si el inicio de sesion fue erroneo, lo detectamos, y limpiamos las preferencias compartidas
                    view.evaluateJavascript("document.getElementById('loginerrormessage').innerHTML", new ValueCallback<String>() {
                        @Override
                        public void onReceiveValue(String value) {
                            System.out.println("Mensaje de error: " + value);

                            if (value.contains("Datos erróneos. Por favor, inténtelo otra vez.")) {


                                // Limpiar las preferencias compartidas
                                limpiarPreferencias();

                                // Mostrar un AlertDialog para indicar que el inicio de sesion ha fallado
                                notificar("El inicio de sesión fue erroneo. Borrando.");

                            }
                        }
                    });

                primeraVez = false;
            }

        }


        @Override
        public void onReceivedError (WebView view, WebResourceRequest request, WebResourceError
        error){
            //Your code to do
            Toast.makeText(getApplicationContext(), "Error:" + error, Toast.LENGTH_LONG).show();
        }

    });

    pagina.loadUrl(getResources().getString(R.string.url));

}

    public void limpiarPreferencias() {
        SharedPreferences settings = getApplicationContext().getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
        settings.edit().remove("user").apply();
        settings.edit().remove("pass").apply();

    }

    public void notificar(String mensaje) {

        AlertDialog.Builder builder1 = new AlertDialog.Builder(ActividadPrincipal.this);
        builder1.setMessage(mensaje);
        builder1.setCancelable(true);
        final AlertDialog alert11 = builder1.create();

        alert11.setOnShowListener(new DialogInterface.OnShowListener() {

            @Override
            public void onShow(DialogInterface dialogInterface) {

                Button button = ((AlertDialog) alert11).getButton(AlertDialog.BUTTON_POSITIVE);
                button.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View view) {
                        alert11.dismiss();
                    }
                });
            }
        });

        alert11.show();


    }
}



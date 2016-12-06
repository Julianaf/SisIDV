package com.example.ju.IDV;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.Toolbar;
import android.view.ContextMenu;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends Activity {

    private static final int IDF_ATIVIDADE_AUDIO = 0; //verificar este valor
    //variavel que será uma instancia da classe BaseDados
    private BaseDados base;
    private ListView lista; //declara a lista como variavel global
    private String linha, dados[], dadoscategorias[], localdafoto = "semfoto.jpg", localdoaudio = "semaudio.amr";
    private Spinner categoria; //declara uma variavel para representar o spinnner de categoria
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        //inicializa um objeto do tipo BaseDados - conecta a Base
        base = new BaseDados(this);

        //cria uma variavel para associar o ListView
        lista = (ListView) findViewById(R.id.listView);

        listar();

        //cria uma variavel para associar o Spinner
        categoria = (Spinner) findViewById(R.id.spnCategorias); //trocar o componente

        listarcategorias();

        //associa o menu de contexto a listview e captura a seleção de um item
        lista.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View v, int position, long id) {
                //captura a linha de dados da listView
                linha = ((TextView) v).getText().toString();

                //associa o menu de contexto com a list view
                registerForContextMenu(lista);
                return false;
            }
        });
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId(); //Pega o id do item selecionado

        //noinspection SimplifiableIfStatement
        if (id == R.id.menuSair) { //testa se o selecionado é o sair
            //o que fazer quando um item de menu é selecionado
            finish();
            return true;
        }
        if (id == R.id.menuSobre) {
            Toast.makeText(getApplicationContext(), "App de Identificação de Vestimentas Desenvolvido por Juliana", Toast.LENGTH_LONG).show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    //Método para criar um menu de contexto
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        //cria o menu via codigo
        super.onCreateContextMenu(menu, v, menuInfo);
        //especifica os itens do menu
        menu.add(0, 0, 0, "Editar");
        menu.add(0, 1, 0, "Excluir");
        menu.add(0, 2, 0, "Ver detalhes");
    }

    //Método que define as ações do menu de contexto
    public boolean onContextItemSelected(MenuItem item) {

        dados = linha.split(" - ");


        if (item.getItemId() == 0) //selecionado o menu editar
        {
            //preenche os campos do formulário com os dados da vestimenta selecionada
            //associa as variaveis aos campos do layout

            //colocar a edição para os campos de foto e de audio-descricao

            //libera o botão alterar
            Button btnAlterar = (Button) findViewById(R.id.btnAlterar);
            btnAlterar.setEnabled(true);

            return true;
        }

        if (item.getItemId() == 1) //selecionado o menu excluir
        {
            //cria uma caixa de dialogo para a confirmação
            AlertDialog.Builder dialogo = new AlertDialog.Builder(this);
            dialogo.setTitle("Excluir");
            dialogo.setMessage("Deseja realmente excluir os dados desta vestimenta?");
            dialogo.setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    //comandos para excluir um registro na tabela
                    SQLiteDatabase db = base.getWritableDatabase();
                    db.delete("vestimentas", "ves_id=?", new String[]{dados[0]});
                    listar();
                }
            });

            dialogo.setNeutralButton("Não", null);
            dialogo.show();

            return true;
        }

        if (item.getItemId() == 2) //selecionado o menu verdetalhes
        {
            //chama a activity detalhes passa os dados da vestimenta selecionada

            Intent abrirdetalhes = new Intent(this, DetalheVestimenta.class);
            abrirdetalhes.putExtra("id", dados[0]);
            abrirdetalhes.putExtra("id_categoria", dados[1]);
            abrirdetalhes.putExtra("foto", dados[2]);
            abrirdetalhes.putExtra("audio", dados[3]);

            startActivity(abrirdetalhes);

            return true;

        }

        return false;
    }

    public void btnCadastrarClick(View v) {

        //associa as variaveis aos campos do layout


        //pega os valores dos campos


        //cria o objeto ContentValues com os dados a serem inseridos
        ContentValues valores = new ContentValues();
        valores.put("id_categoria", dadoscategorias[0]); //cadastra o id do curso selecionado no spinner
        valores.put("foto", localdafoto);
        valores.put("audio", localdoaudio);

        //instancia um objeto do tipo SQLite em modo de gravação
        SQLiteDatabase db = base.getWritableDatabase();
        // insere os dados na tabela
        long id = db.insert("vestimentas", null, valores);
        //testa se inseriu com sucesso

        if (id == 1)
            Toast.makeText(getApplicationContext(), "Erro de cadastro", Toast.LENGTH_LONG).show();
        else
            Toast.makeText(getApplicationContext(), "Cadastrada com sucesso", Toast.LENGTH_LONG).show();

        //fecha a base
        db.close();

        listar();

    }

    public void btnAlterarClick(View v) {

        //associa as variaveis aos campos do layout

        //pega os valores dos campos

        //cria o objeto ContentValues com os dados a serem inseridos
        ContentValues valores = new ContentValues();
        //valores.put("nome", loo); //recebe como paramentro nome do campo e valor


        //instancia um objeto do tipo SQLite em modo de gravação
        SQLiteDatabase db = base.getWritableDatabase();
        // altera os dados na tabela
        long id = db.update("vestimentas", valores, "id=?", new String[]{dados[0]});
        //testa se inseriu com sucesso

        if (id != 1)
            Toast.makeText(getApplicationContext(), "Erro na alteração", Toast.LENGTH_LONG).show();
        else
            Toast.makeText(getApplicationContext(), "Alterado com sucesso", Toast.LENGTH_LONG).show();

        //fecha a base
        db.close();

        listar();

        //desablita o botao alterar
        Button btnAlterar = (Button) findViewById(R.id.btnAlterar);
        btnAlterar.setEnabled(false);

        //limpa os campos


    }


    public void listar() {

        //cria uma variavel do tipo arrayList para preencher o ListView
        List<String> dados = new ArrayList<String>();
        //instanciar a classe SQLiteDatabase em modo leitura

        SQLiteDatabase db = base.getReadableDatabase();
        //executa a consulta(select)
        Cursor resultado = db.query("vestimentas", null, null, null, null, null, "nome ASC"); //verificar
        //percorre os registros retornados
        while (resultado.moveToNext()) {
            //adiciona o valor dos campos retornados no array de dados
            dados.add(resultado.getString(0) + " - " + resultado.getString(1) + " - " + resultado.getString(2)
                    + " - " + resultado.getString(3) + " - " + resultado.getString(4));
        }
        lista.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, dados));
        db.close();
    }

    public void listarcategorias() {

        //cria uma variavel do tipo arrayList para preencher o ListView
        final List<String> dados = new ArrayList<String>();
        //instanciar a classe SQLiteDatabase em modo leitura

        SQLiteDatabase db = base.getReadableDatabase();
        //executa a consulta(select)
        Cursor resultado = db.query("categoria", null, null, null, null, null, "nome ASC");
        //percorre os registros retornados
        while (resultado.moveToNext()) {
            //adiciona o valor dos campos retornados no array de dados
            dados.add(resultado.getString(0) + " - " + resultado.getString(1));
        }
        categoria.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, dados));
        db.close();

        //Método para capturar o curso selecionado no spidder
        categoria.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                //pega o item selecionado do spinner
                String categoriaselecionada = parent.getItemAtPosition(position).toString();
                //separa os dados em um array
                dadoscategorias = categoriaselecionada.split(" - ");
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    public void btnTelaCategorias(View v)//metodo para abrir uma nova tela (activity)
    {

        Intent telaCategorias = new Intent(this, TelaCategorias.class);

        startActivity(telaCategorias);
    }

    public void cameraClick(View v) {
        //define o local da foto para o External Storage

        //usa a hora do sistema como nome da foto
        localdafoto = Environment.getExternalStorageDirectory() + "/fotos/" + System.currentTimeMillis() + ".jpg";
        File arquivo = new File(localdafoto);

        //associa o endereço URI da foto
        Uri localfotouri = Uri.fromFile(arquivo);

        //instancia a classe intent para a camera
        Intent abrircamera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        //passa um parametro para a camera que é o local para salvar a foto
        abrircamera.putExtra(MediaStore.EXTRA_OUTPUT, localfotouri);

        //inicia a camera
        startActivity(abrircamera);
    }

    public void iniciaAudio() {

        Intent intent = new Intent();

    intent.setAction(MediaStore.Audio.Media.RECORD_SOUND_ACTION);
    try{
        startActivityForResult(intent, IDF_ATIVIDADE_AUDIO);
    }

    catch(ActivityNotFoundException e){
        Toast.makeText(getApplicationContext(), "Nenhum aplicativo p/ gravação de audio encontrado!", Toast.LENGTH_LONG).show();
    }

}
    private boolean gravaAudio(Intent intent, String nome) {
        FileOutputStream fos = null; FileInputStream fis = null;
        try {
            AssetFileDescriptor videoAsset = getContentResolver().openAssetFileDescriptor(intent.getData(),"r");
            fis = videoAsset.createInputStream();
            String pathAudio = getFilesDir() + "/" + nome + ".amr"; File audio = new File(pathAudio);
            fos = new FileOutputStream(audio);
            byte[] buf = new byte[1024];
            int len;
            while ((len = fis.read(buf)) > 0) {
                fos.write(buf, 0, len);
            }
            return true;
        }
        catch (IOException io)
        {
            return false;
        }
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Main Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app deep link URI is correct.
                Uri.parse("android-app://com.example.ju.IDV/http/host/path")
        );
        AppIndex.AppIndexApi.start(client, viewAction);
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Main Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app deep link URI is correct.
                Uri.parse("android-app://com.example.ju.IDV/http/host/path")
        );
        AppIndex.AppIndexApi.end(client, viewAction);
        client.disconnect();
    }
}

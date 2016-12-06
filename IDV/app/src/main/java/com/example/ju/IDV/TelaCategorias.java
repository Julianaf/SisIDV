package com.example.ju.IDV;

import android.app.Activity;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class TelaCategorias extends Activity {

    //variavel que será uma instancia da classe BaseDados
    private BaseDados base;
    private ListView lista; //declara a lista como variavel global
    private String linha, dados[];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tela_vestimentas);
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

    }

    public void btnCadastrarClick (View v) {

        //associa as variaveis aos campos do layout
        EditText edtNome = (EditText) findViewById(R.id.edtNome);

        //pega os valores dos campos
        String nome = edtNome.getText().toString();

        //cria o objeto ContentValues com os dados a serem inseridos
        ContentValues valores = new ContentValues();
        valores.put("nome",nome); //recebe como paramentro nome do campo e valor

        //instancia um objeto do tipo SQLite em modo de gravação
        SQLiteDatabase db = base.getWritableDatabase();
        // insere os dados na tabela
        long id = db.insert("categorias", null, valores);
        //testa se inseriu com sucesso

        if(id==1)
            Toast.makeText(getApplicationContext(),"Erro de cadastro",Toast.LENGTH_LONG).show();
        else
            Toast.makeText(getApplicationContext(),"Cadastrado com sucesso",Toast.LENGTH_LONG).show();

        //fecha a base
        db.close();

       listar();

    }

    public void btnAlterarClick (View v) {

        //associa as variaveis aos campos do layout
        EditText edtNome = (EditText) findViewById(R.id.edtNome);

        //pega os valores dos campos
        String nome = edtNome.getText().toString();

        //cria o objeto ContentValues com os dados a serem inseridos
        ContentValues valores = new ContentValues();
        valores.put("nome",nome); //recebe como paramentro nome do campo e valor

        //instancia um objeto do tipo SQLite em modo de gravação
        SQLiteDatabase db = base.getWritableDatabase();
        // altera os dados na tabela
        long id = db.update("categorias", valores, "id=?", new String[]{dados[0]});
        //testa se inseriu com sucesso

        if(id!=1)
            Toast.makeText(getApplicationContext(), "Erro na alteração", Toast.LENGTH_LONG).show();
        else
            Toast.makeText(getApplicationContext(),"Alterado com sucesso",Toast.LENGTH_LONG).show();

        //fecha a base
        db.close();

        listar();

        //desablita o botao alterar
        Button btnAlterar = (Button) findViewById(R.id.btnAlterar);
        btnAlterar.setEnabled(false);

        //limpa os campos
        edtNome.setText("");

    }

    public void listar (){

        //cria uma variavel do tipo arrayList para preencher o ListView
        List<String> dados = new ArrayList<String>();
        //instanciar a classe SQLiteDatabase em modo leitura

        SQLiteDatabase db = base.getReadableDatabase();
        //executa a consulta(select)
        Cursor resultado= db.query("categorias", null, null, null, null, null, "nome ASC");
        //percorre os registros retornados
        while(resultado.moveToNext()){
            //adiciona o valor dos campos retornados no array de dados
            dados.add(resultado.getString(0) + " - " + resultado.getString(1));
        }
        lista.setAdapter(new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,dados));
        db.close();
    }

}

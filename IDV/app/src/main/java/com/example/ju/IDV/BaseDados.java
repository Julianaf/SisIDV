package com.example.ju.IDV;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by juliana 2016
 */
public class BaseDados extends SQLiteOpenHelper{
    //define os atributos para o nome da base de dados e sua versão
    private static final String nome_base ="basevestimentas";
    private static int versao=17;

    //metodo construtor - cria ou abre a base de dados
    public BaseDados(Context context) {
        super(context, nome_base, null, versao);
    }

    @Override
    //metodo executado no primeiro start da aplicação
    public void onCreate(SQLiteDatabase db) {
        //define a estrutura da base de dados ou seja, cria as tabelas
        //Define o sql
        String sql = "CREATE TABLE vestimentas (id INTEGER PRIMARY KEY AUTOINCREMENT," +
              "id_categoria INTEGER, foto TEXT, audio TEXT);";
//inserir o atributo para armazernar a audio-descrição da vestimenta, proporcionada através do cadastro

        //executa o sql
        db.execSQL(sql);

        sql = "CREATE TABLE categorias (id_categoria INTEGER PRIMARY KEY AUTOINCREMENT," +
                "nome TEXT NOT NULL);";

        //executa o sql
        db.execSQL(sql);

    }
   //metodo que é executado quando mudamos a versao da base de dados
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // devemos definir neste metodo as alterações necessarias na base
        // nesse caso iremos definir o metodo para reinicializar a Base
        String sql = "DROP TABLE IF EXISTS vestimentas;";
        db.execSQL(sql);

        sql = "DROP TABLE IF EXISTS categorias;";
        db.execSQL(sql);
        onCreate(db); //chama novamente o método para criar a tabela
    }
}

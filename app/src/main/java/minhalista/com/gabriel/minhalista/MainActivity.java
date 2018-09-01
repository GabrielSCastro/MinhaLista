package minhalista.com.gabriel.minhalista;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private ConstraintLayout layout;
    private FloatingActionButton fbtn_adicionar;
    private ListView lista;
    private SQLiteDatabase bd;
    private ArrayAdapter<String> adapter;
    private ArrayList<String> informacoes;
    private ArrayList<Integer> ids;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        try {
            //Recuperando os componentes da tela
            fbtn_adicionar = findViewById(R.id.fbtn_adicionar);
            lista = findViewById(R.id.list_lista);
            layout = findViewById(R.id.layout);

            //Metodos de botao
            fbtn_adicionar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final AlertDialog.Builder alert = new AlertDialog.Builder(MainActivity.this);
                    View mview = getLayoutInflater().inflate(R.layout.layout_dialog, null);
                    final EditText edit_digitado = mview.findViewById(R.id.edit_digitado);
                    Button btn_adicionar = mview.findViewById(R.id.btn_adicionar);

                    btn_adicionar.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if(!edit_digitado.getText().toString().isEmpty()){
                                //Vamos obter do usuario o que for digitado e passado para a variavel digitado
                                salvarInformacao(edit_digitado.getText().toString());
                            }else{
                                Snackbar.make(layout, "Digite algo para ser adicionado", Snackbar.LENGTH_SHORT).show();
                            }
                        }
                    });

                    alert.setView(mview);
                    alert.create();
                    alert.show();
                }
            });


            //Criando o banco de dados
            bd = openOrCreateDatabase("app", MODE_PRIVATE, null);

            //Criando a tabela
            bd.execSQL("CREATE TABLE IF NOT EXISTS informacoes('id INTEGER PRIMARY KEY AUTOINCREMENT, informacao VARCHAR')");


            lista.setLongClickable(true);
            lista.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                @Override
                public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                    removerInformacao(ids.get(position));
                    recuperarInformacao();
                    Toast.makeText(getApplicationContext(), "Removido com sucesso", Toast.LENGTH_LONG).show();
                    return true;
                }
            });
            lista.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                }
            });

            recuperarInformacao();

        }catch(Exception e){
            e.printStackTrace();
        }
    }

    //Metodo que vai salvar a informacao
    private void salvarInformacao(String digitado){
        try{
            if(digitado.equals("")){
                Toast.makeText(this, "Digite algo para ser salvo", Toast.LENGTH_LONG).show();
            }else{
                //Estamos agora usando o método para inserir na tabela o valor que está na variável digitado
                //Observe que adicionamos no campo informacao e concatenamos a variavel
                bd.execSQL("INSERT INTO informacoes VALUES('"+ digitado +"')");
                Toast.makeText(this, "Adicionado a lista", Toast.LENGTH_LONG).show();
                recuperarInformacao();
            }

        }catch (Exception e){
            e.printStackTrace();
        }

    }

    //Metodo para recuperar as informacoes
    private void recuperarInformacao(){
        try{
            //Vamos agora listar as informacoes que estao na tabela
            //cursor recupera as informacoes
            Cursor cursor = bd.rawQuery("SELECT * FROM informacoes ORDER BY id DESC", null);

            //Vamos agora recuperar o indice das colunas
            int indiceColunaId = cursor.getColumnIndex("id");
            int indiceColunaInformacao = cursor.getColumnIndex("informacao");

            //Criar o adaptador para a lista
            informacoes = new ArrayList<String>();
            ids = new ArrayList<Integer>();
            adapter = new ArrayAdapter<String>(getApplicationContext(),
                    android.R.layout.simple_list_item_1,
                    android.R.id.text1,
                    informacoes);
            lista.setAdapter(adapter);

            //Vamos agora listar as tarefas
            //Quando voce recupera os registros, o cursos aponta para o ultimo registro, por isso precisamos voltar para o primeiro elemento
            cursor.moveToFirst();

            while(cursor != null){
                informacoes.add(cursor.getString(indiceColunaInformacao));
                ids.add(Integer.parseInt(cursor.getString(indiceColunaId)));
                cursor.moveToNext();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void removerInformacao(Integer id){
        try{
            bd.execSQL("DELETE FROM informacoes WHERE id = "+id);
        }catch (Exception e){
            e.printStackTrace();
        }
    }


}

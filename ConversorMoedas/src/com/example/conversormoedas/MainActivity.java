package com.example.conversormoedas;

import android.widget.Toast;
import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Spinner;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import java.io.IOException;
import java.net.MalformedURLException;
import java.text.DecimalFormat;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends Activity implements OnItemSelectedListener {
    private Spinner moedas;
	private EditText txValor;
    private TextView tvConvertido;
	private Button btnConverte;
    private Handler hdrValorConv = new Handler();
	private ArrayAdapter<CharSequence> adapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		moedas = (Spinner)findViewById(R.id.spnMoedas);
		adapter = ArrayAdapter.createFromResource(this, R.array.array_moedas,
				android.R.layout.simple_list_item_1);
		moedas.setAdapter(adapter);
		moedas.setOnItemSelectedListener(this);
		txValor = (EditText)findViewById(R.id.edValor);
		btnConverter = (Button)findViewById(R.id.btnConverte);
		tvConvertido = (TextView)findViewById(R.id.txtConvertido);
	}

    @Override
	public void onNothingSelected(AdapterView<?> parent) {
		paisMoedaConversao = "USD";
	}

	public void onClickConverter(View v) {
		int valorParaConversao = 0;
		try {
			valorParaConversao = Integer.parseInt(txValor.getText().toString());
		} catch (NumberFormatException e) {
			Toast.makeText(getApplicationContext(), "Erro na conversao do valor", Toast.LENGTH_LONG);
		}
		btnConverter.setEnabled(false);
		converter(valorParaConversao);
	}
    
	@Override
	public void onItemSelected(AdapterView<?> parent, View view, int position,
			long id) {
		paisMoedaConversao = (String)adapter.getItem(position);
	}

    // {"to": "EUR", "rate": 0.78894299999999995, "from": "USD", "v": 0.78894299999999995}
	public String criarString(int val) {
		StringBuilder s = new StringBuilder();
		s.append("http://rate-exchange.appspot.com/currency?from=").append(paisMoedaConversao).append("&to=").append("BRL")
				                                                   .append("&q=").append(valor);
		return s.toString();
	}

	private void converter(int valor) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				double convertido = 0.0;
				try {
					DefaultHttpClient httpClient = new DefaultHttpClient();
					HttpGet get = new HttpGet(gerarURL(valor));

					HttpResponse httpResponse = httpClient.execute(get);
					String json = EntityUtils.toString(httpResponse.getEntity());

					JSONObject valorJSon = new JSONObject(json);

					convertido = (Double)valorJSon.get("v");
					
				} catch (MalformedURLException e) {
					Log.e("ERRO", "MalformedURLException");
					e.printStackTrace();
				} catch (IOException e) {
					Log.e("ERRO", "IOException");
					e.printStackTrace();
				} catch (JSONException e) {
					Log.e("ERRO", "JSONException");
					e.printStackTrace();
				}

				DecimalFormat formatacaoDuasCasas = new DecimalFormat("0.00"); 

				final String novoTextoValorConvertido = formatacaoDuasCasas.format(convertido);

				hdrValorConv.post(new Runnable() {
					@Override
					public void run() {
						tvConvertido.setText("R$ "+novoTextoValorConvertido);
						btnConverter.setEnabled(true);
					}
				});
			}
		}).start();
	}
}


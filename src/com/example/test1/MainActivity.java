package com.example.test1;

import java.io.IOException;
import java.util.Arrays;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.http.AndroidHttpClient;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class MainActivity extends Activity {

	private ProgressDialog mProgressDialog = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		((Button) this.findViewById(R.id.loginBtn)).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {

				final EditText textName = (EditText) MainActivity.this.findViewById(R.id.editName);
				final EditText textPass = (EditText) MainActivity.this.findViewById(R.id.editPass);

				Log.d("sample", textName.getText().toString());
				Log.d("sample", textPass.getText().toString());
				new AsyncTask<NameValuePair, Integer, String>() {

					@Override
					protected void onPreExecute() {
						mProgressDialog = new ProgressDialog(MainActivity.this);
						mProgressDialog.setTitle("ログイン中");
						mProgressDialog.setIndeterminate(true);
						mProgressDialog.show();
					}

					@Override
					protected String doInBackground(NameValuePair... params) {
						try {
							HttpPost post = new HttpPost("http://54.250.182.30:8080/dakoku/");
							post.setEntity(new UrlEncodedFormEntity(Arrays.asList(params)));
							AndroidHttpClient client = AndroidHttpClient.newInstance("Android UserAgent");
							String result = client.execute(post, new ResponseHandler<String>() {
								@Override
								public String handleResponse(HttpResponse response) throws ClientProtocolException,
										IOException {
									Log.d("sample", String.valueOf(response.getStatusLine().getStatusCode()));
									Log.d("sample", response.getStatusLine().toString());
									return EntityUtils.toString(response.getEntity(), "UTF-8");
								}
							});
							Log.d("sample", result);
							client.close();
							Integer progress = 10;
							publishProgress(progress);
							return result;
						} catch (Exception e) {
							Log.w("sample", e.getMessage().toString());
							return null;
						}
					}

					@Override
					protected void onProgressUpdate(Integer... progress) {
						mProgressDialog.setProgress(progress[0]);
					}

					@Override
					protected void onPostExecute(String result) {
						Log.d("sample2", result);
						String res = result.substring(4, 6);
						if (res.equals("00")) {
							mProgressDialog.dismiss();
							System.out.println("ログインOK");
							// インテントを生成&データをセット
							Intent i = new Intent(MainActivity.this, com.example.test1.SubActivity.class);
							i.putExtra("txtName", result.substring(12));
							i.putExtra("txtId", textName.getText().toString());
							// アクティビティを起動
							startActivity(i);
						} else {
							mProgressDialog.dismiss();
							System.out.println("ログイン失敗");
							AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
							builder.setTitle("エラー")
									.setMessage("ログインIDまたはパスワートが違います。")
									.setIcon(R.drawable.ic_launcher)
									.setPositiveButton("OK", new DialogInterface.OnClickListener() {
										public void onClick(DialogInterface dialog, int which) {
										}
									})
									.show();
						}
					}
				}.execute(
						new BasicNameValuePair("param1", "1"),
						new BasicNameValuePair("param2", textName.getText().toString()),
						new BasicNameValuePair("param3", textPass.getText().toString())
						);
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}

package com.example.test1;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Locale;

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
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.http.AndroidHttpClient;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class SubActivity extends Activity implements LocationListener {

	LocationManager locationManager;

	double ido;

	double keido;

	private ProgressDialog mProgressDialog = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_sub);
		// インテントを取得&トーストに反映
		Intent i = this.getIntent();
		String txtName = i.getStringExtra("txtName");
		Log.d("ei", txtName);
		final String txtId = i.getStringExtra("txtId");
		TextView txt = (TextView)findViewById(R.id.userTxt);
		txt.setText("こんにちわ " + txtName + "さん");

		Toast.makeText(this, "ログインしました。",Toast.LENGTH_SHORT).show();
		LinearLayout layout = new LinearLayout(this);
	    //デジタル時計
        DigitalClock digitalClock=new DigitalClock(this);
        layout.addView(digitalClock);

	     // LocationManager を取得する
	     locationManager = (LocationManager) SubActivity.this.getSystemService(Context.LOCATION_SERVICE);


 	   ((Button)this.findViewById(R.id.startBtn)).setOnClickListener(new View.OnClickListener() {
 			@Override
 			public void onClick(View v) {
 				dakokuInsert(txtId, "1");
 			}
 		});

 	   ((Button)this.findViewById(R.id.emdBtn)).setOnClickListener(new View.OnClickListener() {
 			@Override
 			public void onClick(View v) {
 				dakokuInsert(txtId, "2");
 			}
 		});
    }

	public void dakokuInsert(String txtId, String flg){

//    	locationManager.requestLocationUpdates(
//                LocationManager.NETWORK_PROVIDER, 0, 0, this);


			Calendar cal = Calendar.getInstance();
			SimpleDateFormat format = new SimpleDateFormat("yyyy年MM月dd日HH時mm分ss秒", Locale.JAPAN);
			String dakoku_time = format.format(cal.getTime());
			Log.d("aa",dakoku_time);
			new AsyncTask<NameValuePair, Integer, String>() {
				@Override
				protected void onPreExecute() {
					mProgressDialog = new ProgressDialog(SubActivity.this);
					mProgressDialog.setTitle("送信中");
					mProgressDialog.setIndeterminate(true);
					mProgressDialog.show();
				}

				@Override
				protected String doInBackground(NameValuePair... params) {
					try {
						HttpPost post = new HttpPost("http://54.250.182.30:8080/dakoku/");
						post.setEntity(new UrlEncodedFormEntity(Arrays.asList(params),  "UTF-8"));
						AndroidHttpClient client = AndroidHttpClient.newInstance("Android UserAgent");
						String result = client.execute(post, new ResponseHandler<String>() {
							@Override
							public String handleResponse(HttpResponse response) throws ClientProtocolException, IOException {
								Log.d("sample", String.valueOf(response.getStatusLine().getStatusCode()));
								Log.d("sample", response.getStatusLine().toString());
								return EntityUtils.toString(response.getEntity(), "UTF-8");
							}
						});

						Log.d("samplea", result);
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
					System.out.println(result);
					String res = result.substring(4, 6);
				if(res.equals("00")){
					mProgressDialog.dismiss();
					AlertDialog.Builder builder = new AlertDialog.Builder(SubActivity.this);
					builder.setTitle("完了")
							.setMessage("打刻されました！")
							.setIcon(R.drawable.ic_launcher)
							.setPositiveButton("OK", new DialogInterface.OnClickListener(){
								public void onClick(DialogInterface dialog, int which) { }})
							.show();

				}else{
						mProgressDialog.dismiss();
						Toast.makeText(SubActivity.this, "エラーとなりました",Toast.LENGTH_SHORT).show();
				}

				}
			}.execute(
				new BasicNameValuePair("param1", "2"),
				new BasicNameValuePair("param2", txtId),
				new BasicNameValuePair("param3", dakoku_time),
				new BasicNameValuePair("param4", flg),
				new BasicNameValuePair("param5", Double.toString(ido)),
				new BasicNameValuePair("param6", Double.toString(keido))
			);


	}


    @Override
    protected void onResume() {
        super.onResume();
		mProgressDialog = new ProgressDialog(SubActivity.this);
		mProgressDialog.setTitle("位置情報取得中");
		mProgressDialog.setIndeterminate(true);
		mProgressDialog.show();
        if (locationManager != null) {
            // 位置情報のリクエストを開始する
        	locationManager.requestLocationUpdates(
                    LocationManager.NETWORK_PROVIDER, 0, 0, SubActivity.this);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
            // 更新が不要であればリクエストを破棄する
        	locationManager.removeUpdates(this);
    }

    @Override
    public void onLocationChanged(Location location) {
    	mProgressDialog.dismiss();
        locationManager.removeUpdates(this);
        Log.d("onLocationChanged", location.getProvider() + " " + String.valueOf(location.getAccuracy()) + " " + location.getTime());
        // 緯度の表示
        System.out.println(location.getLatitude());
        ido = location.getLatitude();
        // 経度の表示
        System.out.println(location.getLongitude());
        keido = location.getLongitude();

//        Toast toast = Toast.makeText(SubActivity.this,
//                Double.toString(location.getLongitude()),
//                Toast.LENGTH_LONG);
//              toast.show();
    }

    @Override
    public void onProviderDisabled(String provider) {
        // TODO Auto-generated method stub

      Toast toast = Toast.makeText(SubActivity.this,"プロパイダが有効になっていません。" ,Toast.LENGTH_LONG);
      toast.show();
    }

    @Override
    public void onProviderEnabled(String provider) {
        // TODO Auto-generated method stub
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        // TODO Auto-generated method stub
    }

    @Override
    protected void onDestroy() {
      super.onDestroy();
      // 重要：requestLocationUpdatesしたままアプリを終了すると挙動がおかしくなる。
      locationManager.removeUpdates(this);
    }



}
